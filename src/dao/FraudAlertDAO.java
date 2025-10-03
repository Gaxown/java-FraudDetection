package dao;

import entity.FraudAlert;
import entity.enums.AlertLevel;
import com.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FraudAlertDAO {

    // Create
    public FraudAlert save(FraudAlert fraudAlert) throws SQLException {
        String sql = "INSERT INTO FraudAlert (description, alertLevel, cardId, creationDate) VALUES (?, ?::alert_level, ?, ?) RETURNING alertId";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fraudAlert.description());
            stmt.setString(2, fraudAlert.alertLevel().name());
            stmt.setInt(3, fraudAlert.cardId());
            stmt.setTimestamp(4, Timestamp.valueOf(fraudAlert.creationDate()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int alertId = rs.getInt("alertId");
                return new FraudAlert(alertId, fraudAlert.description(), fraudAlert.alertLevel(),
                                    fraudAlert.cardId(), fraudAlert.creationDate());
            }
            throw new SQLException("Failed to create fraud alert");
        }
    }

    // Read by ID
    public Optional<FraudAlert> findById(int alertId) throws SQLException {
        String sql = "SELECT * FROM FraudAlert WHERE alertId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToFraudAlert(rs));
            }
            return Optional.empty();
        }
    }

    // Read all
    public List<FraudAlert> findAll() throws SQLException {
        String sql = "SELECT * FROM FraudAlert ORDER BY creationDate DESC";
        List<FraudAlert> fraudAlerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fraudAlerts.add(mapResultSetToFraudAlert(rs));
            }
        }
        return fraudAlerts;
    }

    // Find by card ID
    public List<FraudAlert> findByCardId(int cardId) throws SQLException {
        String sql = "SELECT * FROM FraudAlert WHERE cardId = ? ORDER BY creationDate DESC";
        List<FraudAlert> fraudAlerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                fraudAlerts.add(mapResultSetToFraudAlert(rs));
            }
        }
        return fraudAlerts;
    }

    // Find by alert level
    public List<FraudAlert> findByAlertLevel(AlertLevel alertLevel) throws SQLException {
        String sql = "SELECT * FROM FraudAlert WHERE alertLevel = ?::alert_level ORDER BY creationDate DESC";
        List<FraudAlert> fraudAlerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, alertLevel.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                fraudAlerts.add(mapResultSetToFraudAlert(rs));
            }
        }
        return fraudAlerts;
    }

    // Find critical alerts
    public List<FraudAlert> findCriticalAlerts() throws SQLException {
        return findByAlertLevel(AlertLevel.CRITICAL);
    }

    // Delete
    public boolean delete(int alertId) throws SQLException {
        String sql = "DELETE FROM FraudAlert WHERE alertId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to FraudAlert
    private FraudAlert mapResultSetToFraudAlert(ResultSet rs) throws SQLException {
        return new FraudAlert(
            rs.getInt("alertId"),
            rs.getString("description"),
            AlertLevel.valueOf(rs.getString("alertLevel")),
            rs.getInt("cardId"),
            rs.getTimestamp("creationDate").toLocalDateTime()
        );
    }
}
