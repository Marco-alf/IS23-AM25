package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is the FourEqualGroupsGoal. It gives points to a player if in their shelf there are four groups each
 * containing at least 4 tiles of the same type. The tiles of one group can be different from those of
 * another group
 */
public class FourEqualsGroupGoal extends CommonGoal {
    /**
     * This is a matrix of int. It is used to verify whether a slot of the shelf has already been checked
     */
    private final int[][] checked = new int[MAX_ROW][MAX_COLUMN];

    /**
     * The constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public FourEqualsGroupGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * Private method used by checkPoints, it uses recursion to find the size of a group containing tiles of the
     * same type.
     * @param matrix is the shelf (a TilesType matrix)
     * @param i is the first coordinate of the cell I want to check (es: matrix[i][...])
     * @param j is the second coordinate of the cell I want to check (es: matrix[...][k])
     * @return the size of the group of tiles of the same type that contains the tile in the
     * coordinates [i][j]
     */
    private int numAdj(TilesType[][] matrix, int i, int j) {
        int adj = 0;
        if(checked[i][j] == 0 || matrix[i][j] == null) return 0;
        adj++;
        TilesType type = matrix[i][j];
        checked[i][j] = 0;
        if((j + 1) < MAX_COLUMN && matrix[i][j + 1] == type) {
            adj = adj + numAdj(matrix, i, j + 1);
        }
        if((i + 1) < MAX_ROW && matrix[i + 1][j] == type) {
            adj = adj + numAdj(matrix, i + 1, j);
        }
        return adj;
    }

    /**
     * This method checks every slot of the bookshelf and verifies if the groups of tiles of the same type
     * contains 4 or more tiles
     * @param player
     * @return true if the number of groups of tiles of the same type containing 4 tiles is greater or equal to 4,
     * false otherwise
     */
    protected boolean checkPoints(Player player) {
        TilesType[][] matrixCopy = player.getShelf().clone();
        int quartets = 0;

        for (int k = 0; k < MAX_ROW; k++) {
            for (int l = 0; l < MAX_COLUMN; l++) {
                checked[k][l] = 1;
            }
        }

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                int dim = numAdj(matrixCopy, i, j);
                quartets += dim / 4;
                if(quartets >= 4) return true;
            }
        }

        return false;
    }
}

