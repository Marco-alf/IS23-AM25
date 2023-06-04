package it.polimi.ingsw.network.messages.serverMessages;

/**
 * NotExistingLobbyMessage is a message that the server send to notify that the requested lobby does not exists
 */
public class NotExistingLobbyMessage extends ServerMessage{
    /**
     * constructor for NotExistingLobbyMessage objects.
     * It sets the type to "NotExistingLobbyMessage"
     */
    public NotExistingLobbyMessage(){
        this.type = "NotExistingLobbyMessage";
    }
}
