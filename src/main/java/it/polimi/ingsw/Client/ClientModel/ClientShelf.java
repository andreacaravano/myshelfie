package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ShelfInterface;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Shelf (Client)
 */
public class ClientShelf implements ShelfInterface {
    /**
     * Shelf implementation of Object Cards
     */
    @Expose
    private Optional<ClientObjectCard>[][] cards;

    /**
     * Returns Object Card's type ordinal in position (x, y) if valid (else -1)
     *
     * @param x coordinate
     * @param y coordinate
     * @return requested Object Card's type ordinal
     * @throws Exception if coordinates are invalid
     */
    @Override
    public int getCardOrdinal(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            if (cards[x][y].isPresent())
                return cards[x][y].get().getType().ordinal();
            else return -1;
        else throw new Exception();
    }

    /**
     * Returns Object Card's image path in position (x, y) if valid (else null)
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    @Override
    public String getCardImage(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            if (cards[x][y].isPresent())
                return cards[x][y].get().getType().name().concat("-").concat(String.valueOf(cards[x][y].get().getImage()));
            else return null;
        else throw new Exception();
    }

    /**
     * @return true if the Shelf is full
     */
    @Override
    public boolean isFull() throws RemoteException {
        return Arrays.stream(cards)
                .flatMap(Arrays::stream)
                .noneMatch(Optional::isEmpty);
    }
}
