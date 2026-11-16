# ARCHITECTURAL BLUEPRINT & COMPONENT CATALOG – SPRINT 4
## REST API, Service-Oriented Architecture (SOA), Security & Event-Driven Messaging

---

### BLUEPRINT METADATA
* **Root Package**: `ro.uaic.feaa.sdbis.supplychain`
* **Academic & Enterprise Milestone**: **Sprint 4 (ADR-007: REST API & Service Exposure & ADR-009: SOA Paradigm)**
* **Architecture Style**: **Service-Oriented Architecture (SOA), Hexagonal Architecture (Ports & Adapters), and Event-Driven Architecture (EDA)**
* **Core Technology Stack**: **Spring Boot 3.3.5**, **Spring MVC REST**, **Jackson JSON 2.17+**, **Spring Security 6.3+ (JWT HMAC-SHA256 & RBAC)**, **Spring AMQP & RabbitMQ 3.13+**, **Stripe Java SDK (v24+)**, and **Springdoc OpenAPI 3.0 (Swagger UI)**.
* **Format**: Pure structural, architectural, and conceptual catalog with clickable relative file links and zero raw code dumping.

---

# 1. SPRINT 4 PHYSICAL NAVIGATION TREE

The following tree represents the exact 1:1 physical structure of all REST controllers, DTOs, security filters, payment adapters, AMQP messaging dispatchers, and integration test components implementing Sprint 4:

