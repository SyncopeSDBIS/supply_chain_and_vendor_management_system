## Descriere Feature
- **Bounded Context / Modul:** `comenzi` | `calitate` | `facturi` | `shared` | `securitate`
- **Referință OOAD (Use Case / BR / Invariant):** ex. `UC1 CreeazaComandaAprovizionare`, `BR-PO-01`
- **Sprint MSD:** Sprint 1 / Sprint 2 / Sprint 3 / Sprint 4

## Ce a fost implementat (1:1 cu Project_OOAD.docx)
- [ ] Clase de Domeniu (Aggregate Root / Entity / Value Object / Domain Service)
- [ ] Aggregate Entity Factory
- [ ] Domain Validation Service & Domain Computation Service
- [ ] Application Service / Command / Query View (CQRS)
- [ ] Infrastructură (JPA Repository / Outbox / Inbox / REST Adapter)
- [ ] Prezentare (REST Controller / DTO JSON+XML)

## Checklist de Calitate (Obligatoriu pentru Merge)
- [ ] Numele claselor și metodelor respectă **1:1** diagramele din `Project_OOAD.docx`
- [ ] `DomainLayer` nu importă clase din `Spring`, `Hibernate`, `Jackson` sau `Stripe` (ADR-011)
- [ ] Testele unitare JUnit 5 acoperă Happy Path + Error Path + Invarianții de domeniu
- [ ] Testele **ArchUnit** trec (`✅ Green`)
- [ ] Pipeline-ul **GitHub Actions CI/CD** este complet verde (`✅ Green`)
