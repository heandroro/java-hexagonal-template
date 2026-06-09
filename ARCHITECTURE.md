# Architecture Overview

> **Quick-start for AI agents:** Read `TEMPLATE-MANIFEST.json` for the machine-readable scaffold descriptor. Read `AGENTS.md` for strict coding rules. This file is the narrative companion.

---

## The Hexagon

```mermaid
flowchart LR
    EXT_KAFKA[/"Kafka\nuser.created"/]
    EXT_SQS_IN[/"SQS\nuser-events"/]

    subgraph INBOUND["Inbound Adapters"]
        direction TB
        API_REST["infra-api-rest\nUserController\n@RestController"]
        API_WS["infra-api-websocket\nUserWebSocketController\n@MessageMapping"]
        API_GRPC["infra-api-grpc\nUserGrpcService\n@GrpcService"]
        KAFKA["infra-kafka\nUserEventListener\n@KafkaListener"]
        SQS_L["infra-sqs\nUserSqsListener\n@SqsListener"]
    end

    subgraph CORE["⬡ CORE — framework-free"]
        direction TB
        PIN["ports.in\n6 UseCases"]
        CMD["command\n3 Commands"]
        UC["usecase\n6 UseCaseImpl\n@Named"]
        POUT["ports.out\nUserRepositoryPort\nUserCachePort"]
        PIN --> UC --> POUT
        CMD -. params .-> UC
    end

    subgraph OUTBOUND["Outbound Adapters"]
        direction TB
        PG["infra-postgres\nUserRepositoryAdapter\n@Repository"]
        MDB["infra-mariadb\nUserRepositoryAdapter\n@Profile('mariadb')"]
        VK["infra-valkey\nUserCacheAdapter\n@Repository"]
        DY["infra-dynamodb\nUserDynamoDbAdapter\n@Profile('dynamodb')"]
        FC["infra-client-api\nExternalUserClient\n@FeignClient"]
        SQS_P["infra-sqs\nUserSqsPublisher\n@Profile('sqs')"]
        SNS["infra-sns\nUserSnsPublisher\n@Profile('sns')"]
    end

    EXT_HTTP(["External Service"])
    EXT_DB[("PostgreSQL")]
    EXT_MARIADB[("MariaDB")]
    EXT_CACHE[("Valkey / Redis")]
    EXT_DYNAMO[("AWS DynamoDB")]
    EXT_SQS[("AWS SQS")]
    EXT_SNS[("AWS SNS")]

    EXT_KAFKA --> KAFKA
    EXT_SQS_IN --> SQS_L
    API_REST --> PIN
    API_WS --> PIN
    API_GRPC --> PIN
    KAFKA --> PIN
    SQS_L --> PIN
    POUT --> PG
    POUT -.-> MDB
    POUT --> VK
    POUT -.-> DY
    POUT -.-> SQS_P
    POUT -.-> SNS
    FC --> EXT_HTTP
    PG --> EXT_DB
    MDB --> EXT_MARIADB
    VK --> EXT_CACHE
    DY --> EXT_DYNAMO
    SQS_P --> EXT_SQS
    SNS --> EXT_SNS
```

---

## Module Map

| Module | Technology | Role |
| --- | --- | --- |
| `core` | Java 21 std + jakarta.inject | Business rules, domain model, command objects, port contracts |
| `infra-api-rest` | Spring Web MVC + springdoc 2.8.4 + MapStruct | REST inbound adapter (Controllers + DTOs) |
| `infra-api-websocket` | Spring WebSocket + Springwolf 1.13.0 | STOMP WebSocket inbound adapter |
| `infra-api-grpc` | net.devh gRPC 3.1.0.RELEASE + Protobuf 3.25.5 + MapStruct | gRPC inbound adapter (Protocol Buffers) |
| `infra-postgres` | Spring Data JPA + Hibernate + Lombok | Relational persistence outbound adapter (PostgreSQL) |
| `infra-mariadb` | Spring Data JPA + Hibernate + Lombok | Relational persistence outbound adapter — drop-in for MariaDB (`@Profile("mariadb")`) |
| `infra-valkey` | Spring Data Redis | Cache outbound adapter (Valkey-compatible) |
| `infra-kafka` | Spring Kafka + Avro + MapStruct | Async messaging inbound/outbound (Kafka) |
| `infra-sqs` | Spring Cloud AWS 3.3 (SQS) + MapStruct | AWS SQS inbound listener + outbound publisher (`@Profile("sqs")`) |
| `infra-sns` | Spring Cloud AWS 3.3 (SNS) + MapStruct | AWS SNS fan-out publisher outbound adapter (`@Profile("sns")`) |
| `infra-dynamodb` | Spring Cloud AWS 3.3 + MapStruct | DynamoDB outbound adapter (`@Profile("dynamodb")`) |
| `infra-client-api` | Spring Cloud OpenFeign | Outbound HTTP client integrations |
| `application` | Spring Boot 3.5.0 | Bootstrapper — wires all modules + `application.yml` |

