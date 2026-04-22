package com.moviebooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ============================================================================
// BOOKING ENTITY - GRASP: INFORMATION EXPERT
// ============================================================================
//
// GRASP Principle: INFORMATION EXPERT
// Assigned to: Shrikant (PES1UG23CS565)
//
// VIOLATION (Before):
// - Customer class had methods: bookTicket(), selectShow(), makePayment()
// - These methods were in Customer but the data they needed was in Booking
//
// FIX (After):
// - Booking class is the INFORMATION EXPERT because it has all the data needed:
//   * customer, show, seats, totalAmount, status
// - Therefore, Booking should have the responsibility for:
//   * calculateTotalAmount() - knows seats and show price
//   * getBookingDetails() - knows all booking information
//   * isConfirmed(), isPending() - knows its own status
//   * getSeatCount() - knows its seats
//
// The class that has the data should have the responsibility!
// ============================================================================

/**
 * Booking entity - INFORMATION EXPERT
 *
 * This class demonstrates the Information Expert GRASP principle.
 * Since Booking has all the data about a booking (customer, show, seats, amount),
 * it should be responsible for operations that use this data.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "booked_seats",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<Seat> seats = new ArrayList<>();

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;

    // ========================================================================
    // CONSTRUCTORS
    // ========================================================================

    public Booking() {
        this.bookingTime = LocalDateTime.now();
    }

    public Booking(Customer customer, Show show, List<Seat> seats) {
        this.customer = customer;
        this.show = show;
        this.seats = seats;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        // INFORMATION EXPERT: Booking calculates its own total
        this.totalAmount = calculateTotalAmount();
    }

    // ========================================================================
    // GRASP: INFORMATION EXPERT METHODS
    // These methods are in Booking because Booking HAS the data they need
    // ========================================================================

    /**
     * INFORMATION EXPERT: Calculate total amount
     * Booking knows the show (price) and seats (type multipliers)
     * Therefore, Booking should calculate the total - not an external service!
     */
    public Double calculateTotalAmount() {
        if (show == null || seats == null || seats.isEmpty()) {
            return 0.0;
        }

        double basePrice = show.getPrice();
        double total = 0.0;

        for (Seat seat : seats) {
            // Each seat type has its own price multiplier
            total += basePrice * seat.getPriceMultiplier();
        }

        this.totalAmount = total;
        return total;
    }

    /**
     * INFORMATION EXPERT: Get booking details summary
     * Booking knows all its data - customer, show, seats, amount, time
     * So Booking should format its own details!
     */
    public String getBookingDetails() {
        StringBuilder details = new StringBuilder();
        details.append("========== BOOKING DETAILS ==========\n");
        details.append("Booking ID: ").append(id).append("\n");
        details.append("Customer: ").append(customer != null ? customer.getName() : "N/A").append("\n");
        details.append("Movie: ").append(show != null && show.getMovie() != null ?
                       show.getMovie().getTitle() : "N/A").append("\n");
        details.append("Show Time: ").append(show != null ? show.getFormattedDate() + " " +
                       show.getFormattedTime() : "N/A").append("\n");
        details.append("Theatre: ").append(show != null && show.getScreen() != null &&
                       show.getScreen().getTheatre() != null ?
                       show.getScreen().getTheatre().getName() : "N/A").append("\n");
        details.append("Screen: ").append(show != null && show.getScreen() != null ?
                       show.getScreen().getName() : "N/A").append("\n");
        details.append("Seats: ").append(getSeatLabels()).append("\n");
        details.append("Total Amount: Rs. ").append(String.format("%.2f", totalAmount)).append("\n");
        details.append("Status: ").append(status).append("\n");
        details.append("Booked On: ").append(getFormattedBookingTime()).append("\n");
        details.append("=====================================");
        return details.toString();
    }

    /**
     * INFORMATION EXPERT: Get seat count
     * Booking knows its own seats!
     */
    public int getSeatCount() {
        return seats != null ? seats.size() : 0;
    }

    /**
     * INFORMATION EXPERT: Check if booking is confirmed
     * Booking knows its own status!
     */
    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    /**
     * INFORMATION EXPERT: Check if booking is pending
     * Booking knows its own status!
     */
    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }

    /**
     * INFORMATION EXPERT: Check if booking is cancelled
     */
    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    /**
     * INFORMATION EXPERT: Get formatted seat labels
     * Booking knows its seats and how to format them!
     */
    public String getSeatLabels() {
        if (seats == null || seats.isEmpty()) {
            return "None";
        }
        return seats.stream()
                    .map(Seat::getSeatLabel)
                    .collect(Collectors.joining(", "));
    }

    /**
     * INFORMATION EXPERT: Get formatted booking time
     */
    public String getFormattedBookingTime() {
        if (bookingTime == null) return "N/A";
        return bookingTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    /**
     * INFORMATION EXPERT: Confirm the booking
     * Booking can change its own status!
     */
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * INFORMATION EXPERT: Cancel the booking
     */
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    /**
     * INFORMATION EXPERT: Add a seat to booking
     * Recalculates total automatically
     */
    public void addSeat(Seat seat) {
        this.seats.add(seat);
        calculateTotalAmount();
    }

    /**
     * INFORMATION EXPERT: Check if a specific seat is in this booking
     */
    public boolean hasSeat(Long seatId) {
        return seats.stream().anyMatch(s -> s.getId().equals(seatId));
    }

    // ========================================================================
    // STANDARD GETTERS AND SETTERS
    // ========================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Show getShow() { return show; }
    public void setShow(Show show) { this.show = show; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
        calculateTotalAmount();
    }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customer=" + (customer != null ? customer.getName() : "null") +
                ", show=" + (show != null ? show.getId() : "null") +
                ", seats=" + getSeatCount() +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}
