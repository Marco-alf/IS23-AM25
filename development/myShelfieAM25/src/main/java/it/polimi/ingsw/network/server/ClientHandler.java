package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Game;
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
    private String clientNickname;
    /**
     * lobby in which the client plays
     */
    private Lobby lobby;
    private Game game;

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
                    if(!(object instanceof Ping)) {
                        ClientMessage msg = (ClientMessage) object;
                        if (msg.getType().equals("CreateLobbyMessage")) {
                            CreateLobbyMessage specificMessage = (CreateLobbyMessage) msg;
                            String lobbyName = specificMessage.getLobbyName();
                            String lobbyCreator = specificMessage.getLobbyCreator();
                            int numPlayers = specificMessage.getPlayerNumber();
                            server.gameBroker.createLobby(lobbyCreator, lobbyName, numPlayers);

                            clientNickname = lobbyCreator;
                            lobby = server.gameBroker.getLobby(lobbyName);

                            CreatedLobbyMessage serverMessage = new CreatedLobbyMessage();
                            serverMessage.setName(specificMessage.getLobbyCreator());
                            serverMessage.setLobbyName(specificMessage.getLobbyName());
                            sendMsgToClient(serverMessage);
                        }
                        if (msg.getType().equals("RetrieveLobbiesMessage")) {
                            List<String> lobbies = server.gameBroker.getLobbies();
                            RetrievedLobbiesMessage message = new RetrievedLobbiesMessage();
                            message.setLobbies(lobbies);
                            sendMsgToClient(message);
                        }
                        if (msg.getType().equals("JoinMessage")) {
                            JoinMessage specificMessage = (JoinMessage) msg;
                            server.gameBroker.addPlayer(specificMessage.getLobbyName(), specificMessage.getName());

                            clientNickname = specificMessage.getName();
                            lobby = server.gameBroker.getLobby(specificMessage.getLobbyName());

                            JoinedMessage serverMessage = new JoinedMessage();
                            serverMessage.setName(specificMessage.getName());
                            serverMessage.setLobbyName(specificMessage.getLobbyName());
                            sendMsgToClient(serverMessage);
                            if (lobby.getOnlinePlayers().size() == lobby.getPlayerNumber()) {
                                try {
                                    GameCreatedMessage createdMessage = new GameCreatedMessage();
                                    lobby.createGame();

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
                        }
                        if (msg.getType().equals("ChatMessage")) {
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
                        if (msg.getType().equals("MoveMessage")) {
                            assert msg instanceof MoveMessage;
                            try {
                                lobby.moveTiles(((MoveMessage) msg).getTiles(), ((MoveMessage) msg).getColumn(), clientNickname);
                                GameUpdatedMessage updatedMessage = new GameUpdatedMessage();
                                GameInfo info = lobby.getGameInfo();
                                updatedMessage.setGameInfo(info);
                                genericServer.sendMsgToAll(updatedMessage, lobby);

                                UpdatedPlayerMessage updatedPlayerMessage = new UpdatedPlayerMessage();
                                updatedPlayerMessage.setUpdatedPlayer(lobby.getCurrentPlayer());
                                genericServer.sendMsgToAll(updatedPlayerMessage, lobby);
                            } catch (IllegalMoveException e) {
                                sendMsgToClient(new InvalidMoveMessage());
                            }

                        }
                    }
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