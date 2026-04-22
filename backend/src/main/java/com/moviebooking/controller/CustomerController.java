package com.moviebooking.controller;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.*;

// ============================================================================
// CUSTOMER CONTROLLER - GRASP: CONTROLLER PATTERN
// ============================================================================
//
// GRASP Principle: CONTROLLER
// Assigned to: Vikas (PES1UG24CS837)
//
// VIOLATION (Before):
// No proper controller existed. The Customer/Admin domain classes were
// handling both domain logic AND UI interaction - mixed responsibilities!
//
// FIX (After):
// Created CustomerController as a GRASP CONTROLLER that:
// 1. Receives system events (HTTP requests) from the UI
// 2. Coordinates with domain objects and services
// 3. Does NOT contain business logic (that's in services/entities)
// 4. Acts as a FACADE between UI and domain layer
//
// Each method handles ONE USE CASE:
// - home() -> UC: View Home
// - listMovies() -> UC: Browse Movies
// - viewShows() -> UC: View Shows
// - selectSeats() -> UC: Select Seats
// - createBooking() -> UC: Create Booking
// - payment() -> UC: Make Payment
// - confirmation() -> UC: View Confirmation
// ============================================================================

/**
 * CustomerController - GRASP CONTROLLER
 *
 * This controller handles all customer-facing use cases.
 * It acts as a facade between the UI (Thymeleaf templates) and the domain layer.
 *
 * Key GRASP Controller characteristics:
 * 1. Handles system events (HTTP requests)
 * 2. Coordinates domain objects (doesn't do the work itself)
 * 3. Thin controller - delegates to services
 * 4. One controller per related group of use cases
 */
@Controller
public class CustomerController {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final UserService userService;

    // These are package-level services, need to inject via constructor
    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowService showService;

    @Autowired
    public CustomerController(BookingService bookingService,
                             PaymentService paymentService,
                             UserService userService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    // ========================================================================
    // USE CASE: View Home (Movies)
    // System Event: User opens the application (after authentication)
    // ========================================================================

    /**
     * GRASP Controller: Handles home page request
     * Coordinates with MovieService to get movie data
     * REQUIRES AUTHENTICATION
     */
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Check if authenticated
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/login-page?error=Please login first";
        }

