<!-- Sursa: ANTIGRAVITY_AI_DEVELOPMENT_CONFIGURATION.md -->
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

---

<!-- Sursa: BEST_PRACTICES_GUIDE.md -->
# BEST_PRACTICES_GUIDE.md

# Scop

Acest document definește regulile, principiile și standardele care trebuie respectate pe întreaga durată a proiectului.

Obiectivul este:

- consistență;
- calitate;
- mentenabilitate;
- scalabilitate;
- securitate;
- pregătire pentru microservicii;
- evaluare profesională la OOAD și MSD.

---

# 1. Principii Generale

## 1.1 Architecture First

Arhitectura se proiectează înainte de implementare.

Ordinea corectă:

```text
Requirements
    ↓
Analysis
    ↓
Architecture
    ↓
Contracts
    ↓
Implementation
```

Nu implementăm "direct din controller".

---

## 1.2 Business First

Orice componentă software trebuie să rezolve un requirement identificat.

Orice clasă trebuie să poată fi mapată către:

```text
Business Requirement
    ↓
Functional Requirement
    ↓
Use Case
    ↓
Implementation
```

---

## 1.3 Simplicity Over Cleverness

Preferăm:

```text
Cod simplu
```

în loc de:

```text
Cod impresionant dar greu de întreținut
```

---

## 1.4 Evolutionary Architecture

Deciziile trebuie să permită:

```text
Modular Monolith
    ↓
Microservices
```

fără refactorizări masive.

---

# 2. Repository Best Practices

## 2.1 Repository-ul este produsul

Repository-ul trebuie să fie suficient de clar încât un dezvoltator nou să îl înțeleagă în mai puțin de 15 minute.

---

## 2.2 README Obligatori

README trebuie să răspundă la:

```text
Ce este proiectul?

Cum îl rulez?

Cum îl testez?

Cum contribui?
```

---

## 2.3 Fără Fișiere Inutile

Nu se comite:

```text
node_modules

target

.idea

.vscode

fișiere temporare
```

---

## 2.4 Structura Stabilă

Nu mutăm folderele după începerea implementării decât dacă există ADR pentru asta.

---

# 3. Git Best Practices

## 3.1 Feature Branches

Tot dezvoltarea se face pe:

```text
feature/*
```

niciodată direct pe:

```text
main
```

---

## 3.2 Pull Requests Obligatorii

Nimeni nu face merge fără review.

---

## 3.3 Commit-uri Mici

Preferăm:

```text
10 commit-uri mici
```

în loc de:

```text
1 commit de 200 fișiere
```

---

## 3.4 Commit Convention

```text
feat:

fix:

refactor:

test:

docs:

chore:
```

Exemple:

```text
feat(order): add order creation workflow

fix(payment): handle failed transaction

test(customer): add repository tests
```

---

# 4. Architecture Best Practices

## 4.1 Respectarea Layerelor

Permis:

```text
Presentation
    ↓

Application
    ↓

Domain
```

Interzis:

```text
Controller
    ↓
Repository
```

---

## 4.2 Domain Centric Design

Domain-ul este centrul aplicației.

Nu framework-ul.

---

## 4.3 Dependency Rule

Dependențele trebuie să meargă spre interior.

```text
Infrastructure
    ↓

Domain
```

nu invers.

---

## 4.4 Public API Only

Acces între module exclusiv prin:

```text
module.api
```

Nu prin:

```text
module.infrastructure
module.persistence
module.controller
```

---

# 5. DDD Best Practices

## 5.1 Entități Bogate

Evitați:

```java
anemic entities
```

Logica trebuie să stea cât mai aproape de domeniu.

---

## 5.2 Aggregate Boundaries

Fiecare Aggregate trebuie să aibă:

```text
Owner clar
Responsabilitate clară
```

---

## 5.3 Domain Events

Pentru acțiuni importante:

```text
OrderCreated

PaymentCompleted

OrderCancelled
```

---

## 5.4 Bounded Contexts

Modulele trebuie să poată deveni microservicii.

---

# 6. Java Best Practices

