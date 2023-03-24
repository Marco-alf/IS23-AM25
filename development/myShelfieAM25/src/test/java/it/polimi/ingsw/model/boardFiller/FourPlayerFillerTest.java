package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FourPlayerFillerTest {
    private final int boardSize = 45;
    @Test
    @DisplayName("filler remaining tiles test")
    public void testRemainingTile(){
        TilesType[][] board = new TilesType[9][9];
        BoardFiller filler = new FourPlayerFiller();
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

        //check for board with multiple tiles
        board = new TilesType[9][9];
        board[4][4-random.nextInt(5)]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[0][4]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[6][7]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[3][1]=TilesType.values()[random.nextInt(TilesType.values().length)];
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
        BoardFiller filler = new FourPlayerFiller();
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
        assertNotNull(board[0][4]);
        assertNotNull(board[1][5]);
        assertNotNull(board[3][1]);
        assertNotNull(board[4][8]);
        assertNotNull(board[4][0]);
        assertNotNull(board[5][7]);
        assertNotNull(board[7][3]);
        assertNotNull(board[8][4]);

        //test that there tiles in a wrong cell
        assertNull(board[2][1]);
        assertNull(board[3][7]);
        assertNull(board[6][1]);
        assertNull(board[8][3]);
        assertNull(board[7][6]);
        assertNull(board[5][8]);

        //test that tiles already placed in the livingroom remains there after the refill
        board = new TilesType[9][9];
        board[3][4]=TilesType.CATS;
        board[2][5]=TilesType.BOOKS;
        board[4][8]=TilesType.FRAMES;
        board[8][4]=TilesType.GAMES;
        board[5][6]=TilesType.TROPHIES;
        board[4][7]=TilesType.PLANTS;

        board = filler.fill(board);
        assertEquals(board[3][4],TilesType.CATS);
        assertEquals(board[2][5],TilesType.BOOKS);
        assertEquals(board[4][8],TilesType.FRAMES);
        assertEquals(board[8][4],TilesType.GAMES);
        assertEquals(board[5][6],TilesType.TROPHIES);
        assertEquals(board[4][7],TilesType.PLANTS);
    }
}