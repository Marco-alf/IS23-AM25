package it.polimi.ingsw.network.messages.serverMessages;

/**
 * UserDisconnectedMessage is a message that a client
 */
public class UserDisconnectedMessage extends ServerMessage{
    /**
     * name of the user that have been disconnected
     */
    private String user;
    /**
     * name of the new current player
     */
    private String currentPlayer;
    /**
     * constructor for UserDisconnectedMessage objects.
     * It sets the type to "UserDisconnectedMessage"
     */
    public UserDisconnectedMessage(){
        this.type = "UserDisconnectedMessage";
    }

    /**
     * setter for the name of the user that has been disconnected
     * @param user is the name of the disconnected player
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * setter for currentPlayer
     * @param currentPlayer is the current player updated after the disconnection
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * getter for user
     * @return return the name of the disconnected user
     */
    public String getUser() {
        return user;
    }

    /**
     * getter for currentPlayer
     * @return the name of the new current player
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
