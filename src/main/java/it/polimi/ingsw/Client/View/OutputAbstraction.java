package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Client.GUI;

import java.io.PrintWriter;

/**
 * Terminal/GUI abstractions, to show informative or error messages
 */
public class OutputAbstraction {
    private final boolean useGUI;
    private final PrintWriter pw;

    /**
     * Abstracts output between terminal and GUI
     *
     * @param useGUI true if GUI is being used
     * @param pw     PrintWriter to write on
     */
    public OutputAbstraction(boolean useGUI, PrintWriter pw) {
        this.useGUI = useGUI;
        this.pw = pw;
    }

    /**
     * Outputs an Informative message
     *
     * @param i informative message
     */
    public void showInfo(String i) {
        if (useGUI) {
            GUI.infoMessage(i);
        } else pw.format("%s%n", i);
    }

    /**
     * Outputs an Error message
     *
     * @param e error message
     */
    public void showError(String e) {
        if (useGUI) {
            GUI.errorMessage(e);
        } else pw.format("Error: %s%n", e);
    }

    /**
     * Outputs an Error and waits (on GUI) for User confirmation
     *
     * @param e error message
     */
    public void showErrorConfirm(String e) {
        if (useGUI) {
            GUI.errorMessageConfirm(e);
        } else pw.format("Error: %s%n", e);
    }
}
