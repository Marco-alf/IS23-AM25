package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.messages.clientMessages.ChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.MoveMessage;
import it.polimi.ingsw.network.messages.clientMessages.PrivateChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.QuitMessage;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.GameUpdatedMessage;
import it.polimi.ingsw.network.messages.serverMessages.PrivateChatUpdateMessage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.paint.Color.BURLYWOOD;

public class GameScreenController {
    GameScreen gameScreen;

    private final AtomicBoolean myTurn = new AtomicBoolean(false);
    private String currentPlayer;
    @FXML
    ImageView armchair;

    GenericClient client;
    String selfName;

    @FXML
    GridPane viewBoard;
    @FXML
    BorderPane mainpanel;
    @FXML
    GridPane realShelf, player1shelf, player2shelf, player3shelf;

    @FXML
    Button player1Button, player2Button, player3Button, myshelfButton;

    @FXML
    BorderPane player1, player2, player3;
    @FXML
    VBox myshelf;

    @FXML
    ImageView goal1, goal2;

    List<String> players = new ArrayList<>();
    List<String> otherPlayers = new ArrayList<>();
    List<String> onlinePlayers = new ArrayList<>();


    private final ImageView [][] shelf0 = new ImageView[5][6];
    private final ImageView [][] shelf1 = new ImageView[5][6];
    private final ImageView [][] shelf2 = new ImageView[5][6];
    private final ImageView [][] shelf3 = new ImageView[5][6];

    private final ImageView[][] imageViewBoard = new ImageView[9][9];
    private TilesType [][] livingroomBoard  = new TilesType[9][9];

    private final List<Tile> selected = new ArrayList<>();
    private final ImageView[] viewSelected = new ImageView[3];
    @FXML
    GridPane selectedList;

    private final ImageView[] arrowsView = new ImageView[5];
    @FXML
    GridPane arrows;

    @FXML
    ListView<String>  chatList;
    ArrayList<String> chat = new ArrayList<>();
    @FXML
    TextField chatField;
    @FXML
    ComboBox<String> chatMode;

    @FXML
    ImageView personalGoalView;

    private final Image cats = new Image("17_MyShelfie_BGA/item_tiles/Gatti1.1.png");
    private final Image trophies = new Image("17_MyShelfie_BGA/item_tiles/Trofei1.1.png");
    private final Image frames = new Image("17_MyShelfie_BGA/item_tiles/Cornici1.1.png");
    private final Image games = new Image("17_MyShelfie_BGA/item_tiles/Giochi1.1.png");
    private final Image books = new Image("17_MyShelfie_BGA/item_tiles/Libri1.1.png");
    private final Image plants = new Image("17_MyShelfie_BGA/item_tiles/Piante1.1.png");


    private Button[] s_buttons = new Button[]{myshelfButton, player1Button, player2Button, player3Button };
    Background buttonback = new Background(new BackgroundFill(BURLYWOOD,new CornerRadii(0), new Insets(0)));
    Background disconnButtonback = new Background(new BackgroundFill(BURLYWOOD,new CornerRadii(0), new Insets(0)));


    public void updateInitialGameInfo(InitialGameInfo info) {
        synchronized (myTurn) {

            livingroomBoard = info.getNewBoard();
            refreshBoard();
            players = new ArrayList<>(info.getPlayers());
            otherPlayers = new ArrayList<>(players);
            otherPlayers.remove(selfName);

            recolorPlayers(onlinePlayers, info.getOnlinePlayers(), otherPlayers);
            onlinePlayers = new ArrayList<>(info.getOnlinePlayers());

            chatMode.getItems().add("Lobby");
            int i = 1;
            int k = 0;
            for (; i < players.size(); i++, k++) {
                if (players.get(k).equals(selfName)) {
                    k++;
                }
                s_buttons[i].setText(players.get(k));
                chatMode.getItems().add(players.get(k));
            }
            myshelfButton.setText("(Me) " + selfName);

            for (; i < 4; i++) {
                s_buttons[i].setVisible(false);
            }

            for (i = 0; i < 5; i++) {
                for (int j = 0; j < 6; j++) {
                    shelf0[i][j].setImage(getTexture(info.getShelves().get(selfName)[j][i]));
                    shelf1[i][j].setImage(getTexture(info.getShelves().get(players.get(1))[j][i]));
                    if (players.size() > 2)
                        shelf2[i][j].setImage(getTexture(info.getShelves().get(players.get(2))[j][i]));
                    if (players.size() > 3)
                        shelf3[i][j].setImage(getTexture(info.getShelves().get(players.get(3))[j][i]));
                }
            }


            personalGoalView.setImage(new Image("17_MyShelfie_BGA/personal_goal_cards/Personal_Goals" + (info.getPersonalGoals().get(selfName).ordinal() + 1) + ".png"));
            goal1.setImage(new Image("17_MyShelfie_BGA/common_goal_cards/" + commongoaltranslator(info.getCommonGoal1()) + ".jpg"));
            goal2.setImage(new Image("17_MyShelfie_BGA/common_goal_cards/" + commongoaltranslator(info.getCommonGoal2()) + ".jpg"));

        }
        updateCurrentPlayer(info.getCurrentPlayer());
    }

