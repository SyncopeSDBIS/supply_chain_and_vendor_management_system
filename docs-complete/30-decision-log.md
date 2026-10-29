# 30. ARCHITECTURAL DECISION LOG (ADR-001 TO ADR-015)
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. ARCHITECTURAL DECISION GOVERNANCE

All fundamental structural, technological, design, and integration decisions governing the platform are formalized through **Architectural Decision Records (ADR)**. Each ADR captures the business context, alternatives evaluated, decision rationale, and architectural consequences.

```mermaid
graph TD
    subgraph ADR_Timeline["Architectural Decision Timeline"]
        ADR1["ADR-001: Modular Monolith"] --> ADR2["ADR-002: Clean Architecture & DDD"]
        ADR2 --> ADR3["ADR-003: Flyway & DB Governance"]
        ADR3 --> ADR4["ADR-004: RabbitMQ & Event Bus"]
        ADR4 --> ADR5["ADR-005: ArchUnit Fitness Functions"]
        ADR5 --> ADR6["ADR-006: Dual Content Negotiation"]
        ADR6 --> ADR7["ADR-007: Zero-Trust RBAC"]
        ADR7 --> ADR8["ADR-008: Immutability & JPA Proxies"]
        ADR8 --> ADR9["ADR-009: ISO 2859-1 AQL Sampling"]
        ADR9 --> ADR10["ADR-010: Stripe Webhook & Bank Clearing"]
        ADR10 --> ADR11["ADR-011: Transactional Outbox & Inbox"]
        ADR11 --> ADR12["ADR-012: Four-Eyes Principle"]
        ADR12 --> ADR13["ADR-013: Spring Modulith Encapsulation"]
        ADR13 --> ADR14["ADR-014: Defense-in-Depth Validation"]
        ADR14 --> ADR15["ADR-015: Append-Only Audit Logging"]
    end
```

---

## 2. EXHAUSTIVE ADR CATALOG

### ADR-001: Modular Monolith vs Distributed Microservices
- **Status:** Accepted
- **Context:** P2P supply chain workflows require strict transactional consistency, high cohesion between purchasing, quality inspection, and invoicing, and simple local developer ergonomics.
- **Decision:** Adopt a **Modular Monolith** over microservices for initial evolution. Modules (`comenzi`, `calitate`, `facturi`) run within a single JVM process but maintain strict code and schema boundaries.
- **Consequences:** Eliminates network serialization latency and distributed transaction complexity (2PC / Sagas); establishes clean module boundaries that enable seamless microservice extraction in future phases.

---

### ADR-002: Clean Architecture & DDD Tactical Patterns
- **Status:** Accepted
- **Context:** Enterprise business logic must remain pure, highly testable, and independent of frameworks, web servlets, and database technologies.
- **Decision:** Implement **Clean Architecture** (concentric layers: Presentation $\rightarrow$ Application $\rightarrow$ Domain $\leftarrow$ Infrastructure) and **DDD Tactical Patterns** (`@AggregateRoot`, `@DomainEntity`, `@ValueObject`, `@DomainRepository`, `@DomainService`).
- **Consequences:** Framework independence; business rules can be unit tested without starting Spring or relational databases; dependencies strictly point inwards toward the Domain model.

---

### ADR-003: Database Governance via Flyway & Hibernate DDL Validation
- **Status:** Accepted
- **Context:** Automated Hibernate schema generation (`hbm2ddl.auto=update`) leads to uncontrolled schema drift, unindexed foreign keys, and production data corruption.
- **Decision:** Use **Flyway** for 100% of schema mutations (`V1__...` to `V6__...`). Configure Hibernate to `validate` mode in all production and PostgreSQL environments.
- **Consequences:** Deterministic, reproducible database state across staging and production; schema changes are reviewed as first-class code in version control.

---

### ADR-004: RabbitMQ Topic Exchange for Cross-Subsystem Messaging
- **Status:** Accepted
- **Context:** B2B supply chain operations (supplier notification, warehouse notification, ERP integration) require asynchronous, decoupled message delivery.
- **Decision:** Standardize on **RabbitMQ 3.12** using a central topic exchange (`supplychain.events.exchange`) and dead-letter exchange (`supplychain.events.dlx`).
- **Consequences:** Asynchronous scaling, message persistence, decoupled subsystem deployment, and resilient error recovery via poison-pill dead-letter queues.

---

### ADR-005: Automated Architectural Fitness Functions with ArchUnit
- **Status:** Accepted
- **Context:** Code rot, cyclic package dependencies, and architectural layer violations often occur silently during agile team iterations.
- **Decision:** Embed **ArchUnit 1.3.0** fitness tests directly in the Maven build lifecycle and CI/CD pipeline.
- **Consequences:** Immediate compile/test failure if any developer violates layer rules, package boundaries, or immutability contracts.

---

### ADR-006: Dual Content Negotiation (JSON & XML) for Enterprise B2B
- **Status:** Accepted
- **Context:** Procurement and financial invoicing frequently integrate with legacy B2B EDI systems (which transmit XML / UBL) and modern SPA web applications (which consume JSON).
- **Decision:** Implement dual content negotiation via Jackson (`jackson-dataformat-xml` and `jackson-databind`), honoring `Accept: application/json`, `Accept: application/xml`, and `?format=json|xml`.
- **Consequences:** Universal interoperability without maintaining redundant REST controllers.

