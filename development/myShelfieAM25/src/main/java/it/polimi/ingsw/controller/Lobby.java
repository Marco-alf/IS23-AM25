package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.data.GameInfo;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.data.InitialGameInfo;

import java.util.ArrayList;
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
    private final List<String> disconnectedPlayers = new ArrayList<>();
    /**
     * Reference to the game
     */
    private Game game;
    private boolean isGameCreated = false;
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
     * @return the name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Adds a player to the lobby
     * @param name is the name of the player that want to join the lobby
     * @throws NameTakenException if there is another player in the lobby with the same name
     * @throws FullLobbyException if the lobby is full
     */
    public void addPlayer(String name) throws NameTakenException, FullLobbyException {
        if (disconnectedPlayers.contains(name)) {
            disconnectedPlayers.remove(name);
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
    public void moveTiles(List<Tile> tiles, int shelfColumn, String player) throws
            IllegalMoveException, GameEndedException {
        try {
            if (game.getEndGame()) throw new GameEndedException();
            game.moveTiles(tiles, shelfColumn, player);
            if (isLastTurn()) game.setGameEnded();
            gameInfo = new GameInfo(game);
            currentPlayer = nextPlayer();
            game.updateCurrentPlayer(currentPlayer.getName());
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
        isGameCreated = true;
    }

    public boolean checkNumberOfPlayers () {
        return (onlinePlayers.size() - disconnectedPlayers.size()) < 2;
    }

    public boolean waitForPlayers () {
        for (int i = 0; i < 2000; i++) {
            try  {
                TimeUnit.MILLISECONDS.sleep(100);
                if (onlinePlayers.size() - disconnectedPlayers.size() > 1) return true;
                if (onlinePlayers.size() - disconnectedPlayers.size() == 0) return false;
            } catch (InterruptedException ignored) {

            }
        }
        return false;
    }

    private boolean isLastTurn () {
        return game.isLastRound() && isLastPlayer();
    }



    /**
     * Writes a message on the chat
     * @param message is the content of the message (String)
     * @param player is the player that sent the message
     */
    public void writeMessage(String message, String player) {}
    public GameInfo getGameInfo () {
        return gameInfo;
    }
    public InitialGameInfo getInitialGameInfo () {
        return new InitialGameInfo(game);
    }

    public String getCurrentPlayer() {
        return currentPlayer.getName();
    }

    public List<String> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    public boolean isGameCreated() {
        return isGameCreated;
    }

    /**
     * @return the next player in the game
     */
    public VirtualPlayer nextPlayer() {
        int index = (onlinePlayers.indexOf(currentPlayer) + 1) % onlinePlayers.size();
        VirtualPlayer player = onlinePlayers.get(index);
        while (disconnectedPlayers.contains(player.getName())) {
            index = (onlinePlayers.indexOf(player) + 1) % onlinePlayers.size();
            player = onlinePlayers.get(index);
        }
        return onlinePlayers.get(index);
    }

    public boolean isLastPlayer () {
        List<String> roundPlayers = new ArrayList<>();
        for (int i = 0; i < onlinePlayers.size(); i++) {
            if (!disconnectedPlayers.contains(onlinePlayers.get(i).getName())) roundPlayers.add(onlinePlayers.get(i).getName());
        }
        return currentPlayer.getName() == roundPlayers.get(roundPlayers.size() - 1);
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
        disconnectedPlayers.add(player.getName());
        if (isGameCreated && currentPlayer.getName().equals(player.getName())) {
            try {
                currentPlayer = nextPlayer();
                game.updateCurrentPlayer(currentPlayer.getName());
            } catch (InvalidPlayerNameException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
