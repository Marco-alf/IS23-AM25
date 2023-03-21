package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.exception.InvalidPlayerNumberException;

import static it.polimi.ingsw.model.TilesType.*;

public class TwoDifferentRowsGoal extends CommonGoal{

    public TwoDifferentRowsGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    public boolean checkPoints(Player player) {

        TilesType[][] matrixCopy = player.getShelf().clone();
        int[] occ = new int[]{0,0,0,0,0,0}; //occ[0] = CATS, occ[1] = BOOKS, ..., occ[5] = PLANTS
        int con = 0;
        int sum = 0;
        int line = 0;

        for(int i = 0; i < MAX_ROW; i++){
            for(int j = 0; j < MAX_COLUMN; j++){
                if(matrixCopy[i][j] == CATS) occ[0]++;
                else if(matrixCopy[i][j] == BOOKS) occ[1]++;
                else if(matrixCopy[i][j] == GAMES) occ[2]++;
                else if(matrixCopy[i][j] == FRAMES) occ[3]++;
                else if(matrixCopy[i][j] == TROPHIES) occ[4]++;
                else if(matrixCopy[i][j] == PLANTS) occ[5]++;
            }

            for(int k = 0; k < 6; k++){
                if(occ[k] > 0) {
                    sum += occ[k];
                    con++;
                    occ[k] = 0;
                }
            }

            if(sum == 5 && con == 5) line++;

            sum = 0;
            con = 0;
        }

        return line >= 2;
    }
}
