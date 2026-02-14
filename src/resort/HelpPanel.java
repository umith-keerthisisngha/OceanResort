package resort;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HelpPanel extends JPanel {
    
    public HelpPanel() {
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout()); 
        setBackground(Color.WHITE); 
        setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JEditorPane helpText = new JEditorPane("text/html", ""); 
        helpText.setEditable(false);
        helpText.setBackground(Color.WHITE);
        
        String helpHTML = "<html><body style='font-family:sans-serif; padding:10px;'>"
                + "<h1 style='color:#0066CC;'>🌊 Ocean Resort Help Center</h1>"
                + "<p style='font-size:14px; color:#666;'>Welcome to the Ocean Resort Management System. This guide will help you navigate and use all features effectively.</p>"
                + "<hr>"
                + "<h2 style='color:#00C896;'>📘 Quick Start Guide</h2>"
                + "<h3>1. Dashboard</h3>"
                + "<ul>"
                + "<li>View real-time statistics including total bookings, active reservations, and revenue</li>"
                + "<li>Access quick actions to create new reservations or view all bookings</li>"
                + "</ul>"
                + "<h3>2. New Reservation</h3>"
                + "<ul>"
                + "<li><b>Required Fields:</b> Reservation ID, Guest Name, and Contact Number</li>"
                + "<li><b>Important:</b> Each Reservation ID must be unique</li>"
                + "<li>Select room type from: Single (8,000 LKR/night), Double (12,000 LKR/night), or Suite (20,000 LKR/night)</li>"
                + "<li>Choose check-in and check-out dates - cost is calculated automatically</li>"
                + "<li>Click 'SAVE RESERVATION' to confirm or 'CLEAR FORM' to reset</li>"
                + "</ul>"
                + "<h3>3. All Bookings</h3>"
                + "<ul>"
                + "<li>View all reservations in a sortable table</li>"
                + "<li>Use the search box to filter by any field (ID, name, room type, status, etc.)</li>"
                + "<li>Select a row and click 'VIEW DETAILS' for complete reservation information</li>"
                + "<li><b>CHECK OUT:</b> Mark a reservation as checked out when guest leaves</li>"
                + "<li><b>CANCEL:</b> Cancel an active reservation</li>"
                + "<li><b>DELETE:</b> Permanently remove a reservation (requires confirmation)</li>"
                + "<li>Use 'REFRESH' to update the table with latest data</li>"
                + "</ul>"
                + "<h3>4. Billing & Invoice</h3>"
                + "<ul>"
                + "<li>Enter a Reservation ID and click 'GENERATE INVOICE'</li>"
                + "<li>Review the detailed invoice showing all charges and status</li>"
                + "<li>Click 'PRINT' to create a hard copy for the guest</li>"
                + "</ul>"
                + "<h3>5. Reports</h3>"
                + "<ul>"
                + "<li><b>Summary Report:</b> Overall statistics and bookings by room type</li>"
                + "<li><b>Room Analysis:</b> Detailed breakdown of revenue and bookings per room type</li>"
                + "<li><b>Status Report:</b> Analysis of reservations by status (Active/Checked-Out/Cancelled)</li>"
                + "</ul>"
                + "<hr>"
                + "<h2 style='color:#00C896;'>💰 Room Rates</h2>"
                + "<table style='width:100%; border-collapse:collapse; margin-top:10px;'>"
                + "<tr style='background:#f5f5f5;'><th style='padding:10px; text-align:left;'>Room Type</th><th style='padding:10px; text-align:left;'>Rate per Night</th></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'>Single Room</td><td style='padding:10px; border-bottom:1px solid #ddd;'>8,000 LKR</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'>Double Room</td><td style='padding:10px; border-bottom:1px solid #ddd;'>12,000 LKR</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'>Suite</td><td style='padding:10px; border-bottom:1px solid #ddd;'>20,000 LKR</td></tr>"
                + "</table>"
                + "<hr>"
                + "<h2 style='color:#00C896;'>🔄 Reservation Status</h2>"
                + "<ul>"
                + "<li><b style='color:#9B59B6;'>Upcoming:</b> Check-in date is in the future (auto-detected)</li>"
                + "<li><b style='color:#00C896;'>Active:</b> Guest is currently staying (between check-in and check-out dates, auto-detected)</li>"
                + "<li><b style='color:#3498DB;'>Completed:</b> Check-out date has passed (auto-detected)</li>"
                + "<li><b style='color:#2ECC71;'>Checked-Out:</b> Guest has been manually checked out by staff</li>"
                + "<li><b style='color:#E74C3C;'>Cancelled:</b> Reservation has been cancelled</li>"
                + "</ul>"
                + "<p><b>Note:</b> Upcoming, Active, and Completed statuses are automatically determined based on today's date. Only Checked-Out and Cancelled are set manually.</p>"
                + "<hr>"
                + "<h2 style='color:#00C896;'>📞 Support</h2>"
                + "<p><b>Email:</b> support@oceanresort.lk<br>"
                + "<b>Phone:</b> +94 11 234 5678<br>"
                + "<b>System Version:</b> 4.0</p>"
                + "<p style='margin-top:30px; padding:15px; background:#f0f8ff; border-left:4px solid #0066CC;'>"
                + "<b>💡 Tip:</b> All reservation data is automatically saved to disk. Your data is preserved even after closing the application."
                + "</p>"
                + "</body></html>";
        
        helpText.setText(helpHTML);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>❓ Help & Documentation</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(helpText), BorderLayout.CENTER);
    }
}
