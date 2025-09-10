package bruh;

import java.util.Scanner;
import java.util.List;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Bruh");
        System.out.println(" What can I do for you?");
        showLine();
    }

    public String readCommand() {
        assert sc != null : "scanner must be initialized";
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    public void showLine() { System.out.println(LINE); }

    public void showError(String msg) {
        assert msg != null && !msg.isBlank() : "error message must be non-empty";
        showBoxed(" " + msg);
    }

    public void showBye() { showBoxed(" Bye bruh. Hope to see you again soon!"); }

    public void showAdded(Task t, int total) {
        assert t != null : "added task must not be null";
        assert total >= 0 : "total task count cannot be negative";
        showBoxed(
           "Got it. I've added this task:",
           "  " + t,
           "Now you have " + total + " tasks in the list."
        );
    }

    public void showRemoved(Task t, int remaining) {
        assert t != null : "removed task must not be null";
        assert remaining >= 0 : "remaining task count cannot be negative";
        showBoxed(
           "Noted. I've removed this task:",
           "  " + t,
           "Now you have " + remaining + " tasks in the list."
        );
    }

    public void showMarked(Task t) {
        assert t != null : "task to mark must not be null";
        showBoxed(" Nice! I've marked this task as done:\n   " + t);
    }

    public void showUnmarked(Task t) {
        assert t != null : "task to unmark must not be null";
        showBoxed(" OK, I've marked this task as not done yet:\n   " + t);
    }

    public void showList(TaskList tasks) {
        assert tasks != null : "TaskList must not be null";
        assert tasks.size() >= 0 : "size() must be non-negative";

        showLine();
        if (tasks.size() == 0) {
            System.out.println(" Your list is empty. Add something with 'todo', 'deadline', or 'event'.");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 1; i <= tasks.size(); i++) {
                Task t = tasks.get(i);
                assert t != null : "task entries must not be null";
                System.out.println(" " + i + ". " + t);
            }
        }
        showLine();
    }

    public void showLoadingError() {
        showError("Couldn't load previous tasks; starting with an empty list.");
    }

    public void showBoxed(String... lines) {
        assert lines != null : "lines array must not be null";
        showLine();
        for (String s : lines) {
            assert s != null : "boxed line must not be null";
            System.out.println(" " + s);
        }
        showLine();
    }

    public void showFound(List<Task> matches) {
        assert matches != null : "matches list must not be null";

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
            assert t != null : "match entries must not be null";
            System.out.println("  " + i + "." + t);
            i++;
        }
        System.out.println("____________________________________________________________");
    }
}

