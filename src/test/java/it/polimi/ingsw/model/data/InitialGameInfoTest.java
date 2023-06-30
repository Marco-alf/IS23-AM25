package it.polimi.ingsw.model.data;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InitialGameInfoTest {

    @Test
    void initialGameInfo() {
        String n1 = "pippo";
        String n2 = "paperino";
        List<String> players = new ArrayList<>();
        players.add(n1);
        players.add(n2);
        Game game = null;
        try {
            game = new Game(players, true);
        } catch (ShelfCreationException | PlayersEmptyException | NotTestException e) {
            fail();
        }
        InitialGameInfo initial = new InitialGameInfo(game);
        assertNotNull(initial);
        assertEquals(n1, initial.getCurrentPlayer());
        assertEquals("RowsGoal (isRegular: false)", initial.getCommonGoal1());
        assertEquals("FullDiagonalGoal", initial.getCommonGoal2());
        for(int i = 0; i <= 5; i++){
            for(int j = 0; j <= 4; j++){
                assertNull(initial.getShelves().get(n1)[i][j]);
                assertNull(initial.getShelves().get(n2)[i][j]);
            }
        }
        assertEquals(PersonalGoal.PERSONALGOAL1, initial.getPersonalGoals().get(n1));
        assertEquals(PersonalGoal.PERSONALGOAL2, initial.getPersonalGoals().get(n2));
        assertEquals(0, initial.getCommonPoints(n1, 0));
        assertEquals(0, initial.getCommonPoints(n1, 1));
        assertEquals(0, initial.getCommonPoints(n2, 0));
        assertEquals(0, initial.getCommonPoints(n2, 1));
    }
}