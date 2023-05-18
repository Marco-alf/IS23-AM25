package it.polimi.ingsw.view;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.serverMessages.*;

import java.util.*;

import static java.lang.Thread.sleep;


public class TextualUI implements ViewInterface {
    private final Integer FRAMESB = 25;
    private final Integer FRAMESF = 15;
    private final Integer GAMEB = 11;
    private final Integer GAMEF = 0;
    private final Integer CATSB = 112;
    private final Integer CATSF = 0;
    private final Integer BOOKSB = 230;
    private final Integer BOOKSF = 0;
    private final Integer PLANTSB = 126;
    private final Integer PLANTSF = 15;
    private final Integer TROPHIESB = 44;
    private final Integer TROPHIESF = 0;
    private final Integer BROWN = 59; //52
    private final Scanner scanner = new Scanner(System.in);
    private GenericClient client;
    private TilesType[][] board;
    private List<String> onlinePlayers = new ArrayList<>();
    private Map<String, TilesType[][]> shelves = new HashMap<>();
    private Map<String, Integer[]> commonGoals = new HashMap<>();
    private String commonGoal1;
    private String commonGoal2;
    private Integer personalPoints;
    private String nickname;
    private String curPlayer;
    private PersonalGoal personalGoal;
    private boolean isEndGame = false;
    private final List<ChatUpdateMessage> messages = new ArrayList<>();
    private static final List<String> commands = List.of("/create", "/join", "/retrieve","/chat","/showchat", "/help", "/move", "/quit", "/gamestate");
    private static final String rst = "\u001B[0m";
    private static final String whiteBack = "\u001B[48;5;" + 15 + "m";
    private static final String red = "\u001B[38;5;" + 9 + "m";
    private final static String yellow = "\u001B[38;5;" + 11 + "m";
    private static final String out = rst + yellow + " > " + rst;
    private static final String in = rst + "\u001B[38;5;" + 6 + "m >>> ";
    private static final String bold = "\u001B[1m";
    private static final String unBold = "\u001B[2m";
    private static final String err = "\u001B[1m\u001B[38;5;" + 1 + "m ERROR: \u001B[2m";
    public void start() {
        String inputCommand = "";
        boolean hasMessage;
        ClientMessage clientMessageOut = null;
        synchronized (this) {
            System.out.println(out + "m Insert \u001B[1m/rmi\u001B[2m if you want to join server with rmi \u001B[1m/socket\u001B[2m if you want to access it with socket");
            System.out.print(in);

            String connType = scanner.nextLine();

            while (!connType.equals("/rmi") && !connType.equals("/socket")) {
                System.out.print(rst + err + "This type of connection is not supported\n" + in);
                connType = scanner.nextLine();
            }
            if (connType.equals("/rmi")) {
                client = new RMIClient("localhost", 1099, this);
                client.init();
            } else {
                client = new SocketClient("localhost", 8088, this);
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
        while (true) {
            hasMessage = false;
            inputCommand = askCommand();
            synchronized (this) {
                switch (inputCommand) {
                    case "/create" -> {
                        if (client.getIsInLobbyStatus()) {
                            System.out.print(out + "You are already in a lobby\n");
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
                                System.out.print(out + "To end the sequenze press enter twice\n" + in);
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
                                QuitMessage clientMessage = new QuitMessage();
                                clientMessageOut = clientMessage;
                                hasMessage = true;

                                if (client instanceof RMIClient) {
                                    client = new RMIClient("localhost", 1099, this);
                                } else if (client instanceof SocketClient) {
                                    client = new SocketClient("localhost", 8088, this);
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
            if(hasMessage){
                if (client instanceof RMIClient) {
                    clientMessageOut.setRmiClient((RMIClient) client);
                }
                client.sendMsgToServer(clientMessageOut);
            }
        }

    }
/*
    public synchronized void endGame() {
        if (!isEndGame) return;
        System.out.println(rst + "    " + yellow  + bold + "THE GAME IS ENDED!"+rst);
        try {
            sleep(3000);
        }catch (InterruptedException e){
            throw new RuntimeException();
        }

        displayEndGame();
        while()
    }
    public synchronized void displayEndGame(){
        restoreWindow();
        List<String> leaderboard = new ArrayList<String>;
        System.out.println("\n\n\n" + yellow +
                "           ██      ███████  █████  ██████  ███████ ██████  ██████   ██████   █████  ██████  ██████      \n" +
                "           ██      ██      ██   ██ ██   ██ ██      ██   ██ ██   ██ ██    ██ ██   ██ ██   ██ ██   ██    ██ \n" +
                "           ██      █████   ███████ ██   ██ █████   ██████  ██████  ██    ██ ███████ ██████  ██   ██     \n" +
                "           ██      ██      ██   ██ ██   ██ ██      ██   ██ ██   ██ ██    ██ ██   ██ ██   ██ ██   ██    ██\n" +
                "           ███████ ███████ ██   ██ ██████  ███████ ██   ██ ██████   ██████  ██   ██ ██   ██ ██████   \n");
        for(String player : shelves.keySet()){

        }
        System.out.println("        * \n" +
                "   <*> <*> <*>\n" +
                " <*><*><*><*><*>       \n" +
                "\"\\\"\\\"\\\"\\|/\"/\"/\"/\"\n" +
                "  \"\\\"\\\"\\|/\"/\"/\" \n" +
                "    <*><*><*>");
    }*/
    public synchronized void restoreWindow(){
        System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(yellow  +
                "                             ███╗   ███╗██╗   ██╗        ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗\n"+
                "                             ████╗ ████║╚██╗ ██╔╝        ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝\n"+
                "                             ██╔████╔██║ ╚████╔╝         ███████╗███████║█████╗  ██║     █████╗  ██║█████╗\n"+
                "                             ██║╚██╔╝██║  ╚██╔╝          ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝\n"+
                "                             ██║ ╚═╝ ██║   ██║           ███████║██║  ██║███████╗███████╗██║     ██║███████╗\n"+
                "                             ╚═╝     ╚═╝   ╚═╝           ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝\n");
    }
    public synchronized void getMoves(List<Tile> tiles){
        for(int i=0; i < 3; i++){
            String coords = scanner.nextLine();
            if(coords.equals("")) i=3;
            else {
                while (!coords.matches("\\d{1},\\d{1}")) {
                    System.out.print(rst + err +"The inserted cell are in a wrong format\n"+in);
                    coords = scanner.nextLine();
                }
                tiles.add(getTiles(coords));
            }
            if(i < 2 && !coords.equals("")){
                System.out.print(in);
            }
        }
    }
    public synchronized void printCommands(){
        System.out.print("\u001B[0m");
        System.out.println("\u001B[1mList of commands:\u001B[2m");
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
    public synchronized void updateView (GameInfo info) {
        board = info.getNewBoard();
        if (info instanceof InitialGameInfo) {
            shelves = ((InitialGameInfo) info).getShelves();
            for(String player : info.getPlayers()){
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
        if(onlinePlayers.size()!=info.getOnlinePlayers().size()){
            for(String player : info.getOnlinePlayers()){
                if(!onlinePlayers.contains(player)) onlinePlayers.add(player);
            }
        }
        curPlayer = info.getCurrentPlayer();
        personalPoints = info.getPersonalGoalPoints();
        isEndGame = info.isGameEnded();
    }
    public synchronized void displayLobbies (List<String> lobbies) {
        System.out.println(rst + bold + out + "Available lobbies:" + rst);
        for (String lobby : lobbies) {
            System.out.println(rst + "   "+ out +lobby);
        }
    }
    public synchronized void displayCreatedLobbyMsg (CreatedLobbyMessage msg) {
        System.out.println(out + "A lobby has been created");
        System.out.println("   Lobby name: " + bold + msg.getLobbyName() + unBold);
        System.out.print("   Lobby creator: " + bold + msg.getName() + unBold + "\n");
    }
    public synchronized void displayChat () {
        String sender;
        for (ChatUpdateMessage message : messages) {
            sender = message.getSender();
            if(sender.equals(nickname)) sender = "You";
            if(message.getType().equals("PrivateChatUpdateMessage")) {
                System.out.println(out + bold + sender + " at " + message.getTimestamp() + unBold + " to "+
                        ((PrivateChatUpdateMessage)message).getReceiver() + red + bold + " [private]" + rst + ": \n" +
                        "      " + message.getContent() +rst);
            }
            else{
                System.out.println(out + bold + sender + " at " + message.getTimestamp() + unBold + ": \n      " + message.getContent());
            }
        }
        System.out.print(in);
    }
    public synchronized void displayGameInfo () {
        restoreWindow();
        String[] output = boardConstructor(board);

        for (String player : shelves.keySet()) {
            output = concatStringArrays(output, shelfConstructor(shelves.get(player), player, commonGoals.get(player)[0], commonGoals.get(player)[0]));
        }
        String info = convertStringArray(output);

        System.out.print(info);

        output = personalGoalConstructor(personalGoal);
        output = concatStringArrays(output, commonGoalConstructor(commonGoal1, 1));
        output = concatStringArrays(output, commonGoalConstructor(commonGoal2, 2));

        info = convertStringArray(output);

        System.out.print(info);
        System.out.println(yellow + "current player is: " + red + bold + this.curPlayer + rst);
    }
    public synchronized Tile getTiles (String coords) {
        int x = Integer.parseInt(String.valueOf(coords.charAt(0)));
        int y = Integer.parseInt(String.valueOf(coords.charAt(2)));
        return new Tile(board[y][x], x, y);
    }
    public synchronized String getTile (TilesType type) {
        String s = "";
        if(type==null) {
            s += (char) 27 + "[48;5;" + BROWN + "m     ";
            s += "\u001B[0m";
            return s;
        }
        switch (type) {
            case TROPHIES -> {
                s += (char) 27 + "[48;5;" + TROPHIESB + "m" +  (char) 27 + "[38;5;" + TROPHIESF + "m  T  ";
            }
            case FRAMES -> {
                s += (char) 27 + "[48;5;" + FRAMESB + "m" +  (char) 27 + "[38;5;" + FRAMESF + "m  F  ";
            }
            case PLANTS -> {
                s += (char) 27 + "[48;5;" + PLANTSB + "m"  +  (char) 27 + "[38;5;" + PLANTSF + "m  P  ";
            }
            case CATS -> {
                s += (char) 27 + "[48;5;" + CATSB + "m" +  (char) 27 + "[38;5;" + CATSF + "m  C  ";
            }
            case GAMES -> {
                s += (char) 27 + "[48;5;" + GAMEB + "m" +  (char) 27 + "[38;5;" + GAMEF + "m  G  ";
            }
            case BOOKS -> {
                s += (char) 27 + "[48;5;" + BOOKSB + "m" +  (char) 27 + "[38;5;" + BOOKSF + "m  B  ";
            }
            default -> {
                s += (char) 27 + "[48;5;" + BROWN + "   ";
            }
        }
        s += "\u001B[0m";
        return s;
    }
    public String askCommand() {
        String command;
        command = scanner.nextLine();
        while(!commands.contains(command)){
            System.out.print("\u001B[38;5;" + 1 + "m\u001B[1m ERROR: invalid command!  ");
            System.out.print("\u001B[0m To see available commands type \u001B[1m\"/help\".\u001B[0m\n" + in);
            command = scanner.nextLine();
        }
        return command;
    }
    public synchronized String askName() {
        String name;
        String confirm;
        do{
            System.out.print(out + "Insert username\n" + in);
            name = scanner.nextLine();
            System.out.print(out + "Your name is: " + bold + name + unBold +". Is it right [Y/n]?\n"+in);
            confirm = scanner.nextLine();
        } while(confirm.equals("n"));
        nickname = name;
        return name;
    }
    public synchronized ChatMessage askChatMessage() {
        String chatMessage, response = "Y";
        ChatMessage message;
        chatMessage = scanner.nextLine();
        if(onlinePlayers.size()>1){
            System.out.print(out + "Is this a public synchronized message? [Y/n]\n" + in);
            response  = scanner.nextLine();
        }
        if(response.equals("n") || response.equals("N")){
            System.out.print(out + "Who is the receiver?\n" + in);
            response = scanner.nextLine();
            while(!onlinePlayers.contains(response)){
                System.out.print(out + "The sender needs to be online and in this lobby\n" + in);
                response = scanner.nextLine();
            }
            message = new PrivateChatMessage(response);
            message.setContent(chatMessage);
        }
        else{
            message = new ChatMessage();
            message.setContent(chatMessage);
        }
        return message;
    }
    public synchronized String askLobbyName() {
        String name;
        String confirm;
        do{
            System.out.print(out + "Insert lobby name\n" + in);
            name = scanner.nextLine();
            System.out.print(out + "The name is: " + bold + name + unBold +". Is it right [Y/n]?\n"+in);
            confirm = scanner.nextLine();
        } while(confirm.equals("n") || confirm.equals("N"));
        return name;
    }
    public synchronized int askNumPlayers() {
        int numPlayers;
        boolean flag;
        try{
            numPlayers = Integer.parseInt(scanner.nextLine());
            flag = true;
        } catch (NumberFormatException e){
            flag = false;
            numPlayers = 0;
        }
        while(!flag || (numPlayers < 2 || numPlayers > 4)){
            System.out.print(rst + err + "Invalid player number. Player number needs to be between 2 and 4\n" + in);
            try{
                numPlayers = Integer.parseInt(scanner.nextLine());
                flag = true;
            }catch (NumberFormatException e){
                flag = false;
            }
        }
        return numPlayers;
    }
    public synchronized String[] boardConstructor (TilesType[][] matrix) {
        String[] board = new String[20];
        board[0] = "\u001B[38;5;" + 246 +"m        0     1     2     3     4     5     6     7     8   ";

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
                board[h] += "\u001B[38;5;" + 11 +"m-----+";
            }
            h++;
        }
        return board;
    }
    public synchronized String[] shelfConstructor(TilesType[][] matrix, String player, int goal1, int goal2) {
        String[] shelf = new String[18]; //length = 31 + space
        shelf[0] = rst;
        int l;
        int delta = 0;
        shelf[1] =rst + "    " + bold + player + ": " + unBold + rst;
        if(player.equals(nickname)){
            shelf[1] += red + bold + "< you" + rst;
            delta = 5;
        }
        if (player.equals(curPlayer)) {
            shelf[1] += yellow + bold + "< cur" + rst;
            delta+=5;
        }
        l = player.length() + delta;
        if(l < 37) l = 37;
        for(int i = 0; i < l; i++) {
            shelf[0] += " ";
        }

        for(int i = (player.length() + delta + 6); i < l; i++) {
            shelf[1] += " ";
        }
        shelf[2] = rst + "      common goals 1: " + whiteBack + red + bold + " " + commonGoals.get(player)[0].toString() + " "
                + unBold + rst + "  2: " + whiteBack + red + bold + " " + commonGoals.get(player)[1].toString() + " " + rst;
        for(int i = 33; i < l; i++){
            shelf[2] += " ";
        }

        shelf[3] = rst + "      shelf:";
        for(int i = 12; i < l; i++){
            shelf[3] += " ";
        }
        shelf[4] = rst + yellow + "      +";
        for (int j = 0; j < 5; j++) {
            shelf[4] += "-----+";
        }
        int h = 5;
        for (int i = 0; i < 6; i ++) {
            shelf[h] = rst + "    " + i + yellow + " |";
            for (int j = 0; j < 5; j++) {
                //printTile(shelf[i][j]);
                shelf[h] += getTile(matrix[i][j]);
                shelf[h] += rst + yellow + "|";
            }
            h++;
            shelf[h] = "      +";
            for (int j = 0; j < 5; j++) {
                shelf[h] +=  "-----+";
            }
            h++;
        }
        shelf[17] = rst + "         0     1     2     3     4   ";
        return shelf;
    }
    public synchronized String[] concatStringArrays(String[] left, String[] right){
        //refactor matrices to get perfects rectangles
        int maxh;
        maxh = left.length;
        if(right.length > maxh) maxh = right.length;
        String[] leftCp = new String[maxh];
        String[] rightCp = new String[maxh];
        String[] matrix = new String[maxh];
        for(int i = 0; i < maxh; i++){
            if(i < left.length) {
                leftCp[i] = left[i];
            }
            else{
                leftCp[i] = "";
                for(int j=0; j<left[0].length(); j++){
                    leftCp[i] = leftCp[i] + " ";
                }
            }
            if(i < right.length){
                rightCp[i] = right[i];
            }
            else{
                rightCp[i] = "";
                for(int j=0; j<right[0].length(); j++){
                    rightCp[i] = rightCp[i] + " ";
                }
            }
            matrix[i] = leftCp[i] + rightCp[i];
        }
        return matrix;
    }
    public synchronized String convertStringArray(String[] in){
        String s = "";
        for (String value : in) {
            s += value + "\n";
        }
        return s;
    }
    public synchronized String[] personalGoalConstructor(PersonalGoal personalGoal){
        String[] shelf = new String[17]; //length = 31 + 7 space
        TilesType[][] matrix = personalGoal.getMatrix();
        int length = 55;
        shelf[0] = rst;
        for(int i = 0; i < length; i++) {
            shelf[0] += " ";
        }

        shelf[1] =rst + bold + "   your personal goal: ";
        String temp = personalPoints.toString();
        shelf[1] += temp;
        for(int i = temp.length() + 23; i < length; i++){
            shelf[1]+=" ";
        }
        shelf[1] += unBold;
        shelf[2] = rst + "    points: ";
        for (int j = 12; j < length; j++) {
            shelf[2] += " ";
        }
        shelf[3] = rst + yellow + "       +";
        for (int j = 0; j < 5; j++) {
            shelf[3] += "-----+";
        }
        shelf[3] += "                 ";
        int h = 4;
        for (int i = 0; i < 6; i ++) {
            shelf[h] = rst + "     " + i + yellow + " |";
            for (int j = 0; j < 5; j++) {
                shelf[h] += getTile(matrix[i][j]);
                shelf[h] += rst + yellow + "|";
            }
            for(int j = 38; j < length; j++) shelf[h]+=" ";
            h++;
            shelf[h] = "       +";
            for (int j = 0; j < 5; j++) {
                shelf[h] +=  "-----+";
            }
            for(int j = 38; j < length; j++) shelf[h]+=" ";
            h++;
        }
        shelf[16] = rst + "          0     1     2     3     4                    ";
        //shelf[17] = rst + "common goals: 1 -> "+commonGoal1+"   2 -> "+commonGoal2;
        return shelf;
    }
    public synchronized String[] commonGoalConstructor(String commonGoal, int pos){
        String[] goal = new String[16];;
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
                        if (j%2 == 0) goal[h] += rst + whiteBack + bold + red + "  ~  " + rst;
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
                        if (j == 1 || j == 3) goal[h] += rst + whiteBack + bold + red + "  "+ (char)8800 +"  " + rst;
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
                            //if (i == 2) goal[h] += rst + "\u001B[48;5;187m" + bold + red + "     " + rst;
                            //if (i == 4) goal[h] += rst + "\u001B[48;5;141m" + bold + red + "     " + rst;
                            //if (i == 5) goal[h] += rst + "\u001B[48;5;110m" + bold + red + "     " + rst;
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
                        if (i == 0 || i == 5) goal[h] += rst + whiteBack + bold + red + "  "+ (char)8800 +"  " + rst;
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

    //the next methods are added to not create errors but will probably be deleted

    @Override
    public synchronized void receiveCreatedLobbyMsg(CreatedLobbyMessage msg) {
        restoreWindow();
        displayCreatedLobbyMsg(msg);
        System.out.print(in);
    }

    @Override
    public synchronized void receiveJoinedMsg(JoinedMessage msg) {
        restoreWindow();
        System.out.print(out + "You have joined " + msg.getLobbyName() + " as " + msg.getName() +"\n" +in);
    }

    @Override
    public synchronized void receiveExistingLobbyMsg(ExistingLobbyMessage msg) {
        restoreWindow();
        System.out.print(err + "A lobby with the selected name already exists! \n You can join it with the command /join\n"+in);
    }

    @Override
    public synchronized void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg) {
        restoreWindow();
        System.out.print(err + "An error occoured, the lobby was not created\n" + in);
    }

    @Override
    public synchronized void receiveNameTakenMsg(NameTakenMessage msg) {
        restoreWindow();
        System.out.print(err + "The selected name is already taken by another player. You can retry to join with another one\n" + in);
    }

    @Override
    public synchronized void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg) {
        restoreWindow();
        System.out.print(err + "No existing lobby matches the selected name! You can get the names of existing lobbies with the command /retrieve\n" + in);
    }

    @Override
    public synchronized void receiveFullLobbyMsg(FullLobbyMessage msg) {
        restoreWindow();
        System.out.print(err + "The selected lobby is full. To see all available lobbies you can use /retrieve\n" + in);
    }

    @Override
    public synchronized void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg) {
        restoreWindow();
        displayLobbies(msg.getLobbies());
        System.out.print(in);
    }

    @Override
    public synchronized void receiveChatUpdateMsg(ChatUpdateMessage msg) {
        if(msg.getSender().equals(nickname)) {
            System.out.print(out + bold + "You just write" + unBold + ":  " + msg.getContent() + "\n" + in);
        }
        else {
            System.out.print("\n" + out + bold + msg.getSender() + " just write" + unBold + ":  " + msg.getContent() + "\n" + in);
        }
        try{
            sleep(600);
        }catch (InterruptedException e){
            throw new RuntimeException("interrupt during sleep!");
        }
        messages.add(msg);
    }

    public synchronized void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg) {
        if(msg.getReceiver().equals(nickname)){
            System.out.print("\n" + out + bold + msg.getSender() + " just write" + unBold + ":  " + msg.getContent() + red + bold + " [private]\n" + in);
            try{
                sleep(600);
            }catch (InterruptedException e){
                throw new RuntimeException("interrupt during sleep!");
            }
            messages.add(msg);
        }
        if(msg.getSender().equals(nickname)) {
            messages.add(msg);
            System.out.print(out + "You have sent a private message to: " + bold + msg.getReceiver() + "\n" + in);
        }
    }

    @Override
    public synchronized void receiveGameCreatedMsg(GameCreatedMessage msg) {
        updateView(msg.getGameInfo());
    }

    @Override
    public synchronized void receiveGameUpdatedMsg(GameUpdatedMessage msg) {
        updateView(msg.getGameInfo());
    }

    @Override
    public synchronized void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg) {
        curPlayer  = msg.getUpdatedPlayer();
        displayGameInfo();
        System.out.print(in);
    }

    @Override
    public synchronized void receiveInvalidMoveMsg(InvalidMoveMessage msg) {
        System.out.print(err + "The selected move is not legal! Retry with a different tiles sequence\n" + in);
    }

    @Override
    public synchronized void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg) {
        restoreWindow();
        System.out.print(err + "Not enough player to continue the game. If no one reconnects the game will end soon\n" + in);
    }

    @Override
    public synchronized void receiveLobbyClosedMsg(LobbyClosedMessage msg) {
        QuitMessage clientMessage = new QuitMessage();
        synchronized (this) {
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
        }
        client.sendMsgToServer(clientMessage);
        if (client instanceof RMIClient) {
            client = new RMIClient("localhost", 1099, this);
        } else if (client instanceof SocketClient) {
            client = new SocketClient("localhost", 8088, this);
        }
        client.init();
        restoreWindow();
        displayGameInfo();
        System.out.print(in);
    }

    @Override
    public synchronized void receiveUserDisconnectedMsg(UserDisconnectedMessage msg) {
        System.out.print(err + "Player " + msg.getUser() + "has been disconnected\n" + in);
    }

    @Override
    public synchronized void receiveInvalidCommandMsg(InvalidCommandMessage msg) {
        System.out.print(err + "Selected command does not exists! To get available commands you type /help\n" + in);
    }

    @Override
    public synchronized void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {
        System.out.print(err + "An unexpected connectivity error occured. You have been disconnected\n" + in);
    }
}
