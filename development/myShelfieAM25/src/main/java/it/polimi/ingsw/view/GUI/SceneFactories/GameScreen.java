package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.view.GUI.SceneState;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.transform.Scale;

public class GameScreen extends SceneHandler implements SceneFactory{

    ImageView board;
    public GameScreen(SceneState state, Rectangle2D screen) {
        super(state, screen);

        board = new ImageView(new Image("17_MyShelfie_BGA/boards/livingroom.png"));

        board.setPreserveRatio(true);
        board.setFitHeight(720);
        TilePane r = new TilePane();

        r.getChildren().add(board);
        r.setAlignment(Pos.CENTER);
        adjustScaling(r);
        scene = new Scene(r);
    }



    @Override
    public SceneFactory next() {
        return null;
    }

    private void quit(){
        state.forceUpdate(new MenuScreen(state, screen));
    }

    @Override
    public void adjustScaling(Parent p){
        Scale scala = new Scale();
        scala.setPivotX(screen.getWidth()/2);
        scala.setPivotY(screen.getHeight()/2);
        double xscaling = screen.getWidth()/1920 ;
        double yscaling = screen.getHeight()/1080 ;
        scala.setX(xscaling);
        scala.setY(yscaling);
        p.getTransforms().add(scala);
    }
}
