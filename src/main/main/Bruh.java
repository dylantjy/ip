import java.util.Scanner;

public class Bruh {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX = 100;

    public static void main(String[] args) {
        Task[] tasks = new Task[MAX];
        int count = 0;

        System.out.println(LINE);
        System.out.println(" Hello! I'm Bruh");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        Scanner sc = new Scanner(System.in);
        while (true) {
            if (!sc.hasNextLine()) {
                goodbye();
                break;
            }
            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("bye")) {
                goodbye();
                break;
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println(LINE);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < count; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(LINE);
            } else if (input.startsWith("mark ")) {
                int idx = parseIndex(input, "mark ");
                if (isValidIndex(idx, count)) {
                    tasks[idx - 1].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                }
            } else if (input.startsWith("unmark ")) {
                int idx = parseIndex(input, "unmark ");
                if (isValidIndex(idx, count)) {
                    tasks[idx - 1].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                }
            } else if (input.startsWith("todo ")) {
                String desc = input.substring("todo ".length()).trim();
                if (!desc.isEmpty() && count < MAX) {
                    tasks[count++] = new Todo(desc);
                    printAdded(tasks[count - 1], count);
                }
            } else if (input.startsWith("deadline ")) {
                // format: deadline <desc> /by <when>
                String rest = input.substring("deadline ".length()).trim();
                String[] parts = rest.split(" /by ", 2);
                if (parts.length == 2 && count < MAX) {
                    String desc = parts[0].trim();
                    String by = parts[1].trim();
                    tasks[count++] = new Deadline(desc, by);
                    printAdded(tasks[count - 1], count);
                }
            } else if (input.startsWith("event ")) {
                // format: event <desc> /from <start> /to <end>
                String rest = input.substring("event ".length()).trim();
                String[] fromSplit = rest.split(" /from ", 2);
                if (fromSplit.length == 2) {
                    String desc = fromSplit[0].trim();
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    if (toSplit.length == 2 && count < MAX) {
                        String from = toSplit[0].trim();
                        String to = toSplit[1].trim();
                        tasks[count++] = new Event(desc, from, to);
                        printAdded(tasks[count - 1], count);
                    }
                }
            } else {
                // Optional: treat any other line as a Todo
                if (count < MAX) {
                    tasks[count++] = new Todo(input);
                    printAdded(tasks[count - 1], count);
                }
            }
        }
        sc.close();
    }

    private static void printAdded(Task t, int total) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + total + " tasks in the list.");
        System.out.println(LINE);
    }

    private static boolean isValidIndex(int idx, int count) {
        return idx >= 1 && idx <= count;
    }

    private static int parseIndex(String input, String prefix) {
        try {
            return Integer.parseInt(input.substring(prefix.length()).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static void goodbye() {
        System.out.println(LINE);
        System.out.println(" Bye bruh. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
