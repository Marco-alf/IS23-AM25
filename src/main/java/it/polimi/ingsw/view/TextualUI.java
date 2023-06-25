package it.polimi.ingsw.view;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * TextualUI is the class that manages the whole TUI
 */
public class TextualUI implements ViewInterface {
    /**
     * scanner is the Scanner object used for reading from System.in input stream
     */
    private final Scanner scanner = new Scanner(System.in);
    /**
     * boolean flag that indicates if the corresponding client is online or not
     */
    private boolean online = true;
    /**
     * reference to the client interface with the network
     */
    private GenericClient client;
    /**
     * board represent the current state of the game board
     */
    private TilesType[][] board;
    /**
     * onlinePlayers is the list of the players that are currently connected to the lobby
     */
    private final List<String> onlinePlayers = new ArrayList<>();
    /**
     * shelves is a hashmap that links each player to his shelf
     */
    private Map<String, TilesType[][]> shelves = new HashMap<>();
    /**
     * commonGoals is a hashmap that links each player to its common goal points
     */
    private final Map<String, Integer[]> commonGoals = new HashMap<>();
    /**
     * commonGoal1 is a string that uniquely identify the current first common goal
     */
    private String commonGoal1;
    /**
     * commonGoal2 is a string that uniquely identify the current second common goal
     */
    private String commonGoal2;
    /**
     * personalPoints is an integer that represents the points that the client have gained through his personal goal
     */
    private Integer personalPoints;
    /**
     * personalGoal is the PersonalGoal of the player linked to this TUI
     */
    private PersonalGoal personalGoal;
    /**
     * nickname is the name of the client linked to this TUI
     */
    private String nickname;
    /**
     * curPlayer is the nickname of the player that is currently allowed to make a move
     */
    private String curPlayer;
    /**
     * messages is the list of all the messages shared though the chat
     */
    private final List<ChatUpdateMessage> messages = new ArrayList<>();
    /**
     * commands is a list of all the commands that are available in the lobby
     */
    private static final List<String> commands = List.of("create", "join", "retrieve","chat","showchat", "help", "move", "quit", "gamestate","egg","exit");
    /**
     * rst is an ANSI sequence that reset all the ANSI codes
     */
    private static final String rst = "\u001B[0m";
    /**
     * whiteBack is an Ansi sequence that makes white the background of the following characters
     */
    private static final String whiteBack = "\u001B[48;5;" + 15 + "m";
    /**
     * red is an Ansi sequence that makes red the next characters
     */
    private static final String red = "\u001B[38;5;" + 1 + "m";
    /**
     * yellow is an Ansi sequence that makes yellow the next characters
     */
    private final static String yellow = "\u001B[38;5;" + 11 + "m";
    /**
     * bold is an Ansi sequence that makes bold the next characters
     */
    private static final String bold = "\u001B[1m";
    /**
     * out is an Ansi sequence that prints a prompt that symbolizes the beginning of an output message
     */
    private static final String out = rst + yellow + bold + " > " + rst;
    /**
     * in is an Ansi sequence that prints a prompt that symbolizes the TUI waiting for an input
     */
    private static final String in = rst + bold + "\u001B[38;5;" + 6 + "m >>> ";
    /**
     * err is an Ansi sequence that prints a prompt that symbolizes the beginning of an error message
     */
    private static final String err = "\u001B[1m\u001B[38;5;" + 1 + "m ERROR: ";
    /**
     * String that represents the IP of the server (IPv4)
     */
    private static String serverIP;
    /**
     * String that represents the last message received by the chat
     */
    private static String lastMessage = "";
    /**
     * flag used to indicate if a game is ended or not
     */
    private boolean hasEnded = false;
    /**
     * flag used to indicate that a function is printing on System.out.
     * Is useful to avoid concurrent output that may result in graphical glitches
     */
    private boolean isDisplaying = false;
    /**
     * flag that indicates that while executing another operation at least on new message has been received
     */
    private boolean missingChatUpdate = false;
    /**
     * flag that indicates that while executing another operation the game state has been updated
     */
    private boolean missingGameUpdate = false;
    /**
     * flag that indicates if the TUI has received the information needed to display the game state
     */
    private boolean hasInfo = false;
    /**
     * flag that indicates a quit state. It remains until the quit state ends and the network acknowledges a success
     */
    private boolean quitState = false;
    private boolean waiting = false;
    /**
     * a GameResults object that contains all the information about how the current game has ended
     */
    private final GameResults results = new GameResults();
    /**
     * port number for rmi connection
     */
    private int rmiPort = 1099;
    /**
     * port number for tcp connection
     */
    private int tcpPort = 8088;

