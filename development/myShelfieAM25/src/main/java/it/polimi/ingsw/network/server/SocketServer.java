package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameBroker;
import it.polimi.ingsw.controller.Lobby;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static it.polimi.ingsw.network.server.Server.SERVER_LOGGER;

public class SocketServer implements Runnable{
    protected final Server server;
    ServerSocket serverSocket;
    private final int port;
    private final List<ClientHandler> clientHandlers = new ArrayList<>();
    GameBroker gameBroker;
    private final ExecutorService executor;
    public SocketServer (Server server, int port) {
        this.server = server;
        this.port = port;
        gameBroker = server.gameBroker;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            SERVER_LOGGER.severe("Unable to open socket port " + port);
            return;
        }
        SERVER_LOGGER.info("Socket server started on port " + port);

        try {
            while (true){
                Socket clientSocket = serverSocket.accept();
                //clientSocket.setSoTimeout(CLIENT_SOCKET_TIMEOUT);
                SERVER_LOGGER.log(Level.INFO,"New socket client connected from (" + clientSocket.getInetAddress().getHostAddress() + ")");
                ClientHandler clientConnection = new ClientHandler(this, clientSocket);
                clientHandlers.add(clientConnection);
                executor.submit(clientConnection);
            }
        } catch (IOException e) {
            SERVER_LOGGER.log(Level.SEVERE, "Error during client acceptance");
        }
    }
    public void sendMsgToAllSocket (Serializable arg, Lobby lobby) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getLobby() == lobby) {
                clientHandler.sendMsgToClient(arg);
            }
        }
    }

    public void removeClient (ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}
