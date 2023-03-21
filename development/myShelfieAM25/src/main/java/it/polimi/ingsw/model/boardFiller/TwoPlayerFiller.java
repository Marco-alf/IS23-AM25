package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;

import java.util.Random;

import it.polimi.ingsw.model.exception.*;

/**
 * TwoPlayerFiller is the concrete implementation of a BoardFiller responsible for refilling a game board for a two-player game
 * @author ceru
 */
public class TwoPlayerFiller extends BoardFiller{

    /**
     * The constructor of the class initialize an array with the number of remaining tiles
     * The initialization of random using the current system time allows further randomization
     */
    public TwoPlayerFiller(){
        this.remainingTiles = new int[]{22,22,22,22,22,22};
        this.random = new Random(System.currentTimeMillis());
    }

    /**
     * implementation of the fill() method requested by extending the BoardFiller abstract class
     * @return TilesType[][]    it returns a new filled (if possible) 9x9 game board as requested in a two-player type of game
     */
    public TilesType[][] fill(TilesType[][] board){
        int i;

        try {
            if(board[1][3]==null) board[1][3] = TilesType.values()[getNextType()];

        if(board[1][4]==null) board[1][4] = TilesType.values()[getNextType()];

        if(board[2][3]==null) board[2][3] = TilesType.values()[getNextType()];
        if(board[2][4]==null) board[2][4] = TilesType.values()[getNextType()];
        if(board[2][5]==null) board[2][5] = TilesType.values()[getNextType()];

        for(i=2; i<8; i++){
            if(board[3][i]==null) board[3][i] = TilesType.values()[getNextType()];
        }

        for(i=1; i<8; i++){
            if(board[4][i]==null) board[4][i] = TilesType.values()[getNextType()];
        }

        for(i=1; i<7; i++){
            if(board[5][i]==null) board[5][i] = TilesType.values()[getNextType()];
        }

        for(i=3; i<6; i++){
            if(board[6][i]==null) board[6][i] = TilesType.values()[getNextType()];
        }

        if(board[7][4]==null) board[7][4] = TilesType.values()[getNextType()];
        if(board[7][5]==null) board[7][5] = TilesType.values()[getNextType()];

        return board;
        } catch (NoTileLeftException e) {
            return board;
        }
    }
}
