package it.polimi.ingsw.Common;

/**
 * RMI Client abstraction
 */
public interface RMIClientInterface {
    /**
     * Client's evolution implementation
     *
     * @return desire to continue login (otherwise ignored)
     */
    public boolean evolve();
}
