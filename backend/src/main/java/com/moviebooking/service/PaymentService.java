package com.moviebooking.service;

import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// ============================================================================
// PAYMENT SERVICE - GRASP: POLYMORPHISM
// ============================================================================
//
// GRASP Principle: POLYMORPHISM
// Assigned to: Sunil (PES1UG23CS613)
//
// VIOLATION (Before):
// Payment class had conditional logic like:
//   if (paymentMethod == CREDIT_CARD) { ... }
//   else if (paymentMethod == DEBIT_CARD) { ... }
//   else if (paymentMethod == UPI) { ... }
//
// FIX (After):
// - Created PaymentStrategy INTERFACE
// - Created separate implementations for each payment type
// - Each implementation handles its own payment processing
// - No conditional logic needed - polymorphism handles it!
//
// Benefits:
// - Adding new payment method = Add new class, no existing code changes
// - Each payment type handles its own validation and processing
// - Open/Closed Principle - open for extension, closed for modification
// ============================================================================

/**
 * PaymentResult - holds the result of a payment attempt
 */
class PaymentResult {
    private boolean success;
    private String transactionId;
    private String message;
    private String errorCode;

    public PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    public static PaymentResult success(String transactionId, String message) {
        return new PaymentResult(true, transactionId, message);
    }

    public static PaymentResult failure(String errorCode, String message) {
        PaymentResult result = new PaymentResult(false, null, message);
        result.errorCode = errorCode;
        return result;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
}

// ============================================================================
// PAYMENT STRATEGY INTERFACE - The base contract for all payment types
// ============================================================================

/**
 * PaymentStrategy Interface - GRASP POLYMORPHISM
 *
 * All payment methods implement this interface.
 * The behavior varies based on the concrete type - no conditionals needed!
 */
interface PaymentStrategy {

    /**
     * Process the payment - each implementation handles it differently
     * @param amount The amount to charge
     * @param paymentDetails Map containing payment-specific details
     * @return PaymentResult indicating success or failure
     */
    PaymentResult processPayment(Double amount, Map<String, String> paymentDetails);

    /**
     * Validate payment details before processing
     */
    boolean validateDetails(Map<String, String> paymentDetails);

    /**
     * Get the display name for this payment method
     */
    String getPaymentMethodName();

    /**
     * Get the payment method enum
     */
    PaymentMethod getPaymentMethod();
}

// ============================================================================
// CONCRETE PAYMENT STRATEGY IMPLEMENTATIONS
// Each class handles its own payment type - POLYMORPHISM in action!
// ============================================================================

/**
 * Credit Card Payment Implementation
 */
@Component
class CreditCardPayment implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(Double amount, Map<String, String> paymentDetails) {
        // Validate card details
        if (!validateDetails(paymentDetails)) {
            return PaymentResult.failure("INVALID_CARD", "Invalid credit card details");
        }

        String cardNumber = paymentDetails.get("cardNumber");
        String maskedCard = maskCardNumber(cardNumber);

        // Simulate credit card payment processing
        // In real application, this would call a payment gateway API
        System.out.println("[CreditCardPayment] Processing Rs. " + amount + " on card " + maskedCard);

        // Simulate successful payment (in production, call actual payment gateway)
        String txnId = "CC" + System.currentTimeMillis();
        return PaymentResult.success(txnId, "Credit card payment successful. Card: " + maskedCard);
    }

    @Override
    public boolean validateDetails(Map<String, String> paymentDetails) {
        String cardNumber = paymentDetails.get("cardNumber");
        String cvv = paymentDetails.get("cvv");
        String expiry = paymentDetails.get("expiry");

        // Basic validation
        return cardNumber != null && cardNumber.length() >= 12 &&
               cvv != null && cvv.length() == 3 &&
               expiry != null && expiry.length() >= 4;
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}

/**
 * Debit Card Payment Implementation
 */
@Component
class DebitCardPayment implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(Double amount, Map<String, String> paymentDetails) {
        if (!validateDetails(paymentDetails)) {
            return PaymentResult.failure("INVALID_CARD", "Invalid debit card details");
        }

        String cardNumber = paymentDetails.get("cardNumber");
        String maskedCard = maskCardNumber(cardNumber);

        // Simulate debit card payment
        System.out.println("[DebitCardPayment] Processing Rs. " + amount + " on card " + maskedCard);

        String txnId = "DC" + System.currentTimeMillis();
        return PaymentResult.success(txnId, "Debit card payment successful. Card: " + maskedCard);
    }

    @Override
    public boolean validateDetails(Map<String, String> paymentDetails) {
        String cardNumber = paymentDetails.get("cardNumber");
        String pin = paymentDetails.get("pin");

        return cardNumber != null && cardNumber.length() >= 12 &&
               pin != null && pin.length() >= 4;
    }

    @Override
    public String getPaymentMethodName() {
        return "Debit Card";
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.DEBIT_CARD;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}

/**
 * UPI Payment Implementation
 */
@Component
class UPIPayment implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(Double amount, Map<String, String> paymentDetails) {
        if (!validateDetails(paymentDetails)) {
            return PaymentResult.failure("INVALID_UPI", "Invalid UPI ID");
        }

