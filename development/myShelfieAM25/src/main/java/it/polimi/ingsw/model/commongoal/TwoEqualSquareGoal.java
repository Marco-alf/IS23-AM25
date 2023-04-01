package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is the TwoEqualSquareGoal. It gives points to a player if they have two groups each containing 4 tiles of
 * the same type in a 2x2 square.
 */
public class TwoEqualSquareGoal extends CommonGoal{

    /**
     * The constructor is the same as the super class
     * @param numPlayers is then number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public TwoEqualSquareGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * Checks if the player placed tiles of the same type forming two 2x2 squares
     * @param shelf is a TilesType matrix
     * @return true if the player completed the goal, false if they didn't
     */
    public boolean checkPoints(TilesType[][] shelf){
        TilesType[][] matrixCopy = shelf.clone();
        int con = 0;

        for(int i = 0; i < MAX_ROW-1; i++) {
            for (int j = 0; j < MAX_COLUMN-1; j++) {
                TilesType start = matrixCopy[i][j];
                if(start != null &&
                    start == matrixCopy[i][j+1] &&
                    start == matrixCopy[i+1][j] &&
                    start == matrixCopy[i+1][j+1]) {
                    con++;
                    matrixCopy[i][j] = null;
                    matrixCopy[i][j+1] = null;
                    matrixCopy[i+1][j] = null;
                    matrixCopy[i+1][j+1] = null;
                    j++;
                }
            }
        }
        return con >= 2;
    }
}
