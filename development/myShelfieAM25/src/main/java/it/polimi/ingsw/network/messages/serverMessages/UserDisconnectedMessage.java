package it.polimi.ingsw.network.messages.serverMessages;

public class UserDisconnectedMessage extends ServerMessage{
    private String user;
    private String currentPlayer;
    @Override
    public String getType() {
        return "UserDisconnectedMessage";
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getUser() {
        return user;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
