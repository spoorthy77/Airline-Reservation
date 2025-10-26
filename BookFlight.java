package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.UUID;
import com.toedter.calendar.JDateChooser;

public class BookFlight extends JFrame {

    // ✅ DB CONNECTION DETAILS (kept for compatibility with existing code)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTextField aadharField, nameField, nationalityField, flightNameField, flightCodeField;
    private JTextArea addressArea;
    private JComboBox<String> genderCombo, sourceCombo, destinationCombo;
    private JDateChooser dateChooser;
    
    // 🧮 Variable to store the fetched ticket price
    private double ticketPrice = 0.0;

    public BookFlight() {
        setTitle("Book Flight");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(ThemeManager.DARK_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Passenger Fields ---
        aadharField = new JTextField(15);
        ThemeManager.applyDarkTextFieldTheme(aadharField);
        
        nameField = new JTextField(15);
        ThemeManager.applyDarkTextFieldTheme(nameField);
        
        nationalityField = new JTextField(15);
        ThemeManager.applyDarkTextFieldTheme(nationalityField);
        
        addressArea = new JTextArea(3, 15);
        ThemeManager.applyDarkTextAreaTheme(addressArea);
        
        genderCombo = new JComboBox<>(new String[]{"Select Gender", "Male", "Female", "Other"});
        ThemeManager.applyDarkComboBoxTheme(genderCombo);
        
        dateChooser = new JDateChooser();

        nameField.setEditable(false);
        nationalityField.setEditable(false);
        addressArea.setEditable(false);

        // --- Flight Fields ---
        sourceCombo = new JComboBox<>();
        ThemeManager.applyDarkComboBoxTheme(sourceCombo);
        
        destinationCombo = new JComboBox<>();
        ThemeManager.applyDarkComboBoxTheme(destinationCombo);
        
        flightNameField = new JTextField(15);
        ThemeManager.applyDarkTextFieldTheme(flightNameField);
        
        flightCodeField = new JTextField(15);
        ThemeManager.applyDarkTextFieldTheme(flightCodeField);

        flightNameField.setEditable(false);
        flightCodeField.setEditable(false);

        // Initially empty dropdowns
        sourceCombo.addItem("Click to Load Sources");
        destinationCombo.addItem("Click to Load Destinations");

        // When dropdown is clicked, load from DB
        sourceCombo.addPopupMenuListener(new PopupMenuListenerAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                loadSources();
            }
        });

