# 🎭 Il Decadimento nell'Ultimo Dettaglio

La muffa non si ferma alla superficie dei blocchi. Penetra nelle interfacce utente, altera i suoni ambientali, compromette l'utilità magica delle librerie e — stranamente — non disturba i villici nel loro lavoro quotidiano.

---

## 🖥️ Decadimento Visivo delle Interfacce (GUI Overlays)

Aprire una stazione di lavoro infetta riflette a pieno schermo il degrado del blocco nel mondo, tramite overlay grafici a risoluzione nativa che si sovrappongono all'interfaccia vanilla:

- **Banchi da Lavoro**: la griglia 3×3 si riempie di macchie organiche e giunture mangiate.
- **Casse e Barili**: gli slot 9×3 e 9×6 mostrano bordi frastagliati dal muschio.
- **Telai e Banchi da Cartografia**: la tela assorbe umidità lungo tutti i bordi dell'interfaccia.
- **Leggii**: leggere un libro su un leggio infetto trasferisce un overlay organico ai margini delle pagine stesse.

Il rendering delle casse è stato ulteriormente raffinato: il classico *Z-fighting* (sfarfallio alla giunzione tra base e coperchio) è risolto tramite un aggiustamento millimetrico della scala del modello che transiziona fluidamente durante l'animazione di apertura.

## 🎶 Alterazione Acustica

La muffa si insinua nelle casse armoniche, alterando profondamente il comportamento acustico degli strumenti musicali.

- **Blocchi Note**: il colpo produce note stonate, dal pitch-shift scuro e cupo, con particelle fungine al posto delle note grafiche.
- **Jukebox**: i dischi musicali vanilla vengono riprodotti con velocità rallentata e intonazione degradata, proporzionale allo stadio di infezione. L'effetto è intenzionalmente straniante — ideale per ambientazioni horror o dungeon.

## 📚 Decadimento delle Librerie: Magia e Drop

Le **Librerie standard** subiscono una duplice degradazione con l'avanzare dell'infezione fungina: perdono potere magico verso il Tavolo da Incantesimi e, se distrutte senza *Tocco di Velluto*, rilasciano un numero decrescente di libri (poiché la muffa corrode carta e rilegature).

| Stadio | Potere Incantesimi per blocco | Libri Rilasciati alla Rottura *(Senza Tocco di Velluto)* | Con Tocco di Velluto |
| :---: | :---: | :---: | :---: |
| **0 — Sano** *(o Cerato)* | **1.0** (pieno) | **3 libri** *(Vanilla)* | Rilascia la libreria sana |
| **1 — Intaccato** | **0.66** | **2 libri** | Rilascia la libreria intaccata |
| **2 — Ammuffito** | **0.33** | **1 libro** | Rilascia la libreria ammuffita |
| **3 — Marcio** | **0.0** *(nessun contributo)* | **0 libri** *(carta marcita)* | Rilascia la libreria marcia |

> [!NOTE]
> Se cerate (*Waxed*), le librerie congelano lo stadio attuale: mantengono invariato il drop di libri del proprio stadio e garantiscono pieno potere incantatorio (1.0) se non deteriorate. Con *Tocco di Velluto* (*Silk Touch*), viene sempre recuperato il rispettivo blocco di libreria (cerato o non cerato) del corrispettivo stadio.

### 📖 Librerie Scolpite (Chiseled Bookshelves)
Il comportamento delle **Librerie Scolpite** è profondamente diverso e preserva il lavoro del giocatore:
- **Conservazione dei Libri**: i volumi inseriti negli slot (libri normali, scritti o incantati) rimangono al 100% protetti e intatti a qualsiasi stadio di infezione, anche durante la transizione a Marcio o durante ceratura e sceratura con l'ascia.
- **Rottura**: alla distruzione del blocco, tutti i libri alloggiati vengono riversati intatti sul terreno (`ItemScatterer`), accompagnati dal blocco della libreria scolpita.
- **Comparatore**: il segnale analogico Redstone posteriore (1..6 in base all'ultimo slot interagito) rimane fedele allo standard Vanilla senza alcuna cilecca o distorsione (si veda anche [Capitolo 6](06_automatizzare_la_bonifica.md)).

## 👨‍🌾 Villici e Punti d'Interesse

Nonostante il decadimento visivo, i villici non abbandonano mai le loro postazioni di lavoro. Pescatori, Contadini, Pastori, Cartografi, Impennatori e Bibliotecari riconoscono nativamente anche i blocchi di Stadio 3 come postazioni legittime, reclutandole, lavorandoci e aprendo scambi commerciali senza alcuna incompatibilità.

## 🚪 Fragilità all'Uso dei Blocchi Funzionali

Il legno marcio non possiede più alcuna integrità meccanica o tenuta nei perni:
- **Rischio di Rottura all'Uso**: ogni volta che un giocatore interagisce con un blocco funzionale non cerato di **Stadio 3 (Marcio)** — come aprire una **porta**, ribaltare una **botola**, aprire un **cancelletto** o premere un **pulsante** in legno — c'è una probabilità del **10%** (`rotten_break_chance_on_use`) che il meccanismo ceda all'istante.
- In caso di cedimento, il blocco si spezza con un rumore di legno rotto (`BLOCK_WOOD_BREAK`) e viene **distrutto senza lasciare alcun drop**.
- **Soluzione**: applicare preventivamente un favo d'api (**ceratura**) salda la struttura e ne impedisce il crollo accidentale durante l'uso.

---

| | |
| :--- | ---: |
| [← Automatizzare la Bonifica](06_automatizzare_la_bonifica.md) | [Configurazione e Tecnica →](08_configurazione_e_tecnica.md) |
| [📑 Indice](README.md) | |
