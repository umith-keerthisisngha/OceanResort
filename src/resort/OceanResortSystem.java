package resort;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class OceanResortSystem {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

/* ================= THEME & UI COLORS ================= */
class Theme {
    static final Color BG_DARK = new Color(50, 50, 50); // Matches the dark grey in your image
    static final Color ACCENT = new Color(0, 119, 182);
    static final Color TEXT_LIGHT = new Color(220, 220, 220);
    static final Font HEADER_FONT = new Font("Serif", Font.PLAIN, 42);
}

/* ================= CUSTOM IMAGE PANEL ================= */
class ImagePanel extends JPanel {
    private Image img;
    public ImagePanel() {
        // Replace with your local image path or a URL
        try {
            img = new ImageIcon(new URL("https://i.imgur.com/8N69f1V.png")).getImage(); 
        } catch (Exception e) { System.out.println("Image not found"); }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.BG_DARK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        if (img != null) {
            // Draw title "The Ocean Resort"
            g2.setColor(Theme.TEXT_LIGHT);
            g2.setFont(Theme.HEADER_FONT);
            g2.drawString("The Ocean Resort", 50, 100);
            
            // Scaled illustrative image
            int iw = img.getWidth(this);
            int ih = img.getHeight(this);
            double ratio = Math.min((double)getWidth()/iw, (double)getHeight()/ih) * 0.7;
            g2.drawImage(img, 20, getHeight() - (int)(ih*ratio) - 20, (int)(iw*ratio), (int)(ih*ratio), this);
        }
    }
}

/* ================= LOGIN FRAME (MATCHING YOUR IMAGE) ================= */
class LoginFrame extends JFrame {
    LoginFrame() {
        setUndecorated(true); setSize(900, 550); setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 550, 15, 15));
        
        JPanel container = new JPanel(new GridLayout(1, 2));
        
        // Left Side: Illustration and Title
        container.add(new ImagePanel());
        
        // Right Side: Login Form
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(60, 60, 60)); // Slightly lighter than left
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel loginTitle = new JLabel("LOGIN");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        loginTitle.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; right.add(loginTitle, gbc);

        // Username Field
        JLabel uLbl = new JLabel("Username");
        uLbl.setForeground(Theme.TEXT_LIGHT);
        gbc.gridy = 1; right.add(uLbl, gbc);
        JTextField uField = new JTextField(15);
        uField.setBackground(new Color(70, 70, 70));
        uField.setForeground(Color.WHITE);
        uField.setCaretColor(Color.WHITE);
        uField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));
        gbc.gridy = 2; right.add(uField, gbc);

        // Password Field
        JLabel pLbl = new JLabel("Password");
        pLbl.setForeground(Theme.TEXT_LIGHT);
        gbc.gridy = 3; right.add(pLbl, gbc);
        JPasswordField pField = new JPasswordField(15);
        pField.setBackground(new Color(70, 70, 70));
        pField.setForeground(Color.WHITE);
        pField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));
        gbc.gridy = 4; right.add(pField, gbc);

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 40));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridy = 5; gbc.insets = new Insets(30, 40, 10, 40);
        right.add(loginBtn, gbc);
        
        // Exit Button (Small)
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBackground(new Color(40, 40, 40));
        exitBtn.setForeground(Color.WHITE);
        gbc.gridy = 6; gbc.anchor = GridBagConstraints.SOUTHEAST;
        right.add(exitBtn, gbc);

        loginBtn.addActionListener(e -> {
            if(uField.getText().equals("admin")) { dispose(); new DashboardFrame(); }
        });
        exitBtn.addActionListener(e -> System.exit(0));

        container.add(right);
        add(container);
        setVisible(true);
    }
}

/* ================= DASHBOARD FRAME (NO POPUPS) ================= */
class DashboardFrame extends JFrame {
    JPanel contentArea;
    JLabel statusLabel;

    DashboardFrame() {
        setTitle("Ocean Resort Management");
        setSize(1200, 800); setLocationRelativeTo(null); setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel sidebar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setPreferredSize(new Dimension(200, 800));

        String[] menu = {"Reservations", "Rooms", "Log Out"};
        for(String m : menu) {
            JButton b = new JButton(m);
            b.setPreferredSize(new Dimension(180, 40));
            b.setForeground(Color.WHITE); b.setContentAreaFilled(false);
            sidebar.add(b);
            b.addActionListener(e -> {
                if(m.equals("Log Out")) System.exit(0);
                switchModule(m);
            });
        }

        statusLabel = new JLabel(" System Ready");
        statusLabel.setOpaque(true); statusLabel.setBackground(Theme.ACCENT);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(1200, 30));

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(new Color(240, 240, 240));

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        switchModule("Reservations");
        setVisible(true);
    }

    void switchModule(String name) {
        contentArea.removeAll();
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        if(name.equals("Reservations")) {
            p.add(new DataModule("reservations.txt", new String[]{"ID", "Guest", "Room", "In", "Out"}));
        } else {
            p.add(new JLabel("Module: " + name, SwingConstants.CENTER));
        }
        
        contentArea.add(p);
        contentArea.revalidate(); contentArea.repaint();
    }
}

/* ================= DATA MODULE (MASTER-DETAIL) ================= */
class DataModule extends JPanel {
    String fileName; String[] cols; DefaultTableModel model; JTable table;

    DataModule(String file, String[] columns) {
        this.fileName = file; this.cols = columns;
        setLayout(new BorderLayout(15, 15)); setOpaque(false);

        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        
        // Input Fields on the Right
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
        entry.setPreferredSize(new Dimension(250, 0));
        entry.setBorder(BorderFactory.createTitledBorder("Manage Entry"));

        JTextField[] fields = new JTextField[cols.length];
        for(int i=0; i<cols.length; i++) {
            fields[i] = new JTextField();
            fields[i].setBorder(BorderFactory.createTitledBorder(cols[i]));
            entry.add(fields[i]);
        }

        JButton addBtn = new JButton("Save Record");
        addBtn.addActionListener(e -> {
            String row = "";
            for(JTextField f : fields) row += f.getText() + ",";
            saveData(row.substring(0, row.length()-1));
        });
        entry.add(Box.createRigidArea(new Dimension(0, 10)));
        entry.add(addBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(entry, BorderLayout.EAST);
        loadData();
    }

    void loadData() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String l; while((l = br.readLine()) != null) model.addRow(l.split(","));
        } catch (Exception e) {}
    }

    void saveData(String data) {
        try (FileWriter fw = new FileWriter(fileName, true)) { fw.write(data + "\n"); } catch (Exception e) {}
        loadData();
    }
}