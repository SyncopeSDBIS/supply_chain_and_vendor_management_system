# GHID COMPLET: GIT WORKFLOW (COMMIT PE FIECARE FEATURE) & PIPELINE CI/CD (GITHUB ACTIONS)
## Supply Chain and Vendor Management System (MSD + OOAD)

Acest document definește procedura exactă prin care echipa (**Balan Mugurel-Ionuț, Durnea Andrei-Sebastian, Gurzu Teodor**) va lucra cu Git și GitHub Actions astfel încât:
1. Fiecare funcționalitate (Use Case / BR / Agregat) să aibă propriul său **Feature Branch** și **commit-uri mici, atomice**, vizibile clar în istoricul Git la evaluare;
2. AI-ul (**Cursor / Antigravity**) să fie forțat să implementeze și să facă commit **câte un singur feature pe rând**, împreună cu testele sale, fără a genera commit-uri gigantice de 100 de fișiere;
3. **Pipeline-ul CI/CD** să ruleze automat și rapid la fiecare `push` și `Pull Request`, validând compilarea, testele unitare, regulile de arhitectură **ArchUnit**, testele de integrare (**Testcontainers**) și securitatea.

---

## 1. Strategia de Branching (Git Flow Simplificat pentru 3 Studenți)

Nu se face **NICIODATĂ** commit direct pe `main` sau `develop`.

```text
main (cod stabil, livrabil la final de Sprint: tag v1.0-sprint1, v2.0-sprint2 etc.)
  ↑ Pull Request (la final de Sprint)
develop (branch-ul de integrare al echipei — aici se unesc modulele comenzi, calitate, facturi)
  ↑ Pull Request (după ce CI/CD este VERDE)
  ├── feature/sprint2-shared-kernel             (Mugurel)
  ├── feature/sprint2-comenzi-domain            (Mugurel)
  ├── feature/sprint2-calitate-domain           (Andrei)
  ├── feature/sprint2-facturi-domain            (Teodor)
  ├── feature/sprint2-uc1-creeaza-comanda       (Mugurel)
  ├── feature/sprint2-uc2-executa-inspectie     (Andrei)
  └── feature/sprint2-uc3-reconciliaza-factura  (Teodor)
```

### Regula de Aur pentru ca Pipeline-ul CI/CD să fie mereu VERDE:
**Un commit = O unitate completă care compilează + Testul ei aferent.**
* **GREȘIT:** Faci commit doar la `ComandaCtrl.java` care apelează o metodă inexistentă din `ComandaApplicationService` $\rightarrow$ Pipeline-ul CI/CD crapă la `mvn compile`.
* **GREȘIT:** Faci commit la o clasă din `domain` care importă `org.springframework.stereotype.Service` $\rightarrow$ Pipeline-ul CI/CD crapă la pasul `ArchUnit`.
* **CORECT (Bottom-Up Atomic Commit):** Faci commit în ordinea straturilor:
  1. `feat(shared): add identity value objects and Perioada` (+ test unitar `PerioadaTest`)
  2. `feat(comenzi): add ComandaAprovizionare and Lot aggregate` (+ test unitar `ComandaAprovizionareTest`)
  3. `feat(comenzi): add EstimareLeadTimeService with Strategy pattern` (+ test `EstimareLeadTimeServiceTest`)
  4. `feat(comenzi): implement UC1 creeazaComanda in ComandaApplicationService` (+ test cu Mockito)

---

## 2. Convenția de Nume pentru Branch-uri și Commit-uri (Conventional Commits)

Pentru ca profesorul să vadă trasabilitatea 1:1 cu documentația OOAD și cu Sprinturile MSD, folosiți formatul:

### Format Branch:
`feature/s<nr_sprint>-<modul>-<scurta-descriere>`
* Exemple:
  * `feature/s2-shared-kernel-vos`
  * `feature/s2-comenzi-agregat-comanda`
  * `feature/s2-calitate-scorecard-lunar`
  * `feature/s2-facturi-three-way-match`
  * `feature/s3-comenzi-jpa-repositories`
  * `feature/s4-facturi-rest-api-xml-json`

