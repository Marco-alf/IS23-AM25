package it.polimi.ingsw.network.messages.connectionMessages;

import it.polimi.ingsw.network.messages.Message;

/**
 * class of messages used to manage the connection
 */
public class ConnectionMessage extends Message {
    /**
     * override of the getType() method
     * @return the string "ConnectionMessage" corresponding to the current message type
     */
    public String getType() {
        return "ConnectionMessage";
    }
}
