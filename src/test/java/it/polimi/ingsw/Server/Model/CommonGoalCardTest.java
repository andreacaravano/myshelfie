package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Client.View.ShelfView;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for CommonGoalCard class
 */

public class CommonGoalCardTest {

    @Test
    void testAttributeEmptyShelf() throws Exception { //empty shelf
        Shelf testShelf = new Shelf();
        for (int i = 1; i <= 12; i++) {
            for (int j = 2; j < 5; j++) {
                CommonGoalCard card = new CommonGoalCard(i, j);
                assert card.getCopy().getDescription().equals(card.getDescription()) && card.getCopy().getIncrements().pop().getValue() == card.getIncrements().pop().getValue() && card.getCopy().getType() == card.getType();
                int score = card.attribute(testShelf).getValue();
                assert score == 0;
            }
        }
    }

    @Test
    void testWrongCardType_WrongNumOfPlayers() throws Exception{
        Exception exception = assertThrows(Exception.class, () -> {
            CommonGoalCard card = new CommonGoalCard(0,3);;
        });
        Exception exception1 = assertThrows(Exception.class, () -> {
            CommonGoalCard card = new CommonGoalCard(1,5);;
        });

    }

    @Test
    void testAttributeCard1() throws Exception {
        Shelf testShelf = new Shelf();
        Shelf testShelf2 = new Shelf();
        CommonGoalCard card = new CommonGoalCard(1, 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        int score = card.attribute(testShelf).getValue();
        assert score == 8;
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf2.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        int score2 = card.attribute(testShelf2).getValue();
        assert score2 == 6;

    }

    @Test
    void testAttributeCard1_invalidPattern() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(1, 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard2_invalidPattern() throws Exception { //One column with 6 different types and a column with
        //only 5 cards
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(2, 3);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard2_invalidPattern2() throws Exception { //One column with 6 different types and a column with
        //6 cards, but only 5 different types
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(2, 3);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard2_ValidPattern3() throws Exception { //Pattern rispettato
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(2, 3);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard3_invalidPattern1() throws Exception { //checks that a group that overlaps another can't be
        //counted
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard3_validPattern1() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);

        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard3_validPattern2() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 1; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), i);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard3_validPattern3() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }


    @Test
    void testAttributeCard3_validPattern4() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard3_validPattern5() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard3_validPattern6() throws Exception { //valid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(3, 2);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard4_invalidPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(4, 2);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard4_validPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(4, 2);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard4_invalidPattern2() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(4, 2);
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard5_invalidPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(5, 2);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard5_invalidPattern2() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(5, 2);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < Shelf.SHELF_DIM_X - 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard5_invalidPattern3() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(5, 2);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard5_validPattern4() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(5, 2);
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        }


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard6_validPattern2() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(6, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard8_validPattern1() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(8, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        for (int i = 0; i < Shelf.SHELF_DIM_X - 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        for (int i = 0; i < Shelf.SHELF_DIM_X - 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard8_invalidPattern1() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(8, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        for (int i = 0; i < Shelf.SHELF_DIM_X - 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        for (int i = 0; i < Shelf.SHELF_DIM_X - 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard9_invalidPattern1() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(9, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard9_validPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(9, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard10_validPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(10, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard10_validPattern2() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(10, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard10_invalidPattern() throws Exception { //invalid pattern
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(10, 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard7_invalidPattern2() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(7, 3);

        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);

        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;
    }

    @Test
    void testAttributeCard7_invalidPattern3() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(7, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard7_validPattern1() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(7, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);

        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard12_validPattern1() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 6; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard12_validPattern2() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard12_validPattern3() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 6; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        }

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard12_validPattern4() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        }


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard12_invalidPattern() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard12_invalidPattern1() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(12, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard11_validPattern1() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard11_validPattern2() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard11_validPattern3() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard11_validPattern4() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard11_invalidPattern() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard11_invalidPattern2() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard11_invalidPattern3() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }
        for (int i = 0; i < 1; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard11_invalidPattern4() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(11, 3);

        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        }
        for (int i = 2; i < 5; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.CATS), 0);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        }
        for (int i = 0; i < 3; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        }
        for (int i = 0; i < 2; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);
        }
        for (int i = 0; i < 6; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 0;

    }

    @Test
    void testAttributeCard4_validPattern4() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(4, 3);

        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);

        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 2);
        }

        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);

        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }

    @Test
    void testAttributeCard4_validPattern5() throws Exception {
        Shelf testShelf = new Shelf();
        CommonGoalCard c1 = new CommonGoalCard(4, 3);

        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), i);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);

        testShelf.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        testShelf.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.PLANTS), 4);
        for (int i = 0; i < 4; i++) {
            testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), i);
        }
        testShelf.placeCard(new ObjectCard(ObjectCardType.GAMES), 4);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 3);
        testShelf.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 4);


        ShelfView.print(testShelf, new PrintWriter(System.out));
        int score = c1.attribute(testShelf).getValue();
        assert score == 8;

    }


}
