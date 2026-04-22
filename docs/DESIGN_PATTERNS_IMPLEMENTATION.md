# 4 Design Patterns Implementation - Movie Ticket Booking System

## Executive Summary

**4 Design Patterns Added** (One from each category + 1 extra):

| # | Category | Pattern | Location | Purpose |
|---|----------|---------|----------|---------|
| **1** | **CREATIONAL** | Factory Method | `factory/UserFactory.java` | Centralize user creation (Customer/Admin) |
| **2** | **STRUCTURAL** | Facade | `facade/BookingFacade.java` | Simplify complex booking workflow |
| **3** | **BEHAVIORAL** | State | `state/BookingState.java` | Validate and manage booking state transitions |
| **4** | **EXTRA (Creational)** | Builder | `builder/BookingBuilder.java` | Fluent API for creating complex bookings |

---

## PATTERN 1: FACTORY METHOD (CREATIONAL) ✅

### 📍 Location
```
src/main/java/com/moviebooking/factory/
├── UserFactory.java          (Interface)
└── UserFactoryImpl.java       (Implementation)
```

### 🎯 What It Does
**Centralizes the creation of User objects** (Customer and Admin). Instead of directly instantiating User types with `new`, clients request objects from the factory.

### 🔧 How It Works

**Before (Without Factory)**:
```java
// UserService.java - Direct instantiation scattered everywhere
public Customer registerCustomer(String name, String email, String password, String phone) {
    Customer customer = new Customer(name, email, password, phone);
    return customerRepository.save(customer);
}

public Admin registerAdmin(String name, String email, String password, String phone) {
    Admin admin = new Admin(name, email, password, phone);
    return adminRepository.save(admin);
}
```

**Problems**:
- If Customer class constructor changes, ALL places must update
- Creation logic duplicated across multiple files
- Hard to add initialization/validation logic
- No way to track all user creations

---

**After (With Factory)**:
```java
// UserService.java - Uses factory for creation
@Autowired
private UserFactory userFactory;

public Customer registerCustomer(String name, String email, String password, String phone) {
    // Use factory instead of new
    Customer customer = userFactory.createCustomer(name, email, password, phone);
    return customerRepository.save(customer);
}

public Admin registerAdmin(String name, String email, String password, String phone) {
    // Use factory instead of new
    Admin admin = userFactory.createAdmin(name, email, password, phone);
    return adminRepository.save(admin);
}
```

### 📝 Code Changes in UserService.java

**Add these lines** (around line 30, after class declaration):
```java
@Autowired
private UserFactory userFactory;
```

**Replace registerCustomer method** (line 40-48):
```java
public Customer registerCustomer(String name, String email, String password, String phone) {
    if (userRepository.existsByEmail(email)) {
        throw new RuntimeException("Email already registered: " + email);
    }
    // USE FACTORY INSTEAD OF new
    Customer customer = userFactory.createCustomer(name, email, password, phone);
    return customerRepository.save(customer);
}
```

**Replace registerAdmin method** (line 128-135):
```java
public Admin registerAdmin(String name, String email, String password, String phone) {
    if (userRepository.existsByEmail(email)) {
        throw new RuntimeException("Email already registered: " + email);
    }
    // USE FACTORY INSTEAD OF new
    Admin admin = userFactory.createAdmin(name, email, password, phone);
    return adminRepository.save(admin);
}
```

### ✨ Benefits
- ✅ **Centralized Creation**: All user creation logic in one place
- ✅ **Easy to Extend**: Add new user type (Moderator, SuperAdmin) without changing client code
- ✅ **Consistent Initialization**: All users created same way
- ✅ **SOLID Compliance**: Open/Closed Principle - open for extension, closed for modification
- ✅ **Future Enhancements**: Can add logging, caching, audit trail in factory

### 🔮 Future Extensibility

**To add a new MODERATOR user type**:

1. Create `Moderator` class extending `User`
2. Add method to `UserFactory`:
```java
public Moderator createModerator(String name, String email, String password, String phone) {
    Moderator moderator = new Moderator(name, email, password, phone);
    return moderator;
}
```
3. **No client code changes needed!**

---

