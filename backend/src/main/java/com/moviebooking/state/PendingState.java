package com.moviebooking.state;

/**
 * PENDING STATE - Initial state when booking is created
 */
public class PendingState implements BookingState {

    @Override
    public String getStateName() {
        return "PENDING";
    }

    @Override
    public BookingState confirm() throws IllegalStateException {
        return new ConfirmedState();
    }

    @Override
    public BookingState cancel() throws IllegalStateException {
        return new CancelledState();
    }

    @Override
    public BookingState expire() throws IllegalStateException {
        return new ExpiredState();
    }

    @Override
    public boolean canRefund() {
        return false;
    }

    @Override
    public boolean canModify() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Booking is pending. Awaiting payment confirmation.";
    }

    @Override
    public String toString() {
        return "PendingState";
    }
}
