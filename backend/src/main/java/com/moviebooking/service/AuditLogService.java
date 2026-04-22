package com.moviebooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

/**
 * AuditLogService - Logs all user actions to the database
 *
 * This service automatically records:
 * - Login/Logout events
 * - Movie operations (Create, Update, Delete)
 * - Booking operations
 * - Payment operations
 * - Show scheduling
 *
 * The database triggers then capture these and create detailed audit records.
 */
@Service
public class AuditLogService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Log user action to database
     */
    public void logAction(String tableName, String operation, Long recordId,
                         String oldValue, String newValue, String userType, Long userId) {
        try {
            String sql = "INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value, user_type, user_id, timestamp) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

            jdbcTemplate.update(sql, tableName, operation, recordId, oldValue, newValue, userType, userId);
        } catch (Exception e) {
            System.err.println("Error logging action: " + e.getMessage());
        }
    }

    /**
     * Log user login
     */
    public void logLogin(String userType, Long userId, String userEmail) {
        logAction("users", "LOGIN", userId, null,
                 "Login from IP: " + userEmail, userType, userId);
    }

    /**
     * Log user logout
     */
    public void logLogout(String userType, Long userId) {
        logAction("users", "LOGOUT", userId, null,
                 "User logged out", userType, userId);
    }

    /**
     * Log movie addition
     */
    public void logMovieAdded(Long movieId, String title, String genre, Long adminId) {
        logAction("movies", "INSERT", movieId,
                 null,
                 "Movie added: " + title + " (Genre: " + genre + ")",
                 "ADMIN", adminId);
    }

    /**
     * Log movie update
     */
    public void logMovieUpdated(Long movieId, String oldTitle, String newTitle, Long adminId) {
        logAction("movies", "UPDATE", movieId,
                 "Title: " + oldTitle,
                 "Title: " + newTitle,
                 "ADMIN", adminId);
    }

    /**
     * Log booking creation
     */
    public void logBookingCreated(Long bookingId, Long customerId, Long showId, int seats, double amount) {
        logAction("bookings", "INSERT", bookingId,
                 null,
                 "Booking created: " + seats + " seats, Amount: ₹" + amount,
                 "CUSTOMER", customerId);
    }

    /**
     * Log booking cancellation
     */
    public void logBookingCancelled(Long bookingId, Long customerId, double refundAmount) {
        logAction("bookings", "CANCEL", bookingId,
                 null,
                 "Booking cancelled, Refund: ₹" + refundAmount,
                 "CUSTOMER", customerId);
    }

    /**
     * Log payment processed
     */
    public void logPaymentProcessed(Long paymentId, Long bookingId, String paymentMethod,
                                   double amount, String status) {
        logAction("payments", "INSERT", paymentId,
                 null,
                 "Payment: " + paymentMethod + ", Amount: ₹" + amount + ", Status: " + status,
                 "CUSTOMER", null);
    }

    /**
     * Log show created
     */
    public void logShowCreated(Long showId, Long movieId, Long screenId, double price, Long adminId) {
        logAction("shows", "INSERT", showId,
                 null,
                 "Show created: Movie " + movieId + ", Screen " + screenId + ", Price: ₹" + price,
                 "ADMIN", adminId);
    }

    /**
     * Log theatre created
     */
    public void logTheatreCreated(Long theatreId, String theatreName, String city, Long adminId) {
        logAction("theatres", "INSERT", theatreId,
                 null,
                 "Theatre created: " + theatreName + " (" + city + ")",
                 "ADMIN", adminId);
    }
}
