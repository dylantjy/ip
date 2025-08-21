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
                int index = parseIndex(input, "mark ");
                if (index >= 1 && index <= count) {
                    tasks[index - 1].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[index - 1]);
                    System.out.println(LINE);
                }
            } else if (input.startsWith("unmark ")) {
                int index = parseIndex(input, "unmark ");
                if (index >= 1 && index <= count) {
                    tasks[index - 1].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[index - 1]);
                    System.out.println(LINE);
                }
            } else if (!input.isEmpty()) {
                if (count < MAX) {
                    tasks[count] = new Task(input);
                    count++;
                    System.out.println(LINE);
                    System.out.println(" added: " + input);
                    System.out.println(LINE);
                }
            }
        }
        sc.close();
    }

    private static void goodbye() {
        System.out.println(LINE);
        System.out.println(" Bye bruh. Hope to see you again soon!");
        System.out.println(LINE);
    }

    private static int parseIndex(String input, String command) {
        try {
            return Integer.parseInt(input.substring(command.length()).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
