# 03. BUSINESS ANALYSIS & CORE BUSINESS PROCESSES
**System:** Enterprise Procure-to-Pay (P2P) Supply Chain Modular Monolith  
**Namespace:** `ro.uaic.feaa.sdbis.supplychain`

---

## 1. STRATEGIC BUSINESS DOMAIN & VALUE STREAM

In modern enterprise manufacturing and distribution, procurement risk directly impacts liquidity and operational continuity. The Procure-to-Pay (P2P) value stream transforms procurement from a fragmented manual administrative flow into an automated, risk-governed digital pipeline.

```mermaid
flowchart TD
    subgraph S1["1. Sourcing & Qualification"]
        V1["Vendor Registration"] --> V2["Document Validation"]
        V2 --> V3["AQL Baseline & Audit"]
        V3 --> V4{"Vendor Approved?"}
        V4 -- Yes --> V5["Status: Aprobat"]
        V4 -- No --> V6["Status: Respins"]
    end

    subgraph S2["2. Purchasing & Commitments"]
        O1["Requisition Authored"] --> O2["Check Supplier Sanctions"]
        O2 --> O3["Create Purchase Order"]
        O3 --> O4["Supplier Order Confirmation"]
        O4 --> O5["Lead Time Forecasting"]
    end

    subgraph S3["3. Receiving & Quality Control"]
        R1["Goods Arrive at Dock"] --> R2["Generate Reception Lot"]
        R2 --> R3["ISO 2859-1 Sampling"]
        R3 --> R4{"AQL Conformance?"}
        R4 -- Conforms --> R5["Issue Formal NIR"]
        R4 -- Defects Exceed Re --> R6["Issue NCR & Block Lot"]
    end

    subgraph S4["4. Settlement & Banking"]
        P1["Supplier Invoice Ingestion"] --> P2["3-Way Match PO x NIR x Inv"]
        P2 --> P3{"Within +/- 2% Margin?"}
        P3 -- Match OK --> P4["Create OrdinPlata"]
        P3 -- Discrepancy --> P5["Lock Invoice: BLOCATA_DISCREPANTE"]
        P4 --> P6{"Sum > 10,000 EUR?"}
        P6 -- Yes --> P7["Four-Eyes Approval Required"]
        P6 -- No --> P8["Auto-Authorize"]
        P7 --> P9["Transmit to Stripe Bank Rail"]
        P8 --> P9
        P9 --> P10["HMAC-SHA256 Webhook Finalization"]
    end

    S1 --> S2 --> S3 --> S4
```

---

## 2. BUSINESS RULES & REGULATORY GOVERNANCE MATRIX

| Rule ID | Domain Concept | Business Rule Specification | Technical Enforcement Mechanism |
| :--- | :--- | :--- | :--- |
| **BR-PO-01** | Supplier Eligibility | Purchase orders can only be issued to suppliers in status `Aprobat` or `Conditionat`. Candidates or blocked suppliers must be rejected. | `ComandaApplicationService` queries `ICalitateModuleApi.esteEligibilPentruComenzi()`; returns HTTP 422 if false. |
| **BR-PO-02** | Line Uniqueness | An order cannot contain duplicate line items for the same `CodMaterial`. Quantities must be consolidated. | Aggregate invariant in `ComandaAprovizionare.adaugaLinie()`. |
| **BR-PO-03** | Cascade Cancellation | Cancelling an order automatically marks all associated pending shipments and uninspected lots as `ANULAT`. | Synchronous domain mutation in `ComandaAprovizionare.anuleazaComanda()`. |
| **BR-SQ-01** | Sanction Defense | A supplier with 3 consecutive non-conforming lots must be automatically transitioned to `BLOCAT`. | `Furnizor.inregistreazaInspectieNeconforma()`. |
| **BR-SQ-02** | Weighted Scorecard | Monthly evaluation calculates: $\text{Score} = 0.40 \cdot \text{OTIF} + 0.40 \cdot \text{Quality} + 0.20 \cdot \text{Price}$. | Domain Service `CalculScorecardService.calculeaza()`. |
| **BR-SQ-03** | AQL Invariants | For ISO 2859-1 sampling, the rejection threshold $Re$ must strictly equal acceptance threshold $Ac + 1$. | Enforced in domain constructor of `InspectieCalitate` and Flyway `chk_inspectii_aql`. |
| **BR-IP-01** | 3-Way Matching | Quantities and unit prices across Purchase Order, Goods Receipt (NIR), and Invoice must match within $\pm 2\%$. | `ReconciliereService.reconciliaza()`; sets `BLOCATA_DISCREPANTE` if invalid. |
| **BR-IP-02** | Four-Eyes Control | For payment orders $>10,000\text{ EUR}$, the approving officer must differ from the creator (`creatDe \ne aprobatorId`). | Aggregate guard clause in `OrdinPlata.aproba()`. |
| **BR-IP-08** | Sanction Lockout | Invoices belonging to a supplier marked as `BLOCAT` are barred from payment order generation or bank dispatch. | Checked via `FurnizoriBlocatiRepo.esteBlocat(furnizorId)`. |

---

## 3. CORE BUSINESS PROCESS SEQUENCE FLOWS

