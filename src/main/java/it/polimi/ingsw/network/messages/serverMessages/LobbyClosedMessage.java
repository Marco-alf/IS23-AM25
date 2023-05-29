package it.polimi.ingsw.network.messages.serverMessages;

public class LobbyClosedMessage extends ServerMessage{
    @Override
    public String getType() {
        return "LobbyClosedMessage";
    }
}
