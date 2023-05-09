package it.polimi.ingsw.network.messages.serverMessages;

public class UpdatedPlayerMessage extends ServerMessage{
    private String updatedPlayer;

    @Override
    public String getType() {
        return "UpdatedPlayerMessage";
    }

    public String getUpdatedPlayer() {
        return updatedPlayer;
    }

    public void setUpdatedPlayer(String updatedPlayer) {
        this.updatedPlayer = updatedPlayer;
    }
}
