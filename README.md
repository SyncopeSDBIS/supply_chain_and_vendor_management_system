# Supply Chain and Vendor Management System

**Master**: SDBIS, Year I[cite: 1]  
**Disciplines**: Multi-tier Software Development & Object Oriented Analysis and Design[cite: 1]  
**Architecture**: Modular Monolith · Layered Architecture · Domain-Driven Design (Strategic & Tactical DDD)[cite: 1]

---

## Contents
- [1. Sprint 1 - Kickoff: Obiectiv & Tema Aleasă](#1-sprint-1---kickoff-obiectiv--tema-aleasă)
- [2. Echipa de Practică (Structură completă SDLC)](#2-echipa-de-practică-structură-completă-sdlc)
- [3. Setup Tehnic & Metodologic](#3-setup-tehnic--metodologic)
- [4. Cerințe Funcționale și Non-Funcționale (Detaliere pe Subdomenii)](#4-cerințe-funcționale-și-non-funcționale-detaliere-pe-subdomenii)
  - [4.1 Procurement Order Management (Core Domain)](#41-procurement-order-management-managementul-comenzilor-de-aprovizionare--core-domain)
  - [4.2 Supplier & Quality Management (Supporting Domain 1)](#42-supplier--quality-management-gestiunea-furnizorilor-și-a-calității--supporting-domain-1)
  - [4.3 Invoice & Payment Management (Supporting Domain 2)](#43-invoice--payment-management-gestionarea-facturilor-și-a-plăților--supporting-domain-2)
  - [4.4 Catalogul Ciclului de Viață al Obiectelor](#44-catalogul-ciclului-de-viață-al-obiectelor)
  - [4.5 Matricea Cerințelor Non-Funcționale Măsurabile (SLA)](#45-matricea-cerințelor-non-funcționale-măsurabile-sla)
- [5. Backlog Inițial (Product Backlog & Sprint 1)](#5-backlog-inițial-product-backlog--sprint-1)
- [6. Scenarii de Acceptanță (BDD / Gherkin)](#6-scenarii-de-acceptanță-bdd--gherkin)
  - [6.1 Sprint 1 Target Scenarios (Core Domain - EN)](#61-sprint-1-target-scenarios-core-domain---en)
  - [6.2 Specificații Gherkin Complete pe Subdomenii (RO - Ubiquitous Language)](#62-specificații-gherkin-complete-pe-subdomenii-ro---ubiquitous-language)
- [7. Decizii de Arhitectură (ADR 001 & ADR 002)](#7-decizii-de-arhitectură-adr-001--adr-002)
  - [ADR 001 - Alegerea Stack-ului Tehnologic](#adr-001---alegerea-stack-ului-tehnologic)
  - [ADR 002 - Stil Arhitectural (Modular Monolith)](#adr-002---stil-arhitectural-modular-monolith)
- [8. Diagrama C4 Context & Strategic DDD Context Map](#8-diagrama-c4-context--strategic-ddd-context-map)
- [9. Diagrama de Activitate (Cross-Bounded Contexts)](#9-diagrama-de-activitate-cross-bounded-contexts)
- [10. Diagrame de Cazuri de Utilizare (Use Case Diagrams) & Descrieri Narative](#10-diagrame-de-cazuri-de-utilizare-use-case-diagrams--descrieri-narative)
- [11. Diagrama de Clase (Tactical DDD Domain Model & Invarianți)](#11-diagrama-de-clase-tactical-ddd-domain-model--invarianți)
- [12. Diagrama ERD & Schema Bazei de Date](#12-diagrama-erd--schema-bazei-de-date)

---

## 1. Sprint 1 - Kickoff: Obiectiv & Tema Aleasă

Proiectul vizează construirea unui sistem enterprise de tip **Supply Chain and Vendor Management** pentru o companie de producție care achiziționează materii prime de la furnizori externi[cite: 1], conceput ca un **Modular Monolith** utilizând o arhitectură stratificată (**Layered Architecture**) bazată pe **Domain-Driven Design (DDD)**.

Sistemul digitalizează și optimizează fluxurile operaționale, fiind împărțit strategic în **3 Bounded Contexts** principale (plus 2 subdomenii generice pentru *Notificări* și *Securitate & Audit GDPR*)[cite: 1]:
- **Procurement Order Management (Core Domain)**: Gestiunea operațiilor CRUD privind comanda (creare, confirmare, expediere, anulare atomică în cascadă), estimarea dinamică a timpului de livrare (*Lead Time Strategies*) și trasabilitatea strictă a loturilor de marfă (`Lots`)[cite: 1].
- **Supplier & Quality Management (Supporting Domain 1)**: Ciclul de calificare a furnizorilor (`Candidat`, `În evaluare`, `Aprobat`, `Condiționat`, `Suspendat`, `Blocat`), evaluarea lunară a performanței, gestiunea inspecțiilor de calitate (AQL) și managementul rapoartelor de neconformitate (`NCR`)[cite: 1].
- **Invoice & Payment Management (Supporting Domain 2)**: Înregistrarea facturilor de la furnizori, generarea notelor de recepție (`NIR`), reconcilierea tranzacțiilor (*3-way match* între Comandă–NIR–Factură) și aprobarea/execuția plăților bancare[cite: 1].

---

## 2. Echipa de Practică (Structură completă SDLC)

Conform cerințelor stricte de proiect (echipe de exact 3 persoane: *analysts*, *developers*, *testers*), echipa este formată din:

- **DURNEA ANDREI-SEBASTIAN**[cite: 1] — **Analyst (Product Owner Proxy / Business Analyst)**  
  Analizează cerințele de business, extrage regulile (invarianții) de domeniu, construiește diagramele UML (Use Cases, Activity Diagram, Domain Model) și redactează User Story-urile în limbaj Gherkin (BDD).
- **BALAN MUGUREL-IONUȚ**[cite: 1] — **Developer (Tech Lead / Software Engineer)**  
  Modelează arhitectura stratificată, implementează infrastructura (Java + Spring Boot, PostgreSQL) și aplică design pattern-urile (Strategy, Factory, Singleton, Facade) în Core Domain. De asemenea, dezvoltă interfețele client (React/Angular).
- **GURZU TEODOR**[cite: 1] — **Tester (QA / DevOps)**  
  Validează criteriile de acceptanță (SLA), scrie testele automate (inclusiv pentru algoritmii de performanță P90 / Exponential Smoothing), configurează repository-ul Git (GitHub) și asigură integrarea continuă (pipeline minimal CI/CD build+test).

---

## 3. Setup Tehnic & Metodologic

- **Version Control & CI/CD**: GitHub (repository-ul proiectului, organizat pe branch-uri dedicate și pipeline minimal CI/CD pentru build + test).
- **Agile Board**: Jira Software / Kanban board. Sprint-uri stabilite la 2 săptămâni, axate pe livrabile funcționale clare.
- **Documentație Arhitectură**: PlantUML (pentru vizualizarea modelelor de domeniu, C4, ERD și a fluxurilor prin link-uri externe live)[cite: 1] și Markdown (pentru deciziile de arhitectură ADR).

---

## 4. Cerințe Funcționale și Non-Funcționale (Detaliere pe Subdomenii)

În cadrul unei companii de producție care achiziționează materii prime de la furnizori externi, aplicația web permite utilizatorilor interni (cumpărători, gestionari de depozit, manageri și inspectori de calitate, contabili și responsabili financiari) să gestioneze întregul lanț de aprovizionare[cite: 1]. Furnizorii externi nu au conturi în aplicație, ci comunică cu personalul companiei și primesc notificări sau solicitări de note de credit[cite: 1].

### 4.1 Procurement Order Management (Managementul comenzilor de aprovizionare — Core Domain)
Reprezintă motorul avantajului competitiv al organizației[cite: 1]. Include trasabilitatea strictă a loturilor (cicluri de viață independente) și izolarea invarianților prin agregatul rădăcină (`ComandaAprovizionare` / `ProcurementOrder`)[cite: 1].

Cerințele funcționale grupate în acest domeniu sunt[cite: 1]:
- **Crearea, editarea, anularea sau ștergerea comenzilor de achiziție**: La crearea unei comenzi, trebuie completate următoarele câmpuri: numărul comenzii, furnizorul, data comenzii, data solicitată pentru livrare, depozitul de destinație, cumpărătorul (responsabilul cu achizițiile), liniile comenzii (codul materialului, cantitatea, unitatea de măsură, prețul unitar) și observațiile. În momentul creării, starea comenzii va fi `Solicitată`. O comandă poate fi plasată doar către un furnizor care are starea `Aprobată` sau `Condiționată` (verificare sincronă). Atunci când o comandă este `Anulată`, toate liniile și loturile acesteia care nu au fost încă recepționate sunt, de asemenea, anulate[cite: 1].
- **Confirmarea furnizorului**: După transmiterea comenzii, furnizorul trebuie fie să o confirme (precizând data de livrare promisă), fie să o refuze, în termen de 48 de ore. Comanda devine, astfel, `Confirmată` sau `Refuzată`. Dacă nu se primește niciun răspuns în intervalul de 48 de ore, cumpărătorul este notificat și poate solicita un răspuns din partea furnizorului, poate anula comanda sau o poate plasa la un alt furnizor[cite: 1].
- **Înregistrarea expedierii**: Odată ce mărfurile părăsesc sediul furnizorului, expedierea (transportatorul, numărul AWB/scrisorii de transport și loturile incluse) este înregistrată, iar comanda trece în starea `În tranzit`. O comandă confirmată poate fi expediată în mai multe loturi[cite: 1].
- **Recepția mărfurilor**: Gestionarul înregistrează cantitățile recepționate efectiv, pe loturi. Dacă totalul recepționat este mai mic decât totalul comandat, comanda devine `Recepționată parțial` (fiind permise recepții ulterioare); atunci când cantitățile comandate sunt acoperite, comanda devine `Recepționată`, iar recepția este transmisă asincron prin *Transactional Outbox Pattern* (`ComandaReceptionataEvent` / `OrderReceivedEvent`) către celelalte subdomenii (generarea NIR-ului, controlul calității)[cite: 1].
- **Urmărirea lotului**: Fiecare lot livrat are un cod de lot, cantitate, data de producție/expirare și propriul ciclu de viață (`Planificat`, `Expediat`, `Sosit în depozit`, `În carantină`, `Eliberat în stoc`, `Respins`, `Returnat furnizorului`), astfel încât fiecare materie primă din stoc poate fi urmărită până la furnizor și comanda de la care a venit[cite: 1].
- **Estimarea duratei de livrare**: Aplicația estimează data de livrare a unei comenzi pe baza istoricului de livrări al furnizorului (strategii privind durata de livrare: media mobilă, netezire exponențială, P90). Estimarea este recalculată în momentul confirmării comenzii, precum și în cazul în care livrarea întârzie peste un prag configurabil; cumpărătorul este notificat atunci când data estimată depășește data solicitată[cite: 1].
- **Monitorizarea livrărilor**: Aplicația oferă rapoarte și tablouri de bord pentru urmărirea livrărilor: stadiul comenzii, cantitățile comandate / recepționate / rămase, indicatorul OTIF (livrări la timp și complete), întârzierile, precum și comparații între datele de livrare solicitate, promise, estimate și cele efective[cite: 1].

**Reguli cheie de validare (Validation Rules)**:
- *Delivery Period Boundary*: Data de livrare solicitată trebuie să fie ulterioară plasării comenzii[cite: 1].
- *Cancellation Guard*: Anularea declanșează o cascadare atomică (*Atomic Cascading Transition*) pe loturile nerecepționate (toate anulate tranzacțional ACID)[cite: 1].
- *Quantity Consistency*: Recepția pe loturi nu poate depăși cantitatea comandată plus marja de toleranță[cite: 1].

### 4.2 Supplier & Quality Management (Gestiunea furnizorilor și a calității — Supporting Domain 1)
Acest domeniu se concentrează pe gestionarea furnizorilor, evaluarea performanței acestora și controlul calității bunurilor livrate[cite: 1]:
- **Gestionarea profilului furnizorilor**: Aplicația gestionează datele furnizorilor: cod fiscal, denumire, adresă, adrese de e-mail de contact, cont bancar (IBAN), categorii de materiale și termeni contractuali. Un furnizor parcurge un ciclu de viață care include stările: `Candidat`, `În evaluare`, `Aprobat`, `Condiționat`, `Suspendat` și `Blocat`. Departamentul de achiziții adaugă și modifică datele, în timp ce managerul de calitate decide asupra statutului de calificare[cite: 1].
- **Evaluarea performanței (calificativ)**: La sfârșitul fiecărei luni, pentru fiecare furnizor activ se calculează un scor pe baza a trei indicatori: livrarea la timp și integrală (OTIF, pondere 40%), calitatea (rata de respingere, pondere 40%) și conformitatea prețului (pondere 20%). Scorul rezultat (0–100) corespunde unui calificativ: **A** (≥ 90), **B** (80–89), **C** (60–79) și **D** (< 60). Un furnizor care primește calificativul C este trecut în categoria `Condiționat`; după trei luni consecutive cu calificativul B sau mai bun, acesta revine în categoria `Aprobat`[cite: 1].
- **Gestionarea inspectorilor de calitate**: Aplicația gestionează profilurile inspectorilor, certificările acestora (inclusiv datele de expirare) și disponibilitatea lor. Un inspector poate fi alocat unei inspecții doar dacă certificarea sa este validă și acesta este disponibil în perioada respectivă[cite: 1].
- **Inspecție de calitate**: Când un lot ajunge în depozit, este programată automat o inspecție dacă planul de prelevare (AQL) al materialului o impune; lotul se pune `În carantină` până la pronunțarea verdictului. Inspecția trece prin stările `Programată`, `Inspector alocat`, `În desfășurare`, `Conformă` / `Neconformă`, `În reinspecție`, `Închisă` (sau `Anulată`). Verdictul se aplică asupra lotului (`Eliberat în stoc` sau `Respins`)[cite: 1].
- **Managementul neconformității**: Un raport de neconformitate (NCR) este deschis pentru fiecare inspecție eșuată. Trece prin analiza cauzei principale, acțiuni corective, verificare și închidere (`Deschis`, `În analiză cauzei`, `Acțiune corectivă în curs`, `Verificat`, `Închis`, `Escaladat`). Două inspecții eșuate consecutive ale unui furnizor condiționat duc la suspendarea acestuia[cite: 1].
- **Monitorizarea furnizorilor**: Aplicația generează rapoartele solicitate de către cumpărători și de managerul calității: fișe de evaluare, rate de respingere, rapoarte de neconformitate (NCR) deschise, precum și istoricul calificativelor pentru fiecare furnizor[cite: 1].

### 4.3 Invoice & Payment Management (Gestionarea facturilor și a plăților — Supporting Domain 2)
Acest domeniu grupează cerințele funcționale referitoare la facturile de la furnizori, notele de recepție a mărfurilor și plăți[cite: 1]:
- **Înregistrarea facturilor de la furnizori**: Contabilul înregistrează facturile primite de la furnizori (serie și număr, dată emitere, scadență, referință comandă, linii și TVA). O factură pornește în starea `Înregistrată`[cite: 1].
- **Generarea notei de recepție (NIR)**: Pentru fiecare recepție, aplicația generează o NIR (Notă de Recepție) care conține loturile și cantitățile recepționate. NIR-ul parcurge stările `Generat`, `În reconciliere`, `Cu discrepanțe`, `Reconciliat`, `Contabilizat` (sau `Stornat`)[cite: 1].
- **Reconciliere (potrivire triplă / three-way match)**: Factura este verificată în raport cu comanda (prețul și cantitatea comandată) și cu NIR-ul (cantitatea primită). Dacă diferențele se încadrează în toleranța configurată (±2%), factura devine `Aprobată la plată`; altfel este `Blocată (discrepanță)` până când NIR-ul sau factura sunt corectate, sau factura este `Contestată` și se solicită o notă de credit de la furnizor[cite: 1].
- **Ordine de plată**: Pentru fiecare factură aprobată, ofițerul financiar creează un ordin de plată (`Creat`). Ordinele peste pragul de aprobare (10.000 EUR) necesită un al doilea aprobator (`În aprobare`); ordinele aprobate sunt transmise băncii (`Transmis la bancă`) și se termină ca `Executat` sau `Respins de bancă`. O factură poate fi plătită în mai multe rate (`Parțial plătită` → `Plătită`)[cite: 1].
- **Monitorizarea termenelor de scadență**: Responsabilul financiar verifică periodic termenele de scadență; o notificare este trimisă cu trei zile înainte de data scadenței unei facturi aprobate, dar neplătite. Facturile unui furnizor care devine `Blocat` nu sunt transmise spre plată[cite: 1].

### 4.4 Catalogul Ciclului de Viață al Obiectelor

*Tabelul 1.1. Catalogul ciclului de viață al principalelor obiecte (stări pe subdomenii)*[cite: 1]

| Obiect | Subdomeniu | Stare (Lifecycle States) |
| :--- | :--- | :--- |
| **Order (`ComandaAprovizionare`)** | Core | `Solicitat`, `Confirmat`, `Refuzat`, `În tranzit`, `Recepționat parțial`, `Recepționat`, `Anulat`[cite: 1] |
| **Lot (`Lot`)** | Core | `Planificat`, `Expediat`, `Sosit în depozit`, `În carantină`, `Eliberat în stoc`, `Respins`, `Returnat` (`Anulat`)[cite: 1] |
| **Supplier (`Furnizor`)** | Suport 1 – Calitate | `Candidat`, `În evaluare`, `Aprobat`, `Condiționat`, `Suspendat`, `Blocat`[cite: 1] |
| **Quality inspection (`InspecțieCalitate`)** | Suport 1 – Calitate | `Programată`, `Inspector alocat`, `În desfășurare`, `Conformă`, `Neconformă`, `În reinspecție`, `Închisă`, `Anulată`[cite: 1] |
| **Non-conformity report (`NCR`)** | Suport 1 – Calitate | `Deschis`, `În analiza cauzei`, `Acțiune corectivă în curs`, `Verificat`, `Închis`, `Escaladat`[cite: 1] |
| **Supplier invoice (`FacturăFurnizor`)** | Suport 2 – Facturare | `Înregistrată`, `În verificare`, `Blocată`, `Contestată`, `Aprobată la plată`, `Parțial plătită`, `Plătită`, `Anulată`[cite: 1] |
| **Payment order (`OrdinPlată`)** | Suport 2 – Facturare | `Creat`, `În aprobare`, `Aprobat`, `Transmis la bancă`, `Executat`, `Respins de bancă`, `Anulat`[cite: 1] |
| **Goods-receipt note (`NIR`)** | Suport 2 – Facturare | `Generat`, `În reconciliere`, `Cu discrepanțe`, `Reconciliat`, `Contabilizat`, `Stornat`[cite: 1] |

### 4.5 Matricea Cerințelor Non-Funcționale Măsurabile (SLA)

*Tabelul 1.2. Matricea cerințelor non-funcționale măsurabile (SLA și criterii de acceptanță)*[cite: 1]

| Categorie | Cerință non-funcțională măsurabilă | Criterii de acceptare (SLA / Verificare) |
| :--- | :--- | :--- |
| **Performanță** | Timp de răspuns pentru algoritmii de estimare a duratei de realizare (media mobilă / netezirea exponențială / P90)[cite: 1] | Latență **< 300 ms** pentru istoricul furnizor–material, incluzând până la 1.000 de livrări anterioare[cite: 1]. |
| **Scalabilitate și tranzacții** | Procesare în lot a reconcilierii asincrone în trei părți la sfârșitul lunii[cite: 1] | Sistemul trebuie să reconcilieze cel puțin **5.000 de facturi** de la furnizori cu notele de intrare-recepție (NIR-uri) aferente, în **mai puțin de 60 de secunde**[cite: 1]. |
| **Securitate și GDPR** | Segregarea accesului la datele bancare ale furnizorilor (IBAN), la prețurile contractuale și la ordinele de plată[cite: 1] | Codurile IBAN, prețurile negociate și datele de plată sunt accesibile strict rolurilor autorizate (Finanțe / Achiziții). Toate accesările generează un jurnal de audit imuabil[cite: 1]. |
| **Consistență tranzacțională** | Starea de consistență la anularea comenzii[cite: 1] | Trecerea unei comenzi în starea `Anulat` declanșează o tranziție atomică în cascadă a tuturor loturilor sale încă nerecepționate în starea `Anulat`, în cadrul aceleiași tranzacții ACID[cite: 1]. |
| **Fiabilitatea integrării** | Evenimente de domeniu livrate cel puțin o dată între contexte delimitate[cite: 1] | Consumatorii de evenimente sunt idempotenți: relivrarea aceluiași eveniment `ComandaRecepționatăEvent` nu trebuie să genereze niciodată un al doilea NIR (fapt verificat printr-un test automatizat)[cite: 1]. |
| **Control financiar** | Nicio plată fără o reconciliere reușită[cite: 1] | Un ordin de plată poate fi creat doar pentru o factură aflată în starea `Aprobată la plată`; 100% dintre încercările de a eluda această regulă sunt respinse de modelul de domeniu[cite: 1]. |

---

## 5. Backlog Inițial (Product Backlog & Sprint 1)

- **EPIC 1 (`E1`)**: **Managementul Comenzilor de Aprovizionare (Core Domain)** — *Targetul Sprintului 1*[cite: 1]
- **EPIC 2 (`E2`)**: **Gestiunea Calității și Evaluarea Furnizorilor (Supporting Domain 1)**[cite: 1]
- **EPIC 3 (`E3`)**: **Reconcilierea Facturilor și Plățile (Supporting Domain 2)**[cite: 1]

*Table 1. Initial Product Backlog (User Stories)*

| ID | Epic | User Story |
| :-: | :-: | :--- |
| 1 | E1 | As a Procurement Officer (Buyer), I want to create, edit, or cancel purchase orders for eligible suppliers (`Aprobat` or `Condiționat`), so that raw materials are ordered only from qualified vendors and unreceived lots are atomically cancelled[cite: 1]. |
| 2 | E1 | As a Buyer, I want to record supplier confirmations or refusals within 48 hours and compute lead time estimations, so that I am alerted if the supplier does not respond or if the estimated delivery date exceeds the requested date[cite: 1]. |
| 3 | E1 | As a Buyer, I want to register shipments (carrier, AWB, included lots) and monitor OTIF and delivery dashboards, so that orders in transit are tracked accurately[cite: 1]. |
| 4 | E1 | As a Warehouse Keeper, I want to record received quantities per batch (lot), so that the order status updates (`Recepționată parțial` / `Recepționată`) and quality control and invoicing are triggered via domain events[cite: 1]. |
| 5 | E2 | As a Quality Manager, I want to manage supplier qualification statuses and calculate monthly scorecards (40% OTIF, 40% quality, 20% price compliance), so that supplier grades (A, B, C, D) reflect real performance[cite: 1]. |
| 6 | E2 | As a Quality Manager, I want arriving lots to be placed in quarantine when AQL requires inspection and to assign available and certified quality inspectors, so that only qualified personnel verify batches[cite: 1]. |
| 7 | E2 | As a Quality Inspector, I want to execute quality inspections and record the verdict (`Conformă` / `Neconformă`), so that batches are either released to stock or rejected[cite: 1]. |
| 8 | E2 | As a Quality Manager, I want a Non-Conformity Report (NCR) opened for every failed inspection and conditioned suppliers suspended after two consecutive failed inspections, so that quality risks are controlled[cite: 1]. |
| 9 | E3 | As an Accountant, I want to register supplier invoices and have Goods-Receipt Notes (NIR) generated idempotently upon goods reception, so that duplicate events never create duplicate NIRs[cite: 1]. |
| 10 | E3 | As an Accountant, I want to reconcile supplier invoices against the purchase order and NIR (three-way match with ±2% tolerance), so that matching invoices become `Aprobată la plată` and discrepant ones are blocked or contested[cite: 1]. |
| 11 | E3 | As a Financial Officer, I want to create and execute payment orders only for reconciled invoices (requiring a second approver above 10,000 EUR and supporting installment payments), so that no payment occurs without reconciliation[cite: 1]. |
| 12 | E3 | As a Financial Officer, I want to receive notifications 3 days before invoice due dates and block payment transmissions to `Blocat` suppliers, so that financial control is maintained[cite: 1]. |

---

## 6. Scenarii de Acceptanță (BDD / Gherkin)

### 6.1 Sprint 1 Target Scenarios (Core Domain - EN)

#### `sprint1_create_procurement_order.feature`
```gherkin
Feature: Create a procurement order
  As a Procurement Officer
  I want to place an order with an eligible supplier
  So that raw materials are delivered in time for production

  Background:
    Given the supplier "Metalurgica Nord SRL" exists
    And the buyer "Ioana Popescu" is logged in

  Scenario: Successfully create an order with an approved supplier
    Given the supplier is in the state "APPROVED"
    When the buyer creates the order "CA-2026-0415" with 500 kg of "OTEL-S235"
    And the requested delivery date is "2026-11-20"
    Then the order is registered with the state "REQUESTED"
    And a "OrderCreatedEvent" domain event is recorded
```

#### `sprint1_order_goods_reception.feature`
```gherkin
Feature: Order goods reception
  As a Warehouse Keeper
  I want to register the received quantities against the confirmed order
  So that stock, quality control and invoicing start from accurate data

  Background:
    Given a confirmed order "CA-2026-0398" is in the state "IN_TRANSIT"

  Scenario Outline: Register a reception and derive the order state
    When the warehouse keeper receives <received> units
    Then the order state becomes "<state>"
    And an "<event>" domain event is recorded

    Examples:
      | received | state              | event                 |
      | 600      | PARTIALLY_RECEIVED | PartialReceptionEvent |
```

---

### 6.2 Specificații Gherkin Complete pe Subdomenii (RO - Ubiquitous Language)

#### E1 — Funcționalitatea 1: `procurement_order_creation_and_cancellation.feature`[cite: 1]
```gherkin
Feature: Crearea și anularea comenzilor de aprovizionare
  În calitate de Cumpărător
  Doresc să plasez comenzi doar la furnizori eligibili și să anulez comenzi într-un mod consecvent
  Astfel încât materiile prime să fie achiziționate de la furnizori calificați și să nu rămână loturi „orfane” (fără asociere)

  Background:
    Given că utilizatorul este autentificat cu rolul "Cumpărător"
    And depozitul "DEP-IASI-01" există

  Scenario: Crearea cu succes a unei comenzi pentru un furnizor aprobat
    Given că furnizorul "SUP-101" are starea "Aprobat"
    When cumpărătorul înregistrează comanda "PO-2026-0145" pentru furnizorul "SUP-101", cu livrare solicitată la data "2026-11-20" în depozitul "DEP-IASI-01"
    And adaugă linia de comandă pentru materialul "MAT-2201", cantitatea 1000 "kg", preț unitar 12,40 "EUR"
    Then comanda este salvată cu starea "Solicitat"
    And comanda conține 1 linie de comandă
    And cumpărătorul autentificat este desemnat drept cumpărătorul comenzii

  Scenario Outline: Crearea comenzii depinde de starea de calificare a furnizorului
    Given că furnizorul "SUP-102" are starea "<stare>"
    When cumpărătorul încearcă să înregistreze comanda "PO-2026-0146" pentru furnizorul "SUP-102"
    Then crearea comenzii are rezultatul "<rezultat>"
    And "<persistență>"

    Examples:
      | stare       | rezultat | persistență                                   |
      | Aprobat     | acceptat | comanda este salvată cu starea "Solicitat"    |
      | Condiționat | acceptat | comanda este salvată cu starea "Solicitat"    |
      | Candidat    | respins  | nicio entitate de tip comandă nu este salvată |
      | În evaluare | respins  | nicio entitate de tip comandă nu este salvată |
      | Suspendat   | respins  | nicio entitate de tip comandă nu este salvată |
      | Blocat      | respins  | nicio entitate de tip comandă nu este salvată |

  Scenario: Anularea unei comenzi anulează loturile care nu au fost încă recepționate
    Given comanda "PO-2026-0145" cu starea "Confirmat"
    And având lotul "L-001" cu starea "Expediat"
    And având lotul "L-002" cu starea "Planificat"
    And având lotul "L-003" cu starea "Sosit în depozit"
    When cumpărătorul anulează comanda "PO-2026-0145"
    Then starea comenzii devine "Anulat"
    And loturile "L-001" și "L-002" devin "Anulat"
    And lotul "L-003" rămâne în starea "Sosit în depozit"
    And toate modificările sunt confirmate (commit) în cadrul aceleiași tranzacții ACID
    And un eveniment de tip "ComandaAnulataEvent" este înregistrat în "outbox"

  Scenario: Anularea este atomică
    Given comanda "PO-2026-0145" cu starea "În tranzit"
    And în condițiile în care lotul "L-001" al comenzii nu poate fi trecut în starea "Anulat"
    When cumpărătorul anulează comanda "PO-2026-0145"
    Then anularea este respinsă
    And comanda rămâne în starea "În tranzit"
    And niciun lot nu își schimbă starea

  Scenario: O comandă recepționată integral nu poate fi anulată
    Given comanda "PO-2026-0145" cu starea "Recepționat"
    When cumpărătorul anulează comanda "PO-2026-0145"
    Then comanda (de anulare) este respinsă, generând o eroare de încălcare a stării (state violation)
    And starea tranzacțională a comenzii rămâne neschimbată
```

#### E1 — Funcționalitatea 2: `goods_reception_by_lots.feature`[cite: 1]
```gherkin
Feature: Recepția mărfurilor pe loturi
  În calitate de gestionar de depozit
  Doresc să înregistrez cantitățile recepționate efectiv pentru fiecare lot
  Astfel încât starea comenzii să reflecte livrările reale, iar celelalte subdomenii să fie informate

  Background:
    Given că utilizatorul este autentificat cu rolul "Gestionar de depozit"
    And comanda "PO-2026-0145" pentru 1000 "kg" din articolul "MAT-2201" are starea "În tranzit"
    And aceasta conține lotul "L-001" de 600 "kg" și lotul "L-002" de 400 "kg", ambele având starea "Expediat"

  Scenario: Recepția parțială menține comanda deschisă
    When gestionarul de depozit înregistrează 600 "kg" recepționate pentru lotul "L-001"
    Then lotul "L-001" devine "Sosit în depozit"
    And starea comenzii devine "Recepționată parțial"
    And cantitatea rămasă din comandă este de 400 "kg"
    And sunt permise recepții ulterioare

  Scenario: Ultima recepție acoperă cantitatea comandată
    Given că lotul "L-001" a fost recepționat cu 600 "kg"
    When gestionarul de depozit înregistrează 400 "kg" recepționate pentru lotul "L-002"
    Then starea comenzii devine "Recepționată"
    And un eveniment "ComandaReceptionataEvent", care conține loturile și cantitățile recepționate, este înregistrat în coada de ieșire (outbox)

  Scenario Outline: Starea comenzii este determinată de cantitatea cumulată recepționată
    When cantitatea recepționată, însumată pe toate loturile comenzii, ajunge la <received> "kg"
    Then starea comenzii este "<status>"

    Examples:
      | received | status               |
      | 600      | Recepționată parțial |
      | 999      | Recepționată parțial |
      | 1000     | Recepționată         |

  Scenario: Recepția este respinsă pentru o comandă care nu a fost expediată
    Given că comanda "PO-2026-0150" are starea "Confirmat"
    When gestionarul depozitului înregistrează 100 "kg" recepționate pentru lotul "L-010" din comanda "PO-2026-0150"
    Then comanda este respinsă, generând o eroare de încălcare a stării
    And starea tranzacțională a comenzii rămâne neschimbată
```

#### E2 — Funcționalitatea 1: `supplier_monthly_scorecard.feature`[cite: 1]
```gherkin
Feature: Fișa de evaluare lunară a furnizorilor și statutul de calificare
  În calitate de Manager de Calitate
  Îmi doresc ca fiecare furnizor activ să fie evaluat lunar pe baza unor indicatori obiectivi
  Astfel încât statutul de calificare să reflecte performanța reală a furnizorului
  Scorul (0-100) este compus din: 40% OTIF (livrare la timp și completă), 40% calitate (100 minus rata de respingere) și 20% conformitate a prețului.
  Calificativul este A (90 sau peste), B (80-89), C (60-79) sau D (sub 60).

  Background:
    Given că furnizorul "SUP-101" are statutul "Aprobat"
    And performanța livrărilor pentru "SUP-101" aferentă lunii "2026-10" a fost preluată din contextul de gestionare a comenzilor

  Scenario Outline: Calcularea scorului și a calificativului pe baza celor trei indicatori
    Given că rata OTIF este de <otif> procente
    And rata de respingere este de <rejection> procente
    And conformitatea prețului este de <price> procente
    When se calculează fișa de evaluare lunară
    Then scorul este <score>
    And calificativul este "<grade>"

    Examples:
      | otif | rejection | price | score | grade |
      | 95   | 8         | 90    | 92.8  | A     |
      | 90   | 10        | 90    | 90.0  | A     |
      | 85   | 15        | 80    | 84.0  | B     |
      | 70   | 25        | 65    | 71.0  | C     |
      | 60   | 40        | 60    | 60.0  | C     |
      | 50   | 45        | 60    | 54.0  | D     |

  Scenario: Un furnizor cu calificativul "C" devine "Condiționat"
    When fișa de evaluare lunară a furnizorului "SUP-101" indică calificativul "C"
    Then starea furnizorului "SUP-101" devine "Condiționat"
    And se pot plasa în continuare comenzi către "SUP-101"

  Scenario: Un furnizor cu starea "Condiționat" revine la starea "Aprobat" după trei luni consecutive cu rezultate bune
    Given că furnizorul "SUP-101" are starea "Condiționat"
    And calificativele din cele două luni anterioare – începând cu cea mai veche – au fost "B" și "A"
    When fișa de evaluare lunară a furnizorului "SUP-101" indică calificativul "B"
    Then starea furnizorului "SUP-101" devine "Aprobat"

  Scenario: O serie întreruptă nu duce la revenirea la starea "Aprobat"
    Given că furnizorul "SUP-101" are starea "Condiționat"
    And calificativele din cele două luni anterioare – începând cu cea mai veche – au fost "C" și "B"
    When fișa de evaluare lunară a furnizorului "SUP-101" indică calificativul "A"
    Then starea furnizorului "SUP-101" rămâne "Condiționat"
```

#### E2 — Funcționalitatea 2: `lot_quality_inspection.feature`[cite: 1]
```gherkin
Feature: Inspecția calității unui lot sosit
  În calitate de Manager Calitate
  Îmi doresc ca loturile sosite să fie inspectate de inspectori certificați și disponibili
  Astfel încât doar materialele conforme să fie introduse în stoc, iar neconformitățile să fie gestionate

  Background:
    Given că materialul "MAT-2201" are un plan de eșantionare AQL care necesită inspecție
    And lotul "L-001" din comanda "PO-2026-0145" a sosit în depozit

  Scenario: O inspecție este programată automat, iar lotul este plasat în carantină
    When este primit evenimentul "ComandaReceptionataEvent" pentru lotul "L-001"
    Then se creează o inspecție cu starea "Programată"
    And lotul "L-001" este plasat "În carantină"

  Scenario Outline: Un inspector este alocat doar dacă deține o certificare validă și este disponibil
    Given că inspecția lotului "L-001" este planificată pentru data de "2026-11-05"
    And inspectorul "INS-07" deține o certificare care expiră la data de "<expiry>"
    And inspectorul "INS-07" este <availability> în data de "2026-11-05"
    When managerul de calitate îl alocă pe inspectorul "INS-07" inspecției
    Then alocarea este <result>
    And starea inspecției devine "<status>"

    Examples:
      | expiry     | availability | result    | status           |
      | 2027-03-31 | disponibil   | acceptată | Inspector alocat |
      | 2026-11-05 | disponibil   | acceptată | Inspector alocat |
      | 2026-11-04 | disponibil   | respinsă  | Programată       |
      | 2027-03-31 | indisponibil | respinsă  | Programată       |

  Scenario: Un verdict de conformitate eliberează lotul în stoc
    Given că inspecția lotului "L-001" este în stadiul "În desfășurare"
    When inspectorul înregistrează verdictul "Conformă"
    Then starea inspecției devine "Conformă"
    And un eveniment de tip "VerdictInspectieEvent" este înregistrat în "outbox"
    And lotul "L-001" devine "Eliberat în stoc" după ce evenimentul este preluat de contextul de gestionare a comenzilor (Order Management)

  Scenario: O inspecție cu rezultat negativ generează un raport de neconformitate (NCR) și duce la respingerea lotului
    Given că inspecția lotului "L-001" este în stadiul "În desfășurare"
    When inspectorul înregistrează verdictul "Neconformă"
    Then starea inspecției devine "Neconformă"
    And se deschide un raport de neconformitate cu starea "Deschis"
    And lotul "L-001" devine "Respins"

  Scenario: Două inspecții consecutive cu rezultat negativ pentru un furnizor aflat în regim condiționat duc la suspendarea acestuia
    Given că furnizorul "SUP-101" are starea "Condiționat"
    And inspecția anterioară a unui lot de la "SUP-101" a avut verdictul "Neconformă"
    When inspecția lotului "L-001" de la furnizorul "SUP-101" se încheie cu verdictul "Neconformă"
    Then starea furnizorului "SUP-101" devine "Suspendat"
    And responsabilii cu achizițiile sunt notificați
```

#### E3 — Funcționalitatea 1: `nir_and_three_way_match.feature`[cite: 1]
```gherkin
Feature: Generarea notei de recepție și reconcilierea prin verificare în trei puncte (three-way match)
  În calitate de contabil,
  Îmi doresc ca fiecare factură de la furnizor să fie verificată prin raportare la comandă și la nota de recepție,
  Astfel încât nicio factură să nu fie aprobată pentru plată dacă prezintă diferențe nejustificate de preț sau cantitate.

  Background:
    Given că utilizatorul este autentificat cu rolul "Contabil"
    And există comanda "PO-2026-0145" pentru 1000 "kg" din articolul "MAT-2201", la un preț unitar de 12,40 "EUR"
    And toleranța de reconciliere configurată este de 2 procente

  Scenario: Se generează un NIR pentru fiecare recepție
    When este primit evenimentul "ComandaReceptionataEvent" pentru 1000 "kg" din articolul "MAT-2201", aferent comenzii "PO-2026-0145"
    Then se creează un NIR cu starea "Generat"
    And NIR-ul conține loturile și cantitățile recepționate

  Scenario: Un eveniment retransmis nu creează un al doilea NIR
    Given că evenimentul "ComandaReceptionataEvent" cu identificatorul "EVT-9001" a fost deja procesat
    When evenimentul "ComandaReceptionataEvent" cu identificatorul "EVT-9001" este transmis din nou
    Then nu se creează un al doilea NIR
    And mesajul este confirmat (primit cu succes)

  Scenario Outline: Reconcilierea facturii cu comanda și NIR-ul
    Given că NIR-ul "NIR-2026-0311" înregistrează o cantitate recepționată de <received> "kg"
    And factura "FF-8841" este înregistrată cu prețul unitar <price> "EUR" și cantitatea <invoiced> "kg"
    When contabilul reconciliază factura "FF-8841"
    Then starea facturii devine "<invoice status>"
    And starea NIR-ului devine "<nir status>"

    Examples:
      | price | invoiced | received | invoice status        | nir status     |
      | 12.40 | 1000     | 1000     | Aprobată la plată     | Reconciliat    |
      | 12.60 | 1000     | 1000     | Aprobată la plată     | Reconciliat    |
      | 12.40 | 1020     | 1000     | Aprobată la plată     | Reconciliat    |
      | 12.40 | 1000     | 985      | Aprobată la plată     | Reconciliat    |
      | 12.65 | 1000     | 1000     | Blocată (discrepanță) | Cu discrepanțe |
      | 12.40 | 1000     | 950      | Blocată (discrepanță) | Cu discrepanțe |

  Scenario: O factură blocată care nu poate fi corectată este contestată
    Given factura "FF-8841" este în starea "Blocată (discrepanță)"
    When contabilul contestă factura "FF-8841" cu motivul "preț unitar peste prețul contractual"
    Then starea facturii devine "Contestată"
    And o cerere de nota de credit este trimisă furnizorului
```

#### E3 — Funcționalitatea 2: `payment_orders.feature`[cite: 1]
```gherkin
Feature: Ordine de plată și control financiar
  În calitate de Responsabil Financiar
  Doresc ca ordinele de plată să poată fi create și aprobate doar pentru facturile reconciliate
  Astfel încât să nu se efectueze nicio plată fără o reconciliere reușită

  Background:
    Given că utilizatorul este autentificat cu rolul "Responsabil Financiar"
    And pragul de aprobare pentru ordinele de plată este de 10.000 "EUR"

  Scenario Outline: Un ordin de plată poate fi creat doar pentru o factură aprobată pentru plată
    Given că factura "FF-8841", în valoare de 5.000 "EUR", are starea "<invoice status>"
    When responsabilul financiar creează un ordin de plată de 5.000 "EUR" pentru factura "FF-8841"
    Then crearea ordinului de plată este <result>

    Examples:
      | invoice status        | result    |
      | Înregistrată          | respinsă  |
      | În verificare         | respinsă  |
      | Blocată (discrepanță) | respinsă  |
      | Contestată            | respinsă  |
      | Anulată               | respinsă  |
      | Aprobată la plată     | acceptată |

  Scenario Outline: Ordinele care depășesc pragul de aprobare necesită un al doilea aprobator
    Given că factura "FF-8842", în valoare de <amount> "EUR", are starea "Aprobată la plată"
    When responsabilul financiar creează un ordin de plată de <amount> "EUR" pentru factura "FF-8842"
    Then starea ordinului de plată este "<status>"

    Examples:
      | amount | status      |
      | 8500   | Creat       |
      | 10000  | Creat       |
      | 25000  | În aprobare |

  Scenario: Un al doilea aprobator validează un ordin de plată care depășește pragul stabilit
    Given că ordinul de plată "OP-501", în valoare de 25.000 "EUR", are starea "În aprobare"
    When un al doilea aprobator aprobă ordinul de plată "OP-501"
    Then starea ordinului de plată devine "Aprobat"
    And ordinul de plată poate fi transmis către bancă

  Scenario: Prima tranșă duce la plata parțială a facturii
    Given că factura "FF-8843", în valoare de 20.000 "EUR", are starea "Aprobată la plată"
    When banca execută ordinul de plată "OP-601", în valoare de 12.000 "EUR", pentru factura "FF-8843"
    Then starea facturii devine "Parțial plătită"
    And suma rămasă de plată este de 8.000 "EUR"

  Scenario: Ultima tranșă finalizează plata
    Given că factura "FF-8843" are starea "Parțial plătită", cu o sumă rămasă de 8.000 "EUR"
    When banca execută ordinul de plată "OP-602", în valoare de 8.000 "EUR", pentru factura "FF-8843"
    Then starea facturii devine "Plătită"

  Scenario: Ordinele de plată către un furnizor blocat nu sunt transmise
    Given că ordinul de plată "OP-701" are starea "Aprobat"
    And furnizorul facturii devine "Blocat"
    When responsabilul financiar transmite ordinul de plată "OP-701" către bancă
    Then transmiterea este respinsă
    And ordinul de plată nu este trimis către bancă

  Scenario: Notificare cu trei zile înainte de data scadenței
    Given că factura "FF-8844" are starea "Aprobată la plată" și nu este plătită
    And data scadenței acesteia este "2026-11-20"
    When verificarea datei scadenței este executată la data de "2026-11-17"
    Then se trimite o notificare responsabilului financiar pentru factura "FF-8844"
```

---

## 7. Decizii de Arhitectură (ADR 001 & ADR 002)

### ADR 001 - Alegerea Stack-ului Tehnologic
- **Context**: Aplicația necesită un ecosistem matur capabil să izoleze logica complexă de business (DDD), să gestioneze tranzacții ACID (mai ales pe zona de agregare Comenzi) și să integreze asincron fluxurile (*Domain Events* prin *Transactional Outbox Pattern*)[cite: 1].
- **Opțiuni Analizate**: `Java + Spring Boot + PostgreSQL` vs. `C# (.NET Core)` vs. `Node.js (NestJS) + MySQL`.
- **Decizie**: Se adoptă **Java + Spring Boot**, cu baza de date relațională **PostgreSQL**, iar pentru frontend framework-ul **React** (sau **Angular**).
- **Consecințe**:
  - **PostgreSQL vs. MySQL**: S-a optat pentru PostgreSQL în primul rând pentru că este un sistem de baze de date 100% open-source și gratuit (fără complicațiile de licențiere corporate ale MySQL). Mai mult, pentru un modul de facturare unde integritatea datelor este critică, PostgreSQL oferă o conformitate ACID mai strictă și performanțe superioare în executarea interogărilor complexe necesare proceselor de reconciliere.
  - **Java vs. Node.js (NestJS)**: Pentru calculele de reconciliere și procesarea unor volume mari de tranzacții financiare, Java oferă un avantaj major de performanță datorită arhitecturii sale *multi-threading*. În schimb, Node.js funcționează pe un singur fir de execuție (*single-threaded*), ceea ce ar fi putut crea blocaje (*bottlenecks*) în timpul calculelor financiare intensive.
  - **Maturitatea ecosistemului Spring (vs. .NET/NestJS)**: Deși C# (.NET Core) este o alternativă competitivă, ecosistemul Java/Spring a fost preferat datorită maturității sale în aplicațiile enterprise. Uneltele precum Spring Data JPA și Spring Web oferă instrumente extrem de rafinate și gata de utilizare pentru arhitecturile de tip Domain-Driven Design (DDD), facilitând enorm crearea de *Repositories* și mecanisme de *Event Publishing*.

### ADR 002 - Stil Arhitectural (Modular Monolith)
- **Context**: Suntem o echipă mică și trebuie să lansăm aplicația rapid. Vrem să ne mișcăm repede, dar fără să ajungem la un cod dezordonat (*"spaghetti code"*) pe care să nu-l mai putem repara mai târziu.
- **Decizie**: Construim aplicația ca un **Monolit Modular** (o singură aplicație, dar cu codul împărțit strict pe module/piese de puzzle).
- **Consecințe**:
  - **Zero bătăi de cap cu infrastructura (vs. Microservicii)**: Nu pierdem timpul configurând rețele complexe și comunicare între zeci de serviciușoare. Echipa se concentrează doar pe scrierea funcționalităților de business.
  - **Livrăm mai repede**: Având un singur proiect, testarea, rularea și publicarea aplicației pe server (deployment-ul) se fac dintr-un singur pas simplu.
  - **Păstrăm ordinea (vs. Monolitul Clasic)**: Chiar dacă e o singură aplicație mare, o organizăm în „cutii” (module) strict separate (ex: modulul de Facturi nu se amestecă direct cu cel de Stocuri/Comenzi)[cite: 1].
  - **Suntem pregătiți pentru viitor**: Pentru că am păstrat modulele separate și ordonate, dacă aplicația crește foarte mult peste câțiva ani, va fi foarte ușor să extragem un modul și să-l transformăm într-un microserviciu independent.
  - **Riscul**: Sub presiunea timpului, programatorii ar putea trișa și ar putea lega direct modulele între ele în mod incorect.

---

## 8. Diagrama C4 Context & Strategic DDD Context Map

### 8.1 Diagrama C4 Context
Personalul intern (Cumpărători, Gestionari Depozit, Manageri/Inspectori Calitate, Contabili, Responsabili Financiari) interacționează cu aplicația prin interfața web React/Angular. Furnizorii externi și banca interacționează prin schimburi de mesaje/notificări și confirmări bancare, iar datele sunt persistate în PostgreSQL[cite: 1].

- 🔗 **Link extern C4 Context Diagram**: [Deschide Diagrama C4 Context (PlantUML PNG)](https://www.plantuml.com/plantuml/png/ZPRVRzis4CVV_LSSCY18W6HntBNDYjIewxXOiALrOcDxgFD0KsU927NKU9GTRTZ_Fb6oOjfou2flvDttFlVZ67zY9wpBKzrnocc4IS4EKnWQSlZiu4Q9s8eK5iR29CyoNS0m4OhW6Fv4YihJEq4YnXJ9hNqx7NvIb0dlTeCBaMitBQK6MiLKcKaaXpQmcYGYCYj5CIo4PWnk7anEqT1OGWkNtL3PedI2OetmRmVAxxEGJx6jpRMnrzTZj6oetuUVkbU_zDxFAwilXjpk_IfH3ciDOoEqRV-tlT7RTvtzOKRF3Yq9lJ7_jVh2SA7TeR1jlQk1_tibV5roRtPv-Uvg7WLXm_jsk9R_c-7xByFkWImRyqDP7WZRjcko_A_JsNJswEF7iJKoj_LuVLiib4GxesjM_-27osx_Ursbn-iBVzAVqOo65eL31a4HfDLeCcIvbObWX0g1psQ4Iw7poawZYC2f5Fdd8mYwBXZcUO5s6-SlOJ4nEIFyZfWTOdd0MB53on0R4x6VMSoSCkMLntEsB7D9O5mxzge9vEFiKjpdGYjNm2rnLfwR0nZboSBObE7ljP-g_3m8pyXGnQMM65KeS3Au_mCoBUXqbweHQEBkO0saD3avGUv7ow9eQPIik-Kf34cbiOHo2QQmKYwfsJAFzUPy9Gf8XPD9cqoynDuPeEhHKjkTQgOFzeoFeOMoAJUZ0foeRDs_ixfjYZd73Nk5qYY6IBoEl0_ey2iQyjOd8UPTCzd1UcYrG5b8ZINqIz9DoqzOcX93s8XFptov6_wNiYlX1wBAfewtpa8kKzv9uQjnQg5avS0mGRiiXVOMk4r-epZpBVHDrsWTO2gKhYS0b-XdXxRa1HSa4sl8vAoBhMg7dA7JccWR-hEW9qLnMDx3jADdbBdph5bcZL-HJ1HfHLaF29nCHkF1nUta6_IwtLxlz5N0UHc_-GTPlInm7faLdVVh4iC7E6gwkfO-VkaZp-ZxRzFfUF9urDbv6Tegc_NAaeAL57hpy8H2kylyEa_xtV1mNuSFeTnc0ryJ2lRzkAbZgD1as_PljZAip3nLRgCOo7HgjqPaps1uoN4-rueJv7hgoaP6g4N1Pn3b295moAUbwE3kVWmNy32QJ3rXg1nqr0jEhI3sJ4rgQrWlChxrN9-G8lz3wty0)

![C4 Context Diagram](https://www.plantuml.com/plantuml/png/ZPRVRzis4CVV_LSSCY18W6HntBNDYjIewxXOiALrOcDxgFD0KsU927NKU9GTRTZ_Fb6oOjfou2flvDttFlVZ67zY9wpBKzrnocc4IS4EKnWQSlZiu4Q9s8eK5iR29CyoNS0m4OhW6Fv4YihJEq4YnXJ9hNqx7NvIb0dlTeCBaMitBQK6MiLKcKaaXpQmcYGYCYj5CIo4PWnk7anEqT1OGWkNtL3PedI2OetmRmVAxxEGJx6jpRMnrzTZj6oetuUVkbU_zDxFAwilXjpk_IfH3ciDOoEqRV-tlT7RTvtzOKRF3Yq9lJ7_jVh2SA7TeR1jlQk1_tibV5roRtPv-Uvg7WLXm_jsk9R_c-7xByFkWImRyqDP7WZRjcko_A_JsNJswEF7iJKoj_LuVLiib4GxesjM_-27osx_Ursbn-iBVzAVqOo65eL31a4HfDLeCcIvbObWX0g1psQ4Iw7poawZYC2f5Fdd8mYwBXZcUO5s6-SlOJ4nEIFyZfWTOdd0MB53on0R4x6VMSoSCkMLntEsB7D9O5mxzge9vEFiKjpdGYjNm2rnLfwR0nZboSBObE7ljP-g_3m8pyXGnQMM65KeS3Au_mCoBUXqbweHQEBkO0saD3avGUv7ow9eQPIik-Kf34cbiOHo2QQmKYwfsJAFzUPy9Gf8XPD9cqoynDuPeEhHKjkTQgOFzeoFeOMoAJUZ0foeRDs_ixfjYZd73Nk5qYY6IBoEl0_ey2iQyjOd8UPTCzd1UcYrG5b8ZINqIz9DoqzOcX93s8XFptov6_wNiYlX1wBAfewtpa8kKzv9uQjnQg5avS0mGRiiXVOMk4r-epZpBVHDrsWTO2gKhYS0b-XdXxRa1HSa4sl8vAoBhMg7dA7JccWR-hEW9qLnMDx3jADdbBdph5bcZL-HJ1HfHLaF29nCHkF1nUta6_IwtLxlz5N0UHc_-GTPlInm7faLdVVh4iC7E6gwkfO-VkaZp-ZxRzFfUF9urDbv6Tegc_NAaeAL57hpy8H2kylyEa_xtV1mNuSFeTnc0ryJ2lRzkAbZgD1as_PljZAip3nLRgCOo7HgjqPaps1uoN4-rueJv7hgoaP6g4N1Pn3b295moAUbwE3kVWmNy32QJ3rXg1nqr0jEhI3sJ4rgQrWlChxrN9-G8lz3wty0)

---

### 8.2 Figura 1. Strategic DDD Context Map (Bounded Contexts & Integration Contracts)
Relațiile dintre cele trei *Bounded Contexts* sunt formalizate prin contracte de integrare explicite: un *Shared Kernel* (`IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`, `Perioada`), relații sincrone *Customer/Supplier* prin adaptoare REST, *Domain Events* asincrone gestionate prin *Transactional Outbox* și *Published Language* (`PerformantaLivrariDTO`, `ComandaSummaryDTO`)[cite: 1].

- 🔗 **Link extern Figura 1**: [Deschide Strategic DDD Context Map (PlantUML PNG)](https://www.plantuml.com/plantuml/png/hLRRRjj64xxNKmnY8ByD-63dRs8dI89899MgiIr5KdDZ-M94ZgXjbxl47fp8HP-eZz2xF5bdIL477u84hGmOvExCDzz-S-2-imwDyuLiEU4amSWPT9IB5E8uXaWhHrySN609kntjLKPPisZXAVGKsxA3qAfQDfWwkzUolmjLei42x1mp_Lce76OeBMtiP3H3BzsOaTfIvAeWvI0b1ZGRLePIXofdOd-q08Q1GQID8WCdptcXeqr69j9I6zWvwpnlF-kq_jnmRnkZFo-tVwf-TpUxp7eaRWbEJbgjTRGdN_yQpT5G1X-8myclVqzKf8j2AnWlIh8JrSlu7LM6-z3BkjueSQjDU3xNRZyOPnVeo0YK-p0W8pHc-0JGmkW3x7IxtMTHj1Nm0XNc55Jm4bBD3xSYyCu8i3JwHjoooCneedPP0OAOemkrDr4JjIJItZI3tNQU6ygP0ykc7HioCTXDb1DkmM-9TQ9WmtF2R2mA6f6v4IdxrS2mVD-hE4Vz2oOT9vtuU8lqUs8KhmXXjbIWucm58AGeXKCd13CU-RBKnd4TL8GRkHuWsLEsP7njejgT7hSHz0sR8OQ0bvHgDTEcUCoC4L8lHoahcgB9lYb2zoFi96VTusxoc0QOEhzIe9JemlC3nw_iy1igNFQ63wpsJIRKW67neGE7ewP22ZBhN7Qy5OgilNEG0H_aPVSqwMuTv58xCHCiWmZqsvAC2zNsdXGPaLRCOs5BTEcS3Aoj9wf65nuILat24GoydLUXBljZZXL-swADAFMcJifJQFjCE7WV3uPtmhMz8xKC4mo7dNOqKR-mRYYNNht2EvmAMRTM4gsZ_GyE3WwWwfNN1syXv228mp0VVQYRHTXlSL8FhdPEkYzEpk9hDWkL_WfUJzz6tZfTa3bg75yVJT_2BkkP6gtsOPYCnaoI_T-iiQz-lTuF4BpM-CDLV3rH8VsleDVKIfDwqSwmT1HgX7TMouaKkU13xRK25lFhl3mzFKu2FuvNqLiMQ793ojcQ6gwuzRsRwYzxgyd0jK5b6DBeiFAOA8OgZSuD5a5QqNGKwx4TZmdyIC1LQmvgy2RWFl3q6QNU2HVJogZUcgXFP3AHkkrruEZMCJi4gPsNNZHgd9oSjbyytrHZuATIs3bbS8ugzvXJpR1VaeAVjGqDCY0J9W4gX-VYng0HyRXV1Mcw7wHNQBOgXxTxwaQBD9JV01TXFeVT1n8p-2yI4m9oywzdkl9oOwyPCJ2LEaL73OrL_JAD7ofVzhzRlmnnhtnxKVL1QierC5aI7FcYGBDWAHlTxZVQuofo_lw5Y4sxT2en6etgmGkwDC9HGsevLLwjnbOGtMvcyhHx5jKIyinY5biKLlceFoZ2h0jx_IMDgKGRsZWYmt_rFX_nEz0les5YKvIOeMjW6QKPVQkFOlAb5AO17dm7y2Z5Z9-gkq4f6jBtL5cEuyLg60TXmZGzlDg9Zl__ybdd-d1Z5gpQglckL2cwQWzwjJjNJ71-ivw7Hw5LZ-hIuegwXytR3wmD6g9RTx6Qt0SuF0pGzHFxDm_i92adbO4H-ToreBxeMSWrp8mkmCq9V6cT8ImWNTvmcIdiLXUeZ0pe6MJe4BG1gfJSOnYdArU-pwgxpZ4xyulr1PczmnRS5rJOAiIC4L7A0DHuss0_vlVjGuPT5Cg2Ld818cGnu7r2wGdwqzyeTNOVDgwPW2fRtJGFMon0BKFh7V_pXVm7)

![Figura 1. Strategic DDD Context Map](https://www.plantuml.com/plantuml/png/hLRRRjj64xxNKmnY8ByD-63dRs8dI89899MgiIr5KdDZ-M94ZgXjbxl47fp8HP-eZz2xF5bdIL477u84hGmOvExCDzz-S-2-imwDyuLiEU4amSWPT9IB5E8uXaWhHrySN609kntjLKPPisZXAVGKsxA3qAfQDfWwkzUolmjLei42x1mp_Lce76OeBMtiP3H3BzsOaTfIvAeWvI0b1ZGRLePIXofdOd-q08Q1GQID8WCdptcXeqr69j9I6zWvwpnlF-kq_jnmRnkZFo-tVwf-TpUxp7eaRWbEJbgjTRGdN_yQpT5G1X-8myclVqzKf8j2AnWlIh8JrSlu7LM6-z3BkjueSQjDU3xNRZyOPnVeo0YK-p0W8pHc-0JGmkW3x7IxtMTHj1Nm0XNc55Jm4bBD3xSYyCu8i3JwHjoooCneedPP0OAOemkrDr4JjIJItZI3tNQU6ygP0ykc7HioCTXDb1DkmM-9TQ9WmtF2R2mA6f6v4IdxrS2mVD-hE4Vz2oOT9vtuU8lqUs8KhmXXjbIWucm58AGeXKCd13CU-RBKnd4TL8GRkHuWsLEsP7njejgT7hSHz0sR8OQ0bvHgDTEcUCoC4L8lHoahcgB9lYb2zoFi96VTusxoc0QOEhzIe9JemlC3nw_iy1igNFQ63wpsJIRKW67neGE7ewP22ZBhN7Qy5OgilNEG0H_aPVSqwMuTv58xCHCiWmZqsvAC2zNsdXGPaLRCOs5BTEcS3Aoj9wf65nuILat24GoydLUXBljZZXL-swADAFMcJifJQFjCE7WV3uPtmhMz8xKC4mo7dNOqKR-mRYYNNht2EvmAMRTM4gsZ_GyE3WwWwfNN1syXv228mp0VVQYRHTXlSL8FhdPEkYzEpk9hDWkL_WfUJzz6tZfTa3bg75yVJT_2BkkP6gtsOPYCnaoI_T-iiQz-lTuF4BpM-CDLV3rH8VsleDVKIfDwqSwmT1HgX7TMouaKkU13xRK25lFhl3mzFKu2FuvNqLiMQ793ojcQ6gwuzRsRwYzxgyd0jK5b6DBeiFAOA8OgZSuD5a5QqNGKwx4TZmdyIC1LQmvgy2RWFl3q6QNU2HVJogZUcgXFP3AHkkrruEZMCJi4gPsNNZHgd9oSjbyytrHZuATIs3bbS8ugzvXJpR1VaeAVjGqDCY0J9W4gX-VYng0HyRXV1Mcw7wHNQBOgXxTxwaQBD9JV01TXFeVT1n8p-2yI4m9oywzdkl9oOwyPCJ2LEaL73OrL_JAD7ofVzhzRlmnnhtnxKVL1QierC5aI7FcYGBDWAHlTxZVQuofo_lw5Y4sxT2en6etgmGkwDC9HGsevLLwjnbOGtMvcyhHx5jKIyinY5biKLlceFoZ2h0jx_IMDgKGRsZWYmt_rFX_nEz0les5YKvIOeMjW6QKPVQkFOlAb5AO17dm7y2Z5Z9-gkq4f6jBtL5cEuyLg60TXmZGzlDg9Zl__ybdd-d1Z5gpQglckL2cwQWzwjJjNJ71-ivw7Hw5LZ-hIuegwXytR3wmD6g9RTx6Qt0SuF0pGzHFxDm_i92adbO4H-ToreBxeMSWrp8mkmCq9V6cT8ImWNTvmcIdiLXUeZ0pe6MJe4BG1gfJSOnYdArU-pwgxpZ4xyulr1PczmnRS5rJOAiIC4L7A0DHuss0_vlVjGuPT5Cg2Ld818cGnu7r2wGdwqzyeTNOVDgwPW2fRtJGFMon0BKFh7V_pXVm7)

*Tabelul 1.3. Contracte de integrare ale Hărții Contextului*[cite: 1]

| # | Upstream `[U]` → Downstream `[D]` | Model de integrare | Contract (Port / Eveniment / DTO) | Regulă de business susținută |
| :-: | :--- | :--- | :--- | :--- |
| 1 | Furnizori și Calitate → Gestiunea Comenzilor | Client/Furnizor, sincron (REST) | `IGestiuneFurnizoriAdapter.esteFurnizorEligibil(IdFurnizor)` | O comandă poate fi plasată doar la un furnizor cu statusul `Aprobat` sau `Condiționat`[cite: 1]. |
| 2 | Gestiunea Comenzilor → Furnizori și Calitate | Domain Events, asincron (Outbox) | `ComandaReceptionataEvent` | La sosirea unui lot, se programează automat o inspecție, dacă planul AQL o impune[cite: 1]. |
| 3 | Furnizori și Calitate → Gestiunea Comenzilor | Domain Events, asincron (Outbox) | `InspectieProgramataEvent`, `LotScutitDeInspectieEvent`, `VerdictInspectieEvent` | Lotul primește statusul `În carantină`, `Eliberat în stoc` sau `Respins`[cite: 1]. |
| 4 | Gestiunea Comenzilor → Furnizori și Calitate | Open Host Service + Published Language | `PerformantaLivrariDTO` (OTIF și conformitatea prețurilor, per furnizor și lună) | Scorecard lunar: OTIF 40%, calitate 40%, conformitatea prețurilor 20%[cite: 1]. |
| 5 | Gestiunea Comenzilor → Facturare și Plăți | Domain Events, asincron (Outbox) | `ComandaReceptionataEvent`, `ComandaAnulataEvent` | Un NIR per recepție (consumator idempotent); facturile neplătite aferente unei comenzi anulate sunt blocate[cite: 1]. |
| 6 | Gestiunea Comenzilor → Facturare și Plăți | Client/Furnizor, sincron (REST) + Published Language | `IComenziAdapter.obtineDateComanda(IdComanda)` → `ComandaSummaryDTO` | Verificare încrucișată (*three-way match*): factură vs. preț comandă și cantitate comandată[cite: 1]. |
| 7 | Furnizori și Calitate → Facturare și Plăți | Domain Events, asincron (Outbox) | `FurnizorBlocatEvent` | Facturile unui furnizor care devine `Blocat` nu sunt transmise spre plată[cite: 1]. |
| 8 | Toate cele 3 contexte → Notificări | Domain Events, asincron (Outbox) | `ComandaNeconfirmataEvent`, `EstimareDepasesteCerereaEvent`, `NCREscaladatEvent`, `CertificareInspectorExpiraEvent`, `ScadentaApropiataEvent` | Alerte: lipsa confirmării în 48h, data estimată ulterioară datei solicitate, scadență peste 3 zile[cite: 1]. |
| 9 | Securitate și Audit → Toate contextele | Open Host Service (API Audit) | `IJurnalAudit.inregistreazaAcces(...)` | IBAN-ul, prețurile negociate și datele de plată sunt accesibile doar rolurilor autorizate; jurnal de audit imuabil[cite: 1]. |
| 10 | Nucleu Partajat (*Shared Kernel*) | Tipuri de identitate partajate | `IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`, `Perioada` | Contextele fac referire unul la celălalt exclusiv prin intermediul identităților[cite: 1]. |

---

## 9. Diagrama de Activitate (Cross-Bounded Contexts)

### Figura 2. Activity Diagram across Bounded Contexts with `InterruptibleActivityRegion` and cancellation path
Diagrama este structurată pe trei *swimlanes* verticale corespunzătoare celor trei *Bounded Contexts* (`Managementul comenzilor de aprovizionare`, `Gestiunea furnizorilor și a calității`, `Gestiunea facturilor și plăților`). După confirmarea comenzii, procesul intră într-o zonă de activitate întreruptibilă în care ciclul de livrare/calitate și ciclul de facturare/plată rulează în paralel, putând fi oprite atomic prin muchia de întrerupere la anularea comenzii[cite: 1].

- 🔗 **Link extern Imgur (Figura 2)**: [Deschide Activity Diagram pe Imgur](https://imgur.com/a/MqFUAGN)
- 🔗 **Link extern DocHub (Figura 2)**: [Deschide Activity Diagram pe DocHub](https://dochub.com/sdbis-portfolio/5lae27DR5qPEP9rRmqjZv1/activity-diagram-png)[cite: 1]

---

## 10. Diagrame de Cazuri de Utilizare (Use Case Diagrams) & Descrieri Narative

Pentru fiecare dintre cele trei subdomenii este definită câte o diagramă a cazurilor de utilizare separată (Figurile 3, 4 și 5)[cite: 1]:

### 10.1 Figura 3. Procurement Order Management Use Cases (Core Domain)
- **Relații `«include»` (comportament obligatoriu)**: crearea unei comenzi include întotdeauna verificarea eligibilității furnizorului; anularea unei comenzi include întotdeauna anularea loturilor nerecepționate; confirmarea din partea furnizorului include întotdeauna estimarea termenului de livrare; fiecare recepție de marfă include actualizarea stării lotului[cite: 1].
- **Relații `«extend»` (comportament condiționat)**: `Urmărire Furnizor Neconfirmat` (când nu se primește răspuns în 48h), `Emitere Alertă Depășire Dată Cerută` (când data estimată > data solicitată), `Reestimare la Întârziere` (peste pragul configurabil), `Recepție Parțială` (când cantitatea recepționată < cantitatea comandată)[cite: 1].
- 🔗 **Link extern Google Drive (Figura 3)**: [Deschide Figura 3 - Procurement Order Management Use Cases (Google Drive)](https://drive.google.com/file/d/1-yWbcEyLZVeP-EepeLC34a7t9TVYuaR9/view)

---

### 10.2 Figura 4. Supplier & Quality Management Use Cases (Supporting Subdomain 1)
- **Relații `«include»`**: evaluarea lunară include calculul scorului ponderat (OTIF 40%, calitate 40%, conformitate preț 20%); alocarea unui inspector include verificarea certificării și a disponibilității; fiecare inspecție se încheie cu înregistrarea verdictului[cite: 1].
- **Relații `«extend»`**: trecerea furnizorului în `Condiționat` (la nota C), revenirea în `Aprobat` (după 3 luni consecutive cu nota B sau mai bună), `Reinspecția`, `Deschiderea unui NCR` (la fiecare inspecție neconformă), `Suspendarea furnizorului` (la două inspecții eșuate consecutive ale unui furnizor condiționat) și `Escaladarea NCR`[cite: 1].
- 🔗 **Link extern Figura 4**: [Deschide Figura 4 - Supplier & Quality Management Use Cases (PlantUML PNG)](https://www.plantuml.com/plantuml/png/ZLLHRnit37w_Nq5amD01oR0qQUmLMT34YOS2JH77tLxcFj0wsY4gKmObkKs6_VT9T-Sxdvi0zHCbVjz7wIDFVkS3QeWhMmGEbc0QrslBfF0pJ29Q3ezmYu9BMf44-CiJZD2JBy3I8a1meBny252oaWdif03_XMMDYYlm3rYwhonBMA3rj9kAdapIWNyB0BX2ysMfBaevSjOf_7Gp7FyslgbIJalIPljuV7Oyl2xWlrsbN1TbQL_I6b-EH-FnZsbTghgl3U2q-XK5e0bfVN1F2zBimULQtOQVs0agpUJLLNma_UK0q4CLjepQDuLHjX53nlOjRLYJsvRoNloQKjYHAaJURZ5Jze5MC7Af8q-SHEjzJoPT6XlDwQDl2NSMXHTiCAy-H45zWVI9LwH5qLcQYiLvcGjnj6_XJ_A1en328ghmarEsMOG1mTJdPOPNUOISXjptceuigK1kJzltWwqMtAbRi9t9k96iBJdkWQ_99CEfCYLVH6aV_xg7JpWJRGiFl9b9ldW3Fda6d3HXQbooviv9IOgXmM8F_4d9K88srHES1KQ9mNduMzAyHxgd3Gdli_8SpLk6UTwTTY1oerFbKD-V3w5altR2Swxdf1cbqvvCydQfk49jXJXFuTfYAZ_vq73Uz3YNrfaTmjRWinxeRzAc4J1g3vMaFSDr_rmDk-_YpJSoCN1tgWOswA7UYzAIVT1S9bKisOG6Xtl-yfxEV6zyl7dWClVju-Y-WVGDdyOa82NkDAd1bSytvYCP9mkdg-sddPGRFuVzktg35alSBJxid__M2USMFtNbwuPxupHXj3m4JLKEeIloUtwwYlh5WQEZY_J9z5OfFxcjmzTTUDg5PrquxC94opsf5uEq_mREZuxgvsHoakFsbKZBNnFe1E4jd9-p61jBkhWeadILEFi-CQWIW_r4rfhNObM6lWMIyk9Y9l-OxXE6Fs3qEKFDyz0Ji54OZ1DVJTScTSL4s5Mu0eyHLiWmZvAr1dNPmRwM9_3Eik60eDrqLHJJNgBFsTGJ2hATZ0fTle16A5r4QAKx6kvS0GzdidrhmNJFJ9OUOYKz_4xPr1DQpKxM01PA3Z19mC_5kuJBV_V5_m00)

![Figura 4. Supplier & Quality Management Use Cases](https://www.plantuml.com/plantuml/png/ZLLHRnit37w_Nq5amD01oR0qQUmLMT34YOS2JH77tLxcFj0wsY4gKmObkKs6_VT9T-Sxdvi0zHCbVjz7wIDFVkS3QeWhMmGEbc0QrslBfF0pJ29Q3ezmYu9BMf44-CiJZD2JBy3I8a1meBny252oaWdif03_XMMDYYlm3rYwhonBMA3rj9kAdapIWNyB0BX2ysMfBaevSjOf_7Gp7FyslgbIJalIPljuV7Oyl2xWlrsbN1TbQL_I6b-EH-FnZsbTghgl3U2q-XK5e0bfVN1F2zBimULQtOQVs0agpUJLLNma_UK0q4CLjepQDuLHjX53nlOjRLYJsvRoNloQKjYHAaJURZ5Jze5MC7Af8q-SHEjzJoPT6XlDwQDl2NSMXHTiCAy-H45zWVI9LwH5qLcQYiLvcGjnj6_XJ_A1en328ghmarEsMOG1mTJdPOPNUOISXjptceuigK1kJzltWwqMtAbRi9t9k96iBJdkWQ_99CEfCYLVH6aV_xg7JpWJRGiFl9b9ldW3Fda6d3HXQbooviv9IOgXmM8F_4d9K88srHES1KQ9mNduMzAyHxgd3Gdli_8SpLk6UTwTTY1oerFbKD-V3w5altR2Swxdf1cbqvvCydQfk49jXJXFuTfYAZ_vq73Uz3YNrfaTmjRWinxeRzAc4J1g3vMaFSDr_rmDk-_YpJSoCN1tgWOswA7UYzAIVT1S9bKisOG6Xtl-yfxEV6zyl7dWClVju-Y-WVGDdyOa82NkDAd1bSytvYCP9mkdg-sddPGRFuVzktg35alSBJxid__M2USMFtNbwuPxupHXj3m4JLKEeIloUtwwYlh5WQEZY_J9z5OfFxcjmzTTUDg5PrquxC94opsf5uEq_mREZuxgvsHoakFsbKZBNnFe1E4jd9-p61jBkhWeadILEFi-CQWIW_r4rfhNObM6lWMIyk9Y9l-OxXE6Fs3qEKFDyz0Ji54OZ1DVJTScTSL4s5Mu0eyHLiWmZvAr1dNPmRwM9_3Eik60eDrqLHJJNgBFsTGJ2hATZ0fTle16A5r4QAKx6kvS0GzdidrhmNJFJ9OUOYKz_4xPr1DQpKxM01PA3Z19mC_5kuJBV_V5_m00)

---

### 10.3 Figura 5. Invoice & Payment Management Use Cases (Supporting Subdomain 2)
- **Relații `«include»`**: reconcilierea verifică întotdeauna prețul și cantitatea comandată în raport cu comanda și cantitatea recepționată în raport cu NIR-ul; crearea unui ordin de plată include întotdeauna verificarea că factura are starea `Aprobată la plată`; transmiterea către bancă include verificarea că furnizorul nu este `Blocat`[cite: 1].
- **Relații `«extend»`**: `Blocare Factură (Discrepanță)` (când diferențele > ±2%), `Contestare Factură și Cerere Notă de Credit`, `Aprobare Suplimentară Ordin` (peste pragul de 10.000 EUR), `Tratare Respingere Bancă`, `Înregistrare Plată Parțială` și `Emitere Notificare Scadență` (cu 3 zile înainte de scadență)[cite: 1].
- 🔗 **Link extern Figura 5**: [Deschide Figura 5 - Invoice & Payment Management Use Cases (PlantUML PNG)](https://www.plantuml.com/plantuml/png/ZLLRJoH73tslNs7nwOjO9KHPBWoxGcY1q2iUiY3O90-PFBYhFKE96bVBxTxBHFdlSVM5QKWcoZovxNDEkLodQjurYgBjAZeD6Wck-LCAdk0RkCMlAsA5dv1nILtuSqDmYGqrBj92GHD8M3ueL478QqZiNFCOk4R15JGFMANFWPUmmDZGfD8svD6a_d005-WVbv9QhYvJJ0B_ktfJpAwAN4bIaGpPrrSdXuTlt9yJbRmayZ9kqIdEYwAu-0ywvoBfyr0-x7xEeLVxtBbCh5Y6EEVTS-zDN97rrGvW0_OzecuMGKcW28pi0ugX-vZWPh48dgIdM7AadDUIIipXRKGDnkWB3NdR0SgmHbtBoBWmFTo2izhZYBiFZT8ABjEAUBrD-4OgvvxcPwIsh38XjF4jlAT6Gyk4i31kAo5cVe0wedRnxdrRrqaq7stFnC0cd8zXFDgTQnPQMY-2Gb1qGjP-qGg7TPAkY-lNKy9xOkh07wxl-lB-j7n7Fh4FCT1KR_VZWn3j_OfVpQRg7rxrp8CfynUIOAUGfM-551gRoYMo1aMbF2Za2XK79cwXFZ7cRArGdTrkBDYrTiTLoodt8YQFpthzCJHUg3OX71b-oiXkeqOdfBvN4ifZIOfGMID2LT21Nqtverl3-jaO3vzXXAoO3H0ugzt6fwqVRTdvLGpBOBB3xiVMQcALzfd0T7Q3nOrlRebXPMZCK-XM7l3FvlLHa9jLK1hRctD4oBOVv8-tz3TwQiuVgCn3rm6FM_ruH-isAkgSRoLvQd9kqjZiHL_QSvhQ74zYCvXqDTksoZ1RMm950yOHNsxPnht7YbYfXvrCKLV3MCm20thE0nm7EDgJvknDWhszCxjOc_1W4nuwLmJkuwDDUBm9JqoZhcM0bCxbbsL0JUAPSpQjFjxVX0VetFK1VD-5y1PEJmFxs5PqTlQKBr_cZxhyqT_mnrt--6NUX7olr1NeYn9NPsTp_irJXBhLm900IzJkeEmB5oW8cY89ibB8hlzsR___lsUjwf-rgirD1JQXTOgVyit2J9fXHvgz9ANkJabdaoQlKYBxdb7-6uE-a6yL-uVf1pW5JOeHq3-4dFNjgeseOtvub3lTaxwJavUw0mIm_vT1KmQcxlrM_6xE1x0EaQ16HeXM7QnaakzCebr5vznV)

![Figura 5. Invoice & Payment Management Use Cases](https://www.plantuml.com/plantuml/png/ZLLRJoH73tslNs7nwOjO9KHPBWoxGcY1q2iUiY3O90-PFBYhFKE96bVBxTxBHFdlSVM5QKWcoZovxNDEkLodQjurYgBjAZeD6Wck-LCAdk0RkCMlAsA5dv1nILtuSqDmYGqrBj92GHD8M3ueL478QqZiNFCOk4R15JGFMANFWPUmmDZGfD8svD6a_d005-WVbv9QhYvJJ0B_ktfJpAwAN4bIaGpPrrSdXuTlt9yJbRmayZ9kqIdEYwAu-0ywvoBfyr0-x7xEeLVxtBbCh5Y6EEVTS-zDN97rrGvW0_OzecuMGKcW28pi0ugX-vZWPh48dgIdM7AadDUIIipXRKGDnkWB3NdR0SgmHbtBoBWmFTo2izhZYBiFZT8ABjEAUBrD-4OgvvxcPwIsh38XjF4jlAT6Gyk4i31kAo5cVe0wedRnxdrRrqaq7stFnC0cd8zXFDgTQnPQMY-2Gb1qGjP-qGg7TPAkY-lNKy9xOkh07wxl-lB-j7n7Fh4FCT1KR_VZWn3j_OfVpQRg7rxrp8CfynUIOAUGfM-551gRoYMo1aMbF2Za2XK79cwXFZ7cRArGdTrkBDYrTiTLoodt8YQFpthzCJHUg3OX71b-oiXkeqOdfBvN4ifZIOfGMID2LT21Nqtverl3-jaO3vzXXAoO3H0ugzt6fwqVRTdvLGpBOBB3xiVMQcALzfd0T7Q3nOrlRebXPMZCK-XM7l3FvlLHa9jLK1hRctD4oBOVv8-tz3TwQiuVgCn3rm6FM_ruH-isAkgSRoLvQd9kqjZiHL_QSvhQ74zYCvXqDTksoZ1RMm950yOHNsxPnht7YbYfXvrCKLV3MCm20thE0nm7EDgJvknDWhszCxjOc_1W4nuwLmJkuwDDUBm9JqoZhcM0bCxbbsL0JUAPSpQjFjxVX0VetFK1VD-5y1PEJmFxs5PqTlQKBr_cZxhyqT_mnrt--6NUX7olr1NeYn9NPsTp_irJXBhLm900IzJkeEmB5oW8cY89ibB8hlzsR___lsUjwf-rgirD1JQXTOgVyit2J9fXHvgz9ANkJabdaoQlKYBxdb7-6uE-a6yL-uVf1pW5JOeHq3-4dFNjgeseOtvub3lTaxwJavUw0mIm_vT1KmQcxlrM_6xE1x0EaQ16HeXM7QnaakzCebr5vznV)

---

### 10.4 Descrieri Narative ale Cazurilor de Utilizare Reprezentative

<details>
<summary><b>CU1: CreeazaComandaAprovizionare (Procurement Order Management)</b></summary>

- **Obiectiv**: Înregistrarea cu succes a unei noi comenzi de achiziție în sistem, cu starea inițială `Solicitat`[cite: 1].
- **Actori**: Cumpărătorul (actor principal); Contextul Furnizori și Calitate (actor secundar — verificare eligibilitate)[cite: 1].
- **Precondiții**: Utilizatorul este autentificat cu drepturi de cumpărător[cite: 1].
- **Postcondiții**: Comanda și liniile ei sunt înregistrate în baza de date cu număr unic și starea inițială `Solicitat`[cite: 1].
- **Pașii scenariului principal**:
  1. Utilizatorul inițiază operațiunea de adăugare a unei comenzi[cite: 1].
  2. Sistemul propune următorul număr de comandă și data curentă drept dată a comenzii[cite: 1].
  3. Utilizatorul introduce datele de identificare ale furnizorului (cod fiscal sau denumire)[cite: 1].
  4. Sistemul verifică starea furnizorului în contextul „Furnizori și Calitate” (`Aprobat` sau `Condiționat`) și afișează datele acestuia[cite: 1].
  5. Utilizatorul introduce data de livrare solicitată și selectează depozitul de destinație[cite: 1].
  6. Sistemul validează ca data de livrare solicitată să nu fie anterioară datei comenzii[cite: 1].
  7. Utilizatorul selectează cumpărătorul (valoare implicită: utilizatorul autentificat)[cite: 1].
  8. Pentru fiecare linie a comenzii, utilizatorul introduce codul materialului, cantitatea (> 0), unitatea de măsură și prețul unitar (> 0)[cite: 1].
  9. Utilizatorul introduce observații și salvează comanda[cite: 1].
  10. Sistemul verifică completitudinea datelor și unicitatea numărului comenzii, înregistrând comanda cu starea `Solicitat`[cite: 1].
- **Scenarii alternative**: (1) Furnizorul nu este eligibil → avertizare cu starea curentă și selectarea altui furnizor; (2) Data estimată de livrare depășește data solicitată → alertă cumpărător[cite: 1].
</details>

<details>
<summary><b>CU2: ExecutaInspectieCalitate (Supplier & Quality Management)</b></summary>

- **Obiectiv**: Efectuarea inspecției calitative a unui lot sosit și înregistrarea verdictului (`Conformă` sau `Neconformă`)[cite: 1].
- **Actori**: Inspectorul de calitate (principal); Managerul de calitate și Contextul Gestiunea Comenzilor (secundari)[cite: 1].
- **Precondiții**: Inspector autentificat, inspecția în starea `Inspector alocat`, certificarea inspectorului validă la data inspecției[cite: 1].
- **Postcondiții**: Inspecție înregistrată cu verdict; verdict publicat prin *Transactional Outbox* (`VerdictInspectieEvent`) care trece lotul în `Eliberat în stoc` sau `Respins`; la rezultat negativ se deschide un `NCR` cu starea `Deschis`[cite: 1].
- **Scenarii alternative**: (1) Numărul de defecte atinge pragul de respingere AQL → `Neconformă` + deschidere NCR + lot `Respins`; (2) A doua inspecție eșuată consecutivă pentru un furnizor `Condiționat` → furnizorul devine `Suspendat`; (3) Solicitare reinspecție → starea `În reinspecție`[cite: 1].
</details>

<details>
<summary><b>CU3: ReconcilieazaFactura (Invoice & Payment Management)</b></summary>

- **Obiectiv**: Verificarea unei facturi de furnizor prin compararea cu comanda și nota de recepție (*three-way match*)[cite: 1].
- **Actori**: Contabilul (principal); Contextul Gestiunea Comenzilor (secundar)[cite: 1].
- **Precondiții**: Contabil autentificat; factura în starea `Înregistrată` (sau `Blocată (discrepanță)` după corecție); cel puțin un NIR în starea `Generat`[cite: 1].
- **Postcondiții**: Dacă diferențele relative se încadrează în ±2%, factura devine `Aprobată la plată` și NIR-ul `Reconciliat`; altfel factura devine `Blocată (discrepanță)` și NIR-ul `Cu discrepanțe`, iar decizia se înregistrează în jurnalul de audit imuabil[cite: 1].
- **Scenariu alternativ**: Dacă discrepanța nu poate fi corectată, factura este marcată `Contestată` și se trimite furnizorului cererea pentru nota de credit[cite: 1].
</details>

---

## 11. Diagrama de Clase (Tactical DDD Domain Model & Invarianți)

Modelul de domeniu aplică tiparele tactice DDD (**Entity**, **Value Object**, **Aggregate & Aggregate Root**, **Domain Service**) cu izolare completă între cele trei *Bounded Contexts*[cite: 1]:
- **Core Domain**: `ComandaAprovizionare` (Aggregate Root) încapsulează entitatea subordonată `Lot` (cu metode de tranziție a stării cu vizibilitate la nivel de pachet `~`) și obiectele valoare `LinieComanda`, `Expediere`, `EstimareLivrare`, `Cantitate`, `PretUnitar`, împreună cu `EstimareLeadTimeService`[cite: 1].
- **Supplier & Quality Management**: Agregatele `Furnizor` (cu entitatea subordonată `ScorecardLunar` și Value Objects `Scor`, `ConditiiContractuale`, `Iban`, `Email`), `InspectorCalitate`, `InspectieCalitate` și `RaportNeconformitate`, deservite de `CalculScorecardService` și `AlocareInspectorService`[cite: 1].
- **Invoice & Payment Management**: Agregatele separate `FacturaFurnizor`, `NIR` și `OrdinPlata` (unde `OrdinPlata` are constructor *package-private* și poate fi creat exclusiv prin `FacturaFurnizor.creeazaOrdinPlata()`), deservite de `ReconciliereService`[cite: 1].
- **Shared Kernel**: Value Objects de tip identitate (`IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`) și `Perioada`[cite: 1].

- 🔗 **Link extern Diagrama de Clase**: [Deschide Tactical DDD Class Diagram (PlantUML PNG)](https://www.plantuml.com/plantuml/png/fLXVZoAv4N_dKrWJAM4lDwkxXxoWP7Gip6wGs1aEPZQwlDMuQzZAkUrMkPhB4kM3HFb2zx62iNiCffdb51wGswx_BbVzx1-y04jRcu6G65Gh0Li1LsfYm7ir9LWpr6euSOnQGDIrchHA70Wgy4uJCXbKl_w7L8KDsWhjh_z5Wsy6_cUo3GHc_nagzmlPjNe6u_5WHGSbOn6cfrPmffrTqHRLTmSa3UYVOOqh-M9GxMsmQtCeOypiVfauurZzxa_xts3mcIfKM3VoHTLOFo7xmU0FwY_zF_NnVleulnsf--NqThawHpP8bgWhnnMolrB_6gZeWPguEeHjtB3Rq9QS1SRzijh5YwgHUdoSJTEdRMlWn31IAs6owxJqtBAbhUFPCOTkw-2gd2nK8F0YQUuqc2a87YpEQSF0E45kvNMI1RkQV1yDDcvBKbWQiWOFdCXV1w9llL2TLPpAhb5SbSWOZrJyKQrHvcI9XczsZEJbp-6hqtAJY7n7PC9_7-6EU-EeIlJ2L9FW7DwdK0-FrxMppyGr38Fzfp4v9WRR6eGj7CZuz-0b9-PEpgM0TjNS5I4Danz1a0bCkG9MI41mfDxHUegQQZ3bscBl6VIGj7PFy15yosN69JUhLWjXVuBS_hEXa-tBIP0SYQEyTPFEhYNKu4E82iD2_BIqO6WROhYNCBIkZQCiiZ-s1pkSWdnv02-FJiCeZs6TUnXhPp4a_wHbHgk_PDy_WN71-61awVYX5xl8OaLuK2g4mVh6iO0uBeonFFxRkvxjIrAA3JhKiq0c1rsa4jlCQcnQwT_pzsJ3nXIx7Xr4BxXZDgs7cHs22JdNQTWv--ISGR25f-c8pn6g1wfnXRmXdVmEnmwCZcTfckl5i2o7t_PjyPiUjz6sTIvAEKTMpf0cWPYdatZMqzSIdzjj6i_i0uFTKhUYiH5o5cG1B0Icc8vaOziQa5CJSXrO6B3qJ3eofEq7IQPvafcTncgRh9W0Xyor4EaDFI73ZcebJYSRV4FMfx6qt6FCpfpUzdJr_PNwvlfQNNttzkqtLogOELBQ1NseRhx2yttWEJg6bp6xh31d_qW1d-MwlhuvxYzp9tbozn4w-npN82s3WWhQtH1KWrQuFPTuWtC9-VRjJNyp7oaCB255LIFgvX8yiNfSBEQp02b-htvy7Czd3pzz7LpujcaC5VYYwrQlD9JtvFL9uQj26XBZIVTcE04VZi6VjgI5yt8Ybfx05bDO0vc9im9QYYL1hj4k30ZydSpE5h9ojeRcnjBv7AGTV7OcMD-W5g8x33tRSOqxxHvrAxJ9EaANQTWzGT7dVUi3M8LXxNRqdS77D4_6wSy8suAejo6jTC37mDMyjT2_JUVndHDwNh3JQAMdpp08n7E-HiOUWkH_GW7O9yDhnmzEm9HhNRCsiIxH9iGf-ddiu2p6st6W3PhE-aZivGxgCkSC5DDH-mPvhtfAld6MdYXA7pPdcqff4k7NJ39EIaGTZ_ZiekqvZoiiRT2CVvoNKUrI-CN2m7Vu7Oc2ynLfAK82ncbuYVUGuYYRTPobP1dNv8L3fdwA0eSLFgCM_02U91w6tgnTGi0QBoVerI1IP_k5GODs2qkeG9E5awhaDHCoZhLGQp7UBMa3VPUCkyaoJCVHYy_wCrLX8X_hikqM9NC2jgBgfR_VRi2qm1YRPQWG6HHKTD3LLx5GfAztnkcU7fytjklqxCAr6GIIlYdwvu0mEvMJ6F6u_B81YJfuvw7G4WEGvE_41Phzk0pYp4vQzbbI7Dw6xW1LnWHTV8gsN5QoaI8lZach1dblenLGDvczQyMvG9-SoqoJ3q2HUWLTsEhSrgbhwvZQUaQqdd9b6XIcxwHbmb77QbeRxEyniYHFm-O5jVNpfRW5zZ3SHVEqLexYeHQbGHWlGm-pkq_tiydixiCEFIp6FtsylNkuu6s2xCQH9hi-WW-WfMNu6ehmo5IKxVXQqNVhg4cGprmPLnegj0B5wcyjl9juZteF30-VncMHpGIlBuQs1pthlgqXW9Boqdm1CedX3579N_reexpUBD7B58FQqojnUR-DWl_F--thTrZjf0XjOSZTRFdgEzPklRIbsE6S5Ud2TEuX8OcAenIu6ZX2i2bvpTY05HoUVn78rfPD_yBuvJlTvIyfntRSSqKHwvu5I75R2-D2DbwUXfeHP8gTX1p4lVeKncpAAxaPdnpYczWxi1hwmxiCXLgJ8UIJopjtQm4g5zvzgonn_gY1_m6txE0Oo6Q4TzBqYbCmwz1ERgsV4e9aF6ZHSDZOOGuv3Ed8dohAoVC1BNA71zg3r0ipEHGPCGW4tBhjGmOd-J09MtS2EnxYBlYq2Ubz07KhkMCNLRptCfve3Y_Z9L_kiccX92WHGNOXte9HDJ66gc6oEmKXSdt6XFbiIAHBokCXpvBpV9vx3G7q9UIWdEikzAL_ewEKLJTd6Ji7zi_lIhUlD-x9ydwrkexjMorlv-E7sVtTwg-pnVcsNLZsslF35UDpBgvN8PY_XVmKjmJckPEErZW9T67cSh4bv0gSESoHkclkWVm7j5LRc_y1)

![Diagrama de Clase](https://www.plantuml.com/plantuml/png/fLXVZoAv4N_dKrWJAM4lDwkxXxoWP7Gip6wGs1aEPZQwlDMuQzZAkUrMkPhB4kM3HFb2zx62iNiCffdb51wGswx_BbVzx1-y04jRcu6G65Gh0Li1LsfYm7ir9LWpr6euSOnQGDIrchHA70Wgy4uJCXbKl_w7L8KDsWhjh_z5Wsy6_cUo3GHc_nagzmlPjNe6u_5WHGSbOn6cfrPmffrTqHRLTmSa3UYVOOqh-M9GxMsmQtCeOypiVfauurZzxa_xts3mcIfKM3VoHTLOFo7xmU0FwY_zF_NnVleulnsf--NqThawHpP8bgWhnnMolrB_6gZeWPguEeHjtB3Rq9QS1SRzijh5YwgHUdoSJTEdRMlWn31IAs6owxJqtBAbhUFPCOTkw-2gd2nK8F0YQUuqc2a87YpEQSF0E45kvNMI1RkQV1yDDcvBKbWQiWOFdCXV1w9llL2TLPpAhb5SbSWOZrJyKQrHvcI9XczsZEJbp-6hqtAJY7n7PC9_7-6EU-EeIlJ2L9FW7DwdK0-FrxMppyGr38Fzfp4v9WRR6eGj7CZuz-0b9-PEpgM0TjNS5I4Danz1a0bCkG9MI41mfDxHUegQQZ3bscBl6VIGj7PFy15yosN69JUhLWjXVuBS_hEXa-tBIP0SYQEyTPFEhYNKu4E82iD2_BIqO6WROhYNCBIkZQCiiZ-s1pkSWdnv02-FJiCeZs6TUnXhPp4a_wHbHgk_PDy_WN71-61awVYX5xl8OaLuK2g4mVh6iO0uBeonFFxRkvxjIrAA3JhKiq0c1rsa4jlCQcnQwT_pzsJ3nXIx7Xr4BxXZDgs7cHs22JdNQTWv--ISGR25f-c8pn6g1wfnXRmXdVmEnmwCZcTfckl5i2o7t_PjyPiUjz6sTIvAEKTMpf0cWPYdatZMqzSIdzjj6i_i0uFTKhUYiH5o5cG1B0Icc8vaOziQa5CJSXrO6B3qJ3eofEq7IQPvafcTncgRh9W0Xyor4EaDFI73ZcebJYSRV4FMfx6qt6FCpfpUzdJr_PNwvlfQNNttzkqtLogOELBQ1NseRhx2yttWEJg6bp6xh31d_qW1d-MwlhuvxYzp9tbozn4w-npN82s3WWhQtH1KWrQuFPTuWtC9-VRjJNyp7oaCB255LIFgvX8yiNfSBEQp02b-htvy7Czd3pzz7LpujcaC5VYYwrQlD9JtvFL9uQj26XBZIVTcE04VZi6VjgI5yt8Ybfx05bDO0vc9im9QYYL1hj4k30ZydSpE5h9ojeRcnjBv7AGTV7OcMD-W5g8x33tRSOqxxHvrAxJ9EaANQTWzGT7dVUi3M8LXxNRqdS77D4_6wSy8suAejo6jTC37mDMyjT2_JUVndHDwNh3JQAMdpp08n7E-HiOUWkH_GW7O9yDhnmzEm9HhNRCsiIxH9iGf-ddiu2p6st6W3PhE-aZivGxgCkSC5DDH-mPvhtfAld6MdYXA7pPdcqff4k7NJ39EIaGTZ_ZiekqvZoiiRT2CVvoNKUrI-CN2m7Vu7Oc2ynLfAK82ncbuYVUGuYYRTPobP1dNv8L3fdwA0eSLFgCM_02U91w6tgnTGi0QBoVerI1IP_k5GODs2qkeG9E5awhaDHCoZhLGQp7UBMa3VPUCkyaoJCVHYy_wCrLX8X_hikqM9NC2jgBgfR_VRi2qm1YRPQWG6HHKTD3LLx5GfAztnkcU7fytjklqxCAr6GIIlYdwvu0mEvMJ6F6u_B81YJfuvw7G4WEGvE_41Phzk0pYp4vQzbbI7Dw6xW1LnWHTV8gsN5QoaI8lZach1dblenLGDvczQyMvG9-SoqoJ3q2HUWLTsEhSrgbhwvZQUaQqdd9b6XIcxwHbmb77QbeRxEyniYHFm-O5jVNpfRW5zZ3SHVEqLexYeHQbGHWlGm-pkq_tiydixiCEFIp6FtsylNkuu6s2xCQH9hi-WW-WfMNu6ehmo5IKxVXQqNVhg4cGprmPLnegj0B5wcyjl9juZteF30-VncMHpGIlBuQs1pthlgqXW9Boqdm1CedX3579N_reexpUBD7B58FQqojnUR-DWl_F--thTrZjf0XjOSZTRFdgEzPklRIbsE6S5Ud2TEuX8OcAenIu6ZX2i2bvpTY05HoUVn78rfPD_yBuvJlTvIyfntRSSqKHwvu5I75R2-D2DbwUXfeHP8gTX1p4lVeKncpAAxaPdnpYczWxi1hwmxiCXLgJ8UIJopjtQm4g5zvzgonn_gY1_m6txE0Oo6Q4TzBqYbCmwz1ERgsV4e9aF6ZHSDZOOGuv3Ed8dohAoVC1BNA71zg3r0ipEHGPCGW4tBhjGmOd-J09MtS2EnxYBlYq2Ubz07KhkMCNLRptCfve3Y_Z9L_kiccX92WHGNOXte9HDJ66gc6oEmKXSdt6XFbiIAHBokCXpvBpV9vx3G7q9UIWdEikzAL_ewEKLJTd6Ji7zi_lIhUlD-x9ydwrkexjMorlv-E7sVtTwg-pnVcsNLZsslF35UDpBgvN8PY_XVmKjmJckPEErZW9T67cSh4bv0gSESoHkclkWVm7j5LRc_y1)

### Matricea Invarianților de Consistență Tranzacțională (Tabelele 2.1, 2.2, 2.3)

| Agregat Rădăcină | Business Invariant | Regulă garantată de Aggregate Root |
| :--- | :--- | :--- |
| **`ComandaAprovizionare`** | **Order Lifecycle & Lot Boundaries** | Loturile pot fi planificate, expediate sau recepționate doar dacă `stareComanda` permite operațiunea (`validaStarePermiteOperatia()`); suma cantităților pe loturi nu depășește cantitatea comandată (`validaCantitateLot()`); `calculeazaCantitatePrimita()` este mereu calculată dinamic din `lot.cantitatePrimita`[cite: 1]. |
| **`ComandaAprovizionare`** | **Cascading Cancellation** | `anuleazaComanda()` mută comanda și toate loturile aflate în starea `Planificat` sau `Expediat` în starea `Anulat` în cadrul aceleiași tranzacții ACID[cite: 1]. |
| **`Furnizor`** | **Qualification Guard & Scorecard** | `esteEligibilPentruComenzi()` este `true` doar pentru `Aprobat` și `Condiționat`; un singur `ScorecardLunar` pe lună (`Scor.ponderat()` 40/40/20); revenire din `Condiționat` în `Aprobat` doar la 3 luni consecutive >= B; 2 inspecții `Neconformă` consecutive în starea `Condiționat` trec furnizorul în `Suspendat`[cite: 1]. |
| **`InspectorCalitate`** | **Inspector Allocation Integrity** | `poateFiAlocat(perioada)` necesită o certificare valabilă pe toată perioada și o perioadă de disponibilitate acoperitoare[cite: 1]. |
| **`FacturaFurnizor` & `OrdinPlata`** | **Financial Control & Tolerance** | `OrdinPlata` are constructor cu vizibilitate de pachet și este creat exclusiv prin `FacturaFurnizor.creeazaOrdinPlata()`, care impune `esteAprobataLaPlata()` (reconciliere reușită în limita a ±2%) și verifică `calculeazaRestDePlata()`[cite: 1]. |
| **`NIR`** | **NIR Idempotence & Life Cycle** | Un singur NIR per `SursaId` de eveniment (`ComandaReceptionataEvent` retransmis este ignorat); un NIR poate deveni `Contabilizat` doar din `Reconciliat`[cite: 1]. |

### Exemple de Cod (Tactical DDD Patterns în Java)

```java
// Value Object imutabil cu logică: scorul ponderat și maparea acestuia la calificativ
public final class Scor {
    private final BigDecimal valoare; // 0..100

    private Scor(BigDecimal valoare) { this.valoare = valoare; }

    // OTIF 40% + calitate 40% + conformitate cu prețul 20%
    public static Scor ponderat(BigDecimal otif, BigDecimal calitate, BigDecimal pret) {
        BigDecimal s = otif.multiply(new BigDecimal("0.40"))
                .add(calitate.multiply(new BigDecimal("0.40")))
                .add(pret.multiply(new BigDecimal("0.20")));
        return new Scor(s.setScale(1, RoundingMode.HALF_UP));
    }

    public Calificativ calificativ() {
        if (valoare.compareTo(BigDecimal.valueOf(90)) >= 0) return Calificativ.A;
        if (valoare.compareTo(BigDecimal.valueOf(80)) >= 0) return Calificativ.B;
        if (valoare.compareTo(BigDecimal.valueOf(60)) >= 0) return Calificativ.C;
        return Calificativ.D;
    }
}
```
[cite: 1]

```java
// Aggregate Root ComandaAprovizionare: anulare în cascadă și recepție pe loturi
public ComandaAnulataEvent anuleazaComanda() {
    if (stareComanda == StareComanda.Receptionat || stareComanda == StareComanda.Refuzat
            || stareComanda == StareComanda.Anulat) {
        throw new IllegalStateException("An order in state " + stareComanda + " cannot be cancelled.");
    }
    List<IdLot> anulate = new ArrayList<>();
    for (Lot lot : loturi) {
        if (lot.esteInainteDeReceptie()) {   // Planificat or Expediat
            lot.anuleaza();                  // package-private: -> Anulat
            anulate.add(lot.getIdLot());
        }
    }
    this.stareComanda = StareComanda.Anulat; // confirmat împreună cu loturile (aceeași tranzacție ACID)
    return new ComandaAnulataEvent(id, anulate);
}

public ComandaReceptionataEvent inregistreazaReceptie(IdLot lotId, Cantitate primit) {
    validaStarePermiteOperatia(StareComanda.InTranzit, StareComanda.ReceptionatPartial);
    gasesteLot(lotId).receptioneaza(primit); // Expediat -> SositInDepozit
    this.stareComanda = esteAcoperitaIntegral() ? StareComanda.Receptionat
            : StareComanda.ReceptionatPartial;
    return new ComandaReceptionataEvent(id, lotId, primit, stareComanda == StareComanda.Receptionat);
}
```
[cite: 1]

```java
// Aggregate Root FacturaFurnizor: ordinele de plată sunt create doar prin intermediul rădăcinii
public OrdinPlata creeazaOrdinPlata(Suma suma, UUID creatDe) {
    if (!esteAprobataLaPlata()) {   // "Aprobata la plata", or "Partial platita"
        throw new IllegalStateException("A payment order requires a reconciled invoice; the invoice is " + stareFactura + ".");
    }
    if (suma.esteMaiMareDecat(calculeazaRestDePlata())) {
        throw new IllegalArgumentException("The payment order exceeds the amount still to be paid.");
    }
    return new OrdinPlata(idFactura, suma, creatDe); // constructor cu vizibilitate de pachet
}
```
[cite: 1]

---

## 12. Diagrama ERD & Schema Bazei de Date

Schema bazei de date relaționale (PostgreSQL) a fost construită pornind de la diagrama de clase a modelului de domeniu tactic DDD[cite: 1]. Fiecare rădăcină de agregat și entitate subordonată devine un tabel, păstrând granițele de consistență ale celor trei *Bounded Contexts*, iar referințele dintre contexte sunt păstrate prin identificatorii din *Shared Kernel* (`IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`), alături de tabela `outbox` pentru publicarea tranzacțională a evenimentelor de domeniu[cite: 1].

- 🔗 **Link extern Diagrama ERD**: [Deschide Database ERD Schema (PlantUML PNG)](https://www.plantuml.com/plantuml/png/jLXjRzou4VwkNy4cG6e3zLqIdtt6mJLklRj2ZJfnpYz1aQ8GnjJOdfeYrT78IPpalrVYQcanviRT3zqlziupmvsNXmyf_Tu8iBILoOJ4e3hxurYTeW4XPy6e6GXSGjFPzGrMe3RcfxDDjQLchWAoweqhqQYfgsg3acL_LcSemAIkqIA3GLMZ5MwLDi0a8AWQKWO-krQKguKgkWV9cbkoDJ1Kof15-LoZSYmtRWHOLsA3jVhrvSZ8O6zxurvaTAqjjMEBhFQo6ofHQM9jSEIZ3JHDxgoSqJsgLxzy3qr4c2vRmGUVxIpxKubNP56z-_l6fqsrVtcmhvrn_DlpmxsztPU71_ilRl7pmVxFtdgWFkt_V7aGWl95q12nupzTX25xkqLMGCieCli-8VKbK-eGzEsrBs_Q-wddkV_qYECIUM4Trkwi4sRtSJ1k-q_shP_HNx__gDd9c_dReujZbL-SlZtwS79wf3RwGB2WpWJiDMm-ZihgBX-uHlKiRzdIlMDIBzGKp6BG6sTjNJiMijURpxfSP_bk_jhNWLP8FgjdLykmPmeQ5RxrTSrtfrFlg_fkKraiqKtrcxguE9fvQ6lB_z6kmwdHO3hm_UHq-hV9wSREoqqFbcZRYXX7qEkTdGNMKn_3qebWiasDMeWAYzhPAySL5jhPflCzUdi-9A4steDfKPrS_hjpRZQNUT4bsE8EZ0F6nE9GCZPGDC9GGfnUvD3b3pp6utZZzD3oaS-hbx5JtwBQDG8ciP1sjgI-I46kq1Qr0O7YdWmEPKPEbRCOvRlTh_8j6mssqOvH0vSjKs5Q2un-n6ExP-_sV3UQSd18ZJbVJOGQcHoKK93LMBVIORF9-Jp6cYlWXYBC2LqLDJkDrjldqwCta-EDdR-yNdI6-qOmDZNPQsHCkGoqy0FebiBKEhxm4HDYFExTGBdbhWXVqmqBSAfXjgsIdAWPKr0w4Q9YoQznGlJ3V0XN3z0uYS25LRiAR0a993dmF5rza8CA19d0fChtkpvacjgPTyWbQOcHc4or0KjNf44ywIB2fNoQgx6BrumJzQvpcFnn70G3_cCAItTe4Xpo83PWXPmD-tA8uGAqnhelx34K-1kmUD1MTpvGE-1Xn9qXETpEdXnhFFarHfXWWuRs7Wg64ZHPw93p-J_EHsEpEgNJGpydMgYrE7NSPt873urT04M9ZMP2nc7X2Epxv6m3bvIWJN0RHF9Xcj-UF5Id_JyVHaVgr5-AVdYaJbs5zfxKf6PtH_VaB33sAp0EToz_gFwIx-Npw53LYy02ne5-h0EYdizsvvDyBumssdvhoMuVs5OrC8ZZCU2FhEMYgMQM813mqCh1RkYE-rbhv5PMmpMxYfeOnzhTawpUzN5A2GhD6w4A68yNFn8OX8EzA25EAG0LYYiX9Qg9wHYoj13uoAAUpm_pdVmmcasFfFKq6jqw4gZ1YfVqnuTIv-GL9_9ggsYLS8n8qLeIuDNBn7x_o-N1E6c7ms_nRxefJjPlYN49QgpHhR6YFz6ncgaYWNNx6aXUiolRuNYAwO-VQcBW25YGEovlvp7xkb0i2OVkXc_91Y_HDPad3BQf7Gk8uzIbrd817ozNQaBusNK4DfzCpo_6pompHgZbYOSMq9vTB_fZM2X-Nl7IEir_ZPzN5X512tqN1sEdgZlvpdmxwk42MwMgwzzw6kJaVSNBxTWUTRGYGNxCeqP3YLRWCO-6j5UBvFl5Riy7lnVgh4iWIANN0hyCGo5tqHu8jvd1wMdS2PYqIzDM_eub96c7fyGcZ7Iiny4owF4ad-Il7klnqxED7TRJvRIGjjPZtGfDo_3_L6IVgiJJIp-kMEA2KMCjG_ywGx9hdNszVU19tOUb6eLtQALeMcwWe78SvhazP9JGI4lnHaaLD693-BQQ2J-iB839M-_wl5XMvYm8h4cInxKwBidYuddVbpis92ls3cjodX6aA75iWvhTPMmD4_8_iL9xWjG42OYOdjI8DCLN7Ykdy-F9-L5tcio85q-wLuxzfgnb-UvIjmoIE5ww1L_zDBm_9LMZkaCMqc10RRm39YkejWxKrDLaDhE7LrvVlswLxgFTSb-YboHPUCADN0Oh1Pn6y5TbDMVPSAyCzgzVugjcxE3N7UvRAIJS5R9iCUDeuKWpOmSV7l4giyJZuB35-ccyVcZU2NUjiG865LbXx9fc1Jy9ncrZkhwHBHUg6xAFi271D6QfwvPnovGDNadVnD3P0OjYefotVrAJ_WjKs8yN6jtQ9hpv1bL38mnA-xi96koxuIm8PkDHgwr_tb1PelsNRyo1UXXzzZlQigtCVm40)

![Diagrama ERD](https://www.plantuml.com/plantuml/png/jLXjRzou4VwkNy4cG6e3zLqIdtt6mJLklRj2ZJfnpYz1aQ8GnjJOdfeYrT78IPpalrVYQcanviRT3zqlziupmvsNXmyf_Tu8iBILoOJ4e3hxurYTeW4XPy6e6GXSGjFPzGrMe3RcfxDDjQLchWAoweqhqQYfgsg3acL_LcSemAIkqIA3GLMZ5MwLDi0a8AWQKWO-krQKguKgkWV9cbkoDJ1Kof15-LoZSYmtRWHOLsA3jVhrvSZ8O6zxurvaTAqjjMEBhFQo6ofHQM9jSEIZ3JHDxgoSqJsgLxzy3qr4c2vRmGUVxIpxKubNP56z-_l6fqsrVtcmhvrn_DlpmxsztPU71_ilRl7pmVxFtdgWFkt_V7aGWl95q12nupzTX25xkqLMGCieCli-8VKbK-eGzEsrBs_Q-wddkV_qYECIUM4Trkwi4sRtSJ1k-q_shP_HNx__gDd9c_dReujZbL-SlZtwS79wf3RwGB2WpWJiDMm-ZihgBX-uHlKiRzdIlMDIBzGKp6BG6sTjNJiMijURpxfSP_bk_jhNWLP8FgjdLykmPmeQ5RxrTSrtfrFlg_fkKraiqKtrcxguE9fvQ6lB_z6kmwdHO3hm_UHq-hV9wSREoqqFbcZRYXX7qEkTdGNMKn_3qebWiasDMeWAYzhPAySL5jhPflCzUdi-9A4steDfKPrS_hjpRZQNUT4bsE8EZ0F6nE9GCZPGDC9GGfnUvD3b3pp6utZZzD3oaS-hbx5JtwBQDG8ciP1sjgI-I46kq1Qr0O7YdWmEPKPEbRCOvRlTh_8j6mssqOvH0vSjKs5Q2un-n6ExP-_sV3UQSd18ZJbVJOGQcHoKK93LMBVIORF9-Jp6cYlWXYBC2LqLDJkDrjldqwCta-EDdR-yNdI6-qOmDZNPQsHCkGoqy0FebiBKEhxm4HDYFExTGBdbhWXVqmqBSAfXjgsIdAWPKr0w4Q9YoQznGlJ3V0XN3z0uYS25LRiAR0a993dmF5rza8CA19d0fChtkpvacjgPTyWbQOcHc4or0KjNf44ywIB2fNoQgx6BrumJzQvpcFnn70G3_cCAItTe4Xpo83PWXPmD-tA8uGAqnhelx34K-1kmUD1MTpvGE-1Xn9qXETpEdXnhFFarHfXWWuRs7Wg64ZHPw93p-J_EHsEpEgNJGpydMgYrE7NSPt873urT04M9ZMP2nc7X2Epxv6m3bvIWJN0RHF9Xcj-UF5Id_JyVHaVgr5-AVdYaJbs5zfxKf6PtH_VaB33sAp0EToz_gFwIx-Npw53LYy02ne5-h0EYdizsvvDyBumssdvhoMuVs5OrC8ZZCU2FhEMYgMQM813mqCh1RkYE-rbhv5PMmpMxYfeOnzhTawpUzN5A2GhD6w4A68yNFn8OX8EzA25EAG0LYYiX9Qg9wHYoj13uoAAUpm_pdVmmcasFfFKq6jqw4gZ1YfVqnuTIv-GL9_9ggsYLS8n8qLeIuDNBn7x_o-N1E6c7ms_nRxefJjPlYN49QgpHhR6YFz6ncgaYWNNx6aXUiolRuNYAwO-VQcBW25YGEovlvp7xkb0i2OVkXc_91Y_HDPad3BQf7Gk8uzIbrd817ozNQaBusNK4DfzCpo_6pompHgZbYOSMq9vTB_fZM2X-Nl7IEir_ZPzN5X512tqN1sEdgZlvpdmxwk42MwMgwzzw6kJaVSNBxTWUTRGYGNxCeqP3YLRWCO-6j5UBvFl5Riy7lnVgh4iWIANN0hyCGo5tqHu8jvd1wMdS2PYqIzDM_eub96c7fyGcZ7Iiny4owF4ad-Il7klnqxED7TRJvRIGjjPZtGfDo_3_L6IVgiJJIp-kMEA2KMCjG_ywGx9hdNszVU19tOUb6eLtQALeMcwWe78SvhazP9JGI4lnHaaLD693-BQQ2J-iB839M-_wl5XMvYm8h4cInxKwBidYuddVbpis92ls3cjodX6aA75iWvhTPMmD4_8_iL9xWjG42OYOdjI8DCLN7Ykdy-F9-L5tcio85q-wLuxzfgnb-UvIjmoIE5ww1L_zDBm_9LMZkaCMqc10RRm39YkejWxKrDLaDhE7LrvVlswLxgFTSb-YboHPUCADN0Oh1Pn6y5TbDMVPSAyCzgzVugjcxE3N7UvRAIJS5R9iCUDeuKWpOmSV7l4giyJZuB35-ccyVcZU2NUjiG865LbXx9fc1Jy9ncrZkhwHBHUg6xAFi271D6QfwvPnovGDNadVnD3P0OjYefotVrAJ_WjKs8yN6jtQ9hpv1bL38mnA-xi96koxuIm8PkDHgwr_tb1PelsNRyo1UXXzzZlQigtCVm40)