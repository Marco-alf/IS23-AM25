package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is SixCoupleGoal. It gives points to a player if their shelf contains six groups each containing at least
 * 2 tiles of the same type. The tiles of one group can be different from those of another group.
 */
public class SixCoupleGoal extends CommonGoal {

    /**
     * Dimension of the groups
     */
    private final int length = 2;

    /**
     * This is a matrix of int. It is used to verify whether a slot of the shelf has already been checked
     */
    private final int[][] checked = new int[MAX_ROW][MAX_COLUMN];

    /**
     * Constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public SixCoupleGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * private method used by checkPoints
     * @param matrix is the shelf (a TilesType matrix)
     * @param i is the first coordinate of the cell I want to check (es: matrix[i][...])
     * @param j is the second coordinate of the cell I want to check (es: matrix[...][j])
     * @return true if the tile to the right of the control cell is the same type of the control cell, and that it has
     * not already been checked, false otherwise
     */
    private boolean checkRow(TilesType[][] matrix, int i, int j) {
        TilesType control = matrix[i][j];
        if(control == null) return false;
        for (int k = 0; k < length; k++) {
            if(matrix[i][j + k] != control || checked[i + k][j] == 0) return false;
        }
        return true;
    }

    /**
     * private method used by checkPoints
     * @param matrix is the shelf (a TilesType matrix)
     * @param i is the first coordinate of the cell I want to check (es: matrix[i][...])
     * @param j is the second coordinate of the cell I want to check (es: matrix[...][j])
     * @return true if the tile below the control cell is the same type of the control cell, and that it has
     * not already been checked, false otherwise
     */
    private boolean checkColumn(TilesType[][] matrix, int i, int j) {
        TilesType control = matrix[i][j];
        if(control == null) return false;
        for (int k = 0; k < length; k++) {
            if(matrix[i + k][j] != control || checked[i + k][j] == 0) return false;
        }
        return true;
    }

    /**
     * @param player
     * @return true if the shelf (associated to the player) contains 6 groups of two tiles of the same type,
     * false otherwise
     */
    protected boolean checkPoints(Player player) {
        TilesType[][] matrixCopy = player.getShelf().clone();
        int couples = 0;
        int i = 0, j = 0;

        for (int k = 0; k < 6; k++) {
            for (int l = 0; l < 5; l++) {
                checked[k][l] = 1;
            }
        }

        while(i < 6 - length + 1) {
            while(j < 5 - length + 1) {
                if(checkRow(matrixCopy, i, j)) {
                    couples++;
                    if(couples == 6) return true;
                    for (int k = 0; k < 2; k++) {
                        checked[i][i + k] = 0;
                    }
                }
                if(checkColumn(matrixCopy, i, j)) {
                    couples++;
                    if(couples == 6) return true;
                    for (int k = 0; k < 2; k++) {
                        checked[i + k][j] = 0;
                    }
                }
                j++;
            }
            i++;
        }
        return false;
    }
}