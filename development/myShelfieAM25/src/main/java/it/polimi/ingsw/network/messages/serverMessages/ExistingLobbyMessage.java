package it.polimi.ingsw.network.messages.serverMessages;

public class ExistingLobbyMessage extends ServerMessage {
    @Override
    public String getType() {
        return "ExistingLobbyMessage";
    }
}
