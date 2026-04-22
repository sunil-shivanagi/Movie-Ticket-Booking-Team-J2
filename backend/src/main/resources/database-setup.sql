-- ============================================================================
-- MOVIE TICKET BOOKING SYSTEM - DATABASE SETUP WITH ADVANCED SQL FEATURES
-- ============================================================================
-- This script includes:
-- 1. Audit Log Table
-- 2. Triggers for tracking all changes
-- 3. Stored Procedures for operations
-- 4. Functions for calculations
-- 5. Views for reporting
-- ============================================================================

USE moviebooking;

-- ============================================================================
-- 1. CREATE AUDIT LOG TABLE
-- ============================================================================
-- This table tracks all actions in the system

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    record_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    user_type VARCHAR(20),
    user_id BIGINT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_timestamp (table_name, timestamp),
    INDEX idx_operation (operation),
    INDEX idx_user (user_type, user_id)
);

-- ============================================================================
-- 2. CREATE TRIGGERS - Track all INSERT, UPDATE, DELETE operations
-- ============================================================================

-- MOVIES TABLE TRIGGERS
DELIMITER $$

CREATE TRIGGER IF NOT EXISTS trg_movies_after_insert
AFTER INSERT ON movies
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type)
    VALUES ('movies', 'INSERT', NEW.id,
            CONCAT('Title: ', NEW.title, ', Genre: ', NEW.genre, ', Language: ', NEW.language),
            'ADMIN');
END$$

CREATE TRIGGER IF NOT EXISTS trg_movies_after_update
AFTER UPDATE ON movies
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value, user_type)
    VALUES ('movies', 'UPDATE', NEW.id,
            CONCAT('Title: ', OLD.title, ', Rating: ', COALESCE(OLD.rating, 0)),
            CONCAT('Title: ', NEW.title, ', Rating: ', COALESCE(NEW.rating, 0)),
            'ADMIN');
END$$

CREATE TRIGGER IF NOT EXISTS trg_movies_after_delete
AFTER DELETE ON movies
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_value, user_type)
    VALUES ('movies', 'DELETE', OLD.id,
            CONCAT('Title: ', OLD.title, ', Genre: ', OLD.genre),
            'ADMIN');
END$$

-- BOOKINGS TABLE TRIGGERS
CREATE TRIGGER IF NOT EXISTS trg_bookings_after_insert
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type, user_id)
    VALUES ('bookings', 'INSERT', NEW.id,
            CONCAT('Show ID: ', NEW.show_id, ', Seats: ', NEW.num_seats, ', Amount: ', NEW.total_amount),
            'CUSTOMER', NEW.customer_id);
END$$

CREATE TRIGGER IF NOT EXISTS trg_bookings_after_update
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value, user_type, user_id)
    VALUES ('bookings', 'UPDATE', NEW.id,
            CONCAT('Status: ', OLD.status, ', Amount: ', OLD.total_amount),
            CONCAT('Status: ', NEW.status, ', Amount: ', NEW.total_amount),
            'CUSTOMER', NEW.customer_id);
END$$

-- PAYMENTS TABLE TRIGGER
CREATE TRIGGER IF NOT EXISTS trg_payments_after_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type)
    VALUES ('payments', 'INSERT', NEW.id,
            CONCAT('Booking ID: ', NEW.booking_id, ', Amount: ', NEW.amount, ', Method: ', NEW.payment_method, ', Status: ', NEW.status),
            'CUSTOMER');
END$$

-- THEATRES TABLE TRIGGERS
CREATE TRIGGER IF NOT EXISTS trg_theatres_after_insert
AFTER INSERT ON theatres
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type)
    VALUES ('theatres', 'INSERT', NEW.id,
            CONCAT('Name: ', NEW.name, ', City: ', NEW.city),
            'ADMIN');
END$$

-- SHOWS TABLE TRIGGERS
CREATE TRIGGER IF NOT EXISTS trg_shows_after_insert
AFTER INSERT ON shows
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type)
    VALUES ('shows', 'INSERT', NEW.id,
            CONCAT('Movie ID: ', NEW.movie_id, ', Screen ID: ', NEW.screen_id, ', Time: ', NEW.show_time, ', Price: ', NEW.price),
            'ADMIN');
END$$

CREATE TRIGGER IF NOT EXISTS trg_shows_after_update
AFTER UPDATE ON shows
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value, user_type)
    VALUES ('shows', 'UPDATE', NEW.id,
            CONCAT('Price: ', OLD.price, ', Time: ', OLD.show_time),
            CONCAT('Price: ', NEW.price, ', Time: ', NEW.show_time),
            'ADMIN');
