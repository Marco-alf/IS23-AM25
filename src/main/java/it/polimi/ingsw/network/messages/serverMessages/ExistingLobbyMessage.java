package it.polimi.ingsw.network.messages.serverMessages;

/**
 * Represents a message that the server sends to comunicate the currently available lobbies to the client
 */
public class ExistingLobbyMessage extends ServerMessage {
    /**
     * Constructor for an ExistingLobbyMessage object. It sets it's type to "ExistingLobbyMessage"
     */
    public ExistingLobbyMessage(){
        this.type = "ExistingLobbyMessage";
    }
}
