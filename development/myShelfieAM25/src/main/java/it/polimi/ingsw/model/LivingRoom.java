package it.polimi.ingsw.model;

import it.polimi.ingsw.model.boardFiller.*;
import it.polimi.ingsw.model.commongoal.*;
import it.polimi.ingsw.model.exception.*;
import java.util.Random;

/**
 * Creating a class that represents the living room which contains the board and the two common goals.
*/
public class LivingRoom {

    /**
     * The board is a 9x9 TilesType matrix
     */
    private TilesType[][] board = new TilesType[9][9];

    /**
     * boardFiller is an object that randomly refills the board
     */
    private final BoardFiller boardFiller;

    /**
     * For every game there are 2 common goals, a 2-element array which contains them is created
     */
    private final CommonGoal[] commonGoals = new CommonGoal[2];

    /**
     * In the constructor firstly the board is initialized with null pointers, then boardFiller is used to fill the board
     * with tiles. After this 2 different common goals are extracted for the game.
     * @param playerNumber is the number of players in the game, it is needed to create different versions of the board.
     */
    public LivingRoom(int playerNumber) throws InvalidPlayerNumberException, NoTileLeftException, InvalidCommonGoalCardException {
        Random random = new Random(System.currentTimeMillis());
        int[] commonGoalIndex = new int[2];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = null;
            }
        }
         switch (playerNumber) {
             case 2:
                 boardFiller = new TwoPlayerFiller();
                 break;
             case 3:
                 boardFiller = new ThreePlayerFiller();
                 break;
             case 4:
                 boardFiller = new FourPlayerFiller();
                 break;
             default:
                 throw new InvalidPlayerNumberException();
         }

        board = boardFiller.fill(board);

        commonGoalIndex[0] = random.nextInt(12);
        commonGoalIndex[1] = random.nextInt(12);
        while (commonGoalIndex[1] == commonGoalIndex[0]) {
            commonGoalIndex[1] = random.nextInt(12);
        }

        switch (commonGoalIndex[0]) {
            case 0 -> commonGoals[0] = new SixCoupleGoal(playerNumber);
            case 1 -> commonGoals[0] = new FullDiagonalGoal(playerNumber);
            case 2 -> commonGoals[0] = new CornerGoal(playerNumber);
            case 3 -> commonGoals[0] = new FourRegularRowsGoal(playerNumber);
            case 4 -> commonGoals[0] = new FourEqualsGroupGoal(playerNumber);
            case 5 -> commonGoals[0] = new TwoDifferentColumnsGoal(playerNumber);
            case 6 -> commonGoals[0] = new TwoEqualSquareGoal(playerNumber);
            case 7 -> commonGoals[0] = new TwoDifferentRowsGoal(playerNumber);
            case 8 -> commonGoals[0] = new ThreeRegularColumnGoal(playerNumber);
            case 9 -> commonGoals[0] = new XGoal(playerNumber);
            case 10 -> commonGoals[0] = new EightEqualsGoal(playerNumber);
            case 11 -> commonGoals[0] = new TriangularMatrixGoal(playerNumber);
            default -> throw new InvalidCommonGoalCardException();
        }

        switch (commonGoalIndex[1]) {
            case 0 -> commonGoals[1] = new SixCoupleGoal(playerNumber);
            case 1 -> commonGoals[1] = new FullDiagonalGoal(playerNumber);
            case 2 -> commonGoals[1] = new CornerGoal(playerNumber);
            case 3 -> commonGoals[1] = new FourRegularRowsGoal(playerNumber);
            case 4 -> commonGoals[1] = new FourEqualsGroupGoal(playerNumber);
            case 5 -> commonGoals[1] = new TwoDifferentColumnsGoal(playerNumber);
            case 6 -> commonGoals[1] = new TwoEqualSquareGoal(playerNumber);
            case 7 -> commonGoals[1] = new TwoDifferentRowsGoal(playerNumber);
            case 8 -> commonGoals[1] = new ThreeRegularColumnGoal(playerNumber);
            case 9 -> commonGoals[1] = new XGoal(playerNumber);
            case 10 -> commonGoals[1] = new EightEqualsGoal(playerNumber);
            case 11 -> commonGoals[1] = new TriangularMatrixGoal(playerNumber);
            default -> throw new InvalidCommonGoalCardException();
        }
    }

    /**
     * This method is used to refill the board when needed.
     * We check if the remaining tiles on the board have no adjacent tiles, if that is the case the board is refilled
     */
    public void checkAndRefill() throws NoTileLeftException {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != null && numAdj(i, j) > 0) return;
            }
        }
        board = boardFiller.fill(board);
    }

    /**
     * @return the board (a 9x9 TilesType matrix)
     */
    public TilesType[][] getEnumArray() {
        return board;
    }

    /**
     * This method is used to return the number of adjacent tiles of a cell on the board
     * @param i is the first parameter of the matrix (es: board[i][...])
     * @param j is the second parameter of the matrix (es: board[...][j])
     * @return the number of adjacent tiles of a cell
     */
    private int numAdj(int i, int j) {
        int adj = 0;
        if((j + 1) < 9 && board[i][j + 1] != null) adj++;
        if((j - 1) >= 0 && board[i][j - 1] != null) adj++;
        if((i + 1) < 9 && board[i + 1][j] != null) adj++;
        if((i - 1) >= 0 && board[i - 1][j] != null) adj++;
        return adj;
    }

    /**
     * @param array is an int array
     * @return the min value of the array
     */
    private int getMaxValue(int[] array) {
        int maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    /**
     * @param array is an int array
     * @return the max value of the array
     */
    private int getMinValue(int[] array) {
        int minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    /**
     * This method checks whether the tiles can be taken from the board or it is an illegal move. If
     * it is a valid move it removes the tiles from the board and return them to the player
     * @param positions is an array of int that represent the coordinates of a tile on the board
     * @return an array of TileType which contains the tiles taken from the board
     * @throws NotInLineException if the tiles are not in a straight line
     * @throws NoFreeEdgeException if one tile has no free adjacent cells
     * @throws OutOfBoundException if one coordinate is out of the board
     * @throws NullPointerException if one cell is empty
     */
    public TilesType[] takeTiles(int[] positions) throws NotInLineException, NoFreeEdgeException, OutOfBoundException, NullPointerException {
        TilesType[] tiles = new TilesType[] {null, null, null};
        int[] x, y;

        if (positions[2] == -1) {
            x = new int[] {positions[1]};
            y = new int[] {positions[0]};
            for (int i = 0; i < x.length; i++) {
                if (board[y[i]][x[i]] == null) throw new NullPointerException();
                if (x[i] >= 9 || x[i] < 0 || y[i] >= 9 || y[i] < 0) throw new OutOfBoundException();
                if (numAdj(y[0], x[0]) == 4) throw new NoFreeEdgeException();
            }

            for (int i = 0; i < x.length; i++) {
                tiles[i] = board[y[i]][x[i]];
                board[y[i]][x[i]] = null;
            }
            return tiles;
        }

        if (positions[4] == -1) {
            x = new int[] {positions[1], positions[3]};
            y = new int[] {positions[0], positions[2]};
            for (int i = 0; i < x.length; i++) {
                if (board[y[i]][x[i]] == null) throw new NullPointerException();
                if (x[i] >= 9 || x[i] < 0 || y[i] >= 9 || y[i] < 0) throw new OutOfBoundException();
                if (numAdj(y[0], x[0]) == 4) throw new NoFreeEdgeException();
            }
            if((getMinValue(x) == getMaxValue(x) && getMaxValue(y) - getMinValue(y) == 1) || (getMinValue(y) == getMaxValue(y) && getMaxValue(x) - getMinValue(x) == 1)) {
                for (int i = 0; i < x.length; i++) {
                    tiles[i] = board[y[i]][x[i]];
                    board[y[i]][x[i]] = null;
                }
            } else throw new NotInLineException();
            return tiles;
        }

        x = new int[] {positions[1], positions[3], positions[5]};
        y = new int[] {positions[0], positions[2], positions[4]};
        for (int i = 0; i < x.length; i++) {
            if (board[y[i]][x[i]] == null) throw new NullPointerException();
            if (x[i] >= 9 || x[i] < 0 || y[i] >= 9 || y[i] < 0) throw new OutOfBoundException();
            if (numAdj(y[0], x[0]) == 4) throw new NoFreeEdgeException();
        }
        if((getMinValue(x) == getMaxValue(x) && getMaxValue(y) - getMinValue(y) == 2) || (getMinValue(y) == getMaxValue(y) && getMaxValue(x) - getMinValue(x) == 2)) {
            for (int i = 0; i < x.length; i++) {
                tiles[i] = board[y[i]][x[i]];
                board[y[i]][x[i]] = null;
            }
        } else throw new NotInLineException();
        return tiles;
    }

    /**
     * @param player
     * @return the points from the common goals accumulated by the player
     */
    public int calculateCommonPoints(Player player) {
        int points = 0;
        for (int i = 0; i < 2; i++) {
            points += commonGoals[i].calculatePoints(player);
        }
        return points;
    }

}
