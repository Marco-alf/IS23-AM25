package it.polimi.ingsw.network.client;

import java.io.Serializable;

public abstract class GenericClient {
    protected boolean isInLobby = false;

    public void init() {}
    public void sendMsgToServer(Serializable message) {}

    public boolean getIsInLobbyStatus() {
        return isInLobby;
    }

}