```text
.
├── src
│   ├── main
│   │   ├── java/ro/uaic/feaa/sdbis/supplychain
│   │   │   ├── comenzi
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── ComandaCtrl.java                                # REST Controller (/api/comenzi)
│   │   │   │   │   ├── CreeazaComandaRequest.java                      # Request DTO
│   │   │   │   │   ├── ConfirmaComandaRequest.java                     # Request DTO
│   │   │   │   │   ├── PlanificaLotRequest.java                        # Request DTO
│   │   │   │   │   ├── InregistreazaExpediereRequest.java              # Request DTO
│   │   │   │   │   └── ReceptioneazaComandaRequest.java                # Request DTO
│   │   │   │   ├── application/
│   │   │   │   │   ├── ComandaApplicationService.java                  # Workflow Orchestrator (Inbound Port)
│   │   │   │   │   ├── ComenziModuleFacade.java                        # Spring Modulith Public Facade
│   │   │   │   │   ├── dto/LinieComandaDTO.java                        # Domain Transfer DTO
│   │   │   │   │   └── query/ComandaQueryService.java                  # CQRS Query Service (Read Models)
│   │   │   │   └── infrastructure/
│   │   │   │       ├── adapter/ComenziPerformantaLivrariAdapter.java   # Module adapter for OTIF metrics
│   │   │   │       └── listener/                                       # Transactional Inbox Event Consumers
│   │   │   │           ├── ComandaEventListener.java
│   │   │   │           └── ComandaReceptieEventListener.java
│   │   │   ├── calitate
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── CalitateCtrl.java                               # REST Controller (/api/furnizori, /api/inspectii)
│   │   │   │   │   ├── InregistreazaFurnizorRequest.java               # Request DTO
│   │   │   │   │   ├── CreeazaInspectorRequest.java                    # Request DTO
│   │   │   │   │   ├── InregistreazaVerdictRequest.java                # Request DTO
│   │   │   │   │   ├── InitiazaActiuneNCRRequest.java                  # Request DTO
│   │   │   │   │   └── CalculScorecardRequest.java                     # Request DTO
│   │   │   │   └── application/
│   │   │   │       ├── CalitateApplicationService.java                 # Workflow Orchestrator
│   │   │   │       ├── CalitateModuleFacade.java                       # Public Inter-module Facade
│   │   │   │       ├── dto/                                            # Read & Transfer DTOs
│   │   │   │       │   ├── FurnizorDTO.java
│   │   │   │       │   ├── InspectieCalitateDTO.java
│   │   │   │       │   ├── NCRDTO.java
│   │   │   │       │   └── ScorecardLunarDTO.java
│   │   │   │       └── query/CalitateQueryService.java                 # CQRS Read-Model Service
│   │   │   ├── facturi
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── FacturaCtrl.java                                # REST Controller (/api/facturi, /api/nir, /api/ordine-plata)
│   │   │   │   │   ├── StripeWebhookCtrl.java                          # Legacy / Alternate Webhook Endpoint
│   │   │   │   │   ├── InregistreazaFacturaRequest.java                # Request DTO
│   │   │   │   │   └── dto/                                            # Webhook & Invoice Views
│   │   │   │   │       ├── WebhookResponseDTO.java
│   │   │   │   │       ├── FacturaListItemView.java
│   │   │   │   │       ├── FacturaDetaliiView.java
│   │   │   │   │       ├── NIRListItemView.java
│   │   │   │   │       └── OrdinPlataListItemView.java
│   │   │   │   ├── application/
│   │   │   │   │   ├── FacturaApplicationService.java                  # Workflow Orchestrator (Non-blocking I/O)
│   │   │   │   │   ├── FacturiModuleFacade.java                        # Public Module Facade
│   │   │   │   │   ├── StripeWebhookProcessingService.java             # Async Decoupled Webhook Handler
│   │   │   │   │   ├── dto/WebhookProcessingResultDTO.java             # Internal Processing Result DTO
│   │   │   │   │   └── query/FacturaQueryService.java                  # CQRS Query Service
│   │   │   │   └── infrastructure/
│   │   │   │       ├── StripeBancaPlataAdapter.java                    # Stripe Payment Intent Client Adapter
│   │   │   │       ├── StripeProperties.java                           # Stripe Configuration Properties
│   │   │   │       ├── configuration/StripeWebhooksConfig.java         # Dedicated ThreadPoolTaskExecutor Bean
│   │   │   │       └── listener/FacturaEventListener.java              # Transactional Inbox Event Listener
│   │   │   ├── securitate/                                             # Authentication & Security Subsystem
│   │   │   │   ├── AutentificareCtrl.java                              # Auth Controller (/api/auth)
│   │   │   │   ├── AuthLoginRequest.java & AuthRegisterRequest.java    # Auth Request DTOs
│   │   │   │   ├── AuthResponse.java & UserMeResponse.java             # Auth Response DTOs
│   │   │   │   ├── JwtTokenService.java                                # RFC 7519 HMAC-SHA256 Token Provider
│   │   │   │   ├── RbacSecurityFilter.java                             # Role-Based URL Authorization Filter
│   │   │   │   ├── RateLimitingFilter.java                             # Token Bucket DDoS Prevention Filter
│   │   │   │   ├── SecurityConfig.java                                 # Spring Security Filter Chain & CORS
│   │   │   │   ├── UserAccountEntity.java & UserAccountRepository.java # User Credentials Persistence
│   │   │   │   └── JurnalAuditService.java                             # Security Audit Trail Writer
│   │   │   ├── shared/
│   │   │   │   ├── DocumentStorageService.java                         # File attachment storage logic
│   │   │   │   ├── DocumentStorageRepository.java                      # File attachment metadata queries
│   │   │   │   ├── DocumentAtasatEntity.java                           # Attachment binary & metadata entity
│   │   │   │   └── messaging/                                          # AMQP Broker Infrastructure
│   │   │   │       ├── RabbitMqConfig.java                             # Exchanges, Queues, DLX & Bindings
│   │   │   │       ├── AmqpEventPublisher.java                         # AMQP Wire Topic Publisher
│   │   │   │       └── AmqpMessageReceivedEvent.java                   # In-process Domain Message Wrap
│   │   │   └── infrastructure/
│   │   │       ├── messaging/                                          # Outbox Dispatcher & AMQP Consumers
│   │   │       │   ├── OutboxDispatcherService.java                    # Polling Publisher with Distributed Lock
│   │   │       │   ├── RabbitMqEventConsumerService.java               # AMQP Consumer with DLQ Redirection
│   │   │       │   └── AmqpMessageDispatchListener.java                # Idempotent Module Event Router
│   │   │       ├── presentation/
│   │   │       │   └── DocumenteCtrl.java                              # Document REST Controller (/api/documente)
│   │   │       ├── scheduler/
│   │   │       │   └── BusinessSchedulers.java                         # Clustered Background Schedulers
│   │   │       └── configuration/
│   │   │           ├── GlobalExceptionHandler.java                     # RFC 7807 Standard Error Handler
│   │   │           ├── OpenApiConfig.java                              # Swagger UI OpenAPI 3.0 Metadata
│   │   │           └── WebConfig.java                                  # MVC Interceptors & Resolvers
│   └── test
│       └── java/ro/uaic/feaa/sdbis/supplychain
│           ├── rest/                                                   # MockMvc REST Controller Unit & Mock Tests
│           │   ├── ComandaCtrlRestTest.java
│           │   ├── CalitateCtrlRestTest.java
│           │   ├── FacturaCtrlRestTest.java
│           │   ├── DocumentStorageIntegrationTest.java
│           │   ├── StripeWebhookIntegrationTest.java
│           │   └── StripeWebhookAsyncDecouplingTest.java
│           ├── securitate/                                             # Authentication & Token Tests
│           │   └── JwtAuthenticationIntegrationTest.java
│           └── integration/                                            # Multi-layer Integration & Hardening
│               ├── AmqpHardeningIntegrationTest.java                   # Poison pill & DLQ parking lot test
│               ├── PaymentWorkflowIntegrationTest.java                 # 4-Eyes principle & webhook flow
│               └── TenRemediationsVerificationTest.java                # Thread pool & non-blocking I/O test
├── tools
│   └── postman/
│       └── SupplyChain_P2P_Collection.json                             # Postman Collection (UC1..UC8, Q1..Q8, I1..I7)
```

