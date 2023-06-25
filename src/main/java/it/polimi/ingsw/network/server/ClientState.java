package it.polimi.ingsw.network.server;

/**
 * ClientState is an enum used to identify the state of a client.
 * The client is CONNECTED whenever it is inside the main menù of the game
 * The client is IN_LOBBY whenever it is in a lobby waiting for the game to start
 * The client is IN_GAME whenever it is playing a game
 */
public enum ClientState {
    CONNECTED,
    IN_LOBBY,
    IN_GAME
}
