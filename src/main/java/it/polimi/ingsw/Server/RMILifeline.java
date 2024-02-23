package it.polimi.ingsw.Server;

import it.polimi.ingsw.Common.ClientStatusInterface;
import it.polimi.ingsw.Server.Controller.GameController;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Application-level KeepAlive implementation
 */
public class RMILifeline {
    private final int SEC_TIMEOUT = (6 * 5) / 2; // Timeout in seconds
    ExecutorService executor = Executors.newCachedThreadPool();
    AtomicBoolean flag = new AtomicBoolean(true);

    /**
     * Naive Application-level KeepAlive implementation
     *
     * @param csi                      Client Status Interface
     * @param clientStatusToController maps Client Status Interfaces to Game Controllers
     */
    public RMILifeline(ClientStatusInterface csi, Map<ClientStatusInterface, GameController> clientStatusToController) {
        // scheduled Lifeline check
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                executor.execute(() -> {
                    try {
                        if (!flag.get())
                            throw new Exception();
                        flag.set(false);
                        csi.lifeline();
                        flag.set(true);
                    } catch (Exception e) {
                        ServerManager.writeWarning("An RMI Client has been detected as unreachable, terminating related games.");
                        synchronized (clientStatusToController) {
                            if (clientStatusToController.containsKey(csi)) {
                                RMIServer.endGameHandler(clientStatusToController.get(csi), false);
                            }
                        }
                        timer.cancel();
                        executor.shutdown();
                    }
                });
            }
        }, 0, SEC_TIMEOUT * 1000);
    }
}