# 🎬 Movie Ticket Booking System - OOAD Mini Project

A modern movie ticket booking application built with Spring Boot (Backend) and Thymeleaf + HTML5 (Frontend), demonstrating Object-Oriented Analysis & Design principles with 4 key design patterns.

---

## 📁 Project Structure

```
movie-ticket-booking/
│
├── backend/                              # Spring Boot Java Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/moviebooking/    # Source code
│   │   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── service/              # Business Logic
│   │   │   │   ├── repository/           # Database Access (JPA)
│   │   │   │   ├── model/                # Entity Models
│   │   │   │   ├── pattern/              # Design Patterns
│   │   │   │   │   ├── factory/          # Factory Method Pattern
│   │   │   │   │   ├── facade/           # Facade Pattern
│   │   │   │   │   ├── state/            # State Pattern
│   │   │   │   │   └── builder/          # Builder Pattern
│   │   │   │   ├── config/               # Spring Configuration
│   │   │   │   └── interceptor/          # Auth Interceptors
│   │   │   └── resources/
│   │   │       ├── templates/            # Thymeleaf HTML Pages
│   │   │       ├── static/               # CSS, JS, Images
│   │   │       ├── application.properties # Configuration
│   │   │       └── data.sql              # Initial Database Data
│   │   └── test/                         # Unit Tests (if any)
│   ├── pom.xml                           # Maven Dependencies
│   ├── target/                           # Compiled Output (ignore)
│   └── restart.bat                       # Windows Restart Script
│
├── frontend/                              # Frontend Source Files
│   ├── pages/                            # HTML Pages
│   │   ├── login.html                    # Login/Register Page
│   │   ├── admin.html                    # Admin Dashboard
│   │   ├── admin-login.html              # Admin Login
│   │   ├── movies.html                   # Movie Listing
│   │   ├── booking.html                  # Seat Selection
│   │   ├── confirmation.html             # Booking Confirmation
│   │   └── index.html                    # Welcome Page
│   ├── styles/                           # CSS Files
│   ├── scripts/                          # JavaScript Files
│   └── assets/                           # Images, Icons, etc.
│       └── images/
│
├── docs/                                  # Documentation
│   ├── DESIGN_PATTERNS_IMPLEMENTATION.md # Pattern Details
│   ├── DESIGN_PRINCIPLES.md              # SOLID Principles
│   └── API_DOCUMENTATION.md              # API Endpoints (if needed)
│
└── README.md                              # This File

```

---

## 🏗️ Architecture Overview

### Backend Structure (Spring Boot)
- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Manage database operations (JPA)
- **Models**: Entity classes mapped to database tables
- **Design Patterns**: Implementations of Factory, Facade, State, Builder patterns
- **Config**: Spring Security, Auth Interceptors
- **Interceptors**: Protect routes with authentication

### Frontend Structure (Thymeleaf + HTML5)
- **Pages**: Responsive HTML templates served by Spring Boot
- **Styles**: CSS files for styling (linked in HTML)
- **Scripts**: JavaScript for client-side interactions
- **Assets**: Images and media resources

---

## 🚀 Running the Application

### Prerequisites
- Java 25.0.2+
- Maven 3.8+
- MySQL 8.0+

### Steps to Run

**1. Navigate to backend folder:**
```bash
cd backend
```

**2. Run the application:**
```bash
mvn spring-boot:run
```

**3. Access in browser:**
```
http://localhost:9090
```

**4. Login Credentials:**
- **Admin**: `admin@moviebooking.com` / `admin@123`
- **Customer**: Register a new account

---

## 🎯 Design Patterns Implemented

### 1. **Factory Method** (`backend/src/main/java/com/moviebooking/pattern/factory/`)
- Creates User objects (Customer, Admin)
- Encapsulates object creation logic
- File: `UserFactory.java`

### 2. **Facade** (`backend/src/main/java/com/moviebooking/pattern/facade/`)
- Simplifies booking process complexity
- Coordinates multiple services
- File: `BookingFacade.java`

### 3. **State** (`backend/src/main/java/com/moviebooking/pattern/state/`)
- Manages booking state transitions
- Different behavior for each state
- Files: `BookingState.java`, `PendingState.java`, `ConfirmedState.java`, etc.

### 4. **Builder** (`backend/src/main/java/com/moviebooking/pattern/builder/`)
- Constructs complex Booking objects
- Step-by-step object creation
- File: `BookingBuilder.java`

---

## 🔐 Authentication & Authorization

| User Type | Login URL | Access |
|-----------|-----------|--------|
| **Admin** | `/admin/login-page` | Dashboard, Movie Management, Reports |
| **Customer** | `/login-page` | Browse Movies, Book Tickets, View Bookings |

- **Session-based** authentication using HttpSession
- Routes protected by **Interceptors**
- Redirects unauthorized users to login

---

## 📊 Database Tables

| Table | Purpose |
|-------|---------|
| `users` | Customer & Admin accounts |
| `movies` | Movie catalog |
| `shows` | Show timings per theatre |
| `theatres` | Theatre information |
| `seats` | Seat inventory |
| `bookings` | Ticket bookings |
| `payments` | Payment records |

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.2.0, Spring Data JPA |
| **Database** | MySQL 8.0 |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript |
| **Server** | Apache Tomcat (Embedded) |
| **Build Tool** | Maven 3.8+ |
| **Language** | Java 25 |

---

## 📝 Key Files to Know

| File | Purpose |
|------|---------|
| `backend/pom.xml` | Maven dependencies |
| `backend/src/main/resources/application.properties` | App config |
| `backend/src/main/resources/data.sql` | Initial test data |
| `frontend/pages/login.html` | Login page template |
| `frontend/pages/admin.html` | Admin dashboard |
| `backend/src/main/.../SecurityConfig.java` | Auth configuration |
| `backend/src/main/.../BookingFacade.java` | Booking logic |

---

## 🔄 Development Workflow

1. **Frontend Changes**: Edit files in `frontend/pages/`, `frontend/styles/`, `frontend/scripts/`
2. **Backend Changes**: Edit files in `backend/src/main/java/`
3. **Database Changes**: Update `backend/src/main/resources/data.sql`
4. **Configuration Changes**: Edit `backend/src/main/resources/application.properties`
5. **Recompile**: Run `mvn clean package` (optional, Spring Boot DevTools auto-reloads)

---

## 📚 Documentation Files

- **`DESIGN_PATTERNS_IMPLEMENTATION.md`** - Detailed explanation of 4 design patterns with code examples
- **`DESIGN_PRINCIPLES.md`** - All 11 OOAD design principles and their application

---

## ✅ Features

✅ User Authentication & Authorization  
✅ Movie Browsing with Posters  
✅ Seat Selection by Type (Regular, Premium, VIP)  
✅ Booking Management  
✅ Multiple Payment Methods  
✅ Admin Dashboard with Analytics  
✅ 4 Design Patterns Implementation  
✅ Responsive UI  
✅ Session-Based Security  

---

## 📞 Support

For issues or questions about the project structure or implementation, refer to:
- `docs/DESIGN_PATTERNS_IMPLEMENTATION.md` - Pattern usage
- `docs/DESIGN_PRINCIPLES.md` - Design principles

---

**Last Updated**: April 16, 2026  
**Version**: 1.0.0  
**Project**: Movie Ticket Booking System - OOAD Mini Project
