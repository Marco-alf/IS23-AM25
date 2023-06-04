package it.polimi.ingsw.network.messages.serverMessages;

/**
 * JoinedMessage is a message that the server send to notify that a player identified by "name" have joined a lobby
 */
public class JoinedMessage extends ServerMessage{
    /**
     * Is the name of the player that joined the lobby
     */
    private String name;
    /**
     * name of the lobby that the player have joined
     */
    private String lobbyName;

    /**
     * constructor for JoinedMessage objects.
     * It sets the type to "JoinedMessage"
     */
    public JoinedMessage(){
        this.type = "JoinedMessage";
    }

    /**
     * getter for lobbyName
     * @return the name of the lobby that the player have joined
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getter for name
     * @return the name of the player that have joined
     */
    public String getName() {
        return name;
    }

    /**
     * setter for lobbyName
     * @param lobbyName is the name of the lobby to set
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * setter for name
     * @param name is the name of the player that have just joined
     */
    public void setName(String name) {
        this.name = name;
    }
}
