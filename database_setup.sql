
-- Create Database
CREATE DATABASE IF NOT EXISTS ocean_resort_db;
USE ocean_resort_db;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS users;

-- ============================================
-- Table: reservations
-- Stores all reservation information
-- ============================================
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_no VARCHAR(50) UNIQUE NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact VARCHAR(50) NOT NULL,
    room_type ENUM('Single', 'Double', 'Suite') NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_cost BIGINT NOT NULL,
    manual_status VARCHAR(20) NULL,  -- NULL for auto-detect, or 'Checked-Out'/'Cancelled'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_reservation_no (reservation_no),
    INDEX idx_guest_name (guest_name),
    INDEX idx_check_in (check_in_date),
    INDEX idx_check_out (check_out_date),
    INDEX idx_status (manual_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: users
-- Stores system users for authentication
-- ============================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Store hashed password in production
    full_name VARCHAR(100),
    role ENUM('admin', 'staff') DEFAULT 'staff',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table: audit_log
-- Tracks all changes to reservations
-- ============================================
CREATE TABLE audit_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_no VARCHAR(50),
    action VARCHAR(50) NOT NULL,  -- CREATE, UPDATE, DELETE, CHECK_OUT, CANCEL
    performed_by VARCHAR(50),
    action_details TEXT,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_reservation (reservation_no),
    INDEX idx_date (performed_at),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Insert Default Admin User
-- Username: admin, Password: 123
-- ============================================
INSERT INTO users (username, password, full_name, role) 
VALUES ('admin', '123', 'System Administrator', 'admin');

-- ============================================
-- Insert Sample Data (Optional - for testing)
-- ============================================
INSERT INTO reservations (reservation_no, guest_name, address, contact, room_type, check_in_date, check_out_date, total_cost, manual_status)
VALUES 
    ('R001', 'John Doe', '123 Main St, Colombo', '+94771234567', 'Double', '2026-02-20', '2026-02-25', 60000, NULL),
    ('R002', 'Jane Smith', '456 Ocean View, Galle', '+94772345678', 'Suite', '2026-02-18', '2026-02-22', 80000, NULL),
    ('R003', 'Mike Johnson', '789 Beach Road, Negombo', '+94773456789', 'Single', '2026-02-16', '2026-02-19', 24000, NULL);
