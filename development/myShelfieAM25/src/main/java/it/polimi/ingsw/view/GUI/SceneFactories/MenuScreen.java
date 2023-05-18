package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MenuScreen extends SceneHandler implements SceneFactory{

    ArrayList<String> lobbies;
    String selected;
    GenericClient client;

    public MenuScreen(SceneState state, Rectangle2D screen, ViewInterface view, GenericClient client){
        super(state, screen, view);
        this.client = client;

        lobbies = refresh();
        scene = new Scene(mainMenu());
    }
    @Override
    public SceneFactory next() {
        return new LobbyScreen(state, screen, view, client);
    }

    private Parent mainMenu(){
        GridPane r = new GridPane();

        Text title = new Text("MyShelfie Menu");
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        ListView<String> lobbylist= new ListView<>();
        lobbylist.getItems().addAll(lobbies);

        Button create = new Button("Create Game");
        create.setOnAction(actionEvent -> {
            scene.setRoot(createGame());
        });
        Button join = new Button("Join");
        join.setOnAction(actionEvent -> {
            selected = lobbylist.getSelectionModel().getSelectedItem();
            if(selected != null)
                scene.setRoot(joinGame());
        });
        Button refresh = new Button("⟳");
        refresh.setOnAction(actionEvent -> {
            refresh();
        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
            state.forceUpdate(new PlayScreen(state, screen, view));
        });

        r.add(title, 0,0);
        r.add(create, 0, 1);
        r.add(join, 1, 1);
        r.add(refresh, 2, 1);
        r.add(cancel, 1 ,2);
        GridPane.setValignment(cancel, VPos.TOP);
        GridPane.setRowIndex(lobbylist, 2);
        GridPane.setColumnIndex(lobbylist, 0);
        r.getChildren().addAll(lobbylist);

        r.setGridLinesVisible(false);
        r.setVgap(screen.getWidth()*0.005);
        r.setHgap(screen.getWidth()*0.005);


        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }

    private Parent joinGame(){
        TilePane r = new TilePane(Orientation.VERTICAL);

        Text title = new Text(selected);
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        TextField name = new TextField();
        name.setPromptText("Nickname");

        Button submit = new Button("Submit");
        submit.setOnAction(actionEvent -> {
            serverJoinGame(name.getText(), selected);
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
            state.forceUpdate(new MenuScreen(state, screen, view, client));
        });
        TilePane horizontal = new TilePane(Orientation.HORIZONTAL);
        horizontal.getChildren().addAll(submit, cancel);
        horizontal.setHgap(screen.getWidth()*0.005);
        horizontal.setAlignment(Pos.CENTER);

        r.getChildren().addAll(title, name, horizontal);
        r.setVgap(screen.getWidth()*0.005);
        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }

    private Parent createGame(){
        TilePane r = new TilePane(Orientation.VERTICAL);
        Text title = new Text("Create a game!");
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        TextField name = new TextField();
        name.setPromptText("Nickname");
        TextField lobbyName = new TextField();
        lobbyName.setPromptText("lobby name");

        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2,4);
        spinnerValueFactory.setValue(2);
        Spinner<Integer> size = new Spinner<>();
        size.setValueFactory(spinnerValueFactory);

        Label l = new Label("number of players");

        Button submit = new Button("Submit");
        submit.setOnAction(actionEvent -> {
            serverGameCreation(name.getText(), lobbyName.getText(), size.getValue());
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
            state.forceUpdate(new MenuScreen(state, screen, view, client));
        });
        TilePane horizontal = new TilePane(Orientation.HORIZONTAL);
        horizontal.setHgap(screen.getWidth()*0.005);
        horizontal.setAlignment(Pos.CENTER);
        horizontal.getChildren().addAll(submit, cancel);

        r.getChildren().addAll(title, name, lobbyName, l, size, horizontal);
        r.setVgap(screen.getWidth()*0.005);
        r.setAlignment(Pos.CENTER);

        adjustScaling(r);
        return r;
    }

    private ArrayList<String> refresh(){
        ArrayList<String> lobbies = new ArrayList<>();
        //asks server to send list of lobbies, now an example
        lobbies.add("Lobby1!");
        lobbies.add("Lobby2!");
        return lobbies;

    }

    private void serverGameCreation(String nickname, String lobbyname, int size){
        if(nickname==null && lobbyname==null || size<2 || size>4){
            return;
        }
        //code to ask the server with catch
        //scene.setRoot(creategame()) se fallisce
        serverJoinGame(nickname, lobbyname);
        //se va a buon fine
    }

    private void serverJoinGame(String nickname, String lobbyname){
        if(nickname==null || lobbyname==null ){
            return;
        }
        //code to ask the server with catch
        state.update();
    }

}
