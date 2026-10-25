# MASTER BLUEPRINT & PLAN COMPLET DE IMPLEMENTARE (SPRINTS 1–4)
## Supply Chain and Vendor Management System (Procure-to-Pay)

---

## 0. Contextul Proiectului & Echipa
* **Program de Master:** SDBIS (Sisteme Distribuite și Business Intelligence Software), Anul I — FEAA, UAIC Iași
* **Discipline:**
  1. **Object-Oriented Analysis and Design (OOAD)** — Sursa principală de adevăr (`Project_OOAD.docx`)
  2. **Multi-tier Software Development (MSD)** — Implementare pe straturi, persistență JPA, SOA/REST, CI/CD, ArchUnit și pregătire pentru Microservicii
* **Echipa (3 studenți — împărțire ideală pe Bounded Contexts):**
  * **Balan Mugurel-Ionuț** — Owner *Core Domain: Managementul comenzilor de aprovizionare (`comenzi`)* + Shared Kernel
  * **Durnea Andrei-Sebastian** — Owner *Supporting Domain 1: Gestiunea furnizorilor și a calității (`calitate`)* + Notificări
  * **Gurzu Teodor** — Owner *Supporting Domain 2: Gestiunea facturilor și plăților (`facturi`)* + Securitate & Audit GDPR

---

## 1. Promptul Oficial Optimizat pentru AI (Cursor / Antigravity / Copilot / Gemini)

Salvați acest bloc în `.cursorrules` și în `ANTIGRAVITY_AI_DEVELOPMENT_CONFIGURATION.md` pentru ca orice secvență de cod generată să respecte **1:1** numele claselor și metodelor din `Project_OOAD.docx`:

```text
You are a Principal Enterprise Java Architect, DDD Expert, and Technical Lead assisting a team of 3 Master's students (SDBIS, FEAA Iași) building the "Supply Chain and Vendor Management System" for OOAD and MSD courses.

CRITICAL RULE — 1:1 NAMING FIDELITY WITH OOAD SPECIFICATION:
Never invent generic English names (like Order, Supplier, Invoice). You MUST use the exact Romanian domain class names, method signatures, field names, enums, ports, adapters, and events defined in our OOAD specification:
- Shared Kernel (`shared`): IdComanda, IdFurnizor, IdLot, CodMaterial, Perioada (esteInclusaIn), DomainEvent (eventId, occurredOn), OutboxRepo, InboxRepo, IJurnalAudit (inregistreazaAcces).
- Core Domain (`comenzi`): ComandaAprovizionare (Aggregate Root), Lot (Entity with package-private ~ methods), LinieComanda, Expediere, Cantitate, PretUnitar, EstimareLivrare, MetriciLivrareVO, IstoricLivrariVO, EstimareLeadTimeService, IMetodaEstimareLeadTime, StareComanda, StareLot, RezultatCalitateLot, ComandaRepo, LotRepo (read-only), IstoricLivrariRepo (read-only), IGestiuneFurnizoriAdapter, ComandaApplicationService, ComandaCtrl, ComenziApiRest, GestiuneFurnizoriAdapterRest, ComandaSummaryDTO, PerformantaLivrariDTO.
- Supporting 1 (`calitate`): Furnizor (Aggregate Root), ScorecardLunar (Entity), InspectorCalitate (Aggregate Root), InspectieCalitate (Aggregate Root), RaportNeconformitate (Aggregate Root), Scor, PerformantaLivrariVO, PlanAQL, Certificare, ActiuneCorectiva, ConditiiContractuale, Iban, Email, Adresa, FullName, CalculScorecardService, AlocareInspectorService, StareFurnizor, StareInspectie, StareNCR, Calificativ, Verdict, FurnizorRepo, InspectorRepo, InspectieRepo, NCRRepo, IPerformantaLivrariAdapter, CalitateApplicationService, CalitateCtrl, FurnizoriApiRest, PerformantaLivrariAdapterRest.
- Supporting 2 (`facturi`): FacturaFurnizor (Aggregate Root), NIR (Aggregate Root), OrdinPlata (Aggregate Root with package-private ~ constructor), LinieFactura, LinieNIR, ValoareFactura, Suma, Toleranta, ReconciliereService, LinieSumar, ComandaSummaryVO, RezultatReconciliere, StareFactura, StareNIR, StareOrdinPlata, FacturaRepo, NIRRepo, OrdinPlataRepo, FurnizoriBlocatiRepo, IComenziAdapter, FacturaApplicationService, FacturaCtrl, ComenziAdapterRest.

ARCHITECTURAL & TECHNICAL CONSTRAINTS:
1. Java 21 + Spring Boot 3.x + Spring Modulith + PostgreSQL + Flyway + RabbitMQ/JMS + Stripe SDK (via Adapter Pattern for Bank/Payment execution).
2. Clean / Layered Architecture inside each Bounded Context: `presentation` (UI/Controllers/REST Open Host), `application` (Application Services, Commands, Published Language DTOs), `domain` (Aggregates, Entities, VOs, Domain Services, Repository Interfaces, Ports, Domain Events), `infrastructure` (JPA Repositories extending AbstractRepositoryJPA, REST Adapters/ACL, OutboxDispatcherJMS, JMSMessageReceiver, Stripe/Bank Adapter).
3. Domain Layer Isolation: Domain NEVER depends on Spring, JPA/Hibernate, Jackson, JAXB, JMS/RabbitMQ, or Stripe (enforced via ArchUnit). Note: In Sprint 3, either map JPA via XML (`orm.xml`) OR use `@Entity` with ArchUnit explicitly configured for the MSD Sprint 3 JPA requirement.
4. Inter-Module Communication: Contexts NEVER reference each other's classes directly. They share ONLY the Shared Kernel (`IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`, `Perioada`) and communicate synchronously via REST Open Host Services + Anti-Corruption Layer (ACL) Adapters (`IGestiuneFurnizoriAdapter`, `IComenziAdapter`, `IPerformantaLivrariAdapter`), and asynchronously via the Transactional Outbox (`OutboxRepo` + `OutboxDispatcherJMS`) and Idempotent Inbox (`InboxRepo` / `eventSursaId` with UNIQUE constraint).
5. Security (OWASP, NIS2, Zero Trust): Simple header/mock-token RBAC filter (skip complex login UI as agreed with professor) enforcing roles (`Buyer`, `Warehouse Keeper`, `Supplier`, `Quality Manager`, `Quality Inspector`, `Accountant`, `Finance Officer`, `Second Approver`) and logging every access to IBAN, negotiated prices, and payment orders via `IJurnalAudit`.
```

---

## 2. Arhitectura Sistemului & Maparea 1:1 cu `Project_OOAD.docx`

### 2.1. Bounded Contexts (Subdomenii)
În loc de modulele generice de e-commerce (`customer`, `order`, `payment`), structura proiectului reflectă **1:1** cele 3 subdomenii funcționale + 2 subdomenii generice + Shared Kernel din `Project_OOAD.docx` (Secțiunea 1.2.1 & Figura 1):

1. **`shared` (Shared Kernel):**
   * Conține exclusiv Value Objects de identitate și tipuri comune: `IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`, `Perioada`, interfața `DomainEvent`, portul transversal `IJurnalAudit`, interfețele `OutboxRepo` și `InboxRepo`, și clasa tehnică de bază `AbstractRepositoryJPA`.
2. **`comenzi` — Managementul comenzilor de aprovizionare (Core Domain):**
   * Gestionează ciclul de viață al comenzilor de aprovizionare (`ComandaAprovizionare`), livrarea pe loturi (`Lot`), estimarea datei de livrare (`EstimareLeadTimeService`), recepția mărfii, anularea în cascadă și calculul indicatorilor OTIF.
3. **`calitate` — Gestiunea furnizorilor și a calității (Supporting Subdomain 1):**
   * Gestionează furnizorii (`Furnizor`), calificativele lunare (`ScorecardLunar`, `CalculScorecardService`), inspectorii (`InspectorCalitate`, `AlocareInspectorService`), inspecțiile AQL (`InspectieCalitate`) și rapoartele de neconformitate (`RaportNeconformitate`).
4. **`facturi` — Gestiunea facturilor și plăților (Supporting Subdomain 2):**
   * Gestionează facturile (`FacturaFurnizor`), notele de recepție (`NIR`), reconcilierea în 3 căi (`ReconciliereService` cu toleranță $\pm 2\%$) și ordinele de plată (`OrdinPlata` cu prag de aprobare suplimentară și execuție bancară prin Stripe/Bank Adapter).
5. **`notificari` — Notificări și Alerte (Generic Subdomain 1):**
   * Consumator decuplat de evenimente pentru alerte e-mail/push (comandă neconfirmată în 48h, estimare > data cerută, NCR escaladat, furnizor suspendat, certificare inspector expiră, scadență în 3 zile).
6. **`securitate` — Securitate & Audit GDPR (Generic Subdomain 2):**
   * Zero Trust RBAC (separare pe roluri fără login complex) și jurnal de audit imutabil (`IJurnalAudit`) pentru accesul la IBAN, prețuri negociate și ordine de plată.

---

### 2.2. Catalogul Complet de Clase, Atribute și Metode (1:1 cu Cap. 2, 3, 4, 5 din `.docx`)

#### A. Pachetul `shared` (Shared Kernel & Transversal)
* **`IdComanda`** `<<Value Object>>` (record / final class): `- value: UUID`
* **`IdFurnizor`** `<<Value Object>>`: `- value: UUID`
* **`IdLot`** `<<Value Object>>`: `- value: UUID`
* **`CodMaterial`** `<<Value Object>>`: `- cod: String`
* **`Perioada`** `<<Value Object>>`:
  * Atribute: `- dataInceput: LocalDate`, `- dataFinalizare: LocalDate` (Invariant: `dataFinalizare >= dataInceput`)
  * Metode: `+ esteInclusaIn(alta: Perioada): boolean`
* **`DomainEvent`** `<<Interface>>`: `+ eventId(): UUID`, `+ occurredOn(): Instant`
* **`OutboxRepo`** `<<Repository>>`: `+ saveEvent(e: DomainEvent): void`, `+ pollUnpublishedEvents(): List<DomainEvent>`, `+ markAsPublished(eventId: UUID): void`
* **`InboxRepo`** `<<Repository>>`: `+ existaEveniment(eventId: UUID): boolean`, `+ marcheazaProcesat(eventId: UUID): void`
* **`IJurnalAudit`** `<<Port>>`: `+ inregistreazaAcces(actiune: String, obiect: String): void`
* **`AbstractRepositoryJPA`** `<<abstract>>`: `# em: EntityManager` (Bază tehnică comună conform Figurii 10).

