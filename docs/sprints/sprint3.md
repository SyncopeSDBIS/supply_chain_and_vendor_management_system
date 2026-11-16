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