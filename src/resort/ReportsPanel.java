package resort;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import resort.CustomComponents.*;

public class ReportsPanel extends JPanel {
    
    private HashMap<String, Reservation> reservations;
    private JEditorPane reportArea;
    
    public ReportsPanel(HashMap<String, Reservation> reservations) {
        this.reservations = reservations;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout()); 
        setBackground(Constants.LIGHT_BG); 
        setBorder(new EmptyBorder(40, 40, 40, 40));
        
        reportArea = new JEditorPane("text/html", ""); 
        reportArea.setEditable(false);
        reportArea.setBackground(Color.WHITE);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.setBackground(Constants.LIGHT_BG);
        controlPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        ActionButton summaryBtn = new ActionButton("SUMMARY REPORT", Constants.PRIMARY_COLOR);
        ActionButton roomBtn = new ActionButton("ROOM ANALYSIS", Constants.ACCENT_COLOR);
        ActionButton statusBtn = new ActionButton("STATUS REPORT", Constants.WARNING_COLOR);
        
        summaryBtn.addActionListener(e -> generateSummaryReport());
        roomBtn.addActionListener(e -> generateRoomAnalysis());
        statusBtn.addActionListener(e -> generateStatusReport());
        
        controlPanel.add(summaryBtn);
        controlPanel.add(roomBtn);
        controlPanel.add(statusBtn);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>📈 Reports & Analytics</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Constants.LIGHT_BG);
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        
        add(header, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Auto-generate summary on load
        generateSummaryReport();
    }
    
    private void generateSummaryReport() {
        long totalRev = reservations.values().stream().mapToLong(Reservation::getTotalCost).sum();
        long upcomingCount = reservations.values().stream().filter(r -> r.getStatus().equals("Upcoming")).count();
        long activeCount = reservations.values().stream().filter(r -> r.getStatus().equals("Active")).count();
        long completedCount = reservations.values().stream().filter(r -> r.getStatus().equals("Completed")).count();
        long checkedOutCount = reservations.values().stream().filter(r -> r.getStatus().equals("Checked-Out")).count();
        long cancelledCount = reservations.values().stream().filter(r -> r.getStatus().equals("Cancelled")).count();
        
        Map<String, Long> roomCounts = new HashMap<>();
        reservations.values().forEach(r -> 
            roomCounts.put(r.getRoomType(), roomCounts.getOrDefault(r.getRoomType(), 0L) + 1)
        );
        
        String report = "<html><body style='font-family:sans-serif; padding:20px;'>"
                + "<h1 style='color:#0066CC;'>📊 Summary Report</h1><hr>"
                + "<h3>Overall Statistics</h3>"
                + "<table style='width:100%; border-collapse:collapse;'>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Total Reservations:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + reservations.size() + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Upcoming:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + upcomingCount + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Active (Currently Staying):</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + activeCount + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Completed:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + completedCount + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Checked-Out:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + checkedOutCount + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Cancelled:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + cancelledCount + "</td></tr>"
                + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Total Revenue:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + String.format("%,d", totalRev) + " LKR</td></tr>"
                + "</table>"
                + "<h3 style='margin-top:30px;'>Bookings by Room Type</h3>"
                + "<ul>";
        
        for(Map.Entry<String, Long> entry : roomCounts.entrySet()) {
            report += "<li><b>" + entry.getKey() + ":</b> " + entry.getValue() + " bookings</li>";
        }
        
        report += "</ul></body></html>";
        reportArea.setText(report);
    }
    
    private void generateRoomAnalysis() {
        Map<String, Long> roomRevenue = new HashMap<>();
        Map<String, Long> roomCount = new HashMap<>();
        
        reservations.values().forEach(r -> {
            String type = r.getRoomType();
            roomRevenue.put(type, roomRevenue.getOrDefault(type, 0L) + r.getTotalCost());
            roomCount.put(type, roomCount.getOrDefault(type, 0L) + 1);
        });
        
        String report = "<html><body style='font-family:sans-serif; padding:20px;'>"
                + "<h1 style='color:#0066CC;'>🏨 Room Type Analysis</h1><hr>"
                + "<table style='width:100%; border-collapse:collapse; margin-top:20px;'>"
                + "<tr style='background:#f5f5f5;'>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Room Type</th>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Bookings</th>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Revenue (LKR)</th>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Avg per Booking</th>"
                + "</tr>";
        
        for(String type : new String[]{"Single", "Double", "Suite"}) {
            long count = roomCount.getOrDefault(type, 0L);
            long revenue = roomRevenue.getOrDefault(type, 0L);
            long avg = count > 0 ? revenue / count : 0;
            
            report += "<tr>"
                    + "<td style='padding:10px; border-bottom:1px solid #eee;'><b>" + type + "</b></td>"
                    + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + count + "</td>"
                    + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", revenue) + "</td>"
                    + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", avg) + "</td>"
                    + "</tr>";
        }
        
        report += "</table></body></html>";
        reportArea.setText(report);
    }
    
    private void generateStatusReport() {
        long upcomingCount = reservations.values().stream().filter(r -> r.getStatus().equals("Upcoming")).count();
        long activeCount = reservations.values().stream().filter(r -> r.getStatus().equals("Active")).count();
        long completedCount = reservations.values().stream().filter(r -> r.getStatus().equals("Completed")).count();
        long checkedOutCount = reservations.values().stream().filter(r -> r.getStatus().equals("Checked-Out")).count();
        long cancelledCount = reservations.values().stream().filter(r -> r.getStatus().equals("Cancelled")).count();
        
        long upcomingRevenue = reservations.values().stream().filter(r -> r.getStatus().equals("Upcoming")).mapToLong(Reservation::getTotalCost).sum();
        long activeRevenue = reservations.values().stream().filter(r -> r.getStatus().equals("Active")).mapToLong(Reservation::getTotalCost).sum();
        long completedRevenue = reservations.values().stream().filter(r -> r.getStatus().equals("Completed")).mapToLong(Reservation::getTotalCost).sum();
        long checkedOutRevenue = reservations.values().stream().filter(r -> r.getStatus().equals("Checked-Out")).mapToLong(Reservation::getTotalCost).sum();
        long cancelledRevenue = reservations.values().stream().filter(r -> r.getStatus().equals("Cancelled")).mapToLong(Reservation::getTotalCost).sum();
        
        String report = "<html><body style='font-family:sans-serif; padding:20px;'>"
                + "<h1 style='color:#0066CC;'>📋 Status Report</h1><hr>"
                + "<table style='width:100%; border-collapse:collapse; margin-top:20px;'>"
                + "<tr style='background:#f5f5f5;'>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Status</th>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Count</th>"
                + "<th style='padding:10px; text-align:left; border-bottom:2px solid #ddd;'>Revenue (LKR)</th>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#9B59B6; font-weight:bold;'>● Upcoming</span></td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + upcomingCount + "</td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", upcomingRevenue) + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#00C896; font-weight:bold;'>● Active</span></td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + activeCount + "</td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", activeRevenue) + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#3498DB; font-weight:bold;'>● Completed</span></td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + completedCount + "</td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", completedRevenue) + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#2ECC71; font-weight:bold;'>● Checked-Out</span></td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + checkedOutCount + "</td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", checkedOutRevenue) + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#E74C3C; font-weight:bold;'>● Cancelled</span></td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + cancelledCount + "</td>"
                + "<td style='padding:10px; border-bottom:1px solid #eee;'>" + String.format("%,d", cancelledRevenue) + "</td>"
                + "</tr>"
                + "</table>"
                + "<div style='margin-top:30px; background:#f5f5f5; padding:15px; border-left:4px solid #0066CC;'>"
                + "<h3 style='margin:0;'>Total Revenue: " + String.format("%,d", upcomingRevenue + activeRevenue + completedRevenue + checkedOutRevenue + cancelledRevenue) + " LKR</h3>"
                + "</div>"
                + "</body></html>";
        reportArea.setText(report);
    }
}