## PATTERN 2: FACADE (STRUCTURAL) ✅

### 📍 Location
```
src/main/java/com/moviebooking/facade/
└── BookingFacade.java        (Main facade + DTOs)
```

### 🎯 What It Does
**Simplifies the complex booking workflow** by providing a single, unified interface. Hides the complexity of coordinating multiple services (BookingService, PaymentService, ShowService, etc.).

### 🔧 How It Works

**Before (Without Facade - Complex)**:
```java
// CustomerController.java - Client handles complexity
@PostMapping("/booking/create")
public String createBooking(...) {
    // STEP 1: Get booking requirements
    Show show = showService.getShowById(showId);
    
    // STEP 2: Check availability
    if (!bookingService.areSeatsAvailable(showId, seatIds)) {
        throw new Exception("Seats not available");
    }
    
    // STEP 3: Create booking
    Booking booking = bookingService.createBooking(customerId, showId, seatIds);
    
    // STEP 4: Process payment
    Payment payment = paymentService.processPayment(
        booking.getId(), paymentMethod, paymentDetails
    );
    
    // STEP 5: Verify payment
    if (!payment.isSuccessful()) {
        throw new Exception("Payment failed");
    }
    
    // STEP 6: Return result
    return "redirect:/booking/" + booking.getId() + "/confirmation";
}
```

**Problems**:
- Client must know the correct sequence
- Lots of error handling
- Difficult to reuse this logic
- Hard to test

---

**After (With Facade - Simple)**:
```java
// CustomerController.java - Uses facade
@Autowired
private BookingFacade bookingFacade;

@PostMapping("/booking/create")
public String createBooking(...) {
    // Facade handles ALL complexity in one call
    Booking booking = bookingFacade.processCompleteBooking(
        customerId, showId, seatIds, paymentMethod, paymentDetails
    );
    
    return "redirect:/booking/" + booking.getId() + "/confirmation";
}
```

### 📝 Code Changes in CustomerController.java

**Add field** (around line 60, after other fields):
```java
@Autowired
private BookingFacade bookingFacade;
```

**Replace createBooking method** (line 198-231):
```java
@PostMapping("/booking/create")
public String createBooking(@RequestParam Long showId,
                           @RequestParam List<Long> seatIds,
                           @RequestParam String paymentMethod,
                           @RequestParam String cardNumber,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
    try {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/login-page?error=Please login first";
        }

        // Prepare payment details
        Map<String, String> paymentDetails = new HashMap<>();
        paymentDetails.put("cardNumber", cardNumber);
        // ... add other payment details

        // USE FACADE - One call handles everything
        Booking booking = bookingFacade.processCompleteBooking(
            customerId, showId, seatIds, PaymentMethod.valueOf(paymentMethod), paymentDetails
        );

        return "redirect:/booking/" + booking.getId() + "/confirmation";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/show/" + showId + "/seats";
    }
}
```

### 📋 Facade Methods Available

The `BookingFacade` provides 5 simplified methods:

```java
// 1. Complete booking process (validation + booking + payment)
Booking processCompleteBooking(Long customerId, Long showId, 
                              List<Long> seatIds, 
                              PaymentMethod method,
                              Map<String, String> details)

// 2. Get complete booking details
BookingDetailsDTO getBookingDetails(Long bookingId)

// 3. Cancel booking with refund
void cancelBookingWithRefund(Long bookingId)

// 4. Get seat availability with pricing
SeatAvailabilityDTO getSeatsWithPricing(Long showId)

// 5. Get customer booking summary
CustomerBookingSummaryDTO getCustomerBookingSummary(Long customerId)
```

### ✨ Benefits
- ✅ **Simplified API**: One method call instead of 10
- ✅ **Reduced Coupling**: Client doesn't know about internal services
- ✅ **Easier Testing**: Can mock the facade
- ✅ **Maintainability**: If internal services change, only facade updates
- ✅ **Code Reuse**: Booking logic available to any client (web, API, etc.)

### 📊 Architectural Benefit

