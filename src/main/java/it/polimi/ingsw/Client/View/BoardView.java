package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.BoardInterface;
import it.polimi.ingsw.Common.ObjectCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Board View
 */
public class BoardView {
    /**
     * Prints a Board on a PrintWriter
     *
     * @param b  the Board to print
     * @param pw the PrintWriter to use
     */
    public static void print(BoardInterface b, PrintWriter pw) {
        if (b == null) {
            pw.println("It is impossible to print the board to the PrintWriter");
            return;
        }

        try {
            pw.println("----------------------------------------------------------");
            pw.print(" ");
            for (int i = 0; i < BoardInterface.BOARD_DIM_Y; i++)
                pw.print("     " + (i + 1));
            pw.println();
            for (int i = 0; i < BoardInterface.BOARD_DIM_X; i++) {
                pw.print((i + 1) + "  ");
                pw.print("|");
                for (int j = 0; j < BoardInterface.BOARD_DIM_Y; j++) {
                    pw.print("  ");
                    if (!b.isSpaceUsable(i, j))
                        pw.print("°");
                    if (b.isSpaceUsable(i, j) && b.getCardOrdinalFromSpace(i, j) == -1)
                        pw.print("*");
                    else if (b.getCardOrdinalFromSpace(i, j) != -1)
                        pw.print(ObjectCardInterface.getColoredSequence(b.getCardOrdinalFromSpace(i, j)));
                    pw.print("  |");
                }
                pw.println();
            }
            pw.println("----------------------------------------------------------");
            pw.flush();
        } catch (Exception e) {
            pw.println("It is impossible to print the board to the PrintWriter");
            pw.flush();
        }
    }

    /**
     * Builds Board string for MultipleView printing
     *
     * @param b               Game's Board Interface
     * @param sequentialPrint enables sequential printing in MultipleView
     * @return Board's string
     */
    public static List<String> buildString(BoardInterface b, boolean sequentialPrint) {
        List<String> builder = new ArrayList<>();
        if (b == null) {
            builder.add("It is impossible to print the board.");
            return Collections.singletonList(builder.get(0));
        }

        try {
            while (!sequentialPrint && builder.size() < (ShelfInterface.SHELF_DIM_X * 2) / 2 / 2) {
                builder.add("   ".concat(" ").concat("  ".repeat(BoardInterface.BOARD_DIM_Y)).concat(" ".repeat(BoardInterface.BOARD_DIM_Y)).concat("   ".repeat(BoardInterface.BOARD_DIM_Y)));
            }
            builder.add("---------------------- Game's Board ----------------------");
            builder.add(" ");
            for (int i = 0; i < BoardInterface.BOARD_DIM_Y; i++)
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("     " + (i + 1)));
            builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("   "));
            builder.add("");
            for (int i = 0; i < BoardInterface.BOARD_DIM_X; i++) {
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat((i + 1) + "  "));
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("|"));
                for (int j = 0; j < BoardInterface.BOARD_DIM_Y; j++) {
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  "));
                    if (!b.isSpaceUsable(i, j))
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("°"));
                    if (b.isSpaceUsable(i, j) && b.getCardOrdinalFromSpace(i, j) == -1)
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("*"));
                    else if (b.getCardOrdinalFromSpace(i, j) != -1)
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat(ObjectCardInterface.getColoredSequence(b.getCardOrdinalFromSpace(i, j))));
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  |"));
                }
                builder.add("");
            }
            builder.remove(builder.get(builder.size() - 1));
            builder.add("----------------------------------------------------------");
            while (!sequentialPrint && builder.size() < 6 + ShelfInterface.SHELF_DIM_X * 2) {
                builder.add("   ".concat(" ").concat("  ".repeat(BoardInterface.BOARD_DIM_Y)).concat(" ".repeat(BoardInterface.BOARD_DIM_Y)).concat("   ".repeat(BoardInterface.BOARD_DIM_Y)));
            }
        } catch (Exception e) {
            builder.add("It is impossible to print the board.");
            return Collections.singletonList(builder.get(builder.size() - 1));
        }
        return builder;
    }
}
