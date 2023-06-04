package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.InitialGameInfo;

/**
 * Represents a message that the server sends to comunicate to the player that the game has started.
 * All the useful information about the newly created game are shared with this message
 */
public class GameCreatedMessage extends ServerMessage{
    /**
     * gameInfo contains the information about the game
     */
    private InitialGameInfo gameInfo;
    /**
     * Constructor of a GameCreatedMessage object.
     * It sets the type to "GameCreatedMessage"
     */
    public GameCreatedMessage(){
        this.type = "GameCreatedMessage";
    }

    /**
     * setter for gameInfo
     * @param gameInfo is the new gameInfo to set
     */
    public void setGameInfo(InitialGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * getter for gameInfo
     * @return the information about the game
     */
    public InitialGameInfo getGameInfo() {
        return gameInfo;
    }

}
