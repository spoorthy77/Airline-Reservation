package com.mycompany.airlinereservation;
 // Adjust package name if necessary

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.net.URL; // For loading the image icon

public class Cancel extends JFrame {

    // ✅ DB CONNECTION DETAILS (MUST MATCH your airline_db settings)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005"; // Ensure this is correct

    private JTextField pnrField, nameField, cancelNoField, flightCodeField, dateField;
    private JButton showDetailsBtn, cancelTicketBtn;

    public Cancel() {
        setTitle("CANCELLATION");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255)); // Light Blue background

        // --- PNR Input Section ---
        JLabel pnrLabel = new JLabel("PNR Number");
        pnrLabel.setBounds(50, 50, 100, 30);
        pnrLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        add(pnrLabel);

        pnrField = new JTextField();
        pnrField.setBounds(180, 50, 150, 30);
        add(pnrField);

        showDetailsBtn = new JButton("Show Details");
        showDetailsBtn.setBounds(350, 50, 150, 30);
        showDetailsBtn.setBackground(new Color(0, 102, 204)); // Darker Blue
        showDetailsBtn.setForeground(Color.WHITE);
        showDetailsBtn.setFocusPainted(false);
        showDetailsBtn.addActionListener(e -> fetchDetailsAction());
        add(showDetailsBtn);

        // --- Details Display Section ---

        // 1. Name
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(50, 110, 100, 30);
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(180, 110, 150, 30);
        nameField.setEditable(false);
        add(nameField);

        // 2. Cancellation No (Will display PNR as reference)
        JLabel cancelNoLabel = new JLabel("Cancellation No");
        cancelNoLabel.setBounds(50, 160, 120, 30);
        cancelNoLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(cancelNoLabel);

        cancelNoField = new JTextField();
        cancelNoField.setBounds(180, 160, 150, 30);
        cancelNoField.setEditable(false); 
        add(cancelNoField);

        // 3. Flight Code
        JLabel flightCodeLabel = new JLabel("Flight Code");
        flightCodeLabel.setBounds(50, 210, 100, 30);
        flightCodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(flightCodeLabel);

        flightCodeField = new JTextField();
        flightCodeField.setBounds(180, 210, 150, 30);
        flightCodeField.setEditable(false);
        add(flightCodeField);

        // 4. Date
        JLabel dateLabel = new JLabel("Date of Travel");
        dateLabel.setBounds(50, 260, 120, 30);
        dateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 260, 150, 30);
        dateField.setEditable(false);
        add(dateField);

        // --- Cancel Button ---
        cancelTicketBtn = new JButton("Cancel Ticket");
        cancelTicketBtn.setBounds(180, 330, 150, 35);
        cancelTicketBtn.setBackground(new Color(204, 51, 0)); // Reddish color
        cancelTicketBtn.setForeground(Color.WHITE);
        cancelTicketBtn.setFocusPainted(false);
        cancelTicketBtn.setEnabled(false); // Disable until details are fetched
        cancelTicketBtn.addActionListener(e -> cancelTicketAction());
        add(cancelTicketBtn);
        
        // --- Image ---
        // NOTE: If you have a specific icon, replace the path below.
        // This attempts to load a generic system resource or uses a placeholder.
        JLabel image = new JLabel();
        try {
            URL imageUrl = getClass().getResource("/images/cancel_icon.png"); // Example path in project structure
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image img = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                image.setIcon(new ImageIcon(img));
            } else {
                 // Placeholder text if image is not found
                 image.setText("Cancellation Icon");
            }
        } catch (Exception e) {
            image.setText("Cancellation Icon");
        }
        image.setBounds(370, 130, 180, 180);
        add(image);


        setVisible(true);
    }

    /**
     * Fetches ticket details from the 'ticket' table using the PNR.
     */
    private void fetchDetailsAction() {
        String pnr = pnrField.getText().trim().toUpperCase();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the PNR number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Reset fields
        nameField.setText("");
        cancelNoField.setText("");
        flightCodeField.setText("");
        dateField.setText("");
        cancelTicketBtn.setEnabled(false);

        String sql = "SELECT customer_name, flight_code, date_of_travel FROM ticket WHERE pnr = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Populate fields
                nameField.setText(rs.getString("customer_name"));
                cancelNoField.setText(pnr); // PNR acts as the Cancellation No
                flightCodeField.setText(rs.getString("flight_code"));
                
                // Format date for display
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date travelDate = rs.getDate("date_of_travel");
                String date = sdf.format(travelDate);
                dateField.setText(date);
                
                cancelTicketBtn.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Ticket with PNR " + pnr + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error fetching details: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the ticket record from the 'ticket' table.
     */
    private void cancelTicketAction() {
        String pnr = pnrField.getText().trim().toUpperCase();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel the ticket for PNR: " + pnr + "? This action is permanent.", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform cancellation in a transaction: increment seats_available -> delete ticket
        String selectSql = "SELECT flight_code FROM ticket WHERE pnr = ? FOR UPDATE";
        String updFlightSql = "UPDATE flight SET seats_available = seats_available + 1 WHERE flight_code = ?";
        String deleteSql = "DELETE FROM ticket WHERE pnr = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try {
                conn.setAutoCommit(false);

                String flightCode = null;
                try (PreparedStatement pstSelect = conn.prepareStatement(selectSql)) {
                    pstSelect.setString(1, pnr);
                    try (ResultSet rs = pstSelect.executeQuery()) {
                        if (rs.next()) {
                            flightCode = rs.getString("flight_code");
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Ticket with PNR " + pnr + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                // increment seats
                try (PreparedStatement pstUpd = conn.prepareStatement(updFlightSql)) {
                    pstUpd.setString(1, flightCode);
                    pstUpd.executeUpdate();
                }

                // delete ticket
                try (PreparedStatement pstDel = conn.prepareStatement(deleteSql)) {
                    pstDel.setString(1, pnr);
                    int rowsAffected = pstDel.executeUpdate();
                    if (rowsAffected == 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Cancellation failed. Ticket not found or already cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                conn.commit();

                JOptionPane.showMessageDialog(this,
                    "Ticket with PNR " + pnr + " successfully cancelled.\nRefund process initiated.",
                    "Cancellation Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window

            } catch (SQLException exInner) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                JOptionPane.showMessageDialog(this, "Cancellation Failed (transaction rolled back): " + exInner.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cancellation Failed (DB Error): " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Ensure JDBC driver is loaded if necessary (though modern Java handles this usually)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            return;
        }
        SwingUtilities.invokeLater(() -> new Cancel());
    }
}