```
BEFORE (Tight Coupling):
Controller → BookingService → SeatRepo
         → PaymentService  → PaymentRepo
         → ShowService     → ShowRepo
         (Client coordinating everything)

AFTER (Using Facade):
Controller → BookingFacade → BookingService → SeatRepo
                         → PaymentService  → PaymentRepo
                         → ShowService     → ShowRepo
         (Facade handles coordination)
```

---

## PATTERN 3: STATE (BEHAVIORAL) ✅

### 📍 Location
```
src/main/java/com/moviebooking/state/
├── BookingState.java         (Interface)
└── BookingStateImpl.java      (Implementations)
   ├── PendingState
   ├── ConfirmedState
   ├── CancelledState
   └── ExpiredState
```

### 🎯 What It Does
**Encapsulates state-specific behavior** and **validates state transitions**. Each booking state (PENDING, CONFIRMED, CANCELLED, EXPIRED) is a separate class that knows what transitions are valid.

### 🔧 How It Works

**Before (Without State Pattern - Simple Enum)**:
```java
// model/BookingStatus.java
public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED, EXPIRED
}

// service/BookingService.java
public Booking confirmBooking(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)...;
    
    // NO VALIDATION - Can go from any state to CONFIRMED
    booking.setStatus(BookingStatus.CONFIRMED);
    return bookingRepository.save(booking);
}
```

**Problems**:
- Can transition from CONFIRMED → PENDING (invalid!)
- Can confirm an already confirmed booking
- No encapsulation of state-specific rules
- Logic scattered across multiple classes

**State Diagram Issue**:
```
Invalid transitions possible:
CONFIRMED ← CANCELLED ✗ (Not allowed - terminal state)
EXPIRED   ← CANCELLED ✗ (Not allowed - terminal state)
```

---

**After (With State Pattern - Encapsulated)**:
```java
// model/BookingState.java
public interface BookingState {
    BookingState confirm() throws IllegalStateException;
    BookingState cancel() throws IllegalStateException;
    BookingState expire() throws IllegalStateException;
    boolean canRefund();
    boolean canModify();
}

// state/ConfirmedState.java
public class ConfirmedState implements BookingState {
    @Override
    public BookingState confirm() throws IllegalStateException {
        // PREVENTED - Cannot confirm twice
        throw new IllegalStateException(
            "Booking is already confirmed. Cannot confirm again."
        );
    }
    
    @Override
    public BookingState cancel() throws IllegalStateException {
        // ALLOWED - Can cancel confirmed booking for refund
        return new CancelledState();
    }
    
    @Override
    public boolean canModify() {
        return false; // Cannot modify confirmed bookings
    }
}

// service/BookingService.java - Using state pattern
public Booking confirmBooking(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)...;
    
    // State validates the transition
    BookingState newState = booking.getState().confirm();
    booking.setState(newState); // Only valid transitions allowed
    
    return bookingRepository.save(booking);
}
```

### 📝 Code Changes in Booking.java

**Add state field** (replace status field, around line 71):
```java
// OLD:
@Enumerated(EnumType.STRING)
private BookingStatus status = BookingStatus.PENDING;

// NEW:
@Transient  // Don't persist state, derive from status
private BookingState state = new PendingState();

// Keep status for database, but sync with state
@Enumerated(EnumType.STRING)
@Column(name = "status")
private BookingStatus status;

public BookingState getState() {
    return state;
}

public void setState(BookingState newState) {
    this.state = newState;
    // Sync database status
    this.status = BookingStatus.valueOf(newState.getStateName());
}
```

**Update confirm() method** (line 204):
```java
public void confirm() {
    try {
        // State validates transition
        BookingState newState = this.state.confirm();
        setState(newState);
    } catch (IllegalStateException e) {
        System.err.println("Invalid state transition: " + e.getMessage());
        throw e;
    }
}
```

**Update cancel() method** (line 211):
```java
public void cancel() {
    try {
        // State validates transition
        BookingState newState = this.state.cancel();
        setState(newState);
    } catch (IllegalStateException e) {
        System.err.println("Invalid state transition: " + e.getMessage());
        throw e;
    }
}
```

**Add new methods**:
```java
public boolean canRefund() {
    return state.canRefund();
}

public boolean canModify() {
    return state.canModify();
}
```

### 📊 State Transitions (Valid)

