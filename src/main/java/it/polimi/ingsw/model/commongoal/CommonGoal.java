package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;

/**
 * This class represents a common goal. It is an abstract class, the method checkPoints
 * is implemented in its subclasses.
 */
public abstract class CommonGoal {

    /**
     * Max height of a shelf
     */
    protected static final int MAX_ROW = 6;
    /**
     * Max width of a shelf
     */
    protected static final int MAX_COLUMN = 5;

    /**
     * This ArrayList tracks the players that scored the points of the common goal and in which order they completed it
     */
    protected final ArrayList<String> players;
    /**
     * It is an array of int, it is built based on the number of players in the game. The index of a player in the
     * ArrayList players matches the index of points in this array
     */
    protected int[] points; // stack of points, it varies with the number of players in the game
    /**
     * type is a string that uniquely identifies the category of common goal
     */
    protected String type;

    /**
     * The constructor initializes the ArrayList players and creates the array points based on the number
     * of players in the game
     * @param numPlayers is the number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is greater than 4 or lower than 2
     */
    public CommonGoal(int numPlayers) throws InvalidPlayerNumberException{
        players = new ArrayList<>();
        switch (numPlayers) {
            case 2:
                points = new int[] {8, 4};
                break;
            case 3:
                points = new int[] {8, 6, 4};
                break;
            case 4:
                points = new int[] {8, 6, 4, 2};
                break;
            default:
                throw new InvalidPlayerNumberException();
        }

    }

    /**
     * getType is a getter for the type of the common goal
     * @return a string that identifies the type of this common goal
     */
    public String getType () {
        return type;
    }

    /**
     * This is an abstract method, it is implemented in its subclasses
     * @param shelf is a TilesType matrix
     * @return true if the shelf completed the goal, false if they didn't
     */
    protected abstract boolean checkPoints(TilesType[][] shelf);

    /**
     * calculatePoints is a method that given a player return the points that he have scored
     * @param player is the player
     * @return the number of points the player accumulated
     */
    public int calculatePoints(Player player) {
        if(players.contains(player.getName())){
            return points[players.indexOf(player.getName())];
        }
        if(checkPoints(player.getShelf())) {
            players.add(player.getName());
            return points[players.indexOf(player.getName())];
        }
        return 0;
    }
}