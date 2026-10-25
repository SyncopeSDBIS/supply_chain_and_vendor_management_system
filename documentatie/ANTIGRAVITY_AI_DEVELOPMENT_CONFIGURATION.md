# ANTIGRAVITY_AI_DEVELOPMENT_CONFIGURATION.md

# Scop

Acest document definește modul în care trebuie configurat și utilizat Antigravity astfel încât codul generat să fie cât mai apropiat de nevoile proiectului și să respecte în permanență:

- Clean Architecture
- DDD
- Modular Monolith
- Microservice Readiness
- OWASP
- NIS2
- DevSecOps
- ArchUnit
- CI/CD
- Testing First
- OOAD Traceability

Obiectivul este reducerea situațiilor în care AI generează:

- cod generic;
- cod inconsistent;
- cod care încalcă arhitectura;
- cod greu de întreținut;
- cod incompatibil cu standardele proiectului.

---

# 1. Permanent Project Context

Acest context trebuie injectat în Antigravity ca memorie permanentă de proiect.

---

## Project Type

```text
Enterprise Java Application
```

---

## Tech Stack

```text
Java 21

Spring Boot

Spring Modulith

PostgreSQL

RabbitMQ

Stripe

Docker

GitHub Actions

JUnit 5

Mockito

ArchUnit

Testcontainers

OpenAPI

Flyway
```

---

## Architectural Style

```text
Modular Monolith
```

cu pregătire pentru:

```text
Microservices
```

---

## Architecture

```text
Clean Architecture

DDD

Hexagonal Principles
```

---

## Quality Requirements

```text
Maintainability

Scalability

Security

Observability

Testability

Extensibility
```

---

# 2. Global System Prompt

Acesta trebuie utilizat pentru toate conversațiile.

```text
You are a Senior Enterprise Software Architect,
Senior Java Developer,
DDD Expert,
Spring Architect,
DevSecOps Engineer,
Security Engineer,
and Technical Lead.

Always prioritize:

1. Clean Architecture
2. Domain Driven Design
3. SOLID
4. Modular Monolith
5. Future Microservices Migration
6. Security by Design
7. Testability
8. Maintainability
9. Architectural Consistency

Do not generate quick solutions.

Generate enterprise-grade solutions.

Explain architectural implications before implementation.

Never bypass architectural boundaries.

All code must be production-ready.

All examples must be compatible with Java 21 and Spring Boot.

Whenever creating code:

- identify the architectural layer;
- identify the module owner;
- identify dependencies;
- identify security implications;
- identify testing strategy.
```

---

# 3. Repository Awareness

Antigravity trebuie să considere permanent structura proiectului:

```text
customer
order
payment
notification
shared
security
```

și să respecte:

```text
api
application
domain
infrastructure
presentation
```

---

# 4. Architectural Constraints

Aceste reguli trebuie considerate absolute.

---

## Rule 1

```text
Domain never depends on Infrastructure.
```

---

## Rule 2

```text
Domain never depends on Spring.
```

---

## Rule 3

```text
Domain never depends on JPA.
```

---

## Rule 4

```text
Domain never depends on Messaging.
```

---

## Rule 5

```text
Domain never depends on Stripe.
```

---

## Rule 6

```text
Inter-module communication uses APIs and Events only.
```

---

## Rule 7

```text
No direct access to repositories outside owner module.
```

---

## Rule 8

```text
No cyclic dependencies.
```

---

# 5. Code Generation Rules

---

## Always Generate

```text
JavaDoc

Meaningful names

Strong typing

Immutable objects where possible

Validation

Logging
```

---

## Never Generate

```text
Magic numbers

God classes

Utility classes full of business logic

Static business logic

Field injection

Hardcoded secrets
```

---

# 6. Domain Rules

Antigravity trebuie să genereze mai întâi modelul domeniului.

Ordinea:

```text
Entity
    ↓

Value Object
    ↓

Aggregate
    ↓

Application Service
    ↓

Infrastructure
```

NICIODATĂ invers.

---

# 7. DDD Generation Rules

Pentru orice feature:

Mai întâi generează:

```text
Entities
Value Objects
Events
Repository Interfaces
```

Doar după aceea:

```text
Controllers
Persistence
Messaging
```

---

# 8. Spring Rules

---

## Dependency Injection

Doar:

```java
@RequiredArgsConstructor
```

niciodată:

```java
@Autowired
```

pe câmpuri.

---

## Transactions

Explicite.

---

## Configuration

Prin:

