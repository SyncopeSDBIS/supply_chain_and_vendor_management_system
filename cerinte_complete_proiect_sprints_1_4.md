<!-- Sursa: sprint1_cerinte_portal.md -->

# ADR-001: Business Domain Design - Sprint 1

## Status

Accepted

---

## Context

Sprint 1 are ca obiectiv definirea și documentarea domeniului de business al aplicației. Activitatea este împărțită în două arii principale:

1. Business Requirements & Business Workflow
2. Domain Model & Business Rules

Scopul este identificarea proceselor de business, a entităților domeniului și a regulilor care guvernează comportamentul acestora.

---

## Decision

### Business Requirements

Sistemul trebuie să documenteze cerințele de business folosind una sau mai multe dintre următoarele tehnici:

- Use Cases
- Usability Scenarios
- Business Stories
- Business Workflow Rules

Aceste artefacte trebuie să descrie interacțiunile utilizatorilor cu sistemul și procesele de business asociate.

---

### Domain Model

Modelul domeniului trebuie să conțină:

#### Business Entities

- Definirea entităților de business
- Definirea relațiilor dintre entități
- Model ORM (Object Relational Mapping)

#### Entity Aggregates

- Aggregate Root
- Component Entities
- Value Objects

Modelul trebuie să reflecte structura conceptuală a domeniului și relațiile dintre entități.

---

### Business Rules

Sistemul trebuie să definească și să documenteze regulile de business asociate domeniului.

#### Validation Rules

- Reguli de validare pentru atributele entităților
- Reguli de validare la nivel de agregat

#### Computation Rules

- Atribute calculate ale entităților
- View-uri sau proiecții calculate

#### Additional Business Rules

- Constrângeri de business
- Politici de business
- Reguli operaționale specifice domeniului

Toate regulile trebuie documentate textual.

---

## Deliverables

### Business Requirements Deliverables

Se vor realiza unul sau mai multe dintre următoarele artefacte:

- Use Case Diagram
- Sequence Diagram
- Activity Diagram

### Domain Model Deliverables

- Entity Class Diagram
- SQL Diagram (opțional)
- Business Rules Specification (descriere textuală)

---

## Consequences

Implementarea acestei abordări oferă:

- Trasabilitate între cerințele de business și modelul domeniului
- Definirea clară a entităților și agregatelor
- Centralizarea regulilor de business
- Fundament pentru proiectarea arhitecturii și implementarea aplicației

---

## Sprint 1 Checklist

### Business Requirements

- [ ] Identificare Use Cases
- [ ] Identificare Usability Scenarios
- [ ] Definire Business Stories
- [ ] Definire Business Workflow Rules

### Domain Model

- [ ] Identificare Business Entities
- [ ] Definire relații între entități
- [ ] Definire ORM Mapping
- [ ] Identificare Aggregate Root
- [ ] Identificare Component Entities
- [ ] Definire Value Objects

### Business Rules

- [ ] Definire Validation Rules
- [ ] Definire Computation Rules
- [ ] Definire Additional Business Rules

### Modeling Artifacts

- [ ] Entity Class Diagram
- [ ] Use Case Diagram / Sequence Diagram / Activity Diagram
- [ ] Business Rules Specification
- [ ] SQL Diagram (opțional)

---

<!-- Sursa: sprint2_cerinte_portal.md -->

# ADR-003: Sprint 2 – Business Model Implementation

## Status

Accepted

---

# Context

Sprint 2 are ca obiectiv implementarea modelului de business definit în Sprint 1.

Acest sprint transformă modelul conceptual și regulile de business în componente software executabile, folosind o arhitectură bazată pe Domain Model, Services și teste automate. 【1-cb48ec】

---

# Decision

Sistemul va implementa toate conceptele identificate în modelul domeniului și toate regulile de business definite anterior prin entități, agregate, servicii și componente de validare/calcul. 【1-cb48ec】

---

# Domain Model Implementation

## Domain Entities

Sistemul trebuie să implementeze toate entitățile identificate în modelul domeniului. 【1-cb48ec】

### Requirements

- implementarea entităților de domeniu;
- implementarea relațiilor dintre entități;
- implementarea atributelor specifice fiecărei entități. 【1-cb48ec】

---

## Value Objects

Sistemul trebuie să implementeze Value Objects utilizați în modelul domeniului. 【1-cb48ec】

### Requirements

- Enums;
- tipuri de date personalizate (Custom VO Data Types);
- obiecte imutabile cu identitate bazată pe valoare. 【1-cb48ec】

---

## Entity Aggregates

Sistemul trebuie să implementeze agregatele definite în modelul DDD. 【1-cb48ec】

### Aggregate Structure

#### Root Entity

- reprezintă punctul unic de acces către agregat. 【1-cb48ec】

#### Components

- Component Entities;
- Value Objects. 【1-cb48ec】

#### Aggregate Associations

- relațiile interne dintre elementele agregatului trebuie modelate explicit. 【1-cb48ec】

---

# Business Rules Implementation

## Workflow Rules

Regulile de business asociate fluxurilor operaționale trebuie implementate prin servicii dedicate. 【1-cb48ec】

### Requirements

- implementarea serviciilor de use-case;
- implementarea workflow-urilor de business;
- orchestrarea tranzacțiilor de business. 【1-cb48ec】

---

## Validation Rules

Regulile de validare trebuie implementate direct în modelul domeniului și în servicii dedicate. 【1-cb48ec】

### Requirements

#### Domain Entity Validation

Utilizarea Java Bean Validation:

- `@NotNull`
- `@NotBlank`
- `@Size`
- `@Min`
- `@Max`
- alte constrângeri specifice domeniului

