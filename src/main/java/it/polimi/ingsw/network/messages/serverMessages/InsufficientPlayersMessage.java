package it.polimi.ingsw.network.messages.serverMessages;

/**
 * InsufficientPlayersMessage is a message that the server send to notify that the there are not enough player to continue
 * the game
 */
public class InsufficientPlayersMessage extends ServerMessage{
    /**
     * constructor for IllegalPlayerNameMessage objects.
     * It sets the type to "IllegalPlayerNameMessage"
     */
    public InsufficientPlayersMessage(){
        this.type = "InsufficientPlayersMessage";
    }
}
