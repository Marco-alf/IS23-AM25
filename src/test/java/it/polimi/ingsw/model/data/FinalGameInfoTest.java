package it.polimi.ingsw.model.data;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinalGameInfoTest {

    @Test
    void finalGameInfo() {
        String name1 = "pippo";
        String name2 = "pluto";
        String name3 = "paperino";
        List<String> players = new ArrayList<>();
        players.add(name1);
        players.add(name2);
        players.add(name3);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        try {
            game.updateCurrentPlayer(name3);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        //#1
        Tile tile1 = new Tile(TilesType.FRAMES, 1, 4);
        Tile tile2 = new Tile(TilesType.FRAMES, 1, 5);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 0, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#2
        tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        tile2 = new Tile(TilesType.FRAMES, 4, 1);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 1, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#3
        tile1 = new Tile(TilesType.FRAMES, 4, 2);
        tile2 = new Tile(TilesType.CATS, 3, 2);
        Tile tile3 = new Tile(TilesType.PLANTS, 5, 2);
        tiles = new ArrayList<>();
        tiles.add(tile3);
        tiles.add(tile2);
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 2, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#4
        tile1 = new Tile(TilesType.GAMES, 2, 3);
        tile2 = new Tile(TilesType.TROPHIES, 3, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#5
        tile1 = new Tile(TilesType.GAMES, 4, 3);
        tile2 = new Tile(TilesType.FRAMES, 5, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#6
        tile1 = new Tile(TilesType.TROPHIES, 2, 4);
        tile2 = new Tile(TilesType.BOOKS, 3, 4);
        tile3 = new Tile(TilesType.PLANTS, 4, 4);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            game.moveTiles(tiles, 4, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#7
        tile1 = new Tile(TilesType.FRAMES, 4, 7);
        tile2 = new Tile(TilesType.FRAMES, 5, 7);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 4, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#8
        tile1 = new Tile(TilesType.CATS, 6, 3);
        tile2 = new Tile(TilesType.TROPHIES, 7, 3);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 3, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#9
        tile1 = new Tile(TilesType.CATS, 5, 4);
        tile2 = new Tile(TilesType.BOOKS, 6, 4);
        tile3 = new Tile(TilesType.PLANTS, 7, 4);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            game.moveTiles(tiles, 2, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#10
        tile1 = new Tile(TilesType.FRAMES, 2, 5);
        tile2 = new Tile(TilesType.BOOKS, 3, 5);
        tile3 = new Tile(TilesType.FRAMES, 4, 5);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            game.moveTiles(tiles, 0, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#11
        tile1 = new Tile(TilesType.BOOKS, 3, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 0, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#12
        tile1 = new Tile(TilesType.FRAMES, 4, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 4, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#13
        tile1 = new Tile(TilesType.GAMES, 5, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 1, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#14
        tile1 = new Tile(TilesType.FRAMES, 5, 5);
        tile2 = new Tile(TilesType.PLANTS, 6, 5);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 1, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        //#15
        tile1 = new Tile(TilesType.PLANTS, 2, 6);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            game.moveTiles(tiles, 1, name3);
        } catch (InvalidPlayerNameException | NotInLineException | NoFreeEdgeException | OutOfBoundException |
                 FullColumnException | NullTilesException | PlayerNotOnlineException | InvalidTileException |
                 PlayerNotCurrentException e) {
            fail();
        }
        FinalGameInfo finale = null;
        try {
            finale = new FinalGameInfo(game);
        } catch (InvalidPlayerNameException e) {
            fail();
        }
        assertEquals(1, (finale.getFinalPoints().get(name3)).get(4));
        assertEquals(0, (finale.getFinalPoints().get(name1)).get(4));
        assertEquals(0, (finale.getFinalPoints().get(name2)).get(4));
        assertEquals(0, (finale.getFinalPoints().get(name3)).get(0));
        assertEquals(8, (finale.getFinalPoints().get(name3)).get(1));
        assertEquals(1, (finale.getFinalPoints().get(name3)).get(2));
        assertEquals(8, (finale.getFinalPoints().get(name3)).get(3));
    }

}