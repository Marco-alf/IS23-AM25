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
    private Player firstPlayer;
    private boolean lastRound = false;
    private boolean endGame = false;

    /**
     * assignedFinalPoint saves if the final point is already been given to the first player who fills their bookshelf
     */
    private boolean assignedFinalPoint = false;

    /**
     * Constructor used for testing.
     * @param players is list containing every player name (takes as guaranteed they are unique)
     * @param isTest used to differentiate the constructor used only for tests, it has to be true to use the constructor
     * @throws ShelfCreationException if the number of players is not correct
     * @throws PlayersEmptyException if a player is null
     * @throws NotTestException if this constructor is used but isTest is false
     */
    public Game(List<String> players, boolean isTest) throws ShelfCreationException, PlayersEmptyException, NotTestException {
        if (isTest) {
            String size = String.valueOf(players.size());
            try {
                this.board = new LivingRoom(size);
            } catch (InvalidPlayerNumberException e) {
                throw new ShelfCreationException();
            }

            if (players.contains(null)) {
                throw new PlayersEmptyException();
            }

            PersonalGoal[] allgoals = PersonalGoal.values();
            List<Player> allPlayers = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                Player player = new Player(players.get(i), board, allgoals[i]);
                allPlayers.add(player);
            }
            this.players = allPlayers;

            onlinePlayers = new ArrayList<>(this.players);
            firstPlayer = onlinePlayers.get(0);
            currentPlayer = firstPlayer;
        }
        else throw new NotTestException();
    }

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
        if( players.contains(null)){
            throw new PlayersEmptyException();
        }

        /* generating random integer and extracting enumerations from an array containing all personalgoals.
        * generating new instances of players through functional programming map function */
        final Random gen = new Random(System.currentTimeMillis());
        PersonalGoal [] allgoals = PersonalGoal.values();

        List<Player> playersList = new ArrayList<>();
        List<Integer> used = new ArrayList<>();

        for (String name : players) {
            int randomIndex = gen.nextInt(allgoals.length);
            while(used.contains(randomIndex)){
                randomIndex = gen.nextInt(allgoals.length);
            }
            used.add(randomIndex);
            PersonalGoal randomGoal = allgoals[randomIndex];
            Player player = new Player(name, board, randomGoal);
            playersList.add(player);
        }
        this.players = playersList;

        /* assumption : game starts with every player online and not disconnected */
        onlinePlayers = new ArrayList<>(this.players);
        int randomIndex = gen.nextInt(players.size());
        firstPlayer = onlinePlayers.get(randomIndex);
        currentPlayer = firstPlayer;
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
        setLastRound();
        giveFullShelfPoint();
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

    public TilesType[][] getShelf () {
        return currentPlayer.getShelf();
    }

    public Map<String, TilesType[][]> getShelves () {
        Map<String, TilesType[][]> shelves = new HashMap<>();
        for (Player player : players) {
            shelves.put(player.getName(), player.getShelf());
        }
        return shelves;
    }

    public int getAdjPoints () {
        return currentPlayer.calculateAdjacencyPoints();
    }
    public int getPersonalPoints () {
        return currentPlayer.calculatePersonalPoints();
    }

    public List<String> getCommonGoals () {
        List<String> commonGoals = new ArrayList<>();
        for (int i = 0; i < board.getCommonGoals().length; i++) {
            commonGoals.add(board.getCommonGoals()[i].getType());
        }
        return commonGoals;
    }

    public Map<String, PersonalGoal> getPersonalGoals () {
        Map<String, PersonalGoal> personalGoals = new HashMap<>();
        for (Player player : players) {
            personalGoals.put(player.getName(), player.getPersonalGoal());
        }
        return personalGoals;
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
    public boolean getEndGame () {
        return endGame;
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
        List<Player> temp = new ArrayList<>();
        for(Player p : players){
            if(onlinePlayers.contains(p) || p.equals(searchPlayer(name))){
                temp.add(p);
            }
        }
        onlinePlayers.clear();
        onlinePlayers.addAll(temp);
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
    public Player searchPlayer(String name) throws InvalidPlayerNameException{
        for(Player p : players){
            if(p.getName().equals(name))
                return p;
        }
        throw new InvalidPlayerNameException();
    }

    /**
     * checks if currentPlayer is first player who fills all the spaces of their bookshelf and gives him a point
     */
    public void giveFullShelfPoint() {
        if (currentPlayer.isBookshelfFull() && !isAssignedFinalPoint()) {
            currentPlayer.setFinalPoint();
            setAssignedFinalPoint();
        }

    }

    public boolean isLastRound() {
        return lastRound;
    }

    private void setLastRound() {
        if (currentPlayer.isBookshelfFull()) lastRound = true;
    }

    public void setGameEnded () {
        endGame = true;
    }

    /**
     * getter for assignedFinalPoint
     * @return the value of assignedFinalPoint
     */
    public boolean isAssignedFinalPoint() {
        return assignedFinalPoint;
    }

    /**
     * setter for assignedFinalPoint
     */
    public void setAssignedFinalPoint() {
        assignedFinalPoint = true;
    }
}
