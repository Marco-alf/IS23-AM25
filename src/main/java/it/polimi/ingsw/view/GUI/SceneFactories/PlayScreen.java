package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.Stack;

public class PlayScreen extends SceneHandler implements SceneFactory{
    Background back = new Background(new BackgroundImage(new Image("17_MyShelfie_BGA/Publisher_material/Display_1.jpg"),
            null,
            null,
            BackgroundPosition.CENTER,
            new BackgroundSize(100,100, true, true, true, false)));
    GenericClient client;
    static int porta = 1099;

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
        Image bkgnd = new Image("17_MyShelfie_BGA/Publisher_material/Display_1.jpg");
        ImageView sfondo = new ImageView(bkgnd);
        sfondo.setPreserveRatio(true);
        sfondo.setFitHeight(1080);


        Button b = new Button("Play");
        b.setOnAction(actionEvent -> {
            scene.setRoot(connectionPrompt());
        });

        BorderPane r = new BorderPane();
        r.setBackground(back);
        r.setMaxSize(1920, 1080);
        r.setPrefSize(1920, 1080);
        r.setMinSize(1920, 1080);

        //t.getChildren().add(b);

        b.setAlignment(Pos.CENTER);
        b.setScaleX(2);
        b.setScaleY(2);

        //r.getChildren().addAll(sfondo);
        r.setCenter(b);

        return r;
    }
    private Parent connectionPrompt(){


        TilePane r = new TilePane(Orientation.VERTICAL);
        TilePane t = new TilePane();
        Text title = new Text("Choose connection mode:");
        //title.setFill(Paint.valueOf( 	"CORNSILK"));
        title.setSelectionFill(Paint.valueOf("WHITE"));
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        int corners = 8;
        Rectangle textBox = new Rectangle(title.getLayoutBounds().getWidth()+corners, title.getLayoutBounds().getHeight()+corners, Paint.valueOf("CORNSILK"));
        textBox.setArcWidth(corners);
        textBox.setArcHeight(corners);
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
        r.getChildren().addAll(new StackPane(textBox, title), t);
        r.setHgap(screen.getWidth()*0.005);
        r.setAlignment(Pos.CENTER);


        StackPane g = new StackPane();
        g.setBackground(back);
        g.setMaxSize(1920, 1080);
        g.setPrefSize(1920, 1080);
        g.setMinSize(1920, 1080);

        g.getChildren().add(r);



        adjustScaling(r);
        return g;
    }

    public void initiateTCP(){
        client = new SocketClient("localhost", 8088, view);
        client.init();
        state.update();
    }

    public void initiateRMI(){
        client = new RMIClient("localhost", porta , view);
        client.init();
        state.update();
    }

}