prin adnotări aplicate entităților. 【1-cb48ec】

#### Domain Validation Services

Servicii dedicate pentru:

- validări complexe;
- validări cross-entity;
- validări cross-aggregate;
- validări dependente de reguli de business. 【1-cb48ec】

---

## Computation Rules

Regulile de calcul trebuie implementate prin servicii dedicate. 【1-cb48ec】

### Entity Field Computations

Servicii pentru calculul atributelor entităților:

- totaluri;
- scoruri;
- indicatori;
- stări derivate. 【1-cb48ec】

### Entity View Computations

Servicii pentru calculul proiecțiilor și vizualizărilor:

- dashboard views;
- report views;
- summary views;
- aggregate views. 【1-cb48ec】

---

## Other Business Rules

Toate regulile suplimentare identificate în Sprint 1 trebuie implementate prin componente dedicate ale domeniului. 【1-cb48ec】

Exemple:

- politici de business;
- constrângeri operaționale;
- reguli de autorizare;
- reguli de audit. 【1-cb48ec】

---

# Deliverables

## Domain Layer

### Entity Classes

Sistemul trebuie să conțină:

- clase pentru entități;
- clase pentru Value Objects;
- clase pentru agregate. 【1-cb48ec】

### Aggregate Entity Factories

Sistemul trebuie să furnizeze:

- factory classes pentru crearea agregatelor;
- inițializarea consistentă a obiectelor de domeniu. 【1-cb48ec】

---

## Service Layer

### Business Service Interfaces

Definirea contractelor pentru:

- servicii de business;
- workflow services;
- validation services;
- computation services;
- audit services. 【1-cb48ec】

### Business Service Implementations

Implementări concrete pentru toate interfețele definite. 【1-cb48ec】

---

## Workflow Services

Sistemul trebuie să implementeze servicii dedicate fluxurilor operaționale ale aplicației. 【1-cb48ec】

---

## Validation Services

Sistemul trebuie să implementeze servicii dedicate validărilor de business. 【1-cb48ec】

---

## Computation Services

Sistemul trebuie să implementeze servicii dedicate calculelor și indicatorilor de business. 【1-cb48ec】

---

## Audit Services

Sistemul trebuie să implementeze servicii pentru:

- audit operațional;
- trasabilitatea modificărilor;
- jurnalizarea acțiunilor importante. 【1-cb48ec】

---

## Application Configuration

### Spring Configuration

Sistemul trebuie să conțină:

- clase de configurare Spring;
- înregistrarea bean-urilor;
- configurarea componentelor aplicației. 【1-cb48ec】

---

# Testing Requirements

## JUnit Test Classes

Trebuie create teste automate pentru verificarea implementării. 【1-cb48ec】

### Validation Service Tests

Verifică:

- regulile de validare;
- scenariile valide;
- scenariile invalide. 【1-cb48ec】

### Computation Service Tests

Verifică:

- formulele de calcul;
- valorile derivate;
- agregările. 【1-cb48ec】

### Workflow Service Tests

Verifică:

- tranzițiile de stare;
- regulile proceselor de business;
- scenariile principale și alternative. 【1-cb48ec】

---

# Definition of Done

Sprint 2 este considerat finalizat dacă există:

- [ ] Domain Entities implementate
- [ ] Relationships implementate
- [ ] Value Objects implementate
- [ ] Aggregate Roots implementate
- [ ] Aggregate Components implementate
- [ ] Business Services Interfaces
- [ ] Business Services Implementations
- [ ] Workflow Services
- [ ] Validation Services
- [ ] Computation Services
- [ ] Audit Services
- [ ] Aggregate Factories
- [ ] Spring Configuration Classes
- [ ] JUnit Validation Tests
- [ ] JUnit Computation Tests
- [ ] JUnit Workflow Tests

---

<!-- Sursa: sprint3.md -->

# ADR-005: Sprint 3 – Persistence Model & Repository Implementation

## Status

Accepted

---

# Context

Sprint 3 are ca obiectiv implementarea stratului de persistență al aplicației și integrarea acestuia cu modelul de business implementat în Sprint 2.

Acest sprint introduce:

- Persistence Model bazat pe JPA
- Repository Pattern
- Spring Repository Services
- Persistence Layer Integration
- Audit și Computation Rules la nivel de entitate

Scopul este transformarea modelului de domeniu într-un model persistent capabil să stocheze și să recupereze date din baza de date. 【1-8eba8d】

---

# Decision

Sistemul va implementa stratul de persistență utilizând:

- JPA Entity Mapping
- Aggregate Persistence
- Repository Pattern
- Spring Repository Services
- JPA Repository Implementations
- Entity Lifecycle Listeners

【1-8eba8d】

---

# 1. Persistence Model Implementation

## JPA Entities

Toate entitățile identificate în Domain Model trebuie transformate în entități persistente JPA. 【1-8eba8d】

### Requirements

Fiecare entitate persistentă trebuie să utilizeze:

```java
@Entity
```

【1-8eba8d】

---

## Inheritance Support

Dacă modelul domeniului conține ierarhii de entități, acestea trebuie implementate folosind:

```java
@Inheritance
```

【1-8eba8d】

---

## Value Objects Persistence

Value Objects trebuie persistate folosind:

```java
@Enumerated
```

și/sau

```java
@Convert
```

în funcție de tipul datelor. 【1-8eba8d】

---

# 2. Aggregate Persistence

## Aggregate Root Mapping

Aggregate Root trebuie să definească explicit relațiile către componentele agregatului. 【1-8eba8d】

### Required Mapping

```java
@OneToMany
```

【1-8eba8d】

---

### Required Relationship Configuration

Relațiile trebuie să definească:

