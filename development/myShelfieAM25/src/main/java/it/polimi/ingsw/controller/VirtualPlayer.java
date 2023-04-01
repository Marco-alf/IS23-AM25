package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.IllegalMoveException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

/**
 * TODO: getShelf(), update(GameState gameState), update(Message: chatState)
 */
public class VirtualPlayer {
    private String name;
    private Lobby lobby;
    public VirtualPlayer(String name) {
        this.name = name;
    }

    public void placeTiles(List<Tile> tiles, int shelfColumn) throws IllegalMoveException {
        lobby.moveTiles(tiles, shelfColumn, name);
    }

    public void disconnect() {
        lobby.disconnectPlayer(this);
    }

    public void writeMessage(String message) {
        lobby.writeMessage(message, name);
    }

    public String getName() {
        return name;
    }
}
