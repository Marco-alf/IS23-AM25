package it.polimi.ingsw.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VirtualPlayerTest {

    @Test
    void getName() {
        String name = "pippo";
        Lobby lobby = new Lobby(name, "lobby", 2);
        VirtualPlayer virtualPlayer = new VirtualPlayer(name, lobby);
        assertEquals(name, virtualPlayer.getName());
    }
}