## 6.1 Constructor Injection Only

Preferat:

```java
@RequiredArgsConstructor
```

Evitați:

```java
@Autowired field injection
```

---

## 6.2 Immutability

Preferați:

```java
final
```

unde este posibil.

---

## 6.3 Nu Folosiți Clase Utility Excesiv

Clasele utility trebuie să fie excepția.

---

## 6.4 Nu Expuneți Entități

Controller-ele expun:

```text
DTO
```

Nu:

```text
JPA Entity
```

---

# 7. Spring Best Practices

## 7.1 Constructor Injection

Obligatoriu.

---

## 7.2 Configuration Properties

Preferabil:

```java
@ConfigurationProperties
```

în loc de valori hardcodate.

---

## 7.3 Profile-uri Separate

```text
dev
test
stage
prod
```

---

## 7.4 Service Layer

Controller-ul nu conține logică de business.

---

# 8. API Best Practices

## 8.1 OpenAPI First

Orice endpoint trebuie documentat.

---

## 8.2 Versionare

Exemplu:

```text
/api/v1/orders
```

---

## 8.3 DTO Separation

Separare între:

```text
Request DTO
Response DTO
Entity
```

---

## 8.4 Consistent Errors

Format unic pentru erori.

---

# 9. Database Best Practices

## 9.1 Flyway

Toate modificările bazei de date trebuie să fie în migration scripts.

---

## 9.2 Nu Modificați Manual Schema

Orice schimbare trebuie făcută prin migrare.

---

## 9.3 Indexuri

Index pentru:

```text
foreign keys
search fields
business identifiers
```

---

## 9.4 UUID

Preferabil pentru entități expuse extern.

---

# 10. Messaging Best Practices

## 10.1 Event Driven

Folosiți evenimente pentru decuplare.

---

## 10.2 Idempotency

Procesarea trebuie să suporte:

```text
duplicate messages
```

---

## 10.3 Retry Strategy

Mesajele trebuie retrimise controlat.

---

# 11. Stripe Best Practices

## 11.1 Adapter Pattern

Stripe nu trebuie utilizat direct în business logic.

---

## 11.2 Sandbox First

Toate testele folosesc sandbox.

---

## 11.3 Webhook Validation

Toate webhook-urile trebuie validate.

---

# 12. Security Best Practices

## 12.1 Security by Default

Acces minim necesar.

---

## 12.2 Validate Everything

Validați:

```text
Request Body

Query Params

Headers

Path Variables
```

---

## 12.3 Never Trust User Input

Intrările utilizatorului sunt considerate ostile.

---

## 12.4 Secrets

Nu stocăm:

```text
passwords

keys

tokens
```

în repository.

---

## 12.5 Principle of Least Privilege

Fiecare rol primește strict accesul necesar.

---

# 13. Logging Best Practices

## 13.1 Structured Logging

Folosiți JSON logs.

---

## 13.2 Correlation IDs

Obligatorii.

---

## 13.3 Log Levels

```text
TRACE

DEBUG

INFO

WARN

ERROR
```

---

## 13.4 Nu Logați Secrete

Interzis:

```text
passwords

tokens

card numbers
```

---

# 14. Testing Best Practices

## 14.1 Testing Pyramid

```text
Unit Tests
    ↑

Integration Tests
    ↑

API Tests
```

---

## 14.2 Teste Rapide

Unit tests trebuie să ruleze rapid.

---

## 14.3 Determinism

Teste care dau același rezultat de fiecare dată.

---

## 14.4 Testcontainers

Folosiți infrastructură reală.

---

## 14.5 Coverage

Testați:

```text
happy paths

error paths

edge cases
```

---

# 15. ArchUnit Best Practices

## 15.1 Arhitectura Este Cod

Orice regulă importantă trebuie validată automat.

---

## 15.2 Layer Rules

Verificate permanent.

---

## 15.3 Module Rules

Verificate permanent.

---

## 15.4 Domain Isolation

Domain fără dependențe tehnice.

---

## 15.5 Cycle Detection

