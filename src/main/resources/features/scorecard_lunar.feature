# language: ro
Functionalitate: Calcul Calificativ Lunar si Degradare / Reabilitare Furnizor
  Ca Manager de Calitate
  Vreau evaluarea lunara automata a performantei furnizorilor

  Scenariul: Degradare automata in Conditionat la calificativ C
    Date fiind indicatorii lunari:
      | OTIF  | Rata Respingere | Conformitate Pret | Scor Ponderat | Calificativ |
      | 70%   | 10%             | 90%               | 74.0          | C           |
    Cand sistemul aplica scorecardul lunar pe un furnizor "Aprobat"
    Atunci starea furnizorului devine "Conditionat"

  Scenariul: Reabilitare dupa 3 luni consecutive cu B sau A
    Date fiind un furnizor in starea "Conditionat"
    Cand furnizorul obtine calificativul B in lunile 1, 2 si 3
    Atunci starea furnizorului revine automat la "Aprobat"