---

#### B. Pachetul `comenzi` (Core Domain: Managementul comenzilor de aprovizionare)

**1. Domain Layer (`comenzi.domain`):**
* **Enums:**
  * `StareComanda`: `Solicitat`, `Confirmat`, `Refuzat`, `InTranzit`, `ReceptionatPartial`, `Receptionat`, `Anulat`
  * `StareLot`: `Planificat`, `Expediat`, `SositInDepozit`, `InCarantina`, `EliberatInStoc`, `Respins`, `Returnat`, `Anulat`
  * `RezultatCalitateLot`: `Conform`, `Neconform`, `Scutit`
* **Aggregate Root — `ComandaAprovizionare`:**
  * Atribute: `- id: IdComanda`, `- numarComanda: String`, `- furnizorId: IdFurnizor`, `- cumparatorId: UUID`, `- dataComanda: LocalDate`, `- dataLivrareCeruta: LocalDate`, `- dataLivrarePromisa: LocalDate`, `- depozit: String`, `- stareComanda: StareComanda`, `- linii: List<LinieComanda>`, `- loturi: List<Lot>`, `- expedieri: List<Expediere>`, `- estimare: EstimareLivrare`
  * Metode publice:
    * `+ trimiteLaFurnizor(): void`
    * `+ confirma(dataPromisa: LocalDate): void`
    * `+ refuza(): void`
    * `+ planificaLot(codLot: String, material: CodMaterial, cantitate: Cantitate, productie: LocalDate, expirare: LocalDate): IdLot`
    * `+ inregistreazaExpediere(transportator: String, awb: String, loturi: Set<IdLot>): void`
    * `+ inregistreazaReceptie(lotId: IdLot, primit: Cantitate): ComandaReceptionataEvent`
    * `+ plaseazaLotInCarantina(lotId: IdLot): void`
    * `+ aplicaVerdictInspectie(lotId: IdLot, rezultat: RezultatCalitateLot): void`
    * `+ returneazaLotFurnizorului(lotId: IdLot): void`
    * `+ anuleazaComanda(): ComandaAnulataEvent`
    * `+ calculeazaCantitatePrimita(material: CodMaterial): Cantitate`
    * `+ calculeazaCantitateRamasa(material: CodMaterial): Cantitate`
    * `+ esteAcoperitaIntegral(): boolean`
    * `+ extrageMetrici(): MetriciLivrareVO`
    * `+ inregistreazaEstimare(e: EstimareLivrare): void`
  * Metode private (Invarianți — Tabelul 2.1):
    * `- validaStarePermiteOperatia(permise: StareComanda...): void`
    * `- validaCantitateLot(material: CodMaterial, cantitate: Cantitate): void`
    * `- gasesteLot(lotId: IdLot): Lot`
* **Child Entity — `Lot` (vizibilitate metode package-private `~`):**
  * Atribute: `- idLot: IdLot`, `- codLot: String`, `- material: CodMaterial`, `- cantitate: Cantitate`, `- cantitatePrimita: Cantitate`, `- dataProductie: LocalDate`, `- dataExpirare: LocalDate`, `- stareLot: StareLot`
  * Metode package-private (`~`): `~ expediaza(): void`, `~ receptioneaza(primit: Cantitate): void`, `~ plaseazaInCarantina(): void`, `~ elibereazaInStoc(): void`, `~ respinge(): void`, `~ returneazaFurnizorului(): void`, `~ anuleaza(): void`, `~ esteInainteDeReceptie(): boolean`
* **Value Objects:**
  * `LinieComanda`: `- material: CodMaterial`, `- cantitate: Cantitate`, `- pretUnitar: PretUnitar`, `+ valoareLinie(): BigDecimal`
  * `Expediere`: `- transportator: String`, `- nrAWB: String`, `- dataExpediere: LocalDate`, `- loturi: Set<IdLot>`
  * `Cantitate`: `- valoare: BigDecimal`, `- unitateMasura: String`, `+ adauga(alta: Cantitate): Cantitate`, `+ esteAcoperitDe(alta: Cantitate): boolean`
  * `PretUnitar`: `- valoare: BigDecimal`, `- moneda: Currency`
  * `EstimareLivrare`: `- dataEstimata: LocalDate`, `- metoda: String`, `+ depasesteData(ceruta: LocalDate): boolean`
  * `MetriciLivrareVO`: `- furnizorId: IdFurnizor`, `- material: CodMaterial`, `- dataComanda: LocalDate`, `- dataLivrarePromisa: LocalDate`
  * `IstoricLivrariVO`: `- leadTimeZile: List<Integer>`
* **Domain Service & Strategy Pattern:**
  * `IMetodaEstimareLeadTime` `<<Strategy Interface>>`: `+ estimeazaZile(istoric: IstoricLivrariVO): int` (Implementări concrete: `MedieMobilaStrategy`, `NetezireExponentialaStrategy`, `Percentila90Strategy`)
  * `EstimareLeadTimeService` `<<Domain Service>>`: `- strategie: IMetodaEstimareLeadTime`, `+ setStrategie(s: IMetodaEstimareLeadTime): void`, `+ calculeazaDataLivrare(m: MetriciLivrareVO, istoric: IstoricLivrariVO): EstimareLivrare`
* **Repositories & Ports (în Domain Layer):**
  * `ComandaRepo`: `+ add(comanda: ComandaAprovizionare): void`, `+ findById(id: IdComanda): Optional<ComandaAprovizionare>`, `+ save(comanda: ComandaAprovizionare): void`, `+ delete(comanda: ComandaAprovizionare): void`, `+ existaNumarComanda(numar: String): boolean`
  * `LotRepo` (read-only): `+ findById(id: IdLot): Optional<Lot>`, `+ findAllByComandaId(id: IdComanda): List<Lot>`
  * `IstoricLivrariRepo` (read-only): `+ obtineIstoric(furnizorId: IdFurnizor, material: CodMaterial): IstoricLivrariVO`
  * `IGestiuneFurnizoriAdapter` `<<Port>>`: `+ esteFurnizorEligibil(id: IdFurnizor): boolean`
* **Domain Events:**
  * `ComandaReceptionataEvent` (`eventId`, `occurredOn`, `comandaId`, `lotId`, `material`, `cantitatePrimita`, `receptieTotala`)
  * `ComandaAnulataEvent` (`eventId`, `occurredOn`, `comandaId`, `loturiAnulate: List<IdLot>`)
  * `ComandaNeconfirmataEvent`
  * `EstimareDepasesteCerereaEvent`
  * `VerdictNeaplicabilEvent`

**2. Application Layer (`comenzi.application`):**
* `ComandaApplicationService`:
  * Colaboratori: `comandaRepo`, `lotRepo`, `istoricRepo`, `outboxRepo`, `inboxRepo`, `furnizoriAdapter`, `estimareService`, `jurnalAudit`
  * Metode: `+ creeazaComanda(cmd: CreeazaComandaCommand): IdComanda`, `+ confirmaComanda(cmd: ConfirmaComandaCommand): void`, `+ inregistreazaExpediere(cmd): void`, `+ inregistreazaReceptie(cmd: InregistreazaReceptieCommand): void`, `+ anuleazaComanda(cmd: AnuleazaComandaCommand): void`, `+ estimeazaDataLivrare(cmd): void`, `+ plaseazaLotInCarantina(cmd): void`, `+ aplicaVerdictInspectie(cmd: AplicaVerdictCommand): void`, `+ obtineSumarComanda(id: IdComanda): ComandaSummaryDTO`, `+ obtinePerformanta(furnizorId: IdFurnizor, luna: Perioada): PerformantaLivrariDTO`
* **Published Language DTOs:**
  * `ComandaSummaryDTO` (`- comandaId: UUID`, `- linii: List<LinieSumarDTO>`)
  * `PerformantaLivrariDTO` (`- furnizorId: UUID`, `- otif: BigDecimal`, `- conformitatePret: BigDecimal`)

**3. Infrastructure Layer (`comenzi.infrastructure`):**
* `ComandaRepoJPA`, `LotRepoJPA`, `IstoricLivrariRepoJPA`, `OutboxRepoJPA`, `InboxRepoJPA` (extind `AbstractRepositoryJPA`)
* `GestiuneFurnizoriAdapterRest` (implementează `IGestiuneFurnizoriAdapter` -> `GET /api/furnizori/{id}/eligibil`)
* `JurnalAuditAdapterRest` (implementează `IJurnalAudit`)
* `OutboxDispatcherJMS` (`+ publicaEvenimente(): void` pe `topic.comenzi`)
* `JMSMessageReceiver` (`+ onMessage(msg: Message): void` — ascultă `topic.calitate`)
* `ComenziApiRest` `<<REST Endpoint (Open Host)>>`: `GET /api/comenzi/{id}/sumar`, `GET /api/comenzi/performanta`

**4. User Interface / Presentation Layer (`comenzi.presentation`):**
* `ComandaCtrl`: `+ creeazaComanda(req)`, `+ confirmaComanda(id, req)`, `+ inregistreazaExpediere(id, req)`, `+ inregistreazaReceptie(id, req)`, `+ anuleazaComanda(id)`, `+ estimeazaDataLivrare(id)`

---

#### C. Pachetul `calitate` (Supporting Subdomain 1: Gestiunea furnizorilor și a calității)

**1. Domain Layer (`calitate.domain`):**
* **Enums:**
  * `StareFurnizor`: `Candidat`, `InEvaluare`, `Aprobat`, `Conditionat`, `Suspendat`, `Blocat`
  * `StareInspectie`: `Programata`, `InspectorAlocat`, `InDesfasurare`, `Conforma`, `Neconforma`, `InReinspectie`, `Inchisa`, `Anulata`
  * `StareNCR`: `Deschis`, `InAnalizaCauzei`, `ActiuneCorectivaInCurs`, `Verificat`, `Inchis`, `Escaladat`
  * `Calificativ`: `A`, `B`, `C`, `D`
  * `Verdict`: `Conforma`, `Neconforma`