---

## Data-Flow Walkthrough: `POST /api/v1/users`

```text
HTTP Request
    │
    ▼
UserController.create(@RequestBody CreateUserRequest)   [infra-api-rest]
    │  UserApiMapper.toCommand(request) → CreateUserCommand
    │
    ▼
CreateUserUseCase.execute(CreateUserCommand)            [core — port contract]
    │
    ▼
CreateUserUseCaseImpl.execute(CreateUserCommand)        [core — @Named impl]
    │  command.name(), command.email()
    │  checks UserRepositoryPort.existsByEmail()
    │  calls   UserRepositoryPort.save(User)
    │  calls   UserCachePort.put(User)
    │
    ├──▶ UserRepositoryAdapter.save()                   [infra-postgres]
    │       UserPostgresMapper: User → UserEntity → save → User
    │
    └──▶ UserCacheAdapter.put()                         [infra-valkey]
             RedisTemplate.set(key, user, TTL from app.cache.user.ttl)
    │
    ▼
UserController returns UserResponse (201 Created)
```

### `GET /api/v1/users/{id}` — cache-first

```text
FindUserUseCaseImpl.execute(id)
    │
    ├──▶ UserCachePort.get(id)  → HIT  → return User
    │
    └──▶ UserCachePort.get(id)  → MISS → UserRepositoryPort.findById(id) → return User
```

### `PUT /api/v1/users/{id}` — full replace

```text
UserController.update(@PathVariable id, @RequestBody UpdateUserRequest)   [infra-api-rest]
    │  UserApiMapper.toCommand(request) → UpdateUserCommand
    │
    ▼
UpdateUserUseCase.execute(id, UpdateUserCommand)        [core]
    │  UserRepositoryPort.findById(id) → existing User
    │  UserRepositoryPort.save(updated User)
    │
    ▼
UserController returns UserResponse (200 OK)
```

### `PATCH /api/v1/users/{id}` — partial update

```text
UserController.patch(@PathVariable id, @RequestBody PatchUserRequest)     [infra-api-rest]
    │  UserApiMapper.toCommand(request) → PatchUserCommand   [null fields ignored via @BeanMapping]
    │
    ▼
PatchUserUseCase.execute(id, PatchUserCommand)          [core]
    │  UserRepositoryPort.findById(id) → existing User
    │  null-coalesce: command.name() != null ? command.name() : existing.name()
    │  UserRepositoryPort.save(merged User)
    │
    ▼
UserController returns UserResponse (200 OK)
```

### Kafka: `user.created` topic

```text
UserEventListener.onUserCreated(UserEventPayload)       [infra-kafka — @KafkaListener]
    │
    ▼
CreateUserUseCase.execute(CreateUserCommand)
```

---

## Naming Conventions

| Suffix | Module | Type | Spring annotation |
| --- | --- | --- | --- |
| `UseCase` | `core.ports.in` | `interface` | — |
| `UseCaseImpl` | `core.usecase` | `class` | `@Named` (jakarta) |
| `Command` | `core.command` | `record` | — |
| `Port` | `core.ports.out` | `interface` | — |
| `Adapter` | `infra-*` outbound | `class` | `@Repository` / `@Component` |
| `Controller` | `infra-api-rest` | `class` | `@RestController` |
| `GrpcService` | `infra-api-grpc` | `class` | `@GrpcService` (net.devh) |
| `Listener` | `infra-kafka` | `class` | `@Component` |
| `Mapper` | `infra-*` mapper pkg | `interface` | `@Mapper(componentModel = SPRING)` |
| `Entity` | `infra-postgres` | `class` | `@Entity` |

---

## Key Architecture Rules

1. **`core` is framework-free** — only `jakarta.inject-api` allowed. No `org.springframework.*`, no `jakarta.persistence.*`, no Jackson.
2. **`@Named` not `@Component`** — use `jakarta.inject.Named` on all `*UseCaseImpl` classes for portable bean discovery.
3. **Constructor injection only** — single constructor with `final` fields; no `@Inject` / `@Autowired` in `core`.
4. **Mappers live in `infra-*`** — always in the `*.mapper` package with `@Mapper(componentModel = SPRING)`.
5. **No extra interfaces on adapters** — `UserRepositoryAdapter` implements `UserRepositoryPort` directly; no `IUserRepositoryAdapter`.
6. **No interfaces on inbound adapters** — `UserController` and `UserEventListener` are concrete classes only.
7. **DynamoDB profile isolation** — `UserDynamoDbAdapter` is `@Profile("dynamodb")`; activating it alongside the Postgres adapter causes a duplicate `UserRepositoryPort` bean.
8. **Lombok scoped to JPA modules only** — `infra-postgres` and `infra-mariadb` use Lombok for entity boilerplate; all other layers use records or plain classes.
9. **Command objects in `core.command`** — write use cases receive `*Command` records instead of loose parameters. `infra-api-rest`: MapStruct converts Request DTOs → Commands; REST PATCH uses `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` to skip null Java fields. `infra-api-grpc`: MapStruct uses source presence checkers (`hasXxx()`) to detect absent proto3 optional fields automatically — no manual `default` methods needed.
10. **Profile isolation for alternative adapters** — `infra-mariadb` is `@Profile("mariadb")` as a drop-in for `infra-postgres` (activate one or the other); `infra-sqs` and `infra-sns` use `@Profile("sqs")`/`@Profile("sns")` with `NoOp*` fallbacks via `@ConditionalOnMissingBean` so the app boots without the broker.