```java
mappedBy
fetch
cascade = CascadeType.ALL
orphanRemoval = true
```

【1-8eba8d】

---

## Component Entity Mapping

Entitățile componente trebuie persistate prin relațiile definite de Aggregate Root. 【1-8eba8d】

### Required Mapping

```java
@OneToMany
```

【1-8eba8d】

---

# 3. Repository Services

## Spring Repository Pattern

Pentru fiecare Aggregate Root trebuie definit un repository dedicat. 【1-8eba8d】

### Base Definition

Repository-urile trebuie să extindă:

```java
JpaRepository<RootEntity, T>
```

【1-8eba8d】

---

## Standard Operations

Repository-urile trebuie să suporte operații CRUD prin mecanismele Spring Data JPA. 【1-8eba8d】

---

## Custom Query Operations

Repository-urile trebuie să permită operații suplimentare precum:

```java
findByPropertyName()
```

și

```java
findByCustomFilter()
```

utilizând:

```java
@Query
```

pentru filtre personalizate. 【1-8eba8d】

---

# 4. Repository Implementation Strategies

## Strategy 1 – Inheritance-Based Repository

Repository-ul de domeniu:

```java
ProjectEntityRepository
```

trebuie să:

- extindă `EntityRepositoryBase<T>`;
- moștenească operațiile CRUD;
- utilizeze reflection/introspection;
- implementeze doar interogările specifice domeniului.

【1-8eba8d】

---

## Strategy 2 – Plain JPA Repository

Repository-ul:

```java
ProjectEntityRepositoryJPA
```

trebuie să:

- implementeze operațiile explicit;
- utilizeze EntityManager;
- implementeze toate metodele CRUD manual.

【1-8eba8d】

---

## Strategy 3 – Spring Data JPA

Se poate utiliza:

```java
JpaRepository
```

unde Spring generează automat implementarea repository-ului. 【1-8eba8d】

### Constraint

Această strategie nu reprezintă abordarea principală a proiectului deoarece configurarea automată Spring Boot este dezactivată. 【1-8eba8d】

---

# 5. Persistence Context Configuration

Primele două strategii trebuie să utilizeze:

```java
@PersistenceContext
```

și configurare prin:

```xml
persistence.xml
```

【1-8eba8d】

---

## Spring Configuration Constraint

Aplicația trebuie să evite autoconfigurarea datasource-ului:

```java
@SpringBootApplication(
    exclude = DataSourceAutoConfiguration.class
)
```

【1-8eba8d】

---

# 6. Business Layer Refactoring

## Business Services Integration

Serviciile dezvoltate în Sprint 2 trebuie actualizate pentru utilizarea repository-urilor JPA. 【1-8eba8d】

### Requirements

- înlocuirea repository-urilor temporare;
- integrarea repository-urilor persistente;
- utilizarea Aggregate Repositories în workflow-uri.

【1-8eba8d】

---

## Factory Integration

Factory-urile trebuie modificate pentru a funcționa cu modelul persistent implementat în JPA. 【1-8eba8d】

---

# 7. Computation and Audit Rules

## Entity Lifecycle Rules

Calculul automat și auditarea trebuie implementate folosind Entity Listeners. 【1-8eba8d】

### Required Mechanism

```java
@EntityListeners(...)
```

【1-8eba8d】

---

## Supported Lifecycle Events

Trebuie implementate reguli pentru:

### Before Insert

```java
@PrePersist
```

【1-8eba8d】

### Before Update

```java
@PreUpdate
```

【1-8eba8d】

### Before Delete

```java
@PreRemove
```

【1-8eba8d】

---

## Typical Use Cases

Entity Listeners pot implementa:

- audit metadata;
- timestamps;
- computed fields;
- validation hooks;
- historical tracking.

【1-8eba8d】

---

# 8. Deliverables

## Mandatory Deliverables

### JPA Entity Classes

Implementarea tuturor entităților persistente:

- Aggregate Roots
- Component Entities
- Value Objects mappings

【1-8eba8d】

---

### Repository Definitions

Implementarea repository-urilor ca:

```java
JpaRepository
```

sau folosind:

```java
EntityRepositoryBase
```

【1-8eba8d】

---

## Bonus Deliverables

Repository-uri complete bazate pe:

```java
IRepository
```

și

```java
EntityRepositoryJPA
```

cu implementare explicită bazată pe EntityManager. 【1-8eba8d】

---

### Automated Tests

Trebuie create:

- teste pentru repository-uri JPA;
- teste actualizate pentru serviciile de business;
- validarea operațiilor CRUD;
- validarea interogărilor custom.

【1-8eba8d】

---

# Definition of Done

Sprint 3 este considerat finalizat dacă există:

- [ ] Toate Entity Classes implementate cu JPA
- [ ] Mapping pentru Value Objects
- [ ] Mapping pentru Aggregate Roots
- [ ] Mapping pentru Component Entities
- [ ] Repository Definitions
- [ ] JpaRepository Interfaces
- [ ] Query Methods
- [ ] Custom Queries (@Query)
- [ ] Persistence Configuration
- [ ] EntityListeners
- [ ] @PrePersist Rules
- [ ] @PreUpdate Rules
- [ ] @PreRemove Rules
- [ ] Business Services integrate cu JPA
- [ ] Factory Components actualizate
- [ ] Repository Unit Tests
- [ ] Repository Integration Tests
- [ ] Business Service Tests actualizate

---

# Architecture Outcome

La finalul Sprintului 3 sistemul trebuie să dispună de:

- Domain Layer complet implementat;
- Persistence Layer complet funcțional;
- Aggregate Persistence;
- Repository Pattern implementat;
- Audit și Lifecycle Management;
- Servicii de business integrate cu baza de date.

