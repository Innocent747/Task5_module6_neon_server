# Task5_module6_neon_server

Ktor 3.x server app for Assignment 5: **Nobel Prize API** with PostgreSQL (Neon compatible).

## Features
- `POST /login` (open): username/password -> JWT token (30 minutes)
- `GET /prizes` (open): reads from DB; if DB is empty, seeds from https://api.nobelprize.org/2.1/
- JWT protected endpoints:
  - `GET /users/me`
  - `GET /users/me/prizes`
  - `POST /users/me/prizes/{prizeId}` (idempotent)
  - `DELETE /users/me/prizes/{prizeId}`
- DB tables:
  - `users(id, username, password_hash, role)`
  - `prizes(id, award_year, category, full_name, motivation, detail_link)`
  - `laureates(id, prize_id, full_name, portion, motivation, portrait_url)`
  - `user_prizes(user_id, prize_id, added_at)` with unique `(user_id, prize_id)`
- Default user auto-seeded on startup if not exists: `admin/admin`
- Call logging enabled
- OpenAPI + Swagger UI

## Environment variables
Set these before run:
- `DB_URL` (for Neon use JDBC URL with `sslmode=require`)
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET` (length must be >= 32)

Example Neon URL format:
`jdbc:postgresql://<host>/<db>?sslmode=require`

## Run
```bash
./gradlew :app:run
```

## Build and test
```bash
./gradlew :app:build
./gradlew :app:test
```

## API docs
- Swagger UI: `http://localhost:8080/swagger`
- OpenAPI spec: `http://localhost:8080/openapi`

## Sample credentials
- username: `admin`
- password: `admin`

HTTP request examples are in `/requests.http`.
