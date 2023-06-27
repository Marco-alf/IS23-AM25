package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void getterTest() {
        int x = 3;
        int y = 4;
        TilesType type = TilesType.PLANTS;
        Tile tile = new Tile(type, x, y);
        assertEquals(x, tile.getPosX());
        assertEquals(y, tile.getPosY());
        assertEquals(type, tile.getType());
        assertNotEquals(15, tile.getPosX());
    }

    @Test
    void testEquals() {
        int x1 = 3;
        int y1 = 4;
        TilesType type1 = TilesType.PLANTS;
        Tile tile1 = new Tile(type1, x1, y1);
        int x2 = 3;
        int y2 = 4;
        TilesType type2 = TilesType.PLANTS;
        Tile tile2 = new Tile(type2, x2, y2);
        assertTrue(tile1.equals(tile2));
        assertTrue(tile2.equals(tile1));
        int x3 = 4;
        int y3 = 4;
        TilesType type3 = TilesType.GAMES;
        Tile tile3 = new Tile(type3, x3, y3);
        assertFalse(tile3.equals(tile2));
    }

    @Test
    void testHashCode() {
        int x1 = 1;
        int y1 = 2;
        TilesType type1 = TilesType.GAMES;
        Tile tile1 = new Tile(type1, x1, y1);
        int x2 = 1;
        int y2 = 2;
        TilesType type2 = TilesType.GAMES;
        Tile tile2 = new Tile(type2, x2, y2);
        assertEquals(tile1.hashCode(), tile2.hashCode());
        int x3 = 4;
        int y3 = 4;
        TilesType type3 = TilesType.BOOKS;
        Tile tile3 = new Tile(type3, x3, y3);
        assertNotEquals(tile3.hashCode(), tile1.hashCode());
    }
}