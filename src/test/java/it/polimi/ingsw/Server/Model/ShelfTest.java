package it.polimi.ingsw.Server.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Client.View.ShelfView;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Player's Shelf tests
 */
public class ShelfTest {
    Shelf s = null;

    /**
     * Creates an empty Shelf, fills it and checks for correct Exception management
     *
     * @throws Exception related to insertion and obtaining of cards
     */
    @Test
    void creation() throws Exception {
        s = new Shelf();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                assert s.getCardImage(i, j) == null;
                assert s.getCard(i, j).isEmpty() && s.getPlainCard(i, j) == null;
            }
        }
        ShelfView.print(s, new PrintWriter(System.out));

        boolean thrownCatch = false;
        try {
            s.placeCard(new ObjectCard(ocList), -1);
            s.placeCard(new ObjectCard(ocList), 6);
        } catch (Exception e) {
            thrownCatch = true;
        }
        assert thrownCatch;

        ShelfView.print(s, new PrintWriter(System.out));

        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
            }
            ShelfView.print(s, new PrintWriter(System.out));
        }

        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                assert s.getCardImage(i, j) != null;
                assert s.getCopy().getCardImage(i, j).equals(s.getCardImage(i, j));
                assert s.getCard(i, j).isPresent() && s.getPlainCard(i, j) != null;
            }
        }

        for (int i = 0; i < Shelf.SHELF_DIM_Y; i++) {
            thrownCatch = false;
            try {
                s.placeCard(new ObjectCard(ocList), i);
            } catch (Exception e) {
                thrownCatch = true;
            }
            assert thrownCatch;
        }
    }

    /**
     * Tests functionality of coordinates and shelfCards methods
     */
    @Test
    void coordinateAndList() throws Exception {
        s = new Shelf();
        assert s.shelfCards().isEmpty();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
                assert s.getCoordinate(s.getCard(Shelf.SHELF_DIM_X - 1 - i, j).get(), true) == Shelf.SHELF_DIM_X - 1 - i;
                assert s.getCoordinate(s.getCard(Shelf.SHELF_DIM_X - 1 - i, j).get(), false) == j;
            }
        }
        assert s.shelfCards().get(0).equals(s.getCard(0, 0).get());
    }

    /**
     * Checks functionality of the final evaluation using the rulebook example
     */
    @Test
    void rulebookFinalCount() throws Exception {
        s = new Shelf();
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);

        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);

        assert s.finalEvaluation() == 21;
    }

    /**
     * Checks functionality of the final evaluation using a randomically generated Shelf
     */
    @Test
    void randomFinalEvaluation() throws Exception {
        s = new Shelf();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
            }
        }
        System.out.format("Final evaluation: %d\n", s.finalEvaluation());
    }

    /**
     * Checks functionality of the final evaluation using a complete example
     */
    @Test
    void completeFinalCount() throws Exception {
        s = new Shelf();

        assert s.finalEvaluation() == 0;

        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 3);

        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);

        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        s.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);

        assert s.finalEvaluation() == 24;
    }

    /**
     * Rulebook example of final evaluation test
     */
    @Test
    void rulebookExample() throws Exception {
        s = new Shelf();
        PersonalGoalCard c1 = new PersonalGoalCard(1);
        CommonGoalCard c2 = new CommonGoalCard(10, 2);
        CommonGoalCard c3 = new CommonGoalCard(4, 2);

        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 0);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 0);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 0);

        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 1);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 1);

        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.TROPHIES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.FRAMES), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);
        s.placeCard(new ObjectCard(ObjectCardType.PLANTS), 2);

        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.GAMES), 3);
        s.placeCard(new ObjectCard(ObjectCardType.BOOKS), 3);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 3);

        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        s.placeCard(new ObjectCard(ObjectCardType.CATS), 4);
        assert s.finalEvaluation() == 18;
        assert s.evaluatePattern(c1) == 6;
        assert c2.attribute(s).getValue() == 8;
        c3.attribute(s);
        assert c3.attribute(s).getValue() == 4;
    }

    /**
     * Test of JSON Serialization
     */
    @Test
    void JSONSerialization() throws Exception {
        s = new Shelf();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
            }
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(s);
        System.out.println(res);
        ShelfView.print(s, new PrintWriter(System.out));
        s = gson.fromJson(res, Shelf.class);
    }

    /**
     * Fills the shelf and checks for isNull and effectively empty spaces in columns properties
     */
    @Test
    void filling() throws Exception {
        s = new Shelf();
        assert !s.isFull();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                assert !s.isFull();
                s.placeCard(new ObjectCard(ocList), j);
                assert s.emptySpacesInColumn(j) == Shelf.SHELF_DIM_X - i - 1;
            }
        }
        assert s.isFull();
        for (int i = 0; i < Shelf.SHELF_DIM_Y; i++) {
            assert s.emptySpacesInColumn(i) == 0;
        }
    }

    /**
     * Tests System.out of the Shelf, after each test
     */
    @AfterEach
    void outShelf() {
        ShelfView.print(s, new PrintWriter(System.out));
    }
}