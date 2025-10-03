package util;

import entity.*;
import entity.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ViewUtils {

    // Customer display methods
    public static void displayCustomer(Customer customer) {
        System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s%n",
            customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone());
    }

    public static void displayCustomerList(List<Customer> customers) {
        if (customers.isEmpty()) {
            ConsoleUtils.printWarning("No customers found.");
            return;
        }

        System.out.println("\n=== CUSTOMERS ===");
        System.out.println("ID    | Name               | Email                    | Phone");
        System.out.println("------|--------------------|--------------------------|--------------");
        for (Customer customer : customers) {
            System.out.printf("%-5d | %-18s | %-24s | %s%n",
                customer.getId(),
                truncate(customer.getName(), 18),
                truncate(customer.getEmail(), 24),
                customer.getPhone());
        }
        System.out.println("Total customers: " + customers.size());
    }

    // Card display methods
    public static void displayCard(Card card) {
        String cardType = card.getClass().getSimpleName();
        System.out.printf("ID: %d | Number: %s | Type: %s | Status: %s | Customer: %d%n",
            card.getId(), maskCardNumber(card.getCardNumber()), cardType,
            card.getStatus(), card.getCustomerId());
    }

    public static void displayCardList(List<Card> cards) {
        if (cards.isEmpty()) {
            ConsoleUtils.printWarning("No cards found.");
            return;
        }

        System.out.println("\n=== CARDS ===");
        System.out.println("ID    | Card Number      | Type          | Status     | Customer");
        System.out.println("------|------------------|---------------|------------|----------");
        for (Card card : cards) {
            System.out.printf("%-5d | %-16s | %-13s | %-10s | %d%n",
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                card.getClass().getSimpleName(),
                card.getStatus(),
                card.getCustomerId());
        }
        System.out.println("Total cards: " + cards.size());
    }

    public static void displayCardDetails(Card card) {
        System.out.println("\n=== CARD DETAILS ===");
        System.out.println("ID: " + card.getId());
        System.out.println("Card Number: " + maskCardNumber(card.getCardNumber()));
        System.out.println("Type: " + card.getClass().getSimpleName());
        System.out.println("Status: " + card.getStatus());
        System.out.println("Customer ID: " + card.getCustomerId());
        System.out.println("Expiration Date: " + card.getExpirationDate());

        // Display specific details based on card type
        if (card instanceof DebitCard) {
            DebitCard debitCard = (DebitCard) card;
            System.out.println("Daily Limit: " + debitCard.getDailyLimit());
        } else if (card instanceof CreditCard) {
            CreditCard creditCard = (CreditCard) card;
            System.out.println("Credit Limit: " + creditCard.getCreditLimit());
            System.out.println("Interest Rate: " + creditCard.getInterestRate());
        } else if (card instanceof PrepaidCard) {
            PrepaidCard prepaidCard = (PrepaidCard) card;
            System.out.println("Balance: " + prepaidCard.getBalance());
        }
    }

    // Operation display methods
    public static void displayOperation(CardOperation operation) {
        System.out.printf("ID: %d | Date: %s | Amount: %.2f | Type: %s | Location: %s%n",
            operation.getId(),
            formatDateTime(operation.getOperationDate()),
            operation.getAmount(),
            operation.getType(),
            operation.getLocation());
    }

    public static void displayOperationList(List<CardOperation> operations, String title) {
        if (operations.isEmpty()) {
            ConsoleUtils.printWarning("No operations found.");
            return;
        }

        System.out.println("\n=== " + title.toUpperCase() + " ===");
        System.out.println("ID    | Date & Time         | Amount    | Type       | Location");
        System.out.println("------|---------------------|-----------|------------|------------------");

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CardOperation op : operations) {
            System.out.printf("%-5d | %-19s | %9.2f | %-10s | %s%n",
                op.getId(),
                formatDateTime(op.getOperationDate()),
                op.getAmount(),
                op.getType(),
                truncate(op.getLocation(), 18));
            totalAmount = totalAmount.add(op.getAmount());
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Total operations: %d | Total amount: %.2f%n", operations.size(), totalAmount);
    }

    // Fraud alert display methods
    public static void displayFraudAlert(FraudAlert alert) {
        System.out.printf("ID: %d | Level: %s | Card: %d | Date: %s%n",
            alert.getId(), alert.getLevel(), alert.getCardId(),
            formatDateTime(alert.getCreatedAt()));
        System.out.println("Description: " + alert.getDescription());
        System.out.println();
    }

    public static void displayFraudAlertList(List<FraudAlert> alerts, String title) {
        if (alerts.isEmpty()) {
            ConsoleUtils.printInfo("No fraud alerts found.");
            return;
        }

        System.out.println("\n=== " + title.toUpperCase() + " ===");
        System.out.println("ID    | Level     | Card ID | Date & Time         | Description");
        System.out.println("------|-----------|---------|---------------------|------------------------");

        for (FraudAlert alert : alerts) {
            System.out.printf("%-5d | %-9s | %-7d | %-19s | %s%n",
                alert.getId(),
                alert.getLevel(),
                alert.getCardId(),
                formatDateTime(alert.getCreatedAt()),
                truncate(alert.getDescription(), 24));
        }
        System.out.println("Total alerts: " + alerts.size());
    }

    // Report display methods
    public static void displayCardStatusDistribution(Map<CardStatus, Long> distribution) {
        System.out.println("\n=== CARD STATUS DISTRIBUTION ===");
        System.out.println("Status      | Count");
        System.out.println("------------|-------");

        long total = 0;
        for (Map.Entry<CardStatus, Long> entry : distribution.entrySet()) {
            System.out.printf("%-11s | %d%n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("------------|-------");
        System.out.println("Total       | " + total);
    }

    public static void displayTopCards(List<Map.Entry<Integer, Long>> topCards) {
        System.out.println("\n=== TOP 5 MOST USED CARDS ===");
        System.out.println("Rank | Card ID | Operations");
        System.out.println("-----|---------|------------");

        int rank = 1;
        for (Map.Entry<Integer, Long> entry : topCards) {
            System.out.printf("%-4d | %-7d | %d%n", rank++, entry.getKey(), entry.getValue());
        }
    }

    public static void displayActiveLocations(List<Map.Entry<String, Long>> locations) {
        System.out.println("\n=== MOST ACTIVE LOCATIONS ===");
        System.out.println("Rank | Location              | Operations");
        System.out.println("-----|----------------------|------------");

        int rank = 1;
        for (Map.Entry<String, Long> entry : locations) {
            System.out.printf("%-4d | %-20s | %d%n",
                rank++, truncate(entry.getKey(), 20), entry.getValue());
        }
    }

    public static void displayMonthlyStatistics(Map<OperationType, BigDecimal> statistics) {
        System.out.println("\n=== MONTHLY STATISTICS BY OPERATION TYPE ===");
        System.out.println("Type        | Total Amount");
        System.out.println("------------|-------------");

        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Map.Entry<OperationType, BigDecimal> entry : statistics.entrySet()) {
            System.out.printf("%-11s | %12.2f%n", entry.getKey(), entry.getValue());
            grandTotal = grandTotal.add(entry.getValue());
        }
        System.out.println("------------|-------------");
        System.out.printf("Total       | %12.2f%n", grandTotal);
    }

    public static void displayMonthlyReport(Map<String, Object> report) {
        System.out.println("\n=== MONTHLY REPORT ===");

        for (Map.Entry<String, Object> entry : report.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "month":
                    System.out.println("Report Period: " + value);
                    break;
                case "statisticsByType":
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<OperationType, BigDecimal> stats = (Map<OperationType, BigDecimal>) value;
                        displayMonthlyStatistics(stats);
                    }
                    break;
                case "cardStatusDistribution":
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<CardStatus, Long> distribution = (Map<CardStatus, Long>) value;
                        displayCardStatusDistribution(distribution);
                    }
                    break;
                case "topCards":
                    if (value instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Map.Entry<Integer, Long>> topCards = (List<Map.Entry<Integer, Long>>) value;
                        displayTopCards(topCards);
                    }
                    break;
                case "activeLocations":
                    if (value instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Map.Entry<String, Long>> locations = (List<Map.Entry<String, Long>>) value;
                        displayActiveLocations(locations);
                    }
                    break;
                default:
                    System.out.println(key + ": " + value);
            }
        }
    }

    // Utility methods
    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.toString().replace("T", " ").substring(0, 16);
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    // Success/Error display with formatting
    public static void displaySuccess(String message) {
        ConsoleUtils.printSuccess("✓ " + message);
    }

    public static void displayError(String message) {
        ConsoleUtils.printError("✗ " + message);
    }

    public static void displayWarning(String message) {
        ConsoleUtils.printWarning("⚠ " + message);
    }

    public static void displayInfo(String message) {
        ConsoleUtils.printInfo("ℹ " + message);
    }
}
