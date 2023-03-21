package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * TO DO: model of the class Player, ALL methods have to be implemented
 */
public class Player {
    private String name;
    private Shelf shelf;
    private LivingRoom board;

    public int calculatePersonalPoints(){
        return 0;
    }

    public int calculateCommonPoints(){
        return 0;
    }

    public TilesType[][] getShelf(){
        TilesType[][] tilesTypes = new TilesType[][]{};
        return tilesTypes;
    }

    public void moveTiles(ArrayList<Tile> tiles, int shelfColumn){

    }

    public int calculateAdjacentPoints(){
        return 0;
    }
}
