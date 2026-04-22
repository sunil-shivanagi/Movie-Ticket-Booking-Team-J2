package com.moviebooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Theatre entity representing a cinema hall
 */
@Entity
@Table(name = "theatres")
public class Theatre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private String city;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Screen> screens = new ArrayList<>();

    // Constructors
    public Theatre() {}

    public Theatre(String name, String address, String city) {
        this.name = name;
        this.address = address;
        this.city = city;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<Screen> getScreens() { return screens; }
    public void setScreens(List<Screen> screens) { this.screens = screens; }

    public void addScreen(Screen screen) {
        screens.add(screen);
        screen.setTheatre(this);
    }
}
