package com.moviebooking.facade;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * STRUCTURAL PATTERN: FACADE
 * ============================================================================
 *
 * Pattern Description:
 * Facade provides a unified, simplified interface to a set of interfaces in a
 * subsystem. It hides the complexity of multiple components working together.
 * Instead of clients directly interacting with many services, they interact
 * with one facade that coordinates everything.
 *
 * Problem Solved:
 * - Hides complexity of multiple services (Booking, Payment, Seat management)
 * - Provides single entry point for complex operations
 * - Reduces coupling between client and subsystems
 * - Makes client code simpler and more readable
 *
 * Implementation:
 * - BookingFacade coordinates BookingService, PaymentService, SeatRepository
 * - Clients call simple methods like processCompleteBooking()
 * - Facade handles all the complexity internally
 * - If internal services change, only facade needs update
 *
 * Benefits:
 * ✓ Simplifies client code
 * ✓ Reduces coupling
 * ✓ Provides clear, high-level API
 * ✓ Easier to maintain and modify
 * ✓ Single responsibility: coordinate subsystems
 *
 * Example Usage in Code (Before Facade):
 * // Client has to orchestrate multiple services
 * Booking booking = bookingService.createBooking(customerId, showId, seatIds);
 * List<Seat> availableSeats = bookingService.getAvailableSeats(showId);
 * Payment payment = paymentService.processPayment(booking.getId(), method, details);
 * booking.confirm();
 * // ... more complex logic
 *
 * Example Usage with Facade (After):
 * // Facade handles all complexity
 * BookingResult result = bookingFacade.processCompleteBooking(
 *     customerId, showId, seatIds, paymentMethod, paymentDetails
 * );
 * ============================================================================
 */

/**
 * BookingFacade - Simplifies the booking process
 *
 * This facade coordinates multiple services to provide a simple interface
 * for the complete booking workflow. Instead of clients calling multiple
 * services in the right order, they call one facade method.
 *
 * Coordinated Services:
 * - BookingService: Create and manage bookings
 * - PaymentService: Process payments
 * - ShowService: Get show details
 * - SeatRepository: Manage seats
 */
@Service
public class BookingFacade {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ShowService showService;

    @Autowired
    public BookingFacade(BookingService bookingService,
                         PaymentService paymentService,
                         ShowService showService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.showService = showService;
    }

    /**
     * ========================================================================
     * FACADE METHOD 1: Complete Booking Process
     * ========================================================================
     *
     * This method encapsulates the entire booking workflow:
     * 1. Validate show exists
     * 2. Check seat availability
     * 3. Create booking
     * 4. Process payment
     * 5. Confirm booking
     * 6. Return result
     *
     * Client Code Before Facade:
     * ```
     * Show show = showService.getShowById(showId);
     * List<Seat> availableSeats = bookingService.getAvailableSeats(showId);
     * if (!bookingService.areSeatsAvailable(showId, seatIds)) {
     *     throw new Exception("Seats not available");
     * }
     * Booking booking = bookingService.createBooking(customerId, showId, seatIds);
     * Payment payment = paymentService.processPayment(booking.getId(), method, details);
     * if (payment.isSuccessful()) {
     *     booking.confirm();
     * }
     * return booking;
     * ```
     *
     * Client Code After Facade (much simpler!):
     * ```
     * Booking booking = bookingFacade.processCompleteBooking(
     *     customerId, showId, seatIds, method, details
     * );
     * ```
     *
     * @param customerId Customer making the booking
     * @param showId Show to book tickets for
     * @param seatIds Seats to book
     * @param paymentMethod Payment method
     * @param paymentDetails Payment details
     * @return Completed booking with payment
     */
    @Transactional
    public Booking processCompleteBooking(Long customerId,
                                          Long showId,
                                          List<Long> seatIds,
                                          PaymentMethod paymentMethod,
                                          Map<String, String> paymentDetails) {

        // STEP 1: Validate show exists
        Show show = showService.getShowById(showId);
        if (show == null) {
            throw new RuntimeException("Show not found: " + showId);
        }

        // STEP 2: Check seat availability
        if (!bookingService.areSeatsAvailable(showId, seatIds)) {
            throw new RuntimeException("Selected seats are not available");
        }

        // STEP 3: Create booking
        Booking booking = bookingService.createBooking(customerId, showId, seatIds);

        // STEP 4: Process payment
        Payment payment = paymentService.processPayment(booking.getId(), paymentMethod, paymentDetails);

        // STEP 5: If payment failed, throw exception
        if (!payment.isSuccessful()) {
            throw new RuntimeException("Payment failed: " + payment.getPaymentDetails());
        }

        // STEP 6: Return completed booking
        return booking;
    }

