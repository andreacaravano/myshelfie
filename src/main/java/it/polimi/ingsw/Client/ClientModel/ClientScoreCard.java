package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ScoreCardInterface;

/**
 * Score Card (Client)
 */
public class ClientScoreCard implements ScoreCardInterface {
    /**
     * Score Card's value
     */
    @Expose
    int value;

    /**
     * @return value of the ScoreCard
     */
    @Override
    public int getValue() {
        return value;
    }
}
