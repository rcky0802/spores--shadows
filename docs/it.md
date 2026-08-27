# Spores & Shadows 

**Spores & Shadows** è una mod per Minecraft (Fabric 1.21.1) che introduce un ecosistema dinamico, realistico e implacabile di decadimento ambientale per il legno. Nessuna struttura è al sicuro dal tempo e dagli elementi!

---

## 🌳 Panoramica e Contenuti

Hai mai costruito una maestosa baita in legno pensando che sarebbe rimasta lì intatta, sfidando i secoli, senza alcun bisogno di manutenzione? **Spores & Shadows** rivoluziona questa certezza, trasformando il legno da un semplice blocco inerte a un materiale vivo, vulnerabile e reattivo all'ambiente circostante.

La mod sostituisce in modo del tutto trasparente e silenzioso ogni pezzo di legno piazzato dal giocatore (o generato naturalmente nelle strutture come relitti e miniere) con una variante "dormiente". Con il passare del tempo, agenti atmosferici come pioggia, umidità, buio e perfino il bioma in cui ti trovi decreteranno il destino delle tue costruzioni, costringendoti a proteggere i tuoi edifici o ad assistere inerme al loro inesorabile decadimento.

### 🔢 Dettagli Tecnici e Blocchi Aggiunti

A livello tecnico, la mod inietta un ecosistema completo per ogni singola variante di legno (inclusi i legni cremisi e distorti del Nether).

* **🧱 13 Formati Architettonici**: *Tronchi*, *Tronchi Scortecciati*, *Legno (Wood)*, *Legno Scortecciato*, *Assi (Planks)*, *Scale*, *Lastre*, *Staccionate*, *Cancelletti*, *Porte*, *Botole*, *Pedane a Pressione*, *Pulsanti*.

Per ognuno dei 130 formati base in legno, la mod aggiunge **3 varianti ammuffite** (Intaccato, Ammuffito, Marcio). Inoltre, per ognuno di questi blocchi — compreso il blocco Vanilla originale — viene creata la rispettiva **variante cerata**.

In questo modo, il gioco mette a disposizione ben **910 varianti uniche e ottenibili in Survival**:
1. Le **130 Varianti Vanilla Cerate**: La copia protetta e incerata del blocco base Vanilla.
2. Le **390 Varianti Ammuffite**: I tre stadi di decadimento naturali.
3. Le **390 Varianti Ammuffite Cerate**: I blocchi decaduti ma fermati nel tempo dalla cera.

Questo sistema ti permette di ricavare in sopravvivenza blocchi parzialmente ammuffiti per poi "sigillarli" con il favo di miele, potendoli così usare in totale sicurezza per scopi decorativi senza rischio di infettare le costruzioni vicine.

---

## 🦠 Il Ciclo della Muffa

Il legno attraversa 4 stadi di decadimento: **Vanilla (0) ➔ Intaccato (1) ➔ Ammuffito (2) ➔ Marcio (3)**.

L'avanzamento avviene solo se il "Rischio di Infezione" (`R`), ricalcolato costantemente, supera la soglia fissa di **0.4** (configurabile). La formula esatta è:
`R = ((Umidità * Luce * Suscettibilità) + Contagio) * Temperatura`

### Fattori e Variabili Esatte
* 💧 **Umidità (Clima + Profondità + Acqua)**: 
  - Il valore base dipende dalle precipitazioni del bioma (Pioggia/Neve: `0.8`, Secco: `0.3`). 
  - **Malus Profondità**: Scendendo sotto il livello del mare (`Y < 64`), l'umidità aumenta vertiginosamente di `+0.01` per ogni blocco di discesa, rendendo le miniere ambienti umidissimi.
  - **Malus Locale**: L'adiacenza a blocchi d'acqua (`+0.15`) o calderoni (`+0.10`) somma ulteriore umidità al blocco.
