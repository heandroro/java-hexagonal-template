# Dependency Catalog

Versioned index of every library, framework, and Maven plugin used in this template.
The authoritative source of truth is the root `pom.xml`; this file is the human-readable companion.

Template version: `1.0.0-SNAPSHOT`

---

## Platform / JVM

| Component | Version | Notes |
|---|---|---|
| Java | **21** (LTS) | Minimum supported runtime |
| Maven | 3.x | No version pinned in root POM |

> **SpotBugs 4.9.x** (ASM 9.8) and **PMD 7.17.0** (maven-pmd-plugin 3.28.0) both support class-file version **69 (JDK 25)**. Both tools run automatically in the `verify` phase.

---

## Spring Ecosystem

### Spring Boot BOM — `3.5.0`

All starters below inherit their version from `spring-boot-dependencies` `3.5.0` unless otherwise noted.

| Artifact | Used in module(s) | Notes |
|---|---|---|
| `spring-boot-starter-web` | `infra-api-rest` | Spring Web MVC |
| `spring-boot-starter-websocket` | `infra-api-websocket` | STOMP over SockJS |
| `spring-boot-starter-validation` | `infra-api-rest` | Bean Validation (Jakarta) |
| `spring-data-commons` | `infra-api-rest` | `Pageable` / `Page` types |
| `spring-boot-starter-data-jpa` | `infra-postgres`, `infra-mariadb` | Hibernate + Spring Data JPA |
| `spring-boot-starter-data-redis` | `infra-valkey` | Spring Data Redis (Valkey-compatible) |
| `spring-kafka` | `infra-kafka` | Spring Kafka consumer / producer |
| `spring-boot-starter-actuator` | `application` | Health, metrics, management endpoints |
| `spring-boot-devtools` | `application` | Hot reload (runtime, optional) |
| `spring-boot-docker-compose` | `application` | Native Docker Compose lifecycle (runtime, optional) |

### Spring Cloud — `2025.0.0`

| Artifact | Used in module(s) | Notes |
|---|---|---|
| `spring-cloud-dependencies` BOM | `infra-client-api` | BOM scope |
| `spring-cloud-starter-openfeign` | `infra-client-api` | Declarative HTTP client |

### Spring Cloud AWS — `3.4.0`

| Artifact | Used in module(s) | Notes |
|---|---|---|
| `spring-cloud-aws-dependencies` BOM | `infra-sqs`, `infra-sns`, `infra-dynamodb` | BOM scope |
| `spring-cloud-aws-starter-sqs` | `infra-sqs` | `@SqsListener` consumer + publisher |
| `spring-cloud-aws-starter-sns` | `infra-sns` | SNS fan-out publisher |
| `spring-cloud-aws-starter-dynamodb` | `infra-dynamodb` | DynamoDB outbound adapter |

---

## Mapping & Code Generation

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `mapstruct` | **1.6.3** | all `infra-*` modules | Runtime mapping annotations |
| `mapstruct-processor` | **1.6.3** | all `infra-*` modules | Annotation processor (provided) |
| `lombok` | **1.18.40** | `infra-postgres`, `infra-mariadb` | Entity boilerplate only |
| `lombok-mapstruct-binding` | **0.2.0** | `infra-postgres`, `infra-mariadb` | Ensures Lombok runs before MapStruct |

---

## Standard Dependency Injection

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `jakarta.inject-api` | **2.0.1** | `core` | `@Named` — portable DI annotation; no Spring in core |

---

## API Documentation

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `springdoc-openapi-starter-webmvc-ui` | **2.8.4** | `infra-api-rest` | OpenAPI 3 + Swagger UI |
| `springwolf-stomp` | **1.13.0** | `infra-api-websocket` | AsyncAPI documentation for STOMP/WebSocket channels |
| `springwolf-ui` | **1.13.0** | `infra-api-websocket` | Springwolf web UI |

---

## gRPC

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `grpc-server-spring-boot-starter` (net.devh) | **3.1.0.RELEASE** | `infra-api-grpc` | gRPC server + Spring DI integration |
| `protobuf-java` (com.google.protobuf) | **3.25.5** | `infra-api-grpc` | Protocol Buffers runtime |
| `javax.annotation-api` | **1.3.2** | `infra-api-grpc` | `@javax.annotation.Generated` — ausente no JDK 9+ |
| `protobuf-maven-plugin` (io.github.ascopes) | **3.1.0** | `infra-api-grpc` | Gera sources .proto; usa `protocVersion` + `binaryMavenPlugins` |

