package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class GameScreen extends SceneHandler implements SceneFactory{

    GenericClient client;
    ImageView board;
    public GameScreen(SceneState state, Rectangle2D screen, ViewInterface view, GenericClient client) {
        super(state, screen, view);
        this.client = client;

        board = new ImageView(new Image("17_MyShelfie_BGA/boards/livingroom.png"));

        Path res = Path.of("development/myShelfieAM25/src/main/resources/fxml_files/playGameScreen.fxml");
        //                      "C:\Users\marco\sweng\idontknowanymore\IS23-AM25\development\myShelfieAM25\src\main\resources\fxml_files\playGameScreen.fxml"

        Parent r = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            r = FXMLLoader.load(res.toUri().toURL());

            Scale sc = new Scale();
            double xscaling = screen.getWidth()/1920 ;
            double yscaling = screen.getHeight()/1080 ;
            sc.setX(xscaling);
            sc.setY(yscaling);
            //r.getTransforms().add(sc);
            scene = new Scene(r, 1920, 1080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }



    @Override
    public SceneFactory next() {
        return null;
    }

    private void quit(){
        state.forceUpdate(new MenuScreen(state, screen, view, client));
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