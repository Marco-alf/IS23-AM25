package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.*;

import javax.imageio.ImageTranscoder;
import java.util.List;

/**
 * class shelf represent a player's shelf
 */
public class Shelf {
    /**
     * attribute shelf represents the real player shelf
     */
    private final TilesType [][] shelf;
    /**
     * attributes xBound and yBound represents dimension of the shelf
     */
    private static final int xBound = 5, yBound = 6;

    /**
     * The constructor of the class initialize an empty TilesTye matrix of the right dimension
     */
    public Shelf() {
        this.shelf = new TilesType[yBound][xBound];
    }

    /**
     * getter for xBound
     * @return xBound, the dimension of shelf regarding the x axes
     */
    public int getxBound() {
        return xBound;
    }

    /**
     * getter for yBound
     * @return yBound, the dimension of shelf regarding the y axes
     */
    public int getyBound() {
        return yBound;
    }

    /**
     * add a list of tiles in the shelf
     * @param tiles is the List of the TilesType to insert into the shelf, the order of the tiles is mantained
     * @param column is the index of the column in which to insert the tiles
     * @throws OutOfBoundException is thrown if the column index is invalid
     * @throws FullColumnException is thrown if there is no space for the tiles in the selected column
     */
    public void add(List<TilesType> tiles, int column) throws OutOfBoundException, FullColumnException {
        if(column >= xBound || column < 0) throw new OutOfBoundException();
        int curTop=0;
        for (int i=0; i < yBound; i++){
            if(shelf[i][column]!=null){
                curTop++;
            }
        }
        if(curTop > yBound - tiles.size()) throw new FullColumnException();

        for (TilesType el: tiles) {
            shelf[curTop][column] = el;
            curTop++;
        }
    }

    /**
     * support private method used by calculatePoints to get the points of each cluster of similar tiles
     * @param i is the row of the element whose cluster dimension will be calculated
     * @param j is the column of the element whose cluster dimension will be calculated
     * @param checked is used to have a memory of already checked clusters
     * @return the dimension of the cluster that contains element in position i,j
     */
    private int numAdj(int i, int j, boolean[][] checked) {
        int adj = 0;
        if(checked[i][j] || shelf[i][j] == null) return 0;
        adj++;
        TilesType type = shelf[i][j];
        checked[i][j] = true;
        if((j + 1) < xBound && shelf[i][j + 1] == type) {
            adj = adj + numAdj(i, j + 1, checked);
        }
        if((i + 1) < yBound && shelf[i + 1][j] == type) {
            adj = adj + numAdj(i + 1, j, checked);
        }
        if((j - 1) >= 0 && shelf[i][j - 1] == type) {
            adj = adj + numAdj(i, j - 1, checked);
        }
        if((i - 1) >= 0 && shelf[i - 1][j] == type) {
            adj = adj + numAdj(i - 1, j, checked);
        }
        return adj;
    }

    private int mapPoints(int dim){
        if(dim<3) return 0;
        if(dim==3) return 2;
        if(dim==4) return 3;
        if(dim==5) return 5;
        return 8;
    }
    /**
     * calculatePoints is used to get the points related to the shelf that are not related to any goal (common or personal)
     * @return points that are the points achieved by the clustering in the shelf
     */
    public int calculatePoints(){
        int points = 0;
        boolean[][] checked = new boolean[6][5];

        for (int i = 0; i < yBound; i++) {
            for (int j = 0; j < xBound; j++) {
                int dim = numAdj(i, j, checked);
                points+=mapPoints(dim);
            }
        }
        return points;
    }


    /**
     * getter for a tile in position y, x
     * @param x is the position of the tile on the x axes, so it represents the column
     * @param y is the position of the tile on the y axes, so it represents the row
     * @return the TilesType of the tile in position y, x
     * @throws OutOfBoundException when the requested position is not valid int the shelf array
     */
    public TilesType getTile(int x, int y) throws OutOfBoundException {
        if(x>=xBound || y>=yBound || x<0 || y<0) throw new OutOfBoundException();
        return shelf[y][x];
    }

    /**
     * Getter for the current shelf state
     * @return shelfCopy that is a copy of the current shelf configuration
     */

    public TilesType[][] getShelf() {
        TilesType[][] shelfCopy = new TilesType[shelf.length][];
        for(int i = 0; i < shelf.length; i++){
            shelfCopy[i] = shelf[i].clone();
        }
        return shelfCopy;
    }
}
