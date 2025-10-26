package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
// No need for import java.awt.event.*; as ActionListener is used via lambda

public class JourneyDetails extends JFrame {

    // ✅ DB CONNECTION DETAILS (Copied from BookFlight.java)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005"; // Ensure this is correct

    private JTextField pnrField;
    private JTextArea journeyArea;

    public JourneyDetails() {
        setTitle("Journey Details - AirIndia");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top label
        JLabel title = new JLabel("Check Journey Details", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Input section
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel pnrLabel = new JLabel("Enter Ticket ID (PNR): ");
        pnrField = new JTextField(15);
        JButton searchBtn = new JButton("Fetch Details");

        inputPanel.add(pnrLabel);
        inputPanel.add(pnrField);
        inputPanel.add(searchBtn);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Output section
        journeyArea = new JTextArea(10, 40);
        journeyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        journeyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(journeyArea);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Button Action
        searchBtn.addActionListener(e -> fetchJourneyDetails());

        add(panel);
        setVisible(true);
    }

    private void fetchJourneyDetails() {
        // PNR is treated as the 'pnr' column value
        String pnr = pnrField.getText().trim().toUpperCase(); 
        
        if (pnr.isEmpty()) {
            ThemeManager.showWarning(this, "Please enter a valid Ticket ID (PNR).");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ✅ Replacing DBConnection.getConnection() with direct logic
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); 
            
            // NOTE: The previous SQL used t.ticket_id. If your PNR column is named 'pnr' (as per the earlier suggested fix), 
            // you should use 'pnr' here. Assuming your PNR column is correctly named 'pnr'.
            String query = """
                    SELECT pnr, customer_aadhar, customer_name, nationality, 
                           address, gender, source, destination, 
                           flight_name, flight_code, date_of_travel
                    FROM ticket 
                    WHERE pnr = ?
                    """;

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("✈️  Journey Details\n\n");
                details.append("Ticket ID (PNR):   ").append(rs.getString("pnr")).append("\n");
                details.append("Customer Name:     ").append(rs.getString("customer_name")).append("\n");
                details.append("Aadhar Number:     ").append(rs.getString("customer_aadhar")).append("\n");
                details.append("Nationality:       ").append(rs.getString("nationality")).append("\n");
                details.append("Gender:            ").append(rs.getString("gender")).append("\n");
                details.append("Address:           ").append(rs.getString("address")).append("\n");
                details.append("------------------------------------------\n");
                details.append("Flight Name:       ").append(rs.getString("flight_name")).append("\n");
                details.append("Flight Code:       ").append(rs.getString("flight_code")).append("\n");
                details.append("Source:            ").append(rs.getString("source")).append("\n");
                details.append("Destination:       ").append(rs.getString("destination")).append("\n");
                details.append("Date of Travel:    ").append(rs.getDate("date_of_travel")).append("\n");

                journeyArea.setText(details.toString());
            } else {
                ThemeManager.showInfo(this, "No journey found for PNR: " + pnr);
                journeyArea.setText("");
            }

        } catch (SQLException e) {
            ThemeManager.showError(this, "Database Error: " + e.getMessage());
        } catch (Exception e) {
            ThemeManager.showError(this, "Error fetching details: " + e.getMessage());
        } finally {
            // Close resources in reverse order
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JourneyDetails());
    }
}