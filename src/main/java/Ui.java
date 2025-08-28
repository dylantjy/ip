import java.util.Scanner;

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
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    public void showLine() { System.out.println(LINE); }
    public void showError(String msg) { showBoxed(" " + msg); }
    public void showBye() { showBoxed(" Bye bruh. Hope to see you again soon!"); }

    public void showAdded(Task t, int total) {
        showBoxed(" Got it. I've added this task:\n   " + t
                + "\n Now you have " + total + " tasks in the list.");
    }
    public void showRemoved(Task t, int remaining) {
        showBoxed(" Noted. I've removed this task:\n   " + t
                + "\n Now you have " + remaining + " tasks in the list.");
    }
    public void showMarked(Task t) {
        showBoxed(" Nice! I've marked this task as done:\n   " + t);
    }
    public void showUnmarked(Task t) {
        showBoxed(" OK, I've marked this task as not done yet:\n   " + t);
    }
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

    public void showLoadingError() { showError("Couldn't load previous tasks; starting with an empty list."); }

    private void showBoxed(String msg) { showLine(); System.out.println(msg); showLine(); }
}
