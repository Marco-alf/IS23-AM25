package it.polimi.ingsw.model.data;

import it.polimi.ingsw.exception.InvalidPlayerNameException;
import it.polimi.ingsw.model.Game;

import java.util.*;

public class FinalGameInfo extends GameInfo{

    private final Map<String, List<Integer>> finalPoints = new HashMap<>();
    public FinalGameInfo(Game game) throws InvalidPlayerNameException {
        super(game);
        for (int i = 0; i < game.getOnlinePlayers().size(); i++) {
            String player = game.getOnlinePlayers().get(i);
            List<Integer> points = new ArrayList<>();
            points.add(game.searchPlayer(player).calculateCommonPoints()[0]);
            points.add(game.searchPlayer(player).calculateCommonPoints()[1]);
            points.add(game.searchPlayer(player).calculatePersonalPoints());
            points.add(game.searchPlayer(player).calculateAdjacencyPoints());
            finalPoints.put(player, points);
        }
    }
    public Map<String, List<Integer>> getFinalPoints() {
        return new HashMap<>(finalPoints);
    }
}
