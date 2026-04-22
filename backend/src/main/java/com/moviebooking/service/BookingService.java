package com.moviebooking.service;

import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// ============================================================================
// BOOKING SERVICE - Works with GRASP: INFORMATION EXPERT
// ============================================================================
//
// This service coordinates booking operations but DELEGATES to Booking entity
// for operations that require Booking's data - following Information Expert!
//
// Example:
// - BookingService.createBooking() creates the Booking
// - But Booking.calculateTotalAmount() computes the total (Booking has the data)
// - BookingService doesn't duplicate the calculation logic!
// ============================================================================

/**
 * BookingService - Coordinates booking operations
 *
 * Key Point: This service uses Booking as the Information Expert.
 * It doesn't duplicate logic that Booking already has.
 *
 * For example, to get total amount:
 * WRONG: bookingService.calculateTotal(seats, showPrice) - duplicates logic
 * RIGHT: booking.calculateTotalAmount() - Booking is the Information Expert!
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          ShowRepository showRepository,
                          SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    // ========================================================================
    // CREATE BOOKING
    // ========================================================================

    /**
     * Create a new booking for a customer
     *
     * Note: We create the Booking, but Booking calculates its own total
     * because Booking is the INFORMATION EXPERT (has the data)
     */
    @Transactional
    public Booking createBooking(Long customerId, Long showId, List<Long> seatIds) {
        // Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        // Validate show exists
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));

        // Get seats
        List<Seat> seats = seatRepository.findByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats not found");
        }

        // Check seat availability
        List<Seat> bookedSeats = bookingRepository.findBookedSeatsByShow(showId);
        List<Long> bookedSeatIds = bookedSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        for (Long seatId : seatIds) {
            if (bookedSeatIds.contains(seatId)) {
                throw new RuntimeException("Seat already booked: " + seatId);
            }
        }

        // Create booking - INFORMATION EXPERT: Booking calculates its own total!
        Booking booking = new Booking(customer, show, seats);
        // Note: Booking.calculateTotalAmount() is called in constructor
        // We don't calculate it here because Booking is the Information Expert

        return bookingRepository.save(booking);
    }

    // ========================================================================
    // GET BOOKINGS
    // ========================================================================

    /**
     * Get booking by ID
     */
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    /**
     * Get booking with seats loaded
     */
    public Booking getBookingWithSeats(Long bookingId) {
        return bookingRepository.findByIdWithSeats(bookingId).orElse(null);
    }

    /**
     * Get all bookings for a customer
     */
    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerIdWithDetails(customerId);
    }

    /**
     * Get bookings for a show
     */
    public List<Booking> getBookingsForShow(Long showId) {
        return bookingRepository.findByShowId(showId);
    }

    /**
     * Get confirmed bookings for a show
     */
    public List<Booking> getConfirmedBookingsForShow(Long showId) {
        return bookingRepository.findConfirmedBookingsByShow(showId);
    }

    // ========================================================================
    // BOOKING STATUS OPERATIONS
    // These delegate to Booking's own methods - Information Expert!
    // ========================================================================

    /**
     * Confirm a booking
     * Note: Booking.confirm() handles the status change - it's the Information Expert
     */
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // INFORMATION EXPERT: Booking knows how to confirm itself
        booking.confirm();

        return bookingRepository.save(booking);
    }

    /**
     * Cancel a booking
     * Note: Booking.cancel() handles the status change - it's the Information Expert
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // INFORMATION EXPERT: Booking knows how to cancel itself
        booking.cancel();

        return bookingRepository.save(booking);
    }

    // ========================================================================
    // SEAT AVAILABILITY
    // ========================================================================

    /**
     * Get available seats for a show
     */
    public List<Seat> getAvailableSeats(Long showId) {
        Show show = showRepository.findByIdWithDetails(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));

        // Get all seats for the screen
        List<Seat> allSeats = seatRepository.findByScreenIdOrdered(show.getScreen().getId());

        // Get booked seats
        List<Seat> bookedSeats = bookingRepository.findBookedSeatsByShow(showId);
        List<Long> bookedSeatIds = bookedSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        // Filter to available seats
        return allSeats.stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Get booked seats for a show
     */
    public List<Seat> getBookedSeats(Long showId) {
        return bookingRepository.findBookedSeatsByShow(showId);
    }

    /**
     * Check if seats are available
     */
    public boolean areSeatsAvailable(Long showId, List<Long> seatIds) {
        List<Seat> bookedSeats = bookingRepository.findBookedSeatsByShow(showId);
        List<Long> bookedSeatIds = bookedSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        for (Long seatId : seatIds) {
            if (bookedSeatIds.contains(seatId)) {
                return false;
            }
        }
        return true;
    }

    // ========================================================================
    // UTILITY METHODS
    // These use Booking's Information Expert methods!
    // ========================================================================

    /**
     * Get booking details - delegates to Booking which is the Information Expert
     */
    public String getBookingDetails(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithSeats(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // INFORMATION EXPERT: Booking formats its own details
        return booking.getBookingDetails();
    }

    /**
     * Calculate booking total - this method exists for cases where
     * we need to preview total before creating booking
     */
    public Double calculateBookingTotal(Long showId, List<Long> seatIds) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));

        List<Seat> seats = seatRepository.findByIds(seatIds);

        // Create temporary booking just to use its calculation
        // This demonstrates that Booking is the Information Expert
        Booking tempBooking = new Booking();
        tempBooking.setShow(show);
        tempBooking.setSeats(seats);

        // Use Booking's Information Expert method
        return tempBooking.calculateTotalAmount();
    }

    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
