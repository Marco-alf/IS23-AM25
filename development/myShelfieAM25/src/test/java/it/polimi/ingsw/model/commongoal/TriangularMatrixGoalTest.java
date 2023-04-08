package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TriangularMatrixGoalTest {

    private TilesType[][] shelf;

    @Test //TEST 12
    void leftTriangularMatrix() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
            shelf = new TilesType[][]{{null, null, null, null, null},
                    {TilesType.BOOKS, null, null, null, null},
                    {TilesType.BOOKS, TilesType.BOOKS, null, null, null},
                    {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                    {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null},
                    {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS},
            };
            assertTrue(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 13
    void rightTriangularMatrix() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
            shelf = new TilesType[][]{{null, null, null, null, null},
                    {null, null, null, null, TilesType.CATS},
                    {null, null, null, TilesType.CATS, TilesType.CATS},
                    {null, null, TilesType.CATS, TilesType.CATS, TilesType.CATS},
                    {null, TilesType.CATS, TilesType.CATS, TilesType.CATS, TilesType.CATS},
                    {TilesType.CATS, TilesType.CATS, TilesType.CATS, TilesType.CATS, TilesType.CATS},
            };
            assertTrue(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 14
    void emptyShelf() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
            shelf = new TilesType[][]{{null, null, null, null, null},
                    {null, null, null, null, null},
                    {null, null, null, null, null},
                    {null, null, null, null, null},
                    {null, null, null, null, null},
                    {null, null, null, null, null},
            };
            assertFalse(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 15
    void fullShelf() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
                shelf = new TilesType[][]{{TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES}
            };
            assertFalse(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 16
    void randomLeftTriangularMatrix() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
                shelf = new TilesType[][]{{null, null, null, null, null},
                    {TilesType.BOOKS, null, null, null, null},
                    {TilesType.CATS, TilesType.BOOKS, null, null, null},
                    {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                    {TilesType.BOOKS, TilesType.TROPHIES, TilesType.BOOKS, TilesType.GAMES, null},
                    {TilesType.GAMES, TilesType.BOOKS, TilesType.BOOKS, TilesType.TROPHIES, TilesType.BOOKS},
            };
            assertTrue(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 17
    void randomRightTriangularMatrix() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
            shelf = new TilesType[][]{{null, null, null, null, null},
                    {null, null, null, null, TilesType.TROPHIES},
                    {null, null, null, TilesType.GAMES, TilesType.TROPHIES},
                    {null, null, TilesType.PLANTS, TilesType.FRAMES, TilesType.TROPHIES},
                    {null, TilesType.FRAMES, TilesType.CATS, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.BOOKS, TilesType.GAMES, TilesType.CATS, TilesType.PLANTS, TilesType.FRAMES},
            };
            assertTrue(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }

    @Test //TEST 18
    void randomBigTriangularMatrix() {
        try{
            CommonGoal cg1 = new TriangularMatrixGoal(2);
            shelf = new TilesType[][]{{null, null, null, null, TilesType.CATS},
                    {null, null, null, TilesType.CATS, TilesType.TROPHIES},
                    {null, null, TilesType.CATS, TilesType.GAMES, TilesType.TROPHIES},
                    {null, TilesType.CATS, TilesType.PLANTS, TilesType.FRAMES, TilesType.TROPHIES},
                    {TilesType.CATS, TilesType.FRAMES, TilesType.CATS, TilesType.GAMES, TilesType.GAMES},
                    {TilesType.BOOKS, TilesType.GAMES, TilesType.CATS, TilesType.PLANTS, TilesType.FRAMES},
            };
            assertFalse(cg1.checkPoints(shelf));
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
    }
}