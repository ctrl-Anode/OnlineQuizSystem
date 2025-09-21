# Online Quiz System

A comprehensive Java Swing-based quiz management system that allows instructors to create and manage quizzes while students can take them and view their results. The application features a modern UI with a professional color scheme and intuitive navigation.

## Features

### For Students 👨‍🎓
- **Join Instructor Classes**: Connect with instructors using unique codes
- **Take Interactive Quizzes**: Multiple choice and true/false questions
- **View Detailed Results**: See scores and correct answers after completion
- **Track Progress**: Monitor quiz history and performance over time

### For Instructors 👨‍🏫
- **Create and Manage Quizzes**: Build custom quizzes with various question types
- **Student Management**: View and manage enrolled students
- **Monitor Progress**: Track student performance and quiz analytics
- **Activity Logs**: Monitor student quiz activities and timestamps
- **Question Management**: Add, edit, and delete quiz questions with multiple options

### System Features
- **Modern UI Design**: Clean, professional interface with card-based layouts
- **Secure Authentication**: Role-based login system for students and instructors
- **Database Integration**: MySQL backend for reliable data storage
- **Real-time Updates**: Live tracking of quiz attempts and results

## Technology Stack

- **Frontend**: Java Swing with modern UI components
- **Backend**: Java with JDBC
- **Database**: MySQL 8.0+ / MariaDB
- **IDE Support**: VS Code, Eclipse IDE, Terminal
- **Build**: Java 8+ compatible

## Prerequisites

Before running the application, ensure you have:

1. **Java Development Kit (JDK) 8 or higher**
   - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

