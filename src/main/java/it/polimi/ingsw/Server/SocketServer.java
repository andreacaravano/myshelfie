package it.polimi.ingsw.Server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Common.*;
import it.polimi.ingsw.Server.Controller.GameController;
import it.polimi.ingsw.Server.Controller.GsonMoveDeserializer;
import it.polimi.ingsw.Server.Controller.Move;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.Scoreboard;
import it.polimi.ingsw.Server.Model.Shelf;
import it.polimi.ingsw.Utility.ProducerConsumerLock;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Socket Server, uses Status Messages to inform clients of game's evolution
 */
public class SocketServer {
    private static final int countParameters = 1;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final Map<PrintWriter, GameController> printWriterToGameController = new HashMap<>();
    private static final Map<Socket, GameController> socketToGameController = new HashMap<>();
    private static int portNumber = 3435;
    private static Map<String, GameController> identifierToController;
    private static Map<GameController, Semaphore> controllerToSemaphore;
    private static Map<GameController, Object> controllerToCommuter;
    private static Map<GameController, Object> controllerToLobbyCommuter;
    private static Gson gson_2;
    private final ExclusionStrategy serializationStrategy;

    /**
     * SocketServer constructor, managed by the Server Manager by default
     *
     * @param identifierToController    maps game's identifiers to GameControllers
     * @param controllerToSemaphore     maps GameControllers to synchronization semaphores
     * @param controllerToCommuter      maps GamesController to synchronization commuters
     * @param controllerToLobbyCommuter maps GameController to synchronization lobbyCommuters
     */

