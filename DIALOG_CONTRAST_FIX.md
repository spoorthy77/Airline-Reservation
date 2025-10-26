# Dialog Box Contrast Fix - October 26, 2025

## Problem
The font color and dialog box background color were too similar, making text inside dialog boxes unreadable. This was particularly noticeable in the dark theme dialogs where the text was not distinguishable from the background.

## Root Cause
The default `JOptionPane` dialogs use the system's look and feel, which doesn't respect the custom dark theme colors defined in `ThemeManager`. This resulted in poor contrast between text and background.

## Solution
Created custom styled dialog methods in the `ThemeManager` class that:
1. Create a custom `JPanel` with the dark theme background color (`DARK_BG`)
2. Style the message label with bright, high-contrast text color (`TEXT_PRIMARY` - white)
3. Use the `JOptionPane` to display the custom panel instead of plain text

## Changes Made

### 1. ThemeManager.java
Added four new public static methods:
- `showStyledDialog(JFrame parent, String message, String title, int messageType)` - Generic method for styled dialogs
- `showInfo(JFrame parent, String message)` - Information dialogs
- `showWarning(JFrame parent, String message)` - Warning dialogs  
- `showError(JFrame parent, String message)` - Error dialogs

These methods ensure all dialog messages use:
- **Background Color**: `DARK_BG` (34, 34, 34) - Dark gray
- **Text Color**: `TEXT_PRIMARY` (White) - High contrast with dark background
- **Font**: Arial, 14pt plain

### 2. Updated Files to Use New Methods
Replaced all `JOptionPane.showMessageDialog()` calls with appropriate `ThemeManager` methods:

#### Login.java
- Input validation warning → `ThemeManager.showWarning()`
- Login success → `ThemeManager.showInfo()`
- Login failure → `ThemeManager.showError()`
- Database errors → `ThemeManager.showError()`

#### ViewFlightDetails.java
- Database errors → `ThemeManager.showError()`
- Selection warnings → `ThemeManager.showWarning()`
- No flights found → `ThemeManager.showInfo()`
- Booking errors → `ThemeManager.showError()`
- Booking success → `ThemeManager.showInfo()`

#### ViewBookings.java
- Loading errors → `ThemeManager.showError()`

#### JourneyDetails.java
- Input validation → `ThemeManager.showWarning()`
- PNR not found → `ThemeManager.showInfo()`
- Database errors → `ThemeManager.showError()`
- General errors → `ThemeManager.showError()`

#### FlightInfo.java
- Input validation → `ThemeManager.showWarning()` (kept original for consistency)
- Success message → `ThemeManager.showInfo()`
- Database errors → `ThemeManager.showError()`

## Benefits
✅ **High Contrast**: White text on dark background provides excellent readability
✅ **Consistent Theming**: All dialogs now follow the application's dark theme
✅ **Better Accessibility**: Easier for users with vision sensitivity to read messages
✅ **Professional Appearance**: Unified styling across the application
✅ **Easy Maintenance**: Single location to update dialog styling (ThemeManager)

## Testing
Test the following scenarios to verify the fix:
1. Login with invalid credentials → Error message should be clearly readable
2. Search flights without selecting options → Warning message should be clear
3. Attempt to book without selecting a flight → Warning message should be clear
4. Book a flight successfully → Success message should be clearly visible
5. Database connection errors → Error messages should be easily readable

## Technical Details
The custom dialogs use HTML rendering in the JLabel to support:
- Multi-line messages (if needed)
- Formatted text
- Future enhancements like color-coded messages
