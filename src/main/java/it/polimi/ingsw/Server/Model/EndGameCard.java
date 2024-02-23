package it.polimi.ingsw.Server.Model;

import java.rmi.RemoteException;

/**
 * ScoreCard with value 1 for the first player who completely fills his/her shelf
 */
public class EndGameCard extends ScoreCard {
    /**
     * Class constructor
     */
    EndGameCard() throws RemoteException {
        super(1);
    }
}
