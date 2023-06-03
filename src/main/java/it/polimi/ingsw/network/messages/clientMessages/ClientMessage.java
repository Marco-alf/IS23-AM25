package it.polimi.ingsw.network.messages.clientMessages;


import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.server.RMIClientInterface;

import java.io.Serializable;

/**
 * ClientMessage is the class that represents the generic message that a client can send
 */
public class ClientMessage extends Message implements Serializable {
    /**
     * serialVersionUID is the version used by the serialization/deserialization protocol to identify ClientMessage class
     */
    static final long serialVersionUID = 1L;

    /**
     * constructor for a ClientMessage
     */
    public ClientMessage() {
        this.type = "ClientMessage";
    }

    /**
     * Interface of the rmiClient that sent the message
     */
    protected RMIClientInterface rmiClient;

    /**
     * setter for the rmiClient that sent the message
     * @param rmiClient is the RMIClientInterface that sent the message
     */
    public void setRmiClient(RMIClientInterface rmiClient) {
        this.rmiClient = rmiClient;
    }

    /**
     * getter for the rmiClient that sent the message
     * @return the RMIClientInterface of the sender of the message
     */
    public RMIClientInterface getRmiClient() {
        return rmiClient;
    }
}
