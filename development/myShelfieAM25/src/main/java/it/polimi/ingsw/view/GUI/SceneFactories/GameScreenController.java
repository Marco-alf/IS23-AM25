package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.InitialGameInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class GameScreenController {
    @FXML
    ImageView board30, board40,
            board31, board41, board51,
            board22, board32, board42, board52,
            board13, board23, board33, board43, board53, board63, board73, board83,
            board04, board14, board24, board34, board44, board54, board64, board74, board84,
            board05, board15, board25, board35, board45, board55, board65, board75,
            board26, board36, board46, board56, board66,
            board37, board47, board57,
            board48, board58;

    @FXML
    BorderPane mainpanel;
    @FXML
    GridPane player1shelf;

    @FXML
    Button player1Button, player2Button, player3Button, myshelfButton;

    @FXML
    BorderPane player1, player2, player3, myshelf;

    @FXML
    ImageView goal1, goal2;

    List<String> players = new ArrayList<>();
    List<String> onlinePlayers = new ArrayList<>();


    private final ImageView [][] shelf0 = new ImageView[5][6];
    private final ImageView [][] shelf1 = new ImageView[5][6];
    private final ImageView [][] shelf2 = new ImageView[5][6];
    private final ImageView [][] shelf3 = new ImageView[5][6];


    private TilesType [][] livingroomBoard  = new TilesType[5][6];
    private final List<Tile> selected = new ArrayList<>();

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
        int i=0;
        for(; i<players.size(); i++){
            s_buttons[i].setText(players.get(i));
        }
        for( ; i<4; i++){
            s_buttons[i].setVisible(false);
        }
        goal1.setImage( new Image("17_MyShelfie_BGA/common_goal_cards/4.jpg"));
        goal2.setImage( new Image("17_MyShelfie_BGA/common_goal_cards/3.jpg"));
    }

    public void initActions(){
        mainpanel.setVisible(true);
        myshelf.setVisible(false);
        player1.setVisible(false);
        player2.setVisible(false);
        player3.setVisible(false);

        for(int i=0;  i<5; i++){
            for(int j=0;  j<6; j++){
                shelf1[i][j] = new ImageView();
                shelf1[i][j].setFitHeight(95);
                shelf1[i][j].setFitWidth(95);
                shelf1[i][j].setImage(cats);
                player1shelf.add(shelf1[i][j], i, j);
            }
        }
        player1Button.setOnAction(actionEvent -> toggleBoard(1));
        player2Button.setOnAction(actionEvent -> toggleBoard(2));
        player3Button.setOnAction(actionEvent -> toggleBoard(3));
        myshelfButton.setOnAction(actionEvent -> toggleBoard(0));

        board30.setOnMouseClicked(mouseEvent -> { select(3,0); } );

        s_buttons = new  Button[]{myshelfButton, player1Button, player2Button, player3Button };
    }

    private void refreshBoard(){
        board30.setImage(getTexture(livingroomBoard[3][0]));
        board40.setImage(getTexture(livingroomBoard[4][0]));
        board31.setImage(getTexture(livingroomBoard[3][1])) ;
        board41.setImage(getTexture(livingroomBoard[4][1])) ;
        board51.setImage(getTexture(livingroomBoard[5][1])) ;
        board22.setImage(getTexture(livingroomBoard[2][2])) ;
        board32.setImage(getTexture(livingroomBoard[3][2])) ;
        board42.setImage(getTexture(livingroomBoard[4][2])) ;
        board52.setImage(getTexture(livingroomBoard[5][2])) ;
        board13.setImage(getTexture(livingroomBoard[1][3])) ;
        board23.setImage(getTexture(livingroomBoard[2][3])) ;
        board33.setImage(getTexture(livingroomBoard[3][3])) ;
        board43.setImage(getTexture(livingroomBoard[4][4])) ;
        board53.setImage(getTexture(livingroomBoard[5][5])) ;
        board63.setImage(getTexture(livingroomBoard[6][6])) ;
        board73.setImage(getTexture(livingroomBoard[7][3])) ;
        board83.setImage(getTexture(livingroomBoard[8][3])) ;
        board04.setImage(getTexture(livingroomBoard[0][4])) ;
        board14.setImage(getTexture(livingroomBoard[1][4])) ;
        board24.setImage(getTexture(livingroomBoard[2][4])) ;
        board34.setImage(getTexture(livingroomBoard[3][4])) ;
        board44.setImage(getTexture(livingroomBoard[4][4])) ;
        board54.setImage(getTexture(livingroomBoard[5][4])) ;
        board64.setImage(getTexture(livingroomBoard[6][4])) ;
        board74.setImage(getTexture(livingroomBoard[7][4])) ;
        board84.setImage(getTexture(livingroomBoard[8][4])) ;
        board05.setImage(getTexture(livingroomBoard[0][5])) ;
        board15.setImage(getTexture(livingroomBoard[1][5])) ;
        board25.setImage(getTexture(livingroomBoard[2][5])) ;
        board35.setImage(getTexture(livingroomBoard[3][5])) ;
        board45.setImage(getTexture(livingroomBoard[4][5])) ;
        board55.setImage(getTexture(livingroomBoard[5][5])) ;
        board65.setImage(getTexture(livingroomBoard[6][5])) ;
        board75.setImage(getTexture(livingroomBoard[7][5])) ;
        board26.setImage(getTexture(livingroomBoard[2][6])) ;
        board36.setImage(getTexture(livingroomBoard[3][6])) ;
        board46.setImage(getTexture(livingroomBoard[4][6])) ;
        board56.setImage(getTexture(livingroomBoard[5][6])) ;
        board66.setImage(getTexture(livingroomBoard[6][6])) ;
        board37.setImage(getTexture(livingroomBoard[3][7])) ;
        board47.setImage(getTexture(livingroomBoard[4][7])) ;
        board57.setImage(getTexture(livingroomBoard[5][7])) ;
        board48.setImage(getTexture(livingroomBoard[4][8])) ;
        board58.setImage(getTexture(livingroomBoard[5][8])) ;
    }
    private void select(int i, int j){

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

}
