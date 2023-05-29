package it.polimi.ingsw.network.messages.serverMessages;

public class InvalidCommandMessage extends ServerMessage{
    @Override
    public String getType() {
        return "InvalidCommandMessage";
    }
}
