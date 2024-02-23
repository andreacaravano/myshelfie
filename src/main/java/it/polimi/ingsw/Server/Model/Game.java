package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Common.CommonGoalCardInterface;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains general information about the current game, such as CommonGoalCards and number of Players.
 */
public class Game {
    /**
     * final attribute, which indicates the maximum number of players who can join a game
     */
    public final int MAX_PLAYERS = 4;
    /**
     * Identifier is the game id. It is chosen by the first player, who joins the game, and it can be used by other players to
     * join a pre-existing game
     */
    private final String identifier;
    /**
     * Map used to store Players, associated to an Integer key, which indicates their position in the game
     */
    private final Map<Integer, Player> players;
    /**
     * Number of players, chosen by the first player, when he/she creates a new game
     */
    private final int maxPlayers;
    /**
     * List of personalCards associated to each player
     */
    private final List<PersonalGoalCard> personalCards;
    /**
     * Game scoreboard
     */
    private Scoreboard scoreboard = null;
    /**
     * board used in the game
     */
    private Board board;
    /**
     * number of players who have already joined the game
     */
    private int playerCount;
    /**
     * It's the first CommonGoalCard drawn for the game
     */
    private CommonGoalCard cgCard1;
    /**
     * It's the second CommonGoalCard drawn for the game
     */
    private CommonGoalCard cgCard2;
    /**
     * Object Cards extracted in the related game
     */
    private List<ObjectCard> objectCards;
    /**
     * this attribute is true if a player has already filled his/her shelf, false otherwise
     */
    private boolean gameEnded = false;
    /**
     * this attribute is true if the game has started
     */
    private boolean prepared = false;

    /**
     * game constructor
     *
     * @param identifier ID of the game, chosen by the Player who creates the game
     * @param maxPlayers maximum number of players, who can join the game (chosen by the first player)
     * @throws Exception if the maximum number of players is higher than the MAX_PLAYERS number
     */

    public Game(String identifier, int maxPlayers) throws Exception {
        if (maxPlayers > MAX_PLAYERS)
            throw new Exception();
        this.identifier = identifier;
        this.maxPlayers = maxPlayers;
        this.players = new HashMap<>();
        this.objectCards = new ArrayList<>();
        this.board = new Board(maxPlayers, objectCards);
        this.personalCards = new ArrayList<>();
        this.gameEnded = false;
    }

    /**
     * Related to Game's refresh strategy
     */
    public Game(String identifier, Map<Integer, Player> players, int maxPlayers, List<PersonalGoalCard> personalCards, Scoreboard scoreboard, Board board, int playerCount, CommonGoalCard cgCard1, CommonGoalCard cgCard2, List<ObjectCard> objectCards, boolean gameEnded, boolean prepared) {
        this.identifier = identifier;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.personalCards = personalCards;
        this.scoreboard = scoreboard;
        this.board = board;
        this.playerCount = playerCount;
        this.cgCard1 = cgCard1;
        this.cgCard2 = cgCard2;
        this.objectCards = objectCards;
        this.gameEnded = gameEnded;
        this.prepared = prepared;
    }

    /**
     * Getter method for the board
     *
     * @return Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter method for the attribute, which indicates the maximum number of players, who can join the game
     *
     * @return maxPlayers
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Getter method for players. An integer is associated to each Player, who is playing the game
     *
     * @return a map of Players
     */
    public Map<Integer, Player> getPlayers() {
        return players;
    }

    /**
     * This method adds a new player to "players" Map.
     *
     * @param newNickname player's nickname
     * @return Player who's been added to players
     * @throws RemoteException if the new instance of class Player hasn't been created
     */
    public Player newPlayer(String newNickname) throws RemoteException {
        Player ret = new Player(newNickname);
        players.put(playerCount++, ret);
        return ret;
    }

    /**
     * getter method to recognize a player from nickname
     *
     * @param nickname player's nickname
     * @return players if nickname != null, null otherwise
     */
    public Player getPlayerFromNickname(String nickname) {
        if (nickname != null && players
                .values()
                .stream()
                .anyMatch(p -> p.getNickname().equals(nickname))) {
            return players
                    .values()
                    .stream()
                    .filter(p -> p.getNickname().equals(nickname))
                    .findFirst()
                    .orElseThrow();
        } else return null;
    }

    /**
     * this method is a getter for the shelf interface obtained from nickname
     *
     * @param nickname player's nickname
     * @return player's shelf interface
     */
    public ShelfInterface getShelfInterfaceFromNickname(String nickname) {
        if (getPlayers()
                .values()
                .stream()
                .map(Player::getNickname)
                .toList()
                .contains(nickname)) {
            return getPlayers()
                    .values()
                    .stream()
                    .filter(p -> p.getNickname().equals(nickname))
                    .map(Player::getShelf)
                    .findFirst()
                    .orElseThrow();
        } else return null;
    }

    /**
     * this method is a getter of PersonalGoalCard obtained from nickname
     *
     * @param nickname player's nickname
     * @return player's PersonalGoalCard
     */
    public PersonalGoalCardInterface getPersonalGoalCardInterfaceFromNickname(String nickname) {
        if (getPlayers()
                .values()
                .stream()
                .map(Player::getNickname)
                .toList()
                .contains(nickname)) {
            return getPlayers()
                    .values()
                    .stream()
                    .filter(p -> p.getNickname().equals(nickname))
                    .map(Player::getPersonalGoalCard)
                    .findFirst()
                    .orElseThrow();
        } else return null;
    }

