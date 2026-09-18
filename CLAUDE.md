# admin_panel_api

Java 25 / Spring Boot REST API for the **admin pair** — the admin/CRM backend.
JPA entities map onto `booking_db` tables; `ddl-auto: validate` (never creates tables).
Flyway migrations; JWT auth; pluggable storage (local/s3/gcs).

```bash
./mvnw spring-boot:run          # run the API (dev)
./mvnw clean package            # build
./mvnw test                     # tests
# DB: needs a Postgres booking_db on localhost:5432 (user booking_user / pass booking_pass)
```

## Non-obvious facts (an agent dropped in here must know these)

1. **Shared database, co-owned — `booking_db`.** This API connects to the **same** `booking_db` as `booking_api` (`jdbc:postgresql://localhost:5432/booking_db`, user `booking_user`/`booking_pass`). It runs its **own** **Flyway** migrations (`src/main/resources/db/migration/V1–V11`, history table `flyway_schema_history`), creating the same tables with `CREATE TABLE IF NOT EXISTS`. JPA `ddl-auto: validate` means it assumes the schema exists and **fails on startup** if the schema drifts from its entities.
   → **Rule: changing a migration here → check `booking_api`'s migrations** (`internal/migrations/000001–000009`, golang-migrate, history `schema_migrations`) **before applying.** Two tools, two history tables, one DB — see the root `CLAUDE.md` shared-DB section.

2. **JPA entities are the contract with the DB.** `@Table(name=…)` in `src/main/java/com/coachly/adminpanel/<domain>/` maps to tables booking_api also uses: `administrators`, `appointments`, `clients`, `disciplines`, `professionals`. A column/type change in booking_api's schema that this entity doesn't mirror = startup failure (`validate`). Enum columns use `@JdbcTypeCode(SqlTypes.ENUM)` + `@Enumerated(EnumType.STRING)` against Postgres enum types (`appointment_type`, `appointment_status`, `administrator_role`) — whose values leak to the admin frontend.

3. **Config is env-overridable.** `src/main/resources/application.yaml`: JWT secret via `JWT_SECRET` (min 32 chars, required in prod); CORS allows `http://localhost:5173` (the admin_panel_client dev port); storage provider via `STORAGE_PROVIDER` (`local`/`s3`/`gcs`). No `.env.example` — these are the env vars.

4. **Talks to `admin_panel_client` (React) over HTTP.** CORS is pinned to the client's dev origin. No codegen/shared types with the frontend — the contract is hand-written, like the booking pair.
