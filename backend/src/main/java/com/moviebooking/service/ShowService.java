package com.moviebooking.service;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Screen;
import com.moviebooking.model.Show;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ScreenRepository;
import com.moviebooking.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ShowService - GRASP: HIGH COHESION
 *
 * This service has a single, focused responsibility: Managing Shows/Screenings.
 * All methods are related to scheduling and managing movie shows.
 */
@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    @Autowired
    public ShowService(ShowRepository showRepository,
                       MovieRepository movieRepository,
                       ScreenRepository screenRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
    }

    // ========== SCHEDULE SHOWS ==========

    /**
     * Schedule a new show
     */
    public Show scheduleShow(Long movieId, Long screenId, LocalDateTime showTime, Double price) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found: " + movieId));

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found: " + screenId));

        Show show = new Show(movie, screen, showTime, price);
        return showRepository.save(show);
    }

    /**
     * Schedule multiple shows for a movie
     */
    public List<Show> scheduleMultipleShows(Long movieId, Long screenId,
                                            List<LocalDateTime> showTimes, Double price) {
        List<Show> shows = new ArrayList<>();
        for (LocalDateTime time : showTimes) {
            shows.add(scheduleShow(movieId, screenId, time, price));
        }
        return shows;
    }

    // ========== GET SHOWS ==========

    /**
     * Get all shows with eager loading to prevent LazyInitializationException
     */
    public List<Show> getAllShows() {
        return showRepository.findAllWithDetails();
    }

    /**
     * Get show by ID with all details
     */
    public Show getShowById(Long id) {
        return showRepository.findByIdWithDetails(id).orElse(null);
    }

    /**
     * Get upcoming shows for a movie
     */
    public List<Show> getUpcomingShowsForMovie(Long movieId) {
        return showRepository.findUpcomingShowsByMovie(movieId, LocalDateTime.now());
    }

    /**
     * Get shows for a theatre
     */
    public List<Show> getShowsForTheatre(Long theatreId) {
        return showRepository.findUpcomingShowsByTheatre(theatreId, LocalDateTime.now());
    }

    /**
     * Get shows for today
     */
    public List<Show> getTodayShows() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return showRepository.findShowsBetween(startOfDay, endOfDay);
    }

    /**
     * Get shows between dates
     */
    public List<Show> getShowsBetween(LocalDateTime start, LocalDateTime end) {
        return showRepository.findShowsBetween(start, end);
    }

    // ========== UPDATE SHOWS ==========

    /**
     * Update show time
     */
    public Show updateShowTime(Long showId, LocalDateTime newTime) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));
        show.setShowTime(newTime);
        return showRepository.save(show);
    }

    /**
     * Update show price
     */
    public Show updateShowPrice(Long showId, Double newPrice) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));
        show.setPrice(newPrice);
        return showRepository.save(show);
    }

    // ========== CANCEL SHOWS ==========

    /**
     * Cancel/Delete a show
     */
    public void cancelShow(Long showId) {
        showRepository.deleteById(showId);
    }

    /**
     * Check if show exists
     */
    public boolean showExists(Long id) {
        return showRepository.existsById(id);
    }
}
