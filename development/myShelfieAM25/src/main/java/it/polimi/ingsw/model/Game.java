package it.polimi.ingsw.model;


import it.polimi.ingsw.exception.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TO DO: model of the class Game, ALL methods have to be implemented
 */
public class Game
{
    private final List<Player> players;
    private final List<Player> onlinePlayers;
    private final LivingRoom board;
    private Player currentPlayer;
    

    public Game(List<String> players) throws GameCreationException {
        try{
            this.board = new LivingRoom(players.size());
        } catch (InvalidCommonGoalCardException e){
            throw new GameCreationException();
        } catch (InvalidPlayerNumberException e) {
            throw new GameCreationException();
        }

        this.players = players.stream()
                .map( name -> new Player(name, board, PersonalGoal.PERSONALGOAL1) )
                .collect(Collectors.toList());

        onlinePlayers = new ArrayList<>(this.players);
    }

    public void updateCurrentPlayer(String name) throws InvalidPlayerNameException {
        currentPlayer = searchPlayer(name);
    }

    public void moveTiles(List<Tile> tiles, int shelfColumn, String name) throws InvalidPlayerNameException,
            PlayerNotCurrentException,
            NotInLineException,
            NoFreeEdgeException,
            OutOfBoundException,
            NullPointerException,
            FullColumnException
            {
        if(!currentPlayer.getName().equals(name)){
            throw new PlayerNotCurrentException();
        }
        searchPlayer(name).moveTiles(tiles, shelfColumn);
    }

    public List<String> getOnlinePlayers(){
        return onlinePlayers.stream().map(Player::getName).collect(Collectors.toList());
    }

    public void disconnectPlayer(String name) throws InvalidPlayerNameException {
        onlinePlayers.remove(searchPlayer(name));
    }
    public void reconnect(String name) throws InvalidPlayerNameException {
        onlinePlayers.add(searchPlayer(name));
    }

    public List<Integer> getPlayersPoints() throws OutOfBoundException {
        List<Integer> results = new ArrayList<>();

        for (Player p : players) {
            results.add(p.calculateCommonPoints() + p.calculatePersonalPoints());
        }
        return results;
    }

    private Player searchPlayer(String name) throws InvalidPlayerNameException{
        for(Player p : players){
            if(p.getName().equals(name))
                return p;
        }
        throw new InvalidPlayerNameException();
    }
}