        // Controller coordinates - doesn't compute
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("languages", movieService.getAllLanguages());
        return "index";
    }

    // ========================================================================
    // USE CASE: Browse Movies
    // System Event: User wants to see available movies
    // ========================================================================

    /**
     * GRASP Controller: Handles movie listing request
     */
    @GetMapping("/movies")
    public String listMovies(@RequestParam(required = false) String genre,
                            @RequestParam(required = false) String language,
                            @RequestParam(required = false) String search,
                            Model model) {
        List<Movie> movies;

        // Controller coordinates filtering - actual logic in service
        if (search != null && !search.isEmpty()) {
            movies = movieService.searchMovies(search);
        } else if (genre != null && !genre.isEmpty()) {
            movies = movieService.getMoviesByGenre(genre);
        } else if (language != null && !language.isEmpty()) {
            movies = movieService.getMoviesByLanguage(language);
        } else {
            movies = movieService.getAllMovies();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("languages", movieService.getAllLanguages());
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedLanguage", language);
        model.addAttribute("searchQuery", search);

        return "movies";
    }

    // ========================================================================
    // USE CASE: View Shows for a Movie
    // System Event: User selects a movie to see available shows
    // ========================================================================

    /**
     * GRASP Controller: Handles show listing for a movie
     */
    @GetMapping("/movie/{movieId}/shows")
    public String viewShows(@PathVariable Long movieId, Model model) {
        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) {
            return "redirect:/movies?error=Movie not found";
        }

        List<Show> shows = showService.getUpcomingShowsForMovie(movieId);

        model.addAttribute("movie", movie);
        model.addAttribute("shows", shows);

        return "movies"; // Will show movie details with shows
    }

    // ========================================================================
    // USE CASE: Select Seats
    // System Event: User selects a show and wants to choose seats
    // ========================================================================

    /**
     * GRASP Controller: Handles seat selection page
     */
    @GetMapping("/show/{showId}/seats")
    public String selectSeats(@PathVariable Long showId, Model model, HttpSession session) {
        Show show = showService.getShowById(showId);
        if (show == null) {
            return "redirect:/movies?error=Show not found";
        }

        // Controller coordinates - services provide data
        List<Seat> availableSeats = bookingService.getAvailableSeats(showId);
        List<Seat> bookedSeats = bookingService.getBookedSeats(showId);

        model.addAttribute("show", show);
        model.addAttribute("movie", show.getMovie());
        model.addAttribute("availableSeats", availableSeats);
        model.addAttribute("bookedSeats", bookedSeats);
        model.addAttribute("paymentMethods", paymentService.getAvailablePaymentMethods());

        return "booking";
    }

    // ========================================================================
    // USE CASE: Create Booking
    // System Event: User confirms seat selection and creates booking
    // ========================================================================

    /**
     * GRASP Controller: Handles booking creation
     * Controller receives the event, delegates to BookingService
     */
    @PostMapping("/booking/create")
    public String createBooking(@RequestParam Long showId,
                               @RequestParam List<Long> seatIds,
                               @RequestParam(required = false) String customerName,
                               @RequestParam(required = false) String customerEmail,
                               @RequestParam(required = false) String customerPhone,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            // Get or create customer
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                // Create guest customer
                Customer customer = userService.registerCustomer(
                    customerName != null ? customerName : "Guest",
                    customerEmail != null ? customerEmail : "guest" + System.currentTimeMillis() + "@temp.com",
                    "guest123",
                    customerPhone
                );
                customerId = customer.getId();
                session.setAttribute("customerId", customerId);
                session.setAttribute("customerName", customer.getName());
            }

            // GRASP Controller: Delegate booking creation to service
            Booking booking = bookingService.createBooking(customerId, showId, seatIds);

            return "redirect:/booking/" + booking.getId() + "/payment";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/show/" + showId + "/seats";
        }
    }

    // ========================================================================
    // USE CASE: Make Payment
    // System Event: User proceeds to pay for booking
    // ========================================================================

    /**
     * GRASP Controller: Handles payment page display
     */
    @GetMapping("/booking/{bookingId}/payment")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingWithSeats(bookingId);
        if (booking == null) {
            return "redirect:/movies?error=Booking not found";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("paymentMethods", paymentService.getAvailablePaymentMethods());

        return "booking"; // Shows payment section
    }

    /**
     * GRASP Controller: Handles payment processing
     * Delegates to PaymentService which uses Polymorphism
     */
    @PostMapping("/booking/{bookingId}/pay")
    public String processPayment(@PathVariable Long bookingId,
                                @RequestParam String paymentMethod,
                                @RequestParam(required = false) String cardNumber,
                                @RequestParam(required = false) String cvv,
                                @RequestParam(required = false) String expiry,
                                @RequestParam(required = false) String upiId,
                                @RequestParam(required = false) String bankCode,
                                @RequestParam(required = false) String accountNumber,
                                RedirectAttributes redirectAttributes) {
        try {
            // Prepare payment details based on method
            Map<String, String> paymentDetails = new HashMap<>();

            PaymentMethod method = PaymentMethod.valueOf(paymentMethod);

            switch (method) {
                case CREDIT_CARD:
                case DEBIT_CARD:
                    paymentDetails.put("cardNumber", cardNumber != null ? cardNumber : "4111111111111111");
                    paymentDetails.put("cvv", cvv != null ? cvv : "123");
                    paymentDetails.put("expiry", expiry != null ? expiry : "12/25");
                    paymentDetails.put("pin", "1234");
                    break;
                case UPI:
                    paymentDetails.put("upiId", upiId != null ? upiId : "user@upi");
                    break;
                case NET_BANKING:
                    paymentDetails.put("bankCode", bankCode != null ? bankCode : "HDFC");
                    paymentDetails.put("accountNumber", accountNumber != null ? accountNumber : "12345678901234");
                    break;
            }

            // GRASP Controller: Delegate to PaymentService (uses Polymorphism)
            Payment payment = paymentService.processPayment(bookingId, method, paymentDetails);

            if (payment.isSuccessful()) {
                return "redirect:/booking/" + bookingId + "/confirmation";
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment failed. Please try again.");
                return "redirect:/booking/" + bookingId + "/payment";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/" + bookingId + "/payment";
        }
    }

    // ========================================================================
    // USE CASE: View Booking Confirmation
    // System Event: User views confirmed booking details
    // ========================================================================

    /**
     * GRASP Controller: Handles confirmation page
     */
    @GetMapping("/booking/{bookingId}/confirmation")
    public String showConfirmation(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingWithSeats(bookingId);
        if (booking == null) {
            return "redirect:/movies?error=Booking not found";
        }

        Payment payment = paymentService.getPaymentByBooking(bookingId);

        model.addAttribute("booking", booking);
        model.addAttribute("payment", payment);
        // INFORMATION EXPERT: Booking provides its own formatted details
        model.addAttribute("bookingDetails", booking.getBookingDetails());

        return "confirmation";
    }

    // ========================================================================
    // USE CASE: View My Bookings
    // System Event: Customer views their booking history
    // ========================================================================

    /**
     * GRASP Controller: Handles booking history page
     */
    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/?error=Please login first";
        }

        List<Booking> bookings = bookingService.getCustomerBookings(customerId);
        model.addAttribute("bookings", bookings);

        return "movies"; // Could be a separate template
    }

    // ========================================================================
    // USE CASE: Customer Login/Register (simplified)
    // ========================================================================

    /**
     * GRASP Controller: Handle login
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            Customer customer = userService.loginCustomer(email, password);
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("customerName", customer.getName());
            session.setAttribute("customerEmail", customer.getEmail());
            redirectAttributes.addFlashAttribute("success", "Welcome " + customer.getName());
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login-page";
        }
    }

    /**
     * GRASP Controller: Handle registration
     */
    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String phone,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            Customer customer = userService.registerCustomer(name, email, password, phone);
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("customerName", customer.getName());
            session.setAttribute("customerEmail", customer.getEmail());
            redirectAttributes.addFlashAttribute("success", "Registration successful! Welcome!");
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login-page";
        }
    }

    /**
     * GRASP Controller: Handle logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login-page";
    }
}
