# BLUEPRINT ARHITECTURAL & CATALOGUL DE COD – SPRINT 2
## Structura Fizică 1:1 a Proiectului (Rich Domain Model – ADR-003)

---

### METADATE BLUEPRINT
* **Pachet Rădăcină**: `ro.uaic.feaa.sdbis.supplychain`
* **Scope Exclusiv**: **Sprintul 2** (Nucleul de Domeniu Bogat, Value Objects, Agregate, Fabrici, Servicii de Calcul/Validare/Workflow, Politici, Porturi Repository, Configurații Spring Domain și Teste Unitare DoD).
* **Navigare Directă**: Fiecare fișier conține calea sa relativă exactă în proiect și un link clickabil local direct (`file:///C:/Users/bmugu/Desktop/app/src/...`) care deschide automat fișierul de cod în editor.

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

# 2. BLUEPRINT DETALIAT PE DIRECTOARE ȘI FIȘIERE (SPECIFICAȚIE DE COD 1:1)

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Adnotări de arhitectură DDD care definesc rolul semantic și stereotipul fiecărei componente din nucleul de domeniu conform ADR-003.

### 1. 📄 [`AggregateRoot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/AggregateRoot.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru o radacina de agregat (Aggregate Root). Punctul unic de acces si control al tranzactiilor si invarianților pentru un cluster de entitati.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o radacina de agregat (Aggregate Root).
 * Punctul unic de acces si control al tranzactiilor si invarianților pentru un cluster de entitati.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AggregateRoot {
    String value() default "";
}
```

### 2. 📄 [`ApplicationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ApplicationService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru un serviciu din Application Layer (Application Service / Use-Case Service). Responsabil de coordonarea fluxurilor de utilizare (Use Cases), managementul tranzacționalității,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un serviciu din Application Layer (Application Service / Use-Case Service).
 * Responsabil de coordonarea fluxurilor de utilizare (Use Cases), managementul tranzacționalității,
 * orchestrarea agregatelor, colaborarea cu repository-urile de domeniu și publicarea
 * evenimentelor de domeniu către Transactional Outbox.
 * Nu conține reguli de business (care aparțin agregatelor și serviciilor de domeniu).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplicationService {
    String value() default "";
}
```

### 3. 📄 [`ComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip semantic pentru servicii de calcul (Computation Service conform cerințelor din Sprint 2 / SOA). Marchează contractele și implementările serviciilor de calcul numeric și indicatori de business.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip semantic pentru servicii de calcul (Computation Service conform cerințelor din Sprint 2 / SOA).
 * Marchează contractele și implementările serviciilor de calcul numeric și indicatori de business.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComputationService {
    String value() default "";
}
```

### 4. 📄 [`DomainComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD / SOA pentru servicii de calcul în domeniu (Domain Computation Service). Marchează serviciile responsabile de calculul formulelor de business, indicatorilor statistici,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru servicii de calcul în domeniu (Domain Computation Service).
 * Marchează serviciile responsabile de calculul formulelor de business, indicatorilor statistici,
 * scorurilor agregate și derivarea atributelor complexe (ex: Scorecard lunar, estimare lead time, reconciliere).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainComputationService {
    String value() default "";
}
```

### 5. 📄 [`DomainEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainEntity.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru o entitate de domeniu (Entity). Un obiect care are identitate de sine statatoare (ID) si ciclu de viata,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o entitate de domeniu (Entity).
 * Un obiect care are identitate de sine statatoare (ID) si ciclu de viata,
 * dar este controlat din cadrul unui Aggregate Root.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainEntity {
    String value() default "";
}
```

### 6. 📄 [`DomainFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD / SOA pentru o fabrică de domeniu (Domain Factory / Entity Service). Responsabil de instanțierea și asamblarea agregatelor complexe sau a entităților
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru o fabrică de domeniu (Domain Factory / Entity Service).
 * Responsabil de instanțierea și asamblarea agregatelor complexe sau a entităților
 * asigurând că obiectul creat se află încă de la început într-o stare validă și conformă invarianților.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainFactory {
    String value() default "";
}
```

### 7. 📄 [`DomainPolicy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainPolicy.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru o politică de business (Domain Policy / Strategy). Marchează clasele ce implementează reguli decizionale configurabile sau algoritmi de decizie
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o politică de business (Domain Policy / Strategy).
 * Marchează clasele ce implementează reguli decizionale configurabile sau algoritmi de decizie
 * de domeniu conform Strategy Pattern (ex: Politica de Aprobare Comenzi, Politica de Autorizare a Plăților,
 * Politica de Selecție Plan AQL conform ISO 2859-1).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainPolicy {
    String value() default "";
}
```

### 8. 📄 [`DomainRepository.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainRepository.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru o interfata de Repository de domeniu (Output Port). Ofera acces de tip colectie la agregatele din domeniu, independent de infrastructura tehnica.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o interfata de Repository de domeniu (Output Port).
 * Ofera acces de tip colectie la agregatele din domeniu, independent de infrastructura tehnica.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainRepository {
    String value() default "";
}
```

### 9. 📄 [`DomainService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru un serviciu de domeniu (Domain Service / Task Service). Marchează componentele din Domain Layer care încapsulează logică de business pură,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un serviciu de domeniu (Domain Service / Task Service).
 * Marchează componentele din Domain Layer care încapsulează logică de business pură,
 * reguli operaționale sau algoritmi ce implică multiple agregate și nu aparțin
 * în mod natural unei singure entități sau rădăcini de agregat.
 * Rămâne complet decuplat de Spring sau alte framework-uri de infrastructură (ADR-011).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainService {
    String value() default "";
}
```

### 10. 📄 [`DomainValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/DomainValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD / SOA pentru servicii de validare a logicii de domeniu (Domain Validation Service). Marchează serviciile responsabile de verificarea invarianților complecși de business,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru servicii de validare a logicii de domeniu (Domain Validation Service).
 * Marchează serviciile responsabile de verificarea invarianților complecși de business,
 * a integrității cross-entity și cross-aggregate (ex: 3-Way Matching, verificarea eligibilității la plată).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainValidationService {
    String value() default "";
}
```

### 11. 📄 [`InfrastructureService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/InfrastructureService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip Clean Architecture pentru servicii din Infrastructure Layer (Infrastructure Service). Marchează componentele tehnice, adaptoarele către sisteme externe (REST, Stripe),
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip Clean Architecture pentru servicii din Infrastructure Layer (Infrastructure Service).
 * Marchează componentele tehnice, adaptoarele către sisteme externe (REST, Stripe),
 * procesele de fundal (Schedulers) și mecanismele de mesagerie asincronă (Outbox/Inbox Dispatchers).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InfrastructureService {
    String value() default "";
}
```

### 12. 📄 [`QueryService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/QueryService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip CQRS pentru servicii dedicate interogărilor și proiecțiilor (Query Service / Entity View Computation). Responsabil de extragerea și asamblarea modelelor de citire optimizate (Read Models, Summary Views, Dashboard Views),
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip CQRS pentru servicii dedicate interogărilor și proiecțiilor (Query Service / Entity View Computation).
 * Responsabil de extragerea și asamblarea modelelor de citire optimizate (Read Models, Summary Views, Dashboard Views),
 * fără a modifica starea agregatelor (operațiuni read-only, idempotente).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryService {
    String value() default "";
}
```

### 13. 📄 [`UtilityService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/UtilityService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip SOA pentru servicii utilitare transversale (Utility Service / Cross-Cutting Service). Marchează componentele responsabile de funcționalități transversale reutilizabile în cadrul soluției:
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip SOA pentru servicii utilitare transversale (Utility Service / Cross-Cutting Service).
 * Marchează componentele responsabile de funcționalități transversale reutilizabile în cadrul soluției:
 * audit operațional imutabil (JurnalAuditService), servicii de notificare (NotificariDispatcherService) etc.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UtilityService {
    String value() default "";
}
```

### 14. 📄 [`ValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip semantic pentru servicii de validare (Validation Service conform cerințelor din Sprint 2 / SOA). Marchează contractele și implementările serviciilor de validare a regulilor de business.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip semantic pentru servicii de validare (Validation Service conform cerințelor din Sprint 2 / SOA).
 * Marchează contractele și implementările serviciilor de validare a regulilor de business.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationService {
    String value() default "";
}
```

### 15. 📄 [`ValueObject.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/ValueObject.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip DDD pentru un obiect valoare (Value Object). Un obiect imutabil definit exclusiv prin atributele sale structurale,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un obiect valoare (Value Object).
 * Un obiect imutabil definit exclusiv prin atributele sale structurale,
 * fara o identitate separata (ID).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueObject {
    String value() default "";
}
```

### 16. 📄 [`WorkflowService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/annotation/WorkflowService.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Stereotip SOA / Application pentru servicii de workflow operațional (Workflow Service / Orchestration Service). Marchează contractele și implementările fluxurilor operaționale complexe ale aplicației
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip SOA / Application pentru servicii de workflow operațional (Workflow Service / Orchestration Service).
 * Marchează contractele și implementările fluxurilor operaționale complexe ale aplicației
 * (ex: flux aprovizionare comenzi, workflow recepție și calificare furnizori, workflow reconciliere și plăți).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WorkflowService {
    String value() default "";
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Excepții de domeniu aruncate de constructorii Value Objects și de serviciile de validare când sunt încălcați invarianții de business.

### 17. 📄 [`DomainValidationException.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/exception/DomainValidationException.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Exceptie pentru incalcarea regulilor de validare a domeniului (ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.exception;

/**
 * Exceptie pentru incalcarea regulilor de validare a domeniului (ADR-003).
 */
public class DomainValidationException extends RuntimeException {

    private final String codEroare;

    public DomainValidationException(String mesaj) {
        super(mesaj);
        this.codEroare = "VALIDARE_DOMENIU";
    }

    public DomainValidationException(String codEroare, String mesaj) {
        super(mesaj);
        this.codEroare = codEroare;
    }

    public String getCodEroare() {
        return codEroare;
    }
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Convertoare JPA AttributeConverter pentru serializarea și deserializarea transparentă a identificatorilor și tipurilor Value Object în coloane SQL.

### 18. 📄 [`CodMaterialConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CodMaterialConverter.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `CodMaterialConverter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;

@Converter(autoApply = false)
public class CodMaterialConverter implements AttributeConverter<CodMaterial, String> {
    @Override
    public String convertToDatabaseColumn(CodMaterial attribute) {
        return attribute == null ? null : attribute.getCod();
    }
    @Override
    public CodMaterial convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CodMaterial.of(dbData);
    }
}
```

### 19. 📄 [`CurrencyConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/CurrencyConverter.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `CurrencyConverter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        return attribute != null ? attribute.getCurrencyCode() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        return (dbData != null && !dbData.isBlank()) ? Currency.getInstance(dbData.trim()) : null;
    }
}

```

### 20. 📄 [`IdComandaConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdComandaConverter.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdComandaConverter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.util.UUID;

@Converter(autoApply = false)
public class IdComandaConverter implements AttributeConverter<IdComanda, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdComanda attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdComanda convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdComanda.of(dbData);
    }
}
```

### 21. 📄 [`IdFurnizorConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdFurnizorConverter.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdFurnizorConverter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.util.UUID;

@Converter(autoApply = false)
public class IdFurnizorConverter implements AttributeConverter<IdFurnizor, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdFurnizor attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdFurnizor convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdFurnizor.of(dbData);
    }
}
```

### 22. 📄 [`IdLotConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/persistence/converters/IdLotConverter.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdLotConverter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.UUID;

@Converter(autoApply = false)
public class IdLotConverter implements AttributeConverter<IdLot, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdLot attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdLot convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdLot.of(dbData);
    }
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/shared`
* **Modul de Business**: `Shared Kernel`
* **Rol în Arhitectură**: Value Objects universale (IdComanda, IdFurnizor, IdLot, CodMaterial, Perioada), mecanismul Transactional Outbox / Inbox și interfețele de audit.

### 23. 📄 [`AbstractRepositoryJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AbstractRepositoryJPA.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Biblioteca tehnica comuna (Infrastructure) - Figura 10 din Project_OOAD.docx. Contine accesul la EntityManager pentru implementarea operatiilor CRUD de baza.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Biblioteca tehnica comuna (Infrastructure) - Figura 10 din Project_OOAD.docx.
 * Contine accesul la EntityManager pentru implementarea operatiilor CRUD de baza.
 */
@Transactional
public abstract class AbstractRepositoryJPA<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractRepositoryJPA(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void add(T entity) {
        em.persist(entity);
    }

    public void save(T entity) {
        em.merge(entity);
    }

    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAll() {
    // ... [restul de 4 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 24. 📄 [`AuditLogEntry.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/AuditLogEntry.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `AuditLogEntry.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_audit_log")
public class AuditLogEntry implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "actiune", nullable = false, length = 255)
    private String actiune;

    @Column(name = "obiect", columnDefinition = "TEXT", nullable = false)
    private String obiect;

    protected AuditLogEntry() {}

    public AuditLogEntry(UUID id, Instant timestamp, String actiune, String obiect) {
        this.id = Objects.requireNonNull(id);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.actiune = Objects.requireNonNull(actiune);
        this.obiect = Objects.requireNonNull(obiect);
    }

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getActiune() { return actiune; }
    public String getObiect() { return obiect; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
    // ... [restul de 9 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 25. 📄 [`CodMaterial.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/CodMaterial.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `CodMaterial.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class CodMaterial implements Serializable {
    private String cod;

    protected CodMaterial() {}

    public CodMaterial(String cod) {
        if (cod == null || cod.trim().isEmpty()) {
            throw new IllegalArgumentException("CodMaterial cannot be null or empty");
        }
        this.cod = cod.trim().toUpperCase();
    }

    public static CodMaterial of(String cod) {
        return new CodMaterial(cod);
    }

    public String getCod() {
        return cod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodMaterial that)) return false;
        return Objects.equals(cod, that.cod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cod);
    }

    @Override
    // ... [restul de 4 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 26. 📄 [`DomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/DomainEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Contract comun pentru toate evenimentele de domeniu din sistem.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Contract comun pentru toate evenimentele de domeniu din sistem.
 */
