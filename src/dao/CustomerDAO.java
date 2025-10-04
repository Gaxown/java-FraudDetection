package dao;

import entity.Customer;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAO {

    // Create
    public Customer save(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (fullName, email, phoneNumber) VALUES (?, ?, ?) RETURNING customerId";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.fullName());
            stmt.setString(2, customer.email());
            stmt.setString(3, customer.phoneNumber());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int customerId = rs.getInt("customerId");
                return new Customer(customerId, customer.fullName(), customer.email(), customer.phoneNumber());
            }
            throw new SQLException("Failed to create customer");
        }
    }

    // Read by ID
    public Optional<Customer> findById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE customerId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            return Optional.empty();
        }
    }

    // Read all
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM Customer ORDER BY fullName";
        List<Customer> customers = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    // Find by email
    public Optional<Customer> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE email = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            return Optional.empty();
        }
    }

    // Find by phone number
    public Optional<Customer> findByPhoneNumber(String phoneNumber) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE phoneNumber = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            return Optional.empty();
        }
    }

    // Update
    public Customer update(Customer customer) throws SQLException {
        String sql = "UPDATE Customer SET fullName = ?, email = ?, phoneNumber = ? WHERE customerId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.fullName());
            stmt.setString(2, customer.email());
            stmt.setString(3, customer.phoneNumber());
            stmt.setInt(4, customer.customerId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return customer;
            }
            throw new SQLException("Failed to update customer");
        }
    }

    // Delete
    public boolean delete(int customerId) throws SQLException {
        String sql = "DELETE FROM Customer WHERE customerId = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to Customer
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customerId"),
            rs.getString("fullName"),
            rs.getString("email"),
            rs.getString("phoneNumber")
        );
    }
}