* **Aggregate Root 1 — `Furnizor`:**
  * Atribute: `- idFurnizor: IdFurnizor`, `- codFiscal: String`, `- denumire: String`, `- adresa: Adresa`, `- emailuri: Set<Email>`, `- iban: Iban`, `- categoriiMateriale: Set<String>`, `- conditii: ConditiiContractuale`, `- stareFurnizor: StareFurnizor`, `- scorecarduri: List<ScorecardLunar>`, `- inspectiiNeconformeConsecutive: int`
  * Metode: `+ trimiteLaEvaluare(): void`, `+ aproba(): void`, `+ aplicaScorecard(s: ScorecardLunar): void`, `+ inregistreazaRezultatInspectie(verdict: Verdict): void`, `+ suspenda(motiv: String): void`, `+ blocheaza(motiv: String): FurnizorBlocatEvent`, `+ esteEligibilPentruComenzi(): boolean`, `+ calculeazaLuniConsecutiveBSauMaiBun(): int`, `- validaScorecardUnicPeLuna(luna: Perioada): void`
* **Child Entity — `ScorecardLunar`:**
  * Atribute: `- id: UUID`, `- luna: Perioada`, `- otif: BigDecimal`, `- rataRespingere: BigDecimal`, `- conformitatePret: BigDecimal`, `- scor: Scor`
  * Metode: `+ {static} calculeaza(luna: Perioada, perf: PerformantaLivrariVO, rataRespingere: BigDecimal): ScorecardLunar`, `+ calificativ(): Calificativ`
* **Aggregate Root 2 — `InspectorCalitate`:**
  * Atribute: `- idInspector: UUID`, `- fullName: FullName`, `- certificari: List<Certificare>`, `- disponibilitate: List<Perioada>`
  * Metode: `+ adaugaCertificare(c: Certificare): void`, `+ declaraDisponibilitate(p: Perioada): void`, `+ poateFiAlocat(p: Perioada): boolean`
* **Aggregate Root 3 — `InspectieCalitate`:**
  * Atribute: `- idInspectie: UUID`, `- lotId: IdLot`, `- comandaId: IdComanda`, `- furnizorId: IdFurnizor`, `- material: CodMaterial`, `- plan: PlanAQL`, `- perioadaPlanificata: Perioada`, `- inspectorId: UUID`, `- stareInspectie: StareInspectie`, `- verdict: Verdict`
  * Metode: `+ aloca(inspectorId: UUID): void`, `+ incepe(): void`, `+ inregistreazaVerdict(defecteGasite: int): VerdictInspectieEvent`, `+ solicitaReinspectie(): void`, `+ inchide(): void`, `+ anuleaza(): void`
* **Aggregate Root 4 — `RaportNeconformitate`:**
  * Atribute: `- idNCR: UUID`, `- inspectieId: UUID`, `- furnizorId: IdFurnizor`, `- lotId: IdLot`, `- cauzaRadacina: String`, `- actiuneCorectiva: ActiuneCorectiva`, `- stareNCR: StareNCR`
  * Metode: `+ analizeazaCauza(cauza: String): void`, `+ initiazaActiuneCorectiva(a: ActiuneCorectiva): void`, `+ verifica(): void`, `+ inchide(): void`, `+ escaladeaza(): NCREscaladatEvent`
* **Value Objects:**
  * `Scor`: `- valoare: BigDecimal`, `+ {static} ponderat(otif: BigDecimal, calitate: BigDecimal, pret: BigDecimal): Scor`, `+ calificativ(): Calificativ`
  * `PerformantaLivrariVO`: `- furnizorId: IdFurnizor`, `- luna: Perioada`, `- otif: BigDecimal`, `- conformitatePret: BigDecimal`
  * `PlanAQL`: `- nivelAQL: BigDecimal`, `- dimensiuneEsantion: int`, `- numarAcceptare: int`, `- numarRespingere: int` (Invariant ISO 2859-1: `numarRespingere = numarAcceptare + 1`), `+ decide(defecteGasite: int): Verdict`
  * `Certificare`: `- denumire: String`, `- dataExpirare: LocalDate`, `+ esteValidaPe(p: Perioada): boolean`
  * `ActiuneCorectiva`: `- descriere: String`, `- responsabil: String`, `- termen: LocalDate`
  * `ConditiiContractuale`: `- termenPlataZile: int`, `- moneda: Currency`, `- preturiNegociate: Map<CodMaterial, BigDecimal>`
  * `Iban`: `- valoare: String`, `+ {static} esteValid(v: String): boolean`
  * `Email`: `- email: String`, `+ {static} isValidEmail(e: String): boolean`
  * `Adresa`: `- strada: String`, `- numar: String`, `- localitate: String`, `- codPostal: String`
  * `FullName`: `- firstName: String`, `- lastName: String`
* **Domain Services:**
  * `CalculScorecardService`: `+ calculeazaScorecard(luna: Perioada, perf: PerformantaLivrariVO, rataRespingere: BigDecimal): ScorecardLunar`
  * `AlocareInspectorService`: `+ aloca(inspectie: InspectieCalitate, inspector: InspectorCalitate): void`
* **Repositories & Ports:**
  * `FurnizorRepo`, `InspectorRepo`, `InspectieRepo`, `NCRRepo`
  * `IPerformantaLivrariAdapter` `<<Port>>`: `+ obtinePerformanta(furnizorId: IdFurnizor, luna: Perioada): PerformantaLivrariVO`
* **Domain Events:**
  * `InspectieProgramataEvent`, `LotScutitDeInspectieEvent`, `VerdictInspectieEvent`, `FurnizorSuspendatEvent`, `FurnizorBlocatEvent`, `NCREscaladatEvent`, `CertificareInspectorExpiraEvent`

**2. Application Layer (`calitate.application`):**
* `CalitateApplicationService`: `+ programeazaInspectie(cmd): void`, `+ alocaInspector(cmd: AlocaInspectorCommand): void`, `+ incepeInspectie(cmd: IncepeInspectieCommand): void`, `+ inregistreazaVerdict(cmd: InregistreazaVerdictCommand): void`, `+ calculeazaScorecardLunar(cmd: CalculScorecardCommand): void`, `+ esteFurnizorEligibil(id: IdFurnizor): boolean`

**3. Infrastructure Layer (`calitate.infrastructure`):**
* `FurnizorRepoJPA`, `InspectorRepoJPA`, `InspectieRepoJPA`, `NCRRepoJPA`, `OutboxRepoJPA`, `InboxRepoJPA`
* `FurnizoriApiRest` (`GET /api/furnizori/{id}/eligibil`)
* `PerformantaLivrariAdapterRest` (ACL ce traduce `PerformantaLivrariDTO` în `PerformantaLivrariVO`)
* `JurnalAuditAdapterRest`, `OutboxDispatcherJMS` (`topic.calitate`), `JMSMessageReceiver`

**4. Presentation Layer (`calitate.presentation`):**
* `CalitateCtrl`: `+ alocaInspector(id, req)`, `+ incepeInspectie(id)`, `+ inregistreazaVerdict(id, req)`, `+ calculeazaScorecard(luna)`

---

#### D. Pachetul `facturi` (Supporting Subdomain 2: Gestiunea facturilor și plăților)

**1. Domain Layer (`facturi.domain`):**
* **Enums:**
  * `StareFactura`: `Inregistrata`, `InVerificare`, `Blocata`, `Contestata`, `AprobataLaPlata`, `PartialPlatita`, `Platita`, `Anulata`
  * `StareNIR`: `Generat`, `InReconciliere`, `CuDiscrepante`, `Reconciliat`, `Contabilizat`, `Stornat`
  * `StareOrdinPlata`: `Creat`, `InAprobare`, `Aprobat`, `TransmisLaBanca`, `Executat`, `RespinsDeBanca`, `Anulat`
* **Aggregate Root 1 — `FacturaFurnizor`:**
  * Atribute: `- idFactura: UUID`, `- serie: String`, `- numar: String`, `- dataEmitere: LocalDate`, `- dataScadenta: LocalDate`, `- comandaId: IdComanda`, `- furnizorId: IdFurnizor`, `- linii: List<LinieFactura>`, `- valoare: ValoareFactura`, `- sumaPlatita: Suma`, `- stareFactura: StareFactura`
  * Metode: `+ trimiteLaVerificare(): void`, `+ aproba(): void`, `+ blocheaza(motiv: String): void`, `+ contesta(motiv: String): void`, `+ creeazaOrdinPlata(suma: Suma, creatDe: UUID): OrdinPlata`, `+ inregistreazaPlata(suma: Suma): void`, `+ anuleaza(): void`, `+ calculeazaRestDePlata(): Suma`, `+ esteAprobataLaPlata(): boolean`
* **Aggregate Root 2 — `NIR`:**
  * Atribute: `- idNIR: UUID`, `- numarNIR: String`, `- comandaId: IdComanda`, `- eventSursaId: UUID` (idempotență), `- dataReceptie: LocalDate`, `- linii: List<LinieNIR>`, `- stareNIR: StareNIR`
  * Metode: `+ trimiteLaReconciliere(): void`, `+ marcheazaDiscrepante(): void`, `+ reconciliaza(): void`, `+ contabilizeaza(): void`, `+ storneaza(motiv: String): void`, `+ calculeazaCantitateReceptionata(material: CodMaterial): BigDecimal`
* **Aggregate Root 3 — `OrdinPlata`:**
  * Atribute: `- idOrdin: UUID`, `- facturaId: UUID`, `- suma: Suma`, `- creatDe: UUID`, `- aprobatDe: UUID`, `- stareOrdin: StareOrdinPlata`
  * Constructor package-private (`~`): `~ OrdinPlata(facturaId: UUID, suma: Suma, creatDe: UUID)` (poate fi instanțiat doar de `FacturaFurnizor.creeazaOrdinPlata()`)
  * Metode: `+ necesitaAprobareSuplimentara(prag: Suma): boolean`, `+ trimiteLaAprobare(): void`, `+ aproba(aprobator: UUID): void`, `+ transmiteLaBanca(): void`, `+ inregistreazaExecutie(): PlataExecutataEvent`, `+ respingeDeBanca(motiv: String): void`, `+ anuleaza(): void`
