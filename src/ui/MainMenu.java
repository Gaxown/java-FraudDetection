package ui;

import service.*;
import entity.*;
import entity.enums.*;
import util.ConsoleUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final CustomerService customerService;
    private final CardService cardService;
    private final OperationService operationService;
    private final FraudService fraudService;
    private final ReportService reportService;
    private final ImportExportService importExportService;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.customerService = new CustomerService();
        this.cardService = new CardService();
        this.operationService = new OperationService();
        this.fraudService = new FraudService();
        this.reportService = new ReportService();
        this.importExportService = new ImportExportService();
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = ConsoleUtils.readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> createClient();
                    case 2 -> issueCard();
                    case 3 -> performOperation();
                    case 4 -> consultCardHistory();
                    case 5 -> analyzeFraud();
                    case 6 -> blockSuspendCard();
                    case 7 -> generateReports();
                    case 8 -> importExportMenu();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n========== FRAUD DETECTION SYSTEM ==========");
        System.out.println("1. Create Client");
        System.out.println("2. Issue Card (Debit, Credit, Prepaid)");
        System.out.println("3. Perform Operation (Purchase, Withdrawal, Online Payment)");
        System.out.println("4. Consult Card History");
        System.out.println("5. Launch Fraud Analysis");
        System.out.println("6. Block/Suspend Card");
        System.out.println("7. Generate Reports");
        System.out.println("8. Import/Export");
        System.out.println("0. Exit");
        System.out.println("=============================================");
    }

    private void createClient() throws SQLException {
        System.out.println("\n--- Create New Client ---");
        String name = ConsoleUtils.readString("Enter client name: ");
        String email = ConsoleUtils.readString("Enter email: ");
        String phone = ConsoleUtils.readString("Enter phone: ");

        Customer client = customerService.createCustomer(name, email, phone);
        System.out.println("Client created successfully with ID: " + client.customerId());
    }

    private void issueCard() throws SQLException {
        System.out.println("\n--- Issue New Card ---");
        int clientId = ConsoleUtils.readInt("Enter client ID: ");

        System.out.println("Select card type:");
        System.out.println("1. Debit Card");
        System.out.println("2. Credit Card");
        System.out.println("3. Prepaid Card");

        int cardType = ConsoleUtils.readInt("Choose card type: ");

        switch (cardType) {
            case 1 -> {
                BigDecimal dailyLimit = ConsoleUtils.readBigDecimal("Enter daily limit: ");
                DebitCard card = cardService.createDebitCard(clientId, dailyLimit);
                System.out.println("Debit card created with number: " + card.getCardNumber());
            }
            case 2 -> {
                BigDecimal monthlyLimit = ConsoleUtils.readBigDecimal("Enter monthly limit: ");
                BigDecimal interestRate = ConsoleUtils.readBigDecimal("Enter interest rate: ");
                CreditCard card = cardService.createCreditCard(clientId, monthlyLimit, interestRate);
                System.out.println("Credit card created with number: " + card.getCardNumber());
            }
            case 3 -> {
                BigDecimal initialBalance = ConsoleUtils.readBigDecimal("Enter initial balance: ");
                PrepaidCard card = cardService.createPrepaidCard(clientId, initialBalance);
                System.out.println("Prepaid card created with number: " + card.getCardNumber());
            }
            default -> System.out.println("Invalid card type.");
        }
    }

    private void performOperation() throws SQLException {
        System.out.println("\n--- Perform Operation ---");
        int cardId = ConsoleUtils.readInt("Enter card ID: ");
        BigDecimal amount = ConsoleUtils.readBigDecimal("Enter amount: ");
        String location = ConsoleUtils.readString("Enter location: ");

        System.out.println("Select operation type:");
        System.out.println("1. Purchase");
        System.out.println("2. Withdrawal");
        System.out.println("3. Online Payment");
        System.out.println("4. Transfer");

        int opType = ConsoleUtils.readInt("Choose operation type: ");
        OperationType type = switch (opType) {
            case 1 -> OperationType.PURCHASE;
            case 2 -> OperationType.WITHDRAWAL;
            case 3 -> OperationType.ONLINE_PAYMENT;
            case 4 -> OperationType.TRANSFER;
            default -> throw new IllegalArgumentException("Invalid operation type");
        };

        CardOperation operation = operationService.recordOperation(cardId, amount, type, location);
        System.out.println("Operation recorded with ID: " + operation.getOperationId());

        // Trigger fraud detection
        fraudService.detectFraud(cardId);
    }

    private void consultCardHistory() throws SQLException {
        System.out.println("\n--- Card History ---");
        int cardId = ConsoleUtils.readInt("Enter card ID: ");

        List<CardOperation> operations = operationService.findOperationsByCard(cardId);
        if (operations.isEmpty()) {
            System.out.println("No operations found for this card.");
        } else {
            System.out.println("Operations for card " + cardId + ":");
            for (CardOperation op : operations) {
                System.out.printf("ID: %d, Date: %s, Amount: %.2f, Type: %s, Location: %s%n",
                    op.getOperationId(), op.getOperationDate(), op.getAmount(), op.getType(), op.getLocation());
            }
        }
    }

    private void analyzeFraud() throws SQLException {
        System.out.println("\n--- Fraud Analysis ---");
        int cardId = ConsoleUtils.readInt("Enter card ID to analyze: ");

        fraudService.detectFraud(cardId);

        List<FraudAlert> alerts = fraudService.getAlertsByCard(cardId);
        if (alerts.isEmpty()) {
            System.out.println("No fraud alerts found for this card.");
        } else {
            System.out.println("Fraud alerts for card " + cardId + ":");
            for (FraudAlert alert : alerts) {
                System.out.printf("Alert ID: %d, Level: %s, Description: %s, Date: %s%n",
                    alert.getAlertId(), alert.getAlertLevel(), alert.getDescription(), alert.getCreationDate());
            }
        }
    }

    private void blockSuspendCard() throws SQLException {
        System.out.println("\n--- Block/Suspend Card ---");
        int cardId = ConsoleUtils.readInt("Enter card ID: ");

        System.out.println("Select action:");
        System.out.println("1. Activate Card");
        System.out.println("2. Suspend Card");
        System.out.println("3. Block Card");

        int action = ConsoleUtils.readInt("Choose action: ");

        boolean success = switch (action) {
            case 1 -> cardService.activateCard(cardId);
            case 2 -> cardService.suspendCard(cardId);
            case 3 -> cardService.blockCard(cardId);
            default -> throw new IllegalArgumentException("Invalid action");
        };

        if (success) {
            System.out.println("Card status updated successfully.");
        } else {
            System.out.println("Failed to update card status.");
        }
    }

    private void generateReports() throws SQLException {
        System.out.println("\n--- Generate Reports ---");
        System.out.println("1. Top 5 Most Used Cards");
        System.out.println("2. Monthly Statistics");
        System.out.println("3. Card Status Distribution");
        System.out.println("4. Critical Alerts");

        int reportType = ConsoleUtils.readInt("Choose report type: ");

        switch (reportType) {
            case 1 -> {
                var topCards = reportService.getTop5MostUsedCards();
                System.out.println("Top 5 Most Used Cards:");
                topCards.forEach(entry ->
                    System.out.printf("Card ID: %d, Operations: %d%n", entry.getKey(), entry.getValue()));
            }
            case 2 -> {
                // Monthly statistics would require date input
                System.out.println("Monthly statistics feature available via ReportService.getMonthlyStatistics()");
            }
            case 3 -> {
                var distribution = reportService.getCardStatusDistribution();
                System.out.println("Card Status Distribution:");
                distribution.forEach((status, count) ->
                    System.out.printf("%s: %d cards%n", status, count));
            }
            case 4 -> {
                List<FraudAlert> criticalAlerts = fraudService.getCriticalAlerts();
                System.out.println("Critical Alerts:");
                criticalAlerts.forEach(alert ->
                    System.out.printf("Alert ID: %d, Card: %d, Description: %s%n",
                        alert.getAlertId(), alert.getCardId(), alert.getDescription()));
            }
            default -> System.out.println("Invalid report type.");
        }
    }

    private void importExportMenu() throws SQLException {
        System.out.println("\n--- Import/Export ---");
        System.out.println("1. Import Operations from Excel");
        System.out.println("2. Import Cards from Excel");
        System.out.println("3. Export Operations to Excel");
        System.out.println("4. Export Cards to Excel");

        int choice = ConsoleUtils.readInt("Choose option: ");
        String filePath = ConsoleUtils.readString("Enter file path: ");

        switch (choice) {
            case 1 -> importExportService.importOperationsFromExcel(filePath);
            case 2 -> importExportService.importCardsFromExcel(filePath);
            case 3 -> {
                // Would need card IDs input
                importExportService.exportOperationsToExcel(filePath, List.of());
            }
            case 4 -> {
                // Would need client IDs input
                importExportService.exportCardsToExcel(filePath, List.of());
            }
            default -> System.out.println("Invalid option.");
        }
    }
}
