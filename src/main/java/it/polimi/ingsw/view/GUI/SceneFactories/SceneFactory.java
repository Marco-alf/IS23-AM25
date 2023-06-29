package it.polimi.ingsw.view.GUI.SceneFactories;

import javafx.scene.Scene;

public interface SceneFactory {
    public SceneFactory next();
    public Scene getScene();
}
