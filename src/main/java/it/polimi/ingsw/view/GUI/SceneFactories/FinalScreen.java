package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.model.data.FinalGameInfo;
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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SceneFactory for the final screen, will generate the scoreboard and return the user to the starting screen when asked.
 * */
public class FinalScreen extends SceneHandler implements SceneFactory{
    private final Background back = new Background(new BackgroundImage(new Image("17_MyShelfie_BGA/misc/base_pagina2.jpg"),
            null,
            null,
            BackgroundPosition.CENTER,
            new BackgroundSize(100,100, true, true, true, false)));

    /**
     * Constructor for finalGameScreen, calling super().
     * */
    FinalScreen(SceneState state, Rectangle2D screen, ViewInterface view) {
        super(state, screen, view);
        AnchorPane anchor = new AnchorPane();
        scene = new Scene(anchor);
    }

    /**
     * Method to set up the final scoreboard, this receives the points, calculates the ranking and then displays the
     * results.
     * @param info contains all info needed to calculate each player's points
     * */
    public void setInfo(FinalGameInfo info){
        StackPane stack = new StackPane();
        stack.setBackground(back);

        List<String> names = new ArrayList<>(info.getOnlinePlayers());
        Map<String, Integer> pointsMap = new HashMap<>();
        for (String name : names) {
            pointsMap.put( name, info.getFinalPoints().get(name).stream().mapToInt(Integer::intValue).sum() );
        }
        /*
        List<Integer> points = names.stream()
                .map( (String x) -> info.getFinalPoints().get(x).stream().mapToInt(Integer::intValue).sum() )
                .toList();
        */
        int [] place = new int[]{1,1,1,1};
        for (int i = 0; i < names.size(); i++) {
            for (String name : names) {
                if(pointsMap.get(names.get(i)) < pointsMap.get(name) ){
                    place[i]++;
                }
            }
        }

        TilePane r = new TilePane(Orientation.VERTICAL);

        Text[] results = new Text[4];
        for (int i = 0; i < names.size(); i++) {
            String s = "";
            switch (place[i]){
                case 1 -> s+= "1st ";
                case 2 -> s+= "2nd ";
                case 3 -> s+= "3rd ";
                case 4 -> s+= "4th ";
                default -> s+= "####  ";
            };
            results[i] = new Text(s + names.get(i) + " with "+ pointsMap.get(names.get(i)) + " points");
            results[i].setFont(Font.font("Arial", FontWeight.NORMAL, 20));
            results[i].setFill(Color.WHITE);
            r.getChildren().add(results[i]);
        }

        Button back = new Button("back to play screen");
        back.setOnMouseReleased(actionEvent -> {
            QuitMessage clientMessage = new QuitMessage();
            if (state.getClient() instanceof RMIClient) {
                clientMessage.setRmiClient((RMIClient) state.getClient());
            }
            state.getClient().sendMsgToServer(clientMessage);

            state.getClient().disconnect(false);
            state.forceUpdate(new PlayScreen(state, screen, view));
        });
        r.getChildren().add(back);
        r.setAlignment(Pos.CENTER);
        adjustScaling(r);

        stack.getChildren().add(r);
        scene.setRoot(stack);
    }

    /**
     * Method called the user has finished looking at the final scoreboard
     * @return new PlayScreen instance, returning to starting screen.
     * */
    @Override
    public SceneFactory next() {
        return new PlayScreen(state, screen, view);
    }
}
