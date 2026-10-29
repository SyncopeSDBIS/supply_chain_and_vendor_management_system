# 35. EXHAUSTIVE DOMAIN, SERVICE & ADAPTER CLASS REFERENCE
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. PURCHASING & PURCHASE ORDERS MODULE (`comenzi`)

### 1.1 Domain Aggregates, Entities & Value Objects

#### `ComandaAprovizionare`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "comenzi_comenzi")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.domain`
- **Responsibility:** Root of the Purchasing aggregate. Encapsulates order lifecycle, line items, and lot deliveries.
- **Key Fields:** `UUID id`, `String numarComanda`, `UUID furnizorId`, `StareComanda stare`, `LocalDate dataEmitere`, `LocalDate dataLivrareSolicitata`, `Suma valoareTotala`, `List<LinieComanda> linii`, `List<LotLivrare> loturi`.
- **Key Invariants:** Cannot add line items if order is `EMISA` or `FINALIZATA`; total order value equals the sum of line totals; cancelling an order cascades cancellation across all non-delivered lots.

#### `LinieComanda`
- **Stereotype:** `@DomainEntity`, `@Entity`, `@Table(name = "comenzi_linii_comanda")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.domain`
- **Responsibility:** Child entity representing an individual ordered item, quantity, and unit price.
- **Key Fields:** `Long id`, `Integer numarLinie`, `CodArticol codArticol`, `Cantitate cantitate`, `PretUnitar pretUnitar`, `Suma subtotal`.

#### `LotLivrare`
- **Stereotype:** `@DomainEntity`, `@Entity`, `@Table(name = "comenzi_loturi")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.domain`
- **Responsibility:** Child entity tracking physical warehouse reception batches for an order.
- **Key Fields:** `UUID id`, `String numarLot`, `LocalDate dataLivrare`, `Integer cantitateReceptionata`, `StareLot stare`.

#### Value Objects (`comenzi.domain`)
- **`CodArticol`:** `@ValueObject`, immutable SKU identifier with validation regex.
- **`Cantitate`:** `@ValueObject`, positive numeric integer representing physical units.
- **`PretUnitar`:** `@ValueObject`, monetary price per unit with currency.
- **`StareComanda`:** `@ValueObject` enum (`INITIATA`, `EMISA`, `CONFIRMATA`, `LIVRATA_PARTIAL`, `FINALIZATA`, `ANULATA`).

---

### 1.2 Services & Repositories (`comenzi`)

#### `ComandaApplicationService`
- **Stereotype:** `@ApplicationService`, `@Service`, `@Transactional`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.application`
- **Responsibility:** Orchestrates order use cases: verifies supplier qualification via `ICalitateModuleApi`, persists orders, dispatches events to `shared_outbox_events`.

#### `ComandaQueryService`
- **Stereotype:** `@QueryService`, `@Service`, `@Transactional(readOnly = true)`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.application.query`
- **Responsibility:** CQRS read service providing fast read-only projections and DTO summaries.

#### `EstimareLeadTimeService`
- **Stereotype:** `@DomainComputationService`, pure Java calculation
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation`
- **Responsibility:** Calculates historical supplier delivery lead time and variance using linear regression.

#### `ComandaCtrl`
- **Stereotype:** `@RestController`, `@RequestMapping("/api/comenzi")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.comenzi.presentation`
- **Responsibility:** REST controller exposing endpoints for order creation, confirmation, cancellation, and lead time queries.

---

## 2. QUALITY ASSURANCE & VENDOR MODULE (`calitate`)

### 2.1 Domain Aggregates & Entities

#### `Furnizor`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "calitate_furnizori")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.calitate.domain`
- **Responsibility:** Root aggregate for vendor profile, qualification state, and performance tier.
- **Key Fields:** `UUID id`, `CodFiscal cif`, `DenumireFurnizor denumire`, `Iban iban`, `StareCalificare stare`, `Integer loturiNeconformeConsecutive`.
- **Key Invariants:** Supplier is automatically blocked upon reaching 3 consecutive non-compliant delivery lots.

#### `InspectieCalitate`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "calitate_inspectii")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.calitate.domain`
- **Responsibility:** Quality inspection aggregate executing statistical ISO 2859-1 (AQL) lot evaluation.
- **Key Fields:** `UUID id`, `UUID lotId`, `UUID furnizorId`, `Integer marimeLot`, `Integer marimeEsantion`, `Integer defecteCritice`, `Integer defecteMajore`, `VerdictInspectie verdict`.
- **Key Invariants:** Verdict `RESPINS` automatically triggers creation of a Non-Conformance Report (`RaportNeconformitate`).

#### `RaportNeconformitate (NCR)`
- **Stereotype:** `@DomainEntity`, `@Entity`, `@Table(name = "calitate_ncr")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.calitate.domain`
- **Responsibility:** Formal deviation document tracking root cause analysis and supplier corrective actions.

