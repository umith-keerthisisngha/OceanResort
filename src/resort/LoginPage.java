package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import resort.CustomComponents.*;

public class LoginPage extends JFrame {

    private OceanResortSystem mainSystem;
    private String selectedRole = "ADMIN"; // default role

    public LoginPage(OceanResortSystem mainSystem) {
        this.mainSystem = mainSystem;
        setupUI();
    }

    private void setupUI() {
        setTitle("Ocean Resort | Staff Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ── Brand Panel ──────────────────────────────────────────────────────
        JPanel brandPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, Constants.PRIMARY_COLOR,
                        getWidth(), getHeight(), new Color(0, 50, 100)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        brandPanel.setLayout(new GridBagLayout());
        String versionText = mainSystem.isUsingDatabase() ? "Database Edition" : "Standalone Edition";
        JLabel brandLabel = new JLabel(
                "<html><center>"
                + "<h1 style='color:white; font-size:40px;'>🌊 OCEAN<br>RESORT</h1>"
                + "<p style='color:#ccc; font-size:16px;'>Management System - " + versionText + "</p>"
                + "</center></html>");
        brandPanel.add(brandLabel);

        // ── Login Panel ──────────────────────────────────────────────────────
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel loginTitle = new JLabel("Staff Portal");
        loginTitle.setFont(Constants.TITLE_FONT);
        loginTitle.setForeground(Constants.PRIMARY_COLOR);

        // ── Role Toggle ──────────────────────────────────────────────────────
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 0, 0));
        rolePanel.setPreferredSize(new Dimension(250, 40));
        rolePanel.setBorder(BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 2));
        rolePanel.setBackground(Color.WHITE);

        JButton adminBtn = new JButton("👑 Admin");
        JButton staffBtn = new JButton("👤 Staff");

        styleRoleButton(adminBtn, true);   // Admin selected by default
        styleRoleButton(staffBtn, false);

        adminBtn.addActionListener(e -> {
            selectedRole = "ADMIN";
            styleRoleButton(adminBtn, true);
            styleRoleButton(staffBtn, false);
        });

        staffBtn.addActionListener(e -> {
            selectedRole = "STAFF";
            styleRoleButton(staffBtn, true);
            styleRoleButton(adminBtn, false);
        });

        rolePanel.add(adminBtn);
        rolePanel.add(staffBtn);

        // ── Fields ───────────────────────────────────────────────────────────
        ModernTextField userField = new ModernTextField();
        userField.setPreferredSize(new Dimension(250, 35));

        JPasswordField passField = new JPasswordField();
        passField.setBorder(userField.getBorder());
        passField.setPreferredSize(new Dimension(250, 35));

        ActionButton loginBtn = new ActionButton("LOGIN", Constants.PRIMARY_COLOR);
        loginBtn.setPreferredSize(new Dimension(250, 45));

        // ── Hint label (updates based on role) ───────────────────────────────
        JLabel hintLabel = new JLabel(
                "<html><center><small></small></center></html>");
        hintLabel.setForeground(Color.GRAY);

        // ── Layout ───────────────────────────────────────────────────────────
        loginPanel.add(loginTitle, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 30, 5, 30);
        loginPanel.add(new JLabel("Login As"), gbc);
        gbc.gridy++;
        loginPanel.add(rolePanel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 30, 5, 30);
        loginPanel.add(new JLabel("Username"), gbc);
        gbc.gridy++;
        loginPanel.add(userField, gbc);
        gbc.gridy++;
        loginPanel.add(new JLabel("Password"), gbc);
        gbc.gridy++;
        loginPanel.add(passField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 30, 10, 30);
        loginPanel.add(loginBtn, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 30, 10, 30);
        loginPanel.add(hintLabel, gbc);

        // ── Login Action ─────────────────────────────────────────────────────
        ActionListener loginAction = e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            boolean authenticated = false;

            if (mainSystem.isUsingDatabase()) {
                // Authenticate AND verify role matches
                String userRole = DatabaseHandler.getUserRole(username, password);
                authenticated = userRole != null && userRole.equalsIgnoreCase(selectedRole);
            } else {
                // Fallback hardcoded credentials
                if (selectedRole.equals("ADMIN")) {
                    authenticated = username.equals("admin") && password.equals("123");
                } else {
                    authenticated = username.equals("staff") && password.equals("staff123");
                }
            }

            if (authenticated) {
                dispose();
                mainSystem.setCurrentRole(selectedRole); // store role in main system
                mainSystem.showDashboard();
            } else {
                // Red border feedback
                userField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                passField.setBorder(userField.getBorder());

                JOptionPane.showMessageDialog(this,
                        "<html>Invalid credentials for <b>" + selectedRole + "</b> login!</html>",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

                // Reset border after 2s
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

    /** Style the role toggle buttons — active = filled, inactive = outline */
    private void styleRoleButton(JButton btn, boolean active) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        if (active) {
            btn.setBackground(Constants.PRIMARY_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Constants.PRIMARY_COLOR);
            btn.setOpaque(true);
        }
    }
}