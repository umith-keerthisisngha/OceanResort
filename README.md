# 🌊 Ocean Resort Management System


---

## 📦 Package Contents

### Java Source Files (resort package):
1. **OceanResortSystem.java** - Main application (DATABASE + FILE MODE, role-based access)
2. **LoginPage.java** - Login with Admin / Staff role toggle
3. **ReservationPanel.java** - Save reservations to database
4. **Reservation.java** - Reservation data model
5. **Constants.java** - Theme colors and constants
6. **CustomComponents.java** - UI components
7. **DashboardPanel.java** - Dashboard statistics
8. **ReportsPanel.java** - Reports and analytics
9. **HelpPanel.java** - Help documentation
10. **DatabaseConnection.java** - MySQL connection manager
11. **DatabaseHandler.java** - Database operations (CRUD)

### Database Files:
12. **database_setup.sql** - Complete database schema

### Documentation:
13. **DATABASE_SETUP_GUIDE.md** - Detailed setup instructions
14. **README.md** - This file

---

## 🚀 Quick Start Guide

### Option 1: Run WITHOUT Database (File Mode)
**Perfect if you just want to test the system quickly!**

```bash
# Step 1: Compile
javac resort/*.java

# Step 2: Run
java resort.OceanResortSystem

# The system will automatically use file storage if database is not available
```

### Option 2: Run WITH Database (Full Features)
**Recommended for production use!**

**Requirements:**
- MySQL Server (or XAMPP/WAMP)
- MySQL Connector/J (JDBC Driver) — download the **.zip**, extract the **.jar**

---

## 📋 DATABASE SETUP - Step by Step

### Step 1: Install MySQL

**Option A: Using XAMPP (Easiest)**
1. Download XAMPP: https://www.apachefriends.org/
2. Install XAMPP
3. Start "Apache" and "MySQL" from XAMPP Control Panel

**Option B: Standalone MySQL**
1. Download MySQL: https://dev.mysql.com/downloads/mysql/
2. Install MySQL Server
3. Remember your root password

---

### Step 2: Create the Database

**Method 1: Using phpMyAdmin (XAMPP)**
1. Open browser: http://localhost/phpmyadmin
2. Click "Import" tab
3. Choose file: `database_setup.sql`
4. Click "Go" ✅

**Method 2: Using MySQL Command Line**
```bash
mysql -u root -p
source /path/to/database_setup.sql
SHOW DATABASES;
USE ocean_resort_db;
SHOW TABLES;
```

**Method 3: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your local MySQL server
3. File → Open SQL Script → select `database_setup.sql`
4. Execute (Ctrl+Shift+Enter)

---

### Step 3: Add Role Column to Users Table

> ⚠️ Required for the Admin / Staff login to work correctly.

Run this SQL after importing `database_setup.sql`:

```sql
-- Add role column
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'STAFF';

-- Set admin user role
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';

-- Add a staff user
INSERT INTO users (username, password, role, is_active)
VALUES ('staff', 'staff123', 'STAFF', TRUE);
```

---

### Step 4: Download MySQL JDBC Driver

> ⚠️ Download the **.zip** file — NOT the `.tar.gz` (that is for Linux only).

1. Go to: https://dev.mysql.com/downloads/connector/j/
2. Set "Select Operating System" → **Platform Independent**
3. Download the **ZIP** file
4. Extract the ZIP → find `mysql-connector-j-9.x.x.jar`
5. Place the JAR in your project folder

---

### Step 5: Add JAR to Your IDE

**Eclipse:**
1. Right-click project → Properties
2. Java Build Path → Libraries tab → Add External JARs
3. Select `mysql-connector-j-9.x.x.jar`
4. Apply and Close

**IntelliJ IDEA:**
1. File → Project Structure (Ctrl+Alt+Shift+S)
2. Modules → Dependencies → + → JARs or Directories
3. Select `mysql-connector-j-9.x.x.jar` → OK → Apply

**NetBeans:**
1. Right-click project → Properties → Libraries
2. Add JAR/Folder → select the JAR → OK

---

### Step 6: Configure Database Connection

