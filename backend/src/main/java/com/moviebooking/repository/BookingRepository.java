package com.moviebooking.repository;

import com.moviebooking.model.Booking;
import com.moviebooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByShowId(Long showId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' AND b.show.id = :showId")
    List<Booking> findConfirmedBookingsByShow(@Param("showId") Long showId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.seats WHERE b.id = :id")
    Optional<Booking> findByIdWithSeats(@Param("id") Long id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.show s JOIN FETCH s.movie WHERE b.customer.id = :customerId ORDER BY b.bookingTime DESC")
    List<Booking> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    // For reports
    @Query("SELECT b FROM Booking b WHERE b.bookingTime BETWEEN :start AND :end AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED' AND b.show.movie.id = :movieId")
    Long countConfirmedBookingsByMovie(@Param("movieId") Long movieId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'CONFIRMED' AND b.show.movie.id = :movieId")
    Double getTotalRevenueByMovie(@Param("movieId") Long movieId);

    // Get booked seats for a specific show
    @Query("SELECT seat FROM Booking b JOIN b.seats seat WHERE b.show.id = :showId AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Seat> findBookedSeatsByShow(@Param("showId") Long showId);
}
