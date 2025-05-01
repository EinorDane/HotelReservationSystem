# 🏨 Hotel Reservation System

A simple **Java-based Hotel Reservation System** that allows guests to **book rooms** and staff to **manage reservations** efficiently.

## 🚀 Features
- **User Authentication** (Login/Signup)
- **Role-Based Access** (Guest vs. Staff)
- **Room Reservation for Guests**
- **Reservation Management for Staff**
- **Secure Password Storage** (SHA-256 Hashing)

## 🛠️ Installation & Setup
### 1️⃣ Clone the Repository
```sh
git clone https://github.com/EinorDane/HotelReservationSystem.git
```

2️⃣ Set Up the Database
- Install MySQL (or use an existing database).
- Run the provided database_setup.sql script to create necessary tables.

3️⃣ Run the Application
- Compile and execute the Java program:
```sh
javac -d . src/com/hotelres/*.java
java com.hotelres.HotelReservationSystem
```
## Project Structure
```sh
HotelReservationSystem/
│── src/
│   ├── com/hotelres/
│   │   ├── model/  # Contains Guest, User, Reservation classes
│   │   ├── database/  # DAO classes for database interaction
│   │   ├── HotelReservationSystem.java  # Main entry point
│── database_setup.sql  # SQL script for database tables
│── README.md  # Documentation
```
## Known Issues
- Git Large File Storage (LFS) may be required for large database files.
- Cross-Platform Compatibility (Windows/Mac/Linux) may affect database connections.

## 📧 Contact & Contributors
Developed by EinorDane
Feel free to open an issue or submit a pull request if you’d like to contribute! 😃

---

### **Next Steps**
✅ Copy this template into a new file named **README.md** in your GitHub repository.  
✅ Customize any sections to match your specific project setup.  
✅ Commit & push it using:
```sh
git add README.md
git commit -m "Added project documentation"
git push origin main
```




