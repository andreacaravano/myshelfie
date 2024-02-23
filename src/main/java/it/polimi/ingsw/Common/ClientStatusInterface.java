package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * ClientStatus Interface
 */

public interface ClientStatusInterface extends Remote {
    /**
     * Getter method for Client Status
     *
     * @return Client's status
     * @throws RemoteException related to RMI
     */
    public Status getStatus() throws RemoteException;

    /**
     * Setter method for Client Status
     * It makes the client evolve
     *
     * @param status Client's status
     * @return client-side validation
     * @throws RemoteException related to RMI
     */
    public boolean setStatus(Status status) throws RemoteException;

    /**
     * Getter method for Game's identifier
     *
     * @return Game's identifier
     * @throws RemoteException related to RMI
     */
    public String getIdentifier() throws RemoteException;

    /**
     * Setter method for Game's identifier
     *
     * @param identifier Game's identifier
     * @throws RemoteException related to RMI
     */
    public void setIdentifier(String identifier) throws RemoteException;

    /**
     * Getter method for Player's nickname
     *
     * @return Player's nickname
     * @throws RemoteException related to RMI
     */
    public String getNickname() throws RemoteException;

    /**
     * Setter method for Player's nickname
     *
     * @param nickname Player's nickname
     * @throws RemoteException related to RMI
     */
    public void setNickname(String nickname) throws RemoteException;

    /**
     * Aggregate setter for Game initial parameters
     *
     * @param board Game's board
     * @param shelf Player's shelf
     * @param mi    Move Intermediate for the client
     * @throws RemoteException related to RMI
     */
    public void setGameParameters(BoardInterface board, ShelfInterface shelf, MoveIntermediateInterface mi) throws RemoteException;

    /**
     * Getter method for Game's board
     *
     * @return Game's board
     * @throws RemoteException related to RMI
     */
    public BoardInterface getBoard() throws RemoteException;

    /**
     * Getter method for Player's shelf
     *
     * @return Player's shelf
     * @throws RemoteException related to RMI
     */
    public ShelfInterface getShelf() throws RemoteException;

    /**
     * Getter method for Player's Personal Goal Card
     *
     * @return Player's Personal Goal Card
     * @throws RemoteException related to RMI
     */
    public PersonalGoalCardInterface getPersonalGoalCard() throws RemoteException;

    /**
     * Getter method for Game's Common Goal Cards
     *
     * @return List of Game's Common Goal Cards
     * @throws RemoteException related to RMI
     */
    public List<CommonGoalCardInterface> getCommonGoalCard() throws RemoteException;

    /**
     * Aggregate setter for Game's and Player's cards
     *
     * @param pgCard Player's Personal Goal Card
     * @param cgCard Game's Common Goal Card
     * @throws RemoteException related to RMI
     */
    public void setCards(PersonalGoalCardInterface pgCard, List<CommonGoalCardInterface> cgCard) throws RemoteException;

    /**
     * Getter for Player's Move Intermediate
     *
     * @return Player's Move Intermediate
     * @throws RemoteException related to RMI
     */
    public MoveIntermediateInterface getMoveIntermediate() throws RemoteException;

    /**
     * Getter method for the current player
     *
     * @return used for View purposes
     * @throws RemoteException related to RMI
     */
    public String getCurrentPlayer() throws RemoteException;

    /**
     * Setter method for the current player
     *
     * @param currentPlayer set by the Server for View purposes
     * @throws RemoteException related to RMI
     */
    public void setCurrentPlayer(String currentPlayer) throws RemoteException;

    /**
     * getter method for Game's Scoreboard
     *
     * @return used for View purposes
     * @throws RemoteException related to RMI
     */
    public ScoreBoardInterface getScoreBoard() throws RemoteException;

    /**
     * setter method for Game's Scoreboard
     *
     * @param sci Score Board Interface
     * @throws RemoteException related to RMI
     */
    public void setScoreBoard(ScoreBoardInterface sci) throws RemoteException;

    /**
     * @return Common Goal Cards achievement made in the last turn
     * @throws RemoteException related to RMI
     */
    public Map<ScoreCardInterface, CommonGoalCardInterface> getLastTurnScores() throws RemoteException;

    /**
     * @param lts Common Goal Cards achievement made in the last turn
     * @throws RemoteException related to RMI
     */
    public void setLastTurnScores(Map<ScoreCardInterface, CommonGoalCardInterface> lts) throws RemoteException;

    /**
     * @return Player's ScoreCards
     * @throws RemoteException related to RMI
     */
    public Stack<ScoreCardInterface> getScoreCards() throws RemoteException;

    /**
     * @param sc Player's ScoreCards
     * @throws RemoteException related to RMI
     */
    public void setScoreCards(Stack<ScoreCardInterface> sc) throws RemoteException;

    /**
     * Naive KeepAlive implementation, used for disconnections management
     *
     * @throws InterruptedException related to RMI Connection
     * @throws RemoteException      related to RMI
     */
    public void lifeline() throws InterruptedException, RemoteException;
}
