package it.polimi.ingsw.Server.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Client.View.BoardView;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Test of Board class
 */
public class BoardTest {
    Board b;

    /**
     * Verifies that initialization creates a non-empty board
     */
    @Test
    void initialize() {
        b = null;
        try {
            for (int p = 2; p <= 4; p++) {
                b = new Board(p, new ArrayList<>());
                if (b != null) {
                    for (int i = 0; i < Board.BOARD_DIM_X; i++) {
                        for (int j = 0; j < Board.BOARD_DIM_Y; j++) {
                            assert b.getSpaces()[i][j] != null;
                        }
                    }
                } else assert false;
                assert !b.resetNeeded();
            }
            for (int p = 2; p <= 4; p++) {
                b = new Board(p, null);
                if (b != null) {
                    for (int i = 0; i < Board.BOARD_DIM_X; i++) {
                        for (int j = 0; j < Board.BOARD_DIM_Y; j++) {
                            assert b.getSpaces()[i][j] != null;
                            BoardSpace temp = new BoardSpace(i, j, b.getSpaces()[i][j].isUsable(), b.getSpaces()[i][j].getDots());
                            assert b.getSpaces()[i][j].getX() == temp.getX();
                            assert b.getSpaces()[i][j].getY() == temp.getY();
                            assert b.getSpaces()[i][j].isUsable() == temp.isUsable();
                            assert b.getSpaces()[i][j].getDots().equals(temp.getDots());
                        }
                    }
                } else assert false;
                assert !b.resetNeeded();
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        assert true;
    }

    /**
     * Checks for couple matches on 2 given Integer lists.
     *
     * @param l1 list 1
     * @param l2 list 2
     * @param x  coordinate
     * @param y  coordinate
     * @return true if x and y are contained in first and second list respectively, in the same position
     * @throws Exception when list sizes are different
     */
    boolean CorrespondingListChecker(List<Integer> l1, List<Integer> l2, int x, int y) throws Exception {
        if (l1.size() != l2.size()) throw new Exception();
        for (int i = 0; i < l1.size(); i++) {
            if (l1.get(i).equals(x) && l2.get(i).equals(y))
                return true;
        }
        return false;
    }

    /**
     * Verifies that initialization of unusable, three and for dots spaces is done as expected and checks for every possible space.
     */
    @Test
    void correctSpaces() {
        b = null;
        try {
            // resource locator
            URI uri = new File("src/test/res/".concat("unusable-spaces.txt")).toURI();

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

            Scanner s = new Scanner(new FileReader(Paths.get(uri).toFile()));
            List<Integer> unusableSpaces1 = new ArrayList<>();
            List<Integer> unusableSpaces2 = new ArrayList<>();
            while (s.hasNext()) {
                unusableSpaces1.add(s.nextInt());
                unusableSpaces2.add(s.nextInt());
            }

            // close FileSystem
            if (fs != null) fs.close();

            // resource locator
            uri = new File("src/test/res/".concat("three-dots.txt")).toURI();

            // FileSystem support structure, related to JAR file structure
            fs = null;
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

            s = new Scanner(new FileReader(Paths.get(uri).toFile()));
            List<Integer> threeDots1 = new ArrayList<>();
            List<Integer> threeDots2 = new ArrayList<>();
            while (s.hasNext()) {
                threeDots1.add(s.nextInt());
                threeDots2.add(s.nextInt());
            }

            // close FileSystem
            if (fs != null) fs.close();

            // resource locator
            uri = new File("src/test/res/".concat("four-dots.txt")).toURI();

            // FileSystem support structure, related to JAR file structure
            fs = null;
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

            s = new Scanner(new FileReader(Paths.get(uri).toFile()));
            List<Integer> fourDots1 = new ArrayList<>();
            List<Integer> fourDots2 = new ArrayList<>();
            while (s.hasNext()) {
                fourDots1.add(s.nextInt());
                fourDots2.add(s.nextInt());
            }

            // close FileSystem
            if (fs != null) fs.close();

            for (int p = 2; p <= 4; p++) {
                b = new Board(p, new ArrayList<>());
                for (int i = 0; i < Board.BOARD_DIM_X; i++) {
                    for (int j = 0; j < Board.BOARD_DIM_Y; j++) {
                        if (CorrespondingListChecker(unusableSpaces1, unusableSpaces2, i, j)) {
                            assert !b.getSpaces()[i][j].isUsable();
                        }

                        if (p == 2) {
                            if (CorrespondingListChecker(threeDots1, threeDots2, i, j)) {
                                assert !b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(3));
                                assert b.getSpaces()[i][j].getPlainDots() == 3;
                            }
                            if (CorrespondingListChecker(fourDots1, fourDots2, i, j)) {
                                assert !b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(4));
                                assert b.getSpaces()[i][j].getPlainDots() == 4;
                            }
                        }

                        if (p == 3) {
                            if (CorrespondingListChecker(threeDots1, threeDots2, i, j)) {
                                assert b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(3));
                                assert b.getSpaces()[i][j].getPlainDots() == 3;
                            }
                            if (CorrespondingListChecker(fourDots1, fourDots2, i, j)) {
                                assert !b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(4));
                                assert b.getSpaces()[i][j].getPlainDots() == 4;
                            }
                        }

                        if (p == 4) {
                            if (CorrespondingListChecker(threeDots1, threeDots2, i, j)) {
                                assert b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(3));
                                assert b.getSpaces()[i][j].getPlainDots() == 3;
                            }
                            if (CorrespondingListChecker(fourDots1, fourDots2, i, j)) {
                                assert b.getSpaces()[i][j].isUsable();
                                assert b.getSpaces()[i][j].getDots().equals(Optional.of(4));
                                assert b.getSpaces()[i][j].getPlainDots() == 4;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        assert true;
    }

    /**
     * checks if boardCards() returns the correct List of cards
     */
    @Test
    void checkBoardCards() {
        b = null;
        try {
            for (int p = 2; p <= 4; p++) {
                b = new Board(p, new ArrayList<>());
                b = b.getCopy();
                for (int i = 0; i < Board.BOARD_DIM_X; i++) {
                    for (int j = 0; j < Board.BOARD_DIM_Y; j++) {
                        if (b.getSpaces()[i][j].isUsable() && b.getSpaces()[i][j].getCard().isPresent()) {
                            assert b.boardCards().contains(b.getSpaces()[i][j].getCard().get()) || b.boardCards().contains(b.getSpaces()[i][j].getPlainCard());
                        }
                    }
                }
                assert b.boardCards().containsAll(b.getObjectCards()) && b.getObjectCards().containsAll(b.boardCards());
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        assert true;
    }

    /**
     * Checks if invalid moves are correctly recognized
     */
    @Test
    void invalidMoves() throws Exception {
        b = new Board(2, new ArrayList<>());

        boolean thrown = false;
        try {
            b.isSpaceUsable(Board.BOARD_DIM_X, Board.BOARD_DIM_Y);
        } catch (Exception e) {
            thrown = true;
        }
        assert thrown;

        thrown = false;
        try {
            b.isSpaceUsable(-1, 0);
        } catch (Exception e) {
            thrown = true;
        }
        assert thrown;

        thrown = false;
        try {
            b.getPlainCardFromSpace(-1, 0);
        } catch (Exception e) {
            thrown = true;
        }
        assert thrown;

        thrown = false;
        try {
            b.getCardOrdinalFromSpace(-1, 0);
            b.getCardImageFromSpace(-1, 0);
        } catch (Exception e) {
            thrown = true;
        }
        assert thrown;

        thrown = false;
        try {
            b.getCardImageFromSpace(-1, 0);
        } catch (Exception e) {
            thrown = true;
        }
        assert thrown;

        assert b.getCardImageFromSpace(1, 2) == null;
        assert b.getCardImageFromSpace(1, 4) != null;
        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[1][4].getCard().get());
            add(b.getSpaces()[1][3].getCard().get());
        }});

        assert b.valid(new ArrayList<>() {{
            add(b.getPlainCardFromSpace(1, 4));
            add(b.getPlainCardFromSpace(1, 3));
        }});

        assert b.validFromCoordinate(new ArrayList<>() {{
            add(1);
            add(1);
        }}, new ArrayList<>() {{
            add(4);
            add(3);
        }});

        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[2][4].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[3][3].getCard().get());
            add(b.getSpaces()[3][4].getCard().get());
            add(b.getSpaces()[3][5].getCard().get());
        }});
        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[3][2].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[3][2].getCard().get());
            add(b.getSpaces()[4][1].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[4][1].getCard().get());
            add(b.getSpaces()[4][2].getCard().get());
            add(b.getSpaces()[4][3].getCard().get());
            add(b.getSpaces()[4][4].getCard().get());
        }});
        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[5][2].getCard().get());
        }});
        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[5][1].getCard().get());
            add(b.getSpaces()[5][2].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[5][1].getCard().get());
            add(b.getSpaces()[5][2].getCard().get());
            add(b.getSpaces()[5][3].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[3][5].getCard().get());
            add(b.getSpaces()[3][6].getCard().get());
            add(b.getSpaces()[3][7].getCard().get());
        }});
        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[3][6].getCard().get());
            add(b.getSpaces()[3][7].getCard().get());
        }});
        assert !b.valid(new ArrayList<>() {{
            add(b.getSpaces()[4][6].getCard().get());
            add(b.getSpaces()[4][7].getCard().get());
        }});

        assert b.valid(new ArrayList<>() {{
            add(b.getSpaces()[4][7].getCard().get());
        }});
    }

    /**
     * Empties the board and checks for limit reachability and management in generation of new Object Cards in end game conditions
     */
    @Test
    void emptyBoard() throws Exception {
        b = new Board(4, new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[0][3].getCard().get());
                add(b.getSpaces()[0][4].getCard().get());
            }});
            b.takeFromBoardFromCoordinate(new ArrayList<>() {{
                add(1);
                add(1);
                add(1);
            }}, new ArrayList<>() {{
                add(3);
                add(4);
                add(5);
            }});

            /* b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[1][3].getCard().get());
                add(b.getSpaces()[1][4].getCard().get());
                add(b.getSpaces()[1][5].getCard().get());
            }}); */

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[2][2].getCard().get());
                add(b.getSpaces()[2][3].getCard().get());
                add(b.getSpaces()[2][4].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[2][5].getCard().get());
                add(b.getSpaces()[2][6].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[3][1].getCard().get());
                add(b.getSpaces()[3][2].getCard().get());
                add(b.getSpaces()[3][3].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[3][4].getCard().get());
                add(b.getSpaces()[3][5].getCard().get());
                add(b.getSpaces()[3][6].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[3][7].getCard().get());
                add(b.getSpaces()[3][8].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[4][0].getCard().get());
                add(b.getSpaces()[4][1].getCard().get());
                add(b.getSpaces()[4][2].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[4][3].getCard().get());
                add(b.getSpaces()[4][4].getCard().get());
                add(b.getSpaces()[4][5].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[4][6].getCard().get());
                add(b.getSpaces()[4][7].getCard().get());
                add(b.getSpaces()[4][8].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[5][0].getCard().get());
                add(b.getSpaces()[5][1].getCard().get());
                add(b.getSpaces()[5][2].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[5][3].getCard().get());
                add(b.getSpaces()[5][4].getCard().get());
                add(b.getSpaces()[5][5].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[5][6].getCard().get());
                add(b.getSpaces()[5][7].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[6][2].getCard().get());
                add(b.getSpaces()[6][3].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[6][4].getCard().get());
                add(b.getSpaces()[6][5].getCard().get());
                add(b.getSpaces()[6][6].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[7][3].getCard().get());
                add(b.getSpaces()[7][4].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[7][5].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[8][4].getCard().get());
            }});

            b.takeFromBoard(new ArrayList<>() {{
                add(b.getSpaces()[8][5].getCard().get());
            }});
        }
    }

    /**
     * Last taken cards restore test
     */
    @Test
    void restore() throws Exception {
        b = new Board(4, new ArrayList<>());
        b.takeFromBoard(new ArrayList<>() {{
            add(b.getSpaces()[0][3].getCard().get());
            add(b.getSpaces()[0][4].getCard().get());
        }});
        b.takeFromBoard(new ArrayList<>() {{
            add(b.getSpaces()[1][3].getCard().get());
            add(b.getSpaces()[1][4].getCard().get());
            add(b.getSpaces()[1][5].getCard().get());
        }});
        b.restoreLastTaken();

        assert b.boardCards().size() == 43;
    }

    /**
     * JSON Serialization Test
     *
     * @throws Exception related to Model Management
     */
    @Test
    void JSONSerialization() throws Exception {
        b = new Board(4, new ArrayList<>());
        Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(b);
        System.out.println(res);
        BoardView.print(b, new PrintWriter(System.out));
        b = gson.fromJson(res, Board.class);
    }

    /**
     * Tests System.out of the Board, after each test
     */
    @AfterEach
    void outBoard() throws RemoteException {
        BoardView.print(b, new PrintWriter(System.out));
    }
}