    /**
     * ========================================================================
     * FACADE METHOD 2: Get Booking Details
     * ========================================================================
     *
     * Simplified method to get complete booking details
     * Coordinates with multiple services internally
     */
    @Transactional(readOnly = true)
    public BookingDetailsDTO getBookingDetails(Long bookingId) {
        // Facade coordinates multiple services
        Booking booking = bookingService.getBookingWithSeats(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found: " + bookingId);
        }

        Payment payment = paymentService.getPaymentByBooking(bookingId);
        Show show = showService.getShowById(booking.getShow().getId());

        // Return aggregated DTO
        return new BookingDetailsDTO(booking, payment, show);
    }

    /**
     * ========================================================================
     * FACADE METHOD 3: Cancel Booking with Refund
     * ========================================================================
     *
     * Encapsulates the cancellation process
     * May involve: cancel booking, process refund, etc.
     */
    @Transactional
    public void cancelBookingWithRefund(Long bookingId) {
        // Facade handles the workflow
        Booking booking = bookingService.cancelBooking(bookingId);

        // In the future, could trigger refund process
        // refundService.processRefund(bookingId, booking.getTotalAmount());

        // Log cancellation
        // auditService.logBookingCancellation(bookingId);
    }

    /**
     * ========================================================================
     * FACADE METHOD 4: Get Available Seats with Pricing
     * ========================================================================
     *
     * Facade coordinates seat info with pricing
     */
    @Transactional(readOnly = true)
    public SeatAvailabilityDTO getSeatsWithPricing(Long showId) {
        Show show = showService.getShowById(showId);
        if (show == null) {
            throw new RuntimeException("Show not found: " + showId);
        }

        List<Seat> availableSeats = bookingService.getAvailableSeats(showId);
        List<Seat> bookedSeats = bookingService.getBookedSeats(showId);

        return new SeatAvailabilityDTO(show, availableSeats, bookedSeats);
    }

    /**
     * ========================================================================
     * FACADE METHOD 5: Get Customer Booking Summary
     * ========================================================================
     *
     * Returns a summary of customer's bookings without exposing
     * the complexity of multiple service calls
     */
    @Transactional(readOnly = true)
    public CustomerBookingSummaryDTO getCustomerBookingSummary(Long customerId) {
        List<Booking> bookings = bookingService.getCustomerBookings(customerId);

        // Calculate summary metrics
        long totalBookings = bookings.size();
        long confirmedBookings = bookings.stream()
                .filter(b -> b.isConfirmed())
                .count();
        Double totalSpent = bookings.stream()
                .filter(b -> b.isConfirmed())
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        return new CustomerBookingSummaryDTO(totalBookings, confirmedBookings, totalSpent);
    }

    /**
     * ========================================================================
     * DTOs (Data Transfer Objects) used by Facade
     * ========================================================================
     * These classes aggregate data from multiple services
     * for easy consumption by clients
     */

    public static class BookingDetailsDTO {
        public Booking booking;
        public Payment payment;
        public Show show;

        public BookingDetailsDTO(Booking booking, Payment payment, Show show) {
            this.booking = booking;
            this.payment = payment;
            this.show = show;
        }

        public String getSummary() {
            return String.format(
                "Booking: %d | Movie: %s | Amount: Rs.%.2f | Status: %s",
                booking.getId(),
                show.getMovie().getTitle(),
                booking.getTotalAmount(),
                booking.getStatus()
            );
        }
    }

    public static class SeatAvailabilityDTO {
        public Show show;
        public List<Seat> availableSeats;
        public List<Seat> bookedSeats;

        public SeatAvailabilityDTO(Show show, List<Seat> available, List<Seat> booked) {
            this.show = show;
            this.availableSeats = available;
            this.bookedSeats = booked;
        }

        public int getAvailableCount() {
            return availableSeats != null ? availableSeats.size() : 0;
        }

        public int getBookedCount() {
            return bookedSeats != null ? bookedSeats.size() : 0;
        }

        public double getOccupancyPercentage() {
            int total = getAvailableCount() + getBookedCount();
            return total > 0 ? (getBookedCount() * 100.0 / total) : 0;
        }
    }

    public static class CustomerBookingSummaryDTO {
        public long totalBookings;
        public long confirmedBookings;
        public Double totalSpent;

        public CustomerBookingSummaryDTO(long total, long confirmed, Double spent) {
            this.totalBookings = total;
            this.confirmedBookings = confirmed;
            this.totalSpent = spent;
        }

        public String getSummary() {
            return String.format(
                "Total Bookings: %d | Confirmed: %d | Total Spent: Rs.%.2f",
                totalBookings, confirmedBookings, totalSpent
            );
        }
    }
}
