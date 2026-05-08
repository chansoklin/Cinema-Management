# 🎬 Cinema Management System

A desktop-based Cinema Management System built using Java Swing and MySQL.

This project was developed for our Object-Oriented Programming final project. The system simulates real cinema operations including movie scheduling, seat booking, food ordering, ticket management, and sales tracking.

The application contains both a staff management system and a customer booking portal.

---

# 📌 Project Overview

The system has two main parts:

## 👨‍💼 Staff Management System
Used by cinema employees and administrators.

Different roles have different permissions:
- Admin
- Manager
- Front Desk Staff

Staff members can:
- Manage movies
- Create schedules
- Sell tickets
- Process refunds
- Manage users
- Track sales data

---

## 🎫 Customer Booking Portal

Customers can:
- Browse currently available movies
- View movie posters and showtimes
- Select seats visually
- Order snacks and drinks
- Generate electronic tickets
- View booking history

---

# ✨ Main Features

## 🎥 Movie Management
- Add, update, and delete movies
- Store movie details in MySQL
- Display movie posters from `resources/icons/`

## 🕒 Showtime & Schedule Management
- Create movie schedules
- Assign movies to cinema screens
- Manage daily showtimes

## 💺 Seat Booking System
- Visual seat selection interface
- Different seat states:
  - Green = Available
  - Red = Booked
  - Yellow = Selected
- Database seat validation

## 🍿 Food Ordering
Customers can add:
- Popcorn
- Drinks
- Combo meals

Food orders are attached to bookings.

## 🎟 Ticket System
- Ticket generation after payment
- Ticket history tracking
- PDF ticket export
- QR code generation

## 📊 Sales Dashboard
Managers can:
- View sales statistics
- Check top-selling movies
- Monitor daily schedules
- Track recent ticket sales

---

# 🏗 System Architecture

The project follows a layered architecture to separate responsibilities.

## Structure Overview

- **Model Layer** → Entity and data classes
- **DAO Layer** → Database operations
- **Service Layer** → Business logic
- **Controller Layer** → Handles interactions between UI and services
- **Panel/UI Layer** → Java Swing user interface

This structure helped make the project easier to organize and maintain.

---

# 📂 Project Structure

```bash
Cinema-Management-System/
│
├── src/
│   ├── model/
│   ├── dao/
│   ├── service/
│   ├── controller/
│   ├── panels/
│   ├── ui/
│   ├── util/
│   ├── enums/
│   ├── exception/
│   └── resources/
│
├── README.md
└── .gitignore

# 🖥️ Useful Commands

## Connect to MySQL

Start and connect to MySQL:

```bash
mysql -u root -p
```

---
## Compile and Run the Project

Compile all Java files and run the application in one command:

```bash
javac -cp "lib/mysql-connector-java-8.0.33.jar" -d out $(find src -name "*.java") && java -cp "lib/mysql-connector-java-8.0.33.jar:out" main.Main
```

This command:
- Compiles all Java source files
- Stores compiled `.class` files inside the `out/` folder
- Runs the main application

---
