package com.moviebooking.service;

import com.moviebooking.model.Screen;
import com.moviebooking.model.Seat;
import com.moviebooking.model.SeatType;
import com.moviebooking.model.Theatre;
import com.moviebooking.repository.ScreenRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TheatreService - Additional service for theatre management
 */
@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Theatre> getAllTheatres() {
        return theatreRepository.findAll();
    }

    public Theatre getTheatreById(Long id) {
        return theatreRepository.findById(id).orElse(null);
    }

    public Theatre addTheatre(String name, String address, String city) {
        Theatre theatre = new Theatre(name, address, city);
        return theatreRepository.save(theatre);
    }

    public List<Screen> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }

    public Screen addScreen(Long theatreId, String name, Integer totalSeats) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new RuntimeException("Theatre not found"));

        Screen screen = new Screen(name, theatre, totalSeats);
        screen = screenRepository.save(screen);

        // Create seats for the screen
        createSeatsForScreen(screen, totalSeats);

        return screen;
    }

    private void createSeatsForScreen(Screen screen, int totalSeats) {
        int seatsPerRow = 10;
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        for (int row = 0; row < rows; row++) {
            char rowName = (char) ('A' + row);
            int seatsInRow = Math.min(seatsPerRow, totalSeats - (row * seatsPerRow));

            for (int seatNum = 1; seatNum <= seatsInRow; seatNum++) {
                SeatType type = SeatType.REGULAR;
                if (row >= rows - 2) type = SeatType.PREMIUM;
                if (row == rows - 1 && seatNum >= 4 && seatNum <= 7) type = SeatType.VIP;

                Seat seat = new Seat(screen, String.valueOf(rowName), seatNum, type);
                seatRepository.save(seat);
            }
        }
    }
}
