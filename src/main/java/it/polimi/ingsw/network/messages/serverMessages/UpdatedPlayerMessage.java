package it.polimi.ingsw.network.messages.serverMessages;

/**
 * UpdatedPlayerMessage is a message that the server sends to notify that the current player have changed
 */
public class UpdatedPlayerMessage extends ServerMessage{
    /**
     * updatedPlayer is the name of the new current player
     */
    private String updatedPlayer;

    /**
     * constructor for UpdatedPlayerMessage objects.
     * It sets the type to "UpdatedPlayerMessage"
     */
    public UpdatedPlayerMessage(){
        this.type = "UpdatedPlayerMessage";
    }

    /**
     * getter for updatedPlayer
     * @return the new current player
     */
    public String getUpdatedPlayer() {
        return updatedPlayer;
    }

    /**
     * setter for updatePlayer
     * @param updatedPlayer is the new current player
     */
    public void setUpdatedPlayer(String updatedPlayer) {
        this.updatedPlayer = updatedPlayer;
    }
}
