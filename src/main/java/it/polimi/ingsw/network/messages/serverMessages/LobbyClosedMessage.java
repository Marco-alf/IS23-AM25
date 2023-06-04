package it.polimi.ingsw.network.messages.serverMessages;


/**
 * LobbyClosedMessage is a message that the server send to notify that the a lobby have been closed
 */
public class LobbyClosedMessage extends ServerMessage{
    /**
     * constructor for LobbyClosedMessage objects.
     * It sets the type to "LobbyClosedMessage"
     */
    public LobbyClosedMessage(){
        this.type = "LobbyClosedMessage";
    }
}
