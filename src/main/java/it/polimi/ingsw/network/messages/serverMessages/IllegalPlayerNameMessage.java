package it.polimi.ingsw.network.messages.serverMessages;

/**
 * IllegalPlayerNameMessage is a message that the server send to notify that the selected player name is not valid
 */
public class IllegalPlayerNameMessage extends ServerMessage {
    /**
     * constructor for IllegalPlayerNameMessage objects.
     * It sets the type to "IllegalPlayerNameMessage"
     */
    public IllegalPlayerNameMessage(){
        this.type = "IllegalPlayerNameMessage";
    }
}
