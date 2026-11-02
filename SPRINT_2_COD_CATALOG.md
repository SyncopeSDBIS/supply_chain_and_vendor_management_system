# BLUEPRINT ARHITECTURAL & CATALOGUL DE COD – SPRINT 2
## Structura Fizică 1:1 a Proiectului (Rich Domain Model – ADR-003)

---

### METADATE BLUEPRINT
* **Pachet Rădăcină**: `ro.uaic.feaa.sdbis.supplychain`
* **Scope**: **Sprintul 2 (Rich Domain Model)** alături de infrastructura tehnică de build, containerizare, pipeline CI/CD și testare API Postman.
* **Format**: **Specificație pur structurală și conceptuală**, fără blocuri de cod sursă brut, oferind pentru fiecare fișier explicații caracteristice, concise și specifice rolului său operațional.
* **Navigare Directă**: Toate căile sunt exprimate relativ față de rădăcina proiectului, iar fiecare fișier deține linkuri markdown directe către calea sa relativă ce permit deschiderea imediată în IDE sau pe GitHub.

---

# 1. ARBORELE DE NAVIGARE 1:1 AL PROIECTULUI

Următorul arbore reflectă **strict și 1:1** ierarhia fizică de fișiere din proiect relevante pentru Sprintul 2 și infrastructura asociată:

```text
.
├── .github
│   └── workflows
│       └── ci-cd.yml
├── tools
│   └── postman
│       └── SupplyChain_P2P_Collection.json
├── docker-compose.yml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   └── java/ro/uaic/feaa/sdbis/supplychain
    │       ├── shared
    │       │   ├── annotation (16 fișiere)
    │       │   ├── exception (1 fișier)
    │       │   ├── persistence/converters (5 fișiere)
    │       │   └── [Value Objects universale & Outbox] (17 fișiere)
    │       ├── comenzi
    │       │   ├── domain
    │       │   │   ├── computation (3 fișiere)
    │       │   │   ├── event (5 fișiere)
    │       │   │   ├── factory (1 fișier)
    │       │   │   ├── policy (1 fișier)
    │       │   │   ├── validation (2 fișiere)
    │       │   │   └── [Agregate, Entități & VOs] (21 fișiere)
    │       │   └── infrastructure/configuration (1 fișier)
    │       ├── calitate
    │       │   ├── domain
    │       │   │   ├── computation (2 fișiere)
    │       │   │   ├── event (7 fișiere)
    │       │   │   ├── factory (2 fișiere)
    │       │   │   ├── policy (1 fișier)
    │       │   │   ├── validation (2 fișiere)
    │       │   │   └── [Agregate, Entități & VOs] (28 fișiere)
    │       │   └── infrastructure/configuration (1 fișier)
    │       └── facturi
    │           ├── domain
    │           │   ├── computation (3 fișiere)
    │           │   ├── event (2 fișiere)
    │           │   ├── factory (2 fișiere)
    │           │   ├── policy (1 fișier)
    │           │   ├── validation (2 fișiere)
    │           │   └── [Agregate & VOs] (21 fișiere)
    │           └── infrastructure/configuration (1 fișier)
    └── test
        └── java/ro/uaic/feaa/sdbis/supplychain
            └── unit (6 fișiere DoD)
```

---

# 2. INFRASTRUCTURĂ, CI/CD, CONTAINERIZARE & BUILD TOOLS

Această secțiune descrie componentele de infrastructură tehnică care guvernează ciclul de compilare, testare automatizată, orchestrare prin containere și validare a contractelor API.

### I1. ⚙️ [`pom.xml`](pom.xml)
* **Path**: [`pom.xml`](pom.xml)
* **Tip Componentă**: `Maven Build Descriptor (Configurație Centrală)`
* **Explicație Specifică**: Descriptorul principal de build Maven (Project Object Model). Configurează Spring Boot 3.3.5 pe Java 21, gestionează dependențele esențiale (Lombok, Spring Data JPA, PostgreSQL, Flyway, RabbitMQ, Stripe SDK, Hibernate Validator, Actuator) și plugin-urile de compilare, testare Surefire și analiză a acoperirii JaCoCo.

### I2. ⚙️ [`Dockerfile`](Dockerfile)
* **Path**: [`Dockerfile`](Dockerfile)
* **Tip Componentă**: `Container Definition (Multi-Stage Build)`
* **Explicație Specifică**: Fișier de containerizare multi-stage optimizat pentru producție pe Java 21. Etapa 1 (builder) utilizează imaginea Eclipse Temurin 21 JDK pentru compilarea JAR-ului cu cache pentru dependențe Maven; Etapa 2 (runtime) folosește o imagine minimală Temurin 21 JRE, rulează ca proces ne-root și include healthcheck prin curl către /actuator/health.

### I3. ⚙️ [`docker-compose.yml`](docker-compose.yml)
* **Path**: [`docker-compose.yml`](docker-compose.yml)
* **Tip Componentă**: `Orchestrare Servicii Locale (Postgres, RabbitMQ, App)`
* **Explicație Specifică**: Orchestrator Docker pentru mediul integrat local. Declară și configurează trei servicii interconectate: baza de date relațională PostgreSQL 16 Alpine (port 5433/5432) cu volume persistente, brokerul de mesagerie RabbitMQ 3 Management (porturi 5672 și 15672) și containerul aplicației Spring Boot (port 8080) cu dependențe condiționate de starea de sănătate (service_healthy).

