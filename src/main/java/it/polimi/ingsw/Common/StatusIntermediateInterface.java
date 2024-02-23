package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Client Status Intermediate interface
 */
public interface StatusIntermediateInterface extends Remote {
    /**
     * Setter for Client Status Object
     * It makes the server manage Game's evolution
     *
     * @param csi Interface for the Client Status
     * @throws RemoteException      related to RMI
     * @throws InterruptedException related to Thread management
     */
    public void setIntermediate(ClientStatusInterface csi) throws Exception;

    /**
     * Naive KeepAlive implementation, used for disconnections management
     *
     * @throws RemoteException related to RMI
     */
    public void lifeline() throws RemoteException;
}
