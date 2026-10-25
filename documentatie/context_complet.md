# PROJECT BLUEPRINT
## MSD + OOAD Enterprise-Oriented Java/Spring Boot Application

---

# 1. Project Context

## Academic Context

- Program: SDBIS (Sisteme Distribuite și Business Intelligence Software)
- Faculty: FEAA, UAIC Iași
- Team Size: 3 students
- Courses:
  - Multi-tier Software Development (MSD)
  - Object-Oriented Analysis and Design (OOAD)

---

## Development Philosophy

The project must emulate enterprise software engineering practices rather than a typical academic project.

Goals:

- Professional repository organization
- Enterprise-grade architecture
- High maintainability
- Scalability
- Security by design
- Testability
- Documentation-driven development
- Architecture-first development
- Microservice-ready design

---

## AI-Assisted Development

Tools:

- Cursor
- Antigravity
- GitHub Copilot
- Gemini
- Google AI
- Additional LLM tools

AI is considered a development accelerator, not an architectural decision maker.

Every AI-generated artifact must be:

- reviewed
- validated
- refactored if necessary
- architecture-compliant

---

# 2. High-Level Architectural Goals

The application shall be:

- Modular Monolith initially
- Prepared for Microservices
- Domain Driven
- Event Driven
- Secure by Default
- CI/CD Enabled
- DevSecOps Ready
- Cloud Ready
- Containerized

---

# 3. Architectural Principles

## Clean Architecture

Layers:

```text
Presentation
Application
Domain
Infrastructure
```

Dependencies:

```text
Presentation
    ↓

Application
    ↓

Domain

Infrastructure
    ↓

Domain
```

Forbidden:

```text
Domain -> Infrastructure

Domain -> Spring

Domain -> JPA

Domain -> Messaging
```

---

## SOLID

Apply all SOLID principles.

Particularly:

- Single Responsibility Principle
- Dependency Inversion Principle

---

## Domain Driven Design

### Entities

Examples:

- Order
- Payment
- Customer

### Value Objects

Examples:

- Money
- Address
- Email

### Aggregates

Examples:

- Order Aggregate
- Payment Aggregate

### Aggregate Roots

Examples:

- Order
- Payment

### Domain Services

Business logic spanning entities.

### Repositories

Persistence contracts.

### Domain Events

Examples:

- OrderCreatedEvent
- PaymentCompletedEvent
- OrderCancelledEvent

---

# 4. Modular Monolith Strategy

Initial architecture:

```text
Modular Monolith
```

Future architecture:

```text
Microservices
```

Modules must behave as independent bounded contexts.

---

# 5. Bounded Contexts

## Customer

Responsibilities:

- customer management
- profile operations

---

## Order

Responsibilities:

- order creation
- order management
- checkout process
- order lifecycle

Owner of:

- Order
- OrderItem
- OrderStatus

---

## Payment

Responsibilities:

- payments
- refunds
- financial transactions

Owner of:

- Payment
- Refund
- Transaction

---

## Notification

Responsibilities:

- email
- sms
- push notifications

---

## Shared

Contains only:

- exceptions
- constants
- annotations
- utilities
- common abstractions

Must not contain business logic.

---

# 6. Module Communication Rules

Allowed:

```text
Order -> Payment API

Order -> Customer API
```

Forbidden:

```text
Order -> Payment Repository

Order -> Payment Entity

Order -> Payment Infrastructure
```

Allowed communication:

```text
Public API

Events
```

Forbidden communication:

```text
Internal classes

Infrastructure classes

Persistence implementations
```

---

# 7. Events

Order Events:

- OrderCreatedEvent
- OrderConfirmedEvent
- OrderCancelledEvent

Payment Events:

- PaymentInitiatedEvent
- PaymentSucceededEvent
- PaymentFailedEvent
- RefundCompletedEvent

Notification Events:

- NotificationSentEvent

---

# 8. Messaging

Preferred:

RabbitMQ

Reasons:

- Simpler
- Easier for academic projects
- Less operational complexity

