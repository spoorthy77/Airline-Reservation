package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    // Use a background image located in project (flight1.jpg or flight2.jpg)
    private static final String BG_PATH = "src/main/java/com/mycompany/flight1.jpg";

    public UserDashboard(String username) {
        setTitle("User Dashboard - Welcome " + username);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel that paints scaled background image
        JPanel background = new JPanel() {
            private Image bg = new ImageIcon(BG_PATH).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    // Scale image to fill the panel while keeping aspect
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        background.setLayout(new BorderLayout());

        // Header
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        ThemeManager.applyDarkLabelTheme(welcomeLabel);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        header.add(welcomeLabel);

        // Center buttons stacked vertically with spacing
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Helper to create styled button panels
        center.add(createButtonPanel("View Available Flights", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new ViewFlightDetails();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("View Bookings", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new ViewBookings();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Book a Flight", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new BookFlight();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Cancel Ticket", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new Cancel();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Logout", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(Login::new);
            }
        }));

        // Add some padding around center
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 60, 40, 60));
        centerWrapper.add(center);

        background.add(header, BorderLayout.NORTH);
        background.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(background);
        setVisible(true);
    }

    private JPanel createButtonPanel(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(420, 60));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 255, 255, 200));
        btn.setForeground(ThemeManager.TEXT_DARK);

        btn.addActionListener(action);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        panel.add(btn);
        return panel;
    }
}
