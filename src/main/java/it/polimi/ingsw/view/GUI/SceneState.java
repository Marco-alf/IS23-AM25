package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.GenericClient;
import it.polimi.ingsw.view.GUI.SceneFactories.SceneFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Interfaces implemented by GraphicalUI used by SceneFactories to call changes to current factory instance.
 * */
public interface SceneState {
    /**
     * AtomicBoolean used to prevent false alerts over dying client connections
     * */
    public AtomicBoolean isDisconnecting = new AtomicBoolean(false);
    /**
     * getter for isDisconnecting
     * @return isDisconnecting
     * */
    public boolean getIsDisconnecting();

    /**
     * setter for isDisconnecting
     * @param b new boolean for isDisconnecting
     * */
    public void setIsDisconnecting(boolean b);

    /**
     * getter for generic abstract instance of a client
     * @return client generalized as GenericClient
     * */
    public GenericClient getClient();

    /**
     * setter for client: used to change connection from a scene
     * @param client new client
     * */
    public void setClient(GenericClient client);
    /**
     * Method to cycle to next scenefactory, invoked from scene or SceneState itself.
     * This method will call the next() method of the current instance of SceneFactory and will substitute the current
     * factory with the result of the previous call of next(); then it will set the stage current scene with the result
     * of the getter for the scene of the new factory
     * */
    public void update();
    /**
     * Method to force the update to a certain Screen, SceneFactory is chosen
     * @param factory new factory
     * */
    public void forceUpdate(SceneFactory factory);
}
