package it.polimi.ingsw.network.messages.serverMessages;

public class ChatUpdateMessage extends ServerMessage{
    @Override
    public String getType() {
        return "ChatUpdateMessage";
    }
    private String content;
    private String sender;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
