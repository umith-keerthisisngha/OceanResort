package resort;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class OceanResortSystem extends JFrame {

    /* ===================== DATA MODEL ===================== */
    static class Reservation implements Serializable {
        private static final long serialVersionUID = 1L;
        private String resNo, name, address, contact, roomType;
        private LocalDate checkIn, checkOut;
        private long totalCost;
        private String status; // "Active", "Checked-Out", "Cancelled"

        Reservation(String resNo, String name, String address, String contact,
                    String roomType, LocalDate checkIn, LocalDate checkOut) {
            this.resNo = resNo;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.roomType = roomType;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.status = "Active";
            calculateCost();
        }

        void calculateCost() {
            long nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
            int rate = roomType.equals("Single") ? 8000 : roomType.equals("Double") ? 12000 : 20000;
            this.totalCost = nights * rate;
        }

        public String getResNo() { return resNo; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getContact() { return contact; }
        public String getRoomType() { return roomType; }
        public LocalDate getCheckIn() { return checkIn; }
        public LocalDate getCheckOut() { return checkOut; }
        public long getTotalCost() { return totalCost; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public long getNights() {
            return Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
        }
    }

    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static final String FILE_NAME = "reservations.dat";

    /* ===================== THEME COLORS ===================== */
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color DARK_BG = new Color(30, 30, 40);
    private final Color LIGHT_BG = new Color(245, 247, 250);
    private final Color ACCENT_COLOR = new Color(0, 200, 150);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private final Font PLAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JPanel contentArea;
    private CardLayout cardLayout;
    private DefaultTableModel tableModel;
    private JLabel totalRevenueLbl, totalBookingsLbl, activeBookingsLbl;
    private JTable mainTable;
    private JLabel statusBar;

    public OceanResortSystem() {
        loadReservationsFromFile();
        showLoginUI();
    }
    
    private void updateStatus(String message, boolean isError) {
        if(statusBar != null) {
            statusBar.setText("  " + message);
            statusBar.setForeground(isError ? DANGER_COLOR : new Color(0, 150, 0));
            javax.swing.Timer timer = new javax.swing.Timer(4000, e -> statusBar.setText("  Ready"));
            timer.setRepeats(false);
            timer.start();
        }
    }

    /* ===================== CUSTOM COMPONENTS ===================== */
    class MenuButton extends JButton {
        public MenuButton(String text, String icon) {
            super(icon + "    " + text);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));
            setForeground(new Color(200, 200, 200));
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { 
                    setForeground(Color.WHITE); 
                    setBackground(new Color(50, 50, 60)); 
                    setOpaque(true); 
                }
                public void mouseExited(MouseEvent e) { 
                    setForeground(new Color(200, 200, 200)); 
                    setOpaque(false); 
                }
            });
        }
    }

    class ActionButton extends JButton {
        public ActionButton(String text, Color bg) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setBackground(bg);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(150, 40));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBackground(bg.darker());
                }
                public void mouseExited(MouseEvent e) {
                    setBackground(bg);
                }
            });
        }
    }

    class ModernTextField extends JTextField {
        public ModernTextField() {
            setFont(PLAIN_FONT);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }
    }

    /* ===================== UI SCREENS ===================== */
    private void showLoginUI() {
        setTitle("Ocean Resort | Staff Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        JPanel brandPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(0, 50, 100)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        brandPanel.setLayout(new GridBagLayout());
        JLabel brandLabel = new JLabel("<html><center><h1 style='color:white; font-size:40px;'>🌊 OCEAN<br>RESORT</h1><p style='color:#ccc; font-size:16px;'>Management System v3.5</p></center></html>");
        brandPanel.add(brandLabel);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.gridx = 0; 
        gbc.gridy = 0;

        JLabel loginTitle = new JLabel("Staff Portal");
        loginTitle.setFont(TITLE_FONT); 
        loginTitle.setForeground(PRIMARY_COLOR);
        
        ModernTextField userField = new ModernTextField();
        userField.setPreferredSize(new Dimension(250, 35));
        JPasswordField passField = new JPasswordField();
        passField.setBorder(userField.getBorder());
        passField.setPreferredSize(new Dimension(250, 35));
        
        ActionButton loginBtn = new ActionButton("LOGIN", PRIMARY_COLOR);
        loginBtn.setPreferredSize(new Dimension(250, 45));

        loginPanel.add(loginTitle, gbc);
        gbc.gridy++; 
        loginPanel.add(new JLabel("Username"), gbc);
        gbc.gridy++; 
        loginPanel.add(userField, gbc);
        gbc.gridy++; 
        loginPanel.add(new JLabel("Password"), gbc);
        gbc.gridy++; 
        loginPanel.add(passField, gbc);
        gbc.gridy++; 
        gbc.insets = new Insets(30, 30, 10, 30); 
        loginPanel.add(loginBtn, gbc);
        
        JLabel hintLabel = new JLabel("<html><center><small>Default: admin / 123</small></center></html>");
        hintLabel.setForeground(Color.GRAY);
        gbc.gridy++;
        gbc.insets = new Insets(5, 30, 10, 30);
        loginPanel.add(hintLabel, gbc);

        // Enter key support
        ActionListener loginAction = e -> {
            if (userField.getText().equals("admin") && new String(passField.getPassword()).equals("123")) {
                showDashboard();
            } else {
                userField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                passField.setBorder(userField.getBorder());
                Timer timer = new Timer(2000, evt -> {
                    userField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                    passField.setBorder(userField.getBorder());
                });
                timer.setRepeats(false);
                timer.start();
            }
        };
        
        loginBtn.addActionListener(loginAction);
        userField.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        mainPanel.add(brandPanel); 
        mainPanel.add(loginPanel);
        setContentPane(mainPanel); 
        setVisible(true);
        
        userField.requestFocusInWindow();
    }

    private void showDashboard() {
        setTitle("Ocean Resort Management System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        JPanel container = new JPanel(new BorderLayout());
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DARK_BG);
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
        contentArea.add(createHomePanel(), "Dashboard");
        contentArea.add(createReservationPanel(), "New Reservation");
        contentArea.add(createTablePanel(), "All Bookings");
        contentArea.add(createBillingPanel(), "Billing");
        contentArea.add(createReportsPanel(), "Reports");
        contentArea.add(createHelpPanel(), "Help");

        container.add(sidebar, BorderLayout.WEST);
        container.add(contentArea, BorderLayout.CENTER);
        
        // Add status bar
        statusBar = new JLabel("  Ready");
        statusBar.setFont(PLAIN_FONT);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(250, 250, 250));
        container.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(container);
        updateDashboardStats();
        revalidate();
    }

    private void navigate(String screen) {
        if(screen.equals("Logout")) {
            showLoginUI();
        } else {
            if(screen.equals("Dashboard")) updateDashboardStats();
            if(screen.equals("All Bookings")) refreshTable();
            cardLayout.show(contentArea, screen);
        }
    }

    /* ===================== PANELS ===================== */
    
    private JPanel createHomePanel() {
        JPanel p = new JPanel(null); 
        p.setBackground(LIGHT_BG);
        
        JLabel title = new JLabel("Performance Overview");
        title.setFont(TITLE_FONT); 
        title.setBounds(40, 40, 400, 40);
        p.add(title);

        totalBookingsLbl = createStatCard(p, "Total Bookings", "0", new Color(46, 204, 113), 40, 100);
        activeBookingsLbl = createStatCard(p, "Active Bookings", "0", new Color(52, 152, 219), 320, 100);
        totalRevenueLbl = createStatCard(p, "Total Revenue (LKR)", "0", new Color(155, 89, 182), 600, 100);

        // Quick actions panel
        JPanel quickActions = new JPanel();
        quickActions.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        quickActions.setBackground(Color.WHITE);
        quickActions.setBounds(40, 260, 800, 100);
        quickActions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        
        ActionButton newRes = new ActionButton("New Reservation", ACCENT_COLOR);
        newRes.addActionListener(e -> navigate("New Reservation"));
        ActionButton viewAll = new ActionButton("View Bookings", PRIMARY_COLOR);
        viewAll.addActionListener(e -> navigate("All Bookings"));
        
        quickActions.add(newRes);
        quickActions.add(viewAll);
        p.add(quickActions);

        return p;
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color, int x, int y) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBounds(x, y, 260, 120); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createMatteBorder(5, 0, 0, 0, color)
        ));
        
        JLabel t = new JLabel(title); 
        t.setFont(new Font("SansSerif", Font.BOLD, 13)); 
        t.setForeground(Color.GRAY);
        t.setBorder(new EmptyBorder(15, 15, 5, 15));
        
        JLabel v = new JLabel(value); 
        v.setFont(new Font("SansSerif", Font.BOLD, 28));
        v.setForeground(color);
        v.setBorder(new EmptyBorder(0, 15, 15, 15));
        
        card.add(t, BorderLayout.NORTH); 
        card.add(v, BorderLayout.CENTER);
        parent.add(card); 
        return v;
    }

    private JPanel createReservationPanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(LIGHT_BG); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JPanel form = new JPanel(new GridLayout(8, 2, 20, 20)); 
        form.setBackground(Color.WHITE); 
        form.setBorder(new EmptyBorder(30, 30, 30, 30));

        ModernTextField resNo = new ModernTextField();
        ModernTextField name = new ModernTextField();
        ModernTextField addr = new ModernTextField();
        ModernTextField cont = new ModernTextField();
        
        // Reset border on focus
        FocusAdapter borderReset = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }
        };
        resNo.addFocusListener(borderReset);
        name.addFocusListener(borderReset);
        addr.addFocusListener(borderReset);
        cont.addFocusListener(borderReset);
        
        JComboBox<String> room = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        room.setFont(PLAIN_FONT);
        
        JSpinner checkIn = new JSpinner(new SpinnerDateModel());
        JSpinner checkOut = new JSpinner(new SpinnerDateModel());
        checkIn.setEditor(new JSpinner.DateEditor(checkIn, "yyyy-MM-dd"));
        checkOut.setEditor(new JSpinner.DateEditor(checkOut, "yyyy-MM-dd"));
        
        // Set default checkout to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        checkOut.setValue(cal.getTime());

        JLabel costPreview = new JLabel("Cost will be calculated automatically");
        costPreview.setFont(new Font("SansSerif", Font.ITALIC, 12));
        costPreview.setForeground(Color.GRAY);

        form.add(new JLabel("Reservation ID:")); form.add(resNo);
        form.add(new JLabel("Guest Name:")); form.add(name);
        form.add(new JLabel("Address:")); form.add(addr);
        form.add(new JLabel("Contact No:")); form.add(cont);
        form.add(new JLabel("Room Type:")); form.add(room);
        form.add(new JLabel("Check-In Date:")); form.add(checkIn);
        form.add(new JLabel("Check-Out Date:")); form.add(checkOut);
        form.add(new JLabel("")); form.add(costPreview);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(LIGHT_BG);
        
        ActionButton saveBtn = new ActionButton("SAVE RESERVATION", ACCENT_COLOR);
        ActionButton clearBtn = new ActionButton("CLEAR FORM", Color.GRAY);
        
        clearBtn.addActionListener(e -> {
            resNo.setText("");
            name.setText("");
            addr.setText("");
            cont.setText("");
            room.setSelectedIndex(0);
            checkIn.setValue(new Date());
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            checkOut.setValue(c.getTime());
        });
        
        saveBtn.addActionListener(e -> {
            // Validation
            if(resNo.getText().trim().isEmpty()) {
                updateStatus("Reservation ID is required!", true);
                resNo.requestFocus();
                resNo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            if(name.getText().trim().isEmpty()) {
                updateStatus("Guest Name is required!", true);
                name.requestFocus();
                name.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            if(cont.getText().trim().isEmpty()) {
                updateStatus("Contact Number is required!", true);
                cont.requestFocus();
                cont.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            
            // Check for duplicate ID
            if(reservations.containsKey(resNo.getText().trim())) {
                updateStatus("Reservation ID already exists! Please use a unique ID.", true);
                resNo.requestFocus();
                resNo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            
            LocalDate in = ((Date)checkIn.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate out = ((Date)checkOut.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Validate dates
            if(!out.isAfter(in)) {
                updateStatus("Check-out date must be after check-in date!", true);
                return;
            }
            
            Reservation r = new Reservation(
                resNo.getText().trim(), 
                name.getText().trim(), 
                addr.getText().trim(), 
                cont.getText().trim(), 
                room.getSelectedItem().toString(), 
                in, 
                out
            );
            
            reservations.put(r.getResNo(), r);
            saveReservationsToFile();
            
            updateStatus("✓ Reservation Saved! ID: " + r.getResNo() + " | Guest: " + r.getName() + " | Cost: " + String.format("%,d", r.getTotalCost()) + " LKR", false);
            
            // Clear form
            clearBtn.doClick();
        });

        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);

        JLabel header = new JLabel("<html><h2 style='color:#333'>📝 New Guest Registration</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        p.add(header, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER); 
        p.add(buttonPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(LIGHT_BG); 
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
        mainTable.setFont(PLAIN_FONT);
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
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout()); 
        topPanel.setBackground(LIGHT_BG);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_BG);
        searchPanel.add(new JLabel("🔍 Search: ")); 
        searchPanel.add(searchField);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(LIGHT_BG);
        
        ActionButton refreshBtn = new ActionButton("REFRESH", PRIMARY_COLOR);
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.addActionListener(e -> refreshTable());
        
        ActionButton deleteBtn = new ActionButton("DELETE", DANGER_COLOR);
        deleteBtn.setPreferredSize(new Dimension(120, 35));
        deleteBtn.addActionListener(e -> {
            int selectedRow = mainTable.getSelectedRow();
            if(selectedRow == -1) {
                updateStatus("Please select a reservation to delete", true);
                return;
            }
            
            int modelRow = mainTable.convertRowIndexToModel(selectedRow);
            String resId = (String) tableModel.getValueAt(modelRow, 0);
            
            // Delete without confirmation
            reservations.remove(resId);
            saveReservationsToFile();
            refreshTable();
            updateStatus("✓ Reservation " + resId + " deleted successfully", false);
        });
        
        ActionButton viewDetailsBtn = new ActionButton("VIEW DETAILS", ACCENT_COLOR);
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
        
        actionPanel.add(viewDetailsBtn);
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
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
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
                + "<tr><td><b>Status:</b></td><td>" + r.getStatus() + "</td></tr>"
                + "</table><hr>"
                + "<h3 style='color:#00C896;'>Total Cost: " + String.format("%,d", r.getTotalCost()) + " LKR</h3>"
                + "</body></html>";
        
        JEditorPane detailsPane = new JEditorPane("text/html", details);
        detailsPane.setEditable(false);
        detailsPane.setBackground(Color.WHITE);
        
        ActionButton closeBtn = new ActionButton("CLOSE", Color.GRAY);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        
        panel.add(detailsPane);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createBillingPanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(LIGHT_BG); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        ModernTextField search = new ModernTextField();
        search.setPreferredSize(new Dimension(250, 35));
        
        JEditorPane invoice = new JEditorPane("text/html", ""); 
        invoice.setEditable(false);
        invoice.setBackground(Color.WHITE);
        
        ActionButton gen = new ActionButton("GENERATE INVOICE", PRIMARY_COLOR);
        ActionButton print = new ActionButton("PRINT", ACCENT_COLOR);
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
        topPanel.setBackground(LIGHT_BG);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        topPanel.add(new JLabel("Enter Reservation ID: ")); 
        topPanel.add(search); 
        topPanel.add(gen); 
        topPanel.add(print);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>💳 Billing & Invoice</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_BG);
        headerPanel.add(header, BorderLayout.WEST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(invoice), BorderLayout.CENTER);
        
        p.add(headerPanel, BorderLayout.NORTH);
        p.add(mainPanel, BorderLayout.CENTER);
        return p;
    }
    
    private JPanel createReportsPanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(LIGHT_BG); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JEditorPane reportArea = new JEditorPane("text/html", ""); 
        reportArea.setEditable(false);
        reportArea.setBackground(Color.WHITE);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.setBackground(LIGHT_BG);
        controlPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        ActionButton summaryBtn = new ActionButton("SUMMARY REPORT", PRIMARY_COLOR);
        ActionButton roomBtn = new ActionButton("ROOM ANALYSIS", ACCENT_COLOR);
        
        summaryBtn.addActionListener(e -> {
            long totalRev = reservations.values().stream().mapToLong(Reservation::getTotalCost).sum();
            long activeCount = reservations.values().stream().filter(r -> r.getStatus().equals("Active")).count();
            
            Map<String, Long> roomCounts = new HashMap<>();
            reservations.values().forEach(r -> 
                roomCounts.put(r.getRoomType(), roomCounts.getOrDefault(r.getRoomType(), 0L) + 1)
            );
            
            String report = "<html><body style='font-family:sans-serif; padding:20px;'>"
                    + "<h1 style='color:#0066CC;'>📊 Summary Report</h1><hr>"
                    + "<h3>Overall Statistics</h3>"
                    + "<table style='width:100%; border-collapse:collapse;'>"
                    + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Total Reservations:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + reservations.size() + "</td></tr>"
                    + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Active Reservations:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + activeCount + "</td></tr>"
                    + "<tr><td style='padding:10px; border-bottom:1px solid #ddd;'><b>Total Revenue:</b></td><td style='padding:10px; border-bottom:1px solid #ddd;'>" + String.format("%,d", totalRev) + " LKR</td></tr>"
                    + "</table>"
                    + "<h3 style='margin-top:30px;'>Bookings by Room Type</h3>"
                    + "<ul>";
            
            for(Map.Entry<String, Long> entry : roomCounts.entrySet()) {
                report += "<li><b>" + entry.getKey() + ":</b> " + entry.getValue() + " bookings</li>";
            }
            
            report += "</ul></body></html>";
            reportArea.setText(report);
        });
        
        roomBtn.addActionListener(e -> {
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
        });
        
        controlPanel.add(summaryBtn);
        controlPanel.add(roomBtn);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>📈 Reports & Analytics</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        p.add(header, BorderLayout.NORTH);
        p.add(controlPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(LIGHT_BG);
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        
        p.add(centerPanel, BorderLayout.CENTER);
        
        // Auto-generate summary on load
        summaryBtn.doClick();
        
        return p;
    }

    private JPanel createHelpPanel() {
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(Color.WHITE); 
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
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
                + "<li>Use the search box to filter by any field (ID, name, room type, etc.)</li>"
                + "<li>Select a row and click 'VIEW DETAILS' for complete reservation information</li>"
                + "<li>Click 'DELETE' to remove a reservation (requires confirmation)</li>"
                + "<li>Use 'REFRESH' to update the table with latest data</li>"
                + "</ul>"
                + "<h3>4. Billing & Invoice</h3>"
                + "<ul>"
                + "<li>Enter a Reservation ID and click 'GENERATE INVOICE'</li>"
                + "<li>Review the detailed invoice showing all charges</li>"
                + "<li>Click 'PRINT' to create a hard copy for the guest</li>"
                + "</ul>"
                + "<h3>5. Reports</h3>"
                + "<ul>"
                + "<li><b>Summary Report:</b> Overall statistics and bookings by room type</li>"
                + "<li><b>Room Analysis:</b> Detailed breakdown of revenue and bookings per room type</li>"
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
                + "<h2 style='color:#00C896;'>📞 Support</h2>"
                + "<p><b>Email:</b> support@oceanresort.lk<br>"
                + "<b>Phone:</b> +94 11 234 5678<br>"
                + "<b>System Version:</b> 3.5</p>"
                + "<p style='margin-top:30px; padding:15px; background:#f0f8ff; border-left:4px solid #0066CC;'>"
                + "<b>💡 Tip:</b> All reservation data is automatically saved to disk. Your data is preserved even after closing the application."
                + "</p>"
                + "</body></html>";
        
        helpText.setText(helpHTML);
        
        JLabel header = new JLabel("<html><h2 style='color:#333'>❓ Help & Documentation</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        p.add(header, BorderLayout.NORTH);
        p.add(new JScrollPane(helpText), BorderLayout.CENTER);
        return p;
    }

    /* ===================== DATA HANDLING ===================== */
    private void updateDashboardStats() {
        totalBookingsLbl.setText(String.valueOf(reservations.size()));
        long activeCount = reservations.values().stream()
            .filter(r -> r.getStatus().equals("Active"))
            .count();
        activeBookingsLbl.setText(String.valueOf(activeCount));
        long total = reservations.values().stream().mapToLong(Reservation::getTotalCost).sum();
        totalRevenueLbl.setText(String.format("%,d", total));
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reservations);
        } catch (IOException e) { 
            updateStatus("Error saving data: " + e.getMessage(), true);
        }
        updateDashboardStats();
    }

    @SuppressWarnings("unchecked")
    private void loadReservationsFromFile() {
        File file = new File(FILE_NAME);
        if(!file.exists()) {
            reservations = new HashMap<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
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
        
        SwingUtilities.invokeLater(() -> new OceanResortSystem());
    }
}