package com.moviebooking.model;

import jakarta.persistence.*;

/**
 * Seat entity representing an individual seat
 */
@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @Column(name = "row_name")
    private String rowName;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType = SeatType.REGULAR;

    // Constructors
    public Seat() {}

    public Seat(Screen screen, String rowName, Integer seatNumber, SeatType seatType) {
        this.screen = screen;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }

    public String getRowName() { return rowName; }
    public void setRowName(String rowName) { this.rowName = rowName; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }

    public String getSeatLabel() {
        return rowName + seatNumber;
    }

    public double getPriceMultiplier() {
        return seatType.getPriceMultiplier();
    }
}
