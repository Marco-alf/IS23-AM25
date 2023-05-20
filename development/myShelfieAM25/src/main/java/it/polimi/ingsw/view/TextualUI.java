package it.polimi.ingsw.view;

import it.polimi.ingsw.model.PersonalGoal;
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

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;


public class TextualUI implements ViewInterface {
    private final Scanner scanner = new Scanner(System.in);
    private GenericClient client;
    private TilesType[][] board;
    private final List<String> onlinePlayers = new ArrayList<>();
    private Map<String, TilesType[][]> shelves = new HashMap<>();
    private final Map<String, Integer[]> commonGoals = new HashMap<>();
    private String commonGoal1;
    private String commonGoal2;
    private Integer personalPoints;
    private String nickname;
    private String curPlayer;
    private PersonalGoal personalGoal;
    private final List<ChatUpdateMessage> messages = new ArrayList<>();
    private static final List<String> commands = List.of("/create", "/join", "/retrieve","/chat","/showchat", "/help", "/move", "/quit", "/gamestate");
    private static final String rst = "\u001B[0m";
    private static final String whiteBack = "\u001B[48;5;" + 15 + "m";
    private static final String red = "\u001B[38;5;" + 1 + "m";
    private final static String yellow = "\u001B[38;5;" + 11 + "m";
    private static final String bold = "\u001B[1m";
    private static final String out = rst + yellow + bold + " > " + rst;
    private static final String in = rst + bold + "\u001B[38;5;" + 6 + "m >>> ";
    private static final String unBold = "\u001B[2m";
    private static final String err = "\u001B[1m\u001B[38;5;" + 1 + "m ERROR: ";
    private static String serverIP;
    private static String lastMessage = "_";
    private boolean hasEnded = false;
    private Lock lock;
    private Lock chatLock;
    private Lock stateLock;
    private boolean display;
    private final GameResults results = new GameResults();
    public void start() {
        display = false;
        lock = new ReentrantLock();
        chatLock = new ReentrantLock();
        stateLock = new ReentrantLock();
        String inputCommand;
        boolean hasMessage;
        ClientMessage clientMessageOut = null;
        lock.lock();
        try{
            System.out.println(out + "Insert the \u001B[1mserver IP address"+rst+".");
            System.out.print(in);
            serverIP = scanner.nextLine();
            System.out.println(out + "Insert \u001B[1m/rmi"+rst+" if you want to join server with rmi \u001B[1m/socket"+rst+" if you want to access it with socket");
            System.out.print(in);

            String connType = scanner.nextLine();

            while (!connType.equals("/rmi") && !connType.equals("/socket")) {
                System.out.print(rst + err + "This type of connection is not supported\n" + in);
                connType = scanner.nextLine();
            }
            if (connType.equals("/rmi")) {
                client = new RMIClient(serverIP, 1099, this);
                client.init();
            } else {
                client = new SocketClient(serverIP, 8088, this);
                boolean socketError = true;
                while (socketError) {
                    client.init();
                    socketError = false;
                }
            }
            restoreWindow();

            printCommands();
            System.out.print(in);
        }
        finally {
            lock.unlock();
        }
        while (!hasEnded) {
            hasMessage = false;
            try {
                sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            inputCommand = askCommand();
            lock.lock();
            try{
                switch (inputCommand) {
                    case "/create" -> {
                        if (client.getIsInLobbyStatus()) {
                            System.out.print(out + "You are already in a lobby\n" + in);
                        } else {
                            String name = askName();
                            String lobbyName = askLobbyName();
                            System.out.print(out + "Insert number of players\n" + in);
                            int numPlayers = askNumPlayers();
                            CreateLobbyMessage clientMessage = new CreateLobbyMessage();
                            clientMessage.setLobbyName(lobbyName);
                            clientMessage.setLobbyCreator(name);
                            clientMessage.setPlayerNumber(numPlayers);
                            hasMessage = true;
                            clientMessageOut = clientMessage;
                        }
                    }
                    case "/retrieve" -> {
                        RetrieveLobbiesMessage clientMessage = new RetrieveLobbiesMessage();
                        hasMessage = true;
                        clientMessageOut = clientMessage;
                    }
                    case "/join" -> {
                        if (client.getIsInLobbyStatus()) {
                            System.out.print(rst + err + "already in a lobby!\n");
                        } else {
                            String name = askName();
                            String lobbyName = askLobbyName();
                            JoinMessage clientMessage = new JoinMessage();
                            clientMessage.setName(name);
                            clientMessage.setLobbyName(lobbyName);
                            hasMessage = true;
                            clientMessageOut = clientMessage;
                        }
                    }
                    case "/chat" -> {
                        if (client.getIsInLobbyStatus()) {
                            System.out.print(out + "You have entered the chat. Write a message\n" + in);

                            hasMessage = true;
                            clientMessageOut = askChatMessage();

                        } else {
                            System.out.print(rst + err + "You have to be inside a lobby to use the chat\n" + in);
                        }
                    }
                    case "/showchat" -> {
                        if (client.getIsInLobbyStatus()) {
                            displayChat();
                        } else {
                            System.out.print(rst + err + "You have to be inside a lobby to use the chat\n" + in);
                        }
                    }
                    case "/move" -> {
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
                                            hasMessage = true;

                                            clientMessageOut = clientMessage;
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
                    case "/quit" -> {
                        if (client.getIsInLobbyStatus()) {
                            System.out.print(out + bold + "Are you sure? [y/N]\n" + in);
                            String quit = scanner.nextLine();
                            if (quit.equals("y") || quit.equals("Y")) {
                                clientMessageOut = new QuitMessage();
                                hasMessage = true;

                                if (client instanceof RMIClient) {
                                    client = new RMIClient(serverIP, 1099, this);
                                } else if (client instanceof SocketClient) {
                                    client = new SocketClient(serverIP, 8088, this);
                                }
                                client.init();
                                System.out.print(in + " ");
                            }

                        } else {
                            System.out.println(rst + err + "You have to be inside a lobby to use this feature" + in);
                        }
                    }
                    case "/help" -> {
                        printCommands();
                        System.out.print(in);
                    }
                    case "/gamestate" -> {
                        if (client.getIsInLobbyStatus()) {
                            displayGameInfo();
                        } else {
                            System.out.print(rst + err + "You have to be inside a game to use this functionality\n" + in);
                        }
                    }
                }
            }
            finally {
                lock.unlock();
            }
            if(display) displayGameInfo();
            if (!lastMessage.equals("_")) {
                System.out.println(out + "You have unread " + bold + "messages" + rst + ". To see them you can use" + bold + " /retrieve" + rst + " command\n     > last message: " +
                        lastMessage);
                lastMessage = "_";
            }
            display = false;
            if(hasMessage){
                if (client instanceof RMIClient) {
                    clientMessageOut.setRmiClient((RMIClient) client);
                }
                client.sendMsgToServer(clientMessageOut);
            }
        }
        manageEndGame();
    }

    public void displayLeaderBoard(){
        lock.lock();
        try {
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
                    "           ███████ ███████ ██   ██ ██████  ███████ ██   ██ ██████   ██████  ██   ██ ██   ██ ██████   \n\n");
            System.out.print(rst + yellow + bold +"1. \n");
            int middle = 8;
            int increment;
            if (results.getLeaderboard().get(0).length() / 2 > middle - 1) {
                increment = results.getLeaderboard().get(0).length() / 2 - middle;
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
                for (int i = 0; i < increment; i++) {
                    System.out.print(" ");
                }
                System.out.println(rst + bold);

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
            System.out.println(rst + yellow + bold + "Total points: " + rst + bold + results.getTotals().get(0));
            System.out.println(rst + "Personal Goal points: " + bold + results.getPersonalPoints().get(0));
            System.out.println(rst + "Common Goal points: " + bold + results.getCommonPoints().get(0));
            System.out.println(rst + "Adjacency points: " + bold + results.getAdjacencyPoints().get(0));

            for (int i = 1; i < results.getLeaderboard().size(); i++) {
                int j = i + 1;
                System.out.println(yellow + bold + "\n" + j + ". " + results.getLeaderboard().get(i) + ": ");
                System.out.println(rst + bold + "Total points: " + yellow + results.getTotals().get(i));
                System.out.println(rst + "Personal Goal points: " + bold + results.getPersonalPoints().get(i));
                System.out.println(rst + "Common Goal points: " + bold + results.getCommonPoints().get(i));
                System.out.println(rst + "Adjacency points: " + bold + results.getAdjacencyPoints().get(i));
            }
        }
        finally {
            lock.unlock();
        }
    }
    public void restoreWindow(){
        lock.lock();
        try {
            //System.out.print("\033[H\033[2J");
            //System.out.flush();
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
        finally {
            lock.unlock();
        }
    }
    public void getMoves(List<Tile> tiles){
        lock.lock();
        try {
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
        finally {
            lock.unlock();
        }
    }
    public void printCommands(){
        lock.lock();
        try {
            System.out.print("\u001B[0m");
            System.out.println("\u001B[1mList of commands:" + rst);
            System.out.println(yellow + "/create:\u001B[0m create a lobby");
            System.out.println(yellow + "/join:\u001B[0m join an existing lobby");
            System.out.println(yellow + "/retrieve:\u001B[0m get a list of available lobbies");
            System.out.println(yellow + "/chat:\u001B[0m write a message in the chat");
            System.out.println(yellow + "/showchat:\u001B[0m show the chat history");
            System.out.println(yellow + "/move:\u001B[0m make a move");
            System.out.print(yellow + "/gamestate:\u001B[0m show the current game state\n");
            System.out.println(yellow + "/quit:\u001B[0m leave a lobby");
            System.out.print(yellow + "/help:\u001B[0m show this list of commands\n");
        }
        finally {
            lock.unlock();
        }

    }
    public void updateView (GameInfo info) {
        stateLock.lock();
        try {
            board = info.getNewBoard();
            if (info instanceof InitialGameInfo) {
                shelves = ((InitialGameInfo) info).getShelves();
                for (String player : info.getPlayers()) {
                    commonGoals.put(player, new Integer[]{0, 0});
                    onlinePlayers.add(player);
                }
                personalGoal = ((InitialGameInfo) info).getPersonalGoals().get(nickname);
                commonGoal1 = ((InitialGameInfo) info).getCommonGoal1();
                commonGoal2 = ((InitialGameInfo) info).getCommonGoal2();
            } else {
                shelves.put(info.getCurrentPlayer(), info.getShelf());
                commonGoals.replace(info.getCurrentPlayer(), new Integer[]{info.getCommonGoal1Points(), info.getCommonGoal2Points()});
            }
            if (onlinePlayers.size() != info.getOnlinePlayers().size()) {
                for (String player : info.getOnlinePlayers()) {
                    if (!onlinePlayers.contains(player)) onlinePlayers.add(player);
                }
            }
            if (curPlayer == null) personalPoints = 0;
            else if(nickname.equals(curPlayer)) personalPoints = info.getPersonalGoalPoints();
            curPlayer = info.getCurrentPlayer();
            hasEnded = info.isGameEnded();
        }
        finally {
            stateLock.unlock();
        }
    }
    public void displayLobbies (List<String> lobbies) {
        lock.lock();
        try {
            System.out.println(rst + bold + out + "Available lobbies:" + rst);
            for (String lobby : lobbies) {
                System.out.println(rst + "   " + out + lobby);
            }
        }
        finally {
            lock.unlock();
        }
    }
    public void displayCreatedLobbyMsg (CreatedLobbyMessage msg) {
        lock.lock();
        try {
            System.out.println(out + "A lobby has been created");
            System.out.println(rst + "   Lobby name: " + bold + msg.getLobbyName() + unBold);
            System.out.print(rst + "   Lobby creator: " + bold + msg.getName() + unBold + "\n");
        }
        finally {
            lock.unlock();
        }
    }
    public void displayChat () {
        lock.lock();
        chatLock.lock();
        try {
            String sender;
            for (ChatUpdateMessage message : messages) {
                sender = message.getSender();
                if (sender.equals(nickname)) sender = "You";
                if (message.getType().equals("PrivateChatUpdateMessage")) {
                    System.out.println(out + bold + sender + " at " + message.getTimestamp() + unBold + " to " +
                            ((PrivateChatUpdateMessage) message).getReceiver() + red + bold + " [private]" + rst + ": \n" +
                            "      " + message.getContent() + rst);
                } else {
                    System.out.println(out + bold + sender + " at " + message.getTimestamp() + unBold + ": \n      " + message.getContent());
                }
            }
            System.out.print(in);
        }
        finally {
            chatLock.unlock();
            lock.unlock();
        }
    }
    public void displayGameInfo () {
        if(lock.tryLock() && chatLock.tryLock()) {
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
            System.out.println(out + "current player is: " + red + bold + this.curPlayer + rst);
            System.out.print(in);
            lock.unlock();
            chatLock.unlock();
        }
        else{
            display = true;
        }
    }
    public Tile getTiles (String coords) {
        int x;
        int y;
        stateLock.lock();
        try {
            x = Integer.parseInt(String.valueOf(coords.charAt(0)));
            y = Integer.parseInt(String.valueOf(coords.charAt(2)));
            if (x < 0 || y < 0 || x > 8 || y > 8) return null;
        }
        finally {
            stateLock.unlock();
        }
        return new Tile(board[y][x], x, y);
    }
    public String getTile (TilesType type) {
        String s = "";
        stateLock.lock();
        try {
            //52
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
        }
        finally {
            stateLock.unlock();
        }
        s += "\u001B[0m";
        return s;
    }
    public String askCommand() {
        String command;
        command = scanner.nextLine();
        while(!commands.contains(command) && !hasEnded){
            System.out.print("\u001B[38;5;" + 1 + "m\u001B[1m ERROR: invalid command!  ");
            System.out.print("\u001B[0m To see available commands type \u001B[1m\"/help\".\u001B[0m\n" + in);
            command = scanner.nextLine();
        }
        return command;
    }
    public String askName() {
        String name;
        String confirm;
        lock.lock();
        try {
            do {
                System.out.print(out + "Insert username\n" + in);
                name = scanner.nextLine();
                System.out.print(out + "Your name is: " + bold + name + unBold + ". Is it right [Y/n]?\n" + in);
                confirm = scanner.nextLine();
            } while (confirm.equals("n"));
            nickname = name;
        }
        finally {
            lock.unlock();
        }
        return name;
    }
    public ChatMessage askChatMessage() {
        String chatMessage, response = "Y";
        ChatMessage message;
        chatLock.lock();
        try {
            chatMessage = scanner.nextLine();
            if (onlinePlayers.size() > 1) {
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
        }
        finally {
            chatLock.unlock();
        }

        return message;
    }
    public String askLobbyName() {
        String name;
        String confirm;
        lock.lock();
        try {
            do {
                System.out.print(out + "Insert lobby name\n" + in);
                name = scanner.nextLine();
                System.out.print(out + "The name is: " + bold + name + unBold + ". Is it right [Y/n]?\n" + in);
                confirm = scanner.nextLine();
            } while (confirm.equals("n") || confirm.equals("N"));
        }
        finally {
            lock.unlock();
        }
        return name;
    }
    public int askNumPlayers() {
        int numPlayers;
        lock.lock();
        try {
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
        }
        finally {
            lock.unlock();
        }
        return numPlayers;
    }
    public String[] boardConstructor (TilesType[][] matrix) {
        String[] board = new String[20];
        stateLock.lock();
        try {
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
        }
        finally {
            stateLock.unlock();
        }
        return board;
    }
    public String[] shelfConstructor(TilesType[][] matrix, String player) {
        String[] shelf = new String[18]; //length = 31 + space
        stateLock.lock();
        try {
            shelf[0] = rst;
            int l;
            int delta = 0;
            shelf[1] = rst + "    " + bold + player + ": " + unBold + rst;
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
                    + unBold + rst + "  2: " + whiteBack + red + bold + " " + commonGoals.get(player)[1].toString() + " " + rst;
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
                    //printTile(shelf[i][j]);
                    shelf[h] += getTile(matrix[i][j]);
                    shelf[h] += rst + yellow + "|";
                }
                for(int j=37; j < l; j++)shelf[h]+=" ";
                h++;
                shelf[h] = "      +";
                for (int j = 0; j < 5; j++) {
                    shelf[h] += "-----+";
                }
                for(int j=37; j < l; j++)shelf[h]+=" ";
                h++;
            }
            shelf[17] = rst + "         0     1     2     3     4   ";
            for(int j=37; j < l; j++)shelf[h]+=" ";
        }
        finally {
            stateLock.unlock();
        }
        return shelf;
    }
    public String[] concatStringArrays(String[] left, String[] right){
        //refactor matrices to get perfects rectangles
        int maxh;
        String[] matrix;
        stateLock.lock();
        try {
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
        }
        finally {
            stateLock.unlock();
        }
        return matrix;
    }
    public String convertStringArray(String[] in){
        String s = "";
        for (String value : in) {
            s += value + "\n" + rst;
        }
        return s;
    }
    public String[] personalGoalConstructor(PersonalGoal personalGoal){
        String[] shelf = new String[17]; //length = 31 + 7 space
        TilesType[][] matrix = personalGoal.getMatrix();
        int length = 55;

        stateLock.lock();
        try {
            shelf[0] = rst;
            for (int i = 0; i < length; i++) {
                shelf[0] += " ";
            }

            shelf[1] = rst + bold + "   your personal goal: ";

            for (int i = 23; i < length; i++) {
                shelf[1] += " ";
            }
            shelf[1] += unBold;
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
                shelf[h] = "       +";
                for (int j = 0; j < 5; j++) {
                    shelf[h] += "-----+";
                }
                for (int j = 38; j < length; j++) shelf[h] += " ";
                h++;
            }
            shelf[16] = rst + "          0     1     2     3     4                    ";
        }
        finally {
            stateLock.unlock();
        }
        return shelf;
    }
    public String[] commonGoalConstructor(String commonGoal, int pos){
        String[] goal;
        stateLock.lock();
        try {
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
        }
        finally {
            stateLock.unlock();
        }
        return goal;
    }

    //the next methods are added to not create errors but will probably be deleted

    @Override
    public void receiveCreatedLobbyMsg(CreatedLobbyMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            displayCreatedLobbyMsg(msg);
            System.out.print(in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveJoinedMsg(JoinedMessage msg) {
        stateLock.lock();
        try {
            restoreWindow();
            System.out.print(out + "You have joined " + msg.getLobbyName() + " as " + msg.getName() + "\n" + in);
        }
        finally {
            stateLock.unlock();
        }
    }

    @Override
    public void receiveExistingLobbyMsg(ExistingLobbyMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "A lobby with the selected name already exists! \n You can join it with the command /join\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "An error occurred, the lobby was not created\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveNameTakenMsg(NameTakenMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "The selected name is already taken by another player. You can retry to join with another one\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "No existing lobby matches the selected name! You can get the names of existing lobbies with the command /retrieve\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveFullLobbyMsg(FullLobbyMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "The selected lobby is full. To see all available lobbies you can use /retrieve\n" + in);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            displayLobbies(msg.getLobbies());
            System.out.print(in);
        }
        finally{
            lock.unlock();
        }
    }

    @Override
    public void receiveChatUpdateMsg(ChatUpdateMessage msg) {
        if(lock.tryLock() && chatLock.tryLock()) {
            try {
                if (msg.getSender().equals(nickname)) {
                    System.out.print(out + bold + "You just write" + rst + ":  " + msg.getContent() + "\n" + in);
                } else {
                    System.out.print("\n" + out + bold + msg.getSender() + " just write" + unBold + ":  " + msg.getContent() + "\n" + in);
                }
            }
            finally {
                lock.unlock();
                chatLock.unlock();
            }
        }
        else if(!msg.getSender().equals(nickname))lastMessage = rst + bold + msg.getSender() +": " + rst + msg.getContent() + rst;
        messages.add(msg);
    }

    public void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg) {
        if(lock.tryLock() && chatLock.tryLock()) {
            if (msg.getReceiver().equals(nickname)) {
                System.out.print("\n" + out + bold + msg.getSender() + " just write" + ":  " + msg.getContent() + red + bold + " [private]\n" + in);
                try {
                    sleep(600);
                } catch (InterruptedException e) {
                    throw new RuntimeException("interrupt during sleep!");
                }
                messages.add(msg);
            }
            if (msg.getSender().equals(nickname)) {
                messages.add(msg);
                System.out.print(out + "You have sent a private message to: " + bold + msg.getReceiver() + "\n" + in);
            }
            lock.unlock();
            chatLock.unlock();
        }
        else if(!msg.getSender().equals(nickname)) lastMessage = rst + bold + msg.getSender() +": " + rst + msg.getContent() + red + bold + " [private]" + rst;
    }

    @Override
    public void receiveGameCreatedMsg(GameCreatedMessage msg) {
        stateLock.lock();
        try {
            updateView(msg.getGameInfo());
        }
        finally {
            stateLock.unlock();
        }
    }
    @Override
    public void receiveGameUpdatedMsg(GameUpdatedMessage msg) {
        stateLock.lock();
        try {
            updateView(msg.getGameInfo());
        }
        finally {
            stateLock.unlock();
        }
    }
    public void manageEndGame(){
        String command;
        List<String> availableCommands = List.of("/quit", "/gamestate", "/chat", "/showchat", "/leaderboard");
        ClientMessage clientMessageOut = null;
        boolean hasMessage;


        System.out.print("\n" + out + bold + "THE GAME IS ENDED!\n" + rst + "  press any key to continue...\n" + in);
        displayLeaderBoard();
        System.out.print(out + "\nHere is a list of available commands:\n" +
                "/quit: to leave the lobby\n" +
                "/gamestate: to see the final state of the game\n" +
                "/chat: to use the chat\n" +
                "/showchat: to visualize the chat\n" +
                "/leaderboard: to show leaderboard\n" + in);

        while(true) {
            command = scanner.nextLine();
            while (!availableCommands.contains(command)) {
                System.out.println(rst + err + bold + "given command is not available\n" + in);
                command = scanner.nextLine();
            }
            lock.lock();
            try {
                hasMessage = false;
                switch (command) {
                    case "/quit" -> {
                        System.out.print(out + bold + "Are you sure? [Y/n]\n" + in);
                        String quit = scanner.nextLine();
                        if (!quit.equals("n") && !quit.equals("N")) {
                            clientMessageOut = new QuitMessage();
                            hasMessage = true;
                            if (client instanceof RMIClient) {
                                client = new RMIClient(serverIP, 1099, this);
                            } else if (client instanceof SocketClient) {
                                client = new SocketClient(serverIP, 8088, this);
                            }
                            client.init();
                            System.out.print(in);
                        }
                    }
                    case "/gamestate" -> displayGameInfo();

                    case "/chat" -> {
                        System.out.print(out + "You have entered the chat. Write a message\n" + in);

                        hasMessage = true;
                        lock.unlock();
                        clientMessageOut = askChatMessage();
                        lock.lock();
                    }
                    case "/showchat" -> displayChat();
                    case "/leaderboard" -> {
                        displayLeaderBoard();
                        System.out.print(in);
                    }
                }
                if (hasMessage) {
                    if (client instanceof RMIClient) {
                        clientMessageOut.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessageOut);
                    System.out.print(in);
                }
            } finally {
                lock.unlock();
            }
            try{
                sleep(300);
            }catch (InterruptedException e){
                throw new RuntimeException();
            }
        }
    }
    public void receiveGameEndedMsg(GameEndedMessage msg){
        FinalGameInfo info = msg.getGameInfo();
        Map<String, List<Integer>> finalPoints = info.getFinalPoints();

        updateView(msg.getGameInfo());

        lock.lock();
        try{
            for (int i = 0; i < info.getOnlinePlayers().size(); i++) {
                String cur = "";
                int curPoints = 0;
                for (String player : info.getOnlinePlayers()) {
                    Integer playerPoints = 0;
                    for (Integer j : finalPoints.get(player)) {
                        playerPoints += j;
                    }
                    if (playerPoints > curPoints && !results.getLeaderboard().contains(player)) {
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
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg) {
        stateLock.lock();
        try {
            curPlayer = msg.getUpdatedPlayer();
            displayGameInfo();
        }
        finally {
            stateLock.unlock();
        }
    }

    @Override
    public void receiveInvalidMoveMsg(InvalidMoveMessage msg) {
        lock.lock();
        try {
            System.out.print(err + "The selected move is not legal! Retry with a different tiles sequence\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg) {
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "Not enough player to continue the game. If no one reconnects the game will end soon\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveLobbyClosedMsg(LobbyClosedMessage msg) {
        QuitMessage clientMessage = new QuitMessage();
        lock.lock();
        try {
            restoreWindow();
            System.out.print(err + "The lobby has been closed because there where not enough player. You are going to return to the main menù");
            try {
                sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            if (client instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) client);
            }
            client.sendMsgToServer(clientMessage);
            restoreWindow();
            printCommands();
        }
        finally {
            lock.unlock();
        }
        if (client instanceof RMIClient) {
            client = new RMIClient(serverIP, 1099, this);
        } else if (client instanceof SocketClient) {
            client = new SocketClient(serverIP, 8088, this);
        }
        client.init();
    }

    @Override
    public void receiveUserDisconnectedMsg(UserDisconnectedMessage msg) {
        lock.lock();
        try {
            System.out.print(err + "Player " + msg.getUser() + " has been disconnected\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveInvalidCommandMsg(InvalidCommandMessage msg) {
        lock.lock();
        try {
            System.out.print(err + "Selected command does not exists! To get available commands you type /help\n" + in);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {
        lock.lock();
        try {
            System.out.print(err + "An unexpected connectivity error occurred. You have been disconnected\n" + in);
        }
        finally {
            lock.unlock();
        }
    }
}