> **Plugin config:** `protocVersion = 3.25.5`, `binaryMavenPlugin = io.grpc:protoc-gen-grpc-java:1.64.0`. Fontes em `src/main/proto`, output em `target/generated-sources/protobuf`.

---

## Messaging & Event Serialization

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `kafka-avro-serializer` (Confluent) | **7.9.1** | `infra-kafka` | Avro serializer for Schema Registry |
| `avro` (Apache Avro) | **1.12.1** | `infra-kafka` | Avro runtime + code generation |

> **Repository:** Confluent artifacts are resolved from `https://packages.confluent.io/maven/` (declared in the root POM).

---

## Persistence Drivers

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `postgresql` JDBC driver | via Spring Boot BOM | `infra-postgres`, `application` (test) | Runtime scope |
| `mariadb-java-client` | via Spring Boot BOM | `infra-mariadb` | Runtime scope |

---

## Serialization (JSON)

| Artifact | Version | Used in module(s) | Notes |
|---|---|---|---|
| `jackson-databind` | via Spring Boot BOM | `infra-valkey` | Redis value serialization |
| `jackson-datatype-jsr310` | via Spring Boot BOM | `infra-valkey` | Java 8 date/time module |

---

## Testing

| Artifact | Version | Used in module(s) | Scope |
|---|---|---|---|
| `spring-boot-starter-test` | via Spring Boot BOM | all modules | test |
| `junit-jupiter` | via Spring Boot BOM | `core` | test |
| `assertj-core` | via Spring Boot BOM | `core` | test |
| `mockito-junit-jupiter` | via Spring Boot BOM | `core` | test |
| `spring-kafka-test` | via Spring Boot BOM | `infra-kafka` | test |
| `spring-boot-testcontainers` | via Spring Boot BOM | `infra-postgres`, `infra-mariadb`, `application` | test |
| `testcontainers` BOM | **1.21.4** | `infra-postgres`, `infra-mariadb`, `application` | BOM scope |
| `testcontainers:postgresql` | via Testcontainers BOM | `infra-postgres`, `application` | test |
| `testcontainers:mariadb` | via Testcontainers BOM | `infra-mariadb` | test |
| `testcontainers:kafka` | via Testcontainers BOM | `application` | test |
| `instancio-junit` | **5.6.0** | all modules | test |
| `datafaker` | **2.5.0** | managed in root BOM | test |
| `archunit-junit5` | **1.4.1** | `application` | test — 19 hexagonal architecture rules |

---

## Build Plugins & Static Analysis

| Plugin | Version | Execution | Module(s) |
|---|---|---|---|
| `maven-compiler-plugin` | **3.13.0** | compile | all |
| `spring-boot-maven-plugin` | **3.5.0** | package (fat-jar) | `application` |
| `avro-maven-plugin` | **1.11.4** | `generate-sources` | `infra-kafka` |
| `jacoco-maven-plugin` | **0.8.14** | `verify` — ≥ 90% lines + branches | all |
| `maven-checkstyle-plugin` | **3.6.0** | `verify` — zero violations | all |
| `spotbugs-maven-plugin` | **4.9.8.3** | `verify` (automatic) — ASM 9.8, supports JDK 11–25 | all |
| `maven-pmd-plugin` | **3.28.0** | `verify` (automatic) — PMD 7.17.0, supports JDK 11–25 | all |

Static analysis configuration files:

| Tool | Config file |
|---|---|
| Checkstyle | `config/checkstyle/checkstyle.xml`, `config/checkstyle/suppressions.xml` |
| SpotBugs | `config/spotbugs/exclude.xml` |
| PMD | `config/pmd/ruleset.xml` |

---

## JDK 25 Testing Status & Community Health

Legenda:
- **JDK 25 testado?** — `✅ confirmado` (CI matrix ou release notes citam JDK 25 explicitamente) · `🔶 compatível` (passou no `mvn verify` local, sem CI JDK 25 próprio) · `⬜ spec` (artefato de especificação, sem runtime)
- **Comunidade** — `🟢 ativa` (múltiplos releases/ano, issues respondidas) · `🟡 moderada` · `⬜ spec`

