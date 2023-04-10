package it.polimi.ingsw.network.server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
    void receiveMsgFromServer (Serializable msg) throws RemoteException;
}
