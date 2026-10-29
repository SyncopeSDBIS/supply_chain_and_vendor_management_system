# 02. SYSTEM OVERVIEW & C4 ARCHITECTURAL CONTEXT
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. SYSTEM CONTEXT (C4 LEVEL 1)

The Procure-to-Pay platform acts as the operational core connecting enterprise staff, external suppliers, banking networks, and event-driven enterprise service buses.

```mermaid
C4Context
    title System Context Diagram (C4 Level 1) - Procure-to-Pay Platform

    Person(buyer, "Procurement Officer", "Creates purchase orders, negotiates pricing, tracks delivery progress.")
    Person(inspector, "Quality Inspector", "Conducts ISO 2859-1 AQL inspections, logs defect counts, issues NCRs.")
    Person(finance, "Finance Officer", "Approves invoices, validates 3-way matching, authorizes payment orders.")
    Person(vendor, "Supplier Representative", "Receives POs, confirms delivery dates, provides commercial invoices.")

    System(p2p, "Procure-to-Pay (P2P) Core System", "Orchestrates order commitments, quality inspections, accounts payable, and supplier evaluations.")

    System_Ext(stripe, "Stripe Banking Rail", "External payment infrastructure handling bank transfers and webhook callbacks.")
    System_Ext(rabbitmq, "RabbitMQ Enterprise Broker", "Asynchronous message broker distributing integration domain events across the enterprise.")
    SystemDb_Ext(postgres, "PostgreSQL Database", "ACID compliant relational database maintaining operational and audit event state.")

    Rel(buyer, p2p, "Manages Orders & Forecasts", "REST / JSON")
    Rel(inspector, p2p, "Records AQL Verdicts & NCRs", "REST / JSON")
    Rel(finance, p2p, "Reconciles & Approves Payments", "REST / JSON / XML")
    Rel(vendor, p2p, "Sends Invoices & Delivery Confirmations", "REST / JSON")

    Rel(p2p, stripe, "Dispatches Payment Intents", "HTTPS / REST SDK")
    Rel(stripe, p2p, "Delivers Webhook Callbacks", "HTTPS / HMAC Signed Webhook")
    Rel(p2p, rabbitmq, "Publishes Domain Integration Events", "AMQP 0-9-1")
    Rel(p2p, postgres, "Reads/Writes Operational & Outbox Data", "JDBC / JPA")
```

---

## 2. SYSTEM CONTAINER TOPOLOGY (C4 LEVEL 2)

Within the monolithic runtime, logical boundaries partition duties into distinct execution components sharing a single JVM process while isolating operational concerns.

```mermaid
C4Container
    title Container Diagram (C4 Level 2) - Modular Monolith Runtime

    Person(user, "Enterprise User", "Procurement, Quality, Finance")

    Container_Boundary(monolith, "Supply Chain Monolith (JVM 21)")
        Container(web, "REST Presentation Layer", "Spring Web MVC", "Exposes JSON/XML endpoints, handles content negotiation, enforces JSR-380 validation.")
        Container(security, "Security & Audit Filter", "Spring Security / RBAC", "Enforces Four-Eyes checks, logs access to shared_audit_log, verifies signatures.")
        Container(comenzi_mod, "Comenzi Module", "Spring Modulith", "Manages purchase order lifecycle, lines, lots, and cascade cancellations.")
        Container(calitate_mod, "Calitate Module", "Spring Modulith", "Manages supplier records, AQL inspections, NCRs, and monthly scorecards.")
        Container(facturi_mod, "Facturi Module", "Spring Modulith", "Manages invoices, 3-way matching, NIR generation, and Stripe payment adapters.")
        Container(outbox_disp, "Outbox Dispatcher", "Spring Task Scheduling", "Polls unpublished outbox events and dispatches to RabbitMQ topics.")
    Container_Boundary_End()

    ContainerDb(db, "Primary Relational Database", "PostgreSQL 16", "Stores normalized domain tables, indexes, outbox/inbox journals.")
    Container(broker, "Enterprise Message Broker", "RabbitMQ 3.12", "Topic exchange routing to domain queues and dead-letter queue (DLQ).")

    Rel(user, web, "Submits Requests", "HTTPS")
    Rel(web, security, "Filters & Audits", "In-Process")
    Rel(security, comenzi_mod, "Routes Requests", "In-Process Call")
    Rel(security, calitate_mod, "Routes Requests", "In-Process Call")
    Rel(security, facturi_mod, "Routes Requests", "In-Process Call")

    Rel(comenzi_mod, db, "Persists State", "Spring Data JPA")
    Rel(calitate_mod, db, "Persists State", "Spring Data JPA")
    Rel(facturi_mod, db, "Persists State", "Spring Data JPA")

    Rel(comenzi_mod, db, "Saves Events", "shared_outbox")
    Rel(calitate_mod, db, "Saves Events", "shared_outbox")
    Rel(facturi_mod, db, "Saves Events", "shared_outbox")

    Rel(outbox_disp, db, "Polls Unpublished", "JDBC")
    Rel(outbox_disp, broker, "Publishes AMQP Messages", "AMQP 0-9-1")
```