> All 10 rules above are automatically enforced by `HexagonalArchitectureTest`
> (ArchUnit 1.3.0) on every `mvn verify` run — located at
> `application/src/test/java/com/mycompany/template/architecture/`.
> Any violation fails the build before the commit reaches CI.

---

## How to Add a New Feature (Blueprint Checklist)

Follow this exact order — do not skip steps or create files out of sequence:

```text
[ ] 1.  core / domain        — add pure domain record/class (zero annotations)
[ ] 2.  core / command       — add *Command record(s) for write operations
[ ] 3.  core / ports.out     — add *Port interface(s) the use case needs
[ ] 4.  core / ports.in      — add *UseCase interface (receives *Command for writes)
[ ] 5.  core / usecase       — add *UseCaseImpl (@Named, clean constructor)
[ ] 6.  infra-postgres       — add *Entity, extend JpaRepository, add *Mapper, add *Adapter
[ ] 7.  infra-valkey         — add *Adapter if caching is needed
[ ] 8.  infra-dynamodb       — add *DynamoDbEntity (mutable, no Lombok), *Mapper, *Adapter (@Profile)
[ ] 9.  messaging            — choose broker: infra-kafka (*Listener), infra-sqs (*Listener + *Publisher + NoOp*Publisher),
                               or infra-sns (*Publisher + NoOp*Publisher) — add payload/notification record, *Mapper
[ ] 10. infra-client-api     — add @FeignClient if external HTTP call needed
[ ] 11. infra-api-rest / infra-api-websocket / infra-api-grpc — choose the inbound protocol:
            REST: add Request/Response records, *Mapper, *Controller (@RestController)
            WebSocket: add Event DTO, *Controller (@MessageMapping)
            gRPC: add .proto definition, *Mapper, *GrpcService (@GrpcService)
```

> **Violation check:** if any infra class imports from `core.domain` without going through a port, or if `core` imports `org.springframework.*`, stop and flag the inconsistency.

---

## LLM Indexing

| File | Purpose |
| --- | --- |
| `TEMPLATE-MANIFEST.json` | Root index: stack, replace tokens, naming/mapper/interface rules, module list |
| `GENERATOR.json` | Setup wizard: architecture profiles, decision questions, post-setup steps |
| `app/{module}/MODULE.json` | Per-module detail: role, packages, keyClasses, config |
| `AGENTS.md` | Prescriptive rules — what to do and what to avoid |
| `ARCHITECTURE.md` | Narrative companion — why the architecture is shaped this way |
| `DEPENDENCY-CATALOG.md` | Versioned index of all libraries, frameworks, and Maven plugins |

---

## Configuration Reference

| Property | Default | Env var override |
| --- | --- | --- |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/hexagonal_db` | — |
| `spring.data.redis.host` | `localhost` | `REDIS_HOST` |
| `spring.data.redis.port` | `6379` | `REDIS_PORT` |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | `KAFKA_BOOTSTRAP_SERVERS` |
| `app.cache.user.ttl` | `PT30M` | `USER_CACHE_TTL` |
| `app.kafka.topics.user-created` | `user.created` | — |
| `spring.cloud.aws.region.static` | `us-east-1` | `AWS_REGION` |
| `spring.cloud.aws.dynamodb.endpoint` | _(empty — uses AWS)_ | `DYNAMODB_ENDPOINT` |
| `app.clients.external-user-service.url` | `http://localhost:8081` | `EXTERNAL_USER_SERVICE_URL` |

---

## Quality Gates

| Tool | Phase | What it checks |
| --- | --- | --- |
| JaCoCo | `verify` | ≥ 90% line + branch coverage per module |
| Checkstyle | `verify` | style — imports, braces, line length ≤ 120 |
| ArchUnit | `verify` | 19 hexagonal rules as JUnit tests |
| SpotBugs | `verify` | bugs and vulnerabilities |
| PMD | `verify` | complexity and code smells |

Configuration lives in `config/checkstyle/`, `config/spotbugs/`, `config/pmd/`.
See `AGENTS.md` section 7 for details and JDK compatibility notes.
