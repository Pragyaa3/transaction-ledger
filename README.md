# Transaction Ledger API

A production-style financial transaction ledger built with Spring Boot, demonstrating core enterprise Java patterns used in banking and fintech systems.

## Tech Stack

- Java 17 + Spring Boot 3.5
- Spring Data JPA + Hibernate (PostgreSQL)
- Maven
- Docker
- JUnit 5 + MockMvc (integration tests)

## Architecture
```
src/main/java/com/jpmc/ledger/
├── controller/       # REST layer
├── service/          # Business logic + @Transactional
├── repository/       # Spring Data JPA
├── entity/           # JPA entities with optimistic locking
├── dto/              # Request/Response objects
└── exception/        # Global error handling
```

## Key Design Decisions

**Optimistic locking (`@Version`)** — concurrent balance updates are handled via JPA versioning rather than pessimistic locks. This avoids serializing all transfers and scales better under typical ledger access patterns (low contention per account).

**`@Transactional` on transfers** — debit and credit operations succeed or fail atomically. A server crash mid-transfer rolls back both operations, maintaining ledger consistency.

**DTO separation** — JPA entities are never exposed directly in API responses, preventing accidental leakage of internal fields like `version`.

**Global exception handler** — all errors return a consistent `ApiResponse` envelope with `success`, `message`, and `data` fields.

## Running Locally

**Prerequisites:** Java 17+, PostgreSQL, Maven
```bash
# Clone the repo
git clone https://github.com/Pragyaa3/transaction-ledger.git
cd transaction-ledger

# Configure database in src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ledgerdb
spring.datasource.username=your_user
spring.datasource.password=your_password

# Run
mvn spring-boot:run
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create account |
| POST | `/api/v1/accounts/{id}/deposit` | Deposit funds |
| GET | `/api/v1/accounts/{id}/balance` | Get balance |
| POST | `/api/v1/transactions` | Transfer funds |
| GET | `/api/v1/transactions/{accountId}` | Transaction history (paginated) |
| GET | `/api/v1/health` | Health check |

## Example Usage

**Create account:**
```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerName":"Alice"}'
```

**Transfer funds:**
```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":"<id>","toAccountId":"<id>","amount":250}'
```

**Insufficient funds response:**
```json
{"success":false,"message":"Insufficient funds","data":null}
```

## Tests
```bash
mvn test
# Tests run: 6, Failures: 0, Errors: 0
```

Covers: account creation, validation, deposit, transfer, insufficient funds, account not found.