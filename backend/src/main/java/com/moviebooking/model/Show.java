package com.moviebooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Show entity representing a movie screening
 */
@Entity
@Table(name = "shows")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "show_time", nullable = false)
    private LocalDateTime showTime;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // Constructors
    public Show() {}

    public Show(Movie movie, Screen screen, LocalDateTime showTime, Double price) {
        this.movie = movie;
        this.screen = screen;
        this.showTime = showTime;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }

    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public String getFormattedTime() {
        return showTime != null ? showTime.toLocalTime().toString() : "";
    }

    public String getFormattedDate() {
        return showTime != null ? showTime.toLocalDate().toString() : "";
    }
}
