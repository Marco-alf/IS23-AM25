package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the game broker. It manages the creation and deletion of lobbies, and the addition of players to a lobby.
 */
public class GameBroker {
    /**
     *  map that links a unique name to the corresponding lobby object
     */
    private final Map<String, Lobby> lobbies;

    /**
     * Constructor for game broker
     */
    public GameBroker() {
        this.lobbies = new HashMap<>();
    }

    /**
     * @return the list of existing lobbies
     */
    public List<String> getLobbies() {
        List<String> lobbyList = new ArrayList<>();
        for(Map.Entry<String, Lobby> entry : lobbies.entrySet()){
            lobbyList.add(entry.getValue().getLobbyName());
        }
        return lobbyList;
    }

    /**
     * Create a new lobby and adds his name to the list of existing lobbies
     * @param creatorName name of the player that creates the lobby
     * @param lobbyName unique name of the lobby
     * @param playerNumber number of players in the lobby
     * @throws ExistingLobbyException if the name of the lobby is already used in another lobby
     * @throws InvalidLobbyNameException if the name of the lobby is an empty string
     * @throws IllegalPlayerNameException if the name of the player is an empty string
     */
    public void createLobby(String creatorName, String lobbyName, int playerNumber) throws ExistingLobbyException, InvalidLobbyNameException, IllegalPlayerNameException {
        if(lobbyName == null || lobbyName.isBlank()) throw new InvalidLobbyNameException();

        if(lobbies.get(lobbyName)!=null) throw new ExistingLobbyException();

        Lobby newLobby = new Lobby(creatorName, lobbyName, playerNumber);
        lobbies.put(lobbyName, newLobby);
        try {
            newLobby.addPlayer(creatorName);
        } catch (NameTakenException | FullLobbyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the lobby and removes his name from the list of existing lobbies
     * @param lobby reference to the lobby that need to be closed
     */
    public void closeLobby(Lobby lobby) {
        lobbies.remove(lobby.getLobbyName());
        System.out.println("lobby closed");

    }

    /**
     * Add a player to an existing lobby
     * @param lobby name of the existing lobby
     * @param player name of the player that want to join the lobby
     * @throws NonExistingLobbyException if the lobby doesn't already exist
     * @throws NameTakenException if the name of the player is already taken from a player in the lobby
     * @throws FullLobbyException if a player wants to join a lobby and the number of players in the lobby is equals to the maximum number of players
     * @throws IllegalPlayerNameException if the name of the player is an empty string
     */
    public void addPlayer(String lobby, String player) throws NonExistingLobbyException, NameTakenException, FullLobbyException, IllegalPlayerNameException {
        if(lobbies.get(lobby)==null) throw new NonExistingLobbyException();
        lobbies.get(lobby).addPlayer(player);
    }

    /**
     * getter for a lobby
     * @param lobbyName name of the lobby
     * @return the reference to the lobby object linked to the name
     * @throws NonExistingLobbyException it the lobby doesn't exist
     */
    public Lobby getLobby(String lobbyName) throws NonExistingLobbyException{
        return lobbies.get(lobbyName);
    }
}