2. **MySQL Server 8.0+ or MariaDB**
   - Option A: [XAMPP](https://www.apachefriends.org/) (includes MySQL)
   - Option B: [Standalone MySQL](https://dev.mysql.com/downloads/mysql/)

3. **IDE (Choose one)**
   - [Visual Studio Code](https://code.visualstudio.com/) with Java extensions
   - [Eclipse IDE](https://www.eclipse.org/downloads/)
   - Terminal/Command Prompt (for direct execution)
   - IntelliJ IDEA or any Java IDE

## Installation & Setup

### 1. Database Setup

#### Using XAMPP:
1. Download and install [XAMPP](https://www.apachefriends.org/)
2. Start the XAMPP Control Panel
3. Start **Apache** and **MySQL** services
4. Open **phpMyAdmin** in your browser: `http://localhost/phpmyadmin`
5. Create a new database named `quiz_system`
6. Import the database schema:
   - Click on the `quiz_system` database
   - Go to the **Import** tab
   - Choose the [`quiz_system.sql`](quiz_system.sql) file from the project root
   - Click **Go** to import

#### Using Standalone MySQL:
1. Install MySQL Server
2. Open MySQL Workbench or command line
3. Create database: `CREATE DATABASE quiz_system;`
4. Import the schema: `mysql -u root -p quiz_system < quiz_system.sql`

### 2. Project Setup

#### Option A: Using Visual Studio Code (Recommended)

1. **Install Required Extensions**
   - Open VS Code
   - Install the following extensions:
     - **Extension Pack for Java** (includes Language Support for Java, Debugger for Java, Test Runner for Java, Maven for Java, Project Manager for Java, and Visual Studio IntelliCode)
     - **MySQL** (optional, for database management)

2. **Clone or Download the Project**
   ```bash
   git clone <repository-url>
   # or download and extract the ZIP file
   ```

3. **Open Project in VS Code**
   - Launch VS Code
   - Go to `File` → `Open Folder`
   - Select the `OnlineQuizSystem` folder
   - VS Code will automatically detect it as a Java project

4. **Configure Java Project**
   - Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
   - Type `Java: Configure Classpath`
   - Add the MySQL connector JAR:
     - Click on **Referenced Libraries**
     - Click the **+** button
     - Navigate to `lib/mysql-connector-j-8.0.33.jar`
     - Select and add it

5. **Set Up Launch Configuration**
   - Create `.vscode/launch.json` in your project root:
   ```json
   {
       "version": "0.2.0",
       "configurations": [
           {
               "type": "java",
               "name": "Launch OnlineQuizSystem",
               "request": "launch",
               "mainClass": "onlinequizsystem.Main",
               "projectName": "OnlineQuizSystem",
               "classPaths": [
                   "${workspaceFolder}/lib/mysql-connector-j-8.0.33.jar"
               ]
           }
       ]
   }
   ```

6. **Configure Settings (Optional)**
   - Create `.vscode/settings.json`:
   ```json
   {
       "java.project.sourcePaths": ["src"],
       "java.project.outputPath": "bin",
       "java.project.referencedLibraries": [
           "lib/**/*.jar"
       ]
   }
   ```

#### Option B: Using Eclipse IDE

1. **Clone or Download the Project**
   ```bash
   git clone <repository-url>
   # or download and extract the ZIP file
   ```

2. **Import Project in Eclipse**
   - Open Eclipse IDE
   - Go to `File` → `Import` → `Existing Projects into Workspace`
   - Browse to the project folder and select it
   - Click **Finish**

3. **Add MySQL Connector**
   - Right-click on the project → `Properties`
   - Go to `Java Build Path` → `Libraries` tab
   - Click `Add External JARs...`
   - Navigate to `lib/mysql-connector-j-8.0.33.jar` and select it
   - Click **Apply and Close**

#### Option C: Terminal/Command Line Setup

1. **Download/Clone the Project**
   ```bash
   # Clone repository
   git clone <repository-url>
   
   # Or download and extract ZIP file
   # Then navigate to the project directory
   cd OnlineQuizSystem
   ```

2. **Verify Java Installation**
   ```bash
   # Check Java version
   java -version
   javac -version
   
   # Should show Java 8 or higher
   ```

3. **Verify Project Structure**
   ```bash
   # List project contents
   ls -la  # On macOS/Linux
   dir     # On Windows
   
   # Should see: src/, lib/, quiz_system.sql, README.md
   ```

### 3. Database Configuration

1. Open `src/onlinequizsystem/DBConnection.java`
2. Update the database connection settings if needed:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/quiz_system";
   private static final String USER = "root";  // Your MySQL username
   private static final String PASS = "";      // Your MySQL password (blank for XAMPP default)
   ```

## Running the Application

### Option 1: Using Terminal/Command Line (Quick Start)

#### Step 1: Open Terminal/Command Prompt
```bash
# Navigate to project directory
cd /path/to/OnlineQuizSystem

# On Windows (if using Command Prompt)
cd C:\path\to\OnlineQuizSystem
```

#### Step 2: Compile the Project
```bash
# Create bin directory (if it doesn't exist)
mkdir bin                    # On macOS/Linux
md bin                       # On Windows

# Compile all Java files
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java
```

#### Step 3: Run the Application
```bash
# Run on macOS/Linux
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main

# Run on Windows
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
```

#### One-Line Commands (for convenience):

**For macOS/Linux:**
```bash
# Compile and run in one go
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java && java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
```

**For Windows:**
```cmd
# Compile and run in one go
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java && java -cp "bin;lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
```

#### Quick Run Script (Optional)

Create a run script for easier execution:

**For macOS/Linux - create `run.sh`:**
```bash
#!/bin/bash
echo "🚀 Starting Online Quiz System..."
echo "📁 Creating bin directory..."
mkdir -p bin

echo "🔧 Compiling Java files..."
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🎓 Launching application..."
    java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
else
    echo "❌ Compilation failed!"
    exit 1
fi
```

Make it executable and run:
```bash
chmod +x run.sh
./run.sh
```

**For Windows - create `run.bat`:**
```batch
@echo off
echo 🚀 Starting Online Quiz System...
echo 📁 Creating bin directory...
if not exist bin mkdir bin

echo 🔧 Compiling Java files...
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java

if %errorlevel% equ 0 (
    echo ✅ Compilation successful!
    echo 🎓 Launching application...
    java -cp "bin;lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
) else (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)
```

Run it by double-clicking or:
```cmd
run.bat
```

### Option 2: Using Visual Studio Code

#### Method 1: Using Run Button
1. Open `src/onlinequizsystem/Main.java`
2. Click the **Run** button that appears above the `main` method
3. The application window should open

#### Method 2: Using Debug Console
1. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
2. Type `Java: Run Java`
3. Select `onlinequizsystem.Main`

#### Method 3: Using VS Code Terminal
1. Open VS Code terminal (`Terminal` → `New Terminal`)
2. Run the following commands:
   ```bash
   # Compile (if not auto-compiled)
   javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java
   
   # Run (macOS/Linux)
   java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
   
   # Run (Windows)
   java -cp "bin;lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main
   ```

### Option 3: Using Eclipse IDE
1. Right-click on `src/onlinequizsystem/Main.java`
2. Select `Run As` → `Java Application`
3. The application window should open

## Terminal Commands Reference

### Essential Commands:

```bash
# Check Java installation
java -version
javac -version

# Navigate to project
cd OnlineQuizSystem

# List project files
ls -la          # macOS/Linux
dir             # Windows

# Create bin directory
mkdir -p bin    # macOS/Linux
md bin          # Windows

# Compile project
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin src/onlinequizsystem/*.java

# Run application (macOS/Linux)
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main

# Run application (Windows)
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.Main

# Test database connection
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.TestDBConnection

# Clean bin directory
rm -rf bin      # macOS/Linux
rmdir /s bin    # Windows
```

### Troubleshooting Commands:

```bash
# Check if Java files compiled
ls bin/onlinequizsystem/     # macOS/Linux
dir bin\onlinequizsystem\    # Windows

# Check MySQL connector
ls lib/                      # macOS/Linux
dir lib\                     # Windows

# Verify MySQL is running
mysql -u root -p             # Connect to MySQL
netstat -an | grep 3306      # Check if MySQL port is open
```

## VS Code Tips & Troubleshooting

### Common VS Code Issues:

1. **Java Extension Not Working**
   - Ensure you have JDK installed (not just JRE)
   - Restart VS Code after installing Java extensions
   - Check Java path: `Ctrl+Shift+P` → `Java: Configure Java Runtime`

2. **ClassPath Issues**
   - Verify MySQL connector is in Referenced Libraries
   - Check `.vscode/settings.json` configuration
   - Clean workspace: `Ctrl+Shift+P` → `Java: Clean Workspace`

3. **MySQL Connector Not Found**
   - Ensure `mysql-connector-j-8.0.33.jar` is in the `lib` folder
   - Refresh the project: `F5` or right-click → `Refresh`
   - Reload VS Code window: `Ctrl+Shift+P` → `Developer: Reload Window`

4. **Application Won't Start**
   - Check Java Problems panel for compilation errors
   - Verify database connection settings
   - Ensure MySQL server is running

### VS Code Shortcuts:
- **Run Java**: `Ctrl+F5`
- **Debug Java**: `F5`
- **Open Problems Panel**: `Ctrl+Shift+M`
- **Open Java Projects**: `Ctrl+Shift+P` → `Java: Open Projects`

### Terminal Troubleshooting:

1. **"javac not found" or "java not found"**
   ```bash
   # Add Java to PATH (Linux/macOS)
   export JAVA_HOME=/path/to/jdk
   export PATH=$JAVA_HOME/bin:$PATH
   
   # On Windows, add to system PATH through Control Panel
   ```

2. **"ClassNotFoundException"**
   - Verify the classpath includes the MySQL connector
   - Check that bin directory contains compiled classes
   - Ensure you're in the correct project directory

3. **"SQLException: Access denied"**
   - Check MySQL credentials in `DBConnection.java`
   - Ensure MySQL server is running
   - Verify database `quiz_system` exists

4. **"Package does not exist" errors**
   - Ensure you're compiling from the project root directory
   - Check that `src` folder contains the `onlinequizsystem` package
   - Verify all `.java` files are present

## First Time Usage

### Creating Accounts:
1. **Launch the application**
2. **Click "Create Account"** on the landing page
3. **Fill in the registration form**:
   - Full Name
   - Username
   - Email Address
   - Password
   - Role (Student or Instructor)
4. **For Instructors**: Save the generated instructor code displayed after registration

### Default Test Data:
The database includes sample data:
- **Instructor**: Username: `Anode`, Password: `anode123`, Code: `471782`
- **Student**: Username: `Ansel`, Password: `ansel123`

## How to Use

### For Students:
1. **Login** with your student credentials
2. **Join an Instructor** using their unique code
3. **Take Available Quizzes** from your instructor
4. **View Results** and detailed feedback after completion

### For Instructors:
1. **Login** with your instructor credentials
2. **Share your instructor code** with students
3. **Create Quizzes** with multiple question types
4. **Manage Questions** for each quiz
5. **Monitor Student Progress** and view detailed results
6. **Check Activity Logs** to track student engagement

## Project Structure

```
OnlineQuizSystem/
├── .vscode/                       # VS Code configuration
│   ├── launch.json               # Debug/Run configuration
│   └── settings.json             # Project settings
├── src/onlinequizsystem/          # Source code
│   ├── Main.java                  # Application entry point
│   ├── DBConnection.java          # Database connection
│   ├── LoginPanel.java            # Login interface
│   ├── RegisterPanel.java         # Registration interface
│   ├── StudentDashboard.java      # Student interface
│   ├── InstructorDashboard.java   # Instructor interface
│   ├── ManageQuizzesPanel.java    # Quiz management
│   ├── ManageQuestionsDialog.java # Question management
│   ├── TakeQuizDialog.java        # Quiz taking interface
│   ├── ResultDetailsDialog.java   # Result viewing
│   └── [other components...]
├── lib/                           # External libraries
│   └── mysql-connector-j-8.0.33.jar
├── bin/                           # Compiled classes (auto-generated)
├── run.sh                         # Linux/macOS run script (optional)
├── run.bat                        # Windows run script (optional)
├── quiz_system.sql               # Database schema
├── ECLIPSE_SETUP.md              # Eclipse setup guide
└── README.md                     # This file
```

## Database Schema

The application uses the following main tables:
- **users**: Student and instructor accounts
- **quizzes**: Quiz information and metadata
- **questions**: Quiz questions with types
- **options**: Multiple choice options
- **results**: Student quiz scores
- **student_answers**: Detailed answer records
- **quiz_logs**: Activity tracking
- **student_instructors**: Student-instructor relationships

## Development

### Setting Up Development Environment in VS Code:

1. **Install Additional Extensions** (Optional):
   - **GitLens**: Enhanced Git capabilities
   - **Bracket Pair Colorizer**: Better code readability
   - **MySQL**: Database management within VS Code

2. **Configure Git** (if using version control):
   ```bash
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

3. **Enable Auto-formatting**:
   - Install **Language Support for Java** extension
   - Go to Settings (`Ctrl+,`)
   - Search for "format on save" and enable it

### Adding New Features:
1. Follow the existing code structure and naming conventions
2. Use the established color palette for UI consistency
3. Implement proper error handling and user feedback
4. Add appropriate database constraints and validations

### Color Scheme:
- **Primary**: `#2980B9` (Blue)
- **Secondary**: `#34495E` (Dark Gray)
- **Accent**: `#2ECC71` (Green)
- **Warning**: `#E74C3C` (Red)
- **Background**: `#F7F9FC` (Light Gray)
- **Cards**: `#FFFFFF` (White)

## Troubleshooting

### Database Connection Error:
- Ensure MySQL server is running
- Check credentials in `DBConnection.java`
- Verify database `quiz_system` exists

### MySQL Connector Not Found:
- Ensure `mysql-connector-j-8.0.33.jar` is in the `lib` folder
- Check that the JAR is added to the classpath

### Application Won't Start:
- Verify Java 8+ is installed
- Check for compilation errors
- Ensure all source files are present

### Testing Database Connection:
Run `TestDBConnection.java` to verify database connectivity:
```bash
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" onlinequizsystem.TestDBConnection
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly in both VS Code, Eclipse, and terminal
5. Submit a pull request

## License

This project is created for educational purposes. Feel free to use and modify as needed.

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Review the VS Code Java extension documentation
3. Verify database schema and connections
4. Check Java and MySQL versions compatibility

### Useful Links:
- [VS Code Java Documentation](https://code.visualstudio.com/docs/languages/java)
- [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/8.0/en/)

---

**Code to Survive 🎓**

*Optimized for VS Code, Eclipse, and Terminal environments*