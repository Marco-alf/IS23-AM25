package it.polimi.ingsw.network.messages.clientMessages;

public class ChatMessage extends ClientMessage{
    @Override
    public String getType() {
        return "ChatMessage";
    }
    private String sender;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
