package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.view.GUI.SceneState;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class PlayScreen extends SceneHandler implements SceneFactory{
    public PlayScreen(SceneState state, Rectangle2D screen){
        super(state, screen);
        scene = new Scene(playButton(), screen.getWidth(), screen.getHeight());

    }

    @Override
    public SceneFactory next() {
        return new MenuScreen(state, screen);
    }

    private Parent playButton(){
        TilePane r = new TilePane();
        Button b = new Button("Play");
        b.setOnAction(actionEvent -> {
            scene.setRoot(connectionPrompt());
        });
        r.getChildren().add(b);
        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }
    private Parent connectionPrompt(){
        TilePane r = new TilePane();
        Button tcp = new Button("TCP");
        tcp.setOnAction(actionEvent -> {
            state.update();
        });
        Button rmi = new Button("RMI");
        rmi.setOnAction(actionEvent -> {
            state.update();
        });
        r.getChildren().addAll(tcp, rmi);
        r.setHgap(screen.getWidth()*0.005);
        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }

}
