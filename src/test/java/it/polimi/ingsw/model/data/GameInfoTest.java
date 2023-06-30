package it.polimi.ingsw.model.data;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameInfoTest {

    @Test
    void gameInfo() {
        String n1 = "pippo";
        String n2 = "paperino";
        List<String> players = new ArrayList<>();
        players.add(n1);
        players.add(n2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        GameInfo info = new GameInfo(game);
        assertEquals(n1, info.getOnlinePlayers().get(0));
        assertEquals(n2, info.getOnlinePlayers().get(1));
        assertEquals(n1, info.getCurrentPlayer());
        assertNotEquals(n2, info.getCurrentPlayer());
        assertNotNull(info.getNewBoard()[1][3]);
        assertNotNull(info.getNewBoard()[1][4]);
        assertEquals(TilesType.CATS, info.getNewBoard()[2][3]);
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            game.moveTiles(tiles, 0, n1);
        } catch (InvalidPlayerNameException | PlayerNotCurrentException | NotInLineException | NoFreeEdgeException |
                 OutOfBoundException | FullColumnException | NullTilesException | PlayerNotOnlineException |
                 InvalidTileException e) {
            fail();
        }
        info = new GameInfo(game);
        assertEquals(TilesType.TROPHIES, info.getShelf()[5][0]);
        assertEquals(TilesType.FRAMES, info.getShelf()[4][0]);
        assertEquals(0, info.getCommonGoal1Points());
        assertEquals(0, info.getCommonGoal2Points());
        assertEquals(0, info.getPersonalGoalPoints());
        assertEquals(0, info.getAdjPoints());
        assertFalse(info.isGameEnded());
        assertNull(info.getNewBoard()[1][3]);
        assertNull(info.getNewBoard()[1][4]);
        assertEquals(TilesType.CATS, info.getNewBoard()[2][3]);
    }

}