* ☀️ **Luce (UV)**: Scala linearmente da `0.0` (Luce 15, blocca totalmente l'infezione) a `1.0` (Buio totale).
* 🪓 **Suscettibilità del Materiale**: Il legno scortecciato è estremamente vulnerabile (`x1.4`), i tronchi grezzi sono standard (`x1.0`), mentre il legno lavorato in assi resiste leggermente di più (`x0.8`).
* 🌡️ **Temperatura (Bioma + Altitudine/Profondità)**: 
  - Agisce come filtro di sopravvivenza. La muffa prolifera **solo** se la temperatura locale è compresa tra `0.15` e `1.5`.
  - **In Superficie**: Dipende dal bioma. Climi estremi come deserti o ghiacciai bloccano totalmente l'infezione portando il fattore a `0.0`.
  - **Nether & End**: Il calore estremo del Nether e il vuoto gelido dell'End sono totalmente letali per la muffa. Il legno non marcirà mai in queste dimensioni.
  - **Sottoterra (`Y < 64`)**: Indipendentemente dal bioma di superficie, scendendo la temperatura si normalizza gradualmente, stabilizzandosi al valore perfetto di `0.5` (mite) sotto `Y=48`. Anche in un deserto o in un bioma ghiacciato, le caverne profonde svilupperanno la muffa!
  - **Alta Quota (`Y > 128`)**: Salendo in quota la temperatura crolla gradualmente, congelando a `-0.5` al livello `Y=256`. Costruire baite in alta montagna preserverà il legno quasi ovunque.
* ☣️ **Contagio (Catalizzatori)**: Somma un malus diretto se il legno è a contatto con agenti infettivi:
  - Legno infetto: Intaccato (`+0.05`), Ammuffito (`+0.10`), Marcio (`+0.20`).
  - Ambiente: Fango (`+0.05`), Podzol/Micelio (`+0.15`), Funghi (`+0.25`), Spore Blossom (`+0.80`).

---

## ☠️ Pericoli Ambientali (Miasma Volumetrico)

- ✨ **Particelle di Spore**: I blocchi negli stadi **Ammuffito** e **Marcio** emettono spore dalle facce esposte (disabilitato se sommersi sott'acqua).
- 🤢 **Sistema Miasma Volumetrico**: Attraverso un algoritmo avanzato di *Flood Fill* volumetrico, il gioco calcola la concentrazione e la propagazione dei gas tossici confinati negli spazi chiusi:
  - **Calcolo Dinamico**: Il sistema calcola l'inquinamento complessivo in base al volume della stanza chiusa (fino a 180 blocchi di volume) e alla densità della muffa presente sulle pareti, sul pavimento o sul soffitto.
  - **Dispersione all'Aperto**: Se ti trovi all'aperto o in aree immensamente vaste, le spore si disperderanno istantaneamente senza provocare danni.
  - **Propagazione e Barriere**: Le pareti solide bloccano il miasma, mentre porte socchiuse, botole o scale permettono all'aria tossica di fluire nelle stanze adiacenti.
  - **Ventilazione Naturale**: I blocchi parziali a contatto con l'esterno (come staccionate, muretti o finestre con sbarre di ferro) agiscono come prese d'aria naturali, riducendo drasticamente il livello di miasma netto!
  
  **Effetti e Soglie (100% Configurabili)**:
  - Concentrazione / Densità Bassa: **Fame** (Particelle di micelio leggere)
  - Concentrazione / Densità Alta: **Nausea + Veleno** (Particelle dense di spore)
  
  *(Tutte le soglie di attivazione per Nausea e Veleno, così come la durata e la potenza degli effetti, sono totalmente personalizzabili nel menu di configurazione! Gli admin possono inoltre usare il comando `/miasma` per visualizzare in tempo reale i dati volumetrici e la densità dell'aria nella stanza in cui si trovano).*

---

## 🛠️ Interazioni e Prevenzione

Il giocatore non è indifeso contro la natura. Equipaggiando lo strumento giusto e agendo in **modalità furtiva (Sneaking / Shift)**, puoi interagire direttamente con lo stato vitale del legno. 
*(Lo sneaking è obbligatorio per evitare di incerare o raschiare per sbaglio i blocchi interattivi, come porte, botole o pulsanti).*

* 🪓 **Uso dell'Ascia (Scrape)**: Facendo *Shift + Tasto Destro* con un'ascia:
  - Se il blocco è **Cerato**, l'ascia rimuoverà lo strato di cera ripristinando il normale ciclo vitale.
  - Se il blocco è **Intaccato o Ammuffito**, l'ascia raschierà via lo strato superficiale di funghi, riducendo il decadimento di 1 stadio. Un blocco allo Stadio 1 tornerà a essere perfettamente pulito (Stadio 0 Vanilla).
  *(Ogni raschiatura consuma normalmente durabilità dell'attrezzo).*
* 🐝 **Uso del Favo (Waxing)**: Facendo *Shift + Tasto Destro* con un favo di miele su un blocco a *qualsiasi* stadio, questo diventerà **Cerato**. Il legno cerato è sigillato: diventa immune ai danni ambientali, congela il suo decadimento all'infinito, perde la capacità di infettare i blocchi vicini e non contribuisce alla formazione del miasma tossico.

*Funzionalità Smart: Se compi queste azioni su un blocco multiplo (come la metà superiore o inferiore di una Porta), l'aggiornamento verrà applicato istantaneamente e in totale sincronia all'intera struttura!*

---

## ⚖️ Penali e Crafting

Usare legno marcio per l'artigianato non è saggio. La struttura interna del materiale è compromessa, introducendo malus bilanciati che premiano la corretta manutenzione:

* 💥 **Integrità Strutturale (Drop) e Scavo**:
  L'attrezzo preferito per minare questi blocchi resta l'**Ascia** (esattamente come nel Vanilla), con l'unica eccezione dei blocchi allo Stadio 3, così deboli da non avere alcun attrezzo associato (si sbriciolano in un istante anche a mani nude).
  - I blocchi Vanilla e **Intaccati** rimangono solidi (droppano sempre al **100%**).
  - I blocchi **Ammuffiti** sono fragili: hanno una probabilità base del **50%** di droppare se stessi, altrimenti andranno in frantumi nel nulla.
  - I blocchi **Marci** si sbriciolano istantaneamente al tocco (probabilità base di drop dello **0%**).
  
  *(💡 **Il segreto della Cera**: Incerare un blocco ne consolida la struttura interna. Qualsiasi blocco della mod, perfino allo stadio **Marcio** (Rotten) dove normalmente il legno andrebbe in frantumi, se **Cerato garantisce il drop al 100%**, anche senza disporre dell'incantesimo Tocco di Velluto! Le probabilità di drop dei blocchi non cerati sono inoltre 100% configurabili).*
* 🛠️ **Resa di Crafting (Recupero) e Crafting Ibrido**:
  Puoi ancora usare il legno infetto nel banco da lavoro per craftare oggetti di base (come Assi, Lastre, Scale o Bastoni). L'oggetto finale sarà sempre perfettamente pulito (**Vanilla**), ma poiché sei costretto a scartare le parti marce del legno originario, la quantità di oggetti ottenuti calerà progressivamente. 
  Inoltre, grazie al **Crafting ibrido**, puoi mischiare liberamente blocchi cerati e non cerati (dello stesso stadio di muffa) all'interno della stessa griglia di crafting per produrre assi pulite o altri oggetti senza alcuna limitazione!

  | Qualità del Materiale | 🌳 Es: Tronco ➔ Assi | 🦯 Es: Assi ➔ Bastoni |
  | :--- | :---: | :---: |
  | 🌲 **Sano (Vanilla / Cerato)** | 1 Tronco ➔ **4** Assi | 2 Assi ➔ **4** Bastoni |
  | 🟢 **Intaccato** | 1 Tronco ➔ **2** Assi | 2 Assi ➔ **2** Bastoni |
  | 🦠 **Ammuffito** | 1 Tronco ➔ **1** Asse | 2 Assi ➔ **1** Bastone |
  | ☠️ **Marcio** | *Ricetta Invalida* ❌ | *Ricetta Invalida* ❌ |

* 🔥 **Potere Combustibile (Furnace Multipliers)**: 
  L'efficienza del legno come combustibile nella fornace decresce progressivamente con l'aumentare della muffa:
  - **Sano (Vanilla / Cerato)**: Efficienza standard al **100%**.
  - **Intaccato**: Efficienza dimezzata al **50%**.
  - **Ammuffito**: Efficienza ridotta a un quarto (**25%**).
  - **Marcio**: Brucia in un istante con efficienza minima (**12.5%**).
  *(I blocchi cerati mantengono le stesse identiche proprietà di combustione della loro controparte non cerata. Tutti i moltiplicatori di resa del carburante sono al 100% configurabili).*
* ♻️ **Compostiera (Il lato positivo del Marciume)**:
  Se un blocco è troppo degradato per costruire, riciclalo! Tutto il legno della mod può essere inserito nella Compostiera Vanilla per produrre Farina d'Ossa. Più il legno è marcio (e ricco di spore), maggiore sarà la probabilità di fertilizzazione:
  - Legno Intaccato: **50%**
  - Legno Ammuffito: **65%**
  - Legno Marcio: **85%** (Ottimo fertilizzante!)
* 🔴 **Componenti in Pietrarossa (Pulsanti e Pedane a Pressione)**:
  La muffa compromette i meccanismi interni dei componenti in pietrarossa, facendoli incastrare e rimanere attivi molto più a lungo. Ad esempio, un normale pulsante di legno sano rimane attivo per 1,5 secondi (30 tick), ma man mano che marcisce:
  - Intaccato: **3 secondi** (60 tick).
  - Ammuffito: **7,5 secondi** (150 tick).
  - Marcio: **22,5 secondi** (450 tick).

*(💡 **Nota sui Blocchi Cerati**: La cera è un sigillante ecologico e protettivo, ma non limita l'uso pratico del materiale: i blocchi cerati si possono mischiare con quelli normali nel crafting, mantengono le stesse identiche proprietà di combustione nella fornace della controparte non cerata, e assicurano il drop garantito al 100% al momento dello scavo anche allo stadio Marcio).*

---

## 🗺️ Generazione delle Strutture

La muffa non si limita ai blocchi piazzati dal giocatore. La mod intercetta il motore di generazione del mondo per applicare l'usura del tempo a tutte le strutture lignee che scoprirai durante l'esplorazione.

Le strutture sono suddivise in 4 categorie di degrado base:
1. 🏴‍☠️ **Degrado Critico** (Alta percentuale di legno Marcio): Relitti Sommersi (`shipwreck`), Capanne della Palude (`swamp_hut`).
2. 🧟 **Degrado Alto** (Misto Intaccato e Ammuffito): Miniere Abbandonate (`mineshaft`), Villaggi Zombie (`zombie_village`), Rovine del Sentiero (`trail_ruins`).
3. 🏹 **Degrado Moderato** (Principalmente Intaccato): Avamposti dei Saccheggiatori (`pillager_outpost`), Portali in Rovina (`ruined_portal`).
4. 🏡 **Degrado Minimo** (Quasi totalmente sano): Villaggi normali (`village`), Magioni della Foresta (`mansion`).

*(💡 **Fattori Dinamici**: Durante la generazione, il codice analizza l'ambiente blocco per blocco! Se una parete del relitto è esposta all'aria e al sole sarà più intatta, mentre le assi sprofondate nel fondale marino o sottoterra saranno drasticamente più marce).*

**🛡️ L'Immunità del Legno Naturale e delle Strutture**:
Per preservare il divertimento ed evitare che i giocatori trovino il mondo già interamente distrutto:
* **Alberi Nativi**: Gli alberi generati naturalmente (o cresciuti dai virgulti) non sviluppano muffa perché il legno è vivo. Solo il legno abbattuto e lavorato dal giocatore è soggetto al decadimento.
* **Strutture Sospese**: Le strutture si generano con la percentuale di muffa indicata sopra, ma poi "si congelano". I blocchi delle strutture sono immuni al marciume spontaneo finché il giocatore non interagisce con essi (rompendoli, raschiandoli o modificandoli). Questa protezione salva i villaggi dal collasso automatico, ma può essere disattivata dal menu di configurazione per un'esperienza hardcore!

---

## 📊 Integrazione HUD & Obiettivi

* 🔍 **Integrazione Jade / WTHIT**: La mod è completamente integrata con **Jade**. Guardando qualsiasi blocco di legno, l'HUD mostrerà in modo nativo la sua variante precisa (es. "Assi di Quercia Intaccate Cerate") e la sua icona, insieme al rischio di infezione (%) attuale. Il rischio calcola esattamente lo 0% per i blocchi cerati e viene nascosto per i blocchi completamente marci (Rotten). La percentuale di rischio cambia colore dinamicamente (**Grigio = Sicuro**, **Rosso = A Rischio**). Gli admin possono anche usare il comando `/moldrisk [verbose]` per calcolare l'esatta formula matematica del blocco che stanno guardando!
* 🏆 **Obiettivi (Advancements)**: Include 5 obiettivi personalizzati per guidare i giocatori attraverso tutte le nuove meccaniche:
  - **Spores & Shadows**: Sopravvivi al decadimento della natura.
  - **Prevenzione Naturale**: Usa un favo di miele per cerare un blocco di legno e fermare la muffa.
  - **Olio di Gomito**: Raschia via la muffa da un blocco di legno usando un'ascia.
  - **Respiro Corto**: Subisci il veleno del miasma respirando troppa muffa.
  - **Polvere alla Polvere**: Tenta di rompere un blocco di legno marcio e guardalo sgretolarsi nel nulla.

---

## ⚙️ Configurazione della Mod
La mod include un menu di configurazione grafico accessibile direttamente dal gioco (richiede **Cloth Config** e **ModMenu**) che garantisce il pieno controllo su ogni singola meccanica.
Le opzioni sono suddivise in 8 categorie principali:

* 🛠️ **Generale (General)**: Disattiva la crescita della muffa globalmente, cambia la soglia di infezione, espandi il raggio di scansione o **disattiva l'immunità delle strutture** per far marcire spontaneamente i villaggi!
* 🌡️ **Ambiente (Environment)**: Modifica i valori di base per pioggia/secco, i bonus per l'acqua, o personalizza a quali altitudini e temperature la muffa deve congelare o proliferare.
* 🪓 **Suscettibilità (Susceptibility)**: Regola la velocità di decadimento dei blocchi lavorati (assi) rispetto a quelli grezzi o scortecciati.
* ☣️ **Catalizzatori (Catalysts)**: Bilancia l'influenza di funghi, fango, *spore blossom* e dei blocchi di legno infetti stessi.
* ☠️ **Tossicità (Toxicity)**: Configura liberamente le soglie di attivazione per Nausea e Veleno, la durata degli effetti, l'intervallo di scansione e i parametri del sistema Miasma Volumetrico.
* 🗺️ **Strutture (Structures)**: Personalizza nel dettaglio (percentuale per percentuale) come si generano i relitti, i villaggi, le miniere e le altre strutture nel mondo.
* 🔥 **Efficienza Fornace (Furnace Multipliers)**: Modifica l'efficienza e i moltiplicatori di cottura del combustibile per ogni singolo stadio di decadimento (100% -> 50% -> 25% -> 12.5%, 100% configurabile).
* 💥 **Drop**: Alza o abbassa il drop rate del legno fragile (Ammuffito) e Marcio a piacimento, per adattare la difficoltà alle proprie preferenze (100% configurabile).
