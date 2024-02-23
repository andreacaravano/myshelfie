package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ScoreCardInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Objective ScoreCards implementation
 */
public class ScoreCard extends UnicastRemoteObject implements ScoreCardInterface {
    /**
     * Score Card's value
     */
    @Expose
    final int value;

    /**
     * ScoreCard constructor
     *
     * @param value sets the value to the given Integer
     * @throws RemoteException related to RMI
     */
    public ScoreCard(int value) throws RemoteException {
        this.value = value;
    }

    /**
     * @return value of the ScoreCard
     */
    public int getValue() {
        return value;
    }

    /**
     * Refreshed copy, after server reload from file
     *
     * @return new copy of the object
     * @throws RemoteException related to RMI
     */
    public ScoreCard getCopy() throws RemoteException {
        return new ScoreCard(this.value);
    }
}
