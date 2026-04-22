package com.moviebooking.repository;

import com.moviebooking.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.bookings WHERE c.id = :id")
    Optional<Customer> findByIdWithBookings(@Param("id") Long id);
}
