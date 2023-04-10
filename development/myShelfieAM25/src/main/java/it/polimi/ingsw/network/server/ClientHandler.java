package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.ExistingLobbyException;
import it.polimi.ingsw.exception.FullLobbyException;
import it.polimi.ingsw.exception.NameTakenException;
import it.polimi.ingsw.exception.NonExistingLobbyException;
import it.polimi.ingsw.network.messages.clientMessages.ChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.ClientMessage;
import it.polimi.ingsw.network.messages.clientMessages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.clientMessages.JoinMessage;
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
    private final int PING_TIME = 5000;
    private final SocketServer server;
    private final Server genericServer;
    private final Socket socket;
    private final Thread pingThread;
    private boolean activeClient;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String clientNickname;
    private Lobby lobby;

    public ClientHandler(SocketServer server, Socket socket) {
        this.server = server;
        genericServer = server.server;
        this.socket = socket;
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
    }

    public Lobby getLobby() {
        return lobby;
    }

    public String getClientNickname() {
        return clientNickname;
    }

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

    public void manageDisconnection() {
        if (activeClient){
            activeClient = false;
            Server.SERVER_LOGGER.log(Level.INFO, "DISCONNECTION: client " + socket.getInetAddress().getHostAddress() + " has disconnected");
            server.removeClient(this);
            disconnect();
        }

    }

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