Fără dependențe circulare.

---

# 16. DevOps Best Practices

## 16.1 CI Failing Means Stop

Nu ignorăm pipeline-uri roșii.

---

## 16.2 Quality Gates

Obligatorii:

```text
ArchUnit

JUnit

Checkstyle

PMD

SpotBugs
```

---

## 16.3 Automatizare

Preferăm automatizare în loc de procese manuale.

---

# 17. Documentation Best Practices

## 17.1 Documentation as Code

Documentația este versionată.

---

## 17.2 ADR-uri

Fiecare decizie importantă:

```text
ADR
```

---

## 17.3 Actualizare Continuă

Documentația trebuie actualizată odată cu codul.

---

# 18. AI Usage Best Practices

## 18.1 AI Generates

AI propune.

---

## 18.2 Developers Decide

Echipa decide.

---

## 18.3 Validate Everything

Codul AI nu este considerat corect implicit.

---

## 18.4 Review Mandatory

Tot codul generat trebuie:

```text
citit

înțeles

testat

revizuit
```

---

## 18.5 Nu Copiem Orb

Orice cod introdus trebuie explicat de cel care îl comite.

---

# 19. Microservices Readiness Best Practices

## 19.1 Module Boundaries

Respectate strict.

---

## 19.2 Event-Based Communication

Preferată.

---

## 19.3 API Contracts

Stabile.

---

## 19.4 Independent Ownership

Fiecare modul este proprietarul datelor sale.

---

# 20. Golden Rules

1. Architecture before implementation.
2. Requirements before coding.
3. Public APIs between modules.
4. Domain independent from frameworks.
5. Every important decision has an ADR.
6. Every architecture rule has an ArchUnit test.
7. Every feature has tests.
8. Every change passes CI/CD.
9. Documentation evolves with code.
10. Build today as if the system will become microservices tomorrow.

---

<!-- Sursa: c4.md -->
# ADR-002: Adoptarea modelului C4 pentru documentarea arhitecturii

## Status

Accepted

---

## Context

Proiectul necesită o metodă clară și progresivă pentru reprezentarea arhitecturii software.

Diagramele UML tradiționale pot deveni fie prea detaliate, fie prea abstracte, ceea ce reduce înțelegerea sistemului de către stakeholderi și echipa de dezvoltare. Modelul C4 oferă o abordare structurată bazată pe mai multe niveluri de abstractizare. 【1-01dd6c】

---

## Decision

Pentru documentarea arhitecturii se va utiliza modelul C4 (Creat de Simon Brown). 【1-01dd6c】

Modelul va descrie sistemul utilizând mai multe niveluri succesive de detaliere.

---

## Architecture Views

### Level 1 – System Context Diagram

#### Purpose

Prezintă imaginea de ansamblu a sistemului și interacțiunile externe. 【1-01dd6c】

#### Must Include

- Sistemul analizat
- Utilizatorii sistemului
- Sistemele externe
- Relațiile dintre acestea

#### Example

- Project Management System
- Users
- Email Service
- Payment Processor

【1-01dd6c】

---

### Level 2 – Container Diagram

#### Purpose

Prezintă structura internă a sistemului la nivel de aplicații și infrastructură. 【1-01dd6c】

#### Must Include

- aplicații web;
- servicii backend;
- baze de date;
- mesagerie;
- alte containere logice.

#### Example

- Web Application
- Spring Boot API
- PostgreSQL Database
- Kafka Message Broker

【1-01dd6c】

---

### Level 3 – Component Diagram

#### Purpose

Prezintă structura internă a fiecărui container. 【1-01dd6c】

#### Must Include

- controllers;
- services;
- repositories;
- module;
- componente de business.

#### Example

API Container:

- REST Controller
- Service Layer
- Repository Layer

【1-01dd6c】

---

### Level 4 – Code Diagram

#### Purpose

Descrie implementarea concretă a componentelor. 【1-01dd6c】

#### Must Include

- clase;
- interfețe;
- metode;
- relații de implementare.

Acest nivel este opțional și este de regulă generat automat din IDE. 【1-01dd6c】