Edit `DatabaseConnection.java` lines 8–10:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ocean_resort_db";
private static final String DB_USER = "root";       // Your MySQL username
private static final String DB_PASSWORD = "";       // Your MySQL password (empty for XAMPP default)
```

---

### Step 7: Compile and Run

**Windows:**
```cmd
javac -cp ".;mysql-connector-j-9.x.x.jar" resort\*.java
java  -cp ".;mysql-connector-j-9.x.x.jar" resort.OceanResortSystem
```

**Linux/Mac:**
```bash
javac -cp ".:mysql-connector-j-9.x.x.jar" resort/*.java
java  -cp ".:mysql-connector-j-9.x.x.jar" resort.OceanResortSystem
```

---

## 🔐 Login Credentials

### Admin Login
| Field    | Value   |
|----------|---------|
| Role     | 👑 Admin |
| Username | admin   |
| Password | 123     |

### Staff Login
| Field    | Value     |
|----------|-----------|
| Role     | 👤 Staff  |
| Username | staff     |
| Password | staff123  |



---

## 🔑 Role Permissions

| Feature            | Admin | Staff |
|--------------------|:-----:|:-----:|
| View Dashboard     | ✅    | ✅    |
| Create Reservation | ✅    | ✅    |
| View All Bookings  | ✅    | ✅    |
| Check Out Guest    | ✅    | ✅    |
| Cancel Reservation | ✅    | ✅    |
| **Delete Reservation** | ✅ | ❌ |
| Billing & Invoice  | ✅    | ✅    |
| Reports            | ✅    | ✅    |

---

## 📊 Database Schema

```
Database: ocean_resort_db

Tables:
├── reservations   — All reservation data
├── users          — System users (username, password, role, is_active)
└── audit_log      — Tracks all changes
```

---

## ✨ Features

### Automatic Mode Detection
- ✅ **Database Mode** — if MySQL is running and connected
- ✅ **File Mode** — if database is unavailable (fallback)
- ✅ **Seamless Fallback** — works either way

### Role-Based Access
- ✅ Admin / Staff toggle on login screen
- ✅ Role shown in sidebar and status bar
- ✅ Delete button hidden from Staff users
- ✅ Role resets on logout

### Database Mode Features
- ✅ Real-time data sync across sessions
- ✅ User authentication with role verification
- ✅ Audit logging (tracks all changes)
- ✅ Multi-user support ready

### File Mode Features
- ✅ No setup required
- ✅ Works immediately
- ✅ Data stored in `reservations.dat`

---

## 🔧 Troubleshooting

### `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
Add the MySQL Connector JAR to your build path (see Step 5 above).

### `Module format not recognized: *.tar.gz`
You downloaded the wrong file. Download the **ZIP**, not `.tar.gz` (see Step 4).

### `Access denied for user`
Check your username and password in `DatabaseConnection.java`.

### `Communications link failure`
MySQL server is not running. Start it via XAMPP Control Panel or `systemctl start mysql`.

### `Unknown database 'ocean_resort_db'`
Run `database_setup.sql` first (see Step 2).

### `cannot find symbol: setCurrentRole`
Add these methods to `OceanResortSystem.java`:
```java
private String currentRole = "ADMIN";
public void setCurrentRole(String role) { this.currentRole = role; }
public String getCurrentRole() { return currentRole; }
public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(currentRole); }
```

### System runs in File Mode instead of Database Mode
1. Check MySQL is running
2. Verify database: `SHOW DATABASES;`
3. Check credentials in `DatabaseConnection.java`
4. Make sure the JDBC JAR is in the classpath

---

## 🎓 Usage Guide

### Creating Reservations
1. Click "New Reservation"
2. Fill in all required fields (ID, Name, Contact)
3. Select room type and dates
4. Click "SAVE RESERVATION"

### Managing Bookings
1. Go to "All Bookings"
2. Search, filter, or sort reservations
3. Select a row and use action buttons:
   - **VIEW DETAILS** — Full reservation info
   - **CHECK OUT** — Mark guest as checked out
   - **CANCEL** — Cancel reservation
   - **DELETE** — Remove permanently *(Admin only)*

### Generating Invoices
1. Go to "Billing"
2. Enter a Reservation ID
3. Click "GENERATE INVOICE"
4. Print if needed

### Viewing Reports
1. Go to "Reports"
2. Choose report type:
   - **SUMMARY** — Overall statistics
   - **ROOM ANALYSIS** — Revenue by room type
   - **STATUS REPORT** — Bookings by status

---

## 📁 Project Structure

```
OceanResort/
│
├── resort/
│   ├── OceanResortSystem.java       ← Main app + role support
│   ├── LoginPage.java               ← Admin/Staff role toggle login
│   ├── ReservationPanel.java
│   ├── Reservation.java
│   ├── Constants.java
│   ├── CustomComponents.java
│   ├── DashboardPanel.java
│   ├── ReportsPanel.java
│   ├── HelpPanel.java
│   ├── DatabaseConnection.java
│   └── DatabaseHandler.java         ← includes getUserRole()
│
├── database_setup.sql
├── mysql-connector-j-9.x.x.jar      ← JDBC driver (ZIP → extract JAR)
├── DATABASE_SETUP_GUIDE.md
└── README.md
```

---

## 🔒 Security Notes

- Default credentials are for testing only — change them before deployment
- Use password hashing (e.g. BCrypt) for real production systems
- Limit database user permissions to only what is needed
- Take regular database backups

---

## 🎉 You're All Set!

Login with `admin` / `123` (Admin) or `staff` / `staff123` (Staff) and enjoy your Ocean Resort Management System! 🌊

---

**Version:** 3.0 — Role-Based Database Edition  
**Last Updated:** 2026
