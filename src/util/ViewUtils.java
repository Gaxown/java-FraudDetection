package util;

import entity.*;
import entity.enums.CardStatus;
import entity.enums.CardType;
import entity.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ViewUtils {

    public static void displayCustomerInfo(Customer customer) {
        System.out.printf("Customer: ID=%d, Name=%s, Email=%s, Phone=%s%n",
            customer.customerId(), customer.fullName(), customer.email(), customer.phoneNumber());
    }

    public static void displayCustomersList(List<Customer> customers) {
        if (customers.isEmpty()) {
            ConsoleUtils.printWarning("No customers found.");
            return;
        }

        System.out.println("\n--- Customers List ---");
        System.out.printf("%-5s %-18s %-24s %-15s%n", "ID", "Name", "Email", "Phone");
        System.out.println("-".repeat(65));

        for (Customer customer : customers) {
            System.out.printf("%-5d %-18s %-24s %-15s%n",
                customer.customerId(),
                truncate(customer.fullName(), 18),
                truncate(customer.email(), 24),
                customer.phoneNumber());
        }
    }

    public static void displayCardInfo(Card card) {
        String cardType = card.getCardType().toString();
        System.out.printf("Card: ID=%d, Number=%s, Type=%s, Status=%s%n",
            card.getCardId(), maskCardNumber(card.getCardNumber()), cardType,
            card.getStatus());
    }

    public static void displayCardsList(List<Card> cards) {
        if (cards.isEmpty()) {
            ConsoleUtils.printWarning("No cards found.");
            return;
        }

        System.out.println("\n--- Cards List ---");
        System.out.printf("%-5s %-20s %-10s %-12s %-10s%n", "ID", "Number", "Type", "Status", "Customer");
        System.out.println("-".repeat(65));

        for (Card card : cards) {
            System.out.printf("%-5d %-20s %-10s %-12s %-10d%n",
                card.getCardId(),
                maskCardNumber(card.getCardNumber()),
                card.getCardType(),
                card.getStatus(),
                card.getCustomerId());
        }
    }

    public static void displayCardDetails(Card card) {
        System.out.println("ID: " + card.getCardId());
        System.out.println("Number: " + maskCardNumber(card.getCardNumber()));
        System.out.println("Type: " + card.getCardType());
        System.out.println("Status: " + card.getStatus());
        System.out.println("Expiration: " + card.getExpirationDate());
        System.out.println("Customer ID: " + card.getCustomerId());

        if (card instanceof DebitCard) {
            DebitCard debitCard = (DebitCard) card;
            System.out.println("Daily Limit: " + debitCard.getDailyLimit());
        } else if (card instanceof CreditCard) {
            CreditCard creditCard = (CreditCard) card;
            System.out.println("Monthly Limit: " + creditCard.getMonthlyLimit());
            System.out.println("Interest Rate: " + creditCard.getInterestRate());
        } else if (card instanceof PrepaidCard) {
            PrepaidCard prepaidCard = (PrepaidCard) card;
            System.out.println("Available Balance: " + prepaidCard.getAvailableBalance());
        }
    }

    public static void displayOperationInfo(CardOperation operation) {
        System.out.printf("Operation: ID=%d, Date=%s, Amount=%.2f, Type=%s, Location=%s%n",
            operation.operationId(),
            formatDateTime(operation.operationDate()),
            operation.amount(),
            operation.operationType(),
            operation.location());
    }

    public static void displayOperationsList(List<CardOperation> operations) {
        if (operations.isEmpty()) {
            ConsoleUtils.printWarning("No operations found.");
            return;
        }

        System.out.println("\n--- Operations List ---");
        System.out.printf("%-5s %-20s %-10s %-15s %-20s %-8s%n",
            "ID", "Date", "Amount", "Type", "Location", "Card");
        System.out.println("-".repeat(85));

        for (CardOperation op : operations) {
            System.out.printf("%-5d %-20s %-10.2f %-15s %-20s %-8d%n",
                op.operationId(),
                formatDateTime(op.operationDate()),
                op.amount(),
                op.operationType(),
                truncate(op.location(), 20),
                op.cardId());
        }
    }

    public static void displayFraudAlertInfo(FraudAlert alert) {
        System.out.printf("Alert: ID=%d, Level=%s, Card=%d, Date=%s%n",
            alert.alertId(), alert.alertLevel(), alert.cardId(),
            formatDateTime(alert.creationDate()));
        System.out.println("Description: " + alert.description());
    }

    public static void displayFraudAlertsList(List<FraudAlert> alerts) {
        if (alerts.isEmpty()) {
            ConsoleUtils.printInfo("No fraud alerts found.");
            return;
        }

        System.out.println("\n--- Fraud Alerts List ---");
        System.out.printf("%-5s %-10s %-8s %-20s %-30s%n",
            "ID", "Level", "Card", "Date", "Description");
        System.out.println("-".repeat(80));

        for (FraudAlert alert : alerts) {
            System.out.printf("%-5d %-10s %-8d %-20s %-30s%n",
                alert.alertId(),
                alert.alertLevel(),
                alert.cardId(),
                formatDateTime(alert.creationDate()),
                truncate(alert.description(), 30));
        }
    }

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
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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

    public static void showSuccess(String message) {
        ConsoleUtils.printSuccess("✓ " + message);
    }

    public static void showError(String message) {
        ConsoleUtils.printError("✗ " + message);
    }

    public static void showWarning(String message) {
        ConsoleUtils.printWarning("⚠ " + message);
    }

    public static void showInfo(String message) {
        ConsoleUtils.printInfo("ℹ " + message);
    }
}
