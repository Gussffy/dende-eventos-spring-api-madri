# 🌴 Dendê Eventos API

API REST para gerenciamento de usuários, organizadores, eventos e ingressos da plataforma **Dendê Eventos**, desenvolvida com **Spring Boot 4**, **Spring Data JPA**, **Flyway**, **Docker** e **MySQL**.

---

## 📌 Sobre a Dendê Softhouse

A **Dendê Softhouse** é uma empresa baiana de desenvolvimento de software, situada na **Avenida Tancredo Neves, nº 1186, Edifício Catuca, bairro Caminho das Árvores, Salvador – BA, CEP 41820-020**.

Este projeto faz parte do programa de trainee **Dendezeiros** e tem caráter educacional, com foco em boas práticas de desenvolvimento backend com Spring Boot.

---

## 👥 Equipe

| Nome |
|------|
| Gustavo Santos França |
| Guilherme Rodrigues França da Rocha |
| Filipe Oliveira Santos |
| Jose Raimundo De Melo Filho |
| Andrew Oliveira Neves Da Silva |
 
---

## 🛠️ Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | 4.0.6 |
| Flyway MySQL | 11.14.1 |
| SpringDoc OpenAPI | 3.0.2 |
| MySQL Connector/J | 9.7.0 |
| Lombok | 1.18.46 |
| Docker / Docker Compose | qualquer versão recente |
 
---

## 🚀 Como rodar o projeto

### Pré-requisitos

- Java 17+
- Maven
- Docker e Docker Compose
---

### Opção 1 — Tudo pelo Docker (API + banco)

```bash
docker compose up --build
```

O Compose irá:
1. Subir o MySQL 8 com o banco `dende_eventos` criado
2. Aguardar o healthcheck do banco
3. Buildar a imagem da API via Maven multi-stage
4. Aplicar as migrations Flyway automaticamente
5. Expor a API em `http://localhost:8080`
   Para parar:
```bash
docker compose down        # mantém os dados
docker compose down -v     # apaga os dados
```
 
---

### Opção 2 — Banco pelo Docker, API pela IDE

Sobe apenas o MySQL:
```bash
docker compose up db
```

O `application.properties` já está configurado com `localhost`, então basta rodar a aplicação normalmente pela IDE.
 
---

## 📄 Documentação (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

Spec OpenAPI em JSON:
```
http://localhost:8080/v3/api-docs
```
 
---

## 🔗 Endpoints

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/usuarios` | Cadastrar usuário comum |
| PUT | `/usuarios/{usuarioId}` | Alterar dados do usuário |
| GET | `/usuarios/{usuarioId}` | Visualizar perfil do usuário |
| PATCH | `/usuarios/{usuarioId}/{status}` | Ativar (`true`) ou desativar (`false`) |

### Organizadores

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/organizadores` | Cadastrar organizador |
| PUT | `/organizadores/{organizadorId}` | Alterar dados do organizador |
| GET | `/organizadores/{organizadorId}` | Visualizar perfil do organizador |
| PATCH | `/organizadores/{organizadorId}/{status}` | Ativar (`true`) ou desativar (`false`) |

### Eventos

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/organizadores/{organizadorId}/eventos` | Cadastrar evento |
| PUT | `/organizadores/{organizadorId}/eventos/{eventoId}` | Alterar evento |
| PATCH | `/organizadores/{organizadorId}/eventos/{eventoId}/{status}` | Ativar (`true`) ou desativar (`false`) |
| GET | `/organizadores/{organizadorId}/eventos` | Listar eventos do organizador |
| GET | `/eventos` | Feed de eventos ativos |

### Ingressos

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/organizadores/{organizadorId}/eventos/{eventoId}/ingressos` | Comprar ingresso |
| POST | `/usuarios/{usuarioId}/ingressos/{ingressoId}` | Cancelar ingresso |
| GET | `/usuarios/{usuarioId}/ingressos` | Listar ingressos do usuário |
 
---

## 📦 Formato de resposta padrão

