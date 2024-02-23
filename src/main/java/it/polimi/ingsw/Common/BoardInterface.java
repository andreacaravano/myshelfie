package it.polimi.ingsw.Common;

import java.rmi.Remote;

/**
 * Game's Board Interface
 */
public interface BoardInterface extends Remote {
    /**
     * Maximum X dimension of the board
     */
    public static final int BOARD_DIM_X = 9;
    /**
     * Maximum Y dimension of the board
     */
    public static final int BOARD_DIM_Y = 9;

    /**
     * Checks if a space is usable given coordinates
     *
     * @param x coordinate
     * @param y coordinate
     * @return usability of that space
     * @throws Exception if parameters are trying to go out of the board
     */
    public boolean isSpaceUsable(int x, int y) throws Exception;

    /**
     * Returns Object Card's type ordinal, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's ordinal
     * @throws Exception if parameters are trying to go out of the board
     */
    public int getCardOrdinalFromSpace(int x, int y) throws Exception;

    /**
     * Returns Object Card's image path, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    public String getCardImageFromSpace(int x, int y) throws Exception;
}
