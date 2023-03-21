package it.polimi.ingsw.model;

public enum PersonalGoal {
    PERSONALGOAL1(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,3,2),
                    new Tile(TilesType.CATS, 4,1),
                    new Tile(TilesType.FRAMES,2,0),
                    new Tile(TilesType.GAMES, 1, 3),
                    new Tile(TilesType.PLANTS, 0, 2),
                    new Tile(TilesType.TROPHIES, 2, 5)}
    ),
    PERSONALGOAL2(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,4,3),
                    new Tile(TilesType.CATS, 0,2),
                    new Tile(TilesType.FRAMES,4,5),
                    new Tile(TilesType.GAMES, 2, 2),
                    new Tile(TilesType.PLANTS, 1, 1),
                    new Tile(TilesType.TROPHIES, 3, 4)}
    ),
    PERSONALGOAL3(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   0, 5),
                    new Tile(TilesType.CATS,    1, 3),
                    new Tile(TilesType.FRAMES,  0, 1),
                    new Tile(TilesType.GAMES,   3, 1),
                    new Tile(TilesType.PLANTS,  2, 2),
                    new Tile(TilesType.TROPHIES,4, 3)}
    ),
    PERSONALGOAL4(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   1, 4),
                    new Tile(TilesType.CATS,    2, 4),
                    new Tile(TilesType.FRAMES,  2, 2),
                    new Tile(TilesType.GAMES,   4, 0),
                    new Tile(TilesType.PLANTS,  3, 3),
                    new Tile(TilesType.TROPHIES,0, 2)}
    ),
    PERSONALGOAL5(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   2, 3),
                    new Tile(TilesType.CATS,    3, 5),
                    new Tile(TilesType.FRAMES,  1, 3),
                    new Tile(TilesType.GAMES,   0, 5),
                    new Tile(TilesType.PLANTS,  4, 4),
                    new Tile(TilesType.TROPHIES,1, 1)}
    ),
    PERSONALGOAL6(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   3, 2),
                    new Tile(TilesType.CATS,    4, 0),
                    new Tile(TilesType.FRAMES,  3, 4),
                    new Tile(TilesType.GAMES,   1, 4),
                    new Tile(TilesType.PLANTS,  0, 5),
                    new Tile(TilesType.TROPHIES,2, 0)}
    ),
    PERSONALGOAL7(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   2, 5),
                    new Tile(TilesType.CATS,    0, 0),
                    new Tile(TilesType.FRAMES,  3, 1),
                    new Tile(TilesType.GAMES,   4, 4),
                    new Tile(TilesType.PLANTS,  1, 2),
                    new Tile(TilesType.TROPHIES,0, 3)}
    ),
    PERSONALGOAL8(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   3, 4),
                    new Tile(TilesType.CATS,    1, 1),
                    new Tile(TilesType.FRAMES,  4, 0),
                    new Tile(TilesType.GAMES,   3, 5),
                    new Tile(TilesType.PLANTS,  0, 3),
                    new Tile(TilesType.TROPHIES,2, 2)}
    ),
    PERSONALGOAL9(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   4, 3),
                    new Tile(TilesType.CATS,    2, 2),
                    new Tile(TilesType.FRAMES,  0, 5),
                    new Tile(TilesType.GAMES,   2, 0),
                    new Tile(TilesType.PLANTS,  4, 4),
                    new Tile(TilesType.TROPHIES,1, 4)}
    ),
    PERSONALGOAL10(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   0, 2),
                    new Tile(TilesType.CATS,    3, 3),
                    new Tile(TilesType.FRAMES,  1, 4),
                    new Tile(TilesType.GAMES,   1, 1),
                    new Tile(TilesType.PLANTS,  3, 5),
                    new Tile(TilesType.TROPHIES,4, 0)}
    ),
    PERSONALGOAL11(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   1, 1),
                    new Tile(TilesType.CATS,    4, 4),
                    new Tile(TilesType.FRAMES,  2, 3),
                    new Tile(TilesType.GAMES,   0, 2),
                    new Tile(TilesType.PLANTS,  2, 0),
                    new Tile(TilesType.TROPHIES,3, 5)}
    ),
    PERSONALGOAL12(
            new int[]{0,1,2,4,6,9,12},
            new Tile[]{
                    new Tile(TilesType.BOOKS,   2, 0),
                    new Tile(TilesType.CATS,    0, 5),
                    new Tile(TilesType.FRAMES,  2, 2),
                    new Tile(TilesType.GAMES,   4, 4),
                    new Tile(TilesType.PLANTS,  1, 1),
                    new Tile(TilesType.TROPHIES,3, 3)}
    );

    private final int[] points= new int[7];
    private final Tile[] tiles = new Tile[6];


    private PersonalGoal(int[] points, Tile[] tiles){
        System.arraycopy(points, 0, this.points, 0, 7);
        System.arraycopy(tiles, 0, this.tiles, 0, 6);
    }

    public int calculatePoints(Shelf shelf){
        int i;
        int count=0;
        for(i=0; i<6; i++){
            if(tiles[i].getType().equals(shelf.getTile(tiles[i].getPosX(), tiles[i].getPosY()))){
                count++;
            }
        }
        return points[count];
    
}
