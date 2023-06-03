package it.polimi.ingsw.network.messages.clientMessages;

/**
 * PrivateChatMessage is the subclass of ChatMessage used to represent a private message that a client sends
 */
public class PrivateChatMessage extends ChatMessage {
    /**
     * String with the name of the receiver
     */
    private final String receiver;

    /**
     * constructor of the PrivateChatMessageClass
     * @param receiver is the name of the receiver of the message
     */
    public PrivateChatMessage(String receiver){
        this.type = "PrivateChatMessage";
        this.receiver = receiver;
    }

    /**
     * getter for receiver
     * @return the name of the receiver of the message
     */
    public String getReceiver() {
        return receiver;
    }
}
