# Supply Chain & Vendor Management System (Procure-to-Pay)

Sistem enterprise de gestiune a lanțului de aprovizionare și a relațiilor cu furnizorii (**Procure-to-Pay - P2P**), implementat conform principiilor **Domain-Driven Design (DDD)**, **Arhitecturii Hexagonale (Ports & Adapters)**, modelului **Event-Driven Architecture (EDA)** cu tranzacționalitate garantată prin șabloanele **Transactional Outbox / Idempotent Inbox**, integrat cu procesatorul de plăți **Stripe** și brokerul de mesagerie **RabbitMQ**.

Proiectul este aliniat 1:1 cu specificațiile academice și industriale din **Project_OOAD.docx** și cerințele evaluate în Sprinturile 1–4 (cu accent pe nucleul de domeniu bogat din **Sprintul 2 / ADR-003**).

---

## 📑 Cuprins
1. [Ghid Rapid de Pornire (Quick Start)](#-1-ghid-rapid-de-pornire-quick-start)
   - [Metoda A: Rulare Completă prin Docker Compose (Recomandat)](#metoda-a-rulare-completă-prin-docker-compose-recomandat)
   - [Metoda B: Rulare Hibridă (Postgres & RabbitMQ în Docker + App Local)](#metoda-b-rulare-hibridă-baza-și-brokerul-în-docker--aplicația-local)
   - [Metoda C: Rulare Locală Minimală (H2 In-Memory, Fără Docker)](#metoda-c-rulare-locală-minimală-h2-in-memory-fără-docker)
2. [Puncte de Acces, Porturi & Credențiale](#-2-puncte-de-acces-porturi--credențiale)
3. [Cum se Utilizează și se Testează Aplicația](#-3-cum-se-utilizează-și-se-testează-aplicația)
   - [Testare Interactivă prin Swagger UI](#31-testare-interactivă-prin-swagger-ui)
   - [Testare End-to-End prin Colecția Postman](#32-testare-end-to-end-prin-colecția-postman)
   - [Monitorizare Asincronă în RabbitMQ Dashboard](#33-monitorizare-asincronă-în-rabbitmq-dashboard)
   - [Conectare Directă la Baza de Date PostgreSQL](#34-conectare-directă-la-baza-de-date-postgresql)
4. [Ghid de Depanare (Troubleshooting & FAQ)](#-4-ghid-de-depanare-troubleshooting--faq)
   - [Eroare Flyway: Migration Checksum Mismatch](#1-eroare-flyway-migration-checksum-mismatch)
   - [Aplicația este în stare Restarting sau Health Starting](#2-aplicația-este-în-stare-restarting-sau-health-starting)
   - [Porturile 8080, 5433 sau 5672 sunt deja ocupate](#3-porturile-8080-5433-sau-5672-sunt-deja-ocupate)
5. [Rularea Testelor Automate (Unit, BDD, ArchUnit, SLA)](#-5-rularea-testelor-automate)
6. [Arhitectură, Bounded Contexts & Reguli de Domeniu](#-6-arhitectură-bounded-contexts--reguli-de-domeniu)
7. [Documentație Suplimentară a Proiectului](#-7-documentație-suplimentară-a-proiectului)

---

## 🚀 1. Ghid Rapid de Pornire (Quick Start)

### Cerințe Preliminare
* **Docker Desktop** instalat și pornit (cu Docker Compose v2+).
* *(Opțional pentru rulare fără container)*: **Java 21 JDK** și **Maven 3.9+** (sau se utilizează wrapper-ul inclus `mvnw` / `mvnw.cmd`).

---

### Metoda A: Rulare Completă prin Docker Compose (Recomandat)
Această metodă construiește și pornește automat întreaga stivă: aplicația Spring Boot 3.3.5, baza de date PostgreSQL 16 și brokerul RabbitMQ 3 Management.

1. **Deschide un terminal** (PowerShell sau Command Prompt) în rădăcina proiectului:
   ```powershell
   cd c:\Users\bmugu\Desktop\app
   ```

2. **Pornește containerele cu build curat**:
   ```powershell
   docker-compose up --build -d
   ```

3. **Verifică starea serviciilor**:
   ```powershell
   docker-compose ps
   ```
   *După circa 20–30 secunde (necesare compilării, inițializării Flyway și pornirii Spring Boot), toate cele 3 containere trebuie să fie în starea `healthy`:*
   ```text
   NAME                   IMAGE                          STATUS
   supplychain-app        app-app                        Up (healthy)   0.0.0.0:8080->8080/tcp
   supplychain-postgres   postgres:16-alpine             Up (healthy)   0.0.0.0:5433->5432/tcp
   supplychain-rabbitmq   rabbitmq:3-management-alpine   Up (healthy)   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
   ```

4. **Pentru a opri aplicația**:
   ```powershell
   docker-compose down
   ```

---

### Metoda B: Rulare Hibridă (Baza și Brokerul în Docker + Aplicația Local)
Utilă pentru dezvoltare activă și debugging direct în IDE (IntelliJ IDEA / VS Code / Eclipse):

1. **Pornește doar infrastructura de suport (PostgreSQL și RabbitMQ)**:
   ```powershell
   docker-compose up -d postgres rabbitmq
   ```

2. **Pornește aplicația Spring Boot local folosind profilul `postgres`**:
   * Pe Windows:
     ```powershell
     .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
     ```
   * Pe Linux / macOS:
     ```bash
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
     ```

---

### Metoda C: Rulare Locală Minimală (H2 In-Memory, Fără Docker)
Dacă nu dorești să utilizezi Docker, aplicația deține o configurație implicită autonomă ce rulează pe baza de date în memorie **H2** (în mod de compatibilitate PostgreSQL):

```powershell
.\mvnw.cmd spring-boot:run
```
*Aplicația va porni instantaneu pe portul `8080`, creând tabelele automate în memoria RAM.*

---

## 🌐 2. Puncte de Acces, Porturi & Credențiale

Odată pornită aplicația (prin Docker sau local), accesează serviciile prin următoarele linkuri:

| Serviciu | URL de Acces | Credențiale / Notă | Descriere |
| :--- | :--- | :--- | :--- |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | *Fără autentificare* | Interfață web interactivă pentru explorarea și testarea tuturor endpoint-urilor REST |
| **OpenAPI Docs (JSON)** | [http://localhost:8080/api-docs](http://localhost:8080/api-docs) | *Fără autentificare* | Specificația tehnică OpenAPI 3.0 pentru import în alte instrumente |
| **RabbitMQ Management** | [http://localhost:15672](http://localhost:15672) | **User:** `guest`<br>**Parolă:** `guest` | Panou de control web pentru cozi, topicuri și mesaje asincrone |
| **Spring Actuator Health**| [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | *Fără autentificare* | Diagnostic de sănătate (stare conexiuni DB, RabbitMQ, DiskSpace) |
| **Actuator Metrics** | [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) | *Fără autentificare* | Metrici JVM, trafic HTTP, pool-uri de conexiuni HikariCP |
| **Baza de Date PostgreSQL**| `localhost:5433` *(extern)*<br>`postgres:5432` *(intern)* | **DB:** `supplychain_db`<br>**User:** `supplychain`<br>**Parolă:** `supplychain_secret` | Conectare din clienți SQL externi (DBeaver, pgAdmin, DataGrip) |
| **RabbitMQ AMQP Broker** | `localhost:5672` | **User:** `guest`<br>**Parolă:** `guest` | Portul binar de mesagerie utilizat de Spring AMQP |

---

## 🧪 3. Cum se Utilizează și se Testează Aplicația

### 3.1. Testare Interactivă prin Swagger UI
Cea mai rapidă modalitate de a testa funcționalitatea de business fără a instala aplicații terțe:
1. Deschide în browser: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**.
2. Vei vedea rutele grupate pe Bounded Contexts:
   * `/api/comenzi`: Inițiere comenzi, aprobare, confirmare furnizor, expediere, recepție, anulare în cascadă a loturilor.
   * `/api/furnizori` & `/api/inspectii`: Calificare furnizori, alocare inspectori, introducere rezultate AQL, emitere rapoarte NCR, calcul scorecard lunar.
   * `/api/facturi` & `/api/nir`: Înregistrare factură, emitere NIR depozit, declanșare 3-Way Matching, emitere ordine de plată.
3. Fă click pe un endpoint (ex: `POST /api/comenzi`), apasă butonul **„Try it out”**, modifică corpul JSON dorit și apasă **„Execute”**. Răspunsul HTTP și detaliile calculate de nucleul de domeniu vor apărea imediat dedesubt.

---

### 3.2. Testare End-to-End prin Colecția Postman
Proiectul include o suită completă de teste API în fișierul:
📁 [`tools/postman/SupplyChain_P2P_Collection.json`](tools/postman/SupplyChain_P2P_Collection.json)

**Instrucțiuni de import și rulare:**
1. Deschide **Postman** (sau *Insomnia*).
2. Fă click pe **Import** (colțul din stânga sus) și selectează fișierul `SupplyChain_P2P_Collection.json`.
3. Verifică ca variabila de mediu a colecției să aibă valoarea:
   * `baseUrl` = `http://localhost:8080`
4. Colecția este organizată în 5 directoare logice conform fluxului Procure-to-Pay:
   * **1. Comenzi Aprovizionare (UC1 - UC8)**: Plasare comandă, confirmare furnizor, generare loturi, anulare în cascadă.
   * **2. Gestiune Furnizori & Calitate (Q1 - Q8)**: Înregistrare furnizor, alocare inspector pe bază de matrice de competențe, verdict AQL, generare raport de neconformitate (NCR).
   * **3. Facturi, NIR & 3-Way Matching (I1 - I4)**: Recepție NIR, înregistrare factură furnizor, confruntare automată trilaterală cu marjă de toleranță.
   * **4. Plăți, Ordin de Plată & Stripe Webhooks (I5 - I7)**: Emitere ordin de plată, principiul Four-Eyes (aprobare dublă la sume > 10.000 EUR), simulare webhook-uri semnate Stripe (`payment_intent.succeeded` / `payment_failed`).
   * **5. Actuator & Healthcheck**: Verificarea stării subsistemelor.

---

### 3.3. Monitorizare Asincronă în RabbitMQ Dashboard
1. Deschide [http://localhost:15672](http://localhost:15672) și autentifică-te cu `guest` / `guest`.
2. Mergi la tab-ul **Queues**:
   * `comenzi.events.queue` (recepționează evenimente din comenzi)
   * `calitate.events.queue` (recepționează alerte calitative și blocări furnizori)
   * `facturi.events.queue` (recepționează evenimente de reconciliere și plăți)
   * `supplychain.events.dlq` (Dead-Letter Queue pentru mesaje eșuate)
3. Poți inspecta în timp real modul în care evenimentele serializate din tabela de Outbox sunt preluate și dirijate către cozi.

---

### 3.4. Conectare Directă la Baza de Date PostgreSQL
Dacă dorești să verifici tabelele persistate și istoricul de audit:
* **Host**: `localhost`
* **Port**: `5433`
* **Database**: `supplychain_db`
* **Username**: `supplychain`
* **Password**: `supplychain_secret`

Tabele principale de inspectat:
* `comenzi_aprovizionare`, `linii_comanda`, `loturi`: Agregatul de comenzi.
* `furnizori`, `inspectii_calitate`, `rapoarte_neconformitate`, `scorecarduri_lunare`: Agregatele de calitate.
* `facturi_furnizori`, `note_intrare_receptie`, `ordine_plata`: Agregatele financiar-contabile.
* `outbox_events`, `inbox_events`: Tabelele șabloanelor de mesagerie garantată.
* `shared_audit_log`: Jurnalul imutabil de trasabilitate și audit operațional.

---

## 🔧 4. Ghid de Depanare (Troubleshooting & FAQ)

### 1. Eroare Flyway: Migration Checksum Mismatch
**Simptom:** La pornire, containerul `supplychain-app` intră în `Restarting (1)` și logurile afișează:
```text
Caused by: org.flywaydb.core.api.exception.FlywayValidateException: Validate failed: Migrations have failed validation
Migration checksum mismatch for migration version 1 (sau 2, 3, 4)
```
**Cauză:** Volumul persistent Docker (`postgres_data`) a reținut schema dintr-o versiune anterioară, iar fișierele SQL de migrare din cod au fost modificate între timp sau au diferențe de caractere sfârșit de linie (CRLF vs LF).
**Rezolvare Rapidă:** Șterge volumul vechi și reconstruiește mediul de la zero:
```powershell
docker-compose down -v
docker-compose up --build -d
```

---

### 2. Aplicația este în stare Restarting sau Health Starting
**Cum verifici exact ce s-a întâmplat:**
Afișează ultimele loguri emise de containerul Spring Boot:
```powershell
docker-compose logs -f app
```
Dacă observi erori la conectarea către PostgreSQL sau RabbitMQ, asigură-te că aceste două containere sunt `healthy` rulând `docker-compose ps`.

---

### 3. Porturile 8080, 5433 sau 5672 sunt deja ocupate
Dacă ai deja o instanță locală de PostgreSQL (de regulă pe portul 5432) sau un server web pe 8080:
* Portul PostgreSQL din `docker-compose.yml` este deja mapat pe **`5433`** pentru a preveni conflictul cu o instalare nativă de Postgres pe 5432.
* Dacă dorești să schimbi portul aplicației, poți seta variabila de mediu înaintea rulării:
  ```powershell
  $env:POSTGRES_PORT=5435
  docker-compose up -d
  ```

---

## 🧪 5. Rularea Testelor Automate

Proiectul dispune de o suită cuprinzătoare de teste automatizate (peste 96 de scenarii) împărțite pe mai multe niveluri:

### Rularea tuturor testelor:
* Pe Windows:
  ```powershell
  .\mvnw.cmd test
  ```
* Pe Linux / macOS:
  ```bash
  ./mvnw test
  ```

### Categorii de Teste Incluse:
1. **Teste de Domeniu (Definition of Done - Sprint 2)** (`src/test/java/.../unit`):
   * `WorkflowRulesTest`: Verifică matricea tranzițiilor de stare și invarianții pe agregate.
   * `WorkflowServicesTest`: Validează fluxurile inter-agregat și cascadarea anulării loturilor.
   * `ComputationRulesTest`: Verifică precizia monetară deterministă (`BigDecimal` cu `HALF_UP`).
   * `ComputationServicesTest`: Verifică formulele de calcul ale scorecard-ului OTIF și reconcilierii.
   * `ValidationRulesTest`: Verifică aruncarea excepțiilor în constructorii Value Objects.
   * `ValidationServicesTest`: Testează regulile de eligibilitate ale partenerilor.
2. **Teste de Arhitectură (ArchUnit)** (`src/test/java/.../architecture`):
   * `BoundedContextIsolationTest`: Garantează că nu există scurgeri de dependențe între cele 3 Bounded Contexts.
   * `AggregateEncapsulationTest`: Verifică că entitățile componente au metode de mutație protejate.
   * `LayerArchitectureTest`: Asigură decuplarea stratului de domeniu de infrastructură și baze de date.
3. **Teste de Integrare & BDD Gherkin (Cucumber)** (`src/test/java/.../bdd`):
   * 6 fișiere `.feature` executate automat ce validează fluxurile funcționale de business.
4. **Teste de Performanță & SLA** (`src/test/java/.../performance`):
   * `PerformanceAndSlaTest`: Verifică conformitatea cu timpii contractuali (calcul lead-time sub 300 ms, reconciliere 5.000 facturi sub 60 s).

---

## 🏛️ 6. Arhitectură, Bounded Contexts & Reguli de Domeniu

Sistemul este împărțit în trei contexte delimitate (Bounded Contexts) independente, deservite de un nucleu comun (Shared Kernel):

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                                SHARED KERNEL                                │
│  - Value Objects: Suma, Cantitate, CodMaterial, IdComanda, IdFurnizor, IdLot│
│  - Mecanismul Transactional Outbox & Inbox (Evenimente de domeniu garantate)│
│  - Jurnalizare Securizată & Audit Operațional (shared_audit_log)            │
└─────────────────────────────────────────────────────────────────────────────┘
              ▲                               ▲                               ▲
              │                               │                               │
┌─────────────┴─────────────┐   ┌─────────────┴─────────────┐   ┌─────────────┴─────────────┐
│      CONTEXT COMENZI      │   │     CONTEXT CALITATE      │   │      CONTEXT FACTURI      │
│                           │   │                           │   │                           │
│ • Agregat:                │   │ • Agregate:               │   │ • Agregate:               │
│   - ComandaAprovizionare  │   │   - Furnizor              │   │   - FacturaFurnizor       │
│   - Lot                   │   │   - InspectieCalitate(AQL)│   │   - NIR                   │
│ • Rol:                    │   │   - InspectorCalitate     │   │   - OrdinPlata            │
│   - Gestiune ciclu viață  │   │   - RaportNeconformitate  │   │ • Rol:                    │
│   - Cascadare anulare lot │   │ • Rol:                    │   │   - 3-Way Matching        │
│   - Estimare lead-time    │   │   - Eșantionare ISO 2859-1│   │   - Principiul Four-Eyes  │
│   - Blocare optimistă     │   │   - Scorecard Lunar OTIF  │   │   - Integrare Stripe Webhk│
└───────────────────────────┘   └───────────────────────────┘   └───────────────────────────┘
```

### Matricea Regulilor de Business & SLA (Project_OOAD Tabelul 1.2)
* **BR-PO-01**: Comenzile pot fi plasate doar către furnizori aflați în starea `ACTIV`.
* **NFR-SLA-04**: Anularea unei comenzi declanșează atomic anularea în cascadă a tuturor loturilor asociate neintrate în depozit.
* **BR-SQ-02**: Evaluare lunară ponderată a furnizorilor: **60% OTIF** (On-Time In-Full) + **40% Rata de Conformitate a Loturilor**.
* **BR-SQ-04/05**: Aplicarea planului statistic de eșantionare ISO 2859-1; deschiderea automată a raportului NCR la respingerea lotului.
* **NFR-SLA-05**: Reconciliere automată trilaterală (3-Way Matching: Comandă vs. NIR vs. Factură) cu marjă de toleranță de $\pm 1\%$ pe cantitate și $\pm 5\text{ EUR}$ pe diferențe de rotunjire TVA.
* **NFR-SLA-06**: Principiul Four-Eyes (Patru Ochi): plățile sub 10.000 EUR sunt aprobate automat; plățile ce depășesc 10.000 EUR necesită semnătura electronică a unui al doilea ofițer financiar.
* **ADR-010**: Verificarea criptografică a semnăturilor webhook-urilor Stripe prin header-ul `Stripe-Signature` cu toleranță de 300 secunde împotriva atacurilor de tip replay.

---

## 📚 7. Documentație Suplimentară a Proiectului

Pentru o aprofundare completă a codului și a deciziilor arhitecturale, consultă documentele din rădăcina proiectului:
* 📄 [`SPRINT_2_COD_CATALOG.md`](SPRINT_2_COD_CATALOG.md) — **Catalogul și Blueprint-ul Arhitectural 1:1** ce documentează individual toate cele 161 de componente (154 clase Java pure de domeniu + 7 fișiere de infrastructură), cu linkuri relative directe către fiecare fișier de pe disc.
* 📄 [`pom.xml`](pom.xml) — Configurația Maven, versiunile dependințelor și plugin-urile de build.
* 📄 [`docker-compose.yml`](docker-compose.yml) — Descrierea serviciilor multi-container și a rețelelor interne.
* 📄 [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) — Pipeline-ul automatizat de integrare continuă în cloud.
