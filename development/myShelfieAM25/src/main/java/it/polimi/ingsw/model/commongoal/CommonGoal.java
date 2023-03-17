package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.exception.*;

import java.util.ArrayList;
public abstract class CommonGoal {

    protected static final int MAX_ROW = 6;
    protected static final int MAX_COLUMN = 5;

    protected final ArrayList<String> players;
    protected int[] points; // stack of points, it varies with the number of players in the game

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

    public abstract boolean checkPoints(Player player);
    public int calculatePoints(Player player) {
        if(players.contains(player.getName())){
            return points[players.indexOf(player.getName())];
        }
        if(checkPoints(player)) {
            players.add(player.getName());
            return points[players.indexOf(player.getName())];
        }
        return 0;
    }
}