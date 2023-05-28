package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.data.InitialGameInfo;
import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.view.GUI.SceneFactories.*;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class GraphicalUI extends Application implements SceneState, ViewInterface {

    SceneFactory factory;
    Stage mainStage;
    Rectangle2D screen;

    @Override
    public void start(Stage stage) throws IOException {
        screen = Screen.getPrimary().getBounds();
        mainStage = stage;

        mainStage.setTitle("MyShelfie");
        factory = new PlayScreen(this, screen, this);
        Scene scene = factory.getScene();
        mainStage.setScene(scene);
        mainStage.setHeight(scene.getHeight());
        mainStage.setWidth(scene.getWidth());
        mainStage.setMaximized(true);
        //stage.setFullScreen(true);
        mainStage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    @Override
    public void receiveJoinedMsg(JoinedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    @Override
    public void receiveExistingLobbyMsg(ExistingLobbyMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" a lobby with the same name already exists!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveLobbyNotCreatedMsg(LobbyNotCreatedMessage msg) {
        //?????
    }

    @Override
    public void receiveNameTakenMsg(NameTakenMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" name is already taken!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveNotExistingLobbyMsg(NotExistingLobbyMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" lobby does not exist!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveFullLobbyMsg(FullLobbyMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" lobby is full!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveRetrievedLobbiesMsg(RetrievedLobbiesMessage msg) {
        if(factory instanceof MenuScreen menu){
            menu.receiveRefresh(msg.getLobbies());
        }
    }

    @Override
    public void receiveChatUpdateMsg(ChatUpdateMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    game.updateChat(msg);
                }
            }
        });
    }

    @Override
    public void receivePrivateChatUpdateMsg(PrivateChatUpdateMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    game.updateChat(msg);
                }
            }
        });
    }

    @Override
    public void receiveGameCreatedMsg(GameCreatedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                update();
                if(factory instanceof GameScreen game){
                    game.updateInitialGameInfo(msg.getGameInfo());
                }
            }
        });
    }

    @Override
    public void receiveGameUpdatedMsg(GameUpdatedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    game.updateGame(msg);
                }
            }
        });
    }

    @Override
    public void receiveGameEndedMsg(GameEndedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    update();
                    if(factory instanceof FinalScreen fin){
                        fin.setInfo(msg.getGameInfo());
                    }
                }
            }
        });
    }

    @Override
    public void receiveUpdatedPlayerMsg(UpdatedPlayerMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    game.updateCurrentPlayer(msg.getUpdatedPlayer());
                }
            }
        });
    }

    @Override
    public void receiveInvalidMoveMsg(InvalidMoveMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" illegal move!" +
                        " formare una riga verticale oppure orizzontale con le tessere che possiedono un lato non " +
                        "condiviso con altre tessere");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveInsufficientPlayersMsg(InsufficientPlayersMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" insufficient players for the lobby");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveLobbyClosedMsg(LobbyClosedMessage msg) {

    }

    @Override
    public void receiveUserDisconnectedMsg(UserDisconnectedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    game.deactivate(msg.getUser());
                    game.updateCurrentPlayer(msg.getCurrentPlayer());
                }
            }
        });
    }

    @Override
    public void receiveInvalidCommandMsg(InvalidCommandMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" invalid command!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {
        mainStage.setTitle("ERRORE");
        TilePane r = new TilePane();
        r.setAlignment(Pos.CENTER);
        Text t = new Text("ERRORE DI CONNESSIONE: SERVER IRRAGGIUNGIBILE");
        t.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
        r.getChildren().add(t);
        mainStage.setScene(new Scene(r));
        mainStage.show();
    }

    @Override
    public void receiveInvalidLobbyNameMsg(InvalidLobbyNameMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" invalid lobby name!");
                a.showAndWait();
            }
        });
    }

    @Override
    public void receiveIllegalPlayerNameMsg(IllegalPlayerNameMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(msg.getType()+" name chosen is not permitted! must be non-blank");
                a.showAndWait();
            }
        });
    }
}