    public void updateGame(GameInfo info) {
        synchronized (myTurn) {
            String oldplayer = currentPlayer;
            livingroomBoard = info.getNewBoard();
            refreshBoard();

            recolorPlayers(onlinePlayers, info.getOnlinePlayers(), otherPlayers);
            onlinePlayers = new ArrayList<>(info.getOnlinePlayers());

            ImageView[][] old = new ImageView[5][6];

            if(oldplayer.equals(selfName)){
                old = shelf0;
            } else {
                switch (otherPlayers.indexOf(oldplayer)) {
                    case 0 -> old = shelf1;
                    case 1 -> old = shelf2;
                    case 2 -> old = shelf3;
                }
            }
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 6; j++) {
                    old[i][j].setImage(getTexture(info.getShelf()[j][i]));
                }
            }

        }
        updateCurrentPlayer(info.getCurrentPlayer());
    }

    private void recolorPlayers(List<String> old, List<String> online, List<String> all) {
        for(String p: all){
            int i = all.indexOf(p);
            Button toMod;
            switch (i+1){
                case 1 -> toMod = player1Button;
                case 2 -> toMod = player2Button;
                case 3 -> toMod = player3Button;
                default -> toMod = null;
            }
            if(toMod!= null) {
                if (old.contains(p) &&!online.contains(p)) {
                    toMod.setBackground(disconnButtonback);
                    toMod.setOpacity(0.2);
                } else if(!old.contains(p) && online.contains(p)) {
                    toMod.setBackground(buttonback);
                    toMod.setOpacity(1);
                }
            }
        }

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

    public void initActions(GenericClient client, String selfName, GameScreen gameScreen){
        this.gameScreen = gameScreen;
        this.client = client;
        this.selfName = selfName;

        mainpanel.setVisible(true);
        myshelf.setVisible(false);
        player1.setVisible(false);
        player2.setVisible(false);
        player3.setVisible(false);
        armchair.setVisible(true);

        personalGoalView.setVisible(false);
        viewBoard.setGridLinesVisible(false);

        for(int i=0;  i<5; i++){
            for(int j=0;  j<6; j++){
                shelf0[i][j] = new ImageView();
                shelf0[i][j].setPreserveRatio(true);
                shelf0[i][j].setFitWidth(95);
                realShelf.add(shelf0[i][j], i, j);

                shelf1[i][j] = new ImageView();
                shelf1[i][j].setPreserveRatio(true);
                shelf1[i][j].setFitWidth(95);
                player1shelf.add(shelf1[i][j], i, j);

                shelf2[i][j] = new ImageView();
                shelf2[i][j].setPreserveRatio(true);
                shelf2[i][j].setFitWidth(95);
                player2shelf.add(shelf2[i][j], i, j);

                shelf3[i][j] = new ImageView();
                shelf3[i][j].setPreserveRatio(true);
                shelf3[i][j].setFitWidth(95);
                player3shelf.add(shelf3[i][j], i, j);
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

        mainpanel.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case ENTER -> toggleBoard(0);
                case W -> toggleBoard(1);
                case A -> {if(s_buttons[2].isVisible())toggleBoard(2);}
                case D -> {if(s_buttons[3].isVisible())toggleBoard(3);}
                case P -> gameScreen.disconnect();
            }
        });


        /*player1Button.setStyle("-fx-background-color: BURLYWOOD");
        player2Button.setStyle("-fx-background-color: BURLYWOOD");
        player3Button.setStyle("-fx-background-color: BURLYWOOD");
        myshelfButton.setStyle("-fx-background-color: BURLYWOOD");

        Background buttonback = new Background(new BackgroundFill(BURLYWOOD,new CornerRadii(0), new Insets(0)));
        player1Button.setBackground(buttonback);
        player2Button.setBackground(buttonback);
        player3Button.setBackground(buttonback);
        myshelfButton.setBackground(buttonback);*/


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
        for (Button s_button : s_buttons) {
            s_button.setBackground(buttonback);
        }

        chatMode.setPromptText("Lobby");
        chatMode.setValue("Lobby");
        chatField.setOnAction(actionEvent -> {
            sendChat();
        });
        armchair.setImage(new Image("17_MyShelfie_BGA/misc/firstplayertoken.png"));

        chatList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                final ListCell cell = new ListCell() {
                    private Text text;

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(chatList.getWidth()-10);
                            setGraphic(text);
                        }
                    }
                };

                return cell;
            }
        });
    }

    private void tryMove(int i) {
        synchronized (selected){
            synchronized (myTurn) {
//                if(!myTurn.get()){
//                    return;
//                }
                MoveMessage clientMessage = new MoveMessage();
                clientMessage.setTiles(selected);
                clientMessage.setColumn(i);

                if (client instanceof RMIClient) {
                    clientMessage.setRmiClient((RMIClient) client);
                }
                client.sendMsgToServer(clientMessage);
            }
        }
    }

    private void refreshBoard(){
        for(int i=0;  i<9; i++){
            for(int j=0;  j<9; j++){
                //if(livingroomBoard[i][j]!= null) {
                    imageViewBoard[j][i].setImage(getTexture(livingroomBoard[i][j]));
                //}
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
            if (selected.size() <= i || i < 0 || i > 3) {
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
        personalGoalView.setVisible(i==0 && !personalGoalView.isVisible());
        myshelf.setVisible(i == 0 && !myshelf.isVisible());
        player1.setVisible(i == 1 && !player1.isVisible());
        player2.setVisible(i == 2 && !player2.isVisible());
        player3.setVisible(i == 3 && !player3.isVisible());
    }

    public synchronized void updateChat(ChatUpdateMessage msg) {
        synchronized (chatList) {
            String s = " ~ ";
            String message = msg.getTimestamp() + s + msg.getSender() + s + msg.getContent();
            chatList.getItems().add(message);
            chatList.scrollTo(chatList.getItems().size());
        }
    }
    public synchronized void updateChat(PrivateChatUpdateMessage msg) {
        synchronized (chatList) {
            String s = " ~ ";
            String fromto = selfName.equals(msg.getReceiver()) ? "[from: "+msg.getSender() : "[to: "+msg.getReceiver();
            String message = msg.getTimestamp() + s + fromto + "]" + s + msg.getContent();
            chatList.getItems().add(message);
            chatList.scrollTo(chatList.getItems().size());
        }
    }
    private synchronized void sendChat(){
        synchronized (chatList) {
            String content = chatField.getText();
            if (content == null || content.isBlank()) {
                return;
            }

            ChatMessage clientMessage;
            if (!chatMode.getValue().equals("Lobby")) {
                clientMessage = new PrivateChatMessage(chatMode.getValue());
            } else {
                clientMessage = new ChatMessage();
            }
            clientMessage.setSender(selfName);
            clientMessage.setContent(content);

            if (client instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) client);
            }
            client.sendMsgToServer(clientMessage);

            chatField.clear();
        }
    }

    public void updateCurrentPlayer(String player){
        synchronized (myTurn) {
            currentPlayer = player;
            myTurn.set(player.equals(selfName));
            changeChairPos();
        }
        deselect(2);
        deselect(1);
        deselect(0);
    }

    private void changeChairPos(){
        double x = 0;
        double y = 0;
        if(currentPlayer.equals(selfName)){
            x=491;
            y=850;
        } else {
            for(int i=0; i<otherPlayers.size(); i++){
                if(currentPlayer.equals(otherPlayers.get(i)))
                    switch(i+1){
                        case 1 -> {x=491; y=35;}
                        case 2 -> {x=50; y=160;}
                        case 3 -> {x=1724; y=160;}
                    }
            }
        }
        armchair.setLayoutX(x);
        armchair.setLayoutY(y);
    }

    public void deactivate(String user) {
        List<String> onlineNow = new ArrayList<>(onlinePlayers);
        onlineNow.remove(user);
        recolorPlayers(onlinePlayers, onlineNow, otherPlayers);
    }

    public void disconnect() {
        QuitMessage clientMessage = new QuitMessage();
        if (client instanceof RMIClient) {
            clientMessage.setRmiClient((RMIClient) client);
        }
        client.sendMsgToServer(clientMessage);

        client.disconnect(false);
        client = null;
    }
}