public interface DomainEvent extends Serializable {
    UUID eventId();
    Instant occurredOn();
}
```

### 27. 📄 [`IJurnalAudit.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IJurnalAudit.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Port pentru jurnalizarea imutabila (append-only) a accesului, actiunilor critice si trasabilitatii modificarilor operationale (Sprint 2 - ADR-003, GDPR, NIS2).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

/**
 * Port pentru jurnalizarea imutabila (append-only) a accesului, actiunilor critice
 * si trasabilitatii modificarilor operationale (Sprint 2 - ADR-003, GDPR, NIS2).
 */
public interface IJurnalAudit {

    /**
     * Jurnalizeaza un acces sau o actiune importanta.
     */
    void inregistreazaAcces(String actiune, String obiect);

    /**
     * Asigura trasabilitatea tranzitiilor de stare si modificarilor operationale (Audit Service).
     */
    void inregistreazaModificare(String entitate, String id, String stareVeche, String stareNoua);

    /**
     * Jurnalizeaza evenimente operationale de business pentru auditabilitate.
     */
    void inregistreazaEvenimentOperational(String categorie, String referinta, String detalii);
}
```

### 28. 📄 [`IdComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdComanda.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdComanda.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@ValueObject
@Embeddable
public final class IdComanda implements Serializable {
    @Column(name = "id_val")
    private UUID value;

    protected IdComanda() {}

    public IdComanda(UUID value) {
        this.value = Objects.requireNonNull(value, "IdComanda value cannot be null");
    }

    public static IdComanda of(UUID value) {
        return new IdComanda(value);
    }