---

## Architectural Constraints

### Progressive Modelling

Diagramele trebuie elaborate în ordine:

1. Context Diagram
2. Container Diagram
3. Component Diagram
4. Code Diagram (opțional)

【1-01dd6c】

---

### Minimal Deliverables

În practică sunt suficiente:

- Context Diagram
- Container Diagram
- unul sau mai multe Component Diagrams

Nivelul Code Diagram poate lipsi. 【1-01dd6c】

---

## Quality Principles

Modelarea trebuie să urmărească:

- claritate;
- ușurință în înțelegere;
- comunicarea arhitecturii către stakeholderi;
- reducerea complexității documentației.

Accentul este pus pe claritate și nu pe respectarea strictă a formalismului UML. 【1-01dd6c】

---

## Sprint 1 Deliverables

### Mandatory

- [ ] System Context Diagram
- [ ] Container Diagram

### Recommended

- [ ] Component Diagram pentru fiecare container important

### Optional

- [ ] Code Diagram

【1-01dd6c】

---

<!-- Sursa: context.md -->
# ADR-001 - Supply Chain and Vendor Management System Domain Design

## Status

Accepted

---

# 1. Business Vision

## Problem Statement

Companiile de producție gestionează manual sau fragmentat:

- furnizori
- comenzi de aprovizionare
- recepții
- inspecții de calitate
- facturi
- plăți

Acest lucru conduce la:

- întârzieri
- lipsă de trasabilitate
- dificultăți de audit
- risc financiar
- performanță redusă a furnizorilor

## Goal

Construirea unei platforme centralizate de gestionare a procesului Procure-to-Pay (P2P), integrând:

- Procurement
- Supplier Management
- Quality Management
- Invoice Management
- Payment Management

---

# 2. Stakeholders

## Internal

### Procurement Manager

Responsabil de:

- creare comenzi
- monitorizare livrări
- negociere furnizori

### Buyer

Responsabil de:

- lansare comenzi
- modificare comenzi

### Warehouse Operator

Responsabil de:

- recepții
- gestionare loturi

### Quality Inspector

Responsabil de:

- verificări AQL
- inspecții
- NCR-uri

### Finance Specialist

Responsabil de:

- verificare facturi
- reconciliere
- plăți

### Finance Manager

Responsabil de:

- aprobări plăți

### System Administrator

Responsabil de:

- configurare
- audit
- securitate

---

# 3. Ubiquitous Language

| Termen | Definiție |
|----------|------------|
| Purchase Order | Comandă de aprovizionare |
| Lot | Livrare fizică asociată unei comenzi |
| Supplier | Furnizor |
| NCR | Non-Conformance Report |
| Three Way Match | Verificare Comandă-NIR-Factură |
| OTIF | On Time In Full |
| AQL | Acceptable Quality Level |
| GRN/NIR | Goods Receipt Note |

---

# 4. Domain Landscape

## Core Domain

### Procurement Order Management

Responsabilități:
- creare PO
- validare PO
- confirmare PO
- recepție PO
- anulare PO

---

## Supporting Domain

### Supplier & Quality Management

Responsabilități:
- onboarding furnizori
- evaluare
- inspecții
- scorecard

---

## Supporting Domain

### Invoice & Payment Management

Responsabilități:
- facturi
- reconciliere
- plăți

---

## Generic Domains

### Notification Management

### Security & Audit

---

# 5. Business Processes

## Procure To Pay Flow

1. Buyer creează comandă
2. Supplier confirmă
3. Supplier expediază
4. Warehouse recepționează
5. Quality efectuează inspecție
6. Se generează NIR
7. Furnizorul emite factura
8. Se realizează Three Way Match
9. Se inițiază plata
10. Plata este executată

---

# 6. Actors and Use Cases

## Procurement

### UC-01 Create Purchase Order

Actor:
- Buyer

Preconditions:
- Supplier activ

Postconditions:
- PO creat în stare Requested

---

### UC-02 Confirm Order

Actor:
- Supplier

