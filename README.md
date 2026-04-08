# Spring Security - Arquitetura Hexagonal (Ports & Adapters)

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-green)
![Maven](https://img.shields.io/badge/Maven-Build-blue)

> Projeto de exemplo em Spring Boot que implementa autenticação e registro de usuários utilizando **Spring Security**, **arquitetura hexagonal** (Ports & Adapters) e **JWT**.

## 📋 Visão Geral

Este projeto demonstra a implementação de um sistema de autenticação robusto seguindo princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**. A arquitetura hexagonal permite que a lógica de negócio central seja completamente isolada dos detalhes de implementação externos (frameworks, bancos de dados, etc.).

### Principais Funcionalidades

- **Registro de Usuários**: Criação de contas com validação de email único
- **Autenticação JWT**: Login com tokens JWT (JSON Web Tokens)
- **Segurança**: Spring Security com Password Encoding (BCrypt)
- **Persistência**: JPA/Hibernate com PostgreSQL
- **Migrações**: Flyway para versionamento do schema
- **Observabilidade**: Métricas Prometheus via Actuator

## 🛠️ Tecnologias

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.10 | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | ORM e persistência |
| PostgreSQL | Latest | Banco de dados relacional |
| JWT (Auth0) | 4.4.0 | Geração de tokens JWT |
| Flyway | - | Migrações de banco de dados |
| Docker | Latest | Containerização |
| Micrometer | - | Métricas e monitoramento |

## 🏗️ Arquitetura

A aplicação segue o padrão **Arquitetura Hexagonal** (também conhecido como **Ports & Adapters**), que separa claramente o domínio central das dependências externas.

```
┌──────────────────────────────────────────────────────────────────────────┐
│                              CLIENT                                       │
│                    (HTTP Requests / Postman / Frontend)                   │
└─────────────────────────────────┬────────────────────────────────────────┘
                                  │
                                  ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        INBOUND ADAPTERS                                   │
│  ┌────────────────────────────────────┐  ┌──────────────────────────┐    │
│  │  AuthController.java               │  │  HelloController.java    │    │
│  │  - POST /auth/login               │  │  - GET /hello          │    │
│  │  - POST /auth/register            │  │                          │    │
│  │  Location:                        │  │  Location:               │    │
│  │  infrastruct/adapters/inbound/   │  │  infrastruct/adapters/   │    │
│  └────────────────┬─────────────────┘  │  inbound/                │    │
│                   │                    └──────────────────────────┘    │
└───────────────────┼─────────────────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        INBOUND PORTS (Interfaces)                        │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                     AuthUseCase.java                            │    │
│  │  - registerUseCase(RegisterUserRequest): RegisterUserResponse   │    │
│  │  - loginUseCase(LoginRequest): ResponseEntity<?>                │    │
│  │  Location: application/ports/inbound/                          │    │
│  └────────────────────────────────┬────────────────────────────────┘    │
│                                   │                                       │
└───────────────────────────────────┼───────────────────────────────────────┘
                                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                                 │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                      AuthService.java                           │    │
│  │  - Implementa AuthUseCase                                       │    │
│  │  - Orquestra regras de negócio                                  │    │
│  │  - Coordena portas outbound                                     │    │
│  │  - Codifica senhas (BCrypt)                                     │    │
│  │  - Gera tokens JWT                                              │    │
│  │  Location: application/service/                                 │    │
│  └────────────────────────────────┬────────────────────────────────┘    │
│                                   │                                       │
└───────────────────────────────────┼───────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       OUTBOUND PORTS (Interfaces)                        │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                   AuthRepositoryPort.java                       │    │
│  │  - saveUser(UserModel): UserModel                              │    │
│  │  - existsByEmail(String): boolean                               │    │
│  │  Location: application/ports/outbound/                         │    │
│  └────────────────────────────────┬────────────────────────────────┘    │
│                                   │                                       │
└───────────────────────────────────┼───────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       OUTBOUND ADAPTERS                                  │
│  ┌────────────────────────────────────────┐  ┌──────────────────────┐    │
│  │  AuthRepositoryPortImpl.java           │  │  TokenConfig.java    │    │
│  │  - Implementa AuthRepositoryPort       │  │  - Gera tokens JWT   │    │
│  │  - Salva usuários no banco            │  │  - Valida secrets     │    │
│  │  Location:                             │  │  Location:           │    │
│  │  infrastruct/adapters/outbound/        │  │  config/security/     │    │
│  └────────────────┬───────────────────────┘  └──────────────────────┘    │
│                   │                                                        │
└───────────────────┼────────────────────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       PERSISTENCE LAYER                                  │
│  ┌──────────────────────────────┐  ┌────────────────────────────────┐    │
│  │  UserEntity.java             │  │  UserRepositoryConcret.java   │    │
│  │  - JPA Entity                │  │  - Spring Data JPA Repository  │    │
│  │  - Implementa UserDetails    │  │  - findUserByEmail()          │    │
│  │  Location:                    │  │  Location:                    │    │
│  │  infrastruct/persistence/     │  │  infrastruct/persistence/     │    │
│  │  entity/                      │  │  repository/                  │    │
│  └───────────────────────────────┘  └────────────────────────────────┘    │
│                                    │                                       │
└────────────────────────────────────┼───────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                           DATABASE                                       │
│                        PostgreSQL                                        │
│                    (users table via Flyway)                             │
└──────────────────────────────────────────────────────────────────────────┘
```

### Estrutura de Pastas

```
src/main/java/com/application/security/
├── SecurityApplication.java          # Ponto de entrada
│
├── domain/                            # DOMAIN LAYER
│   ├── UserModel.java               # Entidade de domínio pura
│   └── dto/
│       ├── request/                 # DTOs de entrada (Records Java)
│       │   ├── LoginRequest.java
│       │   └── RegisterUserRequest.java
│       └── response/                # DTOs de saída (Records Java)
│           ├── LoginResponse.java
│           └── RegisterUserResponse.java
│
├── application/                       # APPLICATION LAYER
│   ├── ports/
│   │   ├── inbound/                 # DRIVING PORTS (Casos de Uso)
│   │   │   └── AuthUseCase.java     # Interface para autenticação
│   │   └── outbound/                # DRIVEN PORTS (Infraestrutura)
│   │       └── AuthRepositoryPort.java
│   └── service/
│       └── AuthService.java         # Implementação dos casos de uso
│
└── infrastruct/                      # INFRASTRUCTURE LAYER
    ├── adapters/
    │   ├── inbound/                 # REST Controllers
    │   │   ├── HelloController.java
    │   │   └── security/
    │   │       └── AuthController.java
    │   └── outbound/               # Repositórios
    │       └── AuthRepositoryPortImpl.java
    ├── config/
    │   └── security/
    │       ├── AuthConfig.java     # UserDetailsService
    │       ├── SecurityConfig.java # Configuração HTTP Security
    │       └── TokenConfig.java    # Geração JWT
    └── persistence/
        ├── entity/
        │   └── UserEntity.java     # JPA Entity
        └── repository/
            └── UserRepositoryConcret.java  # Spring Data Repository
```

## 🎨 Design Patterns

Este projeto utiliza diversos padrões de design para garantir manutenção, testabilidade e baixo acoplamento.

### 1. Strategy Pattern (Ports & Adapters)

O coração da arquitetura hexagonal. Define contratos (interfaces) que permitem trocar implementações.

```java
// INBOUND PORT - Interface para casos de uso
public interface AuthUseCase {
    RegisterUserResponse registerUseCase(RegisterUserRequest userRequest);
    ResponseEntity<?> loginUseCase(LoginRequest loginRequest);
}

// IMPLEMENTAÇÃO - Strategy concreta
@Service
public class AuthService implements AuthUseCase {
    // ... implementação
}

// OUTBOUND PORT - Interface para repositório
public interface AuthRepositoryPort {
    UserModel saveUser(UserModel userModel);
    boolean existsByEmail(String email);
}
```

**Benefício**: Permite testar a lógica de negócio sem depender do banco de dados.

### 2. Adapter Pattern

Controllers actúan como adaptadores entre o mundo HTTP e a lógica de negócio.

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthUseCase authUseCase; // Depende da interface
    
    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {
        return authUseCase.registerUseCase(request);
    }
}
```

### 3. Builder Pattern (via Java Records)

DTOs são implementados como Records, que são imutáveis por natureza.

```java
// Records são automaticamente builders
public record LoginRequest(
    @NotEmpty(message = "Email é obrigatório.")
    String email,
    
    @NotEmpty(message = "Senha é obrigatória")
    String password
) {}

