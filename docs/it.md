# Spores & Shadows 

**Spores & Shadows** è una mod per Minecraft (Fabric 1.21.1) che introduce un ecosistema dinamico, realistico e implacabile di decadimento ambientale per il legno. Nessuna struttura è al sicuro dal tempo e dagli elementi!

---

## 🌳 Panoramica e Contenuti

Hai mai costruito una maestosa baita in legno pensando che sarebbe rimasta lì intatta, sfidando i secoli, senza alcun bisogno di manutenzione? **Spores & Shadows** rivoluziona questa certezza, trasformando il legno da un semplice blocco inerte a un materiale vivo, vulnerabile e reattivo all'ambiente circostante.

La mod sostituisce in modo del tutto trasparente e silenzioso ogni pezzo di legno piazzato dal giocatore (o generato naturalmente nelle strutture come relitti e miniere) con una variante "dormiente". Con il passare del tempo, agenti atmosferici come pioggia, umidità, buio e perfino il bioma in cui ti trovi decreteranno il destino delle tue costruzioni, costringendoti a proteggere i tuoi edifici o ad assistere inerme al loro inesorabile decadimento.

### 🔢 Dettagli Tecnici e Blocchi Aggiunti

A livello tecnico, la mod inietta un ecosistema completo per ogni singola variante di legno.

* **🧱 13 Formati Architettonici**: *Tronchi*, *Tronchi Scortecciati*, *Legno (Wood)*, *Legno Scortecciato*, *Assi (Planks)*, *Scale*, *Lastre*, *Staccionate*, *Cancelletti*, *Porte*, *Botole*, *Pedane a Pressione*, *Pulsanti*.

Per ognuno dei 104 formati base in legno, la mod aggiunge **3 varianti ammuffite** (Intaccato, Ammuffito, Marcio). Inoltre, per ognuno di questi blocchi — compreso il blocco Vanilla originale — viene creata la rispettiva **variante cerata**.

In questo modo, il gioco mette a disposizione ben **728 varianti uniche e ottenibili in Survival**:
1. Le **104 Varianti Vanilla Cerate**: La copia protetta e incerata del blocco base Vanilla.
2. Le **312 Varianti Ammuffite**: I tre stadi di decadimento naturali.
3. Le **312 Varianti Ammuffite Cerate**: I blocchi decaduti ma fermati nel tempo dalla cera.

Questo sistema ti permette di ricavare in sopravvivenza blocchi parzialmente ammuffiti per poi "sigillarli" con il favo di miele, potendoli così usare in totale sicurezza per scopi decorativi senza rischio di infettare le costruzioni vicine.

---

## 🦠 Il Ciclo della Muffa

Il legno attraversa 4 stadi di decadimento: **Vanilla (0) ➔ Intaccato (1) ➔ Ammuffito (2) ➔ Marcio (3)**.

L'avanzamento avviene solo se il "Rischio di Infezione" (`R`), ricalcolato costantemente, supera la soglia fissa di **0.4**. La formula esatta è:
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
  - **In Superficie**: Dipende dal bioma. Climi estremi come deserti o ghiacciai bloccano totalmente l'infezione bloccando il fattore a `0.0`.
  - **Sottoterra (`Y < 64`)**: Indipendentemente dal bioma di superficie, scendendo la temperatura si normalizza gradualmente, stabilizzandosi al valore perfetto di `0.5` (mite) sotto `Y=48`. Anche in un deserto o in un bioma ghiacciato, le caverne profonde svilupperanno la muffa!
  - **Alta Quota (`Y > 128`)**: Salendo in quota la temperatura crolla gradualmente, congelando a `-0.5` al livello `Y=256`. Costruire baite in alta montagna preserverà il legno quasi ovunque.
* ☣️ **Contagio (Catalizzatori)**: Somma un malus diretto se il legno è a contatto con agenti infettivi:
  - Legno infetto: Intaccato (`+0.05`), Ammuffito (`+0.10`), Marcio (`+0.20`).
  - Ambiente: Fango (`+0.05`), Podzol/Micelio (`+0.15`), Funghi (`+0.25`), Spore Blossom (`+0.80`).

---

## ☠️ Pericoli Ambientali (Miasma)

