package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.OutOfBoundException;

/**
 * PersonalGoal is an enumeration whose instances represents the 12 different type of personal goal present in the game
 * The class has two private attributes: an array of int representing the points given by the card and an array of Tile representing the constraints that the player have to fulfill
 */
public enum PersonalGoal {
    PERSONALGOAL1(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 3, 2),
                    new Tile(TilesType.CATS, 4, 1),
                    new Tile(TilesType.FRAMES, 2, 0),
                    new Tile(TilesType.GAMES, 1, 3),
                    new Tile(TilesType.PLANTS, 0, 0),
                    new Tile(TilesType.TROPHIES, 2, 5)}
    ),
    PERSONALGOAL2(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 4, 3),
                    new Tile(TilesType.CATS, 0, 2),
                    new Tile(TilesType.FRAMES, 4, 5),
                    new Tile(TilesType.GAMES, 2, 2),
                    new Tile(TilesType.PLANTS, 1, 1),
                    new Tile(TilesType.TROPHIES, 3, 4)}
    ),
    PERSONALGOAL3(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 0, 5),
                    new Tile(TilesType.CATS, 1, 3),
                    new Tile(TilesType.FRAMES, 0, 1),
                    new Tile(TilesType.GAMES, 3, 1),
                    new Tile(TilesType.PLANTS, 2, 2),
                    new Tile(TilesType.TROPHIES, 4, 3)}
    ),
    PERSONALGOAL4(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 1, 4),
                    new Tile(TilesType.CATS, 2, 4),
                    new Tile(TilesType.FRAMES, 2, 2),
                    new Tile(TilesType.GAMES, 4, 0),
                    new Tile(TilesType.PLANTS, 3, 3),
                    new Tile(TilesType.TROPHIES, 0, 2)}
    ),
    PERSONALGOAL5(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 2, 3),
                    new Tile(TilesType.CATS, 3, 5),
                    new Tile(TilesType.FRAMES, 1, 3),
                    new Tile(TilesType.GAMES, 0, 5),
                    new Tile(TilesType.PLANTS, 4, 4),
                    new Tile(TilesType.TROPHIES, 1, 1)}
    ),
    PERSONALGOAL6(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 3, 2),
                    new Tile(TilesType.CATS, 4, 0),
                    new Tile(TilesType.FRAMES, 3, 4),
                    new Tile(TilesType.GAMES, 1, 4),
                    new Tile(TilesType.PLANTS, 0, 5),
                    new Tile(TilesType.TROPHIES, 2, 0)}
    ),
    PERSONALGOAL7(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 2, 5),
                    new Tile(TilesType.CATS, 0, 0),
                    new Tile(TilesType.FRAMES, 3, 1),
                    new Tile(TilesType.GAMES, 4, 4),
                    new Tile(TilesType.PLANTS, 1, 2),
                    new Tile(TilesType.TROPHIES, 0, 3)}
    ),
    PERSONALGOAL8(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 3, 4),
                    new Tile(TilesType.CATS, 1, 1),
                    new Tile(TilesType.FRAMES, 4, 0),
                    new Tile(TilesType.GAMES, 3, 5),
                    new Tile(TilesType.PLANTS, 0, 3),
                    new Tile(TilesType.TROPHIES, 2, 2)}
    ),
    PERSONALGOAL9(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 4, 3),
                    new Tile(TilesType.CATS, 2, 2),
                    new Tile(TilesType.FRAMES, 0, 5),
                    new Tile(TilesType.GAMES, 2, 0),
                    new Tile(TilesType.PLANTS, 4, 4),
                    new Tile(TilesType.TROPHIES, 1, 4)}
    ),
    PERSONALGOAL10(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 0, 2),
                    new Tile(TilesType.CATS, 3, 3),
                    new Tile(TilesType.FRAMES, 1, 4),
                    new Tile(TilesType.GAMES, 1, 1),
                    new Tile(TilesType.PLANTS, 3, 5),
                    new Tile(TilesType.TROPHIES, 4, 0)}
    ),
    PERSONALGOAL11(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 1, 1),
                    new Tile(TilesType.CATS, 4, 4),
                    new Tile(TilesType.FRAMES, 2, 3),
                    new Tile(TilesType.GAMES, 0, 2),
                    new Tile(TilesType.PLANTS, 2, 0),
                    new Tile(TilesType.TROPHIES, 3, 5)}
    ),
    PERSONALGOAL12(
            new int[]{0, 1, 2, 4, 6, 9, 12},
            new Tile[]{
                    new Tile(TilesType.BOOKS, 2, 0),
                    new Tile(TilesType.CATS, 0, 5),
                    new Tile(TilesType.FRAMES, 2, 2),
                    new Tile(TilesType.GAMES, 4, 4),
                    new Tile(TilesType.PLANTS, 1, 1),
                    new Tile(TilesType.TROPHIES, 3, 3)}
    );
    /**
     * points is an array of int that represents the points to give for each level of completion of the goal.
     * The i-th element in the array represents the points to give to a player with a board that complete i constraints
     */
    private final int[] points = new int[7];
    /**
     * tiles is an array of Tile that represents the constraints of the goal.
     * The constraints is represented by the requested TileType associated with its position (as in the Tile class)
     */
    private final Tile[] tiles = new Tile[6];

    /**
     * The constructor of the class (which is private because PersonalGoal is a defined enumeration) takes as parameters two array that are representing its private attribute
     * @param points is an array of int that correspond to the private attribute points
     * @param tiles is an array of Tile that correspond to the private attribute tiles
     */
    private PersonalGoal(int[] points, Tile[] tiles) {
        System.arraycopy(points, 0, this.points, 0, 7);
        System.arraycopy(tiles, 0, this.tiles, 0, 6);
    }

    /**
     * calculatePoints is a method shared by all instances of the enumeration.
     * It is used for calculating the points assigned to a personal goal to a particular shelf configuration.
     * If getTile return an exception we simply consider that
     * @param shelf the method requires a shelf configuration as input
     * @return points[i] the method returns the points achieved by the shelf in exam
     */
    public int calculatePoints(TilesType[][] shelf){
        int i;
        int count = 0;
        for (i = 0; i < 6; i++) {
            if (tiles[i].getType() == shelf[tiles[i].getPosY()][tiles[i].getPosX()]) {
                count++;
            }
        }
        return points[count];
    }

    /**
     * getMatrix is a getter for a representation of a personal goal as a shelf
     * @return a TilesTipe[][] that represents the personal goal as a shelf
     */
    public TilesType[][] getMatrix(){
        TilesType[][] matrix = new TilesType[6][5];
        for(int y = 0; y < 6; y ++){
            for(int x = 0; x < 5; x ++){
                matrix[y][x] = null;
                for(int i = 0; i < 6; i++){
                    if(tiles[i].getPosY() == y && tiles[i].getPosX() == x){
                        matrix[y][x] = tiles[i].getType();
                    }
                }
            }
        }
        return matrix;
    }
}
