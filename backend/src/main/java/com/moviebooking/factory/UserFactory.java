package com.moviebooking.factory;

import com.moviebooking.model.User;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Admin;

/**
 * ============================================================================
 * CREATIONAL PATTERN: FACTORY METHOD
 * ============================================================================
 *
 * Pattern Description:
 * Factory Method is a creational pattern that provides an interface for creating
 * objects, but lets subclasses or implementations decide which class to instantiate.
 * Instead of clients directly creating objects with 'new', they request objects
 * through a factory.
 *
 * Problem Solved:
 * - Centralizes object creation logic
 * - Makes it easy to add new user types without changing client code
 * - Encapsulates knowledge of which User subclass to create
 * - Supports OPEN/CLOSED PRINCIPLE: Open for extension, closed for modification
 *
 * Implementation:
 * - UserFactory interface defines methods to create User objects
 * - UserFactoryImpl implements the actual creation logic
 * - Clients call factory.createCustomer() or factory.createAdmin()
 * - To add new user type: Add new method and implementation, no client changes
 *
 * Benefits:
 * ✓ Centralized creation logic
 * ✓ Easy to add new user types
 * ✓ Consistent object creation
 * ✓ Separates object creation from business logic
 * ============================================================================
 */

/**
 * UserFactory Interface - Defines the contract for creating Users
 *
 * This interface specifies methods for creating different types of users.
 * Different implementations can create users differently (e.g., from database,
 * from API, from cache, etc.)
 */
public interface UserFactory {

    /**
     * Create a new Customer user
     *
     * @param name Customer name
     * @param email Customer email
     * @param password Customer password
     * @param phone Customer phone
     * @return Created Customer instance
     */
    Customer createCustomer(String name, String email, String password, String phone);

    /**
     * Create a new Admin user
     *
     * @param name Admin name
     * @param email Admin email
     * @param password Admin password
     * @param phone Admin phone
     * @return Created Admin instance
     */
    Admin createAdmin(String name, String email, String password, String phone);

    /**
     * Create a user based on type
     * Useful for dynamic user creation
     *
     * @param userType "CUSTOMER" or "ADMIN"
     * @param name User name
     * @param email User email
     * @param password User password
     * @param phone User phone
     * @return Created User instance
     */
    User createUser(String userType, String name, String email, String password, String phone);
}
