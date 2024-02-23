package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.ClientModel.ClientStatus;
import it.polimi.ingsw.Client.View.MultipleView;
import it.polimi.ingsw.Client.View.OutputAbstraction;
import it.polimi.ingsw.Client.View.ScoreBoardView;
import it.polimi.ingsw.Client.View.TerminalAbstraction;
import it.polimi.ingsw.Common.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RMI Client abstraction, attached to terminal and GUI views
 *
 * @author Gruppo AM34
 */
public class RMIClient implements RMIClientInterface {
    private static final int countParameters = 2 + 1 + 1; // port and server name + GUI selection + RMI Hostname
    private static final PrintWriter terminalOutput = new PrintWriter(System.out, true);
    private static final BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private static final TerminalAbstraction ta = new TerminalAbstraction(terminalInput, terminalOutput);
    private static final ExecutorService executors = Executors.newCachedThreadPool();
    private static int PORT = 3434; // default value
    private static String serverName = "localhost"; // default value
    private static boolean useGUI;
    private static OutputAbstraction oa;
    private ClientStatus cs = null;
    private RMIControllerInterface controller = null;
    private boolean firstTime = true;
    private final int SEC_TIMEOUT = (6 * 5) / 2; // Timeout in seconds
    AtomicBoolean lifelineFlag = new AtomicBoolean(true);

