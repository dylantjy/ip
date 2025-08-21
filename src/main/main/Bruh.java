import java.util.Scanner;

public class Bruh {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX = 100;

    public static void main(String[] args) {
        String[] tasks = new String[MAX];
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
                if (count == 0) {
                    System.out.println(" (no items yet)");
                } else {
                    for (int i = 0; i < count; i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks[i]);
                    }
                }
                System.out.println(LINE);
            } else if (!input.isEmpty()) {
                if (count < MAX) {
                    tasks[count++] = input;
                    System.out.println(LINE);
                    System.out.println(" added: " + input);
                    System.out.println(LINE);
                } else {
                    System.out.println(LINE);
                    System.out.println(" sorry, list is full (" + MAX + " items)");
                    System.out.println(LINE);
                }
            }
            // blank lines are ignored
        }
        sc.close();
    }

    private static void goodbye() {
        System.out.println(LINE);
        System.out.println(" Bye bruh. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
