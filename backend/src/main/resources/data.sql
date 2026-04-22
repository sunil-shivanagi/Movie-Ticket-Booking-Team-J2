-- ============================================================================
-- MOVIE TICKET BOOKING SYSTEM - Sample Data
-- ============================================================================
-- This file initializes the database with sample data for testing
-- Run this after the application creates the schema (ddl-auto=update)
-- ============================================================================

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- DATA CLEANUP: Remove orphaned/stale data to prevent EntityNotFoundException
-- This ensures clean state on every application restart
-- ============================================================================

-- Step 1: Delete all transient data (order matters due to foreign keys)
DELETE FROM booked_seats;
DELETE FROM bookings;
DELETE FROM payments;

-- Step 2: Delete shows that reference non-existent movies (orphaned shows)
DELETE FROM shows WHERE movie_id NOT IN (SELECT id FROM movies);

-- Step 3: Delete shows that reference non-existent screens (orphaned shows)
DELETE FROM shows WHERE screen_id NOT IN (SELECT id FROM screens);

-- Step 4: Clean up old shows data completely for fresh insert
TRUNCATE TABLE shows;

-- Step 5: Clean up old movies data completely for fresh insert
TRUNCATE TABLE movies;

-- Re-enable foreign key checks before inserting new data
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- DEVELOPER ADMIN CREDENTIALS (REQUIRED TO LOGIN)
-- Email: admin@moviebooking.com
-- Password: admin@123
-- This is the only admin account for backend access
-- ============================================================================
INSERT INTO users (user_type, name, email, password, phone) VALUES
('ADMIN', 'Admin Developer', 'admin@moviebooking.com', 'admin@123', '9999999999')
ON DUPLICATE KEY UPDATE password='admin@123', name='Admin Developer';

-- ============================================================================
-- SAMPLE CUSTOMER DATA (COMMENTED OUT - Users can register new accounts)
-- Uncomment below if you want sample customer accounts for testing
-- ============================================================================
-- INSERT INTO users (user_type, name, email, password, phone) VALUES
-- ('CUSTOMER', 'John Doe', 'john@example.com', 'password123', '9876543211'),
-- ('CUSTOMER', 'Jane Smith', 'jane@example.com', 'password123', '9876543212'),
-- ('CUSTOMER', 'Bob Wilson', 'bob@example.com', 'password123', '9876543213')
-- ON DUPLICATE KEY UPDATE name=name;

-- Insert Theatres
INSERT INTO theatres (name, address, city) VALUES
('PVR Cinemas', 'Phoenix Mall, Whitefield', 'Bangalore'),
('INOX Movies', 'Garuda Mall, Magrath Road', 'Bangalore'),
('Cinepolis', 'Orion Mall, Rajajinagar', 'Bangalore')
ON DUPLICATE KEY UPDATE name=name;

-- Insert Screens (Theatre 1)
INSERT INTO screens (name, theatre_id, total_seats) VALUES
('Screen 1', 1, 100),
('Screen 2', 1, 80),
('Screen 3', 1, 60)
ON DUPLICATE KEY UPDATE name=name;

-- Insert Screens (Theatre 2)
INSERT INTO screens (name, theatre_id, total_seats) VALUES
('Audi 1', 2, 120),
('Audi 2', 2, 100)
ON DUPLICATE KEY UPDATE name=name;

