package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.view.TextualUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;


/** socket based implementation of the client */
public class SocketClient extends GenericClient{
    /** server ip address */
    private final String ip;
    /** server port number */
    private final int port;
    /** view to be bound*/
    private final TextualUI view;
    /** boolean represents the status of the connection (true means connected) */
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);
    /** socket object of the client*/
    private Socket clientSocket;
    /** receiving stream from the server*/
    private ObjectInputStream inputStream;
    /** target stream to communicate to the server*/
    private ObjectOutputStream outputStream;
    /** listener thread to manage input stream */
    private final Thread messageListener;
    /** ping frequency */
    private final int PING_TIME = 5000;
    /** ping thread */
    //private final Thread pingThread;

    /** constructor sets the parameters and launches the ping thread
     * @param ip server ip address
     * @param port server port number
     * @param view view to be bound
     */
    public SocketClient(String ip, int port, TextualUI view) {
        this.ip = ip;
        this.port = port;
        this.view = view;
        messageListener = new Thread(this::readMessages);

        /*
        pingThread = new Thread(() -> {
            while (clientConnected.get()) {
                try {
                    Thread.sleep(PING_TIME);
                    sendMsgToServer(new Ping());
                } catch (InterruptedException ignored) {
                    disconnect(true);
                }
            }
        });

         */
    }

    /** method sends serializable object on the output stream
     * @param message serializable message to be sent*/
    public void sendMsgToServer(Serializable message) {
        if (clientConnected.get()) {
            try {
                outputStream.writeObject(message);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                disconnect(true);
            }
        }
    }

    /**
     * initializes the socket based connection; creates the socket and establishes the connection to the server, then
     * assigns the socket streams to the local attributes of the class, sets the connectionStatus to true and starts
     * the listening and the ping threads */
    @Override
    public void init() {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port));
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            clientConnected.set(true);
            messageListener.start();
            //pingThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** this method reads the latest object sent from the server on the input stream and forwards the right command to
     * the view */
    public void readMessages() {
        try {
            while (clientConnected.get()) {
                Object msg = inputStream.readObject();
                if(msg instanceof ServerMessage){
                    if (((ServerMessage) msg).getType().equals("CreatedLobbyMessage")) {
                        isInLobby = true;
                        view.displayCreatedLobbyMsg((CreatedLobbyMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("JoinedMessage")) {
                        isInLobby = true;
                        assert msg instanceof JoinedMessage;
                        view.displayJoinedMsg((JoinedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("ExistingLobbyMessage")) {
                        view.displayServerMsg("Already existing lobby");
                    }
                    if (((ServerMessage) msg).getType().equals("LobbyNotCreatedMessage")) {
                        view.displayServerMsg("Lobby has not been created");
                    }
                    if (((ServerMessage) msg).getType().equals("NameTakenMessage")) {
                        view.displayServerMsg("Name is already taken, insert another one");
                    }
                    if (((ServerMessage) msg).getType().equals("NotExistingLobbyMessage")) {
                        view.displayServerMsg("This lobby does not exist, try another one or creating a new lobby");
                    }
                    if (((ServerMessage) msg).getType().equals("FullLobbyMessage")) {
                        view.displayServerMsg("Lobby is full");
                    }
                    if (((ServerMessage) msg).getType().equals("RetrievedLobbiesMessage")) {
                        RetrievedLobbiesMessage specificMessage = (RetrievedLobbiesMessage) msg;
                        view.displayLobbies(specificMessage.getLobbies());
                    }
                    if (((ServerMessage) msg).getType().equals("ChatUpdateMessage")) {
                        assert msg instanceof ChatUpdateMessage;
                        view.addMessage((ChatUpdateMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("GameCreatedMessage")) {
                        assert msg instanceof GameCreatedMessage;
                        view.updateView(((GameCreatedMessage) msg).getGameInfo());
                        view.displayInitialGameInfo();
                    }
                    if (((ServerMessage) msg).getType().equals("GameUpdatedMessage")) {
                        assert msg instanceof GameUpdatedMessage;
                        view.updateView(((GameUpdatedMessage) msg).getGameInfo());
                        view.displayGameInfo();
                    }
                    if (((ServerMessage) msg).getType().equals("UpdatedPlayerMessage")) {
                        assert msg instanceof UpdatedPlayerMessage;
                        view.displayServerMsg("It's " + ((UpdatedPlayerMessage) msg).getUpdatedPlayer() + "'s turn");
                    }
                    if (((ServerMessage) msg).getType().equals("InvalidMoveMessage")) {
                        view.displayServerMsg("Move is not valid");
                    }
                    if (((ServerMessage) msg).getType().equals("InsufficientPlayersMessage")) {
                        view.displayServerMsg("Not enough players to continue the game");
                    }
                    if (((ServerMessage) msg).getType().equals("LobbyClosedMessage")) {
                        view.displayServerMsg("Lobby has been closed");
                        isInLobby = false;
                    }
                    if (((ServerMessage) msg).getType().equals("UserDisconnectedMessage")) {
                        view.displayServerMsg(((UserDisconnectedMessage) msg).getUser() + " disconnected");
                        view.displayServerMsg(((UserDisconnectedMessage) msg).getCurrentPlayer() + "'s turn");
                    }
                    if (((ServerMessage) msg).getType().equals("InvalidCommandMessage")) {
                        view.displayServerMsg("Command not recognized, try again");
                    }
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            disconnect(true);
        }
    }

    /**
     * signals the disconnection or failed connection to the view, interrupts the listener thread, closes all streams
     * and sockets.
     * @param error true if the reason for disconnection is an error */
    public void disconnect(boolean error) {
        if (clientConnected.get()) {
            clientConnected.set(false);
            if (messageListener.isAlive()) messageListener.interrupt();

            try {
                inputStream.close();
            } catch (IOException ignored) {}

            try {
                outputStream.close();
            } catch (IOException ignored) {}

            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            if (error) view.displayServerMsg("An error occurred during the communication with the server, you're being disconnected! See ya!");
        }
    }


}