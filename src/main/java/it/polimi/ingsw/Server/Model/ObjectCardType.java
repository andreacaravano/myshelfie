package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Common.ObjectCardInterface;

/**
 * This enumeration represents types of ObjectCards
 */

public enum ObjectCardType implements ObjectCardInterface {
    CATS(0),
    BOOKS(1),
    GAMES(2),
    FRAMES(3),
    TROPHIES(4),
    PLANTS(5);
    /**
     * This attribute stands for the progressive number of enum values
     */
    private final int ord;

    /**
     * Class constructor
     *
     * @param ord progressive number
     */
    ObjectCardType(int ord) {
        this.ord = ord;
    }

    /**
     * getter of ord
     *
     * @return attribute ord
     */
    public int getOrd() {
        return ord;
    }
}
