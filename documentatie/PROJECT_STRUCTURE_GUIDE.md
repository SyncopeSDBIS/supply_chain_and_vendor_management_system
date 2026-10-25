# PROJECT_STRUCTURE_GUIDE

# Scop

Acest document descrie structura completă a repository-ului, responsabilitatea fiecărui director și modul în care acesta contribuie la obiectivele de Business Analysis, OOAD, MSD, DevOps, Security și viitoarea migrare către microservicii.

---

# Repository Overview

```text
project-root/

├── docs/
├── adr/
├── diagrams/
├── project-management/
├── docker/
├── config/
├── scripts/
├── tools/
├── .github/
├── src/
├── README.md
├── CONTRIBUTING.md
├── AI_USAGE_POLICY.md
├── pom.xml
└── docker-compose.yml
```

---

# docs/

Conține întreaga documentație oficială a proiectului.

Nu trebuie să conțină:

- cod sursă
- fișiere temporare
- imagini nestructurate

Este sursa principală pentru:

- evaluarea OOAD
- evaluarea MSD
- documentarea deciziilor

---

## docs/01-project-foundation

Conține informațiile fundamentale despre proiect.

### Ce trebuie să conțină

#### Project Vision

Descrie:

- problema rezolvată
- obiectivele proiectului
- beneficiile oferite

#### Scope

Descrie:

- ce intră în proiect
- ce NU intră în proiect

#### Stakeholders

Descrie:

- utilizatori
- administratori
- echipa de dezvoltare
- profesori evaluatori

#### Team Organization

Conține:

- roluri
- responsabilități
- ownership

---

## docs/02-business-analysis

Conține analiza de business.

### Business Requirements

Exemplu:

```text
BR-001
BR-002
BR-003
```

### Business Rules

Exemplu:

```text
O comandă nu poate fi confirmată fără plată.
```

### Constraints

Exemplu:

```text
Stripe trebuie utilizat pentru procesarea plăților.
```

---

## docs/03-requirements

Conține cerințele sistemului.

### Functional Requirements

Exemplu:

```text
FR-001 Create Order

FR-002 Complete Payment

FR-003 Cancel Order
```

### Non Functional Requirements

Obligatoriu:

```text
Security

Scalability

Availability

Reliability

Maintainability

Performance

Observability
```

---

## docs/04-process-modeling

Conține procesele BPMN.

### Ce trebuie să existe

Order Flow

Payment Flow

Notification Flow

Refund Flow

Exception Handling Flow

---

## docs/05-domain-analysis

Conține analiza domeniului.

### Domain Glossary

Toți termenii de business.

### Domain Model

Definirea:

```text
Entities

Value Objects

Aggregates

Domain Services

Events
```

### Bounded Contexts

Identificarea modulelor.

---

## docs/06-ooad

Conține toate artefactele OOAD.

### Use Cases

Descriere completă.

### Activity Diagrams

### Sequence Diagrams

### State Diagrams

### Class Diagrams

### Component Diagrams

### Deployment Diagram

---

## docs/07-architecture

Una dintre cele mai importante secțiuni.

Conține întreaga arhitectură.

---

### Architectural Vision

Explică:

```text
Modular Monolith
→
Microservices
```

---

### Module Architecture

Pentru fiecare modul:

#### Responsabilități

#### API Public

#### Entități

#### Evenimente

#### Dependințe

---

### Layer Architecture

Pentru fiecare layer se documentează:

#### Scop

#### Responsabilități

#### Dependențe permise

#### Dependențe interzise

---

### Design Patterns

Pentru fiecare pattern:

- motiv
- problemă rezolvată
- localizare

---

## docs/08-security

Documentează securitatea.

---

### Security Principles

Zero Trust

OWASP

NIS2

---

### Threat Modeling

Pentru fiecare componentă critică.

Format:

```text
Asset

Threat

Impact

Mitigation
```

---

### Access Matrix

Mapare:

```text
Role
    ↓
Permission
```

---

## docs/09-devops

Documentează procesele DevOps.

---

### Repository Strategy

### Branching Strategy

### Commit Convention

### Release Strategy

### CI/CD Pipeline

---

## docs/10-testing

Conține strategia de testare.

---

### Unit Testing

JUnit

Mockito

---

### Integration Testing

Spring Boot Test

Testcontainers

---

### Architecture Testing

ArchUnit

Aici se documentează fiecare regulă.

---

### API Testing

RestAssured

---

### Coverage Reports

---

## docs/11-api

Documentația API.

---

### Endpoints

Pentru fiecare endpoint:

```text
Request

Response

Errors

Authorization

Examples
```

---

### Module Contracts

Pentru fiecare modul:

```text
Order API

Payment API

Notification API
```

---

## docs/12-deployment

Descrie deployment-ul.

---

### Infrastructure

### Docker

### Docker Compose

### Environment Variables

### Deployment Steps

---

## docs/13-microservices-roadmap

Explică viitorul sistemului.

---

### Current State

