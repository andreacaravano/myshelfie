package it.polimi.ingsw.Common;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.rmi.Remote;

/**
 * Object Cards Interface
 */
public interface ObjectCardInterface extends Remote {
    /**
     * This method associates an int to a letter referred to an ObjectCardType
     *
     * @param c char read from file
     * @return an int
     * @throws Exception when char doesn't match any first letter of the ObjectCardTypes
     */
    public static int getOrdinal(char c) throws Exception {
        switch (c) {
            case 'C' -> {
                return 0;
            }
            case 'B' -> {
                return 1;
            }
            case 'G' -> {
                return 2;
            }
            case 'F' -> {
                return 3;
            }
            case 'T' -> {
                return 4;
            }
            case 'P' -> {
                return 5;
            }
        }
        throw new Exception();
    }

    /**
     * Returns a char corresponding to the ordinal of a given card type.
     *
     * @param ordinal position in ObjectCardType enumeration
     * @return the corresponding char
     * @throws Exception if ordinal given does not correspond to an ObjectCardType
     */
    public static char getChar(int ordinal) throws Exception {
        switch (ordinal) {
            case 0 -> {
                return 'C';
            }
            case 1 -> {
                return 'B';
            }
            case 2 -> {
                return 'G';
            }
            case 3 -> {
                return 'F';
            }
            case 4 -> {
                return 'T';
            }
            case 5 -> {
                return 'P';
            }
        }
        throw new Exception();
    }

    /**
     * Returns a colored escape sequence corresponding to the ordinal of a given card type.
     *
     * @param ordinal position in ObjectCardType enumeration
     * @return the corresponding colored escape sequence
     * @throws Exception if ordinal given does not correspond to an ObjectCardType
     */
    public static String getColoredSequence(int ordinal) throws Exception {
        String ret = "";
        switch (ordinal) {
            case 0 -> ret = Ansi.colorize("C", Attribute.TEXT_COLOR(48));
            case 1 -> ret = Ansi.colorize("B", Attribute.TEXT_COLOR(226));
            case 2 -> ret = Ansi.colorize("G", Attribute.TEXT_COLOR(220));
            case 3 -> ret = Ansi.colorize("F", Attribute.TEXT_COLOR(45));
            case 4 -> ret = Ansi.colorize("T", Attribute.TEXT_COLOR(81));
            case 5 -> ret = Ansi.colorize("P", Attribute.TEXT_COLOR(207));
        }
        if (ret.isEmpty())
            throw new Exception();
        return ret;
    }
}
