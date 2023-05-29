package it.polimi.ingsw.network.messages.serverMessages;

public class FullLobbyMessage extends ServerMessage{
    @Override
    public String getType() {
        return "FullLobbyMessage";
    }
}
