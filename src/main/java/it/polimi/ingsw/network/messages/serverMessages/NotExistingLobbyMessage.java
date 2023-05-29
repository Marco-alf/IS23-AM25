package it.polimi.ingsw.network.messages.serverMessages;

public class NotExistingLobbyMessage extends ServerMessage{
    @Override
    public String getType() {
        return "NotExistingLobbyMessage";
    }
}