    public static IdComanda random() {
        return new IdComanda(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdComanda that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
    // ... [restul de 8 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 29. 📄 [`IdFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdFurnizor.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdFurnizor.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@ValueObject
@Embeddable
public final class IdFurnizor implements Serializable {
    @Column(name = "id_val")
    private UUID value;

    protected IdFurnizor() {}

    public IdFurnizor(UUID value) {
        this.value = Objects.requireNonNull(value, "IdFurnizor value cannot be null");
    }

    public static IdFurnizor of(UUID value) {
        return new IdFurnizor(value);
    }

    public static IdFurnizor random() {
        return new IdFurnizor(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdFurnizor that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
    // ... [restul de 8 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 30. 📄 [`IdLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/IdLot.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IdLot.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@ValueObject
@Embeddable
public final class IdLot implements Serializable {
    @Column(name = "id_val")
    private UUID value;

    protected IdLot() {}

    public IdLot(UUID value) {
        this.value = Objects.requireNonNull(value, "IdLot value cannot be null");
    }

    public static IdLot of(UUID value) {
        return new IdLot(value);
    }

    public static IdLot random() {
        return new IdLot(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdLot that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
    // ... [restul de 8 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 31. 📄 [`InboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxEventEntity.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InboxEventEntity.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_inbox")
public class InboxEventEntity implements Serializable {

    @Id
    @Column(name = "id", length = 128)
    private String id; // format: "consumer:eventId"

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "consumer", nullable = false, length = 255)
    private String consumer;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxEventEntity() {}

    public InboxEventEntity(String consumer, UUID eventId, Instant processedAt) {
        this.consumer = consumer != null ? consumer : "default";
        this.eventId = Objects.requireNonNull(eventId);
        this.id = this.consumer + ":" + eventId;
        this.processedAt = Objects.requireNonNull(processedAt);
    }

    public String getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getConsumer() { return consumer; }
    public Instant getProcessedAt() { return processedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
    // ... [restul de 9 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 32. 📄 [`InboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Port pentru verificarea idempotentei la consumul evenimentelor (Idempotent Inbox pattern). Suporta urmarirea per-consumer a procesarii fiecarui eveniment.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.UUID;

/**
 * Port pentru verificarea idempotentei la consumul evenimentelor (Idempotent Inbox pattern).
 * Suporta urmarirea per-consumer a procesarii fiecarui eveniment.
 */
public interface InboxRepo {
    boolean existaEveniment(UUID eventId);
    void marcheazaProcesat(UUID eventId);

    boolean existaEveniment(String consumer, UUID eventId);
    void marcheazaProcesat(String consumer, UUID eventId);
}
```

### 33. 📄 [`InboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/InboxRepoJPA.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InboxRepoJPA.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.UUID;

@Repository
public class InboxRepoJPA extends AbstractRepositoryJPA<InboxEventEntity, String> implements InboxRepo {

    public InboxRepoJPA() {
        super(InboxEventEntity.class);
    }

    @Override
    public boolean existaEveniment(UUID eventId) {
        return existaEveniment("default", eventId);
    }

    @Override
    public void marcheazaProcesat(UUID eventId) {
        marcheazaProcesat("default", eventId);
    }

    @Override
    public boolean existaEveniment(String consumer, UUID eventId) {
        String key = (consumer != null ? consumer : "default") + ":" + eventId;
        return em.find(InboxEventEntity.class, key) != null;
    }

    @Override
    public void marcheazaProcesat(String consumer, UUID eventId) {
        if (!existaEveniment(consumer, eventId)) {
            em.persist(new InboxEventEntity(consumer, eventId, Instant.now()));
            em.flush();
        }
    }
}
```

### 34. 📄 [`OutboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxEventEntity.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `OutboxEventEntity.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_outbox")
public class OutboxEventEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID eventId;

    @Column(name = "timestamp", nullable = false)
    private Instant occurredOn;

    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "published", nullable = false)
    private boolean published = false;

    protected OutboxEventEntity() {}

    public OutboxEventEntity(UUID eventId, Instant occurredOn, String eventType, String payload) {
        this.eventId = Objects.requireNonNull(eventId);
        this.occurredOn = Objects.requireNonNull(occurredOn);
        this.eventType = Objects.requireNonNull(eventType);
        this.payload = Objects.requireNonNull(payload);
        this.published = false;
    }

    public UUID getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    // ... [restul de 15 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 35. 📄 [`OutboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Port pentru salvarea si publicarea evenimentelor conform Transactional Outbox pattern.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.List;
import java.util.UUID;

/**
 * Port pentru salvarea si publicarea evenimentelor conform Transactional Outbox pattern.
 */
public interface OutboxRepo {
    void saveEvent(DomainEvent e);
    List<DomainEvent> pollUnpublishedEvents();
    void markAsPublished(UUID eventId);
}
```

### 36. 📄 [`OutboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/OutboxRepoJPA.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `OutboxRepoJPA.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepoJPA extends AbstractRepositoryJPA<OutboxEventEntity, UUID> implements OutboxRepo {

    private final ObjectMapper objectMapper;

    public OutboxRepoJPA(@Autowired(required = false) ObjectMapper objectMapper) {
        super(OutboxEventEntity.class);
        if (objectMapper != null) {
            this.objectMapper = objectMapper;
        } else {
            this.objectMapper = new ObjectMapper();
            this.objectMapper.registerModule(new JavaTimeModule());
        }
    }

    @Override
    public void saveEvent(DomainEvent e) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(e);
        } catch (Exception ex) {
            payload = "{\"error\":\"SerializationFailed\",\"message\":\"" + ex.getMessage() + "\"}";
        }

        OutboxEventEntity entity = new OutboxEventEntity(
            e.eventId(), e.occurredOn() != null ? e.occurredOn() : Instant.now(), e.getClass().getName(), payload
        );
        em.persist(entity);
        em.flush();
    }
    // ... [restul de 42 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 37. 📄 [`Perioada.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/Perioada.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Perioada.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Perioada implements Serializable {
    private LocalDate dataInceput;
    private LocalDate dataFinalizare;

    protected Perioada() {}

    public Perioada(LocalDate dataInceput, LocalDate dataFinalizare) {
        Objects.requireNonNull(dataInceput, "dataInceput cannot be null");
        Objects.requireNonNull(dataFinalizare, "dataFinalizare cannot be null");
        if (dataFinalizare.isBefore(dataInceput)) {
            throw new IllegalArgumentException("dataFinalizare must be >= dataInceput");
        }
        this.dataInceput = dataInceput;
        this.dataFinalizare = dataFinalizare;
    }

    public static Perioada of(LocalDate dataInceput, LocalDate dataFinalizare) {
        return new Perioada(dataInceput, dataFinalizare);
    }

    public static Perioada ofLuna(int an, int luna) {
        LocalDate start = LocalDate.of(an, luna, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return new Perioada(start, end);
    }

    public static Perioada lunaCurenta() {
        LocalDate now = LocalDate.now();
        return ofLuna(now.getYear(), now.getMonthValue());
    }

    public static Perioada zileUrmatoare(int days) {
    // ... [restul de 39 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 38. 📄 [`StoredDomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/StoredDomainEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Reprezentare generica sau fallback pentru evenimente de domeniu persistate in Outbox/Inbox.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Reprezentare generica sau fallback pentru evenimente de domeniu persistate in Outbox/Inbox.
 */
public record StoredDomainEvent(
    UUID eventId,
    Instant occurredOn,
    String eventType,
    String payload
) implements DomainEvent {}
```

### 39. 📄 [`package-info.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/shared/package-info.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `package-info.java`.
* **Secvență de Cod (Implementare)**:
```java
@org.springframework.modulith.ApplicationModule(
    type = org.springframework.modulith.ApplicationModule.Type.OPEN
)
package ro.uaic.feaa.sdbis.supplychain.shared;
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Rădăcina de agregat ComandaAprovizionare, entitatea componentă Lot, Value Objects, enumerări de stare și porturile de persistență.

### 40. 📄 [`Cantitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Cantitate.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Cantitate.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Cantitate implements Serializable {
    private BigDecimal valoare;
    private String unitateMasura;

    protected Cantitate() {}

    public Cantitate(BigDecimal valoare, String unitateMasura) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cantitate valoare cannot be null or negative");
        }
        if (unitateMasura == null || unitateMasura.trim().isEmpty()) {
            throw new IllegalArgumentException("UnitateMasura cannot be null or empty");
        }
        this.valoare = valoare;
        this.unitateMasura = unitateMasura.trim().toUpperCase();
    }

    public static Cantitate of(double valoare, String unitate) {
        return new Cantitate(BigDecimal.valueOf(valoare), unitate);
    }

    public static Cantitate of(BigDecimal valoare, String unitate) {
        return new Cantitate(valoare, unitate);
    }

    public static Cantitate zero(String unitate) {
        return new Cantitate(BigDecimal.ZERO, unitate);
    }

    public Cantitate adauga(Cantitate alta) {
        Objects.requireNonNull(alta, "alta cantitate cannot be null");
        if (!this.unitateMasura.equalsIgnoreCase(alta.unitateMasura)) {
    // ... [restul de 42 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 41. 📄 [`ComandaAprovizionare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaAprovizionare.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Aggregate Root pentru Bounded Context-ul Procurement Order Management. Garanteaza invarianții din Tabelul 2.1 (Lifecycle Guard, Lot Quantity Boundary, Cascading Cancellation).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregate Root pentru Bounded Context-ul Procurement Order Management.
 * Garanteaza invarianții din Tabelul 2.1 (Lifecycle Guard, Lot Quantity Boundary, Cascading Cancellation).
 */
@AggregateRoot
@Entity
@Table(name = "comenzi_aprovizionare")
public class ComandaAprovizionare implements Serializable {

    @EmbeddedId
    private IdComanda id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numarComanda;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id"))
    private IdFurnizor furnizorId;

    @NotNull
    @Column(nullable = false)
    // ... [restul de 302 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 42. 📄 [`ComandaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/ComandaRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComandaRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;

@DomainRepository
public interface ComandaRepo {
    void add(ComandaAprovizionare comanda);
    Optional<ComandaAprovizionare> findById(IdComanda id);
    void save(ComandaAprovizionare comanda);
    void delete(ComandaAprovizionare comanda);
    boolean existaNumarComanda(String numarComanda);
    List<ComandaAprovizionare> findAll();
}
```

### 43. 📄 [`EstimareLeadTimeService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLeadTimeService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Domain Service pentru calculul datei de livrare folosind Strategy Pattern (MedieMobila, NetezireExponentiala, Percentila90). Pure Domain: fara adnotari de Spring!
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain Service pentru calculul datei de livrare folosind Strategy Pattern (MedieMobila, NetezireExponentiala, Percentila90).
 * Pure Domain: fara adnotari de Spring!
 */
@DomainComputationService
public class EstimareLeadTimeService {

    private IMetodaEstimareLeadTime strategie;

    public EstimareLeadTimeService() {
        this.strategie = new MedieMobilaStrategy();
    }

    public EstimareLeadTimeService(IMetodaEstimareLeadTime strategie) {
        this.strategie = Objects.requireNonNull(strategie, "Strategia cannot be null");
    }

    public void setStrategie(IMetodaEstimareLeadTime s) {
        this.strategie = Objects.requireNonNull(s, "Strategia cannot be null");
    }

    public EstimareLivrare calculeazaDataLivrare(MetriciLivrareVO metrici, IstoricLivrariVO istoric) {
        Objects.requireNonNull(metrici, "metrici cannot be null");
        Objects.requireNonNull(istoric, "istoric cannot be null");
        int zile = strategie.estimeazaZile(istoric);
        LocalDate dataBaza = metrici.dataLivrarePromisa() != null ? metrici.dataLivrarePromisa() : metrici.dataComanda();
        LocalDate dataEstimata = dataBaza.plusDays(zile);
        return new EstimareLivrare(dataEstimata, strategie.getClass().getSimpleName());
    }
}
```

### 44. 📄 [`EstimareLivrare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/EstimareLivrare.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `EstimareLivrare.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class EstimareLivrare implements Serializable {

    @NotNull
    @Column(name = "data_estimata")
    private LocalDate dataEstimata;

    @NotBlank
    @Column(name = "metoda")
    private String metoda;

    protected EstimareLivrare() {}

    public EstimareLivrare(LocalDate dataEstimata, String metoda) {
        this.dataEstimata = Objects.requireNonNull(dataEstimata, "dataEstimata cannot be null");
        if (metoda == null || metoda.trim().isEmpty()) {
            throw new IllegalArgumentException("metoda cannot be null or empty");
        }
        this.metoda = metoda.trim();
    }

    public boolean depasesteData(LocalDate ceruta) {
        Objects.requireNonNull(ceruta, "data ceruta cannot be null");
        return dataEstimata.isAfter(ceruta);
    }

    public LocalDate getDataEstimata() {
        return dataEstimata;
    }

    // ... [restul de 22 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 45. 📄 [`Expediere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Expediere.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Expediere.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ValueObject
@Embeddable
public final class Expediere implements Serializable {

    @NotBlank
    @Column(name = "transportator", nullable = false)
    private String transportator;

    @NotBlank
    @Column(name = "nr_awb", nullable = false)
    private String nrAWB;

    @NotNull
    @Column(name = "data_expediere", nullable = false)
    private LocalDate dataExpediere;

    @Column(name = "loturi_ids", length = 2000)
    private String loturiIds;

    protected Expediere() {}

    public Expediere(String transportator, String nrAWB, LocalDate dataExpediere, Set<IdLot> loturi) {
        if (transportator == null || transportator.trim().isEmpty()) {
            throw new IllegalArgumentException("transportator cannot be null or empty");
        }
        if (nrAWB == null || nrAWB.trim().isEmpty()) {
            throw new IllegalArgumentException("nrAWB cannot be null or empty");
        }
        this.transportator = transportator.trim();
    // ... [restul de 53 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 46. 📄 [`IGestiuneFurnizoriAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IGestiuneFurnizoriAdapter.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Port din Domain Layer pentru verificarea eligibilitatii furnizorului (Figura 11).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

/**
 * Port din Domain Layer pentru verificarea eligibilitatii furnizorului (Figura 11).
 */
public interface IGestiuneFurnizoriAdapter {
    boolean esteFurnizorEligibil(IdFurnizor id);
}
```

### 47. 📄 [`IMetodaEstimareLeadTime.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IMetodaEstimareLeadTime.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IMetodaEstimareLeadTime.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

public interface IMetodaEstimareLeadTime {
    int estimeazaZile(IstoricLivrariVO istoric);
}
```

### 48. 📄 [`IstoricLivrariRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IstoricLivrariRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;

@DomainRepository
public interface IstoricLivrariRepo {
    IstoricLivrariVO obtineIstoric(IdFurnizor furnizorId, CodMaterial material);
}
```

### 49. 📄 [`IstoricLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/IstoricLivrariVO.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IstoricLivrariVO.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ValueObject
public record IstoricLivrariVO(List<Integer> leadTimeZile) implements Serializable {
    public IstoricLivrariVO {
        if (leadTimeZile == null) {
            leadTimeZile = Collections.emptyList();
        }
    }
}
```

### 50. 📄 [`LinieComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LinieComanda.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `LinieComanda.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public class LinieComanda implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate", nullable = false))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "unitate_masura", nullable = false))
    private Cantitate cantitate;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "pret_unitar", nullable = false))
    @AttributeOverride(name = "moneda", column = @Column(name = "moneda", nullable = false))
    private PretUnitar pretUnitar;

    protected LinieComanda() {}

    public LinieComanda(CodMaterial material, Cantitate cantitate, PretUnitar pretUnitar) {
        this.material = Objects.requireNonNull(material, "material cannot be null");
        this.cantitate = Objects.requireNonNull(cantitate, "cantitate cannot be null");
        this.pretUnitar = Objects.requireNonNull(pretUnitar, "pretUnitar cannot be null");
    }

    // ... [restul de 28 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 51. 📄 [`Lot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Lot.java)
* **Rol Arhitectural (Sprint 2)**: `Component Entity (Entitate Componentă DDD)`
* **Descriere de Business & Invarianți**: Entitate componenta a agregatului ComandaAprovizionare. Metodele de mutatie sunt exclusiv package-private (~) conform Tabelelor 2.1 din Project_OOAD.docx.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainEntity;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entitate componenta a agregatului ComandaAprovizionare.
 * Metodele de mutatie sunt exclusiv package-private (~) conform Tabelelor 2.1 din Project_OOAD.docx.
 */
@DomainEntity
@Entity
@Table(name = "comenzi_loturi")
public class Lot implements Serializable {

    @NotNull
    @EmbeddedId
    private IdLot idLot;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "cod_lot", nullable = false, length = 100)
    private String codLot;

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "unitate_masura", nullable = false, length = 20))
    private Cantitate cantitate;

    // ... [restul de 131 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 52. 📄 [`LotRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/LotRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Repository read-only pentru Loturi (Figura 10). Mutațiile trec exclusiv prin ComandaAprovizionare.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository read-only pentru Loturi (Figura 10). Mutațiile trec exclusiv prin ComandaAprovizionare.
 */
@DomainRepository
public interface LotRepo {
    Optional<Lot> findById(IdLot id);
    List<Lot> findAllByComandaId(IdComanda id);
}
```

### 53. 📄 [`MedieMobilaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MedieMobilaStrategy.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `MedieMobilaStrategy.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.List;

public class MedieMobilaStrategy implements IMetodaEstimareLeadTime {
    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = istoric.leadTimeZile();
        if (zile == null || zile.isEmpty()) return 14;
        double avg = zile.stream().mapToInt(Integer::intValue).average().orElse(14.0);
        return (int) Math.round(avg);
    }
}
```

### 54. 📄 [`MetriciLivrareVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/MetriciLivrareVO.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `MetriciLivrareVO.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
public record MetriciLivrareVO(
    IdFurnizor furnizorId,
    CodMaterial material,
    LocalDate dataComanda,
    LocalDate dataLivrarePromisa
) implements Serializable {
    public MetriciLivrareVO {
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(material, "material cannot be null");
        Objects.requireNonNull(dataComanda, "dataComanda cannot be null");
    }
}
```

### 55. 📄 [`NetezireExponentialaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/NetezireExponentialaStrategy.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `NetezireExponentialaStrategy.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.List;

public class NetezireExponentialaStrategy implements IMetodaEstimareLeadTime {
    private final double alpha;

    public NetezireExponentialaStrategy() {
        this(0.3);
    }

    public NetezireExponentialaStrategy(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = istoric.leadTimeZile();
        if (zile == null || zile.isEmpty()) return 14;
        double s = zile.get(0);
        for (int i = 1; i < zile.size(); i++) {
            s = alpha * zile.get(i) + (1.0 - alpha) * s;
        }
        return (int) Math.round(s);
    }
}
```

### 56. 📄 [`Percentila90Strategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/Percentila90Strategy.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Percentila90Strategy.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Percentila90Strategy implements IMetodaEstimareLeadTime {
    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = new ArrayList<>(istoric.leadTimeZile());
        if (zile.isEmpty()) return 14;
        Collections.sort(zile);
        int index = (int) Math.ceil(0.90 * zile.size()) - 1;
        if (index < 0) index = 0;
        return zile.get(index);
    }
}
```

### 57. 📄 [`PretUnitar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/PretUnitar.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `PretUnitar.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@ValueObject
@Embeddable
public final class PretUnitar implements Serializable {
    private BigDecimal valoare;
    private Currency moneda;

    protected PretUnitar() {}

    public PretUnitar(BigDecimal valoare, Currency moneda) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("PretUnitar cannot be null or negative");
        }
        this.valoare = valoare;
        this.moneda = Objects.requireNonNull(moneda, "Moneda cannot be null");
    }

    public static PretUnitar of(BigDecimal valoare, String currencyCode) {
        return new PretUnitar(valoare, Currency.getInstance(currencyCode));
    }

    public static PretUnitar of(double valoare, String currencyCode) {
        return new PretUnitar(BigDecimal.valueOf(valoare), Currency.getInstance(currencyCode));
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    public Currency getMoneda() {
        return moneda;
    }

    @Override
    // ... [restul de 16 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 58. 📄 [`RezultatCalitateLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/RezultatCalitateLot.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `RezultatCalitateLot.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum RezultatCalitateLot {
    Conform,
    Neconform,
    Scutit
}
```

### 59. 📄 [`StareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareComanda.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareComanda.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareComanda {
    Solicitat,
    Confirmat,
    Refuzat,
    InTranzit,
    ReceptionatPartial,
    Receptionat,
    Anulat
}
```

### 60. 📄 [`StareLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/StareLot.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareLot.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareLot {
    Planificat,
    Expediat,
    SositInDepozit,
    InCarantina,
    EliberatInStoc,
    Respins,
    Returnat,
    Anulat
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Serviciul de calcul matematic al valorilor comenzilor (total net, discount de volum, taxe) și proiecțiile sumare.

### 61. 📄 [`ComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Implementare a Serviciului de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003). Respecta izolarea pura de domeniu (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Implementare a Serviciului de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainComputationService
public class ComandaComputationService implements IComandaComputationService {

    @Override
    public BigDecimal calculSubtotal(ComandaAprovizionare comanda) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        BigDecimal subtotal = BigDecimal.ZERO;
        if (comanda.getLinii() != null) {
            for (LinieComanda linie : comanda.getLinii()) {
                subtotal = subtotal.add(linie.valoareLinie());
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA) {
        Objects.requireNonNull(cotaTVA, "Cota TVA nu poate fi null!");
        BigDecimal subtotal = calculSubtotal(comanda);
        return subtotal.multiply(cotaTVA).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculTotalCuTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA) {
        BigDecimal subtotal = calculSubtotal(comanda);
        BigDecimal tva = calculTVA(comanda, cotaTVA);
        return subtotal.add(tva).setScale(2, RoundingMode.HALF_UP);
    }
    // ... [restul de 54 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 62. 📄 [`ComandaSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/ComandaSummaryView.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Entity View Computation: Proiectie sumarizata pentru comanda de aprovizionare (Sprint 2 - ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity View Computation: Proiectie sumarizata pentru comanda de aprovizionare (Sprint 2 - ADR-003).
 */
@ValueObject
public record ComandaSummaryView(
    IdComanda id,
    String numarComanda,
    BigDecimal subtotal,
    BigDecimal tva,
    BigDecimal totalCuTVA,
    double procentLivrare,
    int numarLinii,
    boolean esteLivrataComplet
) implements Serializable {}
```

### 63. 📄 [`IComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/computation/IComandaComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003). Trateaza Entity Field Computations (totaluri, TVA, procent livrare)
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003).
 * Trateaza Entity Field Computations (totaluri, TVA, procent livrare)
 * si Entity View Computations (ComandaSummaryView).
 */
@DomainComputationService
public interface IComandaComputationService {

    /**
     * Calculeaza subtotalul comenzii (fara TVA) prin agregarea liniilor.
     */
    BigDecimal calculSubtotal(ComandaAprovizionare comanda);

    /**
     * Calculeaza TVA-ul aplicabil comenzii in baza cotei furnizate (ex: 0.19 pentru 19%).
     */
    BigDecimal calculTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA);

    /**
     * Calculeaza valoarea totala a comenzii incluzand TVA.
     */
    BigDecimal calculTotalCuTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA);

    /**
     * Calculeaza procentul de acoperire/livrare a marfurilor (0.0 - 100.0%).
     */
    double calculProcentLivrare(ComandaAprovizionare comanda);

    /**
     * Genereaza o proiectie sumarizata (Entity View Computation) a comenzii.
     */
    ComandaSummaryView genereazaSumar(ComandaAprovizionare comanda, BigDecimal cotaTVA);
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la crearea, confirmarea sau anularea în cascadă a comenzilor de aprovizionare.

### 64. 📄 [`ComandaAnulataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaAnulataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComandaAnulataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ComandaAnulataEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    List<IdLot> loturiAnulate
) implements DomainEvent {}
```

### 65. 📄 [`ComandaNeconfirmataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaNeconfirmataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComandaNeconfirmataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.time.Instant;
import java.util.UUID;

public record ComandaNeconfirmataEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId
) implements DomainEvent {}
```

### 66. 📄 [`ComandaReceptionataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/ComandaReceptionataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComandaReceptionataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ComandaReceptionataEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    IdLot lotId,
    CodMaterial material,
    BigDecimal cantitatePrimita,
    String unitateMasura,
    boolean receptieTotala
) implements DomainEvent {}
```

### 67. 📄 [`EstimareDepasesteCerereaEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/EstimareDepasesteCerereaEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `EstimareDepasesteCerereaEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EstimareDepasesteCerereaEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    LocalDate dataCeruta,
    LocalDate dataEstimata
) implements DomainEvent {}
```

### 68. 📄 [`VerdictNeaplicabilEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/event/VerdictNeaplicabilEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `VerdictNeaplicabilEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.UUID;

public record VerdictNeaplicabilEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    IdLot lotId,
    String motiv
) implements DomainEvent {}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Fabrica agregatului ComandaAprovizionare ce validează precondițiile și asamblează instanța inițială.

### 69. 📄 [`ComandaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/factory/ComandaFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Factory (Fabrică de Agregate DDD)`
* **Descriere de Business & Invarianți**: Aggregate Factory conform Sprint 2 (ADR-003). Garanteaza initializarea consistenta a agregatului ComandaAprovizionare.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Factory conform Sprint 2 (ADR-003).
 * Garanteaza initializarea consistenta a agregatului ComandaAprovizionare.
 */
@DomainFactory
public class ComandaFactory {

    public static ComandaAprovizionare creeaza(String numarComanda,
                                               IdFurnizor furnizorId,
                                               UUID cumparatorId,
                                               LocalDate dataComanda,
                                               LocalDate dataLivrareCeruta,
                                               String depozit,
                                               List<LinieComanda> linii) {
        Objects.requireNonNull(numarComanda, "numarComanda cannot be null");
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(cumparatorId, "cumparatorId cannot be null");
        Objects.requireNonNull(dataComanda, "dataComanda cannot be null");
        Objects.requireNonNull(dataLivrareCeruta, "dataLivrareCeruta cannot be null");
        Objects.requireNonNull(depozit, "depozit cannot be null");
        if (linii == null || linii.isEmpty()) {
            throw new IllegalArgumentException("O comanda trebuie sa aiba cel putin o linie!");
        }
        if (dataLivrareCeruta.isBefore(dataComanda)) {
            throw new IllegalArgumentException("Data de livrare ceruta nu poate fi anterioara datei comenzii!");
        }

        IdComanda id = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
    // ... [restul de 6 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Politici de aprobare a comenzilor în funcție de pragurile valorice contractuale.

### 70. 📄 [`PoliticaAprobareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/policy/PoliticaAprobareComanda.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Policy (Politică de Business DDD)`
* **Descriere de Business & Invarianți**: Politica de business pentru stabilirea pragurilor si nivelelor de aprobare a comenzilor de aprovizionare. Conform regulilor de business (Other Business Rules - ADR-003 / Project_OOAD):
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Politica de business pentru stabilirea pragurilor si nivelelor de aprobare a comenzilor de aprovizionare.
 * Conform regulilor de business (Other Business Rules - ADR-003 / Project_OOAD):
 * - Sub 10,000 RON: Responsabil Achizitii (aprobare operationala)
 * - 10,000 - 50,000 RON: Manager Departament
 * - Peste 50,000 RON: Director General / Consiliu
 */
@DomainPolicy
public class PoliticaAprobareComanda {

    public static final BigDecimal PRAG_MANAGER = BigDecimal.valueOf(10000.00);
    public static final BigDecimal PRAG_DIRECTOR = BigDecimal.valueOf(50000.00);

    public enum NivelAprobare {
        RESPONSABIL_ACHIZITII(1),
        MANAGER_DEPARTAMENT(2),
        DIRECTOR_GENERAL(3);

        private final int rang;

        NivelAprobare(int rang) {
            this.rang = rang;
        }

        public int getRang() {
            return rang;
        }
    }

    public NivelAprobare determinaNivelNecesar(BigDecimal valoareTotala) {
        Objects.requireNonNull(valoareTotala, "valoareTotala nu poate fi null");
        if (valoareTotala.compareTo(PRAG_DIRECTOR) > 0) {
            return NivelAprobare.DIRECTOR_GENERAL;
        }
    // ... [restul de 17 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Servicii de validare a eligibilității plasării comenzilor (furnizor activ, linii valide, articole distincte).

### 71. 📄 [`ComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/ComandaValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Implementare Domain Validation Service pentru Comenzi (Sprint 2 - ADR-003). Respecta izolarea pura de domeniu (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Comenzi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainValidationService
public class ComandaValidationService implements IComandaValidationService {

    @Override
    public void valideazaComanda(ComandaAprovizionare comanda, boolean furnizorEligibil) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");

        if (!furnizorEligibil) {
            throw new DomainValidationException("FURNIZOR_NEELIGIBIL",
                "Furnizorul asociat comenzii " + comanda.getNumarComanda() + " nu este eligibil/aprobat pentru plasarea de comenzi!");
        }

        if (comanda.getDataLivrareCeruta().isBefore(comanda.getDataComanda())) {
            throw new DomainValidationException("DATA_LIVRARE_INVALIDA",
                "Data de livrare ceruta (" + comanda.getDataLivrareCeruta() + ") nu poate fi anterioara datei comenzii (" + comanda.getDataComanda() + ")!");
        }

        if (comanda.getLinii() == null || comanda.getLinii().isEmpty()) {
            throw new DomainValidationException("LINII_COMANDA_GOALE",
                "Comanda " + comanda.getNumarComanda() + " trebuie sa contina cel putin o linie de comanda!");
        }

        for (LinieComanda linie : comanda.getLinii()) {
            valideazaLinie(linie);
        }
    }

    // ... [restul de 68 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 72. 📄 [`IComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/domain/validation/IComandaValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Validare a Domeniului Comenzi (ADR-003). Trateaza validari complexe, cross-entity si cross-aggregate (Furnizor - Comanda).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Validare a Domeniului Comenzi (ADR-003).
 * Trateaza validari complexe, cross-entity si cross-aggregate (Furnizor - Comanda).
 */
@DomainValidationService
public interface IComandaValidationService {

    /**
     * Valideaza consistenta integrala a comenzii de aprovizionare si eligibilitatea furnizorului.
     */
    void valideazaComanda(ComandaAprovizionare comanda, boolean furnizorEligibil);

    /**
     * Valideaza linia individuala de comanda (cantitate > 0, pret > 0).
     */
    void valideazaLinie(LinieComanda linie);

    /**
     * Valideaza permisiunea tranzitiei catre o stare tinta.
     */
    void valideazaTranzitieStare(ComandaAprovizionare comanda, StareComanda stareTinta);

    /**
     * Valideaza operatiunea de receptie de marfa asociata comenzii.
     */
    void valideazaReceptie(ComandaAprovizionare comanda, BigDecimal cantitateReceptieTotala);
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Comenzi`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce asamblează serviciile pure de domeniu în containerul IoC fără a polua clasele de business.

### 73. 📄 [`ComenziDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/comenzi/infrastructure/configuration/ComenziDomainConfig.java)
* **Rol Arhitectural (Sprint 2)**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComenziDomainConfig.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLeadTimeService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.IComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;

@Configuration
public class ComenziDomainConfig {

    @Bean
    public EstimareLeadTimeService estimareLeadTimeService() {
        return new EstimareLeadTimeService();
    }

    @Bean
    public IComandaValidationService comandaValidationService() {
        return new ComandaValidationService();
    }

    @Bean
    public IComandaComputationService comandaComputationService() {
        return new ComandaComputationService();
    }

    @Bean
    public PoliticaAprobareComanda politicaAprobareComanda() {
        return new PoliticaAprobareComanda();
    }
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Agregatele Furnizor, InspectieCalitate, InspectorCalitate, RaportNeconformitate (NCR), entitatea ScorecardLunar, VOs și porturile de repository.

### 74. 📄 [`ActiuneCorectiva.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ActiuneCorectiva.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ActiuneCorectiva.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class ActiuneCorectiva implements Serializable {

    @NotBlank
    @Column(name = "descriere_actiune")
    private String descriere;

    @NotBlank
    @Column(name = "responsabil", length = 100)
    private String responsabil;

    @NotNull
    @Column(name = "termen")
    private LocalDate termen;

    protected ActiuneCorectiva() {}

    public ActiuneCorectiva(String descriere, String responsabil, LocalDate termen) {
        if (descriere == null || descriere.trim().isEmpty()) {
            throw new IllegalArgumentException("Descriere cannot be null or empty");
        }
        if (responsabil == null || responsabil.trim().isEmpty()) {
            throw new IllegalArgumentException("Responsabil cannot be null or empty");
        }
        this.descriere = descriere.trim();
        this.responsabil = responsabil.trim();
        this.termen = Objects.requireNonNull(termen, "termen cannot be null");
    }

    public String getDescriere() { return descriere; }
    // ... [restul de 22 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 75. 📄 [`Adresa.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Adresa.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Adresa.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Adresa implements Serializable {

    @NotBlank
    @Column(name = "strada")
    private String strada;

    @NotBlank
    @Column(name = "numar")
    private String numar;

    @NotBlank
    @Column(name = "localitate")
    private String localitate;

    @NotBlank
    @Column(name = "cod_postal")
    private String codPostal;

    protected Adresa() {}

    public Adresa(String strada, String numar, String localitate, String codPostal) {
        if (strada == null || strada.trim().isEmpty()) {
            throw new IllegalArgumentException("Strada cannot be null or empty");
        }
        if (numar == null || numar.trim().isEmpty()) {
            throw new IllegalArgumentException("Numar cannot be null or empty");
        }
        if (localitate == null || localitate.trim().isEmpty()) {
            throw new IllegalArgumentException("Localitate cannot be null or empty");
        }
        if (codPostal == null || codPostal.trim().isEmpty()) {
    // ... [restul de 33 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 76. 📄 [`AlocareInspectorService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/AlocareInspectorService.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Domain Service pentru validarea si alocarea inspectorului de calitate conform disponibilitatii si certificarii (Figura 20).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainService;

import java.util.Objects;

/**
 * Domain Service pentru validarea si alocarea inspectorului de calitate conform disponibilitatii si certificarii (Figura 20).
 */
@DomainService
public class AlocareInspectorService {

    public void aloca(InspectieCalitate inspectie, InspectorCalitate inspector) {
        Objects.requireNonNull(inspectie, "inspectie cannot be null");
        Objects.requireNonNull(inspector, "inspector cannot be null");

        if (!inspector.poateFiAlocat(inspectie.getPerioadaPlanificata())) {
            throw new IllegalStateException("Inspectorul " + inspector.getFullName() + " nu are certificare valida sau nu este disponibil in perioada planificata");
        }
        inspectie.aloca(inspector.getIdInspector());
    }
}
```

### 77. 📄 [`CalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/CalculScorecardService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Domain Service pentru calculul scorecardului lunar si indicatorilor de calitate (Figura 20). Pure Domain: fara adnotari de Spring (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Domain Service pentru calculul scorecardului lunar si indicatorilor de calitate (Figura 20).
 * Pure Domain: fara adnotari de Spring (ADR-011).
 */
@DomainComputationService
public class CalculScorecardService implements ICalculScorecardService {

    @Override
    public ScorecardLunar calculeazaScorecard(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere) {
        return ScorecardLunar.calculeaza(luna, perf, rataRespingere);
    }

    @Override
    public CalitateDashboardView calculeazaDashboard(List<Furnizor> furnizori, List<InspectieCalitate> inspectii) {
        int totalFurnizori = furnizori != null ? furnizori.size() : 0;
        int furnizoriAprobati = 0;
        int furnizoriBlocati = 0;

        if (furnizori != null) {
            for (Furnizor f : furnizori) {
                if (f.getStareFurnizor() == StareFurnizor.Aprobat) furnizoriAprobati++;
                if (f.getStareFurnizor() == StareFurnizor.Blocat) furnizoriBlocati++;
            }
        }

        int totalInspectii = inspectii != null ? inspectii.size() : 0;
        int inspectiiAdmise = 0;
        int inspectiiRespinse = 0;

        if (inspectii != null) {
            for (InspectieCalitate insp : inspectii) {
    // ... [restul de 25 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 78. 📄 [`Calificativ.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Calificativ.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Calificativ.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum Calificativ {
    A,
    B,
    C,
    D
}
```

### 79. 📄 [`Certificare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Certificare.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Certificare.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Certificare implements Serializable {

    @NotBlank
    @Column(name = "denumire", nullable = false)
    private String denumire;

    @NotNull
    @Column(name = "data_expirare", nullable = false)
    private LocalDate dataExpirare;

    protected Certificare() {}

    public Certificare(String denumire, LocalDate dataExpirare) {
        if (denumire == null || denumire.trim().isEmpty()) {
            throw new IllegalArgumentException("Denumire cannot be null or empty");
        }
        this.denumire = denumire.trim();
        this.dataExpirare = Objects.requireNonNull(dataExpirare, "dataExpirare cannot be null");
    }

    public boolean esteValidaPe(Perioada p) {
        Objects.requireNonNull(p, "perioada cannot be null");
        return !dataExpirare.isBefore(p.getDataFinalizare());
    }

    public boolean esteExpirataIn(LocalDate data) {
        return dataExpirare.isBefore(data);
    }
    // ... [restul de 22 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 80. 📄 [`ConditiiContractuale.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ConditiiContractuale.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ConditiiContractuale.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ValueObject
@Embeddable
public class ConditiiContractuale implements Serializable {

    @Min(0)
    @Column(name = "termen_plata_zile")
    private int termenPlataZile;

    @NotNull
    @Column(name = "moneda", length = 3)
    private Currency moneda;

    @Transient
    private Map<CodMaterial, BigDecimal> preturiNegociate = new HashMap<>();

    protected ConditiiContractuale() {}

    public ConditiiContractuale(int termenPlataZile, Currency moneda, Map<CodMaterial, BigDecimal> preturiNegociate) {
        if (termenPlataZile < 0) {
            throw new IllegalArgumentException("Termenul de plata nu poate fi negativ");
        }
        this.termenPlataZile = termenPlataZile;
        this.moneda = Objects.requireNonNull(moneda, "moneda cannot be null");
        if (preturiNegociate != null) {
            this.preturiNegociate.putAll(preturiNegociate);
    // ... [restul de 26 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 81. 📄 [`Email.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Email.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Email.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Email implements Serializable {
    private String email;

    protected Email() {}

    public Email(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Adresa de email invalida: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public static boolean isValidEmail(String e) {
        return e != null && e.contains("@") && e.contains(".");
    }

    public static Email of(String e) {
        return new Email(e);
    }

    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email1)) return false;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() { return Objects.hash(email); }

    @Override
    // ... [restul de 2 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 82. 📄 [`FullName.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FullName.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FullName.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class FullName implements Serializable {

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    protected FullName() {}

    public FullName(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullName fullName)) return false;
        return Objects.equals(firstName, fullName.firstName) &&
    // ... [restul de 13 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 83. 📄 [`Furnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Furnizor.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Furnizor.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorSuspendatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@AggregateRoot
@Entity
@Table(name = "calitate_furnizori")
public class Furnizor implements Serializable {

    @NotNull
    @EmbeddedId
    private IdFurnizor id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "cod_fiscal", nullable = false, unique = true, length = 50)
    private String codFiscal;

    @NotBlank
    @Size(min = 2, max = 255)
    @Column(name = "denumire", nullable = false, length = 255)
    private String denumire;

    @Embedded
    private Adresa adresa;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "calitate_furnizori_emailuri", joinColumns = @JoinColumn(name = "furnizor_id"))
    // ... [restul de 179 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 84. 📄 [`FurnizorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/FurnizorRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FurnizorRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;

@DomainRepository
public interface FurnizorRepo {
    void add(Furnizor f);
    Optional<Furnizor> findById(IdFurnizor id);
    void save(Furnizor f);
    List<Furnizor> findAll();
}
```

### 85. 📄 [`IPerformantaLivrariAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/IPerformantaLivrariAdapter.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IPerformantaLivrariAdapter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

public interface IPerformantaLivrariAdapter {
    PerformantaLivrariVO obtinePerformanta(IdFurnizor furnizorId, Perioada luna);
}
```

### 86. 📄 [`Iban.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Iban.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Iban.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Iban implements Serializable {

    @NotBlank
    @Column(name = "valoare", nullable = false)
    private String valoare;

    protected Iban() {}

    public Iban(String valoare) {
        if (!esteValid(valoare)) {
            throw new IllegalArgumentException("IBAN invalid: " + valoare);
        }
        this.valoare = valoare.replaceAll("\\s+", "").toUpperCase();
    }

    public static boolean esteValid(String v) {
        if (v == null) return false;
        String clean = v.replaceAll("\\s+", "").toUpperCase();
        return clean.length() >= 15 && clean.length() <= 34 && Character.isLetter(clean.charAt(0)) && Character.isLetter(clean.charAt(1));
    }

    public static Iban of(String v) {
        return new Iban(v);
    }

    public String getValoare() { return valoare; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban iban)) return false;
    // ... [restul de 11 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 87. 📄 [`InspectieCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieCalitate.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Aggregate Root pentru inspectia de calitate AQL (Figura 20 si 23).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.InspectieProgramataEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.VerdictInspectieEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root pentru inspectia de calitate AQL (Figura 20 si 23).
 */
@AggregateRoot
@Entity
@Table(name = "calitate_inspectii")
public class InspectieCalitate implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_inspectie", nullable = false)
    private UUID idInspectie;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    private IdLot lotId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id", nullable = false))
    private IdComanda comandaId;

    @NotNull
    // ... [restul de 133 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 88. 📄 [`InspectieRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectieRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InspectieRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface InspectieRepo {
    void add(InspectieCalitate i);
    Optional<InspectieCalitate> findById(UUID id);
    void save(InspectieCalitate i);
    List<InspectieCalitate> findAll();
}
```

### 89. 📄 [`InspectorCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorCalitate.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InspectorCalitate.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "calitate_inspectori")
public class InspectorCalitate implements Serializable {

    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private UUID idInspector;

    @NotNull
    @Embedded
    private FullName fullName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "calitate_inspectori_certificari", joinColumns = @JoinColumn(name = "inspector_id"))
    private List<Certificare> certificari = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "calitate_inspectori_disponibilitate", joinColumns = @JoinColumn(name = "inspector_id"))
    @AttributeOverride(name = "dataInceput", column = @Column(name = "data_inceput", nullable = false))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "data_finalizare", nullable = false))
    private List<Perioada> disponibilitate = new ArrayList<>();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    protected InspectorCalitate() {}
    // ... [restul de 43 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 90. 📄 [`InspectorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/InspectorRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InspectorRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface InspectorRepo {
    void add(InspectorCalitate i);
    Optional<InspectorCalitate> findById(UUID id);
    void save(InspectorCalitate i);
    List<InspectorCalitate> findAll();
}
```

### 91. 📄 [`NCRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/NCRRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `NCRRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface NCRRepo {
    void add(RaportNeconformitate r);
    Optional<RaportNeconformitate> findById(UUID id);
    void save(RaportNeconformitate r);
    List<RaportNeconformitate> findAll();
}
```

### 92. 📄 [`PerformantaLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PerformantaLivrariVO.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `PerformantaLivrariVO.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public record PerformantaLivrariVO(
    IdFurnizor furnizorId,
    Perioada luna,
    BigDecimal otif,
    BigDecimal conformitatePret
) implements Serializable {
    public PerformantaLivrariVO {
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(luna, "luna cannot be null");
        Objects.requireNonNull(otif, "otif cannot be null");
        Objects.requireNonNull(conformitatePret, "conformitatePret cannot be null");
    }
}
```

### 93. 📄 [`PlanAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PlanAQL.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Value Object pentru planul de esantionare conform ISO 2859-1 (Tabelul 2.2). Invariant strict: numarRespingere = numarAcceptare + 1.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object pentru planul de esantionare conform ISO 2859-1 (Tabelul 2.2).
 * Invariant strict: numarRespingere = numarAcceptare + 1.
 */
@ValueObject
@Embeddable
public final class PlanAQL implements Serializable {

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "aql_nivel", nullable = false)
    private BigDecimal nivelAQL;

    @Min(1)
    @Column(name = "aql_dimensiune_esantion", nullable = false)
    private int dimensiuneEsantion;

    @Min(0)
    @Column(name = "aql_numar_acceptare", nullable = false)
    private int numarAcceptare;

    @Min(1)
    @Column(name = "aql_numar_respingere", nullable = false)
    private int numarRespingere;

    protected PlanAQL() {}

    public PlanAQL(BigDecimal nivelAQL, int dimensiuneEsantion, int numarAcceptare, int numarRespingere) {
        if (numarRespingere != numarAcceptare + 1) {
            throw new IllegalArgumentException("Invariant ISO 2859-1 incalcat: numarRespingere (" + numarRespingere + ") trebuie sa fie numarAcceptare (" + numarAcceptare + ") + 1");
    // ... [restul de 47 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 94. 📄 [`PretNegociat.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/PretNegociat.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `PretNegociat.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public final class PretNegociat implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @PositiveOrZero
    @Column(name = "pret", nullable = false, precision = 18, scale = 4)
    private BigDecimal pret;

    protected PretNegociat() {}

    public PretNegociat(CodMaterial material, BigDecimal pret) {
        this.material = Objects.requireNonNull(material, "material cannot be null");
        this.pret = Objects.requireNonNull(pret, "pret cannot be null");
        if (pret.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Pret cannot be negative");
        }
    }

    public CodMaterial getMaterial() {
        return material;
    }
    // ... [restul de 23 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 95. 📄 [`RaportNeconformitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/RaportNeconformitate.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `RaportNeconformitate.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.NCREscaladatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "calitate_ncr")
public class RaportNeconformitate implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_ncr", nullable = false)
    private UUID idNCR;

    @NotNull
    @Column(name = "inspectie_id", nullable = false)
    private UUID inspectieId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id", nullable = false))
    private IdFurnizor furnizorId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    private IdLot lotId;

    @Column(name = "cauza_radacina")
    private String cauzaRadacina;

    @Embedded
    // ... [restul de 73 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 96. 📄 [`Scor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Scor.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Value Object pentru scorul lunar ponderat conform cerintelor OOAD: 40% OTIF + 40% Calitate (100 - rata respingere) + 20% Pret.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object pentru scorul lunar ponderat conform cerintelor OOAD:
 * 40% OTIF + 40% Calitate (100 - rata respingere) + 20% Pret.
 */
@ValueObject
@Embeddable
public final class Scor implements Serializable {
    private BigDecimal valoare;

    protected Scor() {}

    public Scor(BigDecimal valoare) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0 || valoare.compareTo(BigDecimal.valueOf(100.0)) > 0) {
            throw new IllegalArgumentException("Scorul trebuie sa fie intre 0 si 100. Valoare: " + valoare);
        }
        this.valoare = valoare.setScale(2, RoundingMode.HALF_UP);
    }

    public static Scor ponderat(BigDecimal otif, BigDecimal calitate, BigDecimal pret) {
        Objects.requireNonNull(otif, "otif cannot be null");
        Objects.requireNonNull(calitate, "calitate cannot be null");
        Objects.requireNonNull(pret, "pret cannot be null");

        // 40% OTIF + 40% Calitate + 20% Pret
        BigDecimal v = otif.multiply(BigDecimal.valueOf(0.40))
            .add(calitate.multiply(BigDecimal.valueOf(0.40)))
            .add(pret.multiply(BigDecimal.valueOf(0.20)));
        return new Scor(v);
    }

    public Calificativ calificativ() {
        if (valoare.compareTo(BigDecimal.valueOf(90.0)) >= 0) return Calificativ.A;
        if (valoare.compareTo(BigDecimal.valueOf(80.0)) >= 0) return Calificativ.B;
    // ... [restul de 25 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 97. 📄 [`ScorecardLunar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/ScorecardLunar.java)
* **Rol Arhitectural (Sprint 2)**: `Component Entity (Entitate Componentă DDD)`
* **Descriere de Business & Invarianți**: Entitate componenta a agregatului Furnizor.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entitate componenta a agregatului Furnizor.
 */
@DomainEntity
@Entity
@Table(name = "calitate_scorecards")
public class ScorecardLunar implements Serializable {

    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Embedded
    @AttributeOverride(name = "dataInceput", column = @Column(name = "data_inceput", nullable = false))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "data_finalizare", nullable = false))
    private Perioada luna;

    @NotNull
    @Column(name = "otif", nullable = false, precision = 5, scale = 2)
    private BigDecimal otif;

    @NotNull
    @Column(name = "rata_respingere", nullable = false, precision = 5, scale = 2)
    private BigDecimal rataRespingere;

    @NotNull
    @Column(name = "conformitate_pret", nullable = false, precision = 5, scale = 2)
    private BigDecimal conformitatePret;

    // ... [restul de 51 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 98. 📄 [`StareFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareFurnizor.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareFurnizor.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareFurnizor {
    Candidat,
    InEvaluare,
    Aprobat,
    Conditionat,
    Suspendat,
    Blocat
}
```

### 99. 📄 [`StareInspectie.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareInspectie.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareInspectie.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareInspectie {
    Programata,
    InspectorAlocat,
    InDesfasurare,
    Conforma,
    Neconforma,
    InReinspectie,
    Inchisa,
    Anulata
}
```

### 100. 📄 [`StareNCR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/StareNCR.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareNCR.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareNCR {
    Deschis,
    InAnalizaCauzei,
    ActiuneCorectivaInCurs,
    Verificat,
    Inchis,
    Escaladat
}
```

### 101. 📄 [`Verdict.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/Verdict.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Verdict.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum Verdict {
    Conforma,
    Neconforma
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Serviciul de calcul al formulei lunare de performanță (OTIF) și DTO-urile de dashboard calitativ.

### 102. 📄 [`CalitateDashboardView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/CalitateDashboardView.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Entity View Computation: Proiectie dashboard pentru calitatea furnizorilor si inspectii (Sprint 2 - ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;

/**
 * Entity View Computation: Proiectie dashboard pentru calitatea furnizorilor si inspectii (Sprint 2 - ADR-003).
 */
@ValueObject
public record CalitateDashboardView(
    int totalFurnizori,
    int furnizoriAprobati,
    int furnizoriBlocati,
    int totalInspectii,
    int inspectiiAdmise,
    int inspectiiRespinse,
    double rataConformitateLoturi
) implements Serializable {}
```

### 103. 📄 [`ICalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/computation/ICalculScorecardService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Calcul Calitate si Scorecard Furnizori (Sprint 2 - ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PerformantaLivrariVO;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contract pentru Serviciul de Calcul Calitate si Scorecard Furnizori (Sprint 2 - ADR-003).
 */
@DomainComputationService
public interface ICalculScorecardService {

    /**
     * Entity Field Computation: Calculeaza scorecardul lunar al furnizorului.
     */
    ScorecardLunar calculeazaScorecard(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere);

    /**
     * Entity View Computation: Agrega indicatorii globali de calitate intr-un dashboard.
     */
    CalitateDashboardView calculeazaDashboard(List<Furnizor> furnizori, List<InspectieCalitate> inspectii);
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la înregistrarea furnizorilor, finalizarea inspecțiilor sau deschiderea rapoartelor NCR.

### 104. 📄 [`CertificareInspectorExpiraEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/CertificareInspectorExpiraEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `CertificareInspectorExpiraEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertificareInspectorExpiraEvent(
    UUID eventId,
    Instant occurredOn,
    UUID inspectorId,
    String denumireCertificare,
    LocalDate dataExpirare
) implements DomainEvent {}
```

### 105. 📄 [`FurnizorBlocatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorBlocatEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FurnizorBlocatEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.Instant;
import java.util.UUID;

public record FurnizorBlocatEvent(
    UUID eventId,
    Instant occurredOn,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent {}
```

### 106. 📄 [`FurnizorSuspendatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/FurnizorSuspendatEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FurnizorSuspendatEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.Instant;
import java.util.UUID;

public record FurnizorSuspendatEvent(
    UUID eventId,
    Instant occurredOn,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent {}
```

### 107. 📄 [`InspectieProgramataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/InspectieProgramataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `InspectieProgramataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.time.Instant;
import java.util.UUID;

public record InspectieProgramataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID inspectieId,
    IdLot lotId,
    IdComanda comandaId
) implements DomainEvent {}
```

### 108. 📄 [`LotScutitDeInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/LotScutitDeInspectieEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Eveniment emis cand un lot este scutit de inspectie fizica AQL (ex: furnizor de clasa A cu istoric impecabil conform Figurii 1, Tabelului 1.3 si Figurii 20 din Project_OOAD.docx).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.UUID;

/**
 * Eveniment emis cand un lot este scutit de inspectie fizica AQL
 * (ex: furnizor de clasa A cu istoric impecabil conform Figurii 1, Tabelului 1.3 si Figurii 20 din Project_OOAD.docx).
 */
public record LotScutitDeInspectieEvent(
    UUID eventId,
    Instant occurredOn,
    IdLot lotId,
    IdComanda comandaId,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent {}
```

### 109. 📄 [`NCREscaladatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/NCREscaladatEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `NCREscaladatEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.Instant;
import java.util.UUID;

public record NCREscaladatEvent(
    UUID eventId,
    Instant occurredOn,
    UUID ncrId,
    IdFurnizor furnizorId
) implements DomainEvent {}
```

### 110. 📄 [`VerdictInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/event/VerdictInspectieEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `VerdictInspectieEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.time.Instant;
import java.util.UUID;

public record VerdictInspectieEvent(
    UUID eventId,
    Instant occurredOn,
    IdLot lotId,
    IdComanda comandaId,
    Verdict verdict
) implements DomainEvent {}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Fabricile de asamblare și calificare a furnizorilor și a inspecțiilor de lot.

### 111. 📄 [`FurnizorFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/FurnizorFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Factory (Fabrică de Agregate DDD)`
* **Descriere de Business & Invarianți**: Aggregate Factory pentru agregatul Furnizor (Sprint 2 - ADR-003). Garanteaza initializarea consistenta a agregatului in starea initiala Candidat.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Factory pentru agregatul Furnizor (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a agregatului in starea initiala Candidat.
 */
@DomainFactory
public class FurnizorFactory {

    public static Furnizor creeaza(String codFiscal,
                                   String denumire,
                                   Adresa adresa,
                                   Set<Email> emailuri,
                                   Iban iban,
                                   Set<String> categoriiMateriale,
                                   ConditiiContractuale conditii) {
        if (codFiscal == null || codFiscal.trim().isEmpty()) {
            throw new DomainValidationException("COD_FISCAL_INVALID", "Codul fiscal al furnizorului este obligatoriu!");
        }
        if (denumire == null || denumire.trim().isEmpty()) {
            throw new DomainValidationException("DENUMIRE_INVALIDA", "Denumirea furnizorului este obligatorie!");
        }
        if (iban == null) {
            throw new DomainValidationException("IBAN_LIPSA", "Contul IBAN este obligatoriu pentru inregistrarea furnizorului!");
        }
        if (categoriiMateriale == null || categoriiMateriale.isEmpty()) {
            throw new DomainValidationException("CATEGORII_LIPSA", "Furnizorul trebuie sa aiba cel putin o categorie de materiale asociata!");
        }

        IdFurnizor id = IdFurnizor.random();
        return new Furnizor(id, codFiscal.trim(), denumire.trim(), adresa, emailuri, iban, categoriiMateriale, conditii);
    }
}
```

### 112. 📄 [`InspectieCalitateFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/factory/InspectieCalitateFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Factory (Fabrică de Agregate DDD)`
* **Descriere de Business & Invarianți**: Aggregate Factory pentru InspectieCalitate (Sprint 2 - ADR-003). Garanteaza initializarea consistenta a agregatului in starea initiala Programata.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Factory pentru InspectieCalitate (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a agregatului in starea initiala Programata.
 */
@DomainFactory
public class InspectieCalitateFactory {

    public static InspectieCalitate programeaza(IdLot lotId,
                                                IdComanda comandaId,
                                                IdFurnizor furnizorId,
                                                CodMaterial material,
                                                PlanAQL plan,
                                                Perioada perioadaPlanificata) {
        if (lotId == null) {
            throw new DomainValidationException("LOT_LIPSA", "Identificatorul lotului este obligatoriu!");
        }
        if (comandaId == null) {
            throw new DomainValidationException("COMANDA_LIPSA", "Identificatorul comenzii este obligatoriu!");
        }
        if (furnizorId == null) {
            throw new DomainValidationException("FURNIZOR_LIPSA", "Identificatorul furnizorului este obligatoriu!");
        }
        if (material == null) {
            throw new DomainValidationException("MATERIAL_LIPSA", "Materialul inspectat este obligatoriu!");
        }
        if (plan == null) {
            throw new DomainValidationException("PLAN_LIPSA", "Planul AQL este obligatoriu!");
    // ... [restul de 12 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Politici de eșantionare statistică AQL conform standardului internațional ISO 2859-1.

### 113. 📄 [`PoliticaSelectieAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/policy/PoliticaSelectieAQL.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Policy (Politică de Business DDD)`
* **Descriere de Business & Invarianți**: Politica de selectie a regimului de inspectie si planului AQL conform ISO 2859-1 (Other Business Rules - ADR-003). Adapteaza severitatea inspectiei in functie de calificativul furnizorului si marimea lotului.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Politica de selectie a regimului de inspectie si planului AQL conform ISO 2859-1 (Other Business Rules - ADR-003).
 * Adapteaza severitatea inspectiei in functie de calificativul furnizorului si marimea lotului.
 */
@DomainPolicy
public class PoliticaSelectieAQL {

    public enum RegimInspectie {
        REDUS,
        NORMAL,
        SEVER,
        TOTAL_100_PROCENT
    }

    public RegimInspectie determinaRegim(Calificativ calificativ) {
        if (calificativ == null) {
            return RegimInspectie.NORMAL;
        }
        return switch (calificativ) {
            case A -> RegimInspectie.REDUS;
            case B -> RegimInspectie.NORMAL;
            case C -> RegimInspectie.SEVER;
            case D -> RegimInspectie.TOTAL_100_PROCENT;
        };
    }

    public PlanAQL determinaPlan(Calificativ calificativ, int marimeLot) {
        if (marimeLot <= 0) {
            throw new IllegalArgumentException("Marimea lotului trebuie sa fie pozitiva: " + marimeLot);
        }
        RegimInspectie regim = determinaRegim(calificativ);

        int esantion;
    // ... [restul de 37 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Servicii de validare a conformității inspecțiilor, disponibilității inspectorilor și a termenelor contractuale.

### 114. 📄 [`CalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/CalitateValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Implementare Domain Validation Service pentru Calitate (Sprint 2 - ADR-003). Respecta izolarea pura de domeniu (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Calitate (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainValidationService
public class CalitateValidationService implements ICalitateValidationService {

    @Override
    public void valideazaPlanAQL(PlanAQL plan, int marimeLot) {
        Objects.requireNonNull(plan, "Planul AQL nu poate fi null!");

        if (marimeLot <= 0) {
            throw new DomainValidationException("LOT_INVALID", "Marimea lotului supus inspectiei trebuie sa fie strict pozitiva!");
        }

        if (plan.getDimensiuneEsantion() <= 0) {
            throw new DomainValidationException("ESANTION_INVALID", "Marimea esantionului de control trebuie sa fie strict pozitiva!");
        }

        if (plan.getDimensiuneEsantion() > marimeLot) {
            throw new DomainValidationException("ESANTION_DEPASIT",
                "Marimea esantionului (" + plan.getDimensiuneEsantion() + ") nu poate depasi marimea totala a lotului (" + marimeLot + ")!");
        }

        if (plan.getNivelAQL() == null) {
            throw new DomainValidationException("NIVEL_AQL_LIPSA", "Nivelul de severitate AQL este obligatoriu!");
        }
    }

    @Override
    public void valideazaInspectieProgramata(InspectieCalitate inspectie, Perioada valabilitateCertificareInspector) {
    // ... [restul de 35 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 115. 📄 [`ICalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/domain/validation/ICalitateValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Validare a Domeniului Calitate & Furnizori (ADR-003). Trateaza reguli de business pentru inspectii AQL, conformitate furnizori si alocari inspectori.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;

/**
 * Contract pentru Serviciul de Validare a Domeniului Calitate & Furnizori (ADR-003).
 * Trateaza reguli de business pentru inspectii AQL, conformitate furnizori si alocari inspectori.
 */
@DomainValidationService
public interface ICalitateValidationService {

    /**
     * Valideaza planul de esantionare AQL raportat la marimea lotului receptionat.
     */
    void valideazaPlanAQL(PlanAQL plan, int marimeLot);

    /**
     * Valideaza programarea unei inspectii in raport cu certificarea/disponibilitatea inspectorului.
     */
    void valideazaInspectieProgramata(InspectieCalitate inspectie, Perioada valabilitateCertificareInspector);

    /**
     * Valideaza profilul si cerintele minime de conformitate ale unui furnizor.
     */
    void valideazaFurnizor(Furnizor furnizor);
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Calitate`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce instanțiază serviciile și fabricile modulului Calitate.

### 116. 📄 [`CalitateDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/calitate/infrastructure/configuration/CalitateDomainConfig.java)
* **Rol Arhitectural (Sprint 2)**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `CalitateDomainConfig.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.AlocareInspectorService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.CalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy.PoliticaSelectieAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.CalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.ICalitateValidationService;

@Configuration
public class CalitateDomainConfig {

    @Bean
    public CalculScorecardService calculScorecardService() {
        return new CalculScorecardService();
    }

    @Bean
    public AlocareInspectorService alocareInspectorService() {
        return new AlocareInspectorService();
    }

    @Bean
    public ICalitateValidationService calitateValidationService() {
        return new CalitateValidationService();
    }

    @Bean
    public PoliticaSelectieAQL politicaSelectieAQL() {
        return new PoliticaSelectieAQL();
    }
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Agregatele FacturaFurnizor, NIR, OrdinPlata, VOs de reconciliere și toleranță, enums, serviciul ReconciliereService și porturile de repository.

### 117. 📄 [`ComandaSummaryVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ComandaSummaryVO.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComandaSummaryVO.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ValueObject
public record ComandaSummaryVO(
    IdComanda comandaId,
    List<LinieSumar> linii
) implements Serializable {
    public ComandaSummaryVO {
        Objects.requireNonNull(comandaId, "comandaId cannot be null");
        if (linii == null) linii = Collections.emptyList();
    }
}
```

### 118. 📄 [`FacturaFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaFurnizor.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Aggregate Root pentru Factura Furnizor (Figura 21 si 24). Invariant strict: Creeaza OrdinPlata DOAR daca este AprobataLaPlata sau PartialPlatita,
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregate Root pentru Factura Furnizor (Figura 21 si 24).
 * Invariant strict: Creeaza OrdinPlata DOAR daca este AprobataLaPlata sau PartialPlatita,
 * iar suma ordinului <= restDePlata.
 */
@AggregateRoot
@Entity
@Table(name = "facturi_furnizori")
public class FacturaFurnizor implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_factura", nullable = false)
    private UUID idFactura;

    @NotBlank
    @Column(name = "serie", nullable = false, length = 50)
    private String serie;

    @NotBlank
    @Column(name = "numar", nullable = false, length = 50)
    private String numar;

    @NotNull
    @Column(name = "data_emitere", nullable = false)
    private LocalDate dataEmitere;

    // ... [restul de 147 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 119. 📄 [`FacturaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FacturaRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FacturaRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface FacturaRepo {
    void add(FacturaFurnizor f);
    Optional<FacturaFurnizor> findById(UUID id);
    void save(FacturaFurnizor f);
    List<FacturaFurnizor> findAll();
}
```

### 120. 📄 [`FurnizoriBlocatiRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/FurnizoriBlocatiRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FurnizoriBlocatiRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;

@DomainRepository
public interface FurnizoriBlocatiRepo {
    void adauga(IdFurnizor id);
    boolean esteBlocat(IdFurnizor id);
}
```

### 121. 📄 [`IBancaPlataProvider.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IBancaPlataProvider.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IBancaPlataProvider.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

public interface IBancaPlataProvider {
    boolean transmiteOrdinPlata(OrdinPlata ordin, String ibanDestinatar);
}
```

### 122. 📄 [`IComenziAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/IComenziAdapter.java)
* **Rol Arhitectural (Sprint 2)**: `Port de Domeniu / Interfață de Business`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `IComenziAdapter.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

public interface IComenziAdapter {
    ComandaSummaryVO obtineDateComanda(IdComanda id);
}
```

### 123. 📄 [`LinieFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieFactura.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `LinieFactura.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public class LinieFactura implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @DecimalMin(value = "0.01", message = "Cantitatea pe linie factura trebuie sa fie pozitiva")
    @Column(name = "cantitate", nullable = false, precision = 18, scale = 4)
    private BigDecimal cantitate;

    @NotBlank
    @Column(name = "unitate_masura", nullable = false, length = 20)
    private String unitateMasura;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "pret_unitar", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "moneda", column = @Column(name = "moneda", nullable = false, length = 3))
    private Suma pretUnitar;

    @NotNull
    // ... [restul de 53 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 124. 📄 [`LinieNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieNIR.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `LinieNIR.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public class LinieNIR implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    private IdLot idLot;

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @DecimalMin(value = "0.00", message = "Cantitatea receptionata nu poate fi negativa")
    @Column(name = "cantitate_receptionata", nullable = false, precision = 18, scale = 4)
    private BigDecimal cantitateReceptionata;

    @NotBlank
    @Column(name = "unitate_masura", nullable = false, length = 20)
    private String unitateMasura;

    protected LinieNIR() {}

    // ... [restul de 32 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 125. 📄 [`LinieSumar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/LinieSumar.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `LinieSumar.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public record LinieSumar(
    CodMaterial material,
    BigDecimal cantitateComandata,
    BigDecimal pretUnitar
) implements Serializable {
    public LinieSumar {
        Objects.requireNonNull(material, "material cannot be null");
        Objects.requireNonNull(cantitateComandata, "cantitateComandata cannot be null");
        Objects.requireNonNull(pretUnitar, "pretUnitar cannot be null");
    }
}
```

### 126. 📄 [`NIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIR.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `NIR.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@AggregateRoot
@Entity
@Table(name = "facturi_nir")
public class NIR implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_nir", nullable = false)
    private UUID idNIR;

    @NotBlank
    @Column(name = "numar_nir", nullable = false, unique = true, length = 100)
    private String numarNIR;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id", nullable = false))
    private IdComanda comandaId;

    @NotNull
    @Column(name = "event_sursa_id", nullable = false, unique = true)
    private UUID eventSursaId; // Constrangere UNIQUE pentru garantia idempotentei

    @NotNull
    @Column(name = "data_receptie", nullable = false)
    private LocalDate dataReceptie;

    // ... [restul de 85 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 127. 📄 [`NIRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/NIRRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `NIRRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface NIRRepo {
    void add(NIR n);
    Optional<NIR> findById(UUID id);
    void save(NIR n);
    List<NIR> findByComandaId(IdComanda id);
    boolean existaNIRPentruEvent(UUID eventId);
    List<NIR> findAll();
}
```

### 128. 📄 [`OrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlata.java)
* **Rol Arhitectural (Sprint 2)**: `Aggregate Root (Rădăcină de Agregat DDD)`
* **Descriere de Business & Invarianți**: Aggregate Root pentru Ordinul de Plata (Figura 21 si 4.4). Constructorul este exclusiv package-private (~): poate fi instantiat doar de FacturaFurnizor.creeazaOrdinPlata().
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.PlataExecutataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root pentru Ordinul de Plata (Figura 21 si 4.4).
 * Constructorul este exclusiv package-private (~): poate fi instantiat doar de FacturaFurnizor.creeazaOrdinPlata().
 */
@AggregateRoot
@Entity
@Table(name = "facturi_ordine_plata")
public class OrdinPlata implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_ordin", nullable = false)
    private UUID idOrdin;

    @NotNull
    @Column(name = "factura_id", nullable = false)
    private UUID facturaId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "suma", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "moneda", column = @Column(name = "moneda", nullable = false, length = 3))
    private Suma suma;

    @NotNull
    @Column(name = "creat_de", nullable = false)
    private UUID creatDe;

    @Column(name = "aprobat_de")
    private UUID aprobatDe;
    // ... [restul de 83 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 129. 📄 [`OrdinPlataRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/OrdinPlataRepo.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Repository Port (Port de Persistență Hexagonal)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `OrdinPlataRepo.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface OrdinPlataRepo {
    void add(OrdinPlata o);
    Optional<OrdinPlata> findById(UUID id);
    void save(OrdinPlata o);
    List<OrdinPlata> findAll();
}
```

### 130. 📄 [`ReconciliereService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ReconciliereService.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Domain Service pentru reconcilierea in 3 cai (Three-Way Match: Factura vs. Comanda vs. NIR). Pure Domain: fara Spring.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain Service pentru reconcilierea in 3 cai (Three-Way Match: Factura vs. Comanda vs. NIR).
 * Pure Domain: fara Spring.
 */
@DomainService
public class ReconciliereService {

    public RezultatReconciliere reconciliaza(FacturaFurnizor f, NIR nir, ComandaSummaryVO comanda, Toleranta t) {
        Objects.requireNonNull(f, "Factura cannot be null");
        Objects.requireNonNull(nir, "NIR cannot be null");
        Objects.requireNonNull(comanda, "Comanda cannot be null");
        Objects.requireNonNull(t, "Toleranta cannot be null");

        List<String> diferente = new ArrayList<>();

        for (LinieFactura lf : f.getLinii()) {
            // 1. Verificare cantitate facturata vs. NIR
            BigDecimal cantNIR = nir.calculeazaCantitateReceptionata(lf.getMaterial());
            if (!t.acopera(cantNIR, lf.getCantitate())) {
                diferente.add("Cantitate facturata (" + lf.getCantitate() + ") difera de cantitatea din NIR (" + cantNIR + ") pt " + lf.getMaterial());
            }

            // 2. Verificare pret si cantitate comandata
            LinieSumar ls = comanda.linii().stream()
                .filter(l -> l.material().equals(lf.getMaterial()))
                .findFirst()
                .orElse(null);

            if (ls == null) {
                diferente.add("Materialul " + lf.getMaterial() + " din factura nu exista pe comanda");
            } else {
                if (!t.acopera(ls.pretUnitar(), lf.getPretUnitar().getValoare())) {
                    diferente.add("Pretul facturat (" + lf.getPretUnitar().getValoare() + ") difera de pretul din comanda (" + ls.pretUnitar() + ") pt " + lf.getMaterial());
    // ... [restul de 8 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 131. 📄 [`RezultatReconciliere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/RezultatReconciliere.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `RezultatReconciliere.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ValueObject
public record RezultatReconciliere(
    boolean aprobata,
    List<String> diferente
) implements Serializable {
    public RezultatReconciliere {
        if (diferente == null) diferente = Collections.emptyList();
    }
}
```

### 132. 📄 [`StareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareFactura.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareFactura.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareFactura {
    Inregistrata,
    InVerificare,
    Blocata,
    Contestata,
    AprobataLaPlata,
    PartialPlatita,
    Platita,
    Anulata
}
```

### 133. 📄 [`StareNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareNIR.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareNIR.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareNIR {
    Generat,
    InReconciliere,
    CuDiscrepante,
    Reconciliat,
    Contabilizat,
    Stornat
}
```

### 134. 📄 [`StareOrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/StareOrdinPlata.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `StareOrdinPlata.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareOrdinPlata {
    Creat,
    InAprobare,
    Aprobat,
    TransmisLaBanca,
    Executat,
    RespinsDeBanca,
    Anulat
}
```

### 135. 📄 [`Suma.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Suma.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `Suma.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Suma implements Serializable {
    private BigDecimal valoare;
    private Currency moneda;

    protected Suma() {}

    public Suma(BigDecimal valoare, Currency moneda) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Suma cannot be null or negative");
        }
        this.valoare = valoare.setScale(2, RoundingMode.HALF_UP);
        this.moneda = Objects.requireNonNull(moneda, "moneda cannot be null");
    }

    public static Suma of(BigDecimal valoare, String currencyCode) {
        return new Suma(valoare, Currency.getInstance(currencyCode));
    }

    public static Suma of(double valoare, String currencyCode) {
        return new Suma(BigDecimal.valueOf(valoare), Currency.getInstance(currencyCode));
    }

    public static Suma zero(Currency moneda) {
        return new Suma(BigDecimal.ZERO, moneda);
    }

    public Suma adauga(Suma alta) {
        Objects.requireNonNull(alta, "alta suma cannot be null");
        if (!this.moneda.equals(alta.moneda)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + this.moneda + " vs " + alta.moneda);
    // ... [restul de 39 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 136. 📄 [`Toleranta.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/Toleranta.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Value Object pentru verificarea incadrarii in toleranta de reconciliere (+- procent).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object pentru verificarea incadrarii in toleranta de reconciliere (+-
 * procent).
 */
@ValueObject
@Embeddable
public final class Toleranta implements Serializable {

    private static final BigDecimal SUTA = BigDecimal.valueOf(100);
    private static final BigDecimal DEFAULT_PROCENT = BigDecimal.valueOf(2.0);

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "procent_toleranta", nullable = false, precision = 5, scale = 2)
    private BigDecimal procent;

    // Modificat in protected pentru a fi accesibil doar pentru JPA/Hibernate
    protected Toleranta() {
        this.procent = DEFAULT_PROCENT;
    }

    public Toleranta(BigDecimal procent) {
        if (procent == null || procent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Procentul de toleranta nu poate fi nul sau negativ");
        }
        this.procent = procent;
    }

    public static Toleranta standard() {
        return new Toleranta(DEFAULT_PROCENT);
    // ... [restul de 45 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 137. 📄 [`ValoareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/ValoareFactura.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ValoareFactura.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@ValueObject
@Embeddable
public final class ValoareFactura implements Serializable {

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_fara_tva", nullable = false)
    private BigDecimal valoareFaraTVA;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_tva", nullable = false)
    private BigDecimal valoareTVA;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_totala", nullable = false)
    private BigDecimal valoareTotala;

    protected ValoareFactura() {}

    public ValoareFactura(BigDecimal valoareFaraTVA, BigDecimal valoareTVA, BigDecimal valoareTotala) {
        Objects.requireNonNull(valoareFaraTVA, "valoareFaraTVA cannot be null");
        Objects.requireNonNull(valoareTVA, "valoareTVA cannot be null");
        Objects.requireNonNull(valoareTotala, "valoareTotala cannot be null");
        this.valoareFaraTVA = valoareFaraTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTVA = valoareTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTotala = valoareTotala.setScale(2, RoundingMode.HALF_UP);
    }
    // ... [restul de 36 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Algoritmul de calcul matematic 3-Way Matching (reconciliere cantitate și preț între Comandă, NIR și Factură).

### 138. 📄 [`IReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/IReconciliereComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Calcul Financiar si Reconciliere (Sprint 2 - ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contract pentru Serviciul de Calcul Financiar si Reconciliere (Sprint 2 - ADR-003).
 */
@DomainComputationService
public interface IReconciliereComputationService {

    /**
     * Entity Field Computation: Calculeaza restul total de plata agregat dintr-o lista de facturi.
     */
    BigDecimal calculeazaRestPlataTotal(List<FacturaFurnizor> facturi);

    /**
     * Entity View Computation: Agrega indicatorii financiari si de plata intr-un tablou de bord.
     */
    PlatiSummaryView calculeazaIndicatoriPlati(List<FacturaFurnizor> facturi);

    /**
     * Calculeaza procentul de achitare a unei facturi (0.0 - 100.0%).
     */
    double calculeazaProcentAchitare(FacturaFurnizor factura);
}
```

### 139. 📄 [`PlatiSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/PlatiSummaryView.java)
* **Rol Arhitectural (Sprint 2)**: `Value Object (Obiect Valoare Imutabil DDD)`
* **Descriere de Business & Invarianți**: Entity View Computation: Proiectie sumarizata a indicatorilor de plati si facturi (Sprint 2 - ADR-003).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity View Computation: Proiectie sumarizata a indicatorilor de plati si facturi (Sprint 2 - ADR-003).
 */
@ValueObject
public record PlatiSummaryView(
    BigDecimal totalFacturat,
    BigDecimal totalAchitat,
    BigDecimal totalRestant,
    int numarFacturi,
    int facturiAchitateIntegral,
    int facturiRestante
) implements Serializable {}
```

### 140. 📄 [`ReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/computation/ReconciliereComputationService.java)
* **Rol Arhitectural (Sprint 2)**: `Computation Service (Serviciu de Calcul Matematic)`
* **Descriere de Business & Invarianți**: Implementare a Serviciului de Calcul Financiar si Reconciliere Facturi (Sprint 2 - ADR-003). Respecta izolarea pura de domeniu (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Implementare a Serviciului de Calcul Financiar si Reconciliere Facturi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainComputationService
public class ReconciliereComputationService implements IReconciliereComputationService {

    @Override
    public BigDecimal calculeazaRestPlataTotal(List<FacturaFurnizor> facturi) {
        if (facturi == null || facturi.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal restTotal = BigDecimal.ZERO;
        for (FacturaFurnizor f : facturi) {
            if (f.getStareFactura() != StareFactura.Anulata && f.getStareFactura() != StareFactura.Platita) {
                restTotal = restTotal.add(f.calculeazaRestDePlata().getValoare());
            }
        }
        return restTotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public PlatiSummaryView calculeazaIndicatoriPlati(List<FacturaFurnizor> facturi) {
        if (facturi == null || facturi.isEmpty()) {
            return new PlatiSummaryView(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0);
        }

        BigDecimal totalFacturat = BigDecimal.ZERO;
        BigDecimal totalAchitat = BigDecimal.ZERO;
        BigDecimal totalRestant = BigDecimal.ZERO;
    // ... [restul de 53 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Evenimentele de domeniu emise la reconcilierea facturilor, apropierea scadenței sau autorizarea plăților.

### 141. 📄 [`PlataExecutataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/PlataExecutataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `PlataExecutataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record PlataExecutataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID ordinId,
    UUID facturaId,
    Suma suma
) implements DomainEvent {}
```

### 142. 📄 [`ScadentaApropiataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/event/ScadentaApropiataEvent.java)
* **Rol Arhitectural (Sprint 2)**: `Clasă de Domeniu Pure`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ScadentaApropiataEvent.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScadentaApropiataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID facturaId,
    LocalDate dataScadenta
) implements DomainEvent {}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Fabricile de creare a facturilor furnizor și a notelor de intrare recepție (NIR).

### 143. 📄 [`FacturaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/FacturaFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Factory (Fabrică de Agregate DDD)`
* **Descriere de Business & Invarianți**: Aggregate Factory pentru FacturaFurnizor (Sprint 2 - ADR-003). Garanteaza initializarea consistenta a facturii in starea initiala Inregistrata
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Factory pentru FacturaFurnizor (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a facturii in starea initiala Inregistrata
 * si calculul invariant al valorilor de linie.
 */
@DomainFactory
public class FacturaFactory {

    public static FacturaFurnizor creeaza(String serie,
                                          String numar,
                                          LocalDate dataEmitere,
                                          LocalDate dataScadenta,
                                          IdComanda comandaId,
                                          IdFurnizor furnizorId,
                                          List<LinieFactura> linii,
                                          Currency moneda) {
        if (serie == null || serie.trim().isEmpty()) {
            throw new DomainValidationException("SERIE_LIPSA", "Seria facturii este obligatorie!");
        }
        if (numar == null || numar.trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_LIPSA", "Numarul facturii este obligatoriu!");
        }
        if (dataEmitere == null) {
            throw new DomainValidationException("DATA_EMITERE_LIPSA", "Data emiterii facturii este obligatorie!");
        }
        if (dataScadenta == null) {
            throw new DomainValidationException("DATA_SCADENTA_LIPSA", "Data scadenta a facturii este obligatorie!");
        }
    // ... [restul de 20 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 144. 📄 [`NIRFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/factory/NIRFactory.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Factory (Fabrică de Agregate DDD)`
* **Descriere de Business & Invarianți**: Aggregate Factory pentru NIR (Nota de Intrare-Receptie) (Sprint 2 - ADR-003). Garanteaza crearea conforma si idempotenta a receptiei de marfa.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieNIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Factory pentru NIR (Nota de Intrare-Receptie) (Sprint 2 - ADR-003).
 * Garanteaza crearea conforma si idempotenta a receptiei de marfa.
 */
@DomainFactory
public class NIRFactory {

    public static NIR creeaza(String numarNIR,
                             IdComanda comandaId,
                             UUID eventSursaId,
                             LocalDate dataReceptie,
                             List<LinieNIR> linii) {
        if (numarNIR == null || numarNIR.trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_NIR_LIPSA", "Numarul NIR-ului este obligatoriu!");
        }
        if (comandaId == null) {
            throw new DomainValidationException("COMANDA_LIPSA", "NIR-ul trebuie asociat unei comenzi!");
        }
        if (eventSursaId == null) {
            throw new DomainValidationException("EVENT_SURSA_LIPSA", "Identificatorul evenimentului sursa este obligatoriu pentru idempotenta!");
        }
        if (dataReceptie == null) {
            throw new DomainValidationException("DATA_RECEPTIE_LIPSA", "Data receptiei este obligatorie!");
        }
        if (linii == null || linii.isEmpty()) {
            throw new DomainValidationException("LINII_NIR_LIPSA", "NIR-ul trebuie sa contina cel putin o linie de receptie!");
        }

        UUID idNIR = UUID.randomUUID();
        return new NIR(idNIR, numarNIR.trim(), comandaId, eventSursaId, dataReceptie, linii);
    // ... [restul de 2 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Politici de autorizare a plăților condiționate de absența blocajelor sau a disputelor calitative.

### 145. 📄 [`PoliticaAutorizarePlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/policy/PoliticaAutorizarePlata.java)
* **Rol Arhitectural (Sprint 2)**: `Domain Policy (Politică de Business DDD)`
* **Descriere de Business & Invarianți**: Politica de autorizare a platilor (Other Business Rules - ADR-003 / Project_OOAD): 1. Principiul celor 4 ochi (Four-Eyes Principle): Creatorul ordinului de plata NU poate fi aprobatorul platii.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Politica de autorizare a platilor (Other Business Rules - ADR-003 / Project_OOAD):
 * 1. Principiul celor 4 ochi (Four-Eyes Principle): Creatorul ordinului de plata NU poate fi aprobatorul platii.
 * 2. Conditie stare factura: Factura trebuie sa fie AprobataLaPlata sau PartialPlatita.
 * 3. Prag autorizare CFO: Platile ce depasesc 20,000 RON necesita aprobarea Directorului Financiar (CFO).
 */
@DomainPolicy
public class PoliticaAutorizarePlata {

    public static final BigDecimal PRAG_CFO = BigDecimal.valueOf(20000.00);

    public boolean respectaFourEyesPrinciple(UUID creatorId, UUID aprobatorId) {
        Objects.requireNonNull(creatorId, "creatorId cannot be null");
        Objects.requireNonNull(aprobatorId, "aprobatorId cannot be null");
        return !creatorId.equals(aprobatorId);
    }

    public boolean necesitaAprobareCFO(Suma suma) {
        Objects.requireNonNull(suma, "suma cannot be null");
        return suma.getValoare().compareTo(PRAG_CFO) > 0;
    }

    public boolean esteStareFacturaEligibila(StareFactura stareFactura) {
        return stareFactura == StareFactura.AprobataLaPlata || stareFactura == StareFactura.PartialPlatita;
    }

    public void valideazaAutorizare(UUID creatorId, UUID aprobatorId, Suma suma, StareFactura stareFactura, boolean esteCFO) {
        if (!respectaFourEyesPrinciple(creatorId, aprobatorId)) {
            throw new IllegalStateException("Four-Eyes Principle incalcat: Creatorul ordinului de plata (" 
                    + creatorId + ") nu poate fi acelasi cu aprobatorul platii.");
        }

    // ... [restul de 11 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Servicii de validare a corectitudinii fiscale a facturilor și a conturilor IBAN.

### 146. 📄 [`FacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/FacturaValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Implementare Domain Validation Service pentru Facturi si 3-Way Matching (Sprint 2 - ADR-003). Respecta izolarea pura de domeniu si separarea de context Bounded Context (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieSumar;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Facturi si 3-Way Matching (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu si separarea de context Bounded Context (ADR-011).
 */
@DomainValidationService
public class FacturaValidationService implements IFacturaValidationService {

    @Override
    public void valideazaFacturaInregistrare(FacturaFurnizor factura) {
        Objects.requireNonNull(factura, "Factura nu poate fi null!");

        if (factura.getSerie() == null || factura.getSerie().trim().isEmpty()) {
            throw new DomainValidationException("SERIE_FACTURA_LIPSA", "Seria facturii este obligatorie!");
        }
        if (factura.getNumar() == null || factura.getNumar().trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_FACTURA_LIPSA", "Numarul facturii este obligatoriu!");
        }
        if (factura.getDataScadenta().isBefore(factura.getDataEmitere())) {
            throw new DomainValidationException("SCADENTA_INVALIDA", "Data scadenta nu poate fi anterioara datei emiterii facturii!");
        }
        if (factura.getLinii() == null || factura.getLinii().isEmpty()) {
            throw new DomainValidationException("LINII_FACTURA_LIPSA", "Factura trebuie sa contina cel putin o linie!");
        }
        if (factura.getValoare() == null || factura.getValoare().getValoareTotala().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("VALOARE_FACTURA_INVALIDA", "Valoarea totala a facturii trebuie sa fie strict pozitiva!");
        }
    }

    // ... [restul de 58 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 147. 📄 [`IFacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/domain/validation/IFacturaValidationService.java)
* **Rol Arhitectural (Sprint 2)**: `Validation Service (Serviciu de Validare a Domeniului)`
* **Descriere de Business & Invarianți**: Contract pentru Serviciul de Validare a Domeniului Facturare & Reconciliere (ADR-003). Respecta izolarea stricta a Bounded Context-ului facturi (ADR-011).
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Validare a Domeniului Facturare & Reconciliere (ADR-003).
 * Respecta izolarea stricta a Bounded Context-ului facturi (ADR-011).
 */
@DomainValidationService
public interface IFacturaValidationService {

    /**
     * Valideaza datele de baza la inregistrarea unei facturi de la furnizor.
     */
    void valideazaFacturaInregistrare(FacturaFurnizor factura);

    /**
     * Valideaza reconcilierea 3-Way Match (Comanda vs NIR vs Factura) conform tolerantei stabilite.
     */
    void valideaza3WayMatch(FacturaFurnizor factura, NIR nir, ComandaSummaryVO comanda, double tolerantaProcentualaMax);

    /**
     * Valideaza emiterea unui ordin de plata in raport cu starea facturii si restul de plata.
     */
    void valideazaOrdinPlata(FacturaFurnizor factura, BigDecimal sumaPlata);
}
```

## 📁 Director: `src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration`
* **Modul de Business**: `Bounded Context: Facturi`
* **Rol în Arhitectură**: Configurația Spring (@Configuration, @Bean) ce expune serviciile de reconciliere și validare ca bean-uri Singleton.

### 148. 📄 [`FacturiDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Path Local Direct**: [`src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java`](file:///C:/Users/bmugu/Desktop/app/src/main/java/ro/uaic/feaa/sdbis/supplychain/facturi/infrastructure/configuration/FacturiDomainConfig.java)
* **Rol Arhitectural (Sprint 2)**: `Spring Domain Configuration (@Configuration & @Bean)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `FacturiDomainConfig.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ReconciliereService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.IReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy.PoliticaAutorizarePlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.FacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.IFacturaValidationService;

@Configuration
public class FacturiDomainConfig {

    @Bean
    public ReconciliereService reconciliereService() {
        return new ReconciliereService();
    }

    @Bean
    public IFacturaValidationService facturaValidationService() {
        return new FacturaValidationService();
    }

    @Bean
    public IReconciliereComputationService reconciliereComputationService() {
        return new ReconciliereComputationService();
    }

    @Bean
    public PoliticaAutorizarePlata politicaAutorizarePlata() {
        return new PoliticaAutorizarePlata();
    }
}
```

## 📁 Director: `src/test/java/ro/uaic/feaa/sdbis/supplychain/unit`
* **Modul de Business**: `Domain Unit Tests (Sprint 2 DoD)`
* **Rol în Arhitectură**: Suitele complete de teste unitare care validează invarianții, regulile de calcul, validările și mașinile de stări conform criteriilor Definition of Done din Sprintul 2.

### 149. 📄 [`ComputationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationRulesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComputationRulesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Toleranta;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Computation Rules Tests (Tabelul Gherkin 1.2.5)")
public class ComputationRulesTest {

    @Test
    @DisplayName("Scorecard weighted formula: 40% OTIF + 40% Calitate + 20% Pret -> Grade Mapping")
    void testScorecardGradeMapping() {
        // Scenariul 1: OTIF=98%, Respingere=2% (Calitate=98%), Pret=100% -> Scor = 98.4 -> A
        Scor sA = Scor.ponderat(BigDecimal.valueOf(98.0), BigDecimal.valueOf(98.0), BigDecimal.valueOf(100.0));
        assertEquals(Calificativ.A, sA.calificativ());
        assertTrue(sA.getValoare().compareTo(BigDecimal.valueOf(90.0)) >= 0);

        // Scenariul 2: OTIF=85%, Calitate=85%, Pret=90% -> Scor = 86.0 -> B
        Scor sB = Scor.ponderat(BigDecimal.valueOf(85.0), BigDecimal.valueOf(85.0), BigDecimal.valueOf(90.0));
        assertEquals(Calificativ.B, sB.calificativ());

        // Scenariul 3: OTIF=70%, Calitate=70%, Pret=75% -> Scor = 71.0 -> C
        Scor sC = Scor.ponderat(BigDecimal.valueOf(70.0), BigDecimal.valueOf(70.0), BigDecimal.valueOf(75.0));
        assertEquals(Calificativ.C, sC.calificativ());

        // Scenariul 4: OTIF=50%, Calitate=50%, Pret=60% -> Scor = 52.0 -> D
        Scor sD = Scor.ponderat(BigDecimal.valueOf(50.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(60.0));
        assertEquals(Calificativ.D, sD.calificativ());
    }

    @Test
    // ... [restul de 38 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 150. 📄 [`ComputationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ComputationServicesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ComputationServicesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaSummaryView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.IComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.IReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.PlatiSummaryView;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Computation Services Tests (ADR-003)")
public class ComputationServicesTest {

    private IComandaComputationService comandaComputationService;
    private ICalculScorecardService calculScorecardService;
    private IReconciliereComputationService reconciliereComputationService;

    @BeforeEach
    void setUp() {
        comandaComputationService = new ComandaComputationService();
        calculScorecardService = new CalculScorecardService();
        reconciliereComputationService = new ReconciliereComputationService();
    }

    // ... [restul de 186 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 151. 📄 [`ValidationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationRulesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ValidationRulesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Validation Rules Tests (Tabelul 2.1, 2.2)")
public class ValidationRulesTest {

    @Test
    @DisplayName("PlanAQL invariant: numarRespingere must equal numarAcceptare + 1")
    void testPlanAQLInvariant() {
        // Valid ISO 2859-1 single sampling plan
        PlanAQL plan = PlanAQL.standard(1.5, 80, 2);
        assertEquals(2, plan.getNumarAcceptare());
        assertEquals(3, plan.getNumarRespingere());
        assertEquals(Verdict.Conforma, plan.decide(2));
        assertEquals(Verdict.Neconforma, plan.decide(3));

        // Invariant breach: Ac=2, Re=4
        assertThrows(IllegalArgumentException.class, () -> {
            new PlanAQL(BigDecimal.valueOf(1.5), 80, 2, 4);
        });
    }

    @Test
    @DisplayName("Perioada invariant: dataFinalizare must be >= dataInceput")
    void testPerioadaInvariant() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);
        Perioada p = Perioada.of(start, end);
        assertTrue(p.includeData(LocalDate.of(2026, 3, 15)));

    // ... [restul de 26 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 152. 📄 [`ValidationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/ValidationServicesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `ValidationServicesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy.PoliticaSelectieAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.CalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.ICalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.NIRFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy.PoliticaAutorizarePlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.FacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.IFacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.*;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Validation Services, Bean Validation & Business Policies Tests (ADR-003)")
public class ValidationServicesTest {

    private IComandaValidationService comandaValidationService;
    private ICalitateValidationService calitateValidationService;
    private IFacturaValidationService facturaValidationService;
    private Validator validator;
    // ... [restul de 491 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 153. 📄 [`WorkflowRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowRulesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `WorkflowRulesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Workflow Rules Tests (Atomic Cascading Cancellation, Partial Reception)")
public class WorkflowRulesTest {

    @Test
    @DisplayName("Cascading Cancellation: anuleazaComanda() atomically cancels order and unreceived lots")
    void testCascadingCancellation() {
        CodMaterial mat = CodMaterial.of("OTEL-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "TO"), PretUnitar.of(500.0, "EUR"));
        ComandaAprovizionare comanda = new ComandaAprovizionare(
            IdComanda.random(), "CMD-2026-001", IdFurnizor.random(), UUID.randomUUID(),
            LocalDate.now(), LocalDate.now().plusDays(20), "DEP-A", Collections.singletonList(linie)
        );

        comanda.confirma(LocalDate.now().plusDays(15));
        IdLot lot1 = comanda.planificaLot("LOT-01", mat, Cantitate.of(40.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));
        IdLot lot2 = comanda.planificaLot("LOT-02", mat, Cantitate.of(60.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));

        // lot1 is received
        comanda.inregistreazaExpediere("DHL", "AWB123", Collections.singleton(lot1));
        comanda.inregistreazaReceptie(lot1, Cantitate.of(40.0, "TO"));
        assertEquals(StareComanda.ReceptionatPartial, comanda.getStareComanda());

        // Cancel order: lot2 (Planificat) must become Anulat, while lot1 (SositInDepozit) remains unchanged
        ComandaAnulataEvent event = comanda.anuleazaComanda();
    // ... [restul de 33 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

### 154. 📄 [`WorkflowServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Path Local Direct**: [`src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java`](file:///C:/Users/bmugu/Desktop/app/src/test/java/ro/uaic/feaa/sdbis/supplychain/unit/WorkflowServicesTest.java)
* **Rol Arhitectural (Sprint 2)**: `JUnit 5 Unit Test Suite (Test de Domeniu Sprint 2 DoD)`
* **Descriere de Business & Invarianți**: Componentă a modelului de business `WorkflowServicesTest.java`.
* **Secvență de Cod (Implementare)**:
```java
package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Workflow Services & State Transitions Tests (ADR-003)")
public class WorkflowServicesTest {

    @Test
    @DisplayName("Workflow Comenzi: Flux complet de la Solicitat -> Confirmat -> InTranzit -> ReceptionatPartial -> Receptionat")
    void testComandaLifecycleWorkflow_Success() {
        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(200.0, "MP"), PretUnitar.of(45.0, "RON"));

        // 1. Creare comanda (initial: Solicitat)
        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-WF-001", IdFurnizor.random(), UUID.randomUUID(),
            LocalDate.now(), LocalDate.now().plusDays(14), "DEP-1", List.of(linie)
        );
        assertEquals(StareComanda.Solicitat, comanda.getStareComanda());

        // 2. Confirmare furnizor
        comanda.confirma(LocalDate.now().plusDays(10));
        assertEquals(StareComanda.Confirmat, comanda.getStareComanda());

        // 3. Planificare lot si expediere (InTranzit)
        IdLot lot1 = comanda.planificaLot("LOT-T1", mat, Cantitate.of(100.0, "MP"), LocalDate.now(), LocalDate.now().plusYears(1));
        IdLot lot2 = comanda.planificaLot("LOT-T2", mat, Cantitate.of(100.0, "MP"), LocalDate.now(), LocalDate.now().plusYears(1));
    // ... [restul de 126 linii omise pentru concizie in blueprint; codul complet este accesibil la link-ul de mai sus]
```

---

**Total fișiere indexate în Blueprint**: 154 fișiere Java exclusiv pentru Sprintul 2.