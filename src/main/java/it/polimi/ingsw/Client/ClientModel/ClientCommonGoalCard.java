package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.CommonGoalCardInterface;
import it.polimi.ingsw.Common.ScoreCardInterface;

import java.util.Stack;

/**
 * Common Goal Card (Client)
 */
public class ClientCommonGoalCard implements CommonGoalCardInterface {
    @Expose
    private final int type;
    @Expose
    private String description;
    @Expose
    private Stack<ClientScoreCard> increments;

    /**
     * CommonGoalCard's constructor.
     * New ScoreCards with a value of 2,4,6 or 8 points are pushed into the Stack increments
     * according to the playerCount, from the lowest to the highest score value.
     *
     * @param type        it indicates the id of the Common Goal Card, in order to identify the pattern
     * @param description String: textual description of the pattern
     */
    public ClientCommonGoalCard(int type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * Common Goal Card's type (ordinal)
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * @return String: textual description of the pattern
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Score Increments stack
     */
    public Stack<ScoreCardInterface> getIncrements() {
        Stack<ScoreCardInterface> ret = new Stack<>();
        ret.addAll(increments);
        return ret;
    }
}
