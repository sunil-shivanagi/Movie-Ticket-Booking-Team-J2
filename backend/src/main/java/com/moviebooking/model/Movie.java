package com.moviebooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Movie entity representing films available for booking
 */
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration; // in minutes

    private String genre;

    private String language;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url")
    private String posterUrl;

    private Double rating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Show> shows = new ArrayList<>();

    // Constructors
    public Movie() {}

    public Movie(String title, String description, Integer duration, String genre,
                 String language, LocalDate releaseDate, String posterUrl) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.genre = genre;
        this.language = language;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public List<Show> getShows() { return shows; }
    public void setShows(List<Show> shows) { this.shows = shows; }

    public String getDurationFormatted() {
        if (duration == null) return "N/A";
        int hours = duration / 60;
        int mins = duration % 60;
        return hours + "h " + mins + "m";
    }
}
