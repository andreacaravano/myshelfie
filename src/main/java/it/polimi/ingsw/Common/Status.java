package it.polimi.ingsw.Common;

import com.google.gson.annotations.Expose;

/**
 * Informative of game's evolution in communication abstraction
 */
public enum Status {
    EOS("End of Sequence"), // End of Sequence
    Sending_Identifier("Sending game identifier"),
    Request_NumberOfPlayers("Number of players request"),
    Response_NumberOfPlayers("Number of players response"),
    Request_Nickname("Nickname request"),
    Response_Nickname("Nickname response"),
    Accepted_Request("Accepted request"), // player accepted in the lobby. Waiting for start
    Denied_Request("Denied request"),
    NotYourTurn("It's not your turn"),
    YourTurn("It's your turn"),
    Move_Request("Client's move request"),
    SuccessfulMove("Correct move"),
    FailedMove("Incorrect move"),
    SendingBoard("Sending board serialization"),
    SendingShelf("Sending shelf serialization"),
    SendingPersonalGoalCard("Sending Personal Goal Card print string"),
    SendingCommonGoalCardSpecification("Sending Common Goal Card specification string"),
    AchievementUpdate("Current turn achievements"),
    PlayerScoreCards("Current player ScoreCards update"),
    LastTurn("Last turn"),//broadcast
    MoveAllowed("Move allowed"),
    MoveNotAllowed("Move not allowed"),
    GameEnded("Game ended"),
    FinalScoreboard("Final Scoreboard"),
    ForcedGameEnd("Forced game end"),
    KeepAlive("KeepAlive message"),
    KATimeout("KeepAlive Timeout reached"),
    InvalidStatus("Invalid status"),
    Error("Error");
    @Expose
    final String description;

    Status(String description) {
        this.description = description;
    }

    /**
     * @return Status' description
     */
    public String getDescription() {
        return description;
    }
}
