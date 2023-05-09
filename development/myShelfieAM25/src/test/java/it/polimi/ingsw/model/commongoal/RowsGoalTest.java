package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowsGoalTest {

    @Test
    void checkEmptyShelfRegular() {
        try{
            CommonGoal commonGoal = new RowsGoal(4, true);
            TilesType[][] matrix = new TilesType[6][5];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    matrix[i][j] = null;
                }
            }
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void checkEmptyShelfDifferent() {
        try{
            CommonGoal commonGoal = new RowsGoal(4, false);
            TilesType[][] matrix = new TilesType[6][5];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    matrix[i][j] = null;
                }
            }
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void fullShelfSameTilesRegular() {
        try{
            CommonGoal commonGoal = new RowsGoal(4, true);
            TilesType[][] matrix = new TilesType[6][5];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    matrix[i][j] = TilesType.BOOKS;
                }
            }
            assertTrue(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void fullShelfSameTilesDifferent() {
        try{
            CommonGoal commonGoal = new RowsGoal(4, false);
            TilesType[][] matrix = new TilesType[6][5];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    matrix[i][j] = TilesType.BOOKS;
                }
            }
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void fourRegularRows() {
        try{
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
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void threeRegularRows() {
        try{
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
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void oneDifferentRow() {
        try{
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
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void twoDifferentRow() {
        try{
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
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }
}