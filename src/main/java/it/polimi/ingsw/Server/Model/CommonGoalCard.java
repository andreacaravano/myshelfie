package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.CommonGoalCardInterface;
import it.polimi.ingsw.Common.ScoreCardInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Stack;

/**
 * This class represents the Common Goal cards, thanks to which players may grant points,
 * achieving a specific pattern
 */
public class CommonGoalCard extends UnicastRemoteObject implements CommonGoalCardInterface {
    /**
     * Limit of Common Goal Cards.
     */
    public static final int LIMIT = 12;
    /**
     * This attribute stands for the id of the Common Goal Card, which permits to
     * identify the different Common Goal Cards.
     */
    @Expose
    private final int type;

    /**
     * This attribute stands for a textual description of the pattern that the player has to achieve, in order
     * to gain points.
     */
    @Expose
    private String description;

    /**
     * This attribute stands for the deck of ScoreCards on each Common Goal Card, put there
     * from the lowest to the highest value. The size of the Stack varies according to the number of
     * players.
     */
    @Expose
    private Stack<ScoreCard> increments;

    /**
     * CommonGoalCard's constructor.
     * New ScoreCards with a value of 2,4,6 or 8 points are pushed into the Stack increments
     * according to the playerCount, from the lowest to the highest score value.
     *
     * @param type        it indicates the id of the Common Goal Card, in order to identify the pattern
     * @param playerCount (2nd parameter) it represents the number of players, according to which
     *                    a different number of ScoreCards are pushed into the Stack
     * @throws Exception if parameters are invalid
     */
    public CommonGoalCard(int type, int playerCount) throws Exception {
        this.type = type;
        if (type <= 0 || type > LIMIT)
            throw new Exception();

        increments = new Stack<>();

        if (playerCount < 2 || playerCount > 4)
            throw new Exception();

        if (playerCount == 4) {
            increments.push(new ScoreCard(2));
        }
        increments.push(new ScoreCard(4));

        if (playerCount >= 3) {
            increments.push(new ScoreCard(6));
        }
        increments.push(new ScoreCard(8));

        switch (type) {
            case 1 -> description = "Two groups each containing 4 cards of the same type in a 2x2 square. " +
                    "The cards of one square must be of the same type of those of the other square.";
            case 2 -> description = "Two columns each formed by 6 different types of cards.";
            case 3 -> description = "Four groups each containing 4 cards of the same type (in line or in column). " +
                    "The cards of a group can be different from those of another group.";
            case 4 -> description = "Six pairs of cards of the same type. " +
                    "The cards of a group can be different from those of another group.";
            case 5 -> description = "Three columns each formed by 6 cards of maximum three different types. " +
                    "One column can show a different combination of another column.";
            case 6 -> description = "Two lines each formed by 5 different types of cards. " +
                    "One line can show a different combination of the other line.";
            case 7 -> description = "Four lines each formed by five cards of maximum three different types. " +
                    "One line can show a different combination of the other line.";
            case 8 -> description = "Four cards of the same type in the four corners of the shelf.";
            case 9 -> description = "Eight cards of the same type. There's no restriction about the position of " +
                    "these cards.";
            case 10 -> description = "Five cards of the same type forming an X.";
            case 11 -> description = "Five cards of the same type forming a diagonal.";
            case 12 -> description = "Five columns of increasing or decreasing height. Starting from the first " +
                    "column on the left or on the right, each next column must be made of exactly one more " +
                    "card. Cards can be of any type.";
        }
    }

    /**
     * Related to Game's refresh strategy
     *
     * @throws RemoteException related to RMI
     */
    private CommonGoalCard(int type, String description, Stack<ScoreCard> increments) throws RemoteException {
        this.type = type;
        this.description = description;
        this.increments = new Stack<>();
        for (ScoreCard sc : increments) {
            this.increments.add(new ScoreCard(sc.getValue()));
        }
    }

