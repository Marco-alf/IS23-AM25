package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;

/**
 * This is TwoDifferentColumnsGoal. It gives points to a player if their shelf contains two columns each formed by 6
 * different types of tiles
 */
public class ColumnsGoal extends CommonGoal {
    private final boolean isRegular;
    /**
     * The constructor is the same as the super class
     * @param numPlayers is the number of players
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public ColumnsGoal(int numPlayers, boolean isRegular) throws InvalidPlayerNumberException {
        super(numPlayers);
        this.isRegular = isRegular;
    }

    /**
     * @param shelf is the player
     * @return true if there actually are 2 columns formed by 6 different types of tiles, false otherwise
     */
    protected boolean checkPoints(TilesType[][] shelf) {
        TilesType[][] matrixCopy = shelf.clone();
        int columns = 0;
        int columnSize = 0;
        ArrayList<TilesType> types = new ArrayList<>();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 6; i++) {
                if (matrixCopy[i][j] != null) {
                    columnSize++;
                    if (!types.contains(matrixCopy[i][j])) {
                        types.add(matrixCopy[i][j]);
                    }
                }
            }
            if (!isRegular && types.size() == 6) {
                columns++;
                if (columns == 2) return true;
            }
            if (isRegular && types.size() <= 3 && columnSize == 6) {
                columns++;
                if (columns == 3) return true;
            }
            types.clear();
            columnSize = 0;
        }

        return false;
    }
}