    /**
     * That's the main function of the TUI that it's responsible for handling all the activities of a client that is using
     * TUI to play the game
     */
    public void start() {
        String inputCommand;
        restoreWindow();
        System.out.println(out + "Insert the \u001B[1mserver IP address"+rst+".");
        System.out.print(in);
        serverIP = scanner.nextLine();
        while(!isValidIP(serverIP)){
            System.out.print(rst + err + "This ip is wrongly formatted\n" + in);
            serverIP = scanner.nextLine();
        }

        System.out.println(out + "Insert \u001B[1mrmi"+rst+" if you want to join server with rmi \u001B[1msocket"+rst+" if you want to access it with socket");
        System.out.print(in);

        String connType = scanner.nextLine();

        while (!connType.equals("rmi") && !connType.equals("socket")) {
            System.out.print(rst + err + "This type of connection is not supported\n" + in);
            connType = scanner.nextLine();
        }
        if (connType.equals("rmi")) {
            System.out.println(out + "Insert port exposed by the server. If not sure press " + bold + "ENTER" + rst + " to use the default port 1099");
            System.out.print(in);
            inputCommand = scanner.nextLine();
            if(!inputCommand.equals("")) rmiPort = Integer.parseInt(inputCommand);
            client = new RMIClient(serverIP, rmiPort, this);
            new Thread(()->client.init()).start();
        } else {
            System.out.println(out + "Insert port exposed by the server. If not sure press " + bold + "ENTER" + rst + " to use the default port 8088");
            System.out.print(in);
            inputCommand = scanner.nextLine();
            if(!inputCommand.equals("")) tcpPort = Integer.parseInt(inputCommand);
            client = new SocketClient(serverIP, tcpPort, this);
            new Thread(()->client.init()).start();
        }

        restoreWindow();
        printCommands();


        while (online) {
            inputCommand = askCommand();
            while(isDisplaying){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }                }
            isDisplaying = true;
            switch (inputCommand) {
                case "create" -> createLobby();
                case "retrieve" -> retrieveLobbies();
                case "join" -> joinLobby();
                case "chat" -> sendMessage();
                case "showchat" -> {
                    if (client.getIsInLobbyStatus()) {
                        displayChat();
                    } else {
                        System.out.print(rst + err + "You have to be inside a lobby to use the chat\n" + in);
                    }
                }
                case "move" -> makeMove();
                case "quit" -> {
                    quitState = true;
                    quitLobby();
                    while (waiting){
                        try {
                            sleep(500);
                        } catch (InterruptedException ignored){

                        }
                    }
                }
                case "help" -> printCommands();
                case "gamestate" -> {
                    if (client.getIsInLobbyStatus()) {
                        displayGameInfo();
                    } else {
                        System.out.print(rst + err + "You have to be inside a game to use this functionality\n" + in);
                    }
                }
                case "egg" -> System.out.print(rst + yellow + "    0\n" + in);//(rst  + "Wow, you discover the " + yellow +"Easter Egg" + rst + ", probably you should go touch some grass\n" + in);
                case "exit" -> {
                    if(client.getIsInLobbyStatus())quitLobby();
                    System.exit(0);
                    online = false;
                }
            }

            if(missingGameUpdate) {
                displayGameInfo();
                missingGameUpdate = false;
            }
            if (missingChatUpdate) {
                System.out.println("\n" + out + "You have unread " + bold + "messages" + rst + ". To see them you can use" + bold + " showchat" + rst + " command\n     > last message: " +
                        lastMessage + "\n" + in);
                missingChatUpdate = false;
            }
            isDisplaying=false;
            if(hasEnded) {
                manageEndGame();
                resetState();
            }
        }
    }

    /**
     * function used to reset the state of the TUI. Is useful to initialize the TUI after the player quits from a lobby
     */
    public void resetState(){
        hasEnded = false;
        isDisplaying = false;
        missingChatUpdate = false;
        missingGameUpdate = false;
        hasInfo = false;
        quitState = false;

        board = null;
        onlinePlayers.clear();
        shelves.clear();
        commonGoals.clear();
        commonGoal1 = null;
        commonGoal2 = null;
        personalPoints = 0;
        nickname = null;
        curPlayer = null;
        personalGoal = null;
        messages.clear();
        lastMessage = "";
    }

    /**
     * method used to visualize the leaderboard. If the game has not ended yet the method returns immediately
     */
    private void displayLeaderBoard(){
        if(!hasEnded) return;
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        // Clear the screen
        if (isWindows) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // For non-Windows operating systems
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        System.out.println();

        System.out.println("\n\n\n" + yellow +
                "           ██      ███████  █████  ██████  ███████ ██████  ██████   ██████   █████  ██████  ██████      \n" +
                "           ██      ██      ██   ██ ██   ██ ██      ██   ██ ██   ██ ██    ██ ██   ██ ██   ██ ██   ██    ██ \n" +
                "           ██      █████   ███████ ██   ██ █████   ██████  ██████  ██    ██ ███████ ██████  ██   ██     \n" +
                "           ██      ██      ██   ██ ██   ██ ██      ██   ██ ██   ██ ██    ██ ██   ██ ██   ██ ██   ██    ██\n" +
                "           ███████ ███████ ██   ██ ██████  ███████ ██   ██ ██████   ██████  ██   ██ ██   ██ ██████   \n\n\n");

        int middle = 8;
        int increment;
        if (results.getLeaderboard().get(0).length() / 2 > middle - 1) {
            increment = results.getLeaderboard().get(0).length() / 2 - middle -1;
            for (int i = 0; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(bold + "\u001B[38;5;45m        * \n");

            for (int i = 0; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(yellow + bold + "   <*> <*> <*>\n");

            for (int i = 0; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(yellow + bold +" <*><*><*><*><*>       \n");

            System.out.print(rst + "\u001B[38;5;18m" + "\u001B[48;5;252m" );
            System.out.print(bold + results.getLeaderboard().get(0));
            System.out.println(rst + bold);
            for (int i = 0; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(yellow + "  \"\\\"\\\"\\|/\"/\"/\" \n");

            for (int i = 0; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.println(yellow + "    <*><*><*>\n" + rst);
        }
        else {
            increment = middle - results.getLeaderboard().get(0).length() / 2;
            System.out.print(rst + "\u001B[38;5;45m        * \n");
            System.out.print(yellow + bold + "   <*> <*> <*>\n");
            System.out.print(yellow + bold + " <*><*><*><*><*>       \n");
            System.out.print("\\"+"\u001B[38;5;18m" + "\u001B[48;5;252m" );
            for (int i = 1; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(bold + results.getLeaderboard().get(0));
            for (int i = 1; i < increment; i++) {
                System.out.print(" ");
            }
            System.out.print(rst + yellow + bold + "/" + rst +"\n");
            System.out.print(yellow + bold + "  \"\\\"\\\"\\|/\"/\"/\" \n");
            System.out.println(yellow + bold + "    <*><*><*>\n");
        }
        System.out.print(rst + yellow + bold +"  1. "+results.getLeaderboard().get(0)+":\n");
        System.out.println(rst + bold + "   Total points: " + yellow + bold + results.getTotals().get(0));
        System.out.println(rst + "   Personal Goal points: " + bold + results.getPersonalPoints().get(0));
        System.out.println(rst + "   Common Goal points: " + bold + results.getCommonPoints().get(0));
        System.out.println(rst + "   Adjacency points: " + bold + results.getAdjacencyPoints().get(0));

        for (int i = 1; i < results.getLeaderboard().size(); i++) {
            int j = i + 1;
            System.out.println(yellow + bold + "\n  " + j + ". " + results.getLeaderboard().get(i) + ": ");
            System.out.println(rst + bold + "   Total points: " + yellow + results.getTotals().get(i));
            System.out.println(rst + "   Personal Goal points: " + bold + results.getPersonalPoints().get(i));
            System.out.println(rst + "   Common Goal points: " + bold + results.getCommonPoints().get(i));
            System.out.println(rst + "   Adjacency points: " + bold + results.getAdjacencyPoints().get(i));
        }
    }

    /**
     * method used to clean the screen and prompt the My Shelfie ascii art
     */
    private void restoreWindow(){
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        // Clear the screen
        if (isWindows) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // For non-Windows operating systems
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        System.out.println();
        System.out.println(yellow +
                "                             ███╗   ███╗██╗   ██╗        ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗\n" +
                "                             ████╗ ████║╚██╗ ██╔╝        ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝\n" +
                "                             ██╔████╔██║ ╚████╔╝         ███████╗███████║█████╗  ██║     █████╗  ██║█████╗\n" +
                "                             ██║╚██╔╝██║  ╚██╔╝          ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝\n" +
                "                             ██║ ╚═╝ ██║   ██║           ███████║██║  ██║███████╗███████╗██║     ██║███████╗\n" +
                "                             ╚═╝     ╚═╝   ╚═╝           ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝\n");
    }

    /**
     * method that let the user select the list of tiles that he intend to move. It also let the player decide in which
     * column place them.
     * @param tiles is a list where the selected tiles are placed
     */
    private void getMoves(List<Tile> tiles){
        for (int i = 0; i < 3; i++) {
            String coords = scanner.nextLine();
            if (coords.equals("")) i = 3;
            else {
                while (!coords.matches("\\d,\\d") && !coords.equals("")) {
                    System.out.print(rst + err + "The inserted cell are in a wrong format\n" + in);
                    coords = scanner.nextLine();
                }
                if(!coords.equals(""))tiles.add(getTiles(coords));
            }
            if (i < 2 && !coords.equals("")) {
                System.out.print(in);
            }
        }
    }

    /**
     * method used to display a list of the commands available to the player
     */
    private void printCommands(){
        System.out.print("\u001B[0m");
        System.out.println("\u001B[1mList of commands:" + rst);
        System.out.println(yellow + "create:\u001B[0m create a lobby");
        System.out.println(yellow + "join:\u001B[0m join an existing lobby");
        System.out.println(yellow + "retrieve:\u001B[0m get a list of available lobbies");
        System.out.println(yellow + "chat:\u001B[0m write a message in the chat");
        System.out.println(yellow + "showchat:\u001B[0m show the chat history");
        System.out.println(yellow + "move:\u001B[0m make a move");
        System.out.print(yellow + "gamestate:\u001B[0m show the current game state\n");
        System.out.println(yellow + "quit:\u001B[0m leave a lobby");
        System.out.print(yellow + "exit:\u001B[0m close the application\n");
        System.out.print(yellow + "help:\u001B[0m show this list of commands\n");
        System.out.print(in);
    }

    /**
     * updateView is the method that allows the update of the TUI state.
     * @param info represents the information that the are needed for the update
     */
    public synchronized void updateView (GameInfo info) {
        board = info.getNewBoard();
        if (info instanceof InitialGameInfo) {
            shelves = ((InitialGameInfo) info).getShelves();
            for (String player : shelves.keySet()) {
                commonGoals.put(player, new Integer[]{((InitialGameInfo) info).getCommonPoints(player, 0), ((InitialGameInfo) info).getCommonPoints(player, 1)});
            }
            onlinePlayers.addAll(info.getOnlinePlayers());
            personalGoal = ((InitialGameInfo) info).getPersonalGoals().get(nickname);
            commonGoal1 = ((InitialGameInfo) info).getCommonGoal1();
            commonGoal2 = ((InitialGameInfo) info).getCommonGoal2();
        } else {
            shelves.put(info.getCurrentPlayer(), info.getShelf());
            commonGoals.replace(info.getCurrentPlayer(), new Integer[]{info.getCommonGoal1Points(), info.getCommonGoal2Points()});
        }
        if (onlinePlayers.size() != info.getOnlinePlayers().size()) {
            onlinePlayers.clear();
            onlinePlayers.addAll(info.getOnlinePlayers());
        }
        if (curPlayer == null) personalPoints = 0;
        else if(nickname.equals(curPlayer)) personalPoints = info.getPersonalGoalPoints();
        curPlayer = info.getCurrentPlayer();
        hasInfo = true;
    }

    /**
     * method used to display a list of all the available lobbies
     * @param lobbies is the list of the name of the available lobbies
     */
    private void displayLobbies (List<String> lobbies) {
            System.out.println(rst + bold + out + "Available lobbies:" + rst);
            if(lobbies.size() == 0){
                System.out.println(rst + "    " + red + "No available lobby was found" + rst);
            }
            for (String lobby : lobbies) {
                System.out.println(rst + "   " + out + lobby);
            }
    }

    /**
     * message used to inform a player that a lobby has been successfully created
     * @param msg is the message from the server containing all the needed information about the lobby
     */
    private void displayCreatedLobbyMsg (CreatedLobbyMessage msg) {
            System.out.println(out + "A lobby has been created");
            System.out.println(rst + "   Lobby name: " + bold + msg.getLobbyName() + rst);
            System.out.print(rst + "   Lobby creator: " + bold + msg.getName() + rst + "\n");

    }

    /**
     * method used to display all the message that have been sent to this client in the chat. The chat is linked to a
     * single lobby, this implies that quitting or changing lobby causes the lost of the chat
     */
    private void displayChat () {
        String sender;
        for (ChatUpdateMessage message : messages) {
            sender = message.getSender();
            if (sender.equals(nickname)) sender = "You";
            if (message.getType().equals("PrivateChatUpdateMessage")) {
                System.out.println(out + bold + sender + " at " + message.getTimestamp() + rst + " to " +
                        ((PrivateChatUpdateMessage) message).getReceiver() + red + bold + " [private]" + rst + ": \n" +
                        "      " + message.getContent() + rst);
            } else {
                System.out.println(out + bold + sender + " at " + message.getTimestamp() + rst + ": \n      " + message.getContent());
            }
        }
        System.out.print(in);
    }

    /**
     * method used to display the current game state. If the required information are not received yet the method ends without
     * displaying anything.
     */
    private void displayGameInfo () {
        if(!hasInfo) return;
        isDisplaying = true;
        restoreWindow();
        String[] output = boardConstructor(board);

        for (String player : shelves.keySet()) {
            output = concatStringArrays(output, shelfConstructor(shelves.get(player), player));
        }
        String info = convertStringArray(output);

        System.out.print(info);

        output = personalGoalConstructor(personalGoal);
        output = concatStringArrays(output, commonGoalConstructor(commonGoal1, 1));
        output = concatStringArrays(output, commonGoalConstructor(commonGoal2, 2));

        info = convertStringArray(output);

        System.out.print(info);
        if(!hasEnded) System.out.println(out + "current player is: " + red + bold + this.curPlayer + rst);
        System.out.print(in);
        isDisplaying = false;
    }

    /**
     * method used to create a link between a position in the board selected by the player and the tile in that position.
     * @param coords is the position in cartesian coordinates of the tile selected by the player
     * @return the Tile in the selected position
     */
    private Tile getTiles (String coords) {
        int x;
        int y;
        x = Integer.parseInt(String.valueOf(coords.charAt(0)));
        y = Integer.parseInt(String.valueOf(coords.charAt(2)));
        if (x < 0 || y < 0 || x > 8 || y > 8) return null;
        return new Tile(board[y][x], x, y);
    }

    /**
     * method that given a type of tile return the correspondent string that allows to visualize graphically the tile through
     * ANSI codes.
     * @param type is the type of tile that we want to print
     * @return the string used to graphically represent the requested type of tile
     */
    private String getTile (TilesType type) {
        String s = "";
        int BROWN = 59;

        if (type == null) {
            s += (char) 27 + "[48;5;" + BROWN + "m     ";
            s += "\u001B[0m";
            return s;
        }
        switch (type) {
            case TROPHIES -> {
                int TROPHIESB = 44;
                int TROPHIESF = 0;
                s += (char) 27 + "[48;5;" + TROPHIESB + "m" + (char) 27 + "[38;5;" + TROPHIESF + "m  T  ";
            }
            case FRAMES -> {
                int FRAMESB = 25;
                int FRAMESF = 15;
                s += (char) 27 + "[48;5;" + FRAMESB + "m" + (char) 27 + "[38;5;" + FRAMESF + "m  F  ";
            }
            case PLANTS -> {
                int PLANTSB = 126;
                int PLANTSF = 15;
                s += (char) 27 + "[48;5;" + PLANTSB + "m" + (char) 27 + "[38;5;" + PLANTSF + "m  P  ";
            }
            case CATS -> {
                int CATSB = 112;
                int CATSF = 0;
                s += (char) 27 + "[48;5;" + CATSB + "m" + (char) 27 + "[38;5;" + CATSF + "m  C  ";
            }
            case GAMES -> {
                int GAMEB = 11;
                int GAMEF = 0;
                s += (char) 27 + "[48;5;" + GAMEB + "m" + (char) 27 + "[38;5;" + GAMEF + "m  G  ";
            }
            case BOOKS -> {
                int BOOKSB = 230;
                int BOOKSF = 0;
                s += (char) 27 + "[48;5;" + BOOKSB + "m" + (char) 27 + "[38;5;" + BOOKSF + "m  B  ";
            }
            default -> s += (char) 27 + "[48;5;" + BROWN + "   ";
        }
        s += "\u001B[0m";
        return s;
    }

    /**
     * method used to ask player for the next command to execute
     * @return the string corresponding to the command
     */
    private String askCommand() {
        String command;
        command = scanner.nextLine();
        while(!commands.contains(command) && !hasEnded && online){
            System.out.print("\u001B[38;5;" + 1 + "m\u001B[1m ERROR: invalid command! To see available commands type \u001B[1m\"help\".\u001B[0m\n" + in);

            if(hasEnded) return "egg";
            command = scanner.nextLine();
        }
        if(hasEnded) return "egg";
        if(!online) return "exit";
        return command;
    }

    /**
     * method used to ask player their nickname
     * @return a string representing the player nickname
     */
    private String askName() {
        String name;
        String confirm;
        do {
            System.out.print(out + "Insert username\n" + in);
            name = scanner.nextLine();
            System.out.print(out + "Your name is: " + bold + name + rst + ". Is it right [Y/n]?\n" + in);
            confirm = scanner.nextLine();
        } while (confirm.equals("n"));
        nickname = name;
        return name;
    }

    /**
     * method used to get a chat message from a player
     * @return the ChatMessage to send to the receiver
     */
    private ChatMessage askChatMessage() {
        String chatMessage, response = "Y";
        ChatMessage message;
        chatMessage = scanner.nextLine();
        if (onlinePlayers.size() > 2) {
            System.out.print(out + "Is this a public message? [Y/n]\n" + in);
            response = scanner.nextLine();
        }
        if (response.equals("n") || response.equals("N")) {
            System.out.print(out + "Who is the receiver?\n" + in);
            response = scanner.nextLine();
            while (!onlinePlayers.contains(response)) {
                System.out.print(out + "The sender needs to be online and in this lobby\n" + in);
                response = scanner.nextLine();
            }
            message = new PrivateChatMessage(response);
            message.setContent(chatMessage);
        } else {
            message = new ChatMessage();
            message.setContent(chatMessage);
        }
        return message;
    }

    /**
     * method used to ask a player for a name of a Lobby
     * @return the chosen name
     */
    private String askLobbyName() {
        String name;
        String confirm;

        do {
            System.out.print(out + "Insert lobby name\n" + in);
            name = scanner.nextLine();
            System.out.print(out + "The name is: " + bold + name + rst + ". Is it right [Y/n]?\n" + in);
            confirm = scanner.nextLine();
        } while (confirm.equals("n") || confirm.equals("N"));

        return name;
    }

    /**
     * method used to get the number of players from a client when creating a lobby.
     * @return the selected number of players
     */
    private int askNumPlayers() {
        int numPlayers;
        boolean flag;
        try {
            numPlayers = Integer.parseInt(scanner.nextLine());
            flag = true;
        } catch (NumberFormatException e) {
            flag = false;
            numPlayers = 0;
        }
        while (!flag || (numPlayers < 2 || numPlayers > 4)) {
            System.out.print(rst + err + "Invalid player number. Player number needs to be between 2 and 4\n" + in);
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                flag = true;
            } catch (NumberFormatException e) {
                flag = false;
            }
        }
        return numPlayers;
    }

    /**
     * method that given a board in input returns an array of strings that gives a graphical representation of it.
     * @param matrix is the input game board. It is necessary that the board is in a 9x9 format
     * @return an array of strings that corresponds to the given board
     */
    private String[] boardConstructor (TilesType[][] matrix) {
        if(matrix.length != 9 || matrix[0].length!=9) return null;
        String[] board = new String[20];
        board[0] = "\u001B[38;5;" + 246 + "m        0     1     2     3     4     5     6     7     8   ";

        board[1] = rst + yellow + "     +";
        for (int j = 0; j < 9; j++) {
            board[1] += "-----+";
        }
        int h = 2;
        for (int i = 0; i < 9; i++) {
            board[h] = yellow + " \u001B[38;5;" + 246 + "m  " + i + yellow + " |";
            for (int j = 0; j < 9; j++) {
                board[h] += getTile(matrix[i][j]);
                board[h] += rst + yellow + "|";
            }
            h++;
            board[h] = rst + yellow + "     +";
            for (int j = 0; j < 9; j++) {
                board[h] += "\u001B[38;5;" + 11 + "m-----+";
            }
            h++;
        }

        return board;
    }

    /**
     * method used to obtain a graphical representation of the shelf of a player
     * @param matrix is the shelf of the player. Is necessary that it is in 6x5 format
     * @param player is the name of the player to associate to the matrix
     * @return an array of string that represents the player shelf
     */
    private String[] shelfConstructor(TilesType[][] matrix, String player) {
        if(matrix.length != 6 || matrix[0].length!=5) return null;
        String[] shelf = new String[18]; //length = 31 + space
        shelf[0] = rst;
        int l;
        int delta = 0;
        shelf[1] = rst + "    " + bold + player + ": " + rst + rst;
        if (!onlinePlayers.contains(player)){
            shelf[1] += "\u001B[38;5;25m [offline]"+rst;
            delta+=10;
        }
        if (player.equals(nickname)) {
            shelf[1] += red + bold + " < you" + rst;
            delta = 6;
        }
        if (player.equals(curPlayer)) {
            shelf[1] += yellow + bold + " < cur" + rst;
            delta += 6;
        }
        l = player.length() + delta + 6;
        if (l < 37) l = 37;
        for (int i = 0; i < l; i++) {
            shelf[0] += " ";
        }

        for (int i = (player.length() + delta + 6); i < l; i++) {
            shelf[1] += " ";
        }
        shelf[2] = rst + "      common goals 1: " + whiteBack + red + bold + " " + commonGoals.get(player)[0].toString() + " "
                 + rst + "  2: " + whiteBack + red + bold + " " + commonGoals.get(player)[1].toString() + " " + rst;
        for (int i = 33; i < l; i++) {
            shelf[2] += " ";
        }

        shelf[3] = rst + "      shelf:";
        for (int i = 12; i < l; i++) {
            shelf[3] += " ";
        }
        shelf[4] = rst + yellow + "      +";
        for (int j = 0; j < 5; j++) {
            shelf[4] += "-----+";
        }
        for(int j=37; j < l; j++)shelf[4]+=" ";
        int h = 5;
        for (int i = 0; i < 6; i++) {
            shelf[h] = rst + "    " + i + yellow + " |";
            for (int j = 0; j < 5; j++) {
                shelf[h] += getTile(matrix[i][j]);
                shelf[h] += rst + yellow + "|";
            }
            for(int j=37; j < l; j++)shelf[h]+=" ";
            h++;
            shelf[h] = yellow + "      +";
            for (int j = 0; j < 5; j++) {
                shelf[h] += "-----+";
            }
            for(int j=37; j < l; j++)shelf[h]+=" ";
            h++;
        }
        shelf[17] = rst + "         0     1     2     3     4   ";
        for(int j=37; j < l; j++)shelf[h]+=" ";

        return shelf;
    }

    /**
     * method used to concatenate lists of strings. It is used to visualize multiple elements on the same line in the terminal
     * @param left is the array of string that will be placed on the left
     * @param right is the array of string that will be places on the right
     * @return an array of strings obtained by concatenate the input arrays
     */
    private String[] concatStringArrays(String[] left, String[] right){
        //refactor matrices to get perfects rectangles
        int maxh;
        String[] matrix;
        maxh = left.length;
        if (right.length > maxh) maxh = right.length;
        String[] leftCp = new String[maxh];
        String[] rightCp = new String[maxh];
        matrix = new String[maxh];
        for (int i = 0; i < maxh; i++) {
            if (i < left.length) {
                leftCp[i] = left[i];
            } else {
                leftCp[i] = "";
                for (int j = 0; j < left[0].length(); j++) {
                    leftCp[i] = leftCp[i] + " ";
                }
            }
            if (i < right.length) {
                rightCp[i] = right[i];
            } else {
                rightCp[i] = "";
                for (int j = 0; j < right[0].length(); j++) {
                    rightCp[i] = rightCp[i] + " ";
                }
            }
            matrix[i] = leftCp[i] + rightCp[i];
        }
        return matrix;
    }

    /**
     * method used to convert an array of string into a single string that can be printed and correctly visualized in the terminal
     * @param in is the input array of string
     * @return a string obtained by serializing all elements in the array
     */
    private String convertStringArray(String[] in){
        String s = "";
        for (String value : in) {
            s += value + "\n" + rst;
        }
        return s;
    }

    /**
     * method used to get the representation of a personal goal
     * @param personalGoal is the personal goal that we want to represent
     * @return an array of strings that graphically represents the personal goal
     */
    private String[] personalGoalConstructor(PersonalGoal personalGoal){
        String[] shelf = new String[17]; //length = 31 + 7 space
        TilesType[][] matrix = personalGoal.getMatrix();
        int length = 55;

        shelf[0] = rst;
        for (int i = 0; i < length; i++) {
            shelf[0] += " ";
        }

        shelf[1] = rst + bold + "   your personal goal: ";

        for (int i = 23; i < length; i++) {
            shelf[1] += " ";
        }
        shelf[1] += rst;
        shelf[2] = rst + "    points: ";
        String temp = personalPoints.toString();
        shelf[2] += temp;
        for (int j = temp.length() + 12; j < length; j++) {
            shelf[2] += " ";
        }
        shelf[3] = rst + yellow + "       +";
        for (int j = 0; j < 5; j++) {
            shelf[3] += "-----+";
        }
        shelf[3] += "                 ";
        int h = 4;
        for (int i = 0; i < 6; i++) {
            shelf[h] = rst + "     " + i + yellow + " |";
            for (int j = 0; j < 5; j++) {
                shelf[h] += getTile(matrix[i][j]);
                shelf[h] += rst + yellow + "|";
            }
            for (int j = 38; j < length; j++) shelf[h] += " ";
            h++;
            shelf[h] = rst + yellow + "       +";
            for (int j = 0; j < 5; j++) {
                shelf[h] += "-----+";
            }
            for (int j = 38; j < length; j++) shelf[h] += " ";
            h++;
        }
        shelf[16] = rst + "          0     1     2     3     4                    ";
        return shelf;
    }

    /**
     * method used to obtain the graphical representation of a common goal
     * @param commonGoal is the common goal to represent
     * @param pos it the position of the selected common goal in the game
     * @return an array of strings that graphically represents the common goal
     */
    private String[] commonGoalConstructor(String commonGoal, int pos){
        String[] goal;
        goal = new String[16];
        int length = 55;
        int h;
        switch (commonGoal) {
            case "ColumnsGoal (isRegular: true)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  3 column with at most 3 different types of tile";
                for (int i = 49; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (j % 2 == 0) goal[h] += rst + whiteBack + bold + red + "  ~  " + rst;
                            //else if (j == 2) goal[h] += rst + "\u001B[48;5;187m" + bold + red + "  ~  " + rst;
                            //else if (j == 4) goal[h] += rst + "\u001B[48;5;145m" + bold + red + "  ~  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "ColumnsGoal (isRegular: false)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  2 column with all different types of tile";
                for (int i = 43; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (j == 1 || j == 3)
                            goal[h] += rst + whiteBack + bold + red + "  " + (char) 8800 + "  " + rst;
                            //else if (j == 3) goal[h] += rst + "\u001B[48;5;181m" + bold + red + "  "+ (char)8800 +"  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "CornerGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  all corner of the shelf are of the same type";
                for (int i = 46; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if ((i == 0 || i == 5) && (j == 0 || j == 4))
                            goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "EightEqualsGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  eight tiles are of the same tile type";
                for (int i = 39; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if ((i == 1 && j % 2 == 1) || ((i == 3 || i == 5) && j % 2 == 0))
                            goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "FullDiagonalGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  a full diagonal has only one tile type";
                for (int i = 40; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i == (j + 1)) goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "RowsGoal (isRegular: true)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  4 rows with at most 3 different type of tile";
                for (int i = 46; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i != 1 && i != 3) goal[h] += rst + whiteBack + bold + red + "  ~  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "RowsGoal (isRegular: false)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  2 rows with all different type of tile";
                for (int i = 40; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = " ";
                for (int i = 0; i < length; i++) {
                    goal[3] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i == 0 || i == 5)
                            goal[h] += rst + whiteBack + bold + red + "  " + (char) 8800 + "  " + rst;
                            //else if (i == 5) goal[h] += rst + "\u001B[48;5;187m" + bold + red + "  "+ (char)8800 +"  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "TriangularMatrixGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  the shelf forms a triangle as in the figure";
                for (int i = 45; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i > j) goal[h] += rst + whiteBack + "     " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "TwoEqualSquareGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  two squares of at least 4 tiles each";
                for (int i = 38; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if ((i > 0 && i < 3 && j < 2) || (i > 3 && j > 1 && j < 4))
                            goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "XGoal" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  an X shape made of tiles of the same type";
                for (int i = 43; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if ((i == 3 && j == 2) || (i == 2 && j == 1) || (i == 2 && j == 3) || (i == 4 && j == 1) || (i == 4 && j == 3))
                            goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "EqualGroupsGoal (4 groups, 4 in size)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  4 groups of different shapes with 4 equal tiles";
                for (int i = 49; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i > 1 && j == 0) goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else if (i == 0 && j > 0) goal[h] += rst + "\u001B[48;5;189m" + bold + red + "  =  " + rst;
                        else if ((i == 2 && j > 1) || (i == 3 && j == 2))
                            goal[h] += rst + "\u001B[48;5;146m" + bold + red + "  =  " + rst;
                        else if (i > 3 && j > 2) goal[h] += rst + "\u001B[48;5;140m" + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
            case "EqualGroupsGoal (6 groups, 2 in size)" -> {
                goal[0] = "";
                for (int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal " + pos + ":";
                for (int i = 14; i < length; i++) {
                    goal[1] += " ";
                }
                goal[2] = rst + "  6 couples of equal tiles";
                for (int i = 26; i < length; i++) {
                    goal[2] += " ";
                }
                goal[3] = rst + yellow + "  +";
                for (int i = 0; i < 5; i++) goal[3] += "-----+";
                for (int i = 33; i < length; i++) goal[3] += " ";
                h = 4;
                for (int i = 0; i < 6; i++) {
                    goal[h] = rst + yellow + "  |";
                    for (int j = 0; j < 5; j++) {
                        if (i > 3 && j == 0) goal[h] += rst + whiteBack + bold + red + "  =  " + rst;
                        else if (i == 0 && j < 2) goal[h] += rst + "\u001B[48;5;189m" + bold + red + "  =  " + rst;
                        else if (i == 1 && (j == 2 || j == 3))
                            goal[h] += rst + "\u001B[48;5;146m" + bold + red + "  =  " + rst;
                        else if ((i == 3 || i == 2) && j == 2)
                            goal[h] += rst + "\u001B[48;5;187m" + bold + red + "  =  " + rst;
                        else if (i == 4 && j < 3)
                            goal[h] += rst + "\u001B[48;5;254m" + bold + red + "  =  " + rst;
                        else if (i > 3 && j > 2) goal[h] += rst + "\u001B[48;5;194m" + bold + red + "  =  " + rst;
                        else goal[h] += rst + getTile(null);
                        goal[h] += rst + yellow + "|";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                    goal[h] = rst + yellow + "  +";
                    for (int j = 0; j < 5; j++) {
                        goal[h] += "-----+";
                    }
                    for (int k = 33; k < length; k++) {
                        goal[h] += " ";
                    }
                    h++;
                }
            }
        }
        return goal;
    }

    /**
     * public method requested by the view interface. It is used to handle the response of the view to the event of creation
     * of a lobby
     * @param msg is the message that notify the client of the success of the lobby creation
     */
    @Override
    public void receiveCreatedLobbyMsg(CreatedLobbyMessage msg) {
        isDisplaying = true;
        restoreWindow();
        displayCreatedLobbyMsg(msg);
        System.out.print(in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to handle the response of the view to the event of joining
     * a lobby
     * @param msg is the message that notify the client that he successfully joined the lobby
     */
    @Override
    public void receiveJoinedMsg(JoinedMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(out + "You have joined " + msg.getLobbyName() + " as " + msg.getName() + "\n" + in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to handle the rejection of the creation of a lobby due to
     * the fact that a lobby with the selected name already exists
     * @param msg is the message that contains all the currently available lobbies
     */
    @Override
    public void receiveExistingLobbyMsg(ExistingLobbyMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(err + "A lobby with the selected name already exists! \n     You can join it with the command join\n" + in);
        isDisplaying = false;

    }

    /**
     * public method requested by the view interface. It is used to handle a generic failure that prevents the creation of
     * a new lobby
     * @param msg is the server message that notify the client of the failure in the creation of the lobby
     */
    @Override
    public void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(err + "An error occurred, the lobby was not created\n" + in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to notify the client that he was not able to join the selected
     * lobby because his name was not unique inside the lobby.
     * @param msg is the server message that notify the client of the failure
     */
    @Override
    public void receiveNameTakenMsg(NameTakenMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(err + "The selected name is already taken by another player. You can retry to join with another one\n" + in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to notify the client that he was not able to join the selected
     * lobby because that lobby does not exist
     * @param msg is the server message that notify the client of the error
     */
    @Override
    public void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(err + "No existing lobby matches the selected name! You can get the names of existing lobbies with the command retrieve\n" + in);
        isDisplaying = false;

    }

    /**
     * public method requested by the view interface. It is used to notify the client that he was not able to join the selected
     * lobby because it was already full
     * @param msg is the server message that notify the client of the failure
     */
    @Override
    public void receiveFullLobbyMsg(FullLobbyMessage msg) {
        isDisplaying = true;
        restoreWindow();
        System.out.print(err + "The selected lobby is full. To see all available lobbies you can use retrieve\n" + in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to handle the reception of the list of available lobbies
     * @param msg is the server message containing the names of all available lobbies
     */
    @Override
    public void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg) {
        isDisplaying = true;
        restoreWindow();
        displayLobbies(msg.getLobbies());
        System.out.print(in);
        isDisplaying = false;
    }

    /**
     * public method requested by the view interface. It is used to handle the response of the view to a new chat message
     * @param msg is the chat message with all the related metadata
     */
    @Override
    public void receiveChatUpdateMsg(ChatUpdateMessage msg) {
        if (msg.getSender().equals(nickname)) {
            System.out.print(out + bold + "You just write" + rst + ":  " + msg.getContent() + "\n" + in);
            try{
                sleep(1500);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        } else if(!isDisplaying) {
            isDisplaying = true;
            System.out.print("\n" + out + bold + msg.getSender() + " just write" + rst + ":  " + msg.getContent() + "\n" + in);
            isDisplaying = false;
        }
        else {
            missingChatUpdate = true;
            lastMessage = rst + bold + msg.getSender() +": " + rst + msg.getContent() + rst;
        }
        messages.add(msg);
    }

    /**
     * public method requested by the view interface. It is used to handle the response of the view to a new private message
     * @param msg is the private chat message with all the related metadata
     */
    public void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg) {
        if(!isDisplaying) {
            isDisplaying = true;
            if (msg.getReceiver().equals(nickname)) {
                System.out.print("\n" + out + bold + msg.getSender() + " just write" + ":  " + msg.getContent() + red + bold + " [private]\n" + in);
                try {
                    sleep(600);
                } catch (InterruptedException e) {
                    throw new RuntimeException("interrupt during sleep!");
                }
            }
            else if (msg.getSender().equals(nickname)) {
                System.out.print(out + "You have sent a private message to: " + bold + msg.getReceiver() + "\n" + in);
            }
            isDisplaying = false;
        }
        else if(!msg.getSender().equals(nickname)){
            lastMessage = rst + bold + msg.getSender() +": " + rst + msg.getContent() + red + bold + " [private]" + rst;
        }

        if(msg.getReceiver().equals(nickname) || msg.getSender().equals(nickname)) messages.add(msg);
    }

    /**
     * public method requested by the view interface. It is invoked to notify the client of the creation of a new game.
     * This method triggers an update of the state of the view. It is used both whenever the player needs all the information
     * about all the game and not only about the latest update
     * @param msg is a message that contains all the information about the current game state
     */
    @Override
    public void receiveGameCreatedMsg(GameCreatedMessage msg) {
        updateView(msg.getGameInfo());
    }

    /**
     * public method requested by the view interface. It is used to handle the updates of the game due to moves.
     * @param msg is a message that contains all the information needed to update the game from the previous state
     */
    @Override
    public void receiveGameUpdatedMsg(GameUpdatedMessage msg) {
        updateView(msg.getGameInfo());
    }

    /**
     * method used for the management of the ending of a game. In this phase player can visualize the final game state, see
     * the leaderboard and use the chat
     */
    private void manageEndGame(){
        String command;
        boolean inGame = true;
        List<String> availableCommands = List.of("quit", "gamestate", "chat", "showchat", "leaderboard", "help");
        ClientMessage clientMessageOut = null;
        while(isDisplaying){
            try {
                sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isDisplaying = true;

        displayLeaderBoard();
        System.out.print("\n" + out + bold + "Here is a list of currently available commands:\n" +
                yellow + bold + "   quit:" + rst +" to leave the lobby\n" +
                yellow + bold + "   gamestate:" + rst +" to see the final state of the game\n" +
                yellow + bold + "   chat:" + rst +" to use the chat\n" +
                yellow + bold + "   showchat:"+ rst + " to visualize the chat\n" +
                yellow + bold + "   leaderboard:" + rst + " to show leaderboard\n" + in);
        isDisplaying = false;
        while(inGame) {
            command = scanner.nextLine();
            isDisplaying = true;
            while (!availableCommands.contains(command)) {
                System.out.print(rst + err + bold + "given command is not available\n" + in);
                command = scanner.nextLine();
            }
            switch (command) {
                case "quit" -> {
                    System.out.print(out + bold + "Are you sure? [Y/n]\n" + in);
                    String quit = scanner.nextLine();
                    if (client.getIsInLobbyStatus()) {
                        if (!quit.equals("n") && !quit.equals("N")) {
                            quitState = true;
                            clientMessageOut = new QuitMessage();
                            if (client instanceof RMIClient) {
                                clientMessageOut.setRmiClient((RMIClient) client);
                                client.sendMsgToServer(clientMessageOut);
                                try {
                                    sleep(800);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                client = new RMIClient(serverIP, rmiPort, this);
                            } else if (client instanceof SocketClient) {
                                waiting = true;
                                client.sendMsgToServer(clientMessageOut);
                                while (waiting) {
                                    try {
                                        sleep(500);
                                    } catch (InterruptedException ignored) {
                                    }
                                }
                                client = new SocketClient(serverIP, tcpPort, this);
                            }
                            new Thread(() -> {
                                client.init();
                                restoreWindow();
                                printCommands();
                                waiting = false;
                            }).start();

                            resetState();
                            inGame = false;
                        }
                    }else{
                        waiting = false;
                        resetState();
                        restoreWindow();
                        printCommands();
                        inGame = false;
                    }
                }
                case "gamestate" -> displayGameInfo();

                case "chat" -> {
                    if(onlinePlayers.size() > 1) {
                        System.out.print(out + "You have entered the chat. Write a message\n" + in);
                        clientMessageOut = askChatMessage();
                        if (client instanceof RMIClient) {
                            clientMessageOut.setRmiClient((RMIClient) client);
                        }
                        isDisplaying = false;
                        client.sendMsgToServer(clientMessageOut);
                    }
                    else{
                        System.out.print(err + "No other player in the lobby\n" + in);
                    }
                }
                case "showchat" -> displayChat();
                case "leaderboard" -> {
                    displayLeaderBoard();
                    System.out.print(in);
                }
                case "help" -> printCommands();
            }
            isDisplaying = false;
            try{
                sleep(300);
            }catch (InterruptedException e){
                throw new RuntimeException();
            }
        }
        resetState();
    }

    /**
     * public method requested by the view interface. It is used to handle the reception of a game state update that corresponds
     * to the last move of a game. In this method are calculated the final points of the players
     * @param msg is the server update message that is sent at the end of a game. It contains the scores of each player divided
     *            by goal
     */
    public void receiveGameEndedMsg(GameEndedMessage msg){

        FinalGameInfo info = msg.getGameInfo();
        Map<String, List<Integer>> finalPoints = info.getFinalPoints();

        for (int i = 0; i < info.getOnlinePlayers().size(); i++) {
            String cur = "";
            int curPoints = -1;
            for (String player : info.getOnlinePlayers()) {
                Integer playerPoints = 0;
                for (Integer j : finalPoints.get(player)) {
                    playerPoints += j;
                }
                if (playerPoints >= curPoints && !results.getLeaderboard().contains(player)) {
                    curPoints = playerPoints;
                    cur = player;
                }
            }
            results.addLeaderboard(cur);
            results.addTotal(curPoints);
        }
        for(String player : results.getLeaderboard()){
            results.addAdjacencyPoints(finalPoints.get(player).get(3));
            results.addPersonalPoints(finalPoints.get(player).get(2));
            results.addCommonPoints(finalPoints.get(player).get(0)+finalPoints.get(player).get(1));
        }
        updateView(msg.getGameInfo());
        hasEnded = true;
        System.out.print("\n" + out + bold + "THE GAME IS ENDED!\n" + rst + "  press enter to continue...\n" + in);
        //scanner.nextLine();
    }

    /**
     * public method requested by the view interface. It is used to handle the update of the current player.
     * Whenever the player is changed is needed a visualization of the new game state.
     * @param msg is the server message that contains the new current player
     */
    @Override
    public void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg) {
        curPlayer = msg.getUpdatedPlayer();
        if(isDisplaying){
            this.missingGameUpdate = true;
        } else displayGameInfo();
    }

    /**
     * public method requested by the view interface. It is used to handle the rejection of a move from the server
     * @param msg is the message that notify the invalidity of the requested move
     */
    @Override
    public void receiveInvalidMoveMsg(InvalidMoveMessage msg) {
        System.out.print(err + "The selected move is not legal! Retry with a different tiles sequence\n" + in);
    }

    /**
     * public method requested by the view interface. It is used to notify that the lobby has not enough player to continue.
     * If no player joins the game the lobby will be closed by the server
     * @param msg is the server message that notify the client that there are not enough player in the lobby
     */
    @Override
    public void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg) {
        if(!hasEnded) {
            restoreWindow();
            System.out.print(err + "Not enough player to continue the game. If no one reconnects the game will end soon\n" + in);
        }
    }

    /**
     * public method requested by the view interface. It is used to handle the forced closure of a lobby.
     * @param msg is the server message that notify the client that the lobby has been closed
     */
    @Override
    public void receiveLobbyClosedMsg(LobbyClosedMessage msg) {
        if(!hasEnded) {
            restoreWindow();
            System.out.print(err + "The lobby has been closed because there where not enough player. You are going to return to the main menù\n");
            client.disconnect(false);

            resetState();
            if (client instanceof RMIClient) {
                client = new RMIClient(serverIP, rmiPort, this);
            } else if (client instanceof SocketClient) {
                client = new SocketClient(serverIP, tcpPort, this);
            }
            quitState = true;
            new Thread(() -> {
                client.init();
                restoreWindow();
                printCommands();
                resetState();
                waiting = false;
            }).start();
        } else{
            client.disconnect(false);
            if (client instanceof RMIClient) {
                client = new RMIClient(serverIP, rmiPort, this);
            } else if (client instanceof SocketClient) {
                client = new SocketClient(serverIP, tcpPort, this);
            }
            quitState = true;
            new Thread(() -> {
                client.init();
                waiting = false;
            }).start();
        }

    }


    /**
     * public method requested by the view interface. It is used to handle the disconnection of a player of the lobby.
      * @param msg is the server message that notify the other clients of the disconnection of a player
     */
    @Override
    public void receiveUserDisconnectedMsg(UserDisconnectedMessage msg) {
        System.out.print(err + "Player " + msg.getUser() + " has been disconnected\n" + in);
        if(onlinePlayers.contains(msg.getUser())) {
            onlinePlayers.remove(msg.getUser());
            if(isDisplaying){
                this.missingGameUpdate = true;
            }else{
                if(!hasEnded)displayGameInfo();
            }
        }
    }

    /**
     * public method requested by the view interface. It is used to handle the rejection of a command from the server.
     * @param msg is the server message that notify the failure
     */
    @Override
    public void receiveInvalidCommandMsg(InvalidCommandMessage msg) {
        System.out.print(err + "Selected command is not available! To get available commands you type help\n" + in);
    }

    /**
     * public method requested by the view interface. It is used to notify the disconnection of a client from the server
     * @param msg is the server message that notify the disconnection from the server
     */
    @Override
    public void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {
        restoreWindow();
        System.out.print("\n" + rst + red + bold + "You have been disconnected\n");
        if(!quitState) {
            online = false;
            System.out.print(rst + red + bold + "Server is unreachable: check your connection then relaunch the application!\n");
            System.exit(0);
        }
        else{
            quitState = false;
        }
        waiting = false;
    }

    /**
     * public method requested by the view interface. It is used to notify the client that the lobby was not created because
     * the selected lobby name was not valid
     * @param msg is the server message used to notify a malformed lobby name
     */
    @Override
    public void receiveInvalidLobbyNameMsg(InvalidLobbyNameMessage msg){
        System.out.print(err + "This lobby name is not valid. Empty and null string are not allowed\n" + in);
    }

    /**
     * public method requested by the view interface. It is used to notify the client that the lobby was not created because
     * the selected player name was not valid
     * @param msg is the server message used to notify a wrongly formatted player name
     */
    @Override
    public void receiveIllegalPlayerNameMsg(IllegalPlayerNameMessage msg){
        System.out.print(err + "This nickname is not valid. Empty and null string are not allowed\n" + in);
    }

    /**
     * method used to check id the provided ip address is correctly formatted
     * @param ip is the string representing the ipv4 address
     * @return true iff the ip may be valid
     */
    private boolean isValidIP(String ip) {
        if(ip.equals("localhost")) return true;
        String[] groups = ip.split("\\.");
        if (groups.length != 4) {
            return false;
        }
        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() >= 1)
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * method used to manage the creation of a lobby
     */
    private void createLobby(){
        if (client.getIsInLobbyStatus()) {
            System.out.print(rst + err + "You are already in a lobby\n" + in);
        } else {
            String name = askName();
            String lobbyName = askLobbyName();
            System.out.print(out + "Insert number of players\n" + in);
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

    /**
     * method used to manage the request of the currently available lobbies
     */
    private void retrieveLobbies(){
        RetrieveLobbiesMessage clientMessage = new RetrieveLobbiesMessage();
        if (client instanceof RMIClient) {
            clientMessage.setRmiClient((RMIClient) client);
        }
        client.sendMsgToServer(clientMessage);
    }

    /**
     * method used to manage the process of joining a lobby
     */
    private void joinLobby(){
        if (client.getIsInLobbyStatus()) {
            System.out.print(rst + err + "You are already in a lobby\n" + in);
        } else {
            String name = askName();
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

    /**
     * method used to send a message to the procedure needed to send a message to another player or to the whole lobby
     */
    private void sendMessage(){
        if (client.getIsInLobbyStatus()) {
            System.out.print(out + "You have entered the chat. Write a message\n" + in);
            ChatMessage clientMessage = askChatMessage();
            if (client instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) client);
            }
            isDisplaying = false;
            client.sendMsgToServer(clientMessage);
        } else {
            System.out.print(rst + err + "You have to be inside a lobby to use the chat\n" + in);
        }
    }

    /**
     * method used to manage the request of a new move
     */
    private void makeMove(){
        if (client.getIsInLobbyStatus()) {
            if (nickname.equals(curPlayer)) {
                List<Tile> tiles = new ArrayList<>();
                System.out.println(out + "Choose up to 3 tiles from the board to insert in your bookshelf");
                System.out.print(out + "For each tile use the format x,y (x is the horizontal axis, y is the vertical axis) then press enter\n");
                System.out.print(out + "To end the sequence press enter twice\n" + in);
                getMoves(tiles);
                System.out.print(out + "Choose the column of the bookshelf where you want to place the tiles\n" + in);
                int column;
                boolean flag = false;
                while (!flag) {
                    try {
                        column = Integer.parseInt(scanner.nextLine());
                        if (column >= 0 && column < 5) {
                            flag = true;
                            MoveMessage clientMessage = new MoveMessage();
                            clientMessage.setTiles(tiles);
                            clientMessage.setColumn(column);
                            if (client instanceof RMIClient) {
                                clientMessage.setRmiClient((RMIClient) client);
                            }
                            client.sendMsgToServer(clientMessage);
                        } else {
                            System.out.print(rst + err + "Invalid Number, retry\n" + in);
                        }
                    } catch (NumberFormatException e) {
                        System.out.print(rst + err + "Invalid Number, retry\n" + in);
                    }
                }
            } else {
                System.out.print(rst + err + "You are not the current player\n" + in);
            }
        } else {
            System.out.print(rst + err + "You have to be inside a game to make a move\n" + in);
        }
    }

    /**
     * method used manage the procedure needed in order to leave a lobby
     */
    private void quitLobby(){
        if (client.getIsInLobbyStatus()) {
            System.out.print(out + bold + "Are you sure? [y/N]\n" + in);
            String quit = scanner.nextLine();
            if (quit.equals("y") || quit.equals("Y")) {
                QuitMessage clientMessage = new QuitMessage();
                waiting = true;
                if (client instanceof RMIClient) {
                    clientMessage.setRmiClient((RMIClient) client);
                    client.sendMsgToServer(clientMessage);
                    //while (waiting){
                        try{
                            sleep(500);
                        } catch (InterruptedException ignored){
                        }
                    //}
                    client = new RMIClient(serverIP, rmiPort, this);
                } else if (client instanceof SocketClient) {
                    client.sendMsgToServer(clientMessage);
                    while (waiting){
                        try{
                            sleep(500);
                        } catch (InterruptedException ignored){
                        }
                    }
                    client = new SocketClient(serverIP, tcpPort, this);
                }
                new Thread(()->{
                    client.init();
                    restoreWindow();
                    printCommands();
                    waiting = false;
                }).start();
                resetState();
            }

        } else {
            System.out.print(rst + err + "You have to be inside a lobby to use this feature\n" + in);
        }
    }
}
