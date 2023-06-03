package it.polimi.ingsw.network.messages.clientMessages;

/**
 * CreateLobbyMessage is the class that represents a message that a client sends to the server in order to create a lobby
 */
public class CreateLobbyMessage extends ClientMessage {
    /**
     * String with the name of the creator of the lobby
     */
    private String lobbyCreator;
    /**
     * String with the name of the created lobby
     */
    private String lobbyName;
    /**
     * number of the player required by a game in this lobby
     */
    private int playerNumber;

    /**
     * constructor of CreateLobbyMessage
     */
    public CreateLobbyMessage () {
        this.type = "CreateLobbyMessage";
    }

    /**
     * setter for lobbyCreator
     * @param lobbyCreator is the name of the lobby creator
     */
    public void setLobbyCreator (String lobbyCreator) {
        this.lobbyCreator = lobbyCreator;
    }

    /**
     * setter for the playerNumber
     * @param playerNumber is the number of player required to play in this lobby
     */
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * setter for the lobbyName
     * @param lobbyName is the new name of the lobby
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * getter for playerNumber
     * @return the number of player required to play in this lobby
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * getter for lobbyCreator
     * @return the name of the lobby creator
     */
    public String getLobbyCreator() {
        return lobbyCreator;
    }

    /**
     * getter for lobbyName
     * @return the name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
