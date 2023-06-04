package it.polimi.ingsw.network.messages.serverMessages;

/**
 * LobbyNotCreatedMessage is a message that the server send to notify that the creation of a lobby failed
 */
public class LobbyNotCreatedMessage extends ServerMessage {
    /**
     * constructor for LobbyNotCreatedMessage objects.
     * It sets the type to "LobbyNotCreatedMessage"
     */
    public LobbyNotCreatedMessage(){
        this.type = "LobbyNotCreatedMessage";
    }
}