-- ============================================================================
-- INSERT MOVIES WITH EXPLICIT IDS AND REAL TMDB POSTER URLS
-- Using actual TMDB (The Movie Database) poster images
-- ============================================================================
INSERT INTO movies (id, title, description, duration, genre, language, release_date, poster_url, rating) VALUES
(1, 'Avengers: Endgame', 'The epic conclusion to the Infinity Saga. The Avengers assemble for one final battle against Thanos.', 181, 'Action', 'English', '2024-01-15', 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 8.4),
(2, 'Inception', 'A thief who steals corporate secrets through dream-sharing technology is given the task of planting an idea into the mind of a CEO.', 148, 'Sci-Fi', 'English', '2024-02-01', 'https://media.themoviedb.org/t/p/w600_and_h900_face/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg', 8.8),
(3, 'The Dark Knight', 'Batman faces the Joker, a criminal mastermind who wants to plunge Gotham City into anarchy.', 152, 'Action', 'English', '2024-01-20', 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 9.0),
(4, 'Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.', 169, 'Sci-Fi', 'English', '2024-03-01', 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', 8.6),
(5, 'Pushpa 2', 'Pushpa Raj continues his rise in the world of red sandalwood smuggling, facing new challenges and enemies.', 178, 'Action', 'Telugu', '2024-12-05', 'https://media.themoviedb.org/t/p/w600_and_h900_face/bhxZj3y59cK7JtGdV285dhDRaMe.jpg', 8.2),
(6, 'Kalki 2898 AD', 'A sci-fi epic set in a dystopian future India, inspired by Hindu mythology.', 165, 'Sci-Fi', 'Telugu', '2024-06-27', 'https://media.themoviedb.org/t/p/w600_and_h900_face/rstcAnBeCkxNQjNp3YXrF6IP1tW.jpg', 7.8),
(7, 'Stree 2', 'The horror comedy sequel where the gang faces a new supernatural threat.', 145, 'Horror', 'Hindi', '2024-08-15', 'https://media.themoviedb.org/t/p/w600_and_h900_face/2NC7sj8rheKxWqLYAbHnCa4mYBH.jpg', 8.0),
(8, 'Fighter', 'An action drama about Indian Air Force pilots.', 160, 'Action', 'Hindi', '2024-01-25', 'https://m.media-amazon.com/images/M/MV5BNjk2YjI2ZTUtOGUzMC00ZWNkLThiY2EtMzZjZTk0NDNhYjQyXkEyXkFqcGc@._V1_.jpg', 7.5),
(9, 'Shershaah', 'A biographical war film about Indian soldier Vikram Batra who fought in the Kargil War.', 143, 'War', 'Hindi', '2024-05-12', 'https://media.themoviedb.org/t/p/w600_and_h900_face/zGvFnwoXJKrYnKhoVPytqkqCJ8V.jpg', 8.1),
(10, 'Drishyam 2', 'A gripping thriller where a man must use his intelligence to protect his family from a murder investigation.', 150, 'Thriller', 'Hindi', '2024-10-18', 'https://media.themoviedb.org/t/p/w600_and_h900_face/8RJBCUGE27LX06tAES4jTELN0KA.jpg', 8.3),
(11, 'Dunkirk', 'The epic story of the evacuation of Allied soldiers from the beaches of Dunkirk during World War II.', 107, 'War', 'English', '2024-07-21', 'https://media.themoviedb.org/t/p/w600_and_h900_face/b4Oe15CGLL61Ped0RAS9JpqdmCt.jpg', 7.9),
(12, 'Oppenheimer', 'A biographical thriller about J. Robert Oppenheimer and his role in the development of the atomic bomb.', 180, 'Biography', 'English', '2024-07-05', 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', 8.5),
(13, 'Khiladi', 'An action-packed thriller about a pilot caught in a dangerous web of espionage.', 126, 'Action', 'Hindi', '2024-06-14', 'https://media.themoviedb.org/t/p/w1066_and_h600_face/rqJRmGFuXJMMc1iMzUC6hWiJANO.jpg', 7.6),
(14, 'Barfi!', 'A romantic comedy-drama about a deaf, dumb and blind boy and his adventures with two girls.', 130, 'Romance', 'Hindi', '2024-09-21', 'https://media.themoviedb.org/t/p/w600_and_h900_face/5cJIx2zKjDoUtPSliou23xsReb1.jpg', 8.2),
(15, 'Jawan', 'An action thriller about a man seeking revenge against those who wronged him.', 169, 'Action', 'Hindi', '2024-09-07', 'https://media.themoviedb.org/t/p/w600_and_h900_face/jFt1gS4BGHlK8xt76Y81Alp4dbt.jpg', 7.7),
(16, 'Avatar: The Way of Water', 'The sequel to the 2009 blockbuster Avatar, exploring the oceans of Pandora.', 192, 'Sci-Fi', 'English', '2024-12-16', 'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg', 8.3),
(17, 'Pathaan', 'A high-octane spy thriller about a secret agent on a dangerous mission.', 146, 'Thriller', 'Hindi', '2024-01-25', 'https://image.tmdb.org/t/p/w500/kuf6dutpsT0vSVehic3EZIqkOBt.jpg', 7.8),
(18, 'Doctor Strange in the Multiverse of Madness', 'Doctor Strange navigates the multiverse with the Scarlet Witch in this action-packed adventure.', 126, 'Sci-Fi', 'English', '2024-05-06', 'https://image.tmdb.org/t/p/w500/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg', 7.5);

-- ============================================================================
-- INSERT SHOWS (Today's shows distributed across all movies)
-- Show times range from 10:00 AM to 9:00 PM
-- ============================================================================
INSERT INTO shows (movie_id, screen_id, show_time, price) VALUES
-- Avengers (Movie 1)
(1, 1, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 250.00),
(1, 1, DATE_ADD(CURDATE(), INTERVAL 14 HOUR), 300.00),
(1, 2, DATE_ADD(CURDATE(), INTERVAL 18 HOUR), 350.00),
-- Inception (Movie 2)
(2, 2, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 280.00),
(2, 3, DATE_ADD(CURDATE(), INTERVAL 15 HOUR), 320.00),
-- Dark Knight (Movie 3)
(3, 3, DATE_ADD(CURDATE(), INTERVAL 12 HOUR), 260.00),
(3, 4, DATE_ADD(CURDATE(), INTERVAL 16 HOUR), 300.00),
-- Interstellar (Movie 4)
(4, 4, DATE_ADD(CURDATE(), INTERVAL 13 HOUR), 280.00),
(4, 5, DATE_ADD(CURDATE(), INTERVAL 17 HOUR), 320.00),
-- Pushpa 2 (Movie 5)
(5, 1, DATE_ADD(CURDATE(), INTERVAL 21 HOUR), 400.00),
(5, 2, DATE_ADD(CURDATE(), INTERVAL 20 HOUR), 380.00),
-- Kalki 2898 AD (Movie 6)
(6, 3, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 350.00),
(6, 4, DATE_ADD(CURDATE(), INTERVAL 14 HOUR), 380.00),
-- Stree 2 (Movie 7)
(7, 5, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 300.00),
(7, 1, DATE_ADD(CURDATE(), INTERVAL 15 HOUR), 320.00),
-- Fighter (Movie 8)
(8, 2, DATE_ADD(CURDATE(), INTERVAL 12 HOUR), 280.00),
(8, 3, DATE_ADD(CURDATE(), INTERVAL 16 HOUR), 300.00),
-- Shershaah (Movie 9)
(9, 4, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 270.00),
(9, 5, DATE_ADD(CURDATE(), INTERVAL 14 HOUR), 290.00),
-- Drishyam 2 (Movie 10)
(10, 1, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 310.00),
(10, 2, DATE_ADD(CURDATE(), INTERVAL 15 HOUR), 330.00),
-- Dunkirk (Movie 11)
(11, 3, DATE_ADD(CURDATE(), INTERVAL 13 HOUR), 290.00),
(11, 4, DATE_ADD(CURDATE(), INTERVAL 17 HOUR), 310.00),
-- Oppenheimer (Movie 12)
(12, 5, DATE_ADD(CURDATE(), INTERVAL 12 HOUR), 350.00),
(12, 1, DATE_ADD(CURDATE(), INTERVAL 16 HOUR), 370.00),
-- Khiladi (Movie 13)
(13, 2, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 260.00),
(13, 3, DATE_ADD(CURDATE(), INTERVAL 14 HOUR), 280.00),
-- Barfi (Movie 14)
(14, 4, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 250.00),
(14, 5, DATE_ADD(CURDATE(), INTERVAL 15 HOUR), 270.00),
-- Jawan (Movie 15)
(15, 1, DATE_ADD(CURDATE(), INTERVAL 12 HOUR), 320.00),
(15, 2, DATE_ADD(CURDATE(), INTERVAL 16 HOUR), 340.00),
-- Avatar 2 (Movie 16)
(16, 3, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), 400.00),
(16, 4, DATE_ADD(CURDATE(), INTERVAL 14 HOUR), 420.00),
-- Pathaan (Movie 17)
(17, 5, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), 300.00),
(17, 1, DATE_ADD(CURDATE(), INTERVAL 15 HOUR), 320.00),
-- Doctor Strange (Movie 18)
(18, 2, DATE_ADD(CURDATE(), INTERVAL 13 HOUR), 310.00),
(18, 3, DATE_ADD(CURDATE(), INTERVAL 17 HOUR), 330.00),
-- ============================================================================
-- COMING SOON - Tomorrow's shows (select movies)
-- ============================================================================
(1, 1, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 10 HOUR), 250.00),
(2, 2, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 14 HOUR), 280.00),
(5, 3, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), 400.00),
(6, 4, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 11 HOUR), 350.00),
(7, 5, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 15 HOUR), 300.00),
(12, 1, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR), 350.00),
(14, 2, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 16 HOUR), 250.00),
(15, 3, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 19 HOUR), 320.00),
(16, 4, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 12 HOUR), 400.00),
(18, 5, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 17 HOUR), 310.00),
-- ============================================================================
-- COMING SOON - Day After Tomorrow (2 days ahead)
-- ============================================================================
(3, 1, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 11 HOUR), 260.00),
(4, 2, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 14 HOUR), 280.00),
(8, 3, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR), 280.00),
(10, 4, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 12 HOUR), 310.00),
(11, 5, DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 18 HOUR), 290.00)
ON DUPLICATE KEY UPDATE price=price;

