package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.ExistingLobbyException;
import it.polimi.ingsw.exception.FullLobbyException;
import it.polimi.ingsw.exception.NameTakenException;
import it.polimi.ingsw.exception.NonExistingLobbyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBroker {
    private final Map<String, Lobby> lobbies;

    public GameBroker() {
        this.lobbies = new HashMap<>();
    }

    public List<String> getLobbies() {
        List<String> lobbyList = new ArrayList<>();
        for(Map.Entry<String, Lobby> entry : lobbies.entrySet()){
            lobbyList.add(entry.getValue().getLobbyName());
        }
        return lobbyList;
    }

    public void createLobby(String creatorName, String lobbyName, int playerNumber) throws ExistingLobbyException {
        if(lobbies.get(lobbyName)!=null) throw new ExistingLobbyException();

        Lobby newLobby = new Lobby(creatorName, lobbyName, playerNumber);
        lobbies.put(lobbyName, newLobby);
        try {
            newLobby.addPlayer(creatorName);
        } catch (NameTakenException | FullLobbyException e) {
            throw new RuntimeException(e);
        }
    }
    public void addPlayer(String lobby, String player) throws NonExistingLobbyException, NameTakenException, FullLobbyException {
        if(lobbies.get(lobby)==null) throw new NonExistingLobbyException();
        lobbies.get(lobby).addPlayer(player);
    }
    public Lobby getLobby(String lobbyName) throws NonExistingLobbyException{
        return lobbies.get(lobbyName);
    }
}
