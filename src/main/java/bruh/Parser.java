package bruh;

import java.util.Scanner;
import java.util.List;

/**
 * Parses raw user input strings into structured commands for the Bruh application.
 * <p>
 * Exposes helpers to parse indices and extract arguments for specific commands
 * such as {@code deadline} and {@code event}.
 */
public class Parser {

    /**
     * Parses the given input string into a {@link Parsed} command.
     *
     * @param input the raw user input (may be {@code null} or blank)
     * @return a parsed command with type and arguments
     * @throws BruhException if the command is empty or unrecognized
     */
    public static Parsed parse(String input) throws BruhException {
        if (input == null) return new Parsed(CommandType.BYE, "");
        String trimmed = input.trim();
        if (trimmed.isEmpty()) throw new BruhException(
                "Empty command. Try: list, todo <desc>, deadline <desc> /by <when>, event <desc> /from <start> /to <end>");

        String cmd, args = "";
        int sp = trimmed.indexOf(' ');
        if (sp == -1) {
            cmd = trimmed.toLowerCase();
        } else {
            cmd = trimmed.substring(0, sp).toLowerCase();
            args = trimmed.substring(sp + 1).trim();
        }

        switch (cmd) {
            case "bye":    return new Parsed(CommandType.BYE, "");
            case "list":   return new Parsed(CommandType.LIST, "");
            case "mark":   return new Parsed(CommandType.MARK, args);
            case "unmark": return new Parsed(CommandType.UNMARK, args);
            case "delete": return new Parsed(CommandType.DELETE, args);
            case "todo":   return new Parsed(CommandType.TODO, args);
            case "deadline": return new Parsed(CommandType.DEADLINE, args);
            case "event":    return new Parsed(CommandType.EVENT, args);
            case "find": return new Parsed(CommandType.FIND, afterCommand(input, "find"));
            default: throw new BruhException(
                    "Unknown command. Try: list, todo, deadline, event, mark N, unmark N, delete N, or bye");
        }
    }

    /**
     * Parses an integer task index from the given argument string.
     *
     * @param args the argument string expected to contain an integer
     * @param verb the verb for error messaging (e.g., {@code mark}, {@code delete})
     * @return the parsed integer index
     * @throws BruhException if the argument is missing or not a valid integer
     */
    public static int parseIndex(String args, String verb) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Please provide a task number. Example: " + verb + " 2");
        try { return Integer.parseInt(args); }
        catch (NumberFormatException e) { throw new BruhException("That doesn't look like a number. Try: " + verb + " 2"); }
    }

    /**
     * Splits arguments for the {@code deadline} command into description and deadline.
     * Expects the format: {@code <desc> /by <when>}.
     *
     * @param args the raw argument string after the {@code deadline} keyword
     * @return a two-element array: {@code [description, by]}
     * @throws BruhException if required parts are missing or blank
     */
    public static String[] parseDeadlineArgs(String args) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Deadline needs a description and '/by'. Example: deadline submit report /by Sunday");
        String[] p = args.split(" /by ", 2);
        if (p.length != 2 || p[0].trim().isEmpty() || p[1].trim().isEmpty())
            throw new BruhException("Please include '/by <when>'. Example: deadline submit report /by 2019-10-15");
        return new String[]{ p[0].trim(), p[1].trim() };
    }

    /**
     * Splits arguments for the {@code event} command into description, start, and end.
     * Expects the format: {@code <desc> /from <start> /to <end>}.
     *
     * @param args the raw argument string after the {@code event} keyword
     * @return a three-element array: {@code [description, from, to]}
     * @throws BruhException if required parts are missing or blank
     */
    public static String[] parseEventArgs(String args) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Event needs a description and times. Example: event meeting /from 2019-10-15 /to 2019-10-16");
        String[] f = args.split(" /from ", 2);
        if (f.length != 2 || f[0].trim().isEmpty()) throw new BruhException("Missing '/from'.");
        String[] t = f[1].split(" /to ", 2);
        if (t.length != 2 || t[0].trim().isEmpty() || t[1].trim().isEmpty()) throw new BruhException("Missing '/to'.");
        return new String[]{ f[0].trim(), t[0].trim(), t[1].trim() };
    }

    /** Enumerates the supported command types understood by {@link Parser}. */
    public enum CommandType { BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND }

    /**
     * Returns the substring of {@code input} after the given command word.
     *
     * @param input full user input (unchanged)
     * @param cmd   command word to strip
     * @return remainder of the input after the command word
     */
    private static String afterCommand(String input, String cmd) {
        if (input.equalsIgnoreCase(cmd)) return "";
        return input.substring(cmd.length()).trim();
    }

    /**
     * Immutable result of parsing a command.
     * <p>
     * Contains the {@link CommandType} and any remaining argument string.
     */
    public static class Parsed {
        /** The type of the parsed command. */
        public final CommandType type;
        /** Raw argument string associated with the command (may be empty). */
        public final String args;

        /**
         * Constructs a parsed command value.
         *
         * @param type the parsed command type
         * @param args the associated argument string (non-null; may be empty)
         */
        public Parsed(CommandType type, String args) { this.type = type; this.args = args; }
    }
}

