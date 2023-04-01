package it.polimi.ingsw.model.commongoal;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class XGoalTest {
    private TilesType[][] shelf;

    @Test //TEST 1
    void threeByThreeSquare() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new XGoal(2);

        shelf = new TilesType[][]{{TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {TilesType.BOOKS, TilesType.BOOKS, TilesType.BOOKS, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 2
    void downRightX() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new XGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, TilesType.CATS, null, TilesType.CATS},
                {null, null, null, TilesType.CATS, null},
                {null, null, TilesType.CATS, null, TilesType.CATS},
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 3
    void emptyShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new XGoal(2);

        shelf = new TilesType[][]{{null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
        };
        assertFalse(cg1.checkPoints(shelf));
    }

    @Test //TEST 4
    void fullShelf() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new XGoal(2);

        shelf = new TilesType[][]{{TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES},
                {TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES, TilesType.GAMES}
        };
        assertTrue(cg1.checkPoints(shelf));
    }

    @Test //TEST 5
    void bigX() throws InvalidPlayerNumberException {
        CommonGoal cg1 = new XGoal(2);

        shelf = new TilesType[][]{{TilesType.GAMES, null, null, null, TilesType.GAMES},
                {null, TilesType.GAMES, null, TilesType.GAMES, null},
                {null, null, TilesType.GAMES, null, null},
                {null, TilesType.GAMES, null, TilesType.GAMES, null},
                {TilesType.GAMES, null, null, null, TilesType.GAMES},
                {null, null, null, null, null}
        };
        assertTrue(cg1.checkPoints(shelf));
    }
}