| Biblioteca | Versão | JDK 25 testado? | Evidência | Comunidade |
|---|---|:---:|---|:---:|
| Spring Boot | 3.5.0 | 🔶 compatível | Spring 6.2/Boot 3.x testa LTS (17, 21); Boot 4.0 = 1ª versão com JDK 25 first-class | 🟢 ativa |
| Spring Cloud | 2025.0.0 | 🔶 compatível | Sem matrix JDK 25 documentada em 2025.0.x | 🟢 ativa |
| Spring Cloud AWS | 3.4.0 | 🔶 compatível | Sem matrix JDK 25 documentada | 🟡 moderada |
| MapStruct | 1.6.3 | 🔶 compatível | Annotation processor — não executa em runtime; sem CI JDK 25 documentada | 🟢 ativa |
| Lombok | 1.18.40 | ✅ confirmado | Issue #3859 — correções explícitas para `val` e `@ExtensionMethod` no JDK 25 | 🟢 ativa |
| Jakarta Inject API | 2.0.1 | ⬜ spec | Artefato de especificação — sem runtime a testar | ⬜ spec |
| springdoc-openapi | 2.8.4 | 🔶 compatível | Sem matrix JDK 25 documentada | 🟡 moderada |
| springwolf | 1.13.0 | 🔶 compatível | Sem matrix JDK 25 documentada | 🟡 moderada |
| JaCoCo | 0.8.14 | ✅ confirmado | ASM 9.10.1 — suporte oficial JDK 25 adicionado (out/2025) | 🟢 ativa |
| ArchUnit | 1.4.1 | ✅ confirmado | PR #1440 — suporte explícito a class-file major version 69 (JDK 25) | 🟡 moderada |
| Testcontainers | 1.21.4 | 🔶 compatível | Sem matrix JDK 25 explícita; última versão estável (dez/2025) | 🟢 ativa |
| Instancio | 5.6.0 | ✅ confirmado | Suporte JDK 25 adicionado explicitamente em 5.6.x | 🟢 ativa |
| DataFaker | 2.5.0 | ✅ confirmado | JDK 25 EA incluído no CI a partir de 2.5.0 | 🟢 ativa |
| Apache Avro | 1.12.1 | 🔶 compatível | Sem menção explícita a JDK 25 nos release notes (out/2025) | 🟡 moderada |
| Confluent serializer | 7.9.1 | 🔶 compatível | Sem matrix JDK 25; compatível via Avro 1.12.1 | 🟡 moderada |
| SpotBugs plugin | 4.9.8.3 | ✅ confirmado | ASM 9.8 — suporte JDK 25 adicionado em 4.9.7 (mar/2026) | 🟢 ativa |
| maven-pmd-plugin / PMD | 3.28.0 / 7.17.0 | ✅ confirmado | PMD 7.16.0 adicionou suporte Java 25 (issue pmd/pmd#5478 / PR#5872) | 🟢 ativa |
| maven-checkstyle-plugin | 3.6.0 | 🔶 compatível | Checkstyle 9.3 — sem menção explícita de JDK 25 no CI | 🟡 moderada |

> **7 de 18** entradas têm JDK 25 testado e confirmado com evidência pública.
> As demais (`🔶 compatível`) passaram no `mvn clean verify` local mas ainda não expõem JDK 25 em seus próprios pipelines de CI.

---

## Module × Dependency Matrix

Quick reference for selective upgrades — which module is affected by each key dependency.

| Dependency | core | infra-api-rest | infra-api-websocket | infra-api-grpc | infra-kafka | infra-postgres | infra-mariadb | infra-valkey | infra-dynamodb | infra-sqs | infra-sns | infra-client-api | application |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Spring Boot BOM | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Spring Cloud BOM (2025.0.0) | — | — | — | — | — | — | — | — | — | — | — | ✓ | — |
| Spring Cloud AWS BOM (3.4.0) | — | — | — | — | — | — | — | — | ✓ | ✓ | ✓ | — | — |
| MapStruct 1.6.3 | — | ✓ | — | ✓ | ✓ | ✓ | ✓ | — | ✓ | ✓ | ✓ | — | — |
| Lombok 1.18.40 | — | — | — | — | — | ✓ | ✓ | — | — | — | — | — | — |
| Jakarta Inject 2.0.1 | ✓ | — | — | — | — | — | — | — | — | — | — | — | — |
| Confluent / Avro 7.9.1 / 1.12.1 | — | — | — | — | ✓ | — | — | — | — | — | — | — | — |
| SpringDoc 2.8.4 | — | ✓ | — | — | — | — | — | — | — | — | — | — | — |
| Springwolf 1.13.0 | — | — | ✓ | — | — | — | — | — | — | — | — | — | — |
| gRPC / Protobuf 3.25.5 | — | — | — | ✓ | — | — | — | — | — | — | — | — | — |
| Testcontainers 1.21.4 | — | — | — | — | — | ✓ | ✓ | — | — | — | — | — | ✓ |
| Instancio 5.6.0 | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| ArchUnit 1.4.1 | — | — | — | — | — | — | — | — | — | — | — | — | ✓ |
