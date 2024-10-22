package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.boardFiller.*;
import it.polimi.ingsw.model.commongoal.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

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
     * Constructor used for testing, it creates a board based on the json files inside the src/main/resources/json/ folder.
     * The json file needs to be called board+${playerNumber}+.json
     * @param playerNumber a String that represents the number of player.
     * @throws InvalidPlayerNumberException if a wrong playerNumber is passed
     */
    public LivingRoom(String playerNumber) throws InvalidPlayerNumberException {
        switch (playerNumber) {
            case "2" -> this.boardFiller = new TwoPlayerFiller();
            case "3" -> this.boardFiller = new ThreePlayerFiller();
            case "4" -> this.boardFiller = new FourPlayerFiller();
            default -> throw new InvalidPlayerNumberException();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Path filePath = Path.of("src/main/resources/json/board"+playerNumber+".json");
            String json = Files.readString(filePath);
            this.board = objectMapper.readValue(json , TilesType[][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        commonGoals[0] = new RowsGoal(parseInt(playerNumber), false);
        commonGoals[1] = new FullDiagonalGoal(parseInt(playerNumber));
    }

    /**
     * In the constructor firstly the board is initialized with null pointers, then boardFiller is used to fill the board
     * with tiles. After this 2 different common goals are extracted for the game.
     * @param playerNumber is the number of players in the game, it is needed to create different versions of the board.
     * @throws InvalidPlayerNumberException whenever is attempted to create a game with more than 4 player or less than 2
     */
    public LivingRoom(int playerNumber) throws InvalidPlayerNumberException {
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
                commonGoals[0] = new EqualGroupsGoal(playerNumber, 2, 6);
                break;
            case 1:
                commonGoals[0] = new FullDiagonalGoal(playerNumber);
                break;
            case 2:
                commonGoals[0] = new CornerGoal(playerNumber);
                break;
            case 3:
                commonGoals[0] = new RowsGoal(playerNumber, true);
                break;
            case 4:
                commonGoals[0] = new EqualGroupsGoal(playerNumber, 4, 4);
                break;
            case 5:
                commonGoals[0] = new ColumnsGoal(playerNumber, false);
                break;
            case 6:
                commonGoals[0] = new TwoEqualSquareGoal(playerNumber);
                break;
            case 7:
                commonGoals[0] = new RowsGoal(playerNumber, false);
                break;
            case 8:
                commonGoals[0] = new ColumnsGoal(playerNumber, true);
                break;
            case 9:
                commonGoals[0] = new XGoal(playerNumber);
                break;
            case 10:
                commonGoals[0] = new EightEqualsGoal(playerNumber);
                break;
            case 11:
                commonGoals[0] = new TriangularMatrixGoal(playerNumber);
                break;
        }

        switch (commonGoalIndex[1]) {
            case 0:
                commonGoals[1] = new EqualGroupsGoal(playerNumber, 2, 6);
                break;
            case 1:
                commonGoals[1] = new FullDiagonalGoal(playerNumber);
                break;
            case 2:
                commonGoals[1] = new CornerGoal(playerNumber);
                break;
            case 3:
                commonGoals[1] = new RowsGoal(playerNumber, true);
                break;
            case 4:
                commonGoals[1] = new EqualGroupsGoal(playerNumber, 4, 4);
                break;
            case 5:
                commonGoals[1] = new ColumnsGoal(playerNumber, false);
                break;
            case 6:
                commonGoals[1] = new TwoEqualSquareGoal(playerNumber);
                break;
            case 7:
                commonGoals[1] = new RowsGoal(playerNumber, false);
                break;
            case 8:
                commonGoals[1] = new ColumnsGoal(playerNumber, true);
                break;
            case 9:
                commonGoals[1] = new XGoal(playerNumber);
                break;
            case 10:
                commonGoals[1] = new EightEqualsGoal(playerNumber);
                break;
            case 11:
                commonGoals[1] = new TriangularMatrixGoal(playerNumber);
                break;
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
        if(boardFiller.checkRemaining()){
            board = boardFiller.fill(board);
        }
    }

    /**
     * getEnumArray is a getter for the board as matrix
     * @return the board (a 9x9 TilesType matrix)
     */
    public TilesType[][] getEnumArray() {
        TilesType[][] boardCopy = new TilesType[board.length][];
        for(int i = 0; i < board.length; i++){
            boardCopy[i] = board[i].clone();
        }
        return boardCopy;
    }

    /**
     * getCommonGoals is a getter for the commonGoal attribute
     * @return a vector with the two common goals chosen for the game
     */
    public CommonGoal[] getCommonGoals() {
        return commonGoals;
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
     * The order of the TilesType in the tiles parameter is kept in the returned array
     * @param tiles is an ArrayList that contains the tiles the player wants to move
     * @return an ArrayList of TilesType to the player
     * @throws NotInLineException if the tiles are not in a straight line
     * @throws NoFreeEdgeException if one tile has no free adjacent cells
     * @throws OutOfBoundException if one coordinate is out of the board
     * @throws NullPointerException if one cell is empty
     * @throws InvalidTileException if the tile requested is different from the one in the board
     */
    public List<TilesType> takeTiles(List<Tile> tiles) throws NotInLineException, NoFreeEdgeException, OutOfBoundException, NullPointerException, InvalidTileException {
        int maxX = 0, minX = 9, maxY = 0, minY = 9;
        List<TilesType> tilesTypes = new ArrayList<>();

        for (Tile tile : tiles) {
            if (tile.getPosX() >= 9 || tile.getPosX() < 0 || tile.getPosY() >= 9 || tile.getPosY() < 0)
                throw new OutOfBoundException();
            if (board[tile.getPosY()][tile.getPosX()] == null) throw new NullPointerException();
            if (tile.getType()!=board[tile.getPosY()][tile.getPosX()]) throw new InvalidTileException();
            if (numAdj(tile.getPosY(), tile.getPosX()) == 4) throw new NoFreeEdgeException();
            if (tile.getPosX() > maxX) maxX = tile.getPosX();
            if (tile.getPosX() < minX) minX = tile.getPosX();
            if (tile.getPosY() > maxY) maxY = tile.getPosY();
            if (tile.getPosY() < minY) minY = tile.getPosY();
        }

        if((minX == maxX && maxY - minY == tiles.size() - 1) || (minY == maxY && maxX - minX == tiles.size() - 1) || tiles.size()==0) {
            for (Tile tile : tiles) {
                tilesTypes.add(tile.getType());
                board[tile.getPosY()][tile.getPosX()] = null;
            }
            this.checkAndRefill();
            return tilesTypes;
        } else throw new NotInLineException();
    }

    /**
     * calculateCommonPoints is the method used to calculate the points of a played scored in common points
     * @param player is the player of which we want to know the score related to common goals
     * @return the points from the common goals accumulated by the player
     */
    public int[] calculateCommonPoints(Player player) {
        int[] points = new int[2];
        for (int i = 0; i < 2; i++) {
            points[i] = commonGoals[i].calculatePoints(player);
        }
        return points;
    }

}
