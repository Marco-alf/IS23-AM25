package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;

public abstract class SceneHandler implements SceneFactory{
    Scene scene;
    Rectangle2D screen;
    SceneState state;
    ViewInterface view;
    SceneHandler(SceneState state, Rectangle2D screen, ViewInterface view){
        this.state = state;
        this.screen = screen;
        this.view = view;
    }

    @Override
    public Scene getScene() {
        return scene;
    }


    public void adjustScaling(Parent p){
        Scale scala = new Scale();
        scala.setPivotX(screen.getWidth()/2);
        scala.setPivotY(screen.getHeight()/2);
        double xscaling = screen.getWidth()/1920 * 1.6;
        double yscaling = screen.getHeight()/1080 * 1.6;
        scala.setX(xscaling);
        scala.setY(yscaling);
        p.getTransforms().add(scala);
    }
}
