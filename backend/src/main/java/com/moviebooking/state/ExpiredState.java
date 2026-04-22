package com.moviebooking.state;

/**
 * EXPIRED STATE - Terminal state, booking expired due to timeout
 */
public class ExpiredState implements BookingState {

    @Override
    public String getStateName() {
        return "EXPIRED";
    }

    @Override
    public BookingState confirm() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking has expired. Please create a new booking."
        );
    }

    @Override
    public BookingState cancel() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking has already expired. Cannot cancel."
        );
    }

    @Override
    public BookingState expire() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking is already expired. No further transitions allowed."
        );
    }

    @Override
    public boolean canRefund() {
        return false;
    }

    @Override
    public boolean canModify() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Booking has expired. Payment was not completed in time.";
    }

    @Override
    public String toString() {
        return "ExpiredState";
    }
}
