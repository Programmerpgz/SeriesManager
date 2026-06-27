# 🎬 SeriesManager

A full-stack desktop application for managing TV series, users, actors, directors, and personal watchlists.

Built using **Java 21**, **JavaFX**, **PostgreSQL**, and **Maven**, the application demonstrates layered architecture, database integration, authentication, XML data import, and persistent activity tracking.

---

## ✨ Features

- 🔐 Secure authentication system (Admin & User roles)
- 📺 Complete TV series management (Create, Read, Update, Delete)
- 🎭 Actor and director management
- ⭐ Personal watchlist for each user
- 📥 Import TV series from an external XML source
- 🗄️ PostgreSQL database integration
- 📊 Persistent user activity logging
- 🖥️ Modern JavaFX desktop interface

---

# 🏗️ Architecture

The application follows a layered architecture to separate presentation, business logic, and data access.

```
+----------------------------+
|        JavaFX UI           |
| (Views & Controllers)      |
+-------------+--------------+
              |
              ▼
+----------------------------+
|       Service Layer        |
|   Business Logic & Rules   |
+-------------+--------------+
              |
              ▼
+----------------------------+
|         DAO Layer          |
|    Database Access Logic   |
+-------------+--------------+
              |
              ▼
+----------------------------+
|    PostgreSQL Database     |
+----------------------------+
```

---

# 🛠️ Tech Stack

| Layer | Technology |
|--------|------------|
| Language | Java 21 |
| UI | JavaFX |
| Build Tool | Maven |
| Database | PostgreSQL |
| XML Parsing | Jackson XML |
| Logging | Logback |

---

# 📂 Project Structure

```
SeriesManager
│
├── assets/                     # Images and UI resources
├── src/
│   ├── main/
│   │   ├── java/               # Application source code
│   │   └── resources/
│   │       ├── SQL/            # Database scripts
│   │       ├── config.xml      # Database configuration
│   │       └── ...
│
├── user_activity.xml           # User activity logs
├── pom.xml
└── README.md
```

---

# ⚙️ Requirements

Before running the application, make sure you have installed:

- Java JDK 21
- Apache Maven
- PostgreSQL
- Internet connection *(only required for XML import)*

---

# 🗄️ Database Setup

## 1. Create the database

```sql
CREATE DATABASE series_manager_db;
```

## 2. Import the database

Run:

```bash
psql -U postgres -d series_manager_db -f src/main/resources/SQL/create_database_full.sql
```

The SQL script automatically creates:

- Tables
- Relationships
- Constraints
- Sequences
- Stored procedures
- Functions
- Initial data
- Default users

---

# 🔐 Environment Variables

## Windows PowerShell

```powershell
$env:DB_USER="postgres"
$env:DB_PASSWORD="your_password"
```

## IntelliJ IDEA

```
DB_USER=postgres
DB_PASSWORD=your_password
```

---

# ⚙️ Configuration

Database connection settings are stored in:

```
src/main/resources/config.xml
```

Example:

```xml
<dbUrl>jdbc:postgresql://localhost:5432/series_manager_db</dbUrl>
```

---

# 🚀 Running the Application

## IntelliJ IDEA

1. Open the project
2. Set **JDK 21**
3. Import Maven dependencies
4. Configure environment variables
5. Run:

```
hr.algebra.main.Main
```

---

## Maven

```bash
mvn clean javafx:run
```

---

# 👤 Default Users

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| User | macak | macak1 |

---

# 📥 XML Import

The application can import TV series from an external XML source.

> **Note:** Internet connection is required only for this feature.

If the application is offline, all other functionality remains fully operational.

---

# 📊 Database Model

```
Users
│
├── Roles
│
├── Watchlists
│      │
│      └── Series
│              │
│              ├── Actors
│              └── Directors
```

---

# 📁 User Activity

Every login and important user action is stored in

```
user_activity.xml
```

allowing persistent activity tracking.

---

# ⚠️ Common Issues

### Database connection failed

- Verify PostgreSQL is running
- Check database name
- Verify `config.xml`
- Confirm `DB_USER` and `DB_PASSWORD`

---

### SQL errors ("already exists")

Import the SQL dump into an empty database.

---

### Maven or Java issues

Ensure:

- Java JDK 21 is installed
- Maven dependencies are downloaded
- JAVA_HOME points to JDK 21

---

# 📝 Notes

- The **assets/** folder must remain in the project root.
- XML import requires an active internet connection.
- The database dump was generated using **PostgreSQL 18.3**.
- This project was developed as a university full-stack desktop application using JavaFX and PostgreSQL.

---

# 📄 License

This project was developed for educational purposes as part of a university coursework.
