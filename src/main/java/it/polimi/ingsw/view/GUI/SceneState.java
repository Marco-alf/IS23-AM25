package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUI.SceneFactories.SceneFactory;

public interface SceneState {

    public void update();
    public void forceUpdate(SceneFactory factory);
}
