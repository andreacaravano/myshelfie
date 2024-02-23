package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Move (Client)
 */

public class ClientMove {
    @Expose
    private int column;
    @Expose
    private List<Integer> x;
    @Expose
    private List<Integer> y;

    /**
     * Game's Move
     *
     * @param column Library's column
     * @param x      List of Move coordinates (x)
     * @param y      List of Move coordinates (y)
     */
    public ClientMove(int column, List<Integer> x, List<Integer> y) {
        this.column = column;
        this.x = x;
        this.y = y;
    }
}