-- Insert Seats for Screen 1 (100 seats: 10 rows x 10 seats)
-- Rows A-F: Regular, G-H: Premium, I-J: VIP
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
-- Row A (Regular)
(1, 'A', 1, 'REGULAR'), (1, 'A', 2, 'REGULAR'), (1, 'A', 3, 'REGULAR'), (1, 'A', 4, 'REGULAR'), (1, 'A', 5, 'REGULAR'),
(1, 'A', 6, 'REGULAR'), (1, 'A', 7, 'REGULAR'), (1, 'A', 8, 'REGULAR'), (1, 'A', 9, 'REGULAR'), (1, 'A', 10, 'REGULAR'),
-- Row B (Regular)
(1, 'B', 1, 'REGULAR'), (1, 'B', 2, 'REGULAR'), (1, 'B', 3, 'REGULAR'), (1, 'B', 4, 'REGULAR'), (1, 'B', 5, 'REGULAR'),
(1, 'B', 6, 'REGULAR'), (1, 'B', 7, 'REGULAR'), (1, 'B', 8, 'REGULAR'), (1, 'B', 9, 'REGULAR'), (1, 'B', 10, 'REGULAR'),
-- Row C (Regular)
(1, 'C', 1, 'REGULAR'), (1, 'C', 2, 'REGULAR'), (1, 'C', 3, 'REGULAR'), (1, 'C', 4, 'REGULAR'), (1, 'C', 5, 'REGULAR'),
(1, 'C', 6, 'REGULAR'), (1, 'C', 7, 'REGULAR'), (1, 'C', 8, 'REGULAR'), (1, 'C', 9, 'REGULAR'), (1, 'C', 10, 'REGULAR'),
-- Row D (Regular)
(1, 'D', 1, 'REGULAR'), (1, 'D', 2, 'REGULAR'), (1, 'D', 3, 'REGULAR'), (1, 'D', 4, 'REGULAR'), (1, 'D', 5, 'REGULAR'),
(1, 'D', 6, 'REGULAR'), (1, 'D', 7, 'REGULAR'), (1, 'D', 8, 'REGULAR'), (1, 'D', 9, 'REGULAR'), (1, 'D', 10, 'REGULAR'),
-- Row E (Regular)
(1, 'E', 1, 'REGULAR'), (1, 'E', 2, 'REGULAR'), (1, 'E', 3, 'REGULAR'), (1, 'E', 4, 'REGULAR'), (1, 'E', 5, 'REGULAR'),
(1, 'E', 6, 'REGULAR'), (1, 'E', 7, 'REGULAR'), (1, 'E', 8, 'REGULAR'), (1, 'E', 9, 'REGULAR'), (1, 'E', 10, 'REGULAR'),
-- Row F (Regular)
(1, 'F', 1, 'REGULAR'), (1, 'F', 2, 'REGULAR'), (1, 'F', 3, 'REGULAR'), (1, 'F', 4, 'REGULAR'), (1, 'F', 5, 'REGULAR'),
(1, 'F', 6, 'REGULAR'), (1, 'F', 7, 'REGULAR'), (1, 'F', 8, 'REGULAR'), (1, 'F', 9, 'REGULAR'), (1, 'F', 10, 'REGULAR'),
-- Row G (Premium)
(1, 'G', 1, 'PREMIUM'), (1, 'G', 2, 'PREMIUM'), (1, 'G', 3, 'PREMIUM'), (1, 'G', 4, 'PREMIUM'), (1, 'G', 5, 'PREMIUM'),
(1, 'G', 6, 'PREMIUM'), (1, 'G', 7, 'PREMIUM'), (1, 'G', 8, 'PREMIUM'), (1, 'G', 9, 'PREMIUM'), (1, 'G', 10, 'PREMIUM'),
-- Row H (Premium)
(1, 'H', 1, 'PREMIUM'), (1, 'H', 2, 'PREMIUM'), (1, 'H', 3, 'PREMIUM'), (1, 'H', 4, 'PREMIUM'), (1, 'H', 5, 'PREMIUM'),
(1, 'H', 6, 'PREMIUM'), (1, 'H', 7, 'PREMIUM'), (1, 'H', 8, 'PREMIUM'), (1, 'H', 9, 'PREMIUM'), (1, 'H', 10, 'PREMIUM'),
-- Row I (VIP center, Premium sides)
(1, 'I', 1, 'PREMIUM'), (1, 'I', 2, 'PREMIUM'), (1, 'I', 3, 'VIP'), (1, 'I', 4, 'VIP'), (1, 'I', 5, 'VIP'),
(1, 'I', 6, 'VIP'), (1, 'I', 7, 'VIP'), (1, 'I', 8, 'VIP'), (1, 'I', 9, 'PREMIUM'), (1, 'I', 10, 'PREMIUM'),
-- Row J (VIP)
(1, 'J', 1, 'VIP'), (1, 'J', 2, 'VIP'), (1, 'J', 3, 'VIP'), (1, 'J', 4, 'VIP'), (1, 'J', 5, 'VIP'),
(1, 'J', 6, 'VIP'), (1, 'J', 7, 'VIP'), (1, 'J', 8, 'VIP'), (1, 'J', 9, 'VIP'), (1, 'J', 10, 'VIP')
ON DUPLICATE KEY UPDATE seat_type=seat_type;

