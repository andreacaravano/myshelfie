package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

/**
 * CommonGoalCard Interface
 */
public interface CommonGoalCardInterface extends Remote {
    /**
     * Limit of Common Goal Cards.
     */
    public static final int LIMIT = 12;

    /**
     * This method returns the textual description of the pattern that the player has to achieve, in order
     * to gain points.
     *
     * @return String: textual description of the pattern
     * @throws RemoteException related to RMI
     */
    public String getDescription() throws RemoteException;

    /**
     * Common Goal Card's type (ordinal)
     *
     * @return type
     * @throws RemoteException related to RMI
     */
    public int getType() throws RemoteException;

    /**
     * @return Score Increments stack
     * @throws RemoteException related to RMI
     */
    public Stack<ScoreCardInterface> getIncrements() throws RemoteException;
}
