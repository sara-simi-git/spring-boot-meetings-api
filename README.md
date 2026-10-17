# MyApp – Meetings Management REST API

A Spring Boot REST API for managing **users, meetings and meeting participants**, secured with **JWT authentication** and packaged to run with **Docker Compose** (application + PostgreSQL).

> Built as a hands-on project to practice a typical Spring Boot back-end stack: layered architecture, JPA relationships, stateless security, AOP and containerization.

---

## Tech stack

| Area | Technology |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.3.0 (Web, Data JPA, Security) |
| Authentication & authorization | JWT (jjwt 0.11.5), stateless, custom `OncePerRequestFilter`, BCrypt password hashing, role-based access with `@PreAuthorize` |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Persistence | Spring Data JPA / Hibernate, PostgreSQL (Docker) or H2 file DB (local) |
| Cross-cutting | Spring AOP (`@Aspect` logging), global `@RestControllerAdvice` exception handler |
| Boilerplate | Lombok |
| Build | Maven |
| Containers | Docker (multi-stage build) + Docker Compose |

---

## Features

- **JWT login** (`POST /auth/login`) – returns a signed token valid for 1 hour.
- **Stateless security** – every endpoint except `/auth/**` requires `Authorization: Bearer <token>`. The token carries the user's role (`ROLE_ADMIN` / `ROLE_PERSON`).
- **Password hashing** – passwords are stored as BCrypt hashes and never returned by the API.
- **Role-based authorization** – method-level security (`@PreAuthorize`): admins manage everything, regular users have read access and can view their own profile.
- **DTOs + request validation** – entities are never exposed; request bodies are validated (`@Valid`) and invalid input returns `400` with the failing fields.
- **CRUD** for users, meetings and participants.
- **"My meetings"** (`GET /meetings/my`) – returns the meetings of the user identified by the token, using a derived query over the relationship `Meeting → Participant → User`.
- **Initial admin user** created on startup, so a fresh database is usable immediately.
- **AOP logging** of every service-layer method (start / success / failure).
- **Global exception handling** – maps validation errors to `400`, missing resources to `404`, and access denial to `403`.
- **One-command run** with Docker Compose.

---

## Project structure

```
src/main/java/com/example/myapp
├── MyAppApplication.java          # entry point
├── DataInitializer.java           # creates the initial admin user on startup
├── GlobalExceptionHandler.java    # @ControllerAdvice – centralized error handling
├── ResourceNotFoundException.java # mapped to HTTP 404
├── controllers/                   # REST layer: Auth, User, Meeting, Participant
├── dto/                           # request / response records (with validation annotations)
├── services/                      # business logic + LoggingAspect (AOP)
├── repositories/                  # Spring Data JPA repositories
├── entities/                      # JPA entities: User, Meeting, Participant
├── security/SecurityConfig.java   # filter chain, public/protected routes, BCrypt bean, method security
└── jwt/                           # JwtUtil (create/validate tokens) + JwtFilter
```

### Data model

```
User (users_tbl) 1 ───< Participant >─── 1 Meeting (meetings)
                         roleInMeeting
```

- `User` – id, username, password, email, phone, isAdmin
- `Meeting` – id, subject, startTime, endTime, location, description
- `Participant` – generated id, meeting, user, roleInMeeting (join entity between users and meetings)

---

## Authentication flow

1. `POST /auth/login` with `{"username": "...", "password": "..."}`.
2. The server validates the credentials and returns a JWT (plain text).
3. Send the token with every other request: `Authorization: Bearer <token>`.
4. `JwtFilter` validates the token, and puts the user and role in the Spring `SecurityContext`.

`401` is returned for wrong credentials; a request without a valid token is rejected by Spring Security. Passwords are compared with `BCryptPasswordEncoder.matches(...)` against the stored hash.

---

## API reference

All endpoints except `/auth/login` require a Bearer token. The **Access** column shows who may call each endpoint:
**Admin** = `ROLE_ADMIN` only, **Any user** = any authenticated user, **Admin / self** = an admin or the user themself.

