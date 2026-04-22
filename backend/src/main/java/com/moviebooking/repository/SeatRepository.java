package com.moviebooking.repository;

import com.moviebooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Seat entity
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScreenId(Long screenId);

    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId ORDER BY s.rowName, s.seatNumber")
    List<Seat> findByScreenIdOrdered(@Param("screenId") Long screenId);

    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds")
    List<Seat> findByIds(@Param("seatIds") List<Long> seatIds);
}