---

# 2. REST CONTROLLER ARCHITECTURE & HTTP ENDPOINTS

All endpoints follow RESTful conventions, leverage HTTP response codes (200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 422 Unprocessable Entity), and are documented via OpenAPI 3.0 annotations:

### 2.1. Purchase Order Controller: [`ComandaCtrl.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/presentation/ComandaCtrl.java)
* **Base Path**: `/api/comenzi`
* **Endpoints**:
  * `POST /api/comenzi`: Create a new purchase order (UC1).
  * `POST /api/comenzi/{id}/aprobare`: Departmental order approval based on financial threshold (UC2).
  * `POST /api/comenzi/{id}/confirmare`: Supplier confirms order with promised delivery date (UC3).
  * `POST /api/comenzi/{id}/refuz`: Supplier rejects order with reason.
  * `POST /api/comenzi/{id}/loturi`: Plan physical inventory lots with expiry date (UC4).
  * `POST /api/comenzi/{id}/expedieri`: Register shipment consignment with carrier and AWB (UC5).
  * `POST /api/comenzi/{id}/receptie`: Register goods receipt at warehouse (UC6).
  * `POST /api/comenzi/{id}/inchidere`: Close fully fulfilled order (UC7).
  * `POST /api/comenzi/{id}/anulare`: Cascading cancellation of unreceived lots (UC8 / SLA-04).
  * `GET /api/comenzi`: Paginated list of purchase orders with status filtering.
  * `GET /api/comenzi/{id}`: Full purchase order details view.

