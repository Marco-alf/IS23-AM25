package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.exception.InvalidPlayerNumberException;

public class XGoal extends CommonGoal{

    public XGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    public boolean checkPoints(Player player) {

        TilesType[][] matrixCopy = player.getShelf().clone();

        for(int i = 0; i < MAX_ROW-2; i++){
            for(int j = 0; j < MAX_COLUMN-2; j++){
                TilesType start = matrixCopy[i][j];
                if (start != null &&
                    start == matrixCopy[i][j + 2] &&
                    start == matrixCopy[i + 2][j] &&
                    start == matrixCopy[i + 1][j + 1] &&
                    start == matrixCopy[i + 2][j + 2]) {
                    return true;
                }
            }
        }
        return false;
    }
}

