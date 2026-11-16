# ADR-003: Sprint 2 – Business Model Implementation

## Status

Accepted

---

# Context

Sprint 2 are ca obiectiv implementarea modelului de business definit în Sprint 1.

Acest sprint transformă modelul conceptual și regulile de business în componente software executabile, folosind o arhitectură bazată pe Domain Model, Services și teste automate. 【1-cb48ec】

---

# Decision

Sistemul va implementa toate conceptele identificate în modelul domeniului și toate regulile de business definite anterior prin entități, agregate, servicii și componente de validare/calcul. 【1-cb48ec】

---

# Domain Model Implementation

## Domain Entities

Sistemul trebuie să implementeze toate entitățile identificate în modelul domeniului. 【1-cb48ec】

### Requirements

- implementarea entităților de domeniu;
- implementarea relațiilor dintre entități;
- implementarea atributelor specifice fiecărei entități. 【1-cb48ec】

---

## Value Objects

Sistemul trebuie să implementeze Value Objects utilizați în modelul domeniului. 【1-cb48ec】

### Requirements

- Enums;
- tipuri de date personalizate (Custom VO Data Types);
- obiecte imutabile cu identitate bazată pe valoare. 【1-cb48ec】

---

## Entity Aggregates

Sistemul trebuie să implementeze agregatele definite în modelul DDD. 【1-cb48ec】

### Aggregate Structure

#### Root Entity

- reprezintă punctul unic de acces către agregat. 【1-cb48ec】

#### Components

- Component Entities;
- Value Objects. 【1-cb48ec】

#### Aggregate Associations

- relațiile interne dintre elementele agregatului trebuie modelate explicit. 【1-cb48ec】

---

# Business Rules Implementation

## Workflow Rules

Regulile de business asociate fluxurilor operaționale trebuie implementate prin servicii dedicate. 【1-cb48ec】

### Requirements

- implementarea serviciilor de use-case;
- implementarea workflow-urilor de business;
- orchestrarea tranzacțiilor de business. 【1-cb48ec】

---

## Validation Rules

Regulile de validare trebuie implementate direct în modelul domeniului și în servicii dedicate. 【1-cb48ec】

### Requirements

#### Domain Entity Validation

Utilizarea Java Bean Validation:

- `@NotNull`
- `@NotBlank`
- `@Size`
- `@Min`
- `@Max`
- alte constrângeri specifice domeniului

prin adnotări aplicate entităților. 【1-cb48ec】

#### Domain Validation Services

Servicii dedicate pentru:

- validări complexe;
- validări cross-entity;
- validări cross-aggregate;
- validări dependente de reguli de business. 【1-cb48ec】

---

## Computation Rules

Regulile de calcul trebuie implementate prin servicii dedicate. 【1-cb48ec】

### Entity Field Computations

Servicii pentru calculul atributelor entităților:

- totaluri;
- scoruri;
- indicatori;
- stări derivate. 【1-cb48ec】

### Entity View Computations

Servicii pentru calculul proiecțiilor și vizualizărilor:

- dashboard views;
- report views;
- summary views;
- aggregate views. 【1-cb48ec】

---

## Other Business Rules

Toate regulile suplimentare identificate în Sprint 1 trebuie implementate prin componente dedicate ale domeniului. 【1-cb48ec】

Exemple:

- politici de business;
- constrângeri operaționale;
- reguli de autorizare;
- reguli de audit. 【1-cb48ec】

---

# Deliverables

## Domain Layer

### Entity Classes

Sistemul trebuie să conțină:

- clase pentru entități;
- clase pentru Value Objects;
- clase pentru agregate. 【1-cb48ec】

### Aggregate Entity Factories

Sistemul trebuie să furnizeze:

- factory classes pentru crearea agregatelor;
- inițializarea consistentă a obiectelor de domeniu. 【1-cb48ec】

---

## Service Layer

### Business Service Interfaces

Definirea contractelor pentru:

- servicii de business;
- workflow services;
- validation services;
- computation services;
- audit services. 【1-cb48ec】

### Business Service Implementations

Implementări concrete pentru toate interfețele definite. 【1-cb48ec】

---

## Workflow Services

Sistemul trebuie să implementeze servicii dedicate fluxurilor operaționale ale aplicației. 【1-cb48ec】

---

## Validation Services

Sistemul trebuie să implementeze servicii dedicate validărilor de business. 【1-cb48ec】

---

## Computation Services

Sistemul trebuie să implementeze servicii dedicate calculelor și indicatorilor de business. 【1-cb48ec】

---

## Audit Services

Sistemul trebuie să implementeze servicii pentru:

- audit operațional;
- trasabilitatea modificărilor;
- jurnalizarea acțiunilor importante. 【1-cb48ec】

---

## Application Configuration

### Spring Configuration

Sistemul trebuie să conțină:

- clase de configurare Spring;
- înregistrarea bean-urilor;
- configurarea componentelor aplicației. 【1-cb48ec】

---

# Testing Requirements

## JUnit Test Classes

Trebuie create teste automate pentru verificarea implementării. 【1-cb48ec】

### Validation Service Tests

Verifică:

- regulile de validare;
- scenariile valide;
- scenariile invalide. 【1-cb48ec】

### Computation Service Tests

Verifică:

- formulele de calcul;
- valorile derivate;
- agregările. 【1-cb48ec】

### Workflow Service Tests

Verifică:

- tranzițiile de stare;
- regulile proceselor de business;
- scenariile principale și alternative. 【1-cb48ec】

---

# Definition of Done

Sprint 2 este considerat finalizat dacă există:

- [ ] Domain Entities implementate
- [ ] Relationships implementate
- [ ] Value Objects implementate
- [ ] Aggregate Roots implementate
- [ ] Aggregate Components implementate
- [ ] Business Services Interfaces
- [ ] Business Services Implementations
- [ ] Workflow Services
- [ ] Validation Services
- [ ] Computation Services
- [ ] Audit Services
- [ ] Aggregate Factories
- [ ] Spring Configuration Classes
- [ ] JUnit Validation Tests
- [ ] JUnit Computation Tests
- [ ] JUnit Workflow Tests

