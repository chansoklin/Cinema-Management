USE cinema_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, name, role) VALUES 
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('manager1', 'manager123', 'John Manager', 'MANAGER'),
('staff1', 'staff123', 'Jane Staff', 'FRONT_DESK')
ON DUPLICATE KEY UPDATE name=VALUES(name);

CREATE TABLE IF NOT EXISTS movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    duration INT NOT NULL,
    genre VARCHAR(50),
    rating VARCHAR(10),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO movies (title, duration, genre, rating) VALUES 
('Inception', 148, 'Sci-Fi', 'PG-13'),
('The Dark Knight', 152, 'Action', 'PG-13'),
('Interstellar', 169, 'Sci-Fi', 'PG-13')
ON DUPLICATE KEY UPDATE title=VALUES(title);

CREATE TABLE IF NOT EXISTS screens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL
);

INSERT INTO screens (name, capacity) VALUES 
('Screen 1', 150),
('Screen 2', 120)
ON DUPLICATE KEY UPDATE name=VALUES(name);

SELECT 'Database setup complete!' as Status;
SELECT COUNT(*) as Users FROM users;
SELECT COUNT(*) as Movies FROM movies;
SELECT COUNT(*) as Screens FROM screens;
