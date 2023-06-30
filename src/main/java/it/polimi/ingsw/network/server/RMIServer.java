package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.RMIServerInterface;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static it.polimi.ingsw.network.server.Server.SERVER_LOGGER;

/**
 * is the server that concretely has to handle RMI connections with the players
 */
public class RMIServer implements Runnable, RMIServerInterface{
    /**
     * is the server that created RMIServer. RMIServer will forward commands to it
     */
    private final Server server;
    /**
     * PING_TIME is the period of the checking for disconnected player
     */
    private final int PING_TIME = 1000;
    /**
     * rmiClients is the list of clients served by the RMIServer
     */
    private final List<RMIClientInterface> rmiClients = new ArrayList<>();
    /**
     * rmiClientsToRemove is a list of clients that are no more online, so they have to be disconnected
     */
    private final List<RMIClientInterface> rmiClientsToRemove = new ArrayList<>();
    /**
     * rmiClientsName maps the unique name of every player to the respective network interface
     */
    private final Map<RMIClientInterface, String> rmiClientsName = new HashMap<>();
    /**
     * RMIClientsLobby maps the uniques network interface of the clients to the respective lobby
     */
    private final Map<RMIClientInterface, Lobby> rmiClientsLobby = new HashMap<>();
    /**
     * rmiClientsStates maps the uniques network interface of the clients to their states
     */
    private final Map<RMIClientInterface, ClientState> rmiClientsStates = new HashMap<>();
    /**
     * port is the port number used for rmi connections (server side)
     */
    private final int port;
    /**
     * pingThread is the thread used to perform the periodical ping action
     */
    private final Thread pingThread;

    /**
     * constructor of RMIServer used to create a server responsible for handling the rmi connections.
     * @param server it's the class to which the commands will be forwarded
     * @param port it's the port that will be used for the communication
     */
    public RMIServer (Server server, int port) {
        this.server = server;
        this.port = port;

        pingThread = new Thread(this::checkClientAliveness);
    }

