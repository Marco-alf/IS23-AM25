//TO DO: add javadoc (and modify) for the exception raised by Shelf
package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exception.*;

import java.util.ArrayList;

/**
 * Player is a class that models the player in a game. This class contains all the information regarding a player of the game.
 * Its active role in the mode is being an hub for the management of the operation regarding the player itself
 * @author andreac01
 */
public class Player {
    /**
     * name is the nickname chosen by the player. Inside a lobby it works as an unique identifier.
     * This role as an unique identifier make necessary the definition of this attribute as final.
     */
    private final String name;
    /**
     * shelf is a matrix representing the shelf used by the player. The link between a player and its shelf is created here
     * and, because the attribute is final, it is never modified
     */
    private final Shelf shelf = new Shelf();
    /**
     * board represents the link between player and the living room. Because both the existences of a Player and a LivingRoom
     * are strictly dependent on a single game the relationship between these two component is immutable hence the attribute
     * is final
     */
    private final LivingRoom board;

    /**
     * personalGoal is the personal goal that the game assign to each. The same motivation that lead to board being final is also
     * applicable for personalGoal hence it is final.
     */
    private final PersonalGoal personalGoal;

    /**
     * The constructor of Player requires all his attributes to be passed as parameters except for the shelf that is initialized
     * outside the contructor
     * @param name is the unique nickname of the player in the game
     * @param board is the reference to the living room used in the game
     * @param personalGoal is the personal goal that the player have to accomplish. Note that personalGoal being a parameter of the constructor
     *                     is important because initializing it inside player would have made difficult making sure that players in the
     *                     same game does not share the same personalGoal
     */
    public Player(String name, LivingRoom board, PersonalGoal personalGoal){
        this.name = name;
        this.board = board;
        this.personalGoal = personalGoal;
    }

    /**
     * getName is a simple getter for the name attribute
     * @return name which is the unique nickname of the player
     */
    public String getName() {
        return name;
    }

    /**
     * calculatePersonalPoints is the method used by game to retrive the points that a player has done by completing his personal goal
     * @return the points calculated by the personal goal based on the shelf of the player
     */
    public int calculatePersonalPoints() throws OutOfBoundException{
        return personalGoal.calculatePoints(shelf);
    }

    /**
     * calculateCommonPoints is the method used by game to retrive the points that a player has done by completing common goals
     * @return the points calculated by the living room for this player
     */
    public int calculateCommonPoints(){
        return board.calculateCommonPoints(this);
    }

    /**
     * getShelf is a simple getter method for the private attribute shelf
     * @return a copy of the matrix representing this player's shelf
     */
    public TilesType[][] getShelf(){
        return shelf.getShelf();
    }

    /**
     * moveTiles is the core method of player used to take a list of Tile from the living room and placing them into the shelf
     * Player have to check that adding tiles to the shelf will be legal in order to avoid taking the tiles if the move throws
     * an exception
     * @param tiles is the list of tiles that the player want to take from the board. The order of the list mirrors the order
     *              in which the player wants to place them into the shelf
     * @param shelfColumn is the index of the column of the shelf in which the player wants to place the tiles
     * @throws NotInLineException if the tiles are not in a straight line
     * @throws NoFreeEdgeException if one tile has no free adjacent cells
     * @throws OutOfBoundException if one coordinate is out of the board
     * @throws NullPointerException if one cell is empty
     * @throws FullColumnException if the tiles couldn't fit in the selected column
     */
    public void moveTiles(ArrayList<Tile> tiles, int shelfColumn) throws NotInLineException, NoFreeEdgeException, OutOfBoundException, NullPointerException, FullColumnException{
        if(shelfColumn >= shelf.getxBound() || shelfColumn < 0) throw new OutOfBoundException();
        int curTop=0;
        for (int i=0; i < shelf.getyBound(); i++){
            if(shelf.getShelf()[i][shelfColumn]!=null){
                curTop++;
            }
        }
        if(curTop >= shelf.getyBound()) throw new FullColumnException();

        ArrayList<TilesType> tileList = board.takeTiles(tiles);
        shelf.add(tileList, shelfColumn);
    }

    /**
     * calculateAdjacentPoints is used for calculating the points that a player have done by placing the tiles neatly in the shelf
     * @return the points that a player have done by placing the tiles neatly in the shelf
     */
    public int calculateAdjacentPoints(){
        return shelf.calculatePoints();
    }
}
