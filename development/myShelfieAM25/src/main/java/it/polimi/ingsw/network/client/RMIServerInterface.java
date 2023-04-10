package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.RMIClientInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    String NAME = "myshelfie";
    void receiveMsgFromClient (Serializable msg) throws RemoteException;
    void register (RMIClientInterface rmiClient) throws RemoteException;

}
