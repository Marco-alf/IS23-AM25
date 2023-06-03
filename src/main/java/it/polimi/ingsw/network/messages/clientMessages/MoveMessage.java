package it.polimi.ingsw.network.messages.clientMessages;

import it.polimi.ingsw.model.Tile;

import java.util.List;

/**
 * MoveMessage is the class that represents the message that a client sends to the server in order to try to make a move
 */
public class MoveMessage extends ClientMessage{
    /**
     * list of the tiles to move
     */
    private List<Tile> tiles;
    /**
     * column where to place the tiles
     */
    private int column;

    /**
     * constructor for the MoveMessage class
     */
    public MoveMessage(){
        this.type = "MoveMessage";
    }

    /**
     * setter for tiles
     * @param tiles is the List<Tile> to move
     */
    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * setter fot column
     * @param column is the number of column to select
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * getter for column
     * @return the column selected for placing the tiles
     */
    public int getColumn() {
        return column;
    }

    /**
     * getter for tiles
     * @return the list of tiles to move
     */
    public List<Tile> getTiles() {
        return tiles;
    }
}
