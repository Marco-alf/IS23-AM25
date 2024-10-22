package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;

/**
 * clientHandler is the class server-side class that manages the connection with a client
 */
public class ClientHandler implements Runnable{
    /**
     * PING_TIME is the time period of the checking for disconnected player
     */
    private final int PING_TIME = 1000;
    /**
     * reference to the SocketServer that instantiated the ClientHandler
     */
    private final SocketServer server;
    /**
     * reference to the Server class that links the network to the controller of the server
     */
    private final Server genericServer;
    /**
     * reference to the Socket used for the connection
     */
    private final Socket socket;
    /**
     * the Thread used for handling the forced disconnections
     */
    private final Thread pingThread;
    /**
     * flag that is true if the client is connected
     */
    private boolean activeClient;
    /**
     * reference to the input stream of data from the socket
     */
    private ObjectInputStream inputStream;
    /**
     * reference to the output stream of data directed to the socket
     */
    private ObjectOutputStream outputStream;
    /**
     * name of the client
     */
    private String clientNickname = null;
    /**
     * lobby in which the client plays
     */
    private Lobby lobby = null;
    /**
     * state maintains the state of the client in order to responds to messages accordingly
     */
    private ClientState state = ClientState.CONNECTED;

    /**
     * constructor of a ClientHandler that requires the associated socket and tcp server. It initializes the ping thread
     * and retrive the server that connects to the controller.
     * @param server is the SocketServer that create the socket connections
     * @param socket is the socket use for the connection
     */
    public ClientHandler(SocketServer server, Socket socket) {
        this.server = server;
        genericServer = server.server;
        this.socket = socket;
        pingThread = new Thread(this::pingClient);

    }

    /**
     * getter for the lobby that the player has joined
     * @return the lobby joined by the player
     */
    public synchronized Lobby getLobby() {
        return lobby;
    }

    /**
     * getter for the nickname of the client
     * @return the nickname of the client
     */
    public String getClientNickname() {
        return clientNickname;
    }

    /**
     * setter for the "state" attribute
     * @param state is the new state of the client
     */
    public synchronized void setStatus (ClientState state) {
        this.state = state;
    }

