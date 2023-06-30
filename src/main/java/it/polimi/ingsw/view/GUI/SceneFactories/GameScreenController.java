package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.messages.clientMessages.ChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.MoveMessage;
import it.polimi.ingsw.network.messages.clientMessages.PrivateChatMessage;
import it.polimi.ingsw.network.messages.clientMessages.QuitMessage;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.GameUpdatedMessage;
import it.polimi.ingsw.network.messages.serverMessages.PrivateChatUpdateMessage;
import it.polimi.ingsw.view.GUI.GraphicalUI;
import it.polimi.ingsw.view.GUI.SceneState;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.paint.Color.BURLYWOOD;

/**
 * Controller for game scene, it will handle calls from GameScreen and apply updates to the elements shown.
 * JavaFx controls' objects will call the client directly sending messages to the server, covering the user's possible
 * actions.
 * */
public class GameScreenController {

    private enum GoalNumbers {
        EIGHT(
                8,
                new Image("17_MyShelfie_BGA/scoring_tokens/scoring_8.jpg")
        ),
        SIX(
                6,
                new Image("17_MyShelfie_BGA/scoring_tokens/scoring_6.jpg")
        ),
        FOUR(
                4,
                new Image("17_MyShelfie_BGA/scoring_tokens/scoring_4.jpg")
        ),
        TWO(
                2,
                new Image("17_MyShelfie_BGA/scoring_tokens/scoring_2.jpg")
        );
        private final int num;
        private final Image texture;
        private GoalNumbers(int num, Image texture){
            this.num = num;
            this.texture = texture;
        }
        public static GoalNumbers next(GoalNumbers prev, int size){
            if(size>4 || size<2 || prev == null){
                return null;
            }
            GoalNumbers toReturn;
            switch (prev.num){
                case 8 -> {
                    toReturn = size>2 ? SIX : FOUR;
                }
                case 6 -> {
                    toReturn = FOUR;
                }
                case 4 -> {
                    toReturn = size >2 ? TWO : null;
                }
                default -> {
                    toReturn = null;
                }
            }
            return toReturn;
        }

        private int getNum(){
            return num;
        }
        public Image getTexture(){
            return texture;
        }

        public static Image getTextureFromNumber(int search){
            return Arrays.stream(values()).filter(x->x.num == search)
                    .findFirst()
                    .map(GoalNumbers::getTexture)
                    .orElse(null);
        }
    }










    private SceneState state = null;
    private GameScreen gameScreen;

    private final AtomicBoolean myTurn = new AtomicBoolean(false);
    private String currentPlayer;
    @FXML
    ImageView armchair;

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
    VBox myshelf, side;
    @FXML
    ImageView conqGoal1, conqGoal2;
    @FXML
    ImageView goal1, goal2, commongoal1stack, commongoal2stack;

    private GoalNumbers goal1token = GoalNumbers.EIGHT, goal2token = GoalNumbers.EIGHT;

    private List<String> players = new ArrayList<>();
    private List<String> otherPlayers = new ArrayList<>();
    private List<String> onlinePlayers = new ArrayList<>();


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

    private ArrayList<String> chat = new ArrayList<>();
    @FXML
    TextField chatField;
    @FXML
    ComboBox<String> chatMode;

    @FXML
    ImageView personalGoalView;

