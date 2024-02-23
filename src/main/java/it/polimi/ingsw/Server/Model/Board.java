package it.polimi.ingsw.Server.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.BoardInterface;
import it.polimi.ingsw.Common.GsonOptionalSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Game's Board
 */
public class Board extends UnicastRemoteObject implements BoardInterface {
    /**
     * Maximum X dimension of the board
     */
    public static final int BOARD_DIM_X = 9;
    /**
     * Maximum Y dimension of the board
     */
    public static final int BOARD_DIM_Y = 9;
    /**
     * Game's minimum number of players
     */
    private final int MINPLAYERS = 2;
    /**
     * Game's maximum number of players
     */
    private final int MAXPLAYERS = 4;
    /**
     * Board Spaces implementation
     */
    @Expose
    private BoardSpace[][] spaces;
    /**
     * Object Cards extracted in the related game
     */
    @Expose(serialize = false, deserialize = true)
    private List<ObjectCard> objectCards = null;
    /**
     * List of the most recent Object Cards taken
     */
    @Expose(serialize = false, deserialize = true)
    private Map<ObjectCard, BoardSpace> lastTaken = null;

    /**
     * Manages board creation and initialization, based on pre-built files indicating usable spaces and restrictions related to the number of players.
     *
     * @param playerCount number of players in the game
     * @param objectCards List of extracted Object Cards
     * @throws FileNotFoundException related to file management. In the default environment, necessary files are found in the "/res/model/" folder of the project.
     */
    public Board(int playerCount, List<ObjectCard> objectCards) throws Exception {
        if (playerCount < MINPLAYERS || playerCount > MAXPLAYERS)
            throw new Exception();

        spacesDeclaration(playerCount);

        // Initialize utilization tracking for Object Cards
        this.objectCards = Objects.requireNonNullElseGet(objectCards, ArrayList::new);

        lastTaken = new HashMap<>();

        // default reset
        reset();
    }

    /**
     * Related to Game's refresh strategy
     */
    private Board(BoardSpace[][] spaces, List<ObjectCard> objectCards, Map<ObjectCard, BoardSpace> lastTaken) throws RemoteException {
        this.spaces = spaces;
        this.objectCards = objectCards;
        this.lastTaken = lastTaken;
    }

    /**
     * Takes care of creating the Board Spaces using JSON declaration files
     *
     * @param playerCount number of players in the game
     * @throws URISyntaxException related to resources locators
     * @throws IOException        related to file management
     */
    private void spacesDeclaration(int playerCount) throws URISyntaxException, IOException {
        // JSON Parser
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();

        // resource locator
        URI uri = ClassLoader.getSystemResource("model/board/"
                .concat(String.valueOf(playerCount))
                .concat(".json")).toURI();

        // FileSystem support structure, related to JAR file structure
        FileSystem fs = null;
        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        fs = provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // creates a temporary File System for artifacts file scheme
                        fs = provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        // create spaces
        String res;
        try (Stream<String> stream = Files.lines(Paths.get(uri), StandardCharsets.UTF_8)) {
            res = stream.map(Object::toString)
                    .collect(Collectors.joining());
        }

        // close FileSystem
        if (fs != null) fs.close();

        this.spaces = gson.fromJson(res, BoardSpace[][].class);
    }

    /**
     * Returns a copy of the spaces array via functional approach.
     *
     * @return copy of spaces
     */
    public synchronized BoardSpace[][] getSpaces() {
        return Arrays.stream(spaces)
                .map(BoardSpace[]::clone)
                .toArray(BoardSpace[][]::new);
    }

    /**
     * Checks if a space is usable given coordinates
     *
     * @param x coordinate
     * @param y coordinate
     * @return usability of that space
     * @throws Exception if parameters are trying to go out of the board
     */
    public synchronized boolean isSpaceUsable(int x, int y) throws Exception {
        if (x >= 0 && x < BOARD_DIM_X && y >= 0 && y < BOARD_DIM_Y)
            return spaces[x][y].isUsable();
        else throw new Exception();
    }

    /**
     * Returns Object Card as an instance (not as Optional.of(card))
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card
     * @throws Exception if parameters are trying to go out of the board
     */
    public synchronized ObjectCard getPlainCardFromSpace(int x, int y) throws Exception {
        if (x >= 0 && x < BOARD_DIM_X && y >= 0 && y < BOARD_DIM_Y)
            return spaces[x][y].getPlainCard();
        else throw new Exception();
    }

    /**
     * Returns Object Card's type ordinal, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's ordinal
     * @throws Exception if parameters are trying to go out of the board
     */
    public synchronized int getCardOrdinalFromSpace(int x, int y) throws Exception {
        if (x >= 0 && x <= BOARD_DIM_X - 1 && y >= 0 && y <= BOARD_DIM_Y - 1)
            if (spaces[x][y].getCard().isPresent())
                return spaces[x][y].getCard().get().getType().ordinal();
            else return -1;
        else throw new Exception();
    }

