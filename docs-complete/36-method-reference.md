# 36. PUBLIC METHOD CATALOG & TRANSACTIONAL BOUNDARIES
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. PURCHASING & PURCHASE ORDERS MODULE (`comenzi`)

### 1.1 `ComandaCtrl` (Presentation Layer)

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> creeazaComanda(@Valid @RequestBody CreeazaComandaRequest req)
```
- **Description:** Exposes REST endpoint for initiating a purchase order.
- **Validation:** JSR-380 on `req` (`@NotNull`, `@NotBlank`, `@NotEmpty`).
- **Security:** Requires `ROLE_BUYER` or `ROLE_ADMIN`.
- **Status Codes:** `201 Created` with order UUID; `400 Bad Request` on validation failure.

```java
@PutMapping("/{id}/confirmare")
public ResponseEntity<?> confirmaComanda(@PathVariable UUID id, @RequestParam LocalDate dataPromisa)
```
- **Description:** Records vendor confirmation of delivery commitment.
- **Security:** Requires `ROLE_SUPPLIER`, `ROLE_BUYER`, or `ROLE_ADMIN`.
- **Status Codes:** `200 OK`; `404 Not Found` if order missing.

```java
@PostMapping("/{id}/anulare")
public ResponseEntity<?> anuleazaComanda(@PathVariable UUID id, @RequestParam String motiv)
```
- **Description:** Triggers cascade cancellation saga.
- **Security:** Requires `ROLE_BUYER` or `ROLE_ADMIN`.
- **Status Codes:** `200 OK`; `400 Bad Request` if already fulfilled.

---

### 1.2 `ComandaApplicationService` (Application Layer)

```java
@Transactional
public UUID creeazaComanda(CreeazaComandaCommand cmd)
```
- **Transaction:** Required (`Propagation.REQUIRED`), rollback on any RuntimeException.
- **Flow:**
  1. Calls `ICalitateModuleApi.isFurnizorEligibil(cmd.furnizorId())`. If false, throws `SupplierNotEligibleException`.
  2. Constructs `ComandaAprovizionare` via factory.
  3. Validates lines and computes total value.
  4. Persists order into `comenzi_comenzi`.
  5. Saves `ComandaCreataEvent` into `shared_outbox_events`.
- **Returns:** Generated order `UUID`.

```java
@Transactional
public void anuleazaComanda(UUID comandaId, String motiv)
```
- **Transaction:** Required.
- **Flow:** Loads aggregate, invokes `comanda.anuleaza(motiv)`, marks non-delivered lots cancelled, emits `ComandaAnulataEvent`.

---

### 1.3 `ComandaAprovizionare` (Domain Layer Aggregate Root)

```java
public void adaugaLinie(CodArticol codArticol, Cantitate cantitate, PretUnitar pretUnitar)
```
- **Invariants:** Order status must be `INITIATA`. Cantitate must be $> 0$.
- **Throws:** `IllegalStateException` if order already emitted.

```java
public void anuleaza(String motiv)
```
- **Invariants:** Cannot cancel an order in state `FINALIZATA` or `LIVRATA_TOTAL`.
- **Side Effects:** Transitions state to `ANULATA`; cascades cancellation to all pending `LotLivrare` instances.

---

## 2. QUALITY ASSURANCE & VENDOR MODULE (`calitate`)

### 2.1 `CalitateCtrl` (Presentation Layer)

```java
@PostMapping("/furnizori")
public ResponseEntity<?> inregistreazaFurnizor(@Valid @RequestBody InregistreazaFurnizorRequest req)
```
- **Security:** Requires `ROLE_QUALITY_INSPECTOR` or `ROLE_ADMIN`.
- **Validation:** CIF, Corporate Name, and IBAN format checks.
- **Returns:** `201 Created` with vendor UUID.

```java
@PostMapping("/inspectii")
public ResponseEntity<?> creeazaInspectie(@Valid @RequestBody CreeazaInspectieRequest req)
```
- **Security:** Requires `ROLE_QUALITY_INSPECTOR` or `ROLE_ADMIN`.
- **Returns:** `201 Created` with inspection UUID.

---

### 2.2 `CalitateApplicationService` (Application Layer)

```java
@Transactional
public void inregistreazaVerdict(UUID inspectieId, VerdictInspectie verdict, String observatii)
```
- **Transaction:** Required.
- **Flow:**
  1. Loads `InspectieCalitate` from repository.
  2. Calls `inspectie.inregistreazaVerdict(verdict, observatii)`.
  3. If verdict is `RESPINS`, instantiates `RaportNeconformitate (NCR)` and registers consecutive defect on vendor.
  4. Emits `InspectieFinalizataEvent`.

```java
@Transactional
public ScorecardSummaryDTO genereazaScorecardLunar(UUID furnizorId, YearMonth luna)
```
- **Transaction:** Required.
- **Flow:** Computes delivery accuracy (OTIF), inspection pass rate, and price compliance; generates `ScorecardLunar` record.

---

### 2.3 `InspectieCalitate` (Domain Layer Aggregate Root)

```java
public VerdictInspectie evalueazaConformitate(int defecteCritice, int defecteMajore)
```
- **Algorithm:** Compares defect counts against ISO 2859-1 (AQL) acceptance ($Ac$) and rejection ($Re$) numbers.
- **Returns:** `VerdictInspectie.ADMIS` or `VerdictInspectie.RESPINS`.

---

## 3. INVOICING & ACCOUNTS PAYABLE MODULE (`facturi`)

### 3.1 `FacturaCtrl` (Presentation Layer)

```java
@PostMapping(value = "/facturi", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public ResponseEntity<?> inregistreazaFactura(@Valid @RequestBody InregistreazaFacturaRequest req)
```
- **Content Negotiation:** Accepts both `application/json` and `application/xml`.
- **Security:** Requires `ROLE_ACCOUNTANT`, `ROLE_CFO`, or `ROLE_ADMIN`.
- **Returns:** `201 Created` with invoice summary.

```java
@PostMapping("/ordine-plata/{id}/aprobare")
public ResponseEntity<?> aprobaOrdin(@PathVariable UUID id, @RequestParam UUID aprobatorId)
```
- **Security:** Requires `ROLE_CFO` or `ROLE_ADMIN`.
- **Four-Eyes Enforcement:** Aprobator cannot be the order creator.
- **Returns:** `200 OK` or `400 Bad Request` on policy violation.

```java
@PostMapping(value = {"/ordine-plata/webhook-banca", "/plati/webhook-banca"})
public ResponseEntity<?> webhookBanca(
        @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
        @RequestBody(required = false) String payload,
        @RequestParam(required = false) UUID ordinId,
        @RequestParam(required = false) String status)
```
- **Security:** Mandatory HMAC-SHA256 signature verification via `Webhook.constructEvent(..., 300)`.
- **Returns:** `200 OK` on valid verified clearing; `400 Bad Request` on invalid/missing signature.

---

### 3.2 `FacturaApplicationService` (Application Layer)

```java
@Transactional
public UUID creeazaOrdinPlata(CreeazaOrdinPlataCommand cmd)
```
- **Transaction:** Required.
- **Flow:** Loads invoice, verifies remaining balance (`suma <= factura.getRestDePlata()`), creates `OrdinPlata` in status `CREAT`.

```java
@Transactional
public void transmiteOrdin(TransmiteOrdinCommand cmd)
```
- **Transaction:** Required.
- **Flow:** Verifies status `APROBAT`, calls `IBancaPlataProvider.transmiteOrdinPlata()`, updates status to `TRANSMIS`.

---

### 3.3 `OrdinPlata` (Domain Layer Aggregate Root)

```java
public void aproba(UUID aprobatorId)
```
- **Invariants:** Status must be `CREAT`. `aprobatorId` must not equal `creatDe` (Four-Eyes Principle).
- **Throws:** `IllegalStateException` on Four-Eyes breach or invalid status.

```java
public void marcheazaPlatit(String referintaBancara)
```
- **Invariants:** Status must be `TRANSMIS`.
- **Side Effects:** Sets status to `EXECUTAT`, records reference, emits `PlataEfectuataEvent`.

---

## 4. SHARED KERNEL & INFRASTRUCTURE SERVICES

### 4.1 `JurnalAuditService` (Security Layer)

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void inregistreazaAcces(String actiune, String obiect)
```
- **Transaction:** `REQUIRES_NEW` (isolated transaction committed independently of outer caller).
- **Persists:** `AuditLogEntry` into `shared_audit_log`.

### 4.2 `OutboxDispatcherService` (Infrastructure Layer)

```java
@Scheduled(fixedDelay = 2000)
@Transactional
public int dispatchUnpublishedEvents()
```
- **Transaction:** Required.
- **Flow:** Polls unpublished events, publishes to Spring Modulith event bus and RabbitMQ topic exchange, updates `published = TRUE`.

