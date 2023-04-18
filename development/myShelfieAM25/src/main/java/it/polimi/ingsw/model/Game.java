package it.polimi.ingsw.model;


import it.polimi.ingsw.exception.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * class Game represents an instance of the game MyShelfie, provides as public the methods to update the whole model.
 */
public class Game
{
    /**
     * list of all players of the game instance
     */
    private final List<Player> players;
    /**
     * list contains every player online, connected to the server, that can send instructions to the controller
     */
    private final List<Player> onlinePlayers;
    /**
     * board of the game instance, multiplicity is 1 */
    private final LivingRoom board;
    /**
     * currentPlayer is the reference to the player that is supposed to move next, the player of the current turn*/
    private Player currentPlayer;
    

    /**
     * constructor of game
     * @param players is list containing every player name (takes as guaranteed they are unique)
     */
    public Game(List<String> players) throws ShelfCreationException, PlayersEmptyException {
        /* initiating board, catching exceptions as one, stacktrace will do the job in debugging*/
        try{
            this.board = new LivingRoom(players.size());
        } catch (InvalidPlayerNumberException e) {
            throw new ShelfCreationException();
        }

        /* checking consistency of passed strings, every string contained in list must be non-null and non-empty */
        if( players.contains(null) || players.contains("")){
            throw new PlayersEmptyException();
        }

        /* generating random integer and extracting enumerations from an array containing all personalgoals.
        * generating new instances of players through functional programming map function */
        final Random gen = new Random(System.currentTimeMillis());
        PersonalGoal [] allgoals = PersonalGoal.values();
        this.players = players.stream()
                .map( name -> new Player(name, board, allgoals[gen.nextInt(allgoals.length)]) )
                .collect(Collectors.toList());

        /* assumption : game starts with every player online and not disconnected */
        onlinePlayers = new ArrayList<>(this.players);
        currentPlayer = onlinePlayers.get(0);
    }

    /**
     * updates the player which has the right to move to the player with the specified name
     * @param name name of the player which has now the right to move
     */
    public void updateCurrentPlayer(String name) throws InvalidPlayerNameException {
        /* method is not named set because it does not set the variable through the parameter, and instead replaces
        * currentplayer with one that has the same attribute NAME as "name" */
        currentPlayer = searchPlayer(name);
    }

    /**
     * updates the internal state of model, method models the only move a player can make
     * @param tiles tiles to be moved, in the order the player decides to insert them into his own shelf
     * @param shelfColumn number corresponding to the selected column of the player's shelf
     * @param name name of the player that will make its move
     * @throws InvalidPlayerNameException is thrown if the name specified is not registered in this game instance
     * @throws PlayerNotCurrentException is thrown if it is NOT the turn of the player specified
     * @throws NullTilesException is thrown if the list of tiles passed contains a null element
     */
    public void moveTiles(final List<Tile> tiles, int shelfColumn, String name) throws InvalidPlayerNameException,
            PlayerNotCurrentException,
            NotInLineException,
            NoFreeEdgeException,
            OutOfBoundException,
            NullPointerException,
            FullColumnException,
            NullTilesException,
            PlayerNotOnlineException,
            InvalidTileException
    {
        /* checking if it IS the turn of the specified player */
        if(!currentPlayer.getName().equals(name)){
            throw new PlayerNotCurrentException();
        }
        /* checking if tiles list has no null elements*/
        if(tiles.contains(null)){
            throw new NullTilesException();
        }
        /* checking if the player is in game, and if it is online*/
        Player p = searchPlayer(name);
        if(!onlinePlayers.contains(p)) {
            throw new PlayerNotOnlineException();
        }
        p.moveTiles(tiles, shelfColumn);
    }

    /**
     * getter method of attribute: OnlinePlayer
     * @return List of all the names of "online" player (players who have NOT disconnected)
     */
    public List<String> getOnlinePlayers(){
        return onlinePlayers.stream().map(Player::getName).collect(Collectors.toList());
    }

    public String getCurrentPlayer() {
        return currentPlayer.getName();
    }

    public Map<String, TilesType[][]> getShelves () {
        Map<String, TilesType[][]> shelves = new HashMap<>();
        for (Player player : players) {
            shelves.put(player.getName(), player.getShelf());
        }
        return shelves;
    }

    public Map<String, Integer> getAdjPoints () {
        Map<String, Integer> adjPoints = new HashMap<>();
        for (Player player : players) {
            adjPoints.put(player.getName(), player.calculateAdjPoints());
        }
        return adjPoints;
    }

    public Map<String, Integer> getComm1Points () {
        Map<String, Integer> comm1Points = new HashMap<>();
        for (Player player : players) {
            comm1Points.put(player.getName(), player.calculateCommonPoints()[0]);
        }
        return comm1Points;
    }

    public Map<String, Integer> getComm2Points () {
        Map<String, Integer> comm2Points = new HashMap<>();
        for (Player player : players) {
            comm2Points.put(player.getName(), player.calculateCommonPoints()[1]);
        }
        return comm2Points;
    }



    public TilesType[][] getNewBoard () {
        return board.getEnumArray();
    }

    public int getCommonGoal1Points () {
        return currentPlayer.calculateCommonPoints()[0];
    }

    public int getCommonGoal2Points () {
        return currentPlayer.calculateCommonPoints()[1];
    }

    public String getFirstPlayer () {
        return players.get(0).getName();
    }

    /**
     * removes the player with the specified name from the OnlinePlayers list
     * @throws InvalidPlayerNameException thrown if the name specified is not associated with any player of the game
     */
    public void disconnectPlayer(String name) throws InvalidPlayerNameException {
        onlinePlayers.remove(searchPlayer(name));
    }

    /**
     * adds back a previously disconnected player with the specified name to the OnlinePlayers list
     * @throws InvalidPlayerNameException thrown if the name specified is not associated with any player of the game
     */
    public void reconnect(String name) throws InvalidPlayerNameException {
        onlinePlayers.add(searchPlayer(name));
    }

    /**
     * method to get the total points that every player has gained over the course of the current game
     * @return the list of all accumulated points of each player in a list ordered by the same index used for players */
    public List<Integer> getPlayersPoints() throws OutOfBoundException {
        List<Integer> results = new ArrayList<>();
        int[] commonPoints;
        for (Player p : players) {
            commonPoints = p.calculateCommonPoints();
            results.add(commonPoints[0] + commonPoints[1] + p.calculatePersonalPoints());
        }
        return results;
    }

    /**
     * private method used to retrieve the reference of a player from its name
     * @param name name of the player to return
     * @return Player object instance whose attribute Player.name is the same as parameter name*/
    private Player searchPlayer(String name) throws InvalidPlayerNameException{
        for(Player p : players){
            if(p.getName().equals(name))
                return p;
        }
        throw new InvalidPlayerNameException();
    }
}
