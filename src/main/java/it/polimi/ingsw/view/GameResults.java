package it.polimi.ingsw.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GameResults is a support class used to save the results of a game
 */
public class GameResults {
    /**
     * leaderboard is an ordered list of the players based on the points scored
     */
    private final List<String> leaderboard = new ArrayList<>();
    /**
     * totals is the list of all the points scored by each player. The ordering is the same as in leaderboard
     */
    private final List<Integer> totals = new ArrayList<>();
    /**
     * personalPoints is the list of all the personal points scored by each player. The ordering is the same as in leaderboard
     */
    private final List<Integer> personalPoints = new ArrayList<>();
    /**
     * commonPoints is the list of all the common points scored by each player. The ordering is the same as in leaderboard
     */
    private final List<Integer> commonPoints = new ArrayList<>();
    /**
     * adjacencyPoints is the list of all the adjacency points scored by each player. The ordering is the same as in leaderboard
     */
    private final List<Integer> adjacencyPoints = new ArrayList<>();

    /**
     * public getter for the leaderboard
     * @return the leaderboard
     */
    public List<String> getLeaderboard() {
        return leaderboard;
    }

    /**
     * addLeaderboard is a method used to add a player to the leaderboard
     * @param player is the name of the player to be added to the leaderboard
     */
    public void addLeaderboard(String player) {
        this.leaderboard.add(player);
    }

    /**
     * getTotals is a getter for the total points of each player
     * @return the list of all the points scored
     */
    public List<Integer> getTotals() {
        return totals;
    }

    /**
     * addTotal is a method used to add a new score to the list of total points
     * @param points is the score to add
     */
    public void addTotal(Integer points) {
        this.totals.add(points);
    }

    /**
     * getPersonalPoints is a getter for the list of personal points
     * @return the list of all the scores relative to personal points
     */
    public List<Integer> getPersonalPoints() {
        return personalPoints;
    }

    /**
     * addTotal is a method used to add a new score to the list of personal points
     * @param personalPoints is the score to add
     */
    public void addPersonalPoints(Integer personalPoints) {
        this.personalPoints.add(personalPoints);
    }

    /**
     * getCommonPoints is a getter for the list of personal points
     * @return the list of all the scores relative to common goal points
     */
    public List<Integer> getCommonPoints() {
        return commonPoints;
    }

    /**
     * addCommonPoints is a method used to add a new score to the list of common goal points
     * @param commonPoints is the score to add
     */
    public void addCommonPoints(Integer commonPoints) {
        this.commonPoints.add(commonPoints);
    }

    /**
     * getAdjacencyPoints is a getter for the list of personal points
     * @return the list of all the scores relative to adjacency points
     */
    public List<Integer> getAdjacencyPoints() {
        return adjacencyPoints;
    }
    /**
     * addAdjacencyPoints is a method used to add a new score to the list of adjacency points
     * @param adjacencyPoints is the score to add
     */
    public void addAdjacencyPoints(Integer adjacencyPoints) {
        this.adjacencyPoints.add(adjacencyPoints);
    }
}
