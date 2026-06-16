# 🎬 SeriesManager

![Java](https://img.shields.io/badge/Java-21-blue)
![Maven](https://img.shields.io/badge/Maven-Build-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-green)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

**SeriesManager** is a JavaFX desktop application for managing TV series, users, actors, directors, and personal watchlists.  
It is built as a full-stack desktop system using **JavaFX + PostgreSQL + Maven**.

---


---

## ✨ Key Features

- 🔐 Secure login system (Admin & User roles)
- 📺 TV series management (CRUD operations)
- 🎭 Actor & director management
- ⭐ Personal watchlist functionality
- 📥 XML data import (external source, requires internet)
- 🗄️ Full PostgreSQL integration
- 📊 Persistent user activity tracking
- 🖥️ Modern JavaFX UI

---

## 🧠 Architecture Overview


+------------------------+
| JavaFX UI Layer |
| (Controllers / Views) |
+-----------+------------+
|
v
+------------------------+
| Service Layer |
| Business Logic / Rules |
+-----------+------------+
|
v
+------------------------+
| DAO Layer |
| Database Access Layer |
+-----------+------------+
|
v
+------------------------+
| PostgreSQL Database |
+------------------------+


---

## 🛠️ Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java 21 |
| UI | JavaFX |
| Build Tool | Maven |
| Database | PostgreSQL |
| XML Parsing | Jackson XML |
| Logging | Logback |

---

## 📦 Project Structure


src/main/java → Application source code
src/main/resources → Configs, SQL scripts, assets
assets/ → Images & UI resources
config.xml → DB configuration
user_activity.xml → User activity logs


---

## ⚙️ Requirements

Before running the project, ensure you have:

- Java JDK 21
- Apache Maven
- PostgreSQL (running locally)
- Internet connection (for XML import feature)

---

## 🗄️ Database Setup

### 1. Create Database

```sql
CREATE DATABASE series_manager_db;
2. Import Full Database Dump

Run the SQL script:

psql -U postgres -d series_manager_db -f src/main/resources/SQL/create_database_full.sql

This will create:

Tables & relationships
Constraints
Sequences
Functions & procedures
Initial data
Default users
🔐 Environment Variables
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
▶ IntelliJ IDEA
Open project
Set JDK 21
Load Maven dependencies
Set environment variables
Run:
hr.algebra.main.Main
▶ Terminal (Maven)
mvn clean javafx:run
👤 Default Users
Admin
username: admin
password: admin123
User
username: macak
password: macak1
📥 XML Import Feature

Series data can be imported from an external XML source.

⚠ Requires internet connection.

If offline, the app still works normally.

⚠️ Common Issues
❌ Database connection failed
Check PostgreSQL service
Verify database name
Check config.xml
Ensure correct credentials
❌ Missing environment variables
Set DB_USER and DB_PASSWORD
❌ SQL errors (already exists)
Ensure database is empty before running dump
❌ Maven / Java issues
Use JDK 21
📊 Data Model Overview
Users
 ├── Roles
 ├── Watchlist
 │     └── Series
 │           ├── Actors
 │           └── Directors
🧾 Notes
assets/ folder must remain in root directory
user_activity.xml stores user activity logs
Database dump generated from PostgreSQL 18.3
Designed as a university-level full-stack desktop project
