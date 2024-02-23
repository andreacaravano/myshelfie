package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Move Intermediate Interface
 */
public interface MoveIntermediateInterface extends Remote {
    /**
     * Move parameters
     *
     * @param x      list of coordinates (x)
     * @param y      list of coordinates (y)
     * @param column in the Player's shelf
     * @throws RemoteException related to RMI
     */
    public void setParameters(List<Integer> x, List<Integer> y, int column) throws RemoteException;

    /**
     * Move's creation and confirmation
     *
     * @return validity of the move
     * @throws Exception related to Shelf and Board management
     */
    public boolean make() throws Exception;
}