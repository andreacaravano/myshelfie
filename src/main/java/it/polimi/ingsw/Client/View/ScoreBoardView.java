package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.ScoreBoardInterface;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;

/**
 * Implments ScoreBoard terminal view
 */
public class ScoreBoardView {
    /**
     * Formulates ScoreBoard View Strategy, given the Interface
     *
     * @param sbi ScoreBoard Interface
     * @param pw  the PrintWriter to write on
     */
    public static void print(ScoreBoardInterface sbi, PrintWriter pw) {
        try {
            Map<String, Integer> localScoreboard = sbi.getScoreBoard();
            if (localScoreboard == null || localScoreboard.isEmpty())
                throw new Exception();
            pw.print("----");
            // looks for the longest nickname
            int longestNickname = localScoreboard.keySet()
                    .stream()
                    .map(String::length)
                    .max(Comparator.naturalOrder())
                    .orElseThrow();
            // looks for the longest score representation
            int longestScore = String.valueOf(localScoreboard.values()
                    .stream()
                    .max(Comparator.naturalOrder())
                    .orElseThrow()).length();
            for (int i = 0; i < longestNickname + longestScore; i++) {
                pw.print("-");
            }
            pw.println("----");
            int counter = 1;
            for (Map.Entry<String, Integer> entry : localScoreboard.entrySet()) {
                pw.format("%d | %s%s | %d%n", counter++, entry.getKey(), " ".repeat(longestNickname - entry.getKey().length()), entry.getValue());
            }
            pw.print("----");
            for (int i = 0; i < longestNickname + longestScore; i++) {
                pw.print("-");
            }
            pw.print("----");
            pw.println();
            pw.flush();
        } catch (Exception e) {
            pw.println("It is impossible to print the scoreboard to the PrintWriter");
            pw.flush();
        }
    }
}
