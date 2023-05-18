package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LobbyScreen extends SceneHandler implements SceneFactory{

    GenericClient client;

    LobbyScreen(SceneState state, Rectangle2D screen, ViewInterface view, GenericClient client) {
        super(state, screen, view);
        scene = new Scene(waitingScreen());
        this.client = client;
    }

    private Parent waitingScreen(){
        TilePane r = new TilePane(Orientation.VERTICAL);
        Text title = new Text("Waiting for players!");
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        Button cancel = new Button("Go back to menu");
        cancel.setOnAction(actionEvent -> {
            state.forceUpdate(new MenuScreen(state, screen, view, client));
        });

        r.setAlignment(Pos.CENTER);
        r.setVgap(screen.getWidth()*0.005);
        r.getChildren().addAll(title, cancel);

        adjustScaling(r);
        return r;
    }

    @Override
    public SceneFactory next() {
        return new GameScreen(state, screen, view, client);
    }
}
