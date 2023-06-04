package it.polimi.ingsw.network.messages.serverMessages;

/**
 * NameTakenMessage is a message that the server send to notify that the requested name was already taken in the lobby
 */
public class NameTakenMessage extends ServerMessage{
    /**
     * constructor for NameTakenMessage objects.
     * It sets the type to "NameTakenMessage"
     */
    public NameTakenMessage(){
        this.type = "NameTakenMessage";
    }
}
