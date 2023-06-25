package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Tile is the class that represents the tile object inside the program
 */
public class Tile implements Serializable {
    /**
     * posX is the position of the tile on the X axes
     */
    private final int posX;
    /**
     * posY is the position of the tile on the y axes
     */
    private final int posY;
    /**
     * type is the type of tile
     */
    private final TilesType type;

    /**
     * The constructor of the tile class need to initialize all its final attributes.
     * @param type is the type of the tile that the constructor is initializing
     * @param posX is the position of the tile on the x axes
     * @param posY is the position of the tile on the y axes
     */
    public Tile(TilesType type, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
    }

    /**
     * getPosX is a getter for the position of the tile on the x axes
     * @return the position of the tile on the x axes
     */
    public int getPosX() {
        return posX;
    }

    /**
     * getPosY is a getter for the position of the tile on the y axes
     * @return the position of the tile on the y axes
     */
    public int getPosY() {
        return posY;
    }

    /**
     * getType is a getter for the type of the tile
     * @return the type of the tile
     */
    public TilesType getType() {
        return type;
    }

    /**
     * equals is an override of the equals method.
     * @param o is the object to compare
     * @return true if o is a Tile and all o's final attributes are equal to the one of the current object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (posX != tile.posX) return false;
        if (posY != tile.posY) return false;
        return type == tile.type;
    }

    /**
     * ovverride of the hashCode method
     * @return a valid hashcode for the tile
     */
    @Override
    public int hashCode() {
        int result = posX;
        result = 31 * result + posY;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
