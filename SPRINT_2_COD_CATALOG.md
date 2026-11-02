# BLUEPRINT ARHITECTURAL & CATALOGUL DE DOMENIU – SPRINT 2
## Structura Fizică 1:1 a Proiectului (Rich Domain Model – ADR-003)

---

### METADATE BLUEPRINT
* **Pachet Rădăcină**: `ro.uaic.feaa.sdbis.supplychain`
* **Scope Exclusiv**: **Sprintul 2** (Nucleul de Domeniu Bogat, Value Objects, Agregate, Fabrici, Servicii de Calcul/Validare/Workflow, Politici, Porturi Repository, Configurații Spring Domain și Teste Unitare DoD).
* **Format**: **Fără blocuri de cod sursă brut**, oferind în schimb o specificație conceptuală sugestivă, rolul în afacere și invarianții protejați pentru fiecare fișier în parte.
* **Navigare Directă**: Fiecare fișier conține calea sa relativă exactă în proiect și un link clickabil local direct către fișierul fizic de pe disc care deschide automat codul în editor.

---

# 1. ARBORELE DE NAVIGARE 1:1 AL PROIECTULUI (SPRINT 2 SCOPE)

Următorul arbore reflectă **strict și 1:1** ierarhia reală de fișiere și directoare din proiect ce țin exclusiv de Sprintul 2:

```text
c:/Users/bmugu/Desktop/app
├── src
│   ├── main
│   │   └── java/ro/uaic/feaa/sdbis/supplychain
│   │       ├── shared
│   │       │   ├── annotation (16 fișiere)
│   │       │   ├── exception (1 fișier)
│   │       │   ├── persistence/converters (5 fișiere)
│   │       │   └── [Value Objects & Outbox] (17 fișiere)
│   │       ├── comenzi
│   │       │   ├── domain
│   │       │   │   ├── computation (3 fișiere)
│   │       │   │   ├── event (5 fișiere)
│   │       │   │   ├── factory (1 fișier)
│   │       │   │   ├── policy (1 fișier)
│   │       │   │   ├── validation (2 fișiere)
│   │       │   │   └── [Agregate & VOs] (21 fișiere)
│   │       │   └── infrastructure/configuration (1 fișier)
│   │       ├── calitate
│   │       │   ├── domain
│   │       │   │   ├── computation (2 fișiere)
│   │       │   │   ├── event (7 fișiere)
│   │       │   │   ├── factory (2 fișiere)
│   │       │   │   ├── policy (1 fișier)
│   │       │   │   ├── validation (2 fișiere)
│   │       │   │   └── [Agregate & VOs] (28 fișiere)
│   │       │   └── infrastructure/configuration (1 fișier)
│   │       └── facturi
│   │           ├── domain
│   │           │   ├── computation (3 fișiere)
│   │           │   ├── event (2 fișiere)
│   │           │   ├── factory (2 fișiere)
│   │           │   ├── policy (1 fișier)
│   │           │   ├── validation (2 fișiere)
│   │           │   └── [Agregate & VOs] (21 fișiere)
│   │           └── infrastructure/configuration (1 fișier)
│   └── test
│       └── java/ro/uaic/feaa/sdbis/supplychain
│           └── unit (6 fișiere)
```

---

# 2. SPECIFICAȚIA STRUCTURALĂ 1:1 PE DIRECTOARE ȘI FIȘIERE

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Adnotări de arhitectură DDD care definesc rolul semantic și stereotipul fiecărei componente din nucleul de domeniu conform ADR-003.

### 1. 📄 [`AggregateRoot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare marker DDD plasată pe rădăcina fiecărui agregat. Garantează că doar clasa adnotată este poarta de acces din exterior și unitatea de bază a tranzacțiilor.

### 2. 📄 [`ApplicationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip DDD pentru un serviciu din Application Layer (Application Service / Use-Case Service). Responsabil de coordonarea fluxurilor de utilizare (Use Cases), managementul tranzacționalității, orchestrarea agregatelor, colaborarea cu repository-urile de domeniu și publicarea evenimentelor de domeniu către Transactional Outbox. Nu conține reguli de business (care aparțin agregatelor și serviciilor de domeniu).

### 3. 📄 [`ComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip semantic pentru servicii de calcul (Computation Service conform cerințelor din Sprint 2 / SOA). Marchează contractele și implementările serviciilor de calcul numeric și indicatori de business.

