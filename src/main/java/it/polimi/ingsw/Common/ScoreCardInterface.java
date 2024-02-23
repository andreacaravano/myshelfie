package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Score Cards interface
 */
public interface ScoreCardInterface extends Remote {
    /**
     * @return value of the ScoreCard
     * @throws RemoteException related to RMI
     */
    public int getValue() throws RemoteException;
}
