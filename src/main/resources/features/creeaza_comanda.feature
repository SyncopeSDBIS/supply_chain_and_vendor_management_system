# language: ro
Functionalitate: Creare si Validare Comanda de Aprovizionare (CU1)
  Ca Referent Aprovizionare
  Vreau sa plasez o comanda de aprovizionare catre un furnizor
  Pentru a asigura stocul de materii prime in depozit

  Scenariul: Plasare reusita a comenzii pentru furnizor aprobat
    Date fiind urmatoarele detalii:
      | Furnizor  | Stare Furnizor | Material  | Cantitate | UM | Pret Unitar | Moneda |
      | ARC-001   | Aprobat        | TABLA-01  | 50.0      | TO | 120.0       | EUR    |
    Cand Referentul plaseaza comanda cu data livrarii peste 20 de zile
    Atunci comanda este inregistrata cu succes in starea "Solicitat"
    Si se genereaza un identificator unic de comanda
    Si accesul este inregistrat in jurnalul de audit

  Scenariul: Respingere comanda pentru furnizor neeligibil
    Date fiind urmatoarele detalii:
      | Furnizor  | Stare Furnizor | Material  | Cantitate | UM | Pret Unitar | Moneda |
      | ARC-002   | Candidat       | TABLA-01  | 10.0      | TO | 100.0       | EUR    |
    Cand Referentul incearca sa plaseze comanda
    Atunci cererea este respinsa cu eroarea "422 Unprocessable Entity"
    Si nicio comanda nu este salvata in sistem
