# language: ro
Functionalitate: Reconciliere in Trei Cai (Three-Way Match) (CU3)
  Ca si Contabil
  Vreau sa verific concordanta Factura vs Comanda vs NIR
  Pentru a autoriza plata doar pentru marfa comandata si receptionata conform

  Scenariul: Reconciliere cu succes in marja de toleranta
    Date fiind:
      | Cantitate Comandata | Cantitate NIR | Cantitate Facturata | Pret Comanda | Pret Factura |
      | 100.0               | 100.0         | 100.0               | 50.0 EUR     | 50.0 EUR     |
    Cand contabilitatea declanseaza reconcilierea
    Atunci factura este aprobata la plata
    Si nota de receptie devine "Reconciliat"

  Scenariul: Blocare factura din cauza discrepantei de pret
    Date fiind:
      | Cantitate Comandata | Cantitate NIR | Cantitate Facturata | Pret Comanda | Pret Factura |
      | 100.0               | 100.0         | 100.0               | 50.0 EUR     | 65.0 EUR     |
    Cand contabilitatea declanseaza reconcilierea
    Atunci abaterea de pret depaseste toleranta permisa de 0.1%
    Si factura este blocata automat
    Si NIR-ul este marcat "CuDiscrepante"
