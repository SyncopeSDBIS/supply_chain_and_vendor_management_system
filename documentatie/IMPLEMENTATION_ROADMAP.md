# IMPLEMENTATION_ROADMAP.md

# Scop

Acest document definește ordinea recomandată de implementare a proiectului astfel încât:

- să existe trasabilitate între OOAD și MSD;
- să fie evitată refactorizarea majoră ulterioară;
- să fie pregătită migrarea către microservicii;
- să fie implementate continuu măsuri de securitate, testare și calitate;
- CI/CD să valideze permanent arhitectura.

---

# Principiu General

Nu implementăm funcționalități direct.

Ordinea este:

```text
Business
    ↓

Requirements
    ↓

Domain
    ↓

Architecture
    ↓

Contracts
    ↓

Implementation
    ↓

Testing
    ↓

DevOps
```

NICIODATĂ:

```text
Controller
    ↓
Repository
    ↓
Merge
```

---

# Faza 0 - Inițializarea Repository-ului

Obiectiv:

Crearea fundației proiectului.

---

## 0.1 Repository

Creare repository Git.

Implementare:

```text
README.md
CONTRIBUTING.md
AI_USAGE_POLICY.md
.gitignore
```

Rezultat:

Repository utilizabil.

---

## 0.2 Structura directoarelor

Creare:

```text
docs/
adr/
diagrams/
project-management/
docker/
config/
tools/
src/
```

Rezultat:

Structură stabilă.

---

## 0.3 Git Workflow

Definire:

```text
main
develop
feature/*
release/*
hotfix/*
```

Implementare:

- branch policy
- pull request template
- code review policy

---

# Faza 1 - Business Analysis

Obiectiv:

Înțelegerea completă a problemei.

---

## 1.1 Vision

Completare:

```text
Project Vision
Project Scope
Project Goals
```

Document:

```text
docs/01-project-foundation
```

---

## 1.2 Stakeholders

Identificare:

```text
Customer
Operator
Administrator
```

Rezultat:

Actorii sistemului.

---

## 1.3 Business Requirements

Creare:

```text
BR-001
BR-002
BR-003
```

Rezultat:

Lista cerințelor de business.

---

## 1.4 Business Rules

Documentare:

Exemplu:

```text
Comanda trebuie plătită înainte de confirmare.
```

---

# Faza 2 - Requirements Engineering

Obiectiv:

Transformarea cerințelor de business în cerințe de sistem.

---

## 2.1 Functional Requirements

Creare:

```text
FR-001 Create Order

FR-002 Pay Order

FR-003 Cancel Order
```

---

## 2.2 Non Functional Requirements

Documentare:

```text
Security
Scalability
Reliability
Performance
Availability
Observability
Maintainability
Extensibility
```

---

## 2.3 User Stories

Scriere User Stories.

---

## 2.4 Acceptance Criteria

Definirea criteriilor de acceptare.

---

# Faza 3 - BPMN

Obiectiv:

Modelarea proceselor.

---

## 3.1 Order Flow

BPMN complet.

---

## 3.2 Payment Flow

BPMN complet.

---

## 3.3 Notification Flow

BPMN complet.

---

## 3.4 Refund Flow

BPMN complet.

---

# Faza 4 - Domain Analysis

Obiectiv:

Înțelegerea domeniului înainte de implementare.

---

## 4.1 Domain Glossary

Definirea termenilor.

---

## 4.2 Domain Model

Identificare:

```text
Entities
Value Objects
Aggregates
Domain Services
```

---

## 4.3 Bounded Contexts

Definire:

```text
Customer

Order

Payment

Notification
```

---

## 4.4 Domain Events

Definire:

```text
OrderCreatedEvent

PaymentSucceededEvent

OrderCancelledEvent
```

---

# Faza 5 - OOAD

Obiectiv:

Realizarea modelelor UML.

---

## 5.1 Use Cases

Mai întâi.

---

## 5.2 Activity Diagrams

După Use Cases.

---

## 5.3 Sequence Diagrams

După Activity.

---

## 5.4 Class Diagram

După Sequence.

---

## 5.5 Package Diagram

După stabilirea modulelor.

---

## 5.6 Component Diagram

După stabilirea arhitecturii.

---

## 5.7 Deployment Diagram

La finalul modelării.

---

# Faza 6 - Architecture

Obiectiv:

Blocarea arhitecturii înainte de cod.

---

## 6.1 C4

Creare:

```text
System Context

Container

Component
```

---

## 6.2 Module Architecture

