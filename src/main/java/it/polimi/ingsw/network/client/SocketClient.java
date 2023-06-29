package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.connectionMessages.Ping;
import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
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
    private final ViewInterface view;
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
    private final int PING_TIME = 1000;
    /** ping thread */
    private final Thread pingThread;

    /** constructor sets the parameters and launches the ping thread
     * @param ip server ip address
     * @param port server port number
     * @param view view to be bound
     */
    public SocketClient(String ip, int port, ViewInterface view) {
        this.ip = ip;
        this.port = port;
        this.view = view;
        messageListener = new Thread(this::readMessages);
        pingThread = new Thread(this::pingServer);

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
     * method used to ping the server every PING_TIME
     */
    public void pingServer () {
        while (clientConnected.get()) {
            try {
                sendMsgToServer(new Ping());
                Thread.sleep(PING_TIME);
            } catch (InterruptedException ignored) {
                //disconnect(true);
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
            clientSocket.setSoTimeout(5000);
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            clientConnected.set(true);
            messageListener.start();
            pingThread.start();
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
                        view.receiveCreatedLobbyMsg((CreatedLobbyMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("JoinedMessage")) {
                        isInLobby = true;
                        assert msg instanceof JoinedMessage;
                        view.receiveJoinedMsg((JoinedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("ExistingLobbyMessage")) {
                        view.receiveExistingLobbyMsg((ExistingLobbyMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("LobbyNotCreatedMessage")) {
                        view.receiveLobbyNotCreatedMsg((LobbyNotCreatedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("NameTakenMessage")) {
                        view.receiveNameTakenMsg((NameTakenMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("NotExistingLobbyMessage")) {
                        view.receiveNotExistingLobbyMsg((NotExistingLobbyMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("FullLobbyMessage")) {
                        view.receiveFullLobbyMsg((FullLobbyMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("RetrievedLobbiesMessage")) {
                        view.receiveRetrievedLobbiesMsg((RetrievedLobbiesMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("ChatUpdateMessage")) {
                        assert msg instanceof ChatUpdateMessage;
                        view.receiveChatUpdateMsg((ChatUpdateMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("PrivateChatUpdateMessage")) {
                        assert msg instanceof PrivateChatUpdateMessage;
                        view.receivePrivateChatUpdateMsg((PrivateChatUpdateMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("GameCreatedMessage")) {
                        assert msg instanceof GameCreatedMessage;
                        view.receiveGameCreatedMsg((GameCreatedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("GameUpdatedMessage")) {
                        assert msg instanceof GameUpdatedMessage;
                        view.receiveGameUpdatedMsg((GameUpdatedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("GameEndedMessage")) {
                        assert msg instanceof GameEndedMessage;
                        view.receiveGameEndedMsg((GameEndedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("UpdatedPlayerMessage")) {
                        assert msg instanceof UpdatedPlayerMessage;
                        view.receiveUpdatedPlayerMsg((UpdatedPlayerMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("InvalidMoveMessage")) {
                        view.receiveInvalidMoveMsg((InvalidMoveMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("InsufficientPlayersMessage")) {
                        view.receiveInsufficientPlayersMsg((InsufficientPlayersMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("LobbyClosedMessage")) {
                        view.receiveLobbyClosedMsg((LobbyClosedMessage) msg);
                        isInLobby = false; /////////////
                    }
                    if (((ServerMessage) msg).getType().equals("UserDisconnectedMessage")) {
                        view.receiveUserDisconnectedMsg((UserDisconnectedMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("InvalidCommandMessage")) {
                        view.receiveInvalidCommandMsg((InvalidCommandMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("InvalidLobbyNameMessage")) {
                        view.receiveInvalidLobbyNameMsg((InvalidLobbyNameMessage) msg);
                    }
                    if (((ServerMessage) msg).getType().equals("IllegalPlayerNameMessage")) {
                        view.receiveIllegalPlayerNameMsg((IllegalPlayerNameMessage) msg);
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

            if (error) view.receiveConnectionErrorMsg(new ConnectionErrorMessage());
        }
    }


}