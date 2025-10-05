package dao;

import entity.FraudAlert;
import entity.enums.AlertLevel;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FraudAlertDAO {

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

    public List<FraudAlert> findByLevel(AlertLevel level) throws SQLException {
        return findByAlertLevel(level);
    }

    public List<FraudAlert> findCriticalAlerts() throws SQLException {
        return findByAlertLevel(AlertLevel.CRITICAL);
    }

    public boolean delete(int alertId) throws SQLException {
        String sql = "DELETE FROM FraudAlert WHERE alertId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

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