Definire:

```text
Customer

Order

Payment

Notification
```

---

## 6.3 Layer Architecture

Definire:

```text
Presentation

Application

Domain

Infrastructure
```

---

## 6.4 Design Patterns

Alegere pattern-uri.

Exemple:

```text
Factory

Strategy

Adapter

Facade

Repository

Specification
```

---

## 6.5 ADR-uri

Creare:

```text
ADR-001 Modular Monolith

ADR-002 DDD

ADR-003 RabbitMQ

ADR-004 Stripe

ADR-005 ArchUnit
```

---

# Faza 7 - ArchUnit First

Obiectiv:

Scriem regulile înaintea implementării.

---

## 7.1 Layer Rules

Implementare:

```text
Presentation → Application

Application → Domain

Infrastructure → Domain
```

---

## 7.2 Module Rules

Implementare:

```text
Order → Payment API

Order → Customer API
```

---

## 7.3 Domain Isolation

Blocare dependențe:

```text
Domain → Spring
Domain → JPA
Domain → Stripe
Domain → RabbitMQ
```

---

## 7.4 Cycle Detection

Implementare.

---

# Faza 8 - Configurație Tehnică

---

## 8.1 PostgreSQL

Configurare.

---

## 8.2 Flyway

Configurare.

---

## 8.3 Docker

Configurare.

---

## 8.4 RabbitMQ

Configurare.

---

# Faza 9 - Skeleton Application

Obiectiv:

Structură fără logică.

---

## 9.1 Module

Creare:

```text
customer
order
payment
notification
shared
security
```

---

## 9.2 Layere

Creare pentru fiecare modul:

```text
api
application
domain
infrastructure
presentation
```

---

## 9.3 Public APIs

Definire contracte.

Fără implementare.

---

# Faza 10 - CI/CD

Obiectiv:

Pipeline funcțional înaintea logicii complexe.

---

## 10.1 Build Pipeline

GitHub Actions.

---

## 10.2 Unit Tests Pipeline

JUnit.

---

## 10.3 ArchUnit Pipeline

Validation.

---

## 10.4 Security Pipeline

OWASP Dependency Check.

---

## 10.5 Quality Pipeline

SpotBugs
PMD
Checkstyle

---

# Faza 11 - Implementare Incrementală

Ordinea recomandată:

```text
Customer
    ↓
Order
    ↓
Payment
    ↓
Notification
```

---

## 11.1 Customer

Implementare completă.

- Domain
- Application
- Infrastructure
- Presentation

Teste.

---

## 11.2 Order

Implementare completă.

Teste.

Evenimente.

---

## 11.3 Payment

Implementare completă.

Stripe Sandbox.

Teste.

---

## 11.4 Notification

Implementare completă.

RabbitMQ.

Teste.

---

# Faza 12 - Security

---

## 12.1 RBAC

Roluri:

```text
Admin
Operator
Customer
```

---

## 12.2 JWT

Implementare.

---

## 12.3 Audit Logging

Implementare.

---

## 12.4 Input Validation

Implementare.

---

# Faza 13 - Integration Testing

---

## 13.1 Database Tests

Testcontainers.

---

## 13.2 RabbitMQ Tests

Testcontainers.

---

## 13.3 Stripe Tests

Sandbox.

---

# Faza 14 - Observability

---

## 14.1 Spring Actuator

---

## 14.2 Micrometer

---

## 14.3 Prometheus

---

## 14.4 Grafana

---

## 14.5 Structured Logging

---

## 14.6 Correlation IDs

---

# Faza 15 - Hardening

Obiectiv:

Pregătire pentru demonstrație.

---

## Verificări

- toate testele trec
- ArchUnit verde
- pipeline verde
- documentație actualizată
- ADR-uri actualizate
- OpenAPI actualizat

---

# Faza 16 - Pregătire Microservicii

Obiectiv:

Validarea pregătirii pentru extracție.

---

## Notification Extraction Analysis

---

## Payment Extraction Analysis

---

## Order Extraction Analysis

---

## API Gateway Analysis

---

## Event-Driven Architecture Validation

---

# Definition of Done

Un modul este considerat finalizat doar dacă:

✅ documentat

✅ modelat UML

✅ ADR actualizat

✅ implementat

✅ testat unitar

✅ testat integrare

✅ validat ArchUnit

✅ inclus în pipeline

✅ OpenAPI actualizat

✅ observabilitate implementată

✅ securizat

✅ pregătit pentru extracție în microservicii