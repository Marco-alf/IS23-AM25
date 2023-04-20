package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

public class GameInfo extends Data{
    private final List<String> onlinePlayers;
    private final String currentPlayer;
    private final TilesType[][] newShelf;
    private final int adjPoints;
    private final TilesType[][] newBoard;
    private final int commonGoal1Points;
    private final int commonGoal2Points;

    public GameInfo(Game game) {
        this.onlinePlayers = game.getOnlinePlayers();
        this.currentPlayer = game.getCurrentPlayer();
        this.newShelf = game.getShelf();
        this.newBoard = game.getNewBoard();
        this.adjPoints = game.getAdjPoints();
        this.commonGoal1Points = game.getCommonGoal1Points();
        this.commonGoal2Points = game.getCommonGoal2Points();
    }

    public List<String> getPlayers() {
        return onlinePlayers;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public TilesType[][] getShelf() {
        return newShelf;
    }

    public int getCommonGoal1Points() {
        return commonGoal1Points;
    }

    public int getCommonGoal2Points() {
        return commonGoal2Points;
    }

    public int getAdjPoints() {
        return adjPoints;
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public TilesType[][] getNewBoard() {
        TilesType[][] board = new TilesType[9][9];
        for(int i=0; i<9; i++){
            board[i]=newBoard[i].clone();
        }
        return board;
    }
}