```java
@ConfigurationProperties
```

nu prin valori hardcodate.

---

# 9. API Generation Rules

Pentru fiecare endpoint AI-ul trebuie să genereze:

```text
Request DTO

Response DTO

Mapper

Controller

Service

Tests
```

---

## Interzis

```text
Controller -> Repository
```

---

# 10. Database Rules

Preferință:

```text
PostgreSQL
```

---

## Schema Changes

Prin:

```text
Flyway Migration
```

nu manual.

---

## IDs

Preferabil:

```text
UUID
```

pentru resurse expuse extern.

---

# 11. Messaging Rules

Pentru orice proces asincron:

Preferat:

```text
Events
```

în loc de:

```text
Direct Service Calls
```

---

## Event Naming

Exemple:

```text
OrderCreatedEvent

OrderCancelledEvent

PaymentCompletedEvent
```

---

# 12. Payment Rules

Integrarea Stripe trebuie întotdeauna să folosească:

```text
Adapter Pattern
```

---

## Required Abstractions

```java
PaymentProvider
```

și nu utilizarea directă Stripe în business logic.

---

# 13. Security Rules

Toate exemplele trebuie să presupună:

```text
OWASP

NIS2

Zero Trust
```

---

## Always Validate

```text
Request Body

Headers

Path Variables

Query Params
```

---

## Never Trust User Input

Intrările sunt considerate ostile.

---

# 14. Logging Rules

Toate serviciile trebuie să conțină:

```text
INFO

WARN

ERROR
```

unde este relevant.

---

## Structured Logging

Preferat.

---

## Correlation IDs

Recomandat.

---

# 15. Testing Rules

Pentru orice cod generat.

AI-ul trebuie să genereze:

```text
Production Code
```

și

```text
Tests
```

în aceeași cerere.

---

# 16. Unit Testing Rules

Obligatoriu:

```text
JUnit 5

Mockito
```

---

## Coverage

Acoperire:

```text
Happy Path

Error Path

Edge Cases
```

---

# 17. Integration Testing Rules

Pentru:

```text
Database

RabbitMQ

Stripe
```

folosește:

```text
Testcontainers
```

---

# 18. ArchUnit Awareness

Orice cod propus trebuie să respecte:

```text
Layer Rules

Module Rules

Domain Isolation Rules
```

---

## Before Generating Code

AI-ul trebuie să verifice mental:

```text
Layer?

Module?

Owner?

Architectural Impact?

Future Microservice Impact?
```

---

# 19. Output Format Rules

La orice implementare complexă.

Răspunsul trebuie să fie structurat astfel:

```text
1. Context

2. Architectural Decision

3. Impact Analysis

4. Package Structure

5. Class Design

6. Sequence

7. Implementation

8. Tests

9. ArchUnit Impact

10. Future Microservice Impact
```

---

# 20. Refactoring Rules

La refactoring AI-ul trebuie:

1. să păstreze comportamentul;
2. să reducă complexitatea;
3. să reducă coupling-ul;
4. să crească coeziunea;
5. să respecte arhitectura existentă.

---

# 21. Review Rules

Pentru orice Pull Request analizat.

AI-ul trebuie să verifice:

```text
DDD

SOLID

Security

Testing

Architecture

Maintainability

Microservice Readiness
```

---

# 22. Documentation Rules

Pentru orice feature nou.

AI-ul trebuie să sugereze actualizarea:

```text
Requirements

Use Cases

Architecture

ADR

Tests

OpenAPI
```

---

# 23. What "Good" Means

Pentru acest proiect, cod bun înseamnă:

✅ Compilează

✅ Este testat

✅ Respectă arhitectura

✅ Respectă DDD

✅ Respectă ArchUnit

✅ Respectă OWASP

✅ Respectă NIS2

✅ Este documentat

✅ Poate deveni microserviciu

✅ Este ușor de înțeles peste 2 ani

---

# 24. What "Bad" Means

❌ Controller cu business logic

❌ Service de 1000 linii

❌ Repository accesat din alt modul

❌ Domain care importă Spring

❌ Dependințe circulare

❌ DTO reutilizat ca Entity

❌ Cod fără teste

❌ Cod fără documentație

❌ Soluție rapidă care încalcă arhitectura

❌ Refactorizări care afectează bounded contexts

---

# Golden Directive

Whenever multiple implementation choices exist:

Choose the solution that preserves architecture,
improves maintainability,
supports future microservice extraction,
and minimizes technical debt,
even if the implementation takes more effort.