package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Controller.GameController;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Application-level KeepAlive implementation
 */
public class SocketKeepAlive {
    private final int CDL_TIMEOUT = 5; // CountDownLatch Timeout
    private final int SEC_TIMEOUT = 6; // Timeout in seconds
    private final AtomicBoolean flag;
    private final Socket socket;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private CountDownLatch cdl;

    /**
     * Naive Application-level KeepAlive implementation
     *
     * @param socket attached to the client
     * @param gc     related to the client
     */
    public SocketKeepAlive(Socket socket, GameController gc) {
        this.socket = socket;
        cdl = new CountDownLatch(CDL_TIMEOUT);
        flag = new AtomicBoolean(false);

        // scheduled KeepAlive check
        executor.scheduleAtFixedRate(() -> {
            if (flag.get()) {
                flag.set(false);
                // received
                if (cdl.getCount() != CDL_TIMEOUT)
                    cdl = new CountDownLatch(CDL_TIMEOUT);
            } else {
                if (!socket.isClosed())
                    ServerManager.writeWarning(String.format("Client %s didn't send a keep-alive at this round. Their connection might be laggy or unstable.", socket.getRemoteSocketAddress()));
                // close connection
                if (cdl.getCount() == 1) {
                    SocketServer.endGameHandler(gc, false, true);
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                    executor.shutdown();
                } else cdl.countDown();
            }
        }, 0, SEC_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * Sets the KeepAlive as received (setting the correspondent flag), the next scheduled task to run will get the flag as valid and reset the CDL
     */
    public synchronized void received() {
        flag.set(true);
    }

    /**
     * @return status of the Client's socket
     */
    public synchronized boolean closed() {
        return socket.isClosed();
    }
}
