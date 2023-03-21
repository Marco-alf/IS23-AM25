package it.polimi.ingsw.model;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * TO DO: model of the class Game, ALL methods have to be implemented
 */
public class Game
{
    private ArrayList<Player> players;
    private ArrayList<Player> onlinePlayers;
    private LivingRoom board;
    private Player currentPlayer;
    

    public Game(ArrayList<String> players) {
    }

    public ArrayList<Integer> getPlayersPoints() {
        return new ArrayList<Integer>();
    }

    public Player updateCurrentPlayer(){
        return players.get(0);
    }

    public void moveTiles(ArrayList<Tile> tiles, int shelfColumn, String player){

    }

    public ArrayList<String> getOnlinePlayers(){
        ArrayList<String> online = new ArrayList<String>();
        return online;
    }

    public void disconnectPlayer(String player){

    }

}
