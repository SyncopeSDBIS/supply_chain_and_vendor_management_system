# language: ro
Functionalitate: Anulare Comanda in Cascada
  Ca Referent Aprovizionare
  Vreau ca anularea comenzii sa anuleze atomic toate loturile nereceptionate
  Pentru a pastra consistenta stocurilor si a tranzitului

  Scenariul: Anulare comanda cu loturi partial receptionate
    Date fiind o comanda cu 2 loturi:
      | Lot   | Cantitate | Stare Initiala  |
      | LOT-1 | 40 TO     | SositInDepozit  |
      | LOT-2 | 60 TO     | Planificat      |
    Cand Referentul anuleaza comanda
    Atunci comanda trece in starea "Anulat"
    Si LOT-1 ramane in starea "SositInDepozit"
    Si LOT-2 trece automat in starea "Anulat"