### Auth
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/auth/login` | Public | Login, returns a JWT |

### Users
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/` | Any user | Simple hello endpoint |
| POST | `/` | Admin | Create a user |
| GET | `/all` | Admin | List all users |
| GET | `/users/{id}` | Admin / self | Get a user |
| PUT | `/users/{id}` | Admin | Update a user (password optional) |
| DELETE | `/users/{id}` | Admin | Delete a user |

### Meetings
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/meetings` | Admin | Create a meeting |
| GET | `/meetings` | Any user | List all meetings |
| GET | `/meetings/meetings/{id}` | Any user | Get a meeting |
| PUT | `/meetings/meetings/{id}` | Admin | Update a meeting |
| DELETE | `/meetings/meetings/{id}` | Admin | Delete a meeting |
| GET | `/meetings/my` | Any user | Meetings of the user in the token |

### Participants
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/participants` | Admin | Add a user to a meeting |
| GET | `/participants` | Any user | List all participants |
| GET | `/participants/participants/{id}` | Any user | Get a participant |
| PUT | `/participants/participants/{id}` | Admin | Update a participant |
| DELETE | `/participants/participants/{id}` | Admin | Remove a participant |

### Request examples

```jsonc
// POST /            – create a user
{ "id": 900001, "username": "rina.demo", "password": "Password123!",
  "email": "rina.demo@example.com", "phone": "0501234567", "admin": false }

// POST /meetings    – create a meeting (endTime must be after startTime)
{ "id": 900001, "subject": "Project kickoff",
  "startTime": "2026-11-01T09:00:00.000+00:00", "endTime": "2026-11-01T10:00:00.000+00:00",
  "location": "Meeting room 1", "description": "Initial project meeting" }

// POST /participants – add a user to a meeting
{ "meetingId": 900001, "userId": 900001, "roleInMeeting": "ORGANIZER" }
```

### Error responses

| Status | When |
|---|---|
| `400` | Validation failed (response lists the failing fields), malformed JSON, duplicate id / username, end time before start time |
| `401` | Wrong credentials, or invalid / expired token |
| `403` | Missing token, or the user's role does not allow the action |
| `404` | The requested user / meeting / participant does not exist |

More examples are available in [`test.http`](test.http).

---

## Running the project

### Option 1 – Docker Compose (recommended)

Requirements: Docker with Compose.

```bash
docker compose up --build
```

This starts PostgreSQL and the application on **http://localhost:8080**. Tables are created automatically (`spring.jpa.hibernate.ddl-auto=update`).

An admin user is created on first startup:

| Username | Password |
|---|---|
| `admin` | `admin123` |

Stop everything with `docker compose down` (add `-v` to also delete the database volume).

### Option 2 – Locally without Docker

Requirements: JDK 21 and Maven. Uses an embedded H2 file database (`./data/mydb`).

```bash
mvn spring-boot:run
```

---

## Trying the endpoints

**VS Code** – install the *REST Client* extension (`humao.rest-client`), open [`test.http`](test.http) and click *Send Request* above each request, from top to bottom. The token is taken automatically from the `login` request.

**curl**

```bash
# 1. get a token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

# 2. call a protected endpoint
curl http://localhost:8080/all -H "Authorization: Bearer $TOKEN"
```

**Automated smoke test** (Linux / macOS / Git Bash / WSL) – logs in, exercises every endpoint, checks validation and role-based access (admin vs. regular user), cleans up and prints PASS/FAIL:

```bash
bash smoke-test.sh
```

---

## Configuration

Settings are read from `application.properties` and can be overridden with environment variables (this is how `compose.yaml` configures the container).

| Variable | Purpose | Default |
|---|---|---|
| `JWT_SECRET` | Secret used to sign tokens (**at least 32 characters**) | dev-only value |
| `ADMIN_USERNAME` | Initial admin username | `admin` |
| `ADMIN_PASSWORD` | Initial admin password | `admin123` |
| `ADMIN_EMAIL` | Initial admin email | `admin@example.com` |
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | Database connection | H2 locally, PostgreSQL in Compose |

> The values in `compose.yaml` are for **development only**. Change the secret and the admin password before any real deployment.