#### `ScorecardLunar`
- **Stereotype:** `@DomainEntity`, `@Entity`, `@Table(name = "calitate_scorecards")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.calitate.domain`
- **Responsibility:** Monthly vendor evaluation: weighted composite score ($40\%\text{ OTIF} + 40\%\text{ Quality} + 20\%\text{ Price}$).

---

### 2.2 Services & Adapters (`calitate`)

#### `CalitateApplicationService`
- **Stereotype:** `@ApplicationService`, `@Service`, `@Transactional`
- **Responsibility:** Manages vendor registration, audits, inspection verdict recordings, and NCR generation.

#### `CalculScorecardService`
- **Stereotype:** `@DomainComputationService`
- **Responsibility:** Pure mathematical calculation of monthly vendor performance ratings.

#### `CalitateCtrl`
- **Stereotype:** `@RestController`, `@RequestMapping("/api")`
- **Responsibility:** REST controller for `/api/furnizori`, `/api/inspectii`, and `/api/calitate`.

---

## 3. INVOICING & ACCOUNTS PAYABLE MODULE (`facturi`)

### 3.1 Domain Aggregates & Entities

#### `Factura`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "facturi_facturi")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.facturi.domain`
- **Responsibility:** Invoicing aggregate managing supplier claims, tax breakdowns, and 3-way match reconciliation.
- **Key Fields:** `UUID id`, `String serieSiNumar`, `UUID furnizorId`, `UUID comandaId`, `LocalDate dataEmiterii`, `LocalDate dataScadentei`, `Suma valoareTotala`, `Suma totalTva`, `StareFactura stare`, `List<LinieFactura> linii`.

#### `NotaReceptie (NIR)`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "facturi_nir")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.facturi.domain`
- **Responsibility:** Formal warehouse goods receipt note providing the quantitative basis for 3-way matching.
- **Key Fields:** `UUID id`, `String numarNir`, `UUID comandaId`, `UUID lotId`, `LocalDate dataReceptiei`, `List<LinieNIR> linii`.

#### `OrdinPlata`
- **Stereotype:** `@AggregateRoot`, `@Entity`, `@Table(name = "facturi_ordine_plata")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.facturi.domain`
- **Responsibility:** Payment order aggregate governing bank disbursements.
- **Key Fields:** `UUID idOrdin`, `UUID facturaId`, `Suma suma`, `UUID creatDe`, `UUID aprobatDe`, `OrdinPlataStatus status`, `String referintaBancara`.
- **Key Invariants:** Four-Eyes Principle (`aprobatDe != creatDe`); payment sum cannot exceed unpaid invoice balance.

---

### 3.2 Services & Adapters (`facturi`)

#### `FacturaApplicationService`
- **Stereotype:** `@ApplicationService`, `@Service`, `@Transactional`
- **Responsibility:** Invoice creation, 3-way matching orchestration, and payment order lifecycle.

#### `StripeBancaPlataAdapter`
- **Stereotype:** `@Component`, implements `IBancaPlataProvider`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure`
- **Responsibility:** Hexagonal outbound adapter communicating with Stripe API (`PaymentIntent`) and sandbox fallbacks.

#### `FacturaCtrl`
- **Stereotype:** `@RestController`, `@RequestMapping("/api")`
- **Location:** `ro.uaic.feaa.sdbis.supplychain.facturi.presentation`
- **Responsibility:** Dual JSON/XML invoice ingestion, payment order approvals, and cryptographic Stripe webhook callbacks.

---

## 4. SHARED KERNEL & INFRASTRUCTURE CLASSES

| Class Name | Stereotype / Category | Package | Purpose |
|---|---|---|---|
| `Suma` | `@ValueObject` | `shared` | Currency and numeric amount value object with math operations. |
| `Moneda` | `@ValueObject` | `shared` | ISO 4217 Currency (`RON`, `EUR`, `USD`). |
| `AuditLogEntry` | `@Entity` | `shared` | Immutable audit log record persisted in `shared_audit_log`. |
| `JurnalAuditService` | `@Service` | `securitate` | Implementation of `IJurnalAudit` with `REQUIRES_NEW` propagation. |
| `RbacSecurityFilter` | `Filter` | `securitate` | Zero-Trust RBAC filter inspecting role and identity headers. |
| `OutboxEventEntity` | `@Entity` | `shared` | Relational entity storing serialized events in `shared_outbox_events`. |
| `InboxEventEntity` | `@Entity` | `shared` | Relational entity tracking processed messages in `shared_inbox_events`. |
| `OutboxDispatcherService`| `@Service` | `infrastructure.messaging` | Polling dispatcher publishing events to Spring Modulith and RabbitMQ. |
| `AmqpEventPublisher` | `@Component` | `shared.messaging` | Routes domain events to `supplychain.events.exchange` by prefix. |
| `RabbitMqConfig` | `@Configuration` | `shared.messaging` | Declares exchange, topic queues, DLX, and DLQ. |
| `AbstractRepositoryJPA` | Generic DAO | `shared.persistence` | Type-safe JPA repository foundation providing entity manager methods. |

