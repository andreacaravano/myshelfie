package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.BoardInterface;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.PrintWriter;
import java.util.List;

/**
 * Terminal View effective implementation
 */
public class MultipleView {
    private static final int completePrintBound = 96;

    /**
     * Automatically selects the correct type of terminal printing, based on terminal size
     *
     * @param b   Board Interface
     * @param s   Shelf Interface
     * @param pgc Personal Goal Card Interface
     * @param pw  PrintWriter to write on
     */
    public static void automaticPrint(BoardInterface b, ShelfInterface s, PersonalGoalCardInterface pgc, PrintWriter pw) {
        if (MultipleView.class.getResource("MultipleView.class").toString().startsWith("jar")) {
            int terminalWidth = 80;
            try (Terminal t = TerminalBuilder.terminal()) {
                terminalWidth = t.getWidth();
            } catch (Exception ignored) {
            }
            if (terminalWidth <= completePrintBound) {
                sequentialPrint(b, s, pgc, pw);
            } else {
                completePrint(b, s, pgc, pw);
            }
        } else completePrint(b, s, pgc, pw);
    }

    /**
     * Utilitary method for sequential printing
     *
     * @param b   Board Interface
     * @param s   Shelf Interface
     * @param pgc Personal Goal Card Interface
     * @param pw  PrintWriter to write on
     */
    private static void sequentialPrint(BoardInterface b, ShelfInterface s, PersonalGoalCardInterface pgc, PrintWriter pw) {
        List<String> boardList = BoardView.buildString(b, true);
        List<String> shelfList = ShelfView.buildString(s);
        List<String> pgcList = PersonalGoalCardView.buildString(pgc);
        for (int i = 0; i < boardList.size(); i++) {
            pw.println((i <= boardList.size() - 1) ? boardList.get(i) : "");
            pw.flush();
        }
        for (int i = 0; i < shelfList.size(); i++) {
            pw.println((i <= shelfList.size() - 1) ? shelfList.get(i) : "");
            pw.flush();
        }
        for (int i = 0; i < pgcList.size(); i++) {
            pw.println((i <= pgcList.size() - 1) ? pgcList.get(i) : "");
            pw.flush();
        }
        pw.flush();
    }

    /**
     * Utilitary method for complete printing
     *
     * @param b   Board Interface
     * @param s   Shelf Interface
     * @param pgc Personal Goal Card Interface
     * @param pw  PrintWriter to write on
     */
    private static void completePrint(BoardInterface b, ShelfInterface s, PersonalGoalCardInterface pgc, PrintWriter pw) {
        List<String> boardList = BoardView.buildString(b, false);
        List<String> shelfList = ShelfView.buildString(s);
        List<String> pgcList = PersonalGoalCardView.buildString(pgc);
        for (int i = 0; i < Math.max(boardList.size(), shelfList.size() + pgcList.size()); i++) {
            if (i <= shelfList.size() - 1)
                pw.println(String.format("%s   %s", (i <= boardList.size() - 1) ? boardList.get(i) : "", (i <= shelfList.size() - 1) ? shelfList.get(i) : ""));
            else
                pw.println(String.format("%s   %s", (i <= boardList.size() - 1) ? boardList.get(i) : "", (i - shelfList.size() <= pgcList.size() - 1) ? pgcList.get(i - shelfList.size()) : ""));
            pw.flush();
        }
        pw.flush();
    }
}
