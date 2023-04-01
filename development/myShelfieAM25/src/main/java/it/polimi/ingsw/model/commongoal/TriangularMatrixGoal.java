package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is the TriangularMatrixGoal. It gives points to a player if they have five columns of increasing or decreasing
 * height. Tiles can be of any type.
 */
public class TriangularMatrixGoal extends CommonGoal{

    /**
     * The constructor is the same as the super class
     * @param numPlayers is then number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public TriangularMatrixGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * Checks if the player placed tiles forming a increasing or decreasing pattern
     * @param shelf is a TilesType matrix
     * @return true if the player completed the goal, false if they didn't
     */
    public boolean checkPoints(TilesType[][] shelf) {

        TilesType[][] matrixCopy = shelf.clone();

        return (matrixCopy[1][0] != null && matrixCopy[2][1] != null && matrixCopy[3][2] != null && matrixCopy[4][3] != null && matrixCopy[5][4] != null &&
                matrixCopy[0][0] == null && matrixCopy[1][1] == null && matrixCopy[2][2] == null && matrixCopy[3][3] == null && matrixCopy[4][4] == null) ||
                (matrixCopy[5][0] != null && matrixCopy[4][1] != null && matrixCopy[3][2] != null && matrixCopy[2][3] != null &&
                matrixCopy[1][4] != null && matrixCopy[4][0] == null && matrixCopy[3][1] == null && matrixCopy[2][2] == null && matrixCopy[1][3] == null && matrixCopy[0][4] == null);
    }
}
