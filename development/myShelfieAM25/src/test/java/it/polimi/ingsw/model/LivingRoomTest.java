package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LivingRoomTest {

    @Test
    void getEnumArray() {
        LivingRoom board = null;
        try {
            board = new LivingRoom(6);
            fail();
        } catch (InvalidPlayerNumberException ignored) {
        }
        try {
            board = new LivingRoom(4);
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        TilesType[][] returnedBoard = board.getEnumArray();
        assertNull(returnedBoard[2][1]);
        assertNull(returnedBoard[7][2]);
        assertNull(returnedBoard[8][6]);
        assertNull(returnedBoard[6][7]);
        assertNull(returnedBoard[2][7]);
        assertNull(returnedBoard[6][0]);
        assertNotNull(returnedBoard[0][4]);
        assertNotNull(returnedBoard[6][4]);
        assertNotNull(returnedBoard[6][2]);

        try {
            board = new LivingRoom(3);
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        returnedBoard = board.getEnumArray();
        assertNull(returnedBoard[1][2]);
        assertNull(returnedBoard[7][1]);
        assertNull(returnedBoard[8][6]);
        assertNull(returnedBoard[6][7]);
        assertNull(returnedBoard[2][7]);
        assertNull(returnedBoard[6][0]);
        assertNull(returnedBoard[0][4]);
        assertNotNull(returnedBoard[5][4]);
        assertNotNull(returnedBoard[6][2]);

        try {
            board = new LivingRoom(2);
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        returnedBoard = board.getEnumArray();
        assertNull(returnedBoard[2][2]);
        assertNull(returnedBoard[7][1]);
        assertNull(returnedBoard[8][5]);
        assertNull(returnedBoard[6][7]);
        assertNull(returnedBoard[2][7]);
        assertNull(returnedBoard[6][0]);
        assertNull(returnedBoard[0][4]);
        assertNotNull(returnedBoard[5][4]);
        assertNull(returnedBoard[6][2]);
    }

    @Test
    void takeTiles() {

        LivingRoom board = null;
        try {
            board = new LivingRoom(2);
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        TilesType[][] returnedBoard = board.getEnumArray();
        List<Tile> tiles = new ArrayList<>();
        List<TilesType> returnedTiles;
        try {
            returnedTiles = board.takeTiles(tiles);
            assertEquals(0, returnedTiles.size());
        } catch (NotInLineException | NoFreeEdgeException | OutOfBoundException | InvalidTileException e) {
            fail();
        }

        //test for exceptions
        tiles.add(new Tile(TilesType.BOOKS, 9, 0));
        try {
            board.takeTiles(tiles);
            fail();
        } catch (NotInLineException | InvalidTileException | NoFreeEdgeException e) {
            fail();
        } catch (OutOfBoundException ignored) {
        }

        Random rand = new Random(1);
        do{
            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.values()[rand.nextInt(6)], 3, 6));
        } while(returnedBoard[6][3]==tiles.get(0).getType());
        try {
            board.takeTiles(tiles);
            fail();
        } catch (NotInLineException | NoFreeEdgeException | OutOfBoundException e) {
            fail();
        } catch (InvalidTileException ignored) {
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(returnedBoard[4][4], 4,4));
        try {
            board.takeTiles(tiles);
            fail();
        } catch (NotInLineException | OutOfBoundException | InvalidTileException e) {
            fail();
        } catch (NoFreeEdgeException ignored){
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(returnedBoard[1][3], 3, 1));
        tiles.add(new Tile(returnedBoard[2][3], 3, 2));
        tiles.add(new Tile(returnedBoard[3][2], 2, 3));
        try {
            board.takeTiles(tiles);
            fail();
        } catch (NoFreeEdgeException | OutOfBoundException | InvalidTileException e) {
            fail();
        } catch (NotInLineException ignored){
        }


        //test for correct input
        tiles=new ArrayList<>();
        tiles.add(new Tile(returnedBoard[1][3], 3, 1));
        tiles.add(new Tile(returnedBoard[1][4], 4, 1));
        try {
            returnedTiles = board.takeTiles(tiles);
            assertEquals(tiles.get(0).getType(), returnedTiles.get(0));
            assertEquals(tiles.get(1).getType(), returnedTiles.get(1));
            assertEquals(2, returnedTiles.size());
            TilesType[][] returnedBoard2 = board.getEnumArray();
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if((i==1 && j==3)||(i==1 && j==4)) assertNull(returnedBoard2[i][j]);
                    else assertEquals(returnedBoard[i][j], returnedBoard2[i][j]);
                }
            }
        } catch (NotInLineException | NoFreeEdgeException | OutOfBoundException | InvalidTileException e) {
            fail();
        }
    }
    //checkAndRefill() is invoked by takeTiles
    @Test
    void checkAndRefill() {
        LivingRoom board;
        try {
            board = new LivingRoom(4);
            TilesType[][] boardCopy = board.getEnumArray();

            //first iteration I check the correct behaviour
            List<Tile> tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[0][3],3,0));
            tiles.add(new Tile(boardCopy[0][4],4,0));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[1][3],3,1));
            tiles.add(new Tile(boardCopy[1][4],4,1));
            tiles.add(new Tile(boardCopy[1][5],5,1));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][2],2,2));
            tiles.add(new Tile(boardCopy[2][3],3,2));
            tiles.add(new Tile(boardCopy[2][4],4,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][5],5,2));
            tiles.add(new Tile(boardCopy[2][6],6,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][1],1,3));
            tiles.add(new Tile(boardCopy[3][2],2,3));
            tiles.add(new Tile(boardCopy[3][3],3,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][4],4,3));
            tiles.add(new Tile(boardCopy[3][5],5,3));
            tiles.add(new Tile(boardCopy[3][6],6,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][7],7,3));
            tiles.add(new Tile(boardCopy[3][8],8,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][0],0,4));
            tiles.add(new Tile(boardCopy[4][1],1,4));
            tiles.add(new Tile(boardCopy[4][2],2,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][6],6,4));
            tiles.add(new Tile(boardCopy[4][7],7,4));
            tiles.add(new Tile(boardCopy[4][8],8,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][1],1,5));
            tiles.add(new Tile(boardCopy[5][2],2,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][3],3,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][6],6,5));
            tiles.add(new Tile(boardCopy[5][7],7,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][5],5,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][2],2,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][6],6,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[8][4],4,8));
            tiles.add(new Tile(boardCopy[8][5],5,8));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[7][3],3,7));
            tiles.add(new Tile(boardCopy[7][4],4,7));
            tiles.add(new Tile(boardCopy[7][5],5,7));
            board.takeTiles(tiles);

            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if((i==5 && j==0) || ((j==3 || j==5) && (i==4 || i==6)) || (j==4 && i>=4 && i<=6)){
                        assertEquals(boardCopy[i][j], board.getEnumArray()[i][j]);
                    }
                    else{
                        assertNull(board.getEnumArray()[i][j]);
                    }
                }
            }

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][4],4,4));
            tiles.add(new Tile(boardCopy[5][4],4,5));
            tiles.add(new Tile(boardCopy[6][4],4,6));
            board.takeTiles(tiles);

            //now the board is refilled, so I test that the only null tiles are the one that are null also at the beginning
            TilesType[][] refilledBoard = board.getEnumArray();
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if(refilledBoard[i][j]==null) assertNull(boardCopy[i][j]);
                    if(boardCopy[i][j]==null) assertNull(refilledBoard[i][j]);
                }
            }

            //second, third and forth iteration in order to get test if the check and refill does not refill when the filler is empty
            boardCopy=board.getEnumArray();
            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[0][3],3,0));
            tiles.add(new Tile(boardCopy[0][4],4,0));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[1][3],3,1));
            tiles.add(new Tile(boardCopy[1][4],4,1));
            tiles.add(new Tile(boardCopy[1][5],5,1));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][2],2,2));
            tiles.add(new Tile(boardCopy[2][3],3,2));
            tiles.add(new Tile(boardCopy[2][4],4,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][5],5,2));
            tiles.add(new Tile(boardCopy[2][6],6,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][1],1,3));
            tiles.add(new Tile(boardCopy[3][2],2,3));
            tiles.add(new Tile(boardCopy[3][3],3,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][4],4,3));
            tiles.add(new Tile(boardCopy[3][5],5,3));
            tiles.add(new Tile(boardCopy[3][6],6,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][7],7,3));
            tiles.add(new Tile(boardCopy[3][8],8,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][0],0,4));
            tiles.add(new Tile(boardCopy[4][1],1,4));
            tiles.add(new Tile(boardCopy[4][2],2,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][6],6,4));
            tiles.add(new Tile(boardCopy[4][7],7,4));
            tiles.add(new Tile(boardCopy[4][8],8,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][1],1,5));
            tiles.add(new Tile(boardCopy[5][2],2,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][3],3,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][6],6,5));
            tiles.add(new Tile(boardCopy[5][7],7,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][5],5,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][2],2,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][6],6,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[8][4],4,8));
            tiles.add(new Tile(boardCopy[8][5],5,8));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[7][3],3,7));
            tiles.add(new Tile(boardCopy[7][4],4,7));
            tiles.add(new Tile(boardCopy[7][5],5,7));
            board.takeTiles(tiles);


            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][4],4,4));
            tiles.add(new Tile(boardCopy[5][4],4,5));
            tiles.add(new Tile(boardCopy[6][4],4,6));
            board.takeTiles(tiles);

            //third iteration
            boardCopy=board.getEnumArray();
            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[0][3],3,0));
            tiles.add(new Tile(boardCopy[0][4],4,0));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[1][3],3,1));
            tiles.add(new Tile(boardCopy[1][4],4,1));
            tiles.add(new Tile(boardCopy[1][5],5,1));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][2],2,2));
            tiles.add(new Tile(boardCopy[2][3],3,2));
            tiles.add(new Tile(boardCopy[2][4],4,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[2][5],5,2));
            tiles.add(new Tile(boardCopy[2][6],6,2));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][1],1,3));
            tiles.add(new Tile(boardCopy[3][2],2,3));
            tiles.add(new Tile(boardCopy[3][3],3,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][4],4,3));
            tiles.add(new Tile(boardCopy[3][5],5,3));
            tiles.add(new Tile(boardCopy[3][6],6,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[3][7],7,3));
            tiles.add(new Tile(boardCopy[3][8],8,3));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][0],0,4));
            tiles.add(new Tile(boardCopy[4][1],1,4));
            tiles.add(new Tile(boardCopy[4][2],2,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][6],6,4));
            tiles.add(new Tile(boardCopy[4][7],7,4));
            tiles.add(new Tile(boardCopy[4][8],8,4));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][1],1,5));
            tiles.add(new Tile(boardCopy[5][2],2,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][3],3,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][6],6,5));
            tiles.add(new Tile(boardCopy[5][7],7,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[5][5],5,5));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][2],2,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[6][6],6,6));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[8][4],4,8));
            tiles.add(new Tile(boardCopy[8][5],5,8));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[7][3],3,7));
            tiles.add(new Tile(boardCopy[7][4],4,7));
            tiles.add(new Tile(boardCopy[7][5],5,7));
            board.takeTiles(tiles);

            tiles = new ArrayList<>();
            tiles.add(new Tile(boardCopy[4][4],4,4));
            tiles.add(new Tile(boardCopy[5][4],4,5));
            tiles.add(new Tile(boardCopy[6][4],4,6));
            board.takeTiles(tiles);

            //fourth iteration removes the tiles
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    tiles = new ArrayList<>();
                    tiles.add(new Tile(boardCopy[i][j],j,i));
                }
            }
            //now thank god I can test if the checkAndRefill does not modify the board when all the tiles where already played
            boardCopy=board.getEnumArray();
            board.checkAndRefill();
            refilledBoard=board.getEnumArray();

            for(int i=0; i<9; i++){
                System.arraycopy(refilledBoard[i], 0, boardCopy[i], 0, 9);
            }

        }catch (InvalidPlayerNumberException | NotInLineException | InvalidTileException | NoFreeEdgeException |
                OutOfBoundException e){
            fail();
        }
    }

    //test for all the random commonGoal initializations and for the IOException
    @Test
    void LivingRoom(){
        for(int i = 0; i<100; i++){
            try {
                new LivingRoom(2);
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InvalidPlayerNumberException | InterruptedException e) {
                fail();
            }
        }

        File file = new File("src/main/resources/json/board2.json");
        File file2 = new File("src/main/resources/json/board23.json");

        if (file2.exists()) fail();

        // Rename file (or directory)
        if(!file.renameTo(file2)) fail();

        try{
            new LivingRoom("2");
        } catch (InvalidPlayerNumberException e) {
            fail();
        }
        catch (RuntimeException ignore){
        }
        file = new File("src/main/resources/json/board23.json");
        file2 = new File("src/main/resources/json/board2.json");
        if (file2.exists()) fail();

        // Rename file (or directory)
        if(!file.renameTo(file2)) fail();

    }
    @Test
    void calculateCommonPoints() {
        try{
            LivingRoom board = new LivingRoom("2");
            Player  player = new Player("Pippo_x11!", board, PersonalGoal.PERSONALGOAL1 );

            ArrayList<Tile> tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.TROPHIES,3, 1));
            tiles.add(new Tile(TilesType.FRAMES,4, 1));
            player.moveTiles(tiles, 0);

            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.FRAMES,4, 2));
            tiles.add(new Tile(TilesType.CATS,3, 2));
            player.moveTiles(tiles, 1);

            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.GAMES,2, 3));
            tiles.add(new Tile(TilesType.TROPHIES,3, 3));
            player.moveTiles(tiles, 2);

            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.BOOKS,3, 6));
            player.moveTiles(tiles, 3);


            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.PLANTS,6, 5));
            player.moveTiles(tiles, 3);

            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.CATS, 6, 3));
            player.moveTiles(tiles, 4);

            tiles = new ArrayList<>();
            tiles.add(new Tile(TilesType.BOOKS, 3, 5));
            player.moveTiles(tiles, 4);

            assertEquals(8, board.calculateCommonPoints(player)[0]);
            assertEquals(0, board.calculateCommonPoints(player)[1]);

        }
        catch (NoFreeEdgeException | OutOfBoundException | FullColumnException | InvalidTileException |
               NotInLineException | InvalidPlayerNumberException e){
            fail();
        }
        try{
            new LivingRoom("ciao");
            fail();
        } catch (InvalidPlayerNumberException ignored) {
        }

    }
}