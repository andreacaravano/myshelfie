package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Common.MoveIntermediateInterface;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.Player;
import it.polimi.ingsw.Server.Model.Shelf;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Intermediate for RMI Turn management, to simplify multiple games management
 */
public class MoveIntermediate extends UnicastRemoteObject implements MoveIntermediateInterface {
    /**
     * Game's board
     */
    private final Board b;
    /**
     * Player's shelf
     */
    private final Shelf s;
    /**
     * Used for validation checks
     */
    private final Player p;
    /**
     * Game's controller
     */
    private final GameController gc;
    /**
     * List of move's coordinates (x)
     */
    private List<Integer> x = null;
    /**
     * List of move's coordinates (y)
     */
    private List<Integer> y = null;
    /**
     * Shelf's column
     */
    private int column;

    /**
     * Move Intermediate constructor
     *
     * @param b  Game's board
     * @param s  Player's shelf
     * @param gc Game's controller
     * @param p  Player, used for validity check
     * @throws RemoteException related to RMI
     */
    public MoveIntermediate(Board b, Shelf s, GameController gc, Player p) throws RemoteException {
        this.b = b;
        this.s = s;
        this.gc = gc;
        this.p = p;
    }

    /**
     * Move parameters
     *
     * @param x      list of coordinates (x)
     * @param y      list of coordinates (y)
     * @param column in the Player's shelf
     */
    public void setParameters(List<Integer> x, List<Integer> y, int column) {
        this.x = x;
        this.y = y;
        this.column = column;
    }

    /**
     * Move's creation and confirmation
     *
     * @return validity of the move
     * @throws Exception related to Shelf and Board management
     */
    public boolean make() throws Exception {
        if (gc == null || !gc.getActivePlayer().equals(p))
            return false;
        Move m = new Move(column, x, y, b, s);

        return gc.make(m);
    }
}
