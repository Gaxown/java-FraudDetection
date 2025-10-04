import ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Fraud Detection System...");

        try {
            MainMenu menu = new MainMenu();
            menu.start();
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Application terminated.");
    }
}
