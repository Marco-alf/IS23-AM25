package it.polimi.ingsw.model;

public class Shelf {

    public Shelf() {
        this.shelf = new TilesType[yBound][xBound];
    }

    /*
     * adds a list of Tiles to the shelf
     * @param    tiles   List of tiletype, ordered by order of insertion
     * @param    column  int, destination column
     * */
    public void add(List<TileType> tiles, int column);

    /*
    * returns calculated score of the shelf
    * */
    int calculatePoints();

    /*
     * Getter for TyleType of a single slot on the shelf, returns null in case it is blank
     * */
    TypeType getTile(int x, int y);

    /*
     * Getter for array representing current state of shelf
     * */
    public TilesType[][] getShelf();
}
