package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;

import java.util.Optional;

/**
 * Board Spaces (client)
 */
public class ClientBoardSpace {
    @Expose
    private final int x;
    @Expose
    private final int y;
    @Expose
    private final boolean usable;
    @Expose
    private final Optional<Integer> dots;
    @Expose
    private Optional<ClientObjectCard> card;

    /**
     * ClientBoardSpace class constructor
     *
     * @param x      coordinate
     * @param y      coordinate
     * @param usable usability of a space in both absolute and players-dependent criteria
     * @param dots   number of dots on that space (based on a number of players)
     */
    public ClientBoardSpace(int x, int y, boolean usable, Optional<Integer> dots) {
        this.x = x;
        this.y = y;
        this.usable = usable;
        this.dots = dots;
        this.card = Optional.empty();
    }

    /**
     * @return usability of the space
     */
    public boolean isUsable() {
        return usable;
    }

    /**
     * @return card in the space (as an Optional)
     */
    public Optional<ClientObjectCard> getCard() {
        return card;
    }
}
