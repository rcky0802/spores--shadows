# 💻 Configurazione e Tecnica

Un riferimento per modpackers, operatori server e giocatori avanzati che vogliono capire o modificare il comportamento della mod.

---

## 📖 Integrazione JEI (Just Enough Items)

La mod integra 7 categorie JEI native per documentare le meccaniche direttamente in-game, senza richiedere wiki esterne:

1. **Ceratura** — tutte le trasformazioni blocco → blocco cerato tramite favo d'api
2. **Raschiatura** — rimozione cera e cura muffa ($2 \rightarrow 1 \rightarrow 0$) con l'ascia
3. **Recupero Assi** — le griglie di pulizia e compressione delle assi infette
4. **Ciclo d'Infezione** — la progressione naturale ($0 \rightarrow 1 \rightarrow 2 \rightarrow 3$) visualizzata
5. **Schede Informative** — friabilità, assenza di drop, regole speciali per stadio 3
6. **Deumidificatore** — consumi energetici, raggio d'azione, modalità operative
7. **Purificatore d'Aria** — ricette filtri e capacità di purificazione

## 🔍 Integrazione Jade / WTHIT

Il tooltip contestuale mostra per ogni blocco inquadrato:

- Stadio di muffa e stato di ceratura
- Rischio d'Infezione locale ($R\%$) con colore dinamico (grigio = stabile, rosso = a rischio)
- Livello di riempimento delle compostiere
- Stato energetico e modalità attiva dei macchinari

## 🏆 Progressi (Advancements)

Il mod include un albero completo di **11 progressi** suddivisi tra sopravvivenza, monitoraggio tecnologico e grandi opere di bonifica:

### 🌿 Sopravvivenza e Cura della Muffa
- **Spores & Shadows** *(Radice)* — Sopravvivi al decadimento della natura nel tuo mondo.
- **Prevenzione Naturale** (*Natural Prevention*) — Usa un favo di miele per cerare un blocco di legno e arrestare per sempre la muffa.
- **Olio di Gomito** (*Elbow Grease*) — Raschia via un livello di muffa da un blocco di legno con un'ascia.
- **Respiro Corto** (*Short Breath*) — Subisci l'avvelenamento da miasma respirando aria satura di spore.
- **Polvere alla Polvere** (*Dust to Dust*) — Tenta di rompere un blocco di legno marcio (Stadio 3) non cerato e guardalo sgretolarsi nel nulla senza drop.

### 🧭 Strumentazione e Monitoraggio
- **Sensore di Umidità** (*Moisture Sensor*) — Fabbrica un Rilevatore di Umidità per monitorare l'igrometria ambientale.
- **Sentinella dell'Aria** (*Air Sentry*) — Fabbrica un Rilevatore di Spore per monitorare la concentrazione del miasma e la qualità dell'aria.

### ⚙️ Ingegneria e Grandi Sfide di Bonifica
- **Controllo del Clima** (*Climate Control*) — Costruisci un Deumidificatore per asciugare stanze chiuse e raccogliere acqua di condensa.
- **Bunker Ermetico** (*Hermetic Bunker*) — Fabbrica un Depuratore d'Aria per bonificare il miasma e rendere respirabili stanze sotterranee sigillate.
- 🏆 **Oasi Sotterranea** (*Dry Oasis*, Sfida) — Asciuga una stanza sotterranea profonda ($Y \le 40$) portando l'umidità effettiva al di sotto del 15% tramite Deumidificatore.
- 🏆 **Aria Pura nel Sottosuolo** (*Pure Air in the Depths*, Sfida) — Bonifica completamente una stanza sotterranea nelle profondità del mondo ($Y \le 0$) contaminata da muffa, ripristinando la qualità dell'aria a livello `CLEAN`.

---

## ⚙️ Configurazione (ModMenu & Cloth Config)

Spores & Shadows espone 19 categorie di configurazione modificabili a caldo dall'interfaccia di ModMenu (salvate in `config/spores_and_shadows.json`):

| Categoria | Cosa controlla |
| :--- | :--- |
| **General** | Toggle decadimento globale, raggio di scansione, soglia infezione (default 0.50), logorio asce, crollo blocchi d'interazione (10%) |
| **Susceptibility** | Moltiplicatori di vulnerabilità per formato ($S_{mat}$): scortecciato (1.4×), assi/scale/gradini/mosaici (0.8×), default (1.0×) |
| **Catalysts** | Pesi catalizzatori ($C_{bonus}$): fango (+0.05), podzol/micelio (+0.15), funghi (+0.25), Spore Blossom (+0.80), blocchi infetti (+0.03 / +0.06 / +0.12) |
| **Environment** | Umidità base pioggia/secco, gradiente profondità, contributo acqua, aerazione e velocità saturazione/dissipazione umidità |
| **Drops** | Probabilità drop senza Silk Touch: Stadio 2 (50%) e Stadio 3 (0%) |
| **Structures** | Pre-decadimento strutture vanilla generate nel mondo e bonus ambientali (sott'acqua, profondità, contatto col suolo) |
| **Furnace Multipliers** | Potere calorifico del combustibile per stadio (1.0×, 0.5×, 0.25×, 0.125×) |
| **Flammability** | Bonus innesco fiamma (+5, +10, +20) e propagazione incendio (+10, +25, +60) per gli stadi 1, 2 e 3 |
| **Blast Resistance** | Moltiplicatori resistenza esplosioni (TNT) per stadio (80%, 50%, 10%) |
| **Hardness** | Scaling durezza blocchi (80%, 50%, 20%) |
| **Redstone** | Durata prolungata pulsanti/pedane e percentuale cilecca bauli trappola (15%, 50%, 85%) |
| **Composter** | Probabilità di successo nel compostatore per stadio (50%, 65%, 85%) |
| **Particles** | Conteggio e tipologie particelle emesse alla rottura dei blocchi infetti |
| **Spore Detector** | Delay tick (iniziale/periodico), moltiplicatore segnale Redstone (default 5× per stadio), cooldown uso, statistiche Maschera |
| **Moisture Detector** | Delay tick (iniziale/periodico), moltiplicatore segnale Redstone (default 5× per stadio), cooldown uso igrometro manuale |
| **Dehumidifier** | Capacità serbatoio (2000 mB), tick per mB (24), efficienza combustibile (4.0×), potere deumidificante (1.0), capacità e consumo FE |
| **Air Purifier** | Potere di pulizia tossica (48.0), durata filtri Spore Filter (2400 tick / 2 min), efficienza combustibile (4.0×), capacità e consumo FE |
| **Toxicity** | Volume BFS (2048 m³), raggio (16 blocchi), ventilazione nodi (Base 6/24), 3 soglie tossicità (6, 12, 18), Maschera e Spore Filtration |
| **Client** | Offset Z anti-z fighting rendering blocchi (0.002) e intensità overlay grafico muffa nelle GUI (1.0) |

## 💻 Comandi Amministrativi

Richiedono livello operatore 2:

- `/miasma` — Scansione BFS in tempo reale: volume stanza, toxic score, ventilazione attiva, classificazione ambiente (Aperto / Confinato).
- `/moldrisk` — Scomposizione completa del Rischio $R$ per il blocco inquadrato: $H_{eff}$, $L_{uv}$, $S_{mat}$, catalizzatori rilevati, $M_{bonus}$, $T_{mult}$, valore $R$ finale.

---

| | |
| :--- | ---: |
| [← Il Decadimento nell'Ultimo Dettaglio](07_il_decadimento_nellultimo_dettaglio.md) | [📑 Indice](README.md) |
