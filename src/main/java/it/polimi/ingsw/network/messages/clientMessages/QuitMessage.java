package it.polimi.ingsw.network.messages.clientMessages;

/**
 * QuitMessage is the class that represents a message used to notify the server that the client wants to leave his current
 * lobby
 */
public class QuitMessage extends ClientMessage{
    /**
     * constructor for the QuitMessage class
     */
    public QuitMessage(){
        this.type = "QuitMessage";
    }
}
