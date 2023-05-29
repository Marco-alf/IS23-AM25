package it.polimi.ingsw.network.messages.serverMessages;

public class LobbyNotCreatedMessage extends ServerMessage {
    @Override
    public String getType() {
        return "LobbyNotExistingMessage";
    }
}