### 2.2. Quality & Supplier Controller: [`CalitateCtrl.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/presentation/CalitateCtrl.java)
* **Base Paths**: `/api/furnizori`, `/api/inspectii`, `/api/inspectori`, `/api/ncr`, `/api/calitate`
* **Endpoints**:
  * `POST /api/furnizori`: Onboard new supplier with tax code, IBAN, and payment terms (Q1).
  * `POST /api/furnizori/{id}/aprobare`: Approve qualified vendor (Q2).
  * `GET /api/furnizori/{id}/eligibil`: Verify vendor eligibility for purchase order placement (BR-PO-01).
  * `POST /api/inspectori`: Register QA inspector with certifications.
  * `POST /api/inspectii`: Schedule ISO 2859-1 AQL inspection for a delivered lot (Q3).
  * `POST /api/inspectii/{id}/aloca-inspector`: Assign certified inspector based on qualification matrix (Q4).
  * `POST /api/inspectii/{id}/verdict`: Submit sample inspection verdict (Conform / Respins) (Q5).
  * `POST /api/ncr/{id}/actiune-corectiva`: Record corrective/preventive action (CAPA) (Q6).
  * `POST /api/ncr/{id}/escaladare`: Escalate unresolved non-conformance to management (Q7).
  * `POST /api/calitate/scorecards/calcul`: Trigger monthly vendor scorecard calculation (Q8 / BR-SQ-02).
  * `GET /api/furnizori`: Paginated list of suppliers.
  * `GET /api/inspectii`: Paginated list of inspections.

### 2.3. Invoice, NIR & Payment Controller: [`FacturaCtrl.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/FacturaCtrl.java)
* **Base Paths**: `/api/facturi`, `/api/nir`, `/api/ordine-plata`, `/api/plati`
* **Endpoints**:
  * `POST /api/facturi`: Register supplier commercial invoice (I1).
  * `POST /api/facturi/{id}/contestare`: Dispute invoice discrepancies.
  * `POST /api/facturi/{id}/reconciliere`: Trigger automated 3-Way Matching reconciliation between PO, NIR, and Invoice (I3 / SLA-05).
  * `POST /api/nir/{id}/contabilizare`: Formally post received goods into ERP accounting (I2).
  * `POST /api/nir/{id}/stornare`: Reverse incorrect goods receipt note with audit justification.
  * `POST /api/ordine-plata`: Generate disbursement order from approved invoice (I5).
  * `POST /api/ordine-plata/{id}/aprobare`: Four-Eyes principle approval for payments $>10,000\text{ EUR}$ (I6 / SLA-06).
  * `POST /api/ordine-plata/{id}/transmitere`: Dispatch payment order to banking gateway (non-blocking HikariCP release).
  * `POST /api/ordine-plata/webhook-banca` & `POST /api/plati/webhook-banca`: Stripe HMAC-verified payment execution webhook (I7 / ADR-010).
  * `GET /api/facturi`: Paginated list of invoices.
  * `GET /api/facturi/scadente`: Aging accounts payable report.
  * `GET /api/nir`: Paginated list of Goods Receipt Notes.
  * `GET /api/ordine-plata`: Paginated list of payment disbursement orders.

### 2.4. Authentication Controller: [`AutentificareCtrl.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/AutentificareCtrl.java)
* **Base Path**: `/api/auth`
* **Endpoints**:
  * `POST /api/auth/register`: Register new system user with BCrypt hashed password.
  * `POST /api/auth/login`: Authenticate credentials and return signed JWT token with expiry.
  * `GET /api/auth/me`: Retrieve authenticated user claims, roles, and profile information.

### 2.5. Document Storage Controller: [`DocumenteCtrl.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/infrastructure/presentation/DocumenteCtrl.java)
* **Base Path**: `/api/documente`
* **Endpoints**:
  * `POST /api/documente/upload`: Upload binary file attachment (PDF invoice, certificate) with SHA-256 hash.
  * `GET /api/documente/{id}`: Download binary document with original content type.
  * `GET /api/documente/referinta/{referintaId}`: List metadata of documents attached to an entity.

---

# 3. DTO MODEL & SERIALIZATION/DESERIALIZATION

### Design Strategy
Domain entities never leak across external REST boundaries. All controllers accept dedicated request DTOs and return projection views or response DTOs:

* **Jackson Serialization Annotations**:
  * `@JsonProperty`: Explicit naming alignment between JSON properties and Java fields.
  * `@JsonIgnoreProperties(ignoreUnknown = true)`: Forward compatibility for evolving client schemas.
  * `@JsonManagedReference` & `@JsonBackReference`: Infinite recursion prevention on parent-child bidirectional relationships.
* **Projections & Views**:
  * [`FacturaListItemView.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/dto/FacturaListItemView.java), [`NIRListItemView.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/dto/NIRListItemView.java), [`OrdinPlataListItemView.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/dto/OrdinPlataListItemView.java), [`DocumentMetadataView.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DocumentMetadataView.java).

---

# 4. SECURITY, AUTHENTICATION & RATE LIMITING

Enterprise security architecture protecting all external attack vectors:

### 4.1. RFC 7519 Compliant JWT Provider: [`JwtTokenService.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/JwtTokenService.java)
* Cryptographic HMAC-SHA256 signature verification over 256-bit secret key.
* Strict mandatory claim validation: tokens missing `exp` (expiration), `sub` (username), or `role` are immediately rejected.

### 4.2. Role-Based Access Control (RBAC): [`RbacSecurityFilter.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/RbacSecurityFilter.java)
* Intercepts incoming requests and verifies bearer token permissions against target endpoints:
  * `ROLE_BUYER` / `ROLE_PURCHASER`: Order creation and lot planning.
  * `ROLE_MANAGER`: High-value order approvals ($>50,000\text{ EUR}$).
  * `ROLE_QA_INSPECTOR`: Inspection verdicts and NCR escalations.
  * `ROLE_FINANCIAL_OFFICER`: Payment order execution, 3-Way match reconciliation, and Four-Eyes approval.
  * `ROLE_ADMIN`: User onboarding and system configuration.

### 4.3. DDoS & Brute-Force Rate Limiting: [`RateLimitingFilter.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/RateLimitingFilter.java)
* Token-bucket rate limiter per remote IP address.
* Enforces 100 requests / minute burst protection, returning HTTP 429 Too Many Requests on saturation.

### 4.4. Stateless Filter Chain Configuration: [`SecurityConfig.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/SecurityConfig.java)
* Configures Spring Security with `SessionCreationPolicy.STATELESS`.
* Disables CSRF (not required for bearer token APIs), enables CORS, and registers BCrypt password hashing.

---

# 5. EXTERNAL GATEWAY INTEGRATION: STRIPE BANKING PAYMENTS

### 5.1. Payment Intent Dispatcher: [`StripeBancaPlataAdapter.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/StripeBancaPlataAdapter.java)
* Implements domain outbound port `IBancaPlataProvider`.
* Communicates via HTTPS with Stripe Payment Intents API to initiate bank transfers.

### 5.2. Non-Blocking I/O Architecture (HikariCP Connection Pool Safety)
* In [`FacturaApplicationService.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/application/FacturaApplicationService.java), the external network call to Stripe is completely isolated from database transactions. The HikariCP JDBC connection is released before initiating the HTTPS request, preventing connection pool exhaustion under high latency.

### 5.3. Async Webhook Decoupling & Idempotency: [`StripeWebhookProcessingService.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/application/StripeWebhookProcessingService.java)
* Declares dedicated [`StripeWebhooksConfig.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/StripeWebhooksConfig.java) `ThreadPoolTaskExecutor` (5 core, 20 max threads, `CallerRunsPolicy`) preventing `ForkJoinPool.commonPool` starvation.
* Cryptographic signature verification via `Stripe-Signature` with a 300-second replay attack tolerance window.
* Atomic idempotency via Transactional Inbox: records the processed event ID *strictly after* successful domain execution to prevent lost updates in the event of node crashes.

---

# 6. EVENT-DRIVEN MESSAGING ARCHITECTURE (RABBITMQ & AMQP HARDENING)

### 6.1. Broker Configuration: [`RabbitMqConfig.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/messaging/RabbitMqConfig.java)
* **Topic Exchange**: `supplychain.events.exchange`
* **Dead Letter Exchange (DLX)**: `supplychain.events.dlx`
* **Queues**:
  * `comenzi.events.queue` (bound to `comenzi.#`)
  * `calitate.events.queue` (bound to `calitate.#`)
  * `facturi.events.queue` (bound to `facturi.#`)
  * `supplychain.events.dlq` (Dead-letter target with x-dead-letter parameters)