    public SocketServer(Map<String, GameController> identifierToController, Map<GameController, Semaphore> controllerToSemaphore, Map<GameController, Object> controllerToCommuter, Map<GameController, Object> controllerToLobbyCommuter) {
        SocketServer.identifierToController = identifierToController;
        SocketServer.controllerToSemaphore = controllerToSemaphore;
        SocketServer.controllerToCommuter = controllerToCommuter;
        SocketServer.controllerToLobbyCommuter = controllerToLobbyCommuter;
        this.serializationStrategy = new ExclusionStrategy() {
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
        gson_2 = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setExclusionStrategies(serializationStrategy)
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();

        try (ServerSocket server = new ServerSocket(portNumber)) {
            ServerManager.writeInfo(String.format("Socket Server correctly launched on %s", server.getLocalSocketAddress().toString()));
            while (true) {
                Socket tempSocket;
                try {
                    // accept new client
                    tempSocket = server.accept();

                    // for every new client, a new Thread is created
                    pool.execute(() -> {
                        Socket client = tempSocket;
                        try (client) {
                            ServerManager.writeInfo(String.format("Client: %s connected to the Server on Thread %d using TCP Socket", client.getRemoteSocketAddress().toString(), Thread.currentThread().getId()));
                            communication(client); // this routine implements effective communication
                        } catch (IOException e) {
                            ServerManager.writeWarning(String.format("Error during communication with client: %s", e.getMessage()));
                        }
                    });
                } catch (IOException e) {
                    ServerManager.writeWarning(String.format("Error in managing new connections: %s", e.getMessage()));
                }
            }
        } catch (IOException e) {
            ServerManager.writeWarning(String.format("Server error: %s", e.getMessage()));
        }
    }

    /**
     * Main method, welcoming Server management console
     *
     * @param args default arguments array
     */
    public static void main(String[] args) {
        try {
            if (args.length == countParameters) {
                portNumber = Integer.parseInt(args[0]);
            }
        } catch (Exception e) {
            ServerManager.writeWarning("Invalid CLI parameter.");
        }
    }

    /**
     * this method manages communication phases between Server and Client (Server side)
     *
     * @param socket socket
     * @throws IOException input/output exception
     */
    private static void communication(Socket socket) throws IOException {
        // in and out streams
        GameController gc = null;
        String nickname = null;
        boolean admittedPlayer = false;
        boolean firstPlayerToJoin = false;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            int numofPlayers = -1;
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                    .registerTypeAdapter(BoardInterface.class, GsonInterfaceSerializer.interfaceSerializer(Board.class))
                    .registerTypeAdapter(ShelfInterface.class, GsonInterfaceSerializer.interfaceSerializer(Shelf.class))
                    .registerTypeAdapter(ScoreBoardInterface.class, GsonInterfaceSerializer.interfaceSerializer(Scoreboard.class))
                    .create();

            //Requests game's identifier
            String identifier = receiveIdentifier(in, out, gson);
            //Checks if game already exists
            boolean containsIdentifier = false;
            synchronized (identifierToController) {
                if (identifierToController.containsKey(identifier))
                    containsIdentifier = true;
            }
            if (containsIdentifier) {
                //game already exists
                synchronized (identifierToController) {
                    gc = identifierToController.get(identifier);
                }
                synchronized (printWriterToGameController) {
                    printWriterToGameController.put(out, gc);
                }
                synchronized (socketToGameController) {
                    socketToGameController.put(socket, gc);
                }
            } else {
                firstPlayerToJoin = true;
                gc = new GameController(identifier);
                synchronized (identifierToController) {
                    identifierToController.put(identifier, gc);
                }
                synchronized (printWriterToGameController) {
                    printWriterToGameController.put(out, gc);
                }
                synchronized (socketToGameController) {
                    socketToGameController.put(socket, gc);
                }

                numofPlayers = requestNumOfPlayer(in, out, gson);
            }

            //Nickname request
            nickname = requestNickname(in, out, identifier, gson);

            //Adds player to the lobby or rejects his/her request
            admittedPlayer = admissionResult(gc, nickname, out, numofPlayers, gson);
            if (!admittedPlayer) {
                socket.close();
                return;
            }

            AtomicBoolean clientsTurn = new AtomicBoolean(false);
            ProducerConsumerLock lineLockers = new ProducerConsumerLock(true, true);
            AtomicReference<String> l = new AtomicReference<>();
            AtomicReference<GameController> localGC = new AtomicReference<>(gc);
            AtomicReference<String> localIdentifier = new AtomicReference<>(identifier);
            AtomicReference<String> localNickname = new AtomicReference<>(nickname);

            AtomicReference<SocketKeepAlive> ka = new AtomicReference<>(new SocketKeepAlive(socket, gc));

            //This thread is used to manage messages sent by a client while it's not its turn to play
            pool.execute(() -> {
                try {
                    readClientLine(in, out, clientsTurn, lineLockers, l, ka);
                } catch (Exception ignored) {
                } finally {
                    if ((localGC.get() != null) && (localGC.get().nicknames() != null && localGC.get().nicknames().contains(localNickname.get()))) {
                        endGameHandler(localGC.get(), false, false);

                        if (!localGC.get().isGameComplete())
                            ServerManager.writeWarning(String.format("It isn't possible to communicate with " +
                                    "client %s anymore. Game %s is terminated", localNickname.get(), localIdentifier.get()));
                    }
                }
            });

            Object commuter;
            synchronized (controllerToCommuter) {
                if (!controllerToCommuter.containsKey(gc))
                    controllerToCommuter.put(gc, commuter = new Object());
                else commuter = controllerToCommuter.get(gc);
            }

            // uses a lobbyCommuter Object for every client: it is used to synchronize client's access to lobby re-enter strategy
            Object lobbyCommuter;
            synchronized (controllerToLobbyCommuter) {
                if (!controllerToLobbyCommuter.containsKey(gc))
                    controllerToLobbyCommuter.put(gc, lobbyCommuter = new Object());
                else lobbyCommuter = controllerToLobbyCommuter.get(gc);
            }

            synchronized (commuter) {
                if (gc.isGamePrepared())
                    commuter.notifyAll();
                else commuter.wait();
            }
            numofPlayers = gc.getMaxPlayers();

            synchronized (lobbyCommuter) {
                if (gc.getLobby().size() == numofPlayers)
                    lobbyCommuter.notifyAll();
                else lobbyCommuter.wait();
            }

            //sending board
            out.println(gson.toJson(new StatusMessage(Status.SendingBoard, gson.toJson(gc.getGameBoard()))));

            //sending shelf
            out.println(gson.toJson(new StatusMessage(Status.SendingShelf, gson.toJson(gc.getShelf(nickname)))));

            //sending PersonalGoalCard
            out.println(gson.toJson(new StatusMessage(Status.SendingPersonalGoalCard, gson.toJson(gc.getPersonalGoalCard(nickname)))));

            //sending CommonGoalCard1
            out.println(gson.toJson(new StatusMessage(Status.SendingCommonGoalCardSpecification, gson.toJson(gc.getCommonGoalCards().get(0)))));

            //sending CommonGoalCard2
            out.println(gson.toJson(new StatusMessage(Status.SendingCommonGoalCardSpecification, gson.toJson(gc.getCommonGoalCards().get(1)))));


            //semaphore to controll access to a single turn(commuter to wait turn's end)
            Semaphore sem;
            synchronized (controllerToSemaphore) {

                if (!controllerToSemaphore.containsKey(gc))
                    controllerToSemaphore.put(gc, sem = new Semaphore(numofPlayers));

                else sem = controllerToSemaphore.get(gc);
            }

            Gson gson_move = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                    .registerTypeAdapter(BoardInterface.class, GsonInterfaceSerializer.interfaceSerializer(Board.class))
                    .registerTypeAdapter(ShelfInterface.class, GsonInterfaceSerializer.interfaceSerializer(Shelf.class))
                    .registerTypeAdapter(Move.class, new GsonMoveDeserializer((Board) gc.getGameBoard(), (Shelf) gc.getShelf(nickname)))
                    .create();

            //boolean firstTurn = true;
            while (!gc.isGameComplete()) {
                sem.acquire(1);
                if (gc.isGameEnded()) { //Last turn: a Player has filled his/her library
                    out.println(gson.toJson(new StatusMessage(Status.LastTurn, "Last turn")));
                    if (gc.getPlayerByNickname(nickname).isInGame()) {
                        out.println(gson.toJson(new StatusMessage(Status.MoveAllowed, "You're allowed to make the last move")));
                    } else {
                        out.println(gson.toJson(new StatusMessage(Status.MoveNotAllowed, "You are not allowed to make another move")));
                    }
                }
                //turn management
                if (gc.getActivePlayer().getNickname().equals(nickname)) {
                    //this variable is used in order not to exit do while loop in case of a wrong move made by the client
                    boolean failedMove = false;
                    ServerManager.writeInfo(String.format("%s - It's %s's turn!", identifier, nickname));
                    out.println(gson.toJson(new StatusMessage(Status.YourTurn, "It's your turn")));
                    makeMove(gc, out, gson, clientsTurn, lineLockers, l, gson_move);

                    try {
                        Files.createDirectories(Paths.get(System.getProperty("user.dir").concat("/games-persistency/")));
                    } catch (IOException e) {
                        ServerManager.writeWarning(String.format("Couldn't create support directory: %s%n", e.getMessage()));
                    }
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir").concat("/games-persistency/game-"
                            .concat(identifier).concat(".json"))))) {

                        bw.write(gson_2.toJson(gc));
                    } catch (Exception e) {
                        ServerManager.writeWarning(String.format("GSON or file management exception: %s%n", e.getMessage()));
                    }