Use Cases:

- Event publishing
- Notification workflows
- Decoupling modules

---

# 9. Payment Integration

Preferred:

Stripe

Reasons:

- Industry adoption
- Professional CV value
- Sandbox support

Required:

- Payment processing
- Webhooks
- Refund support
- Secure API integration

Implementation:

```text
PaymentProvider
        ▲
        │
StripePaymentProvider
```

Avoid direct Stripe dependency in business logic.

---

# 10. Security

Principles:

- Zero Trust
- OWASP Top 10
- OWASP ASVS
- NIS2 Alignment

---

## Roles

Administrator

Operator

Customer

---

## Security Controls

### Input Validation

Required.

---

### Output Encoding

Required.

---

### Secrets Management

Required.

---

### Audit Logging

Required.

---

### API Security

Required.

---

### Rate Limiting

Recommended.

---

# 11. Observability

Components:

- Spring Actuator
- Micrometer
- Prometheus
- Grafana

Metrics:

- requests
- errors
- JVM metrics
- database metrics
- messaging metrics

---

# 12. Logging

Structured logging only.

Example:

```json
{
  "correlationId":"123",
  "module":"order",
  "event":"ORDER_CREATED"
}
```

---

## Correlation IDs

Required.

Track:

```text
Request
  ↓

Order
  ↓

Payment
  ↓

Notification
```

---

# 13. Non Functional Requirements

## Security

Must satisfy security requirements.

---

## Scalability

Future microservice readiness.

---

## Availability

High availability principles.

---

## Reliability

Consistent behavior.

---

## Maintainability

Clean architecture.

---

## Extensibility

Plug-in friendly design.

---

## Performance

Efficient APIs.

---

## Observability

Metrics + logs.

---

# 14. Design Patterns

Apply only when justified.

---

## Creational

### Factory

### Abstract Factory

### Builder

---

## Structural

### Adapter

Stripe Integration

### Facade

Module APIs

### Decorator

Optional

---

## Behavioral

### Strategy

PaymentProvider

### Observer

Domain Events

### Command

Application Services

### Chain of Responsibility

Validation pipelines

### Mediator

Optional

---

## Enterprise Patterns

### Repository

### Specification

### Unit Of Work

### CQRS Light

Recommended.

---

# 15. Spring Modulith

Recommended.

Benefits:

- explicit modules
- event-driven modules
- architecture verification
- migration readiness

---

# 16. Git Strategy

Recommended:

Git Flow

Branches:

```text
main

develop

feature/*

release/*

hotfix/*
```

---

# 17. Commit Convention

```text
feat:

fix:

docs:

test:

refactor:

chore:
```

---

# 18. Pull Request Policy

Required:

- Code Review
- Tests Passing
- CI Green
- Architecture Rules Passing

---

# 19. DevSecOps

Pipeline:

```text
Commit
    ↓
Build
    ↓
Unit Tests
    ↓
ArchUnit
    ↓
Integration Tests
    ↓
Security Scan
    ↓
Quality Gate
    ↓
Package
    ↓
Deployment
```

---

# 20. Quality Gates

Tools:

- JUnit
- ArchUnit
- Checkstyle
- SpotBugs
- PMD
- OWASP Dependency Check

---

# 21. Testing Strategy

Follow Testing Pyramid.

---

## Unit Tests

Tools:

- JUnit 5
- Mockito

---

## Integration Tests

Tools:

- Spring Boot Test
- Testcontainers

Use:

- PostgreSQL Container
- RabbitMQ Container

---

## API Tests

Tools:

- RestAssured

---

## Contract Testing

Possible:

- OpenAPI
- Spring Cloud Contract

---

# 22. ArchUnit

Purpose:

Architecture as executable rules.

---

## Layer Rules

Presentation -> Application

Application -> Domain

Infrastructure -> Domain

Forbidden:

Domain -> Infrastructure

---

## Module Rules

Only public APIs accessible.

Internal implementation inaccessible.

---

