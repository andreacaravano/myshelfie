package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ShelfInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Player's Shelf
 */
public class Shelf extends UnicastRemoteObject implements ShelfInterface {
    /**
     * Maximum X dimension of the shelf
     */
    public static final int SHELF_DIM_X = 6;
    /**
     * Maximum Y dimension of the shelf
     */
    public static final int SHELF_DIM_Y = 5;
    /**
     * Shelf implementation of Object Cards
     */
    @Expose
    private Optional<ObjectCard>[][] cards;

    /**
     * Manages creation of the shelf (filling with empty values)
     *
     * @throws RemoteException related to RMI
     */
    public Shelf() throws RemoteException {
        cards = new Optional[SHELF_DIM_X][SHELF_DIM_Y];
        for (Optional<ObjectCard>[] r : cards)
            Arrays.fill(r, Optional.empty());
    }

    /**
     * Related to Game's refresh strategy
     */
    private Shelf(Optional<ObjectCard>[][] cards) throws RemoteException {
        this.cards = cards;
    }

    /**
     * Returns a list of effectively present cards on the board, based on a functional approach using Optionals.
     *
     * @return list of cards in the shelf
     */
    public List<ObjectCard> shelfCards() {
        return Stream.of(cards)
                .flatMap(Stream::of)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .collect(Collectors.toList());
    }

    /**
     * gets Object Card in position (x, y) if valid (else empty)
     *
     * @param x coordinate
     * @param y coordinate
     * @return requested ObjectCard
     * @throws Exception if coordinates are invalid
     */
    public Optional<ObjectCard> getCard(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            return cards[x][y];
        else throw new Exception();
    }

    /**
     * gets Object Card in position (x, y) if valid (else null)
     *
     * @param x coordinate
     * @param y coordinate
     * @return requested ObjectCard
     * @throws Exception if coordinates are invalid
     */
    public ObjectCard getPlainCard(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            return cards[x][y].orElse(null);
        else throw new Exception();
    }


    /**
     * Returns Object Card's type ordinal in position (x, y) if valid (else -1)
     *
     * @param x coordinate
     * @param y coordinate
     * @return requested Object Card's type ordinal
     * @throws Exception if coordinates are invalid
     */
    public int getCardOrdinal(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            if (cards[x][y].isPresent())
                return cards[x][y].get().getType().ordinal();
            else return -1;
        else throw new Exception();
    }

    /**
     * Returns Object Card's image path in position (x, y) if valid (else null)
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    public String getCardImage(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            if (cards[x][y].isPresent())
                return cards[x][y].get().getType().name().concat("-").concat(String.valueOf(cards[x][y].get().getImage()));
            else return null;
        else throw new Exception();
    }

    /**
     * Looks for coordinates of a given card (x, y)
     *
     * @param oc the Object Card to be found
     * @param X  kind of coordinate: X = true means x coordinate, false y
     * @return coordinate of the given card
     * @throws Exception related to invalid coordinates in getCard method
     */
    public int getCoordinate(ObjectCard oc, boolean X) throws Exception {
        if (oc == null || !shelfCards().contains(oc))
            return -1;
        for (int i = 0; i < SHELF_DIM_X; i++) {
            for (int j = 0; j < SHELF_DIM_Y; j++) {
                if (getCard(i, j).isPresent() && oc.equals(getCard(i, j).get())) {
                    if (X)
                        return i;
                    else return j;
                }
            }
        }
        return -1;
    }

    /**
     * Inserts a card into the shelf at the selected column (y)
     *
     * @param oc ObjectCard to insert
     * @param y  number of column to use
     * @throws Exception newly defined exception to manage error related to Shelf Insertion
     */
    public void placeCard(ObjectCard oc, int y) throws Exception {
        if (y < 0 || y >= SHELF_DIM_Y)
            throw new Exception("Inexistent column");
        if (cards[0][y].isPresent())
            throw new Exception("Selected column is full");
        for (int i = 0; i < SHELF_DIM_X; i++) {
            if (cards[SHELF_DIM_X - i - 1][y].isEmpty()) {
                cards[SHELF_DIM_X - i - 1][y] = Optional.of(oc);
                return;
            }
        }
        throw new Exception("Unable to insert new card in selected column");
    }

    /**
     * Evaluates score for a PersonalGoalCard
     *
     * @param pgCard Personal Goal Card
     * @return corresponding score
     */
    public int evaluatePattern(PersonalGoalCard pgCard) {
        int counter = 0;
        Optional<ObjectCardType>[][] pattern = pgCard.getPattern();
        for (int i = 0; i < SHELF_DIM_X; i++) {
            for (int j = 0; j < SHELF_DIM_Y; j++) {
                if (pattern[i][j].isPresent() && cards[i][j].isPresent() && pattern[i][j].get().equals(cards[i][j].get().getType())) {
                    counter++;
                }
            }
        }
        if (counter == 1 || counter == 2)
            return counter;
        if (counter == 3)
            return 4;
        if (counter == 4)
            return 6;
        if (counter == 5)
            return 9;
        if (counter == 6)
            return 12;
        return 0;
    }

