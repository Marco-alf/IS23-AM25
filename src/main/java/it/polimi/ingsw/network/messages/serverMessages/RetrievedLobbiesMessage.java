package it.polimi.ingsw.network.messages.serverMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * RetrievedLobbiesMessage is a message that the server send to the player to tell them all the available lobbies
 */
public class RetrievedLobbiesMessage extends ServerMessage{
    /**
     * lobbies is a list with all the available lobbies
     */
    private List<String> lobbies = new ArrayList<>();
    /**
     * constructor for RetrievedLobbiesMessage objects.
     * It sets the type to "RetrievedLobbiesMessage"
     */
    public RetrievedLobbiesMessage(){
        this.type = "RetrievedLobbiesMessage";
    }

    /**
     * setter for lobbies
     * @param lobbies is the list of available lobbies to comunicate
     */
    public void setLobbies(List<String> lobbies) {
        this.lobbies = lobbies;
    }

    /**
     * getter for the list of lobbies
     * @return the list of available lobbies
     */
    public List<String> getLobbies() {
        return lobbies;
    }
}