【1-8eba8d】

---

<!-- Sursa: sprint3_detalii.md -->

# ADR-005: Sprint 3 – Persistence Model & Repository Implementation

## Status

Accepted

---

# Context

Sprint 3 are ca obiectiv implementarea stratului de persistență al aplicației și integrarea acestuia cu modelul de business implementat în Sprint 2.

Acest sprint introduce:

- Persistence Model bazat pe JPA
- Repository Pattern
- Spring Repository Services
- Persistence Layer Integration
- Audit și Computation Rules la nivel de entitate

Scopul este transformarea modelului de domeniu într-un model persistent capabil să stocheze și să recupereze date din baza de date. 【1-8eba8d】

---

# Decision

Sistemul va implementa stratul de persistență utilizând:

- JPA Entity Mapping
- Aggregate Persistence
- Repository Pattern
- Spring Repository Services
- JPA Repository Implementations
- Entity Lifecycle Listeners

【1-8eba8d】

---

# 1. Persistence Model Implementation

## JPA Entities

Toate entitățile identificate în Domain Model trebuie transformate în entități persistente JPA. 【1-8eba8d】

### Requirements

Fiecare entitate persistentă trebuie să utilizeze:

```java
@Entity
```

【1-8eba8d】

---

## Inheritance Support

Dacă modelul domeniului conține ierarhii de entități, acestea trebuie implementate folosind:

```java
@Inheritance
```

【1-8eba8d】

---

## Value Objects Persistence

Value Objects trebuie persistate folosind:

```java
@Enumerated
```

și/sau

```java
@Convert
```

în funcție de tipul datelor. 【1-8eba8d】

---

# 2. Aggregate Persistence

## Aggregate Root Mapping

Aggregate Root trebuie să definească explicit relațiile către componentele agregatului. 【1-8eba8d】

### Required Mapping

```java
@OneToMany
```

【1-8eba8d】

---

### Required Relationship Configuration

Relațiile trebuie să definească:

```java
mappedBy
fetch
cascade = CascadeType.ALL
orphanRemoval = true
```

【1-8eba8d】

---

## Component Entity Mapping

Entitățile componente trebuie persistate prin relațiile definite de Aggregate Root. 【1-8eba8d】

### Required Mapping

```java
@OneToMany
```

【1-8eba8d】

---

# 3. Repository Services

## Spring Repository Pattern

Pentru fiecare Aggregate Root trebuie definit un repository dedicat. 【1-8eba8d】

### Base Definition

Repository-urile trebuie să extindă:

```java
JpaRepository<RootEntity, T>
```

【1-8eba8d】

---

## Standard Operations

Repository-urile trebuie să suporte operații CRUD prin mecanismele Spring Data JPA. 【1-8eba8d】

---

## Custom Query Operations

Repository-urile trebuie să permită operații suplimentare precum:

```java
findByPropertyName()
```

și

```java
findByCustomFilter()
```

utilizând:

```java
@Query
```

pentru filtre personalizate. 【1-8eba8d】

---

# 4. Repository Implementation Strategies

## Strategy 1 – Inheritance-Based Repository

Repository-ul de domeniu:

```java
ProjectEntityRepository
```

trebuie să:

- extindă `EntityRepositoryBase<T>`;
- moștenească operațiile CRUD;
- utilizeze reflection/introspection;
- implementeze doar interogările specifice domeniului.

【1-8eba8d】

---

## Strategy 2 – Plain JPA Repository

Repository-ul:

```java
ProjectEntityRepositoryJPA
```

trebuie să:

- implementeze operațiile explicit;
- utilizeze EntityManager;
- implementeze toate metodele CRUD manual.

【1-8eba8d】

---

## Strategy 3 – Spring Data JPA

Se poate utiliza:

```java
JpaRepository
```

unde Spring generează automat implementarea repository-ului. 【1-8eba8d】

### Constraint

Această strategie nu reprezintă abordarea principală a proiectului deoarece configurarea automată Spring Boot este dezactivată. 【1-8eba8d】

---

# 5. Persistence Context Configuration

Primele două strategii trebuie să utilizeze:

```java
@PersistenceContext
```

și configurare prin:

```xml
persistence.xml
```

【1-8eba8d】

---

## Spring Configuration Constraint

Aplicația trebuie să evite autoconfigurarea datasource-ului:

```java
@SpringBootApplication(
    exclude = DataSourceAutoConfiguration.class
)
```

【1-8eba8d】

---

# 6. Business Layer Refactoring

## Business Services Integration

Serviciile dezvoltate în Sprint 2 trebuie actualizate pentru utilizarea repository-urilor JPA. 【1-8eba8d】

### Requirements

- înlocuirea repository-urilor temporare;
- integrarea repository-urilor persistente;
- utilizarea Aggregate Repositories în workflow-uri.

【1-8eba8d】

---

## Factory Integration

Factory-urile trebuie modificate pentru a funcționa cu modelul persistent implementat în JPA. 【1-8eba8d】

---

# 7. Computation and Audit Rules

## Entity Lifecycle Rules

Calculul automat și auditarea trebuie implementate folosind Entity Listeners. 【1-8eba8d】

### Required Mechanism

```java
@EntityListeners(...)
```

【1-8eba8d】

---

## Supported Lifecycle Events

Trebuie implementate reguli pentru:

### Before Insert

```java
@PrePersist
```

【1-8eba8d】

### Before Update

```java
@PreUpdate
```

【1-8eba8d】

### Before Delete

```java
@PreRemove
```

【1-8eba8d】

---

## Typical Use Cases

Entity Listeners pot implementa:

- audit metadata;
- timestamps;
- computed fields;
- validation hooks;
- historical tracking.