### 4. 📄 [`DomainComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD pentru servicii matematice pure, deterministe și fără efecte secundare (calcule monetare, formule OTIF, 3-Way Matching).

### 5. 📄 [`DomainEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare marker DDD aplicată entităților componente ce posedă identitate locală și ciclu de viață subordonat rădăcinii de agregat.

### 6. 📄 [`DomainFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD ce marchează fabricile responsabile de asamblarea inițială și verificarea precondițiilor de creare ale agregatelor complexe.

### 7. 📄 [`DomainPolicy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD pentru politici de afaceri decuplate conform Strategy Pattern (praguri de aprobare, selecție AQL, etc.).

### 8. 📄 [`DomainRepository.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD pentru porturile de persistență: interfețe pure prin care nucleul de domeniu solicită salvarea sau regăsirea agregatelor fără a depinde de baze de date.

### 9. 📄 [`DomainService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip DDD pentru un serviciu de domeniu (Domain Service / Task Service). Marchează componentele din Domain Layer care încapsulează logică de business pură, reguli operaționale sau algoritmi ce implică multiple agregate și nu aparțin în mod natural unei singure entități sau rădăcini de agregat. Rămâne complet decuplat de Spring sau alte framework-uri de infrastructură (ADR-011).

### 10. 📄 [`DomainValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD pentru servicii specializate în verificarea regulilor de conformitate și a eligibilității operațiunilor economice.

### 11. 📄 [`InfrastructureService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip Clean Architecture pentru servicii din Infrastructure Layer (Infrastructure Service). Marchează componentele tehnice, adaptoarele către sisteme externe (REST, Stripe), procesele de fundal (Schedulers) și mecanismele de mesagerie asincronă (Outbox/Inbox Dispatchers).

### 12. 📄 [`QueryService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip CQRS pentru servicii dedicate interogărilor și proiecțiilor (Query Service / Entity View Computation). Responsabil de extragerea și asamblarea modelelor de citire optimizate (Read Models, Summary Views, Dashboard Views), fără a modifica starea agregatelor (operațiuni read-only, idempotente).

### 13. 📄 [`UtilityService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip SOA pentru servicii utilitare transversale (Utility Service / Cross-Cutting Service). Marchează componentele responsabile de funcționalități transversale reutilizabile în cadrul soluției: audit operațional imutabil (JurnalAuditService), servicii de notificare (NotificariDispatcherService) etc.

### 14. 📄 [`ValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Stereotip semantic pentru servicii de validare (Validation Service conform cerințelor din Sprint 2 / SOA). Marchează contractele și implementările serviciilor de validare a regulilor de business.

### 15. 📄 [`ValueObject.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare marker DDD ce semnalează obiecte imutabile fără identitate proprie, definite exclusiv prin valorile atributelor lor.

### 16. 📄 [`WorkflowService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Adnotare DDD pentru serviciile de aplicație ce orchestrează pașii consecutivi ai unui proces de afaceri și tranzacțiile.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Excepții de domeniu aruncate de constructorii Value Objects și de serviciile de validare când sunt încălcați invarianții de business.

### 17. 📄 [`DomainValidationException.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Tip Componentă DDD**: `Domain Exception (Excepție de Business)`
* **Explicație Sugestivă & Rol de Business**: Excepție de bază aruncată când o regulă structurală sau un invariant de domeniu este încălcat.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Convertoare JPA AttributeConverter pentru serializarea și deserializarea transparentă a identificatorilor și tipurilor Value Object în coloane SQL.

### 18. 📄 [`CodMaterialConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`CodMaterialConverter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 19. 📄 [`CurrencyConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`CurrencyConverter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 20. 📄 [`IdComandaConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IdComandaConverter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 21. 📄 [`IdFurnizorConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IdFurnizorConverter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 22. 📄 [`IdLotConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Tip Componentă DDD**: `JPA Attribute Converter (Mapare Transparentă VO)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IdLotConverter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Value Objects universale (IdComanda, IdFurnizor, IdLot, CodMaterial, Perioada), mecanismul Transactional Outbox / Inbox și interfețele de audit.

### 23. 📄 [`AbstractRepositoryJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Biblioteca tehnica comuna (Infrastructure) - Figura 10 din Project_OOAD.docx. Contine accesul la EntityManager pentru implementarea operatiilor CRUD de baza.

