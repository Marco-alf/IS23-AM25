package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.GameInfo;
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
    private GameInfo gameInfo;
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
            if (inputCommand.equals("/move")) {
                if (client.getIsInLobbyStatus()) {
                    List<Tile> tiles = new ArrayList<>();
                    System.out.println("Choose up to 3 tiles from the board to insert in your bookshelf");
                    System.out.println("Use the format y,x (x is the horizontal axis, y is the vertical axis");
                    System.out.println("Insert enter key to finish your selection");
                    String coords = scanner.nextLine();
                    while (!coords.equals("")) {
                        tiles.add(getTiles(coords));
                        coords = scanner.nextLine();
                    }
                    System.out.println("Choose the column of the bookshelf where you want to place the tiles");
                    int column = Integer.parseInt(scanner.nextLine());
                    MoveMessage clientMessage = new MoveMessage();
                    clientMessage.setTiles(tiles);
                    clientMessage.setColumn(column);
                    if (client instanceof RMIClient) {
                        clientMessage.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessage);
                }
                else {
                    System.out.println("You have to be inside a lobby to use the chat");
                }
            }
        }

    }

    public void updateView (GameInfo info) {
        gameInfo = info;
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

    public void displayShelf (TilesType[][] shelf) {
        System.out.print("+");
        for (int j = 0; j < 5; j++) {
            System.out.print("-----+");
        }
        System.out.println();

        for (int i = 0; i < 6; i++) {
            System.out.print("|");
            for (int j = 0; j < 5; j++) {
                if (shelf[i][j] == null) {
                    System.out.print("     |");
                } else {
                    System.out.print("  " + printTile(shelf[i][j]) + "  |");
                }
            }
            System.out.println();

            System.out.print("+");
            for (int j = 0; j < 5; j++) {
                System.out.print("-----+");
            }
            System.out.println();
        }
        System.out.println("   0     1     2     3     4   ");
    }

    public void displayBoard (TilesType[][] matrix) {
        System.out.println("         0     1     2     3     4     5     6     7     8   ");

        System.out.print("      +");
        for (int j = 0; j < 9; j++) {
            System.out.print("-----+");
        }
        System.out.println();

        for (int i = 0; i < 9; i++) {

            System.out.print("   " + i + "  |");
            for (int j = 0; j < 9; j++) {
                if (matrix[i][j] == null) {
                    System.out.print("     |");
                } else {
                    System.out.print("  " + printTile(matrix[i][j]) + "  |");
                }
            }
            System.out.println();

            System.out.print("      +");
            for (int j = 0; j < 9; j++) {
                System.out.print("-----+");
            }
            System.out.println();
        }
    }

    public void displayGameInfo () {
        //System.out.println("The game has started");
        System.out.println();
        displayBoard(gameInfo.getNewBoard());
        System.out.println();
        for (int i = 0; i < gameInfo.getPlayers().size(); i++) {
            System.out.println(gameInfo.getPlayers().get(i) + "'s bookshelf");
            System.out.println();
            displayShelf(gameInfo.getShelves().get(gameInfo.getPlayers().get(i)));
            System.out.println();
        }
        System.out.println("It's " + gameInfo.getCurrentPlayer() + "'s turn");


    }
    public Tile getTiles (String coords) {
        int y = Integer.parseInt(String.valueOf(coords.charAt(0)));
        int x = Integer.parseInt(String.valueOf(coords.charAt(2)));
        return new Tile(gameInfo.getNewBoard()[y][x], x, y);
    }

    public String printTile (TilesType type) {
        switch (type) {
            case TROPHIES -> {
                return "T";
            }
            case FRAMES -> {
                return "F";
            }
            case PLANTS -> {
                return "P";
            }
            case CATS -> {
                return "C";
            }
            case GAMES -> {
                return "G";
            }
            case BOOKS -> {
                return "B";
            }
            default -> {
                return " ";
            }

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
