package com.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Movie Ticket Booking System
 * OOAD Mini Project demonstrating GRASP Principles:
 * 1. Information Expert - Booking class
 * 2. High Cohesion + Pure Fabrication - AdminServices
 * 3. Polymorphism - PaymentStrategy
 * 4. Controller - CustomerController
 *
 * Team: Shrikant, Srujan, Sunil, Vikas
 */
@SpringBootApplication
public class MovieTicketBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieTicketBookingApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Movie Ticket Booking System Started!");
        System.out.println("  Access: http://localhost:8080");
        System.out.println("  Admin:  http://localhost:8080/admin");
        System.out.println("===========================================");
    }
}