### 24. 📄 [`AuditLogEntry.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Entitate JPA imutabilă de auditare: reține utilizatorul, acțiunea, timestamp-ul UTC și datele operațiunii pentru trasabilitate completă.

### 25. 📄 [`CodMaterial.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce validează și încapsulează codul alfanumeric standardizat al unui produs sau materie primă (ex: MAT-STEEL-001).

### 26. 📄 [`DomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Interfață de bază pentru toate evenimentele de domeniu emise la modificările de stare din agregate, reținând timestamp-ul producerii.

### 27. 📄 [`IJurnalAudit.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port de domeniu pentru serviciul de jurnalizare a urmelor de audit în tranzacție independentă (REQUIRES_NEW).

### 28. 📄 [`IdComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object imutabil ce încapsulează cheia primară universal unică (UUID) a unei comenzi de aprovizionare.

### 29. 📄 [`IdFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object imutabil ce încapsulează identificatorul unic de partener comercial al furnizorului.

### 30. 📄 [`IdLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object imutabil ce identifică în mod unic un lot fizic de marfă asociat unei comenzi și supus inspecției calitative.

### 31. 📄 [`InboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Entitate JPA pentru stocarea cheilor mesajelor recepționate din cozi în cadrul șablonului Inbox.

### 32. 📄 [`InboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru evidența mesajelor consumate, prevenind procesarea duplicatelor prin mecanismul de Idempotent Consumer.

### 33. 📄 [`InboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`InboxRepoJPA.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 34. 📄 [`OutboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Entitate JPA pentru stocarea evenimentelor de expediat către brokerul de mesagerie RabbitMQ.

### 35. 📄 [`OutboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru cutia de ieșire (Transactional Outbox), garantând salvarea atomică a evenimentelor odată cu modificările de domeniu.

### 36. 📄 [`OutboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`OutboxRepoJPA.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 37. 📄 [`Perioada.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object temporal ce modelează un interval calendaristic [dataInceput, dataFinalizare]. Protejează invariantul ca data de început să fie anterioară sau egală cu finalul și oferă metode de verificare a suprapunerii sau incluziunii temporale.

### 38. 📄 [`StoredDomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Entitate de persistență ce reține evenimentele de domeniu serializate pentru modelul Transactional Outbox.

### 39. 📄 [`package-info.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Tip Componentă DDD**: `Package Descriptor & Module Boundary (Spring Modulith)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`package-info.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Rădăcina de agregat ComandaAprovizionare, entitatea componentă Lot, Value Objects, enumerări de stare și porturile de persistență.

### 40. 📄 [`Cantitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object numeric strict pozitiv (BigDecimal cu scara 2). Asigură că nicio operațiune nu poate introduce cantități nule sau negative.

### 41. 📄 [`ComandaAprovizionare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat a comenzilor de aprovizionare. Gestionează stările comenzii (NOUA, CONFIRMATA, EXPEDIATA, LIVRATA, RECEPTIONATA, ANULATA). Încapsulează colecția de linii de comandă și lista de loturi fizice. Protejează invarianții: recalculează automat totalurile monetare, blochează modificările neautorizate și declanșează anularea în cascadă a loturilor la anularea comenzii. Conține câmpul de versiune @Version pentru blocare optimistă.

### 42. 📄 [`ComandaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository hexagonal pentru salvarea, căutarea după ID și interogarea comenzilor de aprovizionare pe baza criteriilor de afaceri.

### 43. 📄 [`EstimareLeadTimeService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Serviciu de domeniu ce combină timpul de producție asumat de furnizor cu timpul de tranzit determinat de clauza Incoterm pentru a estima data sosirii.

### 44. 📄 [`EstimareLivrare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce încapsulează data estimată de livrare calculată de EstimareLeadTimeService pe baza termenului de fabricație și a duratei de transport.

### 45. 📄 [`Expediere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce reține detaliile de expediție logistică: transportator, număr de urmărire AWB și condiția comercială internațională Incoterm.

### 46. 📄 [`IGestiuneFurnizoriAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port din Domain Layer pentru verificarea eligibilitatii furnizorului (Figura 11).

