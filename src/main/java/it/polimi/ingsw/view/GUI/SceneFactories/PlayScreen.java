package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;

public class PlayScreen extends SceneHandler implements SceneFactory{
    Background back = new Background(new BackgroundImage(new Image("17_MyShelfie_BGA/Publisher_material/Display_1.jpg"),
            null,
            null,
            BackgroundPosition.CENTER,
            new BackgroundSize(100,100, true, true, true, false)));
    static int porta = 1099;

    TextField askip;

    public PlayScreen(SceneState state, Rectangle2D screen, ViewInterface view){
        super(state, screen, view);
        scene = new Scene(playButton(), screen.getWidth(), screen.getHeight());

        state.setIsDisconnecting(false);
    }

    @Override
    public SceneFactory next() {
        if (state.getClient() != null){
            return new MenuScreen(state, screen, view);
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


        askip = new TextField();
        askip.setText("localhost");

        t.getChildren().addAll(tcp, rmi);
        t.setHgap(screen.getWidth()*0.005);
        t.setAlignment(Pos.CENTER);
        r.getChildren().addAll(new StackPane(textBox, title), t, askip);
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
        if(isIPbroken(askip)){
            return;
        }
        state.setClient(new SocketClient(askip.getText(), 8088, view));
        state.getClient().init();
        state.update();
    }

    public void initiateRMI(){
        if(isIPbroken(askip)){
            return;
        }
        state.setClient(new SocketClient(askip.getText(), 8088, view));
        state.getClient().init();
        state.update();
    }

    private boolean isIPbroken(TextField t){
        if( isValidIP(t.getText())){
            return false;
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("enter a valid ip address!");
            a.showAndWait();
            return true;
        }
    }
    private boolean isValidIP(String ip) {
        if(ip.equals("localhost")) return true;
        String[] groups = ip.split("\\.");
        if (groups.length != 4) {
            return false;
        }
        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() >= 1)
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
