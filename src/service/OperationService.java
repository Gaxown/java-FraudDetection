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

    public CardOperation recordOperation(int cardId, BigDecimal amount, OperationType type, String location) throws SQLException {
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

    public Optional<CardOperation> findOperationById(int id) throws SQLException {
        return operationDAO.findById(id);
    }

    public List<CardOperation> findOperationsByCard(int cardId) throws SQLException {
        return operationDAO.findByCardId(cardId);
    }

    public List<CardOperation> findOperationsByType(OperationType type) throws SQLException {
        return operationDAO.findByType(type);
    }

    public List<CardOperation> findOperationsByLocation(String location) throws SQLException {
        return operationDAO.findByLocation(location);
    }

    public List<CardOperation> findOperationsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) throws SQLException {
        return operationDAO.findByAmountRange(minAmount, maxAmount);
    }

    public List<CardOperation> findOperationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return operationDAO.findByDateRange(startDate, endDate);
    }

    public List<CardOperation> findAllOperations() throws SQLException {
        return operationDAO.findAll();
    }

    public boolean updateOperation(CardOperation operation) throws SQLException {
        return operationDAO.update(operation);
    }

    public boolean deleteOperation(int id) throws SQLException {
        return operationDAO.delete(id);
    }

    public List<CardOperation> getRecentOperations(int cardId) throws SQLException {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return operationDAO.findByCardIdAndDateAfter(cardId, thirtyDaysAgo);
    }

    public BigDecimal calculateTotalAmount(int cardId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<CardOperation> operations = operationDAO.findByCardIdAndDateRange(cardId, startDate, endDate);
        return operations.stream()
                .map(CardOperation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