### I4. ⚙️ [`mvnw`](mvnw)
* **Path**: [`mvnw`](mvnw)
* **Tip Componentă**: `Maven Wrapper Script (POSIX Shell / Linux / macOS)`
* **Explicație Specifică**: Script executabil POSIX Shell (Maven Wrapper) pentru sisteme Unix, Linux și macOS. Garantează compilarea uniformă a proiectului pe orice mașină de dezvoltare sau nod CI/CD prin descărcarea și utilizarea automată a versiunii exacte de Apache Maven declarate în wrapper.

### I5. ⚙️ [`mvnw.cmd`](mvnw.cmd)
* **Path**: [`mvnw.cmd`](mvnw.cmd)
* **Tip Componentă**: `Maven Wrapper Script (Batch / Windows Command Prompt)`
* **Explicație Specifică**: Script Batch (Maven Wrapper) dedicat sistemelor de operare Windows. Asigură execuția aceluiași build reproductibil Maven în Command Prompt sau PowerShell, configurând mediul de execuție Java fără a impune preinstalarea manuală a Maven.

### I6. ⚙️ [`ci-cd.yml`](.github/workflows/ci-cd.yml)
* **Path**: [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml)
* **Tip Componentă**: `GitHub Actions CI/CD Pipeline (Build, Test, ArchUnit, JaCoCo)`
* **Explicație Specifică**: Pipeline enterprise de Integrare și Livrare Continuă (CI/CD) în GitHub Actions. Se declanșează automat la push și pull-request pe ramurile principale (main, develop, feature/**), executând în paralel validarea compilării pe Java 21, suita de teste unitare DoD, verificarea regulilor de arhitectură ArchUnit și raportarea calității prin JaCoCo.

### I7. ⚙️ [`SupplyChain_P2P_Collection.json`](tools/postman/SupplyChain_P2P_Collection.json)
* **Path**: [`tools/postman/SupplyChain_P2P_Collection.json`](tools/postman/SupplyChain_P2P_Collection.json)
* **Tip Componentă**: `Postman API Test Collection (End-to-End P2P Scenarios)`
* **Explicație Specifică**: Colecție oficială Postman de teste de integrare și acceptanță API pentru întreg ciclul Procure-to-Pay (P2P). Include scenarii ordonate pe 5 foldere: Comenzi Aprovizionare (UC1-UC8), Calitate & Furnizori (Q1-Q8), Facturi, NIR & 3-Way Matching (I1-I4), Plăți & Webhook-uri Stripe (I5-I7) și verificarea Actuator Health.

---

# 3. SPECIFICAȚIA STRUCTURALĂ 1:1 PE PACHETE ȘI CLASE DE DOMENIU

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation`
* **Secțiune Arhitecturală**: `Shared Kernel`
* **Rol Funcțional**: Adnotări de arhitectură DDD care definesc stereotipurile semantice pentru componentele din nucleul de domeniu conform ADR-003.

### 1. 📄 [`AggregateRoot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Adnotare marker DDD aplicată rădăcinilor de agregat (Aggregate Root). Garantează că entitatea marcată este singura poartă de acces pentru operațiuni externe și unitatea exclusivă de consistență tranzacțională.

### 2. 📄 [`ApplicationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip DDD/Clean Architecture pentru serviciile din stratul de aplicație. Marchează componentele responsabile de coordonarea cazurilor de utilizare, gestionarea tranzacțiilor și delegarea către agregat.

### 3. 📄 [`ComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Adnotare semantică pentru servicii de calcul matematic aplicat. Semnalează componente dedicate calculelor de business (sumare financiară, rotunjiri fiscale, scoruri statistice) fără mutații de stare.

### 4. 📄 [`DomainComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Stereotip DDD pentru servicii matematice pure de domeniu. Asigură determinism total, absența efectelor secundare și calculul regulilor numerice complexe (ex: algoritmi OTIF, reconciliere 3-Way).

### 5. 📄 [`DomainEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Adnotare marker DDD pentru entitățile componente din interiorul unui agregat. Semnalează că obiectul posedă o identitate locală unică, dar are ciclu de viață dependent de rădăcina sa.

### 6. 📄 [`DomainFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip DDD pentru clasele Factory responsabile de instanțierea agregatelor complexe. Verifică precondițiile de afaceri, generează ID-uri unice și livrează agregate în stare validă.

### 7. 📄 [`DomainPolicy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Adnotare DDD pentru reguli și politici de business decuplate conform Strategy Pattern (ex: praguri de autorizare a plăților, grile AQL de eșantionare statistică).

### 8. 📄 [`DomainRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Adnotare DDD pentru porturile de persistență ale nucleului de domeniu. Reprezintă interfețe pure prin care agregatul comunică cu stocarea, decuplate total de JPA sau baze de date.

### 9. 📄 [`DomainService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip DDD pentru servicii de domeniu (Domain Service). Încapsulează logică operațională pură ce implică mai multe agregate și care nu aparține în mod natural unei singure entități.

### 10. 📄 [`DomainValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Stereotip DDD pentru servicii de validare a conformității de afaceri. Verifică eligibilitatea partenerilor, compatibilitatea certificărilor și integritatea regulilor de domeniu.

### 11. 📄 [`InfrastructureService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip de arhitectură hexagonală pentru componente din stratul de infrastructură (adaptoare externe, gateway-uri de plată Stripe, mesagerie asincronă RabbitMQ, joburi programate).

### 12. 📄 [`QueryService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip CQRS pentru servicii dedicate exclusiv interogării și proiecțiilor de citire (Read Models / Summary Views). Execută operații strict idempotente și fără mutație de stare.

### 13. 📄 [`UtilityService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Stereotip SOA pentru servicii transversale reutilizabile (Cross-Cutting Concerns), cum ar fi auditul securizat în tranzacții izolate sau notificările de sistem.

### 14. 📄 [`ValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Stereotip semantic pentru contractele și implementările serviciilor de validare a invarianților operaționali din fluxurile de business.

### 15. 📄 [`ValueObject.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Adnotare marker DDD pentru obiecte imutabile fără identitate proprie (Value Objects). Egalitatea lor este determinată exclusiv de valoarea atributelor conținute.

### 16. 📄 [`WorkflowService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Adnotare DDD pentru servicii de orchestrare a fluxurilor de lucru (Workflow Services). Coordonează secvența de pași și tranzițiile de stare din procesele inter-agregat.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception`
* **Secțiune Arhitecturală**: `Shared Kernel`
* **Rol Funcțional**: Excepții de domeniu aruncate de constructorii Value Objects și de serviciile de validare când sunt încălcați invarianții de business.

### 17. 📄 [`DomainValidationException.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Tip Componentă DDD**: `Domain Exception (Excepție de Business)`
* **Explicație Specifică**: Excepție de domeniu (unchecked) aruncată la încălcarea oricărui invariant structural sau a unei reguli de business din constructorii Value Objects sau serviciile de validare.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters`
* **Secțiune Arhitecturală**: `Shared Kernel`
* **Rol Funcțional**: Convertoare JPA AttributeConverter pentru serializarea și deserializarea transparentă a identificatorilor și tipurilor Value Object în coloane SQL.

### 18. 📄 [`CodMaterialConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Specifică**: Convertor JPA (AttributeConverter) cu autoApply=true pentru Value Object-ul CodMaterial, serializând obiectul imutabil în coloana VARCHAR(50) a bazei de date.

### 19. 📄 [`CurrencyConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Specifică**: Convertor JPA transparent cu autoApply=true ce mapează instanțele standard Java Currency în codul alfanumeric ISO-4217 de 3 litere (ex: EUR, RON).

### 20. 📄 [`IdComandaConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Specifică**: Convertor JPA cu autoApply=true pentru Value Object-ul IdComanda, transformând identificatorul tipizat în tipul nativ SQL UUID.

### 21. 📄 [`IdFurnizorConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Specifică**: Convertor JPA cu autoApply=true ce transformă Value Object-ul IdFurnizor într-un șir VARCHAR(36) în tabelele bazei de date relaționale.

### 22. 📄 [`IdLotConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Specifică**: Convertor JPA cu autoApply=true pentru identificatorul tipizat IdLot, gestionând persistarea și reconstrucția transparentă din coloane UUID.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared`
* **Secțiune Arhitecturală**: `Shared Kernel`
* **Rol Funcțional**: Value Objects universale (IdComanda, IdFurnizor, IdLot, CodMaterial, Perioada), mecanismul Transactional Outbox / Inbox și interfețele de audit.

### 23. 📄 [`AbstractRepositoryJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Clasă de bază abstractă generică pentru implementările de repository cu EntityManager. Oferă operațiuni CRUD de bază, regăsire după ID și management tranzacțional al sesiunii.

### 24. 📄 [`AuditLogEntry.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Entitate JPA imutabilă pentru pista de audit. Înregistrează utilizatorul, tipul acțiunii, timestamp-ul UTC și datele operațiunii pentru trasabilitate completă.

### 25. 📄 [`CodMaterial.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce validează și încapsulează codul unic al unui articol sau materie primă (ex: MAT-STEEL-001), impunând format alfanumeric non-vid.

### 26. 📄 [`DomainEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Tip Componentă DDD**: `Domain Event (Eveniment de Domeniu)`
* **Explicație Specifică**: Interfață contract de bază pentru toate evenimentele de domeniu emise de agregate. Expune timestamp-ul exact al apariției prin metoda occurredOn().

### 27. 📄 [`IJurnalAudit.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Port de domeniu pentru serviciul de jurnalizare a auditului. Asigură scrierea urmelor operaționale într-o tranzacție independentă (Propagation.REQUIRES_NEW).

### 28. 📄 [`IdComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object imutabil bazat pe UUID ce reprezintă cheia primară unică a unei comenzi de aprovizionare în întregul sistem.

### 29. 📄 [`IdFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object imutabil bazat pe String ce identifică în mod unic un furnizor sau partener comercial.

### 30. 📄 [`IdLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object imutabil bazat pe UUID ce garantează identificarea global unică și trasabilitatea fiecărui lot fizic de marfă.

### 31. 📄 [`InboxEventEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Entitate JPA utilizată în modelul Inbox Pattern. Reține identificatorii unici ai mesajelor procesate din cozi pentru garantarea consumului idempotent.

### 32. 📄 [`InboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de domeniu pentru verificarea existenței și marcarea evenimentelor consumate în cadrul mecanismului de deduplicare a mesajelor.

### 33. 📄 [`InboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Implementare Spring Data JPA a portului InboxRepo, permițând interogarea rapidă a mesajelor deja recepționate după ID-ul evenimentului.

### 34. 📄 [`OutboxEventEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Entitate JPA pentru șablonul Transactional Outbox. Stochează evenimentele de domeniu serializate JSON în aceeași tranzacție de bază de date cu agregatul.

### 35. 📄 [`OutboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de domeniu pentru cutia de ieșire (Outbox). Oferă metode pentru salvarea evenimentelor și interogarea mesajelor neexpediate către RabbitMQ.

### 36. 📄 [`OutboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Implementare Spring Data JPA pentru portul OutboxRepo, asigurând selectarea evenimentelor cu flag-ul procesat=false pentru dispatcher-ul de fundal.

### 37. 📄 [`Perioada.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object temporal ce modelează un interval de date [dataInceput, dataFinal]. Validează invariantul ca data de sfârșit să nu preceadă începutul și oferă verificări de suprapunere.

### 38. 📄 [`StoredDomainEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Entitate JPA ce conține payload-ul serializat JSON al unui eveniment de domeniu, tipul agregatului sursă și timestamp-ul emiterii pentru Outbox.

### 39. 📄 [`package-info.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Descriptor de pachet pentru nucleul comun (Shared Kernel), documentând regulile de izolare, imutabilitate și decuplare ale claselor transversale.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Rădăcina de agregat ComandaAprovizionare, entitatea componentă Lot, Value Objects specifice, enumerări de stare și porturile de persistență.

### 40. 📄 [`Cantitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object numeric strict pozitiv bazat pe BigDecimal la scara 2. Blochează cantitățile negative sau zero și oferă operații sigure de adunare și scădere.

### 41. 📄 [`ComandaAprovizionare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat a comenzilor de aprovizionare. Gestionează stările (NOUA, CONFIRMATA, EXPEDIATA, LIVRATA, RECEPTIONATA, ANULATA), recalculează totalurile monetare, cascadează anularea la loturile fizice și previne modificările concurente prin @Version.

### 42. 📄 [`ComandaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository hexagonal pentru comenzi de aprovizionare. Definește contractul de regăsire după ID, salvare a agregatului și filtrare după furnizor sau stare.

### 43. 📄 [`EstimareLeadTimeService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Serviciu de domeniu ce prognozează data de livrare prin selectarea dinamică a unei strategii statistice (medie mobilă, netezire exponențială, percentilă 90) pe istoricul recepțiilor.

### 44. 📄 [`EstimareLivrare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce încapsulează data estimată de sosire a mărfii și gradul de certitudine sau intervalul de încredere asociat calculului.

### 45. 📄 [`Expediere.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce reține detaliile logistice de transport: compania de curierat, codul de urmărire AWB și condiția comercială internațională Incoterm (ex: FOB, CIF, DDP).

### 46. 📄 [`IGestiuneFurnizoriAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de integrare (SPI) prin care contextul Comenzi verifică eligibilitatea și starea furnizorului din contextul Calitate fără a stabili cuplaj la entitățile interne ale acestuia.

### 47. 📄 [`IMetodaEstimareLeadTime.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Tip Componentă DDD**: `Domain Interface / Business Port`
* **Explicație Specifică**: Interfață Strategy Pattern ce definește contractul matematic comun pentru algoritmii de prognoză a timpului de livrare al furnizorului.

### 48. 📄 [`IstoricLivrariRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru interogarea istoricului livrărilor anterioare ale furnizorului (date promise vs date efective de descărcare).

### 49. 📄 [`IstoricLivrariVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce agregă seria temporală a livrărilor precedente pentru a servi drept date de intrare strategiilor de prognoză a lead time-ului.

### 50. 📄 [`LinieComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce modelează un articol din comandă: produsul (CodMaterial), cantitatea comandată, prețul unitar negociat și subtotalul calculat.

### 51. 📄 [`Lot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Tip Componentă DDD**: `Domain Entity (Entitate Componentă DDD)`
* **Explicație Specifică**: Entitate componentă în agregatul ComandaAprovizionare. Posedă identitate locală IdLot, număr de serie, stare fizică și metode de mutație cu vizibilitate package-private controlate strict de comandă.

### 52. 📄 [`LotRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru regăsirea și trasabilitatea loturilor fizice de marfă asociate comenzilor de aprovizionare.

### 53. 📄 [`MedieMobilaStrategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Tip Componentă DDD**: `Estimation Strategy (Implementare Strategy Pattern)`
* **Explicație Specifică**: Implementare a strategiei de estimare a lead time-ului bazată pe media aritmetică mobilă a ultimelor N recepții de la furnizor.

### 54. 📄 [`MetriciLivrareVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce conține indicatorii sintetici de fiabilitate logistică ai furnizorului: lead time mediu, abatere standard și dispersie.

### 55. 📄 [`NetezireExponentialaStrategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Tip Componentă DDD**: `Estimation Strategy (Implementare Strategy Pattern)`
* **Explicație Specifică**: Implementare a strategiei de prognoză a lead time-ului prin netezire exponențială simplă (Holt-Winters), acordând pondere crescută livrărilor recente prin parametrul alpha.

### 56. 📄 [`Percentila90Strategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Tip Componentă DDD**: `Estimation Strategy (Implementare Strategy Pattern)`
* **Explicație Specifică**: Strategie conservatoare de estimare a termenului de livrare ce selectează percentila 90 a istoricului, acoperind 90% din riscurile de întârziere.

### 57. 📄 [`PretUnitar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object financiar compus din valoare numerică (BigDecimal cu scara 4) și cod de monedă ISO-4217. Garantează precizia calculelor monetare.

### 58. 📄 [`RezultatCalitateLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare de domeniu pentru decizia calitativă acordată lotului recepționat: CONFORM, ACCEPTAT_CONDITIONAT, RESPINS.

### 59. 📄 [`StareComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare ce definește ciclul de viață al comenzii: NOUA, CONFIRMATA, EXPEDIATA, LIVRATA, RECEPTIONATA, ANULATA, conținând metoda poateFiAnulata().

### 60. 📄 [`StareLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru starea operațională a lotului fizic: ALOCAT, IN_TRANZIT, RECEPTIONAT, DISPONIBIL, CARANTINA, ANULAT.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Serviciul de calcul matematic al valorilor comenzilor (total net, discount de volum, taxe) și proiecțiile sumare de citire.

### 61. 📄 [`ComandaComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Serviciu matematic determinist ce calculează valoarea netă a comenzii prin sumarea liniilor, aplică grilele de discount de volum și calculează TVA-ul aplicabil.

### 62. 📄 [`ComandaSummaryView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object de tip proiecție DTO (Entity View / Read Model) ce oferă o vizualizare condensată a comenzii pentru dashboard-uri fără a expune agregatul complet.

### 63. 📄 [`IComandaComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul de calcul financiar și tarifar al comenzilor de aprovizionare.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Evenimente de domeniu emise la mutațiile de stare ale comenzilor (anulare, recepție loturi, avertizare termen depășit).

### 64. 📄 [`ComandaAnulataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de domeniu emis la anularea comenzii. Conține identificatorul comenzii, motivul anulării și lista loturilor fizice afectate.

### 65. 📄 [`ComandaNeconfirmataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de avertizare emis atunci când furnizorul nu confirmă comanda în termenul contractual stabilit.

### 66. 📄 [`ComandaReceptionataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de domeniu declanșat la descărcarea loturilor în depozit, notificând contextul Calitate pentru declanșarea inspecției la recepție.

### 67. 📄 [`EstimareDepasesteCerereaEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de alertă operațională emis când termenul estimat de livrare depășește data limită cerută de planul de producție.

### 68. 📄 [`VerdictNeaplicabilEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment excepțional de domeniu emis când se încearcă atașarea unui verdict de calitate pe un lot aflat într-o stare fizică nepermisă.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Fabrica agregatului ComandaAprovizionare, responsabilă de asamblarea inițială și generarea identificatorilor unici.

### 69. 📄 [`ComandaFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Tip Componentă DDD**: `Aggregate Factory (Fabrică DDD)`
* **Explicație Specifică**: Fabrica agregatului ComandaAprovizionare. Validează furnizorul activ, instanțiază agregatul, asamblează liniile și generează loturile inițiale.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Politici de domeniu pe praguri valorice pentru autorizarea comenzilor de aprovizionare.

### 70. 📄 [`PoliticaAprobareComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Tip Componentă DDD**: `Domain Enumeration (Enumerare de Domeniu)`
* **Explicație Specifică**: Politică de business ce stabilește treptele de autorizare financiară: aprobare automată sub 10.000 EUR, aprobare Manager Achiziții până la 50.000 EUR, aprobare CFO peste 50.000 EUR.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Servicii de validare a conformității comenzilor (stare furnizor, unicitate linii, prezență date).

### 71. 📄 [`ComandaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Serviciu de validare ce verifică starea activă a furnizorului, unicitatea materialelor pe linii și prezența datelor de livrare obligatorii.

### 72. 📄 [`IComandaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul de validare a comenzilor de aprovizionare.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration`
* **Secțiune Arhitecturală**: `Bounded Context: Comenzi`
* **Rol Funcțional**: Configurație Spring IoC (@Configuration, @Bean) ce expune serviciile de domeniu pure fără a le polua cu adnotări de framework.

### 73. 📄 [`ComenziDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration, @Bean)`
* **Explicație Specifică**: Clasă Spring @Configuration ce instanțiază și expune ca @Bean componentele pure ale contextului Comenzi (fabrica, politica, serviciile de calcul și validare) fără dependențe de framework în clasele de domeniu.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Agregatele Furnizor, InspectieCalitate, InspectorCalitate, RaportNeconformitate, Value Objects și istoricul evaluărilor lunare.

### 74. 📄 [`ActiuneCorectiva.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce descrie măsura corectivă impusă furnizorului într-un raport NCR: descriere detaliată, responsabil desemnat și termen limită de remediere.

### 75. 📄 [`Adresa.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object imutabil pentru adresa poștală a partenerilor comerciali: stradă, oraș, județ, cod poștal și țară.

### 76. 📄 [`AlocareInspectorService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Serviciu de domeniu ce identifică inspectorul optim pentru un lot pe baza certificărilor pe grupa de materiale și a intervalelor libere din calendar.

### 77. 📄 [`CalculScorecardService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Serviciu de domeniu ce calculează formula lunară a performanței furnizorului: 60% pondere indicatorul OTIF + 40% rata de conformitate a loturilor.

### 78. 📄 [`Calificativ.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare de performanță acordată furnizorului pe baza scorului lunar: CLASA_A (excelent), CLASA_B (conform), CLASA_C (risc), SUSPENDAT.

### 79. 📄 [`Certificare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce atestă calificarea tehnică a unui inspector de calitate: cod calificare, standard ISO acoperit și data de expirare.

### 80. 📄 [`ConditiiContractuale.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce grupează termenul de plată negociat în zile, clauza comercială Incoterm și durata perioadei de garanție.

### 81. 📄 [`Email.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce validează și stochează o adresă electronică de contact conform specificațiilor RFC-5322.

### 82. 📄 [`FullName.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce încapsulează numele complet (prenume și nume de familie), impunând prezența ambelor componente nominale.

### 83. 📄 [`Furnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat pentru partenerii comerciali furnizori. Gestionează starea de calificare (PROSPECT, ACTIV, SUSPENDAT, BLOCAT), datele de contact și istoricul scorecard-urilor lunare.

### 84. 📄 [`FurnizorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru salvarea, regăsirea și filtrarea partenerilor furnizori după stare și cod fiscal.

### 85. 📄 [`IPerformantaLivrariAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port secundar prin care modulul Calitate solicită istoricul recepțiilor din modulul Comenzi pentru a calcula rata comenzilor livrate la timp (OTIF).

### 86. 📄 [`Iban.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object bancar ce validează integritatea structurală și suma de control a codului IBAN prin algoritmul ISO 7064 Modulo 97.

### 87. 📄 [`InspectieCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat a inspecției de laborator. Conține referința lotului, planul AQL aplicat, lista defectelor consemnate și verdictul oficial emis.

### 88. 📄 [`InspectieRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru evidența, planificarea și regăsirea dosarelor de inspecție calitativă.

### 89. 📄 [`InspectorCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat ce modelează inspectorul de calitate. Deține lista certificărilor profesionale și colecția intervalelor calendaristice de disponibilitate.

### 90. 📄 [`InspectorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru identificarea inspectorilor atestați pe anumite categorii de materiale și disponibili la o anumită dată.

### 91. 📄 [`NCRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru evidența rapoartelor de neconformitate (Non-Conformance Reports).

### 92. 📄 [`PerformantaLivrariVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce sintetizează datele de expediție ale furnizorului: total comenzi livrate, comenzi on-time, comenzi in-full și procentajul agregat OTIF.

### 93. 📄 [`PlanAQL.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce definește parametrii statistici conform standardului ISO 2859-1: mărimea eșantionului de inspectat, numărul de acceptare (Ac) și numărul de respingere (Re).

### 94. 📄 [`PretNegociat.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce definește prețul contractual agreat pentru un articol pe o perioadă de valabilitate determinată.

### 95. 📄 [`RaportNeconformitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat NCR. Consemnează defectele constatate la recepție, investigația cauzei rădăcină (Root Cause), acțiunile corective solicitate și starea de soluționare.

### 96. 📄 [`Scor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object numeric zecimal scalat între 0.00 și 100.00, utilizat în cuantificarea indicatorilor de calitate și a scorecard-ului.

### 97. 📄 [`ScorecardLunar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Tip Componentă DDD**: `Domain Entity (Entitate Componentă DDD)`
* **Explicație Specifică**: Entitate componentă a agregatului Furnizor ce păstrează istoricul evaluării lunare: an, lună, OTIF calculat, rata conformității loturilor și scorul ponderat final.

### 98. 📄 [`StareFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru statutul juridico-operațional al furnizorului: PROSPECT, ACTIV, SUSPENDAT, BLOCAT.

### 99. 📄 [`StareInspectie.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru ciclul de execuție al inspecției calitative: PROGRAMATA, IN_CURS, FINALIZATA, ANULATA.

### 100. 📄 [`StareNCR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru stadiile raportului NCR: DESCHIS, ANALIZA_CAUZA, ACTIUNE_DEFINITA, IN_VERIFICARE, INCHIS, ESCALADAT.

### 101. 📄 [`Verdict.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru decizia calitativă finală asupra lotului: CONFORM, ACCEPTAT_CONDITIONAT, RESPINS.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Contracte de calcul matematic pentru formula scorecard-ului ponderat și proiecțiile de citire tip dashboard.

### 102. 📄 [`CalitateDashboardView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object de proiecție (Read Model / DTO) ce oferă o vedere de sinteză asupra calificativelor furnizorilor și a inspecțiilor în așteptare.

### 103. 📄 [`ICalculScorecardService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul matematic de evaluare a scorecard-ului ponderat al furnizorilor.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Evenimente de domeniu privind degradarea calificativului, blocarea furnizorilor, alocarea inspecțiilor și rapoartele NCR.

### 104. 📄 [`CertificareInspectorExpiraEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de notificare emis când o calificare sau autorizație profesională a unui inspector se apropie de termenul de expirare.

### 105. 📄 [`FurnizorBlocatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment critic de domeniu emis la blocarea unui furnizor din cauza rapoartelor NCR nerezolvate sau a scorului sub pragul minim admis.

### 106. 📄 [`FurnizorSuspendatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de domeniu emis la degradarea calificativului furnizorului la CLASA_C sau suspendarea temporară a colaborării.

### 107. 📄 [`InspectieProgramataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment emis la crearea dosarului de inspecție și alocarea inspectorului de calitate pe lotul descărcat.

### 108. 📄 [`LotScutitDeInspectieEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment emis când marfa recepționată provine de la un partener din CLASA_A certificat și este scutită de testarea de laborator.

### 109. 📄 [`NCREscaladatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment emis la depășirea termenului limită al acțiunilor corective din raportul de neconformitate, escaladând cazul către management.

### 110. 📄 [`VerdictInspectieEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de domeniu ce comunică verdictul acordat lotului (CONFORM/RESPINS) către gestiunea stocurilor și modulul de facturare.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Fabrici DDD pentru instanțierea consecventă a agregatelor Furnizor și InspectieCalitate pe baza tabelelor AQL.

### 111. 📄 [`FurnizorFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Tip Componentă DDD**: `Aggregate Factory (Fabrică DDD)`
* **Explicație Specifică**: Fabrica agregatului Furnizor. Verifică unicitatea codului fiscal CUI, validează formatul contului IBAN și instanțiază furnizorul în stare inițială PROSPECT.

### 112. 📄 [`InspectieCalitateFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Tip Componentă DDD**: `Aggregate Factory (Fabrică DDD)`
* **Explicație Specifică**: Fabrica inspecțiilor de calitate. Asociază lotul recepționat cu eșantionul statistic determinat de politica AQL pe baza mărimii lotului.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Politica de afaceri conformă standardului ISO 2859-1 pentru selectarea planului statistic de eșantionare AQL.

### 113. 📄 [`PoliticaSelectieAQL.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Tip Componentă DDD**: `Domain Enumeration (Enumerare de Domeniu)`
* **Explicație Specifică**: Politică de afaceri conform standardului ISO 2859-1 (Nivel General II de Inspecție) ce extrage mărimea eșantionului și criteriile Ac/Re în funcție de cantitatea lotului.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Servicii de validare a atestatelor tehnice ale inspectorilor și a disponibilității calendaristice a acestora.

### 114. 📄 [`CalitateValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Serviciu de validare ce verifică competența tehnică a inspectorului alocat pe grupa de materiale și lipsa suprapunerilor de concediu.

### 115. 📄 [`ICalitateValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul de validare a conformității operațiunilor de asigurare a calității.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration`
* **Secțiune Arhitecturală**: `Bounded Context: Calitate`
* **Rol Funcțional**: Configurație Spring IoC pentru instanțierea ca @Bean a fabricilor, politicilor și serviciilor modulului Calitate.

### 116. 📄 [`CalitateDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration, @Bean)`
* **Explicație Specifică**: Clasă Spring @Configuration ce instanțiază ca @Bean fabricile, politicile și serviciile de calcul și validare ale modulului Calitate.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Agregatele FacturaFurnizor, NIR, OrdinPlata, serviciul de reconciliere 3-Way Matching și porturile SPI.

### 117. 📄 [`ComandaSummaryVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce furnizează modulului Facturi datele esențiale ale comenzii (cantitate aprobată, preț unitar) necesare procedurii 3-Way Matching.

### 118. 📄 [`FacturaFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat a facturilor primite. Gestionează liniile facturate, totalul net, TVA și brut, starea reconcilierii și emite propuneri de plată la aprobare.

### 119. 📄 [`FacturaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository hexagonal pentru salvarea, regăsirea și filtrarea facturilor de la furnizori după stare și număr fiscal.

### 120. 📄 [`FurnizoriBlocatiRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de securitate și verificare ce oferă acces la lista neagră a furnizorilor suspendați sau blocați, consultat înaintea autorizării plăților.

### 121. 📄 [`IBancaPlataProvider.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port secundar (SPI) prin care modulul Facturi transmite instrucțiunile de debitare către gateway-ul de plăți (ex: procesatorul extern Stripe).

### 122. 📄 [`IComenziAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de integrare prin care contextul Facturi interoghează detaliile comenzii pentru 3-Way Matching fără cuplaj direct la agregatul ComandaAprovizionare.

### 123. 📄 [`LinieFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce reflectă un articol facturat: produsul (CodMaterial), cantitatea facturată, prețul unitar, cota TVA și subtotalul.

### 124. 📄 [`LinieNIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce consemnează articolul și cantitatea fizică efectiv descărcată în depozit conform Notei de Intrare Recepție.

### 125. 📄 [`LinieSumar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object de raportare contabilă ce grupează valorile facturate pe categorii de cote de TVA.

### 126. 📄 [`NIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat a Notei de Intrare Recepție (NIR). Consemnează faptic cantitățile descărcate în depozit, servind ca reper cantitativ în 3-Way Matching.

### 127. 📄 [`NIRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru persistența și căutarea documentelor de recepție NIR după ID sau număr de comandă.

### 128. 📄 [`OrdinPlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Specifică**: Rădăcina de agregat ce mandatează transferul fondurilor bancare. Gestionează ciclul de transfer (DRAFT, APROBAT, IN_PROCESARE, EXECUTAT, ESUAT) și referința externă de plată.

### 129. 📄 [`OrdinPlataRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port / Outbound SPI (Port Hexagonal)`
* **Explicație Specifică**: Port de repository pentru evidența și stocarea ordinelor de plată emise.

### 130. 📄 [`ReconciliereService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Specifică**: Serviciu de domeniu ce orchestrează reconcilierea trilaterală (3-Way Matching): compară Comanda, NIR-ul și Factura pe baza marjelor de toleranță admise.

### 131. 📄 [`RezultatReconciliere.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce sintetizează rezultatul 3-Way Matching: statutul potrivirii, diferențele detectate de preț/cantitate și marja de toleranță aplicată.

### 132. 📄 [`StareFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru ciclul de viață al facturii: INREGISTRATA, IN_RECONCILIERE, RECONCILIATA, DISCREPANTA_DETECTATA, APROBATA_PENTRU_PLATA, PLATITA, CONTESTATA, ANULATA.

### 133. 📄 [`StareNIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru starea Notei de Recepție: EMIS, CONFIRMAT, CONTESTAT.

### 134. 📄 [`StareOrdinPlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Enumerare pentru ciclul de transfer al plății: DRAFT, PROPUS, APROBAT, IN_PROCESARE, EXECUTAT, ESUAT, ANULAT.

### 135. 📄 [`Suma.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object monetar imutabil compus din sumă zecimală (BigDecimal la scara 2) și cod monedă ISO-4217. Previne adunarea valorilor în valute eterogene.

### 136. 📄 [`Toleranta.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object ce definește pragul de deviație admis la 3-Way Matching (ex: variație procentuală de ±1% la cantitate și ±5 EUR la diferențe de rotunjire TVA).

### 137. 📄 [`ValoareFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object financiar compus ce structurează transparent totalul net, valoarea totală a TVA-ului și totalul brut de plată al facturii.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Serviciul determinist de calcul al discrepanțelor matematice din 3-Way Matching și proiecțiile de plăți.

### 138. 📄 [`IReconciliereComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul matematic de calcul al discrepanțelor din procedura 3-Way Matching.

### 139. 📄 [`PlatiSummaryView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Value Object de proiecție (Read Model / DTO) ce oferă sinteza facturilor aprobate și a ordinelor de plată executate.

### 140. 📄 [`ReconciliereComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Serviciu matematic determinist ce calculează variațiile absolute și procentuale între cantitățile din NIR, prețurile din comandă și totalurile din factură.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Evenimente de domeniu emise la executarea cu succes a transferului bancar și la apropierea scadenței facturilor.

### 141. 📄 [`PlataExecutataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de domeniu emis în urma confirmării debitării plății prin Stripe sau transfer bancar, actualizând factura ca PLATITA.

### 142. 📄 [`ScadentaApropiataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil)`
* **Explicație Specifică**: Eveniment de avertizare financiară emis când o factură aprobată se apropie de data scadenței contractuale.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Fabrici DDD pentru construirea agregatelor FacturaFurnizor și NIR din documentele fizice de recepție.

### 143. 📄 [`FacturaFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Tip Componentă DDD**: `Aggregate Factory (Fabrică DDD)`
* **Explicație Specifică**: Fabrica agregatului FacturaFurnizor. Verifică datele de identificare fiscală, calculează totalul liniilor și instanțiază factura în stare INREGISTRATA.

### 144. 📄 [`NIRFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Tip Componentă DDD**: `Aggregate Factory (Fabrică DDD)`
* **Explicație Specifică**: Fabrica Notei de Recepție. Asamblează agregatul NIR pe baza loturilor fizice descărcate și a procesului-verbal de primire din depozit.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Politică de afaceri ce impune reconcilierea trilaterală completă și verificarea listei negre înaintea autorizării plăților.

### 145. 📄 [`PoliticaAutorizarePlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Tip Componentă DDD**: `Domain Policy (Politică de Afaceri)`
* **Explicație Specifică**: Politică de afaceri ce autorizează emiterea ordinului de plată doar dacă 3-Way Matching este valid și furnizorul nu figurează pe lista neagră.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Servicii de validare a conformității fiscale a facturilor și a cotelor de TVA aplicate.

### 146. 📄 [`FacturaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Serviciu de validare ce verifică conformitatea datelor fiscale ale facturii, validitatea CUI-ului emitentului și corectitudinea cotelor TVA aplicate.

### 147. 📄 [`IFacturaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Contract de domeniu pentru serviciul de validare fiscală a facturilor de achiziție.

## 📁 Pachet: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration`
* **Secțiune Arhitecturală**: `Bounded Context: Facturi`
* **Rol Funcțional**: Configurație Spring IoC ce leagă prin @Bean componentele de domeniu ale facturilor și reconcilierii.

### 148. 📄 [`FacturiDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Path**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java`](src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration, @Bean)`
* **Explicație Specifică**: Clasă Spring @Configuration ce expune ca @Bean componentele pure ale contextului Facturi (reconciliere, fabrici, politici și validare).

## 📁 Pachet: `src/test/java/ro/uaic/feaa/sdbis/supplychain/unit`
* **Secțiune Arhitecturală**: `Teste Unitare DoD`
* **Rol Funcțional**: Suita completă de 6 clase de teste unitare JUnit 5 ce verifică regulile și serviciile conform criteriilor Definition of Done.

### 149. 📄 [`ComputationRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 ce verifică precizia matematică și imutabilitatea operațiilor pe Value Objects (rotunjire HALF_UP, operații cu sume monetare fără erori zecimale).

### 150. 📄 [`ComputationServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 pentru serviciile matematice deterministe: formula ponderată a scorecard-ului lunar OTIF și calculul discrepanțelor din 3-Way Matching.

### 151. 📄 [`ValidationRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 ce validează auto-protecția constructorilor de Value Objects (aruncarea de DomainValidationException la parametri nuli, negativi sau formate greșite).

### 152. 📄 [`ValidationServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Regulilor)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 ce testează validările inter-agregate: verificarea stării active a furnizorilor și a certificărilor valide ale inspectorilor.

### 153. 📄 [`WorkflowRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 ce verifică diagrama tranzițiilor de stare și invarianții din agregatele ComandaAprovizionare, Furnizor și FacturaFurnizor.

### 154. 📄 [`WorkflowServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Path**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java`](src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Specifică**: Suită de teste unitare JUnit 5 ce testează orchestrarea fluxurilor end-to-end: anularea în cascadă a loturilor la anularea comenzii, alocarea inspecțiilor și reconcilierea trilaterală.

---

# 4. RECAPITULARE STATISTICĂ BLUEPRINT SPRINT 2

* **Componente de Infrastructură, Build & Tooling**: 7 fișiere (`pom.xml`, `Dockerfile`, `docker-compose.yml`, `mvnw`, `mvnw.cmd`, `.github/workflows/ci-cd.yml`, `tools/postman/SupplyChain_P2P_Collection.json`).
* **Pachete de Domeniu & Testare Indexate**: 26 directoare fizice aliniate 1:1.
* **Clase Java Pure de Domeniu & Teste Unitare**: 154 fișiere Java specifice Sprintului 2.
* **Total General Componente Mapate**: 161 componente complet documentate.
* **Conformitate**: 100% aliniat cu structura proiectului, fără dependențe de căi absolute locale de mașină, cu linkuri relative portabile.