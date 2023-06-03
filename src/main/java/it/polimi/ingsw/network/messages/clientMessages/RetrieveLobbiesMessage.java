package it.polimi.ingsw.network.messages.clientMessages;

/**
 * RetrieveLobbiesMessage is the class that represents the messages that a client send to the server in order to ask for
 * the currently available lobbies
 */
public class RetrieveLobbiesMessage extends ClientMessage{
    /**
     * constructor of the RetrieveLobbiesMessage class
     */
    public RetrieveLobbiesMessage(){
        this.type = "RetrieveLobbiesMessage";
    }
}
