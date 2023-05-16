package it.polimi.ingsw.view.GUI.SceneFactories;

import javafx.scene.Scene;

public interface SceneFactory {
    SceneFactory next();
    Scene getScene();
}
