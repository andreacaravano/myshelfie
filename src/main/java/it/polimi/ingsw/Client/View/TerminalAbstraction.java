package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Common.CommonGoalCardInterface;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * Terminal View Abstraction
 */
public class TerminalAbstraction {
    /**
     * Terminal Input Stream
     */
    private final BufferedReader terminalInput;
    /**
     * Terminal Output Stream
     */
    private final PrintWriter terminalOutput;

    /**
     * Construction of the view abstraction
     *
     * @param terminalInput  stream
     * @param terminalOutput stream
     */
    public TerminalAbstraction(BufferedReader terminalInput, PrintWriter terminalOutput) {
        this.terminalInput = terminalInput;
        this.terminalOutput = terminalOutput;
    }

    /**
     * Prints Common Goal Cards description
     *
     * @param c1 first
     * @param c2 second
     * @throws RemoteException related to RMI
     */
    public void printCGC(CommonGoalCardInterface c1, CommonGoalCardInterface c2) throws RemoteException {
        if (c1 != null) {
            terminalOutput.print("Game's Common Goal Card 1: ");
            terminalOutput.println(c1.getDescription());
        }
        if (c2 != null) {
            terminalOutput.print("Game's Common Goal Card 2: ");
            terminalOutput.println(c2.getDescription());
        }
    }
}
