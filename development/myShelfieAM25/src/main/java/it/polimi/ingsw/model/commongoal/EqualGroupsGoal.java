package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;

/**
 * This is EqualGroups. It generalizes two common goals (FourEqualsGroupsGoal and SixCoupleGoal). It gives points to
 * a player if in their shelf there are @quantity groups each containing at least @size tiles of the same type.
 * The tiles of one group can be different from those of another group
 */
public class EqualGroupsGoal extends CommonGoal{
    /**
     * This is a matrix of int. It is used to verify whether a slot of the shelf has already been checked
     */
    private final int[][] checked = new int[MAX_ROW][MAX_COLUMN];
    /**
     * This is the size of the groups
     */
    private final int size;
    /**
     * This is the number of groups
     */
    private final int quantity;
    /**
     * The constructor is the same as the super class. It adds the number of group of tiles and the size of the
     * groups to generalize two other common goals.
     * @param numPlayers is the number of players in the game
     * @param size is the size of the groups
     * @param quantity is the number of groups
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public EqualGroupsGoal(int numPlayers, int size, int quantity) throws InvalidPlayerNumberException {
        super(numPlayers);
        this.size = size;
        this.quantity = quantity;
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
        if((j - 1) >= 0 && matrix[i][j - 1] == type) {
            adj = adj + numAdj(matrix, i, j - 1);
        }
        if((i - 1) >= 0 && matrix[i - 1][j] == type) {
            adj = adj + numAdj(matrix, i - 1, j);
        }
        return adj;
    }

    /**
     * This method checks every slot of the bookshelf and verifies if the groups of tiles of the same type
     * contains @size or more tiles
     * @param shelf is the players
     * @return true if the number of groups of tiles of the same type containing @size tiles is greater or equal
     * to @quantity, false otherwise
     */
    protected boolean checkPoints(TilesType[][] shelf) {
        TilesType[][] matrixCopy = shelf.clone();
        int groups = 0;

        for (int k = 0; k < MAX_ROW; k++) {
            for (int l = 0; l < MAX_COLUMN; l++) {
                checked[k][l] = 1;
            }
        }

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                int dim = numAdj(matrixCopy, i, j);
                if (dim >= size) groups++;
                if(groups >= quantity) return true;
            }
        }
        return false;
    }
}
