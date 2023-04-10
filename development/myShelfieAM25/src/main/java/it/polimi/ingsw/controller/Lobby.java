package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;


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
     * Reference to the game
     */
    private Game game;

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
     * @return the name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Adds a player to the lobby
     * @param name is the name of the player that want to join the lobby
     * @throws NameTakenException if there is another player in the lobby with the same name
     * @throws FullLobbyException if the lobby is full
     */
    public void addPlayer(String name) throws NameTakenException, FullLobbyException {
        if (onlinePlayers.stream().map(VirtualPlayer::getName).toList().contains(name)) {
            throw new NameTakenException();
        }
        if (onlinePlayers.size() == playerNumber) {
            throw new FullLobbyException();
        }
        onlinePlayers.add(new VirtualPlayer(name, this));
    }

    /**
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
     */
    public void moveTiles(List<Tile> tiles, int shelfColumn, String player) throws IllegalMoveException {
        try {
            game.moveTiles(tiles, shelfColumn, player);
        } catch (NotInLineException | OutOfBoundException | NoFreeEdgeException | InvalidPlayerNameException |
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
        currentPlayer = onlinePlayers.get(0);
    }

    /**
     * Writes a message on the chat
     * @param message is the content of the message (String)
     * @param player is the player that sent the message
     */
    public void writeMessage(String message, String player) {}

    /**
     * @return the next player in the game
     */
    public VirtualPlayer nextPlayer() {
        int index = (onlinePlayers.indexOf(currentPlayer) + 1) % onlinePlayers.size();
        return onlinePlayers.get(index);
    }

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
        onlinePlayers.remove(player);
    }

}
