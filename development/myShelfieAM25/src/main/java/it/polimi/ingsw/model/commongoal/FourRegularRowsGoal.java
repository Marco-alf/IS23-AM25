package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;
import java.util.ArrayList;

/**
 * This is FourRegularRowsGoal. It gives points to a player if their shelf there are four lines each formed
 * by 5 tiles of maximum three different types. One line can show the same or a different combination of
 * another line
 */
public class FourRegularRowsGoal extends CommonGoal {

    /**
     * The constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public FourRegularRowsGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * This method checks if the shelf contains 4 rows containing three or more different types of tiles
     * @param player
     * @return true if there actually are 4 row containing three or more different types of tiles,
     * false otherwise
     */
    protected boolean checkPoints(Player player) {
        TilesType[][] matrixCopy = player.getShelf().clone();
        int matchingRows = 0;
        ArrayList<TilesType> types = new ArrayList<>();

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                if(matrixCopy[i][j] != null && !types.contains(matrixCopy[i][j])) types.add(matrixCopy[i][j]);
            }
            if(types.size() <= 3) matchingRows++;
            if(matchingRows == 4) return true;
            types.clear();
        }
        return false;
    }
}
