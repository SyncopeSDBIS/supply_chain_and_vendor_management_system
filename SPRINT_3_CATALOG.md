# ARCHITECTURAL BLUEPRINT & COMPONENT CATALOG – SPRINT 3
## Data Access & Persistence Layer (JPA, Hibernate & Repository Services)

---

### BLUEPRINT METADATA
* **Root Package**: `ro.uaic.feaa.sdbis.supplychain`
* **Academic & Enterprise Milestone**: **Sprint 3 (Theme_2: Course_5 – Data Access Components Implementation)**
* **Architecture Pattern**: **Hexagonal Architecture (Ports & Adapters) & Domain-Driven Design (DDD)**
* **Persistence Strategy**: **Object-Relational Mapping (ORM) via Jakarta Persistence (JPA 3.1) & Hibernate 6.5+**, backed by **Spring Data JPA**, **Custom EntityManager DAO Implementations (Bonus Complete JPA Repositories)**, and **Flyway Database Migration Engine (V1–V15)**.
* **Database Engine Portability**: 100% Dual Portability across **PostgreSQL 16 Alpine** (Production) and **H2 In-Memory** (Continuous Integration & Testing).
* **Format**: Pure structural, architectural, and conceptual catalog with clickable relative file links and zero raw code dumping.

---

# 1. SPRINT 3 PHYSICAL NAVIGATION TREE

The following tree represents the exact 1:1 physical structure of all data access, entity mapping, converter, repository, and lifecycle audit components implementing Sprint 3:

```text
.
├── src
│   ├── main
│   │   ├── java/ro/uaic/feaa/sdbis/supplychain
│   │   │   ├── shared
│   │   │   │   ├── AbstractRepositoryJPA.java                          # Generic JPA CRUD DAO base class
│   │   │   │   ├── OutboxRepo.java & OutboxRepoJPA.java                # Transactional Outbox repository
│   │   │   │   ├── InboxRepo.java & InboxRepoJPA.java                  # Idempotent Inbox repository
│   │   │   │   ├── AuditLogEntry.java & AuditLogArchiveEntry.java       # Audit trail JPA entities
│   │   │   │   ├── DistributedLockEntity.java & DistributedLockJpaService.java # Distributed locking
│   │   │   │   ├── DeadLetterMessageEntity.java & DeadLetterParkingLotRepoJPA.java # DLQ Parking lot
│   │   │   │   ├── DocumentAtasatEntity.java & DocumentStorageRepository.java # Metadata & binary storage
│   │   │   │   └── persistence/converters/                             # Universal JPA AttributeConverters
│   │   │   │       ├── CodMaterialConverter.java
│   │   │   │       ├── CurrencyConverter.java
│   │   │   │       ├── IdComandaConverter.java
│   │   │   │       ├── IdFurnizorConverter.java
│   │   │   │       └── IdLotConverter.java
│   │   │   ├── comenzi
│   │   │   │   ├── domain/                                             # Comenzi JPA Entities & Value Objects
│   │   │   │   │   ├── ComandaAprovizionare.java                       # Aggregate Root (@Entity, @Table)
│   │   │   │   │   ├── LinieComanda.java                               # Component Entity / Value Object
│   │   │   │   │   ├── Lot.java                                        # Child Entity (@Entity, @ManyToOne)
│   │   │   │   │   ├── Expediere.java                                  # Child Entity (@Entity, @ManyToOne)
│   │   │   │   │   ├── ComandaRepo.java                                # Pure DDD IRepository Interface
│   │   │   │   │   └── StareComanda.java & StareLot.java               # Enum Value Objects (@Enumerated)
│   │   │   │   └── infrastructure/
│   │   │   │       ├── ComandaRepoJPA.java                             # Bonus Complete JPA Repository (EntityManager)
│   │   │   │       └── persistence/
│   │   │   │           └── SpringDataComandaRepository.java            # Standard Spring JpaRepository with @Query
│   │   │   ├── calitate
│   │   │   │   ├── domain/                                             # Calitate JPA Entities & Value Objects
│   │   │   │   │   ├── Furnizor.java                                   # Aggregate Root (@Entity, @Table)
│   │   │   │   │   ├── ScorecardLunar.java                             # Child Entity (@Entity, @Version)
│   │   │   │   │   ├── InspectieCalitate.java                          # Aggregate Root (@Entity, @Table)
│   │   │   │   │   ├── InspectorCalitate.java                          # Aggregate Root (@Entity, @Table)
│   │   │   │   │   ├── RaportNeconformitate.java                       # Aggregate Root (@Entity, @Table)
│   │   │   │   │   ├── FurnizorRepo.java, InspectieRepo.java, etc.     # Pure DDD IRepository Interfaces
│   │   │   │   │   └── StareFurnizor.java, StareInspectie.java, etc.   # Enum Value Objects (@Enumerated)
│   │   │   │   └── infrastructure/
│   │   │   │       ├── FurnizorRepoJPA.java, InspectieRepoJPA.java, etc. # Complete JPA Repositories
│   │   │   │       └── persistence/
│   │   │   │           └── SpringDataFurnizorRepository.java           # Standard Spring JpaRepository with @Query
│   │   │   ├── facturi
│   │   │   │   ├── domain/                                             # Facturi JPA Entities & Value Objects
│   │   │   │   │   ├── FacturaFurnizor.java                            # Aggregate Root (@Entity, UniqueConstraint)
│   │   │   │   │   ├── LinieFactura.java                               # Component Value Object
│   │   │   │   │   ├── NIR.java                                        # Aggregate Root (@Entity, Idempotent Key)
│   │   │   │   │   ├── LinieNIR.java                                   # Component Value Object
│   │   │   │   │   ├── OrdinPlata.java                                 # Aggregate Root (@Entity, State Pattern)
│   │   │   │   │   ├── FacturaRepo.java, NIRRepo.java, OrdinPlataRepo.java # Pure DDD IRepository Interfaces
│   │   │   │   │   └── StareFactura.java, StareNIR.java, StareOrdinPlata.java # Enum Value Objects
│   │   │   │   └── infrastructure/
│   │   │   │       ├── FacturaRepoJPA.java, NIRRepoJPA.java, etc.      # Complete JPA Repositories (EntityManager)
│   │   │   │       └── persistence/
│   │   │   │           ├── SpringDataFacturaRepository.java            # Standard Spring JpaRepository with @Query
│   │   │   │           └── SpringDataNIRRepository.java                # Standard Spring JpaRepository with @Query
│   │   │   └── infrastructure
│   │   │       └── configuration/
│   │   │           └── AggregateAuditAndComputationListener.java       # Global JPA @EntityListener (@PrePersist, @PreUpdate)
│   │   └── resources
│   │       ├── META-INF/
│   │       │   └── orm.xml                                             # Global entity listener configuration descriptor
│   │       └── db/migration/                                           # Flyway Versioned Migration Scripts (V1..V15)
│   │           ├── V1__shared_kernel_outbox_inbox.sql
│   │           ├── V2__comenzi_module.sql
│   │           ├── V3__calitate_module.sql
│   │           ├── V4__facturi_module.sql
│   │           ├── V5__performance_indexes_and_constraints.sql
│   │           ├── V6__audit_remediation_constraints.sql
│   │           ├── V7__production_readiness_and_seed_data.sql
│   │           ├── V8__enterprise_readiness.sql
│   │           ├── V9__persistence_optimizations_and_audit.sql
│   │           ├── V10__remediation_normalization_and_parity.sql
│   │           ├── V11__comanda_approval_and_closure.sql
│   │           ├── V12__unique_factura_furnizor_and_distributed_locks.sql
│   │           ├── V13__audit_log_archiving_and_partitioning.sql
│   │           ├── V14__amqp_dlq_parking_lot.sql
│   │           └── V15__scorecard_optimistic_locking.sql
│   └── test
│       └── java/ro/uaic/feaa/sdbis/supplychain
│           └── integration/                                            # Dedicated Persistence & Migration Test Suite
│               ├── FlywayMigrationIntegrationTest.java                 # Validates V1..V15 execution cleanly
│               ├── PersistenceRemediationIntegrationTest.java          # Validates ORM mapping, 1NF normalization & queries
│               ├── OrdinPlataPersistenceIntegrationTest.java           # Validates payment order persistence & L1 cache evict
│               ├── NPlusOneOptimizationIntegrationTest.java            # Validates 2-step eager fetching without N+1 queries
│               └── TenRemediationsVerificationTest.java                # Validates @Version, locks & fetch optimizations
```

