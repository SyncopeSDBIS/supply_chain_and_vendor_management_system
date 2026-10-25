# BEST_PRACTICES_GUIDE.md

# Scop

Acest document definește regulile, principiile și standardele care trebuie respectate pe întreaga durată a proiectului.

Obiectivul este:

- consistență;
- calitate;
- mentenabilitate;
- scalabilitate;
- securitate;
- pregătire pentru microservicii;
- evaluare profesională la OOAD și MSD.

---

# 1. Principii Generale

## 1.1 Architecture First

Arhitectura se proiectează înainte de implementare.

Ordinea corectă:

```text
Requirements
    ↓
Analysis
    ↓
Architecture
    ↓
Contracts
    ↓
Implementation
```

Nu implementăm "direct din controller".

---

## 1.2 Business First

Orice componentă software trebuie să rezolve un requirement identificat.

Orice clasă trebuie să poată fi mapată către:

```text
Business Requirement
    ↓
Functional Requirement
    ↓
Use Case
    ↓
Implementation
```

---

## 1.3 Simplicity Over Cleverness

Preferăm:

```text
Cod simplu
```

în loc de:

```text
Cod impresionant dar greu de întreținut
```

---

## 1.4 Evolutionary Architecture

Deciziile trebuie să permită:

```text
Modular Monolith
    ↓
Microservices
```

fără refactorizări masive.

---

# 2. Repository Best Practices

## 2.1 Repository-ul este produsul

Repository-ul trebuie să fie suficient de clar încât un dezvoltator nou să îl înțeleagă în mai puțin de 15 minute.

---

## 2.2 README Obligatori

README trebuie să răspundă la:

```text
Ce este proiectul?

Cum îl rulez?

Cum îl testez?

Cum contribui?
```

---

## 2.3 Fără Fișiere Inutile

Nu se comite:

```text
node_modules

target

.idea

.vscode

fișiere temporare
```

---

## 2.4 Structura Stabilă

Nu mutăm folderele după începerea implementării decât dacă există ADR pentru asta.

---

# 3. Git Best Practices

## 3.1 Feature Branches

Tot dezvoltarea se face pe:

```text
feature/*
```

niciodată direct pe:

```text
main
```

---

## 3.2 Pull Requests Obligatorii

Nimeni nu face merge fără review.

---

## 3.3 Commit-uri Mici

Preferăm:

```text
10 commit-uri mici
```

în loc de:

```text
1 commit de 200 fișiere
```

---

## 3.4 Commit Convention

```text
feat:

fix:

refactor:

test:

docs:

chore:
```

Exemple:

```text
feat(order): add order creation workflow

fix(payment): handle failed transaction

test(customer): add repository tests
```

---

# 4. Architecture Best Practices

## 4.1 Respectarea Layerelor

Permis:

```text
Presentation
    ↓

Application
    ↓

Domain
```

Interzis:

```text
Controller
    ↓
Repository
```

---

## 4.2 Domain Centric Design

Domain-ul este centrul aplicației.

Nu framework-ul.

---

## 4.3 Dependency Rule

Dependențele trebuie să meargă spre interior.

```text
Infrastructure
    ↓

Domain
```

nu invers.

---

## 4.4 Public API Only

Acces între module exclusiv prin:

```text
module.api
```

Nu prin:

```text
module.infrastructure
module.persistence
module.controller
```

---

# 5. DDD Best Practices

## 5.1 Entități Bogate

Evitați:

```java
anemic entities
```

Logica trebuie să stea cât mai aproape de domeniu.

---

## 5.2 Aggregate Boundaries

Fiecare Aggregate trebuie să aibă:

```text
Owner clar
Responsabilitate clară
```

---

## 5.3 Domain Events

Pentru acțiuni importante:

```text
OrderCreated

PaymentCompleted

OrderCancelled
```

---

## 5.4 Bounded Contexts

Modulele trebuie să poată deveni microservicii.

---

# 6. Java Best Practices

## 6.1 Constructor Injection Only

Preferat:

```java
@RequiredArgsConstructor
```

Evitați:

```java
@Autowired field injection
```

---

## 6.2 Immutability

Preferați:

```java
final
```

unde este posibil.

---

## 6.3 Nu Folosiți Clase Utility Excesiv

Clasele utility trebuie să fie excepția.

---

## 6.4 Nu Expuneți Entități

Controller-ele expun:

```text
DTO
```

Nu:

```text
JPA Entity
```

---

# 7. Spring Best Practices

## 7.1 Constructor Injection

Obligatoriu.

---

## 7.2 Configuration Properties

Preferabil:

```java
@ConfigurationProperties
```

în loc de valori hardcodate.

---

## 7.3 Profile-uri Separate

```text
dev
test
stage
prod
```

---

## 7.4 Service Layer

Controller-ul nu conține logică de business.

---

# 8. API Best Practices

## 8.1 OpenAPI First

