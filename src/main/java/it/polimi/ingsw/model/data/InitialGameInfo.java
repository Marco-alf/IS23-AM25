package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InitialGameInfo is a class of GameInfo that is used to represents all the information regarding the current game state
 * Is used every time a client connect to a game to initialize the view
 */
public class InitialGameInfo extends GameInfo{
    /**
     * commonGoal1 is a String that uniquely identify the first common goal
     */
    private final String commonGoal1;
    /**
     * commongoal2 is a String that uniquely identify the second common goal
     */
    private final String commonGoal2;
    /**
     * shelves is a map that associates each player to his shelf
     */
    private final Map<String, TilesType[][]> shelves;
    /**
     * personalGoals is a map that links each player to his personal goal
     */
    private final Map<String, PersonalGoal> personalGoals;
    /**
     * commonPoints is a map that links each player to its score regarding common goals
     */
    private final Map<String, int[]> commonPoints;

    /**
     * constructor for InitialGameInfo
     * @param game is the game from which we want to retrieve information
     */
    public InitialGameInfo(Game game) {
        super(game);
        this.commonGoal1 = game.getCommonGoals().get(0);
        this.commonGoal2 = game.getCommonGoals().get(1);
        this.shelves = game.getShelves();
        this.personalGoals = game.getPersonalGoals();
        this.commonPoints  = game.getCommonGoalPoints();
    }

    /**
     * getter for the shelves of the players
     * @return a HashMap that links each player to his shelf
     */
    public Map<String, TilesType[][]> getShelves() {
        return shelves;
    }

    /**
     * getter for commonGoal1
     * @return a String representing the first selected common goal
     */
    public String getCommonGoal1() {
        return commonGoal1;
    }

    /**
     * getter for commonGoal2
     * @return a String representing the second selected commond goal
     */
    public String getCommonGoal2() {
        return commonGoal2;
    }

    /**
     * function that return the points of a player relative to a common goal
     * @param player is the player name
     * @param goalNumber is the index of the
     * @return the points of player regarding the requested common goal as an int
     */
    public int getCommonPoints(String player, int goalNumber){
        if(goalNumber!=0 && goalNumber!=1) return 0;
        return commonPoints.get(player)[goalNumber];
    }

    /**
     * getter for personalGoals
     * @return a HashMap that links each player to his personal goal
     */
    public Map<String, PersonalGoal> getPersonalGoals() {
        return personalGoals;
    }
}
