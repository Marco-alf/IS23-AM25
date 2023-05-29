package it.polimi.ingsw.network.messages.serverMessages;

public class PrivateChatUpdateMessage extends ChatUpdateMessage {
    private String receiver;
    public String getType() {
        return "PrivateChatUpdateMessage";
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
