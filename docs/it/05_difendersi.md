# 🤿 Difendersi

Conoscere i rischi non basta: bisogna avere gli strumenti giusti. La mod introduce un set coerente di meccaniche di difesa, dalla prevenzione passiva all'equipaggiamento protettivo attivo.

---

## 🐝 Ceratura Preventiva

Applicare un **Favo d'Api** su qualsiasi blocco di legno lo sigilla con uno strato di cera, congelando permanentemente il suo stadio attuale.

Un blocco cerato:
- Non decade ulteriormente, indipendentemente dalle condizioni ambientali
- Non emette spore nell'aria circostante
- Non può contagiare i blocchi adiacenti
- Droppa sempre al **100%** quando rotto, anche allo Stadio 3

La cera non fa guarire il blocco — lo conserva nello stadio in cui si trova. Un blocco Marcio cerato rimane Marcio, ma smette di essere un problema.

## 🪓 Cura con l'Ascia

Con **Furtività (Sneak) + Tasto Destro** tenendo un'ascia, è possibile intervenire direttamente sul blocco:

- **De-ceratura**: rimuove il rivestimento di cera (`ITEM_AXE_WAX_OFF`), riattivando il ciclo biologico al costo di 1 punto di durabilità.
- **Cura della Muffa**: su legno non cerato di Stadio 1 o 2, l'ascia scrosta le ife superficiali (`ITEM_AXE_SCRAPE`), facendo retrocedere l'infezione di uno stadio ($2 \rightarrow 1 \rightarrow 0$) al costo di 1 punto di durabilità.
- **Stadio 3 (Marcio) — Totalmente Incurabile**: la struttura interna è compromessa irrimediabilmente. L'ascia non produce alcun effetto sul legno marcio. L'unico modo per renderlo inerte senza distruggerlo è sigillarlo con un favo d'api (ceratura).

---

## 😷 La Maschera Antispore

La `Spore Mask` è l'unico equipaggiamento che garantisce sopravvivenza passiva nel miasma prolungato. Si equipaggia al posto dell'elmo e presenta un modello 3D protrudente con visiera, respiratori e cartuccia filtrante.

**Protezione & Combattimento**: annulla completamente gli effetti letali del miasma (Fame, Nausea, Veleno). Funziona anche come armatura leggera (fornisce **1 punto armatura**, equivalente a un elmo in pelle, con **165 punti di durabilità**): si danneggia sia normalmente subendo colpi in **combattimento**, sia consumando 1 punto di durabilità a ogni ciclo in cui filtra l'aria tossica al posto dei polmoni del giocatore.

**Riparazione**: esclusivamente con i **Filtri di Lana (Spore Filters)** su un'incudine — craftati con lana, carbonella e spago. Ogni filtro ripristina il 100% della durabilità. In alternativa, si possono combinare due maschere usurate nella griglia di crafting per una riparazione rapida sul campo.

### 🔮 Incantabilità della Maschera Antispore
La Maschera Antispore ha **Incantabilità = 0** (non può essere incantata al Tavolo degli Incantesimi). Può ricevere incantesimi **esclusivamente tramite libri incantati su un'Incudine**, con rigide restrizioni di compatibilità:

| Incantesimo | Compatibilità Maschera | Effetto sulla Maschera |
| :--- | :---: | :--- |
| **Indistruttibilità (Unbreaking I–III)** | ✅ **Consentito** | Riduce la probabilità di usura sia per i colpi subiti che per l'aria filtrata. |
| **Ripristino (Mending)** | ✅ **Consentito** | Ripara la durabilità della maschera raccogliendo sfere di esperienza. |
| **Maledizione della Scomparsa (Vanishing)** | ✅ **Consentito** | La maschera svanisce alla morte del giocatore invece di cadere a terra. |
| **Filtrazione Spore (Spore Filtration)** | ❌ **Incompatibile** | **Non applicabile**: la maschera filtra già nativamente il miasma; l'incantesimo è ridondante. |
| **Protezione / Respirazione / Affinità / Spine** | ❌ **Incompatibile** | Rifiutati: la maschera è un respiratore tecnico, non un elmo da guerra incantato. |

---

### ✨ Incantesimo per Elmi: Filtrazione Spore (`Spore Filtration`)

`Spore Filtration` è un incantesimo progettato specificamente per **qualsiasi elmo convenzionale** (in pelle, ferro, diamante, Netherite, tartaruga). Permette a chi indossa un'armatura standard di respirare in sicurezza dentro il miasma senza dover indossare la Maschera Antispore, scaricando il carico tossico sulla durabilità dell'elmo:

| Livello | Consumo Durabilità per Ciclo | Efficienza di Risparmio | Comportamento |
| :---: | :---: | :---: | :--- |
| **I** | **2 punti** / ciclo | Standard | Filtrazione grezza: neutralizza il miasma ma usura rapidamente l'elmo. |
| **II** | **1 punto** / ciclo | Ottimizzata | Filtrazione bilanciata: equipara l'efficienza a quella della Maschera Antispore base. |
| **III** | **0 o 1 punto** (media 0.5) | **50% Salvaguardia** | Filtrazione avanzata: **50% di probabilità di non consumare durabilità** a ogni ciclo di esposizione. |

> [!TIP]
> Applicato su un elmo ad alta resistenza (come un elmo in Netherite abbinato a *Indistruttibilità III* e *Ripristino*), `Spore Filtration III` consente di esplorare e combattere in sicurezza dentro ambienti con miasma letale mantenendo la massima protezione dell'armatura pesante!


---

## 🧭 Rilevatori Portatili

Per diagnosticare ambienti sconosciuti o pianificare interventi:

**💧 Rilevatore di Umidità** — Tenuto in mano e usato a vuoto (click destro), scatta con un click meccanico e invia in chat privata un report analitico del Rischio d'Infezione locale ($H_{eff}$, luce, temperatura, catalizzatori adiacenti). Utile per capire all'istante perché una stanza continua a marcire.

**☢️ Rilevatore di Spore** — Tenuto in mano e usato a vuoto (click destro), scatta con un click meccanico ed esegue una scansione istantanea dell'aria attorno agli occhi del giocatore (volume stanza, ventilazione attiva, densità spore, trend dinamico). È completamente silenzioso durante gli spostamenti (nessun ticchettio passivo continuo), garantendo la massima discrezione nell'esplorazione.

*La modalità stazionaria di questi rilevatori — come sensori Redstone ibridi a parete/pavimento/soffitto — è trattata nel [Capitolo 6](06_automatizzare_la_bonifica.md).*

---

| | |
| :--- | ---: |
| [← L'Aria che Uccide](04_laria_che_uccide.md) | [Automatizzare la Bonifica →](06_automatizzare_la_bonifica.md) |
| [📑 Indice](README.md) | |
