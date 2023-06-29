package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.client.RMIClient;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.messages.clientMessages.QuitMessage;
import it.polimi.ingsw.network.messages.serverMessages.ChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.GameUpdatedMessage;
import it.polimi.ingsw.network.messages.serverMessages.PrivateChatUpdateMessage;
import it.polimi.ingsw.network.messages.serverMessages.UpdatedPlayerMessage;
import it.polimi.ingsw.view.GUI.GraphicalUI;
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

/**
 * Scene Factory for game creation and management of game interface, calls and updates.
 * */
public class GameScreen extends SceneHandler implements SceneFactory{

    private GameScreenController controller;

    /**
     * Constructor for GameScreen, will load fxml and create its controller.
     * controller will be initialized calling initActions() and
     * */
    public GameScreen(SceneState state, Rectangle2D screen, ViewInterface view, String selfName) {
        super(state, screen, view);

        Parent r = null;
        try {
            controller = new GameScreenController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/playGameScreen.fxml"));
            loader.setController(controller);
            r = loader.load();

            controller.initActions(state, selfName, this);

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
            quit();
        }



    }

    /**
     * Method to update shown information of the game from zero, called from ViewInterface implementation on GraphicalUI
     * @param info InitialGameInfo object, containing all information of the game
     * */
    public void updateInitialGameInfo(InitialGameInfo info){
        controller.updateInitialGameInfo(info);
    }

    /**
     * Method to update the turn indicator, called from ViewInterface implementation on GraphicalUI
     * @param player Name of the player that now has the right to make a move
     * */
    public void updateCurrentPlayer(String player){
        controller.updateCurrentPlayer(player);
    }

    /**
     * Method called when game ends, it will show the ranking of the game
     * @return new LobbyScreen instance, with nicknamebuffer as the name chosen by the user as his
     * */
    @Override
    public SceneFactory next() {
        return new FinalScreen(state, screen, view);
    }

    private void quit(){
        state.forceUpdate(new MenuScreen(state, screen, view));
    }

    /**
     * Custom implementation of adjust scaling, made to fit the fxml to the screen
     * @param p node to have its scaling changed
     * */
    @Override
    public void adjustScaling(Parent p){
        Scale scala = new Scale();
        scala.setPivotX(screen.getWidth()/2);
        scala.setPivotY(screen.getHeight()/2);
        double xscaling = screen.getWidth()/1920 ;
        double yscaling = screen.getHeight()/1080 ;
        scala.setX(xscaling);
        scala.setY(yscaling);
        p.getTransforms().clear();
        p.getTransforms().add(scala);
    }

    /**
     * Updates the chat adding a single message, called from ViewInterface implementation on GraphicalUI
     * @param msg Contains a message
     * */
    public void updateChat(ChatUpdateMessage msg) {
        controller.updateChat(msg);
    }
    /**
     * Updates the chat adding a single Private message, called from ViewInterface implementation on GraphicalUI
     * @param msg Contains a Private message
     * */
    public void updateChat(PrivateChatUpdateMessage msg) {
        controller.updateChat(msg);
    }

    /**
     * Updates the UI to change game state, changing board, shelves and turn indicator.
     * Method called from ViewInterface implementation on GraphicalUI
     * @param msg Contains a message
     * */
    public void updateGame(GameUpdatedMessage msg) {
        controller.updateGame(msg.getGameInfo());
    }

    /**
     * Updates user banners to have disconnected one as transparent.
     * Method called from ViewInterface implementation on GraphicalUI
     * @param user The user that has just disconnected
     * */
    public void deactivate(String user) {
        controller.deactivate(user);
    }

    /**
     * Method to disconnect this client from the server, if fromserver parameter is false this client will send a
     * QuitMessage to the server, signalling its will to cease all connection.
     * This method is either called from ViewInterface implementation on GraphicalUI or as a server consequence of a
     * message from a server signalling the closure of the lobby
     * @param fromserver true if this call comes from the server, false if it's called from JavaFx controls
     * */
    public void disconnect(boolean fromserver) {

            state.setIsDisconnecting(true);

        controller.disconnect(fromserver);
        state.setClient(null);
        System.gc();


        state.forceUpdate(new PlayScreen(state, screen, view));
    }
}