    /**
     * RMI Client declaration
     *
     * @param args default arguments array
     */
    public RMIClient(String[] args) {
        terminalOutput.format("Your client is running Java %s in %s%n%n", System.getProperty("java.version"), System.getProperty("user.dir"));
        terminalOutput.println("Welcome to My Shelfie! (RMI client component)");
        terminalOutput.println("Made in Academic Year 2022/23 by AM34 group");
        try {
            Registry registry = LocateRegistry.getRegistry(serverName, PORT);
            terminalOutput.format("Connected to server: %s:%d%n", serverName, PORT);
            StatusIntermediateInterface intermediate = (StatusIntermediateInterface) registry.lookup("intermediate");
            controller = (RMIControllerInterface) registry.lookup("controller");
            cs = new ClientStatus(this);
            if (useGUI) executors.execute(() -> GUI.main(args));

            // schedules lifeline calls
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    executors.execute(() -> {
                        try {
                            if (!lifelineFlag.get())
                                throw new Exception();
                            lifelineFlag.set(false);
                            intermediate.lifeline();
                            lifelineFlag.set(true);
                        } catch (Exception e) {
                            oa.showErrorConfirm("Couldn't reach server. Terminating game.");
                            timer.cancel();
                            executors.shutdown();
                            System.exit(1);
                        }
                    });
                }
            }, 0, SEC_TIMEOUT * 1000);

            // initiates callback
            intermediate.setIntermediate(cs);
        } catch (RemoteException e) {
            terminalOutput.format("Server is not available: %s", e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            terminalOutput.format("Property not bound in RMI Registry: %s", e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            terminalOutput.format("Cuncurrent management exception: %s", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            terminalOutput.format("Hierarchy exception: %s", e.getMessage());
            System.exit(1);
        }
        System.exit(1);
    }

    /**
     * Main method, welcoming Client
     *
     * @param args default arguments array
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0)
                useGUI = true; // default abstraction is GUI
            if (args.length == countParameters) {
                useGUI = Boolean.parseBoolean(args[0]);
                PORT = Integer.parseInt(args[1]);
                serverName = args[2];
                System.setProperty("java.rmi.server.hostname", args[3]);
            } else if (args.length == 2 + 1) {
                useGUI = Boolean.parseBoolean(args[0]);
                PORT = Integer.parseInt(args[1]);
                serverName = args[2];
            } else if (args.length == 1 + 1) {
                useGUI = Boolean.parseBoolean(args[0]);
                PORT = Integer.parseInt(args[1]);
            } else if (args.length == 1) {
                useGUI = Boolean.parseBoolean(args[0]);
            }
            oa = new OutputAbstraction(useGUI, terminalOutput);
        } catch (Exception e) {
            terminalOutput.println("Invalid CLI parameter.");
        }
        executors.execute(() -> new RMIClient(args));
    }

    /**
     * Client's evolution implementation
     *
     * @return desire to continue login (otherwise ignored)
     */
    public boolean evolve() {
        try {
            switch (cs.getStatus()) {
                case Sending_Identifier -> {
                    if (useGUI) {
                        GUI.allowExitMenu.lock();
                        GUI.allowIdentifier.lock();
                    }
                    return login();
                }
                case YourTurn -> {
                    if (useGUI)
                        GUI.setMyTurn(true);
                    return formulateMove();
                }
                case NotYourTurn -> {
                    if (useGUI)
                        GUI.setMyTurn(false);
                    return notYourTurn();
                }
                case ForcedGameEnd -> {
                    return forcedGameEnd();
                }
                case LastTurn -> {
                    return lastTurn();
                }
                case FinalScoreboard -> {
                    return finalScoreBoard();
                }
                case AchievementUpdate -> {
                    return achievementsUpdate();
                }
            }
        } catch (RemoteException e) {
            oa.showErrorConfirm(String.format("Remote error: %s", e.getMessage()));
        } catch (Exception e) {
            oa.showErrorConfirm(String.format("Exception: %s", e.getMessage()));
        }
        return false;
    }

    /**
     * Login strategy
     *
     * @return conventionally, true if client's intention is to continue login actions, false otherwise
     * @throws RemoteException Related to RMI
     */
    private boolean login() throws RemoteException {
        String identifier;
        String nickname;
        int maxPlayers = -1;

        String input = null;

        if (!useGUI) {
            terminalOutput.println("Note that both the game identifier and your nickname must be an alphanumeric string, of 3 or more letters");

            // Insert game identifier
            do {
                terminalOutput.print("Please, insert the game identifier: ");
                terminalOutput.flush();

                try {
                    input = terminalInput.readLine();
                } catch (IOException e) {
                    terminalOutput.format("Error with I/O: %s%n", e.getMessage());
                }
            } while (input == null || !input.matches("^[a-zA-Z0-9_]+$") || input.length() < 3);
            terminalOutput.println("Valid identifier.");
        }
        identifier = useGUI ? GUI.getGameID() : input;
        cs.setIdentifier(identifier);

        // Insert number of players, if new game
        if (!controller.identifierExists(identifier)) {
            if (useGUI) {
                GUI.setNewMatch(true);
                GUI.allowLogin.lock();
            }
            if (!controller.createGame(identifier, cs)) {
                oa.showErrorConfirm("Fatal error, cannot create Game.");
                return false;
            } else {
                if (!useGUI) {
                    terminalOutput.println("You can play with up to 4 players in a game.");
                    terminalOutput.print("You are creating a new game! ");
                    do {
                        terminalOutput.print("Insert the number of players you want to play with: ");
                        terminalOutput.flush();
                        try {
                            maxPlayers = Integer.parseInt(terminalInput.readLine());
                        } catch (NumberFormatException e) {
                            terminalOutput.println("You have not inserted a number! Retry.");
                            maxPlayers = -1;
                        } catch (IOException e) {
                            terminalOutput.format("Error with I/O: %s%n", e.getMessage());
                            maxPlayers = -1;
                        }
                    }
                    while (maxPlayers < 2 || maxPlayers > 4); // includes -1 condition
                } else maxPlayers = Integer.parseInt(GUI.getNumOfPlayers());
            }
        } else {
            if (useGUI) GUI.allowLogin.lock();
        }
        if (!useGUI)
            terminalOutput.println("Valid number of players.");

        if (!useGUI) {
            // Insert nickname
            input = null;
            do {
                terminalOutput.print("Please, insert your nickname: ");
                terminalOutput.flush();

                try {
                    input = terminalInput.readLine();
                } catch (IOException e) {
                    terminalOutput.format("Error with I/O: %s%n", e.getMessage());
                }
            }
            while (input == null || !input.matches("^[a-zA-Z0-9_]+$") || input.length() < 3);
        }
        nickname = useGUI ? GUI.getNickname() : input;
        if (controller.nicknameExists(identifier, nickname)) {
            oa.showInfo(String.format("You are trying to re-enter the game as %s.", nickname));
        }
        if (!useGUI)
            terminalOutput.println("Valid nickname.");
        cs.setNickname(nickname);

        try {
            if (!controller.nicknameExists(identifier, nickname)) {
                if (controller.acceptPlayer(identifier, nickname, maxPlayers)) {
                    if (!useGUI)
                        terminalOutput.println("Waiting for the game to start.");
                    else {
                        GUI.setScene(Scenes.loadingScene);
                        GUI.setNicknameText(nickname);
                    }
                    return true;
                } else {
                    oa.showErrorConfirm("Game is full or has been deleted!");
                    throw new Exception("Game is full or has been deleted!");
                }
            } else {
                if (controller.reEnterGame(identifier, nickname)) {
                    if (useGUI) {
                        GUI.setScene(Scenes.loadingScene);
                        GUI.setNicknameText(nickname);
                    }
                    return true;
                } else {
                    oa.showErrorConfirm("You are not allowed to re-enter the game.");
                    throw new Exception("You are not allowed to re-enter the game.");
                }
            }
        } catch (Exception e) {
            oa.showErrorConfirm(String.format("Fatal error! Could not enter game! %s", e.getMessage()));
        }
        return false;
    }

    /**
     * Move creation phase, updated effective Model
     *
     * @return true (ignored)
     * @throws RemoteException related to RMI
     */
    private boolean formulateMove() throws Exception {
        String line = null;
        if (firstTime && useGUI) {
            GUI.setScene(Scenes.gameScene);
            firstTime = false;
        }
        BoardInterface board = cs.getBoard();
        ShelfInterface shelf = cs.getShelf();
        PersonalGoalCardInterface pgCard = cs.getPersonalGoalCard();
        if (!useGUI) MultipleView.automaticPrint(board, shelf, pgCard, terminalOutput);
        else {
            GUI.updateBoard(board);
            GUI.updateShelf(shelf);
            GUI.updateCommonGoalCards(cs.getCommonGoalCard().get(0), cs.getCommonGoalCard().get(1));
            GUI.updatePersonalGoalCard(pgCard);
        }
        if (!useGUI) {
            ta.printCGC(cs.getCommonGoalCard().get(0), cs.getCommonGoalCard().get(1));
        }

        MoveIntermediateInterface mi = cs.getMoveIntermediate();

        if (!useGUI) {
            List<Integer> x = new ArrayList<>();
            List<Integer> y = new ArrayList<>();

            int xTemp = -1;
            int yTemp = -1;

            int column = -1;

            // Insert move coordinates
            terminalOutput.println("You are making a move!");
            do {
                do {
                    try {
                        terminalOutput.println("When you want to stop, insert S here.");
                        terminalOutput.print("Insert X coordinate: ");
                        terminalOutput.flush();
                        line = terminalInput.readLine();
                        if (line.equals("s".toLowerCase()) || line.equals("S".toUpperCase())) {
                            break;
                        }
                        xTemp = Integer.parseInt(line) - 1;
                        terminalOutput.print("Insert Y coordinate: ");
                        terminalOutput.flush();
                        yTemp = Integer.parseInt(line = terminalInput.readLine()) - 1;
                    } catch (NumberFormatException e) {
                        terminalOutput.println("You have not inserted a number! Retry.");
                        xTemp = -1;
                        yTemp = -1;
                    } catch (IOException e) {
                        terminalOutput.format("Error with I/O: %s%n", e.getMessage());
                        xTemp = -1;
                        yTemp = -1;
                    }
                }
                while (xTemp == -1 || yTemp == -1);
                if (line.equals("s".toLowerCase()) || line.equals("S".toUpperCase())) {
                    break;
                } else {
                    x.add(xTemp);
                    y.add(yTemp);
                }
            }
            while (!(line.equals("s".toLowerCase()) || line.equals("S".toUpperCase())));

            // ask for Shelf coordinates, if move is composed at least of a card
            if (x.size() >= 1 && y.size() >= 1) {
                terminalOutput.println("Insert the column in which you want to insert taken cards.");
                do {
                    terminalOutput.print("Column of your shelf: ");
                    terminalOutput.flush();
                    try {
                        column = Integer.parseInt(terminalInput.readLine()) - 1;
                    } catch (NumberFormatException e) {
                        terminalOutput.println("You have not inserted a number! Retry.");
                        column = -1;
                    } catch (IOException e) {
                        terminalOutput.format("Error with I/O: %s%n", e.getMessage());
                        column = -1;
                    }
                }
                while (column == -1);
            }

            // make move, if composed at least of a card
            if (x.size() >= 1 && y.size() >= 1) {
                mi.setParameters(x, y, column);
                try {
                    if (mi.make()) {
                        terminalOutput.println("Valid move! Loading next turn.");
                    } else terminalOutput.println("Invalid move! Asking for a new move.");
                } catch (Exception e) {
                    terminalOutput.format("Exception during move: %s%n", e.getMessage());
                }
            } else
                terminalOutput.println("Invalid move! Asking for a new move.");
        } else {
            GUI.allowMove.lock();
            mi.setParameters(GUI.getX(), GUI.getY(), GUI.getSelectedColumn());
            if (mi.make()) {
                GUI.resetSelection();
                oa.showInfo("Valid move! Loading next turn");
            } else {
                GUI.resetSelection();
                oa.showErrorConfirm("Invalid move! Asking for a new move");
            }
        }
        return true;
    }

    private boolean notYourTurn() throws Exception {
        if (firstTime && useGUI) {
            GUI.setScene(Scenes.gameScene);
            firstTime = false;
        }
        BoardInterface board = cs.getBoard();
        ShelfInterface shelf = cs.getShelf();
        PersonalGoalCardInterface pgCard = cs.getPersonalGoalCard();
        if (!useGUI) {
            MultipleView.automaticPrint(board, shelf, pgCard, terminalOutput);
            ta.printCGC(cs.getCommonGoalCard().get(0), cs.getCommonGoalCard().get(1));

            terminalOutput.format("Now it's %s's turn. Please wait for their move to complete.%n", cs.getCurrentPlayer());
        } else {
            GUI.updateBoard(board);
            GUI.updateShelf(shelf);
            GUI.updateCommonGoalCards(cs.getCommonGoalCard().get(0), cs.getCommonGoalCard().get(1));
            GUI.updatePersonalGoalCard(pgCard);
        }
        return true;
    }

    /**
     * Notifies players of a forced game end, related to a client unexpected disconnection
     *
     * @return true (ignored)
     */
    private boolean forcedGameEnd() {
        oa.showErrorConfirm(String.format("%nAnother player disconnected, game ended forcefully."));
        System.exit(1);
        return true;
    }

    /**
     * Notifies players of the last turn, related to a client unexpected disconnection
     *
     * @return true (ignored)
     */
    private boolean lastTurn() {
        oa.showInfo("This is the last turn. The game is going to end at the end of this round.");
        if (useGUI) GUI.buildEndGameCard();
        return true;
    }

    /**
     * Shows game's Final Scoreboard
     *
     * @return true (ignored)
     * @throws RemoteException related to RMI
     */
    private boolean finalScoreBoard() throws RemoteException {
        ScoreBoardInterface scoreBoard = cs.getScoreBoard();
        if (!useGUI) {
            terminalOutput.println("The game has ended! This is the final scoreboard: ");
            ScoreBoardView.print(scoreBoard, terminalOutput);
            terminalOutput.println("Thanks for playing to My Shelfie! Have a nice day!");
            System.exit(0);
        } else {
            Map<String, Integer> data = scoreBoard.getScoreBoard();
            GUI.buildScoreBoard(data);
            GUI.setScene(Scenes.scoreboardScene);
            GUI.allowScoreboard.lock();
        }
        return true;
    }

    /**
     * Sends informative achievements update messages
     *
     * @return true (ignored)
     * @throws RemoteException related to RMI
     */
    private boolean achievementsUpdate() throws RemoteException {
        if (cs.getLastTurnScores() != null && !cs.getLastTurnScores().isEmpty()) {
            for (Map.Entry<ScoreCardInterface, CommonGoalCardInterface> e : cs.getLastTurnScores().entrySet()) {
                oa.showInfo(String.format("%s scored %d points for CommonGoalCard number %d",
                        (cs.getCurrentPlayer().equals(cs.getNickname()) ? "You have" : String.format("Player %s has", cs.getCurrentPlayer())),
                        e.getKey().getValue(),
                        cs.getCommonGoalCard()
                                .indexOf(cs.getCommonGoalCard()
                                        .stream()
                                        .filter(c -> {
                                            try {
                                                return c.getType() == e.getValue().getType();
                                            } catch (RemoteException ignored) {
                                            }
                                            return false;
                                        })
                                        .findFirst()
                                        .orElseThrow()) + 1));
            }
        }
        Stack<ScoreCardInterface> stack = cs.getScoreCards();
        if (!useGUI) {
            AtomicInteger points = new AtomicInteger();
            cs.getScoreCards().forEach(c -> {
                try {
                    points.addAndGet(c.getValue());
                } catch (RemoteException ignored) {
                }
            });
            terminalOutput.format("You have made %d points from Common Goal Cards until now.%n", points.get());
        } else {
            ArrayList<Integer> scoreCards = new ArrayList<>();
            for (int i = 0; i < stack.size(); i++) {
                scoreCards.add(stack.pop().getValue());
            }
            GUI.updateCGCScoredPoints(scoreCards);
        }
        return true;
    }
}
