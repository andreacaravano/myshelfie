package it.polimi.ingsw.Server.Model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Object Cards randomization tests
 */
public class ObjectCardTest {

    /**
     * Test ObjectCard's class constructor
     */
    @Test
    void testObjectCard() throws Exception {
        ArrayList<ObjectCard> testlist = new ArrayList<>();
        for (int j = 0; j < 111; j++) {
            ObjectCard card = new ObjectCard(testlist);
            assertTrue(testlist.contains(card));
        }
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.CATS)).count() > 0);
        assert ObjectCardType.CATS.getOrd() == 0;
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.BOOKS)).count() > 0);
        assert ObjectCardType.BOOKS.getOrd() == 1;
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.GAMES)).count() > 0);
        assert ObjectCardType.GAMES.getOrd() == 2;
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.FRAMES)).count() > 0);
        assert ObjectCardType.FRAMES.getOrd() == 3;
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.TROPHIES)).count() > 0);
        assert ObjectCardType.TROPHIES.getOrd() == 4;
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.PLANTS)).count() > 0);
        assert ObjectCardType.PLANTS.getOrd() == 5;
        ArrayList<ObjectCard> testlist1 = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 22; j++) {
                ObjectCard card = new ObjectCard(testlist1);
                assertTrue(testlist1.contains(card));
            }
        }
        for (int i = 0; i < 23; i++) {
            ObjectCard card = new ObjectCard(testlist1);
            assertTrue(testlist1.contains(card));
        }
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.CATS)).count() > 0);
        assertTrue(testlist.stream().filter(x -> x.getType().equals(ObjectCardType.PLANTS)).count() > 0);
    }
}
