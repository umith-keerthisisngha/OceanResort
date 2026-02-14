# Ocean Resort Management System - Modular Version

## Project Structure

```
resort/
├── Reservation.java           - Data model
├── Constants.java             - Theme colors and constants  
├── CustomComponents.java      - Reusable UI components
├── LoginPage.java             - Login screen
├── DashboardPanel.java        - Dashboard panel
├── ReservationPanel.java      - New reservation form
├── ReportsPanel.java          - Reports panel
├── HelpPanel.java             - Help panel
└── OceanResortSystemMain.java - Main application class
```

## How to Compile and Run

### Option 1: Using Command Line

```bash
# Navigate to the parent directory of 'resort' folder
cd /path/to/your/project

# Compile all Java files
javac resort/*.java

# Run the application
java resort.OceanResortSystemMain
```

### Option 2: Using IDE (Eclipse/IntelliJ/NetBeans)

1. Create a new Java project
2. Create a package named `resort`
3. Copy all `.java` files into the `resort` package folder
4. Run `OceanResortSystemMain.java`

## File Dependencies

- **OceanResortSystemMain.java** depends on:
  - Reservation.java
  - Constants.java
  - CustomComponents.java
  - LoginPage.java
  - DashboardPanel.java
  - ReservationPanel.java
  - ReportsPanel.java
  - HelpPanel.java

- **All other files** depend on:
  - Reservation.java
  - Constants.java
  - CustomComponents.java

## Login Credentials

- Username: `admin`
- Password: `123`

## Features

✅ Login system
✅ Dashboard with statistics
✅ Create new reservations
✅ View all bookings
✅ Search and filter
✅ Check-out and cancel reservations
✅ Generate invoices
✅ Reports and analytics
✅ Help documentation
✅ Auto-status detection (Upcoming/Active/Completed)

## System Requirements

- Java 8 or higher
- Swing GUI library (included in JDK)

## Notes

- All data is saved to `reservations.dat` file
- The file is created automatically in the application directory
- Data persists between sessions
