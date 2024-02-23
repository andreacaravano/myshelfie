package it.polimi.ingsw.Server.Model;

import java.rmi.RemoteException;
import java.util.*;

/**
 * This class represent all player's info and finalize shelf insertion and score update operation
 */

public class Player {
    /**
     * Player's nickname
     */
    private final String nickname;
    /**
     * List of ScoreCards achieved by the player
     */
    private final Stack<ScoreCard> scoreCards;
    private final Map<ScoreCard, CommonGoalCard> lastAchieved;
    /**
     * Player's position relative to the first player clockwise
     */
    private int position;
    /**
     * Player's score
     */
    private int score;
    /**
     * First common goal card reference
     */
    private CommonGoalCard cgCard1;
    /**
     * Second common goal card reference
     */
    private CommonGoalCard cgCard2;
    /**
     * Player's shelf reference
     */
    private Shelf shelf;
    /**
     * Player's personal goal card
     */
    private PersonalGoalCard pgCard;
    /**
     * Boolean attribute used to check player's presence in the ongoing game
     */
    private boolean inGame;
    /**
     * Boolean attributes that indicate if one of the two common goals is achieved
     */
    private boolean achievedCG1, achievedCG2;

    /**
     * Player's class constructor
     *
     * @param nickname (1st parameter)
     * @param pgCard   (2nd parameter)
     * @param cgCard1  (3rd parameter)
     * @param cgCard2  (4th parameter)
     * @param position (5th parameter)
     * @param board    (6th parameter)
     */
    Player(String nickname, PersonalGoalCard pgCard, CommonGoalCard cgCard1, CommonGoalCard cgCard2, int position, Board board) throws RemoteException {
        this.nickname = nickname;
        this.score = 0;
        this.inGame = true;
        this.position = position;
        this.cgCard1 = cgCard1;
        this.cgCard2 = cgCard2;
        this.shelf = new Shelf();
        this.pgCard = pgCard;
        this.achievedCG1 = false;
        this.achievedCG2 = false;
        this.scoreCards = new Stack<>();
        this.lastAchieved = new HashMap<>();
    }

    /**
     * Player's class constructor
     *
     * @param nickname (1st parameter)
     * @throws RemoteException related to RMI
     */
    public Player(String nickname) throws RemoteException {
        this.nickname = nickname;
        this.score = 0;
        this.inGame = true;
        this.shelf = new Shelf();
        this.achievedCG1 = false;
        this.achievedCG2 = false;
        this.scoreCards = new Stack<>();
        this.lastAchieved = new HashMap<>();
    }

    /**
     * Setter for initial Game parameters
     *
     * @param pgCard  Personal Goal Card for the player
     * @param cgCard1 First Common Goal Card for the game
     * @param cgCard2 Second Common Goal Card for the game
     * @param board   Board for the game
     */
    public void setGameParameters(PersonalGoalCard pgCard, CommonGoalCard cgCard1, CommonGoalCard cgCard2, Board board) {
        this.pgCard = pgCard;
        this.cgCard1 = cgCard1;
        this.cgCard2 = cgCard2;
    }

    /**
     * @return Player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This is a getter method for the position attribute
     *
     * @return int player's position
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * @param position Player's clockwise position in the game
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * This is a getter method for the score attribute
     *
     * @return int player's score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * This is a getter method for the shelf attribute
     *
     * @return Player's shelf
     */
    public Shelf getShelf() {
        return this.shelf;
    }

    /**
     * This is a getter method for the PersonalGoalCard
     *
     * @return PersonalGoalCard
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return pgCard;
    }

    /**
     * This method updates player's score checking the possible point obtained from the common and personal goal cards
     *
     * @throws Exception related to Model management
     */
    public void updateScore() throws Exception {
        ScoreCard s;
        if (!achievedCG1) {
            int memScore = this.score;
            s = this.cgCard1.attribute(this.shelf);
            this.score += s.getValue();
            if (s.getValue() != 0) {
                scoreCards.push(s);
                scoreCards.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
                lastAchieved.put(s, cgCard1);
            }
            if (memScore < this.score) this.achievedCG1 = true;
        }
        if (!achievedCG2) {
            int memScore = score;
            s = this.cgCard2.attribute(this.shelf);
            this.score += s.getValue();
            if (s.getValue() != 0) {
                scoreCards.push(s);
                scoreCards.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
                lastAchieved.put(s, cgCard2);
            }
            if (memScore < this.score) this.achievedCG2 = true;
        }
        if (!isInGame()) {
            this.score += shelf.evaluatePattern(this.pgCard);
            this.score += shelf.finalEvaluation();
        }
    }

    /**
     * This method returns player's game status
     *
     * @return boolean true if the player is still in game, false otherwise.
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * This method set the inGame parameter at false
     */
    public void setOutOfTurn() {
        this.inGame = false;
    }

    /**
     * This method gives to the first player that has fulfilled the shelf an EndGameCard
     *
     * @throws RemoteException related to RMI
     */
    public void firstShelfFull() throws RemoteException {
        EndGameCard s = new EndGameCard();
        scoreCards.push(s);
        scoreCards.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
    }

    /**
     * Refreshment strategy, related to persistency management strategy
     *
     * @param cgCard1 new Common Goal Card 1 object
     * @param cgCard2 new Common Goal Card 2 object
     * @throws RemoteException related to RMI
     */
    public void refreshEntities(CommonGoalCard cgCard1, CommonGoalCard cgCard2) throws RemoteException {
        this.shelf = shelf.getCopy();
        this.pgCard = pgCard.getCopy();
        List<ScoreCard> iterCopy = new ArrayList<>(scoreCards);
        for (ScoreCard sc : iterCopy) {
            scoreCards.add(sc.getCopy());
            scoreCards.remove(sc);
        }
        scoreCards.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
        this.cgCard1 = cgCard1;
        this.cgCard2 = cgCard2;
    }

    /**
     * @return lastly achieved Common Goal Cards objectives
     */
    public Map<ScoreCard, CommonGoalCard> getLastAchieved() {
        Map<ScoreCard, CommonGoalCard> temp = new HashMap<>(lastAchieved);
        if (lastAchieved != null) {
            lastAchieved.clear();
            return temp;
        }
        return null;
    }

    /**
     * @return Player's ScoreCards
     */
    public Stack<ScoreCard> getScoreCards() {
        return scoreCards;
    }
}