### Format Mesaj de Commit:
`<tip>(<modul>): <descriere scurtă> [<cod OOAD / BR / UC>]`

Tipuri permise:
* `feat`: adăugare de cod nou (entitate, value object, serviciu, controller)
* `test`: adăugare sau actualizare de teste (JUnit, Mockito, ArchUnit, Testcontainers)
* `refactor`: modificare de cod fără schimbarea comportamentului (ex. trecerea la JPA în Sprint 3)
* `docs`: actualizare diagrame PlantUML, README, ADR, OpenAPI
* `ci`: configurare GitHub Actions, Docker, Maven plugins
* `fix`: rezolvarea unui bug sau a unei erori din pipeline

---

## 3. Planul Exact de Commit-uri pe Sprinturi (Checklist pas cu pas)

Dacă urmați exact această listă de commit-uri, fiecare membru al echipei va avea ~10–15 commit-uri curate pe fiecare Sprint, iar pipeline-ul va fi **100% verde** la fiecare pas.

### Etapa 0 — Fundația & ArchUnit (Înainte de codul de business)
1. `ci(root): initialize maven project with spring boot 3, java 21 and quality plugins`
2. `ci(github): add github actions workflow for build, unit tests and archunit`
3. `docs(adr): add initial ADRs and OOAD plantuml diagrams`
4. `test(archunit): add LayerArchitectureTest, DomainIsolationTest and ModuleDependencyTest`
5. `feat(shared): implement IdComanda, IdFurnizor, IdLot, CodMaterial and Perioada VOs [Shared Kernel]`

### Sprint 2 — Domain Model & Business Services (Fiecare pe branch-ul său!)

#### Stud. 1 — Balan Mugurel-Ionuț (`comenzi`)
1. `feat(comenzi): add StareComanda, StareLot, RezultatCalitateLot enums and Cantitate, PretUnitar VOs`
2. `feat(comenzi): add LinieComanda, Expediere, EstimareLivrare, MetriciLivrareVO and IstoricLivrariVO`
3. `feat(comenzi): implement ComandaAprovizionare aggregate root and Lot child entity [BR-PO-01..06]`
4. `test(comenzi): add unit tests for ComandaAprovizionare invariants and cascading cancellation`
5. `feat(comenzi): implement EstimareLeadTimeService with Strategy pattern [BR-PO-07]`
6. `test(comenzi): add unit and SLA (<300ms) tests for EstimareLeadTimeService`
7. `feat(comenzi): add ComandaRepo, LotRepo, IstoricLivrariRepo and IGestiuneFurnizoriAdapter ports`
8. `feat(comenzi): implement ComandaApplicationService and CQRS views [UC1-UC10]`
9. `test(comenzi): add workflow unit tests for ComandaApplicationService`

#### Stud. 2 — Durnea Andrei-Sebastian (`calitate` + `notificari`)
1. `feat(calitate): add enums and VOs (Scor, PlanAQL, Certificare, Iban, Email, Adresa, FullName)`
2. `test(calitate): add unit tests for Scor (40/40/20 grade mapping) and PlanAQL (ISO 2859-1)`
3. `feat(calitate): implement Furnizor aggregate root and ScorecardLunar entity [BR-SQ-01..04]`
4. `feat(calitate): implement InspectorCalitate, InspectieCalitate and RaportNeconformitate aggregates [BR-SQ-05..09]`
5. `feat(calitate): implement CalculScorecardService and AlocareInspectorService domain services`
6. `feat(calitate): add repository interfaces and IPerformantaLivrariAdapter port`
7. `feat(calitate): implement CalitateApplicationService and MonitorizareFurnizoriView [Q1-Q9]`
8. `test(calitate): add JUnit tests for validation, scorecard computation and inspection workflows`