// Uso: new LoginRequest(email, password)
```

### 4. Factory Pattern (Spring IoC)

Spring funciona como factory através do `ApplicationContext`.

```java
// Todas as dependências são "fabricadas" pelo Spring
@Service
public class AuthService {
    private final UserRepositoryConcret userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    
    public AuthService(UserRepositoryConcret userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenConfig tokenConfig,
                       AuthRepositoryPort authRepositoryPort) {
        // Spring injeta as dependências automaticamente
    }
}
```

### 5. Proxy Pattern (Spring Security)

Spring Security utiliza uma cadeia de filtros (proxies) para segurança.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        return httpSecurity.build(); // Retorna proxy de filtro
    }
}
```

## 📝 Convenções de Código

### Naming Conventions

| Elemento | Convenção | Exemplo |
|----------|-----------|---------|
| Classes | PascalCase | `AuthController`, `UserModel` |
| Métodos | camelCase | `findUserByEmail()`, `loginUseCase()` |
| Variáveis | camelCase | `userRepository`, `passwordEncoder` |
| Interfaces | PascalCase | `AuthUseCase`, `AuthRepositoryPort` |
| DTOs (Records) | PascalCase | `LoginRequest`, `RegisterUserResponse` |

### Package Structure

```
com.application.security/
├── domain/              # Modelo de domínio puro (sem dependências)
├── application/         # Casos de uso e portas
│   ├── ports/
│   │   ├── inbound/    # Interfaces para drivers
│   │   └── outbound/   # Interfaces para driven
│   └── service/        # Implementações
└── infrastruct/         # Implementações concretas
    ├── adapters/        # Controllers e repositórios
    ├── config/          # Configurações Spring
    └── persistence/     # Entidades e JPA
```

