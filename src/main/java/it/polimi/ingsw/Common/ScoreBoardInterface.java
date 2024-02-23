package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * ScoreBoard Interface
 */
public interface ScoreBoardInterface extends Remote {
    /**
     * Getter for the final Scoreboard sorted Map
     *
     * @return final Scoreboard
     * @throws RemoteException related to RMI
     */
    public Map<String, Integer> getScoreBoard() throws RemoteException;
}
