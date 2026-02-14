package resort;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import resort.CustomComponents.*;
import java.awt.print.PrinterException;

public class OceanResortSystemMain extends JFrame {

    private HashMap<String, Reservation> reservations = new HashMap<>();
    private JPanel contentArea;
    private CardLayout cardLayout;
    private DefaultTableModel tableModel;
    private JTable mainTable;
    private JLabel statusBar;
    private DashboardPanel dashboardPanel;
    
    public OceanResortSystemMain() {
        loadReservationsFromFile();
        showLoginUI();
    }
    
    public void showLoginUI() {
        dispose();
        new LoginPage(this);
    }
    
    private void updateStatus(String message, boolean isError) {
        if(statusBar != null) {
            statusBar.setText("  " + message);
            statusBar.setForeground(isError ? Constants.DANGER_COLOR : new Color(0, 150, 0));
            javax.swing.Timer timer = new javax.swing.Timer(4000, e -> statusBar.setText("  Ready"));
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void showDashboard() {
        setTitle("Ocean Resort Management System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel container = new JPanel(new BorderLayout());
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Constants.DARK_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        
        JLabel brand = new JLabel("🌊 OCEAN RESORT", SwingConstants.CENTER);
        brand.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        brand.setForeground(Color.WHITE);
        brand.setBorder(new EmptyBorder(30, 20, 50, 20));
        sidebar.add(brand);

        String[][] menu = {
            {"Dashboard", "📊"}, 
            {"New Reservation", "➕"}, 
            {"All Bookings", "📅"}, 
            {"Billing", "💳"}, 
            {"Reports", "📈"},
            {"Help", "❓"}, 
            {"Logout", "🚪"}
        };
        
        for (String[] item : menu) {
            MenuButton btn = new MenuButton(item[0], item[1]);
            btn.setMaximumSize(new Dimension(250, 60));
            btn.addActionListener(e -> navigate(item[0]));
            sidebar.add(btn);
        }

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        
        // Create panels
        dashboardPanel = new DashboardPanel(reservations, e -> navigate(e.getActionCommand()));
        contentArea.add(dashboardPanel, "Dashboard");
        contentArea.add(new ReservationPanel(reservations, this::saveReservationsToFile, this::updateStatus), "New Reservation");
        contentArea.add(createTablePanel(), "All Bookings");
        contentArea.add(createBillingPanel(), "Billing");
        contentArea.add(new ReportsPanel(reservations), "Reports");
        contentArea.add(new HelpPanel(), "Help");

        container.add(sidebar, BorderLayout.WEST);
        container.add(contentArea, BorderLayout.CENTER);
        
        // Add status bar
        statusBar = new JLabel("  Ready");
        statusBar.setFont(Constants.PLAIN_FONT);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(250, 250, 250));
        container.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(container);
        dashboardPanel.updateStats();
        revalidate();
        setVisible(true);
    }

    private void navigate(String screen) {
        if(screen.equals("Logout")) {
            showLoginUI();
        } else {
            if(screen.equals("Dashboard")) dashboardPanel.updateStats();
            if(screen.equals("All Bookings")) refreshTable();
            cardLayout.show(contentArea, screen);
        }
    }

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(Constants.LIGHT_BG); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Guest Name", "Room", "Check-In", "Check-Out", "Nights", "Status", "Cost (LKR)"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        mainTable = new JTable(tableModel);
        mainTable.setRowHeight(35);
        mainTable.setFont(Constants.PLAIN_FONT);
        mainTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        mainTable.setRowSorter(sorter);
        
        ModernTextField searchField = new ModernTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if(text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                            String searchLower = text.toLowerCase();
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                if (entry.getStringValue(i).toLowerCase().contains(searchLower)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout()); 
        topPanel.setBackground(Constants.LIGHT_BG);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Constants.LIGHT_BG);
        searchPanel.add(new JLabel("🔍 Search: ")); 
        searchPanel.add(searchField);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Constants.LIGHT_BG);
        
        ActionButton refreshBtn = new ActionButton("REFRESH", Constants.PRIMARY_COLOR);
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.addActionListener(e -> refreshTable());
        
        ActionButton deleteBtn = new ActionButton("DELETE", Constants.DANGER_COLOR);
        deleteBtn.setPreferredSize(new Dimension(120, 35));
        deleteBtn.addActionListener(e -> {
            int selectedRow = mainTable.getSelectedRow();
            if(selectedRow == -1) {
                updateStatus("Please select a reservation to delete", true);
                return;
            }
            
            int modelRow = mainTable.convertRowIndexToModel(selectedRow);
            String resId = (String) tableModel.getValueAt(modelRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete reservation " + resId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if(confirm == JOptionPane.YES_OPTION) {
                reservations.remove(resId);
                saveReservationsToFile();
                refreshTable();
                updateStatus("✓ Reservation " + resId + " deleted successfully", false);
            }
        });
        
        ActionButton viewDetailsBtn = new ActionButton("VIEW DETAILS", Constants.ACCENT_COLOR);
        viewDetailsBtn.setPreferredSize(new Dimension(140, 35));
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = mainTable.getSelectedRow();
            if(selectedRow == -1) {
                updateStatus("Please select a reservation to view", true);
                return;
            }
            
            int modelRow = mainTable.convertRowIndexToModel(selectedRow);
            String resId = (String) tableModel.getValueAt(modelRow, 0);
            Reservation r = reservations.get(resId);
            
            if(r != null) {
                showReservationDetails(r);
            }
        });
        
        ActionButton checkOutBtn = new ActionButton("CHECK OUT", Constants.WARNING_COLOR);
        checkOutBtn.setPreferredSize(new Dimension(130, 35));
        checkOutBtn.addActionListener(e -> {
            int selectedRow = mainTable.getSelectedRow();
            if(selectedRow == -1) {
                updateStatus("Please select a reservation to check out", true);
                return;
            }
            
            int modelRow = mainTable.convertRowIndexToModel(selectedRow);
            String resId = (String) tableModel.getValueAt(modelRow, 0);
            Reservation r = reservations.get(resId);
            
            if(r != null) {
                String status = r.getStatus();
                if(status.equals("Checked-Out")) {
                    updateStatus("This reservation is already checked out", true);
                    return;
                }
                if(status.equals("Cancelled")) {
                    updateStatus("Cannot check out a cancelled reservation", true);
                    return;
                }
                if(status.equals("Upcoming")) {
                    updateStatus("Cannot check out an upcoming reservation", true);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Check out guest: " + r.getName() + "?",
                    "Confirm Check-Out",
                    JOptionPane.YES_NO_OPTION
                );
                
                if(confirm == JOptionPane.YES_OPTION) {
                    r.setStatusManual("Checked-Out");
                    saveReservationsToFile();
                    refreshTable();
                    updateStatus("✓ Guest " + r.getName() + " checked out successfully", false);
                }
            }
        });
        
        ActionButton cancelBtn = new ActionButton("CANCEL", Constants.DANGER_COLOR);
        cancelBtn.setPreferredSize(new Dimension(120, 35));
        cancelBtn.addActionListener(e -> {
            int selectedRow = mainTable.getSelectedRow();
            if(selectedRow == -1) {
                updateStatus("Please select a reservation to cancel", true);
                return;
            }
            
            int modelRow = mainTable.convertRowIndexToModel(selectedRow);
            String resId = (String) tableModel.getValueAt(modelRow, 0);
            Reservation r = reservations.get(resId);
            
            if(r != null) {
                String status = r.getStatus();
                if(status.equals("Cancelled")) {
                    updateStatus("This reservation is already cancelled", true);
                    return;
                }
                if(status.equals("Checked-Out")) {
                    updateStatus("Cannot cancel a checked-out reservation", true);
                    return;
                }
                if(status.equals("Completed")) {
                    updateStatus("Cannot cancel a completed reservation", true);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Cancel reservation for " + r.getName() + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if(confirm == JOptionPane.YES_OPTION) {
                    r.setStatusManual("Cancelled");
                    saveReservationsToFile();
                    refreshTable();
                    updateStatus("✓ Reservation " + resId + " cancelled", false);
                }
            }
        });
        
        actionPanel.add(viewDetailsBtn);
        actionPanel.add(checkOutBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        p.add(topPanel, BorderLayout.NORTH); 
        p.add(new JScrollPane(mainTable), BorderLayout.CENTER);
        
        return p;
    }
    
    private void showReservationDetails(Reservation r) {
        JDialog dialog = new JDialog(this, "Reservation Details", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        String statusColor = "#00C896";
        String status = r.getStatus();
        if(status.equals("Upcoming")) {
            statusColor = "#9B59B6";
        } else if(status.equals("Completed")) {
            statusColor = "#3498DB";
        } else if(status.equals("Checked-Out")) {
            statusColor = "#2ECC71";
        } else if(status.equals("Cancelled")) {
            statusColor = "#E74C3C";
        }
        
        String details = "<html><body style='font-family:sans-serif; padding:10px;'>"
                + "<h2 style='color:#0066CC;'>Reservation Details</h2><hr>"
                + "<table style='width:100%; margin-top:10px;'>"
                + "<tr><td><b>Reservation ID:</b></td><td>" + r.getResNo() + "</td></tr>"
                + "<tr><td><b>Guest Name:</b></td><td>" + r.getName() + "</td></tr>"
                + "<tr><td><b>Address:</b></td><td>" + r.getAddress() + "</td></tr>"
                + "<tr><td><b>Contact:</b></td><td>" + r.getContact() + "</td></tr>"
                + "<tr><td><b>Room Type:</b></td><td>" + r.getRoomType() + "</td></tr>"
                + "<tr><td><b>Check-In:</b></td><td>" + r.getCheckIn().format(formatter) + "</td></tr>"
                + "<tr><td><b>Check-Out:</b></td><td>" + r.getCheckOut().format(formatter) + "</td></tr>"
                + "<tr><td><b>Total Nights:</b></td><td>" + r.getNights() + "</td></tr>"
                + "<tr><td><b>Status:</b></td><td><span style='color:" + statusColor + "; font-weight:bold;'>" + r.getStatus() + "</span></td></tr>"
                + "</table><hr>"
                + "<h3 style='color:#00C896;'>Total Cost: " + String.format("%,d", r.getTotalCost()) + " LKR</h3>"
                + "</body></html>";
        
        JEditorPane detailsPane = new JEditorPane("text/html", details);
        detailsPane.setEditable(false);
        detailsPane.setBackground(Color.WHITE);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        if(status.equals("Active")) {
            ActionButton checkOutBtn = new ActionButton("CHECK OUT", Constants.WARNING_COLOR);
            checkOutBtn.addActionListener(e -> {
                r.setStatusManual("Checked-Out");
                saveReservationsToFile();
                refreshTable();
                dialog.dispose();
                updateStatus("✓ Guest " + r.getName() + " checked out successfully", false);
            });
            btnPanel.add(checkOutBtn);
            
            ActionButton cancelBtn = new ActionButton("CANCEL", Constants.DANGER_COLOR);
            cancelBtn.addActionListener(e -> {
                r.setStatusManual("Cancelled");
                saveReservationsToFile();
                refreshTable();
                dialog.dispose();
                updateStatus("✓ Reservation cancelled", false);
            });
            btnPanel.add(cancelBtn);
        } else if(status.equals("Upcoming")) {
            ActionButton cancelBtn = new ActionButton("CANCEL", Constants.DANGER_COLOR);
            cancelBtn.addActionListener(e -> {
                r.setStatusManual("Cancelled");
                saveReservationsToFile();
                refreshTable();
                dialog.dispose();
                updateStatus("✓ Reservation cancelled", false);
            });
            btnPanel.add(cancelBtn);
        }
        
        ActionButton closeBtn = new ActionButton("CLOSE", Color.GRAY);
        closeBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(closeBtn);
        
        panel.add(detailsPane);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createBillingPanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(Constants.LIGHT_BG); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        ModernTextField search = new ModernTextField();
        search.setPreferredSize(new Dimension(250, 35));
        
        JEditorPane invoice = new JEditorPane("text/html", ""); 
        invoice.setEditable(false);
        invoice.setBackground(Color.WHITE);
        
        ActionButton gen = new ActionButton("GENERATE INVOICE", Constants.PRIMARY_COLOR);
        ActionButton print = new ActionButton("PRINT", Constants.ACCENT_COLOR);
        print.setEnabled(false);

        gen.addActionListener(e -> {
            String id = search.getText().trim();
            if(id.isEmpty()) {
                updateStatus("Please enter a Reservation ID", true);
                return;
            }
            
            Reservation r = reservations.get(id);
            if(r != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                long nights = r.getNights();
                
                String content = "<html><body style='font-family:sans-serif; padding:20px;'>"
                        + "<div style='text-align:center;'>"
                        + "<h1 style='color:#0066CC; margin:0;'>🌊 OCEAN RESORT</h1>"
                        + "<p style='color:#666;'>Luxury Beach Resort & Spa</p>"
                        + "<hr style='border:1px solid #ddd;'>"
                        + "</div>"
                        + "<h2 style='color:#333;'>Guest Invoice</h2>"
                        + "<table style='width:100%; margin-top:20px; border-collapse:collapse;'>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Reservation ID:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getResNo() + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Guest Name:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getName() + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Contact:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getContact() + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Room Type:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getRoomType() + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Check-In:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getCheckIn().format(formatter) + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Check-Out:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getCheckOut().format(formatter) + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Number of Nights:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + nights + "</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Rate per Night:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + String.format("%,d", r.getTotalCost()/nights) + " LKR</td></tr>"
                        + "<tr><td style='padding:8px; border-bottom:1px solid #eee;'><b>Status:</b></td><td style='padding:8px; border-bottom:1px solid #eee;'>" + r.getStatus() + "</td></tr>"
                        + "</table>"
                        + "<div style='margin-top:30px; background:#f5f5f5; padding:15px; border-left:4px solid #00C896;'>"
                        + "<h2 style='margin:0; color:#00C896;'>Total Amount: " + String.format("%,d", r.getTotalCost()) + " LKR</h2>"
                        + "</div>"
                        + "<p style='margin-top:30px; color:#666; font-size:12px; text-align:center;'>Thank you for choosing Ocean Resort!<br>Contact: +94 11 234 5678 | Email: info@oceanresort.lk</p>"
                        + "</body></html>";
                invoice.setText(content);
                print.setEnabled(true);
                updateStatus("✓ Invoice generated for " + r.getName(), false);
            } else {
                updateStatus("No reservation found for ID: " + id, true);
                invoice.setText("");
                print.setEnabled(false);
            }
        });

        print.addActionListener(e -> { 
            try { 
                invoice.print();
                updateStatus("✓ Invoice sent to printer", false);
            } catch (PrinterException ex) { 
                updateStatus("Print error: " + ex.getMessage(), true);
            } 
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); 
        topPanel.setBackground(Constants.LIGHT_BG);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        topPanel.add(new JLabel("Enter Reservation ID: ")); 
        topPanel.add(search); 
        topPanel.add(gen); 
        topPanel.add(print);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>💳 Billing & Invoice</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.LIGHT_BG);
        headerPanel.add(header, BorderLayout.WEST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.LIGHT_BG);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(invoice), BorderLayout.CENTER);
        
        p.add(headerPanel, BorderLayout.NORTH);
        p.add(mainPanel, BorderLayout.CENTER);
        return p;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Reservation r : reservations.values()) {
            tableModel.addRow(new Object[]{
                r.getResNo(), 
                r.getName(), 
                r.getRoomType(), 
                r.getCheckIn(), 
                r.getCheckOut(), 
                r.getNights(),
                r.getStatus(),
                String.format("%,d", r.getTotalCost())
            });
        }
    }

    private void saveReservationsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Constants.FILE_NAME))) {
            oos.writeObject(reservations);
        } catch (IOException e) { 
            updateStatus("Error saving data: " + e.getMessage(), true);
        }
        dashboardPanel.updateStats();
    }

    @SuppressWarnings("unchecked")
    private void loadReservationsFromFile() {
        File file = new File(Constants.FILE_NAME);
        if(!file.exists()) {
            reservations = new HashMap<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constants.FILE_NAME))) {
            reservations = (HashMap<String, Reservation>) ois.readObject();
        } catch (Exception e) { 
            reservations = new HashMap<>();
        }
    }

    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(() -> new OceanResortSystemMain());
    }
}
