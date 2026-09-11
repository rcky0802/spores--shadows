# 💧 Piano di Progettazione: Deumidificatore / Nebulizzatore (Macchinario & Integrazione Atmosferica)

Questo documento definisce il design, la fisica operativa, l'interfaccia utente, le formule atmosferiche e l'architettura tecnica per l'implementazione del **Deumidificatore**, il macchinario a doppia modalità per il controllo attivo e bidirezionale dell'umidità negli ambienti chiusi e sotterranei.

> [!NOTE]
> **Prerequisiti completati:**
> Il modello volumetrico dinamico dell'umidità della stanza ($H(t)$, $H_{\text{raw}}$, $H_{\text{eff}}$, confinamento BFS, diluizione volumetrica e inerzia temporale $\alpha_{\text{sat}} / \alpha_{\text{diss}}$ gestite da `RoomSaturationManager` e `RoomAtmosphereCalculator`) è interamente implementato, sincronizzato con il comando `/moldrisk`, Jade HUD e verificato al 100% da tutti i GameTest.

---

## 1. Visione di Gioco: Il Controllo Bidirezionale del Clima

Nelle cantine, nei bunker, nelle miniere e nei magazzini sotterranei privi di ventilazione verso il cielo, l'umidità accumulata dai blocchi d'acqua e dalla profondità geologica tende a saturare l'aria favorendo la proliferazione e diffusione della muffa. Al contempo, serre sotterranee o camere di bioprocesso richiedono un'elevata umidità artificiale per favorire colture specifiche.

Il macchinario supporta **due modalità operative commutabili a piacimento**:

### 1.1 Modalità Deumidificatore (`DEHUMIDIFY`)
1. **Pozzo Attivo di Umidità:** Quando è in funzione (`RUNNING`), estrae umidità dall'aria della stanza, abbattendo l'Umidità Effettiva ($H_{\text{eff}}$) della camera senza alterare la sorgente grezza.
2. **Inerzia e Sicurezza (Grace Period):** Grazie al motore dinamico $H(t)$, la stanza si asciuga progressivamente con velocità $\alpha_{\text{diss}}$. Se il combustibile si esaurisce o il serbatoio si riempie, la stanza risatura gradualmente con velocità $\alpha_{\text{sat}}$, concedendo tempo per la manutenzione.
3. **Raccolta della Condensa:** L'umidità estratta viene condensata sotto forma di acqua liquida in un serbatoio interno da 2000 mB, riutilizzabile o automatizzabile.

### 1.2 Modalità Nebulizzatore / Umidificatore (`HUMIDIFY`)
1. **Sorgente Attiva di Vapore:** Quando è in funzione (`RUNNING`), vaporizza acqua pura dal proprio serbatoio interno, incrementando direttamente l'Umidità Grezza ($H_{\text{raw}}$) dell'ambiente circostante.
2. **Umidificazione Controllata:** Consente di inumidire stanze aride o cantine per scopi agricoli, micologici o sperimentali.
3. **Consumo Idrico:** Consuma acqua dal serbatoio (1 mB ogni intervallo di tick) combinata a carburante/energia, arrestandosi in standby (`OFF`) quando il serbatoio è vuoto o l'ambiente è saturo ($H \ge 0.95$).

---

## 2. Specifiche Tecniche del Macchinario

### 2.0 Orientamento del Blocco
* **Stile:** Orizzontale, conforme agli standard Vanilla (Furnace, Blast Furnace, Smoker).
* **Proprietà:** `HORIZONTAL_FACING` (4 direzioni: `NORTH`, `SOUTH`, `EAST`, `WEST`).
* **Piazzamento:** Sempre appoggiato a terra, la faccia frontale rivolta verso il giocatore al piazzamento.
* **Motivazione:** L'indicatore di livello serbatoio (`WATER_LEVEL` 0..4) richiede un orientamento verticale fisso affinché la canalina graduata sia coerente.

### 2.1 Alimentazione Ibrida e Consumi (Dual-Power)
* **Combustibile Solido:** Accetta tutti i combustibili convenzionali di Minecraft (carbone, carbonella, blocchi di carbone, legno, secchi di lava, ecc.).
* **Efficienza Quadruplicata (`fuel_multiplier = 4.0x`):** 
  - 1 Carbone (80 secondi in una fornace) dura **320 secondi** (~5 minuti e 20 secondi).
  - 1 Secchio di Lava dura oltre **66 minuti**.
