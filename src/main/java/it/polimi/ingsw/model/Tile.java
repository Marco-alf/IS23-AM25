package it.polimi.ingsw.model;

import java.io.Serializable;

public class Tile implements Serializable {
    private final int posX;
    private final int posY;
    private final TilesType type;

    public Tile(TilesType type, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public TilesType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (posX != tile.posX) return false;
        if (posY != tile.posY) return false;
        return type == tile.type;
    }

    @Override
    public int hashCode() {
        int result = posX;
        result = 31 * result + posY;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