    /**
     * This method returns the textual description of the pattern that the player has to achieve, in order
     * to gain points.
     *
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


    /**
     * This method calculates how many points the player has scored, by achieving the pattern which
     * is displayed in the Common Goal Card.
     *
     * @param shelf player's shelf
     * @return score granted by the player, if the pattern displayed by the card has been achieved
     * @throws Exception if coordinates are invalid
     */
    public ScoreCard attribute(Shelf shelf) throws Exception {
        boolean found = false;
        switch (type) {
            case 1 -> {
                boolean[][] alrInAPattern = new boolean[Shelf.SHELF_DIM_X][Shelf.SHELF_DIM_Y];
                //Pattern: two groups each containing 4 ObjectCards of the same type in a 2x2 square
                for (ObjectCardType t : ObjectCardType.values()) {
                    int count = 0;
                    for (int i = 0; i < Shelf.SHELF_DIM_X - 1; i++) {
                        for (int j = 0; j < Shelf.SHELF_DIM_Y - 1; j++) {
                            if (shelf.getCard(i, j).isPresent() &&
                                    shelf.getCard(i, j).get().getType().equals(t) && !alrInAPattern[i][j]) {
                                if (shelf.getCard(i + 1, j).isPresent() &&
                                        shelf.getCard(i + 1, j).get().getType().equals(t)
                                        && !alrInAPattern[i + 1][j]) {
                                    //check adjacency in the following line
                                    if (shelf.getCard(i, j + 1).isPresent() &&
                                            shelf.getCard(i, j + 1).get().getType().equals(t)
                                            && !alrInAPattern[i][j + 1]) {
                                        if (shelf.getCard(i + 1, j + 1).isPresent() &&
                                                shelf.getCard(i + 1, j + 1).get().getType().equals(t)
                                                && !alrInAPattern[i + 1][j + 1]) {
                                            alrInAPattern[i][j] = true;
                                            alrInAPattern[i][j + 1] = true;
                                            alrInAPattern[i + 1][j] = true;
                                            alrInAPattern[i + 1][j + 1] = true;
                                            count++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (count >= 2) {
                        found = true;
                    }
                }
            }
            case 2 -> {
                //two columns each formed by 6 different types of ObjectCards
                found = findTypesInColumns(6, 6, 2, shelf);
            }
            case 3 -> {
                if (sameTypeGroupFirstCheck(4, 4, shelf)) {
                    found = true;
                } else {
                    found = sameTypeGroupSecondCheck(4, 4, shelf);
                }

            }
            case 4 -> {
                //six pairs of cards of the same type (pairs may have different types)
                if (sameTypeGroupFirstCheck(2, 6, shelf)) {
                    found = true;
                } else {
                    found = sameTypeGroupSecondCheck(2, 6, shelf);
                }
            }
            case 5 -> {
                //3 columns of 6 cards (max 3 different ObjectCards)
                found = findTypesInColumns(3, 1, 3, shelf);
            }
            case 6 -> { //two rows with five cards of five different types
                found = findTypesInRows(5, 5, 2, shelf);
            }
            case 7 -> { //four rows with five cards of (from one to three) different types
                found = findTypesInRows(3, 1, 4, shelf);
            }
            case 8 -> {
                //four cards of the same type in the four corners of the shelf
                if (shelf.getCard(0, 0).isPresent() &&
                        shelf.getCard(0, Shelf.SHELF_DIM_Y - 1).isPresent() &&
                        shelf.getCard(Shelf.SHELF_DIM_X - 1, Shelf.SHELF_DIM_Y - 1).isPresent() &&
                        shelf.getCard(Shelf.SHELF_DIM_X - 1, 0).isPresent()) {
                    if (shelf.getCard(0, 0).get().getType().equals(shelf.getCard(0, Shelf.SHELF_DIM_Y - 1).get().getType())
                            && shelf.getCard(0, 0).get().getType().equals(shelf.getCard(Shelf.SHELF_DIM_X - 1, 0).get().getType()) &&
                            shelf.getCard(0, 0).get().getType().equals(shelf.getCard(Shelf.SHELF_DIM_X - 1, Shelf.SHELF_DIM_Y - 1).get().getType())) {
                        found = true;
                    }
                }
            }
            case 9 -> {
                //eight cards of the same type
                int[] typeCounter = new int[ObjectCardType.values().length];
                Arrays.fill(typeCounter, 0);
                for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
                    for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                        if (shelf.getCard(i, j).isPresent()) {
                            typeCounter[shelf.getCard(i, j).get().getType().ordinal()]++;
                        }
                    }
                }
                for (int i = 0; i < ObjectCardType.values().length; i++) {
                    if (typeCounter[i] >= 8) {
                        found = true;
                        break;
                    }
                }
            }
            case 10 -> {
                for (int i = 1; i < Shelf.SHELF_DIM_X - 1; i++) {
                    for (int j = 1; j < Shelf.SHELF_DIM_Y - 1; j++) {
                        if (!found) {
                            //cards are present
                            if (shelf.getCard(i, j).isPresent() && shelf.getCard(i - 1, j + 1).isPresent()
                                    && shelf.getCard(i - 1, j - 1).isPresent() && shelf.getCard(i + 1, j - 1).isPresent() &&
                                    shelf.getCard(i + 1, j + 1).isPresent() && shelf.getCard(i, j - 1).isPresent() &&
                                    shelf.getCard(i + 1, j).isPresent() && shelf.getCard(i, j + 1).isPresent()) {
                                //cards of the same type create an X
                                if (shelf.getCard(i, j).get().getType().equals(shelf.getCard(i - 1, j + 1).get().getType())
                                        && shelf.getCard(i, j).get().getType().equals(shelf.getCard(i - 1, j - 1).get().getType()) &&
                                        shelf.getCard(i, j).get().getType().equals(shelf.getCard(i + 1, j - 1).get().getType()) &&
                                        shelf.getCard(i, j).get().getType().equals(shelf.getCard(i + 1, j + 1).get().getType()
                                        )) {
                                    //the other 4 cards are of a different type (they don't create a square)
                                    /*if (!shelf.getCard(i, j).get().getType().equals(shelf.getCard(i, j - 1).get().getType()) &&
                                            !shelf.getCard(i, j).get().getType().equals(shelf.getCard(i, j + 1).get().getType())
                                            && !shelf.getCard(i, j).get().getType().equals(shelf.getCard(i + 1, j).get().getType()) &&
                                            (shelf.getCard(i - 1, j).isEmpty() ||
                                                    !shelf.getCard(i, j).get().getType().equals(shelf.getCard(i - 1, j).get().getType()))) {*/
                                    found = true;
                                    // }
                                }
                            }
                        }
                    }
                }
            }
            case 11 -> {
                boolean skipDiag1 = false;
                boolean skipDiag2 = false;
                boolean skipDiag3 = false;
                boolean skipDiag4 = false;
                for (int i = 0; i < Shelf.SHELF_DIM_X - 2; i++) {
                    for (int j = 0; j < Shelf.SHELF_DIM_Y - 1; j++) {
                        if (i == j) {
                            if (!(!skipDiag1 && shelf.getCard(i, j).isPresent() && shelf.getCard(i + 1, j + 1).isPresent() &&
                                    shelf.getCard(i, j).get().getType().equals(shelf.getCard(i + 1, j + 1).get().getType()))) {
                                skipDiag1 = true;
                            }
                            if (!(!skipDiag2 && shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).isPresent() &&
                                    shelf.getCard(i + 1, Shelf.SHELF_DIM_Y - j - 2).isPresent() &&
                                    shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).get().getType().equals(shelf.getCard(i + 1, Shelf.SHELF_DIM_Y - j - 2).get().getType()))
                            ) {
                                skipDiag2 = true;
                            }
                            if (!(!skipDiag3 && shelf.getCard(i + 1, j).isPresent() &&
                                    shelf.getCard(i + 2, j + 1).isPresent() &&
                                    shelf.getCard(i + 1, j).get().getType().equals(shelf.getCard(i + 2, j + 1).get().getType()))) {
                                skipDiag3 = true;
                            }
                            if (!(!skipDiag4 && shelf.getCard(Shelf.SHELF_DIM_X - i - 1, j).isPresent() &&
                                    shelf.getCard(Shelf.SHELF_DIM_X - i - 2, j + 1).isPresent() &&
                                    shelf.getCard(Shelf.SHELF_DIM_X - i - 1, j).get().getType().equals(shelf.getCard(Shelf.SHELF_DIM_X - i - 2, j + 1).get().getType()))) {
                                skipDiag4 = true;
                            }
                        }
                    }
                }
                if (!skipDiag1 || !skipDiag2 || !skipDiag3 || !skipDiag4) {
                    found = true;
                }
            }
            case 12 -> {
                boolean skipDiag1 = false;
                boolean skipDiag2 = false;
                boolean skipDiag3 = false;
                boolean skipDiag4 = false;
                for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
                    for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                        if (!(!skipDiag1 && ((i >= j && shelf.getCard(i, j).isPresent()) || (j > i && shelf.getCard(i, j).isEmpty())))) {
                            skipDiag1 = true;
                        }
                        if (!(!skipDiag2 && ((i > j && shelf.getCard(i, j).isPresent()) || (j >= i && shelf.getCard(i, j).isEmpty())))) {
                            skipDiag2 = true;
                        }
                        if (!(!skipDiag3 && ((i >= j && shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).isPresent()) ||
                                (j > i && shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).isEmpty())))) {
                            skipDiag3 = true;
                        }
                        if (!(!skipDiag4 && ((i > j && shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).isPresent()) ||
                                (j >= i && shelf.getCard(i, Shelf.SHELF_DIM_Y - j - 1).isEmpty())))) {
                            skipDiag4 = true;
                        }
                    }
                }
                if (!skipDiag1 || !skipDiag2 || !skipDiag3 || !skipDiag4) {
                    found = true;
                }
            }
        }

        if (found && !increments.isEmpty()) {
            return increments.pop();
        }

        return new ScoreCard(0);
    }


    /**
     * This method is a support method to attribute() for cards 2 and 5.
     *
     * @param diffTypesMax max number of different types in a column
     * @param diffTypesMin min number of different types in a column
     * @param numOfPattern num of pattern
     * @param shelf        player's shelf
     * @return true if the pattern has been found, false otherwise
     * @throws Exception invalid coordinates
     */
    private boolean findTypesInColumns(int diffTypesMax, int diffTypesMin, int numOfPattern, Shelf shelf) throws Exception {
        int patternCount = 0;
        ObjectCardType[] checkTypes = new ObjectCardType[Shelf.SHELF_DIM_X];
        Arrays.fill(checkTypes, null);
        for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
            if (shelf.getCard(0, j).isPresent()) { //checks the presence of 6 elements in the column
                for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
                    if (shelf.getCard(i, j).isPresent()) {
                        checkTypes[i] = shelf.getCard(i, j).get().getType();
                    }
                }
                if ((Arrays.stream(checkTypes).distinct().count() <= diffTypesMax && Arrays.stream(checkTypes).distinct().count() >= diffTypesMin)) {
                    patternCount++;
                }
                Arrays.fill(checkTypes, null);
            }
        }
        return patternCount >= numOfPattern;
    }

    /**
     * This method is a support method to attribute() for cards 6 and 7.
     *
     * @param diffTypesMax max number of different types in a line
     * @param diffTypesMin min number of different types in a column
     * @param numOfPattern num of pattern
     * @param shelf        player's shelf
     * @return true if the pattern has been found, false otherwise
     * @throws Exception invalid coordinates
     */
    private boolean findTypesInRows(int diffTypesMax, int diffTypesMin, int numOfPattern, Shelf shelf) throws Exception {
        int patternCount = 0;
        ObjectCardType[] checkTypes = new ObjectCardType[Shelf.SHELF_DIM_Y];
        Arrays.fill(checkTypes, null);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            int cardCounter = 0;
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                if (shelf.getCard(i, j).isPresent()) {
                    checkTypes[j] = shelf.getCard(i, j).get().getType();
                    cardCounter++;
                }
            }
            if (cardCounter == Shelf.SHELF_DIM_Y && Arrays.stream(checkTypes).distinct().count() >= diffTypesMin &&
                    Arrays.stream(checkTypes).distinct().count() <= diffTypesMax) {

                patternCount++;
            }
            Arrays.fill(checkTypes, null);
        }
        return patternCount >= numOfPattern;
    }

    /**
     * This method is a support method to attribute() for cards 3 and 4. It checks the
     * presence of groups in lines and then in columns.
     *
     * @param dimOfGroup dimension of each group
     * @param numPattern number of pattern
     * @param shelf      player's shelf
     * @return true if the pattern has been found, false otherwise
     * @throws Exception invalid coordinates
     */
    private boolean sameTypeGroupFirstCheck(int dimOfGroup, int numPattern, Shelf shelf) throws Exception {
        boolean[][] alrInAPattern = new boolean[Shelf.SHELF_DIM_X][Shelf.SHELF_DIM_Y];
        ObjectCardType firstType;
        int foundPattern = 0; //counts how many patterns have been found
        for (int i = 0; i <= Shelf.SHELF_DIM_X - dimOfGroup; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                int sameType = 0; //counts how many adjacent cards have the same type
                if (shelf.getCard(i, j).isPresent() && !alrInAPattern[i][j]) { //checks adjacency in a column
                    firstType = shelf.getCard(i, j).get().getType();
                    for (int k = 1; k < dimOfGroup; k++) {
                        if (shelf.getCard(i + k, j).isPresent() && shelf.getCard(i + k, j).get().getType().equals(firstType)
                                && !alrInAPattern[i + k][j]) {
                            sameType++;
                        }
                    }
                    if (sameType == dimOfGroup - 1) {
                        foundPattern++;
                        for (int k = 0; k < dimOfGroup; k++) {
                            alrInAPattern[i + k][j] = true;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j <= Shelf.SHELF_DIM_Y - dimOfGroup; j++) {
                int sameType = 0; //counts how many adjacent cards have the same type
                if (shelf.getCard(i, j).isPresent() && !alrInAPattern[i][j]) {
                    firstType = shelf.getCard(i, j).get().getType();
                    for (int k = 1; k < dimOfGroup; k++) {
                        if (shelf.getCard(i, j + k).isPresent() && shelf.getCard(i, j + k).get().getType().equals(firstType)
                                && !alrInAPattern[i][j + k]) {
                            sameType++;
                        }
                    }
                    if (sameType == dimOfGroup - 1) {
                        foundPattern++;
                        for (int k = 0; k < dimOfGroup; k++) {
                            alrInAPattern[i][j + k] = true;
                        }
                    }
                }
            }
        }
        return foundPattern >= numPattern;
    }

    /**
     * This method is a support method to attribute() for cards 3 and 4. It checks the
     * presence of groups in columns and then in lines.
     *
     * @param dimOfGroup dimension of each group
     * @param numPattern number of patterns
     * @param shelf      player's shelf
     * @return true if the pattern has been found, false otherwise
     * @throws Exception invalid coordinates
     */
    private boolean sameTypeGroupSecondCheck(int dimOfGroup, int numPattern, Shelf shelf) throws Exception {
        boolean[][] alrInAPattern = new boolean[Shelf.SHELF_DIM_X][Shelf.SHELF_DIM_Y];
        ObjectCardType firstType;
        int foundPattern = 0; //counts how many patterns have been found
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j <= Shelf.SHELF_DIM_Y - dimOfGroup; j++) {
                int sameType = 0; //counts how many adjacent cards have the same type
                if (shelf.getCard(i, j).isPresent() && !alrInAPattern[i][j]) {
                    firstType = shelf.getCard(i, j).get().getType();
                    for (int k = 1; k < dimOfGroup; k++) {
                        if (shelf.getCard(i, j + k).isPresent() && shelf.getCard(i, j + k).get().getType().equals(firstType)
                                && !alrInAPattern[i][j + k]) {
                            sameType++;
                        }
                    }
                    if (sameType == dimOfGroup - 1) {
                        foundPattern++;
                        for (int k = 0; k < dimOfGroup; k++) {
                            alrInAPattern[i][j + k] = true;
                        }
                    }
                }
            }
        }
        for (int i = 0; i <= Shelf.SHELF_DIM_X - dimOfGroup; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                int sameType = 0; //counts how many adjacent cards have the same type
                if (shelf.getCard(i, j).isPresent() && !alrInAPattern[i][j]) { //checks adjacency in a column
                    firstType = shelf.getCard(i, j).get().getType();
                    for (int k = 1; k < dimOfGroup; k++) {
                        if (shelf.getCard(i + k, j).isPresent() && shelf.getCard(i + k, j).get().getType().equals(firstType)
                                && !alrInAPattern[i + k][j]) {
                            sameType++;
                        }
                    }
                    if (sameType == dimOfGroup - 1) {
                        foundPattern++;
                        for (int k = 0; k < dimOfGroup; k++) {
                            alrInAPattern[i + k][j] = true;
                        }
                    }
                }
            }
        }
        return foundPattern >= numPattern;
    }

    /**
     * Refreshed copy, after server reload from file
     *
     * @return new copy of the object
     * @throws RemoteException related to RMI
     */
    public CommonGoalCard getCopy() throws RemoteException {
        return new CommonGoalCard(this.type, this.description, this.increments);
    }

    /**
     * Common Goal Card's type (ordinal)
     *
     * @return type
     * @throws RemoteException related to RMI
     */
    public int getType() throws RemoteException {
        return type;
    }
}