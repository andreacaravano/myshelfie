package it.polimi.ingsw.Server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Common.ClientStatusInterface;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import it.polimi.ingsw.Common.ScoreCardInterface;
import it.polimi.ingsw.Common.Status;
import it.polimi.ingsw.Server.Controller.GameController;
import it.polimi.ingsw.Server.Controller.RMIController;
import it.polimi.ingsw.Server.Controller.StatusIntermediate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * RMI Server, uses Status callbacks to inform clients of game's evolution
 *
 * @author Gruppo AM34
 */

public class RMIServer {
    private static final int countParameters = 2; // Server port and RMI Hostname
    private static final ExecutorService executors = Executors.newCachedThreadPool();
    private static int PORT = 3434; // default value
    private static Map<String, GameController> identifierToController;
    private static Map<GameController, Semaphore> controllerToSemaphore;
    private static Map<GameController, Object> controllerToCommuter;
    private static Map<GameController, Object> controllerToLobbyCommuter;
    private static Map<ClientStatusInterface, GameController> clientStatusToController;
    private final ExclusionStrategy serializationStrategy;
    private final Gson gson;
    private StatusIntermediate intermediate = null;
    private RMIController serverController = null;

    /**
     * RMIServer constructor, managed by the Server Manager by default
     *
     * @param identifierToController    maps game's identifiers to GameControllers
     * @param controllerToSemaphore     maps GameControllers to synchronization semaphores
     * @param controllerToCommuter      maps GamesController to synchronization commuters
     * @param controllerToLobbyCommuter maps GameController to synchronization lobbyCommuters
     */
    public RMIServer(Map<String, GameController> identifierToController, Map<GameController, Semaphore> controllerToSemaphore, Map<GameController, Object> controllerToCommuter, Map<GameController, Object> controllerToLobbyCommuter) {
        RMIServer.identifierToController = identifierToController;
        RMIServer.controllerToSemaphore = controllerToSemaphore;
        RMIServer.controllerToCommuter = controllerToCommuter;
        RMIServer.controllerToLobbyCommuter = controllerToLobbyCommuter;
        RMIServer.clientStatusToController = new HashMap<>();

        // Gson Exclusion Strategy
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
        this.gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setExclusionStrategies(serializationStrategy)
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();

        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            ServerManager.writeInfo(String.format("RMI Server correctly launched on %s:%d", System.getProperty("java.rmi.server.hostname") == null ? "localhost" : System.getProperty("java.rmi.server.hostname"), PORT));
            intermediate = new StatusIntermediate(this, clientStatusToController);
            registry.rebind("intermediate", intermediate);
            serverController = new RMIController(identifierToController, clientStatusToController);
            registry.rebind("controller", serverController);
        } catch (RemoteException e) {
            ServerManager.writeWarning(String.format("RMI Exception: %s%n", e.getMessage()));
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
                PORT = Integer.parseInt(args[0]);
                System.setProperty("java.rmi.server.hostname", args[1]);
            } else if (args.length == 1) {
                PORT = Integer.parseInt(args[0]);
            }
        } catch (Exception e) {
            ServerManager.writeWarning("Invalid CLI parameter.");
        }
    }

    /**
     * Sets Final Scoreboard and informs client of the availability
     *
     * @param csi        Client Status Interface, remote interface of client's implementation of the Status Object
     * @param controller Game's Controller
     * @throws RemoteException Related to RMI
     */
    private static void sendScoreBoard(ClientStatusInterface csi, GameController controller) throws RemoteException {
        if (controller.isGameComplete()) {
            csi.setScoreBoard(controller.getScoreboard());
            csi.setStatus(Status.FinalScoreboard);
        }
    }

    /**
     * Implements End Game strategy, informing all clients (RMI and Socket ones) to disconnect ad deallocates Model Objects from global structures
     *
     * @param gc              Game's Controller
     * @param invokedBySocket to avoid Server callbacks
     */
    public static void endGameHandler(GameController gc, boolean invokedBySocket) {
        Set<ClientStatusInterface> keySet;
        synchronized (clientStatusToController) {
            keySet = new HashSet<>(clientStatusToController.keySet());
        }
        for (ClientStatusInterface csi : keySet) {
            boolean gameCorrect = false;
            synchronized (clientStatusToController) {
                if (clientStatusToController.containsKey(csi) && clientStatusToController.get(csi).equals(gc)) {
                    gameCorrect = true;
                }
            }
            if (gameCorrect) {
                executors.execute(() -> {
                    try {
                        if (!gc.isGameComplete())
                            csi.setStatus(Status.ForcedGameEnd);
                    } catch (RemoteException ignored) {
                    }
                });
            }
            synchronized (clientStatusToController) {
                clientStatusToController.remove(csi);
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
        if (!invokedBySocket) {
            SocketServer.endGameHandler(gc, true, false);
        }
        if (new File(System.getProperty("user.dir").concat("/games-persistency/").concat("game-").concat(gc.getIdentifier()).concat(".json")).delete()) {
            ServerManager.writeWarning(String.format("%s's game file has been correctly deleted.", gc.getIdentifier()));
        }
    }

    /**
     * abstraction method, invoked by Status Intermediate by default and listening for client status updates
     *
     * @throws Exception related to Model management
     */
    public void manage() throws Exception {
        ClientStatusInterface csi = intermediate.csi;

        if (!csi.setStatus(Status.Sending_Identifier)) {
            csi.setStatus(Status.Denied_Request);
            return;
        }

        // checks for client parameters before proceding
        String clientIdentifier = csi.getIdentifier();
        String clientNickname = csi.getNickname();
        GameController controller = null;
        int numPlayers = -1;

        controller = loginCheck(csi, clientIdentifier, clientNickname);
        if (controller == null) return;

        ServerManager.writeInfo(String.format("%s joined %s game using RMI Client", clientNickname, clientIdentifier));

        AdmissionResult admissionResult = admission(csi, clientNickname, controller);

        turnManagement(csi, clientIdentifier, clientNickname, controller, admissionResult);

        sendScoreBoard(csi, controller);
    }

    /**
     * Game's lobby admission and re-enter strategy
     *
     * @param csi              Client Status Interface, remote interface of client's implementation of the Status Object
     * @param clientIdentifier Game's identifier, as given by the client (checked before)
     * @param clientNickname   Game's nickname, as given by the client (checked before)
     * @return Game Controller, obtained after the login phase
     * @throws RemoteException related to RMI
     */
    private GameController loginCheck(ClientStatusInterface csi, String clientIdentifier, String clientNickname) throws RemoteException {
        GameController controller;
        List<String> nicknames = new ArrayList<>();
        boolean containsIdentifier;
        synchronized (identifierToController) {
            containsIdentifier = identifierToController.containsKey(clientIdentifier);
            if (containsIdentifier)
                nicknames = identifierToController.get(clientIdentifier).nicknames();
        }
        if (clientIdentifier == null || !containsIdentifier || !nicknames.contains(clientNickname)) {
            csi.setStatus(Status.Denied_Request);
            return null;
        } else {
            synchronized (identifierToController) {
                controller = identifierToController.get(clientIdentifier);
            }
            synchronized (clientStatusToController) {
                clientStatusToController.put(csi, controller);
            }
            csi.setGameParameters(controller.getGameBoard(), controller.getShelf(clientNickname), controller.getMoveIntermediate(clientNickname));
        }
        return controller;
    }

    /**
     * Admission phase implementation
     *
     * @param csi            Client Status Interface, remote interface of client's implementation of the Status Object
     * @param clientNickname Game's nickname, as given by the client (checked before)
     * @param controller     Game's Controller
     * @return an Admission Result
     * @throws InterruptedException related to synchronization management
     * @throws RemoteException      related to RMI
     */
    private AdmissionResult admission(ClientStatusInterface csi, String clientNickname, GameController controller) throws InterruptedException, RemoteException {
        int numPlayers;
        // uses a commuter Object for every client: it is used to synchronize client's access to game and turn management
        Object commuter;
        synchronized (controllerToCommuter) {
            if (!controllerToCommuter.containsKey(controller))
                controllerToCommuter.put(controller, commuter = new Object());
            else commuter = controllerToCommuter.get(controller);
        }

        // uses a lobbyCommuter Object for every client: it is used to synchronize client's access to lobby re-enter strategy
        Object lobbyCommuter;
        synchronized (controllerToLobbyCommuter) {
            if (!controllerToLobbyCommuter.containsKey(controller))
                controllerToLobbyCommuter.put(controller, lobbyCommuter = new Object());
            else lobbyCommuter = controllerToLobbyCommuter.get(controller);
        }

        // waits for the game to be prepared (at every new player, game status is checked)
        synchronized (commuter) {
            if (controller.isGamePrepared())
                commuter.notifyAll();
            else commuter.wait();
        }

        // now the game has been prepared
        csi.setCards(controller.getPersonalGoalCard(clientNickname), controller.getCommonGoalCards());

        numPlayers = controller.getMaxPlayers();

        synchronized (lobbyCommuter) {
            if (controller.getLobby().size() == numPlayers)
                lobbyCommuter.notifyAll();
            else lobbyCommuter.wait();
        }

        // semaphore to control access to a single turn (commuter to wait turn's end)
        Semaphore sem;
        synchronized (controllerToSemaphore) {
            if (!controllerToSemaphore.containsKey(controller))
                controllerToSemaphore.put(controller, sem = new Semaphore(numPlayers));
            else sem = controllerToSemaphore.get(controller);
        }
        return new AdmissionResult(numPlayers, commuter, sem);
    }

    /**
     * Turn Management implementation
     *
     * @param csi              Client Status Interface, remote interface of client's implementation of the Status Object
     * @param clientIdentifier Game's identifier, as given by the client (checked before)
     * @param clientNickname   Game's nickname, as given by the client (checked before)
     * @param controller       Game's Controller
     * @param admissionResult  Admission Result
     * @throws Exception related to Model management
     */
    private void turnManagement(ClientStatusInterface csi, String clientIdentifier, String clientNickname, GameController controller, AdmissionResult admissionResult) throws Exception {
        Semaphore sem = admissionResult.sem();
        int numPlayers = admissionResult.numPlayers();
        Object commuter = admissionResult.commuter();

        while (!controller.isGameComplete()) {
            sem.acquire(1);

            if (controller.isGameEnded()) {
                csi.setStatus(Status.LastTurn);
            }

            // turn management implementation
            csi.setCurrentPlayer(controller.getActivePlayer().getNickname());
            if (controller.getActivePlayer().getNickname().equals(clientNickname)) {
                ServerManager.writeInfo(String.format("%s - It's %s's turn!", clientIdentifier, clientNickname));
                csi.setStatus(Status.YourTurn);

                try {
                    Files.createDirectories(Paths.get(System.getProperty("user.dir").concat("/games-persistency/")));
                } catch (IOException e) {
                    ServerManager.writeWarning(String.format("Couldn't create support directory: %s%n", e.getMessage()));
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir").concat("/games-persistency/game-"
                        .concat(clientIdentifier).concat(".json"))))) {

                    bw.write(gson.toJson(controller));
                } catch (Exception e) {
                    ServerManager.writeWarning(String.format("GSON or file management exception: %s%n", e.getMessage()));
                }
                sem.release(numPlayers);
                // I notify other players that I have completed a move (so a new player may do their move)
                // (it might also be incorrect, in that case, the same pattern is repeated)
                synchronized (commuter) {
                    commuter.notifyAll();
                }
            } else {
                csi.setStatus(Status.NotYourTurn);
                // I wait for the move of the other player to complete before rechecking player's turn
                synchronized (commuter) {
                    commuter.wait();
                }
            }

            csi.setLastTurnScores(new HashMap<>(controller.getLastTurnScores()));
            Stack<ScoreCardInterface> temp = new Stack<>();
            temp.addAll(controller.getScoreCardsFromNickname(clientNickname));
            csi.setScoreCards(temp);
            csi.setStatus(Status.AchievementUpdate);
        }
    }

    /**
     * Admission phase result
     *
     * @param numPlayers number of players in the game, tipically ignored by players following the first
     * @param commuter   synchronization commuter
     * @param sem        synchronization Semaphore
     */
    private record AdmissionResult(int numPlayers, Object commuter, Semaphore sem) {
    }
}
