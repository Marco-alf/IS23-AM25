package it.polimi.ingsw.network.messages.serverMessages;

public class InsufficientPlayersMessage extends ServerMessage{
    @Override
    public String getType() {
        return "InsufficientPlayersMessage";
    }
}
