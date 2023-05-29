package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.InitialGameInfo;

public class GameEndedMessage extends ServerMessage{
    private FinalGameInfo gameInfo;

    public void setGameInfo(FinalGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public FinalGameInfo getGameInfo() {
        return gameInfo;
    }

    @Override
    public String getType() {
        return "GameEndedMessage";
    }
}
