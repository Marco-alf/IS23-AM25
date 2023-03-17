package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

public class CornerGoal extends CommonGoal{
        public CornerGoal(int numPlayers) throws InvalidPlayerNumberException {
            super(numPlayers);
        }

        public boolean checkPoints(Player player){
            TilesType[][] matrixCopy = player.getShelf().clone();

            TilesType topLeft = matrixCopy[0][0];
            return topLeft == matrixCopy[0][4] && topLeft == matrixCopy[5][0] && topLeft == matrixCopy[5][4] && topLeft != null;
        }
}
