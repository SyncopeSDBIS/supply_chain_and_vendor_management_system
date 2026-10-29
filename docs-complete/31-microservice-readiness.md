# 31. MICROSERVICE READINESS & DECOMPOSITION ROADMAP
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. STRATEGIC DECOMPOSITION PHILOSOPHY (MODULITH TO MICROSERVICES)

The Procure-to-Pay platform was architected as a **Spring Modulith Modular Monolith** with zero direct cross-module table joins, zero cyclic package dependencies, and strict public API contracts specifically to guarantee **Frictionless Microservice Migration Readiness** when organizational scale demands distributed operations.

```mermaid
graph TD
    subgraph Phase1["Phase 1: Current State (Modular Monolith)"]
        M1["Single JVM Process<br/>(Spring Modulith 1.2.4)"]
        M1_C["comenzi"]
        M1_Q["calitate"]
        M1_F["facturi"]
        DB_SHARED[("PostgreSQL 16<br/>(Logical Module Schemas)")]

        M1 --- M1_C
        M1 --- M1_Q
        M1 --- M1_F
        M1 --> DB_SHARED
    end

    subgraph Phase2["Phase 2: Database Schema Segregation"]
        DB_C[("comenzi_db")]
        DB_Q[("calitate_db")]
        DB_F[("facturi_db")]
    end

    subgraph Phase3["Phase 3: Autonomous Microservices Deployment"]
        SVC_C["Comenzi Microservice<br/>(Order Service Pod)"]
        SVC_Q["Calitate Microservice<br/>(Quality Service Pod)"]
        SVC_F["Facturi Microservice<br/>(Invoicing Service Pod)"]
        BROKER["RabbitMQ Topic Exchange<br/>(supplychain.events.exchange)"]

        SVC_C --> DB_C
        SVC_Q --> DB_Q
        SVC_F --> DB_F

        SVC_C <-->|AMQP / Outbox| BROKER
        SVC_Q <-->|AMQP / Outbox| BROKER
        SVC_F <-->|AMQP / Outbox| BROKER
    end

    Phase1 ==> Phase2
    Phase2 ==> Phase3
```

---

## 2. BOUNDED CONTEXT INDEPENDENCE AUDIT

An audit of the existing modular boundaries confirms the following architectural independence readiness metrics:

| Bounded Context | Cross-Module Database Foreign Keys | Cross-Module Entity Mappings | Inter-Module Communication | Microservice Readiness Rating |
|---|:---:|:---:|---|:---:|
| **Comenzi** (Purchasing) | **0** (Zero) | **0** (Zero) | Queries `ICalitateModuleApi` interface | **100% Ready** |
| **Calitate** (Quality) | **0** (Zero) | **0** (Zero) | Autonomous (No outgoing synchronous calls) | **100% Ready** |
| **Facturi** (Invoicing) | **0** (Zero) | **0** (Zero) | Queries `IComenziModuleApi` interface | **100% Ready** |

> [!NOTE]
> All inter-module references use opaque UUID identity keys (`UUID comandaId`, `UUID furnizorId`, `UUID facturaId`). There are **no Hibernate `@ManyToOne` or `@OneToMany` relationships traversing module boundaries**.

---

## 3. DATABASE-PER-SERVICE TRANSITION STRATEGY

### Step 1: Logical Schema Partitioning (Completed via Flyway)
Existing Flyway scripts (`V1` to `V6`) prefix all database tables with their bounded context name:
- `comenzi_*`: Owned exclusively by the Purchasing module.
- `calitate_*`: Owned exclusively by the Quality & Supplier module.
- `facturi_*`: Owned exclusively by the Invoicing & Accounts Payable module.
- `shared_*`: Shared Kernel infrastructure (Outbox, Inbox, Audit).

### Step 2: Physical Database Extraction
To achieve true physical isolation, the monolithic database `supplychain_db` will be split into three independent PostgreSQL instances:
1. `comenzi_db`: Tables `comenzi_comenzi`, `comenzi_linii_comanda`, `comenzi_loturi`.
2. `calitate_db`: Tables `calitate_furnizori`, `calitate_inspectii`, `calitate_defecte`, `calitate_ncr`, `calitate_scorecards`.
3. `facturi_db`: Tables `facturi_facturi`, `facturi_linii_factura`, `facturi_nir`, `facturi_linii_nir`, `facturi_ordine_plata`.

Each microservice will maintain its own dedicated `shared_outbox_events` and `shared_inbox_events` tables to manage local asynchronous messaging.

---

## 4. DISTRIBUTED DATA CONSISTENCY & SAGA PATTERN

In a distributed microservice topology, atomic ACID database transactions cannot cross service boundaries. Cross-service business flows will transition from synchronous transactions to **Choreographed Event Sagas**:

```mermaid
sequenceDiagram
    autonumber
    participant C as Comenzi Microservice
    participant MQ as RabbitMQ Broker
    participant Q as Calitate Microservice
    participant F as Facturi Microservice

    Note over C,F: Order Placement & Inspection Flow
    C->>MQ: Publish ComandaAprobataEvent
    MQ->>Q: Route to calitate.events.queue
    Q->>Q: Create Pending Quality Inspection

    Note over Q,F: Quality Acceptance & Invoicing Saga
    Q->>MQ: Publish InspectieFinalizataEvent (Verdict: ADMIS)
    MQ->>F: Route to facturi.events.queue
    F->>F: Auto-generate NIR & Unlock Invoice Reconciliation

    Note over C,F: Cascade Cancellation Saga (Compensating Flow)
    C->>MQ: Publish ComandaAnulataEvent
    MQ->>Q: Cancel Pending Inspections
    MQ->>F: Reject Unmatched Invoices (Compensating Action)
```

---

## 5. SHARED KERNEL & CODE REPOSITORY EVOLUTION

To extract microservices without code duplication:
1. **Extract `supplychain-shared-kernel`:** Publish `ro.uaic.feaa.sdbis.supplychain.shared.*` as an enterprise internal Maven artifact.
2. **Convert Public Module APIs to REST / OpenFeign Clients:** The existing interface [ICalitateModuleApi.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/api/ICalitateModuleApi.java) will be backed by a Spring Cloud OpenFeign client calling `http://calitate-service/api/furnizori/{id}/summary`.
3. **API Gateway Ingress:** Introduce Spring Cloud Gateway or Traefik at the edge to route external requests to individual service pods.

