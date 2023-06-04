package it.polimi.ingsw.network.messages.serverMessages;

/**
 * PrivateChatUpdateMessage is a message that contains the information about a private message.
 * This class extends ChatUpdateMessage by adding a receiver to the message
 */
public class PrivateChatUpdateMessage extends ChatUpdateMessage {
    /**
     * receiver is the name of the receiver
     */
    private String receiver;
    /**
     * constructor for PrivateChatUpdateMessage objects.
     * It sets the type to "PrivateChatUpdateMessage"
     */
    public PrivateChatUpdateMessage(){
        this.type = "PrivateChatUpdateMessage";
    }

    /**
     * getter for the receiver name
     * @return the name of the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * setter for receiver
     * @param receiver is the name of the receiver of the message
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
