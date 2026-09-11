# Coachly Admin Panel API

[![Java Version](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org/)

The **Coachly Admin Panel API** is a high-performance, stateless REST API backend built with **Java 25** and **Spring Boot 4.1.0**. It serves as the administrative core for the Coachly platform, providing robust management capabilities over professionals (coaches, practitioners), client associations, appointments, platform disciplines, and system administrator accounts.

---

## 📌 Core Features

- 🔐 **Stateless Security & RBAC:** Robust Authentication and Authorization via JWT. Role-Based Access Control supports both `ADMIN` and `MANAGER` roles with method-level security enforcement.
- 👥 **Professional Registry:** Complete onboarding, registration, profile curation, and custom avatar management for professionals.
- 📅 **Appointment Supervision:** Real-time visibility into booking statuses, professional-client assignments, and full lifecycle tracking.
- 🏷️ **Platform Disciplines:** Multi-lingual, slug-addressed taxonomy of platform specialties (e.g., Yoga, Nutrition, Strength Training).
- 💾 **Pluggable Storage Abstraction:** Upload handling utilizing the Strategy Pattern to dynamically switch between **Local Disk Storage**, **Amazon S3**, or **Google Cloud Storage (GCS)**.
- ⚡ **Virtual Threads Enabled:** Built with modern concurrency patterns utilizing JVM virtual threads for near-infinite I/O scalability.

---

## 🛠️ Tech Stack & Dependencies

- **Language:** Java 25 (leveraging virtual threads, records, and modern switch expressions).
- **Framework:** Spring Boot 4.1.0 (Spring Web MVC, Spring Security, Spring Data JPA).
- **Persistence:** Hibernate 7.x, PostgreSQL.
- **Migrations:** Flyway (with native schema validation and baselining).
- **API Documentation:** Springdoc OpenAPI / Swagger UI 2.8.3.
- **Authentication:** JSON Web Tokens (JJWT 0.13.0).
- **Cloud Storage:** Amazon Web Services SDK (S3) 2.25.15 & Google Cloud Storage Client 2.36.1.
- **Utilities:** Project Lombok (annotations for getters, setters, builders).

---

## 🏗️ Architectural & Design Principles

To ensure extreme maintainability, performance, and scalability, the codebase adheres to several strict architectural conventions:

### 1. Disabled Open Session In View (OSIV)
To prevent the "N+1 query problem" and avoid holding database connections open during long-running views or DTO mapping, **OSIV is explicitly disabled** (`spring.jpa.open-in-view: false`).
- *Consequence:* All Hibernate lazy-loaded collections and relationships must be fully initialized or fetched within explicit `@Transactional` service boundaries before the database session is closed.

### 2. Transaction and Connection Lease Optimization
Database connection leases are kept as short as possible to optimize the HikariCP pool:
- **No Transaction (Preferred for simple reads & CPU-bound hashing):** Simple read operations that do not access lazy relationships are omitted from `@Transactional` boundaries. Database connections are returned to the pool immediately after the JPA/Spring Data repository query completes.
- **No Transaction for Hashing:** Highly CPU-intensive operations (such as BCrypt password encoding) are strictly executed outside `@Transactional` service boundaries. This avoids holding connection resources idle during expensive mathematical calculations.
- **Read-Only Transactions (`@Transactional(readOnly = true)`):** Used only when orchestrating multiple queries requiring a consistent transactional snapshot or mapping lazy-loaded associations within the service boundary.
- **Write Transactions (`@Transactional`):** Reserved strictly for atomicity during multi-aggregate writes or modifications.

### 3. Explicit Repository Saves
While Hibernate automatically flushes dirty entities at the end of transactions, this codebase enforces **explicit repository `.save(...)` calls** on modified persistent entities. This practice enhances code readability, self-documentation, and clarity of intent.

### 4. Pluggable Storage Strategy Pattern
File upload capability is structured via the `StorageService` interface. The active implementation is bound conditionally at runtime depending on the `app.storage.provider` environment configuration:
- `local`: Serves files from a local directory, registered via Spring Web MVC resource handlers at `/uploads/**`.
- `s3`: Streams uploads directly to Amazon S3.
- `gcs`: Integrates natively with Google Cloud Storage.

---

## 📂 Package Structure

The repository organizes classes strictly by domain boundaries:

```
com.coachly.adminpanel
├── auth/           # JWT Login processing & profile fetching
│   └── dto/        # Login request/response and Profile records
├── administrator/  # Admin and Manager accounts administration
│   └── dto/        # Registration requests and models
├── professional/   # Professional onboarding, profiles, and avatar endpoints
│   └── dto/        # Registration, update, and statistics records
├── appointment/    # Booking management, status auditing, and schedules
│   └── dto/        # Specialized DTO lists for professionals and clients
├── client/         # Client profiles and registration metadata
│   └── dto/        # Client list and detail records
├── common/         # BaseEntity, Global Exception handlers, Storage Services
│   ├── exception/  # Specialized business exceptions
│   └── storage/    # Pluggable Storage implementations
├── security/       # JWT filters, token providers, and security rules
└── config/         # System-wide configuration properties
```

---

## 🔧 Environment Configuration

Sensitive and environment-specific settings are managed via standard system environment variables or configured in `src/main/resources/application.yaml`:

| Environment Variable | Description | Default Value / Choice |
|----------------------|-------------|-------------------------|
| `JWT_SECRET` | Secret key used to sign and verify HMAC JWTs (min 32 chars) | `dev-only-secret-change-in-production-min-32-chars` |
| `STORAGE_PROVIDER` | Active asset storage plugin | `local` (choices: `local`, `s3`, `gcs`) |
| `STORAGE_LOCAL_DIR` | Filesystem path for uploads when using `local` | `uploads` |
| `STORAGE_CDN_BASE_URL` | Optional CDN prefix to format returned asset URLs | *(Empty)* |
| `AWS_S3_BUCKET` | AWS S3 Bucket Name | *(Empty)* |
| `AWS_S3_REGION` | AWS Regional Endpoint | `us-east-1` |
| `GCS_BUCKET` | GCS Bucket Name | *(Empty)* |

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK):** Version 25 or higher
- **Build Tool:** Apache Maven 3.9+
- **Database:** PostgreSQL 16+

