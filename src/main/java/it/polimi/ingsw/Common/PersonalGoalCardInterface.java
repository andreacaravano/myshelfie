package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Personal Goal Card Interface
 */
public interface PersonalGoalCardInterface extends Remote {
    /**
     * Limit of Personal Goal Cards.
     */
    public static final int LIMIT = 12;

    /**
     * Getter method for Personal Goal Card
     *
     * @return type of the card
     * @throws RemoteException related to RMI
     */
    public int getType() throws RemoteException;

    /**
     * getter method
     *
     * @param x coordinate
     * @param y coordinate
     * @return ordinal of ObjectCardType or -1 if the card is empty
     * @throws Exception if coordinates are invalid
     */
    public int getOrdinal(int x, int y) throws Exception;
}
