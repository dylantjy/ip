package bruh;

import java.util.Scanner;
import java.util.List;

/**
 * Handles all user-facing input/output for the Bruh application.
 * <p>
 * Responsible for greeting, reading commands from {@link System#in},
 * and printing formatted responses for actions performed on {@link Task}
 * and {@link TaskList}.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    /**
     * Prints the welcome banner shown when the app starts.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Bruh");
        System.out.println(" What can I do for you?");
        showLine();
    }

    /**
     * Reads the next line of user input from standard input.
     *
     * @return the next line entered by the user, or {@code null} if no more input
     */
    public String readCommand() {
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() { System.out.println(LINE); }

    /**
     * Prints an error message in a boxed format.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) { showBoxed(" " + msg); }

    /**
     * Prints the goodbye message and closing divider.
     */
    public void showBye() { showBoxed(" Bye bruh. Hope to see you again soon!"); }

    /**
     * Prints a confirmation that a task was added, including the new total.
     *
     * @param t     the task that was added
     * @param total the total number of tasks after the addition
     */
    public void showAdded(Task t, int total) {
        showBoxed(" Got it. I've added this task:\n   " + t
                + "\n Now you have " + total + " tasks in the list.");
    }

    /**
     * Prints a confirmation that a task was removed, including the remaining total.
     *
     * @param t          the task that was removed
     * @param remaining  the number of tasks remaining
     */
    public void showRemoved(Task t, int remaining) {
        showBoxed(" Noted. I've removed this task:\n   " + t
                + "\n Now you have " + remaining + " tasks in the list.");
    }

    /**
     * Prints a confirmation that a task was marked as done.
     *
     * @param t the task that was marked
     */
    public void showMarked(Task t) {
        showBoxed(" Nice! I've marked this task as done:\n   " + t);
    }

    /**
     * Prints a confirmation that a task was marked as not done.
     *
     * @param t the task that was unmarked
     */
    public void showUnmarked(Task t) {
        showBoxed(" OK, I've marked this task as not done yet:\n   " + t);
    }

    /**
     * Prints the list of tasks, or a helpful message if the list is empty.
     *
     * @param tasks the task list to display
     */
    public void showList(TaskList tasks) {
        showLine();
        if (tasks.size() == 0) {
            System.out.println(" Your list is empty. Add something with 'todo', 'deadline', or 'event'.");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 1; i <= tasks.size(); i++) {
                System.out.println(" " + i + ". " + tasks.get(i));
            }
        }
        showLine();
    }

    /**
     * Prints a message indicating that previously saved tasks could not be loaded.
     */
    public void showLoadingError() { showError("Couldn't load previous tasks; starting with an empty list."); }

    /**
     * Prints a message boxed between divider lines.
     *
     * @param msg the message to display
     */
    private void showBoxed(String msg) { showLine(); System.out.println(msg); showLine(); }

    /**
     * Prints the results of a find operation, or a no-results message if empty.
     *
     * @param matches the tasks that matched the search
     */
    public void showFound(List<Task> matches) {
        if (matches.isEmpty()) {
            System.out.println("____________________________________________________________");
            System.out.println("  No matching tasks found.");
            System.out.println("____________________________________________________________");
            return;
        }

        System.out.println("____________________________________________________________");
        System.out.println("  Here are the matching tasks in your list:");
        int i = 1;
         for (Task t : matches) {
            System.out.println("  " + i + "." + t);
            i++;
        }
        System.out.println("____________________________________________________________");
    }
}

