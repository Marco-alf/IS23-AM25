package it.polimi.ingsw.network.messages.connectionMessages;

public class Ping extends ConnectionMessage {
    @Override
    public String getType() {
        return "Ping";
    }
}
