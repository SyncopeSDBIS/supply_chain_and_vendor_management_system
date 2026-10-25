# ADR-001: Business Domain Design - Sprint 1

## Status

Accepted

---

## Context

Sprint 1 are ca obiectiv definirea și documentarea domeniului de business al aplicației. Activitatea este împărțită în două arii principale:

1. Business Requirements & Business Workflow
2. Domain Model & Business Rules

Scopul este identificarea proceselor de business, a entităților domeniului și a regulilor care guvernează comportamentul acestora.

---

## Decision

### Business Requirements

Sistemul trebuie să documenteze cerințele de business folosind una sau mai multe dintre următoarele tehnici:

- Use Cases
- Usability Scenarios
- Business Stories
- Business Workflow Rules

Aceste artefacte trebuie să descrie interacțiunile utilizatorilor cu sistemul și procesele de business asociate.

---

### Domain Model

Modelul domeniului trebuie să conțină:

#### Business Entities

- Definirea entităților de business
- Definirea relațiilor dintre entități
- Model ORM (Object Relational Mapping)

#### Entity Aggregates

- Aggregate Root
- Component Entities
- Value Objects

Modelul trebuie să reflecte structura conceptuală a domeniului și relațiile dintre entități.

---

### Business Rules

Sistemul trebuie să definească și să documenteze regulile de business asociate domeniului.

#### Validation Rules

- Reguli de validare pentru atributele entităților
- Reguli de validare la nivel de agregat

#### Computation Rules

- Atribute calculate ale entităților
- View-uri sau proiecții calculate

#### Additional Business Rules

- Constrângeri de business
- Politici de business
- Reguli operaționale specifice domeniului

Toate regulile trebuie documentate textual.

---

## Deliverables

### Business Requirements Deliverables

Se vor realiza unul sau mai multe dintre următoarele artefacte:

- Use Case Diagram
- Sequence Diagram
- Activity Diagram

### Domain Model Deliverables

- Entity Class Diagram
- SQL Diagram (opțional)
- Business Rules Specification (descriere textuală)

---

## Consequences

Implementarea acestei abordări oferă:

- Trasabilitate între cerințele de business și modelul domeniului
- Definirea clară a entităților și agregatelor
- Centralizarea regulilor de business
- Fundament pentru proiectarea arhitecturii și implementarea aplicației

---

## Sprint 1 Checklist

### Business Requirements

- [ ] Identificare Use Cases
- [ ] Identificare Usability Scenarios
- [ ] Definire Business Stories
- [ ] Definire Business Workflow Rules

### Domain Model

- [ ] Identificare Business Entities
- [ ] Definire relații între entități
- [ ] Definire ORM Mapping
- [ ] Identificare Aggregate Root
- [ ] Identificare Component Entities
- [ ] Definire Value Objects

### Business Rules

- [ ] Definire Validation Rules
- [ ] Definire Computation Rules
- [ ] Definire Additional Business Rules

### Modeling Artifacts

- [ ] Entity Class Diagram
- [ ] Use Case Diagram / Sequence Diagram / Activity Diagram
- [ ] Business Rules Specification
- [ ] SQL Diagram (opțional)

---