    private final Image [] cats = new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Gatti1.1.png")
    };

    private final Image [] trophies = new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Trofei1.1.png")
    };

    private final Image [] frames = new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Cornici1.1.png")
    };
    private final Image[] games =new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Giochi1.1.png")
    };
    private final Image[] books =new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Libri1.1.png")
    };

    private final Image[] plants =new Image[]{ new Image("17_MyShelfie_BGA/item_tiles/Piante1.1.png")
    };

    private Button[] s_buttons = new Button[]{myshelfButton, player1Button, player2Button, player3Button };
    private Background buttonback = new Background(new BackgroundFill(BURLYWOOD,new CornerRadii(0), new Insets(0)));
    private Background disconnButtonback = new Background(new BackgroundFill(BURLYWOOD,new CornerRadii(0), new Insets(0)));

    private Map<String, Integer> commonscores1 = new HashMap<>();
    private Map<String, Integer> commonscores2 = new HashMap<>();


    protected void updateInitialGameInfo(InitialGameInfo info) {
        synchronized (myTurn) {

            livingroomBoard = info.getNewBoard();
            refreshBoard();
            players = new ArrayList<>(info.getOnlinePlayers());
            otherPlayers = new ArrayList<>(players);
            otherPlayers.remove(selfName);

            recolorPlayers(onlinePlayers, info.getOnlinePlayers(), otherPlayers);
            onlinePlayers = new ArrayList<>(info.getOnlinePlayers());

            chatMode.getItems().clear();
            chatMode.getItems().add("Lobby");
            int i = 1;
            int k = 0;
            for (; i < players.size(); i++, k++) {
                if (players.get(k).equals(selfName)) {
                    k++;
                }
                s_buttons[i].setText(players.get(k));
                s_buttons[i].setVisible(true);
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

            for(String p : players){
                
                commonscores1.put(p, info.getCommonPoints(p, 0));
                commonscores2.put(p, info.getCommonPoints(p, 1));
            }

            int counter1=0, counter2=0;
            for(String p : players){
                counter1 += info.getCommonPoints(p, 0)>0 ? 1 : 0;
                counter2 += info.getCommonPoints(p, 1)>0 ? 1 : 0;
            }
            while(counter1>0 || counter2>0){
                if(counter1>0){
                    goal1token = GoalNumbers.next(goal1token, players.size());
                    counter1--;
                }
                if(counter2>0){
                    goal2token = GoalNumbers.next(goal2token, players.size());
                    counter2--;
                }
            }
            commongoal1stack.setImage(goal1token != null ? goal1token.getTexture() : null);
            commongoal2stack.setImage(goal2token != null ? goal2token.getTexture() : null);

            conqGoal1.setImage( GoalNumbers.getTextureFromNumber(info.getCommonPoints(selfName,0)) );
            conqGoal2.setImage( GoalNumbers.getTextureFromNumber(info.getCommonPoints(selfName,1)) );


            personalGoalView.setImage(new Image("17_MyShelfie_BGA/personal_goal_cards/Personal_Goals" + (info.getPersonalGoals().get(selfName).ordinal() + 1) + ".png"));
            goal1.setImage(new Image("17_MyShelfie_BGA/common_goal_cards/" + commongoaltranslator(info.getCommonGoal1()) + ".jpg"));
            goal2.setImage(new Image("17_MyShelfie_BGA/common_goal_cards/" + commongoaltranslator(info.getCommonGoal2()) + ".jpg"));


        }
        updateCurrentPlayer(info.getCurrentPlayer());
    }

    protected void updateGame(GameInfo info) {
        synchronized (myTurn) {
            String oldplayer = currentPlayer;
            livingroomBoard = info.getNewBoard();
            refreshBoard();

            recolorPlayers(onlinePlayers, info.getOnlinePlayers(), otherPlayers);
            onlinePlayers = new ArrayList<>(info.getOnlinePlayers());
            players = onlinePlayers;
            otherPlayers = new ArrayList<>(players);
            otherPlayers.remove(selfName);


            chatMode.getItems().clear();
            chatMode.getItems().add("Lobby");
            int iii = 1;
            int lll = 0;
            for (; iii < players.size(); iii++, lll++) {
                if (players.get(lll).equals(selfName)) {
                    lll++;
                }
                s_buttons[iii].setText(players.get(lll));
                s_buttons[iii].setVisible(true);
                chatMode.getItems().add(players.get(lll));
            }
            myshelfButton.setText("(Me) " + selfName);

            for (; iii < 4; iii++) {
                s_buttons[iii].setVisible(false);
            }


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

            if(info.getCommonGoal1Points()>0   &&    commonscores1.get(oldplayer) != info.getCommonGoal1Points() ){

                commonscores1.replace(oldplayer, info.getCommonGoal1Points());
                if(oldplayer.equals(selfName)){
                    conqGoal1.setImage(goal1token.getTexture());
                }
                goal1token = GoalNumbers.next(goal1token, players.size());
                commongoal1stack.setImage(goal1token != null ? goal1token.getTexture() : null);

            }
            if(info.getCommonGoal2Points()>0   &&   commonscores2.get(oldplayer) != info.getCommonGoal2Points() ){

                commonscores2.replace(oldplayer, info.getCommonGoal2Points());
                if(oldplayer.equals(selfName)){
                    conqGoal2.setImage(goal2token.getTexture());
                }
                goal2token = GoalNumbers.next(goal2token, players.size());
                commongoal2stack.setImage(goal2token != null ? goal2token.getTexture() : null);
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

    protected void initActions(SceneState state, String selfName, GameScreen gameScreen){
        this.gameScreen = gameScreen;
        this.state = state;
        this.selfName = selfName;

        mainpanel.setVisible(true);
        myshelf.setVisible(false);
        player1.setVisible(false);
        player2.setVisible(false);
        player3.setVisible(false);
        armchair.setVisible(true);

        side.setVisible(false);
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
        realShelf.setGridLinesVisible(false);
        player1shelf.setGridLinesVisible(false);
        player2shelf.setGridLinesVisible(false);
        player3shelf.setGridLinesVisible(false);

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
                case SPACE -> toggleBoard(0);
                case S -> toggleBoard(0);
                case W -> toggleBoard(1);
                case A -> {if(s_buttons[2].isVisible())toggleBoard(2);}
                case D -> {if(s_buttons[3].isVisible())toggleBoard(3);}
                case P -> gameScreen.disconnect(false);
                case J -> System.exit(0);
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
            arrowsView[i] = new ImageView("17_MyShelfie_BGA/misc/arrow3.png");
            arrowsView[i].setPreserveRatio(true);
            arrowsView[i].setFitHeight(95);
            int finalI = i;
            arrowsView[i].setOnMouseClicked(mouseEvent -> {tryMove(finalI);});
            arrows.add(arrowsView[i], i, 0);
        }
        arrows.setGridLinesVisible(false);

        s_buttons = new  Button[]{myshelfButton, player1Button, player2Button, player3Button };
        for (Button s_button : s_buttons) {
            s_button.setBackground(buttonback);
        }

        chatMode.setPromptText("Lobby");
        chatMode.setValue("Lobby");
        chatField.setOnAction(actionEvent -> {
            sendChat();
        });

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

                if (state.getClient() instanceof RMIClient) {
                    clientMessage.setRmiClient((RMIClient) state.getClient());
                }
                state.getClient().sendMsgToServer(clientMessage);
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
            case TROPHIES -> result = trophies[0];
            case GAMES -> result = games[0];
            case BOOKS -> result = books[0];
            case PLANTS -> result = plants[0];
            case FRAMES -> result = frames[0];
            case CATS -> result = cats[0];
            default -> result = null;
        }
        return result;
    }

    private void toggleBoard(int i){
        side.setVisible(i==0 && !side.isVisible());
        myshelf.setVisible(i == 0 && !myshelf.isVisible());
        player1.setVisible(i == 1 && !player1.isVisible());
        player2.setVisible(i == 2 && !player2.isVisible());
        player3.setVisible(i == 3 && !player3.isVisible());
    }

    protected synchronized void updateChat(ChatUpdateMessage msg) {
        synchronized (chatList) {
            String s = " ~ ";
            String message = msg.getTimestamp() + s + msg.getSender() + s + msg.getContent();
            chatList.getItems().add(message);
            chatList.scrollTo(chatList.getItems().size());
        }
    }
    protected synchronized void updateChat(PrivateChatUpdateMessage msg) {
        synchronized (chatList) {
            if(msg.getReceiver().equals(selfName) || msg.getSender().equals(selfName)) {
                String s = " ~ ";
                String fromto = selfName.equals(msg.getReceiver()) ? "[from: " + msg.getSender() : "[to: " + msg.getReceiver();
                String message = msg.getTimestamp() + s + fromto + "]" + s + msg.getContent();
                chatList.getItems().add(message);
                chatList.scrollTo(chatList.getItems().size());
            }
        }
    }
    protected synchronized void sendChat(){
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

            if (state.getClient() instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) state.getClient());
            }
            state.getClient().sendMsgToServer(clientMessage);

            chatField.clear();
        }
    }

    protected void updateCurrentPlayer(String player){
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
        double theta = 0;
        if(currentPlayer.equals(selfName)){
            x=545;
            y=891;
            theta= -90;
        } else {
            for(int i=0; i<otherPlayers.size(); i++){
                if(currentPlayer.equals(otherPlayers.get(i)))
                    switch(i+1){
                        case 1 -> {x=491; y=41; theta = 90;}
                        case 2 -> {x=55; y=160; theta = 0;}
                        case 3 -> {x=1735; y=160;theta = -180;}
                    }
            }
        }
        armchair.setLayoutX(x);
        armchair.setLayoutY(y);
        armchair.setRotate(theta);
    }

    protected void deactivate(String user) {
        List<String> onlineNow = new ArrayList<>(onlinePlayers);
        onlineNow.remove(user);
        recolorPlayers(onlinePlayers, onlineNow, otherPlayers);
        onlinePlayers.remove(user);
    }

    protected void disconnect(boolean fromserver) {
        if(!fromserver) {
            QuitMessage clientMessage = new QuitMessage();
            if (state.getClient() instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) state.getClient());
            }
            state.getClient().sendMsgToServer(clientMessage);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        state.getClient().disconnect(false);
        state.setClient(null);
    }
}
