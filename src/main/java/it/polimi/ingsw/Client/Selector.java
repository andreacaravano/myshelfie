package it.polimi.ingsw.Client;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Implements selecion between clients technologies
 */
public class Selector {
    private static final int kinds = 4;

    /**
     * Main method, welcoming User
     *
     * @param args default arguments array
     */
    public static void main(String[] args) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            System.out.println("Activating Windows ANSI coloring support...");
            enableANSIColoring();
        }
        System.out.println("Welcome to My Shelfie! Please select the kind of graphical interface you want to use: ");
        System.out.println("1) RMI textual client");
        System.out.println("2) RMI graphical client");
        System.out.println("3) Socket textual client");
        System.out.println("4) Socket graphical client");

        Scanner s = new Scanner(System.in);
        int chosen = -1;
        do {
            System.out.print("Your choose (as number): ");
            try {
                chosen = Integer.parseInt(s.next());
            } catch (Exception e) {
                chosen = -1;
            }
        } while (chosen <= 0 || chosen > kinds);

        switch (chosen) {
            // first string indicates whether to launch GUI or mantain only terminal interface
            case 1 -> RMIClient.main(mergeArrays(new String[]{"false"}, args));
            case 2 -> RMIClient.main(mergeArrays(new String[]{"true"}, args));
            case 3 -> SocketClient.main(mergeArrays(new String[]{"false"}, args));
            case 4 -> SocketClient.main(mergeArrays(new String[]{"true"}, args));
            default -> System.out.println("Error selecting correct graphical interface.");
        }
    }

    /**
     * Enables Windows's ANSI Sequences escaping for coloring in terminal
     */
    private static void enableANSIColoring() {
        Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
        WinDef.DWORD STD_OUTPUT_HANDLE = new WinDef.DWORD(-11);
        WinNT.HANDLE hOut = (WinNT.HANDLE) GetStdHandleFunc.invoke(WinNT.HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

        WinDef.DWORDByReference p_dwMode = new WinDef.DWORDByReference(new WinDef.DWORD(0));
        Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
        GetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, p_dwMode});

        int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
        WinDef.DWORD dwMode = p_dwMode.getValue();
        dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
        Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
        SetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, dwMode});
    }

    private static <T> T[] mergeArrays(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }
}
