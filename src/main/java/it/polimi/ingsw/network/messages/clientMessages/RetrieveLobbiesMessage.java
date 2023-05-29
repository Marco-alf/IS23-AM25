package it.polimi.ingsw.network.messages.clientMessages;

public class RetrieveLobbiesMessage extends ClientMessage{

    @Override
    public String getType() {
        return "RetrieveLobbiesMessage";
    }
}
