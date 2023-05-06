package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import org.junit.jupiter.api.Test;

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
            throw new RuntimeException(e);
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
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        assertEquals(player1, lobby.getCurrentPlayer());
        // after move assertEquals(player2, lobby.getCurrentPlayer());
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
            assertEquals(lobby.getPlayer(player2), lobby.nextPlayer());
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
            lobby.createGame();
        } catch (GameCreationException e) {
            fail();
        }
        assertFalse(lobby.isLastPlayer());
        //after move assertTrue(lobby.isLastPlayer());
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
    }
}