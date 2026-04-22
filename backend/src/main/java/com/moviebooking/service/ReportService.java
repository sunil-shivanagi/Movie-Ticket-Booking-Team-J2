package com.moviebooking.service;

import com.moviebooking.model.*;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * ReportService - GRASP: PURE FABRICATION
 *
 * This is a PURE FABRICATION because:
 * 1. "Report" is NOT a domain entity in our movie booking system
 * 2. It doesn't represent a real-world concept like Movie, Booking, or Customer
 * 3. It was FABRICATED purely for:
 *    - Separating reporting concerns from other services
 *    - Achieving high cohesion in other classes
 *    - Better code organization
 *
 * Without Pure Fabrication, this logic would be scattered across
 * Admin, BookingService, and other classes - low cohesion!
 */
@Service
public class ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    @Autowired
    public ReportService(BookingRepository bookingRepository,
                         PaymentRepository paymentRepository,
                         MovieRepository movieRepository,
                         ShowRepository showRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    // ========== DAILY BOOKING REPORT ==========

    /**
     * Get daily booking report
     * Pure Fabrication: This report aggregation doesn't belong to any domain entity
     */
    public Map<String, Object> getDailyBookingReport(LocalDate date) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportTitle", "Daily Booking Report");
        report.put("reportDate", date.toString());

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findConfirmedBookingsBetween(startOfDay, endOfDay);

        report.put("totalBookings", bookings.size());

        double totalRevenue = bookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        report.put("totalRevenue", totalRevenue);

        int totalTickets = bookings.stream()
                .mapToInt(Booking::getSeatCount)
                .sum();
        report.put("totalTicketsSold", totalTickets);

        return report;
    }

    // ========== MOVIE WISE REVENUE REPORT ==========

    /**
     * Get revenue report by movie
     * Pure Fabrication: Cross-cutting concern across movies and bookings
     */
    public List<Map<String, Object>> getMovieWiseRevenueReport() {
        List<Map<String, Object>> report = new ArrayList<>();

        List<Movie> movies = movieRepository.findAll();
        for (Movie movie : movies) {
            Map<String, Object> movieReport = new LinkedHashMap<>();
            movieReport.put("movieId", movie.getId());
            movieReport.put("movieTitle", movie.getTitle());

            Long bookingCount = bookingRepository.countConfirmedBookingsByMovie(movie.getId());
            movieReport.put("totalBookings", bookingCount != null ? bookingCount : 0);

            Double revenue = bookingRepository.getTotalRevenueByMovie(movie.getId());
            movieReport.put("totalRevenue", revenue != null ? revenue : 0.0);

            report.add(movieReport);
        }

        // Sort by revenue descending
        report.sort((a, b) -> Double.compare(
                (Double) b.get("totalRevenue"),
                (Double) a.get("totalRevenue")));

        return report;
    }

    // ========== OCCUPANCY REPORT ==========

    /**
     * Get theatre occupancy report
     * Pure Fabrication: Complex calculation across multiple entities
     */
    public Map<String, Object> getOccupancyReport(LocalDate date) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportTitle", "Occupancy Report");
        report.put("reportDate", date.toString());

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Show> shows = showRepository.findShowsBetween(startOfDay, endOfDay);

        int totalShows = shows.size();
        report.put("totalShows", totalShows);

        int totalSeatsAvailable = 0;
        int totalSeatsBooked = 0;

        for (Show show : shows) {
            if (show.getScreen() != null) {
                totalSeatsAvailable += show.getScreen().getTotalSeats();
            }
            List<Seat> bookedSeats = bookingRepository.findBookedSeatsByShow(show.getId());
            totalSeatsBooked += bookedSeats.size();
        }

        report.put("totalSeatsAvailable", totalSeatsAvailable);
        report.put("totalSeatsBooked", totalSeatsBooked);

        double occupancyRate = totalSeatsAvailable > 0
                ? (double) totalSeatsBooked / totalSeatsAvailable * 100
                : 0;
        report.put("occupancyRate", String.format("%.2f%%", occupancyRate));

        return report;
    }

    // ========== PAYMENT METHOD STATISTICS ==========

    /**
     * Get payment method statistics
     * Pure Fabrication: Analytics across payment data
     */
    public List<Map<String, Object>> getPaymentMethodStatistics() {
        List<Map<String, Object>> stats = new ArrayList<>();

        List<Object[]> rawStats = paymentRepository.getPaymentMethodStatistics();
        for (Object[] row : rawStats) {
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("paymentMethod", row[0]);
            stat.put("transactionCount", row[1]);
            stat.put("totalAmount", row[2]);
            stats.add(stat);
        }

        return stats;
    }

    // ========== SUMMARY DASHBOARD ==========

    /**
     * Get dashboard summary for admin
     * Pure Fabrication: Aggregates data from multiple sources
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("totalMovies", movieRepository.count());
        dashboard.put("totalShows", showRepository.count());
        dashboard.put("totalBookings", bookingRepository.count());

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        List<Booking> todayBookings = bookingRepository.findConfirmedBookingsBetween(startOfToday, endOfToday);
        dashboard.put("todayBookings", todayBookings.size());

        Double todayRevenue = todayBookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        dashboard.put("todayRevenue", todayRevenue);

        return dashboard;
    }
}
