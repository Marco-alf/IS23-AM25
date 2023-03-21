package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;
import java.util.ArrayList;

/**
 * This is TwoDifferentColumnsGoal. It gives points to a player if their shelf contains two columns each formed by 6
 * different types of tiles
 */
public class TwoDifferentColumnsGoal extends CommonGoal {
    /**
     * The constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public TwoDifferentColumnsGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * @param player
     * @return true if there actually are 2 columns formed by 6 different types of tiles, false otherwise
     */
    protected boolean checkPoints(Player player) {
        TilesType[][] matrixCopy = player.getShelf().clone();
        int matchingColumns = 0;
        ArrayList<TilesType> types = new ArrayList<>();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 6; i++) {
                if(matrixCopy[i][j] != null && !types.contains(matrixCopy[i][j])) types.add(matrixCopy[i][j]);
            }
            if(types.size() == 6) matchingColumns++;
            if(matchingColumns == 2) return true;
            types.clear();
        }

        return false;
    }
}