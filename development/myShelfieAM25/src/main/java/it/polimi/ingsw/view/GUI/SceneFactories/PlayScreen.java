package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
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

public class PlayScreen extends SceneHandler implements SceneFactory{

    GenericClient client;

    public PlayScreen(SceneState state, Rectangle2D screen, ViewInterface view){
        super(state, screen, view);
        scene = new Scene(playButton(), screen.getWidth(), screen.getHeight());
    }

    @Override
    public SceneFactory next() {
        if (client != null){
            return new MenuScreen(state, screen, view, client);
            //System.exit(69);
        }

        return this;
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
        TilePane r = new TilePane(Orientation.VERTICAL);
        TilePane t = new TilePane();
        Text title = new Text("Choose connection mode:");
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        Button tcp = new Button("TCP");
        tcp.setOnAction(actionEvent -> {
            initiateTCP();
        });
        Button rmi = new Button("RMI");
        rmi.setOnAction(actionEvent -> {
            initiateRMI();
        });

        t.getChildren().addAll(tcp, rmi);
        t.setHgap(screen.getWidth()*0.005);
        t.setAlignment(Pos.CENTER);
        r.getChildren().addAll(title, t);
        r.setHgap(screen.getWidth()*0.005);
        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }

    public void initiateTCP(){
        client = new SocketClient("localhost", 8088, view);
        client.init();
        state.update();
    }

    public void initiateRMI(){
        client = new RMIClient("localhost", 1099, view);
        client.init();
        state.update();
    }

}
