package dao;

import entity.CardOperation;
import entity.enums.OperationType;
import com.bank.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardOperationDAO {

    // Create
    public CardOperation save(CardOperation cardOperation) throws SQLException {
        String sql = "INSERT INTO CardOperation (operationDate, amount, operationType, location, cardId) VALUES (?, ?, ?::operation_type, ?, ?) RETURNING operationId";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cardOperation.operationDate()));
            stmt.setBigDecimal(2, cardOperation.amount());
            stmt.setString(3, cardOperation.operationType().name());
            stmt.setString(4, cardOperation.location());
            stmt.setInt(5, cardOperation.cardId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int operationId = rs.getInt("operationId");
                return new CardOperation(operationId, cardOperation.operationDate(), cardOperation.amount(),
                                       cardOperation.operationType(), cardOperation.location(), cardOperation.cardId());
            }
            throw new SQLException("Failed to create card operation");
        }
    }

    // Read by ID
    public Optional<CardOperation> findById(int operationId) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE operationId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, operationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCardOperation(rs));
            }
            return Optional.empty();
        }
    }

    // Read all
    public List<CardOperation> findAll() throws SQLException {
        String sql = "SELECT * FROM CardOperation ORDER BY operationDate DESC";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Find by card ID
    public List<CardOperation> findByCardId(int cardId) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE cardId = ? ORDER BY operationDate DESC";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Find by operation type
    public List<CardOperation> findByOperationType(OperationType operationType) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE operationType = ?::operation_type ORDER BY operationDate DESC";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, operationType.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Find by date range
    public List<CardOperation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE operationDate BETWEEN ? AND ? ORDER BY operationDate DESC";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Find by card and date range
    public List<CardOperation> findByCardAndDateRange(int cardId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE cardId = ? AND operationDate BETWEEN ? AND ? ORDER BY operationDate DESC";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            stmt.setTimestamp(2, Timestamp.valueOf(startDate));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Find recent operations for a card (last N operations)
    public List<CardOperation> findRecentOperationsByCard(int cardId, int limit) throws SQLException {
        String sql = "SELECT * FROM CardOperation WHERE cardId = ? ORDER BY operationDate DESC LIMIT ?";
        List<CardOperation> cardOperations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cardOperations.add(mapResultSetToCardOperation(rs));
            }
        }
        return cardOperations;
    }

    // Delete
    public boolean delete(int operationId) throws SQLException {
        String sql = "DELETE FROM CardOperation WHERE operationId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, operationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to CardOperation
    private CardOperation mapResultSetToCardOperation(ResultSet rs) throws SQLException {
        return new CardOperation(
            rs.getInt("operationId"),
            rs.getTimestamp("operationDate").toLocalDateTime(),
            rs.getBigDecimal("amount"),
            OperationType.valueOf(rs.getString("operationType")),
            rs.getString("location"),
            rs.getInt("cardId")
        );
    }
}