【1-8eba8d】

---

# 8. Deliverables

## Mandatory Deliverables

### JPA Entity Classes

Implementarea tuturor entităților persistente:

- Aggregate Roots
- Component Entities
- Value Objects mappings

【1-8eba8d】

---

### Repository Definitions

Implementarea repository-urilor ca:

```java
JpaRepository
```

sau folosind:

```java
EntityRepositoryBase
```

【1-8eba8d】

---

## Bonus Deliverables

Repository-uri complete bazate pe:

```java
IRepository
```

și

```java
EntityRepositoryJPA
```

cu implementare explicită bazată pe EntityManager. 【1-8eba8d】

---

### Automated Tests

Trebuie create:

- teste pentru repository-uri JPA;
- teste actualizate pentru serviciile de business;
- validarea operațiilor CRUD;
- validarea interogărilor custom.

【1-8eba8d】

---

# Definition of Done

Sprint 3 este considerat finalizat dacă există:

- [ ] Toate Entity Classes implementate cu JPA
- [ ] Mapping pentru Value Objects
- [ ] Mapping pentru Aggregate Roots
- [ ] Mapping pentru Component Entities
- [ ] Repository Definitions
- [ ] JpaRepository Interfaces
- [ ] Query Methods
- [ ] Custom Queries (@Query)
- [ ] Persistence Configuration
- [ ] EntityListeners
- [ ] @PrePersist Rules
- [ ] @PreUpdate Rules
- [ ] @PreRemove Rules
- [ ] Business Services integrate cu JPA
- [ ] Factory Components actualizate
- [ ] Repository Unit Tests
- [ ] Repository Integration Tests
- [ ] Business Service Tests actualizate

---

# Architecture Outcome

La finalul Sprintului 3 sistemul trebuie să dispună de:

- Domain Layer complet implementat;
- Persistence Layer complet funcțional;
- Aggregate Persistence;
- Repository Pattern implementat;
- Audit și Lifecycle Management;
- Servicii de business integrate cu baza de date.

【1-8eba8d】

---

<!-- Sursa: sprint4_cerinte.md -->

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

---

<!-- Sursa: sprint4_constrangeri.md -->

# ADR-008: REST Web Services Architecture and Implementation

## Status

Accepted

---

# Context

Sprint 4 are ca obiectiv expunerea funcționalităților sistemului prin servicii REST construite peste Business Layer și Persistence Layer implementate în sprinturile anterioare.

Arhitectura trebuie să respecte principiile:

- Service Oriented Architecture (SOA)
- REST Architectural Style
- Spring MVC
- Spring REST Services
- DTO-Based Communication
- XML / JSON Data Exchange

【1-4bca1b】

---

# Architecture Decision

Sistemul va adopta o arhitectură REST bazată pe:

- HTTP Protocol
- Resource-Oriented Design
- Spring MVC REST Controllers
- DTO Serialization Layer
- Jackson JSON Processing
- JAXB XML Mapping

【1-4bca1b】

---

# 1. HTTP Communication Requirements

## Supported HTTP Methods

Serviciile REST trebuie să utilizeze următoarele metode HTTP:

### Resource Access

```http
GET
```

Pentru citirea resurselor.

### Resource Creation

```http
POST
```

Pentru crearea resurselor.

### Resource Update

```http
PUT
```

Pentru actualizarea resurselor.

### Partial Update

```http
PATCH
```

Pentru modificări parțiale.

### Resource Removal

```http
DELETE
```

Pentru ștergerea resurselor.

### Metadata Retrieval

```http
HEAD
```

Pentru obținerea metadatelor fără corpul răspunsului.

### Communication Negotiation

```http
OPTIONS
```

Pentru negocierea parametrilor de comunicare.

【1-4bca1b】

---

## HTTP Status Codes

Serviciile trebuie să utilizeze coduri HTTP standard.

### Success

```http
200 OK
204 No Content
```

### Errors

```http
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
408 Request Timeout
```

【1-4bca1b】

---

# 2. HTTP Header Requirements

## Request Headers

Trebuie suportate:

```http
Accept
Content-Type
Content-Length
Authorization
```

【1-4bca1b】

---

## Response Headers

Trebuie suportate:

```http
Access-Control-Allow-Origin
Content-Type
Content-Length
```

【1-4bca1b】

---

## Supported MIME Types

### Request / Response

```http
application/json
application/xml
```

### Additional Formats

```http
text/plain
text/html
text/xml
application/atom+xml
```

【1-4bca1b】

---

# 3. REST Technology Stack

## Spring Framework

Sistemul trebuie să utilizeze:

### Spring Core

- Beans
- Dependency Injection

### Spring AOP

- Aspects
- Cross-cutting concerns

### Spring Data

- Spring JDBC
- Spring JPA

### Spring Web

- Spring MVC
- Spring REST

### Spring Security

- Authentication
- Authorization

【1-4bca1b】

---

## Serialization Technologies

Sistemul trebuie să suporte:

### XML Processing

- JAXB
- Spring OXM

### JSON Processing

- Jackson

【1-4bca1b】

---

# 4. REST Application Architecture

## Data Components

Sistemul trebuie să conțină:

- Entity Classes
- Entity Repositories

【1-4bca1b】

---

## Service Components

Sistemul trebuie să conțină:

- Business Services
- REST Controllers

【1-4bca1b】

---

## Configuration Components

Sistemul trebuie să conțină:

### WebInitializer

Bazat pe:

```java
AbstractAnnotationConfigDispatcherServletInitializer
```

### WebConfig

Bazat pe:

```java
WebMvcConfigurer
```

### ApplicationConfiguration

Configurarea aplicației.

【1-4bca1b】

---

## Deployment Components

