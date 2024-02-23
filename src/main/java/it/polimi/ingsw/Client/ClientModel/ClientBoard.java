package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.BoardInterface;

/**
 * Board (Client)
 */

public class ClientBoard implements BoardInterface {
    @Expose
    private ClientBoardSpace[][] spaces;

    /**
     * Checks if a space is usable given coordinates
     *
     * @param x coordinate
     * @param y coordinate
     * @return usability of that space
     * @throws Exception if parameters are trying to go out of the board
     */
    @Override
    public boolean isSpaceUsable(int x, int y) throws Exception {
        if (x >= 0 && x < BOARD_DIM_X && y >= 0 && y < BOARD_DIM_Y)
            return spaces[x][y].isUsable();
        else throw new Exception();
    }

    /**
     * Returns Object Card's type ordinal, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's ordinal
     * @throws Exception if parameters are trying to go out of the board
     */
    @Override
    public int getCardOrdinalFromSpace(int x, int y) throws Exception {
        if (x >= 0 && x <= BOARD_DIM_X - 1 && y >= 0 && y <= BOARD_DIM_Y - 1)
            if (spaces[x][y].getCard().isPresent())
                return spaces[x][y].getCard().get().getType().ordinal();
            else return -1;
        else throw new Exception();
    }

    /**
     * Returns Object Card's image path, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    @Override
    public String getCardImageFromSpace(int x, int y) throws Exception {
        if (x >= 0 && x <= BOARD_DIM_X - 1 && y >= 0 && y <= BOARD_DIM_Y - 1)
            if (spaces[x][y].getCard().isPresent())
                return spaces[x][y].getCard().get().getType().name().concat("-").concat(String.valueOf(spaces[x][y].getCard().get().getImage()));
            else return null;
        else throw new Exception();
    }
}
