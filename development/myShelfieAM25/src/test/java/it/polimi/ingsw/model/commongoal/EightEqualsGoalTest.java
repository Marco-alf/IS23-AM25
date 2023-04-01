package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.exception.InvalidPlayerNumberException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EightEqualsGoalTest {

    private TilesType[][] shelf;

    @Test //TEST 19
    void eightRandom() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new EightEqualsGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, TilesType.FRAMES},
                {null, TilesType.FRAMES, null, null, null},
                {null, null, TilesType.FRAMES, TilesType.FRAMES, null},
                {TilesType.FRAMES, TilesType.FRAMES, null, null, null},
                {null, TilesType.FRAMES, null, null, null},
                {null, null, TilesType.FRAMES, null, null},
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 20
    void sevenRandom() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new EightEqualsGoal(2);

        shelf = new TilesType[][]{{TilesType.BOOKS, null, null, null, null},
                {null, null, TilesType.BOOKS, null, null},
                {TilesType.BOOKS, null, null, null, null},
                {null, null, null, TilesType.BOOKS, null},
                {null, TilesType.BOOKS, null, null, null},
                {null, null, null, null, TilesType.BOOKS},
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 21
    void emptyShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new EightEqualsGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 22
    void fullShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new EightEqualsGoal(2);

        shelf = new TilesType[][]{{TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES}
        };
        assertTrue(cg1.checkPoints(shelf));
    }
}