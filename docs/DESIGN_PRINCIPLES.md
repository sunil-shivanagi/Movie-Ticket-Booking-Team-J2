# Design Principles & Patterns - Movie Ticket Booking System

## 📋 Overview
This document outlines all Object-Oriented Analysis and Design (OOAD) principles and design patterns implemented in the Movie Ticket Booking System.

---

## 1. GRASP Principles (General Responsibility Assignment Software Patterns)

### 1.1 INFORMATION EXPERT
**Definition**: Assign a responsibility to the class that has the information needed to fulfill it.

**Implementation in Project**:

#### Location: `Booking.java`
- **Assigned to**: Shrikant (PES1UG23CS565)
- **Responsibilities**:
  - `calculateTotalAmount()` - Booking has seat details and show price
  - `getBookingDetails()` - Booking knows all its data
  - `getSeatCount()`, `getSeatLabels()` - Booking owns the seats
  - `isConfirmed()`, `isPending()`, `isCancelled()` - Booking knows its status

```java
// Example: Booking calculates its own total (not BookingService)
public Double calculateTotalAmount() {
    double basePrice = show.getPrice();
    double total = 0.0;
    for (Seat seat : seats) {
        total += basePrice * seat.getPriceMultiplier();
    }
    return total;
}
```

**Benefit**: No duplicate calculation logic in services. Single source of truth.

---

### 1.2 CONTROLLER (MVC Pattern)
**Definition**: Assign responsibility to handle system events to a dedicated controller.

**Implementation in Project**:

#### Location: `CustomerController.java`
- **Assigned to**: Vikas (PES1UG23CS837)
- **Responsibilities**:
  1. Receive HTTP requests from UI
  2. Coordinate with services and domain objects
  3. Delegate business logic to services (NOT implement it)
  4. Act as facade between UI and business logic

**Controllers in Project**:
- `CustomerController` - Handles customer use cases (login, browse, book)
- `AdminAuthController` - Handles admin authentication
- `AdminController` - Handles admin dashboard operations
- `LandingPageController` - Handles authentication-first redirect flow

```java
// Example: Controller coordinates, doesn't compute
@GetMapping("/home")
public String home(HttpSession session, Model model) {
    // Controller receives event
    Long customerId = (Long) session.getAttribute("customerId");
    
    // Controller coordinates (gets data from service)
    model.addAttribute("movies", movieService.getAllMovies());
    
    // Controller doesn't do the business logic
    return "index";
}
```

**Key Methods Demonstrating Controller Pattern**:
- `listMovies()` → Coordinates filtering via MovieService
- `createBooking()` → Delegates to BookingService
- `processPayment()` → Delegates to PaymentService with polymorphism
- `login()` / `register()` → Delegates to UserService

---

### 1.3 POLYMORPHISM (Strategy Pattern)
**Definition**: Handle variations through polymorphic behavior instead of conditionals.

**Implementation in Project**:

#### Location: `PaymentService.java`
- **Assigned to**: Sunil (PES1UG23CS613)

**Problem (Before)**:
```java
// OLD WAY - Conditional logic
if (paymentMethod == CREDIT_CARD) { 
    // Credit card payment logic
} else if (paymentMethod == UPI) { 
    // UPI payment logic
} else if (paymentMethod == NET_BANKING) {
    // Net banking logic
}
```

**Solution (After)**:
- Created `PaymentStrategy` interface
- Implemented concrete strategies:
  - `CreditCardPayment`
  - `DebitCardPayment`
  - `UPIPayment`
  - `NetBankingPayment`

```java
// NEW WAY - Polymorphism, no conditionals!
PaymentStrategy strategy = strategies.get(method);
PaymentResult result = strategy.processPayment(amount, paymentDetails);
```

**Payment Methods Supported**:
1. Credit Card - with card number, CVV, expiry validation
2. Debit Card - with card number and PIN validation
3. UPI - with UPI ID validation (username@bankname)
4. Net Banking - with bank code and account validation

**Benefits**:
- ✅ Open/Closed Principle: Open for extension, closed for modification
- ✅ Adding new payment method = Add new class, no existing code changes
- ✅ Each payment type handles its own validation
- ✅ No conditional logic needed

---

### 1.4 PURE FABRICATION
**Definition**: Create a service class to handle operations that don't naturally belong to domain objects.