```
              confirm()
PENDING ─────────────────→ CONFIRMED
  ↑                            ↓
  │        cancel()            │
  └────────────────────────────┘
                               
PENDING ─────→ CANCELLED (terminal)
  ↓
EXPIRED (terminal)

CONFIRMED ─────→ CANCELLED (terminal)
```

### 🚫 Prevented Invalid Transitions

```
✗ CONFIRMED.confirm() → throws IllegalStateException
✗ CANCELLED.confirm() → throws IllegalStateException
✗ CONFIRMED.expire()  → throws IllegalStateException
✗ CANCELLED.cancel()  → throws IllegalStateException
```

### ✨ Benefits
- ✅ **Prevents Invalid Transitions**: Can't go CONFIRMED → PENDING
- ✅ **Encapsulated Rules**: Each state knows its valid transitions
- ✅ **No Conditionals**: No if-else for state checks
- ✅ **Easy to Extend**: Add new state = new class, no existing code changes
- ✅ **Self-Documenting**: Code clearly shows what transitions are allowed

### 🔮 Future Extension

**Add new "ON_HOLD" state**:
```java
public class OnHoldState implements BookingState {
    // Implement required methods
    // Can transition to PENDING or CANCELLED
}
```
**No changes needed to existing state classes!**

---

## PATTERN 4: BUILDER (CREATIONAL - EXTRA) ✅

### 📍 Location
```
src/main/java/com/moviebooking/builder/
└── BookingBuilder.java
```

### 🎯 What It Does
**Provides a fluent, step-by-step API for creating complex Booking objects**. Instead of multiple constructors with many parameters, clients build bookings with readable, chainable method calls.

### 🔧 How It Works

**Before (Without Builder - Multiple Constructors)**:
```java
// Without builder - unclear which constructor to use
Booking booking1 = new Booking(customer, show, seats);
Booking booking2 = new Booking(customer, show, seats, LocalDateTime.now());
Booking booking3 = new Booking(customer, show, seats, LocalDateTime.now(), BookingStatus.PENDING);
// Problem: Constructor overloading is confusing and hard to maintain
```

---

**After (With Builder - Fluent API)**:
```java
// With builder - clear and self-documenting
Booking booking = new BookingBuilder()
    .forCustomer(customer)
    .forShow(show)
    .withSeats(seatList)
    .atTime(LocalDateTime.now())
    .build();

// Or simpler, with defaults
Booking booking = new BookingBuilder()
    .forCustomer(customer)
    .forShow(show)
    .withSeats(seatList)
    .build(); // Time defaults to now
```

### 📝 Code Changes in BookingService.java

**Update createBooking method** (line 65):
```java
@Transactional
public Booking createBooking(Long customerId, Long showId, List<Long> seatIds) {
    // ... validation code ...
    
    // OLD WAY:
    // Booking booking = new Booking(customer, show, seats);
    
    // NEW WAY - Using Builder:
    Booking booking = new BookingBuilder()
        .forCustomer(customer)
        .forShow(show)
        .withSeats(seats)
        .atCurrentTime()
        .build();
    
    return bookingRepository.save(booking);
}
```

**Add new method for preview calculation**:
```java
public Double calculateBookingTotal(Long showId, List<Long> seatIds) {
    Show show = showRepository.findById(showId)
            .orElseThrow(() -> new RuntimeException("Show not found: " + showId));

    List<Seat> seats = seatRepository.findByIds(seatIds);

    // Use builder for temporary booking (just for calculation)
    Booking tempBooking = new BookingBuilder()
        .forShow(show)
        .withSeats(seats)
        .build();

    return tempBooking.calculateTotalAmount();
}
```

### 📋 Builder Methods

```java
// Required methods (must call)
.forCustomer(Customer customer)          // Set customer
.forShow(Show show)                      // Set show
.withSeats(List<Seat> seats)             // Set seats

// Optional methods (with defaults)
.addSeat(Seat seat)                      // Add individual seat
.atTime(LocalDateTime time)              // Set booking time
.atCurrentTime()                         // Use current time (default)

// Build method (creates object)
.build()                                 // Create and validate

// Static factory
BookingBuilder.newBooking()              // Create new builder
```

