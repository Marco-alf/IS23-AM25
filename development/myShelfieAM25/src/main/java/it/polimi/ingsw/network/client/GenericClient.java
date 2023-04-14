package it.polimi.ingsw.network.client;

import java.io.Serializable;

/**
 * abstract class that represents a client, all client subclasses will have the following implemented
 * */
public abstract class GenericClient {
    /**
    * boolean is true if the client has joined a lobby and is currently inside one */
    protected boolean isInLobby = false;

    /**
     * initializes the connection to the game server, will disconnect if failed
     */
    public void init() {}

    /**
     * method used by the view to access to game features by sending messages.
     * the method will send a serializable message that will be interpreted by the server into the proper command
     * @param message is a serializable instance of ClientMessage
     */
    public void sendMsgToServer(Serializable message) {}


    /**
     * getter for isInLobbyStatus
     * @return isInLobbyStatus boolean*/
    public boolean getIsInLobbyStatus() {
        return isInLobby;
    }

    /**
     * signals the disconnection or failed connection to the view.
     * @param error true if the reason for disconnection is an error */
    public void disconnect(boolean error){}

}
