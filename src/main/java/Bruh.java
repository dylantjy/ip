import java.util.Scanner;

public class Bruh {

    public static void main(String[] args) {
        Storage storage = new Storage("data/bruh.txt");
        TaskList tasks = new TaskList(storage.load());
        Ui ui = new Ui();

        ui.showWelcome();

        Scanner sc = new Scanner(System.in); // keep Scanner for now (no Parser step yet)
        while (true) {
            if (!sc.hasNextLine()) {
                ui.showBye();
                break;
            }
            String input = sc.nextLine().trim();

            try {
                if (input.equalsIgnoreCase("bye")) {
                    ui.showBye();
                    break;

                } else if (input.equalsIgnoreCase("list")) {
                    ui.showList(tasks);

                } else if (startsWithWord(input, "mark")) {
                    int idx = parseIndexStrict(input, "mark");
                    ensureIndex(idx, tasks.size());
                    tasks.mark(idx);
                    ui.showMarked(tasks.get(idx));
                    storage.save(tasks.asList());

                } else if (startsWithWord(input, "unmark")) {
                    int idx = parseIndexStrict(input, "unmark");
                    ensureIndex(idx, tasks.size());
                    tasks.unmark(idx);
                    ui.showUnmarked(tasks.get(idx));
                    storage.save(tasks.asList());

                } else if (startsWithWord(input, "delete")) {
                    int idx = parseIndexStrict(input, "delete");
                    ensureIndex(idx, tasks.size());
                    Task removed = tasks.delete(idx);
                    ui.showRemoved(removed, tasks.size());
                    storage.save(tasks.asList());

                } else if (startsWithWord(input, "todo")) {
                    String desc = afterCommand(input, "todo");
                    if (desc.isEmpty()) {
                        throw new BruhException("Todo needs a description. Try: todo borrow book");
                    }
                    tasks.add(new Todo(desc));
                    ui.showAdded(tasks.get(tasks.size()), tasks.size());
                    storage.save(tasks.asList());

                } else if (startsWithWord(input, "deadline")) {
                    String rest = afterCommand(input, "deadline");
                    String[] parts = rest.split(" /by ", 2);
                    if (rest.isEmpty()) {
                        throw new BruhException("Deadline needs a description and '/by'. Try: deadline return book /by Sunday");
                    }
                    if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        throw new BruhException("Please include '/by <when>'. Example: deadline submit report /by 2019-10-15");
                    }
                    tasks.add(new Deadline(parts[0].trim(), parts[1].trim()));
                    ui.showAdded(tasks.get(tasks.size()), tasks.size());
                    storage.save(tasks.asList());

                } else if (startsWithWord(input, "event")) {
                    String rest = afterCommand(input, "event");
                    if (rest.isEmpty()) {
                        throw new BruhException("Event needs a description and times. Try: event project meeting /from 2019-10-15 /to 2019-10-16");
                    }
                    String[] fromSplit = rest.split(" /from ", 2);
                    if (fromSplit.length != 2 || fromSplit[0].trim().isEmpty()) {
                        throw new BruhException("Missing '/from'. Example: event orientation week /from 2019-10-04 /to 2019-10-11");
                    }
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    if (toSplit.length != 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                        throw new BruhException("Missing '/to'. Example: event team sync /from 2019-10-15 /to 2019-10-16");
                    }
                    tasks.add(new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim()));
                    ui.showAdded(tasks.get(tasks.size()), tasks.size());
                    storage.save(tasks.asList());

                } else if (input.isEmpty()) {
                    throw new BruhException("I got an empty line. Try: list, todo <desc>, deadline <desc> /by <when>, event <desc> /from <start> /to <end>");

                } else {
                    throw new BruhException("Hmm, I don't recognize that. Try: list, todo, deadline, event, mark N, unmark N, delete N, or bye");
                }

            } catch (BruhException ex) {
                ui.showError(ex.getMessage());
            } catch (Exception io) {
                // Save failures shouldn't crash the UI
                ui.showError("Oops, I couldn't save your tasks. They'll still work for this session.");
            }
        }

        sc.close();
    }

    // ---------- helpers (unchanged) ----------

    private static void ensureIndex(int idx, int size) throws BruhException {
        if (idx < 1 || idx > size) {
            throw new BruhException("That task number doesn't exist. Use 'list' to see valid numbers.");
        }
    }

    private static boolean startsWithWord(String input, String word) {
        return input.equalsIgnoreCase(word) || input.toLowerCase().startsWith(word.toLowerCase() + " ");
    }

    private static String afterCommand(String input, String cmd) {
        if (input.equalsIgnoreCase(cmd)) return "";
        return input.substring(cmd.length()).trim();
    }

    private static int parseIndexStrict(String input, String cmd) throws BruhException {
        String rest = afterCommand(input, cmd);
        if (rest.isEmpty()) throw new BruhException("Please provide a task number. Example: " + cmd + " 2");
        try {
            return Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new BruhException("That doesn't look like a number. Try: " + cmd + " 2");
        }
    }
}
