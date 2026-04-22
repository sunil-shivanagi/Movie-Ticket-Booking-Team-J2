package com.moviebooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer entity - can browse movies, book tickets, make payments.
 * Note: Booking logic moved to BookingService (GRASP: Information Expert fix)
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public Customer(String name, String email, String password, String phone) {
        super(name, email, password, phone);
    }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setCustomer(this);
    }
}
