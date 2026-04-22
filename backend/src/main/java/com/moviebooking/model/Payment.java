package com.moviebooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Payment entity for storing payment transaction details.
 * The actual payment processing is handled by PaymentStrategy implementations
 * (GRASP: Polymorphism) in PaymentService.java
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_id")
    private String transactionId;

    // Card/UPI details (masked for security)
    @Column(name = "payment_details")
    private String paymentDetails;

    // ========================================================================
    // CONSTRUCTORS
    // ========================================================================

    public Payment() {
        this.paymentTime = LocalDateTime.now();
        this.transactionId = generateTransactionId();
    }

    public Payment(Booking booking, Double amount, PaymentMethod paymentMethod) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
        this.transactionId = generateTransactionId();
    }

    // ========================================================================
    // BUSINESS METHODS
    // ========================================================================

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() +
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Mark payment as successful
     */
    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
        // Also confirm the booking when payment succeeds
        if (booking != null) {
            booking.confirm();
        }
    }

    /**
     * Mark payment as failed
     */
    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    /**
     * Process refund
     */
    public void refund() {
        this.status = PaymentStatus.REFUNDED;
        if (booking != null) {
            booking.cancel();
        }
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCESS;
    }

    /**
     * Get formatted payment time
     */
    public String getFormattedPaymentTime() {
        if (paymentTime == null) return "N/A";
        return paymentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    /**
     * Get payment method display name
     */
    public String getPaymentMethodDisplayName() {
        if (paymentMethod == null) return "N/A";
        switch (paymentMethod) {
            case CREDIT_CARD: return "Credit Card";
            case DEBIT_CARD: return "Debit Card";
            case UPI: return "UPI";
            case NET_BANKING: return "Net Banking";
            default: return paymentMethod.toString();
        }
    }

    /**
     * Get payment receipt details
     */
    public String getReceiptDetails() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("========== PAYMENT RECEIPT ==========\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Amount: Rs. ").append(String.format("%.2f", amount)).append("\n");
        receipt.append("Payment Method: ").append(getPaymentMethodDisplayName()).append("\n");
        receipt.append("Status: ").append(status).append("\n");
        receipt.append("Date: ").append(getFormattedPaymentTime()).append("\n");
        if (booking != null) {
            receipt.append("Booking ID: ").append(booking.getId()).append("\n");
        }
        receipt.append("=====================================");
        return receipt.toString();
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
