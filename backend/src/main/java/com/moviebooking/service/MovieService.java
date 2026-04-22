package com.moviebooking.service;

import com.moviebooking.model.Movie;
import com.moviebooking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * MovieService - GRASP: HIGH COHESION
 *
 * This service has a single, focused responsibility: Managing Movies.
 * Compare to the original Admin class that handled everything!
 *
 * All methods in this service are related to movies:
 * - CRUD operations for movies
 * - Movie search and filtering
 * - Movie validation
 */
@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // ========== CREATE ==========

    /**
     * Add a new movie to the system
     */
    public Movie addMovie(String title, String description, Integer duration,
                         String genre, String language, LocalDate releaseDate, String posterUrl) {
        Movie movie = new Movie(title, description, duration, genre, language, releaseDate, posterUrl);
        return movieRepository.save(movie);
    }

    /**
     * Add movie from Movie object
     */
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // ========== READ ==========

    /**
     * Get all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Get movie by ID
     */
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    /**
     * Get movies by genre
     */
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    /**
     * Get movies by language
     */
    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguage(language);
    }

    /**
     * Search movies by keyword
     */
    public List<Movie> searchMovies(String keyword) {
        return movieRepository.searchByKeyword(keyword);
    }

    /**
     * Get currently showing movies
     */
    public List<Movie> getCurrentlyShowingMovies() {
        return movieRepository.findCurrentlyShowing(LocalDate.now());
    }

    /**
     * Get upcoming movies
     */
    public List<Movie> getUpcomingMovies() {
        return movieRepository.findUpcoming(LocalDate.now());
    }

    /**
     * Get all genres
     */
    public List<String> getAllGenres() {
        return movieRepository.findAllGenres();
    }

    /**
     * Get all languages
     */
    public List<String> getAllLanguages() {
        return movieRepository.findAllLanguages();
    }

    // ========== UPDATE ==========

    /**
     * Update movie details
     */
    public Movie updateMovie(Long id, String title, String description, Integer duration,
                            String genre, String language, LocalDate releaseDate, String posterUrl) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found: " + id));

        if (title != null) movie.setTitle(title);
        if (description != null) movie.setDescription(description);
        if (duration != null) movie.setDuration(duration);
        if (genre != null) movie.setGenre(genre);
        if (language != null) movie.setLanguage(language);
        if (releaseDate != null) movie.setReleaseDate(releaseDate);
        if (posterUrl != null) movie.setPosterUrl(posterUrl);

        return movieRepository.save(movie);
    }

    // ========== DELETE ==========

    /**
     * Delete a movie
     */
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    /**
     * Check if movie exists
     */
    public boolean movieExists(Long id) {
        return movieRepository.existsById(id);
    }
}
