package it.polimi.ingsw.Server.Controller;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.ObjectCard;
import it.polimi.ingsw.Server.Model.Shelf;

import java.util.List;

/**
 * Move declaration, composed of list of coordinates and Shelf column
 */
public class Move {
    @Expose
    private int column;
    @Expose
    private List<ObjectCard> orderedCards = null;
    @Expose
    private List<Integer> x;
    @Expose
    private List<Integer> y;
    @Expose(serialize = false, deserialize = false)
    private Board board;
    @Expose(serialize = false, deserialize = false)
    private Shelf shelf;

    /**
     * Game's Move implementation
     *
     * @param column Library's column
     * @param x      List of Move coordinates (x)
     * @param y      List of Move coordinates (y)
     * @param board  Game's Board
     * @param shelf  Player's Shelf
     */
    public Move(int column, List<Integer> x, List<Integer> y, Board board, Shelf shelf) {
        this.column = column;
        this.x = x;
        this.y = y;
        this.board = board;
        this.shelf = shelf;
        this.orderedCards = null;
    }

    /**
     * Sets Move's Board and Shelf
     *
     * @param board Game's Board
     * @param shelf Player's Shelf
     */
    public void setBoardAndShelf(Board board, Shelf shelf) {
        this.board = board;
        this.shelf = shelf;
    }

    /**
     * Takes card from Board
     *
     * @return action validity
     */
    public boolean take() {
        try {
            if (orderedCards != null) {
                if (board.valid(orderedCards))
                    board.takeFromBoard(orderedCards);
                else return false;
            } else {
                if (board.validFromCoordinate(x, y))
                    orderedCards = board.takeFromBoardFromCoordinate(x, y);
                else return false;
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Restores taken cards on the Board (when Move was Invalid)
     *
     * @throws Exception related to Model management
     */
    public void restoreTaken() throws Exception {
        board.restoreLastTaken();
    }

    /**
     * Places taken cards in the Player's Library
     *
     * @return action validity
     */
    public boolean place() {
        try {
            if (shelf.emptySpacesInColumn(column) >= orderedCards.size()) {
                for (ObjectCard oc : orderedCards)
                    shelf.placeCard(oc, column);
                return true;
            } else return false;
        } catch (Exception ignored) {
            return false;
        }
    }
}