### 47. 📄 [`IMetodaEstimareLeadTime.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IMetodaEstimareLeadTime.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 48. 📄 [`IstoricLivrariRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru consultarea istoricului de recepție în vederea evaluării fiabilității furnizorilor.

### 49. 📄 [`IstoricLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IstoricLivrariVO.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 50. 📄 [`LinieComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce modelează un rând individual pe comandă: produsul (CodMaterial), cantitatea comandată, prețul unitar negociat și valoarea totală calculată.

### 51. 📄 [`Lot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Tip Componentă DDD**: `Component Entity (Entitate Componentă DDD)`
* **Explicație Sugestivă & Rol de Business**: Entitate componentă a agregatului ComandaAprovizionare. Are identitate locală (IdLot, cod fizic de lot) și metode de mutație exclusiv package-private (~). Își actualizează starea operațională (DISPONIBIL, CARANTINA) pe baza verdictului primit în urma inspecției calitative.

### 52. 📄 [`LotRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru căutarea rapidă a loturilor după identificator sau cod de trasabilitate.

### 53. 📄 [`MedieMobilaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`MedieMobilaStrategy.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 54. 📄 [`MetriciLivrareVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`MetriciLivrareVO.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 55. 📄 [`NetezireExponentialaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`NetezireExponentialaStrategy.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 56. 📄 [`Percentila90Strategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`Percentila90Strategy.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 57. 📄 [`PretUnitar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object financiar compus din valoare numerică (BigDecimal, HALF_UP) și monedă (ISO-4217, ex: EUR, RON). Previne utilizarea tipurilor primitive aproximative.

### 58. 📄 [`RezultatCalitateLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de domeniu pentru verdictul calitativ acordat lotului: CONFORM, ACCEPTAT_CONDITIONAT, RESPINS.

### 59. 📄 [`StareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de domeniu pentru ciclul de viață al comenzii. Conține logica de tranziție de stare și metoda poateFiAnulata() (permisă doar din stările NOUA sau CONFIRMATA).

### 60. 📄 [`StareLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de domeniu ce reflectă disponibilitatea fizică a lotului: ALOCAT, IN_TRANZIT, RECEPTIONAT, DISPONIBIL, CARANTINA, ANULAT.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Serviciul de calcul matematic al valorilor comenzilor (total net, discount de volum, taxe) și proiecțiile sumare.

### 61. 📄 [`ComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Serviciu matematic determinist ce calculează totalul net prin sumare de linii, aplică grila de discount de volum și adaugă taxele fiscale aplicabile.

### 62. 📄 [`ComandaSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object de tip proiecție DTO (Entity View) ce oferă o vizualizare condensată a valorilor comenzii pentru dashboard-uri.

### 63. 📄 [`IComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Interfața serviciului de calcul financiar pentru comenzi.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la crearea, confirmarea sau anularea în cascadă a comenzilor de aprovizionare.

### 64. 📄 [`ComandaAnulataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ComandaAnulataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 65. 📄 [`ComandaNeconfirmataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ComandaNeconfirmataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 66. 📄 [`ComandaReceptionataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ComandaReceptionataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 67. 📄 [`EstimareDepasesteCerereaEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`EstimareDepasesteCerereaEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 68. 📄 [`VerdictNeaplicabilEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`VerdictNeaplicabilEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Fabrica agregatului ComandaAprovizionare ce validează precondițiile și asamblează instanța inițială.

### 69. 📄 [`ComandaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Tip Componentă DDD**: `Domain Factory (Fabrică de Agregate DDD)`
* **Explicație Sugestivă & Rol de Business**: Fabrica agregatului ComandaAprovizionare. Verifică eligibilitatea furnizorului prin serviciul de validare, generează identificatorul unic și asamblează comanda inițială.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Politici de aprobare a comenzilor în funcție de pragurile valorice contractuale.

### 70. 📄 [`PoliticaAprobareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Tip Componentă DDD**: `Domain Policy (Politică de Business DDD)`
* **Explicație Sugestivă & Rol de Business**: Politică de domeniu ce stabilește pragurile de autorizare: comenzi sub 10.000 EUR sunt aprobate automat, comenzi peste 10.000 EUR cer Manager Achiziții, peste 50.000 EUR cer aprobare CFO.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Servicii de validare a eligibilității plasării comenzilor (furnizor activ, linii valide, articole distincte).