* **Serialization**: `Jackson2JsonMessageConverter` with trusted package isolation.

### 6.2. Transactional Outbox Dispatcher: [`OutboxDispatcherService.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/infrastructure/messaging/OutboxDispatcherService.java)
* Periodically polls pending events from `shared_outbox` table.
* Guarded by distributed locking (`DistributedLockService`) to guarantee that only one cluster node polls and dispatches at a time.
* Publishes events to RabbitMQ topic exchange via [`AmqpEventPublisher.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/messaging/AmqpEventPublisher.java), marking them as published upon broker ACK.

### 6.3. Idempotent Inbox Consumer & DLQ Parking Lot: [`RabbitMqEventConsumerService.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/infrastructure/messaging/RabbitMqEventConsumerService.java)
* Consumes messages from RabbitMQ queues.
* Intercepts poisoned messages or deserialization failures, routing them to the Dead Letter Queue.
* Persists permanently failing messages into [`DeadLetterParkingLotRepoJPA.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DeadLetterParkingLotRepoJPA.java) (`shared_dlq_parking_lot` table) for administrative inspection and replay.
* Dispatches valid messages to domain listeners via [`AmqpMessageDispatchListener.java`](../../src/main/java/ro/uaic/feaa/sdbis/supplychain/infrastructure/messaging/AmqpMessageDispatchListener.java) utilizing atomic `inboxRepo.incearcaInregistrare` to eliminate TOCTOU race conditions.

---

# 7. SPRINT 4 AUTOMATED TEST SUITE & POSTMAN VERIFICATION

### 7.1. MockMvc REST & Webhook Unit Tests
* 📄 [`ComandaCtrlRestTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/rest/ComandaCtrlRestTest.java): Verifies all 10 order lifecycle endpoints, JSON serialization, and error code handling.
* 📄 [`CalitateCtrlRestTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/rest/CalitateCtrlRestTest.java): Verifies vendor registration, AQL inspection verdicts, and monthly scorecard calculation endpoints.
* 📄 [`FacturaCtrlRestTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/rest/FacturaCtrlRestTest.java): Tests 3-Way match reconciliation, Goods Receipt Notes, and Stripe webhook HMAC signature verification (valid, invalid, and missing headers).
* 📄 [`StripeWebhookAsyncDecouplingTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/rest/StripeWebhookAsyncDecouplingTest.java): Confirms asynchronous execution SLA ($<500\text{ ms}$) and worker pool thread names (`stripe-webhook-*`).
* 📄 [`DocumentStorageIntegrationTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/rest/DocumentStorageIntegrationTest.java): Verifies multipart file upload, SHA-256 integrity hash, and download streaming.
* 📄 [`JwtAuthenticationIntegrationTest.java`](../../src/test/java/ro/uaic/feaa/sdbis/supplychain/securitate/JwtAuthenticationIntegrationTest.java): Tests user registration, BCrypt verification, login token generation, and expired token rejection.

### 7.2. End-to-End Postman Collection
* 📁 [`tools/postman/SupplyChain_P2P_Collection.json`](../../tools/postman/SupplyChain_P2P_Collection.json)
  * Pre-configured environment (`baseUrl = http://localhost:8080`).
  * 5 comprehensive folders validating all 23 core use cases:
    1. **1. Comenzi Aprovizionare (UC1 - UC8)**
    2. **2. Gestiune Furnizori & Calitate (Q1 - Q8)**
    3. **3. Facturi, NIR & 3-Way Matching (I1 - I4)**
    4. **4. Plăți, Ordin de Plată & Stripe Webhooks (I5 - I7)**
    5. **5. Actuator & Healthcheck Probes**
