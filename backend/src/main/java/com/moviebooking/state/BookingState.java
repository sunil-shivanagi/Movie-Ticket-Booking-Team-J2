package com.moviebooking.state;

/**
 * ============================================================================
 * BEHAVIORAL PATTERN: STATE PATTERN
 * ============================================================================
 *
 * Pattern Description:
 * State Pattern allows an object to alter its behavior when its internal state
 * changes. The object appears to change its class. Instead of using conditionals
 * to check state and execute state-specific logic, each state is a separate class
 * that knows what valid transitions are allowed.
 *
 * Problem Solved:
 * - Prevents invalid state transitions (e.g., CONFIRMED → PENDING not allowed)
 * - Encapsulates state-specific behavior in separate classes
 * - Makes state transitions clear and maintainable
 * - Easy to add new states without modifying existing code
 *
 * Implementation:
 * - BookingState interface defines state contract
 * - Concrete states: PendingState, ConfirmedState, CancelledState, ExpiredState
 * - Each state knows valid transitions and actions
 * - Booking class delegates to current state
 *
 * Benefits:
 * ✓ Eliminates long conditional statements
 * ✓ Encapsulates state-specific behavior
 * ✓ Makes state transitions explicit
 * ✓ Easy to add new states
 * ✓ Prevents invalid state transitions
 *
 * State Diagram:
 * PENDING → CONFIRMED (payment successful)
 * PENDING → CANCELLED (user cancels)
 * PENDING → EXPIRED (timeout)
 * CONFIRMED → CANCELLED (refund)
 * CANCELLED → (terminal state, no transitions)
 * EXPIRED → (terminal state, no transitions)
 *
 * Current Implementation (before State Pattern):
 * ```
 * public enum BookingStatus {
 *     PENDING, CONFIRMED, CANCELLED, EXPIRED
 * }
 * ```
 * Problem: No validation of transitions, logic scattered across multiple classes
 *
 * Improved Implementation (with State Pattern):
 * - BookingState interface with transition methods
 * - Each state validates its own transitions
 * - Booking delegates state changes to current state
 * ============================================================================
 */

/**
 * BookingState Interface - Defines the contract for all booking states
 *
 * Each concrete state implements these methods to define:
 * - What transitions are valid from this state
 * - What actions can be performed in this state
 * - What the next state should be
 */
public interface BookingState {

    /**
     * Get the name of this state
     * @return State name (PENDING, CONFIRMED, etc.)
     */
    String getStateName();

    /**
     * Confirm the booking (transition to CONFIRMED)
     * Some states allow this, others throw an exception
     *
     * @return New state after confirmation
     * @throws IllegalStateException if transition not allowed from this state
     */
    BookingState confirm() throws IllegalStateException;

    /**
     * Cancel the booking (transition to CANCELLED)
     * Most states allow this, but maybe CONFIRMED with complex rules
     *
     * @return New state after cancellation
     * @throws IllegalStateException if transition not allowed
     */
    BookingState cancel() throws IllegalStateException;

    /**
     * Mark booking as expired (transition to EXPIRED)
     * Only PENDING bookings can expire
     *
     * @return New state after expiration
     * @throws IllegalStateException if transition not allowed
     */
    BookingState expire() throws IllegalStateException;

    /**
     * Check if booking can be refunded in current state
     * @return true if refund is allowed
     */
    boolean canRefund();

    /**
     * Check if booking can be modified in current state
     * @return true if modifications are allowed
     */
    boolean canModify();

    /**
     * Get description of current state for UI/logging
     * @return User-friendly state description
     */
    String getDescription();
}