### 71. 📄 [`ComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Implementarea verificărilor de eligibilitate: furnizor în stare ACTIVĂ, unicitatea articolelor pe linii și prezența datelor obligatorii.

### 72. 📄 [`IComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Interfața serviciului de validare a comenzilor de aprovizionare.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce asamblează serviciile pure de domeniu în containerul IoC fără a polua clasele de business.

### 73. 📄 [`ComenziDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Explicație Sugestivă & Rol de Business**: Clasă de configurare Spring (@Configuration, @Bean) ce expune serviciile de calcul, validare, politica și fabrica modulului Comenzi în containerul IoC.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Agregatele Furnizor, InspectieCalitate, InspectorCalitate, RaportNeconformitate (NCR), entitatea ScorecardLunar, VOs și porturile de repository.

### 74. 📄 [`ActiuneCorectiva.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce descrie măsura impusă furnizorului pentru remedierea unei neconformități din raportul NCR.

### 75. 📄 [`Adresa.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object imutabil pentru adresa poștală (stradă, oraș, județ, cod poștal, țară).

### 76. 📄 [`AlocareInspectorService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Domain Service pentru validarea si alocarea inspectorului de calitate conform disponibilitatii si certificarii (Figura 20).

### 77. 📄 [`CalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Serviciu de domeniu ce calculează scorul lunar ponderat: 60% OTIF + 40% Rata Conformitate Loturi.

### 78. 📄 [`Calificativ.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de performanță acordată lunar pe baza scorului: CLASA_A, CLASA_B, CLASA_C, SUSPENDAT.

### 79. 📄 [`Certificare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`Certificare.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 80. 📄 [`ConditiiContractuale.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ConditiiContractuale.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 81. 📄 [`Email.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce validează formatul adreselor electronice conform standardului RFC-5322.

### 82. 📄 [`FullName.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object compus din prenume și nume de familie.

### 83. 📄 [`Furnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat pentru partenerii furnizori. Gestionează starea de calificare (PROSPECT, ACTIV, SUSPENDAT, BLOCAT), datele de contact (Adresa, Email, Iban) și lista de evaluări ScorecardLunar. Interzice plasarea comenzilor dacă starea nu este ACTIV.

### 84. 📄 [`FurnizorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru partenerii furnizori și stările lor de calificare.

### 85. 📄 [`IPerformantaLivrariAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`IPerformantaLivrariAdapter.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 86. 📄 [`Iban.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object bancar ce validează corectitudinea codului internațional de cont prin algoritmul Modulo 97.

### 87. 📄 [`InspectieCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat a inspecțiilor calitative pe loturi. Încorporează planul de eșantionare AQL și lista de defecte identificate. Emite verdictul oficial de conformitate și declanșează rapoarte NCR dacă lotul este respins.

### 88. 📄 [`InspectieRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru gestionarea inspecțiilor calitative programate și executate.

### 89. 📄 [`InspectorCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat ce reprezintă inspectorul de calitate. Gestionează certificările profesionale deținute și colecția intervalelor de disponibilitate calendaristică (@ElementCollection cu @CollectionTable).

### 90. 📄 [`InspectorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru interogarea inspectorilor disponibili într-un interval temporal.

### 91. 📄 [`NCRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru persistența rapoartelor de neconformitate calitativă.

### 92. 📄 [`PerformantaLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`PerformantaLivrariVO.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 93. 📄 [`PlanAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce definește parametrii de eșantionare statistică ISO 2859-1 (mărime eșantion, nivel de acceptare Ac, nivel de respingere Re).

### 94. 📄 [`PretNegociat.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce reflectă prețul agreat contractual pentru un articol pe o perioadă determinată.

### 95. 📄 [`RaportNeconformitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat pentru rapoartele de neconformitate (NCR). Înregistrează abaterile calitative, analiza cauzei rădăcină și acțiunile corective impuse furnizorului.

### 96. 📄 [`Scor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object numeric (0.00 la 100.00) pentru punctajul acordat în scorecard.

### 97. 📄 [`ScorecardLunar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Tip Componentă DDD**: `Component Entity (Entitate Componentă DDD)`
* **Explicație Sugestivă & Rol de Business**: Entitate componentă a agregatului Furnizor. Reține performanța lunară calculată: indicatorul OTIF, rata de conformitate a loturilor și scorul ponderat total.

