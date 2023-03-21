package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * TO DO: model of the class Player, ALL methods have to be implemented
 */
public class Player {
    private String name;
    private Shelf shelf = new Shelf();
    private LivingRoom board;
    private PersonalGoal personalGoal;

    public Player(String name, LivingRoom board, PersonalGoal personalGoal){
        this.name = name;
        this.board = board;
        this.personalGoal = personalGoal;
    }

    public int calculatePersonalPoints(){

        return personalGoal.calculatePoints(shelf);
    }

    public int calculateCommonPoints(){

        return LivingRoom.calculateCommonPoints(this);
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
