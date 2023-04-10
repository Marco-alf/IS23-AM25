package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameBroker;
import it.polimi.ingsw.controller.Lobby;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This class represents the server. It is used to start and close the games,
 * and to manage clients that connect to play.
 */
public class Server implements Runnable{
    private final int socketPort;
    private final int rmiPort;
    protected SocketServer socketServer;
    protected RMIServer rmiServer;
    public static final Logger SERVER_LOGGER = Logger.getLogger(Server.class.getName() + "Logger");
    protected GameBroker gameBroker;

    public Server(int socketPort, int rmiPort) {
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
        gameBroker = new GameBroker();
    }

    @Override
    public void run() {
        socketServer = new SocketServer(this, socketPort);
        rmiServer = new RMIServer(this, rmiPort);
        new Thread(socketServer).start();
        new Thread(rmiServer).start();

    }

    public void sendMsgToAll (Serializable arg, Lobby lobby) {
        socketServer.sendMsgToAllSocket(arg, lobby);
        rmiServer.sendMsgToAllRMI(arg, lobby);
    }


}