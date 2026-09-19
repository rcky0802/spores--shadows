# ⚙️ Automatizzare la Bonifica

I rilevatori manuali e la ceratura preventiva coprono le situazioni ordinarie. Per strutture grandi, ambienti sotterranei o scenari in cui il giocatore non può essere sempre presente, la mod mette a disposizione un sistema di automazione completo: sensori permanenti, macchinari industriali e circuiti Redstone.

---

## 📡 Sensori Redstone Permanenti (Sistema Ibrido)

Entrambi i rilevatori possono essere montati in qualsiasi orientamento su blocchi solidi (**pavimento, parete o soffitto**). Oltre alla lettura visiva e diagnostica, integrano un **sistema Redstone ibrido** (Emissione Diretta + Uscita Comparatore):

**💧 Rilevatore di Umidità (piazzato)** — Monitora l'umidità effettiva $H_{eff}$ e il microclima locale:
- **Quadrante Visivo**: 4 stadi graduati (0 = Secco, 1 = Umidiccio, 2 = Umido, 3 = Critico).
- **Segnale Diretto**: Emette una potenza Redstone proporzionale allo stadio (**0, 5, 10, 15**), alimentando direttamente la polvere adiacente, lampade di segnalazione, macchinari o il blocco di supporto a parete (consentendo di occultare i cavi dietro il muro).
- **Supporto Comparatore**: Qualsiasi Comparatore adiacente legge nativamente lo stesso segnale (0, 5, 10, 15) per logiche analogiche o soglie di precisione.
- **Interrogazione**: Cliccando con il tasto destro sul blocco (a mano o con strumento), il dispositivo scatta con un suono meccanico ed invia in chat il report diagnostico completo.

**☢️ Rilevatore di Spore (piazzato)** — Analizza periodicamente il volume BFS dell'aria della stanza:
- **Quadrante Visivo**: 4 stadi di allarme tossico (0 = Pulito, 1 = Attenzione, 2 = Fame, 3 = Veleno Letale).
- **Segnale Diretto**: Emette potenza Redstone scalare (**0, 5, 10, 15**), permettendo di avviare automaticamente Deumidificatori e Purificatori d'Aria non appena il miasma comincia ad accumularsi, senza bisogno di ripetitori o circuiti intermedi.
- **Supporto Comparatore**: Pienamente interfacciabile con Comparatori per realizzare allarmi a soglia e circuiti di emergenza avanzati.
- **Interrogazione**: Al click destro, scatta con un suono metallico e trasmette in chat privata la telemetria volumetrica e la tendenza del miasma (in accumulo, stabile o in purificazione).

## 💣 Meccanica Redstone: Casse Trappola e Cilecca

La muffa compromette anche i meccanismi di emissione Redstone integrati nei blocchi di legno.

**Pulsanti in Legno** — Il fungo intrappola la molla del perno, moltiplicando il tempo in cui il pulsante resta premuto:
- Stadio 0 (Sano / Cerato): **1.5 secondi** (30 tick — standard Vanilla)
- Stadio 1 (Intaccato): **3.0 secondi** (60 tick)
- Stadio 2 (Ammuffito): **7.5 secondi** (150 tick)
- Stadio 3 (Marcio): **22.5 secondi** (450 tick) — rende inutilizzabile qualsiasi timing di circuito

**Pedane a Pressione in Legno** — La biomassa fungina rallenta la distensione della pedana dopo che l'entità è scesa:
- Stadio 0 (Sano / Cerato): **1.0 secondo** (20 tick — standard Vanilla)
- Stadio 1 (Intaccato): **2.0 secondi** (40 tick)
- Stadio 2 (Ammuffito): **5.0 secondi** (100 tick)
- Stadio 3 (Marcio): **15.0 secondi** (300 tick) — il segnale persiste per 15 secondi dopo il calpestio

