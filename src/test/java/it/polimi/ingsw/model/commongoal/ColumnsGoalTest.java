package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnsGoalTest {

    @Test
    void checkEmptyShelfRegular() {
        try{
            CommonGoal commonGoal = new ColumnsGoal(4, true);
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
            CommonGoal commonGoal = new ColumnsGoal(4, false);
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
            CommonGoal commonGoal = new ColumnsGoal(4, true);
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
            CommonGoal commonGoal = new ColumnsGoal(4, false);
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
    void threeRegularColumns() {
        try{
            CommonGoal commonGoal = new ColumnsGoal(4, true);
            TilesType[][] matrix = new TilesType[][]{{TilesType.GAMES, null, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.FRAMES, null, TilesType.BOOKS, TilesType.FRAMES, TilesType.FRAMES},
                    {TilesType.TROPHIES, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.FRAMES},
                    {TilesType.PLANTS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.CATS, TilesType.CATS, TilesType.GAMES, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.BOOKS, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.FRAMES},
            };
            assertTrue(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void twoRegularColumns() {
        try{
            CommonGoal commonGoal = new ColumnsGoal(4, true);
            TilesType[][] matrix = new TilesType[][]{{TilesType.GAMES, null, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.FRAMES, null, TilesType.BOOKS, TilesType.FRAMES, TilesType.FRAMES},
                    {TilesType.TROPHIES, TilesType.FRAMES, TilesType.PLANTS, TilesType.BOOKS, TilesType.FRAMES},
                    {TilesType.PLANTS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.CATS, TilesType.CATS, TilesType.GAMES, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.BOOKS, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.FRAMES},
            };
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void oneDifferentColumn() {
        try{
            CommonGoal commonGoal = new ColumnsGoal(4, false);
            TilesType[][] matrix = new TilesType[][]{{TilesType.GAMES, null, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.FRAMES, null, TilesType.BOOKS, TilesType.FRAMES, TilesType.FRAMES},
                    {TilesType.TROPHIES, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.FRAMES},
                    {TilesType.PLANTS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.CATS, TilesType.CATS, TilesType.GAMES, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.BOOKS, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.FRAMES},
            };
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void twoDifferentColumn() {
        try{
            CommonGoal commonGoal = new ColumnsGoal(4, false);
            TilesType[][] matrix = new TilesType[][]{{TilesType.GAMES, null, TilesType.CATS, TilesType.CATS, TilesType.FRAMES},
                    {TilesType.FRAMES, null, TilesType.BOOKS, TilesType.FRAMES, TilesType.GAMES},
                    {TilesType.TROPHIES, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.BOOKS},
                    {TilesType.PLANTS, TilesType.BOOKS, TilesType.CATS, TilesType.CATS, TilesType.TROPHIES},
                    {TilesType.CATS, TilesType.CATS, TilesType.GAMES, TilesType.CATS, TilesType.PLANTS},
                    {TilesType.BOOKS, TilesType.FRAMES, TilesType.GAMES, TilesType.BOOKS, TilesType.CATS},
            };
            assertTrue(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }
}