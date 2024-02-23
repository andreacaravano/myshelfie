package it.polimi.ingsw.Server.Controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Client.ClientModel.ClientStatus;
import it.polimi.ingsw.Client.View.BoardView;
import it.polimi.ingsw.Client.View.ScoreBoardView;
import it.polimi.ingsw.Client.View.ShelfView;
import it.polimi.ingsw.Common.BoardInterface;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.Shelf;
import it.polimi.ingsw.Server.ServerManager;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Test for GameController class
 */
public class GameControllerTest {
    @Test
    void RMIcreation() throws Exception {
        RMIController c = new RMIController(new HashMap<>(), new HashMap<>());
        assert !c.identifierExists("test");
        assert c.createGame("test", new ClientStatus(null));
        assert !c.acceptPlayer("test", "nick", -3);
        assert c.acceptPlayer("test", "nick", 2);
        assert !c.acceptPlayer("test", "nick", 2);
        assert c.acceptPlayer("test", "nick2", -5);
        assert !c.acceptPlayer("test", "nick3", 2);
    }

    @Test
    void Testgame() throws Exception {
        //Game setup
        Move m;
        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        GameController gc = new GameController("TestGame");
        assert !gc.isGamePrepared() && !gc.isGameComplete() && !gc.isGameEnded();
        assert gc.getIdentifier().equals("TestGame");
        assert gc.acceptPlayer("Bob21", 4);
        assert gc.getMaxPlayers() == 4;
        assert gc.acceptPlayer("AlicE1", 0);
        assert gc.acceptPlayer("Max2X", 0);
        assert gc.acceptPlayer("Tom", 0);
        assert !gc.acceptPlayer("Lplayer", 0);
        assert gc.getPlayerByNickname("Lplayer") == null;
        assert gc.getPlayerByNickname("Tom").getNickname().equals("Tom");
        assert gc.isGamePrepared();
        assert gc.getShelf("Tom") != null;
        assert gc.getPersonalGoalCard("Tom") != null;
        assert gc.getCommonGoalCards() != null;
        assert gc.getMoveIntermediate("Tom") != null;
        assert gc.getActivePlayer() != null;
        assert gc.getScoreCardsFromNickname("Bob21").isEmpty();
        assert gc.getScoreCardsFromNickname("AlicE1").isEmpty();
        assert gc.getScoreCardsFromNickname("Max2X").isEmpty();
        assert gc.getScoreCardsFromNickname("Tom").isEmpty();
        assert gc.nicknames().containsAll(Arrays.asList("Bob21", "AlicE1", "Max2X", "Tom"));
        assert !gc.nicknames().containsAll(Arrays.asList("Bob21", "AlicE1", "Max2X", "Tom", "Lplayer"));
        assert gc.getLastTurnScores().isEmpty();
        BoardInterface boardInterface = gc.getGameBoard();
        BoardView.print(boardInterface, new PrintWriter(System.out));

        //first player card selection
        Shelf shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(0);
        y.add(3);
        //second card
        x.add(0);
        y.add(4);
        m = new Move(0, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        Shelf shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(1);
        y.add(3);
        //second card
        x.add(1);
        y.add(4);
        //third card
        x.add(1);
        y.add(5);
        m = new Move(0, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        Shelf shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(2);
        y.add(2);
        //second card
        x.add(2);
        y.add(3);
        //third card
        x.add(2);
        y.add(4);
        m = new Move(0, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        Shelf shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(2);
        y.add(5);
        //second card
        x.add(2);
        y.add(6);
        m = new Move(0, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4:" + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(1);
        //second card
        x.add(3);
        y.add(2);
        //third card
        x.add(3);
        y.add(3);
        m = new Move(0, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(4);
        //second card
        x.add(3);
        y.add(5);
        //third card
        x.add(3);
        y.add(6);
        m = new Move(0, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(7);
        //second card
        x.add(3);
        y.add(8);
        m = new Move(0, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //forth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(0);
        //second card
        x.add(4);
        y.add(1);
        //third card
        x.add(4);
        y.add(2);
        m = new Move(0, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(3);
        m = new Move(0, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        gc.refreshEntities();
        assert !gc.acceptPlayer("nuovo", 0);
        assert gc.getLobby().isEmpty();
        assert gc.nicknames().containsAll(Arrays.asList("Bob21", "AlicE1", "Max2X", "Tom"));
        assert gc.reEnterGame("Bob21");
        assert !gc.reEnterGame("sbagliato");
        assert gc.reEnterGame("AlicE1");
        assert gc.reEnterGame("Max2X");
        assert gc.reEnterGame("Tom");
        assert gc.getLobby().size() == 4;

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(4);
        //second card
        x.add(4);
        y.add(5);
        //third card
        x.add(4);
        y.add(6);
        m = new Move(1, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(7);
        m = new Move(0, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(8);
        m = new Move(0, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));


        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(0);
        //second card
        x.add(5);
        y.add(1);
        //third card
        x.add(5);
        y.add(2);
        m = new Move(1, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(3);
        //second card
        x.add(5);
        y.add(4);
        //third card
        x.add(5);
        y.add(5);
        m = new Move(1, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(6);
        //second card
        x.add(5);
        y.add(7);
        m = new Move(1, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(6);
        y.add(2);
        //second card
        x.add(6);
        y.add(3);
        //third card
        x.add(6);
        y.add(4);
        m = new Move(1, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(6);
        y.add(5);
        //second card
        x.add(6);
        y.add(6);
        m = new Move(1, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(7);
        y.add(3);
        //second card
        x.add(7);
        y.add(4);
        //third card
        x.add(7);
        y.add(5);
        m = new Move(2, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(8);
        y.add(4);
        //second card
        x.add(8);
        y.add(5);
        m = new Move(1, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(0);
        y.add(3);
        //second card
        x.add(0);
        y.add(4);
        m = new Move(1, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(1);
        y.add(3);
        m = new Move(1, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(1);
        y.add(4);
        //second card
        x.add(1);
        y.add(5);
        m = new Move(2, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(2);
        y.add(2);
        //second card
        x.add(2);
        y.add(3);
        m = new Move(1, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(2);
        y.add(4);
        m = new Move(1, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(2);
        y.add(5);
        //second card
        x.add(2);
        y.add(6);
        m = new Move(2, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(1);
        m = new Move(2, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(2);
        m = new Move(2, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(3);
        m = new Move(2, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(4);
        m = new Move(2, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(5);
        //second card
        x.add(3);
        y.add(6);
        //third card
        x.add(3);
        y.add(7);
        m = new Move(3, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(3);
        y.add(8);
        m = new Move(2, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(0);
        m = new Move(2, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(1);
        m = new Move(2, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(2);
        //second card
        x.add(4);
        y.add(3);
        //third card
        x.add(4);
        y.add(4);
        m = new Move(3, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(5);
        //second card
        x.add(4);
        y.add(6);
        m = new Move(2, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(7);
        m = new Move(2, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(4);
        y.add(8);
        m = new Move(2, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(0);
        //second card
        x.add(5);
        y.add(1);
        //third card
        x.add(5);
        y.add(2);
        m = new Move(4, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(3);
        //second card
        x.add(5);
        y.add(4);
        m = new Move(2, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(5);
        //second card
        x.add(5);
        y.add(6);
        m = new Move(2, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        // writeFile(gc);

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(5);
        y.add(7);
        m = new Move(2, x, y, (Board) boardInterface, shelf1);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf1: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf1, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //second player card selection
        shelf2 = gc.getActivePlayer().getShelf();
        //first card
        x.add(6);
        y.add(2);
        //second card
        x.add(6);
        y.add(3);
        //third card
        x.add(6);
        y.add(4);
        m = new Move(1, x, y, (Board) boardInterface, shelf2);
        assert !gc.make(m);
        m = new Move(4, x, y, (Board) boardInterface, shelf2);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf2: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf2, new PrintWriter(System.out));
        assert gc.isGameEnded();

        x.clear();
        y.clear();

        //third player card selection
        shelf3 = gc.getActivePlayer().getShelf();
        //first card
        x.add(6);
        y.add(5);
        m = new Move(3, x, y, (Board) boardInterface, shelf3);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf3: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf3, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //fourth player card selection
        shelf4 = gc.getActivePlayer().getShelf();
        //first card
        x.add(6);
        y.add(6);
        m = new Move(2, x, y, (Board) boardInterface, shelf4);
        assert gc.make(m);
        BoardView.print(boardInterface, new PrintWriter(System.out));
        System.out.println("\nShelf4: " + gc.getActivePlayer().getNickname());
        ShelfView.print(shelf4, new PrintWriter(System.out));

        x.clear();
        y.clear();

        //first player card selection
        shelf1 = gc.getActivePlayer().getShelf();
        //first card
        x.add(7);
        y.add(3);
        m = new Move(3, x, y, (Board) boardInterface, shelf1);
        assert !gc.make(m);    //this player has the first player chair, so he cannot make the move

        assert gc.isGameComplete();

        ScoreBoardView.print(gc.getScoreboard(), new PrintWriter(System.out));
    }

    private void writeFile(GameController gc) {
        // Gson Exclusion Strategy
        ExclusionStrategy serializationStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaringClass() == java.rmi.server.UnicastRemoteObject.class;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };

        // Gson Builder
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setExclusionStrategies(serializationStrategy)
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir").concat("/games-persistency/game-"
                .concat("almost-end").concat(".json"))))) {

            bw.write(gson.toJson(gc));
        } catch (Exception e) {
            ServerManager.writeWarning(String.format("GSON or file management exception: %s%n", e.getMessage()));
        }
    }
}