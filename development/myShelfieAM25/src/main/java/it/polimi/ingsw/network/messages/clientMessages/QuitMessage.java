package it.polimi.ingsw.network.messages.clientMessages;

public class QuitMessage extends ClientMessage{
    @Override
    public String getType() {
        return "QuitMessage";
    }
}
