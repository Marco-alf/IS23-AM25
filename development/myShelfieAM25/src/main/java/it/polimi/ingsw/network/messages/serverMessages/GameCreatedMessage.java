package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.InitialGameInfo;

public class GameCreatedMessage extends ServerMessage{
    private InitialGameInfo gameInfo;

    public void setGameInfo(InitialGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public InitialGameInfo getGameInfo() {
        return gameInfo;
    }

    @Override
    public String getType() {
        return "GameCreatedMessage";
    }
}
