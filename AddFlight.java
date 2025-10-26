package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddFlight extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTextField txtCode, txtName, txtSource, txtDest, txtPrice, txtSeats, txtTotalSeats;
    private JTextField txtDepartureTime, txtArrivalTime; // For datetime input
    private JComboBox<String> comboAirline;
    
    // Map to store Airline Name -> Airline ID for insertion
    private Map<String, Integer> airlineMap = new HashMap<>();

    public AddFlight() {
        setTitle("✈️ Add New Flight Information");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Enter Flight Details", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Flight Code
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Flight Code:"), gbc);
        txtCode = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtCode, gbc);

        // Flight Name
        gbc.gridx = 2; gbc.gridy = row++;
        formPanel.add(new JLabel("Flight Name:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 3;
        formPanel.add(txtName, gbc);

        // Source
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Source:"), gbc);
        txtSource = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtSource, gbc);

        // Destination
        gbc.gridx = 2; gbc.gridy = row++;
        formPanel.add(new JLabel("Destination:"), gbc);
        txtDest = new JTextField(20);
        gbc.gridx = 3;
        formPanel.add(txtDest, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Price (₹):"), gbc);
        txtPrice = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtPrice, gbc);

        // Seats Available
        gbc.gridx = 2; gbc.gridy = row++;
        formPanel.add(new JLabel("Seats Available:"), gbc);
        txtSeats = new JTextField(20);
        gbc.gridx = 3;
        formPanel.add(txtSeats, gbc);

        // Total Seats
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Seats:"), gbc);
        txtTotalSeats = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtTotalSeats, gbc);

        // Airline (LINKED TO airlines table)
        gbc.gridx = 2; gbc.gridy = row++;
        formPanel.add(new JLabel("Airline:"), gbc);
        comboAirline = new JComboBox<>();
        gbc.gridx = 3;
        formPanel.add(comboAirline, gbc);
        
        // Departure Time
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Departure Time (YYYY-MM-DD HH:MM:SS):"), gbc);
        txtDepartureTime = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtDepartureTime, gbc);
        gbc.gridwidth = 1; // Reset width
        row++;

        // Arrival Time
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Arrival Time (YYYY-MM-DD HH:MM:SS):"), gbc);
        txtArrivalTime = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtArrivalTime, gbc);
        gbc.gridwidth = 1; // Reset width
        row++;

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Button Panel
        JButton btnAddFlight = new JButton("Add Flight");
        btnAddFlight.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.add(btnAddFlight);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        loadAirlines(); // Load airlines into ComboBox
        btnAddFlight.addActionListener(e -> handleAddFlight());

        setVisible(true);
    }
    
    // Helper to get Connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    // Load Airlines from DB into the ComboBox
    private void loadAirlines() {
        String sql = "SELECT airline_id, airline_name FROM airlines ORDER BY airline_name";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            comboAirline.removeAllItems();
            while (rs.next()) {
                String name = rs.getString("airline_name");
                int id = rs.getInt("airline_id");
                airlineMap.put(name, id);
                comboAirline.addItem(name);
            }
            if (comboAirline.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No airlines found. Please add an airline first.", "Setup Required", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading airlines: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddFlight() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String source = txtSource.getText().trim();
        String dest = txtDest.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String seatsStr = txtSeats.getText().trim();
        String totalSeatsStr = txtTotalSeats.getText().trim();
        String deptTimeStr = txtDepartureTime.getText().trim();
        String arrTimeStr = txtArrivalTime.getText().trim();
        String airlineName = (String) comboAirline.getSelectedItem();
        
        // Basic Validation
        if (code.isEmpty() || name.isEmpty() || source.isEmpty() || dest.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty() || totalSeatsStr.isEmpty() || deptTimeStr.isEmpty() || arrTimeStr.isEmpty() || airlineName == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Retrieve airline_id using the selected name
        Integer airlineId = airlineMap.get(airlineName);
        if (airlineId == null) {
            JOptionPane.showMessageDialog(this, "Selected airline ID not found. Please reload or check the database.", "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Numeric and Time Validation
        double price;
        int seats, totalSeats;
        try {
            price = Double.parseDouble(priceStr);
            seats = Integer.parseInt(seatsStr);
            totalSeats = Integer.parseInt(totalSeatsStr);
            if (seats > totalSeats) {
                JOptionPane.showMessageDialog(this, "Seats available cannot exceed Total seats.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Basic date validation (just to check format, full validation is complex)
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(deptTimeStr);
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(arrTimeStr);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price, Seats, and Total Seats must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Date/Time format must be YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO flight (flight_code, flight_name, airline_id, source, destination, price, seats_available, total_seats, departure_time, arrival_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, code);
            pst.setString(2, name);
            pst.setInt(3, airlineId); // Using the retrieved ID
            pst.setString(4, source);
            pst.setString(5, dest);
            pst.setDouble(6, price);
            pst.setInt(7, seats);
            pst.setInt(8, totalSeats);
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.parse(deptTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            pst.setTimestamp(10, Timestamp.valueOf(LocalDateTime.parse(arrTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Flight '" + code + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields after success
                clearFields();
            }

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Flight Code already exists. Please choose a unique code.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        txtCode.setText("");
        txtName.setText("");
        txtSource.setText("");
        txtDest.setText("");
        txtPrice.setText("");
        txtSeats.setText("");
        txtTotalSeats.setText("");
        txtDepartureTime.setText("");
        txtArrivalTime.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddFlight::new);
    }
}