**Implementation in Project**:

#### Location: `ReportService.java`
- Handles analytics and reporting (doesn't fit any domain object)
- Calculates occupancy rates, revenue, booking statistics
- Purely computational/reporting responsibility

#### Location: `AuditLogService.java`
- Handles system-wide audit logging
- Tracks all user actions and system events
- Pure infrastructure responsibility

```java
// Pure Fabrication: ReportService handles reporting
// (Not part of core domain, but essential for system)
public OccupancyReportDTO getOccupancyReport() {
    // Calculates complex metrics
    // Aggregates data from multiple sources
    // Returns formatted report
}
```

---

### 1.5 HIGH COHESION
**Definition**: Group responsibilities that are strongly related and remove unrelated responsibilities.

**Implementation in Project**:

**Separated Concerns**:
- `BookingService` - Only booking operations
- `PaymentService` - Only payment operations
- `MovieService` - Only movie management
- `ShowService` - Only show management
- `UserService` - Only user authentication
- `TheatreService` - Only theatre management
- `ReportService` - Only reporting

**Result**: Each service has single, well-defined responsibility → High Cohesion

---

### 1.6 LOW COUPLING
**Definition**: Minimize dependencies between classes/services.

**Implementation in Project**:

**Achieved Through**:
1. **Dependency Injection** (Spring Framework)
   - Services injected via constructor, not hardcoded
   - Reduces direct dependencies
   - Easier to test and maintain

2. **Interface-based Design**
   - PaymentStrategy interface for payment types
   - Repository interfaces for data access
   - Allows switching implementations without code changes

3. **Repository Pattern**
   - Data access logic isolated
   - Controllers/Services don't know about database details
   - Can change database without affecting business logic

```java
// Low Coupling: Services depend on interfaces, not concrete classes
@Autowired
private BookingRepository bookingRepository; // Interface, not implementation

@Autowired
public BookingService(BookingRepository bookingRepository,
                      CustomerRepository customerRepository,
                      ShowRepository showRepository,
                      SeatRepository seatRepository) {
    // Loose coupling through constructor injection
}
```

---

## 2. Object-Oriented Design Principles

### 2.1 INHERITANCE & POLYMORPHISM
**Implementation in Project**:

#### User Hierarchy (Single Table Inheritance)
```
User (Abstract Base)
├── Customer (extends User)
└── Admin (extends User)
```

**Features**:
- `User.java` - Abstract base class with common attributes
  - `id`, `name`, `email`, `password`, `phone`
- `Customer.java` - Extends User, adds booking functionality
- `Admin.java` - Extends User, adds admin privileges

**JPA Single Table Inheritance**:
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User { ... }

@DiscriminatorValue("CUSTOMER")
public class Customer extends User { ... }

@DiscriminatorValue("ADMIN")
public class Admin extends User { ... }
```

**Benefits**:
- Single database table for all users
- Polymorphic queries possible
- Type safety through inheritance
- Shared common properties

---

### 2.2 ENCAPSULATION
**Implementation in Project**:

#### Private Data, Public Interface
- All domain object fields are `private`
- Access through `public` getters/setters
- Business logic methods are `public`
- Internal state management is `private`

```java
@Entity
public class Booking {
    // Private data
    private Long id;
    private Customer customer;
    private Show show;
    private List<Seat> seats;
    private Double totalAmount;
    private BookingStatus status;
    
    // Public interface (getters/setters)
    public Double calculateTotalAmount() { ... }
    public String getBookingDetails() { ... }
    public void confirm() { ... }
}
```

---

### 2.3 ABSTRACTION
**Implementation in Project**:

#### Abstract Base Classes
- `User` - Abstract base for all users
- `Enum` types: `BookingStatus`, `PaymentStatus`, `SeatType`, etc.

#### Interfaces
- `PaymentStrategy` - Abstract payment processing contract
- `JpaRepository<T, ID>` - Abstract data access contract

**Benefits**:
- Hide implementation details
- Expose only necessary behavior
- Clients work with abstractions, not concrete classes

---

## 3. Architectural Patterns

### 3.1 MVC (Model-View-Controller)
**Pattern Structure**:
- **Model**: Entity classes (Booking, Movie, Show, Seat, etc.)
- **View**: Thymeleaf templates (booking.html, movies.html, etc.)
- **Controller**: CustomerController, AdminController, etc.

**Data Flow**:
```
User Request → Controller → Service → Repository → Database
                   ↓           ↓           ↓
              Coordinates  Business    Data Access
              with UI      Logic       Logic
              
Database → Repository → Service → Model → View → Template → Browser
```

---

### 3.2 Repository Pattern
**Purpose**: Abstract database access logic

**Implementation**:
- `BookingRepository extends JpaRepository<Booking, Long>`
- `CustomerRepository extends JpaRepository<Customer, Long>`
- `MovieRepository extends JpaRepository<Movie, Long>`
- `ShowRepository extends JpaRepository<Show, Long>`
- `SeatRepository extends JpaRepository<Seat, Long>`
- etc.

**Benefits**:
- Decouple business logic from data access
- Easy to write unit tests (mock repositories)
- Can change database without changing business logic
- Custom queries via `@Query` annotations

---

### 3.3 Service Layer Pattern
**Purpose**: Encapsulate business logic

**Implementation**:
- `BookingService` - Booking operations
- `PaymentService` - Payment processing
- `MovieService` - Movie management
- `ShowService` - Show management
- `UserService` - User authentication
- `TheatreService` - Theatre management
- `ReportService` - Analytics and reporting

**Benefits**:
- Centralized business logic
- Reusable across multiple controllers
- Easier to test business logic
- Transaction management (@Transactional)

---

### 3.4 Strategy Pattern (for Payment Processing)
**Pattern Structure**:
```
PaymentStrategy (Interface)
├── CreditCardPayment (implements PaymentStrategy)
├── DebitCardPayment (implements PaymentStrategy)
├── UPIPayment (implements PaymentStrategy)
└── NetBankingPayment (implements PaymentStrategy)
```

**How It Works**:
1. Client specifies payment method
2. PaymentService looks up appropriate strategy
3. Strategy handles payment processing
4. Result returned to client

**Open/Closed Principle**:
- Open for extension: Add new payment method = add new strategy class
- Closed for modification: Existing code doesn't change

---

### 3.5 Session-Based Authentication Pattern
**Implementation**:

#### Flow:
1. User submits login/register form
2. `CustomerController.login()` or `register()` called
3. `UserService` validates credentials
4. On success: Session attributes set
   - `customerId` for customers
   - `adminId` for admins
   - `customerName`, `adminName` for display
5. `SecurityConfig` interceptors check session on each request
6. Unauthenticated users redirected to login page

#### Key Components:
- `SecurityConfig.java` - Admin and Customer auth interceptors
- `LandingPageController.java` - Root redirect to login-page
- `login.html` - Unified login template with 3 tabs
- `CustomerController.login()` / `AdminAuthController.login()`

---

## 4. Design Patterns Summary Table

| Pattern | Location | Purpose | Benefits |
|---------|----------|---------|----------|
| **GRASP: Information Expert** | Booking.java | Booking calculates its own total | Single source of truth |
| **GRASP: Controller** | CustomerController.java | Handle HTTP requests | Separation of concerns |
| **GRASP: Polymorphism** | PaymentService.java | Different payment types | Open/Closed Principle |
| **GRASP: Pure Fabrication** | ReportService.java | Handle reporting | Avoid bloating domain objects |
| **MVC** | Entire project | Separate model, view, controller | Organized architecture |
| **Repository** | All *Repository.java | Abstract data access | Decouple from database |
| **Service Layer** | All *Service.java | Encapsulate business logic | Reusable, testable |
| **Strategy** | PaymentService | Different payment methods | Extensible, no conditionals |
| **Dependency Injection** | Spring @Autowired | Loose coupling | Easy testing, flexible |
| **Single Table Inheritance** | User.java | Store User hierarchy | Efficient database design |

---

## 5. SOLID Principles Applied

### 5.1 Single Responsibility Principle (SRP)
- **BookingService** - Only handles bookings
- **PaymentService** - Only handles payments
- **MovieService** - Only handles movies
- Each class has ONE reason to change

### 5.2 Open/Closed Principle (OCP)
- **PaymentStrategy pattern** - Open for new payment types, closed for modification
- Adding new payment method = new class, no existing code changes

### 5.3 Liskov Substitution Principle (LSP)
- **PaymentStrategy implementations** - Any strategy can replace another
- `CreditCardPayment`, `UPIPayment`, etc. are interchangeable
- Code using strategies doesn't care which type is used

### 5.4 Interface Segregation Principle (ISP)
- **PaymentStrategy** - Segregated interface with only payment-related methods
- Clients don't depend on methods they don't use

### 5.5 Dependency Inversion Principle (DIP)
- **Services depend on repositories (interfaces)** - Not concrete implementations
- **Controllers depend on services (interfaces)** - Not specific implementations
- High-level modules don't depend on low-level details

---

## 6. Database Design Patterns

### 6.1 Single Table Inheritance
- All users (Customer, Admin) stored in single `users` table
- Discriminator column: `user_type` (CUSTOMER/ADMIN)
- Efficient, simple to query

### 6.2 Many-to-Many Relationships
- **Booking ↔ Seats** via `booked_seats` junction table
- Allows flexible seat selection

### 6.3 Cascade Operations
- Delete booking → automatically delete booked_seats records
- Maintains referential integrity

---

## 7. Enterprise Patterns

### 7.1 DTO (Data Transfer Object) - Future Use
- Designed for complex queries returning aggregated data
- Example: `OccupancyReportDTO` in ReportService

### 7.2 VO (Value Object)
- Enums: `BookingStatus`, `PaymentStatus`, `SeatType`, `PaymentMethod`
- Immutable, type-safe values

### 7.3 Entity vs Value Object
- **Entity**: Booking (has identity/ID)
- **Value Object**: BookingStatus (no identity, just value)

---

## 8. Authentication & Security Patterns

### 8.1 Session-Based Authentication
- User credentials validated at login
- Session ID created and stored
- Credentials embedded in HttpSession
- Token expires after inactivity or browser close

### 8.2 Interceptor-Based Authorization
- `SecurityConfig.java` implements `HandlerInterceptor`
- All requests to protected URLs intercepted
- Session checked before allowing access

### 8.3 Role-Based Access Control (RBAC)
- Admin routes protected with AdminAuthInterceptor
- Customer routes protected with CustomerAuthInterceptor
- Different interceptors for different roles

---

## 9. Design Decisions Rationale

### 9.1 Why Spring Boot?
- Rapid development with starter dependencies
- Built-in dependency injection (IoC container)
- Embedded Tomcat (no deployment hassles)
- Excellent for microservices architecture

### 9.2 Why JPA/Hibernate?
- ORM handles database complexity
- Object-oriented database interaction
- JPQL for type-safe queries
- Easy to switch databases

### 9.3 Why Thymeleaf?
- Server-side template engine
- Clean, HTML-like syntax
- Excellent Spring integration
- No duplication between HTML and Java

### 9.4 Why Session-Based Auth?
- Simple to implement
- No external dependencies
- Suitable for monolithic application
- CSRF protection easier to implement

### 9.5 Why Single Table Inheritance?
- Simpler than separate tables
- Single query handles all users
- Easier to add new user types later
- Less database complexity

---

## 10. Extensibility Points

### Adding New Payment Method:
1. Create new class implementing `PaymentStrategy`
2. Implement `processPayment()`, `validateDetails()`
3. Register in `PaymentService` constructor
4. Done! No changes to existing code

### Adding New User Role:
1. Create class extending `User`
2. Add `@DiscriminatorValue("NEW_ROLE")`
3. Add interceptor for new role
4. Done! All user queries still work

### Adding New Report:
1. Add method to `ReportService`
2. Add repository query if needed
3. Create DTO for report structure
4. Add controller endpoint to expose report

---

## 11. Summary

**This project demonstrates**:
- ✅ GRASP principles (Information Expert, Controller, Polymorphism, etc.)
- ✅ SOLID principles (SRP, OCP, LSP, ISP, DIP)
- ✅ MVC architectural pattern
- ✅ Repository and Service layer patterns
- ✅ Strategy pattern for payments
- ✅ Dependency injection and IoC
- ✅ Polymorphism through inheritance
- ✅ Encapsulation and abstraction
- ✅ Session-based authentication
- ✅ Single Table Inheritance for entity hierarchy

**Result**: Clean, maintainable, extensible codebase following industry best practices!

---

**Generated**: 2026-04-16
**Project**: Movie Ticket Booking System - OOAD Mini Project