-- Insert Seats for Screen 2 (80 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
-- Rows A-D (Regular - 40 seats)
(2, 'A', 1, 'REGULAR'), (2, 'A', 2, 'REGULAR'), (2, 'A', 3, 'REGULAR'), (2, 'A', 4, 'REGULAR'), (2, 'A', 5, 'REGULAR'),
(2, 'A', 6, 'REGULAR'), (2, 'A', 7, 'REGULAR'), (2, 'A', 8, 'REGULAR'), (2, 'A', 9, 'REGULAR'), (2, 'A', 10, 'REGULAR'),
(2, 'B', 1, 'REGULAR'), (2, 'B', 2, 'REGULAR'), (2, 'B', 3, 'REGULAR'), (2, 'B', 4, 'REGULAR'), (2, 'B', 5, 'REGULAR'),
(2, 'B', 6, 'REGULAR'), (2, 'B', 7, 'REGULAR'), (2, 'B', 8, 'REGULAR'), (2, 'B', 9, 'REGULAR'), (2, 'B', 10, 'REGULAR'),
(2, 'C', 1, 'REGULAR'), (2, 'C', 2, 'REGULAR'), (2, 'C', 3, 'REGULAR'), (2, 'C', 4, 'REGULAR'), (2, 'C', 5, 'REGULAR'),
(2, 'C', 6, 'REGULAR'), (2, 'C', 7, 'REGULAR'), (2, 'C', 8, 'REGULAR'), (2, 'C', 9, 'REGULAR'), (2, 'C', 10, 'REGULAR'),
(2, 'D', 1, 'REGULAR'), (2, 'D', 2, 'REGULAR'), (2, 'D', 3, 'REGULAR'), (2, 'D', 4, 'REGULAR'), (2, 'D', 5, 'REGULAR'),
(2, 'D', 6, 'REGULAR'), (2, 'D', 7, 'REGULAR'), (2, 'D', 8, 'REGULAR'), (2, 'D', 9, 'REGULAR'), (2, 'D', 10, 'REGULAR'),
-- Rows E-F (Premium - 20 seats)
(2, 'E', 1, 'PREMIUM'), (2, 'E', 2, 'PREMIUM'), (2, 'E', 3, 'PREMIUM'), (2, 'E', 4, 'PREMIUM'), (2, 'E', 5, 'PREMIUM'),
(2, 'E', 6, 'PREMIUM'), (2, 'E', 7, 'PREMIUM'), (2, 'E', 8, 'PREMIUM'), (2, 'E', 9, 'PREMIUM'), (2, 'E', 10, 'PREMIUM'),
(2, 'F', 1, 'PREMIUM'), (2, 'F', 2, 'PREMIUM'), (2, 'F', 3, 'PREMIUM'), (2, 'F', 4, 'PREMIUM'), (2, 'F', 5, 'PREMIUM'),
(2, 'F', 6, 'PREMIUM'), (2, 'F', 7, 'PREMIUM'), (2, 'F', 8, 'PREMIUM'), (2, 'F', 9, 'PREMIUM'), (2, 'F', 10, 'PREMIUM'),
-- Rows G-H (VIP - 20 seats)
(2, 'G', 1, 'VIP'), (2, 'G', 2, 'VIP'), (2, 'G', 3, 'VIP'), (2, 'G', 4, 'VIP'), (2, 'G', 5, 'VIP'),
(2, 'G', 6, 'VIP'), (2, 'G', 7, 'VIP'), (2, 'G', 8, 'VIP'), (2, 'G', 9, 'VIP'), (2, 'G', 10, 'VIP'),
(2, 'H', 1, 'VIP'), (2, 'H', 2, 'VIP'), (2, 'H', 3, 'VIP'), (2, 'H', 4, 'VIP'), (2, 'H', 5, 'VIP'),
(2, 'H', 6, 'VIP'), (2, 'H', 7, 'VIP'), (2, 'H', 8, 'VIP'), (2, 'H', 9, 'VIP'), (2, 'H', 10, 'VIP')
ON DUPLICATE KEY UPDATE seat_type=seat_type;

-- Insert Seats for Screen 3 (20 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
(3, 'A', 1, 'REGULAR'), (3, 'A', 2, 'REGULAR'), (3, 'A', 3, 'REGULAR'), (3, 'A', 4, 'REGULAR'), (3, 'A', 5, 'REGULAR'),
(3, 'A', 6, 'REGULAR'), (3, 'A', 7, 'REGULAR'), (3, 'A', 8, 'REGULAR'), (3, 'A', 9, 'REGULAR'), (3, 'A', 10, 'REGULAR'),
(3, 'B', 1, 'REGULAR'), (3, 'B', 2, 'REGULAR'), (3, 'B', 3, 'REGULAR'), (3, 'B', 4, 'REGULAR'), (3, 'B', 5, 'REGULAR'),
(3, 'B', 6, 'REGULAR'), (3, 'B', 7, 'REGULAR'), (3, 'B', 8, 'REGULAR'), (3, 'B', 9, 'REGULAR'), (3, 'B', 10, 'REGULAR')
ON DUPLICATE KEY UPDATE seat_type=seat_type;

-- Insert Seats for Screen 4 (20 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
(4, 'A', 1, 'REGULAR'), (4, 'A', 2, 'REGULAR'), (4, 'A', 3, 'REGULAR'), (4, 'A', 4, 'REGULAR'), (4, 'A', 5, 'REGULAR'),
(4, 'A', 6, 'REGULAR'), (4, 'A', 7, 'REGULAR'), (4, 'A', 8, 'REGULAR'), (4, 'A', 9, 'REGULAR'), (4, 'A', 10, 'REGULAR'),
(4, 'B', 1, 'REGULAR'), (4, 'B', 2, 'REGULAR'), (4, 'B', 3, 'REGULAR'), (4, 'B', 4, 'REGULAR'), (4, 'B', 5, 'REGULAR'),
(4, 'B', 6, 'REGULAR'), (4, 'B', 7, 'REGULAR'), (4, 'B', 8, 'REGULAR'), (4, 'B', 9, 'REGULAR'), (4, 'B', 10, 'REGULAR')
ON DUPLICATE KEY UPDATE seat_type=seat_type;

-- Insert Seats for Screen 5 (20 seats)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type) VALUES
(5, 'A', 1, 'REGULAR'), (5, 'A', 2, 'REGULAR'), (5, 'A', 3, 'REGULAR'), (5, 'A', 4, 'REGULAR'), (5, 'A', 5, 'REGULAR'),
(5, 'A', 6, 'REGULAR'), (5, 'A', 7, 'REGULAR'), (5, 'A', 8, 'REGULAR'), (5, 'A', 9, 'REGULAR'), (5, 'A', 10, 'REGULAR'),
(5, 'B', 1, 'REGULAR'), (5, 'B', 2, 'REGULAR'), (5, 'B', 3, 'REGULAR'), (5, 'B', 4, 'REGULAR'), (5, 'B', 5, 'REGULAR'),
(5, 'B', 6, 'REGULAR'), (5, 'B', 7, 'REGULAR'), (5, 'B', 8, 'REGULAR'), (5, 'B', 9, 'REGULAR'), (5, 'B', 10, 'REGULAR')
ON DUPLICATE KEY UPDATE seat_type=seat_type;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
