package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

/**
 * GameInfo is the generic data that could be used to retrieve information about a game
 */
public class GameInfo extends Data{
    /**
     * onlinePlayers is a list of the currently online players
     */
    private final List<String> onlinePlayers;
    /**
     * currentPlayer is the name of the current player
     */
    private final String currentPlayer;
    /**
     * newShelf is the state of the last shelf that has been updated
     */
    private final TilesType[][] newShelf;
    /**
     * adjPoints is an update of the adjacency points currently scored by the current player
     */
    private final int adjPoints;
    /**
     * newBoard is the new state of the board
     */
    private final TilesType[][] newBoard;
    /**
     * commonGoal1Points represents the points obtained by the current player regarding the first common goal
     */
    private final int commonGoal1Points;
    /**
     * commonGoal2Points represents the points obtained by the current player regarding the second common goal
     */
    private final int commonGoal2Points;
    /**
     * personalGoalPoints represents the points obtained by the current player regarding the personal goal
     */
    private final int personalGoalPoints;
    /**
     * gameEnded is a boolean that indicates whether the game is ended or not
     */
    private final boolean gameEnded;

    /**
     * constructor of the class
     * @param game is the game from where we want to retrieve informations
     */
    public GameInfo(Game game) {
        this.onlinePlayers = game.getOnlinePlayers();
        this.currentPlayer = game.getCurrentPlayer();
        this.newShelf = game.getShelf();
        this.newBoard = game.getNewBoard();
        this.adjPoints = game.getAdjPoints();
        this.commonGoal1Points = game.getCommonGoal1Points();
        this.commonGoal2Points = game.getCommonGoal2Points();
        this.personalGoalPoints = game.getPersonalPoints();
        this.gameEnded = game.getEndGame();
    }

    /**
     * getter for onlinePlayers
     * @return a list with the currently online players
     */
    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }
    /**
     * getter for the currentPlayer
     * @return the name of the current player
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * getter for newShelf
     * @return the last modified shelf
     */
    public TilesType[][] getShelf() {
        return newShelf;
    }

    /**
     * getter for getCommonGoal1Points
     * @return the points obtained by the current player regarding the first common goal
     */
    public int getCommonGoal1Points() {
        return commonGoal1Points;
    }

    /**
     * getter for commonGoal2Points
     * @return the points obtained by the current player regarding the second common goal
     */
    public int getCommonGoal2Points() {
        return commonGoal2Points;
    }

    /**
     * getter for personalGoalPoints
     * @return the points obtained by the current player regarding the personal goal
     */
    public int getPersonalGoalPoints () {
        return personalGoalPoints;
    }

    /**
     * getter for adjPoints
     * @return the points obtained by the current player regarding the adjacency of similar tiles
     */
    public int getAdjPoints() {
        return adjPoints;
    }

    /**
     * getter for the isGameEnded boolean flag
     * @return true iff the game has ended
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * create a copy of the board and return it
     * @return a copy of the current board state
     */
    public TilesType[][] getNewBoard() {
        TilesType[][] board = new TilesType[9][9];
        for(int i=0; i<9; i++){
            board[i]=newBoard[i].clone();
        }
        return board;
    }
}
