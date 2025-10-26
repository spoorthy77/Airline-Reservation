package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ViewFlightDetails extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTable flightTable;
    private JComboBox<String> comboSource;
    private JComboBox<String> comboDestination;
    private DefaultTableModel model;

    public ViewFlightDetails() {
        setTitle("✈️ View Available Flights");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ThemeManager.DARK_BG);

        // --- North Panel (Filter Section) ---
        JPanel topPanel = new JPanel(new FlowLayout());
        ThemeManager.applyDarkPanelTheme(topPanel);
        
        comboSource = new JComboBox<>();
        comboDestination = new JComboBox<>();
        ThemeManager.applyDarkComboBoxTheme(comboSource);
        ThemeManager.applyDarkComboBoxTheme(comboDestination);

        JButton searchBtn = new JButton("🔍 Search Flights");
        JButton bookBtn = new JButton("🧾 Book Selected Flight");
        ThemeManager.applyDarkButtonTheme(searchBtn, true);
        ThemeManager.applyDarkButtonTheme(bookBtn, true);

        JLabel lblSource = new JLabel("Source:");
        JLabel lblDest = new JLabel("Destination:");
        ThemeManager.applyDarkLabelTheme(lblSource);
        ThemeManager.applyDarkLabelTheme(lblDest);

        topPanel.add(lblSource);
        topPanel.add(comboSource);
        topPanel.add(lblDest);
        topPanel.add(comboDestination);
        topPanel.add(searchBtn);
        topPanel.add(bookBtn);

        // --- Table Section ---
        String[] columns = {"Flight Code", "Flight Name", "Source", "Destination", "Seats", "Price"};
        model = new DefaultTableModel(columns, 0);
        flightTable = new JTable(model);
        flightTable.setFont(new Font("Arial", Font.PLAIN, 14));
        flightTable.setRowHeight(25);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setDefaultEditor(Object.class, null);
        
        // Apply dark theme to table
        ThemeManager.applyDarkTableTheme(flightTable);

        JScrollPane scrollPane = new JScrollPane(flightTable);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadSourceDestinations();
        fetchFlightData(); // initially show all flights

        searchBtn.addActionListener(this::handleSearch);
        bookBtn.addActionListener(this::handleBooking);

        setVisible(true);
    }

    // 🔹 Load distinct source and destination for filters
    private void loadSourceDestinations() {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement pstSrc = con.prepareStatement("SELECT DISTINCT source FROM flight");
            PreparedStatement pstDest = con.prepareStatement("SELECT DISTINCT destination FROM flight");

            ResultSet rsSrc = pstSrc.executeQuery();
            while (rsSrc.next()) comboSource.addItem(rsSrc.getString("source"));

            ResultSet rsDest = pstDest.executeQuery();
            while (rsDest.next()) comboDestination.addItem(rsDest.getString("destination"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Fetch and display all flights
    private void fetchFlightData() {
        model.setRowCount(0); // clear table
        String sql = "SELECT flight_code, flight_name, source, destination, seats_available, price FROM flight";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("flight_code"),
                        rs.getString("flight_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getInt("seats_available"),
                        rs.getDouble("price")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Error loading flight data.");
        }
    }

    // 🔹 Handle Search Button
    private void handleSearch(ActionEvent e) {
        String source = (String) comboSource.getSelectedItem();
        String destination = (String) comboDestination.getSelectedItem();

        if (source == null || destination == null) {
            ThemeManager.showWarning(this, "Please select both Source and Destination.");
            return;
        }

        model.setRowCount(0);
        String sql = "SELECT flight_code, flight_name, source, destination, seats_available, price FROM flight WHERE source=? AND destination=?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, source);
            pst.setString(2, destination);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("flight_code"),
                        rs.getString("flight_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getInt("seats_available"),
                        rs.getDouble("price")
                });
            }

            if (model.getRowCount() == 0) {
                ThemeManager.showInfo(this, "No flights found for this route.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 🔹 Handle Booking
    private void handleBooking(ActionEvent e) {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            ThemeManager.showWarning(this, "Please select a flight to book.");
            return;
        }

        String flightCode = model.getValueAt(selectedRow, 0).toString();
        String flightName = model.getValueAt(selectedRow, 1).toString();
        String source = model.getValueAt(selectedRow, 2).toString();
        String destination = model.getValueAt(selectedRow, 3).toString();
        double price = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Book this flight?\n\n" +
                        "Flight: " + flightName + "\n" +
                        "Route: " + source + " → " + destination + "\n" +
                        "Price: ₹" + price,
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            bookFlight(flightCode, price);
        }
    }

    // 🔹 Insert into Bookings table
    private void bookFlight(String flightCode, double price) {
        // Use a transaction: check seats_available (FOR UPDATE) -> insert booking -> decrement seats_available
        String checkSql = "SELECT seats_available FROM flight WHERE flight_code = ? FOR UPDATE";
        String insertSql = "INSERT INTO bookings (flight_code, booking_date, amount) VALUES (?, NOW(), ?)";
        String updateSql = "UPDATE flight SET seats_available = seats_available - 1 WHERE flight_code = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try {
                conn.setAutoCommit(false);

                // 0. Check availability
                try (PreparedStatement pstCheck = conn.prepareStatement(checkSql)) {
                    pstCheck.setString(1, flightCode);
                    try (ResultSet rs = pstCheck.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            ThemeManager.showError(this, "Flight not found. Cannot book.");
                            return;
                        }
                        int avail = rs.getInt("seats_available");
                        if (avail <= 0) {
                            conn.rollback();
                            ThemeManager.showError(this, "No seats available for this flight.");
                            return;
                        }
                    }
                }

                // 1. Insert booking
                try (PreparedStatement pstIns = conn.prepareStatement(insertSql)) {
                    pstIns.setString(1, flightCode);
                    pstIns.setDouble(2, price);
                    int rows = pstIns.executeUpdate();
                    if (rows <= 0) {
                        conn.rollback();
                        ThemeManager.showError(this, "Booking failed!");
                        return;
                    }
                }

                // 2. Decrement seats
                try (PreparedStatement pstUpd = conn.prepareStatement(updateSql)) {
                    pstUpd.setString(1, flightCode);
                    pstUpd.executeUpdate();
                }

                conn.commit();
                ThemeManager.showInfo(this, "✅ Booking Successful!");

            } catch (SQLException exInner) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                exInner.printStackTrace();
                ThemeManager.showError(this, "Error during booking (rolled back): " + exInner.getMessage());
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Error during booking!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewFlightDetails::new);
    }
}
