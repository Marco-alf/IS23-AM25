package it.polimi.ingsw.network.messages.serverMessages;

/**
 * Represents a message that the server sends to comunicate that the selected lobby is already full
 */
public class FullLobbyMessage extends ServerMessage{
    /**
     * Constructor of a FullLobbyMessage object.
     * It sets the type to "FullLobbyMessage"
     */
    public FullLobbyMessage(){
        this.type = "FullLobbyMessage";
    }
}
