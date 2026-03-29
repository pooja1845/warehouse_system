<div align="center">

# 📦 Warehouse Management System

### A modern desktop application for managing warehouse operations

![Java](https://img.shields.io/badge/Java-JDK%2011+-orange?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.9.6-red?style=for-the-badge&logo=apachemaven)
![FlatLaf](https://img.shields.io/badge/FlatLaf-3.4.1-purple?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

</div>

---

## 🎬  Demo

https://github.com/user-attachments/assets/21e42f8a-dac7-4ea8-945d-4b16ce8bc2c4



---

## ✨ Features

| Module | Description |
|--------|-------------|
| 🔐 **Login System** | Secure user authentication with username & password |
| 📊 **Dashboard** | Real-time charts — stock levels, order trends, revenue |
| 📦 **Inventory Management** | Add, update, delete products with stock tracking |
| 📥 **Inbound Management** | Record incoming stock and supplier deliveries |
| 🛒 **Orders Management** | Create and track customer orders |
| 🚚 **Shipments Tracking** | Monitor outgoing shipments and delivery status |
| 📈 **Reports** | Generate inventory and sales reports |
| ⚙️ **Settings** | Manage users, warehouse profiles, and preferences |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| **Java 11+** | Core application language |
| **Java Swing** | Desktop GUI framework |
| **MySQL 8.0** | Relational database |
| **JDBC** | Java-to-database connectivity |
| **Apache Maven** | Build tool & dependency management |
| **JFreeChart 1.5.3** | Dashboard charts and graphs |
| **FlatLaf 3.4.1** | Modern dark/light UI theme |

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:
- ✅ [Java JDK 11 or higher](https://www.oracle.com/java/technologies/downloads/)
- ✅ [MySQL Server 8.0](https://dev.mysql.com/downloads/mysql/)
- ✅ [Apache Maven 3.x](https://maven.apache.org/download.cgi) *(optional — only needed to build from source)*

---

## ▶️ Option 1: Run Directly (Easiest)

### 1. Download the JAR
Go to [**Releases**](../../releases) → Download `WarehouseSystem.jar`

### 2. Set up the Database
```sql
-- Open MySQL and run the schema file
source db_schema.sql;
```

### 3. Configure Database Connection
Edit `src/main/java/com/wms/utils/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/warehouse_db";
private static final String USER = "root";        // your MySQL username
private static final String PASSWORD = "yourpassword"; // your MySQL password
```

### 4. Run the Application
```bash
java -jar WarehouseSystem.jar
```

---

## 🔨 Option 2: Build from Source

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/warehouse_system.git
cd warehouse_system

# 2. Build the project
build.bat        # Windows — double-click or run in terminal

# 3. Run the application
run.bat          # Windows — double-click or run in terminal
```

---

## 📁 Project Structure

```
warehouse_system/
├── src/
│   └── main/
│       └── java/
│           └── com/wms/
│               ├── MainApplication.java      # Entry point
│               ├── views/                    # All GUI screens
│               │   ├── LoginView.java
│               │   ├── MainFrame.java
│               │   ├── DashboardView.java
│               │   ├── InventoryView.java
│               │   ├── InboundView.java
│               │   ├── OrdersView.java
│               │   ├── ShipmentsView.java
│               │   ├── ReportsView.java
│               │   └── SettingsView.java
│               ├── models/                   # Data models
│               │   ├── Product.java
│               │   ├── Order.java
│               │   ├── OrderItem.java
│               │   ├── Shipment.java
│               │   └── User.java
│               ├── dao/                      # Database Access Objects
│               ├── controllers/              # Business logic
│               └── utils/                    # Utilities (DB connection etc.)
├── db_schema.sql                             # Database schema
├── pom.xml                                   # Maven dependencies
├── build.bat                                 # Windows build script
└── run.bat                                   # Windows run script
```

---

## 🗄️ Database Schema

```sql
-- Core tables used in this system:
-- users          → login credentials & roles
-- products       → warehouse inventory items
-- orders         → customer orders
-- order_items    → line items per order
-- shipments      → outgoing deliveries
-- inbound        → incoming stock records
```

> Full schema: [`db_schema.sql`](db_schema.sql)

---

## 📸 Screenshots


| Login Screen |
|:---:|
| <img width="1919" height="1014" alt="Screenshot 2026-03-29 124508" src="https://github.com/user-attachments/assets/7806f849-b20c-4546-8b2e-9c076616136f" />
| Dashboard |
 | <img width="1919" height="1018" alt="Screenshot 2026-03-29 124611" src="https://github.com/user-attachments/assets/37a5c3bf-484d-4733-8a7f-9b63deb1018b" />
|

| Inventory |
|:---:|
| <img width="1919" height="1016" alt="Screenshot 2026-03-29 124919" src="https://github.com/user-attachments/assets/9432f180-a591-4acb-be9b-82a2dfe1068a" />
| Orders |
| <img width="1919" height="1017" alt="image" src="https://github.com/user-attachments/assets/28a908bc-8e8c-4968-943e-a04b2cf084a9" />


---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature/new-feature`
5. Open a Pull Request

---

## 👩‍💻 Author

**Pooja**
- GitHub: [pooja1845](https://github.com/pooja1845)

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
⭐ If you found this project helpful, please give it a star!
</div>
