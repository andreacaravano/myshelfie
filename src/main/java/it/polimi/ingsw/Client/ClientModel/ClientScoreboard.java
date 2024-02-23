package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ScoreBoardInterface;

import java.util.Map;

/**
 * Scoreboard (Client)
 */
public class ClientScoreboard implements ScoreBoardInterface {
    /**
     * Scoreboard, made of Nickname (String) and score (int)
     */
    @Expose
    private Map<String, Integer> scoreBoard;

    /**
     * Getter for the final Scoreboard sorted Map
     *
     * @return final Scoreboard
     */
    public Map<String, Integer> getScoreBoard() {
        return scoreBoard;
    }
}