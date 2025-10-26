package com.mycompany.airlinereservation;

import java.awt.*;

/**
 * Centralized theme/color management for consistent UI across the application
 * This ensures text colors have proper contrast with backgrounds
 */
public class ThemeManager {

    // --- BACKGROUND COLORS ---
    public static final Color DARK_BG = new Color(34, 34, 34);           // Dark background
    public static final Color DARKER_BG = new Color(25, 25, 25);         // Darker background
    public static final Color INPUT_BG = new Color(70, 70, 70);          // Input field background - LIGHTENED for visibility
    public static final Color PANEL_BG = new Color(60, 60, 60);          // Button/panel background

    // --- TEXT COLORS ---
    public static final Color TEXT_BRIGHT = new Color(255, 255, 255);   // Pure white text (for input fields)
    public static final Color TEXT_PRIMARY = Color.WHITE;                // Primary white text
    public static final Color TEXT_DARK = new Color(30, 60, 90);         // Dark text (for light backgrounds)
    public static final Color TEXT_SECONDARY = Color.BLACK;              // Secondary black text

    // --- ACCENT COLORS ---
    public static final Color ACCENT_BLUE = new Color(100, 200, 255);   // Bright blue accent
    public static final Color ACCENT_BORDER = new Color(100, 150, 200);  // Blue border
    public static final Color ACCENT_BUTTON = new Color(75, 110, 175);   // Button blue

    // --- UTILITY COLORS ---
    public static final Color ERROR_RED = new Color(255, 100, 100);      // Error/warning red
    public static final Color SUCCESS_GREEN = new Color(100, 200, 100);  // Success green
    public static final Color BUTTON_HOVER = new Color(64, 64, 64);      // Button hover

    /**
     * Apply dark theme styling to a JTextField
     */
    public static void applyDarkTextFieldTheme(javax.swing.JTextField field) {
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_BRIGHT);
        field.setCaretColor(ACCENT_BLUE);
        field.setOpaque(true);
        field.setBorder(javax.swing.BorderFactory.createLineBorder(ACCENT_BORDER));
    }

    /**
     * Apply dark theme styling to a JPasswordField
     */
    public static void applyDarkPasswordFieldTheme(javax.swing.JPasswordField field) {
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_BRIGHT);
        field.setCaretColor(ACCENT_BLUE);
        field.setOpaque(true);
        field.setBorder(javax.swing.BorderFactory.createLineBorder(ACCENT_BORDER));
    }

    /**
     * Apply dark theme styling to a JComboBox
     */
    public static void applyDarkComboBoxTheme(javax.swing.JComboBox<?> combo) {
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT_BRIGHT);
        combo.setOpaque(true);
    }

    /**
     * Apply dark theme styling to a JButton
     */
    public static void applyDarkButtonTheme(javax.swing.JButton button, boolean isPrimary) {
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setForeground(TEXT_PRIMARY);
        if (isPrimary) {
            button.setBackground(ACCENT_BUTTON);
        } else {
            button.setBackground(BUTTON_HOVER);
        }
    }

    /**
     * Apply dark theme styling to a JLabel
     */
    public static void applyDarkLabelTheme(javax.swing.JLabel label) {
        label.setForeground(TEXT_PRIMARY);
    }

    /**
     * Apply dark theme styling to a panel
     */
    public static void applyDarkPanelTheme(javax.swing.JPanel panel) {
        panel.setBackground(DARK_BG);
    }

    /**
     * Apply dark theme styling to a JTextArea
     */
    public static void applyDarkTextAreaTheme(javax.swing.JTextArea area) {
        area.setBackground(INPUT_BG);
        area.setForeground(TEXT_BRIGHT);
        area.setOpaque(true);
    }

    /**
     * Show a styled dialog with dark theme and high contrast text
     * Replaces default JOptionPane to fix readability issues
     */
    public static void showStyledDialog(javax.swing.JFrame parent, String message, String title, int messageType) {
        // Create a custom JPanel for the dialog content
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create styled label for the message
        javax.swing.JLabel messageLabel = new javax.swing.JLabel("<html>" + message + "</html>");
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(messageLabel);

        // Show the styled dialog
        javax.swing.JOptionPane.showMessageDialog(
            parent,
            panel,
            title,
            messageType
        );
    }

    /**
     * Show an information message with styled dialog
     */
    public static void showInfo(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Information", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a warning message with styled dialog
     */
    public static void showWarning(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show an error message with styled dialog
     */
    public static void showError(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Apply dark theme styling to a JTable (header and rows)
     */
    public static void applyDarkTableTheme(javax.swing.JTable table) {
        // Style the table header
        table.getTableHeader().setBackground(ACCENT_BUTTON);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Style the table itself
        table.setBackground(INPUT_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(TEXT_SECONDARY);
        table.setGridColor(ACCENT_BORDER);
        table.setOpaque(true);
    }
}
