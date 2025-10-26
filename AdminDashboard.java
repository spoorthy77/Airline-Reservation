package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private String adminUsername;

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("✈️ Admin Dashboard - Welcome " + username);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use BorderLayout for main structure and a Grid for buttons
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel header = new JLabel("Administration Portal", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(ThemeManager.ACCENT_BLUE);
        add(header, BorderLayout.NORTH);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton btnAddAirline = new JButton("1. Add New Airline");
        JButton btnAddFlight = new JButton("2. Add/Manage Flight Info");
        JButton btnLogout = new JButton("Logout");

        // Style buttons
        Font btnFont = new Font("Arial", Font.PLAIN, 16);
        btnAddAirline.setFont(btnFont);
        btnAddFlight.setFont(btnFont);
        btnLogout.setFont(btnFont);
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);

        // Add actions
        btnAddAirline.addActionListener(e -> new AddAirline());
        btnAddFlight.addActionListener(e -> new AddFlight());
        
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new Login()); // Return to Login screen
        });
        
        buttonPanel.add(btnAddAirline);
        buttonPanel.add(btnAddFlight);
        buttonPanel.add(btnLogout);
        
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // You can test the dashboard directly if needed
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("TestAdmin"));
    }
}
