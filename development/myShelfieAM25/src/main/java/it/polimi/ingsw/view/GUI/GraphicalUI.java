package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.view.GUI.SceneFactories.GameScreen;
import it.polimi.ingsw.view.GUI.SceneFactories.PlayScreen;
import it.polimi.ingsw.view.GUI.SceneFactories.SceneFactory;
import it.polimi.ingsw.view.ViewInterface;
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

public class GraphicalUI extends Application implements SceneState, ViewInterface {

    SceneFactory factory;
    Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        mainStage = stage;
        stage.setTitle("MyShelfie");
        factory = new PlayScreen(this, screen, this);
        Scene scene = factory.getScene();
        stage.setScene(scene);
        stage.setHeight(scene.getHeight());
        stage.setWidth(scene.getWidth());
        stage.setMaximized(true);
        //stage.setFullScreen(true);
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

    @Override
    public void receiveCreatedLobbyMsg(CreatedLobbyMessage msg) {

    }

    @Override
    public void receiveJoinedMsg(JoinedMessage msg) {

    }

    @Override
    public void receiveExistingLobbyMsg(ExistingLobbyMessage msg) {

    }

    @Override
    public void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg) {

    }

    @Override
    public void receiveNameTakenMsg(NameTakenMessage msg) {

    }

    @Override
    public void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg) {

    }

    @Override
    public void receiveFullLobbyMsg(FullLobbyMessage msg) {

    }

    @Override
    public void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg) {

    }

    @Override
    public void receiveChatUpdateMsg(ChatUpdateMessage msg) {

    }

    @Override
    public void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg) {

    }

    @Override
    public void receiveGameCreatedMsg(GameCreatedMessage msg) {

    }

    @Override
    public void receiveGameUpdatedMsg(GameUpdatedMessage msg) {

    }

    @Override
    public void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg) {

    }

    @Override
    public void receiveInvalidMoveMsg(InvalidMoveMessage msg) {

    }

    @Override
    public void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg) {

    }

    @Override
    public void receiveLobbyClosedMsg(LobbyClosedMessage msg) {

    }

    @Override
    public void receiveUserDisconnectedMsg(UserDisconnectedMessage msg) {

    }

    @Override
    public void receiveInvalidCommandMsg(InvalidCommandMessage msg) {

    }

    @Override
    public void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {

    }
}
