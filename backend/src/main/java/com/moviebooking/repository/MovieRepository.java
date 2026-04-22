package com.moviebooking.repository;

import com.moviebooking.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Movie entity
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);

    List<Movie> findByLanguage(String language);

    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:keyword% OR m.description LIKE %:keyword%")
    List<Movie> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= :date ORDER BY m.releaseDate DESC")
    List<Movie> findCurrentlyShowing(@Param("date") LocalDate date);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate > :date ORDER BY m.releaseDate ASC")
    List<Movie> findUpcoming(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.genre IS NOT NULL")
    List<String> findAllGenres();

    @Query("SELECT DISTINCT m.language FROM Movie m WHERE m.language IS NOT NULL")
    List<String> findAllLanguages();
}