---

### ADR-007: Stateless REST Security & Zero-Trust RBAC
- **Status:** Accepted
- **Context:** The system must comply with NIS2 cybersecurity requirements, prevent privilege escalation, and operate without HTTP session state.
- **Decision:** Implement Spring Security with `SessionCreationPolicy.STATELESS` and a custom `RbacSecurityFilter` inspecting `X-User-Role` and `X-User-Id` headers.
- **Consequences:** Horizontally scalable stateless endpoints; granular Least Privilege enforcement returning HTTP 403 Forbidden on role mismatch.

---

### ADR-008: Value Object Immutability & Proxy-Safe JPA Equality
- **Status:** Accepted
- **Context:** Mutable domain value objects cause insidious state corruption; default `equals()` and `hashCode()` implementations break under Hibernate lazy proxy loading.
- **Decision:** Enforce immutable value objects with `final` fields; implement proxy-safe entity equality using `Hibernate.getClass(this)` and stable business UUID identifiers.
- **Consequences:** Thread-safe domain calculations; zero `ClassCastException` or hash collision bugs in Hibernate session caches.

---

### ADR-009: ISO 2859-1 (AQL) Statistical Acceptance Sampling
- **Status:** Accepted
- **Context:** Inspecting 100% of received units in large supply chain deliveries is financially prohibitive and operationally unfeasible.
- **Decision:** Formalize statistical lot sampling in `InspectieCalitate` based on **ISO 2859-1 (AQL)**, defining sample sizes, acceptance limits ($Ac$), and rejection limits ($Re$).
- **Consequences:** Statistically sound quality assurance with predictable producer/consumer risk levels.

---

### ADR-010: Stripe Banking Clearing Simulation & HMAC Verification
- **Status:** Accepted
- **Context:** Simulating real-world bank clearing requires asynchronous webhook callbacks, idempotency, and cryptographic non-repudiation.
- **Decision:** Integrate Stripe SDK (`stripe-java 26.1.0`), using `PaymentIntent` for outbound clearing and mandatory `Stripe-Signature` HMAC-SHA256 verification (with 300s replay tolerance) on incoming webhooks.
- **Consequences:** Robust, secure banking simulation immune to webhook spoofing and replay attacks.

---

### ADR-011: Transactional Outbox & Idempotent Inbox Pattern
- **Status:** Accepted
- **Context:** Dual-write hazards between PostgreSQL commits and RabbitMQ publications can cause message loss or phantom event dispatch.
- **Decision:** Implement the **Transactional Outbox Pattern** (`shared_outbox_events` polled by `OutboxDispatcherService`) and the **Idempotent Inbox Pattern** (`shared_inbox_events` keyed by `consumer:eventId`).
- **Consequences:** Guaranteed at-least-once message dispatch; guaranteed exactly-once consumer execution.

---

### ADR-012: Four-Eyes Principle for Financial Disbursements
- **Status:** Accepted
- **Context:** Preventing internal fraud and embezzlement during supplier payment order approvals.
- **Decision:** Enforce the **Four-Eyes Principle (BR-FAC-03)** in the `OrdinPlata` aggregate root: the user approving a payment order must be strictly distinct from the user who created it (`aprobatorId != creatDe`).
- **Consequences:** Domain-level fraud prevention that cannot be bypassed by presentation controllers.

---

### ADR-013: Spring Modulith Package Encapsulation & Metamodel
- **Status:** Accepted
- **Context:** Java package-private visibility alone is insufficient to prevent cross-module coupling in large enterprise codebases.
- **Decision:** Adopt **Spring Modulith 1.2.4**, defining explicit module boundaries via `package-info.java` (`@ApplicationModule`) and public `api` subpackages.
- **Consequences:** Automated metamodel verification via `ApplicationModules.of(SupplyChainApplication.class).verify()`.

---

### ADR-014: Defense-in-Depth Multi-Tier Validation
- **Status:** Accepted
- **Context:** Malformed or corrupt data must be rejected at the earliest possible stage with precise error diagnostics.
- **Decision:** Implement multi-tier validation: Tier 1 (JSR-380 at Controller DTOs), Tier 2 (Domain aggregate invariant guards), Tier 3 (Relational database check constraints, unique indexes, and foreign keys).
- **Consequences:** Comprehensive data integrity; impossible to persist invalid state even if one defensive layer is compromised.

---

### ADR-015: Append-Only Immutable Audit Trail with Isolated Transactions
- **Status:** Accepted
- **Context:** Regulatory compliance (NIS2, GDPR, SOX) requires an tamper-evident audit trail of all security and financial events.
- **Decision:** Implement `JurnalAuditService` persisting records into `shared_audit_log` using `@Transactional(propagation = Propagation.REQUIRES_NEW)`.
- **Consequences:** Audit log entries are committed immediately in an isolated transaction and survive even if the enclosing business operation encounters a failure or rollback.

