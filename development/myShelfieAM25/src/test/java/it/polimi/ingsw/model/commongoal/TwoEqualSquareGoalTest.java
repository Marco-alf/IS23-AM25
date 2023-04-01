package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TwoEqualSquareGoalTest {
    private TilesType[][] shelf;

    @Test //TEST 6
    void threeByThreeSquare() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 7
    void twoRandomSquares() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, TilesType.TROPHIES, TilesType.TROPHIES, null, null},
                {null, TilesType.TROPHIES, TilesType.TROPHIES, null, null},
                {null, null, null, null, null},
                {null, null, null, TilesType.PLANTS, TilesType.PLANTS},
                {null, null, null, TilesType.PLANTS, TilesType.PLANTS}
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 8
    void overlappingSquares() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, TilesType.TROPHIES, TilesType.TROPHIES, null, null},
                {null, TilesType.TROPHIES, TilesType.TROPHIES, TilesType.TROPHIES, null},
                {null, null, TilesType.TROPHIES, TilesType.TROPHIES, null},
                {null, null, null, null, null}
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 9
    void fullShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES}
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 10
    void emptyShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 11
    void adjacentSquares() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new TwoEqualSquareGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {null, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
        };
        assertTrue(cg1.checkPoints(shelf));
    }
}