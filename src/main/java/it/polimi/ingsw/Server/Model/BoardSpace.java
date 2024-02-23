package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;

import java.util.Optional;

/**
 * Board Spaces
 */
public class BoardSpace {
    /**
     * Coordinate
     */
    @Expose
    private final int x;
    /**
     * Coordinate
     */
    @Expose
    private final int y;
    /**
     * Usability of the space in the board/current game
     */
    @Expose
    private final boolean usable;
    /**
     * Number of (eventual) dots in the space
     */
    @Expose
    private final Optional<Integer> dots;
    /**
     * (eventual) Object Card in the space
     */
    @Expose
    private Optional<ObjectCard> card;

    /**
     * BoardSpace class constructor
     *
     * @param x      coordinate
     * @param y      coordinate
     * @param usable usability of a space in both absolute and players-dependent criteria
     * @param dots   number of dots on that space (based on a number of players)
     */
    public BoardSpace(int x, int y, boolean usable, Optional<Integer> dots) {
        this.x = x;
        this.y = y;
        this.usable = usable;
        this.dots = dots;
        this.card = Optional.empty();
    }

    /**
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return y coordinate
     */
    public int getY() {
        return y;
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
    public Optional<ObjectCard> getCard() {
        return card;
    }

    /**
     * Sets the card in the space
     *
     * @param card the card
     */
    public void setCard(Optional<ObjectCard> card) {
        this.card = card;
    }

    /**
     * @return card in the space (as a plain Nullable type)
     */
    public ObjectCard getPlainCard() {
        return card.orElse(null);
    }

    /**
     * @return number of dots in a space (as Optional)
     */
    public Optional<Integer> getDots() {
        return dots;
    }

    /**
     * @return number of dots in a space (as a plain Nullable type)
     */
    public int getPlainDots() {
        return dots.orElse(null);
    }
}