### Dependency Injection

**Convenção**: Todos os componentes usam **Constructor Injection**.

```java
@Service
public class AuthService implements AuthUseCase {
    private final UserRepositoryConcret userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Constructor injection (imutável)
    public AuthService(UserRepositoryConcret userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
```

### Brace Style

**Convenção**: K&R (One True Brace Style)

```java
public void method() {
    if (condition) {
        doSomething();
    } else {
        doSomethingElse();
    }
}
```

### Error Handling

**Convenção**: Try-catch com tratamento específico

```java
try {
    // lógica de negócio
} catch (BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Erro: Credenciais inválidas.");
} catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Erro inesperado.");
}
```

## 📡 API Reference

### Base URL

```
http://localhost:8181
```

### Endpoints

#### 1. Registro de Usuário

**POST** `/auth/register`

Registra um novo usuário no sistema.

**Request:**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123"
}
```

**Response (201 Created):**
```json
{
  "name": "João Silva",
  "email": "joao@email.com"
}
```

**Response (409 Conflict):**
```json
{
  "message": "Email já cadastrado no sistema."
}
```

---

#### 2. Login

**POST** `/auth/login`

Autentica um usuário e retorna um token JWT.

**Request:**
```json
{
  "email": "joao@email.com",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (401 Unauthorized):**
```json
{
  "message": "Erro: Credenciais inválidas. Por favor, verifique seu email e senha."
}
```

---

#### 3. Health Check

**GET** `/hello`

Endpoint de teste (sem autenticação).

**Response (200 OK):**
```
Welcome
```

---

#### 4. Métricas Prometheus

**GET** `/actuator/prometheus`

Métricas da aplicação para monitoramento.

---

## 🚀 Setup e Instalação

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker e Docker Compose
- PostgreSQL (ou usar Docker)

### 1. Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/security_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=your-super-secret-key-at-least-32-characters-long
```

### 2. Configuração do Banco

**Opção A: Docker Compose (Recomendado)**

```bash
# Subir PostgreSQL + pgAdmin
docker compose -f infra/docker-compose.yaml up -d
```

**Opção B: Aplicação Completa**

```bash
# Subir toda a stack (App + DB + Monitoring)
docker compose -f infra/docker-compose-application.yaml up -d
```

### 3. Execução Local

```bash
# Compilar
./mvnw clean package

# Executar
./mvnw spring-boot:run

# Ou usar JAR
java -jar target/security-0.0.1-SNAPSHOT.jar
```

### 4. Execução de Testes

```bash
./mvnw test
```

## 🔐 Fluxo de Dados - Autenticação JWT

```
┌──────────┐     POST /auth/login      ┌─────────────────┐
│  Client  │ ───────────────────────▶  │ AuthController  │
└──────────┘                          └────────┬────────┘
                                               │
                                               ▼
                                    ┌─────────────────┐
                                    │  AuthService    │
                                    │  (Use Case)     │
                                    └────────┬────────┘
                                             │
                        ┌────────────────────┼────────────────────┐
                        ▼                    ▼                    ▼
              ┌──────────────┐    ┌──────────────────┐   ┌─────────────┐
              │ AuthConfig  │    │ PasswordEncoder  │   │ TokenConfig │
              │ (UserDetail)│    │ (BCrypt)         │   │ (JWT Gen)   │
              └──────────────┘    └──────────────────┘   └─────────────┘
                        │                                       │
                        ▼                                       ▼
              ┌──────────────────┐                  ┌─────────────────┐
              │ UserRepository   │                  │  JWT Token       │
              │ (PostgreSQL)     │                  │  (Retornado)     │
              └──────────────────┘                  └─────────────────┘
```

### Validação de Token

```
┌──────────┐    GET /secure     ┌──────────────────┐
│  Client  │ ─────────────────▶ │ Security Filter  │
└──────────┘  + JWT Header      │   (JWT Filter)   │
                               └────────┬─────────┘
                                        │
                                        ▼
                              ┌──────────────────┐
                              │ TokenConfig      │
                              │ (validateToken)  │
                              └────────┬─────────┘
                                       │
                                       ▼
                              ┌──────────────────┐
                              │  SecurityContext │
                              │  (Authenticated) │
                              └──────────────────┘
```

## 📊 Decisões Arquiteturais

### 1. Arquitetura Hexagonal

**Decisão**: Isolar a lógica de negócio em domínio puro.

**Justificativa**:
- Testabilidade: Fácil testar regras de negócio sem frameworks
- Flexibilidade: Trocar banco de dados ou UI sem alterar domínio
- Manutenção: Separação clara de responsabilidades

**Trade-off**: Mais código inicial, mas melhor manutenibilidade a longo prazo.

### 2. JWT para Autenticação

**Decisão**: Stateless authentication com JWT.

**Justificativa**:
- Escalabilidade: Não requer sessão no servidor
- Performance: Validação rápida de tokens
- Mobile-friendly: Funciona bem com APIs mobile

**Trade-off**: Tokens não podem ser revocados facilmente (requer blacklist ou curta expiração).

### 3. Spring Data JPA

**Decisão**: Usar Spring Data para repositories.

**Justificativa**:
- Redução de boilerplate
- Métodos de query derivados de nomes de métodos
- Integração nativa com Spring

**Trade-off**: Abstrai SQL, pode gerar queries não otimizadas.

### 4. Records para DTOs

**Decisão**: Java Records para DTOs imutáveis.

**Justificativa**:
- Imutabilidade por padrão
- Menos código (getters, equals, hashCode automáticos)
- Clarity: DTOs são claramente separados de entidades

**Trade-off**: Records não podem ser extendidos.

## 📈 Monitoramento

### Prometheus Metrics

```
GET http://localhost:8181/actuator/prometheus
```

### Health Check

```
GET http://localhost:8181/actuator/health
```

### Docker Monitoring Stack

```bash
# Prometheus + Grafana
docker compose -f infra/observalidade.yaml up -d
```

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 🔄 Melhorias Futuras

- [ ] Adicionar testes unitários para `AuthService`
- [ ] Adicionar testes de integração para controllers
- [ ] Implementar refresh tokens JWT
- [ ] Documentar API com OpenAPI/Swagger
- [ ] Adicionar rate limiting
- [ ] Implementar logout com blacklist de tokens
- [ ] Adicionar validação de senha (mínimo 8 caracteres, maiúsculas, etc.)
- [ ] Implementar email verification
- [ ] Adicionar internacionalização (i18n)
- [ ] Configurar CI/CD com GitHub Actions

## 📄 Licença

Este projeto é apenas para fins educacionais.

---

**Autor**: Projeto Demo Spring Security  
**Versão**: 0.0.1-SNAPSHOT  
**Última Atualização**: 2026
