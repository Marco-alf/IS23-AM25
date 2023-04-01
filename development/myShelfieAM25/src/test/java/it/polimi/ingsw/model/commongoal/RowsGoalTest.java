package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowsGoalTest {

    @Test
    void checkEmptyShelfRegular() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, true);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = null;
            }
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void checkEmptyShelfDifferent() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, false);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = null;
            }
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void fullShelfSameTilesRegular() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, true);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = TilesType.BOOKS;
            }
        }
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void fullShelfSameTilesDifferent() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, false);
        TilesType[][] matrix = new TilesType[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = TilesType.BOOKS;
            }
        }
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void fourRegularRows() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, true);
        TilesType[][] matrix = new TilesType[][] {
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.PLANTS},
                {TilesType.FRAMES, TilesType.FRAMES, TilesType.GAMES, TilesType.TROPHIES, TilesType.TROPHIES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.TROPHIES},
                {TilesType.TROPHIES, TilesType.PLANTS, TilesType.PLANTS, TilesType.GAMES, TilesType.TROPHIES},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertTrue(commonGoal.checkPoints(matrix));
    }

    @Test
    void threeRegularRows() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, true);
        TilesType[][] matrix = new TilesType[][] {
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.PLANTS},
                {TilesType.FRAMES, TilesType.FRAMES, TilesType.GAMES, TilesType.TROPHIES, TilesType.TROPHIES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.TROPHIES},
                {TilesType.FRAMES, TilesType.PLANTS, TilesType.PLANTS, TilesType.GAMES, TilesType.TROPHIES},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void oneDifferentRow() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, false);
        TilesType[][] matrix = new TilesType[][] {
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.PLANTS},
                {TilesType.FRAMES, TilesType.FRAMES, TilesType.GAMES, TilesType.TROPHIES, TilesType.TROPHIES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.TROPHIES},
                {TilesType.FRAMES, TilesType.CATS, TilesType.PLANTS, TilesType.GAMES, TilesType.TROPHIES},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertFalse(commonGoal.checkPoints(matrix));
    }

    @Test
    void twoDifferentRow() throws InvalidPlayerNumberException {
        CommonGoal commonGoal = new RowsGoal(4, false);
        TilesType[][] matrix = new TilesType[][] {
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.PLANTS},
                {TilesType.FRAMES, TilesType.CATS, TilesType.PLANTS, TilesType.GAMES, TilesType.TROPHIES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.TROPHIES},
                {TilesType.FRAMES, TilesType.CATS, TilesType.PLANTS, TilesType.GAMES, TilesType.TROPHIES},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertTrue(commonGoal.checkPoints(matrix));
    }
}