package it.polimi.ingsw.network.messages.serverMessages;

public class NameTakenMessage extends ServerMessage{
    @Override
    public String getType() {
        return "NameTakenMessage";
    }
}
