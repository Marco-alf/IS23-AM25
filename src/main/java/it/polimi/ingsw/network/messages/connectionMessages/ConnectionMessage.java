package it.polimi.ingsw.network.messages.connectionMessages;

import it.polimi.ingsw.network.messages.Message;

/**
 * class of messages used to manage the connection
 */
public class ConnectionMessage extends Message {
    /**
     * constructor for the ConnectionMessage class
     */
    public ConnectionMessage(){
        this.type = "ConnectionMessage";
    }
}
