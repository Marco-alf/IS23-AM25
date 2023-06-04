package it.polimi.ingsw.network.messages.serverMessages;

/**
 * InvalidCommandMessage is a message that the server send to notify that the requested command is not valid
 */
public class InvalidCommandMessage extends ServerMessage{
    /**
     * constructor for InvalidCommandMessage objects.
     * It sets the type to "InvalidCommandMessage"
     */
    public InvalidCommandMessage(){
        this.type = "InvalidCommandMessage";
    }
}
