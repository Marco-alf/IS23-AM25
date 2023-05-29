package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.network.messages.Message;

import java.io.Serializable;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 1L;
    public String getType() {
        return "ServerMessage";
    }

}
