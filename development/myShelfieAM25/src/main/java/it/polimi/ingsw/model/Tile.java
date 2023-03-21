package it.polimi.ingsw.model;

public class Tile {
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
}