**Bauli Trappola — Jamming** — La muffa ossida le lamelle della cerniera interna. Ad ogni apertura, c'è una probabilità crescente di *cilecca* (mancata emissione di segnale con click a vuoto):
- Stadio 1 (Intaccato): **15%** di probabilità di cilecca
- Stadio 2 (Ammuffito): **50%** di probabilità di cilecca
- Stadio 3 (Marcio): **85%** di probabilità di cilecca

Lo stato del meccanismo è intenzionalmente nascosto agli HUD esterni (Jade/WTHIT) per preservare l'effetto sorpresa.

**Librerie Scolpite e Comparatori** — I libri conservati al loro interno sopravvivono intatti al decadimento a qualsiasi stadio. Il segnale analogico emesso dal Comparatore posteriore (da 1 a 6 in base all'ultimo slot con cui si è interagito) rimane pienamente deterministico e fedele al comportamento Vanilla, senza alcuna perdita di segnale.

---

## 🌀 Deumidificatore

Il macchinario cardine per la bonifica atmosferica attiva dell'umidità. Mentre il Purificatore interviene a valle neutralizzando il Miasma già formato, il Deumidificatore agisce a monte prevenendo l'insorgenza e la proliferazione di qualsiasi muffa asciugando l'aria della stanza.

- **Potenza di Asciugatura (1.0)** — Ogni Deumidificatore attivo applica un potere di asciugatura di **1.0 punto** all'algoritmo igrometrico di stanza:
  $$H_{\text{target}} = \max(0.0, \, H_{\text{raw}} - 1.0 \times N_{\text{deumidificatori}})$$
  Dato che l'umidità grezza naturale $H_{\text{raw}}$ è compresa tra $0.0$ e $1.0$, un singolo deumidificatore attivo è sufficiente ad abbattere l'umidità effettiva $H_{\text{eff}}$ a **$0.0$ ($0\%$)** in qualsiasi stanza sigillata fino a $2048\text{ m}^3$, trasformando l'ambiente in un'oasi desertica immune al decadimento biologico.
- **Alimentazione Ibrida (32.000 FE / Combustibile 4.0×)** — Opera consumando **10 FE/tick** durante il funzionamento attivo ($0\text{ FE/tick}$ in standby/off). Supporta ricarica da cavi elettrici (fino a 500 FE/t su qualsiasi faccia) o combustibile solido nello Slot 0 con resa quadruplicata ($4.0\times$, un pezzo di carbone eroga $64.000\text{ FE}$, saturando l'intero buffer).
- **Serbatoio Condensa & Modalità Operative (2.000 mB)** — Dotato di serbatoio interno per liquidi:
  - **Modalità Deumidificazione**: assorbe attivamente l'umidità e accumula condensa nel serbatoio al ritmo di **1 mB ogni 24 tick** base (la condensazione accelera in stanze molto umide).
  - **Standby a Serbatoio Pieno (FULL)**: raggiunto il limite di 2.000 mB (2 secchi d'acqua), il macchinario entra in pausa standby per non sprecare energia né combustibile.
  - **Modalità Nebulizzazione (Humidify)**: polarità invertita; consuma l'acqua del serbatoio (1 mB ogni 24 tick) per disperdere vapore nell'aria ($+1.0$ di umidità). Si arresta in automatico se l'umidità della stanza tocca il 98% ($0.98$). Utile per coltivare fungaie controllate.
- **Automazione a Tramogge e Fluidi (SidedInventory & Fluid Transfer)**:
  - **Combustibile Solido**: la tramoggia può inserire carburante da **qualsiasi faccia** del blocco nello Slot 0.
  - **Estrazione/Inserimento Acqua**: supporta il prelievo/svuotamento manuale con secchio (1.000 mB a click destro) e la connessione a tubature automatiche di fluidi tramite *Fabric Transfer API*.
- **Interfaccia, Redstone e Comparatore**:
  - **Controllo Redstone**: toggle a tre stati (*Sempre Acceso* `IGNORED`, *Acceso con Redstone* `HIGH`, *Spento con Redstone* `LOW`). Collegato a un Rilevatore di Umidità a parete, si accende solo quando l'aria supera la soglia critica.
  - **Uscita Comparatore**: emette un segnale analogico da **$0$ a $15$** proporzionale al livello di riempimento del serbatoio d'acqua interno (0 se vuoto, 15 se pieno a 2.000 mB).

---

## 🌬️ Purificatore d'Aria

Il macchinario cardine per l'abbattimento attivo della tossicità atmosferica. Mentre il Deumidificatore previene l'infezione asciugando l'aria, il Purificatore interviene neutralizzando direttamente le spore volatili del Miasma in stanze ermetiche già contaminate.

- **Potenza di Purificazione (48.0)** — Ogni Purificatore attivo sottrae **48.0 punti** dal carico tossico volumetrico della stanza:
  $$\text{targetMiasma} = \max(0.0, \, \text{toxicScore} - \text{ventilationScore} - 48.0 \times N_{\text{purificatori}})$$
  Dato che un singolo tronco ammuffito genera circa $2.25$ punti di tossicità, un solo purificatore neutralizza le esalazioni di **oltre 21 blocchi infetti contemporaneamente**, azzerando il Miasma in stanze sigillate fino a $2048\text{ m}^3$.
- **Alimentazione Ibrida (32.000 FE / Combustibile 4.0×)** — Opera consumando **10 FE/tick** durante la filtrazione attiva ($0\text{ FE/tick}$ in standby/depleted). Supporta ricarica da cavi elettrici (fino a 500 FE/t su qualsiasi faccia) o combustibile solido nello Slot 0 con resa quadruplicata ($4.0\times$, un pezzo di carbone eroga $64.000\text{ FE}$, saturando l'intero buffer).
- **Cartucce Filtro Spore & Usura Dinamica (Spore Filters)** — Alloggiate nello Slot 1 (stack fino a 64 unità):
  - **Durabilità Base**: **2.400 tick (2 minuti continui)** per filtro al ritmo di 1 punto/tick.
  - **Auto-Reload**: all'esaurimento della cartuccia attiva, il macchinario preleva istantaneamente il filtro successivo dallo stack di scorta.
  - **Usura Accelerata sotto Miasma Letale**: se la stanza tocca la concentrazione critica *LETHAL_POISON*, l'usura raddoppia a **2 punti/tick** (durata 60 secondi) a causa del sovraccarico di microspore.
  - **Allarme Esaurimento (FILTER_DEPLETED)**: se i filtri terminano, la macchina spegne la griglia ed emette un click a vuoto metallico (`BLOCK_DISPENSER_FAIL`).
- **Automazione a Tramogge (SidedInventory)**:
  - **Faccia Superiore (UP)**: la tramoggia inserisce *esclusivamente* i **Filtri di Spore** (Slot 1).
  - **Facce Laterali e Inferiore (NORTH, SOUTH, EAST, WEST, DOWN)**: accettano *esclusivamente* il **Combustibile solido** (Slot 0).
- **Interfaccia, Redstone e Comparatore**:
  - **Controllo Redstone**: toggle a tre stati (*Sempre Acceso* `IGNORED`, *Acceso con Redstone* `HIGH`, *Spento con Redstone* `LOW`). Collegato a un Rilevatore di Spore a parete, si accende in automatico solo all'accumulo di miasma, azzerando gli sprechi di filtri a stanza bonificata.
  - **Uscita Comparatore**: emette un segnale analogico da **$0$ a $15$** proporzionale alla scorta di filtri residui nello slot (0 se vuoto, 15 per uno stack completo da 64), ideale per accendere spie d'allarme di rifornimento.

---

| | |
| :--- | ---: |
| [← Difendersi](05_difendersi.md) | [Il Decadimento nell'Ultimo Dettaglio →](07_il_decadimento_nellultimo_dettaglio.md) |
| [📑 Indice](README.md) | |
