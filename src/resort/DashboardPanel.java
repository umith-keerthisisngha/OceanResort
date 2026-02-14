package resort;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;
import resort.CustomComponents.*;

public class DashboardPanel extends JPanel {
    
    private JLabel totalRevenueLbl, totalBookingsLbl, activeBookingsLbl;
    private HashMap<String, Reservation> reservations;
    private ActionListener navigationListener;
    
    public DashboardPanel(HashMap<String, Reservation> reservations, ActionListener navListener) {
        this.reservations = reservations;
        this.navigationListener = navListener;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(null); 
        setBackground(Constants.LIGHT_BG);
        
        JLabel title = new JLabel("Performance Overview");
        title.setFont(Constants.TITLE_FONT); 
        title.setBounds(40, 40, 400, 40);
        add(title);

        totalBookingsLbl = createStatCard("Total Bookings", "0", new Color(46, 204, 113), 40, 100);
        activeBookingsLbl = createStatCard("Active Bookings", "0", new Color(52, 152, 219), 320, 100);
        totalRevenueLbl = createStatCard("Total Revenue (LKR)", "0", new Color(155, 89, 182), 600, 100);

        // Quick actions panel
        JPanel quickActions = new JPanel();
        quickActions.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        quickActions.setBackground(Color.WHITE);
        quickActions.setBounds(40, 260, 800, 100);
        quickActions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        
        ActionButton newRes = new ActionButton("New Reservation", Constants.ACCENT_COLOR);
        newRes.setActionCommand("New Reservation");
        newRes.addActionListener(navigationListener);
        
        ActionButton viewAll = new ActionButton("View Bookings", Constants.PRIMARY_COLOR);
        viewAll.setActionCommand("All Bookings");
        viewAll.addActionListener(navigationListener);
        
        quickActions.add(newRes);
        quickActions.add(viewAll);
        add(quickActions);
    }
    
    private JLabel createStatCard(String title, String value, Color color, int x, int y) {
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
        add(card); 
        return v;
    }
    
    public void updateStats() {
        totalBookingsLbl.setText(String.valueOf(reservations.size()));
        long activeCount = reservations.values().stream()
            .filter(r -> r.getStatus().equals("Active"))
            .count();
        activeBookingsLbl.setText(String.valueOf(activeCount));
        long total = reservations.values().stream().mapToLong(Reservation::getTotalCost).sum();
        totalRevenueLbl.setText(String.format("%,d", total));
    }
}
