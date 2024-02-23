package it.polimi.ingsw.Client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Client.ClientModel.*;
import it.polimi.ingsw.Client.View.MultipleView;
import it.polimi.ingsw.Client.View.OutputAbstraction;
import it.polimi.ingsw.Client.View.ScoreBoardView;
import it.polimi.ingsw.Client.View.TerminalAbstraction;
import it.polimi.ingsw.Common.*;
import it.polimi.ingsw.Utility.ProducerConsumerLock;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Socket Client abstraction, attached to terminal and GUI views
 *
 * @author Gruppo AM34
 */
public class SocketClient {
    private static final int KA_FREQUENCY = 5; // KeepAlive frequency in seconds
    private static final int KA_MAXCOUNT = 6;
    private static final int countParameters = 2 + 1; // port and server name + GUI selection
    private static final PrintWriter terminalOutput = new PrintWriter(System.out, true);
    private static final BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private static final TerminalAbstraction ta = new TerminalAbstraction(terminalInput, terminalOutput);
    private static final ExecutorService executors = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(1);
    private static String serverName = "localhost";
    private static int portNumber = 3435;
    private static boolean useGUI;
    private static boolean firstTime = true;
    private static OutputAbstraction oa;

    /**
     * Main method, welcoming Server management console
     *
     * @param args default arguments array
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0)
                useGUI = true; // default abstraction is GUI
            if (args.length == countParameters) {
                useGUI = Boolean.parseBoolean(args[0]);
                portNumber = Integer.parseInt(args[1]);
                serverName = args[2];
            } else if (args.length == 1 + 1) {
                useGUI = Boolean.parseBoolean(args[0]);
                portNumber = Integer.parseInt(args[1]);
            } else if (args.length == 1) {
                useGUI = Boolean.parseBoolean(args[0]);
            }
            oa = new OutputAbstraction(useGUI, terminalOutput);
        } catch (Exception e) {
            terminalOutput.println("Invalid CLI parameter.");
        }

        terminalOutput.format("Your client is running Java %s in %s%n%n", System.getProperty("java.version"), System.getProperty("user.dir"));
        terminalOutput.println("Welcome to My Shelfie! (Socket Client component)");
        terminalOutput.println("Made in Academic Year 2022/23 by AM34 group");

        try (Socket socket = new Socket(serverName, portNumber)) {
            if (useGUI) executors.execute(() -> GUI.main(args));
            terminalOutput.format("Connected to server: %s%n", socket.getRemoteSocketAddress().toString());
            communication(socket); // this routine implements effective communication
        } catch (UnknownHostException e) {
            terminalOutput.format("Invalid server name: %s%n", e.getMessage());
        } catch (IOException e) {
            terminalOutput.format("Error during server communication: %s%n", e.getMessage());
        }
    }

    /**
     * this method manages communication phases between Server and Client (Client side)
     *
     * @param socket socket
     * @throws IOException input/output exception
     */
    private static void communication(Socket socket) throws IOException {
        // in and out streams
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            // JSON Parser
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                    .excludeFieldsWithoutExposeAnnotation()
                    .enableComplexMapKeySerialization()
                    .create();
            String line;
            boolean keepReading = false;


            if (useGUI) {
                GUI.allowExitMenu.lock();
                GUI.allowIdentifier.lock();
            }

            login(in, out);

            ProducerConsumerLock lineLockers = new ProducerConsumerLock(true, true);
            AtomicReference<String> lineBuffer = new AtomicReference<>();

            AtomicInteger KACounter = new AtomicInteger(0);
            scheduledExecutors.scheduleAtFixedRate(() -> {
                        out.println(gson.toJson(new StatusMessage(Status.KeepAlive)));
                        if (out.checkError())
                            KACounter.addAndGet(1);
                        else
                            KACounter.set(0);
                        if (KACounter.get() == KA_MAXCOUNT) {
                            oa.showErrorConfirm("Couldn't reach server recently. Terminating game.");
                            System.exit(1);
                        }
                    },
                    0, KA_FREQUENCY, TimeUnit.SECONDS);

            executors.execute(() -> {
                try {
                    readServerLine(in, lineLockers, lineBuffer);
                } catch (IOException | InterruptedException e) {
                    oa.showErrorConfirm(String.format("I/O error: %s. Couldn't reach server. Terminating game.%n", e.getMessage()));
                    System.exit(1);
                }
            });

            ClientBoard b;
            ClientShelf s;
            ClientPersonalGoalCard pgc;
            ClientCommonGoalCard c1;
            ClientCommonGoalCard c2;
            AtomicInteger atomicPoints = new AtomicInteger(0);
            int points;

            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingBoard)) {
                b = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientBoard.class);
            } else {
                b = null;
            }

            lineLockers.readLock().unlock(); // releases server-side reading mutex


            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingShelf)) {
                s = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientShelf.class);
            } else {
                s = null;
            }

            lineLockers.readLock().unlock(); // releases server-side reading mutex


            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingPersonalGoalCard)) {
                pgc = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientPersonalGoalCard.class);
            } else {
                pgc = null;
            }
            //Prints shelf,board and personal goal card
            if (s != null && b != null && pgc != null) {
                if (useGUI) {
                    GUI.updateShelf(s);
                    GUI.updateBoard(b);
                    GUI.updatePersonalGoalCard(pgc);
                } else MultipleView.automaticPrint(b, s, pgc, terminalOutput);
            }

            lineLockers.readLock().unlock(); // releases server-side reading mutex


            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingCommonGoalCardSpecification)) {
                c1 = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientCommonGoalCard.class);
            } else {
                c1 = null;
            }
            lineLockers.readLock().unlock(); // releases server-side reading mutex

            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingCommonGoalCardSpecification)) {
                c2 = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientCommonGoalCard.class);
            } else {
                c2 = null;
            }

            if (useGUI) {
                GUI.updateCommonGoalCards(c1, c2);
            } else ta.printCGC(c1, c2);

            lineLockers.readLock().unlock(); // releases server-side reading mutex

            if (useGUI && firstTime) {
                GUI.setScene(Scenes.gameScene);
                firstTime = false;
            }

            boolean clientsTurn = false;
            String currentPlayer = null;
            AtomicReference<String> atomicCurrentPlayer = new AtomicReference<>();

            clientsTurn = turnManagement(lineLockers, lineBuffer, atomicCurrentPlayer, c1, c2, clientsTurn, atomicPoints);
            currentPlayer = atomicCurrentPlayer.get();

            do {
                if (clientsTurn) {
                    if (!useGUI) terminalOutput.println("It's your turn!");
                    makeMove(out, lineLockers, lineBuffer);
                    lineLockers.writeLock().lock(); // wait until the other thread receives a message
                    line = lineBuffer.get();
                    if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingBoard)) {
                        b = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientBoard.class);
                    }
                    lineLockers.readLock().unlock(); // releases server-side reading mutex

                } else {
                    // String activePlayer = gson.fromJson(line, StatusMessage.class).getMessage();
                    if (!useGUI) terminalOutput.format("It's %s's turn!%n", currentPlayer);
                    do {
                        lineLockers.writeLock().lock(); // wait until the other thread receives a message
                        line = lineBuffer.get();
                        if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingBoard)) {
                            b = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientBoard.class);
                        }
                        lineLockers.readLock().unlock(); // releases server-side reading mutex

                    } while (!(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingBoard)));
                }

                lineLockers.writeLock().lock(); // wait until the other thread receives a message
                line = lineBuffer.get();

                if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingShelf)) {
                    s = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientShelf.class);
                }
                lineLockers.readLock().unlock();// releases server-side reading mutex

                lineLockers.writeLock().lock(); // wait until the other thread receives a message
                line = lineBuffer.get();
                if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingCommonGoalCardSpecification)) {
                    c1 = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientCommonGoalCard.class);
                }
                lineLockers.readLock().unlock(); // releases server-side reading mutex

                lineLockers.writeLock().lock(); // wait until the other thread receives a message
                line = lineBuffer.get();
                if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SendingCommonGoalCardSpecification)) {
                    c2 = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientCommonGoalCard.class);
                }
                lineLockers.readLock().unlock(); // releases server-side reading mutex

                //Prints updated Board and Shelf + pgc and cg
                if (!useGUI) {
                    MultipleView.automaticPrint(b, s, pgc, terminalOutput);
                    ta.printCGC(c1, c2);
                } else {
                    GUI.updateBoard(b);
                    GUI.updateShelf(s);
                    GUI.updatePersonalGoalCard(pgc);
                    GUI.updateCommonGoalCards(c1, c2);
                }
                clientsTurn = turnManagement(lineLockers, lineBuffer, atomicCurrentPlayer, c1, c2, clientsTurn, atomicPoints);
                if (!useGUI) {
                    points = atomicPoints.get();
                    terminalOutput.format("You have achieved %d points from Common Goal Cards until now.%n", points);
                }
                currentPlayer = atomicCurrentPlayer.get();


            } while (!(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.FinalScoreboard)) &&
                    !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.ForcedGameEnd)));

            System.exit(0);
        } catch (Exception e) {
            oa.showError("%s" + e.getMessage());
        }
    }

    /**
     * Socket thread listener
     *
     * @param in          bufferedReader
     * @param lineLockers ProducerConsumerLock
     * @param lineBuffer  line read by the thread
     * @throws IOException          input/output exception
     * @throws InterruptedException related to RMI
     */
    private static void readServerLine(BufferedReader in, ProducerConsumerLock lineLockers, AtomicReference<String> lineBuffer) throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .registerTypeAdapter(BoardInterface.class, GsonInterfaceSerializer.interfaceSerializer(ClientBoard.class))
                .registerTypeAdapter(ShelfInterface.class, GsonInterfaceSerializer.interfaceSerializer(ClientShelf.class))
                .create();
        String line;
        while (true) {
            boolean keepReading = false;
            do {
                keepReading = false;
                line = in.readLine();
                if (!StatusMessage.isValid(line)) {
                    if (!in.ready()) {
                        oa.showErrorConfirm(String.format("%nConnection error, game ended forcefully."));
                        // game is ended or Server closed connection
                        System.exit(0);
                    }
                    keepReading = true;
                    terminalOutput.println("Something went wrong!");
                } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Denied_Request)) {
                    terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                    keepReading = true;
                } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.InvalidStatus)) {
                    keepReading = true;
                } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Accepted_Request)) {
                    keepReading = true;
                }
            } while (keepReading);

            if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.ForcedGameEnd))) {
                oa.showErrorConfirm(String.format("%nAnother player disconnected, game ended forcefully."));
                System.exit(1);
                return;
            } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.KATimeout))) {
                oa.showErrorConfirm(String.format("%nKeepAlive timeout reached, please check your client's settings."));
                System.exit(1);
                return;
            }
            if (lineBuffer != null) {
                lineBuffer.set(line);
                lineLockers.writeLock().unlock();
                lineLockers.readLock().lock();
            }
        }
    }

    /**
     * this method manages login phase
     *
     * @param in  BufferedReader
     * @param out PrintWriter
     * @throws IOException input/output exception
     */
    public static void login(BufferedReader in, PrintWriter out) throws IOException {
        String line = null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .excludeFieldsWithoutExposeAnnotation()
                .enableComplexMapKeySerialization()
                .create();
        if (!useGUI) {
            terminalOutput.println("Note that both the game identifier and your nickname must be an alphanumeric string, of 3 or more letters");
            terminalOutput.flush();
        }
        boolean keepReading = false;
        if (!useGUI) {
            do {
                keepReading = false;
                do {
                    terminalOutput.print("Please, insert the game identifier: ");
                    terminalOutput.flush();
                    line = terminalInput.readLine();
                } while (line == null || !line.matches("^[a-zA-Z0-9_]+$") || line.length() < 3); //Check if needed
                out.println(gson.toJson(new StatusMessage(Status.Sending_Identifier, line)));
                if (!StatusMessage.isValid(line = in.readLine())) {
                    keepReading = true;
                } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Denied_Request)) {
                    terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                    keepReading = true;
                } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.InvalidStatus))) {
                    keepReading = true;
                }
            } while (keepReading);
        } else {
            out.println(gson.toJson(new StatusMessage(Status.Sending_Identifier, GUI.getGameID())));
            if (!StatusMessage.isValid(line = in.readLine())) {
                terminalOutput.println("Invalid message format");
                System.exit(1);
            }
        }
        //If the player is creating a new game, she/he's asked to insert the number of players
        if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Request_NumberOfPlayers))) {
            if (!useGUI) {
                terminalOutput.println("You can play with up to 4 players in a game.");
                terminalOutput.println("You are creating a new game! ");
                terminalOutput.flush();
                do {
                    keepReading = false;
                    //This if is necessary in case of a multiple iteration of do-while
                    if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Request_NumberOfPlayers))) {
                        terminalOutput.print("Insert the number of players you want to play with: ");
                        terminalOutput.flush();
                    }
                    line = terminalInput.readLine();
                    out.println(gson.toJson(new StatusMessage(Status.Response_NumberOfPlayers, line)));
                    if (!StatusMessage.isValid(line = in.readLine())) {
                        keepReading = true;
                    } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Denied_Request)) {
                        terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                        keepReading = true;
                        line = in.readLine();
                    } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.InvalidStatus))) {
                        keepReading = true;
                        line = in.readLine();
                    } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Error))) {
                        terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                        keepReading = true;
                        line = in.readLine();
                    }
                } while (keepReading);
            } else {
                GUI.setNewMatch(true);
                GUI.allowLogin.lock();
                out.println(gson.toJson(new StatusMessage(Status.Response_NumberOfPlayers, GUI.getNumOfPlayers())));
                if (!StatusMessage.isValid(line = in.readLine())) {
                    terminalOutput.println("Invalid message format");
                    System.exit(1);
                } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Error))) {
                    terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                    System.exit(1);
                }
            }
        }

        if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Request_Nickname))) {
            if (!useGUI) {
                do {
                    //This "if" is necessary in case of a multiple iteration of do-while
                    if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Request_Nickname))) {
                        terminalOutput.print("Please, insert your nickname: ");
                    }
                    terminalOutput.flush();
                    keepReading = false;
                    line = terminalInput.readLine();
                    out.println(gson.toJson(new StatusMessage(Status.Response_Nickname, line)));
                    line = in.readLine();
                    if (!StatusMessage.isValid(line)) {
                        keepReading = true;
                    } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Denied_Request)) {
                        terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
                        keepReading = true;
                        line = in.readLine();
                    } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.InvalidStatus))) {
                        keepReading = true;
                        line = in.readLine();
                    }
                } while (keepReading);
            } else {
                GUI.allowLogin.lock();
                GUI.setNicknameText(GUI.getNickname());
                out.println(gson.toJson(new StatusMessage(Status.Response_Nickname, GUI.getNickname())));
                if (!StatusMessage.isValid(line = in.readLine())) {
                    terminalOutput.println("Invalid message format");
                    System.exit(1);
                }
            }
        }

        if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Accepted_Request)) {
            if (!useGUI) {
                terminalOutput.println("Waiting for the game to start.");
                terminalOutput.flush();
            } else {
                GUI.setScene(Scenes.loadingScene);
            }
        } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Error)) {
            terminalOutput.println(gson.fromJson(line, StatusMessage.class).getMessage());
            terminalOutput.println("Could not enter the game.");
            System.exit(1);
        }
    }

    /**
     * this method manages the turn evolution
     *
     * @param lineLockers   ProducerConsumerLock
     * @param lineBuffer    line read by the thread
     * @param currentPlayer currentPlayer
     * @param c1            CommonGoalCard1
     * @param c2            CommonGoalCard2
     * @param clientsTurn   true if it's clientsTurn, false otherwise
     * @param points        scores achieved by the player (CommonGoal)
     * @return true if the client will play the next turn
     * @throws RemoteException related to RMI
     */
    public static boolean turnManagement(ProducerConsumerLock lineLockers, AtomicReference<String> lineBuffer,
                                         AtomicReference<String> currentPlayer, ClientCommonGoalCard c1, ClientCommonGoalCard c2,
                                         boolean clientsTurn, AtomicInteger points) throws RemoteException {
        String line;
        boolean newClientsTurn = false;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .excludeFieldsWithoutExposeAnnotation()
                .enableComplexMapKeySerialization()
                .create();
        Status status;
        ClientScoreboard sb = null;
        Map<ClientScoreCard, ClientCommonGoalCard> cg_achievements = new HashMap<>();
        do {
            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();
            status = (gson.fromJson(line, StatusMessage.class).getStatus());
            switch (status) {
                case AchievementUpdate -> {
                    cg_achievements = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()),
                            new TypeToken<Map<ClientScoreCard, ClientCommonGoalCard>>() {
                            }.getType());
                    if (clientsTurn) {
                        for (ClientScoreCard sc : cg_achievements.keySet()) {
                            if (cg_achievements.get(sc).getType() == c1.getType()) {
                                //terminalOutput.printf("You have scored %d points for CommonGoalCard number 1%n", sc.getValue());
                                //terminalOutput.flush();
                                oa.showInfo("You have scored " + sc.getValue() + " points for CommonGoalCard number 1");
                            } else if (cg_achievements.get(sc).getType() == c2.getType()) {
                                //terminalOutput.printf("You have scored %d points for CommonGoalCard number 2%n", sc.getValue());
                                //terminalOutput.flush();
                                oa.showInfo("You have scored " + sc.getValue() + " points for CommonGoalCard number 1");
                            }
                        }
                    } else {
                        for (ClientScoreCard sc : cg_achievements.keySet()) {
                            if (cg_achievements.get(sc).getType() == c1.getType()) {
                                //terminalOutput.printf("Player %s has scored %d points for CommonGoalCard number 1%n", currentPlayer, sc.getValue());
                                //terminalOutput.flush();
                                oa.showInfo("Player " + currentPlayer + " has scored " + sc.getValue() + " points for CommonGoalCard number 1");
                            } else if (cg_achievements.get(sc).getType() == c2.getType()) {
                                //terminalOutput.printf("Player %s has scored %d points for CommonGoalCard number 2%n", currentPlayer, sc.getValue());
                                //terminalOutput.flush();
                                oa.showInfo("Player " + currentPlayer + " has scored " + sc.getValue() + " points for CommonGoalCard number 2");
                            }
                        }
                    }
                }
                case LastTurn -> {
                    //terminalOutput.println("This is the last turn. The game is going to end at the end of this round.");
                    oa.showInfo("This is the last turn. The game is going to end at the end of this round.");
                    if (useGUI) GUI.buildEndGameCard();
                }
                case MoveAllowed -> {
                    //terminalOutput.println("You will be allowed to make another move before the end of the game");
                    oa.showInfo("You will be allowed to make another move before the end of the game");
                }
                case MoveNotAllowed -> {
                    //terminalOutput.println("You won't be allowed to make another move before the end of the game");
                    oa.showInfo("You won't be allowed to make another move before the end of the game");
                }
                case YourTurn -> {
                    if (useGUI) GUI.setMyTurn(true);
                    newClientsTurn = true;
                }
                case NotYourTurn -> {
                    if (useGUI) GUI.setMyTurn(false);
                    newClientsTurn = false;
                    currentPlayer.set(gson.fromJson(line, StatusMessage.class).getMessage());
                }
                case FinalScoreboard -> {
                    sb = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()), ClientScoreboard.class);
                    if (!useGUI) {
                        ScoreBoardView.print(sb, terminalOutput);
                        terminalOutput.println("Thanks for playing to My Shelfie! Have a nice day!");
                    } else {
                        Map<String, Integer> data = sb.getScoreBoard();
                        GUI.buildScoreBoard(data);
                        GUI.setScene(Scenes.scoreboardScene);
                        GUI.allowScoreboard.lock();
                    }
                    System.exit(0);
                }
                case PlayerScoreCards -> {
                    Stack<ClientScoreCard> playerScoreCards = gson.fromJson((gson.fromJson(line, StatusMessage.class).getMessage()),
                            new TypeToken<Stack<ClientScoreCard>>() {
                            }.getType());
                    if (!useGUI) {
                        points.set(0);
                        playerScoreCards.forEach(c ->
                                points.addAndGet(c.getValue()));
                    } else {
                        ArrayList<Integer> scoreCards = new ArrayList<>();
                        for (int i = 0; i < playerScoreCards.size(); i++) {
                            scoreCards.add(playerScoreCards.pop().getValue());
                        }
                        GUI.updateCGCScoredPoints(scoreCards);
                    }
                }
            }


            lineLockers.readLock().unlock(); // releases server-side reading mutex

        } while (!status.equals(Status.YourTurn) && !status.equals(Status.NotYourTurn) && !status.equals(Status.FinalScoreboard));

        return newClientsTurn;
    }

    /**
     * this method manages move phase
     *
     * @param out         printwriter
     * @param lineLockers ProducerConsumerLock
     * @param lineBuffer  line read by the thread
     */
    public static void makeMove(PrintWriter out, ProducerConsumerLock lineLockers, AtomicReference<String> lineBuffer) {
        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();

        int xTemp = -1;
        int yTemp = -1;

        int column = -1;
        String line = null;
        ClientMove cm;
        boolean keepReading = false;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .excludeFieldsWithoutExposeAnnotation()
                .enableComplexMapKeySerialization()
                .create();
        do {
            if (!useGUI) {
                do {
                    do {
                        try {
                            terminalOutput.println("When you want to stop, insert S here.");
                            terminalOutput.print("Insert X coordinate: ");
                            terminalOutput.flush();
                            line = terminalInput.readLine();
                            if (line.equals("s".toLowerCase()) || line.equals("s".toUpperCase())) {
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

                    } while (xTemp == -1 || yTemp == -1);
                    if (line.equals("s".toLowerCase()) || line.equals("s".toUpperCase())) {
                        break;
                    } else {
                        x.add(xTemp);
                        y.add(yTemp);
                    }

                } while (!(line.equals("s".toLowerCase()) || line.equals("S".toUpperCase())));

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
                cm = new ClientMove(column, x, y);
            } else {
                GUI.allowMove.lock();
                cm = new ClientMove(GUI.getSelectedColumn(), GUI.getX(), GUI.getY());
                GUI.resetSelection();
            }

            out.println(gson.toJson(new StatusMessage(Status.Move_Request, gson.toJson(cm))));
            x.clear();
            y.clear();

            lineLockers.writeLock().lock(); // wait until the other thread receives a message
            line = lineBuffer.get();

            if (!StatusMessage.isValid(line)) {
                keepReading = true;
                oa.showError("Something went wrong!");
            } else if (gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.FailedMove)) {
                oa.showError("Invalid move! Asking for a new move.");
                keepReading = true;
            } else if ((gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.SuccessfulMove))) {
                keepReading = false;
                oa.showInfo("Valid move! Loading next turn.");
            }
            lineLockers.readLock().unlock(); // releases server-side reading mutex
        } while (keepReading);
    }
}
