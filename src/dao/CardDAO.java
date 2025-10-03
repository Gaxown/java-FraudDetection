package dao;

import entity.*;
import entity.enums.CardStatus;
import entity.enums.CardType;
import com.bank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDAO {

    // Create
    public Card save(Card card) throws SQLException {
        String sql = "INSERT INTO Card (cardNumber, expirationDate, cardStatus, cardType, customerId, dailyLimit, monthlyLimit, interestRate, availableBalance) VALUES (?, ?, ?::card_status, ?::card_type, ?, ?, ?, ?, ?) RETURNING cardId";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, card.getCardNumber());
            stmt.setDate(2, Date.valueOf(card.getExpirationDate()));
            stmt.setString(3, card.getCardStatus().name());
            stmt.setString(4, card.getCardType().name());
            stmt.setInt(5, card.getCustomerId());

            // Set type-specific fields
            if (card instanceof DebitCard debitCard) {
                stmt.setBigDecimal(6, debitCard.getDailyLimit());
                stmt.setNull(7, Types.NUMERIC);
                stmt.setNull(8, Types.NUMERIC);
                stmt.setNull(9, Types.NUMERIC);
            } else if (card instanceof CreditCard creditCard) {
                stmt.setNull(6, Types.NUMERIC);
                stmt.setBigDecimal(7, creditCard.getMonthlyLimit());
                stmt.setBigDecimal(8, creditCard.getInterestRate());
                stmt.setNull(9, Types.NUMERIC);
            } else if (card instanceof PrepaidCard prepaidCard) {
                stmt.setNull(6, Types.NUMERIC);
                stmt.setNull(7, Types.NUMERIC);
                stmt.setNull(8, Types.NUMERIC);
                stmt.setBigDecimal(9, prepaidCard.getAvailableBalance());
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int cardId = rs.getInt("cardId");
                card.setCardId(cardId);
                return card;
            }
            throw new SQLException("Failed to create card");
        }
    }

    // Read by ID
    public Optional<Card> findById(int cardId) throws SQLException {
        String sql = "SELECT * FROM Card WHERE cardId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCard(rs));
            }
            return Optional.empty();
        }
    }

    // Read all
    public List<Card> findAll() throws SQLException {
        String sql = "SELECT * FROM Card ORDER BY cardNumber";
        List<Card> cards = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    // Find by customer ID
    public List<Card> findByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM Card WHERE customerId = ?";
        List<Card> cards = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    // Find by card status
    public List<Card> findByCardStatus(CardStatus cardStatus) throws SQLException {
        String sql = "SELECT * FROM Card WHERE cardStatus = ?::card_status";
        List<Card> cards = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cardStatus.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    // Update
    public boolean update(Card card) throws SQLException {
        String sql = "UPDATE Card SET cardNumber = ?, expirationDate = ?, cardStatus = ?::card_status, dailyLimit = ?, monthlyLimit = ?, interestRate = ?, availableBalance = ? WHERE cardId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, card.getCardNumber());
            stmt.setDate(2, Date.valueOf(card.getExpirationDate()));
            stmt.setString(3, card.getCardStatus().name());

            // Set type-specific fields
            if (card instanceof DebitCard debitCard) {
                stmt.setBigDecimal(4, debitCard.getDailyLimit());
                stmt.setNull(5, Types.NUMERIC);
                stmt.setNull(6, Types.NUMERIC);
                stmt.setNull(7, Types.NUMERIC);
            } else if (card instanceof CreditCard creditCard) {
                stmt.setNull(4, Types.NUMERIC);
                stmt.setBigDecimal(5, creditCard.getMonthlyLimit());
                stmt.setBigDecimal(6, creditCard.getInterestRate());
                stmt.setNull(7, Types.NUMERIC);
            } else if (card instanceof PrepaidCard prepaidCard) {
                stmt.setNull(4, Types.NUMERIC);
                stmt.setNull(5, Types.NUMERIC);
                stmt.setNull(6, Types.NUMERIC);
                stmt.setBigDecimal(7, prepaidCard.getAvailableBalance());
            }

            stmt.setInt(8, card.getCardId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Delete
    public boolean delete(int cardId) throws SQLException {
        String sql = "DELETE FROM Card WHERE cardId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to Card
    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        int cardId = rs.getInt("cardId");
        String cardNumber = rs.getString("cardNumber");
        LocalDate expirationDate = rs.getDate("expirationDate").toLocalDate();
        CardStatus cardStatus = CardStatus.valueOf(rs.getString("cardStatus"));
        CardType cardType = CardType.valueOf(rs.getString("cardType"));
        int customerId = rs.getInt("customerId");

        return switch (cardType) {
            case DEBIT -> new DebitCard(
                cardId, cardNumber, expirationDate, cardStatus, customerId,
                rs.getBigDecimal("dailyLimit")
            );
            case CREDIT -> new CreditCard(
                cardId, cardNumber, expirationDate, cardStatus, customerId,
                rs.getBigDecimal("monthlyLimit"),
                rs.getBigDecimal("interestRate")
            );
            case PREPAID -> new PrepaidCard(
                cardId, cardNumber, expirationDate, cardStatus, customerId,
                rs.getBigDecimal("availableBalance")
            );
        };
    }
}
