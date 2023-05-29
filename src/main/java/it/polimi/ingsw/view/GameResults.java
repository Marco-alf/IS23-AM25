package it.polimi.ingsw.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameResults {
    private final List<String> leaderboard = new ArrayList<>();
    private final List<Integer> totals = new ArrayList<>();
    private final List<Integer> personalPoints = new ArrayList<>();
    private final List<Integer> commonPoints = new ArrayList<>();
    private final List<Integer> adjacencyPoints = new ArrayList<>();

    public List<String> getLeaderboard() {
        return leaderboard;
    }

    public void addLeaderboard(String player) {
        this.leaderboard.add(player);
    }

    public List<Integer> getTotals() {
        return totals;
    }

    public void addTotal(Integer points) {
        this.totals.add(points);
    }

    public List<Integer> getPersonalPoints() {
        return personalPoints;
    }

    public void addPersonalPoints(Integer personalPoints) {
        this.personalPoints.add(personalPoints);
    }

    public List<Integer> getCommonPoints() {
        return commonPoints;
    }

    public void addCommonPoints(Integer commonPoints) {
        this.commonPoints.add(commonPoints);
    }

    public List<Integer> getAdjacencyPoints() {
        return adjacencyPoints;
    }

    public void addAdjacencyPoints(Integer adjacencyPoints) {
        this.adjacencyPoints.add(adjacencyPoints);
    }
}
