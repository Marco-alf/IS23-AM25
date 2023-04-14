package it.polimi.ingsw.view;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.CreatedLobbyMessage;
import it.polimi.ingsw.network.messages.serverMessages.JoinedMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Comandi della view
- muovi tiles
 */

public class TextualUI implements ViewInterface {
    private final Scanner scanner = new Scanner(System.in);
    private GenericClient client;
    private final List<ChatUpdateMessage> messages = new ArrayList<>();
    public void start() {
        System.out.println("Insert /rmi if you want to join server with rmi");
        System.out.println("Insert /socket if you want to join server with socket");
        String connType = scanner.nextLine();
        if (connType.equals("/rmi")) {
            client = new RMIClient("localhost", 1099, this);
            client.init();
        } else if (connType.equals("/socket")) {
            client = new SocketClient("localhost", 8088, this);
            boolean socketError = true;
            while (socketError){
                client.init();
                socketError = false;
            }
        }

        String inputCommand;

        System.out.println("Welcome to my Shelfie!");
        System.out.println("List of commands:");
        System.out.println("/create: to create a lobby");
        System.out.println("/join: to join a lobby that has already been created");
        System.out.println("/retrieve: to get a list of the available lobbies");
        System.out.println("/chat: to write a message in the chat");
        System.out.println("/showchat: to show the chat history");

        while (true) {
            inputCommand = askCommand();
            if (inputCommand.equals("/create")) {
                if (client.getIsInLobbyStatus()) {
                    System.out.println("You are already in a lobby");
                } else {
                    System.out.println("Insert username");
                    String name = askName();
                    System.out.println("Insert name of the lobby");
                    String lobbyName = askLobbyName();
                    System.out.println("Insert number of players");
                    int numPlayers = askNumPlayers();
                    CreateLobbyMessage clientMessage = new CreateLobbyMessage();
                    clientMessage.setLobbyName(lobbyName);
                    clientMessage.setLobbyCreator(name);
                    clientMessage.setPlayerNumber(numPlayers);
                    if (client instanceof RMIClient) {
                        clientMessage.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessage);
                }
            }
            if (inputCommand.equals("/retrieve")) {
                ClientMessage clientMessage = new RetrieveLobbiesMessage();
                if (client instanceof RMIClient) {
                    clientMessage.setRmiClient((RMIClient) client);
                }
                client.sendMsgToServer(clientMessage);
            }
            if (inputCommand.equals("/join")) {
                if (client.getIsInLobbyStatus()) {
                    System.out.println("You are already in a lobby");
                } else {
                    System.out.println("Insert username");
                    String name = askName();
                    System.out.println("Insert name of the lobby");
                    String lobbyName = askLobbyName();
                    JoinMessage clientMessage = new JoinMessage();
                    clientMessage.setName(name);
                    clientMessage.setLobbyName(lobbyName);
                    if (client instanceof RMIClient) {
                        clientMessage.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessage);
                }
            }
            if (inputCommand.equals("/chat")) {
                if (client.getIsInLobbyStatus()) {
                    System.out.println("You have entered the chat. Write a message");
                    String chatMessage = askChatMessage();

                    ChatMessage clientMessage = new ChatMessage();
                    clientMessage.setContent(chatMessage);
                    if (client instanceof RMIClient) {
                        clientMessage.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessage);

                } else {
                    System.out.println("You have to be inside a lobby to use the chat");
                }

            }
            if (inputCommand.equals("/showchat")) {
                if (client.getIsInLobbyStatus()) {
                    displayChat();
                }
                else {
                    System.out.println("You have to be inside a lobby to use the chat");
                }
            }
        }

    }

    public void displayLobbies (List<String> lobbies) {
        System.out.println("Available lobbies:");
        for (String lobby : lobbies) {
            System.out.println(lobby);
        }
    }

    public void displayCreatedLobbyMsg (CreatedLobbyMessage msg) {
        System.out.println("A lobby has been created");
        System.out.println("Lobby name: " + msg.getLobbyName());
        System.out.println("Lobby creator: " + msg.getName());
    }

    public void displayChat () {
        for (ChatUpdateMessage message : messages) {
            System.out.println(message.getSender() + " at " + message.getTimestamp() + ": " + message.getContent());
        }
    }

    public void addMessage (ChatUpdateMessage msg) {
        messages.add(msg);
    }

    public void displayJoinedMsg (JoinedMessage msg) {
        System.out.println("You joined " + msg.getLobbyName());
    }

    public void displayServerMsg (String string) {
        System.err.println(string);
    }

    public String askCommand() {
        String command;
        command = scanner.nextLine();
        return command;
    }

    public String askName() {
        String name;
        name = scanner.nextLine();
        return name;
    }

    public String askChatMessage() {
        String chatMessage;
        chatMessage = scanner.nextLine();
        return chatMessage;
    }

    public String askLobbyName() {
        String lobbyName;
        lobbyName = scanner.nextLine();
        return lobbyName;
    }

    public int askNumPlayers() {
        int numPlayers;
        numPlayers = Integer.parseInt(scanner.nextLine());
        return numPlayers;
    }

}