END$$

-- USERS TABLE TRIGGERS
CREATE TRIGGER IF NOT EXISTS trg_users_after_insert
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type)
    VALUES ('users', 'INSERT', NEW.id,
            CONCAT('Name: ', NEW.name, ', Email: ', NEW.email, ', Type: ', NEW.user_type),
            NEW.user_type);
END$$

DELIMITER ;

-- ============================================================================
-- 3. CREATE STORED PROCEDURES
-- ============================================================================

-- Procedure: Book Tickets (handles booking creation and seat updates)
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS sp_book_tickets(
    IN p_customer_id BIGINT,
    IN p_show_id BIGINT,
    IN p_seat_ids TEXT,
    OUT p_booking_id BIGINT,
    OUT p_total_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_seat_count INT;
    DECLARE v_show_price DECIMAL(10,2);

    -- Get show price
    SELECT price INTO v_show_price FROM shows WHERE id = p_show_id;

    -- Calculate number of seats
    SET v_seat_count = (LENGTH(p_seat_ids) - LENGTH(REPLACE(p_seat_ids, ',', '')) + 1);

    -- Calculate total amount
    SET p_total_amount = v_show_price * v_seat_count;

    -- Create booking
    INSERT INTO bookings (customer_id, show_id, num_seats, total_amount, booking_time, status)
    VALUES (p_customer_id, p_show_id, v_seat_count, p_total_amount, NOW(), 'PENDING');

    SET p_booking_id = LAST_INSERT_ID();

    -- Log the operation
    INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type, user_id)
    VALUES ('bookings', 'PROCEDURE_BOOK', p_booking_id,
            CONCAT('Seats booked: ', v_seat_count, ', Amount: ', p_total_amount),
            'CUSTOMER', p_customer_id);
END$$

