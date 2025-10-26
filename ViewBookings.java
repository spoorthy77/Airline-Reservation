package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple viewer for the `bookings` table.
 * Uses DBConnection.getConnection() so it shares the project's DB config.
 */
public class ViewBookings extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public ViewBookings() {
        setTitle("✈️ View Bookings / Tickets");
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ThemeManager.DARK_BG);

        // Show PNR (ticket id) and ticket-specific columns by reading from `ticket` table
        model = new DefaultTableModel(new String[]{"PNR", "Flight Code", "Flight Name", "Source", "Destination", "Date of Travel", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        // Apply dark theme to table
        ThemeManager.applyDarkTableTheme(table);

        JScrollPane scroll = new JScrollPane(table);

        JButton btnRefresh = new JButton("Refresh");
        ThemeManager.applyDarkButtonTheme(btnRefresh, true);
        btnRefresh.addActionListener(this::loadBookings);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ThemeManager.applyDarkPanelTheme(top);
        top.add(btnRefresh);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadBookings(null);

        setVisible(true);
    }

    // Loads tickets and joins with flight and payments to show PNR and amounts
    private void loadBookings(ActionEvent e) {
        model.setRowCount(0);

    String sql = "SELECT t.pnr, t.flight_code, IFNULL(f.flight_name, '') AS flight_name, t.source, t.destination, t.date_of_travel, IFNULL(p.amount, 0) AS amount "
        + "FROM ticket t "
        + "LEFT JOIN flight f ON t.flight_code = f.flight_code "
        + "LEFT JOIN payments p ON t.pnr = p.pnr "
        + "ORDER BY t.date_of_travel DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
        model.addRow(new Object[] {
            rs.getString("pnr"),
            rs.getString("flight_code"),
            rs.getString("flight_name"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getDate("date_of_travel"),
            rs.getDouble("amount")
        });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewBookings::new);
    }
}
