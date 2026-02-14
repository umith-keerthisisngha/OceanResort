package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import resort.CustomComponents.*;

public class LoginPage extends JFrame {
    
    private OceanResortSystemMain mainSystem;
    
    public LoginPage(OceanResortSystemMain mainSystem) {
        this.mainSystem = mainSystem;
        setupUI();
    }
    
    private void setupUI() {
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
                g2.setPaint(new GradientPaint(0, 0, Constants.PRIMARY_COLOR, getWidth(), getHeight(), new Color(0, 50, 100)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        brandPanel.setLayout(new GridBagLayout());
        JLabel brandLabel = new JLabel("<html><center><h1 style='color:white; font-size:40px;'>🌊 OCEAN<br>RESORT</h1><p style='color:#ccc; font-size:16px;'>Management System v1.0</p></center></html>");
        brandPanel.add(brandLabel);

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
        
        ModernTextField userField = new ModernTextField();
        userField.setPreferredSize(new Dimension(250, 35));
        JPasswordField passField = new JPasswordField();
        passField.setBorder(userField.getBorder());
        passField.setPreferredSize(new Dimension(250, 35));
        
        ActionButton loginBtn = new ActionButton("LOGIN", Constants.PRIMARY_COLOR);
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
                dispose();
                mainSystem.showDashboard();
            } else {
                userField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2), 
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
}
