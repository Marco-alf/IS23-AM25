package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.exception.InvalidPlayerNumberException;

public class TriangularMatrixGoal extends CommonGoal{

    public TriangularMatrixGoal(int numPlayers) throws InvalidPlayerNumberException {
        super(numPlayers);
    }

    public boolean checkPoints(TilesType[][] shelf) {

        TilesType[][] matrixCopy = shelf.clone();

        return (matrixCopy[1][0] != null && matrixCopy[2][1] != null && matrixCopy[3][2] != null && matrixCopy[4][3] != null && matrixCopy[5][4] != null &&
                matrixCopy[0][0] == null && matrixCopy[1][1] == null && matrixCopy[2][2] == null && matrixCopy[3][3] == null && matrixCopy[4][4] == null) ||
                (matrixCopy[5][0] != null && matrixCopy[4][1] != null && matrixCopy[3][2] != null && matrixCopy[2][3] != null &&
                matrixCopy[1][4] != null && matrixCopy[4][0] == null && matrixCopy[3][1] == null && matrixCopy[2][2] == null && matrixCopy[1][3] == null && matrixCopy[0][4] == null);
    }
}
