package it.polimi.ingsw.model.data;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

public class GameInfo extends Data{
    private final List<String> onlinePlayers;
    private final String currentPlayer;
    private final TilesType[][] newShelf;
    private final TilesType[][] newBoard;
    private final int commonGoal1Points;
    private final int commonGoal2Points;

    public GameInfo(List<String> onlinePlayers, String currentPlayer, TilesType[][] newShelf, TilesType[][] newBoard, int commonGoal1Points, int commonGoal2Points) {
        this.onlinePlayers = onlinePlayers;
        this.currentPlayer = currentPlayer;
        this.newShelf = newShelf;
        this.newBoard = newBoard;
        this.commonGoal1Points = commonGoal1Points;
        this.commonGoal2Points = commonGoal2Points;
    }

    public List<String> getPlayers() {
        return onlinePlayers;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public TilesType[][] getShelf() {
        TilesType[][] shelf = new TilesType[6][5];
        for(int i=0; i<6; i++){
            shelf[i]=newShelf[i].clone();
        }
        return shelf;
    }

    public TilesType[][] getNewBoard() {
        TilesType[][] board = new TilesType[9][9];
        for(int i=0; i<9; i++){
            board[i]=newBoard[i].clone();
        }
        return board;
    }

    public int[] getCommonGoalPoints(){
        return new int[]{this.commonGoal1Points, this.commonGoal2Points};
    }
}
