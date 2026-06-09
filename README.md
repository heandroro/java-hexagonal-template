# java-hexagonal-template

A production-ready **Hexagonal Architecture** project template using a **multi-module Maven** layout.

## Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Persistence | Spring Data JPA (PostgreSQL · MariaDB) · AWS DynamoDB |
| Cache | Spring Data Redis (Valkey-compatible) |
| Messaging | Spring Kafka · AWS SQS · AWS SNS |
| HTTP Client | Spring Cloud OpenFeign |
| Mapping | MapStruct 1.6 |
| DI annotation | Jakarta Inject (`@Named`) |
| Testing | JUnit 5 · Mockito · Instancio · Testcontainers |
| Coverage | JaCoCo ≥ 90% lines + branches per module |
| Static analysis | Checkstyle (verify) · SpotBugs · PMD |
| Architecture tests | ArchUnit 1.3.0 — 19 automated rules |

## Module Structure

```
java-hexagonal-template/
├── app/
│   ├── core/                    # Business rules — zero framework dependencies
│   ├── infra-api/               # REST inbound adapter (Spring Web MVC)
│   ├── infra-kafka/             # Async messaging adapter (Spring Kafka)
│   ├── infra-postgres/          # Relational persistence adapter (Spring Data JPA + PostgreSQL)
│   ├── infra-mariadb/           # Relational persistence adapter (MariaDB) — @Profile("mariadb")
│   ├── infra-valkey/            # Cache adapter (Spring Data Redis / Valkey)
│   ├── infra-dynamodb/          # NoSQL persistence adapter (AWS DynamoDB) — @Profile("dynamodb")
│   ├── infra-sqs/               # SQS inbound listener + outbound publisher — @Profile("sqs")
│   ├── infra-sns/               # SNS fan-out publisher — @Profile("sns")
│   ├── infra-client-api/        # Outbound HTTP adapter (OpenFeign)
│   └── application/             # Spring Boot bootstrapper + configuration
├── infra/
│   └── local/
│       └── docker-compose.yml   # Dev infrastructure (Postgres, Kafka, Valkey, LocalStack…)
├── config/                      # Static analysis configs (Checkstyle, SpotBugs, PMD)
└── pom.xml                      # Root Maven POM
```

## Reference Domain: `User`

The template ships with a full `User` CRUD feature to demonstrate all architectural boundaries:

```
POST   /api/v1/users         → CreateUserUseCase   → UserRepositoryAdapter (Postgres) + UserCacheAdapter (Valkey)
GET    /api/v1/users         → FindAllUsersUseCase → UserRepositoryAdapter (paginated)
GET    /api/v1/users/{id}    → FindUserUseCase     → UserCacheAdapter (hit) or UserRepositoryAdapter (miss)
PUT    /api/v1/users/{id}    → UpdateUserUseCase   → UserRepositoryAdapter
PATCH  /api/v1/users/{id}    → PatchUserUseCase    → UserRepositoryAdapter
DELETE /api/v1/users/{id}    → DeleteUserUseCase   → UserRepositoryAdapter + UserCacheAdapter (evict)
Kafka: user.created          → UserEventListener  → CreateUserUseCase
SQS:   user-events-queue     → UserSqsListener    → CreateUserUseCase
```

## Running Locally

Prerequisites: Docker (for Postgres, Valkey/Redis, Kafka).

```bash
# Start infrastructure
docker compose -f infra/local/docker-compose.yml up -d

# Build & run
mvn clean package -pl app/application -am
java -jar app/application/target/application-1.0.0-SNAPSHOT.jar
```

## Quality Gates

| Check | Command | Threshold |
|---|---|---|
| Unit tests | `mvn verify` | zero failures — build breaks |
| JaCoCo coverage | `mvn verify` | ≥ 90% lines + branches per module |
| Checkstyle | `mvn verify` | zero violations (line ≤ 120, imports, braces) |
| ArchUnit (19 rules) | `mvn verify` | zero architectural violations |
| SpotBugs | `mvn verify` | threshold High |
| PMD | `mvn verify` | cyclomatic complexity ≤ 15 |

## Architecture Rules

See [AGENTS.md](AGENTS.md) for the full set of architectural constraints enforced by the AI copilot.
See [ARCHITECTURE.md](ARCHITECTURE.md) for the narrative guide and hexagon diagram.
