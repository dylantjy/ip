import java.util.ArrayList;
import java.util.Scanner;

public class Bruh {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Storage storage = new Storage("data/bruh.txt");
        TaskList tasks = new TaskList(storage.load());

        System.out.println(LINE);
        System.out.println(" Hello! I'm Bruh");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        Scanner sc = new Scanner(System.in);
        while (true) {
            if (!sc.hasNextLine()) { goodbye(); break; }
            String input = sc.nextLine().trim();
            try {
                if (input.equalsIgnoreCase("bye")) {
                    goodbye();
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    printList(tasks);
                } else if (startsWithWord(input, "mark")) {
                    int idx = parseIndexStrict(input, "mark"); ensureIndex(idx, tasks.size());
                    tasks.get(idx - 1).markAsDone();
                    boxed(" Nice! I've marked this task as done:\n   " + tasks.get(idx - 1));
                    storage.save(tasks.asList());
                } else if (startsWithWord(input, "unmark")) {
                    int idx = parseIndexStrict(input, "unmark"); ensureIndex(idx, tasks.size());
                    tasks.get(idx - 1).markAsNotDone();
                    boxed(" OK, I've marked this task as not done yet:\n   " + tasks.get(idx - 1));
                    storage.save(tasks.asList());
                } else if (startsWithWord(input, "delete")) {
                    int idx = parseIndexStrict(input, "delete"); ensureIndex(idx, tasks.size());
                    Task removed = tasks.delete(idx);
                    boxed(" Noted. I've removed this task:\n   " + removed
                            + "\n Now you have " + tasks.size() + " tasks in the list.");
                    storage.save(tasks.asList());
                } else if (startsWithWord(input, "todo")) {
                    String desc = afterCommand(input, "todo");
                    if (desc.isEmpty()) throw new BruhException("Todo needs a description. Try: todo borrow book");
                    tasks.add(new Todo(desc));
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    storage.save(tasks.asList());
                } else if (startsWithWord(input, "deadline")) {
                    String rest = afterCommand(input, "deadline");
                    String[] parts = rest.split(" /by ", 2);
                    if (rest.isEmpty()) throw new BruhException("Deadline needs a description and '/by'. Try: deadline return book /by Sunday");
                    if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        throw new BruhException("Please include '/by <when>'. Example: deadline submit report /by 11/10/2019 5pm");
                    }
                    tasks.add(new Deadline(parts[0].trim(), parts[1].trim()));
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    storage.save(tasks.asList());
                } else if (startsWithWord(input, "event")) {
                    String rest = afterCommand(input, "event");
                    if (rest.isEmpty()) throw new BruhException("Event needs a description and times. Try: event project meeting /from Mon 2pm /to 4pm");
                    String[] fromSplit = rest.split(" /from ", 2);
                    if (fromSplit.length != 2 || fromSplit[0].trim().isEmpty()) {
                        throw new BruhException("Missing '/from'. Example: event orientation week /from 4/10/2019 /to 11/10/2019");
                    }
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    if (toSplit.length != 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                        throw new BruhException("Missing '/to'. Example: event team sync /from 2pm /to 4pm");
                    }
                    tasks.add(new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim()));
                    printAdded(tasks.get(tasks.size() - 1), tasks.size());
                    storage.save(tasks.asList());
                } else if (input.isEmpty()) {
                    throw new BruhException("I got an empty line. Try: list, todo <desc>, deadline <desc> /by <when>, event <desc> /from <start> /to <end>");
                } else {
                    throw new BruhException("Hmm, I don't recognize that. Try: list, todo, deadline, event, mark N, unmark N, delete N, or bye");
                }
            } catch (BruhException ex) {
                boxed(" " + ex.getMessage());
            } catch (Exception io) {
                // Save failures shouldn't crash the UI
                boxed(" Oops, I couldn't save your tasks. They'll still work for this session.");
            }
        }
        sc.close();
    }

    // ---------- helpers ----------

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

    private static void printList(TaskList tasks) {
        System.out.println("____________________________________________________________");
        if (tasks.size() == 0) {
            System.out.println("Your list is empty.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 1; i <= tasks.size(); i++) {
                System.out.println(i + "." + tasks.get(i));
            }
        }
        System.out.println("____________________________________________________________");
    }

    private static void printAdded(Task t, int total) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + total + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void boxed(String msg) {
        System.out.println(LINE);
        System.out.println(msg);
        System.out.println(LINE);
    }

    private static void goodbye() {
        System.out.println(LINE);
        System.out.println(" Bye bruh. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
