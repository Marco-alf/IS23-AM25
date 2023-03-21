package it.polimi.ingsw.model;

public class Shelf {

    private TilesType [][] shelf;
    private final int xBound = 5, yBound = 6;

    public Shelf() {
        this.shelf = new TilesType[yBound][xBound];
    }
    /**
    * adds a list of Tiles to the shelf
    * @param    tiles   List of tiletype, ordered by order of insertion
    * @param    column  int, destination column
    * */
    public void add(List<TileType> tiles, int column) throws
            TileBadFormattingEcxeption,
            ColumnOutOfBoundsException,
            PlacementException
    {
        int firstOpen = null;
        /* (not for javadoc) checking for exceptions*/
        if(column > xBound) throw ColumnOutOfBoundsException;
        for(int i=0; i < yBound ; i++){
            if(shelf[i][column] != null)
                if(i<tiles.size()) throw PlacementException;
                else break;
            else
                firstOpen = i;
        }
        for(int i; i<tiles.size() ;i++){
            shelf[firstOpen - i][column];
        }
    }


    int calculatePoints(){

        int concatCount = 0;
        Stack<Tile> tileStack = new Stack<>();

        for(int j=0; j<yBound; j++){
            for(int i=0; i<xBound; i++){

                if(shelf[i][j] != null){
                    TileType usedTypeshelf = shelf[i][j];
                    tileStack.push(new Tile(usedTypeshelf, i, j));
                    Do{
                        Tile explorer
                        try{
                            int corrX= 1, corrY = 0;
                            if(shelf[i+corrX][j+corrY] = concatCount)
                                tileStack.add(new Tile(usedTypeshelf ,i+corrX,j+corrY));
                        }
                        try{
                            int corrX= -1, corrY = 0;
                            if(shelf[i+corrX][j+corrY] = concatCount)
                                tileStack.add(new Tile(usedTypeshelf ,i+corrX,j+corrY));
                        }
                        try{
                            int corrX= 0, corrY = 1;
                            if(shelf[i+corrX][j+corrY] = concatCount)
                                tileStack.add(new Tile(usedTypeshelf ,i+corrX,j+corrY));
                        }
                        try{
                            int corrX= 0, corrY = -1;
                            if(shelf[i+corrX][j+corrY] = concatCount)
                                tileStack.add(new Tile(usedTypeshelf ,i+corrX,j+corrY));
                        }
                    } while( !tileStack.empty() );
                } else {
                    concatCount = 0;
                }

            }
        }
    }


    /*
    * Getter for TyleType of a single slot on the shelf, returns null in case it is blank
    * */
    TypeType getTile(int x, int y) throws IndexOutOfShelfBound{
        if(x>=xBound || y>=yBound || x<0 || y<0)
            throw IndexOutOfShelfBound;
        return shelf[y][x];
    }

    /*
    * Getter for array representing current state of shelf
    * */
    public TilesType[][] getShelf() {
        return shelf;
    }
}
