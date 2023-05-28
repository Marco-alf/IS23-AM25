package it.polimi.ingsw.network.messages.serverMessages;

public class IllegalPlayerNameMessage extends ServerMessage {
    @Override
    public String getType() {
        return "IllegalPlayerNameMessage";
    }
}
