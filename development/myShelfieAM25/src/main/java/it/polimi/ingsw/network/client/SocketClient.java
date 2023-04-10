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

public class SocketClient extends GenericClient{
    private final String ip;
    private final int port;
    private final TextualUI view;
    private final AtomicBoolean clientConnected = new AtomicBoolean(false);
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final Thread messageListener;
    private final int PING_TIME = 5000;
    private final Thread pingThread;

    public SocketClient(String ip, int port, TextualUI view) {
        this.ip = ip;
        this.port = port;
        this.view = view;
        messageListener = new Thread(this::readMessages);

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
    }

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

    @Override
    public void init() {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, port));
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            clientConnected.set(true);
            messageListener.start();
            pingThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            disconnect(true);
        }
    }

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