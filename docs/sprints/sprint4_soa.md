# ADR-009: Service-Oriented Architecture (SOA) and REST Service Design

## Status

Accepted

---

# Context

Sistemul trebuie să adopte o arhitectură orientată pe servicii (SOA) pentru a asigura:

- modularitate;
- reutilizare;
- scalabilitate;
- interoperabilitate;
- extensibilitate.

Arhitectura serviciilor reprezintă stratul de integrare dintre Business Layer, Persistence Layer și API Layer și constituie baza pentru implementarea REST Web Services. 【1-23bd59】

---

# Architecture Decision

Aplicația va utiliza principii Service-Oriented Architecture (SOA) și REST pentru organizarea logicii aplicației și expunerea funcționalităților prin servicii distribuite. 【1-23bd59】

---

# 1. Service-Oriented Computing Model

## Supported Application Styles

Soluția trebuie să permită evoluția către:

- Monolithic Applications;
- Component-Based Applications;
- Service-Based Applications.

【1-23bd59】

---

## SOA Concepts

Arhitectura trebuie să utilizeze conceptele:

- Service-Oriented Architecture (SOA);
- Service-Oriented Design Paradigm;
- Service-Oriented Solution Logic;
- Services;
- Service Composition;
- Service Inventory.

【1-23bd59】

---

# 2. SOA Design Principles

## Principle 1: Service Contracts

Fiecare serviciu trebuie să expună un contract standardizat. 【1-23bd59】

### Contract Responsibilities

Contractul trebuie să definească:

- termenii de utilizare;
- constrângerile tehnice;
- cerințele funcționale;
- descrierile tehnice și non-tehnice ale serviciului.

【1-23bd59】

### Requirements

- toate serviciile trebuie să urmeze aceleași standarde de contract;
- contractele trebuie să fie ușor de înțeles;
- contractele trebuie să permită interoperabilitatea.

【1-23bd59】

---

## Principle 2: Service Loose Coupling

Serviciile trebuie să fie slab cuplate. 【1-23bd59】

### Requirements

- serviciile trebuie să minimizeze dependențele;
- modificările interne nu trebuie să afecteze consumatorii;
- dependențele dintre servicii trebuie menținute la minimum.

【1-23bd59】

---

## Principle 3: Service Abstraction

Serviciile trebuie să ascundă implementarea internă. 【1-23bd59】

### Requirements

Contractele trebuie să publice doar:

- informațiile esențiale;
- capabilitățile expuse.

Detaliile de implementare trebuie ascunse.

【1-23bd59】

---

## Principle 4: Service Reusability

Serviciile trebuie proiectate ca resurse reutilizabile. 【1-23bd59】

### Requirements

- logica de business trebuie să fie agnostică;
- serviciile trebuie să poată fi reutilizate de mai mulți consumatori;
- serviciile trebuie proiectate pentru reutilizare viitoare.

【1-23bd59】

---

## Principle 5: Service Autonomy

Serviciile trebuie să fie autonome. 【1-23bd59】

### Requirements

- fiecare serviciu trebuie să-și controleze execuția;
- serviciile trebuie să fie independente operațional;
- serviciile trebuie să gestioneze propriile resurse.

【1-23bd59】

---

## Principle 6: Service Statelessness

Serviciile trebuie să minimizeze starea internă. 【1-23bd59】

### Requirements

- informațiile de sesiune nu trebuie păstrate în service;
- starea temporară trebuie externalizată;
- serviciile trebuie să poată fi executate independent.

### Goals

- scalabilitate;
- reutilizare;
- distribuție simplificată.

【1-23bd59】

---

## Principle 7: Service Discoverability

Serviciile trebuie să fie descoperibile. 【1-23bd59】

### Requirements

- metadate descriptive;
- catalog de servicii;
- documentație centralizată;
- convenții uniforme de denumire.

【1-23bd59】

---

## Principle 8: Service Composability

Serviciile trebuie să poată participa în compoziții complexe. 【1-23bd59】

### Requirements

- serviciile trebuie să poată fi orchestrate;
- serviciile trebuie să fie reutilizabile în workflow-uri multiple;
- compozițiile nu trebuie să depindă de implementarea internă.

【1-23bd59】

---

# 3. Service Classification Model

## Orchestration Services

Responsabile pentru:

- procese complexe;
- workflow-uri de business;
- coordonarea altor servicii.

Exemple:

- ProcurementWorkflowService;
- SupplierOnboardingWorkflowService;
- InvoiceApprovalWorkflowService.

【1-23bd59】

---

## Task Services

Responsabile pentru:

- logică de business;
- calcule;
- validări;
- procesări specifice domeniului.

Exemple:

- LeadTimeCalculationService;
- ScorecardCalculationService;
- ThreeWayMatchService.

【1-23bd59】

---

## Entity Services

Responsabile pentru:

- managementul entităților;
- Repository Pattern;
- Factory Pattern.

Exemple:

- SupplierRepository;
- PurchaseOrderRepository;
- InvoiceRepository;
- SupplierFactory.

【1-23bd59】

---

## Utility Services

Responsabile pentru funcționalități transversale.

Exemple:

- AuditService;
- NotificationService;
- LoggingService.

【1-23bd59】

---

# 4. Service Collaboration Architecture

## Composite Service Pattern