### 98. 📄 [`StareFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de domeniu pentru starea juridică a furnizorului: PROSPECT, ACTIV, SUSPENDAT, BLOCAT.

### 99. 📄 [`StareInspectie.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare de stare pentru inspecția de laborator: PROGRAMATA, IN_CURS, FINALIZATA, ANULATA.

### 100. 📄 [`StareNCR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare pentru ciclul de viață al raportului NCR: DESCHIS, ANALIZA_CAUZA, ACTIUNE_DEFINITA, IN_VERIFICARE, INCHIS, ESCALADAT.

### 101. 📄 [`Verdict.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare pentru decizia calitativă finală: CONFORM, ACCEPTAT_CONDITIONAT, RESPINS.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Serviciul de calcul al formulei lunare de performanță (OTIF) și DTO-urile de dashboard calitativ.

### 102. 📄 [`CalitateDashboardView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Entity View Computation: Proiectie dashboard pentru calitatea furnizorilor si inspectii (Sprint 2 - ADR-003).

### 103. 📄 [`ICalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Contract pentru Serviciul de Calcul Calitate si Scorecard Furnizori (Sprint 2 - ADR-003).

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la înregistrarea furnizorilor, finalizarea inspecțiilor sau deschiderea rapoartelor NCR.

### 104. 📄 [`CertificareInspectorExpiraEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`CertificareInspectorExpiraEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 105. 📄 [`FurnizorBlocatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`FurnizorBlocatEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 106. 📄 [`FurnizorSuspendatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`FurnizorSuspendatEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 107. 📄 [`InspectieProgramataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`InspectieProgramataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 108. 📄 [`LotScutitDeInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Eveniment emis cand un lot este scutit de inspectie fizica AQL (ex: furnizor de clasa A cu istoric impecabil conform Figurii 1, Tabelului 1.3 si Figurii 20 din Project_OOAD.docx).

### 109. 📄 [`NCREscaladatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`NCREscaladatEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 110. 📄 [`VerdictInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`VerdictInspectieEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Fabricile de asamblare și calificare a furnizorilor și a inspecțiilor de lot.

### 111. 📄 [`FurnizorFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Tip Componentă DDD**: `Domain Factory (Fabrică de Agregate DDD)`
* **Explicație Sugestivă & Rol de Business**: Fabrica agregatului Furnizor, validând unicitatea fiscală a CUI-ului și conformitatea contului IBAN la înregistrare.

### 112. 📄 [`InspectieCalitateFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Tip Componentă DDD**: `Domain Factory (Fabrică de Agregate DDD)`
* **Explicație Sugestivă & Rol de Business**: Fabrica inspecțiilor calitative, selectând automat planul AQL adecvat mărimii lotului prin politica de selecție.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Politici de eșantionare statistică AQL conform standardului internațional ISO 2859-1.

### 113. 📄 [`PoliticaSelectieAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Tip Componentă DDD**: `Domain Policy (Politică de Business DDD)`
* **Explicație Sugestivă & Rol de Business**: Politică de afaceri ce implementează tabelele de eșantionare statistică ISO 2859-1 (General Inspection Level II).

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Servicii de validare a conformității inspecțiilor, disponibilității inspectorilor și a termenelor contractuale.

### 114. 📄 [`CalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Validează compatibilitatea certificărilor inspectorului cu tipul de material inspectat și absența suprapunerilor de concediu.

### 115. 📄 [`ICalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Interfața serviciului de validare a conformității calitative.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce instanțiază serviciile și fabricile modulului Calitate.

### 116. 📄 [`CalitateDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Explicație Sugestivă & Rol de Business**: Clasă de configurare Spring (@Configuration, @Bean) ce instanțiază serviciile, politicile și fabricile modulului Calitate.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Agregatele FacturaFurnizor, NIR, OrdinPlata, VOs de reconciliere și toleranță, enums, serviciul ReconciliereService și porturile de repository.

### 117. 📄 [`ComandaSummaryVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ComandaSummaryVO.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 118. 📄 [`FacturaFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat a facturilor primite. Gestionează liniile de factură, starea reconcilierii (INREGISTRATA, IN_RECONCILIERE, RECONCILIATA, DISCREPANTA_DETECTATA, PLATITA) și restul de plată. Emite ordine de plată exclusiv dacă factura este aprobată.

