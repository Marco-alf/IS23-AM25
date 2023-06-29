package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.data.FinalGameInfo;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.data.InitialGameInfo;

import java.util.ArrayList;
import java.util.IllegalFormatWidthException;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This class represents the lobby. It creates the game, manage the game turn and keeps the chat, it sends the messages
 * from the model the player to the game model.
 */
public class Lobby {
    /**
     * Name of the lobby
     */
    private final String lobbyName;
    /**
     * Nickname of the creator of the lobby (String)
     */
    private final String lobbyCreator;
    /**
     * Number of players in the lobby
     */
    private final int playerNumber;
    /**
     * Current player in the game
     */
    private VirtualPlayer currentPlayer;
    /**
     * List of online players
     */
    protected List<VirtualPlayer> onlinePlayers;
    /**
     * List of disconnected players
     */
    private final List<String> disconnectedPlayers = new ArrayList<>();
    /**
     * Reference to the game
     */
    private Game game;
    /**
     * boolean that is true if the game has been created, false otherwise
     */
    private boolean isGameCreated = false;
    /**
     * Reference to gameInfo
     */
    private GameInfo gameInfo;

    /**
     * Constructor for lobby
     * @param lobbyCreator name of the creator of the lobby
     * @param lobbyName name of the lobby
     * @param playerNumber number of players in the lobby
     */
    public Lobby(String lobbyCreator, String lobbyName, int playerNumber) {
        this.playerNumber = playerNumber;
        this.lobbyName = lobbyName;
        this.lobbyCreator = lobbyCreator;
        onlinePlayers = new ArrayList<>();

    }

    /**
     * getLobbyName is a getter for the name of the lobby
     * @return the name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getPlayerNumber is a getter for the number of the player of the lobby
     * @return the number of players in the lobby
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Adds a player to the lobby
     * @param name is the name of the player that want to join the lobby
     * @throws NameTakenException if there is another player in the lobby with the same name
     * @throws FullLobbyException if the lobby is full
     * @throws IllegalPlayerNameException if the name is not valid or null
     */
    public void addPlayer(String name) throws NameTakenException, FullLobbyException, IllegalPlayerNameException {
        if (name == null || name.isBlank()) {
            throw new IllegalPlayerNameException();
        }

        if (disconnectedPlayers.contains(name)) {
            disconnectedPlayers.remove(name);
            if(isGameCreated){
                try{
                    game.reconnect(name);
                }catch (InvalidPlayerNameException e){
                    throw new IllegalPlayerNameException();
                }
            }
            return;
        }
        if (onlinePlayers.stream().map(VirtualPlayer::getName).toList().contains(name)) {
            throw new NameTakenException();
        }
        if (onlinePlayers.size() == playerNumber) {
            throw new FullLobbyException();
        }

        onlinePlayers.add(new VirtualPlayer(name, this));
    }

    /**
     * getOnlinePlayers is a getter for the list of online players
     * @return a list of the names (String) of the online players
     */
    public List<String> getOnlinePlayers() {
        return onlinePlayers.stream().map(VirtualPlayer::getName).toList();
    }

    /**
     * This method is used to move tiles from the living room to a player's shelf
     * @param tiles are the tiles the player wants to take
     * @param shelfColumn is the column of the shelf the player wants to move their tiles into
     * @param player is the player that moves the tiles
     * @throws IllegalMoveException if the move is not valid
     * @throws GameEndedException if the game is already ended
     */
    public void moveTiles(List<Tile> tiles, int shelfColumn, String player) throws
            IllegalMoveException, GameEndedException {

        if (tiles.size() == 0) throw new IllegalMoveException();
        try {
            if (game.getEndGame()) throw new GameEndedException();
            game.moveTiles(tiles, shelfColumn, player);
            if (isLastTurn()) {
                game.setGameEnded();
                gameInfo = new FinalGameInfo(game);
            } else {
                gameInfo = new GameInfo(game);
            }
            currentPlayer = nextPlayer();
            game.updateCurrentPlayer(currentPlayer.getName());
        } catch (NotInLineException | OutOfBoundException | NoFreeEdgeException | InvalidPlayerNameException | NullPointerException |
                 PlayerNotCurrentException | FullColumnException |NullTilesException | PlayerNotOnlineException | InvalidTileException e) {
            throw new IllegalMoveException();
        }
    }

    /**
     * Method used to create a game
     * @throws GameCreationException if the number of players is not sufficient
     */
    public void createGame() throws GameCreationException {
        if (onlinePlayers.size() < playerNumber) {
            throw new GameCreationException();
        }
        List<String> players = new ArrayList<>();
        for (VirtualPlayer onlinePlayer : onlinePlayers) {
            players.add(onlinePlayer.getName());
        }
        game = new Game(players);
        try {
            currentPlayer = this.getPlayer(game.getCurrentPlayer());
        } catch (PlayerNotInLobbyException e) {
            throw new GameCreationException();
        }
        isGameCreated = true;
    }

