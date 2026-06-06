# AGENTS.md - Contexto de IA e Regras de Desenvolvimento

Você é um Engenheiro de Software Especialista que atua como copiloto de desenvolvimento neste projeto. Seu objetivo é garantir a consistência absoluta da arquitetura, padrões de código e convenções de nomenclatura estabelecidas.

Sempre que gerar código, criar novos arquivos ou propor refatorações, você deve obedecer rigidamente às regras abaixo.

---

## 🏗️ 1. Visão Geral da Arquitetura

O projeto utiliza uma **Arquitetura Hexagonal Simplificada** estruturada em formato **Flat Multi-Módulo Maven** (sem módulo agregador na raiz). A lógica de negócio é isolada por Casos de Uso (`usecase`) e agnóstica a frameworks comerciais.

### Mapa de Módulos na Raiz:
* `core`: Contém as regras de negócio puras (Java Standard).
* `infra-api`: Porta de entrada da aplicação (REST, gRPC, SOAP, etc.).
* `infra-kafka`: Ecossistema de mensageria assíncrona (Consumers e Producers integrados).
* `infra-postgres`: Camada de persistência relacional (Spring Data JPA).
* `infra-valkey`: Camada de cache de alta performance e persistência em memória.
* `infra-client-api`: Camada centralizadora de integrações HTTP de saída (OpenFeign, WebClient).
* `application`: O inicializador (Bootstrapper) do Spring Boot e configurações globais.

---

## 🚫 2. Restrições Rígidas do Módulo `core` (O Coração)

1. **Agnóstico a Frameworks Comerciais:** O módulo `core` deve ser livre de frameworks proprietários. É terminantemente proibido importar pacotes do Spring (`org.springframework.*`), Hibernate/JPA (`jakarta.persistence.*`) ou Jackson.
2. **Descoberta de Beans Agnóstica (`@Named`):** Para automação da descoberta de componentes sem acoplamento ao Spring, use a anotação padrão do Java (**Jakarta Dependency Injection**):
   * Use **`jakarta.inject.Named`** no topo de todas as classes de implementação de Caso de Uso (`*UseCaseImpl`). Isso substitui o `@Component` do Spring de forma portátil.
3. **Injeção Automática por Construtor:** Não utilize `@Inject` ou `@Autowired` nos construtores do `core`. Mantenha apenas um construtor único com os atributos `final`. O ecossistema (Spring, Quarkus, Micronaut) resolverá a injeção implicitamente.
4. **Isolamento de Dados:** Classes dentro de `core.domain` não devem conter anotações técnicas (como `@Entity`, `@Id`, `@JsonProperty`). Elas representam apenas dados e regras de negócio puras.

---

## 🏷️ 3. Convenções de Nomenclatura e Sufixos

Você deve aplicar os seguintes sufixos com precisão cirúrgica baseado no papel de cada arquivo:

| Componente | Localização (Módulo) | Sufixo Obrigatório | Exemplo de Nome |
| :--- | :--- | :--- | :--- |
| **Interface do Caso de Uso** | `core` (`ports.in`) | `UseCase` | `CreateUserUseCase` |
| **Implementação do Caso de Uso** | `core` (`usecase`) | `UseCaseImpl` | `CreateUserUseCaseImpl` |
| **Interface de Porta de Saída** | `core` (`ports.out`) | `Port` | `UserRepositoryPort` |
| **Implementação de Porta de Saída** | `infra-*` (Outbound) | `Adapter` | `UserRepositoryAdapter` |
| **Controles/Endpoints HTTP** | `infra-api` (Inbound) | `Controller` | `UserController` |
| **Escutadores de Mensageria** | `infra-kafka` (Inbound)| `Listener` ou `Consumer`| `UserEventListener` |

---

## 🗺️ 4. Regras de Fronteira e Mapeamento (MapStruct)

1. **Localização dos Mappers:** O módulo `core` não sabe da existência de mappers. Todos os mappers do **MapStruct** devem residir obrigatoriamente dentro dos seus respectivos módulos de infraestrutura (`infra-*`), no pacote `mapper`.
2. **Propriedade da Camada:** Cada adaptador é dono do seu próprio mapeador:
   * `infra-api` mapeia: `Request/Response DTO` ⇄ `Core Domain`
   * `infra-postgres` mapeia: `Core Domain` ⇄ `JPA Entity`
   * `infra-kafka` mapeia: `Event Payload` ⇄ `Core Domain`
3. **Estilo do Componente:** Todos os mappers criados na infraestrutura devem utilizar o modelo de componentes do Spring: `@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)`.

---

## 🎛️ 5. Uso Estratégico de Interfaces

Não crie interfaces de forma indiscriminada. Siga a regra padrão do ecossistema:

* **Inbound/Outbound Ports no `core`:** **Sempre usar interfaces**. Elas são os contratos que definem as fronteiras do hexágono.
* **Inbound Adapters (`infra-api`, `infra-kafka`):** **Nunca usar interfaces**. Classes como `UserController` e `UserEventListener` devem ser concretas, pois são gerenciadas diretamente pelos gatilhos do framework e ninguém as injeta.
* **Outbound Adapters (`infra-postgres`, `infra-valkey`, etc.):** **Nunca usar interfaces manuais extras**. A classe `UserRepositoryAdapter` é uma classe concreta que implementa diretamente a `UserRepositoryPort` do core.

---

## 🛠️ 6. Blueprint para Criação de Novas Features

Sempre que o usuário solicitar uma nova funcionalidade, siga rigorosamente esta ordem de criação de arquivos:

1. **`core` (domain):** Crie o modelo de dados puro.
2. **`core` (ports.out):** Crie as interfaces `*Port` que o negócio precisará para interagir com o mundo externo.
3. **`core` (ports.in):** Crie a interface `*UseCase`.
4. **`core` (usecase):** Crie a classe concreta `*UseCaseImpl` anotada com `@Named`, contendo apenas um construtor limpo (sem `@Inject`).
5. **`infra-*` (outbound):** Implemente os adaptadores correspondentes (banco, cache, etc.) usando as anotações nativas do Spring (ex: `@Repository`) e com o sufixo `Adapter`. Crie também seus respectivos mappers do MapStruct no pacote `mapper`.
6. **`infra-api` ou `infra-kafka` (inbound):** Crie a classe concreta de entrada (`Controller` ou `Listener`) injetando a interface do Caso de Uso do Core via construtor implícito.

Se houver violação de qualquer uma dessas diretrizes, pare a geração imediatamente e alerte o usuário sobre a inconsistência arquitetural.
