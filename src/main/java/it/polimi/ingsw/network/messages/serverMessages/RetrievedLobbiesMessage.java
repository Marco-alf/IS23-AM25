package it.polimi.ingsw.network.messages.serverMessages;

import java.util.ArrayList;
import java.util.List;

public class RetrievedLobbiesMessage extends ServerMessage{
    @Override
    public String getType() {
        return "RetrievedLobbiesMessage";
    }
    private List<String> lobbies = new ArrayList<>();

    public void setLobbies(List<String> lobbies) {
        this.lobbies = lobbies;
    }

    public List<String> getLobbies() {
        return lobbies;
    }
}
