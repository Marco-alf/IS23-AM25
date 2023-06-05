package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameBroker;
import it.polimi.ingsw.controller.Lobby;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * This class represents the server. It is used to start and close the games,
 * and to manage clients that connect to play.
 */
public class Server implements Runnable{
    /**
     * socketPort is the port used for the TCP comunication
     */
    private final int socketPort;
    /**
     * rmiPort is the port used for the RMI comunication
     */
    private final int rmiPort;
    /**
     * socketServer is the class responsible for handling TCP connections
     */
    protected SocketServer socketServer;
    /**
     * rmiServer is the class responsible for handling the rmi connections
     */
    protected RMIServer rmiServer;
    /**
     * SERVER_LOGGER is used by the server for sending information message about the connection state
     */
    public static final Logger SERVER_LOGGER = Logger.getLogger(Server.class.getName() + "Logger");
    /**
     * gameBroker is the reference to the GameBroker class responsible for the logins and the creation of lobbies
     */
    protected GameBroker gameBroker;

    /**
     * constructor for the class Server.
     * @param socketPort is the port where the TCP socket is open
     * @param rmiPort is the port where rmi is listening
     */
    public Server(int socketPort, int rmiPort) {
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
        gameBroker = new GameBroker();
    }

    /**
     * public run() method used to start in new thread the servers responsible for handling the connection using rmi or TCP
     */
    @Override
    public void run() {
        try {
            System.out.println("In order to play connect to: \u001B[1m" + InetAddress.getLocalHost().getHostAddress() + "\u001B[0m");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Localhost ip not found!");
            throw new RuntimeException(e);
        }
        socketServer = new SocketServer(this, socketPort);
        rmiServer = new RMIServer(this, rmiPort);
        new Thread(socketServer).start();
        new Thread(rmiServer).start();

    }

    /**
     * sendMsgToAll is a method used to send a message to every player connected to a specific lobby
     * @param arg is the forwarded message, it needs to be serializable
     * @param lobby is the target lobby
     */
    public void sendMsgToAll (Serializable arg, Lobby lobby) {
        socketServer.sendMsgToAllSocket(arg, lobby);
        rmiServer.sendMsgToAllRMI(arg, lobby);
    }

    public void setInGameStatus (String lobby) {
        socketServer.setInGameStatus(lobby);
        rmiServer.setInGameStatus(lobby);
    }


}