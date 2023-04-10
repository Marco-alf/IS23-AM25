package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.network.server.RMIClientInterface;
import it.polimi.ingsw.view.TextualUI;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

public class RMIClient extends GenericClient implements RMIClientInterface {
    private RMIServerInterface rmiServerInterface;
    private final String ip;
    private final int port;
    private final TextualUI view;
    private final int PING_TIME = 5000;
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);
    private final Thread pingThread;
    public RMIClient (String ip, int port, TextualUI view) {
        this.ip = ip;
        this.port = port;
        this.view = view;

        pingThread = new Thread(() -> {
            while (clientConnected.get()) {
                try {
                    Thread.sleep(PING_TIME);
                    sendMsgToServer(new Ping());
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        });
    }
    public void init() {
        try{
            rmiServerInterface = (RMIServerInterface) LocateRegistry.getRegistry(ip, port).lookup(RMIServerInterface.NAME);
            rmiServerInterface.register((RMIClientInterface) UnicastRemoteObject.exportObject(this, 0));
            clientConnected.set(true);
        } catch (NotBoundException | RemoteException e) {
            disconnect(true);
        }
    }
    @Override
    public void sendMsgToServer (Serializable arg) {
        try {
            rmiServerInterface.receiveMsgFromClient(arg);
        } catch (RemoteException e) {
            disconnect(true);
        }
    }
    @Override
    public void receiveMsgFromServer(Serializable arg) {
        if (arg instanceof ServerMessage msg) {
            if (msg.getType().equals("CreatedLobbyMessage")) {
                isInLobby = true;
                view.displayCreatedLobbyMsg((CreatedLobbyMessage) msg);
            }
            if (msg.getType().equals("JoinedMessage")) {
                isInLobby = true;
                assert msg instanceof JoinedMessage;
                view.displayJoinedMsg((JoinedMessage) msg);
            }
            if (msg.getType().equals("ExistingLobbyMessage")) {
                view.displayServerMsg("A lobby with this name already exists");
            }
            if (msg.getType().equals("LobbyNotCreatedMessage")) {
                view.displayServerMsg("Lobby has not been created");
            }
            if (msg.getType().equals("NameTakenMessage")) {
                view.displayServerMsg("Name is already taken, insert another one");
            }
            if (msg.getType().equals("NotExistingLobbyMessage")) {
                view.displayServerMsg("This lobby does not exist, try another one or creating a new lobby");
            }
            if (msg.getType().equals("FullLobbyMessage")) {
                view.displayServerMsg("Lobby is full");
            }
            if (msg.getType().equals("RetrievedLobbiesMessage")) {
                RetrievedLobbiesMessage specificMessage = (RetrievedLobbiesMessage) msg;
                view.displayLobbies(specificMessage.getLobbies());
            }
            if (msg.getType().equals("ChatUpdateMessage")) {
                assert msg instanceof ChatUpdateMessage;
                view.addMessage((ChatUpdateMessage) msg);
            }
        }

    }

    public void disconnect(boolean error) {
        clientConnected.set(false);
        rmiServerInterface = null;
        if (error) view.displayServerMsg("An error occurred during the communication with the server, you're being disconnected! See ya!");
    }

}
