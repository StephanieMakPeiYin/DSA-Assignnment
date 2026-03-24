package util;

public class ConsoleColors {
    // ANSI color codes
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

    public static String success(String message) {
        return GREEN + message + RESET;
    }

    public static String error(String message) {
        return RED + message + RESET;
    }
}
