package it.polimi.ingsw.network.messages.clientMessages;

public class CreateLobbyMessage extends ClientMessage {
    private String lobbyCreator;
    private String lobbyName;
    private int playerNumber;

    public CreateLobbyMessage () {

    }
    public void setLobbyCreator (String lobbyCreator) {
        this.lobbyCreator = lobbyCreator;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getLobbyCreator() {
        return lobbyCreator;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    @Override
    public String getType() {
        return "CreateLobbyMessage";
    }
}
