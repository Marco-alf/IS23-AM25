package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final String lobbyName;
    private final String lobbyCreator;
    private final int playerNumber;
    private VirtualPlayer currentPlayer;
    protected List<VirtualPlayer> onlinePlayers;
    private Game game;


    public Lobby(String lobbyCreator, String lobbyName, int playerNumber) {
        this.playerNumber = playerNumber;
        this.lobbyName = lobbyName;
        this.lobbyCreator = lobbyCreator;
        onlinePlayers = new ArrayList<>();
    }
    public String getLobbyName() {
        return lobbyName;
    }

    public void addPlayer(String name) throws NameTakenException, FullLobbyException {
        if (onlinePlayers.stream().map(VirtualPlayer::getName).toList().contains(name)) {
            throw new NameTakenException();
        }
        if (onlinePlayers.size() == playerNumber) {
            throw new FullLobbyException();
        }
        onlinePlayers.add(new VirtualPlayer(name, this));
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers.stream().map(VirtualPlayer::getName).toList();
    }

    public void moveTiles(List<Tile> tiles, int shelfColumn, String player) throws IllegalMoveException {
        try {
            game.moveTiles(tiles, shelfColumn, player);
        } catch (NotInLineException | OutOfBoundException | NoFreeEdgeException | InvalidPlayerNameException |
                 PlayerNotCurrentException | FullColumnException e) {
            throw new IllegalMoveException();
        }
    }

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

    public void writeMessage(String message, String player) {}

    public VirtualPlayer nextPlayer() {
        int index = (onlinePlayers.indexOf(currentPlayer) + 1) % onlinePlayers.size();
        return onlinePlayers.get(index);
    }

    public void disconnectPlayer(VirtualPlayer player) {
        onlinePlayers.remove(player);
    }

}