-- Procedure: Cancel Booking
CREATE PROCEDURE IF NOT EXISTS sp_cancel_booking(
    IN p_booking_id BIGINT,
    IN p_customer_id BIGINT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_status VARCHAR(20);
    DECLARE v_amount DECIMAL(10,2);

    -- Check if booking exists and belongs to customer
    SELECT status, total_amount INTO v_status, v_amount
    FROM bookings
    WHERE id = p_booking_id AND customer_id = p_customer_id;

    IF v_status IS NULL THEN
        SET p_result = 'ERROR: Booking not found';
    ELSEIF v_status = 'CANCELLED' THEN
        SET p_result = 'ERROR: Booking already cancelled';
    ELSE
        -- Update booking status
        UPDATE bookings SET status = 'CANCELLED' WHERE id = p_booking_id;

        -- Update payment status if exists
        UPDATE payments SET status = 'REFUNDED' WHERE booking_id = p_booking_id;

        SET p_result = CONCAT('SUCCESS: Booking cancelled, Refund: ', v_amount);

        -- Log the cancellation
        INSERT INTO audit_log (table_name, operation, record_id, new_value, user_type, user_id)
        VALUES ('bookings', 'PROCEDURE_CANCEL', p_booking_id,
                CONCAT('Booking cancelled, Refund: ', v_amount),
                'CUSTOMER', p_customer_id);
    END IF;
END$$

-- Procedure: Get Revenue Report
CREATE PROCEDURE IF NOT EXISTS sp_get_revenue_report(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        DATE(b.booking_time) as booking_date,
        COUNT(b.id) as total_bookings,
        SUM(b.num_seats) as total_seats_sold,
        SUM(b.total_amount) as total_revenue,
        AVG(b.total_amount) as avg_booking_value
    FROM bookings b
    WHERE DATE(b.booking_time) BETWEEN p_start_date AND p_end_date
      AND b.status = 'CONFIRMED'
    GROUP BY DATE(b.booking_time)
    ORDER BY booking_date DESC;
END$$

-- Procedure: Get Movie Performance
CREATE PROCEDURE IF NOT EXISTS sp_get_movie_performance()
BEGIN
    SELECT
        m.id,
        m.title,
        m.genre,
        COUNT(DISTINCT s.id) as total_shows,
        COUNT(b.id) as total_bookings,
        SUM(b.num_seats) as tickets_sold,
        SUM(b.total_amount) as revenue,
        AVG(m.rating) as avg_rating
    FROM movies m
    LEFT JOIN shows s ON m.id = s.movie_id
    LEFT JOIN bookings b ON s.id = b.show_id AND b.status = 'CONFIRMED'
    GROUP BY m.id, m.title, m.genre
    ORDER BY revenue DESC;
END$$

DELIMITER ;

-- ============================================================================
-- 4. CREATE FUNCTIONS
-- ============================================================================

DELIMITER $$

-- Function: Calculate booking revenue for a show
CREATE FUNCTION IF NOT EXISTS fn_calculate_show_revenue(p_show_id BIGINT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_revenue DECIMAL(10,2);

    SELECT COALESCE(SUM(total_amount), 0) INTO v_revenue
    FROM bookings
    WHERE show_id = p_show_id AND status = 'CONFIRMED';

    RETURN v_revenue;
END$$

-- Function: Get available seats count for a show
CREATE FUNCTION IF NOT EXISTS fn_get_available_seats(p_show_id BIGINT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total_seats INT;
    DECLARE v_booked_seats INT;

    -- Get total seats in the screen
    SELECT sc.total_seats INTO v_total_seats
    FROM shows sh
    JOIN screens sc ON sh.screen_id = sc.id
    WHERE sh.id = p_show_id;

    -- Get booked seats count
    SELECT COALESCE(SUM(b.num_seats), 0) INTO v_booked_seats
    FROM bookings b
    WHERE b.show_id = p_show_id AND b.status IN ('CONFIRMED', 'PENDING');

    RETURN (v_total_seats - v_booked_seats);
END$$

-- Function: Calculate customer loyalty points
CREATE FUNCTION IF NOT EXISTS fn_calculate_loyalty_points(p_customer_id BIGINT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_points INT;

    -- 1 point per 100 rupees spent
    SELECT FLOOR(COALESCE(SUM(total_amount) / 100, 0)) INTO v_points
    FROM bookings
    WHERE customer_id = p_customer_id AND status = 'CONFIRMED';

    RETURN v_points;
END$$

-- Function: Get movie popularity score
CREATE FUNCTION IF NOT EXISTS fn_movie_popularity_score(p_movie_id BIGINT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_score DECIMAL(10,2);
    DECLARE v_bookings INT;
    DECLARE v_rating DECIMAL(3,1);

    -- Get booking count
    SELECT COUNT(*) INTO v_bookings
    FROM bookings b
    JOIN shows s ON b.show_id = s.id
    WHERE s.movie_id = p_movie_id AND b.status = 'CONFIRMED';

    -- Get rating
    SELECT COALESCE(rating, 0) INTO v_rating FROM movies WHERE id = p_movie_id;

    -- Calculate score (bookings * 10 + rating * 20)
    SET v_score = (v_bookings * 10) + (v_rating * 20);

    RETURN v_score;
END$$

DELIMITER ;

-- ============================================================================
-- 5. CREATE VIEWS FOR REPORTING
-- ============================================================================

-- View: Movie Performance Summary
CREATE OR REPLACE VIEW vw_movie_performance AS
SELECT
    m.id as movie_id,
    m.title,
    m.genre,
    m.language,
    m.rating,
    COUNT(DISTINCT s.id) as total_shows,
    COUNT(DISTINCT b.id) as total_bookings,
    COALESCE(SUM(b.num_seats), 0) as tickets_sold,
    COALESCE(SUM(b.total_amount), 0) as total_revenue,
    COALESCE(AVG(b.total_amount), 0) as avg_booking_value,
    fn_movie_popularity_score(m.id) as popularity_score
FROM movies m
LEFT JOIN shows s ON m.id = s.movie_id
LEFT JOIN bookings b ON s.id = b.show_id AND b.status = 'CONFIRMED'
GROUP BY m.id, m.title, m.genre, m.language, m.rating
ORDER BY total_revenue DESC;

-- View: Daily Revenue Report
CREATE OR REPLACE VIEW vw_daily_revenue AS
SELECT
    DATE(b.booking_time) as booking_date,
    COUNT(b.id) as total_bookings,
    SUM(b.num_seats) as seats_sold,
    SUM(b.total_amount) as revenue,
    AVG(b.total_amount) as avg_booking_value,
    COUNT(DISTINCT b.customer_id) as unique_customers
FROM bookings b
WHERE b.status = 'CONFIRMED'
GROUP BY DATE(b.booking_time)
ORDER BY booking_date DESC;

-- View: Theatre Performance
CREATE OR REPLACE VIEW vw_theatre_performance AS
SELECT
    t.id as theatre_id,
    t.name as theatre_name,
    t.city,
    COUNT(DISTINCT sc.id) as total_screens,
    COUNT(DISTINCT s.id) as total_shows,
    COUNT(DISTINCT b.id) as total_bookings,
    COALESCE(SUM(b.total_amount), 0) as total_revenue
FROM theatres t
LEFT JOIN screens sc ON t.id = sc.theatre_id
LEFT JOIN shows s ON sc.id = s.screen_id
LEFT JOIN bookings b ON s.id = b.show_id AND b.status = 'CONFIRMED'
GROUP BY t.id, t.name, t.city
ORDER BY total_revenue DESC;

-- View: Customer Activity Summary
CREATE OR REPLACE VIEW vw_customer_activity AS
SELECT
    u.id as customer_id,
    u.name,
    u.email,
    u.phone,
    COUNT(b.id) as total_bookings,
    COALESCE(SUM(b.num_seats), 0) as total_tickets,
    COALESCE(SUM(b.total_amount), 0) as total_spent,
    fn_calculate_loyalty_points(u.id) as loyalty_points,
    MAX(b.booking_time) as last_booking_date
FROM users u
LEFT JOIN bookings b ON u.id = b.customer_id AND b.status = 'CONFIRMED'
WHERE u.user_type = 'CUSTOMER'
GROUP BY u.id, u.name, u.email, u.phone
ORDER BY total_spent DESC;

-- View: Upcoming Shows with Availability
CREATE OR REPLACE VIEW vw_upcoming_shows AS
SELECT
    s.id as show_id,
    m.title as movie_title,
    m.genre,
    m.language,
    m.duration,
    t.name as theatre_name,
    t.city,
    sc.name as screen_name,
    s.show_time,
    s.price,
    sc.total_seats,
    fn_get_available_seats(s.id) as available_seats,
    (sc.total_seats - fn_get_available_seats(s.id)) as booked_seats,
    ROUND((sc.total_seats - fn_get_available_seats(s.id)) * 100.0 / sc.total_seats, 2) as occupancy_percentage
FROM shows s
JOIN movies m ON s.movie_id = m.id
JOIN screens sc ON s.screen_id = sc.id
JOIN theatres t ON sc.theatre_id = t.id
WHERE s.show_time >= NOW()
ORDER BY s.show_time ASC;

-- View: Payment Summary
CREATE OR REPLACE VIEW vw_payment_summary AS
SELECT
    p.payment_method,
    COUNT(p.id) as transaction_count,
    SUM(p.amount) as total_amount,
    AVG(p.amount) as avg_transaction,
    SUM(CASE WHEN p.status = 'SUCCESS' THEN 1 ELSE 0 END) as successful_transactions,
    SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END) as failed_transactions,
    ROUND(SUM(CASE WHEN p.status = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(p.id), 2) as success_rate
FROM payments p
GROUP BY p.payment_method
ORDER BY total_amount DESC;

-- View: Audit Log Summary (Recent 1000 entries)
CREATE OR REPLACE VIEW vw_audit_summary AS
SELECT
    id,
    table_name,
    operation,
    record_id,
    SUBSTRING(old_value, 1, 100) as old_value_preview,
    SUBSTRING(new_value, 1, 100) as new_value_preview,
    user_type,
    user_id,
    timestamp
FROM audit_log
ORDER BY timestamp DESC
LIMIT 1000;

-- ============================================================================
-- 6. CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Indexes on frequently queried columns
CREATE INDEX IF NOT EXISTS idx_bookings_customer ON bookings(customer_id);
CREATE INDEX IF NOT EXISTS idx_bookings_show ON bookings(show_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_bookings_time ON bookings(booking_time);

CREATE INDEX IF NOT EXISTS idx_shows_movie ON shows(movie_id);
CREATE INDEX IF NOT EXISTS idx_shows_screen ON shows(screen_id);
CREATE INDEX IF NOT EXISTS idx_shows_time ON shows(show_time);

CREATE INDEX IF NOT EXISTS idx_payments_booking ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_method ON payments(payment_method);

CREATE INDEX IF NOT EXISTS idx_movies_genre ON movies(genre);
CREATE INDEX IF NOT EXISTS idx_movies_language ON movies(language);

-- ============================================================================
-- SETUP COMPLETE!
-- ============================================================================
-- You can now:
-- 1. Use stored procedures for operations: CALL sp_book_tickets(...);
-- 2. Use functions in queries: SELECT fn_calculate_show_revenue(1);
-- 3. Query views: SELECT * FROM vw_movie_performance;
-- 4. Check audit logs: SELECT * FROM audit_log ORDER BY timestamp DESC;
-- ============================================================================
