package com.moviebooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen entity representing an individual screen in a theatre
 */
@Entity
@Table(name = "screens")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Show> shows = new ArrayList<>();

    // Constructors
    public Screen() {}

    public Screen(String name, Theatre theatre, Integer totalSeats) {
        this.name = name;
        this.theatre = theatre;
        this.totalSeats = totalSeats;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Theatre getTheatre() { return theatre; }
    public void setTheatre(Theatre theatre) { this.theatre = theatre; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    public List<Show> getShows() { return shows; }
    public void setShows(List<Show> shows) { this.shows = shows; }
}
