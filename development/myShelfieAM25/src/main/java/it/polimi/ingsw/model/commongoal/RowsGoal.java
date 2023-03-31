package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This is FourRegularRowsGoal. It gives points to a player if their shelf there are four lines each formed
 * by 5 tiles of maximum three different types. One line can show the same or a different combination of
 * another line
 */
public class RowsGoal extends CommonGoal {
    private final boolean isRegular;

    /**
     * The constructor is the same as the super class
     * @param numPlayers is the number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public RowsGoal(int numPlayers, boolean isRegular) throws InvalidPlayerNumberException {
        super(numPlayers);
        this.isRegular = isRegular;
    }

    /**
     * This method checks if the shelf contains 4 rows containing three or more different types of tiles
     * @param shelf is the player
     * @return true if there actually are 4 row containing three or more different types of tiles,
     * false otherwise
     */
    protected boolean checkPoints(TilesType[][] shelf) {
        TilesType[][] matrixCopy = shelf.clone();
        int rows = 0;
        int rowSize = 0;
        List<TilesType> types = new ArrayList<>();

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                if(matrixCopy[i][j] != null) {
                    rowSize++;
                    if (!types.contains(matrixCopy[i][j])) {
                        types.add(matrixCopy[i][j]);
                    }
                }
            }
            if (isRegular && types.size() <= 3 && rowSize == 5) {
                rows++;
                if(rows == 4) return true;
            }
            if (!isRegular && types.size() == 5) {
                rows++;
                if (rows == 2) return true;
            }
            types.clear();
            rowSize = 0;
        }
        return false;
    }
}
