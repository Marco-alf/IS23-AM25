package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBrokerTest {

    @Test
    void getLobbies() {
        GameBroker gameBroker = new GameBroker();
        String player1 = "Pippo";
        String lobby1 = "lobby1";
        try {
            gameBroker.createLobby(player1, lobby1, 2);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        String lobby2 = "lobby2";
        try {
            gameBroker.createLobby(player1, lobby2, 3);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        String lobby3 = "lobby3";
        try {
            gameBroker.createLobby(player1, lobby3, 4);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        assertEquals(3, gameBroker.getLobbies().size());
        assertEquals(lobby1, gameBroker.getLobbies().get(2));
        assertEquals(lobby2, gameBroker.getLobbies().get(1));
        assertEquals(lobby3, gameBroker.getLobbies().get(0));
    }

    @Test
    void createLobby() {
        GameBroker gameBroker = new GameBroker();
        String player1 = "Pippo";
        String lobby1 = "lobby";
        String illegalNameLobby = "    ";
        try {
            gameBroker.createLobby(player1, illegalNameLobby, 4);
        } catch (ExistingLobbyException | IllegalPlayerNameException e) {
            fail();
        } catch (InvalidLobbyNameException e) {
            assertTrue(true);
        }
        try {
            gameBroker.createLobby(player1, lobby1, 2);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        try {
            gameBroker.createLobby(player1, lobby1, 4);
            fail();
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            assertTrue(true);
        }
        String lobby2 = "lobby2";
        try {
            gameBroker.createLobby(player1, lobby2, 3);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        assertEquals(2, gameBroker.getLobbies().size());
    }

    @Test
    void closeLobby() {
        GameBroker gameBroker = new GameBroker();
        String player1 = "Pippo";
        String lobby1 = "lobby";
        try {
            gameBroker.createLobby(player1, lobby1, 2);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        String lobby2 = "lobby2";
        try {
            gameBroker.createLobby(player1, lobby2, 3);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        assertEquals(2, gameBroker.getLobbies().size());
        try {
            gameBroker.closeLobby(gameBroker.getLobby(lobby2));
        } catch (NonExistingLobbyException e) {
            fail();
        }
        assertEquals(1, gameBroker.getLobbies().size());
    }

    @Test
    void addPlayer() {
        GameBroker gameBroker = new GameBroker();
        String player1 = "Pippo";
        String lobby1 = "lobby";
        try {
            gameBroker.createLobby(player1, lobby1, 2);
        } catch (ExistingLobbyException | InvalidLobbyNameException | IllegalPlayerNameException e) {
            fail();
        }
        Lobby lobby = null;
        try {
            lobby = gameBroker.getLobby(lobby1);
        } catch (NonExistingLobbyException e) {
            fail();
        }
        assertEquals(1, lobby.getOnlinePlayers().size());
        try {
            gameBroker.addPlayer(lobby1, player1);
            fail();
        } catch (NonExistingLobbyException | FullLobbyException | IllegalPlayerNameException e) {
            fail();
        } catch (NameTakenException e) {
            assertTrue(true);
        }
        String player2 = "Pluto";
        try {
            gameBroker.addPlayer(lobby1, player2);
        } catch (NonExistingLobbyException | FullLobbyException | IllegalPlayerNameException | NameTakenException e) {
            fail();
        }
        assertEquals(2, lobby.getOnlinePlayers().size());
    }
}