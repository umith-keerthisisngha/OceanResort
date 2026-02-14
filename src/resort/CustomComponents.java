package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomComponents {
    
    public static class MenuButton extends JButton {
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

    public static class ActionButton extends JButton {
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

    public static class ModernTextField extends JTextField {
        public ModernTextField() {
            setFont(Constants.PLAIN_FONT);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }
    }
}
