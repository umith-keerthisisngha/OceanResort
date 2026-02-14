package resort;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import resort.CustomComponents.*;

public class ReservationPanel extends JPanel {
    
    private HashMap<String, Reservation> reservations;
    private Runnable saveCallback;
    private java.util.function.BiConsumer<String, Boolean> statusCallback;
    
    public ReservationPanel(HashMap<String, Reservation> reservations, 
                           Runnable saveCallback,
                           java.util.function.BiConsumer<String, Boolean> statusCallback) {
        this.reservations = reservations;
        this.saveCallback = saveCallback;
        this.statusCallback = statusCallback;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout()); 
        setBackground(Constants.LIGHT_BG); 
        setBorder(new EmptyBorder(40, 40, 40, 40));
        
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
        room.setFont(Constants.PLAIN_FONT);
        
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
        buttonPanel.setBackground(Constants.LIGHT_BG);
        
        ActionButton saveBtn = new ActionButton("SAVE RESERVATION", Constants.ACCENT_COLOR);
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
                statusCallback.accept("Reservation ID is required!", true);
                resNo.requestFocus();
                resNo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            if(name.getText().trim().isEmpty()) {
                statusCallback.accept("Guest Name is required!", true);
                name.requestFocus();
                name.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            if(cont.getText().trim().isEmpty()) {
                statusCallback.accept("Contact Number is required!", true);
                cont.requestFocus();
                cont.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            
            // Check for duplicate ID
            if(reservations.containsKey(resNo.getText().trim())) {
                statusCallback.accept("Reservation ID already exists! Please use a unique ID.", true);
                resNo.requestFocus();
                resNo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.DANGER_COLOR, 2), 
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                return;
            }
            
            LocalDate in = ((Date)checkIn.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate out = ((Date)checkOut.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Validate dates
            if(!out.isAfter(in)) {
                statusCallback.accept("Check-out date must be after check-in date!", true);
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
            saveCallback.run();
            
            statusCallback.accept("✓ Reservation Saved! ID: " + r.getResNo() + " | Guest: " + r.getName() + " | Cost: " + String.format("%,d", r.getTotalCost()) + " LKR", false);
            
            // Clear form
            clearBtn.doClick();
        });

        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);

        JLabel header = new JLabel("<html><h2 style='color:#333'>📝 New Guest Registration</h2></html>");
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
