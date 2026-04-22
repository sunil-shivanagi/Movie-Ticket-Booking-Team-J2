package com.moviebooking.state;

/**
 * CONFIRMED STATE - Booking is confirmed and paid
 */
public class ConfirmedState implements BookingState {

    @Override
    public String getStateName() {
        return "CONFIRMED";
    }

    @Override
    public BookingState confirm() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking is already confirmed. Cannot confirm again."
        );
    }

    @Override
    public BookingState cancel() throws IllegalStateException {
        return new CancelledState();
    }

    @Override
    public BookingState expire() throws IllegalStateException {
        throw new IllegalStateException(
            "Confirmed booking cannot expire. Only pending bookings expire."
        );
    }

    @Override
    public boolean canRefund() {
        return true;
    }

    @Override
    public boolean canModify() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Booking is confirmed. Tickets are booked and payment completed.";
    }

    @Override
    public String toString() {
        return "ConfirmedState";
    }
}
