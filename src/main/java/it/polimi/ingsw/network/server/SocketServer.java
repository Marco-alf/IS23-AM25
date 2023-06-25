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

/**
 * is the server that concretely has to handle connections with the players via TCP sockets
 */
public class SocketServer implements Runnable{
    /**
     * is the server that created Socketserver. SocketServer will forward commands to it
     */
    protected final Server server;
    /**
     * serverSocket is the reference to the socket used for the connection
     */
    ServerSocket serverSocket;
    /**
     * port is the number of the port used for TCP connection
     */
    private final int port;
    /**
     * clientHandlers is a list of classes each responsible for handling a socket client
     */
    private final List<ClientHandler> clientHandlers = new ArrayList<>();
    /**
     * reference to the class of the controller responsible for handling the creation of lobbies
     */
    GameBroker gameBroker;
    /**
     * executor is an ExecutorService used for creating threads each responsible for a connection with a client
     */
    private final ExecutorService executor;

    /**
     * constructor for SocketServer, requires the server that creates it and the port to use. It also initializes
     * the reference to the gameBroker and create the ExecutorService
     * @param server is the server that the serverSocket will use to communicate with the controller
     * @param port is the port number where the connections will be established
     */
    public SocketServer (Server server, int port) {
        this.server = server;
        this.port = port;
        gameBroker = server.gameBroker;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * method used to start all the services that will handle the connection of new players through the socket
     */
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
                //clientSocket.setSoTimeout(60000);
                ClientHandler clientConnection = new ClientHandler(this, clientSocket);
                clientHandlers.add(clientConnection);
                executor.submit(clientConnection);
                SERVER_LOGGER.log(Level.INFO,"New socket client connected from (" + clientSocket.getInetAddress().getHostAddress() + ")");
            }
        } catch (IOException e) {
            SERVER_LOGGER.log(Level.SEVERE, "Error during client acceptance");
        }
    }
    /**
     * sendMsgToAllRMI is the method used to send a message to every client that is connected through the socket to a specific lobby
     * @param arg is the forwarded message, it needs to be serializable
     * @param lobby is the target Lobby
     */
    public synchronized void sendMsgToAllSocket (Serializable arg, Lobby lobby) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getLobby() == lobby) {
                clientHandler.sendMsgToClient(arg);
            }
        }
    }

    public synchronized void setInGameStatus (String lobby) {
        for (ClientHandler clientHandler : clientHandlers) {
            if(clientHandler != null ) {
                if (clientHandler.getLobby()!=null && clientHandler.getLobby().getLobbyName().equals(lobby)) {
                    clientHandler.setStatus(ClientState.IN_GAME);
                }
            }
        }
    }

    /**
     * removeClient remove the connection with a client that uses the TCP Socket
     * @param clientHandler is the handler of the connections that are using the socket
     */
    public synchronized void removeClient (ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    /**
     *
     */
    public synchronized void removeLobby (Lobby lobby){
        for(ClientHandler h : clientHandlers){
            if(h.getLobby().equals(lobby)){
                removeClient(h);
            }
        }
    }
}
