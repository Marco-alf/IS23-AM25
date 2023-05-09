package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.GameInfo;

public class GameUpdatedMessage extends ServerMessage{
    private GameInfo gameInfo;

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    @Override
    public String getType() {
        return "GameUpdatedMessage";
    }
}
