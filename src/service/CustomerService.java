package service;

import dao.CustomerDAO;
import entity.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer createCustomer(String name, String email, String phone) throws SQLException {
        Optional<Customer> existing = customerDAO.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A customer with this email already exists");
        }

        Customer customer = new Customer(0, name, email, phone);
        return customerDAO.save(customer);
    }

    public Optional<Customer> findCustomerById(int id) throws SQLException {
        return customerDAO.findById(id);
    }

    public Optional<Customer> findCustomerByEmail(String email) throws SQLException {
        return customerDAO.findByEmail(email);
    }

    public List<Customer> findAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        return customerDAO.update(customer);
    }

    public boolean deleteCustomer(int id) throws SQLException {
        return customerDAO.delete(id);
    }

    public List<Customer> searchCustomersByName(String name) throws SQLException {
        return customerDAO.searchByName(name);
    }
}
