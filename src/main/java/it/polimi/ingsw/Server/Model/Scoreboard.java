package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ScoreBoardInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Final Game's Scoreboard
 */
public class Scoreboard extends UnicastRemoteObject implements ScoreBoardInterface {
    /**
     * Scoreboard, made of Nickname (String) and score (int)
     */
    @Expose
    private Map<String, Integer> scoreBoard;

    /**
     * Scoreboard constructor. It sorts the Scoreboard unsorted Map directly.
     *
     * @param scoreBoard unsorted final scoreboard
     * @throws RemoteException related to RMI
     */
    public Scoreboard(Map<String, Integer> scoreBoard) throws RemoteException {
        this.scoreBoard = scoreBoard;
        Map<String, Integer> tempSB = new LinkedHashMap<>();
        scoreBoard.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> tempSB.put(e.getKey(), e.getValue()));
        scoreBoard.clear();
        this.scoreBoard = tempSB;
    }

    /**
     * Getter for the final Scoreboard sorted Map
     *
     * @return final Scoreboard
     */
    public Map<String, Integer> getScoreBoard() {
        return scoreBoard;
    }
}