Implementarea trebuie să suporte:

- Apache Tomcat
- Maven Deploy Plugins

【1-4bca1b】

---

# 5. URL Design Constraints

## URL Structure

Endpoint-urile trebuie să respecte structura:

```text
http://host:port/application/servlet/resource
```

Exemplu:

```text
http://localhost:8080/scrum/data/projects
```

【1-4bca1b】

---

## URL Sources

### Application Context

Definit în:

```xml
pom.xml
```

### Servlet Mapping

Definit în:

```java
WebInitializer
```

### Resource Mapping

Definit în:

```java
@RequestMapping
```

【1-4bca1b】

---

# 6. REST Controller Requirements

## Controller Structure

Fiecare serviciu REST trebuie implementat folosind:

```java
@RestController
@RequestMapping("/resource")
```

【1-4bca1b】

---

## HTTP Mapping

### GET

```java
@GetMapping
```

sau

```java
@RequestMapping(method = RequestMethod.GET)
```

### POST

```java
@PostMapping
```

sau

```java
@RequestMapping(method = RequestMethod.POST)
```

### PUT

```java
@PutMapping
```

sau

```java
@RequestMapping(method = RequestMethod.PUT)
```

### DELETE

```java
@DeleteMapping
```

sau

```java
@RequestMapping(method = RequestMethod.DELETE)
```

### PATCH

```java
@PatchMapping
```

【1-4bca1b】

---

## Request Body Processing

Pentru extragerea datelor din request:

```java
@RequestBody
```

【1-4bca1b】

---

## Response Body Processing

Pentru serializarea răspunsului:

```java
@ResponseBody
```

【1-4bca1b】

---

## Path Parameters

Trebuie utilizat:

```java
@PathVariable
```

pentru identificatori expuși în URL.

Exemplu:

```java
/projects/{id}
```

【1-4bca1b】

---

# 7. Content Negotiation

## Supported Formats

Serviciile trebuie să suporte:

- JSON
- XML

【1-4bca1b】

---

## Request Configuration

### JSON

```http
Content-Type: application/json
Accept: application/json
```

### XML

```http
Content-Type: application/xml
Accept: application/xml
```

【1-4bca1b】

---

## Request Mapping Configuration

Controller-ele trebuie să utilizeze:

```java
consumes
produces
```

în cadrul adnotării:

```java
@RequestMapping
```

【1-4bca1b】

---

# 8. DTO Architecture

## DTO Requirement

Entitățile nu trebuie expuse direct.

Comunicarea REST trebuie realizată prin DTO-uri.

【1-4bca1b】

---

## XML DTO Mapping

DTO-urile XML trebuie să utilizeze:

### Root Mapping

```java
@XmlRootElement
```

### Element Mapping

```java
@XmlElement
```

### Attribute Mapping

```java
@XmlAttribute
```

### Collection Mapping

```java
@XmlElements
@XmlElementWrapper
```

### Ignore Fields

```java
@XmlTransient
```

【1-4bca1b】

---

# 9. JSON DTO Architecture

## JSON Root Object

```java
@JsonRootName
```

【1-4bca1b】

---

## Attribute Mapping

```java
@JsonProperty
@JsonGetter
```

【1-4bca1b】

---

## Circular Reference Handling

Pentru relații bidirecționale trebuie utilizate:

```java
@JsonManagedReference
@JsonBackReference
```

【1-4bca1b】

---

## Ignored Fields

```java
@JsonIgnore
@JsonIgnoreProperties
```

【1-4bca1b】

---

# 10. Aggregate Serialization Rules

## Circular Dependency Constraint

Referințele circulare între:

```text
Aggregate Root
 ↕
Child Entity
```

nu trebuie serializate direct.

Trebuie utilizat:

- DTO Mapping
- Json Managed/Back Reference

pentru evitarea ciclurilor infinite.

【1-4bca1b】

---

# 11. XML/JSON Converter Configuration

## Maven Dependencies

Trebuie incluse:

```xml
jaxb-api
jackson-databind
jackson-dataformat-xml
jackson-module-jaxb-annotations
```

【1-4bca1b】

---

## Spring Configuration

WebConfig trebuie să înregistreze:

```java
MappingJackson2XmlHttpMessageConverter
MappingJackson2HttpMessageConverter
```

【1-4bca1b】

---

## JAXB Integration

Trebuie utilizat:

```java
JaxbAnnotationModule
```

pentru reutilizarea adnotărilor JAXB.

【1-4bca1b】

---

# 12. REST API Contract

## Collection Resource

```http
GET    /resource
POST   /resource
```

---

## Single Resource

```http
GET    /resource/{id}
PUT    /resource/{id}
DELETE /resource/{id}
```

【1-4bca1b】

---

# 13. REST Repository Exposure

## REST Ready Repository

Repository-urile pot fi expuse automat folosind:

```java
@RepositoryRestResource
```

【1-4bca1b】

---

# 14. Testing Requirements

## Deployment Testing

Aplicația trebuie să ruleze pe:

```text
Apache Tomcat 9+
```

【1-4bca1b】

---

## API Testing Tools

Trebuie validate endpoint-urile folosind:

- Browser
- Postman
- Apache HTTP Client
- JUnit Tests

【1-4bca1b】

---

## HTTP Client Testing

Testele trebuie să utilizeze:

```java
HttpGet
HttpPost
HttpPut
HttpDelete
```

împreună cu:

```java
HttpClientBuilder
HttpResponse
ObjectMapper
```

【1-4bca1b】

---

# Deliverables

## Mandatory

