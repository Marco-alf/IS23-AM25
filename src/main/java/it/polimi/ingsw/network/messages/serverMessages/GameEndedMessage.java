package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;

/**
 * Represents a message that the server sends to comunicate to the player that the game has ended.
 * All the useful information about the ended game are shared with this message
 */
public class GameEndedMessage extends ServerMessage{
    /**
     * gameInfo has all the useful information about the just ended game
     */
    private FinalGameInfo gameInfo;

    /**
     * constructor for GameEndedMessage objects
     * It set the message type to "GameEndedMessage"
     */
    public GameEndedMessage(){
        this.type = "GameEndedMessage";
    }

    /**
     * setter for gameInfo attribute
     * @param gameInfo is the new FinalGameInfo to set
     */
    public void setGameInfo(FinalGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * getter for gameInfo attribute
     * @return the information about the just ended game
     */
    public FinalGameInfo getGameInfo() {
        return gameInfo;
    }

}
