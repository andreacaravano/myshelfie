package it.polimi.ingsw.Client.ClientModel;

import it.polimi.ingsw.Common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Status (Client)
 */
public class ClientStatus extends UnicastRemoteObject implements ClientStatusInterface {
    /**
     * Client's implementation
     */
    private final RMIClientInterface client;
    /**
     * Client status related to the game
     */
    private Status status = Status.InvalidStatus;
    /**
     * Game's identifier
     */
    private String identifier;
    /**
     * Player's nickname
     */
    private String nickname;
    /**
     * Game's board
     */
    private BoardInterface board;
    /**
     * Player's shelf
     */
    private ShelfInterface shelf;
    /**
     * Personal Goal Card for the Player
     */
    private PersonalGoalCardInterface pgCard;
    /**
     * Game's Common Goal Card (1-2)
     */
    private List<CommonGoalCardInterface> cgCard;
    /**
     * Move Intermediate for the client
     */
    private MoveIntermediateInterface mi;
    /**
     * Current player in the game, used for View purposes
     */
    private String currentPlayer;
    /**
     * Game's final Scoreboard
     */
    private ScoreBoardInterface sci;
    /**
     * Game's last turn scores
     */
    private Map<ScoreCardInterface, CommonGoalCardInterface> lts = null;
    private Stack<ScoreCardInterface> sc = null;

    /**
     * Client Status constructor
     *
     * @param client RMI Client implementation
     * @throws RemoteException related to RMI
     */
    public ClientStatus(RMIClientInterface client) throws RemoteException {
        super();
        this.client = client;
    }

    /**
     * Getter method for Client Status
     *
     * @return Client's status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter method for Client Status
     * It makes the client evolve
     *
     * @param status Client's status
     * @return client-side validation
     */
    public boolean setStatus(Status status) {
        this.status = status;
        return client.evolve();
    }

    /**
     * Getter method for Game's identifier
     *
     * @return Game's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Setter method for Game's identifier
     *
     * @param identifier Game's identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Getter method for Player's nickname
     *
     * @return Player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter method for Player's nickname
     *
     * @param nickname Player's nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Aggregate setter for Game initial parameters
     *
     * @param board Game's board
     * @param shelf Player's shelf
     * @param mi    Move Intermediate for the client
     */
    public void setGameParameters(BoardInterface board, ShelfInterface shelf, MoveIntermediateInterface mi) {
        this.board = board;
        this.shelf = shelf;
        this.mi = mi;
    }

    /**
     * Getter method for Game's board
     *
     * @return Game's board
     */
    public BoardInterface getBoard() {
        return board;
    }

    /**
     * Getter method for Player's shelf
     *
     * @return Player's shelf
     */
    public ShelfInterface getShelf() {
        return shelf;
    }

    /**
     * Getter method for Player's Personal Goal Card
     *
     * @return Player's Personal Goal Card
     */
    public PersonalGoalCardInterface getPersonalGoalCard() {
        return pgCard;
    }

    /**
     * Getter method for Game's Common Goal Cards
     *
     * @return List of Game's Common Goal Cards
     */
    public List<CommonGoalCardInterface> getCommonGoalCard() {
        return cgCard;
    }

    /**
     * Aggregate setter for Game's and Player's cards
     *
     * @param pgCard Player's Personal Goal Card
     * @param cgCard Game's Common Goal Card
     */
    public void setCards(PersonalGoalCardInterface pgCard, List<CommonGoalCardInterface> cgCard) {
        this.pgCard = pgCard;
        this.cgCard = cgCard;
    }

    /**
     * Getter for Player's Move Intermediate
     *
     * @return Player's Move Intermediate
     */
    public MoveIntermediateInterface getMoveIntermediate() {
        return mi;
    }

    /**
     * Getter method for the current player
     *
     * @return used for View purposes
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Setter method for the current player
     *
     * @param currentPlayer set by the Server for View purposes
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * getter method for Game's Scoreboard
     *
     * @return used for View purposes
     */
    public ScoreBoardInterface getScoreBoard() {
        return sci;
    }

    /**
     * setter method for Game's Scoreboard
     */
    public void setScoreBoard(ScoreBoardInterface sci) {
        this.sci = sci;
    }

    /**
     * @return Common Goal Cards achievement made in the last turn
     */
    public Map<ScoreCardInterface, CommonGoalCardInterface> getLastTurnScores() {
        return lts;
    }

    /**
     * @param lts Common Goal Cards achievement made in the last turn
     */
    public void setLastTurnScores(Map<ScoreCardInterface, CommonGoalCardInterface> lts) {
        this.lts = lts;
    }

    /**
     * @return Player's ScoreCards
     */
    public Stack<ScoreCardInterface> getScoreCards() {
        return sc;
    }

    /**
     * @param sc Player's ScoreCards
     */
    public void setScoreCards(Stack<ScoreCardInterface> sc) {
        this.sc = sc;
    }

    /**
     * Naive KeepAlive implementation, used for disconnections management
     *
     * @throws InterruptedException related to RMI Connection
     */
    public void lifeline() throws InterruptedException {
        return;
    }
}
