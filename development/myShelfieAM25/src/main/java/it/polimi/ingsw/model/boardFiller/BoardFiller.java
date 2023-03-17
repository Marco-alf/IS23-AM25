package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;

import java.util.Random;

import it.polimi.ingsw.model.exception.*;
/**
 * Creating a class that represent an element that is able to randomly refill the game board when requested
 * In order to implement this functionality we'll use a strategy pattern that incrementally fill the board based on the subtype of filler used
 * @author ceru
 */
public abstract class BoardFiller {
    /**
     * remainingTiles represents the constraints on the limited number of tiles existing for each TileType
     * the attribute is static because it is used in all subclasses
     */
    protected int[] remainingTiles;

    /**
     * random is a random number generator used for extracting random TileType
     */
    protected Random random;

    /**
     * public interface of the method used to refill the board
     * @return TilesType[][]    it returns a new filled 9x9 game board
     */
    public abstract TilesType[][] fill(TilesType[][] board) throws NoTileLeftException;

    /**
     * protected function used for getting a random tile type to place on the board
     * @return  index of the tile
     * @throws NoTileLeftException  if the number of generated tile exceeds the number of tile in the game
     */
    protected int getNextType() throws NoTileLeftException{
        int curType;
        curType = random.nextInt(6);
        while(remainingTiles[curType]<=0){
            if(!checkRemaining()) throw new NoTileLeftException();
            curType = random.nextInt(6);
        }
        remainingTiles[curType]--;
        return curType;
    }

    /**
     * private method used to check if no tiles are available to be placed
     * @return true iff there are tiles available
     */
    private boolean checkRemaining(){
        for(int i=0; i<6; i++){
            if (remainingTiles[i]>0){
                return true;
            }
        }
        return false;
    }
}
