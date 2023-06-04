package it.polimi.ingsw.network.messages.serverMessages;

/**
 * ChatUpdateMessage is the class used by the server to notify clients of new messages in the chat
 */
public class ChatUpdateMessage extends ServerMessage {
    /**
     * content is the body of the message
     */
    private String content;
    /**
     * sender is the name of the player who sent the message
     */
    private String sender;
    /**
     * timestamp is the timestamp of when the message has been sent
     */
    private String timestamp;

    /**
     * Constructs a new ChatUpdateMessage object.
     */
    public ChatUpdateMessage() {
        this.type = "ChatUpdateMessage";
    }

    /**
     * Getter for the timestamp of the message.
     * @return the timestamp of the message
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Setter for the timestamp of the message.
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for the sender of the message.
     * @return the sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setter for the sender of the message.
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Getter for the content of the message.
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of the message.
     * @param content the new content of the message
     */
    public void setContent(String content) {
        this.content = content;
    }
}