    /**
     * this method is a getter for CommonGoalCards
     *
     * @return a list of CommonGoalCards
     */
    public List<CommonGoalCardInterface> getCommonGoalCards() {
        return Arrays.asList(cgCard1, cgCard2);
    }

    /**
     * this method is a getter for player's shelf obtained from nickname
     *
     * @param nickname player's nickname
     * @return player's shelf
     */
    public Shelf getShelfFromNickname(String nickname) {
        if (getPlayers()
                .values()
                .stream()
                .map(Player::getNickname)
                .toList()
                .contains(nickname)) {
            return getPlayers()
                    .values()
                    .stream()
                    .filter(p -> p.getNickname().equals(nickname))
                    .map(Player::getShelf)
                    .findFirst()
                    .orElseThrow();
        } else return null;
    }

    /**
     * this method is a getter for ScoreCards obtained from Nickname
     *
     * @param nickname player's nickname
     * @return a stack of player's ScoreCard
     */
    public Stack<ScoreCard> getScoreCardsFromNickname(String nickname) {
        if (getPlayers()
                .values()
                .stream()
                .map(Player::getNickname)
                .toList()
                .contains(nickname)) {
            return getPlayers()
                    .values()
                    .stream()
                    .filter(p -> p.getNickname().equals(nickname))
                    .map(Player::getScoreCards)
                    .findFirst()
                    .orElseThrow();
        } else return null;
    }

    /**
     * This method returns a list of nicknames of the players, who are playing a game (if game isn't null), null otherwise
     *
     * @return a list of player's nicknames
     */
    public List<String> nicknames() {
        return getPlayers()
                .values()
                .stream()
                .map(Player::getNickname)
                .toList();
    }

    /**
     * This method includes setup procedures, such as drawing personal goal cards and common goal cards,
     * choosing randomly the first player and placing ObjectCards on the board.
     *
     * @throws Exception if the number of drawn personal goal cards has exceeded the limit
     */
    public void prepareGame() throws Exception {
        if (!prepared) {
            if (personalCards.size() > PersonalGoalCard.LIMIT)
                throw new Exception();

            Random r = new Random();

            int cardType1 = -1;
            int cardType2 = -1;
            while (cardType1 == cardType2) {
                cardType1 = r.nextInt(1, CommonGoalCard.LIMIT + 1);
                cardType2 = r.nextInt(1, CommonGoalCard.LIMIT + 1);
            }
            cgCard1 = new CommonGoalCard(cardType1, maxPlayers);
            cgCard2 = new CommonGoalCard(cardType2, maxPlayers);

            for (Integer index : players.keySet()) {
                int personalType;
                do {
                    personalType = r.nextInt(1, PersonalGoalCard.LIMIT + 1);
                }
                while (personalCards.stream().map(PersonalGoalCard::getType).toList().contains(personalType));
                PersonalGoalCard pgc = new PersonalGoalCard(personalType);
                personalCards.add(pgc);
                new ArrayList<>(players.values()).get(index).setGameParameters(pgc, cgCard1, cgCard2, board);
            }

            List<Player> support = new ArrayList<>(players.values());
            players.clear();
            Collections.shuffle(support);
            int counter = 0;
            for (Player p : support) {
                p.setPosition(counter);
                players.put(counter++, p);
            }
        }
        prepared = true;
    }

    /**
     * This method indicates if the game is prepared
     *
     * @return true if the game is prepared, false otherwise
     */
    public boolean isPrepared() {
        return prepared;
    }

    /**
     * This method checks if the active player has completely filled his/her shelf
     *
     * @param activePlayer represents the player who is playing in the current turn
     * @return true if the player has filled all the spaces, false otherwise
     * @throws Exception If the coordinates are invalid
     */
    public boolean checkEndGame(Player activePlayer) throws Exception {
        if (gameEnded) {
            return true;
        }
        if (activePlayer.getShelf().isFull()) {
            activePlayer.firstShelfFull();
            this.gameEnded = true;
            return true;
        }
        return false;
    }

    /**
     * This method is used for endgame procedures, such as final scoring.
     *
     * @return true if the game is completed, false otherwise
     * @throws Exception if updateScore() throws an Exception
     */
    public boolean gameComplete() throws Exception {
        if (gameEnded && players
                .values()
                .stream()
                .filter(x -> !x.isInGame())
                .count() == playerCount) {
            if (this.scoreboard == null) {
                for (Player p : getPlayers().values()) {
                    p.updateScore();
                }
                Map<String, Integer> unsortedPointMap = players.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getValue().getNickname(),
                                e -> e.getValue().getScore()
                        ));
                this.scoreboard = new Scoreboard(unsortedPointMap);
            }
            return true;
        } else return false;
    }

    /**
     * this method is a getter for attribute scoreboard
     *
     * @return scoreboard
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * this method is a getter for attribute gameEnded
     *
     * @return true if game has ended, false otherwise
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Objects refresh following a game reloaded from a persistency file
     *
     * @return the refreshed game
     * @throws RemoteException Related to RMI
     */
    public Game refreshEntities() throws RemoteException {
        this.board = board.getCopy();
        this.objectCards = board.getObjectCards();
        this.cgCard1 = cgCard1.getCopy();
        this.cgCard2 = cgCard2.getCopy();
        for (Player p : players.values()) {
            p.refreshEntities(cgCard1, cgCard2);
        }
        return new Game(this.identifier, this.players, this.maxPlayers, this.personalCards, this.scoreboard, this.board, this.playerCount, this.cgCard1, this.cgCard2, this.objectCards, this.gameEnded, this.prepared);
    }
}
