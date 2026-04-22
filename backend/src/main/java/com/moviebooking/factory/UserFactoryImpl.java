package com.moviebooking.factory;

import com.moviebooking.model.User;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Admin;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * FACTORY METHOD IMPLEMENTATION
 * ============================================================================
 *
 * This is the concrete implementation of UserFactory.
 * It encapsulates the logic for creating different user types.
 *
 * Usage in Code:
 * Instead of: Customer customer = new Customer(name, email, password, phone);
 * Use:        Customer customer = userFactory.createCustomer(name, email, password, phone);
 *
 * Advantages:
 * - If Customer class changes, only factory needs update
 * - Can add validation/initialization logic here
 * - Can add caching or object pooling
 * - Easy to test (mock the factory)
 * ============================================================================
 */

@Component
public class UserFactoryImpl implements UserFactory {

    /**
     * Create a new Customer instance
     * Encapsulates Customer creation logic
     */
    @Override
    public Customer createCustomer(String name, String email, String password, String phone) {
        // Can add initialization logic here if needed
        // Example: validate email format, hash password, etc.

        Customer customer = new Customer(name, email, password, phone);

        // Future: Can add logging, caching, audit trail, etc.
        // logger.info("Created new customer: " + email);

        return customer;
    }

    /**
     * Create a new Admin instance
     * Encapsulates Admin creation logic
     */
    @Override
    public Admin createAdmin(String name, String email, String password, String phone) {
        // Can add initialization logic here if needed

        Admin admin = new Admin(name, email, password, phone);

        // Future: Can add logging, audit trail, etc.
        // logger.info("Created new admin: " + email);
        // auditService.logAdminCreation(email);

        return admin;
    }

    /**
     * Create a user based on type parameter
     * This demonstrates dynamic object creation
     *
     * Example Usage:
     *   User user = factory.createUser("CUSTOMER", name, email, pwd, phone);
     *   User user = factory.createUser("ADMIN", name, email, pwd, phone);
     *
     * This is useful when the user type is determined at runtime
     */
    @Override
    public User createUser(String userType, String name, String email, String password, String phone) {
        if ("CUSTOMER".equalsIgnoreCase(userType)) {
            return createCustomer(name, email, password, phone);
        } else if ("ADMIN".equalsIgnoreCase(userType)) {
            return createAdmin(name, email, password, phone);
        } else {
            throw new IllegalArgumentException("Unknown user type: " + userType);
        }
    }

    /**
     * Future Extension Point: Add new user types here
     *
     * Example: To add a new MODERATOR user type:
     * 1. Create Moderator class extending User
     * 2. Add createModerator() method here
     * 3. Update createUser() method to handle "MODERATOR"
     * 4. Client code doesn't change!
     *
     * This demonstrates the OPEN/CLOSED PRINCIPLE in action.
     */
}
