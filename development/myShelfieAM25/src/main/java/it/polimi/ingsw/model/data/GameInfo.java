package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TilesType;

import java.util.List;
import java.util.Map;

public class GameInfo extends Data{
    private final List<String> onlinePlayers;
    private final String currentPlayer;
    private final Map<String, TilesType[][]> shelves;
    private final Map<String, Integer> adjPoints;
    private final Map<String, Integer> comm1Points;
    private final Map<String, Integer> comm2Points;
    private final TilesType[][] newBoard;
    private final int commonGoal1Points;
    private final int commonGoal2Points;

    public GameInfo(Game game) {
        this.onlinePlayers = game.getOnlinePlayers();
        this.currentPlayer = game.getCurrentPlayer();
        this.shelves = game.getShelves();
        this.newBoard = game.getNewBoard();
        // array common goal
        this.adjPoints = game.getAdjPoints();
        this.comm1Points = game.getComm1Points();
        this.comm2Points = game.getComm2Points();
        this.commonGoal1Points = game.getCommonGoal1Points();
        this.commonGoal2Points = game.getCommonGoal2Points();
    }

    public List<String> getPlayers() {
        return onlinePlayers;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<String, TilesType[][]> getShelves() {
        return shelves;
    }

    public int getCommonGoal1Points() {
        return commonGoal1Points;
    }

    public int getCommonGoal2Points() {
        return commonGoal2Points;
    }

    public Map<String, Integer> getAdjPoints() {
        return adjPoints;
    }

    public TilesType[][] getNewBoard() {
        TilesType[][] board = new TilesType[9][9];
        for(int i=0; i<9; i++){
            board[i]=newBoard[i].clone();
        }
        return board;
    }
}
