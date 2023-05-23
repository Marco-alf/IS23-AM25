package it.polimi.ingsw.network.messages.serverMessages;

public class InvalidLobbyNameMessage extends ServerMessage {
    @Override
    public String getType() {
        return "InvalidLobbyNameMessage";
    }
}
