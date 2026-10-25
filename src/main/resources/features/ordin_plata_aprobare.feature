# language: ro
Functionalitate: Creare si Aprobare Ordin de Plata (Principiul celor 4 Ochi)
  Ca Ofiter Financiar
  Vreau ca platile de valori mari sa necesite o a doua aprobare

  Scenariul: Ordin de plata sub pragul de 10.000 EUR
    Cand se creeaza un ordin de plata de 5.000 EUR
    Atunci ordinul este salvat in starea "Creat"
    Si poate fi transmis direct la banca

  Scenariul: Ordin de plata peste pragul de 10.000 EUR
    Cand se creeaza un ordin de plata de 25.000 EUR de catre Utilizator-A
    Atunci ordinul intra in starea "InAprobare"
    Si nu poate fi aprobat de Utilizator-A
    Dar poate fi aprobat de Utilizator-B (Second Approver)
