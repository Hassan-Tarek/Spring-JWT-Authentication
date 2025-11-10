# Spring JWT Authentication & Authorization

A production-ready **Spring Boot 3** project implementing **JWT-based authentication and authorization** with refresh tokens, role-based access control, and secure token management.

---

## Features

- Register & Login (email/password)
- Generate Access & Refresh Tokens
- Refresh token rotation & revocation
- Logout (single session or all sessions)
- Role-based authorization
- Secure password hashing (BCrypt)
- Global exception handling
- Clean, layered architecture (Controller → Service → Repository)

---

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security 6**
- **Spring Data JPA (Hibernate)**
- **MySQL**
- **JWT (io.jsonwebtoken / JJWT)**
- **Lombok**
- **Jakarta Validation**

---

## Project Structure

```
src/main/java/com/jwt/auth
├── config/              # Security & JWT configuration
├── controller/          # REST controllers
├── dto/
│   ├── request/         # Incoming requests
│   └── response/        # Outgoing responses
├── entity/              # JPA entities
├── exception/           # Custom exceptions & handler
├── mapper/              # mappers
├── repository/          # Data access layer
├── service/             # Business logic (JWT, Auth, RefreshToken)
└── JwtAuthApplication.java
```