    /**
     * Method used to create a game, used only for tests
     * @param isTest used to check if the method is call during a test
     * @throws GameCreationException if the number of players is not sufficient
     * @throws NotTestException if isTest is not true
     */
    public void createGame(boolean isTest) throws GameCreationException, NotTestException {
        if (isTest){
            if (onlinePlayers.size() < playerNumber) {
                throw new GameCreationException();
            }
            List<String> players = new ArrayList<>();
            for (VirtualPlayer onlinePlayer : onlinePlayers) {
                players.add(onlinePlayer.getName());
            }
            game = new Game(players, true);
            currentPlayer = onlinePlayers.get(0);
            isGameCreated = true;
        }
    }

    /**
     * Method used to check if the number of players is enough to continue playing
     * @return true if the number of players is more than 2, false otherwise
     */
    public boolean checkNumberOfPlayers () {
        return (onlinePlayers.size() - disconnectedPlayers.size()) < 2;
    }

    /**
     * is a method that waits for player to reconnect to the lobby. After a timeout if no player has reconnected is
     * returned false, otherwise is returned true
     * @return true if the number of player still in game is at least 2, false if there are no player in game
     */
    public boolean waitForPlayers () {
        for (int i = 0; i < 200; i++) {
            try  {
                TimeUnit.MILLISECONDS.sleep(100);
                if (onlinePlayers.size() - disconnectedPlayers.size() > 1) return true;
                if (onlinePlayers.size() - disconnectedPlayers.size() <= 0) return false;
            } catch (InterruptedException ignored) {

            }
        }
        return false;
    }

    /**
     * isLastTurn is a test to check if the game is going to end after the next move
     * @return true if is the last turn of the game, false otherwise
     */
    private boolean isLastTurn () {
        return game.isLastRound() && isLastPlayer();
    }

    /**
     * getGameInfo is a getter for the information about the game
     * @return the reference to game info
     */
    public GameInfo getGameInfo () {
        if(!isGameCreated) return null;
        return gameInfo;
    }

    /**
     * getInitialGameInfo is a getter for the complete information about the current game
     * @return the reference to the game info created at the beginning of the game
     */
    public InitialGameInfo getInitialGameInfo () {
        if(!isGameCreated) return null;
        return new InitialGameInfo(game);
    }

    /**
     * getCurrentPlayer is a getter for the name of the current player
     * @return the name (String) of the player that can play in this turn
     */
    public String getCurrentPlayer() {
        if(!isGameCreated) return null;
        if(currentPlayer == null) return null;
        return currentPlayer.getName();
    }

    /**
     * getDisconnectedPlayers is a getter for the list of the names of the disconnected players
     * @return the list of disconnected players
     */
    public List<String> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    /**
     * isGameCreated is a method used whether a game has been created
     * @return true if the game has been created, false otherwise
     */
    public boolean isGameCreated() {
        return isGameCreated;
    }

    /**
     * nextPlayer is the method used to calculate the next player in the game
     * @return the next player in the game, null if there is no player in the game
     */
    public VirtualPlayer nextPlayer() {
        int index = (onlinePlayers.indexOf(currentPlayer) + 1) % onlinePlayers.size();
        VirtualPlayer player = onlinePlayers.get(index);
        while (disconnectedPlayers.contains(player.getName()) && onlinePlayers.size()>disconnectedPlayers.size()) {
            index = (onlinePlayers.indexOf(player) + 1) % onlinePlayers.size();
            player = onlinePlayers.get(index);
            try {
                Thread.sleep(10);
            }catch (InterruptedException ignored){

            }
        }
        if(onlinePlayers.size()<=disconnectedPlayers.size()) return null;
        return onlinePlayers.get(index);
    }

    /**
     * isLastPlayer is a function used to test if the current player is the last one
     * @return true if the current player is the last player among the online players
     */
    public boolean isLastPlayer () {
        List<String> roundPlayers = new ArrayList<>();
        for (int i = 0; i < onlinePlayers.size(); i++) {
            if (!disconnectedPlayers.contains(onlinePlayers.get(i).getName())) roundPlayers.add(onlinePlayers.get(i).getName());
        }
        String firstPlayer = game.getFirstPlayer().getName();
        int firstIndex = roundPlayers.indexOf(firstPlayer);
        int index = (firstIndex + (playerNumber-1)) % playerNumber;
        String lastPlayer = roundPlayers.get(index);
        return currentPlayer.getName().equals(lastPlayer);
    }

    /**
     * getPlayer is a getter for class that representing a player given its name
     * @param name name of the virtual player
     * @return the reference to the virtual player corresponding to the parameter
     * @throws PlayerNotInLobbyException if the name asked is not an online player
     */
    public VirtualPlayer getPlayer(String name) throws PlayerNotInLobbyException {
        for (VirtualPlayer onlinePlayer : onlinePlayers) {
            if (onlinePlayer.getName().equals(name)) return onlinePlayer;
        }
        throw new PlayerNotInLobbyException();
    }

    /**
     * Disconnects a player from the lobby
     * @param player is the player that is being disconnected
     */
    public void disconnectPlayer(VirtualPlayer player) {
        disconnectedPlayers.add(player.getName());
        if (isGameCreated) {
            try{
                game.disconnectPlayer(player.getName());
            } catch (InvalidPlayerNameException e){
                throw new RuntimeException(e);
            }
            if (currentPlayer.getName().equals(player.getName())) {
                try {
                    currentPlayer = nextPlayer();
                    if(currentPlayer!=null) game.updateCurrentPlayer(currentPlayer.getName());
                } catch (InvalidPlayerNameException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
