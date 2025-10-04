package util;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleUtils {

    private final static Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
            }
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                BigDecimal value = new BigDecimal(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid decimal number");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Enter 'y' for yes or 'n' for no");
            }
        }
    }

    public static String readEmail(String prompt) {
        while (true) {
            String email = readString(prompt);
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("Enter a valid email address");
        }
    }

    public static void displayMenu(String title, String[] options) {
        System.out.println("\n==== " + title + " ====");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println();
    }

    public static void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    public static void clearScreen() {
        // Simple clear screen simulation
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