- ✨ **Particelle di Spore**: I blocchi negli stadi **Ammuffito** e **Marcio** emettono spore dalle facce esposte (disabilitato sott'acqua).
- 🤢 **Miasma Tossico**: La mod scansiona un raggio di 4 blocchi attorno al giocatore. Ogni blocco aggiunge il suo livello di decadimento al punteggio totale di tossicità:
  - **Contagiato**: +1
  - **Ammuffito**: +2
  - **Marcio**: +3
  - *(I blocchi cerati sono sicuri e valgono 0).*
  
  **Effetti**:
  - Punteggio > **15**: **Nausea**
  - Punteggio > **35**: **Nausea + Veleno**

---

## 🛠️ Interazioni e Prevenzione

Il giocatore non è indifeso contro la natura. Equipaggiando lo strumento giusto e agendo in **modalità furtiva (Sneaking / Shift)**, puoi interagire direttamente con lo stato vitale del legno. 
*(Lo sneaking è obbligatorio per evitare di incerare o raschiare per sbaglio i blocchi interattivi, come porte, botole o pulsanti).*

* 🪓 **Uso dell'Ascia (Scrape)**: Facendo *Shift + Tasto Destro* con un'ascia:
  - Se il blocco è **Cerato**, l'ascia rimuoverà lo strato di cera ripristinando il normale ciclo vitale.
  - Se il blocco è **Intaccato o Ammuffito**, l'ascia raschierà via lo strato superficiale di funghi, riducendo il decadimento di 1 stadio. Un blocco allo Stadio 1 tornerà a essere perfettamente pulito (Stadio 0 Vanilla).
  *(Ogni raschiatura consuma normalmente durabilità).*
* 🐝 **Uso del Favo (Waxing)**: Facendo *Shift + Tasto Destro* con un favo di miele su un blocco a *qualsiasi* stadio, questo diventerà **Cerato**. Il legno cerato è sigillato: diventa immune ai danni ambientali, congela il suo decadimento all'infinito e perde la capacità di infettare i blocchi vicini. 

*Funzionalità Smart: Se compi queste azioni su un blocco multiplo (come la metà superiore o inferiore di una Porta), l'aggiornamento verrà applicato istantaneamente e in totale sincronia all'intera struttura!*

---

## ⚖️ Penali e Crafting

Usare legno marcio per l'artigianato non è saggio. La struttura interna del materiale è irrimediabilmente compromessa, introducendo malus severi che puniscono la pigrizia:

* 💥 **Integrità Strutturale (Drop) e Scavo**:
  L'attrezzo preferito per minare questi blocchi resta l'**Ascia** (esattamente come nel Vanilla), con l'unica eccezione dei blocchi allo Stadio 3, così deboli da non avere alcun attrezzo associato (si sbriciolano in un istante anche a mani nude).
  - I blocchi Vanilla e **Intaccati** rimangono solidi (droppano sempre al **100%**).
  - I blocchi **Ammuffiti** sono fragili: hanno solo il **50%** di probabilità di droppare se stessi, altrimenti andranno in frantumi nel nulla.
  - I blocchi **Marci** si sbriciolano istantaneamente al tocco (probabilità di drop dello **0%**).
  
  *(💡 **Il segreto della Cera**: Incerare un blocco ne consolida la struttura. Qualsiasi blocco della mod, perfino il Marcio, se **Cerato** avrà sempre il **100% di probabilità di drop**, anche senza usare il Tocco di Velluto!)*
* 🛠️ **Resa di Crafting (Recupero)**:
  Puoi ancora usare il legno infetto nel banco da lavoro per craftare oggetti di base (come Assi, Lastre, Scale o Bastoni). L'oggetto finale sarà sempre perfettamente pulito (**Vanilla**), ma poiché sei costretto a scartare le parti marce del legno originario, la quantità di oggetti ottenuti crollerà drasticamente:

  | Qualità del Materiale | 🌳 Es: Tronco ➔ Assi | 🦯 Es: Assi ➔ Bastoni |
  | :--- | :---: | :---: |
  | 🌲 **Sano (Vanilla)**5 | 1 Tronco ➔ **4** Assi | 2 Assi ➔ **4** Bastoni |
  | 🟢 **Intaccato** | 1 Tronco ➔ **2** Assi | 2 Assi ➔ **2** Bastoni |
  | 🦠 **Ammuffito** | 1 Tronco ➔ **1** Asse | 2 Assi ➔ **1** Bastone |
  | ☠️ **Marcio** | *Ricetta Invalida* ❌ | *Ricetta Invalida* ❌ |

* 🔥 **Potere Combustibile**: 
  - Il legno Intaccato brucia con efficienza dimezzata (**50%**).
  - L'Ammuffito scende a un quarto dell'efficienza (**25%**).
  - Il Marcio brucia in pochi istanti (**12.5%**), rendendolo inutile come combustibile.
* ♻️ **Compostiera (Il lato positivo del Marciume)**:
  Se un blocco è troppo marcio per costruirci, riciclalo! Tutto il legno della mod è stato integrato con la Compostiera Vanilla per generare Farina d'Ossa. Più il legno è degradato (e ricco di spore), maggiore sarà la probabilità di successo:
  - Legno Intaccato: **50%**
  - Legno Ammuffito: **65%**
  - Legno Marcio: **85%** (Eccellente fertilizzante!)
* 🔴 **Componenti in Pietrarossa (Pulsanti e Pedane a Pressione)**:
  La muffa compromette i meccanismi interni dei componenti in pietrarossa, facendoli incastrare e rimanere attivi molto più a lungo. Ad esempio, un normale pulsante di legno sano rimane attivo per 1,5 secondi (30 tick), ma man mano che marcisce:
  - Intaccato: **3 secondi** (60 tick).
  - Ammuffito: **7,5 secondi** (150 tick).
  - Marcio: **22,5 secondi** (450 tick).

*(💡 **Nota sui Blocchi Cerati**: La cera è un sigillante ambientale, ma non blocca l'utilizzo dell'oggetto! Puoi usare i blocchi cerati nel banco da lavoro, bruciarli nella fornace o gettarli nella compostiera: si comporteranno esattamente come la loro controparte non cerata, mantenendo gli stessi identici malus o bonus legati unicamente al loro livello interno di marciume).*

---

## 🗺️ Generazione delle Strutture

La muffa non si limita ai blocchi piazzati dal giocatore. La mod intercetta il motore di generazione di Minecraft per applicare l'usura del tempo a tutte le strutture di legno che scoprirai nel mondo. 

Le strutture sono divise in 4 livelli di degrado base:
1. 🏴‍☠️ **Degrado Critico** (Alta percentuale di legno Marcio): Relitti Sommersi (`shipwreck`), Capanne della Palude (`swamp_hut`).
2. 🧟 **Degrado Alto** (Misto Intaccato e Ammuffito): Miniere Abbandonate (`mineshaft`), Villaggi Zombie (`zombie_village`), Rovine del Sentiero (`trail_ruins`).
3. 🏹 **Degrado Moderato** (Principalmente Intaccato): Avamposti dei Saccheggiatori (`pillager_outpost`), Portali in Rovina (`ruined_portal`).
4. 🏡 **Degrado Minimo** (Quasi totalmente sano): Villaggi normali (`village`), Magioni della Foresta (`mansion`).

*(💡 **Fattori Dinamici**: Durante la generazione, il codice analizza l'ambiente blocco per blocco! Se una parete del relitto è esposta all'aria e al sole sarà più intatta, mentre le assi sprofondate nel fondale marino o sottoterra saranno drasticamente più marce).*

**🛡️ L'Immunità del Legno Naturale e delle Strutture**:
Per non rovinare l'esperienza di gioco (evitando che i giocatori trovino il mondo intero già collassato prima di poterlo esplorare), ci sono due eccezioni al decadimento automatico:
* **Alberi Nativi**: Gli alberi generati naturalmente (o cresciuti dai virgulti) non generano muffa perché il legno è ancora "vivo". Solo il legno abbattuto e lavorato dal giocatore inizia a marcire.
* **Strutture Sospese**: Le strutture si generano con la percentuale di muffa indicata sopra, ma poi "si congelano". I blocchi delle strutture sono nativamente immuni all'avanzare del marciume, a meno che il giocatore non interagisca con essi (es. spaccandoli, raschiandoli o modificandoli). Questa protezione salva i villaggi dalla distruzione spontanea. Se vuoi un'esperienza super-hardcore, puoi disabilitare l'immunità delle strutture dal menu delle configurazioni!

---

## ⚙️ Mod Configuration
La mod include un menu di configurazione accessibile direttamente dal gioco (richiede **Cloth Config** e **ModMenu**) che ti garantisce il controllo assoluto su ogni singola meccanica. 
Le opzioni sono divise in 7 categorie principali:

* 🛠️ **Generale**: Disattiva la crescita della muffa globalmente, cambia la soglia di infezione, espandi il raggio di scansione o **disattiva l'immunità delle strutture** per far marcire spontaneamente i villaggi!
* 🌡️ **Ambiente (Environment)**: Modifica i valori di base per pioggia/secco, i bonus per l'acqua, o personalizza a quali altitudini e temperature la muffa deve congelare o proliferare.
* 🪓 **Suscettibilità (Susceptibility)**: Regola quanto velocemente marciscono i blocchi lavorati (assi) rispetto a quelli grezzi o scortecciati.
* ☣️ **Catalizzatori (Catalysts)**: Bilancia l'aggressività di funghi, fango, *spore blossom* e dei blocchi di legno infetti stessi.
* 🗺️ **Strutture (Structures)**: Personalizza nel dettaglio (percentuale per percentuale) come si generano i relitti, i villaggi e le miniere.
* ☠️ **Tossicità (Toxicity)**: Permette di modificare le soglie, la durata degli effetti, il raggio della nuvola tossica e il raggio di scansione dell'acqua.
* 🔥 **Fornace (Furnace Multipliers)**: Modifica l'efficienza di cottura del legno per i vari stadi di decadimento.
* 💥 **Drop**: Alza o abbassa il drop rate del legno fragile, se ritieni la mod troppo punitiva.
