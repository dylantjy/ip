package bruh;

public class Parser {
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

    public static int parseIndex(String args, String verb) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Please provide a task number. Example: " + verb + " 2");
        try { return Integer.parseInt(args); }
        catch (NumberFormatException e) { throw new BruhException("That doesn't look like a number. Try: " + verb + " 2"); }
    }

    public static String[] parseDeadlineArgs(String args) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Deadline needs a description and '/by'. Example: deadline submit report /by Sunday");
        String[] p = args.split(" /by ", 2);
        if (p.length != 2 || p[0].trim().isEmpty() || p[1].trim().isEmpty())
            throw new BruhException("Please include '/by <when>'. Example: deadline submit report /by 2019-10-15");
        return new String[]{ p[0].trim(), p[1].trim() };
    }

    public static String[] parseEventArgs(String args) throws BruhException {
        if (args.isEmpty()) throw new BruhException("Event needs a description and times. Example: event meeting /from 2019-10-15 /to 2019-10-16");
        String[] f = args.split(" /from ", 2);
        if (f.length != 2 || f[0].trim().isEmpty()) throw new BruhException("Missing '/from'.");
        String[] t = f[1].split(" /to ", 2);
        if (t.length != 2 || t[0].trim().isEmpty() || t[1].trim().isEmpty()) throw new BruhException("Missing '/to'.");
        return new String[]{ f[0].trim(), t[0].trim(), t[1].trim() };
    }

    public enum CommandType { BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND }

    private static String afterCommand(String input, String cmd) {
        if (input.equalsIgnoreCase(cmd)) return "";
        return input.substring(cmd.length()).trim();
    }


    public static class Parsed {
        public final CommandType type;
        public final String args;
        public Parsed(CommandType type, String args) { this.type = type; this.args = args; }
    }
}