Orice endpoint trebuie documentat.

---

## 8.2 Versionare

Exemplu:

```text
/api/v1/orders
```

---

## 8.3 DTO Separation

Separare între:

```text
Request DTO
Response DTO
Entity
```

---

## 8.4 Consistent Errors

Format unic pentru erori.

---

# 9. Database Best Practices

## 9.1 Flyway

Toate modificările bazei de date trebuie să fie în migration scripts.

---

## 9.2 Nu Modificați Manual Schema

Orice schimbare trebuie făcută prin migrare.

---

## 9.3 Indexuri

Index pentru:

```text
foreign keys
search fields
business identifiers
```

---

## 9.4 UUID

Preferabil pentru entități expuse extern.

---

# 10. Messaging Best Practices

## 10.1 Event Driven

Folosiți evenimente pentru decuplare.

---

## 10.2 Idempotency

Procesarea trebuie să suporte:

```text
duplicate messages
```

---

## 10.3 Retry Strategy

Mesajele trebuie retrimise controlat.

---

# 11. Stripe Best Practices

## 11.1 Adapter Pattern

Stripe nu trebuie utilizat direct în business logic.

---

## 11.2 Sandbox First

Toate testele folosesc sandbox.

---

## 11.3 Webhook Validation

Toate webhook-urile trebuie validate.

---

# 12. Security Best Practices

## 12.1 Security by Default

Acces minim necesar.

---

## 12.2 Validate Everything

Validați:

```text
Request Body

Query Params

Headers

Path Variables
```

---

## 12.3 Never Trust User Input

Intrările utilizatorului sunt considerate ostile.

---

## 12.4 Secrets

Nu stocăm:

```text
passwords

keys

tokens
```

în repository.

---

## 12.5 Principle of Least Privilege

Fiecare rol primește strict accesul necesar.

---

# 13. Logging Best Practices

## 13.1 Structured Logging

Folosiți JSON logs.

---

## 13.2 Correlation IDs

Obligatorii.

---

## 13.3 Log Levels

```text
TRACE

DEBUG

INFO

WARN

ERROR
```

---

## 13.4 Nu Logați Secrete

Interzis:

```text
passwords

tokens

card numbers
```

---

# 14. Testing Best Practices

## 14.1 Testing Pyramid

```text
Unit Tests
    ↑

Integration Tests
    ↑

API Tests
```

---

## 14.2 Teste Rapide

Unit tests trebuie să ruleze rapid.

---

## 14.3 Determinism

Teste care dau același rezultat de fiecare dată.

---

## 14.4 Testcontainers

Folosiți infrastructură reală.

---

## 14.5 Coverage

Testați:

```text
happy paths

error paths

edge cases
```

---

# 15. ArchUnit Best Practices

## 15.1 Arhitectura Este Cod

Orice regulă importantă trebuie validată automat.

---

## 15.2 Layer Rules

Verificate permanent.

---

## 15.3 Module Rules

Verificate permanent.

---

## 15.4 Domain Isolation

Domain fără dependențe tehnice.

---

## 15.5 Cycle Detection

Fără dependențe circulare.

---

# 16. DevOps Best Practices

## 16.1 CI Failing Means Stop

Nu ignorăm pipeline-uri roșii.

---

## 16.2 Quality Gates

Obligatorii:

```text
ArchUnit

JUnit

Checkstyle

PMD

SpotBugs
```

---

## 16.3 Automatizare

Preferăm automatizare în loc de procese manuale.

---

# 17. Documentation Best Practices

## 17.1 Documentation as Code

Documentația este versionată.

---

## 17.2 ADR-uri

Fiecare decizie importantă:

```text
ADR
```

---

## 17.3 Actualizare Continuă

Documentația trebuie actualizată odată cu codul.

---

# 18. AI Usage Best Practices

## 18.1 AI Generates

AI propune.

---

## 18.2 Developers Decide

Echipa decide.

---

## 18.3 Validate Everything

Codul AI nu este considerat corect implicit.

---

## 18.4 Review Mandatory

Tot codul generat trebuie:

```text
citit

înțeles

testat

revizuit
```

---

## 18.5 Nu Copiem Orb

Orice cod introdus trebuie explicat de cel care îl comite.

---

# 19. Microservices Readiness Best Practices

## 19.1 Module Boundaries

Respectate strict.

---

## 19.2 Event-Based Communication

Preferată.

---

## 19.3 API Contracts

Stabile.

---

## 19.4 Independent Ownership

Fiecare modul este proprietarul datelor sale.

---

# 20. Golden Rules

1. Architecture before implementation.
2. Requirements before coding.
3. Public APIs between modules.
4. Domain independent from frameworks.
5. Every important decision has an ADR.
6. Every architecture rule has an ArchUnit test.
7. Every feature has tests.
8. Every change passes CI/CD.
9. Documentation evolves with code.
10. Build today as if the system will become microservices tomorrow.