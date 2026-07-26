# Hotel Room Booking System (CRUD Project)

A beginner-friendly Spring Boot project that demonstrates **CRUD** (Create, Read, Update, Delete)
operations on two related entities: **Room** and **Booking**.

## Tech Stack
- Java 17
- Spring Boot 3 (Web, JPA, Thymeleaf, Validation)
- H2 (in-memory database — no installation needed, comes bundled)
- Bootstrap 5 (for styling)
- Maven

> This version uses H2, an in-memory database that runs inside the app itself.
> There's nothing to install or configure — just run the app and it works.
> The tradeoff: all data resets every time you stop and restart the app.
> Sample rooms and bookings are pre-loaded automatically on every startup
> (see `DataSeeder.java`) so you always have data to look at.

## Project Structure
```
hotel-room-booking-system/
├── pom.xml
├── src/main/java/com/hotel/booking/
│   ├── HotelBookingApplication.java   -> starts the app
│   ├── entity/                        -> Room.java, Booking.java (database tables)
│   ├── repository/                    -> talks to the database
│   ├── service/                       -> business logic (CRUD rules)
│   └── controller/                    -> handles web requests (URLs)
└── src/main/resources/
    ├── application.properties         -> DB connection settings
    ├── templates/                     -> HTML pages (Thymeleaf)
    └── static/css/style.css
```

This is the standard **Controller → Service → Repository → Entity** layering used in real
Spring Boot projects. Each layer has one job:
- **Controller** — receives the URL request, decides which page to show
- **Service** — contains the actual CRUD logic and rules
- **Repository** — the only layer that touches the database
- **Entity** — a Java class that maps directly to a database table

## Modules
1. **Room Management** — Add, view, edit, delete rooms; availability tracked automatically.
2. **Customer Management** — Register, view, search (by name), edit, delete customers — independent of any booking.
3. **Booking Management** — Create bookings (linked to a Customer + Room), edit, cancel.
4. **Check-In / Check-Out** — One-click status change buttons on the bookings list; checking out automatically frees the room.
5. **Admin Login** — The whole app is protected behind a login screen (Spring Security).
   - Username: `admin`
   - Password: `admin123`
6. **Guest (User) Side** — Guests can self-register, log in, browse available rooms, book one, and view only their own bookings.
   - Sample guest logins: `arjun` / `guest123` and `priya` / `guest123`
   - New guests can create an account at `/register`

## Two sides of the app
| Role | Landing page after login | Can do |
|---|---|---|
| Admin | `/` | Manage rooms, customers, all bookings, check-in/out any guest |
| Guest (User) | `/user/dashboard` | Browse available rooms, book a room, view their own bookings only |

## Setup Instructions

### 1. Prerequisites
- JDK 17 or later installed
- VS Code (with Extension Pack for Java + Spring Boot Extension Pack) or IntelliJ IDEA
- That's it — no database installation required.

### 2. Run the project

**Option A — VS Code**
1. Unzip this project and open the folder in VS Code (the one containing `pom.xml`).
2. Wait for Java extension to finish indexing (bottom-right progress notification).
3. Open `src/main/java/com/hotel/booking/HotelBookingApplication.java`.
4. Click the **Run** link above the `main` method (or use the Spring Boot Dashboard).

**Option B — IntelliJ IDEA**
1. Unzip this project.
2. Open IntelliJ → `Open` → select the unzipped `hotel-room-booking-system` folder.
3. Wait for Maven to download dependencies (bottom-right progress bar).
4. Open `HotelBookingApplication.java` → click the green ▶ run button.

**Option C — Terminal**
```bash
cd hotel-room-booking-system
mvn spring-boot:run
```

### 3. Open the app
Visit: **http://localhost:8080**

You'll be redirected to a login page first. Log in with:
- Username: `admin`
- Password: `admin123`

After logging in, you'll see three sections, already pre-loaded with 5 sample rooms,
2 sample customers, and 2 sample bookings:
- **Manage Rooms** → `/rooms` — add, edit, delete rooms
- **Manage Customers** → `/customers` — register, search, edit, delete customers
- **Manage Bookings** → `/bookings` — book a room for a customer, check them in/out, edit or cancel bookings

### 4. (Optional) View the raw database
Visit **http://localhost:8080/h2-console** while the app is running.
- JDBC URL: `jdbc:h2:mem:hotel_booking_db`
- Username: `sa`
- Password: *(leave blank)*
Click **Connect** to run SQL queries directly and see the `ROOMS` and `BOOKINGS` tables.

### Note on data persistence
Because H2 runs in-memory, **all data resets to the sample data every time you stop
and restart the app**. This is intentional for easy testing. When you're ready for
real persistence, switching to MySQL is a config-only change (swap the H2 dependency
in `pom.xml` and the datasource properties in `application.properties`) — no code
changes needed, since JPA abstracts the database away.

## How the CRUD flow works (Rooms example)

| Operation | HTTP Method | URL | What happens |
|---|---|---|---|
| Create | GET then POST | `/rooms/new` → `/rooms` | Shows blank form, then saves it |
| Read (all) | GET | `/rooms` | Lists every room in a table |
| Read (one, for editing) | GET | `/rooms/edit/{id}` | Loads that room into the form |
| Update | POST | `/rooms/update/{id}` | Saves changes to that room |
| Delete | GET | `/rooms/delete/{id}` | Removes the room |

Bookings follow the exact same pattern, plus one extra rule: booking a room
automatically marks it as unavailable, and deleting a booking frees the room again
(see `BookingService.java`).

## Suggested next steps (once you're comfortable with this)
1. Add Spring Security (login for admin vs guest)
2. Add REST API endpoints (`@RestController`) alongside the web pages
3. Add search/filter (e.g. "show only available rooms")
4. Write unit tests for the Service layer
5. Dockerize it for deployment

## Notes
- `spring.jpa.hibernate.ddl-auto=update` auto-creates/updates tables from your entity
  classes — convenient for learning, but in real production projects you'd use a
  migration tool like Flyway or Liquibase instead.