    /**
     * method used to handle all the commands and event related to the connection.
     * ClientHandler.run() is the real link between a socket client and the server
     */
    @Override
    public void run() {
        try{
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            activeClient = true;

            pingThread.start();

            while(activeClient){
                try{
                    Object object = inputStream.readObject();
                    synchronized (this){
                        Message msg = (Message) object;
                        if (msg.getType().equals("CreateLobbyMessage") && state == ClientState.CONNECTED) {
                            CreateLobbyMessage specificMessage = (CreateLobbyMessage) msg;
                            String lobbyName = specificMessage.getLobbyName();
                            String lobbyCreator = specificMessage.getLobbyCreator();
                            int numPlayers = specificMessage.getPlayerNumber();
                            server.gameBroker.createLobby(lobbyCreator, lobbyName, numPlayers);

                            state = ClientState.IN_LOBBY;
                            clientNickname = lobbyCreator;
                            lobby = server.gameBroker.getLobby(lobbyName);

                            CreatedLobbyMessage serverMessage = new CreatedLobbyMessage();
                            serverMessage.setName(specificMessage.getLobbyCreator());
                            serverMessage.setLobbyName(specificMessage.getLobbyName());
                            sendMsgToClient(serverMessage);
                        }
                        if (msg.getType().equals("RetrieveLobbiesMessage") && state == ClientState.CONNECTED) {
                            List<String> lobbies = server.gameBroker.getLobbies();
                            RetrievedLobbiesMessage message = new RetrievedLobbiesMessage();
                            message.setLobbies(lobbies);
                            sendMsgToClient(message);
                        }
                        if (msg.getType().equals("JoinMessage") && state == ClientState.CONNECTED) {
                            JoinMessage specificMessage = (JoinMessage) msg;
                            boolean isRejoining = false;
                            if (server.gameBroker.getLobbies().contains(specificMessage.getLobbyName()) && server.gameBroker.getLobby(specificMessage.getLobbyName()).getDisconnectedPlayers().contains(specificMessage.getName())) {
                                isRejoining = true;
                            }
                            server.gameBroker.addPlayer(specificMessage.getLobbyName(), specificMessage.getName());

                            state = ClientState.IN_LOBBY;
                            clientNickname = specificMessage.getName();
                            lobby = server.gameBroker.getLobby(specificMessage.getLobbyName());

                            JoinedMessage serverMessage = new JoinedMessage();
                            serverMessage.setName(specificMessage.getName());
                            serverMessage.setLobbyName(specificMessage.getLobbyName());
                            sendMsgToClient(serverMessage);
                            if (lobby.getOnlinePlayers().size() == lobby.getPlayerNumber() && !lobby.isGameCreated()) {
                                try {
                                    lobby.createGame();
                                    genericServer.setInGameStatus(specificMessage.getLobbyName());
                                    GameCreatedMessage createdMessage = new GameCreatedMessage();
                                    InitialGameInfo info = lobby.getInitialGameInfo();
                                    createdMessage.setGameInfo(info);
                                    genericServer.sendMsgToAll(createdMessage, lobby);

                                    UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                                    updatedPlayerMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                                    genericServer.sendMsgToAll(updatedPlayerMessage, lobby);
                                } catch (GameCreationException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (isRejoining && lobby.isGameCreated()) {
                                state = ClientState.IN_GAME;
                                GameCreatedMessage createdMessage = new GameCreatedMessage();
                                InitialGameInfo info = lobby.getInitialGameInfo();
                                createdMessage.setGameInfo(info);
                                sendMsgToClient(createdMessage);

                                UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                                updatedPlayerMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                                genericServer.sendMsgToAll(updatedPlayerMessage, lobby);
                            }
                        }
                        if (msg.getType().equals("ChatMessage")  && (state == ClientState.IN_LOBBY || state == ClientState.IN_GAME)) {
                            assert msg instanceof ChatMessage;
                            ChatMessage specificMessage = (ChatMessage) msg;
                            ChatUpdateMessage serverMessage = new ChatUpdateMessage();
                            LocalDateTime timestamp = LocalDateTime.now();
                            String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            serverMessage.setTimestamp(formattedTimestamp);
                            serverMessage.setContent(specificMessage.getContent());
                            serverMessage.setSender(clientNickname);
                            genericServer.sendMsgToAll(serverMessage, lobby);
                        }
                        if (msg.getType().equals("PrivateChatMessage")  && (state == ClientState.IN_LOBBY || state == ClientState.IN_GAME)) {
                            assert msg instanceof PrivateChatMessage;
                            PrivateChatMessage specificMessage = (PrivateChatMessage) msg;
                            PrivateChatUpdateMessage serverMessage = new PrivateChatUpdateMessage();
                            LocalDateTime timestamp = LocalDateTime.now();
                            String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            serverMessage.setTimestamp(formattedTimestamp);
                            serverMessage.setContent(specificMessage.getContent());
                            serverMessage.setSender(clientNickname);
                            serverMessage.setReceiver(specificMessage.getReceiver());
                            genericServer.sendMsgToAll(serverMessage, lobby);
                        }
                        if (msg.getType().equals("QuitMessage") && (state == ClientState.IN_LOBBY || state == ClientState.IN_GAME)) {
                            manageDisconnection();
                        }
                        if (msg.getType().equals("MoveMessage") && state == ClientState.IN_GAME) {
                            assert msg instanceof MoveMessage;
                            try {
                                lobby.moveTiles(((MoveMessage) msg).getTiles(), ((MoveMessage) msg).getColumn(), clientNickname);
                                GameInfo info = lobby.getGameInfo();
                                GameUpdatedMessage updatedMessage = new GameUpdatedMessage();

                                updatedMessage.setGameInfo(info);
                                genericServer.sendMsgToAll(updatedMessage, lobby);

                                UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                                updatedPlayerMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                                genericServer.sendMsgToAll(updatedPlayerMessage, lobby);
                                if(info.isGameEnded()){
                                    GameEndedMessage gameEndedMessage = new GameEndedMessage();
                                    gameEndedMessage.setGameInfo((FinalGameInfo) info);
                                    genericServer.sendMsgToAll(gameEndedMessage, lobby);
                                }
                            } catch (IllegalMoveException e) {
                                sendMsgToClient(new InvalidMoveMessage());
                            } catch (GameEndedException ignored) {

                            }
                        }
                        if(!msg.getType().equals(new Ping().getType())){
                            System.out.println("Received " + msg.getType() + " from Socket client");
                        }
                    }
                } catch (ClassNotFoundException | SocketTimeoutException e) {
                    manageDisconnection();
                } catch (ExistingLobbyException e) {
                    sendMsgToClient(new ExistingLobbyMessage());
                } catch (NameTakenException e) {
                    sendMsgToClient(new NameTakenMessage());
                } catch (FullLobbyException e) {
                    sendMsgToClient(new FullLobbyMessage());
                } catch (NonExistingLobbyException e) {
                    sendMsgToClient(new NotExistingLobbyMessage());
                } catch (InvalidLobbyNameException e) {
                    sendMsgToClient(new InvalidLobbyNameMessage());
                } catch (IllegalPlayerNameException e) {
                    sendMsgToClient(new IllegalPlayerNameMessage());
                }
            }

        } catch (IOException e) {
            //manageDisconnection();
        }
    }

    /**
     * method used to send a message to the client associated with the clientHandler
     * @param msg is the message that is sent
     */
    public synchronized void sendMsgToClient(Serializable msg){
        assert (msg instanceof ServerMessage) || (msg instanceof Ping);
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            if(((Message)msg).getType().equals("Ping"))manageDisconnection();
        }
        Message temp = (Message) msg;
        if(!temp.getType().equals(new Ping().getType())) {
            System.out.println("Sent " + ((Message) msg).getType() + " to Socket client");
        }
    }

    /**
     * pingClient is the method that the server uses to check the availability of the connection with the client
     */
    public void pingClient () {
        while (activeClient) {
            try {
                sendMsgToClient(new Ping());
                Thread.sleep(PING_TIME);
            } catch (InterruptedException ignored) {

            }
        }

    }

    /**
     * method used to manage a regular disconnection of a player.
     * If the lobby where the client was has only a player left the lobby will be closed after a timeout and the remaining
     * player will be disconnected from the lobby.
     */
    public synchronized void manageDisconnection() {
        if (activeClient){
            activeClient = false;
            Server.SERVER_LOGGER.log(Level.INFO, "DISCONNECTION: client " + socket.getInetAddress().getHostAddress() + " has disconnected");
            disconnect();
            server.removeClient(this);

            try {
                if (lobby != null) {
                    String curPlayer = lobby.getCurrentPlayer();
                    lobby.disconnectPlayer(lobby.getPlayer(clientNickname));

                    UserDisconnectedMessage serverMessage = new UserDisconnectedMessage();
                    serverMessage.setUser(clientNickname);
                    if (lobby.isGameCreated()) serverMessage.setCurrentPlayer(lobby.getCurrentPlayer());
                    genericServer.sendMsgToAll(serverMessage, lobby);

                    if (lobby.checkNumberOfPlayers() && (lobby.isGameCreated() || lobby.getOnlinePlayers().size()==0)) {
                        InsufficientPlayersMessage insufficientPlayersMessage = new InsufficientPlayersMessage();
                        genericServer.sendMsgToAll(insufficientPlayersMessage, lobby);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                    if (!lobby.waitForPlayers()) {
                                        server.gameBroker.closeLobby(lobby);
                                        LobbyClosedMessage lobbyClosedMessage = new LobbyClosedMessage();
                                        genericServer.sendMsgToAll(lobbyClosedMessage, lobby);
                                    }
                            }

                        });
                        t.start();

                    } else if (clientNickname.equals(curPlayer)) {
                        UpdatedPlayerMessage updateMessage = new UpdatedPlayerMessage();
                        updateMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                        genericServer.sendMsgToAll(updateMessage, lobby);
                    }


                }

            } catch (PlayerNotInLobbyException ignored) {

            }


        }

    }

    /**
     * method used to close all the communication stream and the socket itself
     */
    public synchronized void disconnect() {
        activeClient = false;
        try {
            inputStream.close();
        } catch (IOException ignored){}
        try {
            outputStream.close();
        } catch (IOException ignored){}
        try {
            socket.close();
        } catch (IOException ignored){}
    }

}