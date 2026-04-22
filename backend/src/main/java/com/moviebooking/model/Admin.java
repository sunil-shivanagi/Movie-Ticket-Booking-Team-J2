package com.moviebooking.model;

import jakarta.persistence.*;

/**
 * Admin entity - manages movies, shows, theatres.
 * Note: Admin operations moved to separate services (GRASP: High Cohesion fix)
 * - MovieService handles movie management
 * - ShowService handles show scheduling
 * - ReportService handles analytics (Pure Fabrication)
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin() {}

    public Admin(String name, String email, String password, String phone) {
        super(name, email, password, phone);
    }
}
