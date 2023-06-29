package it.polimi.ingsw.view.GUI.SceneFactories;

import it.polimi.ingsw.view.GUI.SceneState;
import it.polimi.ingsw.view.ViewInterface;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;

/**
 * SceneHandler gives to a SceneFactory the skeleton to handle scene production, includes a
 * getter for scene used by the view, a method to adjust scaling and a base constructor.
 * Each different subclass of this abstract class will implement the constructor calling super first, then launching a
 * private method to set the scene graph and move
 * */
public abstract class SceneHandler implements SceneFactory{
    protected Scene scene;
    protected Rectangle2D screen;
    protected SceneState state;
    protected final ViewInterface view;
    /**
     * Base constructor for a Scenehandler
     * @param view reference to ViewInterface used to make message exchange binding between view and client network
     * @param screen rectangle with sizes of the screen
     * @param state reference to SceneState to call factory substitution
     * */
    SceneHandler(SceneState state, Rectangle2D screen, ViewInterface view){
        this.state = state;
        this.screen = screen;
        this.view = view;
    }

    /**
     * Getter for scene attribute
     * @return scene
     * */
    @Override
    public Scene getScene() {
        return scene;
    }


    /**
     * adjusts node scaling based off of screen size
     * @param p node to have scaling changed
     * */
    protected void adjustScaling(Parent p){
        Scale scala = new Scale();
        scala.setPivotX(screen.getWidth()/2);
        scala.setPivotY(screen.getHeight()/2);
        double xscaling = screen.getWidth()/1920 * 1.6;
        double yscaling = screen.getHeight()/1080 * 1.6;
        scala.setX(xscaling);
        scala.setY(yscaling);
        p.getTransforms().clear();
        p.getTransforms().add(scala);
    }
}