    /**
     * method used to start all the services that will handle the connections of new players using rmi
     */
    @Override
    public void run() {
        try {
            System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
            RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(this, 0);
            LocateRegistry.createRegistry(port);
            LocateRegistry.getRegistry(port).bind(RMIServerInterface.NAME, stub);
            SERVER_LOGGER.info("RMI server started on port " + port);
            pingThread.start();
        } catch (UnknownHostException | AlreadyBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * function used to correctly initialize a rmiClient to be in a lobby
     * @param lobby is the lobby joined by the player
     */
    public synchronized void setInGameStatus (String lobby) {
        for (RMIClientInterface rmiClient : rmiClients) {
            if(rmiClient != null ) {
                if (rmiClientsLobby.get(rmiClient) != null && rmiClientsLobby.get(rmiClient).getLobbyName().equals(lobby)){
                    rmiClientsStates.put(rmiClient, ClientState.IN_GAME);
                }
            }
        }
    }

    /**
     * For each client a thread is created that checks if the client is reachable.
     * Every second the server checks whether there are new client online that needs to be pinged.
     * For each ping to be discarded it's considered a timeout of 5 seconds. Between two ping is waited another second.
     * If the client is unreachable for 3 consecutive iterations it is disconnected from the server
     */
    public void checkClientAliveness () {
        List<RMIClientInterface> checkedClients = new ArrayList<>();
        while (true) {
            synchronized (this) {
                for (RMIClientInterface rmiClient : rmiClients) {
                    if (!checkedClients.contains(rmiClient)) {
                        checkedClients.add(rmiClient);
                        new Thread(() -> {
                            boolean online = true;
                            int count = 0;
                            while (online) {
                                synchronized (rmiClientsStates) {
                                    synchronized (rmiClientsLobby) {
                                        if (ClientState.IN_GAME.equals(rmiClientsStates.get(rmiClient)) && !rmiClientsLobby.containsKey(rmiClient)) {
                                            rmiClientsToRemove.add(rmiClient);
                                            online = false;
                                        }
                                    }
                                }
                                if(online) {
                                    try {
                                        if (rmiClient.checkAliveness()) {
                                            count = 0;
                                        }
                                    } catch (RemoteException e) {
                                        count++;
                                        if (count > 2) {
                                            synchronized (rmiClientsToRemove) {
                                                rmiClientsToRemove.add(rmiClient);
                                                online = false;
                                            }
                                        }
                                    }
                                    try {
                                        Thread.sleep(PING_TIME);
                                    } catch (InterruptedException ignored) {

                                    }
                                }
                            }
                        }).start();
                    }
                }
            }
            synchronized (this){
                for (RMIClientInterface rmiClientInterface : rmiClientsToRemove) {
                    manageDisconnection(rmiClientInterface);
                    checkedClients.remove(rmiClientInterface);
                }
                rmiClientsToRemove.clear();
            }
            try {
                Thread.sleep(PING_TIME);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * sendMsgToAllRMI is the method used to send a message to every client that is connected through rmi connection to a specific lobby
     * @param msg is the forwarded message, it needs to be serializable
     * @param lobby is the target Lobby
     */
    public synchronized void sendMsgToAllRMI (Serializable msg, Lobby lobby) {

        for (RMIClientInterface rmiClient : rmiClients) {
            try {
                if (rmiClientsLobby.get(rmiClient) == lobby) {
                    rmiClient.receiveMsgFromServer(msg);
                    System.out.println("Sent " + ((ServerMessage)msg).getType() + " to RMI client");
                }
            } catch (RemoteException e) {
            }
        }
    }

    /**
     * sendMsgToClient is the method used to send a message to a specific rmi client
     * @param rmiClient is the destinatari of the message
     * @param msg is the serializable message
     */
    public void sendMsgToClient (RMIClientInterface rmiClient, ServerMessage msg) {
        try {
            rmiClient.receiveMsgFromServer(msg);
        } catch (RemoteException e) {
        }
        System.out.println("Sent " + msg.getType() + " to RMI client");
    }

    /**
     * receiveMsgFromClient is the method used to handle the requests that arrives from the clients in the form of rmi messages.
     * It's responsible for the communication between the client and the controller in the server
     * @param arg is the serializable message received from the client
     */
    @Override
    public synchronized void receiveMsgFromClient (Serializable arg) {
        ClientMessage msg = (ClientMessage) arg;
        RMIClientInterface sender = msg.getRmiClient();
        System.out.println("Received " + msg.getType() + " from RMI client");

        try {
            if (msg.getType().equals("CreateLobbyMessage") && rmiClientsStates.get(sender) == ClientState.CONNECTED) {
                CreateLobbyMessage specificMessage = (CreateLobbyMessage) msg;
                String lobbyName = specificMessage.getLobbyName();
                String lobbyCreator = specificMessage.getLobbyCreator();
                int numPlayers = specificMessage.getPlayerNumber();
                server.gameBroker.createLobby(lobbyCreator, lobbyName, numPlayers);

                rmiClientsStates.put(sender, ClientState.IN_LOBBY);
                rmiClientsName.put(msg.getRmiClient(), lobbyCreator);
                rmiClientsLobby.put(msg.getRmiClient(), server.gameBroker.getLobby(lobbyName));

                CreatedLobbyMessage serverMessage = new CreatedLobbyMessage();
                serverMessage.setName(specificMessage.getLobbyCreator());
                serverMessage.setLobbyName(specificMessage.getLobbyName());
                sendMsgToClient(sender, serverMessage);
            } else if (msg.getType().equals("RetrieveLobbiesMessage") && rmiClientsStates.get(sender) == ClientState.CONNECTED) {
                List<String> lobbies = server.gameBroker.getLobbies();
                RetrievedLobbiesMessage specificMessage = new RetrievedLobbiesMessage();
                specificMessage.setLobbies(lobbies);
                sendMsgToClient(sender, specificMessage);
            } else if (msg.getType().equals("JoinMessage") && rmiClientsStates.get(sender) == ClientState.CONNECTED) {
                JoinMessage specificMessage = (JoinMessage) msg;
                boolean isRejoining = false;
                if (server.gameBroker.getLobbies().contains(specificMessage.getLobbyName()) && server.gameBroker.getLobby(specificMessage.getLobbyName()).getDisconnectedPlayers().contains(specificMessage.getName())) {
                    isRejoining = true;
                }
                server.gameBroker.addPlayer(specificMessage.getLobbyName(), specificMessage.getName());
                rmiClientsStates.put(sender, ClientState.IN_LOBBY);

                rmiClientsName.put(msg.getRmiClient(), specificMessage.getName());
                rmiClientsLobby.put(msg.getRmiClient(), server.gameBroker.getLobby(specificMessage.getLobbyName()));

                JoinedMessage serverMessage = new JoinedMessage();
                serverMessage.setName(specificMessage.getName());
                serverMessage.setLobbyName(specificMessage.getLobbyName());
                sendMsgToClient(sender, serverMessage);
                if (rmiClientsLobby.get(msg.getRmiClient()).getOnlinePlayers().size() == rmiClientsLobby.get(msg.getRmiClient()).getPlayerNumber() && !rmiClientsLobby.get(msg.getRmiClient()).isGameCreated()) {
                    try {
                        rmiClientsLobby.get(msg.getRmiClient()).createGame();
                        server.setInGameStatus(specificMessage.getLobbyName());

                        GameCreatedMessage createdMessage = new GameCreatedMessage();
                        InitialGameInfo info = rmiClientsLobby.get(msg.getRmiClient()).getInitialGameInfo();
                        createdMessage.setGameInfo(info);
                        server.sendMsgToAll(createdMessage, rmiClientsLobby.get(msg.getRmiClient()));

                        UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                        updatedPlayerMessage.setUpdatedPlayer(rmiClientsLobby.get(msg.getRmiClient()).getCurrentPlayer());
                        server.sendMsgToAll(updatedPlayerMessage, rmiClientsLobby.get(msg.getRmiClient()));
                    } catch (GameCreationException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (isRejoining && rmiClientsLobby.get(msg.getRmiClient()).isGameCreated()) {
                    rmiClientsStates.put(sender, ClientState.IN_GAME);

                    GameCreatedMessage createdMessage = new GameCreatedMessage();
                    InitialGameInfo info = rmiClientsLobby.get(msg.getRmiClient()).getInitialGameInfo();
                    createdMessage.setGameInfo(info);
                    sendMsgToClient(sender, createdMessage);

                    UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                    updatedPlayerMessage.setUpdatedPlayer(rmiClientsLobby.get(msg.getRmiClient()).getCurrentPlayer());
                    server.sendMsgToAll(updatedPlayerMessage, rmiClientsLobby.get(msg.getRmiClient()));
                }
            } else if (msg.getType().equals("ChatMessage") && (rmiClientsStates.get(sender) == ClientState.IN_LOBBY || rmiClientsStates.get(sender) == ClientState.IN_GAME)) {
                assert msg instanceof ChatMessage;
                ChatMessage specificMessage = (ChatMessage) msg;
                ChatUpdateMessage serverMessage = new ChatUpdateMessage();
                LocalDateTime timestamp = LocalDateTime.now();
                String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                serverMessage.setTimestamp(formattedTimestamp);
                serverMessage.setContent(specificMessage.getContent());
                serverMessage.setSender(rmiClientsName.get(specificMessage.getRmiClient()));
                server.sendMsgToAll(serverMessage, rmiClientsLobby.get(specificMessage.getRmiClient()));
            } else if (msg.getType().equals("PrivateChatMessage") && (rmiClientsStates.get(sender) == ClientState.IN_LOBBY || rmiClientsStates.get(sender) == ClientState.IN_GAME)) {
                assert msg instanceof PrivateChatMessage;
                PrivateChatMessage specificMessage = (PrivateChatMessage) msg;
                PrivateChatUpdateMessage serverMessage = new PrivateChatUpdateMessage();
                LocalDateTime timestamp = LocalDateTime.now();
                String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                serverMessage.setTimestamp(formattedTimestamp);
                serverMessage.setContent(specificMessage.getContent());
                serverMessage.setSender(rmiClientsName.get(specificMessage.getRmiClient()));
                serverMessage.setReceiver(specificMessage.getReceiver());
                server.sendMsgToAll(serverMessage, rmiClientsLobby.get(specificMessage.getRmiClient()));
            } else if (msg.getType().equals("QuitMessage") && (rmiClientsStates.get(sender) == ClientState.IN_LOBBY || rmiClientsStates.get(sender) == ClientState.IN_GAME)) {
                manageDisconnection(msg.getRmiClient());
            } else if (msg.getType().equals("MoveMessage") && rmiClientsStates.get(sender) == ClientState.IN_GAME) {
                assert msg instanceof MoveMessage;
                MoveMessage specificMessage = (MoveMessage) msg;
                try {
                    rmiClientsLobby.get(msg.getRmiClient()).moveTiles(specificMessage.getTiles(), specificMessage.getColumn(), rmiClientsName.get(msg.getRmiClient()));
                    GameUpdatedMessage updatedMessage = new GameUpdatedMessage();
                    GameInfo info = rmiClientsLobby.get(msg.getRmiClient()).getGameInfo();
                    updatedMessage.setGameInfo(info);
                    server.sendMsgToAll(updatedMessage, rmiClientsLobby.get(msg.getRmiClient()));

                    UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                    updatedPlayerMessage.setUpdatedPlayer(rmiClientsLobby.get(msg.getRmiClient()).getCurrentPlayer());
                    server.sendMsgToAll(updatedPlayerMessage, rmiClientsLobby.get(msg.getRmiClient()));

                    if (info.isGameEnded()) {
                        GameEndedMessage gameEndedMessage = new GameEndedMessage();
                        gameEndedMessage.setGameInfo((FinalGameInfo) info);
                        server.sendMsgToAll(gameEndedMessage, rmiClientsLobby.get(msg.getRmiClient()));
                    }
                } catch (IllegalMoveException e) {
                    sendMsgToClient(sender, new InvalidMoveMessage());
                } catch (GameEndedException ignored) {

                }
            } else {
                sendMsgToClient(sender, new InvalidCommandMessage());
            }
        } catch (ExistingLobbyException e) {
            sendMsgToClient(sender, new ExistingLobbyMessage());
        } catch (NameTakenException e) {
            sendMsgToClient(sender, new NameTakenMessage());
        } catch (FullLobbyException e) {
            sendMsgToClient(sender, new FullLobbyMessage());
        } catch (NonExistingLobbyException e) {
            sendMsgToClient(sender, new NotExistingLobbyMessage());
        } catch (InvalidLobbyNameException e) {
            sendMsgToClient(sender, new InvalidLobbyNameMessage());
        } catch (IllegalPlayerNameException e) {
            sendMsgToClient(sender, new IllegalPlayerNameMessage());
        }
    }

    /**
     * register is the method used to add a new rmi client to the list of the ones using this server
     * @param rmiClient is the interface of the client to add
     * @throws RemoteException whenever an error regarding the player connection happened
     */
    @Override
    public synchronized void register(RMIClientInterface rmiClient) throws RemoteException {
        rmiClients.add(rmiClient);
        rmiClientsStates.put(rmiClient, ClientState.CONNECTED);
        SERVER_LOGGER.log(Level.INFO, "New RMI client connected");
    }

    /**
     * function used to check the server reachability
     * @return true
     * @throws RemoteException if not reachable
     */
    @Override
    public boolean checkAliveness() throws RemoteException {
        return true;
    }

    /**
     * manageDisconnection handles the disconnections of rmi clients.
     * If the lobby where the client was has only a player left the lobby will be closed after a timeout and the remaining
     * player will be disconnected from the lobby.
     * @param rmiClient is the interface of the client that is disconnected
     */
    public synchronized void manageDisconnection(RMIClientInterface rmiClient) {
        Server.SERVER_LOGGER.log(Level.INFO, "DISCONNECTION: RMI client has disconnected");
        Lobby lobby = rmiClientsLobby.get(rmiClient);
        String name = rmiClientsName.get(rmiClient);
        rmiClients.remove(rmiClient);
        rmiClientsStates.remove(rmiClient);
        rmiClientsLobby.remove(rmiClient);
        rmiClientsName.remove(rmiClient);
        try {
            if (lobby != null) {
                String curPlayer = lobby.getCurrentPlayer();
                lobby.disconnectPlayer(lobby.getPlayer(name));

                UserDisconnectedMessage serverMessage = new UserDisconnectedMessage();
                serverMessage.setUser(name);
                if (lobby.isGameCreated()) serverMessage.setCurrentPlayer(lobby.getCurrentPlayer());
                server.sendMsgToAll(serverMessage, lobby);

                if (lobby.checkNumberOfPlayers() && (lobby.isGameCreated() || lobby.getOnlinePlayers().size()==0)) {
                    InsufficientPlayersMessage insufficientPlayersMessage = new InsufficientPlayersMessage();
                    server.sendMsgToAll(insufficientPlayersMessage, lobby);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {

                                if (!lobby.waitForPlayers()) {
                                    server.gameBroker.closeLobby(lobby);
                                    LobbyClosedMessage lobbyClosedMessage = new LobbyClosedMessage();
                                    server.sendMsgToAll(lobbyClosedMessage, lobby);
                                    rmiClientsLobby.values().remove(lobby);
                                }

                        }
                    });
                    t.start();

                } else if (name.equals(curPlayer)) {
                    UpdatedPlayerMessage updateMessage = new UpdatedPlayerMessage();
                    updateMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                    server.sendMsgToAll(updateMessage, lobby);
                }

                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (lobby.checkNumberOfPlayers()) {
                            InsufficientPlayersMessage insufficientPlayersMessage = new InsufficientPlayersMessage();
                            server.sendMsgToAll(insufficientPlayersMessage, lobby);
                            if (!lobby.waitForPlayers()) {
                                server.gameBroker.closeLobby(lobby);
                                LobbyClosedMessage lobbyClosedMessage = new LobbyClosedMessage();
                                server.sendMsgToAll(lobbyClosedMessage, lobby);
                                rmiClientsLobby.values().remove(lobby);
                            }
                        } else if (name.equals(curPlayer)) {
                            UpdatedPlayerMessage updateMessage = new UpdatedPlayerMessage();
                            updateMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                            server.sendMsgToAll(updateMessage, lobby);
                        }
                    }
                });
                t.start();

            }

        } catch (PlayerNotInLobbyException ignored) {

        }

    }

}
