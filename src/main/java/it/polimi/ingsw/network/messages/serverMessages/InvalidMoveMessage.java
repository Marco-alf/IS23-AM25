package it.polimi.ingsw.network.messages.serverMessages;

/**
 * InvalidMoveMessage is a message that the server send to notify that the requested move is not allowed
 */
public class InvalidMoveMessage extends ServerMessage{
    /**
     * constructor for InvalidMoveMessage objects.
     * It sets the type to "InvalidMoveMessage"
     */
    public InvalidMoveMessage(){
        this.type = "InvalidMoveMessage";
    }
}
