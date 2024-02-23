package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Shelf Interface
 */
public interface ShelfInterface extends Remote {
    /**
     * Maximum X dimension of the shelf
     */
    public static final int SHELF_DIM_X = 6;
    /**
     * Maximum Y dimension of the shelf
     */
    public static final int SHELF_DIM_Y = 5;

    /**
     * Returns Object Card's type ordinal in position (x, y) if valid (else -1)
     *
     * @param x coordinate
     * @param y coordinate
     * @return requested Object Card's type ordinal
     * @throws Exception if coordinates are invalid
     */
    public int getCardOrdinal(int x, int y) throws Exception;

    /**
     * Returns Object Card's image path in position (x, y) if valid (else null)
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    public String getCardImage(int x, int y) throws Exception;

    /**
     * @return true if the Shelf is full
     * @throws RemoteException related to RMI
     */
    public boolean isFull() throws RemoteException;
}
