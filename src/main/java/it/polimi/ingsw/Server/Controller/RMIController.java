package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Common.ClientStatusInterface;
import it.polimi.ingsw.Common.RMIControllerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * Implements intermediate to the Game Controllers, making multiple games management easier
 */
public class RMIController extends UnicastRemoteObject implements RMIControllerInterface {
    /**
     * Associates a Game's identifier to its controller
     */
    private final Map<String, GameController> identifierToController;
    private final Map<ClientStatusInterface, GameController> clientStatusToController;

    /**
     * RMI Controller constructor
     *
     * @param identifierToController   games identifiers map
     * @param clientStatusToController maps Client Status Interfaces to Game's Controller
     * @throws RemoteException related to RMI
     */
    public RMIController(Map<String, GameController> identifierToController, Map<ClientStatusInterface, GameController> clientStatusToController) throws RemoteException {
        super();
        this.identifierToController = identifierToController;
        this.clientStatusToController = clientStatusToController;
    }

    /**
     * @param identifier Game's identifier
     * @return whether the identifier exists
     */
    public boolean identifierExists(String identifier) {
        return identifierToController.containsKey(identifier);
    }

    /**
     * @param identifier Game's identifier
     * @param nickname   Player's nickname
     * @return whether the nickname exists in the given Game's identifier
     */
    public boolean nicknameExists(String identifier, String nickname) {
        if (!identifierToController.containsKey(identifier))
            return false;
        else
            return (identifierToController.get(identifier).nicknames() != null && identifierToController.get(identifier).nicknames().contains(nickname));
    }

    /**
     * Creates a game using the corresponding identifier
     *
     * @param identifier Game's identifier
     * @return true if the Game have been correctly created
     */
    public boolean createGame(String identifier, ClientStatusInterface csi) {
        if ((!identifier.matches("^[a-zA-Z0-9_]+$") || identifier.length() < 3) || identifierToController.containsKey(identifier) || csi == null)
            return false;
        else {
            GameController gc = new GameController(identifier);
            synchronized (identifierToController) {
                identifierToController.put(identifier, gc);
            }
            synchronized (clientStatusToController) {
                clientStatusToController.put(csi, gc);
            }
            return true;
        }
    }

    /**
     * Admission phase
     *
     * @param identifier Game's identifier
     * @param nickname   Player's nickname
     * @param maxPlayers Maximum number of players for the game
     *                   used only if the first player is being accepted to the game.
     *                   It is ignored otherwise.
     * @return true if the admission process have been correctly completed
     * @throws Exception related to Model management
     */
    public boolean acceptPlayer(String identifier, String nickname, int maxPlayers) throws Exception {
        if (!identifierToController.containsKey(identifier))
            return false;
        else
            return identifierToController.get(identifier).acceptPlayer(nickname, maxPlayers);
    }

    /**
     * Game's re-enter strategy
     *
     * @param identifier Game's identifier
     * @param nickname   Player's nickname
     * @return action validity
     */
    public boolean reEnterGame(String identifier, String nickname) {
        if (!identifierToController.containsKey(identifier))
            return false;
        else
            return identifierToController.get(identifier).reEnterGame(nickname);
    }
}