        destinationCombo.addPopupMenuListener(new PopupMenuListenerAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                loadDestinations();
            }
        });

        // --- Layout Design ---
        gbc.gridx = 0; gbc.gridy = 0; 
        JLabel aadharLabel = new JLabel("Aadhar");
        ThemeManager.applyDarkLabelTheme(aadharLabel);
        add(aadharLabel, gbc);
        
        gbc.gridx = 1; add(aadharField, gbc);
        JButton fetchUserBtn = new JButton("Fetch User");
        ThemeManager.applyDarkButtonTheme(fetchUserBtn, true);
        fetchUserBtn.addActionListener(this::fetchUserAction);
        gbc.gridx = 2; add(fetchUserBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1; 
        JLabel nameLabel = new JLabel("Name");
        ThemeManager.applyDarkLabelTheme(nameLabel);
        add(nameLabel, gbc);
        gbc.gridx = 1; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; 
        JLabel nationalityLabel = new JLabel("Nationality");
        ThemeManager.applyDarkLabelTheme(nationalityLabel);
        add(nationalityLabel, gbc);
        gbc.gridx = 1; add(nationalityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; 
        JLabel addressLabel = new JLabel("Address");
        ThemeManager.applyDarkLabelTheme(addressLabel);
        add(addressLabel, gbc);
        gbc.gridx = 1; add(new JScrollPane(addressArea), gbc);

        gbc.gridx = 0; gbc.gridy = 4; 
        JLabel genderLabel = new JLabel("Gender");
        ThemeManager.applyDarkLabelTheme(genderLabel);
        add(genderLabel, gbc);
        gbc.gridx = 1; add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; 
        JLabel sourceLabel = new JLabel("Source");
        ThemeManager.applyDarkLabelTheme(sourceLabel);
        add(sourceLabel, gbc);
        gbc.gridx = 1; add(sourceCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6; 
        JLabel destinationLabel = new JLabel("Destination");
        ThemeManager.applyDarkLabelTheme(destinationLabel);
        add(destinationLabel, gbc);
        gbc.gridx = 1; add(destinationCombo, gbc);

        JButton fetchFlightsBtn = new JButton("Fetch Flight & Price");
        ThemeManager.applyDarkButtonTheme(fetchFlightsBtn, true);
        fetchFlightsBtn.addActionListener(this::fetchFlightsAction);
        gbc.gridx = 2; gbc.gridy = 6; add(fetchFlightsBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 7; 
        JLabel flightNameLabel = new JLabel("Flight Name");
        ThemeManager.applyDarkLabelTheme(flightNameLabel);
        add(flightNameLabel, gbc);
        gbc.gridx = 1; add(flightNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 8; 
        JLabel flightCodeLabel = new JLabel("Flight Code");
        ThemeManager.applyDarkLabelTheme(flightCodeLabel);
        add(flightCodeLabel, gbc);
        gbc.gridx = 1; add(flightCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 9; 
        JLabel dateLabel = new JLabel("Date of Travel");
        ThemeManager.applyDarkLabelTheme(dateLabel);
        add(dateLabel, gbc);
        gbc.gridx = 1; add(dateChooser, gbc);

        JButton bookFlightBtn = new JButton("Book & Pay");
        ThemeManager.applyDarkButtonTheme(bookFlightBtn, true);
        bookFlightBtn.addActionListener(this::bookFlightAction);
        gbc.gridx = 1; gbc.gridy = 10; gbc.anchor = GridBagConstraints.CENTER;
        add(bookFlightBtn, gbc);

        setVisible(true);
    }
    
    // --- UTILITY METHODS ---

    // 💰 Get Ticket Price
    private double getTicketPrice(String flightCode) {
        String sql = "SELECT price FROM flight WHERE flight_code = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flightCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching flight price: " + ex.getMessage());
        }
        return 0.0;
    }

    // 💳 Record Payment in Database
    private boolean recordPayment(String pnr, double amount) {
        // Assume 'Credit Card' and 'Completed' for simplicity in this step.
        String paymentMethod = "Credit Card";
        String paymentStatus = "Completed";
        
        String sql = "INSERT INTO payments (pnr, amount, payment_method, payment_status, transaction_date) VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pnr);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, paymentStatus);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Payment failed to record: " + ex.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payment DB Error: " + ex.getMessage());
            return false;
        }
    }
    
    // --- ACTION HANDLERS ---
    
    private void fetchUserAction(ActionEvent e) {
        // Existing implementation is fine
        String aadhar = aadharField.getText();
        if (aadhar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Aadhar number.");
            return;
        }

        String sql = "SELECT name, nationality, address, gender FROM customer WHERE aadhar_no = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ... (rest of the fetchUserAction logic)
            pstmt.setString(1, aadhar);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                nationalityField.setText(rs.getString("nationality"));
                addressArea.setText(rs.getString("address"));
                genderCombo.setSelectedItem(rs.getString("gender"));
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found. Please add customer first.");
                nameField.setEditable(true);
                nationalityField.setEditable(true);
                addressArea.setEditable(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error fetching user: " + ex.getMessage());
        }
    }

    // ✅ Fetch Flight Details and Price
    private void fetchFlightsAction(ActionEvent e) {
        String source = (String) sourceCombo.getSelectedItem();
        String destination = (String) destinationCombo.getSelectedItem();

        if (source == null || destination == null) {
            JOptionPane.showMessageDialog(this, "Select source and destination.");
            return;
        }

        // Fetch flight details AND price
        String sql = "SELECT flight_name, flight_code, price FROM flight WHERE source = ? AND destination = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                flightNameField.setText(rs.getString("flight_name"));
                String code = rs.getString("flight_code");
                flightCodeField.setText(code);
                
                // Store the price
                ticketPrice = rs.getDouble("price");
                
                JOptionPane.showMessageDialog(this, String.format("Flight %s found. Price: %.2f INR", code, ticketPrice));
            } else {
                JOptionPane.showMessageDialog(this, "No flight found for this route.");
                ticketPrice = 0.0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching flight: " + ex.getMessage());
        }
    }

    // ✅ Book Flight Action (Insert into ticket and Payments tables)
    private void bookFlightAction(ActionEvent e) {
        if (aadharField.getText().isEmpty() || flightCodeField.getText().isEmpty() || dateChooser.getDate() == null || ticketPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Fill all required details and ensure flight price is fetched (click 'Fetch Flight & Price').");
            return;
        }

        String pnr = generatePNR(); 

        // 1. INSERT INTO TICKET TABLE
        String sqlTicket = "INSERT INTO ticket (pnr, customer_aadhar, customer_name, nationality, address, gender, source, destination, flight_name, flight_code, date_of_travel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Use a transaction so: check seats -> insert ticket -> insert payment -> decrement seats (atomic)
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try {
                conn.setAutoCommit(false);

                // 0. Check seat availability with row-level lock
                String checkSql = "SELECT seats_available FROM flight WHERE flight_code = ? FOR UPDATE";
                try (PreparedStatement pstCheck = conn.prepareStatement(checkSql)) {
                    pstCheck.setString(1, flightCodeField.getText());
                    try (ResultSet rs = pstCheck.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Selected flight not found. Booking cancelled.");
                            return;
                        }
                        int avail = rs.getInt("seats_available");
                        if (avail <= 0) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "No seats available for the selected flight.");
                            return;
                        }
                    }
                }

                // 1. INSERT INTO TICKET TABLE
                try (PreparedStatement pstmtTicket = conn.prepareStatement(sqlTicket)) {
                    pstmtTicket.setString(1, pnr);
                    pstmtTicket.setString(2, aadharField.getText());
                    pstmtTicket.setString(3, nameField.getText());
                    pstmtTicket.setString(4, nationalityField.getText());
                    pstmtTicket.setString(5, addressArea.getText());
                    pstmtTicket.setString(6, (String) genderCombo.getSelectedItem());
                    pstmtTicket.setString(7, (String) sourceCombo.getSelectedItem());
                    pstmtTicket.setString(8, (String) destinationCombo.getSelectedItem());
                    pstmtTicket.setString(9, flightNameField.getText());
                    pstmtTicket.setString(10, flightCodeField.getText());
                    pstmtTicket.setDate(11, new java.sql.Date(dateChooser.getDate().getTime()));

                    pstmtTicket.executeUpdate();
                }

                // 2. INSERT INTO PAYMENTS TABLE (use same connection so it's part of the transaction)
                String paymentSql = "INSERT INTO payments (pnr, amount, payment_method, payment_status, transaction_date) VALUES (?, ?, ?, ?, NOW())";
                try (PreparedStatement pstPay = conn.prepareStatement(paymentSql)) {
                    pstPay.setString(1, pnr);
                    pstPay.setDouble(2, ticketPrice);
                    pstPay.setString(3, "Credit Card");
                    pstPay.setString(4, "Completed");
                    pstPay.executeUpdate();
                }

                // 3. Decrement seats_available
                String updSql = "UPDATE flight SET seats_available = seats_available - 1 WHERE flight_code = ?";
                try (PreparedStatement pstUpd = conn.prepareStatement(updSql)) {
                    pstUpd.setString(1, flightCodeField.getText());
                    pstUpd.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Flight booked and paid successfully!\nYour PNR is: " + pnr + "\nAmount Paid: " + String.format("%.2f", ticketPrice) + " INR");
                dispose();

            } catch (SQLException exInner) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                JOptionPane.showMessageDialog(this, "Booking failed (transaction rolled back): " + exInner.getMessage());
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
        }
    }

    // --- Utility: Generate Random 6-char PNR using UUID and ensure (best-effort) uniqueness ---
    private String generatePNR() {
        String pnr = null;
        int attempts = 0;
        while (attempts < 5) {
            // generate 6-char alphanumeric from UUID and uppercase it
            pnr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

            // Check DB for collision (best-effort). If DB check fails, just return the generated PNR.
            String sql = "SELECT COUNT(*) FROM ticket WHERE pnr = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, pnr);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count == 0) return pnr;
                    } else {
                        return pnr;
                    }
                }
            } catch (SQLException ex) {
                // If DB check fails, log and return the generated PNR (so booking can proceed)
                System.err.println("PNR uniqueness check failed: " + ex.getMessage());
                return pnr;
            }

            attempts++;
        }

        // After a few attempts, return the last generated PNR
        return pnr != null ? pnr : UUID.randomUUID().toString().replace("-", "").substring(0,6).toUpperCase();
    }
    
    // ... (loadSources, loadDestinations, and PopupMenuListenerAdapter remain the same) ...

    private void loadSources() {
        sourceCombo.removeAllItems();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT source FROM flight");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                sourceCombo.addItem(rs.getString("source"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading sources: " + ex.getMessage());
        }
    }

    private void loadDestinations() {
        destinationCombo.removeAllItems();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT destination FROM flight");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                destinationCombo.addItem(rs.getString("destination"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading destinations: " + ex.getMessage());
        }
    }

    private abstract static class PopupMenuListenerAdapter implements javax.swing.event.PopupMenuListener {
        public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
        public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
        public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookFlight::new);
    }
    
}
