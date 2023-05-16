package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUI.SceneFactories.GameScreen;
import it.polimi.ingsw.view.GUI.SceneFactories.PlayScreen;
import it.polimi.ingsw.view.GUI.SceneFactories.SceneFactory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalUI extends Application implements SceneState{

    SceneFactory factory;
    Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        mainStage = stage;
        stage.setTitle("MyShelfie");
        factory = new GameScreen(this, screen);
        stage.setScene(factory.getScene());
        stage.setHeight(screen.getHeight());
        stage.setWidth(screen.getWidth());
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void update() {
        factory = factory.next();
        mainStage.setScene(factory.getScene());
        mainStage.show();
    }

    @Override
    public void forceUpdate(SceneFactory factory) {
        this.factory = factory;
        mainStage.setScene(factory.getScene());
        mainStage.show();
    }

}