### 119. 📄 [`FacturaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru căutarea, salvarea și interogarea facturilor primite de la furnizori.

### 120. 📄 [`FurnizoriBlocatiRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de consultare a listei negre a furnizorilor blocați, verificată înaintea autorizării oricărei plăți.

### 121. 📄 [`IBancaPlataProvider.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port de ieșire hexagonal către gateway-ul de plăți (implementat de StripeBancaPlataAdapter în stratul de infrastructură).

### 122. 📄 [`IComenziAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Tip Componentă DDD**: `Port de Domeniu / Interfață de Business`
* **Explicație Sugestivă & Rol de Business**: Port de comunicare pentru obținerea datelor din comanda de aprovizionare necesare procedurii 3-Way Matching.

### 123. 📄 [`LinieFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce reflectă rândul de pe factura furnizorului: articol, cantitate facturată, preț unitar, cotă TVA și valoare totală.

### 124. 📄 [`LinieNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce reflectă rândul de recepție fizică în depozit: articol, cantitate descărcată și starea integrității coletelor.

### 125. 📄 [`LinieSumar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object de sinteză utilizat în raportarea contabilă.

### 126. 📄 [`NIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat a Notei de Intrare Recepție întocmită în depozit la descărcarea fizică a mărfii. Folosită ca reper faptic în 3-Way Matching.

### 127. 📄 [`NIRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru persistența Notelor de Intrare Recepție.

### 128. 📄 [`OrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Tip Componentă DDD**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Explicație Sugestivă & Rol de Business**: Rădăcina de agregat ce mandatează transferul bancar sau debitarea prin Stripe. Gestionează stările DRAFT, APROBAT, IN_PROCESARE, EXECUTAT, ESUAT.

### 129. 📄 [`OrdinPlataRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Tip Componentă DDD**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Explicație Sugestivă & Rol de Business**: Port de repository pentru ordinele de plată emise către procesatorii financiari.

### 130. 📄 [`ReconciliereService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Serviciu de domeniu ce orchestrează reconcilierea trilaterală: confruntă Factura cu Comanda și NIR-ul și aprobă plata dacă discrepanțele sunt în limitele de toleranță.

### 131. 📄 [`RezultatReconciliere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce sintetizează rezultatul algoritmului 3-Way Matching (potrivire perfectă sau discrepanțe pe preț/cantitate).

### 132. 📄 [`StareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare pentru ciclul de viață al facturii: INREGISTRATA, IN_RECONCILIERE, RECONCILIATA, DISCREPANTA_DETECTATA, APROBATA_PENTRU_PLATA, PLATITA, CONTESTATA, ANULATA.

### 133. 📄 [`StareNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare pentru starea Notei de Recepție: EMIS, CONFIRMAT, CONTESTAT.

### 134. 📄 [`StareOrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Enumerare pentru ordinul de plată: DRAFT, PROPUS, APROBAT, IN_PROCESARE, EXECUTAT, ESUAT, ANULAT.

### 135. 📄 [`Suma.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object monetar imutabil ce încapsulează o valoare și moneda tranzacției, protejând operațiile de adunare/scădere împotriva amestecării valutelor.

### 136. 📄 [`Toleranta.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object ce definește abaterile procentuale sau absolute acceptabile la reconciliere (ex: ±1% cantitate sau ±5 EUR rotunjire TVA).

### 137. 📄 [`ValoareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Value Object financiar ce reține structura valorică a facturii: total net, valoare TVA și total brut.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Algoritmul de calcul matematic 3-Way Matching (reconciliere cantitate și preț între Comandă, NIR și Factură).

### 138. 📄 [`IReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Interfața serviciului matematic de calcul al discrepanțelor 3-Way Matching.

### 139. 📄 [`PlatiSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Tip Componentă DDD**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Explicație Sugestivă & Rol de Business**: Entity View Computation: Proiectie sumarizata a indicatorilor de plati si facturi (Sprint 2 - ADR-003).

### 140. 📄 [`ReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Tip Componentă DDD**: `Computation Service (Serviciu de Calcul Matematic)`
* **Explicație Sugestivă & Rol de Business**: Serviciu matematic determinist ce calculează delta cantitate și delta preț conform formulelor de reconciliere.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la reconcilierea facturilor, apropierea scadenței sau autorizarea plăților.

