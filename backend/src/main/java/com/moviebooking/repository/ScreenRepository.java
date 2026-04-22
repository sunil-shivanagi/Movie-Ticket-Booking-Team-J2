package com.moviebooking.repository;

import com.moviebooking.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Screen entity
 */
@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByTheatreId(Long theatreId);

    @Query("SELECT s FROM Screen s LEFT JOIN FETCH s.seats WHERE s.id = :id")
    Optional<Screen> findByIdWithSeats(@Param("id") Long id);
}
