package com.moviebooking.repository;

import com.moviebooking.model.Payment;
import com.moviebooking.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.paymentTime BETWEEN :start AND :end")
    List<Payment> findPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentTime BETWEEN :start AND :end")
    Double getTotalRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();
}
