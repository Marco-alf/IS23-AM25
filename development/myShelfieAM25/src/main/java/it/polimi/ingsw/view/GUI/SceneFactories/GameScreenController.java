package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.messages.clientMessages.ChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.MoveMessage;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameScreenController {
    GenericClient client;
    String selfName;

    @FXML
    GridPane viewBoard;

    @FXML
    BorderPane mainpanel;
    @FXML
    GridPane player1shelf;

    @FXML
    Button player1Button, player2Button, player3Button, myshelfButton;

    @FXML
    BorderPane player1, player2, player3;
    @FXML
    VBox myshelf;

    @FXML
    ImageView goal1, goal2;

    List<String> players = new ArrayList<>();
    List<String> onlinePlayers = new ArrayList<>();


    private final ImageView [][] shelf0 = new ImageView[5][6];
    private final ImageView [][] shelf1 = new ImageView[5][6];
    private final ImageView [][] shelf2 = new ImageView[5][6];
    private final ImageView [][] shelf3 = new ImageView[5][6];

    private final ImageView[][] imageViewBoard = new ImageView[9][9];
    private TilesType [][] livingroomBoard  = new TilesType[5][6];

    private final List<Tile> selected = new ArrayList<>();
    private final ImageView[] viewSelected = new ImageView[3];
    @FXML
    GridPane selectedList;

    private final ImageView[] arrowsView = new ImageView[5];
    @FXML
    GridPane arrows;

    @FXML
    ListView<String> chatList;
    ArrayList<String> chat = new ArrayList<>();
    @FXML
    TextField chatField;

    private final Image cats = new Image("17_MyShelfie_BGA/item_tiles/Gatti1.1.png");
    private final Image trophies = new Image("17_MyShelfie_BGA/item_tiles/Trofei1.1.png");
    private final Image frames = new Image("17_MyShelfie_BGA/item_tiles/Cornici1.1.png");
    private final Image games = new Image("17_MyShelfie_BGA/item_tiles/Giochi1.1.png");
    private final Image books = new Image("17_MyShelfie_BGA/item_tiles/Libri1.1.png");
    private final Image plants = new Image("17_MyShelfie_BGA/item_tiles/Piante1.1.png");


    private Button[] s_buttons = new Button[]{myshelfButton, player1Button, player2Button, player3Button };

    public void updateInitialGameInfo(InitialGameInfo info) {
        livingroomBoard = info.getNewBoard();
        refreshBoard();
        players = new ArrayList<>(info.getPlayers());
        onlinePlayers = new ArrayList<>(info.getOnlinePlayers());


        int i=1;
        int k=0;
        for(; i<players.size(); i++, k++){
            if(players.get(k).equals(selfName)) {
                k++;
            }
            s_buttons[i].setText(players.get(k));
        }
        myshelfButton.setText(selfName);

        for( ; i<4; i++){
            s_buttons[i].setVisible(false);
        }

        goal1.setImage( new Image("17_MyShelfie_BGA/common_goal_cards/"+commongoaltranslator(info.getCommonGoal1())+".jpg"));
        goal2.setImage( new Image("17_MyShelfie_BGA/common_goal_cards/"+commongoaltranslator(info.getCommonGoal2())+".jpg"));
    }
    private int commongoaltranslator(String goal){
        int result = 1;
        switch (goal){
            case "ColumnsGoal (isRegular: true)" -> result = 5;
            case "ColumnsGoal (isRegular: false)" -> result = 2;
            case "CornerGoal" -> result = 8;
            case "EightEqualsGoal" -> result = 9;
            case "FullDiagonalGoal" -> result = 11;
            case "RowsGoal (isRegular: true)" -> result = 7;
            case "RowsGoal (isRegular: false)" -> result = 6;
            case "TriangularMatrixGoal" -> result = 12;
            case "TwoEqualSquareGoal" -> result = 1;
            case "XGoal" -> result = 10;
            case "EqualGroupsGoal (4 groups, 4 in size)" -> result = 3;
            case "EqualGroupsGoal (6 groups, 2 in size)" -> result = 4;
            default -> result = 1;
        }
        return result;
    }

    public void initActions(GenericClient client, String selfName){
        this.client = client;
        this.selfName = selfName;

        mainpanel.setVisible(true);
        myshelf.setVisible(false);
        player1.setVisible(false);
        player2.setVisible(false);
        player3.setVisible(false);

        for(int i=0;  i<5; i++){
            for(int j=0;  j<6; j++){
                shelf1[i][j] = new ImageView();
                shelf1[i][j].setPreserveRatio(true);
                shelf1[i][j].setFitWidth(95);
                shelf1[i][j].setImage(cats);
                player1shelf.add(shelf1[i][j], i, j);
            }
        }

        for(int i=0;  i<9; i++){
            for(int j=0;  j<9; j++){
                imageViewBoard[i][j] = new ImageView();
                imageViewBoard[i][j].setPreserveRatio(true);
                imageViewBoard[i][j].setFitHeight(95);
                int finalI = i;
                int finalJ = j;
                imageViewBoard[i][j].setOnMouseClicked(mouseEvent -> {
                    select(finalJ, finalI);
                });
                viewBoard.setAlignment(Pos.CENTER);
                viewBoard.add(imageViewBoard[i][j], i, j);
            }
        }

        player1Button.setOnAction(actionEvent -> toggleBoard(1));
        player2Button.setOnAction(actionEvent -> toggleBoard(2));
        player3Button.setOnAction(actionEvent -> toggleBoard(3));
        myshelfButton.setOnAction(actionEvent -> toggleBoard(0));

        for(int i=0; i<viewSelected.length; i++){
            viewSelected[i] = new ImageView();
            viewSelected[i].setPreserveRatio(true);
            viewSelected[i].setFitHeight(95);
            int finalI = i;
            viewSelected[i].setOnMouseClicked(mouseEvent -> {deselect(finalI);});
            selectedList.add(viewSelected[i], 0, 2-i);
        }

        for(int i=0; i<5; i++){
            arrowsView[i] = new ImageView("17_MyShelfie_BGA/misc/arrow.jpg");
            arrowsView[i].setPreserveRatio(true);
            arrowsView[i].setFitHeight(95);
            int finalI = i;
            arrowsView[i].setOnMouseClicked(mouseEvent -> {tryMove(finalI);});
            arrows.add(arrowsView[i], i, 0);
        }

        s_buttons = new  Button[]{myshelfButton, player1Button, player2Button, player3Button };

        chatField.setOnAction(actionEvent -> {sendChat();});
    }

    private void tryMove(int i) {
        synchronized (selected){
            MoveMessage clientMessage = new MoveMessage();
            clientMessage.setTiles(selected);
            clientMessage.setColumn(i);

            if (client instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) client);
            }
            client.sendMsgToServer(clientMessage);
        }
    }

    private void refreshBoard(){
        for(int i=0;  i<9; i++){
            for(int j=0;  j<9; j++){
                if(livingroomBoard[i][j]!= null) {
                    imageViewBoard[j][i].setImage(getTexture(livingroomBoard[i][j]));
                }
            }
        }
    }
    private void select(int i, int j){
        synchronized (selected) {
            TilesType t = livingroomBoard[i][j];
            if (t == null) {
                return;
            }
            for (Tile tile : selected) {
                if (tile.getPosY() == i && tile.getPosX() == j) {
                    selected.remove(tile);
                    imageViewBoard[j][i].setFitHeight(95);
                    updateSelectedViews();
                    return;
                }
            }
            if (selected.size() >= 3) {
                return;
            }
            Tile toadd = new Tile(t, j, i);
            imageViewBoard[j][i].setFitHeight(105);
            selected.add(toadd);
            updateSelectedViews();
        }
    }
    private void deselect(int i){
        synchronized (selected) {
            if (selected.size() < 1 || i < 0 || i > 3) {
                return;
            }
            Tile t = selected.remove(i);
            imageViewBoard[t.getPosX()][t.getPosY()].setFitHeight(95);
            updateSelectedViews();
        }
    }
    private void updateSelectedViews(){
        synchronized (selected) {
            int i = 0;
            for (; i < selected.size(); i++) {
                viewSelected[i].setImage(getTexture(selected.get(i).getType()));
            }
            for (; i < 3; i++) {
                viewSelected[i].setImage(null);
            }
        }
    }

    private Image getTexture(TilesType type){
        Image result;
        if(type == null){
            return null;
        }
        switch (type){
            case TROPHIES -> result = trophies;
            case GAMES -> result = games;
            case BOOKS -> result = books;
            case PLANTS -> result = plants;
            case FRAMES -> result = frames;
            case CATS -> result = cats;
            default -> result = null;
        }
        return result;
    }

    public void toggleBoard(int i){
        myshelf.setVisible(i == 0 && !myshelf.isVisible());
        player1.setVisible(i == 1 && !player1.isVisible());
        player2.setVisible(i == 2 && !player2.isVisible());
        player3.setVisible(i == 3 && !player3.isVisible());
    }

    public synchronized void updateChat(ChatUpdateMessage msg) {
        String s = " : ";
        String message = msg.getTimestamp()+s+msg.getSender()+s+msg.getContent();
        chatList.getItems().add(message);
        chatList.scrollTo(chatList.getItems().size());
    }
    private synchronized void sendChat(){
        String content = chatField.getText();
        if(content == null || content.isBlank()){
            return;
        }

        ChatMessage clientMessage = new ChatMessage();
        clientMessage.setSender(selfName);
        clientMessage.setContent(content);

        if (client instanceof RMIClient) {
            clientMessage.setRmiClient((RMIClient) client);
        }
        client.sendMsgToServer(clientMessage);
    }
}
