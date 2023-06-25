package it.polimi.ingsw.exception;

/**
 * PlayerNotInLobbyException is thrown whenever a client requests an action that is only available inside a lobby but
 * he still is inside the main menù
 */
public class PlayerNotInLobbyException extends Exception{
}
