package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FullDiagonalGoalTest {

    @Test
    void emptyShelf() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = null;
            }
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void checkDiagonal1() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 5; i++) {
            matrix[i][i] = TilesType.BOOKS;
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void checkDiagonal2() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 5; i++) {
            matrix[5 - i][i] = TilesType.BOOKS;
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void checkDiagonal3() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 5; i++) {
            matrix[5 - i][4 - i] = TilesType.BOOKS;
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }


    @Test
    void checkDiagonal4() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 5; i++) {
            matrix[i][4 - i] = TilesType.BOOKS;
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void fullShelf() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = TilesType.BOOKS;
            }
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void oneColumn() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new FullDiagonalGoal(4);
        TilesType[][] matrix = new TilesType[6][5];

        for (int i = 0; i < 6; i++) {
            matrix[i][0] = TilesType.BOOKS;
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }
}