### 141. 📄 [`PlataExecutataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`PlataExecutataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

### 142. 📄 [`ScadentaApropiataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Tip Componentă DDD**: `Componentă de Domeniu Pur`
* **Explicație Sugestivă & Rol de Business**: Componentă a nucleului de domeniu din Sprintul 2 (`ScadentaApropiataEvent.java`). Implementează reguli de business, protejează invarianți de domeniu și asigură decuplarea modulară conform principiilor DDD și ADR-003.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Fabricile de creare a facturilor furnizor și a notelor de intrare recepție (NIR).

### 143. 📄 [`FacturaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Tip Componentă DDD**: `Domain Factory (Fabrică de Agregate DDD)`
* **Explicație Sugestivă & Rol de Business**: Fabrica agregatului FacturaFurnizor, verificând corectitudinea datelor fiscale și concordanța totalului liniilor cu totalul brut declarat.

### 144. 📄 [`NIRFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Tip Componentă DDD**: `Domain Factory (Fabrică de Agregate DDD)`
* **Explicație Sugestivă & Rol de Business**: Fabrica Notei de Intrare Recepție întocmită pe baza loturilor fizice descărcate în depozit.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Politici de autorizare a plăților condiționate de absența blocajelor sau a disputelor calitative.

### 145. 📄 [`PoliticaAutorizarePlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Tip Componentă DDD**: `Domain Policy (Politică de Business DDD)`
* **Explicație Sugestivă & Rol de Business**: Politică de afaceri ce interzice aprobarea plății dacă furnizorul este blocat sau dacă există rapoarte NCR nerezolvate pentru marfa asociată.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Servicii de validare a corectitudinii fiscale a facturilor și a conturilor IBAN.

### 146. 📄 [`FacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Validează conformitatea fiscală a facturii, data emiterii și corectitudinea calculului cotelor de TVA.

### 147. 📄 [`IFacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Tip Componentă DDD**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Explicație Sugestivă & Rol de Business**: Interfața serviciului de validare a facturilor.

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce expune serviciile de reconciliere și validare ca bean-uri Singleton.

### 148. 📄 [`FacturiDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Tip Componentă DDD**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Explicație Sugestivă & Rol de Business**: Clasă de configurare Spring (@Configuration, @Bean) ce expune serviciile de reconciliere, politicile și fabricile modulului Facturi.

## 📁 Director: `src/test/java/ro/uaic/feaa/sdbis/supplychain/unit`
* **Modul de Business**: `Domain Unit Tests (Sprint 2 DoD)`
* **Rol în Arhitectură**: Suitele complete de teste unitare care validează invarianții, regulile de calcul, validările și mașinile de stări conform criteriilor Definition of Done din Sprintul 2.

### 149. 📄 [`ComputationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită de teste unitare ce garantează determinismul matematic și absența oricăror erori de rotunjire financiară prin utilizarea BigDecimal cu HALF_UP.

### 150. 📄 [`ComputationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită de teste unitare ce verifică algoritmul formulei de scorecard lunar OTIF și formula matematică a reconcilierii trilaterale.

### 151. 📄 [`ValidationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită de teste unitare ce verifică auto-validarea în constructorii Value Objects (aruncarea de excepții la valori negative, monede nule sau formate invalide).

### 152. 📄 [`ValidationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită de teste unitare ce verifică serviciile de validare inter-agregate (furnizori eligibili, certificări valide de inspector).

### 153. 📄 [`WorkflowRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită completă de teste unitare JUnit 5 pe regulile și invarianții de tranziție de stare din agregatele ComandaAprovizionare, Furnizor și FacturaFurnizor.

### 154. 📄 [`WorkflowServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Tip Componentă DDD**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Explicație Sugestivă & Rol de Business**: Suită de teste unitare ce validează orchestrarea fluxurilor de comenzi (inclusiv cascadarea anulării loturilor), alocarea inspecțiilor și reconcilierea 3-Way Matching.

---

# 3. RECAPITULARE STATISTICĂ BLUEPRINT SPRINT 2

* **Total Directoare din Arbore**: 26 directoare fizice.
* **Total Componente Indexate**: 154 fișiere Java exclusiv pentru Sprintul 2.
* **Conformitate**: 100% aliniat cu structura de fișiere de pe disc și criteriile Definition of Done (DoD).