Workflow-urile complexe trebuie să fie implementate prin servicii compuse. 【1-23bd59】

### Example Structure

```text
BusinessWorkflowService
 ├── DomainService
 ├── RepositoryService
 ├── FactoryService
 └── AuditService
```

【1-23bd59】

---

## Collaboration Strategies

### Local Facade Collaboration

Serviciile colaborează direct în aceeași aplicație.

【1-23bd59】

### Remote HTTP Facade Collaboration

Serviciile colaborează prin HTTP.

【1-23bd59】

---

# 5. Service Styles

## RPC API

Model orientat pe operații. 【1-23bd59】

### Characteristics

- endpointurile reprezintă metode;
- focus pe acțiuni;
- apelarea proceselor remote.

Exemple:

```http
POST /approveOrder
POST /calculateScorecard
POST /executePayment
```

【1-23bd59】

---

## Resource API

Model orientat pe resurse. 【1-23bd59】

### Characteristics

- focus pe entități;
- CRUD;
- URI-based access.

Exemple:

```http
GET /suppliers
GET /orders
GET /payments
```

【1-23bd59】

---

## Messaging API

Model asincron bazat pe mesaje. 【1-23bd59】

### Examples

- JMS;
- SMTP;
- Event Messaging.

【1-23bd59】

---

# 6. Service Communication Styles

## HTTP / REST

Servicii bazate pe:

- URI;
- JSON;
- XML.

【1-23bd59】

---

## SOAP

Servicii bazate pe:

- SOAP Envelope;
- XML Messages;
- Formal Contracts.

【1-23bd59】

---

## Messaging

Servicii bazate pe:

- JMS;
- Event Driven Communication.

【1-23bd59】

---

# 7. REST Architectural Requirements

## Resources and URIs

Toate resursele trebuie să aibă identificatori unici URI. 【1-23bd59】

### Examples

```text
/api/suppliers
/api/suppliers/{id}

/api/orders
/api/orders/{id}
```

---

## Resource Representation

Resursele trebuie să poată fi reprezentate în mai multe formate:

- JSON;
- XML;
- HTML;
- CSV;
- XLS;
- TXT.

【1-23bd59】

---

## Addressability

Orice informație relevantă trebuie să fie accesibilă prin URI. 【1-23bd59】

### Requirements

- acces direct prin URL;
- acoperire completă a operațiilor CRUD.

【1-23bd59】

---

## Connectedness

Resursele trebuie să fie conectate între ele prin URI-uri. 【1-23bd59】

### Requirements

- hiperlink-uri către resurse conexe;
- navigare între agregate;
- graful relațiilor vizibil public.

【1-23bd59】

---

## Uniform Interface

Toate resursele trebuie gestionate uniform prin HTTP. 【1-23bd59】

### Methods

```http
GET
POST
PUT
PATCH
DELETE
```

---

## Statelessness

Serverul nu trebuie să gestioneze sesiuni client-side. 【1-23bd59】

### Requirements

- sesiunea aparține clientului;
- fiecare request trebuie să fie complet;
- serviciile trebuie să fie independente de context.

【1-23bd59】

---

# 8. DTO Architecture

## Data Transfer Objects

Toate datele expuse prin API trebuie transportate prin DTO-uri. 【1-23bd59】

---

## DTO Lifecycle

Procesul trebuie să includă:

1. Data Cleaning
2. Data Mapping
3. Data Formatting
4. Data Serialization
5. Data Interchange

【1-23bd59】

---

## Supported DTO Formats

### Structured Documents

- XML DTO
- JSON DTO
- CSV DTO
- XLS DTO

【1-23bd59】

---

# 9. DTO Design Strategy

## Structural Strategy

DTO-urile pot fi generate:

### Strategy A

Direct din entități.

### Strategy B

Utilizând modele dedicate:

- EntityView
- DTO Model

【1-23bd59】

---

## Factory Strategy

DTO-urile trebuie create prin:

### Method Factory

Metode definite în entități.

### Class Factory

Factory dedicată DTO-urilor.

【1-23bd59】

---

## Document Builder Strategy

Conversia DTO → XML/JSON trebuie realizată prin:

- JAXB;
- Jackson;
- alte biblioteci de serializare.

【1-23bd59】

---

# Deliverables

## Mandatory

- [ ] Catalog de servicii
- [ ] Clasificarea serviciilor pe tipuri
- [ ] Workflow Services
- [ ] Domain Services
- [ ] Repository Services
- [ ] Utility Services
- [ ] Contracte de servicii
- [ ] DTO Model
- [ ] DTO Factories
- [ ] Resource-based REST API
- [ ] URI Design
- [ ] JSON/XML Support
- [ ] Service Composition Model

---

# Definition of Done

Sprintul SOA este considerat finalizat dacă:

- [ ] serviciile respectă toate cele 8 principii SOA;
- [ ] există clasificare clară între Workflow, Task, Entity și Utility Services;
- [ ] toate funcționalitățile sunt accesibile prin servicii documentate;
- [ ] există DTO-uri pentru schimbul de date;
- [ ] resursele REST sunt identificate prin URI-uri;
- [ ] serviciile sunt stateless;
- [ ] există suport JSON și XML;
- [ ] serviciile pot fi compuse în workflow-uri complexe.

---