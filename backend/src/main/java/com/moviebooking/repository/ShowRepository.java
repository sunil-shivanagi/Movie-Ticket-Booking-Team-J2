package com.moviebooking.repository;

import com.moviebooking.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Show entity
 */
@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long movieId);

    List<Show> findByScreenId(Long screenId);

    @Query("SELECT DISTINCT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.theatre WHERE s.movie.id = :movieId AND s.showTime > :now ORDER BY s.showTime")
    List<Show> findUpcomingShowsByMovie(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.theatre WHERE s.showTime BETWEEN :start AND :end ORDER BY s.showTime")
    List<Show> findShowsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.theatre WHERE s.id = :id")
    Optional<Show> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.theatre ORDER BY s.showTime DESC")
    List<Show> findAllWithDetails();

    @Query("SELECT DISTINCT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.theatre WHERE s.screen.theatre.id = :theatreId AND s.showTime > :now ORDER BY s.showTime")
    List<Show> findUpcomingShowsByTheatre(@Param("theatreId") Long theatreId, @Param("now") LocalDateTime now);
}
