package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ThreePlayerFillerTest {
    private final int boardSize = 37;
    @Test
    @DisplayName("filler remaining tiles test")
    public void testRemainingTile(){
        TilesType[][] board = new TilesType[9][9];
        BoardFiller filler = new ThreePlayerFiller();
        Random random = new Random(System.currentTimeMillis());
        int remaining = 132;

        assertEquals(remaining, filler.getRemainingNumber());
        assertTrue(filler.checkRemaining());

        //check for empty board
        filler.fill(board);
        remaining-=boardSize;
        assertTrue(filler.checkRemaining());
        assertEquals(remaining, filler.getRemainingNumber());

        //check for full board
        filler.fill(board);
        assertTrue(filler.checkRemaining());
        assertEquals(remaining, filler.getRemainingNumber());

        //check for board with one tile
        board = new TilesType[9][9];
        board[4+random.nextInt()%3][4+random.nextInt(2)] = TilesType.values()[random.nextInt(TilesType.values().length)];
        filler.fill(board);
        assertTrue(filler.checkRemaining());
        remaining=remaining-boardSize+1;
        assertEquals(remaining, filler.getRemainingNumber());

        //check for board with multiple tiles
        board = new TilesType[9][9];
        board[4][4-random.nextInt(4)]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[1][3]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[5][6]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[3][8]=TilesType.values()[random.nextInt(TilesType.values().length)];
        filler.fill(board);
        remaining=remaining-boardSize+4;
        assertEquals(remaining, filler.getRemainingNumber());

        board = new TilesType[9][9];
        filler.fill(board);
        assertFalse(filler.checkRemaining());
        remaining=0;
        assertEquals(remaining, filler.getRemainingNumber());
    }

    @Test
    @DisplayName("fill() method test - TwoPlayerFiller")
    public void testFill(){
        TilesType[][] board = new TilesType[9][9];
        BoardFiller filler = new ThreePlayerFiller();
        Random random = new Random(System.currentTimeMillis());

        board = filler.fill(board);

        //verify that the number of tiles in the board are equal to boardSize
        int count = 0;
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(board[i][j]!=null) count++;
            }
        }
        assertEquals(count, boardSize);

        //verify new positions completeness
        assertNotNull(board[0][3]);
        assertNotNull(board[2][2]);
        assertNotNull(board[2][6]);
        assertNotNull(board[3][8]);
        assertNotNull(board[5][0]);
        assertNotNull(board[6][6]);
        assertNotNull(board[6][2]);
        assertNotNull(board[8][5]);

        //test that there tiles in a wrong cell
        assertNull(board[0][0]);
        assertNull(board[4][8]);
        assertNull(board[1][1]);
        assertNull(board[2][7]);
        assertNull(board[7][7]);
        assertNull(board[7][3]);

        //test that tiles already placed in the livingroom remains there after the refill
        board = new TilesType[9][9];
        board[3][5]=TilesType.CATS;
        board[3][6]=TilesType.BOOKS;
        board[4][5]=TilesType.FRAMES;
        board[6][5]=TilesType.GAMES;
        board[4][4]=TilesType.TROPHIES;
        board[8][5]=TilesType.PLANTS;

        board = filler.fill(board);
        assertEquals(board[3][5],TilesType.CATS);
        assertEquals(board[3][6],TilesType.BOOKS);
        assertEquals(board[4][5],TilesType.FRAMES);
        assertEquals(board[6][5],TilesType.GAMES);
        assertEquals(board[4][4],TilesType.TROPHIES);
        assertEquals(board[8][5],TilesType.PLANTS);
    }
}