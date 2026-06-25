# PayPal Clone — Microservices Backend

A simplified PayPal-like payment backend built with **Spring Boot microservices**, **Kafka** event streaming, **JWT** authentication, and **Docker Compose** orchestration.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client / API Consumer                    │
└────────────────────┬────────────────────────────┬───────────────┘
                     │ REST :8082                  │ REST :8083
         ┌───────────▼──────────┐      ┌──────────▼──────────────┐
         │    User Service      │      │   Transaction Manager    │
         │  (Spring Boot 4.0.6) │      │   (Spring Boot 4.0.6)   │
         │                      │      │                          │
         │  • User registration │      │  • Create transactions   │
         │  • Get users         │      │  • Query transactions    │
         │  • JWT generation    │      │  • Publish events        │
         │                      │      │                          │
         │  H2 in-memory DB     │      │  H2 in-memory DB         │
         └──────────────────────┘      └──────────┬───────────────┘
                                                  │ Kafka Producer
                                       ┌──────────▼───────────────┐
                                       │   Apache Kafka (KRaft)   │
                                       │   Topic: transactions     │
                                       │   Port: 9092              │
                                       └──────────────────────────┘
```

**Event Flow:** Every new transaction is persisted to H2 and then immediately published as a JSON event to the `transactions` Kafka topic, enabling downstream consumers (analytics, notifications, fraud detection) to react asynchronously.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Build Tool | Gradle 9.4.1 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Messaging | Apache Kafka (KRaft mode) |
| ORM | Spring Data JPA / Hibernate |
| Database | H2 (in-memory, embedded) |
| Containerization | Docker + Docker Compose |

---

## Services

### User Service — port `8082`

Manages user accounts and issues JWT tokens.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Create a new user |
| `GET` | `/api/users/{id}` | Get a user by ID |
| `GET` | `/api/users/all` | List all users |

**Request body — POST /api/users**
```json
{
  "name": "Suhas Kumar",
  "email": "suhas@example.com",
  "password": "secret"
}
```

---

### Transaction Manager — port `8083`

Handles money transfers and streams every transaction to Kafka.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/transactions` | Create a transaction (publishes to Kafka) |
| `GET` | `/api/transactions/{id}` | Get transaction by ID |
| `GET` | `/api/transactions/all` | List all transactions |
| `GET` | `/api/transactions/sender/{senderId}` | Transactions sent by a user |
| `GET` | `/api/transactions/receiver/{receiverId}` | Transactions received by a user |

**Request body — POST /api/transactions**
```json
{
  "senderId": 1,
  "receiverId": 2,
  "amount": 250.00,
  "status": "SUCCESS",
  "type": "TRANSFER"
}
```

---

## Quick Start with Docker

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Compose)

### Run everything
```bash
docker compose up --build
```

This will:
1. Start **Kafka** in KRaft mode (no Zookeeper needed)
2. Build and start **user-service** on port `8082`
3. Build and start **transaction-manager** on port `8083` (waits for Kafka to be healthy)

First build downloads Gradle 9.4.1 and all Maven dependencies — allow ~5 minutes.

### Stop
```bash
docker compose down
```

### Tear down including volumes
```bash
docker compose down -v
```

---

## Run Locally (without Docker)

### Prerequisites
- Java 17
- Kafka running on `localhost:9092`

### User Service
```bash
cd user-service
./gradlew bootRun
# Windows: gradlew.bat bootRun
```

### Transaction Manager
```bash
cd transaction-manager
# Gradle wrapper not included — use user-service wrapper or install Gradle 9.4.1
../user-service/gradlew bootRun
```

---

## Environment Variables

| Variable | Service | Default | Description |
|---|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | transaction-manager | `localhost:9092` | Kafka broker address |

---

## Project Structure

```
paypal-clone/
├── docker-compose.yml              # Orchestrates all services
├── .dockerignore
├── user-service/                   # Microservice 1
│   ├── Dockerfile
│   ├── build.gradle
│   ├── gradlew / gradlew.bat
│   └── src/main/java/com/paypal/user_service/
│       ├── controller/UserController.java
│       ├── entity/User.java
│       ├── repository/UserRepository.java
│       ├── service/UserserviceImpl.java
│       ├── security/SecurityConfig.java
│       └── util/JWTutil.java       # JWT generation (HS256, 24h expiry)
│
└── transaction-manager/            # Microservice 2
    ├── Dockerfile
    ├── build.gradle
    └── src/main/java/com/paypal/transaction_manager/
        ├── controller/TransactionController.java
        ├── entity/Transaction.java
        ├── repository/TransactionRepository.java
        ├── service/TransactionServiceImpl.java
        └── config/KafkaConfig.java # Producer — topic: "transactions"
```

---

## Implementation Notes

- **JWT** is generated by `user-service` using HMAC-SHA256. The filter (`JWTrequestFilter`) is wired but endpoints are currently open (`permitAll`) for development.
- **Kafka** is run in **KRaft mode** (no Zookeeper) using `bitnami/kafka:3.7`. The topic `transactions` is auto-created on first publish.
- **H2** databases are in-memory and reset on restart. Swap to PostgreSQL or MySQL by adding the driver to `build.gradle` and updating `application.properties`.
- Both Dockerfiles share the **same root build context** (`.`) so the transaction-manager can reuse the Gradle wrapper from user-service.