#### Stud. 3 — Gurzu Teodor (`facturi` + `securitate`)
1. `feat(facturi): add enums and VOs (Suma, ValoareFactura, LinieFactura, LinieNIR, Toleranta)`
2. `test(facturi): add unit tests for Toleranta (+-2%) and ValoareFactura calculations`
3. `feat(facturi): implement FacturaFurnizor, NIR and OrdinPlata aggregates [BR-IP-01..09]`
4. `test(facturi): add unit tests for Financial Control invariant (package-private OrdinPlata)`
5. `feat(facturi): implement ReconciliereService three-way match domain service [UC3]`
6. `feat(facturi): add FacturaRepo, NIRRepo, OrdinPlataRepo, FurnizoriBlocatiRepo and IComenziAdapter`
7. `feat(facturi): implement FacturaApplicationService and ScadentarFacturiView [I1-I8]`
8. `feat(securitate): implement RBAC filter and immutable audit logger via IJurnalAudit`

### Sprint 3 — Persistență JPA, Repositories & Event-Driven (Outbox/Inbox)
1. `ci(docker): add docker-compose for PostgreSQL and RabbitMQ`
2. `feat(db): add Flyway migrations V1__shared_outbox_inbox.sql to V4__facturi_schema.sql`
3. `feat(shared): implement AbstractRepositoryJPA, OutboxRepoJPA, InboxRepoJPA and JPA AttributeConverters`
4. `feat(comenzi): add JPA mappings, ComandaRepoJPA, LotRepoJPA and EntityListeners`
5. `feat(calitate): add JPA mappings, FurnizorRepoJPA, InspectieRepoJPA, InspectorRepoJPA and NCRRepoJPA`
6. `feat(facturi): add JPA mappings, FacturaRepoJPA, NIRRepoJPA and OrdinPlataRepoJPA`
7. `feat(messaging): implement OutboxDispatcherJMS and idempotent JMSMessageReceiver across contexts`
8. `test(integration): add Testcontainers JPA and idempotent event redelivery tests`

### Sprint 4 — REST API, Content Negotiation (JSON/XML), ACL Adapters & Stripe
1. `feat(config): configure Jackson JSON and JAXB XML message converters in WebConfig`
2. `feat(comenzi): implement ComandaCtrl, ComenziApiRest (Open Host) and GestiuneFurnizoriAdapterRest`
3. `feat(calitate): implement CalitateCtrl, FurnizoriApiRest (Open Host) and PerformantaLivrariAdapterRest`
4. `feat(facturi): implement FacturaCtrl, ComenziAdapterRest (ACL) and StripeBankPaymentAdapter`
5. `test(rest): add RestTemplate and Apache HttpClient integration tests for all endpoints`

---

## 4. Cum forțezi Cursor / Antigravity să facă Commit pentru fiecare Feature în parte

Adăugați această regulă în `.cursorrules` (sau dați-o în prompt când începeți o sesiune de lucru în Cursor/Antigravity):

```text
GIT & INCREMENTAL FEATURE WORKFLOW RULE:
Never generate more than ONE feature / vertical slice at a time.
For every task I give you, follow this strict 5-step execution loop:
1. Implement ONLY the requested class(es) for the current feature, respecting 1:1 the names from Project_OOAD.docx.
2. Immediately generate/update the corresponding JUnit 5 test class for this exact feature.
3. Run `mvn test-compile` and `mvn test -Dtest=<CurrentTestName>,*ArchTest` in the terminal to verify that both the new unit test and all ArchUnit rules pass.
4. Do NOT proceed to the next feature until the test is GREEN.
5. Propose the exact git commands to stage and commit ONLY the files belonging to this feature:
   `git add <files>`
   `git commit -m "feat(<module>): <description> [<OOAD-Ref>]"`
```

