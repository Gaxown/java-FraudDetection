import service.*;
import entity.*;
import entity.enums.*;
import util.ConsoleUtils;
import util.ViewUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerService customerService = new CustomerService();
    private static final CardService cardService = new CardService();
    private static final OperationService operationService = new OperationService();
    private static final FraudService fraudService = new FraudService();
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        ConsoleUtils.printHeader("Bank Card Fraud Detection System");

        try {
            // Run initial setup
            setupTestData();

            // Main menu loop
            boolean running = true;
            while (running) {
                displayMainMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        customerManagement();
                        break;
                    case 2:
                        cardManagement();
                        break;
                    case 3:
                        operationManagement();
                        break;
                    case 4:
                        fraudDetectionDemo();
                        break;
                    case 5:
                        reportingDemo();
                        break;
                    case 6:
                        runAutomatedTests();
                        break;
                    case 0:
                        running = false;
                        ViewUtils.displaySuccess("Thank you for using the Fraud Detection System!");
                        break;
                    default:
                        ViewUtils.displayError("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            ViewUtils.displayError("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        ConsoleUtils.printSeparator();
        System.out.println("=== MAIN MENU ===");
        System.out.println("1. Customer Management");
        System.out.println("2. Card Management");
        System.out.println("3. Operation Management");
        System.out.println("4. Fraud Detection Demo");
        System.out.println("5. Reporting & Analytics");
        System.out.println("6. Run Automated Tests");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void setupTestData() throws SQLException {
        ConsoleUtils.printInfo("Setting up test data...");

        // Create test customers
        Customer customer1 = customerService.createCustomer("John Doe", "john.doe@email.com", "+1234567890");
        Customer customer2 = customerService.createCustomer("Jane Smith", "jane.smith@email.com", "+0987654321");

        // Create test cards
        DebitCard debitCard = cardService.createDebitCard(customer1.getId(), new BigDecimal("1000.00"));
        CreditCard creditCard = cardService.createCreditCard(customer1.getId(), new BigDecimal("5000.00"), new BigDecimal("0.15"));
        PrepaidCard prepaidCard = cardService.createPrepaidCard(customer2.getId(), new BigDecimal("500.00"));

        ViewUtils.displaySuccess("Test data setup completed!");
        System.out.println("Created customers: " + customer1.getName() + ", " + customer2.getName());
        System.out.println("Created cards: Debit, Credit, Prepaid");
    }

    private static void customerManagement() throws SQLException {
        ConsoleUtils.printSeparator();
        System.out.println("=== CUSTOMER MANAGEMENT ===");
        System.out.println("1. Create Customer");
        System.out.println("2. View All Customers");
        System.out.println("3. Search Customer by Email");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                createCustomer();
                break;
            case 2:
                viewAllCustomers();
                break;
            case 3:
                searchCustomerByEmail();
                break;
            default:
                ConsoleUtils.printError("Invalid choice.");
        }
    }

    private static void createCustomer() throws SQLException {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();
        System.out.print("Enter customer phone: ");
        String phone = scanner.nextLine();

        try {
            Customer customer = customerService.createCustomer(name, email, phone);
            ViewUtils.displaySuccess("Customer created successfully! ID: " + customer.getId());
        } catch (IllegalArgumentException e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void viewAllCustomers() throws SQLException {
        List<Customer> customers = customerService.findAllCustomers();
        ViewUtils.displayCustomerList(customers);
    }

    private static void searchCustomerByEmail() throws SQLException {
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();

        var customer = customerService.findCustomerByEmail(email);
        if (customer.isPresent()) {
            System.out.println("\n=== CUSTOMER FOUND ===");
            ViewUtils.displayCustomer(customer.get());
        } else {
            ViewUtils.displayWarning("Customer not found.");
        }
    }

    private static void cardManagement() throws SQLException {
        ConsoleUtils.printSeparator();
        System.out.println("=== CARD MANAGEMENT ===");
        System.out.println("1. Create Debit Card");
        System.out.println("2. Create Credit Card");
        System.out.println("3. Create Prepaid Card");
        System.out.println("4. View All Cards");
        System.out.println("5. Block/Suspend Card");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                createDebitCard();
                break;
            case 2:
                createCreditCard();
                break;
            case 3:
                createPrepaidCard();
                break;
            case 4:
                viewAllCards();
                break;
            case 5:
                manageCardStatus();
                break;
            default:
                ConsoleUtils.printError("Invalid choice.");
        }
    }

    private static void createDebitCard() throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter daily limit: ");
        BigDecimal dailyLimit = scanner.nextBigDecimal();

        try {
            DebitCard card = cardService.createDebitCard(customerId, dailyLimit);
            ViewUtils.displaySuccess("Debit card created! Number: " + card.getCardNumber());
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void createCreditCard() throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter credit limit: ");
        BigDecimal creditLimit = scanner.nextBigDecimal();
        System.out.print("Enter interest rate (0.15 for 15%): ");
        BigDecimal interestRate = scanner.nextBigDecimal();

        try {
            CreditCard card = cardService.createCreditCard(customerId, creditLimit, interestRate);
            ViewUtils.displaySuccess("Credit card created! Number: " + card.getCardNumber());
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void createPrepaidCard() throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter initial balance: ");
        BigDecimal initialBalance = scanner.nextBigDecimal();

        try {
            PrepaidCard card = cardService.createPrepaidCard(customerId, initialBalance);
            ViewUtils.displaySuccess("Prepaid card created! Number: " + card.getCardNumber());
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void viewAllCards() throws SQLException {
        List<Card> cards = cardService.findAllCards();
        ViewUtils.displayCardList(cards);
    }

    private static void manageCardStatus() throws SQLException {
        System.out.print("Enter card ID: ");
        int cardId = scanner.nextInt();
        System.out.println("1. Block Card");
        System.out.println("2. Suspend Card");
        System.out.println("3. Activate Card");
        System.out.print("Choose action: ");
        int action = scanner.nextInt();

        try {
            switch (action) {
                case 1:
                    cardService.blockCard(cardId);
                    ViewUtils.displaySuccess("Card blocked successfully!");
                    break;
                case 2:
                    cardService.suspendCard(cardId);
                    ViewUtils.displaySuccess("Card suspended successfully!");
                    break;
                case 3:
                    cardService.activateCard(cardId);
                    ViewUtils.displaySuccess("Card activated successfully!");
                    break;
                default:
                    ViewUtils.displayError("Invalid action.");
            }
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void operationManagement() throws SQLException {
        ConsoleUtils.printSeparator();
        System.out.println("=== OPERATION MANAGEMENT ===");
        System.out.println("1. Record New Operation");
        System.out.println("2. View Operations by Card");
        System.out.println("3. View Recent Operations");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                recordOperation();
                break;
            case 2:
                viewOperationsByCard();
                break;
            case 3:
                viewRecentOperations();
                break;
            default:
                ConsoleUtils.printError("Invalid choice.");
        }
    }

    private static void recordOperation() throws SQLException {
        System.out.print("Enter card ID: ");
        int cardId = scanner.nextInt();
        System.out.print("Enter amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Enter location: ");
        String location = scanner.nextLine();
        System.out.println("Operation types: 1=PAYMENT, 2=WITHDRAWAL, 3=TRANSFER");
        System.out.print("Choose operation type: ");
        int typeChoice = scanner.nextInt();

        OperationType type = switch (typeChoice) {
            case 1 -> OperationType.PAYMENT;
            case 2 -> OperationType.WITHDRAWAL;
            case 3 -> OperationType.TRANSFER;
            default -> OperationType.PAYMENT;
        };

        try {
            CardOperation operation = operationService.recordOperation(cardId, amount, type, location);
            ViewUtils.displaySuccess("Operation recorded! ID: " + operation.getId());

            // Trigger fraud detection
            fraudService.detectFraud(cardId);
            ViewUtils.displayInfo("Fraud detection completed for this operation.");
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void viewOperationsByCard() throws SQLException {
        System.out.print("Enter card ID: ");
        int cardId = scanner.nextInt();

        List<CardOperation> operations = operationService.findOperationsByCard(cardId);
        ViewUtils.displayOperationList(operations, "Operations for Card " + cardId);
    }

    private static void viewRecentOperations() throws SQLException {
        System.out.print("Enter card ID: ");
        int cardId = scanner.nextInt();

        List<CardOperation> operations = operationService.getRecentOperations(cardId);
        ViewUtils.displayOperationList(operations, "Recent Operations (Last 30 days)");
    }

    private static void fraudDetectionDemo() throws SQLException {
        ConsoleUtils.printSeparator();
        System.out.println("=== FRAUD DETECTION DEMO ===");
        System.out.println("1. View Fraud Alerts");
        System.out.println("2. View Critical Alerts");
        System.out.println("3. Run Fraud Detection on Card");
        System.out.println("4. Simulate Suspicious Activity");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                viewAllAlerts();
                break;
            case 2:
                viewCriticalAlerts();
                break;
            case 3:
                runFraudDetection();
                break;
            case 4:
                simulateSuspiciousActivity();
                break;
            default:
                ConsoleUtils.printError("Invalid choice.");
        }
    }

    private static void viewAllAlerts() throws SQLException {
        List<FraudAlert> alerts = fraudService.getAllAlerts();
        ViewUtils.displayFraudAlertList(alerts, "All Fraud Alerts");
    }

    private static void viewCriticalAlerts() throws SQLException {
        List<FraudAlert> alerts = fraudService.getCriticalAlerts();
        ViewUtils.displayFraudAlertList(alerts, "Critical Fraud Alerts");
    }

    private static void runFraudDetection() throws SQLException {
        System.out.print("Enter card ID: ");
        int cardId = scanner.nextInt();

        try {
            fraudService.detectFraud(cardId);
            ViewUtils.displaySuccess("Fraud detection completed for card " + cardId);

            // Show any new alerts for this card
            List<FraudAlert> cardAlerts = fraudService.getAlertsByCard(cardId);
            if (!cardAlerts.isEmpty()) {
                ViewUtils.displayFraudAlertList(cardAlerts, "Alerts for Card " + cardId);
            } else {
                ViewUtils.displayInfo("No fraud alerts detected for this card.");
            }
        } catch (Exception e) {
            ViewUtils.displayError("Error: " + e.getMessage());
        }
    }

    private static void simulateSuspiciousActivity() throws SQLException {
        ViewUtils.displayInfo("Simulating suspicious activity...");

        // Get the first available card
        List<Card> cards = cardService.findAllCards();
        if (cards.isEmpty()) {
            ViewUtils.displayError("No cards available for simulation.");
            return;
        }

        Card testCard = cards.get(0);
        ViewUtils.displayInfo("Using card: " + testCard.getCardNumber());

        // Simulate high amount transaction
        operationService.recordOperation(testCard.getId(), new BigDecimal("6000.00"),
            OperationType.PAYMENT, "Suspicious Location");

        // Simulate rapid transactions in different locations
        LocalDateTime now = LocalDateTime.now();
        operationService.recordOperationWithDate(testCard.getId(), new BigDecimal("500.00"),
            OperationType.PAYMENT, "Paris", now);
        operationService.recordOperationWithDate(testCard.getId(), new BigDecimal("300.00"),
            OperationType.PAYMENT, "London", now.plusMinutes(15));

        // Run fraud detection
        fraudService.detectFraud(testCard.getId());

        ViewUtils.displaySuccess("Suspicious activity simulation completed!");
        ViewUtils.displayInfo("Check fraud alerts to see detected issues.");
    }

    private static void reportingDemo() throws SQLException {
        ConsoleUtils.printSeparator();
        System.out.println("=== REPORTING & ANALYTICS ===");
        System.out.println("1. Card Status Distribution");
        System.out.println("2. Top 5 Most Used Cards");
        System.out.println("3. Most Active Locations");
        System.out.println("4. Monthly Report");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                showCardStatusDistribution();
                break;
            case 2:
                showTop5Cards();
                break;
            case 3:
                showActiveLocations();
                break;
            case 4:
                showMonthlyReport();
                break;
            default:
                ConsoleUtils.printError("Invalid choice.");
        }
    }

    private static void showCardStatusDistribution() throws SQLException {
        Map<CardStatus, Long> distribution = reportService.getCardStatusDistribution();
        ViewUtils.displayCardStatusDistribution(distribution);
    }

    private static void showTop5Cards() throws SQLException {
        List<Map.Entry<Integer, Long>> topCards = reportService.getTop5MostUsedCards();
        ViewUtils.displayTopCards(topCards);
    }

    private static void showActiveLocations() throws SQLException {
        List<Map.Entry<String, Long>> locations = reportService.getMostActiveLocations();
        ViewUtils.displayActiveLocations(locations);
    }

    private static void showMonthlyReport() throws SQLException {
        YearMonth currentMonth = YearMonth.now();
        Map<String, Object> report = reportService.generateMonthlyReport(currentMonth);
        ViewUtils.displayMonthlyReport(report);
    }

    private static void runAutomatedTests() throws SQLException {
        ConsoleUtils.printSeparator();
        ViewUtils.displayInfo("Running automated tests...");

        try {
            // Test customer creation
            Customer testCustomer = customerService.createCustomer("Test User", "test@example.com", "+1111111111");
            ViewUtils.displaySuccess("Customer creation test passed");

            // Test card creation
            DebitCard testCard = cardService.createDebitCard(testCustomer.getId(), new BigDecimal("2000.00"));
            ViewUtils.displaySuccess("Card creation test passed");

            // Test operation recording
            CardOperation testOp = operationService.recordOperation(testCard.getId(), new BigDecimal("100.00"),
                OperationType.PAYMENT, "Test Location");
            ViewUtils.displaySuccess("Operation recording test passed");

            // Test fraud detection
            fraudService.detectFraud(testCard.getId());
            ViewUtils.displaySuccess("Fraud detection test passed");

            // Test high amount alert
            operationService.recordOperation(testCard.getId(), new BigDecimal("7000.00"),
                OperationType.PAYMENT, "High Amount Test");
            fraudService.detectFraud(testCard.getId());

            List<FraudAlert> alerts = fraudService.getAlertsByCard(testCard.getId());
            if (!alerts.isEmpty()) {
                ViewUtils.displaySuccess("High amount fraud detection test passed");
            } else {
                ViewUtils.displayWarning("High amount fraud detection test failed");
            }

            ViewUtils.displaySuccess("All automated tests completed!");

        } catch (Exception e) {
            ViewUtils.displayError("Test failed: " + e.getMessage());
        }
    }
}
