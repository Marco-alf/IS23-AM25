package it.polimi.ingsw.network.messages.connectionMessages;

/**
 * message used by both client and server to ping each other and check the connection status
 */
public class Ping extends ConnectionMessage {
    /**
     * override of the getType() method
     * @return the string "Ping" corresponding to the current message type
     */
    @Override
    public String getType() {
        return "Ping";
    }
}
