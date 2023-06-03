package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitialGameInfo extends GameInfo{
    private final String commonGoal1;
    private final String commonGoal2;

    private final Map<String, TilesType[][]> shelves;
    private final Map<String, PersonalGoal> personalGoals;
    private final Map<String, int[]> commonPoints;

    public InitialGameInfo(Game game) {
        super(game);
        this.commonGoal1 = game.getCommonGoals().get(0);
        this.commonGoal2 = game.getCommonGoals().get(1);
        this.shelves = game.getShelves();
        this.personalGoals = game.getPersonalGoals();
        this.commonPoints  = game.getCommonGoalPoints();
    }

    public Map<String, TilesType[][]> getShelves() {
        return shelves;
    }

    public String getCommonGoal1() {
        return commonGoal1;
    }

    public String getCommonGoal2() {
        return commonGoal2;
    }
    public int getCommonPoints(String player, int goalNumber){
        return commonPoints.get(player)[goalNumber];
    }

    public Map<String, PersonalGoal> getPersonalGoals() {
        return personalGoals;
    }
}
