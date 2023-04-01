package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.model.TilesType.*;

/**
 * This is the EightEqualsGoal. It gives points to a player if they have eight tiles of the same type,
 * they can be in any position.
 */
public class EightEqualsGoal extends CommonGoal{

    /**
     * The constructor is the same as the super class
     * @param numPlayers is then number of players in the game
     * @throws InvalidPlayerNumberException if the number of players is not valid
     */
    public EightEqualsGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    /**
     * Checks if the player placed eight tiles of the same type
     * @param shelf is a TilesType matrix
     * @return true if the player completed the goal, false if they didn't
     */
    public boolean checkPoints(TilesType[][] shelf) {

        TilesType[][] matrixCopy = shelf.clone();

        int[] occ = new int[]{0,0,0,0,0,0}; //occ[0] = CATS, occ[1] = BOOKS, ..., occ[5] = PLANTS

        for(int i = 0; i < MAX_ROW; i++){
            for(int j = 0; j < MAX_COLUMN; j++){
                if(matrixCopy[i][j] == CATS) occ[0]++;
                else if(matrixCopy[i][j] == BOOKS) occ[1]++;
                else if(matrixCopy[i][j] == GAMES) occ[2]++;
    			else if(matrixCopy[i][j] == FRAMES) occ[3]++;
                else if(matrixCopy[i][j] == TROPHIES) occ[4]++;
                else if(matrixCopy[i][j] == PLANTS) occ[5]++;
            }
        }

        for(int k = 0; k < 6; k++){
            if(occ[k] >= 8) return true;
        }
        return false;
    }
}