Rezultat:
- Confirmed
sau
- Rejected

---

### UC-03 Receive Goods

Actor:
- Warehouse Clerk

Rezultat:
- Lot recepționat

---

### UC-04 Perform Quality Inspection

Actor:
- Inspector

Rezultat:
- Pass
- Fail

---

### UC-05 Register Invoice

Actor:
- Finance Specialist

---

### UC-06 Execute Payment

Actor:
- Finance Manager

---

# 7. Domain Model

## Aggregate: PurchaseOrder

### Aggregate Root

PurchaseOrder

### Entities

- PurchaseOrderLine
- ShipmentLot

### Value Objects

- OrderNumber
- Money
- Quantity
- DeliveryAddress

---

## Aggregate: Supplier

### Root

Supplier

### Entities

- SupplierCategory
- SupplierContract
- SupplierScorecard

### Value Objects

- TaxId
- Address
- IBAN

---

## Aggregate: Inspection

### Root

QualityInspection

### Entities

- InspectionResult
- Defect

### Value Objects

- AQL
- InspectionStatus

---

## Aggregate: Invoice

### Root

SupplierInvoice

### Entities

- InvoiceLine

### Value Objects

- InvoiceNumber
- VAT
- Currency

---

## Aggregate: Payment

### Root

PaymentOrder

### Value Objects

- PaymentAmount
- BankAccount

---

# 8. State Machines

## Purchase Order

Requested
→ Confirmed
→ Partially Received
→ Received

Requested
→ Cancelled

Requested
→ Rejected

---

## Supplier

Candidate
→ Evaluation
→ Approved

Approved
→ Conditional

Conditional
→ Approved

Conditional
→ Suspended

Suspended
→ Blocked

---

## Invoice

Draft
→ Registered
→ Matched
→ Approved
→ Paid

Registered
→ Blocked

---

# 9. Business Rules

## Procurement Rules

BR-PO-01 ...
BR-PO-02 ...
...
BR-PO-08 ...

## Supplier Rules

BR-SQ-01 ...
...
BR-SQ-10 ...

## Finance Rules

BR-IP-01 ...
...
BR-IP-09 ...

---

# 10. Domain Events

## Procurement Events

- PurchaseOrderCreated
- PurchaseOrderConfirmed
- PurchaseOrderRejected
- ShipmentRecorded
- GoodsReceived
- PurchaseOrderCancelled

## Supplier Events

- SupplierApproved
- SupplierConditional
- SupplierSuspended
- SupplierBlocked

## Quality Events

- InspectionCompleted
- NCRCreated
- NCRClosed

## Finance Events

- InvoiceRegistered
- InvoiceMatched
- PaymentApproved
- PaymentExecuted

---

# 11. Integration Contracts

## Synchronous APIs

### Procurement → Supplier

validateSupplier()

### Finance → Procurement

getOrderSummary()

---

## Asynchronous Events

GoodsReceivedEvent
InspectionCompletedEvent
SupplierBlockedEvent
InvoiceApprovedEvent

---

# 12. KPI Metrics

## Procurement

- OTIF
- Average Lead Time
- Order Fulfillment Rate

## Supplier

- Supplier Score
- NCR Rate

## Finance

- Invoice Match Rate
- Payment Delay Rate

---

# 13. Non Functional Requirements

## Performance

<300 ms lead time estimation

## Security

RBAC
GDPR
Audit Trail

## Reliability

Idempotent Event Consumers

## Scalability

5000 invoices/minute

## Consistency

ACID transactions

---

# 14. Acceptance Criteria

Sprint 1 este acceptat dacă:

- există Use Case Diagram
- există Activity Diagram
- există Domain Model
- există Aggregate Design
- există Business Rules
- există State Diagrams
- există Event Catalogue
- există Architecture Context Diagram
- există Bounded Context Diagram

---

<!-- Sursa: context_complet.md -->
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

---

<!-- Sursa: IMPLEMENTATION_ROADMAP.md -->
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

---

<!-- Sursa: PROJECT_STRUCTURE_GUIDE.md -->
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