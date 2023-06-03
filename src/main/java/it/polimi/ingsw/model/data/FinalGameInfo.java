package it.polimi.ingsw.model.data;

import it.polimi.ingsw.exception.InvalidPlayerNameException;
import it.polimi.ingsw.model.Game;

import java.util.*;


public class FinalGameInfo extends GameInfo{
    /**
     * finalPoints is an HashMap where the keys are the name of the players and the value is a list with the points
     * scored by each player divided by topic
     */
    private final Map<String, List<Integer>> finalPoints = new HashMap<>();

    /**
     * constructor of a FinalGameInfo object
     * @param game is the game from which the information are retrieved
     * @throws InvalidPlayerNameException is an exception that is thrown if there is a player in this onlinePlayers that
     * is not really in the game
     */
    public FinalGameInfo(Game game) throws InvalidPlayerNameException {
        super(game);
        for (int i = 0; i < game.getOnlinePlayers().size(); i++) {
            String player = game.getOnlinePlayers().get(i);
            List<Integer> points = new ArrayList<>();
            points.add(game.searchPlayer(player).calculateCommonPoints()[0]);
            points.add(game.searchPlayer(player).calculateCommonPoints()[1]);
            points.add(game.searchPlayer(player).calculatePersonalPoints());
            points.add(game.searchPlayer(player).calculateAdjacencyPoints());
            points.add(game.searchPlayer(player).getFinalPoint());
            finalPoints.put(player, points);
        }
    }

    /**
     * getter for the finalPoints HashMap
     * @return the HashMap containing the final points
     */
    public Map<String, List<Integer>> getFinalPoints() {
        return new HashMap<>(finalPoints);
    }
}
