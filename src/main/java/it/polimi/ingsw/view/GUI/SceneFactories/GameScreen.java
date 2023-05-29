package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.GameUpdatedMessage;
import it.polimi.ingsw.network.messages.serverMessages.PrivateChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.UpdatedPlayerMessage;
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
    GameScreenController controller;

    String selfName;
    public GameScreen(SceneState state, Rectangle2D screen, ViewInterface view, GenericClient client, String selfName) {
        super(state, screen, view);
        this.client = client;
        this.selfName = selfName;

        board = new ImageView(new Image("17_MyShelfie_BGA/boards/livingroom.png"));

        Parent r = null;
        try {
            controller = new GameScreenController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/playGameScreen.fxml"));
            loader.setController(controller);
            r = loader.load();

            controller.initActions(client, selfName);

            Scale sc = new Scale();
            double xscaling = 1;
            double yscaling = 1;
            if(screen.getWidth()/screen.getHeight() < 16d/9) {
                /* height is greater*/
                xscaling = screen.getWidth() / 1920;
                yscaling = xscaling;
            } else{
                yscaling = screen.getHeight() / 1080;
                xscaling = yscaling;
            }
            sc.setX(xscaling);
            sc.setY(yscaling);
            r.getTransforms().add(sc);
            scene = new Scene(r, 1920, 1080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    public void updateInitialGameInfo(InitialGameInfo info){
        controller.updateInitialGameInfo(info);
    }

    public void updateCurrentPlayer(String player){
        controller.updateCurrentPlayer(player);
    }

    @Override
    public SceneFactory next() {
        return new FinalScreen(state, screen, view, client);
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

    public void updateChat(ChatUpdateMessage msg) {
        controller.updateChat(msg);
    }
    public void updateChat(PrivateChatUpdateMessage msg) {
        controller.updateChat(msg);
    }

    public void updateGame(GameUpdatedMessage msg) {
        controller.updateGame(msg.getGameInfo());
    }

    public void deactivate(String user) {
        controller.deactivate(user);
    }
}
