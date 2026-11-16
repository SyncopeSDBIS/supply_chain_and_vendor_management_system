# ADR-007: Sprint 4 – REST API & Service Exposure Architecture

## Status

Accepted

---

# Context

Sprint 4 are ca obiectiv expunerea funcționalităților aplicației prin servicii REST și implementarea stratului API peste componentele de business și persistență realizate în sprinturile anterioare. 【1-965078】

Acest sprint introduce:

- DTO Model;
- JSON Serialization / Deserialization;
- REST Controllers;
- REST Endpoints;
- Spring Boot REST Infrastructure;
- REST Integration Testing.

【1-965078】

---

# Architecture Decision

Sistemul va expune funcționalitățile de business prin servicii REST implementate utilizând:

- Spring Boot;
- Spring MVC;
- REST Controllers;
- DTO Objects;
- Jackson JSON Serialization.

【1-965078】

---

# 1. DTO Model Requirements

## DTO Classes

Pentru transferul datelor între client și server trebuie definite DTO-uri dedicate. 【1-965078】

### Serialization Requirements

DTO-urile trebuie să suporte serializare/deserializare JSON utilizând:

```java
@JsonRootName
@JsonProperty
@JsonIgnoreProperties
```

【1-965078】

---

## DTO Aggregate Mapping

Agregatele trebuie serializate fără a produce cicluri infinite în relațiile bidirecționale. 【1-965078】

### Aggregate Root Relationships

Pentru relațiile părinte → copii:

```java
@JsonManagedReference
```

【1-965078】

---

### Component Entity Relationships

Pentru relațiile copil → părinte:

```java
@JsonBackReference
```

【1-965078】

---

# 2. REST Controller Architecture

## REST Resource Services

Sistemul trebuie să implementeze controlere REST pentru expunerea operațiilor Repository. 【1-965078】

### Required Annotations

Fiecare controller REST trebuie să utilizeze:

```java
@RestController
@RequestMapping
```

【1-965078】

---

## Repository Integration

Repository-urile trebuie injectate în controller-ele REST folosind Dependency Injection.

```java
@Autowired
```

【1-965078】

---

## REST Operations

Pentru fiecare Aggregate Root trebuie expuse operații REST pentru:

- creare resursă;
- obținere resursă;
- actualizare resursă;
- ștergere resursă;
- căutare resurse.

【1-965078】

---

# 3. REST-RPC Business Services (Optional)

## Workflow Service Exposure

Sistemul poate expune și operațiile de business care reprezintă workflow-uri sau use-case-uri. 【1-965078】

### Examples

- approveSupplier();
- suspendSupplier();
- receiveGoods();
- performInspection();
- registerInvoice();
- executePayment();

(aceste operații sunt specifice domeniului aplicației și nu CRUD simplu)

---

## Service Integration

Business Workflow Services trebuie injectate în controller-ele REST.

```java
@Autowired
```

【1-965078】

---

# 4. Spring Data REST Integration (Optional)

## Repository Exposure

Repository-urile pot fi expuse automat prin:

```java
@RepositoryRestResource
```

【1-965078】

---

## Purpose

Permite generarea automată a endpoint-urilor REST pentru operațiile CRUD fără implementarea explicită a controller-elor.

【1-965078】

---

# 5. Security Architecture (Optional)

## Spring Security Integration

Sistemul poate implementa mecanisme de autentificare și autorizare. 【1-965078】

---

## Security Configuration

Trebuie definită o clasă:

```java
@EnableWebSecurity
```

【1-965078】

---

## Authentication Model

Se recomandă implementarea:

### UserDetails

```java
UserDetails
```

pentru reprezentarea utilizatorului autentificat. 【1-965078】

---

### UserDetailsService

```java
UserDetailsService
```

pentru încărcarea utilizatorilor și autentificare. 【1-965078】

---

# 6. Spring Boot Application Requirements

## Main Application Definition

Proiectul trebuie să conțină o clasă principală:

```java
@SpringBootApplication
```

【1-965078】

---

## Configuration Management

Configurațiile aplicației trebuie definite în:

```text
src/main/resources/application.properties
```

【1-965078】

---

## Configuration Areas

Fișierul trebuie să conțină configurații pentru:

- datasource;
- server port;
- JPA;
- logging;
- REST infrastructure;
- security (dacă este utilizată).

---

# 7. REST Endpoint Standards

## Resource Naming

Endpoint-urile trebuie să fie orientate pe resurse.

### Example

```http
GET    /api/orders
GET    /api/orders/{id}
POST   /api/orders
PUT    /api/orders/{id}
DELETE /api/orders/{id}
```

---

## HTTP Methods

Trebuie utilizate convențiile REST:

| Operation | HTTP Method |
|------------|------------|
| Create | POST |
| Read | GET |
| Update | PUT |
| Delete | DELETE |

---

## Data Format

Toate comunicațiile trebuie să utilizeze:

```json
application/json
```

---

# 8. Integration with Previous Sprints

## Sprint 1

REST API trebuie să expună agregatele și regulile identificate în modelul domeniului.

---

## Sprint 2

REST API trebuie să utilizeze:

- Business Services;
- Workflow Services;
- Validation Services;
- Computation Services.

---

## Sprint 3

REST API trebuie să utilizeze:

- JPA Repositories;
- Persistence Layer;
- Transaction Management.

---

# 9. REST Testing Requirements

## JUnit REST Tests

Trebuie implementate teste automate pentru endpoint-urile REST. 【1-965078】

---

## REST Client Library

Testele trebuie să utilizeze:

```java
RestTemplate
```

ca HTTP Client. 【1-965078】

---

## Test Scenarios

### Resource Retrieval

- GET by ID
- GET collection

### Resource Creation

- POST valid request
- POST invalid request

### Resource Update

- PUT existing resource
- PUT invalid resource

### Resource Deletion

- DELETE existing resource

### Error Handling

- 400 Bad Request
- 404 Not Found
- 409 Conflict
- 500 Internal Server Error

---

# Deliverables

## Mandatory Deliverables

### Spring Boot Project

- [ ] Spring Boot Application
- [ ] Main Class Annotated with `@SpringBootApplication`

【1-965078】

---

### DTO Model

- [ ] DTO Classes
- [ ] JSON Serialization Mapping
- [ ] Aggregate Serialization Support

【1-965078】

---

### REST Controllers

- [ ] `@RestController`
- [ ] `@RequestMapping`
- [ ] Repository Integration

【1-965078】

---

### Configuration

- [ ] application.properties

【1-965078】

---

### Testing

- [ ] JUnit REST Tests
- [ ] RestTemplate Integration Tests

【1-965078】

---

# Definition of Done

Sprint 4 este considerat finalizat dacă:

- [ ] Aplicația pornește ca Spring Boot Application
- [ ] Există DTO-uri pentru resursele expuse
- [ ] Serializarea JSON funcționează corect
- [ ] Nu există cicluri de serializare în agregate
- [ ] Există RestControllers pentru toate agregatele principale
- [ ] Repository-urile sunt integrate în controller-e
- [ ] Endpoints REST răspund corect
- [ ] application.properties este configurat
- [ ] Există teste REST automate
- [ ] RestTemplate validează endpoint-urile

---

# Architecture Outcome

La finalul Sprintului 4 sistemul trebuie să furnizeze:

- REST API complet funcțional;
- JSON-based communication model;
- DTO Layer;
- Endpoint-uri pentru accesarea funcționalităților aplicației;
- Integrare completă cu Business Layer și Persistence Layer;
- Testare automată a serviciilor REST.

【1-965078】