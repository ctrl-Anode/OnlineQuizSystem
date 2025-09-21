# Eclipse Setup Instructions for Online Quiz System

## Prerequisites
1. **Eclipse IDE** (2023-03 or later recommended)
2. **Java 8 or higher** (Java 11+ recommended)
3. **MySQL Server** (8.0 or later) or MariaDB
4. **XAMPP** (if using local MySQL through XAMPP)

## Setting up the Project in Eclipse

### Step 1: Create New Java Project
1. Open Eclipse IDE
2. Go to `File` → `New` → `Java Project`
3. Project name: `OnlineQuizSystem`
4. Use default location or choose your desired location
5. Select `Create module-info.java file` → **UNCHECK** this option
6. Click `Finish`

### Step 2: Import Source Code
1. Right-click on your project in Package Explorer
2. Select `Import...` → `General` → `File System`
3. Browse to your OnlineQuizSystem directory
4. Select the `src` folder and click `OK`
5. Make sure all `.java` files are selected
6. Click `Finish`

### Step 3: Add MySQL Connector JAR
1. Right-click on your project → `Properties`
2. Go to `Java Build Path` → `Libraries` tab
3. Click `Add External JARs...`
4. Navigate to your project folder and select `lib/mysql-connector-j-8.0.33.jar`
5. Click `Open` and then `Apply and Close`

### Step 4: Database Setup
1. Start your MySQL server (through XAMPP or standalone)
2. Open phpMyAdmin or MySQL Workbench
3. Import the `quiz_system.sql` file to create the database schema
4. Note your database connection details:
   - Host: `localhost`
   - Port: `3306` (default)
   - Database: `quiz_system`
   - Username: `root` (or your MySQL username)
   - Password: (your MySQL password, blank for XAMPP default)

### Step 5: Configure Database Connection
1. Open `src/onlinequizsystem/DBConnection.java`
2. Update the connection details if needed:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/quiz_system";
   private static final String USERNAME = "root";
   private static final String PASSWORD = ""; // Update if you have a password
   ```

### Step 6: Run the Application
1. Right-click on `Main.java` in Package Explorer
2. Select `Run As` → `Java Application`
3. The application should start with the improved modern UI

## Common Eclipse Issues and Solutions

### Issue 1: "Module not found" errors
**Solution:** Make sure you didn't create a module-info.java file. If you did, delete it.

### Issue 2: MySQL connector not found
**Solution:** 
- Verify the JAR file is in the `lib` folder
- Check that it's added to the build path
- Clean and rebuild the project (`Project` → `Clean`)

### Issue 3: Database connection errors
**Solution:**
- Ensure MySQL server is running
- Check connection parameters in DBConnection.java
- Verify the database schema is imported correctly

### Issue 4: UI not displaying correctly
**Solution:**
- Ensure you're using Java 8 or higher
- Try different Look and Feel if needed
- Clean and rebuild the project

## Project Structure in Eclipse
```
OnlineQuizSystem/
├── src/
│   └── onlinequizsystem/
│       ├── Main.java (Entry point with landing page)
│       ├── LoginPanel.java (Modern login interface)
│       ├── RegisterPanel.java (Modern registration)
│       ├── StudentDashboard.java (Student interface)
│       ├── InstructorDashboard.java (Instructor interface)
│       ├── DBConnection.java (Database configuration)
│       └── [other .java files]
├── lib/
│   └── mysql-connector-j-8.0.33.jar
├── quiz_system.sql (Database schema)
└── bin/ (compiled classes - auto-generated)
```

## Features of the Improved Design
- **Modern Color Scheme:** Professional blue, green, and gray palette
- **Icons:** Emoji icons for better visual appeal
- **Card-based Layout:** Clean, modern card designs
- **Responsive Buttons:** Hover effects and modern styling
- **Better Typography:** Segoe UI font family for better readability
- **Improved Spacing:** Proper margins and padding throughout
- **Professional Navigation:** Clean sidebar and top navigation
- **User Feedback:** Better error messages and success notifications

## Default Login Credentials
After importing the database, you can create test accounts through the registration form, or add them directly to the database.

## Need Help?
If you encounter any issues:
1. Check the Eclipse Error Log (`Window` → `Show View` → `Error Log`)
2. Ensure all files are properly imported
3. Verify database connection parameters
4. Make sure MySQL server is running and accessible