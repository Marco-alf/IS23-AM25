package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonalGoalTest {

    @Test
    void calculatePointsPG1() {
        TilesType[][] shelf = new TilesType[][]{{TilesType.PLANTS, null, TilesType.FRAMES, null, null},
                {null, null, null, null, TilesType.CATS},
                {null,  null,  null, TilesType.BOOKS, null},
                {null, TilesType.GAMES, null, null, null},
                {null, null, null, null, null},
                {null, null, TilesType.TROPHIES, null, null}
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL1.calculatePoints(shelf));

        shelf = new TilesType[][]{{TilesType.PLANTS, TilesType.PLANTS, TilesType.PLANTS, null, null},
                {TilesType.BOOKS, TilesType.PLANTS, TilesType.PLANTS, TilesType.CATS, null},
                {TilesType.FRAMES, TilesType.BOOKS, TilesType.FRAMES, TilesType.BOOKS, null},
                {TilesType.TROPHIES, TilesType.GAMES, TilesType.TROPHIES, TilesType.BOOKS, null},
                {TilesType.TROPHIES, TilesType.TROPHIES, TilesType.CATS, TilesType.CATS, TilesType.CATS},
                {TilesType.TROPHIES, TilesType.TROPHIES, TilesType.TROPHIES, TilesType.CATS, TilesType.CATS}
        };
        assertEquals(6, PersonalGoal.PERSONALGOAL1.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL1.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG2() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, null},
                {null, TilesType.PLANTS, null, null, null},
                {TilesType.CATS, null, TilesType.GAMES, null, null},
                {null, null, null, null, TilesType.BOOKS},
                {null, null, null, TilesType.TROPHIES, null},
                {null, null, null, null, TilesType.FRAMES},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL2.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL2.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG3() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, null},
                {TilesType.FRAMES, null, null, TilesType.GAMES, null},
                {null, null, TilesType.PLANTS, null, null},
                {null, TilesType.CATS, null, null, TilesType.TROPHIES},
                {null, null, null, null, null},
                {TilesType.BOOKS, null, null, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL3.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL3.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG4() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, TilesType.GAMES},
                {null, null, null, null, null},
                {TilesType.TROPHIES, null, TilesType.FRAMES, null, null},
                {null, null, null, TilesType.PLANTS, null},
                {null, TilesType.BOOKS, TilesType.CATS, null, null},
                {null, null, null, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL4.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL4.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG5() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, null},
                {null, TilesType.TROPHIES, null, null, null},
                {null, null, null, null, null},
                {null, TilesType.FRAMES, TilesType.BOOKS, null, null},
                {null, null, null, null, TilesType.PLANTS},
                {TilesType.GAMES, null, null, TilesType.CATS, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL5.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL5.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG6() {
        TilesType[][] shelf = new TilesType[][]{{null, null, TilesType.TROPHIES, null, TilesType.CATS},
                {null, null, null, null, null},
                {null, null, null, TilesType.BOOKS, null},
                {null, null, null, null, null},
                {null, TilesType.GAMES, null, TilesType.FRAMES, null},
                {TilesType.PLANTS, null, null, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL6.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL6.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG7() {
        TilesType[][] shelf = new TilesType[][]{{TilesType.CATS, null, null, null, null},
                {null, null, null, TilesType.FRAMES, null},
                {null, TilesType.PLANTS, null, null, null},
                {TilesType.TROPHIES, null, null, null, null},
                {null, null, null, null, TilesType.GAMES},
                {null, null, TilesType.BOOKS, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL7.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL7.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG8() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, TilesType.FRAMES},
                {null, TilesType.CATS, null, null, null},
                {null, null, TilesType.TROPHIES, null, null},
                {TilesType.PLANTS, null, null, null, null},
                {null, null, null, TilesType.BOOKS, null},
                {null, null, null, TilesType.GAMES, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL8.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL8.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG9() {
        TilesType[][] shelf = new TilesType[][]{{null, null, TilesType.GAMES, null, null},
                {null, null, null, null, null},
                {null, null, TilesType.CATS, null, null},
                {null, null, null, null, TilesType.BOOKS},
                {null, TilesType.TROPHIES, null, null, TilesType.PLANTS},
                {TilesType.FRAMES, null, null, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL9.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL9.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG10() {
        TilesType[][] shelf = new TilesType[][]{{null, null, null, null, TilesType.TROPHIES},
                {null, TilesType.GAMES, null, null, null},
                {TilesType.BOOKS, null, null, null, null},
                {null, null, null, TilesType.CATS, null},
                {null, TilesType.FRAMES, null, null, null},
                {null, null, null, TilesType.PLANTS, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL10.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL10.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG11() {
        TilesType[][] shelf = new TilesType[][]{{null, null, TilesType.PLANTS, null, null},
                {null, TilesType.BOOKS, null, null, null},
                {TilesType.GAMES, null, null, null, null},
                {null, null, TilesType.FRAMES, null, null},
                {null, null, null, null, TilesType.CATS},
                {null, null, null, TilesType.TROPHIES, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL11.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL11.calculatePoints(shelf));
    }

    @Test
    void calculatePointsPG12() {
        TilesType[][] shelf = new TilesType[][]{{null, null, TilesType.BOOKS, null, null},
                {null, TilesType.PLANTS, null, null, null},
                {null, null, TilesType.FRAMES, null, null},
                {null, null, null, TilesType.TROPHIES, null},
                {null, null, null, null, TilesType.GAMES},
                {TilesType.CATS, null, null, null, null},
        };
        assertEquals(12, PersonalGoal.PERSONALGOAL12.calculatePoints(shelf));

        shelf = new TilesType[6][5];
        assertEquals(0, PersonalGoal.PERSONALGOAL12.calculatePoints(shelf));
    }

    @Test
    void values() {
        assertEquals(PersonalGoal.PERSONALGOAL1, PersonalGoal.values()[0]);
    }

    @Test
    void valueOf() {
        assertEquals(PersonalGoal.PERSONALGOAL1, PersonalGoal.valueOf("PERSONALGOAL1"));
        assertNotEquals(PersonalGoal.PERSONALGOAL5, PersonalGoal.valueOf("PERSONALGOAL6"));
    }

    @Test
    void getMatrixTest(){
        TilesType[][] shelf = new TilesType[][]{{null, null, TilesType.BOOKS, null, null},
                {null, TilesType.PLANTS, null, null, null},
                {null, null, TilesType.FRAMES, null, null},
                {null, null, null, TilesType.TROPHIES, null},
                {null, null, null, null, TilesType.GAMES},
                {TilesType.CATS, null, null, null, null},
        };
        TilesType[][] matrix = PersonalGoal.PERSONALGOAL12.getMatrix();
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 5; j++){
                assertEquals(shelf[i][j], matrix[i][j]);
            }
        }
    }
}