### 1. Database Setup
Create a PostgreSQL database and database user matching the development defaults:
```sql
CREATE DATABASE booking_db;
CREATE USER booking_user WITH PASSWORD 'booking_pass';
GRANT ALL PRIVILEGES ON DATABASE booking_db TO booking_user;
```

### 2. Database Migration & Seed Data
Flyway migrations are executed automatically on application startup. Schema definitions, constraints, and standard indexes are version-controlled inside `src/main/resources/db/migration/`.

- **Seeded Admin Account:** A default system administrator account is pre-seeded via `V5__seed_admin_users.sql`:
  - **Username:** `admin`

---

## 🖥️ Build & Run Commands

Execute these commands from the root directory of the project:

* **Compile the application:**
  ```powershell
  mvn clean compile
  ```

* **Run the application locally:**
  ```powershell
  mvn spring-boot:run
  ```

* **Build executable JAR (excluding tests):**
  ```powershell
  mvn clean package -DskipTests
  ```

* **Run unit and validation tests:**
  ```powershell
  mvn test
  ```

---

## 📖 API Documentation & Swagger UI

API documentation is generated dynamically utilizing Springdoc-OpenAPI. Once the application is running locally:

- **Swagger UI Dashboard:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Raw OpenAPI Specification JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Authentication in Swagger UI:
1. Authenticate with the API by calling `POST /api/auth/login` using your admin credentials.
2. Extract the `token` from the JSON response.
3. Click the **Authorize** button in the upper right-hand corner of the Swagger UI dashboard.
4. Input your token into the dialog (choose the `bearerAuth` scheme) and submit. All subsequent requests executed through the UI will automatically include the bearer token header.

---

## 🧪 Testing Conventions

Unit tests are written with **JUnit 5** and **Mockito**, operating strictly without launching the Spring Context for lightning-fast feedback:
- **Test File Location:** `src/test/java/...`
- **Naming Convention:** `methodName_whenCondition_shouldExpectedResult`
- **Isolation:** Tests are run in isolation using `@ExtendWith(MockitoExtension.class)`.

To run specific tests:
```powershell
mvn test -Dtest=DisciplineServiceTest                                 # Single class
mvn test -Dtest=DisciplineServiceTest#createDiscipline_whenSlugIsUnique_shouldSave  # Single method
```

---
