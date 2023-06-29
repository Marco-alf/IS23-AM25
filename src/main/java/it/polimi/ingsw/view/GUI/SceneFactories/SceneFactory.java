package it.polimi.ingsw.view.GUI.SceneFactories;

import javafx.scene.Scene;

/**
 * Interface models abstract factory with product scene and next() method to build next factory
 * */
public interface SceneFactory {
    /**
     * abstract factory method to build next factory
     * @return new factory, build following a straight line of succession of factories
     * */
    public SceneFactory next();
    /**
     * getter for scene
     * @return current Scene object
     * */
    public Scene getScene();
}
