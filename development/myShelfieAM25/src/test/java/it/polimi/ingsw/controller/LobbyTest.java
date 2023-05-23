package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TilesType;
import it.polimi.ingsw.model.commongoal.FullDiagonalGoal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void getLobbyNameTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        assertEquals(lobbyName, lobby.getLobbyName());
    }

    @Test
    void getPlayerNumberTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        assertEquals(playerNumber, lobby.getPlayerNumber());
    }

    @Test
    void addPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "pippo";
        try {
            lobby.addPlayer(player2);
            fail();
        } catch (NameTakenException e) {
            assertTrue(true);
        } catch (FullLobbyException e) {
            fail();
        }
        player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player3 = "paperina";
        try {
            lobby.addPlayer(player3);
            fail();
        } catch (NameTakenException e) {
            fail();
        } catch (FullLobbyException e) {
            assertTrue(true);
        }
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertEquals(player2, lobby.getDisconnectedPlayers().get(0));
        assertEquals(2, lobby.getPlayerNumber());
        //assertEquals(1, lobby.getOnlinePlayers().size());
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        assertEquals(2, lobby.getPlayerNumber());
    }

    @Test
    void getOnlinePlayersTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        assertEquals(player1, lobby.getOnlinePlayers().get(0));
        assertEquals(player2, lobby.getOnlinePlayers().get(1));
    }

    @Test
    void moveTilesTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        boolean isTest = true;
        try {
            lobby.createGame(isTest);
        } catch (GameCreationException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            lobby.moveTiles(tiles, 0, player1);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        tile1 = new Tile(TilesType.CATS, 3, 2);
        tile2 = new Tile(TilesType.FRAMES, 4, 2);
        Tile tile3 = new Tile(TilesType.PLANTS, 5, 2);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        try {
            lobby.moveTiles(tiles, 0, player1);
            fail();
        } catch (IllegalMoveException e) {
            assertTrue(true);
        } catch (GameEndedException e) {
            fail();
        }
        try {
            lobby.moveTiles(tiles, 0, player2);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        tile1 = null;
        tiles = new ArrayList<>();
        tiles.add(tile1);
        try {
            lobby.moveTiles(tiles, 0, player1);
            fail();
        } catch (IllegalMoveException e) {
            assertTrue(true);
        } catch (GameEndedException e) {
            fail();
        }
    }

    @Test
    void createGameTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame();
            fail();
        } catch (GameCreationException e) {
            assertTrue(true);
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
    }

    @Test
    void checkNumberOfPlayersTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 3;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        assertFalse(lobby.checkNumberOfPlayers());
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertTrue(lobby.checkNumberOfPlayers());
    }

    @Test
    void waitForPlayersTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 4;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        assertFalse(lobby.waitForPlayers());
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        assertTrue(lobby.waitForPlayers());
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player1));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertFalse(lobby.waitForPlayers());
    }

    @Test
    void getGameInfoTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame(true);
        } catch (GameCreationException | NotTestException e) {
            fail();
        }
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            lobby.moveTiles(tiles, 0, player1);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        assertEquals(TilesType.TROPHIES, lobby.getGameInfo().getShelf()[5][0]);
        assertEquals(TilesType.FRAMES, lobby.getGameInfo().getShelf()[4][0]);
        assertEquals(player1, lobby.getGameInfo().getPlayers().get(0));
        assertEquals(player2, lobby.getGameInfo().getPlayers().get(1));
        assertEquals(player1, lobby.getGameInfo().getCurrentPlayer());
        tile1 = new Tile(TilesType.CATS, 3, 2);
        tile2 = new Tile(TilesType.FRAMES, 4, 2);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            lobby.moveTiles(tiles, 0, player2);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        assertEquals(player2, lobby.getGameInfo().getCurrentPlayer());
    }

    @Test
    void getInitialGameInfoTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame(true);
        } catch (GameCreationException | NotTestException e) {
            fail();
        }
        assertEquals("RowsGoal (isRegular: false)", lobby.getInitialGameInfo().getCommonGoal1());
        assertEquals("FullDiagonalGoal", lobby.getInitialGameInfo().getCommonGoal2());
        assertEquals(PersonalGoal.PERSONALGOAL1, lobby.getInitialGameInfo().getPersonalGoals().get(player1));
        assertEquals(PersonalGoal.PERSONALGOAL2, lobby.getInitialGameInfo().getPersonalGoals().get(player2));
    }

    @Test
    void getCurrentPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame(true);
        } catch (GameCreationException | NotTestException e) {
            fail();
        }
        assertEquals(player1, lobby.getCurrentPlayer());
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            lobby.moveTiles(tiles, 0, player1);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        assertEquals(player2, lobby.getCurrentPlayer());
    }

    @Test
    void getDisconnectedPlayersTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player1));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertEquals(player1, lobby.getDisconnectedPlayers().get(0));
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertEquals(player2, lobby.getDisconnectedPlayers().get(1));
        assertEquals(2, lobby.getDisconnectedPlayers().size());
    }

    @Test
    void isGameCreatedTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        assertFalse(lobby.isGameCreated());
        try {
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        assertTrue(lobby.isGameCreated());
    }

    @Test
    void nextPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 3;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player3 = "pluto";
        try {
            lobby.addPlayer(player3);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        try {
            assertEquals(lobby.getPlayer(player2), lobby.nextPlayer());
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        try {
            assertEquals(lobby.getPlayer(player3), lobby.nextPlayer());
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
    }

    @Test
    void isLastPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame(true);
        } catch (GameCreationException | NotTestException e) {
            fail();
        }
        assertFalse(lobby.isLastPlayer());
        Tile tile1 = new Tile(TilesType.TROPHIES, 3, 1);
        Tile tile2 = new Tile(TilesType.FRAMES, 4, 1);
        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile1);
        tiles.add(tile2);
        try {
            lobby.moveTiles(tiles, 0, player1);
        } catch (IllegalMoveException | GameEndedException e) {
            fail();
        }
        assertTrue(lobby.isLastPlayer());
    }

    @Test
    void getPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        try {
            assertEquals(lobby.nextPlayer(), lobby.getPlayer(player2));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        String player3 = "pluto";
        try {
            lobby.getPlayer(player3);
            fail();
        } catch (PlayerNotInLobbyException e) {
            assertTrue(true);
        }
    }

    @Test
    void disconnectPlayerTest() {
        String lobbyCreator = "gggg";
        String lobbyName = "lobby";
        int playerNumber = 2;
        Lobby lobby = new Lobby(lobbyCreator, lobbyName, playerNumber);
        String player1 = "pippo";
        try {
            lobby.addPlayer(player1);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        String player2 = "paperino";
        try {
            lobby.addPlayer(player2);
        } catch (NameTakenException | FullLobbyException e) {
            fail();
        }
        try {
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player1));
        } catch (PlayerNotInLobbyException e) {
            fail();
        }
        assertEquals(player1, lobby.getDisconnectedPlayers().get(0));
        String player3 = "pluto";
        try {
            lobby.disconnectPlayer(lobby.getPlayer(player3));
            fail();
        } catch (PlayerNotInLobbyException e) {
            assertTrue(true);
        }
    }
}