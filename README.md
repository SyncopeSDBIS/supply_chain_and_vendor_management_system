# Supply Chain and Vendor Management System (Procure-to-Pay)

Enterprise-grade **Procure-to-Pay (P2P)** system implementing **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, and **Event-Driven Architecture (EDA)** with guaranteed atomicity through the **Transactional Outbox / Idempotent Inbox** patterns. Fully integrated with **RabbitMQ** for asynchronous messaging and **Stripe** for payment execution.

Strictly aligned 1:1 with academic and enterprise specifications from **Project_OOAD.docx** and course milestones across Sprints 1–4, emphasizing the Rich Domain Model of **Sprint 2 (ADR-003)**.

---

## 📑 Table of Contents
1. [Quick Start Guide](#-1-quick-start-guide)
   - [Method A: Full Containerized Execution via Docker Compose (Recommended)](#method-a-full-containerized-execution-via-docker-compose-recommended)
   - [Method B: Hybrid Local Development (Postgres & RabbitMQ in Docker + App Local)](#method-b-hybrid-local-development-postgres--rabbitmq-in-docker--app-local)
   - [Method C: Standalone In-Memory Execution (H2 Database, No Docker Required)](#method-c-standalone-in-memory-execution-h2-database-no-docker-required)
2. [Access Points, Ports & Default Credentials](#-2-access-points-ports--default-credentials)
3. [How to Use and Test the Application](#-3-how-to-use-and-test-the-application)
   - [Interactive Testing via Swagger UI](#31-interactive-testing-via-swagger-ui)
   - [End-to-End Testing via Postman Collection](#32-end-to-end-testing-via-postman-collection)
   - [RabbitMQ Management & AMQP Wire Broker (Ports 15672 & 5672)](#33-rabbitmq-management--amqp-wire-broker-ports-15672--5672)
   - [PostgreSQL Database Access: CLI & GUI (Port 5433)](#34-postgresql-database-access-cli--gui-port-5433)
4. [Troubleshooting & FAQ](#-4-troubleshooting--faq)
   - [Flyway Error: Migration Checksum Mismatch](#1-flyway-error-migration-checksum-mismatch)
   - [Application in Restarting or Health Starting State](#2-application-in-restarting-or-health-starting-state)
   - [Port Conflicts (8080, 5433, or 5672 Already in Use)](#3-port-conflicts-8080-5433-or-5672-already-in-use)
5. [Automated Test Execution (Unit, BDD, ArchUnit, SLA)](#-5-automated-test-execution)
6. [Architecture, Bounded Contexts & Domain Invariants](#-6-architecture-bounded-contexts--domain-invariants)
   - [Business Rules & SLA Performance Matrix](#business-rules--sla-performance-matrix-table-12)
7. [Project Documentation & Blueprints](#-7-project-documentation--blueprints)

---

## 🚀 1. Quick Start Guide

### Prerequisites
* **Docker & Docker Compose** (Docker Desktop on Windows/macOS or Docker Engine + Compose v2+ on Linux).
* *(Optional for local Maven builds without containers)*: **Java 21 JDK** and **Maven 3.9+** (or use the bundled `mvnw` / `mvnw.cmd` wrapper scripts).

---

### Method A: Full Containerized Execution via Docker Compose (Recommended)
This approach automatically provisions and connects the entire enterprise stack: Spring Boot 3.3.5 application, PostgreSQL 16 database, and RabbitMQ 3 Management message broker.

1. **Open a terminal** in the root directory of the project:
   ```bash
   # Navigate to the project root directory
   cd path/to/project-root
   ```

2. **Build and start the multi-container stack in detached mode**:
   ```bash
   docker-compose up --build -d
   ```

3. **Check container status and health**:
   ```bash
   docker-compose ps
   ```
   *Within approximately 20–30 seconds (allowing for Maven compile, Flyway migrations, and Spring Boot bootup), all three containers will report `healthy`:*
   ```text
   NAME                   IMAGE                          STATUS
   supplychain-app        app-app                        Up (healthy)   0.0.0.0:8080->8080/tcp
   supplychain-postgres   postgres:16-alpine             Up (healthy)   0.0.0.0:5433->5432/tcp
   supplychain-rabbitmq   rabbitmq:3-management-alpine   Up (healthy)   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
   ```

4. **To stop and shut down all containers**:
   ```bash
   docker-compose down
   ```

---

### Method B: Hybrid Local Development (Postgres & RabbitMQ in Docker + App Local)
Recommended for active coding and step-by-step debugging inside an IDE (IntelliJ IDEA, VS Code, or Eclipse):

1. **Start only the backing infrastructure services**:
   ```bash
   docker-compose up -d postgres rabbitmq
   ```

2. **Run the Spring Boot application locally using the `postgres` profile**:
   * **Windows (PowerShell / Command Prompt)**:
     ```powershell
     .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
     ```
   * **Linux / macOS**:
     ```bash
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
     ```

---

### Method C: Standalone In-Memory Execution (H2 Database, No Docker Required)
For lightweight local verification without Docker installed, the application provides an embedded **H2 database** configuration operating in PostgreSQL compatibility mode:

* **Windows**:
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
* **Linux / macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```
*The application starts immediately on port `8080`, holding all tables and entities transiently in RAM.*

---

## 🌐 2. Access Points, Ports & Default Credentials

Once the system is up and running, access all services via the following endpoints:

| Service / Tool | Access URL | Credentials / Notes | Purpose |
| :--- | :--- | :--- | :--- |
| **Interactive Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | *No authentication required* | Interactive API playground to inspect and execute all REST endpoints live |
| **OpenAPI Specification (JSON)** | [http://localhost:8080/api-docs](http://localhost:8080/api-docs) | *No authentication required* | OpenAPI 3.0 contract definition for client generation and tooling |
| **RabbitMQ Management Dashboard** | [http://localhost:15672](http://localhost:15672) | **User:** `guest`<br>**Password:** `guest` | Web UI to monitor exchanges, queues, consumer rates, and dead-letter channels |
| **Spring Boot Actuator Health** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | *No authentication required* | Healthcheck probes (Postgres connectivity, RabbitMQ broker, disk space) |
| **Spring Boot Actuator Metrics** | [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) | *No authentication required* | Observability metrics (JVM heap, HikariCP connection pool, HTTP request rates) |
| **PostgreSQL Database** | `localhost:5433` *(host mapped)*<br>`postgres:5432` *(internal)* | **DB:** `supplychain_db`<br>**User:** `supplychain`<br>**Password:** `supplychain_secret` | Direct database connection for SQL clients (DBeaver, pgAdmin, DataGrip) |
| **RabbitMQ AMQP Broker** | `localhost:5672` | **User:** `guest`<br>**Password:** `guest` | Native AMQP protocol wire port consumed by Spring AMQP listeners |

---

## 🧪 3. How to Use and Test the Application

### 3.1. Interactive Testing via Swagger UI
The easiest way to explore and execute business workflows directly in your browser:
1. Open **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)** in any modern web browser.
2. Endpoints are organized across the three Bounded Contexts:
   * `/api/comenzi`: Purchase order lifecycle (create, confirm, allocate physical lots, dispatch, receive, and atomic cascade cancellation).
   * `/api/furnizori` & `/api/inspectii`: Supplier onboarding, ISO 2859-1 AQL inspection allocation, non-conformance reports (NCR), and monthly scorecard calculation.
   * `/api/facturi` & `/api/nir`: Supplier invoice intake, warehouse goods receipt (NIR), automated 3-Way Matching, and payment order dispatch.
3. Click on any route (e.g. `POST /api/comenzi`), click **"Try it out"**, supply or adjust the JSON request payload, and click **"Execute"**. The server's domain calculation and response will appear immediately below.

---

### 3.2. End-to-End Testing via Postman Collection
The repository includes a ready-to-run, comprehensive Postman test suite:
📁 [`tools/postman/SupplyChain_P2P_Collection.json`](tools/postman/SupplyChain_P2P_Collection.json)

**How to import and run:**
1. Open **Postman** (or *Insomnia*).
2. Click **Import** in the top left and select `SupplyChain_P2P_Collection.json`.
3. Verify that the collection environment variable is set:
   * `baseUrl` = `http://localhost:8080`
4. The collection is arranged into 5 ordered folders reflecting real-world business scenarios:
   * **1. Comenzi Aprovizionare (UC1 - UC8)**: Order placement, supplier confirmation, lot allocation, receipt, and cascade cancellation.
   * **2. Gestiune Furnizori & Calitate (Q1 - Q8)**: Supplier registration, inspector assignment by qualification matrix, AQL sample verdict, and NCR issuance.
   * **3. Facturi, NIR & 3-Way Matching (I1 - I4)**: Warehouse NIR generation, supplier invoice submission, and automated trilateral matching with tolerance margins.
   * **4. Plăți, Ordin de Plată & Stripe Webhooks (I5 - I7)**: Payment order generation, Four-Eyes approval (> 10,000 EUR), and simulated HMAC-signed Stripe webhooks (`payment_intent.succeeded` / `payment_failed`).
   * **5. Actuator & Healthcheck**: Probing subsystem operational health.

---

### 3.3. RabbitMQ Management & AMQP Wire Broker (Ports 15672 & 5672)

The system exposes both the native **AMQP 0-9-1 wire protocol broker** and the **Management Web UI**:

#### A. Web Management Dashboard (Port 15672)
1. Open **[http://localhost:15672](http://localhost:15672)** in your browser.
2. Sign in with default credentials:
   * **Username**: `guest`
   * **Password**: `guest`
3. Navigate to the **Queues** tab to inspect background asynchronous message flow:
   * `comenzi.events.queue`: Order lifecycle events (placement, cancellation, lot status).
   * `calitate.events.queue`: Quality inspection schedules, verdicts, and supplier suspension alerts.
   * `facturi.events.queue`: 3-Way matching completion notifications and payment execution triggers.
   * `supplychain.events.dlq`: Dead-Letter Queue capturing poisoned or rejected messages.
4. Navigate to the **Exchanges** tab to inspect the topic exchanges: `supplychain.events.exchange` and `supplychain.events.dlx`.

#### B. Native AMQP Wire Broker (Port 5672)
The AMQP wire port (`5672`) is used by Spring AMQP and can be accessed by external microservices, background workers, or testing tools:
* **Connection URI**: `amqp://guest:guest@localhost:5672/`
* **Host**: `localhost` (or `127.0.0.1`)
* **AMQP Port**: `5672`
* **Virtual Host**: `/` (default)
* **Direct CLI Queue Inspection**:
  You can also inspect queues and broker status directly from your terminal using Docker:
  ```bash
  # List all declared queues and message counts
  docker exec -it supplychain-rabbitmq rabbitmqctl list_queues name messages consumers

  # List topic exchanges and bindings
  docker exec -it supplychain-rabbitmq rabbitmqctl list_exchanges

  # Check cluster / node health
  docker exec -it supplychain-rabbitmq rabbitmqctl status
  ```

---

### 3.4. PostgreSQL Database Access: CLI & GUI (Port 5433)

You have two simple methods to connect to the PostgreSQL database and inspect the 24 persisted domain and outbox tables:

#### Method A: Direct Terminal CLI via Docker (Fastest, No Client Needed)
Run the interactive PostgreSQL terminal client directly inside the running container without installing PostgreSQL locally:

```bash
docker exec -it supplychain-postgres psql -U supplychain -d supplychain_db
```

This immediately opens the interactive SQL prompt (`supplychain_db=#`). Common useful commands:

* **List all 24 tables created by Flyway migrations:**
  ```sql
  \dt
  ```
* **Inspect Purchase Orders and Lots:**
  `sql
  SELECT id_val, numar_comanda, stare_comanda, data_comanda FROM comenzi_aprovizionare;
  SELECT id_val, comanda_id, cod_lot, stare_lot, cantitate FROM comenzi_loturi;
  `
* **Inspect Suppliers and Quality Scorecards:**
  `sql
  SELECT id_val, cod_fiscal, denumire, stare_furnizor FROM calitate_furnizori;
  SELECT * FROM calitate_scorecards;
  `
* **Inspect Transactional Outbox / Inbox & Audit Trail:**
  `sql
  SELECT id, event_type, published, timestamp FROM shared_outbox;
  SELECT id, actiune, obiect, timestamp FROM shared_audit_log;
  `
* **Exit the psql session:**
  ```sql
  \q
  ```

#### Method B: Graphical SQL Clients (DBeaver, pgAdmin, DataGrip, VS Code)
If you prefer a graphical user interface, open your SQL client and configure a new **PostgreSQL** connection with:

* **Host / Server**: `localhost` *(or `127.0.0.1`)*
* **Port**: **`5433`** *(use port 5433 as mapped to your host to avoid colliding with default PostgreSQL 5432)*
* **Database**: `supplychain_db`
* **Username**: `supplychain`
* **Password**: `supplychain_secret`

Click **"Test Connection"** and **"Connect"**. You will find all 24 domain tables located under the `public` schema.

---

## 🔧 4. Troubleshooting & FAQ

### 1. Flyway Error: Migration Checksum Mismatch
**Symptom:** During startup, container `supplychain-app` fails and enters `Restarting (1)` with the following log entry:
```text
Caused by: org.flywaydb.core.api.exception.FlywayValidateException: Validate failed: Migrations have failed validation
Migration checksum mismatch for migration version 1 (or 2, 3, 4)
```
**Cause:** The persistent Docker volume (`postgres_data`) retains schema history from an earlier build, while local migration scripts in `src/main/resources/db/migration/` were modified or line endings changed (LF vs. CRLF).  
**Clean Resolution:** Wipe the stale database volume and start fresh:
```bash
docker-compose down -v
docker-compose up --build -d
```

---

### 2. Application in Restarting or Health Starting State
**How to diagnose:**  
Inspect real-time application logs to pinpoint any startup exceptions:
```bash
docker-compose logs -f app
```
Ensure that the PostgreSQL and RabbitMQ containers are already in `healthy` status before inspecting the app by checking `docker-compose ps`.

---

### 3. Port Conflicts (8080, 5433, or 5672 Already in Use)
If an existing service is already occupying port 8080 or port 5432:
* PostgreSQL in `docker-compose.yml` is pre-configured on host port **`5433`** to avoid colliding with default PostgreSQL installations on 5432.
* To override host ports dynamically, provide environment variables prior to running:
  ```bash
  POSTGRES_PORT=5435 docker-compose up -d
  ```

---

## 🧪 5. Automated Test Execution

The codebase is backed by an automated suite of over 96 tests spanning unit, architectural, BDD, and SLA benchmarks:

### Run the full test suite:
* **Windows (PowerShell / Command Prompt)**:
  ```powershell
  .\mvnw.cmd test
  ```
* **Linux / macOS**:
  ```bash
  ./mvnw test
  ```

### Included Test Layers:
1. **Domain Unit Tests (Definition of Done - Sprint 2)** (`src/test/java/.../unit`):
   * `WorkflowRulesTest`: Enforces aggregate state machine transitions and invariants (`ComandaAprovizionare`, `Furnizor`, `FacturaFurnizor`).
   * `WorkflowServicesTest`: Validates end-to-end multi-aggregate orchestration (cascade lot cancellation, inspection booking, 3-Way Matching).
   * `ComputationRulesTest`: Verifies deterministic monetary math and zero roundoff loss using `BigDecimal` with `HALF_UP`.
   * `ComputationServicesTest`: Evaluates monthly OTIF scorecard computation and trilateral reconciliation discrepancy formulas.
   * `ValidationRulesTest`: Validates self-enforcing Value Object constructors throwing `DomainValidationException` on invalid inputs.
   * `ValidationServicesTest`: Tests partner eligibility rules and inspector qualification checks.
2. **Architectural Fitness Tests (ArchUnit)** (`src/test/java/.../architecture`):
   * `BoundedContextIsolationTest`: Guarantees strict zero-leakage boundaries between Comenzi, Calitate, and Facturi.
   * `AggregateEncapsulationTest`: Enforces package-private mutation rules on internal child entities (`Lot`, `ScorecardLunar`).
   * `LayerArchitectureTest`: Ensures the domain layer remains strictly decoupled from Spring, JPA, and external infrastructure.
3. **Integration & BDD Gherkin Tests (Cucumber)** (`src/test/java/.../bdd`):
   * 6 Gherkin `.feature` specifications covering 11 acceptance scenarios executed via `BddFeaturesTest`.
4. **Performance & SLA Benchmarks** (`src/test/java/.../performance`):
   * `PerformanceAndSlaTest`: Proves execution compliance with contractual SLAs (lead time estimation $<300	ext{ ms}$, batch reconciliation of 5,000 invoices $<60	ext{ s}$).

---

## 🏛️ 6. Architecture, Bounded Contexts & Domain Invariants

The solution is decomposed into three isolated Bounded Contexts united by a minimal Shared Kernel:

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                                SHARED KERNEL                                │
│  - Value Objects: Suma, Cantitate, CodMaterial, IdComanda, IdFurnizor, IdLot│
│  - Transactional Outbox & Idempotent Inbox Entities and Dispatchers        │
│  - Tamper-evident Audit Logging (shared_audit_log) & Security Filters       │
└─────────────────────────────────────────────────────────────────────────────┘
              ▲                               ▲                               ▲
              │                               │                               │
┌─────────────┴─────────────┐   ┌─────────────┴─────────────┐   ┌─────────────┴─────────────┐
│      COMENZI CONTEXT      │   │     CALITATE CONTEXT      │   │      FACTURI CONTEXT      │
│                           │   │                           │   │                           │
│ • Aggregates:             │   │ • Aggregates:             │   │ • Aggregates:             │
│   - ComandaAprovizionare  │   │   - Furnizor              │   │   - FacturaFurnizor       │
│   - Lot                   │   │   - InspectieCalitate(AQL)│   │   - NIR (Receipt Note)    │
│ • Key Features:           │   │   - InspectorCalitate     │   │   - OrdinPlata (Payment)  │
│   - Order lifecycle       │   │   - RaportNeconformitate  │   │ • Key Features:           │
│   - Atomic lot cancellation   │ • Key Features:           │   │   - 3-Way Matching        │
│   - Lead time forecasting │   │   - ISO 2859-1 Sampling   │   │   - Four-Eyes Principle   │
│   - Optimistic locking    │   │   - Weighted Scorecard    │   │   - Stripe Webhook HMAC   │
└───────────────────────────┘   └───────────────────────────┘   └───────────────────────────┘
```

### Business Rules & SLA Performance Matrix (Table 1.2)

| Rule / SLA ID | Domain Specification & Rule Details | Implementation & Architectural Pattern | Automated Verification Test |
| :--- | :--- | :--- | :--- |
| **BR-PO-01** | Orders may only be placed with suppliers in `ACTIV` status. | Hexagonal SPI `IGestiuneFurnizoriAdapter`, 422 Unprocessable Entity | `ValidationServicesTest`, `CreeazaComandaUnitTest` |
| **NFR-SLA-04** | Cancelling an order triggers atomic cascade cancellation of all unreceived lots. | Aggregate boundary method `ComandaAprovizionare.anuleazaComanda()` | `WorkflowServicesTest`, `CascadeCancellationIntegrationTest` |
| **BR-SQ-02** | Monthly supplier score: **60% OTIF** (On-Time In-Full) + **40% Lot Compliance Rate**. | Deterministic domain service `CalculScorecardService.calculeaza()` | `ComputationServicesTest`, BDD `scorecard_lunar.feature` |
| **BR-SQ-04/05** | Statistical sampling via ISO 2859-1; automatic NCR issuance on lot rejection. | State pattern in `InspectieCalitate`, automated `RaportNeconformitate` | `WorkflowRulesTest`, BDD `executa_inspectie.feature` |
| **NFR-SLA-02** | Idempotent Goods Receipt Note (NIR) processing. | Idempotent consumer validating `NIRRepo.existaNIRPentruEvent` | `PaymentWorkflowIntegrationTest`, `IdempotentNIRTest` |
| **NFR-SLA-05** | 3-Way Match Reconciliation with $\pm 1\%$ quantity & $\pm 5\text{ EUR}$ VAT tolerance. | `ReconciliereService.reconciliaza()` cross-matching PO vs NIR vs Invoice | `PerformanceAndSlaTest` (5,000 invoices $<60	ext{ s}$) |
| **NFR-SLA-06** | Four-Eyes Approval Principle: auto-approval $\le 10,000	ext{ EUR}$; second approval for $> 10,000	ext{ EUR}$. | Approval state transition `OrdinPlata.aproba(UUID)` by independent user | `ControlFinanciarUnitTest`, `PaymentWorkflowIntegrationTest` |
| **NFR-SLA-01** | Lead time forecasting SLA $<300	ext{ ms}$ over 1,000 historical records. | Strategy Pattern in `EstimareLeadTimeService` (Moving Average, Exp Smoothing, P90) | `PerformanceAndSlaTest` ($<300	ext{ ms}$) |
| **ADR-010** | Cryptographic verification of Stripe webhooks via `Stripe-Signature` with 300s replay window. | HMAC SHA-256 signature check in `StripeBancaPlataAdapter` with audit logging | `PaymentWorkflowIntegrationTest` |

---

## 📚 7. Project Documentation & Blueprints

For exhaustive component catalogs, design decisions, and infrastructure blueprints, refer to:
* 📄 [`SPRINT_2_COD_CATALOG.md`](SPRINT_2_COD_CATALOG.md) — **1:1 Architectural Blueprint & Domain Catalog** detailing all 161 components (154 Rich Domain Java classes + 7 infrastructure/tooling items) with relative navigation links and zero raw code snippets.
* 📄 [`pom.xml`](pom.xml) — Central Maven Project Object Model configuration (Java 21, Spring Boot 3.3.5, plugins).
* 📄 [`docker-compose.yml`](docker-compose.yml) — Multi-container service definitions, internal networking, and persistent volumes.
* 📄 [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) — Enterprise GitHub Actions continuous integration pipeline.
