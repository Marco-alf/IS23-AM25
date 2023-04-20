package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.network.server.RMIClientInterface;
import it.polimi.ingsw.view.TextualUI;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

/** rmi based implementation of the client */
public class RMIClient extends GenericClient implements RMIClientInterface {
    /** server reference object, has methods to register and to receive messages */
    private RMIServerInterface rmiServerInterface;
    /** server ip */
    private final String ip;
    /** server port*/
    private final int port;
    /** view to be bound*/
    private final TextualUI view;
    /** ping frequency */
    private final int PING_TIME = 2000;
    /** boolean represents the status of the connection (true means connected) */
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);
    private final Thread pingThread;
    /** constructor sets the parameters and launches the ping thread
     * @param ip server ip address
     * @param port server port number
     * @param view view to be bound
     */
    public RMIClient (String ip, int port, TextualUI view) {
        this.ip = ip;
        this.port = port;
        this.view = view;

        pingThread = new Thread(this::checkServerAliveness);
    }

    /**
     * initializes the rmi connection, locating the remote server object and running the register method with itself as
     * the parameter, then sets the connectedStatus to true */
    public void init() {
        try{
            rmiServerInterface = (RMIServerInterface) LocateRegistry.getRegistry(ip, port).lookup(RMIServerInterface.NAME);
            rmiServerInterface.register((RMIClientInterface) UnicastRemoteObject.exportObject(this, 0));
            clientConnected.set(true);
            pingThread.start();
        } catch (NotBoundException | RemoteException e) {
            disconnect(true);
        }
    }

    /** sends through rmi the serializable message
     * @param arg is the message to send */
    @Override
    public void sendMsgToServer (Serializable arg) {
        try {
            rmiServerInterface.receiveMsgFromClient(arg);
        } catch (RemoteException e) {
            disconnect(true);
        }
    }

    public void checkServerAliveness () {
        while (clientConnected.get()) {
            try {
                rmiServerInterface.checkAliveness();
                Thread.sleep(PING_TIME);
            } catch (RemoteException e) {
                disconnect(true);
            } catch (InterruptedException ignored) {

            }
        }
    }

    /** this method handles all the interpretation of the message received from the server, and propagates the right
     * commands to the bound view */
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
            if (msg.getType().equals("GameCreatedMessage")) {
                assert msg instanceof GameCreatedMessage;
                view.updateView(((GameCreatedMessage) msg).getGameInfo());
                view.displayGameInfo();
            }
            if (msg.getType().equals("GameUpdatedMessage")) {
                assert msg instanceof GameUpdatedMessage;
                view.updateView(((GameUpdatedMessage) msg).getGameInfo());
                view.displayGameInfo();
            }
            if (msg.getType().equals("UpdatedPlayerMessage")) {
                assert msg instanceof UpdatedPlayerMessage;
                view.displayServerMsg("It's " + ((UpdatedPlayerMessage) msg).getUpdatedPlayer() + "'s turn");
            }
            if (msg.getType().equals("InvalidMoveMessage")) {
                view.displayServerMsg("Move is not valid");
            }
        }

    }

    @Override
    public boolean checkAliveness() throws RemoteException {
        return true;
    }

    /**
     * signals the disconnection or failed connection to the view and resets the server reference.
     * @param error true if the reason for disconnection is an error */
    public void disconnect(boolean error) {
        clientConnected.set(false);
        isInLobby = false;
        rmiServerInterface = null;
        if (error) view.displayServerMsg("An error occurred during the communication with the server, you're being disconnected! See ya!");
    }

}
