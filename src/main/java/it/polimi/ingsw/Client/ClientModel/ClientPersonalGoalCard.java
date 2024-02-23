package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;

import java.rmi.RemoteException;
import java.util.Optional;

/**
 * Personal Goal Card (Client)
 */

public class ClientPersonalGoalCard implements PersonalGoalCardInterface {
    @Expose
    private int type;
    @Expose
    private Optional<ClientObjectCardType>[][] pattern;

    /**
     * Getter method for Personal Goal Card
     *
     * @return type of the card
     */
    @Override
    public int getType() throws RemoteException {
        return type;
    }

    /**
     * getter method
     *
     * @param x coordinate
     * @param y coordinate
     * @return ordinal of ObjectCardType or -1 if the card is empty
     * @throws Exception if coordinates are invalid
     */
    @Override
    public int getOrdinal(int x, int y) throws Exception {
        if (x >= 0 && x <= ShelfInterface.SHELF_DIM_X - 1 && y >= 0 && y <= ShelfInterface.SHELF_DIM_Y - 1)
            if (pattern[x][y].isPresent())
                return pattern[x][y].get().ordinal();
            else return -1;
        else throw new Exception();
    }
}
