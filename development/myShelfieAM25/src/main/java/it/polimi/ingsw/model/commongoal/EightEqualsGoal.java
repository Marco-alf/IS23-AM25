package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

import static it.polimi.ingsw.model.TilesType.*;

public class EightEqualsGoal extends CommonGoal{

    public EightEqualsGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    public boolean checkPoints(Player player) {

        TilesType[][] matrixCopy = player.getShelf().clone();

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
