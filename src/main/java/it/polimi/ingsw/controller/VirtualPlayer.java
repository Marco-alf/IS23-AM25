package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.GameEndedException;
import it.polimi.ingsw.exception.IllegalMoveException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

/**
 * TODO: getShelf(), update(GameState gameState), update(Message: chatState)
 */
public class VirtualPlayer {
    private final String name;
    private final Lobby lobby;
    public VirtualPlayer(String name, Lobby lobby) {
        this.name = name;
        this.lobby = lobby;
    }

    public void placeTiles(List<Tile> tiles, int shelfColumn) throws IllegalMoveException, GameEndedException {
        lobby.moveTiles(tiles, shelfColumn, name);
    }

    public void disconnect() {
        lobby.disconnectPlayer(this);
    }

    public String getName() {
        return name;
    }
}
