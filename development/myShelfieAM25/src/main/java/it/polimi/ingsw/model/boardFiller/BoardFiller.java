package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;

import java.util.Random;

import it.polimi.ingsw.model.exception.*;

/**
 * BoardFiller is the abstract class whose subclasses are responsible for refilling the game board with random tiles
 * when ever it is requested by the LivingRoom
 * In order to implement this functionality we'll use a strategy pattern that incrementally fill the board based on the subtype of filler used
 * @author andreac01
 */
public abstract class BoardFiller {
    /**
     * remainingTiles represents the constraints on the number of tiles for each TilesType that are present in the bag
     * the attribute is protected because it is used and accessed in all subclasses
     */
    protected int[] remainingTiles;

    /**
     * random is a random number generator used for extracting random TilesType
     */
    protected Random random;

    /**
     * public signature of the method used to refill the board, this is the abstract method that subclasses have to implement
     * @return TilesType[][]    it returns a new filled (if possible) 9x9 game board
     */
    public abstract TilesType[][] fill(TilesType[][] board);

    /**
     * This methods is used for debugging and returns the number of tiles remaining in the deck.
     * @return total number contained in remaining tiles
     */
    public int getRemainingNumber(){
        int count = 0;
        for(int el : remainingTiles){
            count = count + el;
        }
        return count;
    }
    /**
     * Protected function used for getting a random tile type to place on the board
     * This function guarantees that when a int is returned there where tiles remaining of that type
     * @return  index of the tile to use
     * @throws NoTileLeftException if the number of generated tile exceeds the number of tile in the game
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
     * private method used to check if there is any tile available to be placed
     * @return true only if there are tiles available
     */
    public boolean checkRemaining(){
        for(int i=0; i<6; i++){
            if (remainingTiles[i]>0){
                return true;
            }
        }
        return false;
    }
}
