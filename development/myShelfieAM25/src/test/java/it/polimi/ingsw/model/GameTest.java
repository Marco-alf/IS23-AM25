package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.commongoal.CommonGoal;
import it.polimi.ingsw.model.commongoal.RowsGoal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void updateCurrentPlayerTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        String name3 = "qui";
        String name4 = "quo";
        String name5 = "qua";
        String name6 = null;
        Game game = null;
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name6);
        try {
            game = new Game(players, true);
            fail();
        } catch (ShelfCreationException | NotTestException e) {
            fail();
        } catch (PlayersEmptyException e) {
            assertTrue(true);
        }
        players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        players.add(name3);
        players.add(name4);
        players.add(name5);
        try {
            game = new Game(players, true);
            fail();
        } catch (PlayersEmptyException | NotTestException e) {
            fail();
        } catch (ShelfCreationException e) {
            assertTrue(true);
        }
        players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        try {
            game.updateCurrentPlayer(name2);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        assertEquals(name2, game.getCurrentPlayer());
    }

    @Test
    void moveTilesTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(TilesType.TROPHIES, game.getShelf()[5][3]);
        assertEquals(TilesType.FRAMES, game.getShelf()[4][3]);
        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.FRAMES, 4, 2);
        tile2 = new Tile(TilesType.PLANTS, 5, 2);
        Tile tile3 = new Tile(TilesType.CATS, 3, 2);
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(TilesType.FRAMES, game.getShelf()[3][3]);
        assertEquals(TilesType.PLANTS, game.getShelf()[2][3]);
        assertEquals(TilesType.CATS, game.getShelf()[1][3]);
        tiles = new ArrayList<>();
        tile1 = null;
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 4, name1);
            fail();
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        } catch (NullTilesException e) {
            assertTrue(true);
        }
        tiles = new ArrayList<>();
        tile1 = new Tile(TilesType.FRAMES, 4, 2);
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 4, name1);
            fail();
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        try {
            game.updateCurrentPlayer(name2);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        try {
            game.moveTiles(tiles, 4, name1);
            fail();
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 NullPointerException e) {
            fail();
        } catch (PlayerNotCurrentException e) {
            assertTrue(true);
        }
    }

    @Test
    void getOnlinePlayersTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        assertEquals(players, game.getOnlinePlayers());
    }

    @Test
    void getCurrentPlayerTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, false);
            fail();
        } catch (ShelfCreationException | PlayersEmptyException e) {
            fail();
        } catch (NotTestException e) {
            assertTrue(true);
        }
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        assertEquals(name1, game.getCurrentPlayer());
        try {
            game.updateCurrentPlayer(name2);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        assertEquals(name2, game.getCurrentPlayer());
    }

    @Test
    void getShelfTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 5; j++){
                assertNull(game.getShelf()[i][j]);
            }
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 7, 3);
        Tile tile2 = new Tile(TilesType.PLANTS, 7, 4);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(TilesType.TROPHIES, game.getShelf()[5][3]);
        assertEquals(TilesType.PLANTS, game.getShelf()[4][3]);
    }

    @Test
    void getShelvesTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(TilesType.TROPHIES, game.getShelves().get(name1)[5][3]);
        assertEquals(TilesType.FRAMES, game.getShelves().get(name1)[4][3]);
        try {
            game.updateCurrentPlayer(name2);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        tile1 = new Tile(TilesType.CATS, 6, 3);
        tile2 = new Tile(TilesType.TROPHIES, 7, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 0, name2);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(TilesType.CATS, game.getShelves().get(name2)[5][0]);
        assertEquals(TilesType.TROPHIES, game.getShelves().get(name2)[4][0]);
        assertEquals(TilesType.TROPHIES, game.getShelves().get(name1)[5][3]);
        assertEquals(TilesType.FRAMES, game.getShelves().get(name1)[4][3]);
    }

    @Test
    void getAdjPointsTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.FRAMES, 1, 4);
        Tile tile2 = new Tile(TilesType.FRAMES, 1, 5);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.FRAMES, 4, 7);
        tile2 = new Tile(TilesType.FRAMES, 5, 7);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 2, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.FRAMES, 4, 6);
        tile2 = new Tile(TilesType.GAMES, 5, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 1, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(5, game.getAdjPoints());
        tile1 = new Tile(TilesType.BOOKS, 3, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 0, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.BOOKS, 3, 5);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 0, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.BOOKS, 3, 4);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 0, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(7, game.getAdjPoints());
        try {
            game.updateCurrentPlayer(name2);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        assertEquals(0, game.getAdjPoints());
    }

    @Test
    void getPersonalPointsTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 2, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.FRAMES, 4, 7);
        tile2 = new Tile(TilesType.FRAMES, 5, 7);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 1, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.GAMES, 2, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 1, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(2, game.getPersonalPoints());
    }

    @Test
    void getCommonGoalsTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        String name3 = "paperino";
        String name4 = "minnie";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        players.add(name3);
        players.add(name4);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        assertEquals("RowsGoal (isRegular: false)", game.getCommonGoals().get(0));
        assertEquals("FullDiagonalGoal", game.getCommonGoals().get(1));
    }

    @Test
    void getPersonalGoalsTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        String name3 = "paperino";
        String name4 = "minnie";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        players.add(name3);
        players.add(name4);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        assertEquals(PersonalGoal.PERSONALGOAL1, game.getPersonalGoals().get(name1));
        assertEquals(PersonalGoal.PERSONALGOAL2, game.getPersonalGoals().get(name2));
        assertEquals(PersonalGoal.PERSONALGOAL3, game.getPersonalGoals().get(name3));
        assertEquals(PersonalGoal.PERSONALGOAL4, game.getPersonalGoals().get(name4));
    }

    @Test
    void getCommonGoal2PointsTest() {
        String name1 = "pippo";
        String name2 = "pluto";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.FRAMES, 1, 4);
        Tile tile2 = new Tile(TilesType.FRAMES, 1, 5);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 0, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        tile2 = new Tile(TilesType.FRAMES, 4, 1);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 1, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.FRAMES, 4, 2);
        tile2 = new Tile(TilesType.CATS, 3, 2);
        Tile tile3 = new Tile(TilesType.PLANTS, 5, 2);
        tiles = new ArrayList<>();
        tiles.add(tile3);
        tiles.add(tile2);
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 2, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.GAMES, 2, 3);
        tile2 = new Tile(TilesType.TROPHIES, 3, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.GAMES, 4, 3);
        tile2 = new Tile(TilesType.FRAMES, 5, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.TROPHIES, 2, 4);
        tile2 = new Tile(TilesType.BOOKS, 3, 4);
        tile3 = new Tile(TilesType.PLANTS, 4, 4);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            game.moveTiles(tiles, 4, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        tile1 = new Tile(TilesType.FRAMES, 4, 7);
        tile2 = new Tile(TilesType.FRAMES, 5, 7);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 4, name1);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        assertEquals(8, game.getCommonGoal2Points());
    }
}