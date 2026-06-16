SeriesManager

SeriesManager is a JavaFX desktop application for managing TV series, users, actors, directors, and personal watchlists. The application uses a PostgreSQL database for persistence and Maven for dependency management and build automation.

📌 Features
User authentication (Admin & Standard user roles)
TV series management
Actor and director management
Personal watchlist system
XML-based data import (requires internet connection)
PostgreSQL-backed persistent storage
JavaFX modern desktop UI
🛠️ Tech Stack
Java 21
JavaFX
Maven
PostgreSQL
Jackson XML
Logback
📦 Project Structure
src/main/java – Application source code
src/main/resources/SQL – Database scripts and dump files
assets/ – Application images and static resources
config.xml – Database connection configuration
user_activity.xml – User activity tracking file
⚙️ Requirements

Before running the project, make sure you have installed:

JDK 21
Apache Maven
PostgreSQL
🗄️ Database Setup
1. Create Database

Create an empty PostgreSQL database:

CREATE DATABASE series_manager_db;
2. Import Database Dump

Run the full SQL script:

src/main/resources/SQL/create_database_full.sql

This script will create:

Tables
Sequences
Constraints
Functions & procedures
Initial application data
Default users
3. Run via Terminal
psql -U postgres -d series_manager_db -f src/main/resources/SQL/create_database_full.sql

If your PostgreSQL user is different, replace postgres accordingly.

🔐 Environment Variables

The application uses environment variables for database authentication:

Windows (PowerShell)
$env:DB_USER="postgres"
$env:DB_PASSWORD="your_password"
IntelliJ IDEA
DB_USER=postgres;DB_PASSWORD=your_password
⚙️ Configuration

Database connection is defined in:

config.xml

Example:

<dbUrl>jdbc:postgresql://localhost:5432/series_manager_db</dbUrl>
🚀 Running the Application
▶️ Option 1: IntelliJ IDEA
Open the project in IntelliJ IDEA
Ensure JDK 21 is selected
Let Maven import dependencies
Set environment variables (DB_USER, DB_PASSWORD)
Run:
hr.algebra.main.Main
▶️ Option 2: Maven (Terminal)
mvn clean javafx:run
👤 Default Application Users

After database import, the following users are available:

Admin
username: admin
password: admin123
Standard User
username: macak
password: macak1
📥 XML Data Import

The application supports importing series data from an external XML source.

⚠️ Internet connection is required for this feature.

If offline, the application will still function normally, but import will be disabled.

⚠️ Common Issues
Missing environment variables

Ensure DB_USER and DB_PASSWORD are set correctly.

Database connection failure

Check:

PostgreSQL service is running
Database series_manager_db exists
SQL script has been executed
Correct port in config.xml
SQL errors (objects already exist)

Ensure the database is empty before running create_database_full.sql.

Maven / Java issues

Ensure JDK 21 is installed and selected.

🧾 Notes
The assets/ folder must remain in the project root (required for images).
user_activity.xml stores user activity data.
SQL dump was generated from PostgreSQL 18.3.
