package it.polimi.ingsw.network.messages.serverMessages;

/**
 * InvalidLobbyNameMessage is a message that the server send to notify that the selected lobby name is not valid
 */
public class InvalidLobbyNameMessage extends ServerMessage {
    /**
     * constructor for InvalidLobbyNameMessage objects.
     * It sets the type to "InvalidLobbyNameMessage"
     */
    public InvalidLobbyNameMessage(){
        this.type = "InvalidLobbyNameMessage";
    }
}
