package it.polimi.ingsw.network.messages.serverMessages;

public class InvalidMoveMessage extends ServerMessage{
    @Override
    public String getType() {
        return "InvalidMoveMessage";
    }
}
