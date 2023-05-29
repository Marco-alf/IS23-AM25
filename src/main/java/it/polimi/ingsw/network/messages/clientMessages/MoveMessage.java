package it.polimi.ingsw.network.messages.clientMessages;

import it.polimi.ingsw.model.Tile;

import java.util.List;

public class MoveMessage extends ClientMessage{
    @Override
    public String getType() {
        return "MoveMessage";
    }

    private List<Tile> tiles;
    private int column;

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
