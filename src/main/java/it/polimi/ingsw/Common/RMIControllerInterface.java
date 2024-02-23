package it.polimi.ingsw.Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Controller Interface
 */
public interface RMIControllerInterface extends Remote {
    /**
     * @param identifier Game's identifier
     * @return whether the identifier exists
     * @throws RemoteException related to RMI
     */
    public boolean identifierExists(String identifier) throws RemoteException;

    /**
     * @param identifier Game's identifier
     * @param nickname   Player's nickname
     * @return whether the nickname exists in the given Game's identifier
     * @throws RemoteException related to RMI
     */
    public boolean nicknameExists(String identifier, String nickname) throws RemoteException;

    /**
     * Creates a game using the corresponding identifier
     *
     * @param identifier Game's identifier
     * @param csi        Client Status Interface
     * @return true if the Game have been correctly created
     * @throws RemoteException related to RMI
     */
    public boolean createGame(String identifier, ClientStatusInterface csi) throws RemoteException;

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
    public boolean acceptPlayer(String identifier, String nickname, int maxPlayers) throws Exception;

    /**
     * Game's re-enter strategy
     *
     * @param identifier Game's identifier
     * @param nickname   Player's nickname
     * @return action validity
     * @throws RemoteException related to RMI
     */
    public boolean reEnterGame(String identifier, String nickname) throws RemoteException;
}