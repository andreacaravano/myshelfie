package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.ObjectCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements Shelf terminal view
 */
public class ShelfView {
    /**
     * Prints a Shelf on a PrintWriter
     *
     * @param s  the Shelf to print
     * @param pw the PrintWriter to use
     */
    public static void print(ShelfInterface s, PrintWriter pw) {
        if (s == null) {
            pw.println("It is impossible to print the shelf to the PrintWriter");
            return;
        }

        try {
            pw.println("----------------------------------");
            pw.print(" ");
            for (int i = 0; i < ShelfInterface.SHELF_DIM_Y; i++)
                pw.print("     " + (i + 1));
            pw.println();
            for (int i = 0; i < ShelfInterface.SHELF_DIM_X; i++) {
                pw.print((i + 1) + "  ");
                pw.print("|");
                for (int j = 0; j < ShelfInterface.SHELF_DIM_Y; j++) {
                    pw.print("  ");
                    if (s.getCardOrdinal(i, j) == -1)
                        pw.print("*");
                    else pw.print(ObjectCardInterface.getColoredSequence(s.getCardOrdinal(i, j)));
                    pw.print("  |");
                }
                pw.println();
            }
            pw.println("----------------------------------");
            pw.flush();
        } catch (Exception e) {
            pw.println("It is impossible to print the shelf to the PrintWriter");
            pw.flush();
        }
    }

    /**
     * Builds Shelf string for MultipleView Printing
     *
     * @param s Shelf Interface
     * @return Built string
     */
    public static List<String> buildString(ShelfInterface s) {
        List<String> builder = new ArrayList<>();
        if (s == null) {
            builder.add("It is impossible to print the shelf.");
            return Collections.singletonList(builder.get(0));
        }

        try {
            builder.add("---------- Your Shelf ------------");
            builder.add(" ");
            for (int i = 0; i < ShelfInterface.SHELF_DIM_Y; i++)
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("     " + (i + 1)));
            builder.add("");
            for (int i = 0; i < ShelfInterface.SHELF_DIM_X; i++) {
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat((i + 1) + "  "));
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("|"));
                for (int j = 0; j < ShelfInterface.SHELF_DIM_Y; j++) {
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  "));
                    if (s.getCardOrdinal(i, j) == -1)
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("*"));
                    else
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat(ObjectCardInterface.getColoredSequence(s.getCardOrdinal(i, j))));
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  |"));
                }
                builder.add("");
            }
            builder.remove(builder.get(builder.size() - 1));
            builder.add("----------------------------------");
        } catch (Exception e) {
            builder.add("It is impossible to print the shelf.");
            return Collections.singletonList(builder.get(builder.size() - 1));
        }
        return builder;
    }
}
