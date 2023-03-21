package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.exception.InvalidPlayerNumberException;

public class TwoEqualSquareGoal extends CommonGoal{

    public TwoEqualSquareGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    public boolean checkPoints(Player player){

        TilesType[][] matrixCopy = player.getShelf().clone();
        int con = 0;

        for(int i = 0; i < MAX_ROW-1; i++) {
            for (int j = 0; j < MAX_COLUMN-1; j++) {
                TilesType start = matrixCopy[i][j];
                if(start != null &&
                    start == matrixCopy[i][j+1] &&
                    start == matrixCopy[i+1][j] &&
                    start == matrixCopy[i+1][j+1]) {
                    con++;
                    j++;
                    matrixCopy[i][j] = null;
                    matrixCopy[i][j+1] = null;
                    matrixCopy[i+1][j] = null;
                    matrixCopy[i+1][j+1] = null;
                }
            }
        }
        return con >= 2;
    }
}
