package service;

import dao.CardOperationDAO;
import entity.CardOperation;
import entity.enums.OperationType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OperationService {
    private final CardOperationDAO operationDAO;
    private final CardService cardService;

    public OperationService() {
        this.operationDAO = new CardOperationDAO();
        this.cardService = new CardService();
    }

    // Create operation (with limit verification)
    public CardOperation recordOperation(int cardId, BigDecimal amount, OperationType type, String location) throws SQLException {
        // Verify card limit before creating operation
        if (!cardService.verifyLimit(cardId, amount)) {
            throw new IllegalArgumentException("Operation refused: limit exceeded or card inactive");
        }

        CardOperation operation = new CardOperation(
            0,
            LocalDateTime.now(),
            amount,
            type,
            location,
            cardId
        );

        return operationDAO.save(operation);
    }

    // Create operation at specific time
    public CardOperation recordOperationWithDate(int cardId, BigDecimal amount, OperationType type, String location, LocalDateTime date) throws SQLException {
        if (!cardService.verifyLimit(cardId, amount)) {
            throw new IllegalArgumentException("Operation refused: limit exceeded or card inactive");
        }

        CardOperation operation = new CardOperation(
            0,
            date,
            amount,
            type,
            location,
            cardId
        );

        return operationDAO.save(operation);
    }

    // Get operation by ID
    public Optional<CardOperation> findOperationById(int id) throws SQLException {
        return operationDAO.findById(id);
    }

    // Get operations by card
    public List<CardOperation> findOperationsByCard(int cardId) throws SQLException {
        return operationDAO.findByCardId(cardId);
    }

    // Get operations by type
    public List<CardOperation> findOperationsByType(OperationType type) throws SQLException {
        return operationDAO.findByType(type);
    }

    // Get operations by location
    public List<CardOperation> findOperationsByLocation(String location) throws SQLException {
        return operationDAO.findByLocation(location);
    }

    // Get operations by amount range
    public List<CardOperation> findOperationsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) throws SQLException {
        return operationDAO.findByAmountRange(minAmount, maxAmount);
    }

    // Get operations by date range
    public List<CardOperation> findOperationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return operationDAO.findByDateRange(startDate, endDate);
    }

    // Get all operations
    public List<CardOperation> findAllOperations() throws SQLException {
        return operationDAO.findAll();
    }

    // Update operation
    public boolean updateOperation(CardOperation operation) throws SQLException {
        return operationDAO.update(operation);
    }

    // Delete operation
    public boolean deleteOperation(int id) throws SQLException {
        return operationDAO.delete(id);
    }

    // Get recent operations (last 30 days)
    public List<CardOperation> getRecentOperations(int cardId) throws SQLException {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return operationDAO.findByCardIdAndDateAfter(cardId, thirtyDaysAgo);
    }

    // Calculate total amount for a card in a date range
    public BigDecimal calculateTotalAmount(int cardId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<CardOperation> operations = operationDAO.findByCardIdAndDateRange(cardId, startDate, endDate);
        return operations.stream()
                .map(CardOperation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
