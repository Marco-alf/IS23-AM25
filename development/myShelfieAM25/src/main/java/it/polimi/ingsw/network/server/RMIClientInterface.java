package it.polimi.ingsw.network.server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * public interface of a client used by the server to identify a client
 */
public interface RMIClientInterface extends Remote {
    /**
     * abstract method that will handle the responses of the client to a message from the server
     * @param msg is the serializable message that the server sends to the client
     * @throws RemoteException is thrown whenever the remote is unable to respond to the request
     */
    void receiveMsgFromServer (Serializable msg) throws RemoteException;
}
