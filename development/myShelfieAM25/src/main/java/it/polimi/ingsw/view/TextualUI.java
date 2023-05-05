package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.messages.clientMessages.*;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.CreatedLobbyMessage;
import it.polimi.ingsw.network.messages.serverMessages.JoinedMessage;

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
    private Map<String, TilesType[][]> shelves = new HashMap<>();
    private Map<String, Integer[]> commonGoals = new HashMap<>();
    private String commonGoal1;
    private String commonGoal2;
    private String nickname;
    private PersonalGoal personalGoal;
    private final List<ChatUpdateMessage> messages = new ArrayList<>();
    private static final List<String> commands = List.of("/create", "/join", "/retrive","/chat","/showchat", "/help", "/move", "/quit");
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
        //System.out.print("\u001B[0m");
        System.out.println(out + "m Insert \u001B[1m/rmi\u001B[2m if you want to join server with rmi \u001B[1m/socket\u001B[2m if you want to access it with socket");
        System.out.print(in);

        String connType = scanner.nextLine();
        while(!connType.equals("/rmi") && !connType.equals("/socket")){
            System.out.print(rst + err + "This type of connection is not supported\n" + in);
            connType = scanner.nextLine();
        }
        if (connType.equals("/rmi")) {
            client = new RMIClient("localhost", 1099, this);
            client.init();
        }
        else{
            client = new SocketClient("localhost", 8088, this);
            boolean socketError = true;
            while (socketError){
                client.init();
                socketError = false;
            }
        }
        String inputCommand = "";
        restoreWindow();

        printCommands();
        System.out.print(in);

        while (true) {
            inputCommand = askCommand();
            switch (inputCommand) {
                case "/create":
                    if (client.getIsInLobbyStatus()) {
                        System.out.print(out + "You are already in a lobby\n");
                    } else {
                        String name = askName();
                        String lobbyName = askLobbyName();
                        System.out.print(out + "Insert number of players\n" + in);
                        int numPlayers = askNumPlayers();
                        CreateLobbyMessage clientMessageC = new CreateLobbyMessage();
                        clientMessageC.setLobbyName(lobbyName);
                        clientMessageC.setLobbyCreator(name);
                        clientMessageC.setPlayerNumber(numPlayers);
                        if (client instanceof RMIClient) {
                            clientMessageC.setRmiClient((RMIClient) client);
                        }
                        client.sendMsgToServer(clientMessageC);
                    }
                    break;
                case "/retrive":
                    RetrieveLobbiesMessage clientMessageR = new RetrieveLobbiesMessage();
                    if (client instanceof RMIClient) {
                        clientMessageR.setRmiClient((RMIClient) client);
                    }
                    client.sendMsgToServer(clientMessageR);
                    break;
                case "/join":
                    if (client.getIsInLobbyStatus()) {
                        System.out.print(rst + err + "already in a lobby!\n");
                    } else {
                        String name = askName();
                        String lobbyName = askLobbyName();
                        JoinMessage clientMessageJ = new JoinMessage();
                        clientMessageJ.setName(name);
                        clientMessageJ.setLobbyName(lobbyName);
                        if (client instanceof RMIClient) {
                            clientMessageJ.setRmiClient((RMIClient) client);
                        }
                        client.sendMsgToServer(clientMessageJ);
                    }
                    break;
                case "/chat":
                    if (client.getIsInLobbyStatus()) {
                        System.out.print(out + "You have entered the chat. Write a message\n" + in);
                        String chatMessage = askChatMessage();

                        ChatMessage clientMessageCh = new ChatMessage();
                        clientMessageCh.setContent(chatMessage);
                        if (client instanceof RMIClient) {
                            clientMessageCh.setRmiClient((RMIClient) client);
                        }
                        client.sendMsgToServer(clientMessageCh);

                    } else {
                        System.out.print(rst + err +"You have to be inside a lobby to use the chat\n");
                    }
                    break;
                case "/showchat":
                    if (client.getIsInLobbyStatus()) {
                        displayChat();
                    }
                    else {
                        System.out.print(rst + err +"You have to be inside a lobby to use the chat\n");
                    }
                    break;
                case "/move":
                    if (client.getIsInLobbyStatus()) {
                        List<Tile> tiles = new ArrayList<>();
                        System.out.println(out + "Choose up to 3 tiles from the board to insert in your bookshelf");
                        System.out.print(out + "For each tile use the format x,y (x is the horizontal axis, y is the vertical axis) then press enter\n");
                        System.out.print(out + "To end the sequenze press enter twice\n"+ in);
                        getMoves(tiles);
                        System.out.print(out + "Choose the column of the bookshelf where you want to place the tiles\n" + in);
                        int column;
                        boolean flag = false;
                        while(!flag){
                            try {
                                column = Integer.parseInt(scanner.nextLine());
                                if(column >= 0 && column < 5){
                                    flag = true;
                                    MoveMessage clientMessageM = new MoveMessage();
                                    clientMessageM.setTiles(tiles);
                                    clientMessageM.setColumn(column);
                                    if (client instanceof RMIClient) {
                                        clientMessageM.setRmiClient((RMIClient) client);
                                    }
                                    client.sendMsgToServer(clientMessageM);
                                }
                                else{
                                    System.out.print(rst + err + "Invalid Number, retry\n" + in);
                                }
                            }catch(NumberFormatException e){
                                System.out.print(rst + err + "Invalid Number, retry\n" + in);
                            }
                        }
                    }
                    else {
                        System.out.print(rst + err +"You have to be inside a game to make a move\n");
                    }
                    break;
                case "/quit":
                    if (client.getIsInLobbyStatus()) {
                        System.out.print(out + bold + "Are you sure? [y/N]\n" + in);
                        String quit = scanner.nextLine();
                        if (quit.equals("y") || quit.equals("Y")) {
                            QuitMessage clientMessageQ = new QuitMessage();
                            if (client instanceof RMIClient) {
                                clientMessageQ.setRmiClient((RMIClient) client);
                            }
                            client.sendMsgToServer(clientMessageQ);
                            if (client instanceof RMIClient) {
                                client = new RMIClient("localhost", 1099, this);
                            } else if (client instanceof SocketClient){
                                client = new SocketClient("localhost", 8088, this);
                            }
                            client.init();
                            System.out.print(out + " ");
                        }

                    }
                    else {
                        System.out.println(rst + err +"You have to be inside a lobby to use this feature");
                    }
                    break;
                case "/help":
                    printCommands();
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print(in);
        }

    }


    public void restoreWindow(){
        System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(yellow + "" +
                "███╗   ███╗██╗   ██╗        ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗\n"+
                "████╗ ████║╚██╗ ██╔╝        ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝\n"+
                "██╔████╔██║ ╚████╔╝         ███████╗███████║█████╗  ██║     █████╗  ██║█████╗\n"+
                "██║╚██╔╝██║  ╚██╔╝          ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝\n"+
                "██║ ╚═╝ ██║   ██║           ███████║██║  ██║███████╗███████╗██║     ██║███████╗\n"+
                "╚═╝     ╚═╝   ╚═╝           ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝\n\n");
    }
    public void getMoves(List<Tile> tiles){
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
    public void printCommands(){
        System.out.print("\u001B[0m");
        System.out.println("\u001B[1mList of commands:\u001B[2m");
        System.out.println(yellow + "/create:\u001B[0m create a lobby");
        System.out.println(yellow + "/join:\u001B[0m join an existing lobby");
        System.out.println(yellow + "/retrieve:\u001B[0m get a list of available lobbies");
        System.out.println(yellow + "/chat:\u001B[0m write a message in the chat");
        System.out.println(yellow + "/showchat:\u001B[0m show the chat history");
        System.out.println(yellow + "/move:\u001B[0m make a move");
        System.out.println(yellow + "/quit:\u001B[0m leave a lobby");
        System.out.print(yellow + "/help:\u001B[0m show this list of commands\n");

    }
    public void updateView (GameInfo info) {
        board = info.getNewBoard();
        if (info instanceof InitialGameInfo) {
            shelves = ((InitialGameInfo) info).getShelves();
            for(String player : info.getPlayers()){
                commonGoals.put(player, new Integer[]{0, 0});
            }
            personalGoal = ((InitialGameInfo) info).getPersonalGoals().get(nickname);
            commonGoal1 = ((InitialGameInfo) info).getCommonGoal1();
            commonGoal2 = ((InitialGameInfo) info).getCommonGoal2();
        } else {
            shelves.put(info.getCurrentPlayer(), info.getShelf());
            commonGoals.replace(info.getCurrentPlayer(), new Integer[]{info.getCommonGoal1Points(), info.getCommonGoal2Points()});
        }
    }

    public void displayLobbies (List<String> lobbies) {
        System.out.println(rst + bold + out + "Available lobbies:" + rst);
        for (String lobby : lobbies) {
            System.out.println(rst + "   " +lobby);
        }
    }

    public void displayCreatedLobbyMsg (CreatedLobbyMessage msg) {
        System.out.println(out + "A lobby has been created");
        System.out.println("   Lobby name: " + bold + msg.getLobbyName() + unBold);
        System.out.print("   Lobby creator: " + bold + msg.getName() + unBold + "\n");
    }

    public void displayChat () {
        for (ChatUpdateMessage message : messages) {
            System.out.println(out + bold + message.getSender() + " at " + message.getTimestamp() + unBold + ": \n" + message.getContent());
        }
    }

    public void displayShelf (TilesType[][] shelf) {
        System.out.print(rst + yellow + "+");
        for (int j = 0; j < 5; j++) {
            System.out.print("-----+");
        }
        System.out.println();

        for (int i = 0; i < 6; i++) {
            System.out.print(rst + i + yellow + "|");
            for (int j = 0; j < 5; j++) {
                printTile(shelf[i][j]);
                System.out.println(rst + yellow + "|");
            }
            System.out.print("+");
            for (int j = 0; j < 5; j++) {
                System.out.print("-----+");
            }
            System.out.println();
        }
        System.out.println(rst + "   0     1     2     3     4   ");
    }

    public void displayInLineShelf () {
        TilesType[][] shelf;

        for (String name : shelves.keySet()) {
            System.out.print(bold + name + "'s bookshelf:                  "+ unBold ); //31 + name
        }
        System.out.println();

        for (String name : shelves.keySet()) {
            System.out.print(rst + yellow + "+");
            for (int j = 0; j < 5; j++) {
                System.out.print("-----+");
            }
            //System.out.print("                               "); //31 spaces
            for(int i = 0; i<name.length(); i++){
                System.out.print(" ");
            }
        }
        System.out.println();

        for(int i=0; i<6; i++){
            for (String name : shelves.keySet()) {
                shelf = shelves.get(name);
                System.out.print(rst + yellow + "|");
                for (int j = 0; j < 5; j++) {
                    printTile(shelf[i][j]);
                    System.out.print(rst + yellow + "|");
                }
                for(int h = 0; h<name.length(); h++){
                    System.out.print(" ");
                }

            }
            System.out.println();
            for (String name : shelves.keySet()) {
                System.out.print(rst + yellow + "+");
                for (int j = 0; j < 5; j++) {
                    System.out.print("-----+");
                }
                for(int h = 0; h<name.length(); h++){
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        for(String name : shelves.keySet()){
            System.out.print(rst + "   0     1     2     3     4    ");
            for(int i = 0; i<name.length(); i++) System.out.print(" ");
        }
        System.out.println();

    }

    public void displayBoard (TilesType[][] matrix) {
        restoreWindow();
        System.out.println("\u001B[38;5;" + 246 +"m         0     1     2     3     4     5     6     7     8   " );

        System.out.print(rst + yellow + "       +");
        for (int j = 0; j < 9; j++) {
            System.out.print("-----+");
        }
        System.out.println();

        for (int i = 0; i < 9; i++) {
            System.out.print(yellow + "   " + "\u001B[38;5;" + 246 + "m  " + i + yellow + " |");
            for (int j = 0; j < 9; j++) {
                printTile(matrix[i][j]);
                System.out.print(rst + yellow + "|");
            }

            System.out.print("\n" + rst + yellow + "       +");
            for (int j = 0; j < 9; j++) {
                System.out.print("\u001B[38;5;" + 11 +"m-----+");
            }
            System.out.println();
        }
        System.out.print(rst);
    }

    public void displayInitialGameInfo () {
        //System.out.println(out + "The game has started \n" + in);
        System.out.println();
        displayGameInfo();
    }
    public void displayGameInfo () {
        restoreWindow();
        String[] output = boardConstructor(board);

        for (String player : shelves.keySet()) {
            output = concatStringArrays(output, shelfConstructor(shelves.get(player), player, commonGoals.get(player)[0], commonGoals.get(player)[0]));
        }
        String info = convertStringArray(output);

        System.out.print(info);

        output = personalGoalConstructor(personalGoal);
        info = convertStringArray(output);

        System.out.println(info);

    }
    /*
    public void displayGameInfo () {
        displayBoard(board);
        System.out.println();
        //for (String name : shelves.keySet()) {
            //System.out.println(bold + name + "'s bookshelf: "+ unBold);
            //System.out.println();
            //displayShelf(shelves.get(name));
        //}
        displayInLineShelf();
        System.out.println();
    }*/

    public Tile getTiles (String coords) {
        int x = Integer.parseInt(String.valueOf(coords.charAt(0)));
        int y = Integer.parseInt(String.valueOf(coords.charAt(2)));
        return new Tile(board[y][x], x, y);
    }

    public void printTile (TilesType type) {
        if(type==null) {
            System.out.print((char) 27 + "[48;5;" + BROWN + "m     ");
            System.out.print("\u001B[0m");
            return;
        }
        switch (type) {
            case TROPHIES -> {
                System.out.print((char) 27 + "[48;5;" + TROPHIESB + "m" +  (char) 27 + "[38;5;" + TROPHIESF + "m  T  ");
            }
            case FRAMES -> {
                System.out.print((char) 27 + "[48;5;" + FRAMESB + "m" +  (char) 27 + "[38;5;" + FRAMESF + "m  F  ");
            }
            case PLANTS -> {
                System.out.print((char) 27 + "[48;5;" + PLANTSB + "m"  +  (char) 27 + "[38;5;" + PLANTSF + "m  P  ");
            }
            case CATS -> {
                System.out.print((char) 27 + "[48;5;" + CATSB + "m" +  (char) 27 + "[38;5;" + CATSF + "m  C  ");
            }
            case GAMES -> {
                System.out.print((char) 27 + "[48;5;" + GAMEB + "m" +  (char) 27 + "[38;5;" + GAMEF + "m  G  ");
            }
            case BOOKS -> {
                System.out.print((char) 27 + "[48;5;" + BOOKSB + "m" +  (char) 27 + "[38;5;" + BOOKSF + "m  B  ");
            }
            default -> {
                System.out.print((char) 27 + "[48;5;" + BROWN + "   ");
            }
        }
        System.out.print("\u001B[0m");
    }

    public String getTile (TilesType type) {
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

    public void addMessage (ChatUpdateMessage msg) {
        messages.add(msg);
    }

    public void displayJoinedMsg (JoinedMessage msg) {
        System.out.print(out + "You joined " + msg.getLobbyName() + "\n" + in);
    }

    public void displayServerMsg (String string) {
        System.out.println(rst + err + string);
        System.out.print(in);
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

    public String askName() {
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

    public String askChatMessage() {
        String chatMessage;
        chatMessage = scanner.nextLine();
        return chatMessage;
    }

    public String askLobbyName() {
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

    public int askNumPlayers() {
        int numPlayers;

        numPlayers = Integer.parseInt(scanner.nextLine());
        while(numPlayers < 2 || numPlayers > 4){
            System.out.print(rst + err + "Invalid player number. Player number needs to be between 2 and 4\n" + in);
            numPlayers = Integer.parseInt(scanner.nextLine());
        }
        return numPlayers;
    }

    public String[] boardConstructor (TilesType[][] matrix) {
        String[] board = new String[21];
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
        board[20] = rst;
        for(int i = 0; i < 61; i++) board[20] += " ";
        return board;
    }
    public String[] shelfConstructor(TilesType[][] matrix, String player, int goal1, int goal2) {
        String[] shelf = new String[19]; //length = 31 + space
        shelf[0] = rst;
        int l;
        l = player.length();
        if(l < 37) l = 37;
        for(int i = 0; i < l; i++) {
            shelf[0] += " ";
        }
        shelf[1] =rst + "    " + bold + player + ":" + unBold;
        for(int i = player.length() + 5; i < l; i++) {
            shelf[1] += " ";
        }
        shelf[2] = rst + "      common goal 1:  " + whiteBack + red + bold + " " + commonGoals.get(player)[0].toString() + " " + unBold + rst;
        for(int i = 25; i < l; i++){
            shelf[2] += " ";
        }
        shelf[3] = rst + "      common goal 2:  " + whiteBack + red + bold + " " + commonGoals.get(player)[1].toString() + " " + unBold + rst;
        for(int i = 25; i < l; i++){
            shelf[3] += " ";
        }
        shelf[4] = rst + "      shelf:";
        for(int i = 12; i < l; i++){
            shelf[4] += " ";
        }
        shelf[5] = rst + yellow + "      +";
        for (int j = 0; j < 5; j++) {
            shelf[5] += "-----+";
        }
        int h = 6;
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
        shelf[18] = rst + "         0     1     2     3     4   ";
        return shelf;
    }
    public String[] concatStringArrays(String[] left, String[] right){
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
    public String convertStringArray(String[] in){
        String s = "";
        for (String value : in) {
            s += value + "\n";
        }
        return s;
    }
    public String[] personalGoalConstructor(PersonalGoal personalGoal){
        String[] shelf = new String[17]; //length = 31 + 7 space
        TilesType[][] matrix = personalGoal.getMatrix();

        shelf[0] = rst;
        for(int i = 0; i < 38; i++) {
            shelf[0] += " ";
        }
        shelf[1] =rst + "   your personal goal:                ";

        shelf[2] = rst + yellow + "       +";
        for (int j = 0; j < 5; j++) {
            shelf[2] += "-----+";
        }
        int h = 3;
        for (int i = 0; i < 6; i ++) {
            shelf[h] = rst + "     " + i + yellow + " |";
            for (int j = 0; j < 5; j++) {
                shelf[h] += getTile(matrix[i][j]);
                shelf[h] += rst + yellow + "|";
            }
            h++;
            shelf[h] = "       +";
            for (int j = 0; j < 5; j++) {
                shelf[h] +=  "-----+";
            }
            h++;
        }
        shelf[15] = rst + "          0     1     2     3     4   ";
        shelf[16] = rst + "common goals: 1 -> "+commonGoal1+"   2 -> "+commonGoal2;
        return shelf;
    }
    public String[] commonGoalConstructor(String commmonGoal){
        String[] goal;
        int length = 55;
        switch (commmonGoal){
            case "ColumnsGoal (isRegular: false)":
                goal = new String[20];
                goal[0] = "";
                for(int i = 0; i < length; i++) goal[0] += " ";
                goal[1] = rst + bold + "Common goal 1:";
                for(int i = 14; i < length; i++){
                    goal[1] += " ";
                }
                goal[2] = rst + "  3 column with at most 3 different types of tile";
                for(int i = 49; i < length; i++){
                    goal[2] += " ";
                }
                int h = 3;
                for(int y = 0; y < 6; y++){
                    //goal[h] =
                }
                break;
            case "ColumnsGoal (isRegular: true)":
                break;
            case "CornerGoal":
                break;
            case "EightEqualsGoal":
                break;
            case "FullDiagonalGoal":
                break;
            case "RowsGoal (isRegular: true)":
                break;
            case "RowsGoal (isRegular: false)":
                break;
            case "TriangularMatrixGoal":
                break;
            case "TwoEqualSquareGoal":
                break;
            case "XGoal":
                break;
            case "EqualGroupsGoal (4 groups, 4 in size)":
                break;
            case "EqualGroupsGoal (6 groups, 2 in size)":
                break;
            default:


        }
        return goal = new String[5];
    }
}
