# Spring Security – Arquitetura Hexagonal (Ports & Adapters)

Projeto de exemplo em Spring Boot que implementa autenticação e registro de usuários utilizando Spring Security, arquitetura em camadas e padrão de portas e adaptadores.

## Tecnologias

- Java 17+ (ajustar conforme seu `pom.xml`)
- Spring Boot (Web, Security, Data JPA)
- Banco de dados relacional (ajustar: PostgreSQL/MySQL/etc.)
- Flyway para migrações (`db/migration`)
- Docker e Docker Compose

## Arquitetura

A aplicação segue uma arquitetura inspirada em Clean/Hexagonal:

- **domain/**
  Contém o modelo de domínio (`UserModel`) e DTOs de entrada/saída para autenticação.

- **application/**
  - `ports/inbound`: contratos de **casos de uso** (ex.: `AuthUseCase`).
  - `ports/outbound`: contratos de **acesso a recursos externos** (ex.: `AuthRepositoryPort`).
  - `service`: implementação dos casos de uso (`AuthService`), orquestrando regras de negócio e chamadas às portas outbound.

- **infrastructure/**
  - `adapters/inbound`: controllers REST (`HelloController`, `AuthController`) que expõem a API HTTP.
  - `adapters/outbound`: implementações das portas outbound (`AuthRepositoryPortImpl`) que conversam com o repositório.
  - `config/security`: configuração do Spring Security (`AuthConfig`, `SecurityConfig`, `TokenConfig`).
  - `persistence`: entidades JPA (`UserEntity`) e repositórios concretos (`UserRepositoryConcret`).

Ponto de entrada da aplicação: `SecurityApplication.java`.

## Estrutura de Pastas

```txt
infra/
  docker-compose-application.yaml
  docker-compose.yaml

src/
  main/
    java/com/application/security/
      application/
        ports/inbound/AuthUseCase.java
        ports/outbound/AuthRepositoryPort.java
        service/AuthService.java
      domain/
        dto/request/LoginRequest.java
        dto/request/RegisterUserRequest.java
        dto/response/LoginResponse.java
        dto/response/RegisterUserResponse.java
        UserModel.java
      infrastruct/ (sugerido: infrastructure/)
        adapters/inbound/HelloController.java
        adapters/inbound/security/AuthController.java
        adapters/outbound/AuthRepositoryPortImpl.java
        config/security/AuthConfig.java
        config/security/SecurityConfig.java
        config/security/TokenConfig.java
        persistence/entity/UserEntity.java
        persistence/repository/UserRepositoryConcret.java
      SecurityApplication.java
    resources/
      application.properties
      db/migration/V1__create_table_user.sql
  test/
    java/com/application/security/SecurityApplicationTests.java
```

## Como executar

1. Configure as variáveis de ambiente e `application.properties` com as credenciais do banco.
2. Com Docker:
   - Na raiz do projeto: `docker compose -f infra/docker-compose-application.yaml up` (ajuste conforme seus arquivos).


## Endpoints principais

- `GET /hello` – endpoint de teste (implementado em `HelloController`).
- `POST /auth/login` – autenticação de usuário (implementado em `AuthController`).
- `POST /auth/register` – registro de usuário (implementado em `AuthController`).

(Ajuste os caminhos exatos conforme as anotações dos seus controllers.)

## Testes

Para rodar os testes:

```bash
./mvnw test
```

## Próximos passos / melhorias

- Adicionar mais casos de uso além de login/registro.
- Cobrir `AuthService` e controllers com testes unitários e de integração.
- Documentar endpoints com OpenAPI/Swagger.