package it.polimi.ingsw.model;

import it.polimi.ingsw.model.boardFiller.*;
import it.polimi.ingsw.model.commongoal.*;
import it.polimi.ingsw.model.exception.*;

import java.util.ArrayList;
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
    public LivingRoom(int playerNumber) throws InvalidPlayerNumberException, InvalidCommonGoalCardException {
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
            case 0:
                commonGoals[0] = new SixCoupleGoal(playerNumber);
                break;
            case 1:
                commonGoals[0] = new FullDiagonalGoal(playerNumber);
                break;
            case 2:
                commonGoals[0] = new CornerGoal(playerNumber);
                break;
            case 3:
                commonGoals[0] = new FourRegularRowsGoal(playerNumber);
                break;
            case 4:
                commonGoals[0] = new FourEqualsGroupGoal(playerNumber);
                break;
            case 5:
                commonGoals[0] = new TwoDifferentColumnsGoal(playerNumber);
                break;
            case 6:
                commonGoals[0] = new TwoEqualSquareGoal(playerNumber);
                break;
            case 7:
                commonGoals[0] = new TwoDifferentRowsGoal(playerNumber);
                break;
            case 8:
                commonGoals[0] = new ThreeRegularColumnGoal(playerNumber);
                break;
            case 9:
                commonGoals[0] = new XGoals(playerNumber);
                break;
            case 10:
                commonGoals[0] = new EightEqualsGoal(playerNumber);
                break;
            case 11:
                commonGoals[0] = new TriangularMatrixGoal(playerNumber);
                break;
            default:
                throw new InvalidCommonGoalCardException();
        }

        switch (commonGoalIndex[1]) {
            case 0:
                commonGoals[1] = new SixCoupleGoal(playerNumber);
                break;
            case 1:
                commonGoals[1] = new FullDiagonalGoal(playerNumber);
                break;
            case 2:
                commonGoals[1] = new CornerGoal(playerNumber);
                break;
            case 3:
                commonGoals[1] = new FourRegularRowsGoal(playerNumber);
                break;
            case 4:
                commonGoals[1] = new FourEqualsGroupGoal(playerNumber);
                break;
            case 5:
                commonGoals[1] = new TwoDifferentColumnsGoal(playerNumber);
                break;
            case 6:
                commonGoals[1] = new TwoEqualSquareGoal(playerNumber);
                break;
            case 7:
                commonGoals[1] = new TwoDifferentRowsGoal(playerNumber);
                break;
            case 8:
                commonGoals[1] = new ThreeRegularColumnGoal(playerNumber);
                break;
            case 9:
                commonGoals[1] = new XGoals(playerNumber);
                break;
            case 10:
                commonGoals[1] = new EightEqualsGoal(playerNumber);
                break;
            case 11:
                commonGoals[1] = new TriangularMatrixGoal(playerNumber);
                break;
            default:
                throw new InvalidCommonGoalCardException();
        }
    }

    /**
     * This method is used to refill the board when needed.
     * We check if the remaining tiles on the board have no adjacent tiles, if that is the case the board is refilled
     */
    public void checkAndRefill() {
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
     * This method checks whether the tiles can be taken from the board or it is an illegal move. If
     * it is a valid move it removes the tiles from the board and return them to the player
     * @param tiles is an ArrayList that contains the tiles the player wants to move
     * @return an ArrayList of TilesType to the player
     * @throws NotInLineException if the tiles are not in a straight line
     * @throws NoFreeEdgeException if one tile has no free adjacent cells
     * @throws OutOfBoundException if one coordinate is out of the board
     * @throws NullPointerException if one cell is empty
     */
    public ArrayList<TilesType> takeTiles(ArrayList<Tile> tiles) throws NotInLineException, NoFreeEdgeException, OutOfBoundException, NullPointerException {
        int maxX = 0, minX = 0, maxY = 0, minY = 0;
        ArrayList<TilesType> tilesTypes = new ArrayList<TilesType>();

        for (Tile tile : tiles) {
            if (board[tile.getPosY()][tile.getPosX()] == null) throw new NullPointerException();
            if (tile.getPosX() >= 9 || tile.getPosX() < 0 || tile.getPosY() >= 9 || tile.getPosY() < 0)
                throw new OutOfBoundException();
            if (numAdj(tile.getPosY(), tile.getPosX()) == 4) throw new NoFreeEdgeException();
            if (tile.getPosX() > maxX) maxX = tile.getPosX();
            if (tile.getPosX() < minX) minX = tile.getPosX();
            if (tile.getPosY() > maxY) maxY = tile.getPosY();
            if (tile.getPosY() > minY) minY = tile.getPosY();
        }

        if((minX == maxX && maxY - minY == tiles.size() - 1) || (minY == maxY && maxX - minX == tiles.size() - 1)) {
            for (Tile tile : tiles) {
                tilesTypes.add(tile.getType());
                board[tile.getPosY()][tile.getPosX()] = null;
            }
            return tilesTypes;
        } else throw new NotInLineException();
    }

    /**
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
