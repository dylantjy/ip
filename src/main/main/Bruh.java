import java.util.Scanner;

public class Bruh {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println(" Hello! I'm Bruh");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        Scanner sc = new Scanner(System.in);
        while (true) {
            if (!sc.hasNextLine()) { // handle EOF (e.g., Ctrl+D)
                System.out.println(LINE);
                System.out.println(" Bye bruh. Hope to see you again soon bruh!");
                System.out.println(LINE);
                break;
            }
            String input = sc.nextLine();

            if ("bye".equalsIgnoreCase(input.trim())) {
                System.out.println(LINE);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            // Echo the command back
            System.out.println(LINE);
            System.out.println(" " + input);
            System.out.println(LINE);
        }
        sc.close();
    }
}

