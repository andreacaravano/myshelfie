package it.polimi.ingsw.Server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import it.polimi.ingsw.Server.Controller.GameController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Server management console
 */
public class ServerManager {
    private static final Map<String, GameController> identifierToController = new HashMap<>();
    private static final Map<GameController, Semaphore> controllerToSemaphore = new HashMap<>();
    private static final Map<GameController, Object> controllerToCommuter = new HashMap<>();
    private static final Map<GameController, Object> controllerToLobbyCommuter = new HashMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final int countParameters = 3;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy");
    private static Logger logFile = null;
    private static FileHandler FH = null;

    /**
     * Main method, welcoming Server management console
     *
     * @param args default arguments array
     */
    public static void main(String[] args) {
        createLog();
        writeInfo(String.format("Your server is running Java %s on %s %s, %s in %s", System.getProperty("java.version"), System.getProperty("os.name"),
                System.getProperty("os.version"), System.getProperty("os.arch"), System.getProperty("user.dir")));
        loadPersistentGames();
        RMIServer(args);
        SocketServer(args);
        executor.shutdown();
    }

    private static void createLog() {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir").concat("/log/")));
        } catch (IOException e) {
            System.out.format("Error with file management: %s - %s", e, e.getMessage());
        }

        manageLog();
    }

    private static void manageLog() {
        String date = new SimpleDateFormat("dd-MM-yy").format(new Date());
        try {
            if (FH != null) {
                FH.flush();
                FH.close();
            }
            logFile = Logger.getLogger(String.format("LogMyShelfieServer-%s", date));

            File f = new File(System.getProperty("user.dir").concat(String.format("/log/events-%s.log", date)));
            boolean isNew = !f.exists() || f.isDirectory();

            FH = new FileHandler(String.format("log/events-%s.log", date), true);
            logFile.addHandler(FH);
            FH.setFormatter(new SimpleFormatter());
            if (isNew)
                logFile.info("Created a new log file.");
        } catch (SecurityException | IOException e) {
            writeWarning(String.format("%s - %s", e, e.getMessage()));
        }

        // Programs creation of new log
        try {
            Date tomorrow = new Date(new SimpleDateFormat("dd-MM-yy HH:mm:ss").parse(String.format("%s 00:00:30", dateFormatter.format(new Date()))).getTime() + (1000 * 60 * 60 * 24));
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    manageLog();
                }
            }, tomorrow);
        } catch (ParseException e) {
            writeWarning(String.format("%s - %s", e, e.getMessage()));
        }
    }

    /**
     * Outputs a Warning in Server's log (and terminalOutput)
     *
     * @param msg message
     */
    public static void writeWarning(String msg) {
        logFile.warning(msg);
    }

    /**
     * Outputs an informative message in Server's log (and terminalOutput)
     *
     * @param msg message
     */
    public static void writeInfo(String msg) {
        logFile.info(msg);
    }

    private static void loadPersistentGames() {
        String basePath = System.getProperty("user.dir").concat("/games-persistency/");
        if (Files.exists(Paths.get(basePath))) {
            try (Stream<Path> stream = Files.list(Paths.get(basePath))) {
                Set<String> gamesFiles = stream.filter(file -> !Files.isDirectory(file))
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toSet());

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

                for (String f : gamesFiles) {
                    if (f.contains("json")) {
                        try (Stream<String> fl = Files.lines(Paths.get(basePath.concat(f)), StandardCharsets.UTF_8)) {
                            GameController gc = gson.fromJson(fl.findFirst().orElseThrow(), GameController.class);
                            gc = gc.refreshEntities();
                            if (identifierToController.containsKey(gc.getIdentifier())) {
                                writeInfo("There are duplicates in games persistency files. Please remove and restart server to proceed.");
                                System.exit(1);
                                return;
                            }
                            identifierToController.put(gc.getIdentifier(), gc);
                        }
                    }
                }
            } catch (IOException e) {
                writeInfo(String.format("Failed to load files in game persistency directory: %s%n", e.getMessage()));
            }
        } else {
            writeInfo("No persistent game found.");
        }
    }

    private static void SocketServer(String[] args) {
        // Create Thread for Socket Server
        executor.execute(() -> {
            if (args.length == countParameters) {
                SocketServer.main(new String[]{args[0]});
                new SocketServer(identifierToController, controllerToSemaphore, controllerToCommuter, controllerToLobbyCommuter);
            } else if (args.length > 0 && args.length < countParameters) {
                writeInfo("Please, insert both RMI and Socket port, plus the preferred RMI Server Hostname.");
            } else {
                SocketServer.main(new String[]{});
                new SocketServer(identifierToController, controllerToSemaphore, controllerToCommuter, controllerToLobbyCommuter);
            }
        });
    }

    private static void RMIServer(String[] args) {
        // Create Thread for RMI Server
        executor.execute(() -> {
            if (args.length == countParameters) {
                RMIServer.main(new String[]{args[1], args[2]});
                new RMIServer(identifierToController, controllerToSemaphore, controllerToCommuter, controllerToLobbyCommuter);
            } else if (args.length > 0 && args.length < countParameters) {
                writeInfo("Please, insert both RMI and Socket port, plus the preferred RMI Server Hostname.");
            } else {
                RMIServer.main(new String[]{});
                new RMIServer(identifierToController, controllerToSemaphore, controllerToCommuter, controllerToLobbyCommuter);
            }
        });
    }
}