* **Value Objects:**
  * `LinieFactura`: `- material: CodMaterial`, `- cantitate: BigDecimal`, `- unitateMasura: String`, `- pretUnitar: Suma`, `- cotaTVA: BigDecimal`
  * `LinieNIR`: `- idLot: IdLot`, `- material: CodMaterial`, `- cantitateReceptionata: BigDecimal`, `- unitateMasura: String`
  * `ValoareFactura`: `- valoareFaraTVA: BigDecimal`, `- valoareTVA: BigDecimal`, `- valoareTotala: BigDecimal`, `+ {static} calculeaza(linii: List<LinieFactura>): ValoareFactura`
  * `Suma`: `- valoare: BigDecimal`, `- moneda: Currency`, `+ adauga(alta: Suma): Suma`, `+ scade(alta: Suma): Suma`, `+ esteMaiMareDecat(alta: Suma): boolean`
  * `Toleranta`: `- procent: BigDecimal`, `+ acopera(referinta: BigDecimal, valoare: BigDecimal): boolean`
  * `LinieSumar`: `- material: CodMaterial`, `- cantitateComandata: BigDecimal`, `- pretUnitar: BigDecimal`
  * `ComandaSummaryVO`: `- comandaId: IdComanda`, `- linii: List<LinieSumar>`
  * `RezultatReconciliere`: `- aprobata: boolean`, `- diferente: List<String>`
* **Domain Service:**
  * `ReconciliereService`: `+ reconciliaza(f: FacturaFurnizor, nir: NIR, comanda: ComandaSummaryVO, t: Toleranta): RezultatReconciliere`
* **Repositories & Ports:**
  * `FacturaRepo`, `NIRRepo` (`findByComandaId`, `existaNIRPentruEvent`), `OrdinPlataRepo`, `FurnizoriBlocatiRepo` (`adauga`, `esteBlocat`)
  * `IComenziAdapter` `<<Port>>`: `+ obtineDateComanda(id: IdComanda): ComandaSummaryVO`
  * `IProcesatorBancarAdapter` / `PaymentProvider` `<<Port>>`: pentru integrarea cu **Stripe Sandbox** la apelul `transmiteOrdin()` / webhook-ul `inregistreazaExecutiePlata()`.
* **Domain Events:**
  * `PlataExecutataEvent`, `ScadentaApropiataEvent`

**2. Application Layer (`facturi.application`):**
* `FacturaApplicationService`: `+ inregistreazaFactura(cmd: InregistreazaFacturaCommand): UUID`, `+ genereazaNIR(cmd: GenereazaNIRCommand): void`, `+ reconcilieazaFactura(cmd: ReconciliereCommand): void`, `+ creeazaOrdinPlata(cmd: CreeazaOrdinPlataCommand): void`, `+ aprobaOrdin(cmd): void`, `+ transmiteOrdin(cmd: TransmiteOrdinCommand): void`, `+ inregistreazaExecutiePlata(cmd): void`, `+ blocheazaFacturiComandaAnulata(cmd): void`, `+ blocheazaPlatiFurnizor(cmd): void`

**3. Infrastructure Layer (`facturi.infrastructure`):**
* `FacturaRepoJPA`, `NIRRepoJPA`, `OrdinPlataRepoJPA`, `FurnizoriBlocatiRepoJPA`, `OutboxRepoJPA`, `InboxRepoJPA`
* `ComenziAdapterRest` (ACL ce primește `ComandaSummaryDTO` de la `GET /api/comenzi/{id}/sumar` și creează `ComandaSummaryVO`)
* `StripeBancaAdapter` (implementează portul bancar folosind Stripe Payouts / PaymentIntents în Sandbox pentru a simula banca din diagrama Use Case Figura 5)
* `JurnalAuditAdapterRest`, `OutboxDispatcherJMS`, `JMSMessageReceiver`

**4. Presentation Layer (`facturi.presentation`):**
* `FacturaCtrl`: `+ inregistreazaFactura(req)`, `+ reconcilieazaFactura(id)`, `+ creeazaOrdinPlata(id, req)`, `+ aprobaOrdin(id)`, `+ transmiteOrdin(id)`

---

## 3. Structura Completă a Repository-ului (Enterprise Blueprint)

```text
supply-chain-p2p-system/
│
├── docs/
│   ├── 01-project-foundation/       # Vision, Scope, Stakeholders, RACI Matrix (3 studenti), AI Policy
│   ├── 02-business-analysis/        # BR-PO, BR-SQ, BR-IP, NFR SLA Matrix (Table 1.2), Gherkin (.feature)
│   ├── 03-system-analysis/          # Ubiquitous Language (Table 1.4), Context Map (Fig 1), Activity (Fig 2), Use Cases (Fig 3-5)
│   ├── 04-architecture/             # Layered Architecture (Fig 7-10), Sync/Async Integration (Fig 11-21), Design Patterns
│   ├── 05-security/                 # Zero Trust, OWASP Top 10, NIS2, RBAC Matrix, Threat Modeling, Audit GDPR
│   ├── 06-devops/                   # Git Flow, Commit Convention, GitHub Actions CI/CD Pipeline, Quality Gates
│   ├── 07-testing/                  # Testing Pyramid, Cucumber/Gherkin, JUnit 5, ArchUnit Rules, Testcontainers
│   ├── 08-api/                      # OpenAPI 3.0 Specs, JSON & XML Content Negotiation, Published Language DTOs
│   ├── 09-deployment/               # Docker, Docker Compose (PostgreSQL + RabbitMQ/ActiveMQ), Tomcat 9/10
│   ├── 10-microservices-roadmap/    # Planul de spargere din Modular Monolith în 4 Microservicii la MSD
│   └── appendices/                  # Traceability Matrix (Requirement -> UC -> Class -> Test), Coding Standards
│
├── adr/
│   ├── ADR-001-domain-design-sprint1.md
│   ├── ADR-002-c4-architecture-model.md
│   ├── ADR-003-business-model-implementation-sprint2.md
│   ├── ADR-004-modular-monolith-and-ddd.md
│   ├── ADR-005-persistence-model-jpa-sprint3.md
│   ├── ADR-006-transactional-outbox-and-idempotent-inbox.md
│   ├── ADR-007-rest-api-exposure-sprint4.md
│   ├── ADR-008-rest-xml-json-negotiation.md
│   ├── ADR-009-soa-principles-and-service-catalog.md
│   └── ADR-010-stripe-bank-adapter-and-nis2-security.md
│
├── diagrams/
│   ├── plantuml/                    # Cele 27 figuri PlantUML extrase 1:1 din Project_OOAD.docx
│   ├── c4/                          # Context, Container, Component Diagrams
│   └── bpmn/                        # Procure-to-Pay BPMN Flows
│
├── project-management/
│   ├── backlog/
│   ├── sprint-1/
│   ├── sprint-2/
│   ├── sprint-3/
│   ├── sprint-4/
│   └── retrospectives/
│
├── docker/
│   ├── app/Dockerfile
│   ├── postgres/init.sql
│   ├── rabbitmq/definitions.json
│   └── docker-compose.yml
│
├── tools/
│   ├── checkstyle/checkstyle.xml
│   ├── pmd/pmd-ruleset.xml
│   ├── spotbugs/excludeFilter.xml
│   └── postman/SupplyChain_P2P_Collection.json
│
├── .github/
│   └── workflows/
│       ├── build.yml
│       ├── unit-tests.yml
│       ├── archunit.yml
│       ├── integration-tests.yml
│       ├── security-scan.yml
│       └── quality-gate.yml
│
├── src/
│   ├── main/
│   │   ├── java/ro/uaic/feaa/sdbis/supplychain/
│   │   │   ├── SupplyChainApplication.java
│   │   │   ├── shared/              # IdComanda, IdFurnizor, IdLot, CodMaterial, Perioada, DomainEvent, OutboxRepo, InboxRepo, IJurnalAudit, AbstractRepositoryJPA
│   │   │   ├── comenzi/             # Core Domain (presentation, application, domain, infrastructure)
│   │   │   ├── calitate/            # Supporting 1 (presentation, application, domain, infrastructure)
│   │   │   ├── facturi/             # Supporting 2 (presentation, application, domain, infrastructure)
│   │   │   ├── notificari/          # Generic 1 (Dispatcher Notificari Email/Push)
│   │   │   └── securitate/          # Generic 2 (RBAC Filter, JurnalAudit Imutabil)
│   │   └── resources/
│   │       ├── META-INF/persistence.xml
│   │       ├── db/migration/        # V1__shared_outbox_inbox.sql, V2__comenzi.sql, V3__calitate.sql, V4__facturi.sql
│   │       ├── features/            # Cele 6 fisiere .feature Gherkin din Project_OOAD.docx (Sectiunea 1.2.5)
│   │       ├── application.properties
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       ├── logback-spring.xml
│   │       └── openapi/openapi.yaml
│   │
│   └── test/
│       └── java/ro/uaic/feaa/sdbis/supplychain/
│           ├── architecture/        # ArchUnit (LayerArchitectureTest, BoundedContextIsolationTest, DomainIsolationTest, AggregateRulesTest)
│           ├── unit/                # JUnit 5 + Mockito (Validation, Computation, Workflow Services - Sprint 2)
│           ├── bdd/                 # Cucumber Step Definitions pentru cele 6 fisiere Gherkin
│           ├── integration/         # Testcontainers (PostgreSQL + RabbitMQ/JMS + JPA Repos - Sprint 3)
│           └── rest/                # RestTemplate & Apache HttpClient Tests (JSON + XML - Sprint 4)
│
├── .cursorrules
├── ANTIGRAVITY_AI_DEVELOPMENT_CONFIGURATION.md
├── BEST_PRACTICES_GUIDE.md
├── README.md
└── pom.xml
```

---

## 4. Planul Complet de Implementare pe Etape Numerotate și Sprints (Sprints 0–4 + Microservicii)

Planul de mai jos corelează **fazele din `IMPLEMENTATION_ROADMAP.md`** cu **livrabilele obligatorii din `sprint1`–`sprint4`** și cu **specificațiile exacte din `Project_OOAD.docx`**.

---

### ETAPA 0: Inițializare Repository, DevSecOps & ArchUnit-First (Săptămâna 1)

**Obiectiv:** Pregătirea fundației tehnice, a regulilor ArchUnit și a pipeline-ului CI/CD înainte de a scrie logica de business, astfel încât niciun student (și niciun tool AI) să nu poată comite cod care încalcă arhitectura din `.docx`.

1. **Pasul 0.1 — Inițializare Git & Documente de Guvernanță:**
   * Creare repository Git cu branch-urile `main` și `develop` + reguli de protecție (Pull Request obligatoriu).
   * Adăugare `README.md`, `AI_USAGE_POLICY.md`, `BEST_PRACTICES_GUIDE.md`, `.cursorrules`.
