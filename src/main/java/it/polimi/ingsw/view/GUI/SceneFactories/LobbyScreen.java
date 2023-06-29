package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.messages.clientMessages.QuitMessage;
import it.polimi.ingsw.view.GUI.GraphicalUI;
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

/**
 * Factory to produce the waiting screen scene
 * */
public class LobbyScreen extends SceneHandler implements SceneFactory{

    private final String selfName;

    /**
     * Constructor used to instantiate the scene graph as the waiting screen and buffer the user's name
     * */
    LobbyScreen(SceneState state, Rectangle2D screen, ViewInterface view, String selfName) {
        super(state, screen, view);
        scene = new Scene(waitingScreen());
        this.selfName = selfName;

    }

    private Parent waitingScreen(){
        TilePane r = new TilePane(Orientation.VERTICAL);
        Text title = new Text("Waiting for players!");
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        Button cancel = new Button("Go back to menu");
        cancel.setOnAction(actionEvent -> {
            QuitMessage clientMessage = new QuitMessage();
            if (state.getClient() instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) state.getClient());
            }
            state.getClient().sendMsgToServer(clientMessage);
            state.getClient().disconnect(false);
            state.setClient(null);
            state.forceUpdate(new PlayScreen(state, screen, view));
        });

        r.setAlignment(Pos.CENTER);
        r.setVgap(screen.getWidth()*0.005);
        r.getChildren().addAll(title, cancel);

        adjustScaling(r);
        return r;
    }

    /**
     * Method called when entering a lobby, it will transition to waiting screen
     * @return new GameScreen instance, selfName as buffer for the user's name
     * */
    @Override
    public SceneFactory next() {
        return new GameScreen(state, screen, view, selfName);
    }
}