                    sem.release(numofPlayers);
                    // I notify other players that I have completed a move (so a new player may do their move)
                    // (it might also be incorrect, in that case, the same pattern is repeated)
                    synchronized (commuter) {
                        commuter.notifyAll();
                    }
                } else {
                    String activePlayer = gc.getActivePlayer().getNickname();
                    out.println(gson.toJson(new StatusMessage(Status.NotYourTurn, activePlayer)));
                    synchronized (commuter) {
                        commuter.wait();
                    }
                }
                //Board, shelf and Common Goal Cards are sent to every player at the end of each turn
                updateClientStatus(gc, nickname, out, gson);
            }

            if (gc.isGameComplete()) {
                out.println(gson.toJson(new StatusMessage(Status.FinalScoreboard, gson.toJson(gc.getScoreboard()))));
            }

        } catch (UnsupportedEncodingException e) {
            ServerManager.writeWarning(String.format("Character encoding not supported: %s", e.getMessage()));
        } catch (IOException e) {
            ServerManager.writeWarning(String.format("I/O error: It isn't possible to communicate with the client %s anymore", socket.getRemoteSocketAddress()));
            if (gc != null && ((gc.nicknames() != null && gc.nicknames().contains(nickname) && admittedPlayer) || firstPlayerToJoin)) {
                endGameHandler(gc, false, false);
            }
        } catch (Exception e) {
            ServerManager.writeWarning(e.getMessage());
            if (gc != null && ((gc.nicknames() != null && gc.nicknames().contains(nickname) && admittedPlayer) || firstPlayerToJoin)) {
                endGameHandler(gc, false, false);
            }
        }
    }

    /**
     * this method sends Board, Shelf and cards at the end of every turn
     *
     * @param gc       game controller
     * @param nickname player's nickname
     * @param out      PrintWriter
     * @param gson     Gson Builder
     * @throws RemoteException related to RMI
     */
    private static void updateClientStatus(GameController gc, String nickname, PrintWriter out, Gson gson) throws RemoteException {
        out.println(gson.toJson(new StatusMessage(Status.SendingBoard, gson.toJson(gc.getGameBoard()))));
        out.println(gson.toJson(new StatusMessage(Status.SendingShelf, gson.toJson(gc.getShelf(nickname)))));
        out.println(gson.toJson(new StatusMessage(Status.SendingCommonGoalCardSpecification, gson.toJson(gc.getCommonGoalCards().get(0)))));
        out.println(gson.toJson(new StatusMessage(Status.SendingCommonGoalCardSpecification, gson.toJson(gc.getCommonGoalCards().get(1)))));
        if (!gc.getLastTurnScores().isEmpty()) {
            out.println(gson.toJson(new StatusMessage(Status.AchievementUpdate, gson.toJson(gc.getLastTurnScores()))));
        }
        out.println(gson.toJson(new StatusMessage(Status.PlayerScoreCards, gson.toJson(gc.getPlayerByNickname(nickname).getScoreCards()))));
    }

    /**
     * this method manages the move phase
     *
     * @param gc          game controller
     * @param out         print writer
     * @param gson        Gson Builder
     * @param clientsTurn true if it's client turn, false otherwise
     * @param lineLockers ProducerConsumerLock
     * @param l           line read by the thread
     * @param gson_move   Gson builder for move
     */
    private static void makeMove(GameController gc, PrintWriter out, Gson gson, AtomicBoolean clientsTurn, ProducerConsumerLock lineLockers, AtomicReference<String> l, Gson gson_move) {
        boolean failedMove;
        String line;
        do {
            clientsTurn.set(true);
            do {
                lineLockers.writeLock().lock(); // wait until the other thread receives a message
                line = l.get();
                if (!StatusMessage.isValid(line) ||
                        !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Move_Request))) {
                    out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
                }
                lineLockers.readLock().unlock(); // releases client-side communication mutex
            } while ((!StatusMessage.isValid(line)) || !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Move_Request)));
            clientsTurn.set(false);//changed
            Move m = gson_move.fromJson(gson.fromJson(line, StatusMessage.class).getMessage(), Move.class);
            try {
                if (gc.make(m)) {
                    out.println(gson.toJson(new StatusMessage(Status.SuccessfulMove)));
                    failedMove = false;
                } else {
                    out.println(gson.toJson(new StatusMessage(Status.FailedMove)));
                    failedMove = true;
                }
            } catch (Exception e) {
                out.println(gson.toJson(new StatusMessage(Status.Error, "Fatal error. Check move parameters")));
                failedMove = true;
            }
        } while (failedMove);
    }

    /**
     * this method manages admission phase
     *
     * @param gc           game controller
     * @param nickname     player's nickname
     * @param out          printWriter
     * @param numofPlayers number of players
     * @param gson         gson builder
     * @return true if player has been accepted, false otherwise
     */
    private static boolean admissionResult(GameController gc, String nickname, PrintWriter out, int numofPlayers, Gson gson) {
        try {
            if ((gc.nicknames() == null) || !(gc.nicknames().contains(nickname))) {
                if (gc.acceptPlayer(nickname, numofPlayers)) {
                    out.println(gson.toJson(new StatusMessage(Status.Accepted_Request, "Accepted")));
                    return true;
                } else {
                    out.println(gson.toJson(new StatusMessage(Status.Error, "Lobby's already full.")));
                    return false;
                }
            } else {
                if (gc.reEnterGame(nickname)) {
                    out.println(gson.toJson(new StatusMessage(Status.Accepted_Request, "Accepted request to re-enter the game")));
                    return true;
                } else {
                    out.println(gson.toJson(new StatusMessage(Status.Error, "You are not allowed to re-enter the game.")));
                    return false;
                }
            }
        } catch (Exception e) {
            out.println(gson.toJson(new StatusMessage(Status.Error, "Fatal error")));
            return false;
        }
    }

    /**
     * this method receives the number of player by the first player
     *
     * @param in   bufferedreader
     * @param out  printwriter
     * @param gson gson builder
     * @return int number of players chosen by the first player
     * @throws IOException input/output exception
     */
    private static int requestNumOfPlayer(BufferedReader in, PrintWriter out, Gson gson) throws IOException {
        int numofPlayers;
        String line;
        do {
            //Requests num of players, only when a new game is created
            out.println(gson.toJson(new StatusMessage(Status.Request_NumberOfPlayers)));
            do {
                if (!StatusMessage.isValid(line = in.readLine()) ||
                        !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Response_NumberOfPlayers))) {
                    out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
                }
            } while (!out.checkError() && ((!StatusMessage.isValid(line)) || !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Response_NumberOfPlayers))));
            if (out.checkError()) {
                throw new IOException("I/O error: It isn't possible to communicate with the client anymore");
            }
            try {
                numofPlayers = Integer.parseInt(gson.fromJson(line, StatusMessage.class).getMessage());
                if (numofPlayers < 2 || numofPlayers > 4) { //checks input
                    numofPlayers = -1;
                    out.println(gson.toJson(new StatusMessage(Status.Denied_Request, "Invalid number of players")));
                }
            } catch (NumberFormatException e) { //invalid format
                numofPlayers = -1;
                out.println(gson.toJson(new StatusMessage(Status.Error, "Invalid input format")));
            }
        } while (numofPlayers == -1);
        return numofPlayers;
    }

    /**
     * this method receives the identifier
     *
     * @param in   buffered reader
     * @param out  printwriter
     * @param gson gson builder
     * @return game identifier
     * @throws IOException input/output exception
     */
    private static String receiveIdentifier(BufferedReader in, PrintWriter out, Gson gson) throws IOException {
        String line;
        String identifier;
        do {
            do {
                if (!StatusMessage.isValid(line = in.readLine()) ||
                        !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Sending_Identifier))) {
                    out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
                }
            } while (!out.checkError() && ((!StatusMessage.isValid(line)) || !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Sending_Identifier))));
            if (out.checkError()) {
                throw new IOException("I/O error: It isn't possible to communicate with the client anymore");
            }
            if (line != null) {
                identifier = gson.fromJson(line, StatusMessage.class).getMessage();
            } else {
                identifier = null;
            }
            if (identifier == null || !identifier.matches("^[a-zA-Z0-9_]+$") || identifier.length() < 3) {
                out.println(gson.toJson(new StatusMessage(Status.Denied_Request, "Invalid identifier format")));
            }
        } while (identifier == null || !identifier.matches("^[a-zA-Z0-9_]+$") || identifier.length() < 3);
        return identifier;
    }

    /**
     * Socket thread listener
     *
     * @param in          bufferedreader
     * @param out         printwriter
     * @param clientsTurn true if it's clientsTurn, false otherwise
     * @param lineLockers ProducerConsumerLock
     * @param l           line read by the thread
     * @throws IOException input/output exception
     */
    private static void readClientLine(BufferedReader in, PrintWriter out, AtomicBoolean clientsTurn,
                                       ProducerConsumerLock lineLockers, AtomicReference<String> l, AtomicReference<SocketKeepAlive> ka) throws IOException {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .registerTypeAdapter(BoardInterface.class, GsonInterfaceSerializer.interfaceSerializer(Board.class))
                .registerTypeAdapter(ShelfInterface.class, GsonInterfaceSerializer.interfaceSerializer(Shelf.class))
                .create();
        String line;
        do {
            line = in.readLine();

            if (StatusMessage.isValid(line) && gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.KeepAlive)) {
                ka.get().received();
            } else {
                if (StatusMessage.isValid(line)) {
                    if (clientsTurn.get()) {
                        l.set(line);
                        lineLockers.writeLock().unlock();

                        lineLockers.readLock().lock();
                    } else if (!clientsTurn.get()) {
                        if (line != null && !clientsTurn.get()) {
                            out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
                        }
                    }
                } else
                    out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
            }
        } while (line != null && !out.checkError() && !ka.get().closed());
    }

    /**
     * this method receives the nickname chosen by the player
     *
     * @param in         bufferedReader
     * @param out        printwriter
     * @param identifier identifier
     * @param gson       gsonBuilder
     * @return player's nickname
     * @throws IOException input/output exception
     */
    public static String requestNickname(BufferedReader in, PrintWriter out, String identifier, Gson gson) throws IOException {
        String line;
        String nickname;
        do {
            do {
                out.println(gson.toJson(new StatusMessage(Status.Request_Nickname)));
                if (!StatusMessage.isValid(line = in.readLine()) ||
                        !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Response_Nickname))) {
                    out.println(gson.toJson(new StatusMessage(Status.InvalidStatus)));
                }
            } while (!out.checkError() && ((!StatusMessage.isValid(line)) || !(gson.fromJson(line, StatusMessage.class).getStatus().equals(Status.Response_Nickname))));
            if (out.checkError()) {
                throw new IOException("I/O error: It isn't possible to communicate with the client anymore");
            }
            nickname = gson.fromJson(line, StatusMessage.class).getMessage();
            ServerManager.writeInfo(String.format("%s joined %s game using Socket Client", nickname, identifier));
            //checks nickname format and if it's already in use
            if (nickname == null || !nickname.matches("^[a-zA-Z0-9_]+$") || nickname.length() < 3) {
                out.println(gson.toJson(new StatusMessage(Status.Denied_Request, "Invalid nickname format")));
//                } else if ((identifierToController.get(identifier).nicknames() != null) && (identifierToController.get(identifier).nicknames().contains(nickname))) {
                // out.println(gson.toJson(new StatusMessage(Status.Denied_Request, "Nickname is already in use")));
            }
        } while (nickname == null || !nickname.matches("^[a-zA-Z0-9_]+$") || nickname.length() < 3);
        return nickname;
    }

    /**
     * Implements End Game strategy, informing all clients (Socket and RMI ones) to disconnect ad deallocates Model Objects from global structures
     *
     * @param gc                 Game's Controller
     * @param invokedbyRMI       to avoid Server callbacks
     * @param invokedAtKATimeout selects the correct Status Message to send to the client
     */
    public static void endGameHandler(GameController gc, boolean invokedbyRMI, boolean invokedAtKATimeout) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();
        Set<PrintWriter> keySet;
        Set<Socket> socketsKeySet;
        synchronized (printWriterToGameController) {
            keySet = new HashSet<>(printWriterToGameController.keySet());
        }
        synchronized (socketToGameController) {
            socketsKeySet = new HashSet<>(socketToGameController.keySet());
        }
        for (PrintWriter pw : keySet) {
            if (keySet.contains(pw) && printWriterToGameController.containsKey(pw) && printWriterToGameController.get(pw).equals(gc)) {
                if (!gc.isGameComplete())
                    if (invokedAtKATimeout)
                        pw.println(gson.toJson(new StatusMessage(Status.KATimeout, "KeepAlive timeout reached, please check your client's settings.")));
                    else
                        pw.println(gson.toJson(new StatusMessage(Status.ForcedGameEnd, "Another player disconnected, game ended forcefully.")));
                synchronized (printWriterToGameController) {
                    printWriterToGameController.remove(pw);
                }
            }
        }
        for (Socket s : socketsKeySet) {
            if (socketsKeySet.contains(s) && socketToGameController.containsKey(s) && socketToGameController.get(s).equals(gc)) {
                try {
                    s.close();
                } catch (IOException ignored) {
                }
                synchronized (socketToGameController) {
                    socketToGameController.remove(s);
                }
            }
        }
        synchronized (identifierToController) {
            identifierToController.remove(gc.getIdentifier());
        }
        synchronized (controllerToCommuter) {
            controllerToCommuter.remove(gc);
        }
        synchronized (controllerToSemaphore) {
            controllerToSemaphore.remove(gc);
        }
        if (!invokedbyRMI) {
            RMIServer.endGameHandler(gc, true);
        }
        if (new File(System.getProperty("user.dir").concat("/games-persistency/").concat("game-").concat(gc.getIdentifier()).concat(".json")).delete()) {
            ServerManager.writeWarning(String.format("%s's game file has been correctly deleted.", gc.getIdentifier()));
        }
    }
}

