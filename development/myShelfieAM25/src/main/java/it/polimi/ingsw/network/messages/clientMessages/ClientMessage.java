package it.polimi.ingsw.network.messages.clientMessages;


import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.server.RMIClientInterface;

import java.io.Serializable;

public class ClientMessage extends Message implements Serializable {
    static final long serialVersionUID = 1L;

    public String getType() {
        return "ClientMessage";
    }
    protected RMIClientInterface rmiClient;

    public void setRmiClient(RMIClientInterface rmiClient) {
        this.rmiClient = rmiClient;
    }

    public RMIClientInterface getRmiClient() {
        return rmiClient;
    }
}