---

# 2. PERSISTENCE MODEL IMPLEMENTATION (JPA ENTITIES & VALUE OBJECTS)

### Core Persistence Strategy
Every business service implemented in Sprint 2 was upgraded to communicate directly with a persistent relational database (PostgreSQL 16 in production, H2 in tests) through JPA entities and transactional repositories.

### 2.1. Root Entities & Child Entities (`@Entity`, `@Table`, `@Inheritance`, `@Version`)
* 📄 [`ComandaAprovizionare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
  * **Annotations**: `@Entity`, `@Table(name = "comenzi_aprovizionare")`, `@EmbeddedId`, `@Version`.
  * **Role**: Aggregate Root managing purchase order lifecycle states, required/promised delivery dates, and approval metadata.
* 📄 [`Lot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
  * **Annotations**: `@Entity`, `@Table(name = "comenzi_loturi")`, `@EmbeddedId`, `@ManyToOne`.
  * **Role**: Physical inventory allocation unit tied to a parent order.
* 📄 [`Expediere.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
  * **Annotations**: `@Entity`, `@Table(name = "comenzi_expedieri")`, `@Id`, `@ManyToOne`, `@ElementCollection`.
  * **Role**: Logistics consignment tracking with 1NF normalized lots association (`comenzi_expedieri_loturi`).
* 📄 [`Furnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
  * **Annotations**: `@Entity`, `@Table(name = "calitate_furnizori")`, `@EmbeddedId`, `@Version`.
  * **Role**: Vendor profile with contact details, negotiated price list, and monthly evaluation scorecard history.
* 📄 [`ScorecardLunar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
  * **Annotations**: `@Entity`, `@Table(name = "calitate_scorecards")`, `@Id`, `@Version`, `@ManyToOne`.
  * **Role**: Vendor performance metric entity guarded against lost updates via optimistic concurrency control (`@Version`).
* 📄 [`InspectieCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
  * **Annotations**: `@Entity`, `@Table(name = "calitate_inspectii")`, `@Id`, `@Version`.
  * **Role**: ISO 2859-1 AQL inspection tracking samples, defect counts, and accept/reject verdicts.
* 📄 [`InspectorCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
  * **Annotations**: `@Entity`, `@Table(name = "calitate_inspectori")`, `@Id`.
  * **Role**: QA inspector profile managing certifications and availability calendar.
* 📄 [`RaportNeconformitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
  * **Annotations**: `@Entity`, `@Table(name = "calitate_ncr")`, `@Id`.
  * **Role**: Non-conformance report aggregate with corrective actions and escalation tracking.
* 📄 [`FacturaFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
  * **Annotations**: `@Entity`, `@Table(name = "facturi_furnizori")`, `@Id`, `@Version`, `@UniqueConstraint(name = "uk_facturi_furnizor_serie_numar")`.
  * **Role**: Supplier commercial invoice aggregate preventing duplicate billing per supplier.
* 📄 [`NIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
  * **Annotations**: `@Entity`, `@Table(name = "facturi_nir")`, `@Id`, `@Version`.
  * **Role**: Warehouse Goods Receipt Note enforcing idempotent intake via unique `event_sursa_id`.
* 📄 [`OrdinPlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
  * **Annotations**: `@Entity`, `@Table(name = "facturi_ordine_plata")`, `@Id`, `@Version`.
  * **Role**: Bank disbursement order aggregate implementing the Four-Eyes principle and payment execution states.

### 2.2. Value Objects Persistence (`@Embedded`, `@ElementCollection`, `@Enumerated`, `@Convert`)
* **Enumerated Types (`@Enumerated(EnumType.STRING)`)**:
  * [`StareComanda`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java), [`StareLot`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java), [`StareFurnizor`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java), [`StareInspectie`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java), [`StareFactura`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java), [`StareNIR`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java), [`StareOrdinPlata`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java).
* **Attribute Converters (`@Convert`, `@Converter`)**:
  * 📄 [`IdComandaConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java): Maps strongly-typed `IdComanda` to JDBC UUID.
  * 📄 [`IdFurnizorConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java): Maps strongly-typed `IdFurnizor` to JDBC UUID.
  * 📄 [`IdLotConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java): Maps strongly-typed `IdLot` to JDBC UUID.
  * 📄 [`CodMaterialConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java): Maps `CodMaterial` VO to VARCHAR.
  * 📄 [`CurrencyConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java): Maps standard `java.util.Currency` to ISO-4217 3-letter string.
* **Composite Embedded Types (`@Embedded`, `@AttributeOverride`)**:
  * [`Suma`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java) (amount & currency), [`Cantitate`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java) (numeric quantity & unit), [`Adresa`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java) (street, number, city, postal code), [`Perioada`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java) (start date & end date).

---

# 3. JPA AGGREGATES & RELATIONAL MAPPINGS

All aggregates strictly encapsulate internal child entities and collections, enforcing lifecycle integrity via cascading and orphan removal:

| Aggregate Root | Target Child Entity / VO | Mapping Annotation | Cascade & Fetch Configuration | Table Name |
| :--- | :--- | :--- | :--- | :--- |
| `ComandaAprovizionare` | `LinieComanda` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `comenzi_linii` |
| `ComandaAprovizionare` | `Lot` | `@OneToMany` | `cascade = CascadeType.ALL`, `orphanRemoval = true`, `FetchType.LAZY` | `comenzi_loturi` |
| `ComandaAprovizionare` | `Expediere` | `@OneToMany` | `cascade = CascadeType.ALL`, `orphanRemoval = true`, `FetchType.LAZY` | `comenzi_expedieri` |
| `Furnizor` | `Email` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `calitate_furnizori_emailuri` |
| `Furnizor` | `categoriiMateriale` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `calitate_furnizori_categorii` |
| `Furnizor` | `PretNegociat` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `furnizor_preturi_negociate` |
| `Furnizor` | `ScorecardLunar` | `@OneToMany` | `cascade = CascadeType.ALL`, `orphanRemoval = true`, `FetchType.LAZY` | `calitate_scorecards` |
| `InspectieCalitate` | `EsantionAQL` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `calitate_inspectii_esantioane` |
| `InspectorCalitate` | `Certificare` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `calitate_inspectori_certificari` |
| `InspectorCalitate` | `Perioada` (Disponibilitate) | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `calitate_inspectori_disponibilitate` |
| `FacturaFurnizor` | `LinieFactura` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `facturi_linii` |
| `NIR` | `LinieNIR` | `@ElementCollection` | `@CollectionTable`, `FetchType.LAZY` | `facturi_nir_linii` |

---

# 4. REPOSITORY SERVICES IMPLEMENTATION

The architecture supports both repository implementation strategies discussed in the curriculum:

### 4.1. Standard Spring Data JPA Repositories
Interfaces extending `org.springframework.data.jpa.repository.JpaRepository<RootEntity, ID>` with property-based query derivation and custom JPQL/native `@Query` operations:
1. 📄 [`SpringDataComandaRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/persistence/SpringDataComandaRepository.java):
   * `findByNumarComanda(String)`
   * `findByFurnizorId(IdFurnizor)`
   * `findByStareComanda(StareComanda)`
   * `@Query` `findByFurnizorAndStare(...)`
2. 📄 [`SpringDataFurnizorRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/persistence/SpringDataFurnizorRepository.java):
   * `findByCodFiscal(String)`
   * `findByStareFurnizor(StareFurnizor)`
   * `@Query` `findActiveFurnizoriOrdered(...)`
3. 📄 [`SpringDataFacturaRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/persistence/SpringDataFacturaRepository.java):
   * `findBySerieAndNumar(String, String)`
   * `findByComandaId(IdComanda)`
   * `findByFurnizorId(IdFurnizor)`
   * `@Query` `findFacturiNeachitatePentruFurnizor(...)`
4. 📄 [`SpringDataNIRRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/persistence/SpringDataNIRRepository.java):
   * `findByNumarNIR(String)`
   * `findByComandaId(IdComanda)`
   * `existsByEventSursaId(UUID)`
   * `@Query` `findByComandaAndStare(...)`
5. 📄 [`UserAccountRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/securitate/UserAccountRepository.java):
   * `findByUsername(String)`
   * `existsByUsername(String)`, `existsByEmail(String)`
6. 📄 [`DocumentStorageRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DocumentStorageRepository.java):
   * `findMetadataByReferintaIdOrderByDataIncarcareDesc(UUID)`
   * `findMetadataByTipDocumentOrderByDataIncarcareDesc(String)`

### 4.2. (Bonus) Complete JPA Repositories (Pure DDD Ports & EntityManager Adapters)
Pure domain repository contracts (`IRepository`) decoupled from framework dependencies, implemented via `EntityManager` injection inheriting common CRUD operations from [`AbstractRepositoryJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java):
* 📄 [`ComandaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java) $\rightarrow$ [`ComandaRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/ComandaRepoJPA.java)
  * Implements two-step eager loading `findAllWithLiniiAndLoturi(IdFurnizor)` to eliminate N+1 queries.
* 📄 [`FurnizorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java) $\rightarrow$ [`FurnizorRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/FurnizorRepoJPA.java)
  * Supports eager loading of scorecards without multiple bag fetch conflicts.
* 📄 [`FacturaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java) $\rightarrow$ [`FacturaRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/FacturaRepoJPA.java)
  * Supports custom filtering by order and vendor IDs.
* 📄 [`NIRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java) $\rightarrow$ [`NIRRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/NIRRepoJPA.java)
  * Optimized two-step fetch preventing N+1 queries during 3-Way reconciliation.
* 📄 [`InspectieRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java) $\rightarrow$ [`InspectieRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/InspectieRepoJPA.java)
* 📄 [`InspectorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java) $\rightarrow$ [`InspectorRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/InspectorRepoJPA.java)
* 📄 [`NCRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java) $\rightarrow$ [`NCRRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/NCRRepoJPA.java)
* 📄 [`OrdinPlataRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java) $\rightarrow$ [`OrdinPlataRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/OrdinPlataRepoJPA.java)
* 📄 [`OutboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java) $\rightarrow$ [`OutboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* 📄 [`InboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java) $\rightarrow$ [`InboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* 📄 [`IDeadLetterParkingLotRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IDeadLetterParkingLotRepo.java) $\rightarrow$ [`DeadLetterParkingLotRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DeadLetterParkingLotRepoJPA.java)

---

# 5. BUSINESS RULES & LIFECYCLE LISTENERS

### 5.1. Integration of Services & Factories with JPA Repositories
Every application service coordinates business transactions through injected JPA repositories:
* [`ComandaApplicationService`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/application/ComandaApplicationService.java) persists commands, enforces supplier eligibility before saving, and triggers cascading lot state changes.
* [`CalitateApplicationService`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/application/CalitateApplicationService.java) manages vendor onboarding, ISO 2859-1 AQL inspection workflows, and monthly scorecard persistence.
* [`FacturaApplicationService`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/application/FacturaApplicationService.java) executes 3-Way Match reconciliation, Goods Receipt Note intake, and bank payment order dispatches.

### 5.2. Computation & Audit Rules via Entity Listeners
* 📄 [`AggregateAuditAndComputationListener.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/infrastructure/configuration/AggregateAuditAndComputationListener.java):
  * Configured globally in [`META-INF/orm.xml`](src/main/resources/META-INF/orm.xml) across all persistence unit entities.
  * Intercepts `@PrePersist`, `@PreUpdate`, and `@PreRemove` lifecycle phases to generate audit logs and track mutation counters.
* **Optimistic Locking Guard**:
  * Root entities (`ComandaAprovizionare`, `ScorecardLunar`, `FacturaFurnizor`, `NIR`, `OrdinPlata`) map a `@Version` field (`version BIGINT NOT NULL DEFAULT 0`). Concurrent modifications trigger `OptimisticLockException` preventing lost updates.

---

# 6. DATABASE SCHEMA MIGRATION PIPELINE (FLYWAY V1..V15)

The database schema is managed through 15 ordered, incremental Flyway migrations guaranteeing exact parity between Java domain models and SQL tables:

| Migration Script | Subsystem / Focus | Database Objects Created / Modified |
| :--- | :--- | :--- |
| `V1__shared_kernel_outbox_inbox.sql` | Shared Kernel | `shared_outbox`, `shared_inbox`, `shared_audit_log` |
| `V2__comenzi_module.sql` | Comenzi | `comenzi_aprovizionare`, `comenzi_linii`, `comenzi_loturi`, `comenzi_expedieri` |
| `V3__calitate_module.sql` | Calitate | `calitate_furnizori`, `calitate_scorecards`, `calitate_inspectii`, `calitate_inspectori`, `calitate_ncr` |
| `V4__facturi_module.sql` | Facturi | `facturi_furnizori`, `facturi_linii`, `facturi_nir`, `facturi_nir_linii`, `facturi_ordine_plata` |
| `V5__performance_indexes_and_constraints.sql` | Optimization | B-Tree performance indexes on foreign keys and search criteria |
| `V6__audit_remediation_constraints.sql` | Audit & Integrity | Unique constraints, check constraints, and referential integrity |
| `V7__production_readiness_and_seed_data.sql` | Production Seed | Pre-seeded baseline catalogs, material codes, and quality test matrices |
| `V8__enterprise_readiness.sql` | Security & Docs | `users`, `user_roles`, `shared_documente_atasate` |
| `V9__persistence_optimizations_and_audit.sql` | Refinement | Timestamp normalization and indexing on audit tables |
| `V10__remediation_normalization_and_parity.sql` | Normalization | 1NF normalization for `comenzi_expedieri_loturi` |
| `V11__comanda_approval_and_closure.sql` | Governance | Approval and closure columns (`data_aprobare`, `aprobat_de`, `nivel_aprobare`) |
| `V12__unique_factura_furnizor_and_distributed_locks.sql` | Concurrency | Unique constraint `(furnizor_id, serie, numar)`, `distributed_locks` table |
| `V13__audit_log_archiving_and_partitioning.sql` | Operations | `shared_audit_log_archive` cold-storage table |
| `V14__amqp_dlq_parking_lot.sql` | Resiliency | `shared_dlq_parking_lot` for AMQP poison pill messages |
| `V15__scorecard_optimistic_locking.sql` | Concurrency | Added `version BIGINT NOT NULL DEFAULT 0` to `calitate_scorecards` |

---

# 7. SPRINT 3 AUTOMATED TEST SUITE

The persistence layer is comprehensively verified through integration tests with real database engines:

* 📄 [`FlywayMigrationIntegrationTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/integration/FlywayMigrationIntegrationTest.java): Proves that all migrations from `V1` to `V15` apply cleanly on empty database schemas without syntax or constraint errors.
* 📄 [`PersistenceRemediationIntegrationTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/integration/PersistenceRemediationIntegrationTest.java): Verifies standard Spring Data JPA repositories, derived queries, `@Query` custom filters, and 1NF normalized table joins.
* 📄 [`OrdinPlataPersistenceIntegrationTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/integration/OrdinPlataPersistenceIntegrationTest.java): Proves persistent database synchronization after Hibernate L1 session cache eviction (`em.flush()`, `em.clear()`).
* 📄 [`NPlusOneOptimizationIntegrationTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/integration/NPlusOneOptimizationIntegrationTest.java): Confirms two-step eager fetching executes in strictly $O(1)$ database trips.
* 📄 [`TenRemediationsVerificationTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/integration/TenRemediationsVerificationTest.java): Validates optimistic locking behavior (`@Version`) on `ScorecardLunar` and safe lock table insertions without marking transactions `rollback-only`.
