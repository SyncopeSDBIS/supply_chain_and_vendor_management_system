# 13. COMPLETE REST API REFERENCE CATALOG
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. COMENZI MODULE ENDPOINTS (`ComandaCtrl`)

### 1.1 Create Purchase Order
- **Method & URI:** `POST /api/comenzi`
- **Consumes:** `application/json`, `application/xml`
- **Produces:** `application/json`, `application/xml`
- **Request Body:** [CreeazaComandaRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/presentation/CreeazaComandaRequest.java)
- **Validation Rules:** `@NotBlank numarComanda`, `@NotNull furnizorId`, `@NotNull @FutureOrPresent dataLivrareCeruta`, `@NotBlank depozit`, `@NotEmpty @Valid linii`.
- **Responses:**
  - `201 Created`: `{"comandaId": "UUID", "numarComanda": "PO-2026-001", "status": "Solicitat"}`
  - `400 Bad Request`: Validation failure.
  - `422 Unprocessable Entity`: Supplier ineligible (blocked or candidate).
- **Security:** Requires `ROLE_CUMPARATOR` or `ROLE_ADMIN`.

### 1.2 Confirm Purchase Order Delivery Date
- **Method & URI:** `POST /api/comenzi/{id}/confirmare`
- **Parameters:** Path variable `id` (UUID), Query param `dataPromisa` (ISO Date string).
- **Responses:** `200 OK`, `400 Bad Request`.
- **Side Effects:** Transitions order to `Confirmat`; evaluates lead time deviation.

### 1.3 Atomic Cascade Cancellation
- **Method & URI:** `POST /api/comenzi/{id}/anulare`
- **Responses:** `200 OK`, `400 Bad Request`.
- **Side Effects:** Order status becomes `Anulat`; uninspected lots transitioned to `Anulat`; publishes `ComandaAnulataPublicEvent`.

### 1.4 Lead Time Estimation Calculation
- **Method & URI:** `GET /api/comenzi/{id}/lead-time`
- **Responses:** `200 OK`: `{"estimareZile": 14.5, "metoda": "LinearRegression"}`.
- **SLA Target:** $<300\text{ ms}$ response time benchmark.

---

## 2. CALITATE MODULE ENDPOINTS (`CalitateCtrl`)

### 2.1 Register Candidate Supplier
- **Method & URI:** `POST /api/furnizori`
- **Consumes:** `application/json`, `application/x-www-form-urlencoded`
- **Request DTO:** [InregistreazaFurnizorRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/presentation/InregistreazaFurnizorRequest.java)
- **Validation Rules:** `@NotBlank @Size(2..30) codFiscal`, `@NotBlank @Size(2..255) denumire`, `@Pattern(IBAN) iban`, `@Email email`.
- **Responses:** `201 Created` with generated `furnizorId`.

### 2.2 Qualify Supplier
- **Method & URI:** `POST /api/furnizori/{id}/calificare`
- **Parameters:** Path `id`, Query `decizie` (`Aprobat`, `Conditionat`, `Respins`), Query `motiv`.
- **Responses:** `200 OK`, `400 Bad Request`.

### 2.3 Record ISO 2859-1 AQL Inspection Verdict
- **Method & URI:** `POST /api/inspectii/{id}/verdict`
- **Request DTO:** [InregistreazaVerdictRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/presentation/InregistreazaVerdictRequest.java)
- **Validation Rules:** `@PositiveOrZero int defecteGasite`.
- **Responses:** `200 OK`, `400 Bad Request`.
- **Side Effects:** If defects $\ge Re$, transitions to `RESPINS`, opens `RaportNeconformitate`, updates supplier defect counts.

### 2.4 Calculate Monthly Vendor Scorecard
- **Method & URI:** `POST /api/furnizori/{id}/scorecard-lunar`
- **Request DTO:** [CalculScorecardRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/presentation/CalculScorecardRequest.java)
- **Validation Rules:** `@Min(2020) @Max(2050) an`, `@Min(1) @Max(12) luna`, `@DecimalMin("0.0") @DecimalMax("100.0") rataRespingere`.
- **Responses:** `200 OK`, `400 Bad Request`.

---

## 3. FACTURI MODULE ENDPOINTS (`FacturaCtrl`)

### 3.1 Register Supplier Invoice
- **Method & URI:** `POST /api/facturi`
- **Consumes:** `application/json`, `application/x-www-form-urlencoded`
- **Request DTO:** [InregistreazaFacturaRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/InregistreazaFacturaRequest.java)
- **Validation Rules:** `@NotBlank serie`, `@NotBlank numar`, `@NotNull dataEmitere`, `@NotNull dataScadenta`, `@NotNull comandaId`, `@NotNull furnizorId`, `@Positive cantitate`, `@PositiveOrZero pretUnitar`, `@Pattern(EUR|RON|USD) moneda`.
- **Responses:** `201 Created` (`{"facturaId": "UUID", "status": "Inregistrata"}`).

### 3.2 Execute 3-Way Match Reconciliation
- **Method & URI:** `POST /api/facturi/{id}/reconciliere`
- **Responses:**
  - `200 OK`: `{"status": "SUCCESS", "message": "Reconcilierea a fost executata"}`
  - `409 Conflict`: Discrepancy exceeds tolerance ($\pm 2\%$) or NIR missing (`status: BLOCATA_DISCREPANTE`).

### 3.3 Create Payment Order
- **Method & URI:** `POST /api/facturi/{id}/ordine-plata`
- **Request DTO:** [CreeazaOrdinPlataRequest.java](file:///c:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/presentation/CreeazaOrdinPlataRequest.java)
- **Validation Rules:** `@Positive suma`, `@Pattern(EUR|RON|USD) moneda`, `@NotNull creatDe`.
- **Responses:** `201 Created` with generated `ordinId`.

### 3.4 Cryptographic Stripe Bank Webhook Callback
- **Method & URI:** `POST /api/ordine-plata/webhook-banca`, `POST /api/plati/webhook-banca`
- **Headers:** `Stripe-Signature` (Mandatory; HMAC-SHA256 signature with timestamp).
- **Request Body:** Raw JSON Stripe payload (`payment_intent.succeeded` or `payment_intent.payment_failed`).
- **Validation Rules:** Cryptographic verification via `Webhook.constructEvent(payload, sigHeader, secret, 300)`.
- **Responses:**
  - `200 OK`: `{"ordinId": "UUID", "status": "Executat"}` (or `status: RespinsDeBanca`).
  - `400 Bad Request`: Missing signature, invalid signature, or expired timestamp.
- **Audit Logging:** Every attempt is recorded in `shared_audit_log`.

