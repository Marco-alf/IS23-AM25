package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;

import java.util.Random;

import it.polimi.ingsw.model.exception.*;

public class ThreePlayerFiller extends TwoPlayerFiller{
    /**
     * The constructor of the class initialize an array with the number of remaining tiles
     * The initialization with the current system time allows further randomization
     */
    public ThreePlayerFiller(){
        this.remainingTiles = new int[]{22,22,22,22,22,22};
        this.random = new Random(System.currentTimeMillis());
    }


    public TilesType[][] fill(TilesType[][] board) throws NoTileLeftException{
        board = super.fill(board);

        if(board[0][3]==null) board[0][3] = TilesType.values()[getNextType()];

        if(board[2][2]==null) board[2][2] = TilesType.values()[getNextType()];
        if(board[2][6]==null) board[2][6] = TilesType.values()[getNextType()];

        if(board[3][6]==null) board[3][8] = TilesType.values()[getNextType()];

        if(board[5][0]==null) board[5][0] = TilesType.values()[getNextType()];

        if(board[6][2]==null) board[6][2] = TilesType.values()[getNextType()];
        if(board[6][6]==null) board[6][6] = TilesType.values()[getNextType()];

        if(board[8][5]==null) board[8][5] = TilesType.values()[getNextType()];

        return board;
    }
}
