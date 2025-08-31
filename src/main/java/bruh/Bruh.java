package bruh;

import java.util.Scanner;
import java.util.List;


public class Bruh {

    public static void main(String[] args) {
        Storage storage = new Storage("data/bruh.txt");
        Ui ui = new Ui();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        ui.showWelcome();

        boolean running = true;
        while (running) {
            try {
                String input = ui.readCommand();
                Parser.Parsed p = Parser.parse(input);

                switch (p.type) {
                    case BYE:
                        ui.showBye();
                        running = false;
                        break;
                    case LIST:
                        ui.showList(tasks);
                        break;
                    case MARK: {
                        int idx = Parser.parseIndex(p.args, "mark");
                        tasks.mark(idx);
                        ui.showMarked(tasks.get(idx));
                        storage.save(tasks.asList());
                        break;
                    }
                    case UNMARK: {
                        int idx = Parser.parseIndex(p.args, "unmark");
                        tasks.unmark(idx);
                        ui.showUnmarked(tasks.get(idx));
                        storage.save(tasks.asList());
                        break;
                    }
                    case DELETE: {
                        int idx = Parser.parseIndex(p.args, "delete");
                        Task removed = tasks.delete(idx);
                        ui.showRemoved(removed, tasks.size());
                        storage.save(tasks.asList());
                        break;
                    }
                    case TODO: {
                        if (p.args.isEmpty())
                            throw new BruhException("Todo needs a description. Example: todo borrow book");
                        tasks.add(new Todo(p.args));
                        ui.showAdded(tasks.get(tasks.size()), tasks.size());
                        storage.save(tasks.asList());
                        break;
                    }
                    case DEADLINE: {
                        String[] parts = Parser.parseDeadlineArgs(p.args);
                        tasks.add(new Deadline(parts[0], parts[1]));
                        ui.showAdded(tasks.get(tasks.size()), tasks.size());
                        storage.save(tasks.asList());
                        break;
                    }
                    case EVENT: {
                        String[] parts = Parser.parseEventArgs(p.args);
                        tasks.add(new Event(parts[0], parts[1], parts[2]));
                        ui.showAdded(tasks.get(tasks.size()), tasks.size());
                        storage.save(tasks.asList());
                        break;
                    }

                    case FIND: {
                        if (p.args.isEmpty()){
                            throw new BruhException("Find needs a keyword. Example: find book");
                        }
                        List<Task> matches = tasks.find(p.args);
                        ui.showFound(matches);
                        break;
                    }
                }
            } catch (BruhException ex) {
                ui.showError(ex.getMessage());
            } catch (Exception io) {
                ui.showError("Couldn't save your tasks. They'll still work for this session.");
            }
        }
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