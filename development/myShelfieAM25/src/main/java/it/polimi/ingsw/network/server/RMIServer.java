package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.network.client.RMIServerInterface;
import it.polimi.ingsw.network.messages.clientMessages.ChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.ClientMessage;
import it.polimi.ingsw.network.messages.clientMessages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.clientMessages.JoinMessage;
import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;

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

public class RMIServer implements Runnable, RMIServerInterface{
    private final Server server;
    private final int PING_TIME = 5000;
    private final List<RMIClientInterface> rmiClients = new ArrayList<>();
    private final Map<RMIClientInterface, String> rmiClientsName = new HashMap<>();
    private final Map<RMIClientInterface, Lobby> rmiClientsLobby = new HashMap<>();
    private final int port;
    private final Thread pingThread;
    public RMIServer (Server server, int port) {
        this.server = server;
        this.port = port;

        pingThread = new Thread(()->{
            while (true){
                try {
                    Thread.sleep(PING_TIME);
                    sendMsgToAllRMI(new Ping());
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

    @Override
    public void run() {
        try {
            System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
            RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(this, 0);
            LocateRegistry.createRegistry(port);
            LocateRegistry.getRegistry(port).bind(RMIServerInterface.NAME, stub);
            SERVER_LOGGER.info("RmiServer started.");
            pingThread.start();
        } catch (UnknownHostException | AlreadyBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsgToAllRMI (Serializable msg, Lobby lobby) {
        for (RMIClientInterface rmiClient : rmiClients) {
            try {
                if (rmiClientsLobby.get(rmiClient) == lobby) {
                    rmiClient.receiveMsgFromServer(msg);
                }
            } catch (RemoteException e) {
                manageDisconnection(rmiClient);
            }
        }
    }

    public void sendMsgToAllRMI (Serializable msg) {
        for (RMIClientInterface rmiClient : rmiClients) {
            try {
                rmiClient.receiveMsgFromServer(msg);
            } catch (RemoteException e) {
                manageDisconnection(rmiClient);
            }
        }
    }

    public void sendMsgToClient (RMIClientInterface rmiClient, ServerMessage msg) {
        try {
            rmiClient.receiveMsgFromServer(msg);
        } catch (RemoteException e) {
            manageDisconnection(rmiClient);
        }
    }

    @Override
    public void receiveMsgFromClient (Serializable arg) {
        ClientMessage msg = (ClientMessage) arg;
        RMIClientInterface sender = msg.getRmiClient();
        try {
            if (msg.getType().equals("CreateLobbyMessage")) {
                CreateLobbyMessage specificMessage = (CreateLobbyMessage) msg;
                String lobbyName = specificMessage.getLobbyName();
                String lobbyCreator = specificMessage.getLobbyCreator();
                int numPlayers = specificMessage.getPlayerNumber();
                server.gameBroker.createLobby(lobbyCreator, lobbyName, numPlayers);

                rmiClientsName.put(msg.getRmiClient(), lobbyCreator);
                rmiClientsLobby.put(msg.getRmiClient(), server.gameBroker.getLobby(lobbyName));

                CreatedLobbyMessage serverMessage = new CreatedLobbyMessage();
                serverMessage.setName(specificMessage.getLobbyCreator());
                serverMessage.setLobbyName(specificMessage.getLobbyName());
                sendMsgToClient(sender, serverMessage);
            }
            if (msg.getType().equals("RetrieveLobbiesMessage")) {
                List<String> lobbies = server.gameBroker.getLobbies();
                RetrievedLobbiesMessage specificMessage = new RetrievedLobbiesMessage();
                specificMessage.setLobbies(lobbies);
                sendMsgToClient(sender, specificMessage);
            }
            if (msg.getType().equals("JoinMessage")) {
                JoinMessage specificMessage = (JoinMessage) msg;
                server.gameBroker.addPlayer(specificMessage.getLobbyName(), specificMessage.getName());

                rmiClientsName.put(msg.getRmiClient(), specificMessage.getName());
                rmiClientsLobby.put(msg.getRmiClient(), server.gameBroker.getLobby(specificMessage.getLobbyName()));

                JoinedMessage serverMessage = new JoinedMessage();
                serverMessage.setName(specificMessage.getName());
                serverMessage.setLobbyName(specificMessage.getLobbyName());
                sendMsgToClient(sender, serverMessage);
            }
            if (msg.getType().equals("ChatMessage")) {
                assert msg instanceof ChatMessage;
                ChatMessage specificMessage = (ChatMessage) msg;
                ChatUpdateMessage serverMessage = new ChatUpdateMessage();
                LocalDateTime timestamp = LocalDateTime.now();
                String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                serverMessage.setTimestamp(formattedTimestamp);
                serverMessage.setContent(specificMessage.getContent());
                serverMessage.setSender(rmiClientsName.get(specificMessage.getRmiClient()));
                server.sendMsgToAll(serverMessage, rmiClientsLobby.get(specificMessage.getRmiClient()));
            }
        } catch (ExistingLobbyException e) {
            sendMsgToClient(sender, new ExistingLobbyMessage());
        } catch (NameTakenException e) {
            sendMsgToClient(sender, new NameTakenMessage());
        } catch (FullLobbyException e) {
            sendMsgToClient(sender, new FullLobbyMessage());
        } catch (NonExistingLobbyException e) {
            sendMsgToClient(sender, new NotExistingLobbyMessage());
        }
    }

    @Override
    public void register(RMIClientInterface rmiClient) throws RemoteException {
        rmiClients.add(rmiClient);
        SERVER_LOGGER.log(Level.INFO, "New RMI client connected");
    }

    public void manageDisconnection(RMIClientInterface rmiClient) {

            Server.SERVER_LOGGER.log(Level.INFO, "DISCONNECTION: RMI client has disconnected");
            Lobby lobby = rmiClientsLobby.get(rmiClient);
            String name = rmiClientsName.get(rmiClient);
            try {
                lobby.disconnectPlayer(lobby.getPlayer(name));
                rmiClients.remove(rmiClient);
                rmiClientsLobby.remove(rmiClient);
                rmiClientsName.remove(rmiClient);
            } catch (PlayerNotInLobbyException e) {
                throw new RuntimeException(e);
            }


    }
}
