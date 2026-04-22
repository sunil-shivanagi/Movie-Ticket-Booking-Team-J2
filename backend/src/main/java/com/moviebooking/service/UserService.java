package com.moviebooking.service;

import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Handles user authentication and management
 *
 * This service manages user registration, login, and profile operations.
 * For a production system, passwords would be encrypted using BCrypt.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    // ========================================================================
    // CUSTOMER OPERATIONS
    // ========================================================================

    /**
     * Register a new customer
     */
    public Customer registerCustomer(String name, String email, String password, String phone) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        Customer customer = new Customer(name, email, password, phone);
        return customerRepository.save(customer);
    }

    /**
     * Customer login
     */
    public Customer loginCustomer(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + email));

        // In production, use BCrypt password matching
        if (!customer.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return customer;
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    /**
     * Get customer by email
     */
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get customer with bookings
     */
    public Customer getCustomerWithBookings(Long id) {
        return customerRepository.findByIdWithBookings(id).orElse(null);
    }

    /**
     * Update customer profile
     */
    public Customer updateCustomerProfile(Long id, String name, String phone) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        if (name != null) customer.setName(name);
        if (phone != null) customer.setPhone(phone);

        return customerRepository.save(customer);
    }

    /**
     * Change customer password
     */
    public void changeCustomerPassword(Long id, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        if (!customer.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Current password is incorrect");
        }

        customer.setPassword(newPassword);
        customerRepository.save(customer);
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ========================================================================
    // ADMIN OPERATIONS
    // ========================================================================

    /**
     * Register a new admin
     */
    public Admin registerAdmin(String name, String email, String password, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        Admin admin = new Admin(name, email, password, phone);
        return adminRepository.save(admin);
    }

    /**
     * Admin login
     */
    public Admin loginAdmin(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found: " + email));

        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return admin;
    }

    /**
     * Get admin by ID
     */
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    /**
     * Get admin by email
     */
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    // ========================================================================
    // GENERAL USER OPERATIONS
    // ========================================================================

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user type
     */
    public String getUserType(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return null;
        if (user instanceof Admin) return "ADMIN";
        if (user instanceof Customer) return "CUSTOMER";
        return "UNKNOWN";
    }

    /**
     * Authenticate any user
     */
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}
