package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

public class InitialGameInfo extends GameInfo{
    private final String commonGoal1;
    private final String commonGoal2;


    public InitialGameInfo(Game game) {
        super(game);
        this.commonGoal1 = game.getCommonGoals().get(0);
        this.commonGoal2 = game.getCommonGoals().get(1);
    }

    public String getCommonGoal1() {
        return commonGoal1;
    }

    public String getCommonGoal2() {
        return commonGoal2;
    }
}
