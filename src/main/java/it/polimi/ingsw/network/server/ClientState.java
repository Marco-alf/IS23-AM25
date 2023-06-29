package it.polimi.ingsw.network.server;

/**
 * ClientState is an enum used to identify the state of a client.
 * The client is CONNECTED whenever it is inside the main menù of the game
 * The client is IN_LOBBY whenever it is in a lobby waiting for the game to start
 * The client is IN_GAME whenever it is playing a game
 */
public enum ClientState {
    /**
     * a client is CONNECTED if it's connected to the server, and it's neither in a lobby nor in a game
     */
    CONNECTED,
    /**
     * a client is IN_LOBBY if it's in a lobby waiting for other player
     */
    IN_LOBBY,
    /**
     * a client is IN_GAME if it is playing a game
     */
    IN_GAME
}