## Domain Isolation Rules

Forbidden:

```text
Domain -> Spring

Domain -> JPA

Domain -> Stripe

Domain -> RabbitMQ
```

---

## Cyclic Dependency Rules

No dependency cycles allowed.

---

## Naming Rules

Required:

```text
*Controller

*Service

*Repository

*Mapper

*Dto
```

---

## Security Rules

Controllers must not access repositories directly.

Controllers must expose DTOs.

Never expose JPA entities.

---

# 23. ArchUnit Test Structure

```text
architecture/

├── LayerArchitectureTest
├── ModuleDependencyTest
├── DependencyCycleTest
├── DomainIsolationTest
├── SecurityArchitectureTest
├── NamingConventionTest

├── customer
├── order
├── payment
└── notification
```

---

## Order Module Rules

Order owns:

- orders
- order items
- order status

Payment cannot modify Order directly.

Communication:

```text
Payment -> Order API
```

Not:

```text
Payment -> Order Repository
```

---

## Payment Module Rules

Payment owns:

- transactions
- payments
- refunds

Order cannot manipulate payments directly.

Communication through:

- APIs
- Events

---

# 24. OpenAPI

Required.

Contains:

- endpoints
- schemas
- errors
- contracts

---

# 25. Database

Preferred:

PostgreSQL

Migration Tool:

Flyway

Alternative:

Liquibase

---

# 26. Docker

Containerize:

- Application
- PostgreSQL
- RabbitMQ

Use Docker Compose.

---

# 27. Project Structure

```text
project-root/

├── docs
├── adr
├── diagrams
├── docker
├── config
├── scripts
├── tools
├── src
├── .github
├── README.md
├── AI_USAGE_POLICY.md
├── CONTRIBUTING.md
└── pom.xml
```

---

# 28. Source Code Structure

```text
customer
order
payment
notification
shared
security
```

Every module contains:

```text
api

application

domain

infrastructure

presentation
```

---

# 29. Documentation Structure

```text
01-project-foundation
02-business-analysis
03-requirements
04-process-modeling
05-domain-analysis
06-ooad
07-architecture
08-security
09-devops
10-testing
11-api
12-deployment
13-microservices-roadmap
appendices
```

---

# 30. ADR Catalogue

ADR-001 Modular Monolith

ADR-002 Clean Architecture

ADR-003 DDD

ADR-004 PostgreSQL

ADR-005 RabbitMQ

ADR-006 Stripe

ADR-007 ArchUnit

ADR-008 Docker

ADR-009 GitHub Actions

ADR-010 Microservice Migration

---

# 31. BPMN

Required:

- Order Process
- Checkout Process
- Payment Process
- Notification Process

---

# 32. UML

Required:

- Use Case Diagram
- Activity Diagram
- Sequence Diagram
- State Diagram
- Class Diagram
- Package Diagram
- Component Diagram
- Deployment Diagram

---

# 33. Traceability

Mandatory:

```text
Requirement
     ↓
User Story
     ↓
Use Case
     ↓
Architecture
     ↓
Implementation
     ↓
Testing
```

Example:

FR-012
 ↓
UC-05
 ↓
Payment Service
 ↓
Stripe Adapter
 ↓
Integration Test

```

---

# 34. Threat Modeling

For every critical component:

```text
Asset

Threat

Impact

Mitigation
```

---

# 35. Future Microservice Migration

Phase 1:

```text
Modular Monolith
```

Phase 2:

```text
Notification Service
```

Phase 3:

```text
Payment Service
```

Phase 4:

```text
Order Service
```

Phase 5:

```text
API Gateway
Service Discovery
```

---

# 36. Definition of Success

The project is considered successful when:

- OOAD artefacts are traceable to implementation
- CI/CD is fully operational
- ArchUnit validates architecture
- RabbitMQ events are integrated
- Stripe integration works
- Security requirements are respected
- Documentation is complete
- Modular boundaries are enforced
- Application is microservice-ready
- Codebase is maintainable and extensible