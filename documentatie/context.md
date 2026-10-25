# ADR-001 - Supply Chain and Vendor Management System Domain Design

## Status

Accepted

---

# 1. Business Vision

## Problem Statement

Companiile de producție gestionează manual sau fragmentat:

- furnizori
- comenzi de aprovizionare
- recepții
- inspecții de calitate
- facturi
- plăți

Acest lucru conduce la:

- întârzieri
- lipsă de trasabilitate
- dificultăți de audit
- risc financiar
- performanță redusă a furnizorilor

## Goal

Construirea unei platforme centralizate de gestionare a procesului Procure-to-Pay (P2P), integrând:

- Procurement
- Supplier Management
- Quality Management
- Invoice Management
- Payment Management

---

# 2. Stakeholders

## Internal

### Procurement Manager

Responsabil de:

- creare comenzi
- monitorizare livrări
- negociere furnizori

### Buyer

Responsabil de:

- lansare comenzi
- modificare comenzi

### Warehouse Operator

Responsabil de:

- recepții
- gestionare loturi

### Quality Inspector

Responsabil de:

- verificări AQL
- inspecții
- NCR-uri

### Finance Specialist

Responsabil de:

- verificare facturi
- reconciliere
- plăți

### Finance Manager

Responsabil de:

- aprobări plăți

### System Administrator

Responsabil de:

- configurare
- audit
- securitate

---

# 3. Ubiquitous Language

| Termen | Definiție |
|----------|------------|
| Purchase Order | Comandă de aprovizionare |
| Lot | Livrare fizică asociată unei comenzi |
| Supplier | Furnizor |
| NCR | Non-Conformance Report |
| Three Way Match | Verificare Comandă-NIR-Factură |
| OTIF | On Time In Full |
| AQL | Acceptable Quality Level |
| GRN/NIR | Goods Receipt Note |

---

# 4. Domain Landscape

## Core Domain

### Procurement Order Management

Responsabilități:
- creare PO
- validare PO
- confirmare PO
- recepție PO
- anulare PO

---

## Supporting Domain

### Supplier & Quality Management

Responsabilități:
- onboarding furnizori
- evaluare
- inspecții
- scorecard

---

## Supporting Domain

### Invoice & Payment Management

Responsabilități:
- facturi
- reconciliere
- plăți

---

## Generic Domains

### Notification Management

### Security & Audit

---

# 5. Business Processes

## Procure To Pay Flow

1. Buyer creează comandă
2. Supplier confirmă
3. Supplier expediază
4. Warehouse recepționează
5. Quality efectuează inspecție
6. Se generează NIR
7. Furnizorul emite factura
8. Se realizează Three Way Match
9. Se inițiază plata
10. Plata este executată

---

# 6. Actors and Use Cases

## Procurement

### UC-01 Create Purchase Order

Actor:
- Buyer

Preconditions:
- Supplier activ

Postconditions:
- PO creat în stare Requested

---

### UC-02 Confirm Order

Actor:
- Supplier

Rezultat:
- Confirmed
sau
- Rejected

---

### UC-03 Receive Goods

Actor:
- Warehouse Clerk

Rezultat:
- Lot recepționat

---

### UC-04 Perform Quality Inspection

Actor:
- Inspector

Rezultat:
- Pass
- Fail

---

### UC-05 Register Invoice

Actor:
- Finance Specialist

---

### UC-06 Execute Payment

Actor:
- Finance Manager

---

# 7. Domain Model

## Aggregate: PurchaseOrder

### Aggregate Root

PurchaseOrder

### Entities

- PurchaseOrderLine
- ShipmentLot

### Value Objects

- OrderNumber
- Money
- Quantity
- DeliveryAddress

---

## Aggregate: Supplier

### Root

Supplier

### Entities

- SupplierCategory
- SupplierContract
- SupplierScorecard

### Value Objects

- TaxId
- Address
- IBAN

---

## Aggregate: Inspection

### Root

QualityInspection

### Entities

- InspectionResult
- Defect

### Value Objects

- AQL
- InspectionStatus

---

## Aggregate: Invoice

### Root

SupplierInvoice

### Entities

- InvoiceLine

### Value Objects

- InvoiceNumber
- VAT
- Currency

---

## Aggregate: Payment

### Root

PaymentOrder

### Value Objects

- PaymentAmount
- BankAccount

---

# 8. State Machines

## Purchase Order

Requested
→ Confirmed
→ Partially Received
→ Received

Requested
→ Cancelled

Requested
→ Rejected

---

## Supplier

Candidate
→ Evaluation
→ Approved

Approved
→ Conditional

Conditional
→ Approved

Conditional
→ Suspended

Suspended
→ Blocked

---

## Invoice

Draft
→ Registered
→ Matched
→ Approved
→ Paid

Registered
→ Blocked

---

# 9. Business Rules

## Procurement Rules

BR-PO-01 ...
BR-PO-02 ...
...
BR-PO-08 ...

## Supplier Rules

BR-SQ-01 ...
...
BR-SQ-10 ...

## Finance Rules

BR-IP-01 ...
...
BR-IP-09 ...

---

# 10. Domain Events

## Procurement Events

- PurchaseOrderCreated
- PurchaseOrderConfirmed
- PurchaseOrderRejected
- ShipmentRecorded
- GoodsReceived
- PurchaseOrderCancelled

## Supplier Events

- SupplierApproved
- SupplierConditional
- SupplierSuspended
- SupplierBlocked

## Quality Events

- InspectionCompleted
- NCRCreated
- NCRClosed

## Finance Events

- InvoiceRegistered
- InvoiceMatched
- PaymentApproved
- PaymentExecuted

---

# 11. Integration Contracts

## Synchronous APIs

### Procurement → Supplier

validateSupplier()

### Finance → Procurement

getOrderSummary()

---

## Asynchronous Events

GoodsReceivedEvent
InspectionCompletedEvent
SupplierBlockedEvent
InvoiceApprovedEvent

---

# 12. KPI Metrics

## Procurement

- OTIF
- Average Lead Time
- Order Fulfillment Rate

## Supplier

- Supplier Score
- NCR Rate

## Finance

- Invoice Match Rate
- Payment Delay Rate

---

# 13. Non Functional Requirements

## Performance

<300 ms lead time estimation

## Security

RBAC
GDPR
Audit Trail

## Reliability

Idempotent Event Consumers

## Scalability

5000 invoices/minute

## Consistency

ACID transactions

---

# 14. Acceptance Criteria

Sprint 1 este acceptat dacă:

- există Use Case Diagram
- există Activity Diagram
- există Domain Model
- există Aggregate Design
- există Business Rules
- există State Diagrams
- există Event Catalogue
- există Architecture Context Diagram
- există Bounded Context Diagram