### Process A: Purchase Order Creation & Validation Flow
```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Procurement Officer
    participant Ctrl as ComandaCtrl
    participant AppSvc as ComandaApplicationService
    participant CalitateApi as ICalitateModuleApi
    participant Repo as ComandaRepoJPA
    participant Outbox as OutboxRepoJPA

    Buyer->>Ctrl: POST /api/comenzi (CreeazaComandaRequest)
    Note over Ctrl: JSR-380 Validation (@Valid)
    Ctrl->>AppSvc: creeazaComanda(CreeazaComandaCommand)
    AppSvc->>CalitateApi: obtineFurnizor(cmd.furnizorId())
    CalitateApi-->>AppSvc: FurnizorSummaryDTO (esteEligibilPentruComenzi)

    alt Supplier is not eligible (BLOCAT or CANDIDAT)
        AppSvc-->>Ctrl: throw BusinessRuleException("Furnizor neeligibil")
        Ctrl-->>Buyer: HTTP 422 Unprocessable Entity
    else Supplier is approved
        AppSvc->>AppSvc: ComandaAprovizionare.creeaza(...)
        AppSvc->>Repo: save(comanda)
        AppSvc->>Outbox: saveEvent(ComandaCreataPublicEvent)
        AppSvc-->>Ctrl: IdComanda
        Ctrl-->>Buyer: HTTP 201 Created (comandaId, numarComanda)
    end
```

---

### Process B: ISO 2859-1 Quality Inspection & NCR Escalation Flow
```mermaid
sequenceDiagram
    autonumber
    actor Insp as Quality Inspector
    participant Ctrl as CalitateCtrl
    participant AppSvc as CalitateApplicationService
    participant Inspectie as InspectieCalitate
    participant Furnizor as Furnizor
    participant Outbox as OutboxRepoJPA

    Insp->>Ctrl: POST /api/inspectii/{id}/verdict (InregistreazaVerdictRequest)
    Ctrl->>AppSvc: inregistreazaVerdict(InregistreazaVerdictCommand)
    AppSvc->>Inspectie: evalueazaConformitate(defecteGasite)

    alt Defects <= Ac (Conforming Lot)
        Inspectie->>Inspectie: stare = ADMIS
        AppSvc->>Furnizor: inregistreazaInspectieConforma()
        AppSvc->>Outbox: saveEvent(VerdictInspectiePublicEvent: CONFORMA)
        Ctrl-->>Insp: HTTP 200 OK (Verdict: ADMIS)
    else Defects >= Re (Non-Conforming Lot)
        Inspectie->>Inspectie: stare = RESPINS
        AppSvc->>Furnizor: inregistreazaInspectieNeconforma()
        Note over Furnizor: If 3 consecutive failures, status becomes BLOCAT
        AppSvc->>AppSvc: deschideRaportNeconformitate(NCR)
        AppSvc->>Outbox: saveEvent(VerdictInspectiePublicEvent: NECONFORMA)
        alt Vendor Blocked Triggered
            AppSvc->>Outbox: saveEvent(FurnizorBlocatPublicEvent)
        end
        Ctrl-->>Insp: HTTP 200 OK (Verdict: RESPINS, NCR generat)
    end
```

---

### Process C: 3-Way Match Reconciliation & Payment Settlement Flow
```mermaid
sequenceDiagram
    autonumber
    actor Fin as Finance Officer
    participant Ctrl as FacturaCtrl
    participant AppSvc as FacturaApplicationService
    participant Reconcil as ReconciliereService
    participant Stripe as StripeBancaPlataAdapter
    participant Webhook as FacturaCtrl (/webhook-banca)

    Fin->>Ctrl: POST /api/facturi/{id}/reconciliere
    Ctrl->>AppSvc: reconcilieazaFactura(cmd)
    AppSvc->>Reconcil: reconciliaza(factura, comanda, nir)

    alt Tolerance Discrepancy > 2%
        Reconcil-->>AppSvc: DiscrepanteDetectateException
        AppSvc->>AppSvc: factura.blocheazaDiscrepante()
        Ctrl-->>Fin: HTTP 409 Conflict (Factura Blocata)
    else Perfect Match within +/- 2%
        Reconcil-->>AppSvc: ReconciliereValida
        AppSvc->>AppSvc: factura.marcheazaReconciliata()
        Ctrl-->>Fin: HTTP 200 OK (Reconciliata)

        Fin->>Ctrl: POST /api/facturi/{id}/ordine-plata
        Ctrl->>AppSvc: creeazaOrdinPlata(cmd)
        AppSvc-->>Ctrl: idOrdin

        Fin->>Ctrl: POST /api/ordine-plata/{id}/transmitere
        Ctrl->>AppSvc: transmiteOrdin(cmd)
        AppSvc->>Stripe: transmiteOrdinPlata(ordin, iban)
        Stripe-->>AppSvc: PaymentIntent created (pi_...)

        Note over Webhook: Asynchronous Bank Callback from Stripe
        Stripe->>Webhook: POST /api/ordine-plata/webhook-banca (Stripe-Signature, Payload)
        Note over Webhook: HMAC-SHA256 Cryptographic Signature Verification
        Webhook->>AppSvc: inregistreazaExecutiePlata(ordinId)
        AppSvc->>AppSvc: ordin.inregistreazaExecutie()
        AppSvc->>AppSvc: factura.inregistreazaPlata()
        Webhook-->>Stripe: HTTP 200 OK (Processed)
    end
```

