package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is FullDiagonalGoal. It gives points to a player if their shelf contains five tiles of the same type
 * forming a diagonal
 */
public class FullDiagonalGoal extends CommonGoal {

    /**
     * The constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public FullDiagonalGoal(int numPlayers) throws InvalidPlayerNumberException {
        super((numPlayers));
    }

    /**
     * This method checks if a shelf contains five tiles of the same type forming a diagonal.
     * There are four possible diagonals, beginning from the top left, from the top right, from the bottom left
     * or from the bottom right
     * @param player
     * @return true if there is at least one of the 4 diagonals, false otherwise
     */
    protected boolean checkPoints(Player player) {
        TilesType[][] matrixCopy = player.getShelf().clone();
        int counterTopLeft = 0;
        int counterBottomLeft = 0;
        int counterTopRight = 0;
        int counterBottomRight = 0;

        TilesType topLeft = matrixCopy[0][0];
        TilesType topRight = matrixCopy[0][4];
        TilesType bottomLeft = matrixCopy[5][0];
        TilesType bottomRight = matrixCopy[5][4];

        for (int i = 0; i < 5; i++) {
            if(matrixCopy[i][i] == topLeft && matrixCopy[i][i] != null) counterTopLeft++;
            if(matrixCopy[5 - i][i] == bottomLeft && matrixCopy[i][i] != null) counterBottomLeft++;
            if(matrixCopy[i][4 - i] == topRight && matrixCopy[i][i] != null) counterTopRight++;
            if(matrixCopy[5 - i][4 - i] == bottomRight && matrixCopy[i][i] != null) counterBottomRight++;
        }
        return counterBottomLeft == 5 || counterBottomRight == 5 || counterTopLeft == 5 || counterTopRight == 5;
    }

}