### 📊 Validation Points

The builder validates:
1. **forCustomer()**: Customer not null
2. **forShow()**: Show not null
3. **withSeats()**: Seats not null and not empty
4. **build()**: All required fields set, no null values

```java
public Booking build() {
    if (customer == null) {
        throw new IllegalStateException("Customer is required");
    }
    if (show == null) {
        throw new IllegalStateException("Show is required");
    }
    if (seats == null || seats.isEmpty()) {
        throw new IllegalStateException("At least one seat is required");
    }
    
    // Build only if all validations pass
    Booking booking = new Booking(customer, show, seats);
    booking.setBookingTime(bookingTime);
    return booking;
}
```

### ✨ Benefits
- ✅ **Fluent API**: Readable, self-documenting code
- ✅ **Optional Parameters**: No need for multiple constructors
- ✅ **Validation**: Build-time validation ensures valid objects
- ✅ **Flexibility**: Can build objects in any order
- ✅ **Maintainability**: Easy to add/remove fields
- ✅ **Testability**: Easy to create test objects with specific configurations

### 🔮 Advanced Usage

```java
// Build booking step by step
BookingBuilder builder = new BookingBuilder();
builder.forCustomer(customer);
builder.forShow(show);

// Dynamically add seats
for (Seat seat : userSelectedSeats) {
    builder.addSeat(seat);
}

Booking booking = builder.build();
```

---

## 📊 Pattern Comparison Table

| Pattern | Category | Purpose | Problem Solved | Key Benefit |
|---------|----------|---------|---|---|
| **Factory Method** | Creational | Centralize object creation | Scattered `new` statements | Easy to extend |
| **Facade** | Structural | Simplify complex subsystems | Complex multi-service coordination | Simpler client code |
| **State** | Behavioral | Validate state transitions | Invalid state changes | Prevents bugs |
| **Builder** | Creational | Build complex objects | Multiple constructors | Readable API |

---

## 🔗 How Patterns Work Together

```
User Registration Flow:
UserController 
    → UserFactory (CREATIONAL) creates Customer
    → Booking Process
        → BookingFacade (STRUCTURAL) orchestrates
        → BookingBuilder (CREATIONAL) creates complex object
        → BookingState (BEHAVIORAL) validates transitions

Result: Clean, maintainable, extensible system!
```

---

## 📝 Summary of Code Changes

### Files Created (New):
1. `factory/UserFactory.java` - Factory interface
2. `factory/UserFactoryImpl.java` - Factory implementation
3. `facade/BookingFacade.java` - Facade with 5 methods + DTOs
4. `state/BookingState.java` - State interface
5. `state/BookingStateImpl.java` - State implementations (4 concrete states)
6. `builder/BookingBuilder.java` - Builder for bookings

### Files Modified:
1. **UserService.java** - Add `@Autowired UserFactory` and use it in registration
2. **CustomerController.java** - Add `@Autowired BookingFacade` and use for booking
3. **Booking.java** - Add state field and update methods (optional - for full State pattern)
4. **BookingService.java** - Use BookingBuilder for creation

### Total LOC Added: ~1000 lines of well-documented code

---

## ✅ Validation Checklist

- [x] Factory Method - Centralizes user creation
- [x] Facade - Simplifies booking workflow
- [x] State - Validates booking transitions
- [x] Builder - Fluent API for object creation
- [x] All 4 patterns documented with code examples
- [x] Integration points shown
- [x] Benefits explained
- [x] Future extensibility demonstrated

---

## 🎓 Learning Outcomes

After implementing these patterns, you understand:
1. **Creational Patterns**: How to create objects cleanly (Factory, Builder)
2. **Structural Patterns**: How to organize complex systems (Facade)
3. **Behavioral Patterns**: How to manage object behavior (State)
4. **SOLID Principles**: Each pattern embodies multiple SOLID principles
5. **OOP Design**: Real-world application of design patterns

---

**Generated**: 2026-04-16
**Project**: Movie Ticket Booking System - OOAD Mini Project
**Faculty Requirement**: 4 Design Patterns (1 from each category + 1 extra) ✅
