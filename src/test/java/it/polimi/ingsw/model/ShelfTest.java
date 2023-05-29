package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.FullColumnException;
import it.polimi.ingsw.exception.OutOfBoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {

    @Test
    void getxBoundTest() {
        Shelf shelf = new Shelf();
        assertEquals(shelf.getxBound(),5);
    }

    @Test
    void getyBoundTest() {
        Shelf shelf = new Shelf();
        assertEquals(shelf.getyBound(),6);
    }

    @Test
    void addTest() {
        Shelf shelf = new Shelf();
        Random rand = new Random(System.currentTimeMillis());
        TilesType tileOne = TilesType.values()[rand.nextInt(0,6)];
        TilesType tileTwo = TilesType.values()[rand.nextInt(0,6)];
        TilesType tileThree = TilesType.values()[rand.nextInt(0,6)];
        List<TilesType> tiles = new ArrayList<>();

        try{
            shelf.add(tiles, -1);
            fail();
        } catch (OutOfBoundException e) {
            assertTrue(true);
        } catch (FullColumnException e) {
            fail();
        }
        try{
            shelf.add(tiles, 5);
            fail();
        } catch (OutOfBoundException e) {
            assertTrue(true);
        } catch (FullColumnException e) {
            fail();
        }

        int column = rand.nextInt(0,5);
        try{
            shelf.add(tiles, column);
            assertNull(shelf.getTile(column,0));
        } catch (OutOfBoundException | FullColumnException e) {
            fail();
        }

        shelf = new Shelf();
        column = rand.nextInt(0,5);
        tiles.add(tileOne);
        tiles.add(tileTwo);
        tiles.add(tileThree);
        try{
            shelf.add(tiles, column);
            for (TilesType t: tiles) {
                assertEquals(t, shelf.getTile(column, shelf.getyBound() -1 - tiles.indexOf(t)));
            }
        } catch (OutOfBoundException | FullColumnException e) {
            fail();
        }
        try{
            shelf.add(tiles, column);
            for (TilesType t: tiles) {
                assertEquals(t, shelf.getTile(column, shelf.getyBound() -1 - tiles.indexOf(t)));
            }
        } catch (OutOfBoundException | FullColumnException e) {
            fail();
        }
        tiles = new ArrayList<>();
        tiles.add(TilesType.values()[rand.nextInt(0,6)]);
        try{
            shelf.add(tiles, column);
            fail();
        } catch (OutOfBoundException e){
            fail();
        }
        catch (FullColumnException e) {
            assertTrue(true);
        }
    }

    @Test
    void calculatePointsTest() {
        Shelf shelf = new Shelf();
        try{
            ArrayList<TilesType> tiles = new ArrayList<>();
            tiles.add(TilesType.TROPHIES);
            shelf.add(tiles, 0);
            shelf.add(tiles, 0);
            shelf.add(tiles, 0);
            shelf.add(tiles, 1);
            shelf.add(tiles, 1);
            shelf.add(tiles, 2);
            tiles = new ArrayList<>();
            tiles.add(TilesType.CATS);
            shelf.add(tiles, 3);
            shelf.add(tiles, 4);
            shelf.add(tiles, 2);
            shelf.add(tiles, 3);
            shelf.add(tiles, 4);
            tiles = new ArrayList<>();
            tiles.add(TilesType.GAMES);
            shelf.add(tiles, 1);
            shelf.add(tiles, 3);
            tiles = new ArrayList<>();
            tiles.add(TilesType.TROPHIES);
            shelf.add(tiles, 2);
            tiles = new ArrayList<>();
            tiles.add(TilesType.FRAMES);
            shelf.add(tiles, 0);
            shelf.add(tiles, 2);
            tiles = new ArrayList<>();
            tiles.add(TilesType.BOOKS);
            shelf.add(tiles, 0);
            shelf.add(tiles, 1);
            shelf.add(tiles, 3);
            tiles = new ArrayList<>();
            tiles.add(TilesType.CATS);
            shelf.add(tiles, 3);
            tiles = new ArrayList<>();
            tiles.add(TilesType.PLANTS);
            shelf.add(tiles, 0);
            shelf.add(tiles, 1);
            shelf.add(tiles, 1);
            shelf.add(tiles, 2);
            shelf.add(tiles, 2);
            assertEquals(shelf.calculatePoints(),18);
            TilesType[][] copy=shelf.getShelf();
            /*
            for(int i=0; i<6; i++){
                for(int j=0; j<5; j++){
                    if(copy[i][j]==null) System.out.print(" ");
                    else{
                        switch(copy[i][j]){
                            case GAMES -> System.out.print("g");
                            case CATS -> System.out.print("c");
                            case BOOKS -> System.out.print("b");
                            case FRAMES -> System.out.print("f");
                            case TROPHIES -> System.out.print("t");
                            case PLANTS -> System.out.print("p");
                        }
                    }
                }
                System.out.print("\n");
            }*/
        } catch (OutOfBoundException | FullColumnException e){
            fail();
        }
    }

    @Test
    void getShelfTest() {
        Shelf shelf = new Shelf();
        Random rand = new Random(System.currentTimeMillis());
        TilesType tileOne = TilesType.values()[rand.nextInt(0,6)];
        TilesType tileTwo = TilesType.values()[rand.nextInt(0,6)];
        TilesType tileThree = TilesType.values()[rand.nextInt(0,6)];
        List<TilesType> tiles = new ArrayList<>();
        int column1 = rand.nextInt(0,5);
        int column2 = rand.nextInt(0,5);
        tiles.add(tileOne);
        tiles.add(tileTwo);
        tiles.add(tileThree);

        try {
            shelf.add(tiles, column1);
        } catch (OutOfBoundException | FullColumnException e) {
            fail();
        }
        try {
            shelf.add(tiles, column2);
        } catch (OutOfBoundException | FullColumnException e) {
            fail();
        }

        TilesType[][] shelfCopy = shelf.getShelf();
        for(int i=0; i<3; i++){
            try{
                assertEquals(shelf.getTile(column1, i),shelfCopy[i][column1]);
                assertEquals(shelf.getTile(column2, i),shelfCopy[i][column2]);
            }catch (OutOfBoundException e){
                fail();
            }
        }
    }
}