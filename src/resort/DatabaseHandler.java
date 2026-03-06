package resort;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;

public class DatabaseHandler {
    
    public static HashMap<String, Reservation> loadAllReservations() {
        HashMap<String, Reservation> reservations = new HashMap<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String resNo = rs.getString("reservation_no");
                String name = rs.getString("guest_name");
                String address = rs.getString("address");
                String contact = rs.getString("contact");
                String roomType = rs.getString("room_type");
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                String manualStatus = rs.getString("manual_status");
                Reservation r = new Reservation(resNo, name, address, contact, roomType, checkIn, checkOut);
                if (manualStatus != null && !manualStatus.isEmpty()) {
                    r.setStatusManual(manualStatus);
                }
                reservations.put(resNo, r);
            }
            System.out.println("✓ Loaded " + reservations.size() + " reservations from database");
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load reservations from database");
            e.printStackTrace();
        }
        return reservations;
    }

    public static boolean saveReservation(Reservation r) {
        String sql = "INSERT INTO reservations (reservation_no, guest_name, address, contact, " +
                     "room_type, check_in_date, check_out_date, total_cost, manual_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getResNo());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getAddress());
            pstmt.setString(4, r.getContact());
            pstmt.setString(5, r.getRoomType());
            pstmt.setDate(6, Date.valueOf(r.getCheckIn()));
            pstmt.setDate(7, Date.valueOf(r.getCheckOut()));
            pstmt.setLong(8, r.getTotalCost());
            pstmt.setString(9, r.isManualStatus() ? r.getStatus() : null);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logAction(r.getResNo(), "CREATE", "admin", "New reservation created");
                System.out.println("✓ Reservation saved: " + r.getResNo());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to save reservation");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateReservation(Reservation r) {
        String sql = "UPDATE reservations SET guest_name=?, address=?, contact=?, " +
                     "room_type=?, check_in_date=?, check_out_date=?, total_cost=?, " +
                     "manual_status=? WHERE reservation_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getName());
            pstmt.setString(2, r.getAddress());
            pstmt.setString(3, r.getContact());
            pstmt.setString(4, r.getRoomType());
            pstmt.setDate(5, Date.valueOf(r.getCheckIn()));
            pstmt.setDate(6, Date.valueOf(r.getCheckOut()));
            pstmt.setLong(7, r.getTotalCost());
            pstmt.setString(8, r.isManualStatus() ? r.getStatus() : null);
            pstmt.setString(9, r.getResNo());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logAction(r.getResNo(), "UPDATE", "admin", "Reservation updated");
                System.out.println("✓ Reservation updated: " + r.getResNo());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update reservation");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteReservation(String reservationNo) {
        String sql = "DELETE FROM reservations WHERE reservation_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservationNo);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logAction(reservationNo, "DELETE", "admin", "Reservation deleted");
                System.out.println("✓ Reservation deleted: " + reservationNo);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to delete reservation");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean reservationExists(String reservationNo) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reservation_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservationNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Kept for backward compatibility */
    public static boolean authenticateUser(String username, String password) {
        return getUserRole(username, password) != null;
    }

    /**
     * Authenticate and return role ("ADMIN" or "STAFF").
     * Returns null if credentials are wrong or user is inactive.
     *
     * ⚠️ Make sure your users table has a 'role' column:
     *   ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'STAFF';
     *   UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
     */
    public static String getUserRole(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                updateLastLogin(username);
                System.out.println("✓ User authenticated: " + username + " [" + role + "]");
                return role != null ? role.toUpperCase() : "STAFF";
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Authentication failed");
            e.printStackTrace();
        }
        return null;
    }

    private static void updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logAction(String reservationNo, String action, String performedBy, String details) {
        String sql = "INSERT INTO audit_log (reservation_no, action, performed_by, action_details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservationNo);
            pstmt.setString(2, action);
            pstmt.setString(3, performedBy);
            pstmt.setString(4, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("WARNING: Failed to log action");
        }
    }

    public static void printDatabaseStats() {
        System.out.println("========================================");
        System.out.println("Database Statistics:");
        System.out.println("========================================");
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM reservations")) {
                if (rs.next()) System.out.println("Total Reservations: " + rs.getInt(1));
            }
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) System.out.println("Total Users: " + rs.getInt(1));
            }
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM audit_log")) {
                if (rs.next()) System.out.println("Total Audit Logs: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("========================================");
    }
}