Modular Monolith

---

### Extraction Plan

Notification

Payment

Order

---

### Target Architecture

Microservices

Event Driven Architecture

---

# adr/

Conține Architecture Decision Records.

---

Fiecare ADR trebuie să respecte structura:

```text
Title

Status

Context

Decision

Alternatives

Consequences
```

---

Exemplu:

```text
ADR-001 Modular Monolith

ADR-002 RabbitMQ

ADR-003 Stripe

ADR-004 ArchUnit
```

---

# diagrams/

Conține toate diagramele.

---

## diagrams/bpmn

Procese BPMN.

---

## diagrams/uml

Toate diagramele UML.

---

## diagrams/c4

Context Diagram

Container Diagram

Component Diagram

Deployment Diagram

---

# project-management/

Conține artefactele Agile.

---

## backlog

Product Backlog.

---

## sprints

Sprint Backlogs.

---

## retrospectives

Concluzii după sprint.

---

# docker/

Conține infrastructura locală.

---

## app/

Imaginea aplicației.

---

## postgres/

Configurarea PostgreSQL.

---

## rabbitmq/

Configurarea RabbitMQ.

---

# config/

Conține configurațiile.

---

## dev

Environment local.

---

## test

Environment pentru testare.

---

## stage

Environment intermediar.

---

## prod-like

Mediu apropiat de producție.

---

# tools/

Conține tool-uri auxiliare.

---

## checkstyle

Reguli stil cod.

---

## pmd

Static Analysis.

---

## spotbugs

Bug Detection.

---

## sonar

Code Quality.

---

## postman

Colecții API.

---

# .github/

Automatizări GitHub.

---

## workflows/

Pipeline-uri GitHub Actions.

---

### build.yml

Build.

---

### unit-tests.yml

JUnit.

---

### archunit.yml

Architecture Validation.

---

### integration-tests.yml

Integration Tests.

---

### security.yml

Security Scan.

---

### deployment.yml

Deployment.

---

# src/

Conține codul sursă.

---

# shared/

Conține doar componente reutilizabile.

Permis:

```text
exceptions

annotations

constants

utilities

auditing

events
```

Interzis:

```text
business logic
```

---

# customer/

Bounded Context Customer.

---

## api

Contract public.

Expune:

```text
Use Cases

Interfaces

DTO-uri publice
```

---

## application

Coordonează cazurile de utilizare.

Conține:

```text
Application Services

Commands

Queries

Handlers

Mappers
```

---

## domain

Nucleul aplicației.

Conține:

```text
Entities

Value Objects

Aggregates

Domain Services

Policies

Events

Repository Interfaces
```

Nu are voie să depindă de:

```text
Spring

JPA

RabbitMQ

Stripe
```

---

## infrastructure

Detalii tehnice.

Conține:

```text
Persistence

Messaging

External APIs

Configurations
```

---

## presentation

Expune sistemul.

Conține:

```text
Controllers

Request DTOs

Response DTOs
```

---

# order/

Are aceeași structură ca customer.

Responsabilități:

```text
Orders

OrderItems

Checkout

Order Lifecycle
```

---

# payment/

Are aceeași structură.

Responsabilități:

```text
Payments

Transactions

Refunds
```

Conține:

```text
Stripe Adapter

PaymentProvider Implementations
```

---

# notification/

Responsabil pentru:

```text
Email

SMS

Push Notifications
```

---

# security/

Separat de business.

Conține:

```text
Authentication

Authorization

RBAC

JWT

Audit
```

---

# src/test/

Conține toate testele.

---

## unit

Teste unitare.

---

## integration

Teste de integrare.

---

## architecture

Teste ArchUnit.

---

## api

Teste API.

---

## performance

Teste de performanță.

---

# architecture/

Conține regulile ArchUnit.

---

## LayerArchitectureTest

Verifică layerele.

---

## ModuleDependencyTest

Verifică dependențele modulelor.

---

## DependencyCycleTest

Verifică ciclurile.

---

## DomainIsolationTest

Asigură independența Domain Layer.

---

## SecurityArchitectureTest

Verifică regulile de securitate.

---

## NamingConventionTest

Verifică convențiile de nume.

---

# database/

Conține schema bazei de date.

---

## migration

Scripturi Flyway.

---

## seed

Date de test.

---

# resources/

Conține resursele aplicației.

---

## application.yml

Configurație principală.

---

## application-dev.yml

Dezvoltare.

---

## application-test.yml

Testare.

---

## application-stage.yml

Staging.

---

## application-prod.yml

Producție.

---

## logback-spring.xml

Configurarea logging-ului.

---

## openapi/

Contractele API.

---

# README.md

Punctul principal de intrare în repository.

Trebuie să permită unui dezvoltator nou să înțeleagă proiectul în maximum 15 minute.

Conține:

- descriere
- arhitectură
- instalare
- rulare
- testare
- deployment
- contribuție

---

# AI_USAGE_POLICY.md

Descrie:

- cum folosim Cursor