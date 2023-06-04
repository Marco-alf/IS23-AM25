package it.polimi.ingsw.network.messages.serverMessages;

/**
 * Represents a message indicating that a lobby has been created.
 * Extends the ServerMessage class.
 */
public class CreatedLobbyMessage extends ServerMessage {
    /**
     * name of the creator of the lobby
     */
    private String name;
    /**
     * name of the lobby
     */
    private String lobbyName;

    /**
     * Constructs a new CreatedLobbyMessage object.
     * Sets the message type to "CreatedLobbyMessage".
     */
    public CreatedLobbyMessage() {
        this.type = "CreatedLobbyMessage";
    }

    /**
     * getter for the lobbyName attribute
     * @return the name associated with the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getter for the creator's name
     * @return the name associated with the lobby creator
     */
    public String getName() {
        return name;
    }

    /**
     * setter for lobbyName
     * @param lobbyName the name to set for the lobby
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * setter for the name attribute
     * @param name the name to set for the lobby creator
     */
    public void setName(String name) {
        this.name = name;
    }
}

