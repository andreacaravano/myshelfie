package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Common.*;
import it.polimi.ingsw.Server.Model.*;

import java.rmi.RemoteException;
import java.util.*;

/**
 * This class represents the controller, which manages the logic of the game, such as login, turn management, chat.
 */
public class GameController {
    /**
     * Lobby where players wait until the start of the game
     */
    private final List<String> lobby;
    /**
     * Controller's identifier
     */
    private final String identifier;
    /**
     * Game instance associated to the Controller
     */
    private Game game = null;
    /**
     * Player who is playing his/her turn at this very moment
     */
    private Player activePlayer = null;
    /**
     * Maximum number of Players accepted in a game
     */
    private int maxPlayers;

    /**
     * This attribute indicates if a game has been re-entered after a disconnection of the server or not
     */
    private boolean refreshed = false;

    /**
     * This attribute indicates a map where are stored pairs of CommonGoalCard and its relative ScoreCard
     */
    private Map<ScoreCard, CommonGoalCard> lastTurnScores;

    /**
     * GameController creator
     *
     * @param identifier ID of this Controller
     */

    public GameController(String identifier) {
        lobby = new ArrayList<>();
        this.identifier = identifier;
        this.lastTurnScores = new HashMap<>();
    }

    /**
     * Related to Game's refresh strategy
     */
    private GameController(List<String> lobby, String identifier, Game game, Player activePlayer, int maxPlayers, boolean refreshed, Map<ScoreCard, CommonGoalCard> lastTurnScores) {
        this.lobby = lobby;
        this.identifier = identifier;
        this.game = game;
        this.activePlayer = activePlayer;
        this.maxPlayers = maxPlayers;
        this.refreshed = refreshed;
        this.lastTurnScores = lastTurnScores;
    }

    /**
     * getter method for the Game Identifier
     *
     * @return Game's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * This method accepts a new Player in the lobby, if his/her nickname is a regular expression of alphanumeric strings and if the number of
     * players waiting in the lobby is less than the maxPlayers number, chosed by the first player
     *
     * @param nickname   nickname chosen by the player
     * @param maxPlayers maximum number of players chosen by the first player
     * @return true if the player has been accepted, false otherwise
     * @throws Exception if prepareGame() or newPlayer() return an Exception
     */
    public boolean acceptPlayer(String nickname, int maxPlayers) throws Exception {
        if (refreshed)
            return false;
        if (game != null && lobby.size() >= game.getMaxPlayers())
            return false;
        // Regular expression of alphanumeric strings
        if (!nickname.matches("^[a-zA-Z0-9_]+$") || nickname.length() < 3 || lobby.contains(nickname.toLowerCase()) || lobby.contains(nickname.toUpperCase()))
            return false;
        else {
            if (lobby.isEmpty()) {
                if (maxPlayers >= 2 && maxPlayers <= 4) {
                    game = new Game(identifier, maxPlayers);
                    this.maxPlayers = maxPlayers;
                    lobby.add(nickname);
                } else return false;
            } else {
                lobby.add(nickname);
            }
            Player p = game.newPlayer(nickname);
            if (lobby.size() == game.getMaxPlayers()) {
                game.prepareGame();
                activePlayer = game.getPlayers().get(0);
            }
            return true;
        }
    }

    /**
     * getter method for lobby
     *
     * @return List of nicknames (players who are waiting in the lobby)
     */
    public List<String> getLobby() {
        return lobby;
    }

    /**
     * getter method for MaxPlayers
     *
     * @return maxPlayers
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * This method indicates if the game is prepared
     *
     * @return false if the game doesn't exist or the game isn't prepared, true otherwise
     */
    public boolean isGamePrepared() {
        if (game == null)
            return false;
        else return game.isPrepared();
    }

    /**
     * getter method for GameBoard
     *
     * @return Board interface if game isn't null, null otherwise
     * @throws RemoteException related to RMI
     */
    public BoardInterface getGameBoard() throws RemoteException {
        if (!(game == null))
            return game.getBoard();
        else return null;
    }

    /**
     * getter method for Shelf
     *
     * @param nickname player's nickname
     * @return Shelf interface of the player with this nickname (if game isn't null), null otherwise
     */
    public ShelfInterface getShelf(String nickname) {
        if (!(game == null))
            return game.getShelfInterfaceFromNickname(nickname);
        else return null;
    }

    /**
     * getter method for PersonalGoalCard
     *
     * @param nickname player's nickname
     * @return PersonalGoalCard interface of the player with this nickname (if game isn't null), null otherwise
     */
    public PersonalGoalCardInterface getPersonalGoalCard(String nickname) {
        if (!(game == null))
            return game.getPersonalGoalCardInterfaceFromNickname(nickname);
        else return null;
    }

    /**
     * getter method for CommonGoalCards
     *
     * @return List of CommonGoalCard interface if game isn't null, null otherwise
     */
    public List<CommonGoalCardInterface> getCommonGoalCards() {
        if (!(game == null))
            return game.getCommonGoalCards();
        else return null;
    }

