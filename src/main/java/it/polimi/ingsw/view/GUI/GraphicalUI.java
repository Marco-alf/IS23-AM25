package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.network.messages.serverMessages.*;
import it.polimi.ingsw.view.GUI.SceneFactories.*;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraphicalUI extends Application implements SceneState, ViewInterface {

    private SceneFactory factory;
    private Stage mainStage;
    private Rectangle2D screen;

    private GenericClient oneClient = null;


    @Override
    public GenericClient getClient(){
        return oneClient;
    };
    @Override
    public void setClient(GenericClient client){
        oneClient = client;
    };


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

    private void cycleToGame(){
        if(factory instanceof MenuScreen){
            update();
        }
        if(factory instanceof LobbyScreen){
            update();
        }
    }

    @Override
    public void update() {
        SceneFactory swap = factory.next();
        factory = null;
        factory = swap;
        mainStage.setScene(factory.getScene());
        mainStage.show();
    }

    @Override
    public void forceUpdate(SceneFactory factory) {
        this.factory = null;
        this.factory = factory;
        mainStage.setScene(factory.getScene());
        mainStage.show();
    }





    @Override
    public void receiveCreatedLobbyMsg(CreatedLobbyMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof MenuScreen) {
                    update();
                }
            }
        });
    }

    @Override
    public void receiveJoinedMsg(JoinedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof MenuScreen) {
                    update();
                }
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof MenuScreen menu){
                    menu.receiveRefresh(msg.getLobbies());
                }
            }
        });
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
                cycleToGame();
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
                a.show();
            }
        });
    }

    @Override
    public void receiveLobbyClosedMsg(LobbyClosedMessage msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(factory instanceof GameScreen game){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText(msg.getType()+" lobby was closed.");
                    a.show();
                    game.disconnect(true);
                }
            }
        });
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
    public boolean getIsDisconnecting() {
        return isDisconnecting.get();
    }

    @Override
    public void setIsDisconnecting(boolean b) {
        isDisconnecting.set(b);
    }


    @Override
    public void receiveConnectionErrorMsg(ConnectionErrorMessage msg) {
        if(!isDisconnecting.get()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                /*mainStage.setTitle("ERRORE");
                TilePane r = new TilePane();
                r.setAlignment(Pos.CENTER);
                Text t = new Text("ERRORE DI CONNESSIONE: SERVER IRRAGGIUNGIBILE");
                t.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
                r.getChildren().add(t);
                mainStage.setScene(new Scene(r));
                mainStage.show();*/

                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("CONNECTION ERROR!");
                    a.show();
                }
            });
        }
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
