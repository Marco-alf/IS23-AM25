package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.RMIClientInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/** interface used for remote method invocation by the server and implemented by the client */
public interface RMIServerInterface extends Remote {
    /** */
    String NAME = "myshelfie";
    /** method to receive messages from the client
     * @param msg is a serializable message that is being received by the server
     */
    void receiveMsgFromClient (Serializable msg) throws RemoteException;

    /** method used to register rmi connections to the game server
     * @param rmiClient is a remote object representing the client to be added to the list of all connected clients
     */
    void register (RMIClientInterface rmiClient) throws RemoteException;
    /**
     * checkServerAliveness is a method used to check if the connection with the server is active
     */
    boolean checkAliveness () throws RemoteException;

}
