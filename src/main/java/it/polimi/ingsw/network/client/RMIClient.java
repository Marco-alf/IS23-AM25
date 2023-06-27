package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.network.server.RMIClientInterface;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/** rmi based implementation of the client */
public class RMIClient extends GenericClient implements RMIClientInterface {
    /**
     * MIN_PORT_NUMBER is the minimum number acceptable for a network port
     */
    private static int MIN_PORT_NUMBER = 49152;
    /**
     * MAX_PORT_NUMBER is the maximum number acceptable for a network port
     */
    private static int MAX_PORT_NUMBER = 65535;
    /**
     * clientport is the number of the port used for the network connection
     */
    private static int clientport = 65535;
    /** server reference object, has methods to register and to receive messages */
    private RMIServerInterface rmiServerInterface;
    /** server ip */
    private final String ip;
    /** server port*/
    private final int port;
    /** view to be bound*/
    private final ViewInterface view;
    /** ping frequency */
    private final int PING_TIME = 1000;
    /** boolean represents the status of the connection (true means connected) */
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);
    /**
     * pingThread is a runnable used to continuously check the connection with the server
     */
    private final Thread pingThread;
    /** constructor sets the parameters and launches the ping thread
     * @param ip server ip address
     * @param port server port number
     * @param view view to be bound
     */
    public RMIClient (String ip, int port, ViewInterface view) {
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

            Random gen = new Random();
            do {
                clientport = gen.nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
            }while(!available(clientport));

            rmiServerInterface.register((RMIClientInterface) UnicastRemoteObject.exportObject(this, clientport));
            clientConnected.set(true);
            pingThread.start();
        } catch (NotBoundException | RemoteException e) {
            disconnect(true);
        }
    }

    /**
     * From apache camel project: Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean available(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    /** sends through rmi the serializable message
     * @param arg is the message to send */
    @Override
    public void sendMsgToServer (Serializable arg) {
        new Thread(()->{
            try {
                rmiServerInterface.receiveMsgFromClient(arg);
            } catch (RemoteException e) {
                disconnect(true);
            }
        }).start();
        /*try {
            rmiServerInterface.receiveMsgFromClient(arg);
        } catch (RemoteException e) {
            disconnect(true);
        }*/
    }

    /**
     * checkServerAliveness is a method used to check if the connection with the server is active
     */
    public void checkServerAliveness () {
        int count = 0;
        while (clientConnected.get()) {
            try {
                rmiServerInterface.checkAliveness();
                count = 0;
            } catch (RemoteException e) {
                count++;
                if(count > 4) disconnect(true);
            }
            try{
                Thread.sleep(1000);
            }catch (InterruptedException ignored) {

            }
        }
    }

    /** this method handles all the interpretation of the message received from the server, and propagates the right
     * commands to the bound view */
    @Override
    public void receiveMsgFromServer(Serializable arg) {
        //System.out.println("Received message from Server: "+ ((Message)arg).getType());
        if (arg instanceof ServerMessage msg) {
            if (msg.getType().equals("CreatedLobbyMessage")) {
                isInLobby = true;
                view.receiveCreatedLobbyMsg((CreatedLobbyMessage) msg);
            }
            if (msg.getType().equals("JoinedMessage")) {
                isInLobby = true;
                assert msg instanceof JoinedMessage;
                view.receiveJoinedMsg((JoinedMessage) msg);
            }
            if (msg.getType().equals("ExistingLobbyMessage")) {
                view.receiveExistingLobbyMsg((ExistingLobbyMessage) msg);
            }
            if (msg.getType().equals("LobbyNotCreatedMessage")) {
                view.receiveLobbyNotCreatedMsg((LobbyNotCreatedMessage) msg);
            }
            if (msg.getType().equals("NameTakenMessage")) {
                view.receiveNameTakenMsg((NameTakenMessage) msg);
            }
            if (msg.getType().equals("NotExistingLobbyMessage")) {
                view.receiveNotExistingLobbyMsg((NotExistingLobbyMessage) msg);
            }
            if (msg.getType().equals("FullLobbyMessage")) {
                view.receiveFullLobbyMsg((FullLobbyMessage) msg);
            }
            if (msg.getType().equals("RetrievedLobbiesMessage")) {
                view.receiveRetrievedLobbiesMsg((RetrievedLobbiesMessage) msg);
            }
            if (msg.getType().equals("ChatUpdateMessage")) {
                assert msg instanceof ChatUpdateMessage;
                view.receiveChatUpdateMsg((ChatUpdateMessage) msg);
            }
            if (msg.getType().equals("PrivateChatUpdateMessage")) {
                assert msg instanceof ChatUpdateMessage;
                view.receivePrivateChatUpdateMsg((PrivateChatUpdateMessage) msg);
            }
            if (msg.getType().equals("GameCreatedMessage")) {
                assert msg instanceof GameCreatedMessage;
                view.receiveGameCreatedMsg((GameCreatedMessage) msg);
            }
            if (msg.getType().equals("GameUpdatedMessage")) {
                assert msg instanceof GameUpdatedMessage;
                view.receiveGameUpdatedMsg((GameUpdatedMessage) msg);
            }
            if (msg.getType().equals("GameEndedMessage")) {
                assert msg instanceof GameEndedMessage;
                view.receiveGameEndedMsg((GameEndedMessage) msg);
            }
            if (msg.getType().equals("UpdatedPlayerMessage")) {
                assert msg instanceof UpdatedPlayerMessage;
                view.receiveUpdatedPlayerMsg((UpdatedPlayerMessage) msg);
            }
            if (msg.getType().equals("InvalidMoveMessage")) {
                view.receiveInvalidMoveMsg((InvalidMoveMessage) msg);
            }
            if (msg.getType().equals("InsufficientPlayersMessage")) {
                view.receiveInsufficientPlayersMsg((InsufficientPlayersMessage) msg);
            }
            if (msg.getType().equals("LobbyClosedMessage")) {
                view.receiveLobbyClosedMsg((LobbyClosedMessage) msg);
                isInLobby = false; /////////////////
            }
            if (msg.getType().equals("UserDisconnectedMessage")) {
                view.receiveUserDisconnectedMsg((UserDisconnectedMessage) msg);
            }
            if (msg.getType().equals("InvalidCommandMessage")) {
                view.receiveInvalidCommandMsg((InvalidCommandMessage) msg);
            }
            if (msg.getType().equals("InvalidLobbyNameMessage")) {
                view.receiveInvalidLobbyNameMsg((InvalidLobbyNameMessage) msg);
            }
            if (msg.getType().equals("IllegalPlayerNameMessage")) {
                view.receiveIllegalPlayerNameMsg((IllegalPlayerNameMessage) msg);
            }
        }

    }

    /**
     * checkAliveness is the method invoked by the server in order to check the reachability of the client
     * @return true
     * @throws RemoteException when unreachable
     */
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
        if (error) view.receiveConnectionErrorMsg(new ConnectionErrorMessage());
    }

}
