-- Drop existing database if exists
DROP DATABASE IF EXISTS cinema_db;
CREATE DATABASE cinema_db;
USE cinema_db;

-- Users table
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100),
                       phone VARCHAR(20),
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movies table
CREATE TABLE movies (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(200) NOT NULL,
                        duration INT NOT NULL,
                        genre VARCHAR(50),
                        rating VARCHAR(10),
                        description TEXT,
                        poster_url VARCHAR(500),
                        is_active BOOLEAN DEFAULT TRUE
);

-- Screens table
CREATE TABLE screens (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL,
                         capacity INT NOT NULL,
                         rows INT NOT NULL,
                         cols INT NOT NULL
);

-- Schedules table
CREATE TABLE schedules (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           movie_id INT NOT NULL,
                           screen_id INT NOT NULL,
                           start_time DATETIME NOT NULL,
                           end_time DATETIME NOT NULL,
                           base_price DECIMAL(10,2) NOT NULL,
                           FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
                           FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE
);

-- Seats table
CREATE TABLE seats (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       screen_id INT NOT NULL,
                       seat_row INT NOT NULL,
                       seat_number INT NOT NULL,
                       seat_type VARCHAR(20) DEFAULT 'REGULAR',
                       FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
                       UNIQUE KEY unique_seat (screen_id, seat_row, seat_number)
);

-- Bookings table
CREATE TABLE bookings (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT,
                          schedule_id INT NOT NULL,
                          booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          total_amount DECIMAL(10,2) NOT NULL,
                          status VARCHAR(20) DEFAULT 'CONFIRMED',
                          payment_method VARCHAR(50),
                          customer_name VARCHAR(100) NOT NULL,
                          customer_phone VARCHAR(20) NOT NULL,
                          hold_expiry TIMESTAMP NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                          FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE
);

-- Tickets table
CREATE TABLE tickets (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         booking_id INT NOT NULL,
                         seat_id INT NOT NULL,
                         price DECIMAL(10,2) NOT NULL,
                         ticket_type VARCHAR(20) DEFAULT 'ADULT',
                         FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
                         FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

-- Logs table
CREATE TABLE logs (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      user_id INT,
                      action VARCHAR(100) NOT NULL,
                      details TEXT,
                      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Insert sample users
INSERT INTO users (username, password, name, email, phone, role) VALUES
                                                                     ('admin', 'admin123', 'System Administrator', 'admin@cinema.com', '1234567890', 'ADMIN'),
                                                                     ('manager1', 'manager123', 'John Manager', 'manager@cinema.com', '1234567891', 'MANAGER'),
                                                                     ('staff1', 'staff123', 'Jane Staff', 'staff@cinema.com', '1234567892', 'FRONT_DESK');

-- Insert sample movies
INSERT INTO movies (title, duration, genre, rating, description, is_active) VALUES
                                                                                ('Inception', 148, 'Sci-Fi', 'PG-13', 'A thief who steals corporate secrets through dream-sharing technology.', TRUE),
                                                                                ('The Dark Knight', 152, 'Action', 'PG-13', 'Batman faces his greatest threat yet - the Joker.', TRUE),
                                                                                ('Interstellar', 169, 'Sci-Fi', 'PG-13', 'A team of explorers travel through a wormhole in space.', TRUE),
                                                                                ('John Wick', 101, 'Action', 'R', 'A former hitman comes out of retirement to seek revenge.', TRUE),
                                                                                ('The Matrix', 136, 'Sci-Fi', 'R', 'A computer hacker learns about the true nature of reality.', TRUE);

-- Insert screens
INSERT INTO screens (name, capacity, rows, cols) VALUES
                                                     ('Screen 1', 150, 10, 15),
                                                     ('Screen 2', 120, 8, 15);

-- Generate seats for Screen 1
INSERT INTO seats (screen_id, seat_row, seat_number, seat_type)
SELECT 1, row_num, col_num,
       CASE
           WHEN row_num <= 2 THEN 'VIP'
           ELSE 'REGULAR'
           END
FROM (
         SELECT a.a+1 as row_num, b.a+1 as col_num
         FROM (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a
                  CROSS JOIN (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14) b
         WHERE a.a+1 <= 10 AND b.a+1 <= 15
     ) seats;

-- Generate seats for Screen 2
INSERT INTO seats (screen_id, seat_row, seat_number, seat_type)
SELECT 2, row_num, col_num, 'REGULAR'
FROM (
         SELECT a.a+1 as row_num, b.a+1 as col_num
         FROM (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) a
                  CROSS JOIN (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14) b
         WHERE a.a+1 <= 8 AND b.a+1 <= 15
     ) seats;

-- Insert sample schedules (shows starting from tomorrow)
INSERT INTO schedules (movie_id, screen_id, start_time, end_time, base_price)
SELECT
    m.id,
    s.id,
    DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 18 HOUR,
    DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 18 HOUR + INTERVAL m.duration MINUTE,
    12.00
FROM movies m
    CROSS JOIN screens s
WHERE m.id = s.id
    LIMIT 2;

INSERT INTO schedules (movie_id, screen_id, start_time, end_time, base_price)
SELECT
    m.id,
    s.id,
    DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 21 HOUR,
    DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 21 HOUR + INTERVAL m.duration MINUTE,
    12.00
FROM movies m
    CROSS JOIN screens s
WHERE m.id = s.id + 1
    LIMIT 2;

-- Verify data
SELECT 'Setup complete!' as Status;
SELECT COUNT(*) as TotalUsers FROM users;
SELECT COUNT(*) as TotalMovies FROM movies;
SELECT COUNT(*) as TotalScreens FROM screens;
SELECT COUNT(*) as TotalSeats FROM seats;
SELECT COUNT(*) as TotalSchedules FROM schedules;