Sucesso:
```json
{
  "mensagem": "Usuário cadastrado com sucesso",
  "statusCode": 201,
  "dados": { },
  "erro": null,
  "timestamp": 1234567890123
}
```

Erro:
```json
{
  "mensagem": "Email já está em uso",
  "statusCode": 409,
  "dados": null,
  "erro": "Conflict",
  "timestamp": 1234567890123
}
```
 
---

## 📋 Exemplos de requisição

### Cadastrar usuário comum

```json
POST /usuarios
 
{
  "nome": "Lucas Almeida Silva",
  "dataNascimento": "2000-02-11",
  "sexo": "M",
  "email": "lucas@mail.com",
  "senha": "senhaCorreta"
}
```

### Cadastrar organizador

```json
POST /organizadores
 
{
  "nome": "João Marcelo Fontes",
  "dataNascimento": "1989-09-14",
  "sexo": "M",
  "email": "joao@mail.com",
  "senha": "senhaCorreta",
  "cnpj": "28.652.921/0001-58",
  "razaoSocial": "EMPRESA LTDA",
  "nomeFantasia": "Empresa das empresas"
}
```

### Cadastrar evento

```json
POST /organizadores/{organizadorId}/eventos
 
{
  "nome": "Meu Evento de Teste",
  "descricao": "Descrição do evento",
  "tipoEvento": "CULTURAL_ENTRETENIMENTO",
  "modalidade": "PRESENCIAL",
  "capacidadeMaxima": 200,
  "dataInicio": "2026-08-29T18:50:00",
  "dataFinal": "2026-08-30T19:30:00",
  "local": "UNEX",
  "precoIngresso": 12.50,
  "estornaCancelamento": true,
  "taxaEstorno": 0.0
}
```

### Comprar ingresso

```json
POST /organizadores/{organizadorId}/eventos/{eventoId}/ingressos
 
{
  "usuarioEmail": "lucas@mail.com",
  "totalPago": 12.50
}
```

### Ativar ou desativar usuário

```json
PATCH /usuarios/{usuarioId}/true   (ativar)
PATCH /usuarios/{usuarioId}/false  (desativar)
 
{
  "senha": "senhaCorreta"
}
```
 
---

## 🗂️ Estrutura do projeto

```
src/
├── main/
│   ├── java/br/com/softhouse/dende/
│   │   ├── controllers/       # @RestController — recebe as requisições HTTP
│   │   ├── services/          # @Service — regras de negócio
│   │   ├── repositories/      # @Repository — Spring Data JPA
│   │   ├── model/
│   │   │   ├── enums/         # Sexo, TipoEvento, ModalidadeEvento, StatusIngresso
│   │   │   └── builders/      # EventoBuilder
│   │   ├── mappers/           # Conversão Entity ↔ DTO
│   │   ├── dto/
│   │   │   ├── request/       # DTOs de entrada
│   │   │   └── response/      # DTOs de saída
│   │   ├── exceptions/        # Hierarquia de exceções + GlobalExceptionHandler
│   │   └── config/            # SwaggerConfig
│   └── resources/
│       ├── application.properties
│       └── db/migration/
│           └── V1__create_schema.sql   # Migration Flyway
```
 
---

## 🗃️ Enums disponíveis

**Sexo**
```
M, F, O
```

**TipoEvento**
```
SOCIAL, CORPORATIVO, ACADEMICO, CULTURAL_ENTRETENIMENTO, RELIGIOSO,
ESPORTIVO, FEIRA, CONGRESSO, OFICINA, CURSO, TREINAMENTO, AULA,
SEMINARIO, PALESTRA, SHOW, FESTIVAL, EXPOSICAO, RETIRO, CULTO,
CELEBRACAO, CAMPEONATO, CORRIDA
```

**ModalidadeEvento**
```
PRESENCIAL, REMOTO, HIBRIDO
```

**StatusIngresso**
```
ATIVO, PENDENTE, CANCELADO, REEMBOLSADO, UTILIZADO
```
---