    /**
     * Implements final evaluation of groups of Object Cards used at the end of the game
     *
     * @return score obtained for current shelf
     * @throws Exception related to invalid coordinates in getCard method
     */
    public int finalEvaluation() throws Exception {
        int ret = 0;
        for (ObjectCardType t : ObjectCardType.values()) {
            List<Integer> groupsTotal = new ArrayList<>();
            Set<ObjectCard> nextToCheck = new HashSet<>();
            Set<ObjectCard> checked = new HashSet<>();
            int localCounter = 0;

            for (int i = 0; i < SHELF_DIM_X; i++) {
                for (int j = 0; j < SHELF_DIM_Y; j++) {
                    if (this.getCard(i, j).isPresent() && (checked.contains(this.getCard(i, j).get()) || !this.getCard(i, j).get().getType().equals(t))) {
                        continue;
                    }
                    if (i >= 1) {
                        if (this.getCard(i - 1, j).isPresent() && this.getCard(i - 1, j).get().getType().equals(t) && !checked.contains(this.getCard(i - 1, j).get())) {
                            nextToCheck.add(this.getCard(i - 1, j).get());
                        }
                    }
                    if (i <= SHELF_DIM_X - 2) {
                        if (this.getCard(i + 1, j).isPresent() && this.getCard(i + 1, j).get().getType().equals(t) && !checked.contains(this.getCard(i + 1, j).get())) {
                            nextToCheck.add(this.getCard(i + 1, j).get());
                        }
                    }
                    if (j >= 1) {
                        if (this.getCard(i, j - 1).isPresent() && this.getCard(i, j - 1).get().getType().equals(t) && !checked.contains(this.getCard(i, j - 1).get())) {
                            nextToCheck.add(this.getCard(i, j - 1).get());
                        }
                    }
                    if (j <= SHELF_DIM_Y - 2) {
                        if (this.getCard(i, j + 1).isPresent() && this.getCard(i, j + 1).get().getType().equals(t) && !checked.contains(this.getCard(i, j + 1).get())) {
                            nextToCheck.add(this.getCard(i, j + 1).get());
                        }
                    }
                    if (this.getCard(i, j).isPresent()) {
                        checked.add(this.getCard(i, j).get());
                        localCounter++;
                    } else continue;
                    while (nextToCheck.size() >= 1) {
                        ArrayList<ObjectCard> support = new ArrayList<>(nextToCheck);
                        if (checked.contains(support.get(0))) {
                            nextToCheck.remove(support.get(0));
                            continue;
                        }
                        localCounter++;
                        int I = this.getCoordinate(support.get(0), true);
                        int J = this.getCoordinate(support.get(0), false);
                        if (I >= 1) {
                            if (this.getCard(I - 1, J).isPresent() && this.getCard(I - 1, J).get().getType().equals(t) && !checked.contains(this.getCard(I - 1, J).get())) {
                                nextToCheck.add(this.getCard(I - 1, J).get());
                            }
                        }
                        if (I <= SHELF_DIM_X - 2) {
                            if (this.getCard(I + 1, J).isPresent() && this.getCard(I + 1, J).get().getType().equals(t) && !checked.contains(this.getCard(I + 1, J).get())) {
                                nextToCheck.add(this.getCard(I + 1, J).get());
                            }
                        }
                        if (J >= 1) {
                            if (this.getCard(I, J - 1).isPresent() && this.getCard(I, J - 1).get().getType().equals(t) && !checked.contains(this.getCard(I, J - 1).get())) {
                                nextToCheck.add(this.getCard(I, J - 1).get());
                            }
                        }
                        if (J <= SHELF_DIM_Y - 2) {
                            if (this.getCard(I, J + 1).isPresent() && this.getCard(I, J + 1).get().getType().equals(t) && !checked.contains(this.getCard(I, J + 1).get())) {
                                nextToCheck.add(this.getCard(I, J + 1).get());
                            }
                        }
                        checked.add(support.get(0));
                        nextToCheck.remove(support.get(0));
                    }
                    groupsTotal.add(localCounter);
                    localCounter = 0;
                }
            }
            for (Integer total : groupsTotal) {
                if (total == 3)
                    ret += 2;
                if (total == 4)
                    ret += 3;
                if (total == 5)
                    ret += 5;
                if (total >= 6)
                    ret += 8;
            }
        }

        return ret;
    }

    /**
     * @return true if the Shelf is full
     */
    public boolean isFull() {
        return Arrays.stream(cards)
                .flatMap(Arrays::stream)
                .noneMatch(Optional::isEmpty);
    }

    /**
     * Calculates the number of empty spaces in the selected column
     *
     * @param column in the Shelf
     * @return number of empty spaces in the column
     * @throws Exception if given column is invalid
     */
    public int emptySpacesInColumn(int column) throws Exception {
        if (column < 0 || column >= SHELF_DIM_Y)
            throw new Exception();
        return ((Long) (Arrays.stream(cards)
                .map(r -> r[column])
                .filter(Optional::isEmpty)
                .count())).intValue();
    }

    /**
     * Refreshed copy, after server reload from file
     *
     * @return new copy of the object
     * @throws RemoteException related to RMI
     */
    public Shelf getCopy() throws RemoteException {
        return new Shelf(cards);
    }
}
