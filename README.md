# java-hexagonal-template

A production-ready **Hexagonal Architecture** project template using a **flat multi-module Maven** layout.

## Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Persistence | Spring Data JPA + PostgreSQL |
| Cache | Spring Data Redis (Valkey-compatible) |
| Messaging | Spring Kafka |
| HTTP Client | Spring Cloud OpenFeign |
| Mapping | MapStruct 1.6 |
| DI annotation | Jakarta Inject (`@Named`) |

## Module Structure

```
java-hexagonal-template/
├── core/                    # Business rules — zero framework dependencies
├── infra-api/               # REST inbound adapter (Spring Web MVC)
├── infra-kafka/             # Async messaging adapter (Spring Kafka)
├── infra-postgres/          # Relational persistence adapter (Spring Data JPA)
├── infra-valkey/            # Cache adapter (Spring Data Redis / Valkey)
├── infra-client-api/        # Outbound HTTP adapter (OpenFeign)
└── application/             # Spring Boot bootstrapper + configuration
```

## Reference Domain: `User`

The template ships with a full `User` feature to demonstrate all architectural boundaries:

```
POST /api/v1/users          → CreateUserUseCase → UserRepositoryAdapter (Postgres) + UserCacheAdapter (Valkey)
GET  /api/v1/users/{id}     → FindUserUseCase   → UserCacheAdapter (hit) or UserRepositoryAdapter (miss)
Kafka topic: user.created   → UserEventListener → CreateUserUseCase
```

## Running Locally

Prerequisites: Docker (for Postgres, Valkey/Redis, Kafka).

```bash
# Start infrastructure
docker compose up -d

# Build & run
./mvnw clean package -pl application -am
java -jar application/target/application-1.0.0-SNAPSHOT.jar
```

## Architecture Rules

See [AGENT.md](AGENT.md) for the full set of architectural constraints enforced by the AI copilot.
