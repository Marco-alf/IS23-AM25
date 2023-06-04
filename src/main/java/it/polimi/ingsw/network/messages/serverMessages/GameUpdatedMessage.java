package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.GameInfo;

/**
 * GameUpdatedMessage is a message that the server send to updates the clients about the game
 */
public class GameUpdatedMessage extends ServerMessage{
    /**
     * gameInfo represents the new information about the game
     */
    private GameInfo gameInfo;

    /**
     * constructor for GameUpdatedMessage objects.
     * It sets the type to "GameUpdatedMessage"
     */
    public GameUpdatedMessage(){
        this.type = "GameUpdatedMessage";
    }

    /**
     * setter for gameInfo
     * @param gameInfo are the information about the game
     */
    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * getter for gameInfo
     * @return the information about the game
     */
    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