---

## 3. SUBSYSTEM INVENTORY & CORE RESPONSIBILITIES

### A. Subsystem: Comenzi (Purchasing)
- **Primary Aggregate:** `ComandaAprovizionare` (Root) encapsulating collection `LinieComanda` and `Lot`.
- **Key Responsibilities:**
  - Authoring and updating purchase orders.
  - Tracking supplier promised delivery dates.
  - Cascading cancellations: When an order is cancelled, all pending uninspected lots are transitioned to `ANULAT` atomically.
  - Algorithmic lead time forecasting benchmarked to complete under $300\text{ ms}$ across $1,000$ historical orders.

### B. Subsystem: Calitate (Quality Assurance & Vendor Governance)
- **Primary Aggregates:** `Furnizor`, `InspectieCalitate`, `ScorecardLunar`, `InspectorCalitate`.
- **Key Responsibilities:**
  - Supplier qualification lifecycle: `CANDIDAT` $\rightarrow$ `CALIFICAT` $\rightarrow$ `APROBAT` (or `CONDITIONAT`, `BLOCAT`).
  - ISO 2859-1 (AQL) statistical inspection execution: Validates sample sizes and defect thresholds ($Ac$, $Re = Ac + 1$).
  - Non-Conformance Report (NCR) workflows: Root cause analysis, corrective action plans, escalation.
  - Monthly supplier evaluation scorecards:
    $$\text{Total Score} = 0.40 \times \text{OTIF} + 0.40 \times \text{Quality} + 0.20 \times \text{Price Compliance}$$
    Triggers automatic rehabilitation or immediate suspension/blocking upon 3 consecutive non-compliant lots.

### C. Subsystem: Facturi (Accounts Payable & Financial Integration)
- **Primary Aggregates:** `FacturaFurnizor`, `NIR` (Reception Note), `OrdinPlata`.
- **Key Responsibilities:**
  - Reception processing: Generating Goods Receipt Notes (`NIR`) upon lot arrival.
  - 3-Way Matching (`ReconciliereService`): Reconciles Purchase Order vs. Reception Note vs. Supplier Invoice within a $\pm 2\%$ tolerance window. Discrepancies lock invoices into `BLOCATA_DISCREPANTE`.
  - Payment Order lifecycle: Creates `OrdinPlata`, enforces Four-Eyes approval for amounts $>10,000\text{ EUR}$.
  - Stripe Sandbox/Production Banking Gateway: Connects to Stripe `PaymentIntent`, verifies incoming webhooks using HMAC-SHA256, and finalizes invoice payments idempotently.

---

## 4. NON-FUNCTIONAL BASELINE & SERVICE LEVEL TARGETS

| SLA / NFR Code | Operational Characteristic | Technical Implementation & Target Metric | Verification Method |
| :--- | :--- | :--- | :--- |
| **NFR-SLA-01** | Lead Time Calculation Speed | Executed in $<300\text{ ms}$ over 1,000 historical records | `PerformanceAndSlaTest` |
| **NFR-SLA-02** | Goods Receipt Idempotency | Duplicate reception events produce 0 redundant NIR records | `PaymentWorkflowIntegrationTest` |
| **NFR-SLA-04** | Atomic Cascade Cancellation | Synchronous aggregate rollback in a single database transaction | `CascadeCancellationIntegrationTest` |
| **NFR-SLA-05** | Batch Reconciliation Speed | Reconciles 5,000 invoices in $<60\text{ seconds}$ | `PerformanceAndSlaTest` |
| **NFR-SLA-06** | Four-Eyes Financial Control | Strict rejection if `aprobatDe == creatDe` for sums $>10,000\text{ EUR}$ | `ControlFinanciarUnitTest` |
| **SEC-WH-01** | Webhook Cryptographic Authenticity | Mandatory `Stripe-Signature` validation, 300s replay window | `FacturaCtrlRestTest` |