### Fluxul de comenzi în terminal pentru fiecare student când lucrează la un feature:
```bash
# 1. Te asiguri că ești pe develop la zi
git checkout develop
git pull origin develop

# 2. Creezi branch dedicat pentru feature-ul curent
git checkout -b feature/s2-comenzi-agregat-comanda

# 3. Generezi/scrii codul + testul unitar pentru feature
# 4. Verifici LOCAL că trece totul (inclusiv ArchUnit!) înainte de commit:
./mvnw clean test

# 5. Faci commit doar pe fișierele acelui feature:
git add src/main/java/.../comenzi/domain/model/ComandaAprovizionare.java \
        src/main/java/.../comenzi/domain/model/Lot.java \
        src/test/java/.../unit/comenzi/ComandaAprovizionareTest.java

git commit -m "feat(comenzi): implement ComandaAprovizionare root and Lot entity [BR-PO-01..06]"

# 6. Împingi branch-ul pe GitHub (declanșează automat pipeline-ul CI/CD!)
git push -u origin feature/s2-comenzi-agregat-comanda
```

---

## 5. Configurația Completă a Pipeline-ului CI/CD (`.github/workflows/ci-cd.yml`)

Pentru ca pipeline-ul vostru de GitHub Actions să fie **rapid, modular, clar vizibil în interfața GitHub** (cu 5 job-uri distincte legate într-un graf vizual ca la companii) și să nu pice din motive stupide, creați fișierul `.github/workflows/ci-cd.yml` cu următorul conținut:

```yaml
name: Enterprise CI/CD Pipeline (MSD & OOAD)

on:
  push:
    branches:
      - main
      - develop
      - 'feature/**'
      - 'release/**'
      - 'hotfix/**'
  pull_request:
    branches:
      - main
      - develop

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

env:
  JAVA_VERSION: '21'
  JAVA_DISTRIBUTION: 'temurin'

jobs:
  # ============================================================================
  # JOB 1: BUILD & COMPILE VALIDATION
  # ============================================================================
  build:
    name: 1. Build & Compile (Java 21)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven

      - name: Compile Source & Test Code
        run: ./mvnw clean test-compile -B -DskipTests

  # ============================================================================
  # JOB 2: ARCHUNIT — ARCHITECTURAL FITNESS FUNCTIONS
  # ============================================================================
  archunit:
    name: 2. ArchUnit (Clean Arch & DDD Boundaries)
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven

      - name: Run ArchUnit Tests
        run: ./mvnw test -B -Dtest="ro.uaic.feaa.sdbis.supplychain.architecture.**.*Test"

  # ============================================================================
  # JOB 3: UNIT & DOMAIN SLA TESTS (JUnit 5 + Mockito + JaCoCo)
  # ============================================================================
  unit-tests:
    name: 3. Domain & Workflow Unit Tests
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven

      - name: Execute Unit Tests (Comenzi, Calitate, Facturi)
        run: ./mvnw test -B -Dtest="ro.uaic.feaa.sdbis.supplychain.unit.**.*Test"

      - name: Generate JaCoCo Coverage Report
        run: ./mvnw jacoco:report -B

      - name: Upload Coverage Report Artifact
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-unit-coverage-report
          path: target/site/jacoco/

  # ============================================================================
  # JOB 4: INTEGRATION TESTS (PostgreSQL, RabbitMQ/JMS, REST API)
  # ============================================================================
  integration-tests:
    name: 4. Integration & Persistence Tests (Testcontainers)
    needs: [archunit, unit-tests]
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven

      - name: Execute Integration & REST Tests
        run: ./mvnw verify -B -Dtest="ro.uaic.feaa.sdbis.supplychain.integration.**.*Test,ro.uaic.feaa.sdbis.supplychain.api.**.*Test"

  # ============================================================================
  # JOB 5: QUALITY GATE, SECURITY SCAN & PACKAGE (DevSecOps)
  # ============================================================================
  quality-and-package:
    name: 5. Quality Gate & Package Artifact
    needs: [integration-tests]
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven

      - name: Package Application JAR/WAR
        run: ./mvnw package -B -DskipTests

      - name: Upload Production Artifact
        uses: actions/upload-artifact@v4
        with:
          name: supply-chain-system-artifact
          path: target/*.jar
```

