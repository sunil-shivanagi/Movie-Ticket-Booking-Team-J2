package com.moviebooking.repository;

import com.moviebooking.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Theatre entity
 */
@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    List<Theatre> findByCity(String city);

    @Query("SELECT DISTINCT t.city FROM Theatre t")
    List<String> findAllCities();

    @Query("SELECT t FROM Theatre t LEFT JOIN FETCH t.screens WHERE t.id = :id")
    Optional<Theatre> findByIdWithScreens(@Param("id") Long id);
}
