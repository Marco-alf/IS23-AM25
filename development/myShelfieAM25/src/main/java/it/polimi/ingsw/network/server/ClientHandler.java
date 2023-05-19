package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;

public class ClientHandler implements Runnable{
    /**
     * PING_TIME is the time period of the checking for disconnected player
     */
    private final int PING_TIME = 5000;
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
    //private final Thread pingThread;
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
        /*
        pingThread = new Thread(()->{
            while (activeClient){
                try {
                    Thread.sleep(PING_TIME);
                    sendMsgToClient(new Ping());
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

         */
    }

    /**
     * getter for the lobby that the player has joined
     * @return the lobby joined by the player
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * getter for the nickname of the client
     * @return the nickname of the client
     */
    public String getClientNickname() {
        return clientNickname;
    }

    public void setStatus (ClientState state) {
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

            //pingThread.start();

            while(activeClient){
                try{
                    Object object = inputStream.readObject();
                    //if(!(object instanceof Ping)) {
                        ClientMessage msg = (ClientMessage) object;
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
                            if (isRejoining) {
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
                    //}
                } catch (ClassNotFoundException e) {
                    manageDisconnection();
                } catch (ExistingLobbyException e) {
                    sendMsgToClient(new ExistingLobbyMessage());
                } catch (NameTakenException e) {
                    sendMsgToClient(new NameTakenMessage());
                } catch (FullLobbyException e) {
                    sendMsgToClient(new FullLobbyMessage());
                } catch (NonExistingLobbyException e) {
                    sendMsgToClient(new NotExistingLobbyMessage());
                }

            }

        } catch (IOException e) {
            manageDisconnection();
        }
    }

    /**
     * method used to send a message to the client associated with the clientHandler
     * @param msg is the message that is sent
     */
    public void sendMsgToClient(Serializable msg){
        assert (msg instanceof ServerMessage) || (msg instanceof Ping);
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            manageDisconnection();
        }
    }

    /**
     * method used to manage a regular disconnection of a player.
     */
    public void manageDisconnection() {
        if (activeClient){
            activeClient = false;
            Server.SERVER_LOGGER.log(Level.INFO, "DISCONNECTION: client " + socket.getInetAddress().getHostAddress() + " has disconnected");
            disconnect();
            server.removeClient(this);

            try {
                if (lobby != null) {
                    lobby.disconnectPlayer(lobby.getPlayer(clientNickname));

                    UserDisconnectedMessage serverMessage = new UserDisconnectedMessage();
                    serverMessage.setUser(clientNickname);
                    if (lobby.isGameCreated()) serverMessage.setCurrentPlayer(lobby.getCurrentPlayer());
                    genericServer.sendMsgToAll(serverMessage, lobby);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (lobby.checkNumberOfPlayers()) {
                                InsufficientPlayersMessage insufficientPlayersMessage = new InsufficientPlayersMessage();
                                genericServer.sendMsgToAll(insufficientPlayersMessage, lobby);
                                if (!lobby.waitForPlayers()) {
                                    LobbyClosedMessage lobbyClosedMessage = new LobbyClosedMessage();
                                    genericServer.sendMsgToAll(lobbyClosedMessage, lobby);
                                    server.gameBroker.closeLobby(lobby);

                                }
                            }
                        }
                    });
                    t.start();

                }

            } catch (PlayerNotInLobbyException ignored) {

            }


        }

    }

    /**
     * method used to close all the communication stream and the socket itself
     */
    public void disconnect() {
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