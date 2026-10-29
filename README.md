# Supply Chain and Vendor Management System (Procure-to-Pay)

Enterprise-grade **Procure-to-Pay (P2P)** system implementing Domain-Driven Design (DDD), Hexagonal Architecture (Ports & Adapters), Event-Driven Architecture with Transactional Outbox/Inbox patterns, and strict enterprise security controls. Aligned 1:1 with **Project_OOAD.docx** and Course Blueprint Sprints 1–4.

---

## 🏛️ 1. Architecture & Bounded Contexts

The system is decomposed into three isolated Bounded Contexts and a shared kernel:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SHARED KERNEL                                  │
│  - Value Objects: Suma, Moneda, Cantitate, CodMaterial, IdLot, IdFurnizor   │
│  - Transactional Outbox / Idempotent Inbox Entities & Dispatchers           │
│  - Audit Logging System (shared_audit_log) & RBAC Security Filter          │
└──────────────┬──────────────────────────────┬───────────────────────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────────┐┌──────────────────────────────┐┌──────────────────────────────┐
│       COMENZI CONTEXT        ││       CALITATE CONTEXT       ││       FACTURI CONTEXT        │
│                              ││                              ││                              │
│ • Aggregates:                ││ • Aggregates:                ││ • Aggregates:                │
│   - ComandaAprovizionare     ││   - Furnizor                 ││   - FacturaFurnizor          │
│   - Lot                      ││   - InspectieCalitate (AQL)  ││   - NIR (Nota Receptie)      │
│ • Lifecycle:                 ││   - RaportNeconformitate(NCR)││   - OrdinPlata               │
│   Solicitat -> Confirmat ->  ││ • Features:                  ││ • Features:                  │
│   InTranzit -> Receptionat   ││   - ISO 2859-1 Sampling      ││   - 3-Way Match (PO/NIR/Fact)│
│ • Atomic Cascade Cancellation││   - Monthly Scorecards (40/40││   - Stripe Payment Gateway   │
│ • Lead Time Estimation (<300ms││   - Automatic Downgrades     ││   - Four-Eyes Principle      │
└──────────────────────────────┘└──────────────────────────────┘└──────────────────────────────┘
```

---

## 🛠️ 2. Technology Stack

- **Platform:** Java 21 (Microsoft OpenJDK / Eclipse Temurin)
- **Framework:** Spring Boot 3.3.5 (Web, Data JPA, Security, Actuator, Validation)
- **Database & Migrations:** PostgreSQL 16, Flyway (`V1__init_schema.sql` to `V4__add_missing_tables.sql`)
- **Messaging:** Transactional Outbox Pattern with RabbitMQ integration
- **External Payment Gateway:** Stripe Java SDK (`StripeBancaPlataAdapter`) with Webhooks
- **Content Negotiation:** Dual JSON / XML via Jackson and JAXB annotations
- **HTTP Client:** Apache HttpClient 5 (sync/async)
- **Testing & Quality:**
  - JUnit 5 & Mockito
  - Cucumber 7 (BDD Gherkin features)
  - TNG ArchUnit 1.3 (Architectural Fitness Functions)
  - Testcontainers (PostgreSQL)
  - JaCoCo (Code coverage analysis)

---

## 📋 3. Business Rules & SLA Performance Matrix (Project_OOAD Table 1.2)

| Rule / SLA ID | Domain Rule Description | Implementation & Pattern | Verification Test |
| :--- | :--- | :--- | :--- |
| **BR-PO-01** | Orders only permitted with `Aprobat` or `Conditionat` suppliers | Hexagonal Port `IGestiuneFurnizoriAdapter`, 422 Unprocessable Entity | `CreeazaComandaUnitTest`, BDD `creeaza_comanda.feature` |
| **NFR-SLA-04** | Atomic cascade cancellation of order and unreceived lots | Aggregate boundary method `ComandaAprovizionare.anuleazaComanda()` | `CascadeCancellationIntegrationTest`, BDD `anulare_in_cascada.feature` |
| **BR-SQ-02** | Monthly supplier score (40% OTIF, 40% Quality, 20% Price) | Domain service `Scor.ponderat()`, auto downgrade/rehabilitation | `ComputationRulesTest`, BDD `scorecard_lunar.feature` |
| **BR-SQ-04/05** | ISO 2859-1 AQL inspection; NCR opened on non-conformance | State Pattern on `InspectieCalitate`, automated `RaportNeconformitate` | `WorkflowRulesTest`, BDD `executa_inspectie.feature` |
| **NFR-SLA-02** | Idempotent Goods Receipt Note (NIR) generation | Idempotent consumer checking `NIRRepo.existaNIRPentruEvent` | `PaymentWorkflowIntegrationTest`, `IdempotentNIRTest` |
| **NFR-SLA-05** | 3-Way Match Reconciliation with $\pm 2\%$ tolerance margin | `ReconciliereService.reconciliaza()` verifying PO vs NIR vs Invoice | `PerformanceAndSlaTest` (5.000 invoices in $<60\text{ s}$) |
| **NFR-SLA-06** | Four-Eyes Approval Principle & Bank Transmission threshold | Approval by distinct user; auto-approval under $\le 10.000\text{ EUR}$ | `ControlFinanciarUnitTest`, `PaymentWorkflowIntegrationTest` |
| **NFR-SLA-01** | Lead time forecasting SLA $<300\text{ ms}$ on 1.000 records | `EstimareLeadTimeService` execution benchmark | `PerformanceAndSlaTest` ($<300\text{ ms}$) |

---

## 💳 4. Stripe Payment Integration & Bank Webhooks

The sub-domain `facturi` communicates with banking systems through the `IBancaPlataPort` adapter (`StripeBancaPlataAdapter`):

1. **Auto-Approval ($\le 10.000\text{ EUR}$):** Transmitted directly to Stripe `PaymentIntent`.
2. **Four-Eyes Principle ($> 10.000\text{ EUR}$):** Requires approval by an independent finance officer (`OrdinPlata.aproba(UUID)`).
3. **Supplier Sanction Check (`BR-IP-08`):** Payments to `Blocat` or sanctioned suppliers are instantly rejected before Stripe invocation.
4. **Cryptographic Stripe Webhook Verification (ADR-010):**
   - Endpoints: POST /api/ordine-plata/webhook-banca and POST /api/plati/webhook-banca
   - **Signature Verification:** Enforces mandatory Stripe-Signature header verification using com.stripe.net.Webhook.constructEvent(payload, sigHeader, secret, 300).
   - **Replay Attack Protection:** Enforces 300-second timestamp tolerance.
   - **Audit Trail:** Every successful or rejected webhook call is persisted in shared_audit_log.
   - **Idempotency:** Repeated callbacks for already executed payment orders are safely handled without re-triggering transitions.
   - Handles payment_intent.succeeded -> records execution (I7, I7b), transitions invoice to Platita or PartialPlatita.
   - Handles payment_intent.payment_failed -> records bank rejection (I7a), keeps invoice open for reconciliation.

---

## 📬 5. Enterprise RabbitMQ Messaging & DLQ Strategy (ADR-004)

- **Topic Exchange:** supplychain.events.exchange
- **Dead-Letter Exchange (DLX):** supplychain.events.dlx
- **Dead-Letter Queue (DLQ):** supplychain.events.dlq (routing dlq.#)
- **Bounded Context Queues:**
  - comenzi.events.queue (routing comenzi.#, DLX enabled)
  - calitate.events.queue (routing calitate.#, DLX enabled)
  - acturi.events.queue (routing acturi.#, DLX enabled)

---

## 🚀 5. Getting Started & Running

### Prerequisites
- Java 21 JDK
- Docker & Docker Compose (optional for local infrastructure)
- Maven 3.9+ (or use the included `mvn.bat` / `./mvnw`)

### Running Locally with PostgreSQL & RabbitMQ
```powershell
# 1. Start database and message broker
docker-compose up -d postgres rabbitmq

# 2. Run application using Maven
.\mvn.bat spring-boot:run
```

The application will start on port `8080`.
- **Healthcheck:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **OpenAPI Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Full Containerized Execution (Docker Compose)
```powershell
docker-compose up --build -d
```

### Traditional Tomcat Deployment
`ro.uaic.feaa.sdbis.supplychain.WebInitializer` extends `SpringBootServletInitializer`, allowing the application to be deployed as a standard `.war` on Apache Tomcat 10+.

---

## 🧪 6. Testing & Quality Verification

Run the entire suite of 96 automated tests (Unit, Integration, BDD, Performance, ArchUnit):

```powershell
.\mvn.bat test
```

### Test Suite Structure:
- **`ro.uaic.feaa.sdbis.supplychain.architecture`:**
  - `AggregateEncapsulationTest` (enforces package-private mutation rules on Aggregates)
  - `BoundedContextIsolationTest` (validates domain layer zero-leakage)
  - `DependencyCycleTest` (enforces strict acyclic packages)
  - `DomainIsolationTest` & `LayerArchitectureTest`
  - `NamingConventionTest` (ensures `*Service`, `*Ctrl`, `*Command`, `*DTO` conventions)
- **`ro.uaic.feaa.sdbis.supplychain.bdd`:**
  - `BddFeaturesTest` executing all 6 Cucumber feature files (11 scenarios, 46 steps)
- **`ro.uaic.feaa.sdbis.supplychain.performance`:**
  - `PerformanceAndSlaTest` (lead time $<300\text{ ms}$, batch 5.000 reconciliation $<60\text{ s}$)
  - `ApacheHttpClientContentNegotiationTest` (dual JSON & XML content negotiation via HttpClient 5)
- **`ro.uaic.feaa.sdbis.supplychain.integration`:**
  - `PaymentWorkflowIntegrationTest` (Stripe adapter, auto-approval, Four-Eyes, NIR idempotency)

---

## 📬 7. Postman API Collection

A complete Postman collection is included in:
[`tools/postman/SupplyChain_P2P_Collection.json`](tools/postman/SupplyChain_P2P_Collection.json)

It covers:
1. `Comenzi Aprovizionare` (Create PO, Confirm, Cascade Cancel)
2. `Gestiune Furnizori & Calitate` (Create Supplier, Approve, AQL Inspection, NCR)
3. `Facturi, NIR & 3-Way Match` (Dual JSON/XML Invoice Registration, NIR, Reconciliation)
4. `Plati, Ordin de Plata & Stripe Webhooks` (Payment Intent, Bank Webhook Succeeded/Failed)
5. `Actuator & OpenAPI Documentation`