2. **Pasul 0.2 — Configurare Maven (`pom.xml`) & Java 21:**
   * Dependențe: Spring Boot 3.x (`spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-data-jpa`, `spring-boot-starter-amqp` / `artemis-jms`, `spring-boot-starter-security`, `spring-boot-starter-actuator`), Jackson XML + JAXB (`jackson-dataformat-xml`, `jackson-module-jaxb-annotations`, `jakarta.xml.bind-api`), Stripe Java SDK (`stripe-java`), Flyway, PostgreSQL Driver, JUnit 5, Mockito, Cucumber JVM, ArchUnit (`archunit-junit5`), Testcontainers.
3. **Pasul 0.3 — Crearea Scheletului de Pachete (Package Skeleton):**
   * Crearea pachetelor `shared`, `comenzi`, `calitate`, `facturi`, `notificari`, `securitate` și a sub-pachetelor `presentation`, `application`, `domain`, `infrastructure`.
4. **Pasul 0.4 — Scrierea Testelor ArchUnit („Architecture as Code”):**
   * `LayerArchitectureTest`: verifică faptul că `presentation` apelează doar `application`, `application` apelează `domain`, iar `infrastructure` depinde de `domain`. Interzice `Controller -> Repository`.
   * `BoundedContextIsolationTest`: verifică faptul că pachetele `comenzi`, `calitate` și `facturi` **NU** au nicio referință directă între ele, ci importă doar `shared`.
   * `DomainIsolationTest`: verifică faptul că clasele din `..domain..` nu importă `org.springframework..`, `com.stripe..`, `jakarta.jms..` sau `com.fasterxml.jackson..`.
   * `AggregateEncapsulationTest`: verifică faptul că metodele de mutație din `Lot` și constructorul din `OrdinPlata` sunt *package-private* (`~`), conform diagramei de clase din `.docx`.
5. **Pasul 0.5 — Configurare Docker Compose & GitHub Actions CI/CD:**
   * Definire `docker/docker-compose.yml` cu PostgreSQL 16 și RabbitMQ 3 (cu management plugin).
   * Configurare `.github/workflows/` pentru rularea automată la fiecare PR a compilării, testelor JUnit, ArchUnit, Checkstyle și OWASP Dependency Check.

---

### SPRINT 1: Business Domain Design & OOAD Modeling (`ADR-001` & `ADR-002`)

**Obiectiv Sprint 1:** Definirea și documentarea completă a cerințelor funcționale/non-funcționale, a proceselor de business, a modelului domeniului și a regulilor de business (100% aliniat cu Capitolele 1, 2 și 6 din `Project_OOAD.docx`).

#### Etapele Numerotate din Sprint 1:
1. **Etapa 1.1 — Documentarea Viziunii, Subdomeniilor și a Limbajului Ubicuu (`docs/01` & `docs/02`):**
   * Transpunerea Secțiunii 1.1 din `.docx`: cele 3 subdomenii funcționale, catalogul ciclurilor de viață ale celor 8 obiecte principale (**Tabelul 1.1**) și matricea cerințelor non-funcționale măsurabile / SLA (**Tabelul 1.2**: $<300\text{ ms}$ pentru lead time pe 1.000 livrări; $5.000$ facturi reconciliate în $<60\text{ s}$; audit GDPR imutabil pe IBAN/prețuri/plăți; anulare atomică ACID; consumatori idempotenți; control financiar $100\%$).
   * Documentarea Glosarului Limbajului Ubicuu (**Tabelul 1.4**).
2. **Etapa 1.2 — Strategic DDD & Context Mapping (`docs/03-system-analysis`):**
   * Generarea diagramei **Strategic DDD Context Map (Figura 1)** și a **Tabelului 1.3** cu cele 10 contracte de integrare (Shared Kernel, Customer/Supplier sincron REST, Domain Events asincron prin Outbox, Open Host Service + Published Language).
3. **Etapa 1.3 — Modelarea Procesului de Business & Use Cases (`diagrams/uml`):**
   * Generarea **Diagramei de Activitate (Figura 2)** pe 3 swimlanes cu regiunea de activitate întreruptibilă (*interruptible activity region*) pentru anularea comenzii.
   * Generarea celor **3 Diagrame Use Case (Figurile 3, 4 și 5)** cu relațiile `<<include>>` și `<<extend>>` (cu condițiile de gardă) pentru:
     * *Managementul comenzilor de aprovizionare* (10 use cases de bază + extensii)
     * *Gestiunea furnizorilor și a calității* (9 use cases + extensii)
     * *Gestiunea facturilor și plăților* (8 use cases + extensii)
   * Redactarea descrierilor narative complete pentru **CU1 (`CreeazaComandaAprovizionare`)**, **CU2 (`ExecutaInspectieCalitate`)** și **CU3 (`ReconcilieazaFactura`)** (Secțiunea 1.2.4).
4. **Etapa 1.4 — Specificații Executabile BDD în Gherkin (`src/main/resources/features/`):**
   * Crearea celor 6 fișiere `.feature` exact ca în Secțiunea 1.2.5:
     1. `procurement_order_creation_and_cancellation.feature`
     2. `goods_reception_by_lots.feature`
     3. `supplier_monthly_scorecard.feature`
     4. `lot_quality_inspection.feature`
     5. `nir_and_three_way_match.feature`
     6. `payment_orders.feature`
5. **Etapa 1.5 — Modelul Conceptual al Domeniului, Mașini de Stări, Secvențe și C4 (`diagrams/`):**
   * Generarea **Diagramei de Clase a Domeniului (Figura 6)** pe cele 4 pachete (`Shared Kernel`, `Core Domain`, `Supporting 1`, `Supporting 2`).
   * Documentarea Regulilor de Business și a Matricelor de Invarianți (**Tabelele 2.1, 2.2 și 2.3**):
     * *Validation Rules:* eligibilitate furnizor (`Aprobat`/`Condiționat`), `numarRespingere = numarAcceptare + 1` în `PlanAQL`, unicitate scorecard pe lună, validare IBAN/Email/Perioada, încadrare în toleranță $\pm 2\%$.
     * *Computation Rules:* calcul `Scor.ponderat()` ($40\%$ OTIF + $40\%$ calitate + $20\%$ preț) și mapare în `Calificativ` (`A`, `B`, `C`, `D`); calcul `ValoareFactura` cu TVA; calcul `calculeazaCantitatePrimita()` și `calculeazaRestDePlata()`; algoritmii de estimare lead time (`EstimareLeadTimeService`).
     * *Workflow / Additional Rules:* anulare în cascadă a loturilor nerecepționate, suspendare furnizor condiționat la 2 inspecții neconforme consecutive, revenire în `Aprobat` după 3 luni consecutive cu `B` sau `A`, aprobare suplimentară ordin de plată peste prag ($10.000\text{ EUR}$).
   * Generarea **Diagramelor State Machine (Figurile 22, 23, 24)** pentru `ComandaAprovizionare`, `InspectieCalitate` și `FacturaFurnizor`.
   * Generarea **Diagramelor de Secvență (Figurile 25, 26, 27)** și a **Diagramelor C4** (Level 1 System Context, Level 2 Container, Level 3 Component).

#### Checklist Definition of Done — Sprint 1:
- [x] Use Cases, Narratives (CU1, CU2, CU3) și cele 6 fișiere Gherkin `.feature` create
- [x] Domain Entities, Value Objects și Aggregate Roots identificate (Figura 6)
- [x] Validation Rules, Computation Rules și Invariants documentate (Tabelele 2.1–2.3)
- [x] Use Case Diagrams (Fig. 3–5), Activity Diagram (Fig. 2), State Diagrams (Fig. 22–24), Sequence Diagrams (Fig. 25–27) și C4 Diagrams finalizate

---

### SPRINT 2: Business Model Implementation & Unit Testing (`ADR-003`)

**Obiectiv Sprint 2:** Transformarea modelului conceptual și a regulilor de business din Sprint 1 în cod Java 21 executabil (Domain Layer + Application/Service Layer + Factories + Configurare Spring + Teste JUnit 5 cu Repository-uri In-Memory / Mockito).

#### Etapele Numerotate din Sprint 2:
1. **Etapa 2.1 — Implementarea `Shared Kernel` (`ro.uaic.feaa.sdbis.supplychain.shared`):**
   * Implementarea claselor imutabile Value Object: `IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial` și `Perioada` (cu validarea în constructor că `dataFinalizare >= dataInceput` și metoda `esteInclusaIn(Perioada alta)`).
   * Definirea interfețelor `DomainEvent`, `OutboxRepo`, `InboxRepo` și `IJurnalAudit`.
2. **Etapa 2.2 — Implementarea Domain Layer în Modulul `comenzi` (Student 1):**
   * Crearea enum-urilor `StareComanda`, `StareLot`, `RezultatCalitateLot`.
   * Implementarea Value Objects: `Cantitate` (adunare doar pe aceeași unitate de măsură), `PretUnitar`, `LinieComanda` (`valoareLinie()`), `Expediere`, `EstimareLivrare` (`depasesteData()`), `MetriciLivrareVO`, `IstoricLivrariVO`.
   * Implementarea entității copil `Lot` cu toate metodele de tranziție *package-private* (`~ expediaza()`, `~ receptioneaza()`, `~ plaseazaInCarantina()`, `~ elibereazaInStoc()`, `~ respinge()`, `~ anuleaza()`, `~ esteInainteDeReceptie()`).
   * Implementarea Aggregate Root-ului `ComandaAprovizionare` cu toate metodele din Figura 6 și invarianții din Tabelul 2.1 (inclusiv adnotări Java Bean Validation `@NotNull`, `@NotBlank`, `@Positive` pe atribute).
   * Implementarea **Strategy Pattern** pentru calculul datei de livrare: interfața `IMetodaEstimareLeadTime`, strategiile concrete (`MedieMobilaStrategy`, `NetezireExponentialaStrategy`, `Percentila90Strategy`) și serviciul de domeniu `EstimareLeadTimeService` (`calculeazaDataLivrare`).
   * Implementarea evenimentelor `ComandaReceptionataEvent`, `ComandaAnulataEvent`, `ComandaNeconfirmataEvent`, `EstimareDepasesteCerereaEvent`, `VerdictNeaplicabilEvent`.
