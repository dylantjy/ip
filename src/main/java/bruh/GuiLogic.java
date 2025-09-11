package bruh;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/** Adapts the existing Bruh logic for a GUI: input -> response string. */
public class GuiLogic {
    private final Storage storage = new Storage("data/bruh.txt");
    private final TaskList tasks;
    private boolean exitRequested = false;

    public GuiLogic() {
        // Load tasks (Storage#load never throws; it returns an empty list on I/O issues)
        this.tasks = new TaskList(storage.load());
    }

    /** Returns true if the last handled command requested exit (i.e., "bye"). */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /** Handle one line of user input and return Bruh's response as a String. */
    public String handle(String input) {
        try {
            Parser.Parsed p = Parser.parse(input);

            switch (p.type) {
                case BYE: {
                    exitRequested = true;
                    return "Bye bruh. Hope to see you again soon!";
                }
                case LIST: {
                    if (tasks.size() == 0) {
                        return "Your list is empty. Add something with 'todo', 'deadline', or 'event'.";
                    }
                    StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
                    for (int i = 1; i <= tasks.size(); i++) {
                        sb.append(i).append(". ").append(tasks.get(i)).append("\n");
                    }
                    return sb.toString().trim();
                }
                case MARK: {
                    int idx = Parser.parseIndex(p.args, "mark");
                    tasks.mark(idx);
                    save();
                    return "Nice! I've marked this task as done:\n" + "  " + tasks.get(idx);
                }
                case UNMARK: {
                    int idx = Parser.parseIndex(p.args, "unmark");
                    tasks.unmark(idx);
                    save();
                    return "OK, I've marked this task as not done yet:\n" + "  " + tasks.get(idx);
                }
                case DELETE: {
                    int idx = Parser.parseIndex(p.args, "delete");
                    Task removed = tasks.delete(idx);
                    save();
                    return "Noted. I've removed this task:\n" + "  " + removed
                         + "\nNow you have " + tasks.size() + " tasks in the list.";
                }
                case TODO: {
                    if (p.args.isEmpty()) {
                        throw new BruhException("Todo needs a description. Example: todo borrow book");
                    }
                    tasks.add(new Todo(p.args));
                    save();
                    return "Got it. I've added this task:\n" + "  " + tasks.get(tasks.size())
                         + "\nNow you have " + tasks.size() + " tasks in the list.";
                }
                case DEADLINE: {
                    String[] parts = Parser.parseDeadlineArgs(p.args);
                    tasks.add(new Deadline(parts[0], parts[1]));
                    save();
                    return "Got it. I've added this task:\n" + "  " + tasks.get(tasks.size())
                         + "\nNow you have " + tasks.size() + " tasks in the list.";
                }
                case EVENT: {
                    String[] parts = Parser.parseEventArgs(p.args);
                    tasks.add(new Event(parts[0], parts[1], parts[2]));
                    save();
                    return "Got it. I've added this task:\n" + "  " + tasks.get(tasks.size())
                         + "\nNow you have " + tasks.size() + " tasks in the list.";
                }
                case FIND: {
                    if (p.args.isEmpty()) {
                        throw new BruhException("Find needs a keyword. Example: find book");
                    }
                    String[] kws = p.args.trim().split("\\s+");   // split by whitespace
                    List<Task> matches = tasks.findAny(kws);

                    if (matches.isEmpty()) {
                        return "No matching tasks found.";
                    }
                    StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
                    int i = 1;
                    for (Task t : matches) {
                        sb.append(" ").append(i++).append(". ").append(t).append("\n");
                    }
                    return sb.toString().trim();
                }

                // --- NEW: Snooze / Unsnooze ---------------------------------
                case SNOOZE: {
                    // Expect: "<index> <+3d2h30m | 2025-09-20T09:00 | 2025-09-20 09:00 | 2025-09-20>"
                    String[] aa = p.args.trim().split("\\s+", 2);
                    if (aa.length < 2) {
                        throw new BruhException("Usage: snooze <index> <+3d2h30m | 2025-09-20T09:00 | 2025-09-20 09:00 | 2025-09-20>");
                    }
                    int snoozeIdx = Parser.parseIndex(aa[0], "snooze");  // reuse your helper
                    String when = aa[1].trim();
                    if (when.startsWith("+")) {
                        Duration d = TimeParsing.parseRelative(when);
                        tasks.snoozeFor(snoozeIdx, TimeParsing.parseRelative(when));
                    } else {
                        LocalDateTime dt = TimeParsing.parseDateTime(when);
                        tasks.snoozeUntil(snoozeIdx, TimeParsing.parseDateTime(when));
                    }
                    save();
                    return "Snoozed this task until:\n" + "  " + tasks.get(snoozeIdx);
                }
                case UNSNOOZE: {
                    int unsnoozeIdx = Parser.parseIndex(p.args, "unsnooze");
                    tasks.unsnooze(unsnoozeIdx);
                    save();
                    return "Unsnoozed this task:\n" + "  " + tasks.get(unsnoozeIdx);
                }
                case RESCHED: {
                // Usage: resched <index> <+Nd | YYYY-MM-DD | YYYY-MM-DDTHH:MM | YYYY-MM-DD HH:MM>
                if (p.args.isEmpty()) {
                    throw new BruhException("Usage: resched <index> <+Nd | YYYY-MM-DD | YYYY-MM-DDTHH:MM>");
                }
                String[] aa = p.args.trim().split("\\s+", 2);
                if (aa.length < 2) {
                    throw new BruhException("Usage: resched <index> <+Nd | YYYY-MM-DD | YYYY-MM-DDTHH:MM>");
                }
                int idx = Parser.parseIndex(aa[0], "resched");
                String when = aa[1].trim();

                if (when.startsWith("+")) {
                    // relative days: +2d, +10d
                    int days = parsePlusDays(when); // helper just below
                    tasks.shiftDeadlineByDays(idx, days);
                } else {
                    // absolute: keep as text (normalize to ISO date if you want)
                    // Normalize to yyyy-MM-dd if a datetime was given
                    java.time.LocalDate d = DateParsing.tryParseToDate(when);
                    if (d == null) throw new BruhException("Unrecognized date format: " + when);
                    tasks.rescheduleDeadlineAbsolute(idx, d.toString());
                }
                save();
                return "Rescheduled this deadline:\n" + "  " + tasks.get(idx);
                }
                // ----------------------------------------------------------------
                default:
                    return "Unknown command.";
            }
        } catch (BruhException ex) {
            return ex.getMessage();
        } catch (Exception io) {
            return "Couldn't save your tasks. They'll still work for this session.";
        }
    }

    private void save() throws IOException {
        storage.save(tasks.asList());
    }

    private static int parsePlusDays(String s) throws BruhException {
    // Accept only +Nd (days) for simplicity
    String t = s.trim().toLowerCase();
    if (!t.startsWith("+") || !t.endsWith("d")) {
        throw new BruhException("Use +Nd for relative days (e.g., +2d).");
    }
    String num = t.substring(1, t.length() - 1);
    try {
        int days = Integer.parseInt(num);
        if (days == 0) throw new NumberFormatException();
        return days;
    } catch (NumberFormatException e) {
        throw new BruhException("Bad relative days: " + s + " (try +2d)");
    }
}

}