* **Supporto Energetico TR Energy / RF / Forge Energy:**
  - Standard Fabric: `teamreborn:energy` (compatibile 1:1 con Tech Reborn, Modern Industrialization, Applied Energistics 2, Powah, ecc.).
  - **Buffer Interno:** $10.000\text{ E/RF}$.
  - **Consumo Operativo:** $10\text{ E/tick}$ ($200\text{ E/sec}$) quando attivo in stato `RUNNING`.
  - **Priorità Elettrica:** Se il buffer elettrico ha carica $\ge 10\text{ E}$, consuma energia e non tocca il combustibile solido.
  - **Backup Solido:** Passaggio automatico al combustibile solido se l'energia si esaurisce.
  - **Ricezione Omnidirezionale:** Connessione cavi da tutti i 6 lati (`UP`, `DOWN`, `NORTH`, `SOUTH`, `EAST`, `WEST`).
  - **Puro Consumatore:** `canExtract() = false` (non esporta energia all'esterno).

### 2.2 Gestione Fluidi Bidirezionale (Serbatoio Condensa / Acqua)
* **Capacità:** **2000 mB** (pari a 2 Secchi d'Acqua Vanilla / $162.000\text{ droplet}$).
* **Inserimento Fluido (Fill):**
  - Consentito **esclusivamente per Acqua Pura** (`minecraft:water` / `Fluids.WATER`).
  - Qualsiasi altro fluido (lava, oli, pozioni) viene categoricamente respinto sia da click destro che da automazioni.
  - Supporto *Click Destro* con un **Secchio d'Acqua**: inserisce 1000 mB e restituisce un Secchio Vuoto.
* **Estrazione Fluido (Drain):**
  - Supporto *Click Destro* con un **Secchio Vuoto**: estrae 1000 mB e restituisce un Secchio d'Acqua.
* **Integrazione Pipe Fluidi (Fabric Transfer API):**
  - Esposizione bidirezionale tramite `FluidStorage.SIDED`:
    - Accetta in ingresso solo `Fluids.WATER`.
    - Permette sempre l'estrazione di `Fluids.WATER`.
    - Accessibile da tutti i 6 lati del blocco.
* **Supporto Comparatore Redstone:**
  - `getComparatorOutput()` emette un segnale redstone da `0` a `15` proporzionale al volume d'acqua presente (0 mB = 0, 2000 mB = 15).

### 2.3 Stato Operativo e Feedback Visivo LED (4 Stati)
Il blocco memorizza nel proprio BlockState le proprietà `STATUS`, `MODE` e `WATER_LEVEL`:
* **`STATUS` (`DehumidifierStatus`):** `OFF`, `RUNNING`, `FULL`.
* **`MODE` (`DehumidifierMode`):** `DEHUMIDIFY`, `HUMIDIFY`.
* **`WATER_LEVEL` (0..4):** Livello visivo graduato a 5 stadi nella canalina frontale:
  - `0`: 0 mB
  - `1`: 1 - 500 mB
  - `2`: 501 - 1000 mB
  - `3`: 1001 - 1500 mB
  - `4`: 1501 - 2000 mB

#### Il LED Frontale 3x3 del Blocco:
Sul pannello frontale in stile steampunk, la spia LED a 3×3 pixel rispecchia immediatamente lo stato e la modalità:
* 🟢 **Verde (`#00E500`):** In funzione come **Deumidificatore** (`RUNNING` + `DEHUMIDIFY`).
* 🩵 **Ciano / Azzurro Vapore (`#00E5FF`):** In funzione come **Nebulizzatore** (`RUNNING` + `HUMIDIFY`).
* 🔴 **Rosso (`#E50000`):** Spento, in standby per assenza di risorse o inibito dal segnale Redstone (`OFF`).
* 🔵 **Blu (`#0055FF`):** Serbatoio pieno a 2000 mB durante la deumidificazione (`FULL`).

### 2.4 Controllo Redstone (`RedstoneMode`)
Configurabile tramite pulsante ciclico nella GUI:
* 🔘 **`IGNORED` (Default):** Funziona sempre, indipendentemente dal segnale Redstone ricevuto.
* 🔴 **`LOW` (Invertito):** Funziona solo se il segnale Redstone ricevuto è 0; si arresta se riceve segnale ($> 0$).
* ⚡ **`HIGH` (Attivo su Segnale):** Funziona solo se riceve un segnale Redstone attivo ($> 0$).

### 2.5 Automazione con Hopper (Carburante Omnidirezionale)
* Implementa `SidedInventory`: lo slot combustibile è accessibile da tutte le 6 facce del blocco per l'inserimento automatico da Hopper, Dropper o condotti.

### 2.6 Suoni e Particelle
* **Deumidificatore (`RUNNING` + `DEHUMIDIFY`):**
  - Crepitio di combustione (`SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE` a basso volume).
  - Gorgoglio condensa ogni ~80 tick (`SoundEvents.BLOCK_BREWING_STAND_BREW`).
  - Particelle `ParticleTypes.SMOKE` dalla grata superiore.
  - Quando `FULL`: particelle `ParticleTypes.DRIPPING_WATER` sul fondo del blocco.
* **Nebulizzatore (`RUNNING` + `HUMIDIFY`):**
  - Sfiato e vaporizzazione d'aria umida.
  - Particelle di vapore e goccioline nebulizzate emesse frontalmente/superiormente.
* **Svuotamento/Riempimento a secchio:** `SoundEvents.ITEM_BUCKET_FILL` / `ITEM_BUCKET_EMPTY`.
* **Piazzamento del blocco:** `SoundEvents.BLOCK_ANVIL_PLACE` (metallico steampunk).

### 2.7 Comportamento alla Rottura
* Il blocco droppa sé stesso come item.
* Il carburante rimasto nello slot viene droppato a terra.
* L'acqua accumulata nel serbatoio viene dispersa.

---

## 3. Interfaccia Grafica (GUI)

La schermata ([`DehumidifierScreen`](file:///C:/Users/rcky0/Desktop/spores--shadows/src/client/java/moldmod/client/screen/DehumidifierScreen.java)) offre un pannello di controllo completo:
1. **Slot Carburante:** Accetta combustibili solidi con icona guida a carbonella semitrasparente.
2. **Indicatore Fiamma Animato:**
   - Animazione progressiva di bruciatura.
   - Tonalità arancione durante la combustione di carbone/legna; tonalità blu/elettrica se alimentato da TR/RF Energy.
3. **Barra Graduata del Serbatoio Fluido:**
   - Colonna graduata da 2000 mB con texture fluida azzurra e tacche a 1000 e 2000 mB.
   - Tooltip interattivo con lettura numerica esatta (es. `1450 / 2000 mB`).
4. **Pulsante Modalità Redstone:**
   - Cicla tra `IGNORED`, `LOW` e `HIGH` con icona dinamica e sincronizzazione client-server via `DehumidifierRedstonePayload`.
5. **Pulsante Modalità Operativa (Deumidificatore / Nebulizzatore):**
   - Icona goccia barrata (Deumidificatore) vs nuvola di vapore (Nebulizzatore).
   - Tooltip esplicativo con descrizione della modalità selezionata.
   - Sincronizzazione in tempo reale tramite `DehumidifierModePayload`.

---

## 4. Integrazione con l'Atmosfera e le Formule Fisiche

La distinzione tra **Umidità Grezza ($H_{\text{raw}}$)** e **Umidità Effettiva ($H_{\text{eff}}$)** è implementata coerentemente in tutto il motore atmosferico:

```
┌─────────────────────────────────────────────────────────────────────────┐
│ ROOM ATMOSPHERE CALCULATOR (RoomAtmosphereCalculator.java)              │
│                                                                         │
│ 1. Calcolo Umidità Grezza:                                              │
│    Hraw = clamp(Hbase + Mdepth + BroomWater + HumidifierMoistureBonus,  │
│                 0.0, 1.0)                                               │
│    -> Il NEBULIZZATORE incrementa Hraw (vapore immesso nella stanza)    │
│                                                                         │
│ 2. Calcolo Umidità Effettiva (Target):                                  │
│    Heff = clamp(Hraw - DehumidifierDryingBonus, 0.0, 1.0)               │
│    -> Il DEUMIDIFICATORE abbatte Heff (asciuga l'aria della stanza)     │
└─────────────────────────────────────────────────────────────────────────┘
                                ▲
                                │ Modulazione dinamica dell'aria
┌───────────────────────────────┴─────────────────────────────────────────┐
│ DEHUMIDIFIER BLOCK ENTITY (Server Tick)                                 │
│ • Modalità DEHUMIDIFY:                                                  │
│   - Se H(t) > 0.05 e c'è combustibile/energia:                          │
│     -> Condensa acqua nel serbatoio (fino a 2000 mB -> FULL)            │
│ • Modalità HUMIDIFY:                                                    │
│   - Se serbatoio > 0 mB e H(t) < 0.95 e c'è combustibile/energia:       │
│     -> Consuma acqua (1 mB / tick_rate) ed eroga HumidifierMoistureBonus│
└─────────────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ MOLD RISK CALCULATOR (MoldRiskCalculator.java)                          │
│ • Hraw determina l'umidità ambientale di base per spore e funghi        │
│ • Heff determina l'umidità effettiva corretta per attecchimento muffa   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Valori Configurabili (`ModConfig.java`)

Configurabile tramite file di configurazione e Cloth Config GUI:
```java
public static class DehumidifierConfig {
    public int dehumidifier_capacity_mb = 2000;
    public int dehumidifier_ticks_per_mb = 24;
    public float dehumidifier_fuel_multiplier = 4.0f;
    public double dehumidifier_drying_power = 1.0;
    public double humidifier_moisture_bonus = 0.50;
}
```

---

## 6. Integrazioni Jade & JEI

### 6.1 Jade HUD (`DehumidifierBlockProvider`)
Fornisce informazioni complete in overlay:
* **Modalità:** `Mode: Dehumidifier` (azzurro chiaro) oppure `Mode: Nebulizer` (ciano).
* **Stato:** `Status: Running` (verde/ciano), `Status: Standby (Full)` (blu), `Status: Off` (rosso/grigio).
* **Serbatoio Fluido:** `Water: X / 2000 mB`.
* **Combustibile / Energia:** Durata residua in secondi/minuti o livello di carica RF/TR.

### 6.2 Just Enough Items (JEI Plugin)
* Scheda informativa per l'item `spores--shadows:dehumidifier`:
  - Doppia modalità di funzionamento (asciugatura vs inumidimento).
  - Alimentazione ibrida (combustibili convenzionali 4x e TR Energy $10\text{ E/tick}$).
  - Gestione fluidi con secchio o tubi automatici (solo acqua pura).
  - Supporto redstone e interazione con comparatore.

---

## 7. Ricetta di Crafting & Advancements

### 7.1 Ricetta di Crafting
```
 S C S
 I F I
 S M S
```
* `F` = Fornace (`minecraft:furnace`)
* `I` = Lingotto di Ferro (`minecraft:iron_ingot`)
* `C` = Lingotto di Rame (`minecraft:copper_ingot`)
* `S` = Pietra Liscia (`minecraft:smooth_stone`)
* `M` = Rilevatore di Umidità (`spores--shadows:moisture_detector`)

### 7.2 Albero Advancements
* *"Sensore di Umidità"*: Ottieni o crafta un Rilevatore di Umidità.
* *"Controllo del Clima"*: Crafta e posiziona un Deumidificatore.
* *"Oasi Sotterranea"*: Riduci l'umidità $H(t)$ di una stanza sotterranea ($Y \le 40$) sotto il 15%.

---

## 8. Roadmap di Implementazione & Stato

Tutti i moduli sono stati completati, integrati e validati:
- [x] **Fase 1: Configurazione & Enum di Stato** (`DehumidifierStatus`, `DehumidifierMode`, `ModConfig`).
- [x] **Fase 2: Blocco & BlockEntity** (`DehumidifierBlock`, `DehumidifierBlockEntity`, `FluidStorage`, `EnergyStorage`).
- [x] **Fase 3: GUI & Networking** (`DehumidifierScreen`, `DehumidifierScreenHandler`, `DehumidifierRedstonePayload`, `DehumidifierModePayload`).
- [x] **Fase 4: Integrazione Atmosferica & Formule** (distinzione tra $H_{\text{raw}}$ e $H_{\text{eff}}$ in `RoomAtmosphereCalculator` e `MoldRiskCalculator`).
- [x] **Fase 5: Asset & Grafica Steampunk** (texture blocco con canalina liscia a 5 livelli, spia LED a 4 stati ciano/verde/rosso/blu, 120 varianti blockstate).
- [x] **Fase 6: Integrazioni Jade HUD & JEI Plugin** (`DehumidifierBlockProvider`, `SporesShadowsJEIPlugin`).
- [x] **Fase 7: Advancements, Ricette & Traduzioni** (5 lingue: IT, EN, DE, FR, ES).
- [x] **Fase 8: Test Suite** (100% test superati: unit test e 204 GameTest).