3. **Etapa 2.3 — Implementarea Domain Layer în Modulul `calitate` (Student 2):**
   * Crearea enum-urilor `StareFurnizor`, `StareInspectie`, `StareNCR`, `Calificativ`, `Verdict`.
   * Implementarea Value Objects cu logică încapsulată:
     * `Scor`: metoda statică `ponderat(otif, calitate, pret)` și metoda `calificativ()` (exact codul din Capitolul 2 al `.docx`).
     * `PlanAQL`: constructor ce impune invariantul ISO 2859-1 `numarRespingere = numarAcceptare + 1` și metoda `decide(int defecteGasite)`.
     * `Certificare` (`esteValidaPe(Perioada)`), `Iban` (`esteValid`), `Email` (`isValidEmail`), `Adresa`, `FullName`, `ActiuneCorectiva`, `ConditiiContractuale`, `PerformantaLivrariVO`.
   * Implementarea entității `ScorecardLunar` și a celor 4 Aggregate Roots: `Furnizor` (cu regulile pentru 3 luni consecutive `B`/`A` și suspendarea la 2 inspecții neconforme consecutive), `InspectorCalitate`, `InspectieCalitate`, `RaportNeconformitate`.
   * Implementarea serviciilor de domeniu `CalculScorecardService` și `AlocareInspectorService`.
   * Implementarea evenimentelor din `calitate`.
4. **Etapa 2.4 — Implementarea Domain Layer în Modulul `facturi` (Student 3):**
   * Crearea enum-urilor `StareFactura`, `StareNIR`, `StareOrdinPlata`.
   * Implementarea Value Objects: `Suma` (`adauga`, `scade`, `esteMaiMareDecat`), `ValoareFactura` (`calculeaza`), `LinieFactura`, `LinieNIR`, `Toleranta` (`acopera` pentru $\pm 2\%$), `LinieSumar`, `ComandaSummaryVO`, `RezultatReconciliere`.
   * Implementarea celor 3 Aggregate Roots:
     * `FacturaFurnizor`: metoda factory `creeazaOrdinPlata(Suma suma, UUID creatDe)` care verifică `esteAprobataLaPlata()` și nu permite depășirea `calculeazaRestDePlata()`.
     * `OrdinPlata`: constructor *package-private* `~ OrdinPlata(...)`, verificare prag aprobare suplimentară (`necesitaAprobareSuplimentara(Suma prag)`), `aproba(UUID aprobator)` (verifică `aprobator != creatDe` — principiul *Four-Eyes*).
     * `NIR`: gestiune `eventSursaId` și tranziții de stare.
   * Implementarea serviciului de domeniu `ReconciliereService` (`reconciliaza`).
5. **Etapa 2.5 — Implementarea Aggregate Factories & Service Layer (Application, Validation, Computation, Workflow & Audit Services):**
   * Crearea claselor Factory (`ComandaAprovizionareFactory`, `FurnizorFactory`, `FacturaFurnizorFactory`) pentru inițializarea consistentă a agregatelor complexe.
   * Definirea interfețelor și implementărilor pentru Application / Workflow Services conform Figurilor 19, 20 și 21:
     * `ComandaApplicationService`
     * `CalitateApplicationService`
     * `FacturaApplicationService`
   * Implementarea serviciului de audit `JurnalAuditService` (implementează `IJurnalAudit`) și a claselor de configurare Spring (`@Configuration`).
6. **Etapa 2.6 — Suita de Teste JUnit 5 & Cucumber BDD pentru Sprint 2 (`src/test/java/unit`):**
   * **Validation Service Tests:** testează respingerea comenzilor pentru furnizori în stările `Candidat`, `InEvaluare`, `Suspendat`, `Blocat`; validarea `PlanAQL`; validarea `Perioada`, `Iban`, `Email`; respingerea creării `OrdinPlata` pentru facturi nereconciliate.
   * **Computation Service Tests:** testează tabelul de exemple din Gherkin pentru `Scor.ponderat()` și `Calificativ` (`92.8 -> A`, `84.0 -> B`, `71.0 -> C`, `54.0 -> D`); calculul `ReconciliereService` cu toleranța de $\pm 2\%$ (Tabelul de exemple din `nir_and_three_way_match.feature`); calculul `EstimareLeadTimeService` ($<300\text{ ms}$ pentru 1.000 livrări).
   * **Workflow Service Tests:** testează anularea atomică în cascadă a comenzii și a loturilor (`Planificat`, `Expediat` -> `Anulat`, în timp ce `SositInDepozit` rămâne neschimbat); recepția parțială vs. totală; suspendarea furnizorului condiționat după a doua inspecție neconformă consecutivă; fluxul de aprobare suplimentară pentru `OrdinPlata` $> 10.000\text{ EUR}$.

#### Checklist Definition of Done — Sprint 2:
- [x] Toate Domain Entities, Value Objects, Enums și Aggregate Roots din `.docx` implementate 1:1
- [x] Aggregate Factories, Domain Services (`EstimareLeadTimeService`, `CalculScorecardService`, `AlocareInspectorService`, `ReconciliereService`) și Application/Workflow Services implementate
- [x] Serviciul de Audit (`IJurnalAudit`) și clasele de configurare Spring create
- [x] Teste JUnit 5 complete pentru Validation, Computation și Workflow Services + rulare scenarii Gherkin

---

### SPRINT 3: Persistence Model, JPA Repositories & Asynchronous Messaging (`ADR-005`)

**Obiectiv Sprint 3:** Implementarea stratului de persistență JPA peste baza de date PostgreSQL, implementarea ierarhiei de Repository-uri din **Figura 10** (`AbstractRepositoryJPA`), configurarea `persistence.xml` (cu excluderea `DataSourceAutoConfiguration`), implementarea Entity Listeners (`@PrePersist`, `@PreUpdate`, `@PreRemove`) și a mecanismului Transactional Outbox + Idempotent Inbox.

#### Etapele Numerotate din Sprint 3:
1. **Etapa 3.1 — Configurare Persistence Context (`persistence.xml` & Spring):**
   * Configurarea clasei principale cu `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)` conform cerinței din `sprint3.md`.
   * Crearea fișierului `src/main/resources/META-INF/persistence.xml` și a configurării explicite pentru `EntityManagerFactory` și `PlatformTransactionManager` cu `@PersistenceContext`.
   * Crearea scripturilor Flyway (`V1__shared_outbox_inbox.sql`, `V2__comenzi.sql`, `V3__calitate.sql`, `V4__facturi.sql`), incluzând constrângerile `UNIQUE` pe `inbox_events(event_id)` și pe `nir(event_sursa_id)` (conform notelor din Figurile 16 și 18).
2. **Etapa 3.2 — Maparea JPA a Entităților, Agregatelor și Value Objects:**
   * Maparea tuturor celor 8 Aggregate Roots (`ComandaAprovizionare`, `Furnizor`, `InspectorCalitate`, `InspectieCalitate`, `RaportNeconformitate`, `FacturaFurnizor`, `NIR`, `OrdinPlata`) și a entităților componente (`Lot`, `ScorecardLunar`) cu `@Entity`.
   * Configurarea relațiilor de agregat cu `@OneToMany(mappedBy = "...", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)`:
     * `ComandaAprovizionare` $\rightarrow$ `List<Lot> loturi`, `List<LinieComanda> linii`, `List<Expediere> expedieri`
     * `Furnizor` $\rightarrow$ `List<ScorecardLunar> scorecarduri`
     * `InspectorCalitate` $\rightarrow$ `List<Certificare> certificari`
     * `FacturaFurnizor` $\rightarrow$ `List<LinieFactura> linii`
     * `NIR` $\rightarrow$ `List<LinieNIR> linii`
   * Persistarea Value Objects folosind `@Embeddable` / `@Embedded`, `@ElementCollection`, `@Enumerated(EnumType.STRING)` (pentru toate cele 10 enum-uri din model) și `@Convert` (`AttributeConverter` pentru `IdComanda`, `IdFurnizor`, `IdLot`, `CodMaterial`, `Currency`, `Iban`, `Email`).
3. **Etapa 3.3 — Implementarea Entity Lifecycle Listeners pentru Calcul & Audit (`@EntityListeners`):**
   * Crearea `AuditAndComputationEntityListener` atașat entităților principale prin `@EntityListeners`:
     * `@PrePersist`: setează automat `createdAt`, `eventId` / `occurredOn`, verifică invarianții înainte de inserare și înregistrează crearea în jurnalul de audit.
     * `@PreUpdate`: actualizează `updatedAt`, recalculează câmpurile derivate și jurnalizează modificările de stare (ex. tranziția în `Anulat` sau `Blocat`).
     * `@PreRemove`: blochează ștergerea fizică a comenzilor/facturilor aflate în execuție și înregistrează tentativa în audit.
4. **Etapa 3.4 — Implementarea Ierarhiei de Repository-uri (1:1 cu Figura 10 din `.docx` + Strategiile din `sprint3.md`):**
   * Implementarea clasei de bază `AbstractRepositoryJPA` (Strategia 1 & 2 din `sprint3.md`, folosind `@PersistenceContext protected EntityManager em;` și metode generice CRUD cu introspecție/JPQL).
   * Implementarea tuturor celor **13 clase concrete JPA** din **Figura 10**:
     * În `comenzi`: `ComandaRepoJPA`, `LotRepoJPA` (exclusiv read-only), `IstoricLivrariRepoJPA` (read-only)
     * În `calitate`: `FurnizorRepoJPA`, `InspectorRepoJPA`, `InspectieRepoJPA`, `NCRRepoJPA`
     * În `facturi`: `FacturaRepoJPA`, `NIRRepoJPA`, `OrdinPlataRepoJPA`, `FurnizoriBlocatiRepoJPA`
     * Transversal (în fiecare context): `OutboxRepoJPA`, `InboxRepoJPA`
   * Adăugarea metodelor custom de interogare cu `@Query` / JPQL (`existaNumarComanda`, `findByComandaId`, `existaNIRPentruEvent`, `obtineIstoric`, `pollUnpublishedEvents`).
5. **Etapa 3.5 — Implementarea Transactional Outbox & Idempotent Inbox (Figurile 15–18 din `.docx`):**
   * Implementarea `OutboxDispatcherJMS` (`publicaEvenimente()`) și `JMSMessageReceiver` (`onMessage()`) pentru livrarea *at-least-once* între `comenzi`, `calitate`, `facturi` și `notificari`.
   * Refactorizarea serviciilor `ComandaApplicationService`, `CalitateApplicationService` și `FacturaApplicationService` pentru a salva starea agregatului și evenimentul în `OutboxRepo` în aceeași tranzacție `@Transactional` ACID.