    /**
     * @param nickname Player's nickname
     * @return Player's obtained ScoreCards
     */
    public Stack<ScoreCard> getScoreCardsFromNickname(String nickname) {
        if (!(game == null))
            return game.getScoreCardsFromNickname(nickname);
        else return null;
    }

    /**
     * This method returns a list of nicknames of the players, who are playing a game (if game isn't null), null otherwise
     *
     * @return a list of player's nicknames
     */
    public List<String> nicknames() {
        if (game == null)
            return null;
        else return game.nicknames();
    }

    /**
     * getter method for ScoreBoard
     *
     * @return null if game is null, Scoreboard otherwise
     */
    public Scoreboard getScoreboard() {
        if (game == null)
            return null;
        else return game.getScoreboard();
    }

    /**
     * getter method for MoveIntermediate
     *
     * @param nickname player's nickname
     * @return MoveIntermediateInterface
     * @throws RemoteException related to RMI
     */
    public MoveIntermediateInterface getMoveIntermediate(String nickname) throws RemoteException {
        return new MoveIntermediate(game.getBoard(), game.getShelfFromNickname(nickname), this, game.getPlayerFromNickname(nickname));
    }

    /**
     * Getter method for activePlayer
     *
     * @return activePlayer
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * This method manages turn evolution during the game. Turn order is decided in class Game.
     */
    private void nextPlayer() {
        if (activePlayer == null) {
            activePlayer = game.getPlayers().get(0);
        } else {
            if (lastTurnScores != null) {
                lastTurnScores.clear();
            }
            lastTurnScores = activePlayer.getLastAchieved();
            Map<Integer, Player> players = game.getPlayers();
            activePlayer = players.get((players.entrySet()
                    .stream()
                    .filter(entry -> activePlayer.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow() + 1) % players.size());
        }
    }

    /**
     * getter method for LastTurnScores (if a player has achieved a CommonGoal)
     *
     * @return a map of ScoreCard and CommonGoalCard
     */
    public Map<ScoreCard, CommonGoalCard> getLastTurnScores() {
        Map<ScoreCard, CommonGoalCard> temp = new HashMap<>();
        if (lastTurnScores != null) {
            for (ScoreCard sc : lastTurnScores.keySet()) {
                temp.put(sc, lastTurnScores.get(sc));
            }
            return temp;
        }
        return null;
    }

    /**
     * This method manages the move m, decided by the Player
     *
     * @param m Move
     * @return true if move has been correctly accomplished, false otherwise
     * @throws Exception if updateScore() in gameComplete() throws an Exception
     */
    public boolean make(Move m) throws Exception {
        if (activePlayer.isInGame()) {
            if (m.take()) {
                if (m.place()) {
                    activePlayer.updateScore();
                    if (game.checkEndGame(activePlayer)) {
                        for (Player p : game.getPlayers().values()) {
                            if (p.equals(activePlayer)) {
                                p.setOutOfTurn();
                                break;
                            } else
                                p.setOutOfTurn();
                        }
                        if (game.gameComplete()) {
                            return true;
                        }
                    }
                    nextPlayer();
                    return true;
                } else {
                    m.restoreTaken();
                    return false;
                }
            } else {
                return false;
            }
        } else return false;
    }

    /**
     * Getter method for isGameEnded
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean isGameEnded() {
        if (game == null)
            return false;
        else return game.isGameEnded();
    }

    /**
     * this method indicates if the game is completed
     *
     * @return false if game is null or isn't completed, true otherwise
     */
    public boolean isGameComplete() {
        if (game == null)
            return false;
        else try {
            return game.gameComplete();
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * getter method for PlayerByNickname
     *
     * @param nickname player's nickname
     * @return player recognized by his nickname
     */
    public Player getPlayerByNickname(String nickname) {
        return game.getPlayerFromNickname(nickname);
    }


    /**
     * Objects refresh following a game reloaded from a persistency file
     *
     * @return the refreshed controller
     * @throws RemoteException Related to RMI
     */
    public GameController refreshEntities() throws RemoteException {
        lobby.clear();
        refreshed = true;
        this.game = game.refreshEntities();
        this.activePlayer = game.getPlayers()
                .values()
                .stream()
                .filter(p -> p.getNickname().equals(activePlayer.getNickname()))
                .findFirst()
                .orElseThrow();
        if (lastTurnScores != null && !lastTurnScores.isEmpty())
            this.lastTurnScores = new HashMap<>(this.lastTurnScores);
        else this.lastTurnScores = new HashMap<>();
        return new GameController(this.lobby, this.identifier, this.game, this.activePlayer, this.maxPlayers, this.refreshed, this.lastTurnScores);
    }

    /**
     * Game's re-entering strategy (related to a game reloaded from a persistency file)
     *
     * @param nickname of the player
     * @return completion of the admission phase
     */
    public boolean reEnterGame(String nickname) {
        if (game != null) {
            if (refreshed && !lobby.contains(nickname) && nicknames().contains(nickname)) {
                lobby.add(nickname);
                if (lobby.size() == this.getMaxPlayers())
                    refreshed = false;
                return true;
            }
        }
        return false;
    }
}
