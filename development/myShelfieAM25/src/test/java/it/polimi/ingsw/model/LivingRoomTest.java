package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.commongoal.CommonGoal;
import it.polimi.ingsw.model.commongoal.TriangularMatrixGoal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            returnedTiles=board.takeTiles(tiles);
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
            returnedTiles=board.takeTiles(tiles);
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

    @Test
    void checkAndRefill() {
        LivingRoom board;
        try {
            board = new LivingRoom(4);
            TilesType[][] boardCopy = board.getEnumArray();

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

            //now the board is refilled so I test that the only null tiles are the one that are null also at the beginning
            TilesType[][] refilledBoard = board.getEnumArray();
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if(refilledBoard[i][j]==null) assertNull(boardCopy[i][j]);
                    if(boardCopy[i][j]==null) assertNull(refilledBoard[i][j]);
                }
            }

        }catch (InvalidPlayerNumberException | NotInLineException | InvalidTileException | NoFreeEdgeException |
                OutOfBoundException e){
            fail();
        }
    }
    /*
    TO DO, at the moment we have missing some way to identify the different common goals so it's difficult to calculate the respective points
    @Test
    void calculateCommonPoints() {
        try{
            LivingRoom board = new LivingRoom(2);
            Player  player = new Player("Pippox11!", board, PersonalGoal.PERSONALGOAL1 );
            try{
                CommonGoal goal = new TriangularMatrixGoal(2);
            }catch (InvalidPlayerNumberException e) {
                fail();
            }
        }
        catch (InvalidPlayerNumberException e){
            fail();
        }
    }
     */
}