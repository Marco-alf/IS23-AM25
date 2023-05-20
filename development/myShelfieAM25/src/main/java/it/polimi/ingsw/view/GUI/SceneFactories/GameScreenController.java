package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.data.InitialGameInfo;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
    GridPane player1shelf;

    private TilesType [][] livingroomBoard  = new TilesType[5][6];

    private final Image cats = new Image("17_MyShelfie_BGA/item_tiles/Gatti1.1.png");
    private final Image trophies = new Image("17_MyShelfie_BGA/item_tiles/Trofei1.1.png");
    private final Image frames = new Image("17_MyShelfie_BGA/item_tiles/Cornici1.1.png");
    private final Image games = new Image("17_MyShelfie_BGA/item_tiles/Giochi1.1.png");
    private final Image books = new Image("17_MyShelfie_BGA/item_tiles/Libri1.1.png");
    private final Image plants = new Image("17_MyShelfie_BGA/item_tiles/Piante1.1.png");

    public void updateInitialGameInfo(InitialGameInfo info) {
        livingroomBoard = info.getNewBoard();
        refreshBoard();
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
}