6. **Etapa 3.6 — Teste de Integrare cu Testcontainers pentru Sprint 3 (`src/test/java/integration`):**
   * Teste automate pentru toate cele 13 repository-uri JPA pe un container real PostgreSQL.
   * Test de atomicitate ACID la `anuleazaComanda()` (dacă un lot eșuează, întreaga tranzacție face rollback).
   * Test de idempotență la relivrarea aceluiași `ComandaReceptionataEvent` (verifică faptul că `existaNIRPentruEvent` și constrângerea `UNIQUE` previn crearea unui al doilea `NIR`).
   * Test de performanță pentru reconcilierea în batch a $5.000$ de facturi în $<60$ secunde.

#### Checklist Definition of Done — Sprint 3:
- [x] Toate entitățile, agregatele și Value Objects mapate cu JPA (`@Entity`, `@OneToMany`, `cascade = CascadeType.ALL`, `orphanRemoval = true`, `@Enumerated`, `@Convert`)
- [x] `persistence.xml` configurat și `DataSourceAutoConfiguration` exclus
- [x] Ierarhia `AbstractRepositoryJPA` și toate cele 13 clase `*RepoJPA` din Figura 10 implementate
- [x] `@EntityListeners` cu `@PrePersist`, `@PreUpdate`, `@PreRemove` active
- [x] Teste de integrare pentru Repository-uri și Application Services verzi

---

### SPRINT 4: SOA, REST API Exposure, Content Negotiation (JSON/XML) & Security (`ADR-007`, `ADR-008`, `ADR-009`)

**Obiectiv Sprint 4:** Expunerea funcționalităților de CRUD și a fluxurilor de business (REST-RPC) prin controllere REST și Open Host Services, implementarea stratului DTO cu suport dublu **JSON (Jackson)** și **XML (JAXB)** cu evitarea ciclurilor infinite, integrarea adaptoarelor sincrone REST între Bounded Contexts, integrarea Stripe Sandbox pentru execuția ordinelor de plată și activarea securității RBAC + Audit GDPR.

#### Etapele Numerotate din Sprint 4:
1. **Etapa 4.1 — Clasificarea SOA a Serviciilor (`ADR-009` / `sprint4_soa.md`):**
   * Documentarea și organizarea Catalogului de Servicii conform celor 8 principii SOA:
     * **Orchestration / Workflow Services:** `ComandaApplicationService`, `CalitateApplicationService`, `FacturaApplicationService`
     * **Task / Domain Services:** `EstimareLeadTimeService`, `CalculScorecardService`, `AlocareInspectorService`, `ReconciliereService`
     * **Entity / Repository Services:** `ComandaRepo`, `FurnizorRepo`, `InspectorRepo`, `InspectieRepo`, `NCRRepo`, `FacturaRepo`, `NIRRepo`, `OrdinPlataRepo` + Factory-urile asociate
     * **Utility Services:** `JurnalAuditService` (`IJurnalAudit`), `NotificariDispatcherService`
2. **Etapa 4.2 — Arhitectura DTO & Serializare Dublă JSON + XML (`ADR-007` & `ADR-008`):**
   * Configurarea `WebConfig` (`WebMvcConfigurer`) cu `MappingJackson2HttpMessageConverter` (JSON), `MappingJackson2XmlHttpMessageConverter` (XML) și `JaxbAnnotationModule` pentru Content Negotiation (`Accept: application/json` și `Accept: application/xml`).
   * Crearea claselor DTO de Request/Response și a DTO-urilor din *Published Language* (`ComandaSummaryDTO`, `PerformantaLivrariDTO`) adnotate atât cu Jackson (`@JsonRootName`, `@JsonProperty`, `@JsonIgnoreProperties`), cât și cu JAXB (`@XmlRootElement`, `@XmlElement`, `@XmlAttribute`, `@XmlElementWrapper`).
   * Prevenirea referințelor circulare părinte $\leftrightarrow$ copil în serializarea agregatelor (`ComandaAprovizionare` $\leftrightarrow$ `Lot`, `Furnizor` $\leftrightarrow$ `ScorecardLunar`, `FacturaFurnizor` $\leftrightarrow$ `LinieFactura`) folosind `@JsonManagedReference` pe colecțiile din Aggregate Root și `@JsonBackReference` / `@XmlTransient` pe referințele inverse din entitățile copil.
3. **Etapa 4.3 — Implementarea Controllerelor REST & Open Host Endpoints (1:1 cu Figurile 19, 20, 21 și 25–27):**
   * **În modulul `comenzi`:**
     * `ComandaCtrl` (`@RestController @RequestMapping("/api/comenzi")`):
       * `POST /api/comenzi` $\rightarrow$ `creeazaComanda(req)` (returnează `201 Created` sau `422 Unprocessable Entity` dacă furnizorul este neeligibil — Figura 25)
       * `GET /api/comenzi` & `GET /api/comenzi/{id}` & `PUT /api/comenzi/{id}` & `DELETE /api/comenzi/{id}` (operații CRUD pe resursă)
       * `POST /api/comenzi/{id}/confirmare` $\rightarrow$ `confirmaComanda(id, req)`
       * `POST /api/comenzi/{id}/expedieri` $\rightarrow$ `inregistreazaExpediere(id, req)`
       * `POST /api/comenzi/{id}/receptii` $\rightarrow$ `inregistreazaReceptie(id, req)`
       * `POST /api/comenzi/{id}/anulare` $\rightarrow$ `anuleazaComanda(id)`
       * `POST /api/comenzi/{id}/estimare-livrare` $\rightarrow$ `estimeazaDataLivrare(id)`
     * `ComenziApiRest` (Open Host Service pentru integrarea sincronă — Figura 14):
       * `GET /api/comenzi/{id}/sumar` $\rightarrow$ returnează `ComandaSummaryDTO`
       * `GET /api/comenzi/performanta` $\rightarrow$ returnează `PerformantaLivrariDTO`
   * **În modulul `calitate`:**
     * `CalitateCtrl` (`@RestController @RequestMapping("/api")`):
       * CRUD pe `/api/furnizori`, `/api/inspectori`, `/api/inspectii`, `/api/ncr`
       * `POST /api/inspectii/{id}/alocare` $\rightarrow$ `alocaInspector(id, req)`
       * `POST /api/inspectii/{id}/start` $\rightarrow$ `incepeInspectie(id)` (Figura 26)
       * `POST /api/inspectii/{id}/verdict` $\rightarrow$ `inregistreazaVerdict(id, req)` (Figura 26)
       * `POST /api/furnizori/scorecard-lunar` $\rightarrow$ `calculeazaScorecard(luna)`
     * `FurnizoriApiRest` (Open Host Service — Figura 12):
       * `GET /api/furnizori/{id}/eligibil` $\rightarrow$ returnează `boolean`
   * **În modulul `facturi`:**
     * `FacturaCtrl` (`@RestController @RequestMapping("/api")`):
       * CRUD pe `/api/facturi`, `/api/nir`, `/api/ordine-plata`
       * `POST /api/facturi` $\rightarrow$ `inregistreazaFactura(req)`
       * `POST /api/facturi/{id}/reconciliere` $\rightarrow$ `reconcilieazaFactura(id)` (returnează `200 OK`, `409 Conflict` dacă nu există NIR, sau `404 Not Found` dacă nu există comanda — Figura 27)
       * `POST /api/facturi/{id}/ordine-plata` $\rightarrow$ `creeazaOrdinPlata(id, req)`
       * `POST /api/ordine-plata/{id}/aprobare` $\rightarrow$ `aprobaOrdin(id)` (Use case *Aprobare Suplimentară Ordin* de către `Second Approver`)
       * `POST /api/ordine-plata/{id}/transmitere` $\rightarrow$ `transmiteOrdin(id)` (apelează `StripeBancaAdapter`)
       * `POST /api/ordine-plata/webhook-banca` $\rightarrow$ `inregistreazaExecutiePlata(req)`
4. **Etapa 4.4 — Implementarea Adaptoarelor REST (Anti-Corruption Layer) între Contexte:**
   * `GestiuneFurnizoriAdapterRest` (în `comenzi.infrastructure`): apelează `GET /api/furnizori/{id}/eligibil`.
   * `ComenziAdapterRest` (în `facturi.infrastructure`): apelează `GET /api/comenzi/{id}/sumar` **înainte** de deschiderea tranzacției de bază de date (exact cum cere nota din Figura 27 pentru scalabilitate) și traduce `ComandaSummaryDTO` în `ComandaSummaryVO`.
   * `PerformantaLivrariAdapterRest` (în `calitate.infrastructure`): apelează `GET /api/comenzi/performanta` și traduce `PerformantaLivrariDTO` în `PerformantaLivrariVO`.
5. **Etapa 4.5 — Securitate Zero Trust, RBAC & Audit GDPR (OWASP & NIS2):**
   * Configurarea `@EnableWebSecurity` cu un filtru stateless ușor (bazat pe header-ul `X-User-Role` și `X-User-Id` sau Basic/JWT, astfel încât să nu fie nevoie de un UI complex de login, dar să fie vizibilă separarea strictă a rolurilor din `.docx`):
     * `ROLE_BUYER`: acces la creare/anulare comenzi, expediere, monitorizare OTIF.
     * `ROLE_WAREHOUSE_KEEPER`: acces la recepția mărfii pe loturi.
     * `ROLE_SUPPLIER`: acces la confirmarea/refuzul comenzii.
     * `ROLE_QUALITY_MANAGER`: acces la calificarea furnizorilor, alocarea inspectorilor, gestiunea NCR.
     * `ROLE_QUALITY_INSPECTOR`: acces la execuția inspecției și înregistrarea verdictului.
     * `ROLE_ACCOUNTANT`: acces la înregistrarea facturilor, reconciliere 3-way match și contabilizare NIR.
     * `ROLE_FINANCE_OFFICER`: acces la crearea și transmiterea ordinelor de plată și la vizualizarea IBAN-ului.
     * `ROLE_SECOND_APPROVER`: acces la aprobarea suplimentară a ordinelor de plată $> 10.000\text{ EUR}$ (cu validarea că `X-User-Id` $\neq$ `creatDe`).
   * Fiecare acces la IBAN, prețuri contractuale negociate și ordine de plată apelează automat `IJurnalAudit.inregistreazaAcces(...)` (conform Tabelului 1.2 — Security & GDPR).