        String upiId = paymentDetails.get("upiId");

        // Simulate UPI payment
        System.out.println("[UPIPayment] Processing Rs. " + amount + " via UPI: " + upiId);

        String txnId = "UPI" + System.currentTimeMillis();
        return PaymentResult.success(txnId, "UPI payment successful. UPI ID: " + upiId);
    }

    @Override
    public boolean validateDetails(Map<String, String> paymentDetails) {
        String upiId = paymentDetails.get("upiId");
        // UPI ID format: username@bankname
        return upiId != null && upiId.contains("@") && upiId.length() >= 5;
    }

    @Override
    public String getPaymentMethodName() {
        return "UPI";
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }
}

/**
 * Net Banking Payment Implementation
 */
@Component
class NetBankingPayment implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(Double amount, Map<String, String> paymentDetails) {
        if (!validateDetails(paymentDetails)) {
            return PaymentResult.failure("INVALID_BANK", "Invalid bank details");
        }

        String bankCode = paymentDetails.get("bankCode");
        String accountNumber = paymentDetails.get("accountNumber");
        String maskedAccount = maskAccountNumber(accountNumber);

        // Simulate net banking payment
        System.out.println("[NetBankingPayment] Processing Rs. " + amount +
                          " via bank " + bankCode + " account " + maskedAccount);

        String txnId = "NB" + System.currentTimeMillis();
        return PaymentResult.success(txnId, "Net banking payment successful. Bank: " + bankCode);
    }

    @Override
    public boolean validateDetails(Map<String, String> paymentDetails) {
        String bankCode = paymentDetails.get("bankCode");
        String accountNumber = paymentDetails.get("accountNumber");

        return bankCode != null && !bankCode.isEmpty() &&
               accountNumber != null && accountNumber.length() >= 8;
    }

    @Override
    public String getPaymentMethodName() {
        return "Net Banking";
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NET_BANKING;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "******" + accountNumber.substring(accountNumber.length() - 4);
    }
}

// ============================================================================
// PAYMENT SERVICE - Uses Strategy Pattern (POLYMORPHISM)
// ============================================================================

/**
 * PaymentService - Orchestrates payment processing using POLYMORPHISM
 *
 * This service:
 * 1. Maintains a map of all PaymentStrategy implementations (injected by Spring)
 * 2. Selects the appropriate strategy based on payment method
 * 3. Delegates processing to the strategy - NO CONDITIONALS!
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    // Map of payment method to strategy - populated by Spring
    private final Map<PaymentMethod, PaymentStrategy> strategies;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          CreditCardPayment creditCardPayment,
                          DebitCardPayment debitCardPayment,
                          UPIPayment upiPayment,
                          NetBankingPayment netBankingPayment) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;

        // Initialize strategy map
        this.strategies = new HashMap<>();
        strategies.put(PaymentMethod.CREDIT_CARD, creditCardPayment);
        strategies.put(PaymentMethod.DEBIT_CARD, debitCardPayment);
        strategies.put(PaymentMethod.UPI, upiPayment);
        strategies.put(PaymentMethod.NET_BANKING, netBankingPayment);
    }

    /**
     * Process payment for a booking - POLYMORPHISM in action!
     *
     * Notice: NO if-else or switch statements for payment types!
     * The correct behavior is determined by the PaymentStrategy implementation.
     */
    public Payment processPayment(Long bookingId, PaymentMethod method, Map<String, String> paymentDetails) {
        // Get the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // Get the payment amount from booking (Information Expert!)
        Double amount = booking.getTotalAmount();

        // POLYMORPHISM: Select strategy based on payment method
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new RuntimeException("Unsupported payment method: " + method);
        }

        // Create payment record
        Payment payment = new Payment(booking, amount, method);

        // POLYMORPHISM: Delegate to the strategy - each type handles it differently!
        PaymentResult result = strategy.processPayment(amount, paymentDetails);

        // Update payment based on result
        if (result.isSuccess()) {
            payment.setTransactionId(result.getTransactionId());
            payment.markSuccess();
            booking.confirm();
        } else {
            payment.markFailed();
        }

        payment.setPaymentDetails(result.getMessage());

        // Save and return
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    /**
     * Get all available payment methods
     */
    public Map<PaymentMethod, String> getAvailablePaymentMethods() {
        Map<PaymentMethod, String> methods = new HashMap<>();
        for (Map.Entry<PaymentMethod, PaymentStrategy> entry : strategies.entrySet()) {
            methods.put(entry.getKey(), entry.getValue().getPaymentMethodName());
        }
        return methods;
    }

    /**
     * Validate payment details before processing
     */
    public boolean validatePaymentDetails(PaymentMethod method, Map<String, String> details) {
        PaymentStrategy strategy = strategies.get(method);
        return strategy != null && strategy.validateDetails(details);
    }

    /**
     * Get payment by booking ID
     */
    public Payment getPaymentByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElse(null);
    }

    /**
     * Get payment by transaction ID
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId).orElse(null);
    }
}
