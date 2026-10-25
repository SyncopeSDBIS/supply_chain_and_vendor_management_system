# language: ro
Functionalitate: Executie Inspectie Calitate pe Plan AQL (CU2)
  Ca Inspector de Calitate
  Vreau sa inspectez un esantion conform standardului ISO 2859-1
  Pentru a garanta conformitatea loturilor receptionate

  Scenariul: Verdict conform pentru defecte sub numarul de acceptare
    Date fiind urmatoarele:
      | Dimensiune Esantion | Numar Acceptare | Numar Respingere | Defecte Gasite |
      | 80                  | 3               | 4                | 2              |
    Cand inspectorul inregistreaza verdictul
    Atunci inspectia este marcata "Conforma"
    Si lotul este eliberat in stocul operational

  Scenariul: Verdict neconform si deschidere automata NCR
    Date fiind urmatoarele:
      | Dimensiune Esantion | Numar Acceptare | Numar Respingere | Defecte Gasite |
      | 80                  | 3               | 4                | 5              |
    Cand inspectorul inregistreaza verdictul
    Atunci inspectia este marcata "Neconforma"
    Si se deschide automat un Raport de Neconformitate (NCR) in starea "Deschis"
    Si lotul este plasat in starea "Respins"
