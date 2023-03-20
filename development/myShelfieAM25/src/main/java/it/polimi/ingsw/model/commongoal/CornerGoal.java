package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

/**
 * This is the CornerGoal. It gives points to a player if they have four tiles of the same type in the four
 * corners of the bookshelf
 */
public class CornerGoal extends CommonGoal{
    /**
     * The constructor is the same as the super class
     * @param numPlayers
     * @throws InvalidPlayerNumberException
     */
    public CornerGoal(int numPlayers) throws InvalidPlayerNumberException {
            super(numPlayers);
    }

    /**
     * Checks if the player placed tiles of the same type in the four corners of the bookshelf
     * @param player
     * @return true if the player completed the goal, false if they didn't
     */
    protected boolean checkPoints(Player player){
        TilesType[][] matrixCopy = player.getShelf().clone();

        TilesType topLeft = matrixCopy[0][0];
        return topLeft == matrixCopy[0][4] && topLeft == matrixCopy[5][0] && topLeft == matrixCopy[5][4] && topLeft != null;
    }
}