- [ ] Spring MVC REST Configuration
- [ ] WebInitializer
- [ ] WebConfig
- [ ] REST Controllers
- [ ] CRUD Endpoints
- [ ] DTO Classes
- [ ] XML Serialization
- [ ] JSON Serialization
- [ ] Content Negotiation
- [ ] JAXB Configuration
- [ ] Jackson Configuration
- [ ] Repository Integration
- [ ] REST Tests
- [ ] Postman Test Collection

---

# Definition of Done

Sprint 4 este finalizat dacă:

- [ ] toate Aggregate Root-urile sunt expuse prin REST;
- [ ] endpoint-urile CRUD funcționează;
- [ ] XML și JSON sunt suportate;
- [ ] DTO-urile sunt serializate corect;
- [ ] nu există cicluri de serializare;
- [ ] Content Negotiation funcționează;
- [ ] serviciile sunt testate prin HTTP Client și Postman;
- [ ] aplicația este deployată cu succes pe Tomcat.

---

<!-- Sursa: sprint4_soa.md -->

# ADR-009: Service-Oriented Architecture (SOA) and REST Service Design

## Status

Accepted

---

# Context

Sistemul trebuie să adopte o arhitectură orientată pe servicii (SOA) pentru a asigura:

- modularitate;
- reutilizare;
- scalabilitate;
- interoperabilitate;
- extensibilitate.

Arhitectura serviciilor reprezintă stratul de integrare dintre Business Layer, Persistence Layer și API Layer și constituie baza pentru implementarea REST Web Services. 【1-23bd59】

---

# Architecture Decision

Aplicația va utiliza principii Service-Oriented Architecture (SOA) și REST pentru organizarea logicii aplicației și expunerea funcționalităților prin servicii distribuite. 【1-23bd59】

---

# 1. Service-Oriented Computing Model

## Supported Application Styles

Soluția trebuie să permită evoluția către:

- Monolithic Applications;
- Component-Based Applications;
- Service-Based Applications.

【1-23bd59】

---

## SOA Concepts

Arhitectura trebuie să utilizeze conceptele:

- Service-Oriented Architecture (SOA);
- Service-Oriented Design Paradigm;
- Service-Oriented Solution Logic;
- Services;
- Service Composition;
- Service Inventory.

【1-23bd59】

---

# 2. SOA Design Principles

## Principle 1: Service Contracts

Fiecare serviciu trebuie să expună un contract standardizat. 【1-23bd59】

### Contract Responsibilities

Contractul trebuie să definească:

- termenii de utilizare;
- constrângerile tehnice;
- cerințele funcționale;
- descrierile tehnice și non-tehnice ale serviciului.

【1-23bd59】

### Requirements

- toate serviciile trebuie să urmeze aceleași standarde de contract;
- contractele trebuie să fie ușor de înțeles;
- contractele trebuie să permită interoperabilitatea.

【1-23bd59】

---

## Principle 2: Service Loose Coupling

Serviciile trebuie să fie slab cuplate. 【1-23bd59】

### Requirements

- serviciile trebuie să minimizeze dependențele;
- modificările interne nu trebuie să afecteze consumatorii;
- dependențele dintre servicii trebuie menținute la minimum.

【1-23bd59】

---

## Principle 3: Service Abstraction

Serviciile trebuie să ascundă implementarea internă. 【1-23bd59】

### Requirements

Contractele trebuie să publice doar:

- informațiile esențiale;
- capabilitățile expuse.

Detaliile de implementare trebuie ascunse.

【1-23bd59】

---

## Principle 4: Service Reusability

Serviciile trebuie proiectate ca resurse reutilizabile. 【1-23bd59】

### Requirements

- logica de business trebuie să fie agnostică;
- serviciile trebuie să poată fi reutilizate de mai mulți consumatori;
- serviciile trebuie proiectate pentru reutilizare viitoare.

【1-23bd59】

---

## Principle 5: Service Autonomy

Serviciile trebuie să fie autonome. 【1-23bd59】

### Requirements

- fiecare serviciu trebuie să-și controleze execuția;
- serviciile trebuie să fie independente operațional;
- serviciile trebuie să gestioneze propriile resurse.

【1-23bd59】

---

## Principle 6: Service Statelessness

Serviciile trebuie să minimizeze starea internă. 【1-23bd59】

### Requirements

- informațiile de sesiune nu trebuie păstrate în service;
- starea temporară trebuie externalizată;
- serviciile trebuie să poată fi executate independent.

### Goals

- scalabilitate;
- reutilizare;
- distribuție simplificată.

【1-23bd59】

---

## Principle 7: Service Discoverability

Serviciile trebuie să fie descoperibile. 【1-23bd59】

### Requirements

- metadate descriptive;
- catalog de servicii;
- documentație centralizată;
- convenții uniforme de denumire.

【1-23bd59】

---

## Principle 8: Service Composability

Serviciile trebuie să poată participa în compoziții complexe. 【1-23bd59】

### Requirements

- serviciile trebuie să poată fi orchestrate;
- serviciile trebuie să fie reutilizabile în workflow-uri multiple;
- compozițiile nu trebuie să depindă de implementarea internă.

【1-23bd59】

---

# 3. Service Classification Model

## Orchestration Services

Responsabile pentru:

- procese complexe;
- workflow-uri de business;
- coordonarea altor servicii.

Exemple:

- ProcurementWorkflowService;
- SupplierOnboardingWorkflowService;
- InvoiceApprovalWorkflowService.

【1-23bd59】

---

## Task Services

Responsabile pentru:

- logică de business;
- calcule;
- validări;
- procesări specifice domeniului.

Exemple:

- LeadTimeCalculationService;
- ScorecardCalculationService;
- ThreeWayMatchService.

【1-23bd59】

---

## Entity Services

Responsabile pentru:

- managementul entităților;
- Repository Pattern;
- Factory Pattern.

Exemple:

- SupplierRepository;
- PurchaseOrderRepository;
- InvoiceRepository;
- SupplierFactory.

【1-23bd59】

---

## Utility Services

Responsabile pentru funcționalități transversale.

Exemple:

- AuditService;
- NotificationService;
- LoggingService.

【1-23bd59】

---

# 4. Service Collaboration Architecture

## Composite Service Pattern

Workflow-urile complexe trebuie să fie implementate prin servicii compuse. 【1-23bd59】

### Example Structure

```text
BusinessWorkflowService
 ├── DomainService
 ├── RepositoryService
 ├── FactoryService
 └── AuditService
```

【1-23bd59】

---

## Collaboration Strategies

### Local Facade Collaboration

Serviciile colaborează direct în aceeași aplicație.

【1-23bd59】

### Remote HTTP Facade Collaboration

Serviciile colaborează prin HTTP.

【1-23bd59】

---

# 5. Service Styles

## RPC API

Model orientat pe operații. 【1-23bd59】

### Characteristics

- endpointurile reprezintă metode;
- focus pe acțiuni;
- apelarea proceselor remote.

Exemple:

```http
POST /approveOrder
POST /calculateScorecard
POST /executePayment
```

【1-23bd59】

---

## Resource API

Model orientat pe resurse. 【1-23bd59】

### Characteristics

- focus pe entități;
- CRUD;
- URI-based access.

Exemple:

```http
GET /suppliers
GET /orders
GET /payments
```

【1-23bd59】

---

## Messaging API

Model asincron bazat pe mesaje. 【1-23bd59】

### Examples

- JMS;
- SMTP;
- Event Messaging.

【1-23bd59】

---

# 6. Service Communication Styles

## HTTP / REST

Servicii bazate pe:

- URI;
- JSON;
- XML.

【1-23bd59】

---

## SOAP

Servicii bazate pe:

- SOAP Envelope;
- XML Messages;
- Formal Contracts.

【1-23bd59】

---

## Messaging

Servicii bazate pe:

- JMS;
- Event Driven Communication.

【1-23bd59】

---

# 7. REST Architectural Requirements

## Resources and URIs

Toate resursele trebuie să aibă identificatori unici URI. 【1-23bd59】

### Examples

```text
/api/suppliers
/api/suppliers/{id}

/api/orders
/api/orders/{id}
```

---

## Resource Representation

Resursele trebuie să poată fi reprezentate în mai multe formate:

- JSON;
- XML;
- HTML;
- CSV;
- XLS;
- TXT.

【1-23bd59】

---

## Addressability

Orice informație relevantă trebuie să fie accesibilă prin URI. 【1-23bd59】

### Requirements

- acces direct prin URL;
- acoperire completă a operațiilor CRUD.

【1-23bd59】

---

## Connectedness

Resursele trebuie să fie conectate între ele prin URI-uri. 【1-23bd59】

### Requirements

- hiperlink-uri către resurse conexe;
- navigare între agregate;
- graful relațiilor vizibil public.

【1-23bd59】

---

## Uniform Interface

Toate resursele trebuie gestionate uniform prin HTTP. 【1-23bd59】

### Methods

```http
GET
POST
PUT
PATCH
DELETE
```

---

## Statelessness

Serverul nu trebuie să gestioneze sesiuni client-side. 【1-23bd59】

### Requirements

- sesiunea aparține clientului;
- fiecare request trebuie să fie complet;
- serviciile trebuie să fie independente de context.

【1-23bd59】

---

# 8. DTO Architecture

## Data Transfer Objects

Toate datele expuse prin API trebuie transportate prin DTO-uri. 【1-23bd59】

---

## DTO Lifecycle

Procesul trebuie să includă:

1. Data Cleaning
2. Data Mapping
3. Data Formatting
4. Data Serialization
5. Data Interchange

【1-23bd59】

---

## Supported DTO Formats

### Structured Documents

- XML DTO
- JSON DTO
- CSV DTO
- XLS DTO

【1-23bd59】

---

# 9. DTO Design Strategy

## Structural Strategy

DTO-urile pot fi generate:

### Strategy A

Direct din entități.

### Strategy B

Utilizând modele dedicate:

- EntityView
- DTO Model

【1-23bd59】

---

## Factory Strategy

DTO-urile trebuie create prin:

### Method Factory

Metode definite în entități.

### Class Factory

Factory dedicată DTO-urilor.

【1-23bd59】

---

## Document Builder Strategy

Conversia DTO → XML/JSON trebuie realizată prin:

- JAXB;
- Jackson;
- alte biblioteci de serializare.

【1-23bd59】

---

# Deliverables

## Mandatory

- [ ] Catalog de servicii
- [ ] Clasificarea serviciilor pe tipuri
- [ ] Workflow Services
- [ ] Domain Services
- [ ] Repository Services
- [ ] Utility Services
- [ ] Contracte de servicii
- [ ] DTO Model
- [ ] DTO Factories
- [ ] Resource-based REST API
- [ ] URI Design
- [ ] JSON/XML Support
- [ ] Service Composition Model

---

# Definition of Done

Sprintul SOA este considerat finalizat dacă:

- [ ] serviciile respectă toate cele 8 principii SOA;
- [ ] există clasificare clară între Workflow, Task, Entity și Utility Services;
- [ ] toate funcționalitățile sunt accesibile prin servicii documentate;
- [ ] există DTO-uri pentru schimbul de date;
- [ ] resursele REST sunt identificate prin URI-uri;
- [ ] serviciile sunt stateless;
- [ ] există suport JSON și XML;
- [ ] serviciile pot fi compuse în workflow-uri complexe.

---