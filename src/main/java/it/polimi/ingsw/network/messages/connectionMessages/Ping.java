package it.polimi.ingsw.network.messages.connectionMessages;

/**
 * message used by both client and server to ping each other and check the connection status
 */
public class Ping extends ConnectionMessage {
    /**
     * constructor for the Ping class
     */
    public Ping(){
        this.type = "Ping";
    }
}
