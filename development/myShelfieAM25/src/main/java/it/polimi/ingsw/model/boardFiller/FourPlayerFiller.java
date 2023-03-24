package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;

import java.util.Random;

import it.polimi.ingsw.model.exception.*;

/**
 * FourPlayerFiller is the concrete implementation of a BoardFiller responsible for refilling a game board for a four-player game
 * @author andreac01
 */
public class FourPlayerFiller extends ThreePlayerFiller{

    /**
     * The constructor of the class initialize an array with the number of remaining tiles
     * The initialization of random using the current system time allows further randomization
     */
    public FourPlayerFiller(){
        this.remainingTiles = new int[]{22,22,22,22,22,22};
        this.random = new Random(System.currentTimeMillis());
    }

    /**
     * Override of the fill() requested by BoardFiller
     * @return TilesType[][]    it returns a new filled (if possible) 9x9 game board as requested in a three-player type of game
     */
    @Override
    public TilesType[][] fill(TilesType[][] board){
        board = super.fill(board);
        try{
            if(board[0][4]==null) board[0][4] = TilesType.values()[getNextType()];

            if(board[1][5]==null) board[1][5] = TilesType.values()[getNextType()];

            if(board[3][1]==null) board[3][1] = TilesType.values()[getNextType()];

            if(board[4][0]==null) board[4][0] = TilesType.values()[getNextType()];
            if(board[4][8]==null) board[4][8] = TilesType.values()[getNextType()];

            if(board[5][7]==null) board[5][7] = TilesType.values()[getNextType()];

            if(board[7][3]==null) board[7][3] = TilesType.values()[getNextType()];

            if(board[8][4]==null) board[8][4] = TilesType.values()[getNextType()];
            return board;
        }
        catch (NoTileLeftException e){
            return board;
        }
    }
}
