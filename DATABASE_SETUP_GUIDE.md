# Ocean Resort Database Setup Guide

## 📋 Prerequisites

1. **MySQL Server** installed and running
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use XAMPP/WAMP (includes MySQL)

2. **MySQL Connector/J (JDBC Driver)**
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Or download directly: `mysql-connector-java-8.0.33.jar`

## 🚀 Step-by-Step Setup

### Step 1: Install MySQL

**Option A: Using XAMPP (Recommended for beginners)**
1. Download XAMPP from https://www.apachefriends.org/
2. Install XAMPP
3. Start Apache and MySQL from XAMPP Control Panel

**Option B: Standalone MySQL**
1. Download MySQL Installer
2. Install MySQL Server
3. Remember your root password!

### Step 2: Create the Database

**Method 1: Using MySQL Command Line**
```bash
# Login to MySQL
mysql -u root -p

# Run the setup script
source /path/to/database_setup.sql
```

**Method 2: Using phpMyAdmin (if using XAMPP)**
1. Open browser: http://localhost/phpmyadmin
2. Click "Import" tab
3. Choose file: `database_setup.sql`
4. Click "Go"

**Method 3: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Open SQL Script → Select `database_setup.sql`
4. Execute (⚡ icon or Ctrl+Shift+Enter)

### Step 3: Download MySQL Connector (JDBC Driver)

1. Download from: https://dev.mysql.com/downloads/connector/j/
2. Extract the ZIP file
3. Find `mysql-connector-java-X.X.XX.jar`
4. Copy this JAR file to your project

### Step 4: Configure Database Connection

Edit `DatabaseConnection.java` and update these lines:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ocean_resort_db";
private static final String DB_USER = "root";      // Your MySQL username
private static final String DB_PASSWORD = "";      // Your MySQL password
```

**Common configurations:**
- **XAMPP default**: user=`root`, password=`` (empty)
- **MySQL default**: user=`root`, password=`your_password`
- **Custom**: user=`your_username`, password=`your_password`

### Step 5: Add JDBC Driver to Project

**Option A: Command Line**
```bash
# Compile with JDBC driver in classpath
javac -cp ".;mysql-connector-java-8.0.33.jar" resort/*.java

# Run with JDBC driver in classpath
java -cp ".;mysql-connector-java-8.0.33.jar" resort.OceanResortSystemMain
```

**Option B: Using IDE**

**IntelliJ IDEA:**
1. File → Project Structure → Libraries
2. Click "+" → Java
3. Select `mysql-connector-java-X.X.XX.jar`
4. Click OK

**Eclipse:**
1. Right-click project → Properties
2. Java Build Path → Libraries
3. Add External JARs
4. Select `mysql-connector-java-X.X.XX.jar`
5. Apply and Close

**NetBeans:**
1. Right-click project → Properties
2. Libraries → Add JAR/Folder
3. Select `mysql-connector-java-X.X.XX.jar`
4. OK

## 🔧 Database Configuration

### Database Details
```
Database Name: ocean_resort_db
Host: localhost
Port: 3306
Username: root (or your MySQL username)
Password: (your MySQL password)
```

### Tables Created
1. **reservations** - Stores all reservation data
2. **users** - Stores system users
3. **audit_log** - Tracks all changes

### Default Login Credentials
```
Username: admin
Password: 123
```

## ✅ Testing the Connection

Run this test to verify database connection:

```java
public static void main(String[] args) {
    // Test database connection
    DatabaseConnection.testConnection();
    DatabaseConnection.printConnectionInfo();
    DatabaseHandler.printDatabaseStats();
}
```

## 📊 Sample Data

The setup script includes 3 sample reservations:
- R001 - John Doe (Double room)
- R002 - Jane Smith (Suite)
- R003 - Mike Johnson (Single room)

## 🐛 Troubleshooting

### Error: "Access denied for user"
**Solution:** Check username and password in `DatabaseConnection.java`

### Error: "Communications link failure"
**Solution:** 
1. Make sure MySQL server is running
2. Check if port 3306 is open
3. Verify localhost connection

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution:** Add mysql-connector-java.jar to classpath

### Error: "Unknown database 'ocean_resort_db'"
**Solution:** Run the `database_setup.sql` script first

### Error: "Table doesn't exist"
**Solution:** Re-run the database_setup.sql script

## 📝 Important Notes

1. **Backup your data** regularly
2. Change default password in production
3. Use prepared statements (already implemented)
4. The database connection is automatically managed
5. All changes are logged in audit_log table

## 🔐 Security Recommendations (For Production)

1. Change default admin password
2. Use password hashing (bcrypt)
3. Create separate user accounts with limited privileges
4. Use environment variables for credentials
5. Enable SSL for database connections
6. Regular database backups

## 📞 Need Help?

If you encounter any issues:
1. Check MySQL server is running
2. Verify database credentials
3. Ensure JDBC driver is in classpath
4. Check MySQL error logs
5. Test connection using command line first

---

**That's it! Your database is now ready to use! 🎉**