6. **Etapa 4.6 — Testarea Automată a Serviciilor REST (`RestTemplate` & `Apache HttpClient`):**
   * Crearea suitei de teste JUnit pentru endpoint-urile REST folosind atât `RestTemplate` (cerința `sprint4_cerinte.md`), cât și `Apache HttpClient` (`HttpGet`, `HttpPost`, `HttpPut`, `HttpDelete` cu `HttpClientBuilder` și `ObjectMapper` — cerința `sprint4_constrangeri.md`).
   * Testarea ambelor formate de Content Negotiation (`application/json` și `application/xml`) și a codurilor HTTP (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `409 Conflict`, `422 Unprocessable Entity`).
   * Exportarea colecției Postman în `tools/postman/SupplyChain_P2P_Collection.json`.

#### Checklist Definition of Done — Sprint 4:
- [x] Aplicația rulează ca Spring Boot Application și este deployabilă pe Apache Tomcat 9/10
- [x] Toate Aggregate Root-urile și use case-urile principale sunt expuse prin `@RestController` (`ComandaCtrl`, `CalitateCtrl`, `FacturaCtrl`, `ComenziApiRest`, `FurnizoriApiRest`)
- [x] DTO-urile suportă serializare/deserializare JSON (Jackson) și XML (JAXB) fără cicluri infinite
- [x] Adaptoarele REST sincrone și mesageria asincronă conectează complet cele 3 Bounded Contexts
- [x] Teste automate cu `RestTemplate`, `Apache HttpClient` și colecție Postman validate

---

### ETAPA 5: Spargea în Microservicii pentru Evaluarea Finală MSD (`docs/10-microservices-roadmap`)

Deoarece încă din Etapa 0 și Sprinturile 1–4 am respectat cu strictețe izolarea din **Figura 1** și **Figurile 19–21** (fără referințe directe între clasele celor 3 contexte, doar `Shared Kernel` + apeluri REST prin `IGestiuneFurnizoriAdapter`, `IComenziAdapter`, `IPerformantaLivrariAdapter` + mesaje JMS/RabbitMQ prin `OutboxDispatcherJMS`), extragerea în microservicii la finalul semestrului pentru MSD se face în **4 pași mecanici, fără rescrierea codului de domeniu sau de aplicație**:

1. **Pasul 5.1 — Transformarea în Maven Multi-Module:**
   * Pachetul `shared` devine artefactul Maven `supplychain-shared-kernel`.
   * Pachetul `notificari` devine microserviciul independent `notificari-service` (Port `8084`) — consumă evenimente din RabbitMQ.
   * Pachetul `calitate` devine microserviciul independent `calitate-service` (Port `8082`, DB proprie `calitate_db`).
   * Pachetul `facturi` devine microserviciul independent `facturi-service` (Port `8083`, DB proprie `facturi_db`).
   * Pachetul `comenzi` devine microserviciul independent `comenzi-service` (Port `8081`, DB proprie `comenzi_db`).
2. **Pasul 5.2 — Actualizarea URL-urilor din Adaptoarele REST (`application.properties`):**
   * În `comenzi-service`: `adapter.calitate.url=http://calitate-service:8082` (folosit de `GestiuneFurnizoriAdapterRest`).
   * În `facturi-service`: `adapter.comenzi.url=http://comenzi-service:8081` (folosit de `ComenziAdapterRest`).
   * În `calitate-service`: `adapter.comenzi.url=http://comenzi-service:8081` (folosit de `PerformantaLivrariAdapterRest`).
3. **Pasul 5.3 — Adăugarea unui API Gateway (Spring Cloud Gateway):**
   * Rutează `/api/comenzi/**` $\rightarrow$ `8081`, `/api/furnizori/**` & `/api/inspectii/**` $\rightarrow$ `8082`, `/api/facturi/**` & `/api/ordine-plata/**` $\rightarrow$ `8083` și propagă `X-Correlation-Id` și header-ele de securitate RBAC.
4. **Pasul 5.4 — Orchestrare în `docker-compose.microservices.yml`:**
   * Pornirea simultană a celor 4 microservicii, PostgreSQL, RabbitMQ și Prometheus/Grafana.

---

## 5. Matricea de Trasabilitate End-to-End (OOAD $\leftrightarrow$ MSD)

Această matrice (de inclus în `docs/appendices/traceability-matrix.md`) demonstrează profesorilor de la OOAD și MSD alinierea 1:1 între cerințe, diagrame, cod, Design Patterns și teste:

| Cerință / SLA (`Project_OOAD.docx`) | Use Case & Scenariu Gherkin | Clase & Metode implicate (1:1 cu `.docx`) | Design Patterns Aplicate | Teste de Verificare (JUnit / ArchUnit / REST) |
| :--- | :--- | :--- | :--- | :--- |
| **BR-PO-01:** Plasare comandă doar cu furnizor `Aprobat` sau `Condiționat` | **CU1:** `CreeazaComandaAprovizionare` (`procurement_order_creation_and_cancellation.feature`) | `ComandaCtrl.creeazaComanda` $\rightarrow$ `ComandaApplicationService.creeazaComanda` $\rightarrow$ `IGestiuneFurnizoriAdapter.esteFurnizorEligibil` $\rightarrow$ `FurnizoriApiRest` $\rightarrow$ `Furnizor.esteEligibilPentruComenzi` | **Adapter / Port (Hexagonal)**, **Aggregate Root**, **Repository**, **Factory** | `CreeazaComandaUnitTest`, `CreeazaComandaRestTest` (`201` vs `422`) |
| **NFR-SLA-01:** Estimare dată livrare în $<300\text{ ms}$ pentru $1.000$ livrări | **UC6:** `Estimare Data Livrare` (`<<include>>` în CU1 și UC5) | `ComandaAprovizionare.extrageMetrici` $\rightarrow$ `EstimareLeadTimeService.calculeazaDataLivrare` $\rightarrow$ `IMetodaEstimareLeadTime.estimeazaZile` | **Strategy Pattern**, **Domain Service**, **Value Object** (`EstimareLivrare`) | `EstimareLeadTimePerformanceTest` (verifică durata $<300\text{ ms}$) |
| **NFR-SLA-04:** Anulare atomică în cascadă a comenzii și a loturilor nerecepționate | **UC4:** `Anulare Comanda` (`procurement_order_creation_and_cancellation.feature`) | `ComandaAprovizionare.anuleazaComanda()` $\rightarrow$ `Lot.esteInainteDeReceptie()` $\rightarrow$ `Lot.anuleaza()` (`~` package-private) + `OutboxRepo.saveEvent(ComandaAnulataEvent)` | **Aggregate Root Boundary**, **Transactional Outbox**, **Domain Event** | `AnulareComandaAcidIntegrationTest`, `AggregateEncapsulationArchTest` |
| **BR-PO-04:** Recepția pe loturi și derivarea stării comenzii | **UC8:** `Receptie Marfa pe Loturi` (`goods_reception_by_lots.feature`) | `ComandaAprovizionare.inregistreazaReceptie` $\rightarrow$ `Lot.receptioneaza` $\rightarrow$ `esteAcoperitaIntegral()` $\rightarrow$ `ComandaReceptionataEvent` | **Aggregate Root**, **Transactional Outbox**, **Publish-Subscribe** | `ReceptieLoturiBddTest`, `OutboxDispatcherIntegrationTest` |
| **BR-SQ-02:** Calcul calificativ lunar ($40/40/20$) și tranziții `Condiționat` $\leftrightarrow$ `Aprobat` | **Q3:** `Calcul Calificativ Lunar` (`supplier_monthly_scorecard.feature`) | `CalitateApplicationService.calculeazaScorecardLunar` $\rightarrow$ `IPerformantaLivrariAdapter.obtinePerformanta` $\rightarrow$ `CalculScorecardService` $\rightarrow$ `Scor.ponderat` $\rightarrow$ `Furnizor.aplicaScorecard` | **Anti-Corruption Layer (ACL)**, **Static Factory Method** (`Scor.ponderat`), **Domain Service** | `ScorecardComputationTest` (testează toate valorile din tabelul Gherkin) |
| **BR-SQ-04 & 05:** Inspecție AQL, deschidere NCR și suspendare la 2 eșecuri consecutive | **CU2:** `ExecutaInspectieCalitate` (`lot_quality_inspection.feature`) | `CalitateApplicationService.inregistreazaVerdict` $\rightarrow$ `InspectieCalitate.inregistreazaVerdict` $\rightarrow$ `PlanAQL.decide` $\rightarrow$ `Furnizor.inregistreazaRezultatInspectie` + `VerdictInspectieEvent` $\rightarrow$ `ComandaAprovizionare.aplicaVerdictInspectie` | **Transactional Outbox**, **Idempotent Consumer** (`InboxRepo`), **State Pattern** | `ExecutaInspectieWorkflowTest`, `IdempotentInboxIntegrationTest` |
| **NFR-SLA-02 & 05:** Generare idempotentă NIR și Reconciliere 3-Way Match ($\pm 2\%$) | **CU3:** `ReconcilieazaFactura` (`nir_and_three_way_match.feature`) | `FacturaApplicationService.genereazaNIR` (`NIRRepo.existaNIRPentruEvent`) & `reconcilieazaFactura` $\rightarrow$ `IComenziAdapter.obtineDateComanda` $\rightarrow$ `ReconciliereService.reconciliaza` $\rightarrow$ `Toleranta.acopera` | **Anti-Corruption Layer**, **Idempotent Consumer**, **Domain Service**, **Specification / VO** | `ReconciliereThreeWayMatchTest`, `Batch5000InvoicesScalabilityTest` ($<60\text{ s}$) |
| **NFR-SLA-06:** Control financiar $100\%$ și aprobare suplimentară $> 10.000\text{ EUR}$ | **I5 & I6:** `Creare / Transmitere Ordin de Plata` (`payment_orders.feature`) | `FacturaFurnizor.creeazaOrdinPlata` $\rightarrow$ `~OrdinPlata` $\rightarrow$ `OrdinPlata.aproba` $\rightarrow$ `OrdinPlata.transmiteLaBanca` $\rightarrow$ `StripeBancaAdapter` | **Factory Method on Aggregate Root**, **Adapter Pattern** (Stripe), **Four-Eyes Security Principle** | `ControlFinanciarUnitTest`, `StripeBancaAdapterTest` |
| **NFR-SLA-03:** Segregare acces și Audit GDPR imutabil pe IBAN / prețuri / plăți | Toate use case-urile financiare și de furnizori | `IJurnalAudit.inregistreazaAcces` $\rightarrow$ `JurnalAuditAdapterRest` + `@EntityListeners` + Spring Security RBAC | **Open Host Service**, **Observer / Entity Listener**, **Zero Trust RBAC** | `SecurityRbacAndAuditTest`, `SecurityArchitectureTest` (ArchUnit) |