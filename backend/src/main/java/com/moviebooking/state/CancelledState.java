package com.moviebooking.state;

/**
 * CANCELLED STATE - Terminal state, booking is cancelled
 */
public class CancelledState implements BookingState {

    @Override
    public String getStateName() {
        return "CANCELLED";
    }

    @Override
    public BookingState confirm() throws IllegalStateException {
        throw new IllegalStateException(
            "Cannot confirm a cancelled booking. Booking is terminated."
        );
    }

    @Override
    public BookingState cancel() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking is already cancelled. Cannot cancel again."
        );
    }

    @Override
    public BookingState expire() throws IllegalStateException {
        throw new IllegalStateException(
            "Booking is already cancelled. Cannot expire."
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
        return "Booking is cancelled. No further actions allowed.";
    }

    @Override
    public String toString() {
        return "CancelledState";
    }
}
