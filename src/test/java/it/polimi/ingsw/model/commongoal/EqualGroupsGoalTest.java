package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EqualGroupsGoalTest {

    @Test
    void checkEmptyFourTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4, 4, 4);
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
    void checkEmptyTwoTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4, 2, 6);
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
    void checkFourSquaresFourTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,4,4);
            TilesType[][] matrix = new TilesType[][]{{null, null, null, TilesType.BOOKS, TilesType.BOOKS},
                    {null, TilesType.TROPHIES, TilesType.TROPHIES, TilesType.BOOKS, TilesType.BOOKS},
                    {null, TilesType.TROPHIES, TilesType.TROPHIES, null, null},
                    {null, null, null, null, null},
                    {TilesType.GAMES, TilesType.GAMES, null, TilesType.PLANTS, TilesType.PLANTS},
                    {TilesType.GAMES, TilesType.GAMES, null, TilesType.PLANTS, TilesType.PLANTS}
            };
            assertTrue(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void checkEightCoupleTwoTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,2,6);
            TilesType[][] matrix = new TilesType[][]{{null, null, null, TilesType.BOOKS, TilesType.BOOKS},
                    {null, TilesType.TROPHIES, TilesType.TROPHIES, TilesType.FRAMES, TilesType.FRAMES},
                    {null, TilesType.PLANTS, TilesType.PLANTS, null, null},
                    {null, null, null, null, null},
                    {TilesType.TROPHIES, TilesType.GAMES, null, TilesType.PLANTS, TilesType.CATS},
                    {TilesType.TROPHIES, TilesType.GAMES, null, TilesType.PLANTS, TilesType.CATS}
            };
            assertTrue(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }
    @Test
    void checkThreeSquaresFourTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,4,4);
            TilesType[][] matrix = new TilesType[][]{{null, null, null, TilesType.BOOKS, TilesType.BOOKS},
                    {null, TilesType.TROPHIES, TilesType.TROPHIES, TilesType.BOOKS, TilesType.BOOKS},
                    {null, TilesType.TROPHIES, TilesType.TROPHIES, null, null},
                    {null, null, null, null, null},
                    {null, null, null, TilesType.PLANTS, TilesType.PLANTS},
                    {null, null, null, TilesType.PLANTS, TilesType.PLANTS}
            };
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test
    void checkFiveCoupleTwoTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,4,4);
            TilesType[][] matrix = new TilesType[][]{{null, null, null, TilesType.BOOKS, TilesType.CATS},
                    {null, TilesType.GAMES, TilesType.TROPHIES, TilesType.BOOKS, TilesType.CATS},
                    {null, TilesType.GAMES, TilesType.TROPHIES, null, null},
                    {null, null, null, null, null},
                    {null, null, null, null, TilesType.PLANTS},
                    {null, null, null, null, TilesType.PLANTS}
            };
            assertFalse(commonGoal.checkPoints(matrix));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }
    @Test
    void checkFullShelfFourTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,4,4);
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
    void checkFullShelfTwoTiles() {
        try{
            CommonGoal commonGoal = new EqualGroupsGoal(4,2,6);
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
}