    /**
     * Returns Object Card's image path, given a Board Space
     *
     * @param x coordinate
     * @param y coordinate
     * @return the card's image path
     * @throws Exception if parameters are trying to go out of the board
     */
    public synchronized String getCardImageFromSpace(int x, int y) throws Exception {
        if (x >= 0 && x <= BOARD_DIM_X - 1 && y >= 0 && y <= BOARD_DIM_Y - 1)
            if (spaces[x][y].getCard().isPresent())
                return spaces[x][y].getCard().get().getType().name().concat("-").concat(String.valueOf(spaces[x][y].getCard().get().getImage()));
            else return null;
        else throw new Exception();
    }

    /**
     * Returns a list of usable and effectively present cards on the board, based on a functional approach using Optionals.
     *
     * @return list of cards on the board
     */
    public synchronized List<ObjectCard> boardCards() {
        return Stream.of(spaces)
                .flatMap(Stream::of)
                .filter(s -> s.isUsable() && s.getCard().isPresent())
                .map(s -> s.getCard().orElseThrow())
                .collect(Collectors.toList());
    }

    /**
     * Checks if a reset of the board is needed, watching at usable and effectively present spaces
     *
     * @return boolean: reset of the board is compulsory to continue the game
     */
    synchronized boolean resetNeeded() {
        if (boardCards().isEmpty())
            return true;
        for (int i = 0; i < BOARD_DIM_Y; i++) {
            for (int j = 0; j < BOARD_DIM_Y; j++) {
                if (spaces[i][j].isUsable() && spaces[i][j].getCard().isPresent()) {
                    if (i >= 1)
                        if (spaces[i - 1][j].isUsable() && spaces[i - 1][j].getCard().isPresent())
                            return false;
                    if (i <= BOARD_DIM_Y - 2)
                        if (spaces[i + 1][j].isUsable() && spaces[i + 1][j].getCard().isPresent())
                            return false;
                    if (j >= 1)
                        if (spaces[i][j - 1].isUsable() && spaces[i][j - 1].getCard().isPresent())
                            return false;
                    if (j <= BOARD_DIM_X - 2)
                        if (spaces[i][j + 1].isUsable() && spaces[i][j + 1].getCard().isPresent())
                            return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks the condition for enabling reset on the board (even if not all cards are put on it)
     *
     * @return condition for reset enabling
     */
    private synchronized boolean resetEnabled() {
        return objectCards.size() < ObjectCard.LIMIT;
    }

    /**
     * Implements reset logic. The same approach is used in the construction of the board.
     */
    private synchronized void reset() {
        if (resetNeeded() && resetEnabled()) {
            for (int i = 0; i < BOARD_DIM_X; i++) {
                for (int j = 0; j < BOARD_DIM_Y; j++) {
                    if (spaces[i][j].isUsable() && spaces[i][j].getCard().isEmpty()) {
                        try {
                            spaces[i][j].setCard(Optional.of(new ObjectCard(objectCards)));
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if, given a list of sequentially ordered cards, every card has a "free side".
     * VERSION 2: checks for every card rather than the group
     *
     * @param list of sequentially ordered cards (indexes on the board can be disordered)
     * @return whether if a move is permitted
     */
    public synchronized boolean valid(List<ObjectCard> list) {
        if (list == null || list.isEmpty() || list.size() > 3 || list.contains(null))
            return false;
        if (!boardCards().containsAll(list))
            return false;

        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        int counter = 0;

        // finds cards on the board
        for (int i = 0; i < BOARD_DIM_Y; i++) {
            for (int j = 0; j < BOARD_DIM_Y; j++) {
                if (spaces[i][j].isUsable() && spaces[i][j].getCard().isPresent()) {
                    if (counter < list.size() && list.contains(spaces[i][j].getCard().get())) {
                        x.add(i);
                        y.add(j);
                        counter++;
                    }
                }
            }
        }
        if (x.size() != list.size() || y.size() != list.size())
            return false;

        // cards are on the same row
        if (x.stream().distinct().count() == 1) {
            Collections.sort(y);
            if (list.size() == 3) {
                if (!(y.get(2).equals(y.get(1) + 1) && y.get(1).equals(y.get(0) + 1))) {
                    return false;
                }
            } else if (list.size() == 2) {
                if (!(y.get(1).equals(y.get(0) + 1))) {
                    return false;
                }
            }
        }

        // cards are on the same column
        if (y.stream().distinct().count() == 1) {
            Collections.sort(x);
            if (list.size() == 3) {
                if (!(x.get(2).equals(x.get(1) + 1) && x.get(1).equals(x.get(0) + 1))) {
                    return false;
                }
            } else if (list.size() == 2) {
                if (!(x.get(1).equals(x.get(0) + 1))) {
                    return false;
                }
            }
        }

        counter = 0;
        // order independent check of free sides
        for (int i = 0; i < x.stream().distinct().count(); i++) {
            for (int j = 0; j < y.stream().distinct().count(); j++) {
                if (x.get(i) == 0 || x.get(i) == BOARD_DIM_X - 1) {
                    counter++;
                    continue;
                }
                if (y.get(j) == 0 || y.get(j) == BOARD_DIM_Y - 1) {
                    counter++;
                    continue;
                }
                if (x.get(i) >= 1)
                    if (spaces[x.get(i) - 1][y.get(j)].getCard().isEmpty()) {
                        counter++;
                        continue;
                    }
                if (x.get(i) <= BOARD_DIM_Y - 2)
                    if (spaces[x.get(i) + 1][y.get(j)].getCard().isEmpty()) {
                        counter++;
                        continue;
                    }
                if (x.get(j) >= 1)
                    if (spaces[x.get(i)][y.get(j) - 1].getCard().isEmpty()) {
                        counter++;
                        continue;
                    }
                if (x.get(j) <= BOARD_DIM_X - 2)
                    if (spaces[x.get(i)][y.get(j) + 1].getCard().isEmpty()) {
                        counter++;
                        continue;
                    }
            }
        }

        return counter == list.size();
    }

    /**
     * Checks if a group of couples of coordinates is a valid move on the board
     *
     * @param x list ofcoordinates
     * @param y list of coordinates
     * @return move validation
     * @throws Exception to manage board dimension limits
     */
    public synchronized boolean validFromCoordinate(List<Integer> x, List<Integer> y) throws Exception {
        if (x == null || y == null || x.isEmpty() || y.isEmpty() || x.contains(null) || y.contains(null))
            return false;
        if (x.size() != y.size())
            return false;
        List<ObjectCard> arg = getCardsFromCoordinate(x, y);
        return valid(arg);
    }

    /**
     * Returns a list of cards contained in a group of coordinates
     *
     * @param x list of coordinates
     * @param y list of coordinates
     * @return the list of cards
     * @throws Exception to manage board dimension limits
     */
    public synchronized List<ObjectCard> getCardsFromCoordinate(List<Integer> x, List<Integer> y) throws Exception {
        if (x == null || y == null || x.isEmpty() || y.isEmpty() || x.contains(null) || y.contains(null))
            throw new Exception();
        if (x.size() != y.size())
            throw new Exception();
        List<ObjectCard> ret = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            ret.add(getPlainCardFromSpace(x.get(i), y.get(i)));
        }
        return ret;
    }

    /**
     * Removes from board a list of ordered cards (only valid moves are accepted)
     *
     * @param orderedCards list of ordered cards (also if not internally)
     * @throws Exception if an invalid deletion is tried
     */
    public synchronized void takeFromBoard(List<ObjectCard> orderedCards) throws Exception {
        if (!valid(orderedCards))
            throw new Exception();
        if (!boardCards().containsAll(orderedCards))
            throw new Exception();

        lastTaken.clear();
        for (int i = 0; i < BOARD_DIM_Y; i++) {
            for (int j = 0; j < BOARD_DIM_Y; j++) {
                if (spaces[i][j].getCard().isPresent() && orderedCards.contains(spaces[i][j].getCard().get())) {
                    lastTaken.put(spaces[i][j].getCard().get(), spaces[i][j]);
                    spaces[i][j].setCard(Optional.empty());
                }
            }
        }
        if (!lastTaken.isEmpty()) {
            reset();
        }
    }

    /**
     * Removes from board a group of cards based on their coordinates (only valid moves are accepted)
     *
     * @param x list of coordinates
     * @param y list of coordinates
     * @return action validity
     * @throws Exception if an invalid deletion is tried
     */
    public synchronized List<ObjectCard> takeFromBoardFromCoordinate(List<Integer> x, List<Integer> y) throws Exception {
        if (x == null || y == null || x.isEmpty() || y.isEmpty())
            throw new Exception();
        if (x.size() != y.size())
            throw new Exception();
        List<ObjectCard> arg = getCardsFromCoordinate(x, y);
        takeFromBoard(arg);
        return arg;
    }

    /**
     * Restores the last move (group of cards taken from board)
     *
     * @throws Exception if a restore is tried before any take has happened
     */
    public synchronized void restoreLastTaken() throws Exception {
        if (lastTaken == null || lastTaken.isEmpty())
            throw new Exception();
        for (ObjectCard oc : lastTaken.keySet()) {
            spaces[lastTaken.get(oc).getX()][lastTaken.get(oc).getY()].setCard(Optional.of(oc));
        }
        lastTaken.clear();
    }

    /**
     * Refreshed copy, after server reload from file
     *
     * @return new copy of the object
     * @throws RemoteException related to RMI
     */
    public Board getCopy() throws RemoteException {
        return new Board(this.spaces, this.objectCards, this.lastTaken);
    }

    /**
     * @return extracted Object Cards list
     */
    public List<ObjectCard> getObjectCards() {
        return objectCards;
    }
}