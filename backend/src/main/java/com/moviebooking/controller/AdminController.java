package com.moviebooking.controller;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AdminController - Handles all admin operations
 *
 * Works with High Cohesion services:
 * - MovieService for movie management
 * - ShowService for show scheduling
 * - ReportService for analytics (Pure Fabrication)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;
    private final UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowService showService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private TheatreService theatreService;

    @Autowired
    public AdminController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // ========================================================================
    // ADMIN DASHBOARD
    // ========================================================================

    /**
     * Admin dashboard - shows summary from ReportService (Pure Fabrication)
     * REQUIRES ADMIN AUTHENTICATION
     */
    @GetMapping("")
    public String dashboard(Model model, HttpSession session) {
        // Check admin authentication
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/login-page?error=Admin login required";
        }

        // Get dashboard summary from Pure Fabrication service
        Map<String, Object> summary = reportService.getDashboardSummary();

        model.addAttribute("summary", summary);
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("todayShows", showService.getTodayShows());
        model.addAttribute("recentBookings", bookingService.getAllBookings());

        return "admin";
    }

    // ========================================================================
    // MOVIE MANAGEMENT - Uses MovieService (High Cohesion)
    // ========================================================================

    /**
     * List all movies
     * REQUIRES ADMIN AUTHENTICATION
     */
    @GetMapping("/movies")
    public String listMovies(Model model, HttpSession session) {
        // Check admin authentication
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/login-page?error=Admin login required";
        }

        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "admin";
    }

    /**
     * Add new movie form
     */
    @GetMapping("/movies/add")
    public String addMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("action", "add");
        return "admin";
    }

    /**
     * Add new movie - delegates to MovieService (High Cohesion)
     */
    @PostMapping("/movies/add")
    public String addMovie(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam Integer duration,
                          @RequestParam String genre,
                          @RequestParam String language,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
                          @RequestParam(required = false) String posterUrl,
                          RedirectAttributes redirectAttributes) {
        try {
            // HIGH COHESION: MovieService handles ONLY movie operations
            movieService.addMovie(title, description, duration, genre, language, releaseDate, posterUrl);
            redirectAttributes.addFlashAttribute("success", "Movie added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Edit movie form
     */
    @GetMapping("/movies/{id}/edit")
    public String editMovieForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("action", "edit");
        return "admin";
    }

    /**
     * Update movie
     */
    @PostMapping("/movies/{id}/update")
    public String updateMovie(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam Integer duration,
                             @RequestParam String genre,
                             @RequestParam String language,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
                             @RequestParam(required = false) String posterUrl,
                             RedirectAttributes redirectAttributes) {
        try {
            movieService.updateMovie(id, title, description, duration, genre, language, releaseDate, posterUrl);
            redirectAttributes.addFlashAttribute("success", "Movie updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Delete movie
     */
    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Movie deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    // ========================================================================
    // SHOW MANAGEMENT - Uses ShowService (High Cohesion)
    // ========================================================================

    /**
     * List all shows
     */
    @GetMapping("/shows")
    public String listShows(Model model) {
        model.addAttribute("shows", showService.getAllShows());
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("theatres", theatreService.getAllTheatres());
        return "admin";
    }

    /**
     * Schedule new show - delegates to ShowService (High Cohesion)
     */
    @PostMapping("/shows/schedule")
    public String scheduleShow(@RequestParam Long movieId,
                              @RequestParam Long screenId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime showTime,
                              @RequestParam Double price,
                              RedirectAttributes redirectAttributes) {
        try {
            // HIGH COHESION: ShowService handles ONLY show operations
            showService.scheduleShow(movieId, screenId, showTime, price);
            redirectAttributes.addFlashAttribute("success", "Show scheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Cancel show
     */
    @PostMapping("/shows/{id}/cancel")
    public String cancelShow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            showService.cancelShow(id);
            redirectAttributes.addFlashAttribute("success", "Show cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    // ========================================================================
    // REPORTS - Uses ReportService (Pure Fabrication)
    // ========================================================================

    /**
     * Daily booking report
     * PURE FABRICATION: ReportService is not a domain entity but created for
     * separating report logic and achieving high cohesion
     */
    @GetMapping("/reports/daily")
    public String dailyReport(@RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             Model model) {
        if (date == null) {
            date = LocalDate.now();
        }

        // PURE FABRICATION: ReportService handles report generation
        Map<String, Object> report = reportService.getDailyBookingReport(date);
        model.addAttribute("report", report);
        model.addAttribute("reportType", "daily");
        model.addAttribute("selectedDate", date);

        return "admin";
    }

    /**
     * Movie-wise revenue report
     */
    @GetMapping("/reports/revenue")
    public String revenueReport(Model model) {
        // PURE FABRICATION: ReportService provides analytics
        List<Map<String, Object>> report = reportService.getMovieWiseRevenueReport();
        model.addAttribute("revenueReport", report);
        model.addAttribute("reportType", "revenue");

        return "admin";
    }

    /**
     * Occupancy report
     */
    @GetMapping("/reports/occupancy")
    public String occupancyReport(@RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {
        if (date == null) {
            date = LocalDate.now();
        }

        Map<String, Object> report = reportService.getOccupancyReport(date);
        model.addAttribute("occupancyReport", report);
        model.addAttribute("reportType", "occupancy");
        model.addAttribute("selectedDate", date);

        return "admin";
    }

    /**
     * Payment statistics
     */
    @GetMapping("/reports/payments")
    public String paymentStats(Model model) {
        List<Map<String, Object>> stats = reportService.getPaymentMethodStatistics();
        model.addAttribute("paymentStats", stats);
        model.addAttribute("reportType", "payments");

        return "admin";
    }

    // ========================================================================
    // BOOKING MANAGEMENT
    // ========================================================================

    /**
     * View all bookings
     */
    @GetMapping("/bookings")
    public String viewBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin";
    }

    /**
     * View booking details
     */
    @GetMapping("/bookings/{id}")
    public String viewBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingWithSeats(id);
        model.addAttribute("booking", booking);
        // INFORMATION EXPERT: Booking provides its own formatted details
        model.addAttribute("bookingDetails", booking.getBookingDetails());
        return "admin";
    }

    /**
     * Cancel booking
     */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}


