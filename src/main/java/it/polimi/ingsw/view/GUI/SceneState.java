package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.view.GUI.SceneFactories.SceneFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public interface SceneState {

    public AtomicBoolean isDisconnecting = new AtomicBoolean(false);
    public boolean getIsDisconnecting();
    public void setIsDisconnecting(boolean b);
    public GenericClient getClient();
    public void setClient(GenericClient client);
    public void update();
    public void forceUpdate(SceneFactory factory);
}
