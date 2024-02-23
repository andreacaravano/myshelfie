package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.ObjectCardInterface;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements effective Personal Goal Card terminal view
 */
public class PersonalGoalCardView {
    /**
     * Prints a PersonalGoalCard on a PrintWriter
     *
     * @param pgc the Personal Goal Card to print
     * @param pw  the PrintWriter to use
     */
    public static void print(PersonalGoalCardInterface pgc, PrintWriter pw) {
        if (pgc == null) {
            pw.println("It is impossible to print the Personal Goal Card to the PrintWriter");
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
                    if (pgc.getOrdinal(i, j) == -1)
                        pw.print("*");
                    else pw.print(ObjectCardInterface.getColoredSequence(pgc.getOrdinal(i, j)));
                    pw.print("  |");
                }
                pw.println();
            }
            pw.println("----------------------------------");
            pw.flush();
        } catch (Exception e) {
            pw.println("It is impossible to print the Personal Goal Card to the PrintWriter");
            pw.flush();
        }
    }

    /**
     * Builds Personal Goal Card string for MultipleView Printing
     *
     * @param pgc Personal Goal Card Interface
     * @return Built string
     */
    public static List<String> buildString(PersonalGoalCardInterface pgc) {
        List<String> builder = new ArrayList<>();
        if (pgc == null) {
            builder.add("It is impossible to print the Personal Goal Card.");
            return Collections.singletonList(builder.get(0));
        }

        try {
            builder.add("---- Your Personal Goal Card -----");
            builder.add(" ");
            for (int i = 0; i < ShelfInterface.SHELF_DIM_Y; i++)
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("     " + (i + 1)));
            builder.add("");
            for (int i = 0; i < ShelfInterface.SHELF_DIM_X; i++) {
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat((i + 1) + "  "));
                builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("|"));
                for (int j = 0; j < ShelfInterface.SHELF_DIM_Y; j++) {
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  "));
                    if (pgc.getOrdinal(i, j) == -1)
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("*"));
                    else
                        builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat(ObjectCardInterface.getColoredSequence(pgc.getOrdinal(i, j))));
                    builder.set(builder.size() - 1, builder.get(builder.size() - 1).concat("  |"));
                }
                builder.add("");
            }
            builder.remove(builder.get(builder.size() - 1));
            builder.add("----------------------------------");
        } catch (Exception e) {
            builder.add("It is impossible to print the Personal Goal Card.");
            return Collections.singletonList(builder.get(builder.size() - 1));
        }
        return builder;
    }
}
