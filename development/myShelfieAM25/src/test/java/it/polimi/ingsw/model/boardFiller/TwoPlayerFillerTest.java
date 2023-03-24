package it.polimi.ingsw.model.boardFiller;

import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.boardFiller.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * tester for the two-player board filler
 * @author andreac01
 */
public class TwoPlayerFillerTest {
    private final int boardSize = 29;
    @Test
    @DisplayName("filler remaining tiles test")
    public void testRemainingTile(){
        TilesType[][] board = new TilesType[9][9];
        BoardFiller filler = new TwoPlayerFiller();
        Random random = new Random(System.currentTimeMillis());

        assertEquals(132, filler.getRemainingNumber());
        assertTrue(filler.checkRemaining());

        filler.fill(board);
        assertTrue(filler.checkRemaining());
        assertEquals(103, filler.getRemainingNumber());

        filler.fill(board);
        assertTrue(filler.checkRemaining());
        assertEquals(103, filler.getRemainingNumber());

        board = new TilesType[9][9];
        board[4+random.nextInt()%3][4+random.nextInt(2)] = TilesType.values()[random.nextInt(TilesType.values().length)];
        filler.fill(board);
        assertTrue(filler.checkRemaining());
        assertEquals(75, filler.getRemainingNumber());

        board = new TilesType[9][9];
        board[4][4-random.nextInt(4)]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[1][3]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[5][6]=TilesType.values()[random.nextInt(TilesType.values().length)];
        board[3][7]=TilesType.values()[random.nextInt(TilesType.values().length)];
        filler.fill(board);
        assertEquals(50, filler.getRemainingNumber());

        board = new TilesType[9][9];
        filler.fill(board);
        assertTrue(filler.checkRemaining());
        assertEquals(21, filler.getRemainingNumber());

        board = new TilesType[9][9];
        filler.fill(board);
        assertFalse(filler.checkRemaining());
        assertEquals(0, filler.getRemainingNumber());
    }

    @Test
    @DisplayName("fill() method test - TwoPlayerFiller")
    public void testFill(){
        TilesType[][] board = new TilesType[9][9];
        BoardFiller filler = new TwoPlayerFiller();
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

        //test that there tiles in a wrong cell
        for(int i=0; i<9; i++){
            assertNull(board[0][i]);
            assertNull(board[i][0]);
            assertNull(board[i][8]);
            assertNull(board[8][i]);
        }
        assertNull(board[2][1]);
        assertNull(board[2][6]);
        assertNull(board[7][7]);
        assertNull(board[6][2]);

        //test that tiles already placed in the livingroom remains there after the refill
        board = new TilesType[9][9];
        board[3][5]=TilesType.CATS;
        board[3][6]=TilesType.BOOKS;
        board[4][5]=TilesType.FRAMES;
        board[5][6]=TilesType.GAMES;
        board[4][2]=TilesType.TROPHIES;
        board[7][5]=TilesType.PLANTS;

        board = filler.fill(board);
        assertEquals(board[3][5],TilesType.CATS);
        assertEquals(board[3][6],TilesType.BOOKS);
        assertEquals(board[4][5],TilesType.FRAMES);
        assertEquals(board[5][6],TilesType.GAMES);
        assertEquals(board[4][2],TilesType.TROPHIES);
        assertEquals(board[7][5],TilesType.PLANTS);
    }
}
