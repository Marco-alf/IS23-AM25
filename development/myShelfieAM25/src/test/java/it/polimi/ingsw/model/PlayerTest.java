package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNameTest() {
        String name = "Pippo_18781!*£";
        LivingRoom board = null;
        try {
            board = new LivingRoom("6");
            fail();
        } catch (InvalidPlayerNumberException e) {
            assertTrue(true);
        }
        try {
            board = new LivingRoom("3");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        PersonalGoal personalGoal1 = PersonalGoal.PERSONALGOAL5;
        Player p1 = new Player(name, board, personalGoal1);
        assertEquals(p1.getName(), name);
    }

    @Test
    void calculatePersonalPoints() {
        String name = "Pippo";
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        PersonalGoal personalGoal1 = PersonalGoal.PERSONALGOAL5;
        Player p1 = new Player(name, board, personalGoal1);

        //prima mossa
        Tile tileOne = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tileTwo = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tileOne);
        tiles.add(tileTwo);
        try {
            p1.moveTiles(tiles, 2);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        try {
            assertEquals(0, p1.calculatePersonalPoints());
        } catch (OutOfBoundException e) {
            fail();
        }

        //seconda mossa
        Tile tileOne2 = new Tile(TilesType.CATS, 3, 2);
        Tile tileTwo2 = new Tile(TilesType.FRAMES, 4, 2);
        Tile tileThree2 = new Tile(TilesType.PLANTS, 5, 2);
        List<Tile> tiles2 = new ArrayList<>();
        tiles2.add(tileOne2);
        tiles2.add(tileTwo2);
        tiles2.add(tileThree2);

        try {
            p1.moveTiles(tiles2, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        try {
            assertEquals(1, p1.calculatePersonalPoints());
        } catch (OutOfBoundException e) {
            fail();
        }
    }

    @Test
    void calculateCommonPointsTest() {
        String name = "Pippo";
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        PersonalGoal personalGoal1 = PersonalGoal.PERSONALGOAL5;
        Player p1 = new Player(name, board, personalGoal1);

        List<Tile> tiles = new ArrayList<>();
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 0);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.FRAMES, 4, 2);
        tile2 = new Tile(TilesType.CATS, 3, 2);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 1);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.GAMES, 2, 3);
        tile2 = new Tile(TilesType.TROPHIES, 3, 3);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 2);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.BOOKS, 3, 6);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.PLANTS, 6, 5);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.CATS, 6, 3);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 4);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.BOOKS, 3, 5);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 4);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        assertEquals(8, p1.calculateCommonPoints()[0]);
        assertEquals(0, p1.calculateCommonPoints()[1]);
    }

    @Test
    void getShelfTest() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("4");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL3);
        Shelf shelf = new Shelf();

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 5; j++){
                try {
                    assertEquals(shelf.getTile(j, i), p1.getShelf()[i][j]);
                } catch (OutOfBoundException e) {
                    fail();
                }
            }
        }
    }

    @Test
    void moveTilesTest() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles = new ArrayList<>();

        Tile tile1 = new Tile(TilesType.FRAMES, 1, 5);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        assertEquals(TilesType.FRAMES, p1.getShelf()[5][3]);
    }

    @Test
    void moveTilesTestInvalidTileException() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles= new ArrayList<>();

        Tile tile1 = new Tile(TilesType.GAMES, 1, 5);
        tiles.add(tile1);

        try {
            p1.moveTiles(tiles, 3);
            fail();
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            assertTrue(true);
        }
    }

    @Test
    void moveTilesTestNoFreeEdgeException() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles= new ArrayList<>();

        Tile tile1 = new Tile(TilesType.FRAMES, 1, 5);
        Tile tile2 = new Tile(TilesType.BOOKS, 3, 5);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 3);
            fail();
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            assertTrue(true);
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }
    }

    @Test
    void moveTilesTestNotInLineException() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles= new ArrayList<>();

        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.GAMES, 2, 3);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 3);
            fail();
        } catch (NotInLineException e) {
            assertTrue(true);
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }
    }

    @Test
    void moveTilesTestOutOfBoundException() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles= new ArrayList<>();

        Tile tile1 = new Tile(TilesType.TROPHIES, -1, 1);
        Tile tile2 = new Tile(TilesType.GAMES, 2, 10);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 3);
            fail();
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            assertTrue(true);
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }
    }

    @Test
    void moveTilesTestFullColumnException() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles= new ArrayList<>();

        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        tiles.add(tile1);
        tiles.add(tile2);

        try {
            p1.moveTiles(tiles, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles= new ArrayList<>();

        tile1 = new Tile(TilesType.CATS, 3, 2);
        tile2 = new Tile(TilesType.FRAMES, 4, 2);
        Tile tile3 = new Tile(TilesType.PLANTS, 5, 2);
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);

        try {
            p1.moveTiles(tiles, 3);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles= new ArrayList<>();

        tile1 = new Tile(TilesType.GAMES, 2, 3);
        tile2 = new Tile(TilesType.TROPHIES, 3, 3);
        tile3 = new Tile(TilesType.GAMES, 4, 3);
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);

        try {
            p1.moveTiles(tiles, 3);
            fail();
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            assertTrue(true);
        } catch (InvalidTileException e) {
            fail();
        }
    }

    @Test
    void calculateAdjacentPointsTest() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 1, 5));
        tiles.add(new Tile(TilesType.FRAMES, 2, 5));

        try {
            p1.moveTiles(tiles, 0);
        } catch (NotInLineException e) {
            throw new RuntimeException(e);
        } catch (NoFreeEdgeException e) {
            throw new RuntimeException(e);
        } catch (OutOfBoundException e) {
            throw new RuntimeException(e);
        } catch (FullColumnException e) {
            throw new RuntimeException(e);
        } catch (InvalidTileException e) {
            throw new RuntimeException(e);
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 4, 7));
        tiles.add(new Tile(TilesType.FRAMES, 5, 7));

        try {
            p1.moveTiles(tiles, 1);
        } catch (NotInLineException e) {
            throw new RuntimeException(e);
        } catch (NoFreeEdgeException e) {
            throw new RuntimeException(e);
        } catch (OutOfBoundException e) {
            throw new RuntimeException(e);
        } catch (FullColumnException e) {
            throw new RuntimeException(e);
        } catch (InvalidTileException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, p1.calculateAdjacencyPoints());
    }

    @Test
    void calculateAdjacentPointsTestFrames() {
        LivingRoom board = null;
        try {
            board = new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }

        Player p1 = new Player("Pippo", board, PersonalGoal.PERSONALGOAL1);

        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 1, 5));
        tiles.add(new Tile(TilesType.FRAMES, 2, 5));

        try {
            p1.moveTiles(tiles, 0);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 4, 7));
        tiles.add(new Tile(TilesType.FRAMES, 5, 7));

        try {
            p1.moveTiles(tiles, 1);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 4, 1));

        try {
            p1.moveTiles(tiles, 2);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(TilesType.FRAMES, 4, 6));
        tiles.add(new Tile(TilesType.GAMES, 5, 6));

        try {
            p1.moveTiles(tiles, 2);
        } catch (NotInLineException e) {
            fail();
        } catch (NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException e) {
            fail();
        } catch (FullColumnException e) {
            fail();
        } catch (InvalidTileException e) {
            fail();
        }

        assertEquals(8, p1.calculateAdjacencyPoints());
    }
}