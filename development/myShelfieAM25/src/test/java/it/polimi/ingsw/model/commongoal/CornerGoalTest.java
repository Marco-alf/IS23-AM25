package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CornerGoalTest {

    @Test
    void emptyShelf() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new CornerGoal(4);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = null;
            }
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }
    @Test
    void fullShelfSameType() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new CornerGoal(4);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = TilesType.BOOKS;
            }
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void threeCornersEqual() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new CornerGoal(4);
        TilesType[][] matrix = new TilesType[6][5];
        matrix[0][0] = TilesType.BOOKS;
        matrix[5][4] = TilesType.BOOKS;
        matrix[5][0] = TilesType.BOOKS;
        assertFalse(commonGoal.checkPoints(matrix));
    }

}