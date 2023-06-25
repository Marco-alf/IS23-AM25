package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.GameEndedException;
import it.polimi.ingsw.exception.IllegalMoveException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;

import java.util.List;

/**
 * VirtualPlayer is a class used to masks a player. It provides the main information regarding it
 */
public class VirtualPlayer {
    /**
     * name is the name of the corresponding player
     */
    private final String name;
    /**
     * lobby is the reference to the lobby where the client is playing
     */
    private final Lobby lobby;

    /**
     * the constructor of VirtualPlayer initialize the final attributes of the class
     * @param name is the name of the corresponding player
     * @param lobby is the reference to the lobby where the user is playing
     */
    public VirtualPlayer(String name, Lobby lobby) {
        this.name = name;
        this.lobby = lobby;
    }

    /**
     * disconnect is the method invoked in order to propagate a request to quit from a lobby
     */
    public void disconnect() {
        lobby.disconnectPlayer(this);
    }

    /**
     * getName is a getter for the name of the player
     * @return the name of the corresponding player
     */
    public String getName() {
        return name;
    }
}
