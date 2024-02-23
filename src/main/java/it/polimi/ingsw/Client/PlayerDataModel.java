package it.polimi.ingsw.Client;

/**
 * Class to define player's data model for ScoreBoard use
 */
public class PlayerDataModel {

    /**
     * Player's nickname
     */
    private final String nickname;

    /**
     * Player's points
     */
    private final Integer points;

    /**
     * Class constructor
     *
     * @param nickname player's nickname
     * @param points   player's points
     */

    public PlayerDataModel(String nickname, int points) {
        this.nickname = nickname;
        this.points = points;
    }

    /**
     * Getter method for player's nickname
     *
     * @return player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Getter method for player's points
     *
     * @return player's points
     */
    public int getPoints() {
        return points;
    }
}
