package it.polimi.ingsw.network.messages.clientMessages;

/**
 * ChatMessage is the serializable class that represent a message that a client can send on the chat
 */
public class ChatMessage extends ClientMessage{
    /**
     * String representing the name of the sender
     */
    private String sender;
    /**
     * String representing the content of the message
     */
    private String content;

    /**
     * constructor of ChatMessage
     */
    public ChatMessage(){
        this.type = "ChatMessage";
    }

    /**
     * getter for content
     * @return content, String with the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * setter fot the content attribute
     * @param content is the String with the new content for the message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * getter for the sender of the message
     * @return the name of the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * setter for the name of the sender
     * @param sender is the name of the sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
}
