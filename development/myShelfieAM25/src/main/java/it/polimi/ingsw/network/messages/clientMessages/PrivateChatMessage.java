package it.polimi.ingsw.network.messages.clientMessages;

public class PrivateChatMessage extends ChatMessage {
    private final String receiver;
    public String getType() {
        return "PrivateChatMessage";
    }

    public PrivateChatMessage(String receiver){
        super();
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }
}
