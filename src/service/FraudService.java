package service;

import dao.CardOperationDAO;
import dao.FraudAlertDAO;
import entity.FraudAlert;
import entity.CardOperation;
import entity.enums.AlertLevel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FraudService {
    private final CardOperationDAO operationDAO;
    private final FraudAlertDAO alertDAO;
    private final CardService cardService;

    private static final BigDecimal SUSPICIOUS_AMOUNT = new BigDecimal("5000");
    private static final long SUSPICIOUS_OPERATION_MINUTES = 30;

    public FraudService() {
        this.operationDAO = new CardOperationDAO();
        this.alertDAO = new FraudAlertDAO();
        this.cardService = new CardService();
    }

    public void detectFraud(int cardId) throws SQLException {
        List<CardOperation> operations = operationDAO.findByCardId(cardId);

        if (operations.isEmpty()) {
            return;
        }

        detectHighAmountTransactions(operations);
        detectRapidTransactions(operations);
        detectMultipleAttempts(operations);
    }

    private void detectHighAmountTransactions(List<CardOperation> operations) throws SQLException {
        for (CardOperation op : operations) {
            if (op.getAmount().compareTo(SUSPICIOUS_AMOUNT) > 0) {
                String description = String.format(
                    "High amount detected: %.2f EUR at %s on %s",
                    op.getAmount(),
                    op.getLocation(),
                    op.getOperationDate()
                );
                createAlert(op.getCardId(), description, AlertLevel.WARNING);
            }
        }
    }

    private void detectRapidTransactions(List<CardOperation> operations) throws SQLException {
        for (int i = 0; i < operations.size() - 1; i++) {
            CardOperation op1 = operations.get(i);
            CardOperation op2 = operations.get(i + 1);

            Duration duration = Duration.between(op2.getOperationDate(), op1.getOperationDate());
            long minutesDiff = Math.abs(duration.toMinutes());

            if (minutesDiff <= SUSPICIOUS_OPERATION_MINUTES && !op1.getLocation().equals(op2.getLocation())) {
                String description = String.format(
                    "Suspicious operations: %s at %s and %s at %s within %d minutes",
                    op1.getLocation(),
                    op1.getOperationDate(),
                    op2.getLocation(),
                    op2.getOperationDate(),
                    minutesDiff
                );
                createAlert(op1.getCardId(), description, AlertLevel.CRITICAL);
                cardService.blockCard(op1.getCardId());
            }
        }
    }

    private void detectMultipleAttempts(List<CardOperation> operations) throws SQLException {
        if (operations.size() < 5) {
            return;
        }

        for (int i = 0; i < operations.size() - 4; i++) {
            CardOperation first = operations.get(i);
            CardOperation fifth = operations.get(i + 4);

            Duration duration = Duration.between(fifth.getOperationDate(), first.getOperationDate());
            long minutesDiff = Math.abs(duration.toMinutes());

            if (minutesDiff <= 60) {
                String description = String.format(
                    "Multiple attempts detected: 5+ operations in %d minutes",
                    minutesDiff
                );
                createAlert(first.getCardId(), description, AlertLevel.CRITICAL);
                cardService.suspendCard(first.getCardId());
            }
        }
    }

    public FraudAlert createAlert(int cardId, String description, AlertLevel level) throws SQLException {
        FraudAlert alert = new FraudAlert(
            0,
            description,
            level,
            cardId,
            LocalDateTime.now()
        );
        return alertDAO.save(alert);
    }

    public List<FraudAlert> getAlertsByCard(int cardId) throws SQLException {
        return alertDAO.findByCardId(cardId);
    }

    public List<FraudAlert> getAllAlerts() throws SQLException {
        return alertDAO.findAll();
    }

    public List<FraudAlert> getCriticalAlerts() throws SQLException {
        return alertDAO.findCriticalAlerts();
    }

    public List<FraudAlert> getAlertsByLevel(AlertLevel level) throws SQLException {
        return alertDAO.findByLevel(level);
    }

    public boolean deleteAlert(int id) throws SQLException {
        return alertDAO.delete(id);
    }
}
