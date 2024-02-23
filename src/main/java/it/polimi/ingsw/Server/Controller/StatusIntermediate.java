package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Common.ClientStatusInterface;
import it.polimi.ingsw.Common.StatusIntermediateInterface;
import it.polimi.ingsw.Server.RMILifeline;
import it.polimi.ingsw.Server.RMIServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * Client Status Intermediate
 */
public class StatusIntermediate extends UnicastRemoteObject implements StatusIntermediateInterface {
    /**
     * RMI Server implementation
     */
    private final RMIServer server;
    private final Map<ClientStatusInterface, GameController> clientStatusToController;
    /**
     * Client Status object
     */
    public ClientStatusInterface csi;

    /**
     * Status Intermediate Constructor
     *
     * @param server                   RMI Server implementation
     * @param clientStatusToController maps a Client Status Interface to the Game's controller
     * @throws RemoteException related to RMI
     */
    public StatusIntermediate(RMIServer server, Map<ClientStatusInterface, GameController> clientStatusToController) throws RemoteException {
        super();
        this.server = server;
        this.clientStatusToController = clientStatusToController;
    }

    /**
     * Setter for Client Status Object
     * It makes the server manage Game's evolution
     *
     * @param csi Interface for the Client Status
     * @throws RemoteException      related to RMI
     * @throws InterruptedException related to Thread management
     */
    public void setIntermediate(ClientStatusInterface csi) throws Exception {
        this.csi = csi;

        new RMILifeline(csi, clientStatusToController);

        server.manage();
    }

    /**
     * Naive KeepAlive implementation, used for disconnections management
     */
    public void lifeline() {
        return;
    }
}
