package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is the XGoal. It gives points to a player if they have five tiles of the same type forming an X.
 */
public class XGoal extends CommonGoal{
    /**
     * The constructor is the same as the super class
     * @param numPlayers is then number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public XGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * Checks if the player placed five tiles of the same type forming an X
     * @param shelf is a TilesType matrix
     * @return true if the player completed the goal, false if they didn't
     */
    public boolean checkPoints(TilesType[][] shelf) {
        TilesType[][] matrixCopy = shelf.clone();

        for(int i = 0; i < MAX_ROW-2; i++){
            for(int j = 0; j < MAX_COLUMN-2; j++){
                TilesType start = matrixCopy[i][j];
                if (start != null &&
                    start == matrixCopy[i][j + 2] &&
                    start == matrixCopy[i + 2][j] &&
                    start == matrixCopy[i + 1][j + 1] &&
                    start == matrixCopy[i + 2][j + 2]) {
                    return true;
                }
            }
        }
        return false;
    }
}

