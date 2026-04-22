package com.moviebooking.builder;

import com.moviebooking.model.Booking;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CREATIONAL PATTERN: BUILDER
 * ============================================================================
 *
 * Pattern Description:
 * Builder separates the construction of a complex object from its representation
 * so that the same construction process can create different representations.
 * It provides a fluent, step-by-step way to create complex objects.
 *
 * Problem Solved:
 * - Simplifies construction of complex objects with many parameters
 * - Provides optional parameters without multiple constructors
 * - Improves code readability (fluent API)
 * - Separates object construction from representation
 * - Allows incremental building and validation
 *
 * Implementation:
 * - BookingBuilder provides fluent API for building Booking
 * - Each method sets a parameter and returns 'this' for chaining
 * - build() method validates and creates the final Booking
 * - Optional setters for fields that have defaults
 *
 * Benefits:
 * ✓ Fluent, readable API
 * ✓ Optional parameters without overloaded constructors
 * ✓ Validation at build time
 * ✓ Better than large constructor with many parameters
 * ✓ Can build objects step-by-step
 *
 * Old Way (Before Builder - Multiple Constructors):
 * ```
 * new Booking(customer, show, seats);
 * new Booking(customer, show, seats, LocalDateTime.now());
 * new Booking(customer, show, seats, LocalDateTime.now(), BookingStatus.PENDING);
 * ```
 * Problem: Unclear which constructor to use, hard to maintain
 *
 * New Way (With Builder - Fluent API):
 * ```
 * Booking booking = new BookingBuilder()
 *     .forCustomer(customer)
 *     .forShow(show)
 *     .withSeats(seats)
 *     .atTime(LocalDateTime.now())
 *     .withStatus(BookingStatus.PENDING)
 *     .build();
 * ```
 * Benefit: Clear, readable, self-documenting
 *
 * Alternative Simpler Usage:
 * ```
 * Booking booking = new BookingBuilder()
 *     .forCustomer(customer)
 *     .forShow(show)
 *     .withSeats(seats)
 *     .build(); // Status and time have defaults
 * ```
 * ============================================================================
 */

/**
 * BookingBuilder - Fluent builder for creating Booking objects
 *
 * Example Usage:
 * ```
 * Booking booking = new BookingBuilder()
 *     .forCustomer(customer)
 *     .forShow(show)
 *     .withSeats(Arrays.asList(seat1, seat2, seat3))
 *     .atTime(LocalDateTime.now())
 *     .build();
 * ```
 *
 * With Default Values:
 * ```
 * Booking booking = new BookingBuilder()
 *     .forCustomer(customer)
 *     .forShow(show)
 *     .withSeats(seatList)
 *     .build(); // Time defaults to now, status to PENDING
 * ```
 *
 * Advantages:
 * - Self-documenting code (forCustomer clearly sets customer)
 * - Optional parameters (only set what you need)
 * - Validation at build time
 * - Easier to maintain than multiple constructors
 */
public class BookingBuilder {

    // Required parameters
    private Customer customer;
    private Show show;
    private List<Seat> seats;

    // Optional parameters with defaults
    private LocalDateTime bookingTime = LocalDateTime.now();

    /**
     * STEP 1: Set the customer for this booking
     * @param customer Customer making the booking
     * @return Builder instance for chaining
     */
    public BookingBuilder forCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        return this;
    }

    /**
     * STEP 2: Set the show for this booking
     * @param show Show to book tickets for
     * @return Builder instance for chaining
     */
    public BookingBuilder forShow(Show show) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }
        this.show = show;
        return this;
    }

    /**
     * STEP 3: Set the seats for this booking
     * @param seats List of seats to book
     * @return Builder instance for chaining
     */
    public BookingBuilder withSeats(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("Seats list cannot be null or empty");
        }
        this.seats = new ArrayList<>(seats);
        return this;
    }

    /**
     * Add a single seat to booking
     * Useful for dynamic seat addition
     *
     * @param seat Seat to add
     * @return Builder instance for chaining
     */
    public BookingBuilder addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }
        if (this.seats == null) {
            this.seats = new ArrayList<>();
        }
        this.seats.add(seat);
        return this;
    }

    /**
     * OPTIONAL STEP: Set custom booking time
     * Defaults to current time if not specified
     *
     * @param bookingTime Time of booking
     * @return Builder instance for chaining
     */
    public BookingBuilder atTime(LocalDateTime bookingTime) {
        if (bookingTime == null) {
            throw new IllegalArgumentException("Booking time cannot be null");
        }
        this.bookingTime = bookingTime;
        return this;
    }

    /**
     * OPTIONAL STEP: Set booking time to current time
     * (This is already the default, but available for clarity)
     *
     * @return Builder instance for chaining
     */
    public BookingBuilder atCurrentTime() {
        this.bookingTime = LocalDateTime.now();
        return this;
    }

    /**
     * BUILD STEP: Create the Booking object
     *
     * Validation performed here:
     * - All required fields are set
     * - No null values in seats
     * - Booking is valid and ready to use
     *
     * @return Built Booking object
     * @throws IllegalStateException if required fields are missing
     */
    public Booking build() {
        // Validation: Check all required fields are set
        if (customer == null) {
            throw new IllegalStateException("Customer is required. Call forCustomer() first.");
        }
        if (show == null) {
            throw new IllegalStateException("Show is required. Call forShow() first.");
        }
        if (seats == null || seats.isEmpty()) {
            throw new IllegalStateException("At least one seat is required. Call withSeats() first.");
        }

        // Additional validation
        validateSeats();

        // Create and return the booking
        Booking booking = new Booking(customer, show, seats);
        booking.setBookingTime(bookingTime);

        return booking;
    }

    /**
     * Validate seats before building
     * Ensures no null seats, proper seat types, etc.
     */
    private void validateSeats() {
        for (Seat seat : seats) {
            if (seat == null) {
                throw new IllegalStateException("Seats list contains null values");
            }
            if (seat.getId() == null) {
                throw new IllegalStateException("Seat ID cannot be null");
            }
        }
    }

    /**
     * Reset builder for reuse
     * Creates a fresh builder state
     *
     * @return New BookingBuilder instance
     */
    public static BookingBuilder newBooking() {
        return new BookingBuilder();
    }

    @Override
    public String toString() {
        return "BookingBuilder{" +
                "customer=" + (customer != null ? customer.getName() : "null") +
                ", show=" + (show != null ? show.getId() : "null") +
                ", seats=" + (seats != null ? seats.size() : 0) +
                ", bookingTime=" + bookingTime +
                '}';
    }
}
