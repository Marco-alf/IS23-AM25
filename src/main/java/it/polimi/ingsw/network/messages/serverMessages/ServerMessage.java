package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.network.messages.Message;

import java.io.Serializable;

/**
 * ServerMessage is a generic message sent by the server
 */
public class ServerMessage extends Message implements Serializable {
    /**
     * serialVersionUID is the version used by the serialization/deserialization protocol.
     * It identifies the class of the serialized object
     */
    static final long serialVersionUID = 1L;
    /**
     * constructor for ServerMessage objects.
     * It sets the type to "ServerMessage"
     */
    public ServerMessage(){
        this.type = "ServerMessage";
    }
}
