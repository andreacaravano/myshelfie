package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Client.View.ShelfView;
import it.polimi.ingsw.Common.ShelfInterface;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Model object Player's test
 */
public class PlayerTest {
    /**
     * Tests if the updateScore method works properly
     *
     * @throws Exception if the PersonalGoalCard's file is not read properly
     */
    @Test
    void testUpdateScore() throws Exception {
        PersonalGoalCard pgc = new PersonalGoalCard(1);
        Random finder = new Random();
        int numPlayers = finder.ints(2, 5).findFirst().getAsInt();
        System.out.println("NumPlayers: " + numPlayers);
        CommonGoalCard cmgc1 = new CommonGoalCard(1, numPlayers);
        CommonGoalCard cmgc2 = new CommonGoalCard(8, numPlayers);
        Board testBoard = new Board(numPlayers, new ArrayList<>());
        Player playertest = new Player("Test", pgc, cmgc1, cmgc2, 1, testBoard);
        playertest.updateScore();
        assertEquals(0, playertest.getScore());
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        ShelfView.print(playertest.getShelf(), new PrintWriter(System.out));
        playertest.updateScore();
        assertEquals(8, playertest.getScore());
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        ShelfView.print(playertest.getShelf(), new PrintWriter(System.out));
        playertest.updateScore();
        assertEquals(16, playertest.getScore());
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.FRAMES), 2);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.FRAMES), 2);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.FRAMES), 2);
        playertest.getShelf().placeCard(new ObjectCard(ObjectCardType.FRAMES), 2);
        ShelfView.print(playertest.getShelf(), new PrintWriter(System.out));
        playertest.setOutOfTurn();
        playertest.updateScore();
        assertEquals(31, playertest.getScore());
        Shelf s = playertest.getShelf();
        int position = playertest.getPosition();
        int pop = playertest.getScoreCards().pop().getValue();
        playertest.refreshEntities(cmgc1, cmgc2);
        assert playertest.getPersonalGoalCard().getType() == pgc.getType();
        assert playertest.getPosition() == position;
        assert playertest.getScoreCards().pop().getValue() == pop;
        for (int i = 0; i < ShelfInterface.SHELF_DIM_X; i++) {
            for (int j = 0; j < ShelfInterface.SHELF_DIM_Y; j++) {
                if (s.getCardImage(i, j) == null)
                    assert playertest.getShelf().getCardImage(i, j) == null;
                else assert playertest.getShelf().getCardImage(i, j).equals(s.getCardImage(i, j));
            }
        }
    }
}