---

## 6. Cum configurați `pom.xml` ca Pipeline-ul să nu pice când un pachet de teste e încă gol

O problemă foarte frecventă în Sprint 1 și Sprint 2 este că, atunci când rulați `-Dtest="...integration.**.*Test"` în GitHub Actions, dar nu ați scris încă niciun test de integrare (pentru că sunteți abia în Sprint 2 la Domain Layer), Maven dă eroare: `No tests were executed!`.

Pentru ca pipeline-ul să fie **mereu verde** în toate Sprinturile (chiar și la primul commit), adăugați în `pom.xml` următoarea configurare pentru `maven-surefire-plugin`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <!-- CRITIC: Permite rularea joburilor din CI/CD chiar dacă în Sprint 1/2
             nu există încă teste în pachetul integration sau api -->
        <failIfNoTests>false</failIfNoTests>
    </configuration>
</plugin>
```

---

## 7. Script de Verificare Locală Pre-Commit (`scripts/pre-commit-check.sh`)

Pentru a fi 100% siguri că niciun coleg nu împinge pe GitHub un commit care strică pipeline-ul, puteți crea un mic script (sau un Git Hook în `.git/hooks/pre-commit`) pe care îl rulați înainte de `git commit`:

```bash
#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "🔍 [1/3] Compilare proiect (Java 21)..."
echo "=========================================================="
./mvnw test-compile -B -q

echo "=========================================================="
echo "🏛️ [2/3] Verificare reguli de arhitectură (ArchUnit)..."
echo "=========================================================="
./mvnw test -Dtest="ro.uaic.feaa.sdbis.supplychain.architecture.**.*Test" -DfailIfNoTests=false -B -q

echo "=========================================================="
echo "✅ [3/3] Rulare teste unitare..."
echo "=========================================================="
./mvnw test -Dtest="ro.uaic.feaa.sdbis.supplychain.unit.**.*Test" -DfailIfNoTests=false -B -q

echo "🎉 TOTUL ESTE VERDE! Poți face git commit & git push în siguranță."
```

---

## 8. Template pentru Pull Request (`.github/pull_request_template.md`)

Adăugați acest fișier în repository. Când Mugurel, Andrei sau Teodor deschide un Pull Request din `feature/...` către `develop`, GitHub va completa automat această listă de verificare (care demonstrează profesorului rigoarea inginerească):

```markdown
## Descriere Feature
- **Bounded Context / Modul:** `comenzi` | `calitate` | `facturi` | `shared` | `securitate`
- **Referință OOAD (Use Case / BR / Invariant):** ex. `UC1 CreeazaComandaAprovizionare`, `BR-PO-01`
- **Sprint MSD:** Sprint 1 / Sprint 2 / Sprint 3 / Sprint 4

## Ce a fost implementat (1:1 cu Project_OOAD.docx)
- [ ] Clase de Domeniu (Aggregate Root / Entity / Value Object / Domain Service)
- [ ] Application Service / Command / Query View (CQRS)
- [ ] Infrastructură (JPA Repository / Outbox / Inbox / REST Adapter)
- [ ] Prezentare (REST Controller / DTO JSON+XML)

## Checklist de Calitate (Obligatoriu pentru Merge)
- [ ] Numele claselor și metodelor respectă **1:1** diagramele din `Project_OOAD.docx`
- [ ] `DomainLayer` nu importă clase din `Spring`, `Hibernate`, `Jackson` sau `Stripe`
- [ ] Testele unitare JUnit 5 acoperă Happy Path + Error Path + Invarianții din tabel
- [ ] Testele **ArchUnit** trec (`✅ Green`)
- [ ] Pipeline-ul **GitHub Actions CI/CD** este complet verde (`✅ Green`)
```