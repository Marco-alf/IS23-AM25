package it.polimi.ingsw.network.messages.clientMessages;

/**
 * JoinMessage is the class that represents the message that a client sends to the server in order to join a lobby
 */
public class JoinMessage extends ClientMessage{
    /**
     * name of the player that want to join the lobby
     */
    private String name;
    /**
     * name of the lobby to join
     */
    private String lobbyName;

    /**
     * constructor for JoinMessage
     */
    public JoinMessage(){
        this.type = "JoinMessage";
    }

    /**
     * setter for lobbyName
     * @param lobbyName is the lobby where the player want to join
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * setter for name
     * @param name is the name of the player that is requesting to join the lobby
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for lobbyName
     * @return the name of the lobby where the player want to join
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getter for name
     * @return the name of the player requesting the access
     */
    public String getName() {
        return name;
    }
}
