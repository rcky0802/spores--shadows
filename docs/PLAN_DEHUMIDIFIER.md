# 💧 Piano di Progettazione: Deumidificatore (Macchinario & Integrazione Atmosferica)

Questo documento definisce il design, la fisica operativa, l'interfaccia utente e l'architettura tecnica per l'implementazione del **Deumidificatore**, il macchinario primario per il controllo attivo dell'umidità negli ambienti chiusi e sotterranei.

> [!NOTE]
> **Prerequisiti già completati:**
> Il modello volumetrico dinamico dell'umidità della stanza ($H(t)$, $H_{\text{target}}$, confinamento BFS, diluizione volumetrica e inerzia temporale $\alpha_{\text{sat}} / \alpha_{\text{diss}}$ gestite da `RoomSaturationManager` e `RoomAtmosphereCalculator`) è già stato interamente implementato, sincronizzato con il comando `/moldrisk` e Jade, e verificato dai GameTest.

---

## 1. Visione di Gioco: Il Controllo dell'Umidità Sotterranea

Nelle cantine, nei bunker, nelle miniere e nei magazzini sotterranei privi di finestre o camini verso il cielo, l'umidità accumulata dai blocchi d'acqua e dalla profondità geologica tende a saturare la stanza portando a infezioni di muffa.

Il **Deumidificatore** rappresenta la soluzione tecnologica attiva:
1. **Pozzo Attivo di Umidità:** Quando è in funzione (`RUNNING`), estrae umidità continua dall'aria della stanza, abbattendo linearmente il target di equilibrio $H_{\text{target}}$ della camera.
2. **Inerzia e Sicurezza (Grace Period):** Grazie al motore dinamico $H(t)$, quando il deumidificatore è attivo la stanza si asciuga progressivamente con velocità $\alpha_{\text{diss}}$. Se il combustibile si esaurisce o il serbatoio si riempie, la stanza non ridiventa all'istante umida, ma risatura gradualmente con velocità $\alpha_{\text{sat}}$, concedendo al giocatore il tempo per effettuare la manutenzione.
3. **Raccolta della Condensa:** L'umidità estratta viene condensata sotto forma di acqua liquida in un serbatoio interno, riutilizzabile o automatizzabile.

---

## 2. Specifiche Tecniche del Macchinario

### 2.1 Alimentazione e Consumi
* **Combustibile Solido:** Accetta tutti i combustibili convenzionali di Minecraft (carbone, carbonella, blocchi di carbone, legno, secchi di lava, ecc.).
* **Efficienza Quadruplicata (`fuel_multiplier = 4.0x`):** 
  - 1 Carbone (80 secondi in una fornace) dura **320 secondi** (~5 minuti e 20 secondi) nel Deumidificatore.
  - 1 Secchio di Lava dura oltre **66 minuti**.
* **Consumo Intelligente (Auto-Standby):** Se la macchina è piena d'acqua (2000 mB) o se la stanza circostante è già completamente asciutta ($H(t) \le 0.05$), il consumo di carburante viene messo in pausa.

### 2.2 Serbatoio della Condensa
* **Capacità:** **2000 mB** (pari a 2 Secchi d'Acqua).
* **Tasso di Condensazione Dinamico:**
  - Il serbatoio si riempie in proporzione all'umidità effettiva presente nell'aria: in stanze molto umide ($H \ge 0.80$) condensa acqua più rapidamente; in stanze quasi asciutte il tasso di condensazione rallenta.
* **Interazione con Secchio:**
  - *Click Destro* con un **Secchio Vuoto** estrae 1000 mB e restituisce un **Secchio d'Acqua**.
* **Supporto Comparatore Redstone:**
  - Emette un segnale redstone da `0` a `15` proporzionale al livello di riempimento del serbatoio interno (0 mB = 0, 2000 mB = 15). Perfetto per collegare campane, lampade d'allarme o pompe/hopper automatici.

### 2.3 Stato Operativo e Feedback Visivo
Il blocco dispone della proprietà `STATUS` (`DehumidifierStatus`):
* 🔴 **`OFF`**: Spento / Privo di carburante.
* 🟢 **`RUNNING`**: Attivo, brucia combustibile e sottrae umidità all'atmosfera.
* 🔵 **`FULL`**: Serbatoio condensa saturo (2000 mB). Il macchinario va in standby preservando il carburante rimasto.

Proprietà visiva aggiuntiva `WATER_LEVEL` (0..4) sul blocco per indicare a colpo d'occhio il riempimento del serbatoio dall'esterno.

---

## 3. Interfaccia Grafica (GUI)

Schermata pulita in stile vanilla:
* **Slot Combustibile:** Accetta carburanti solidi, con icona semitrasparente a carbonella come guida.
* **Indicatore di Fiamma:** Animazione della fiamma progressiva durante la combustione attiva.
* **Barra del Serbatoio Condensa:** Indicatore verticale a colonna (capacità 2000 mB) con sfumatura d'acqua azzurra e tacche a 1000 mB e 2000 mB.
* **Tooltip Informativi:**
  - Passando il cursore sulla barra dell'acqua: visualizzazione numerica precisa (es. `1250 / 2000 mB`).
  - Passando il cursore sulla fiamma: tempo di combustione residuo.

---

## 4. Integrazione con l'Atmosfera Esistente

L'integrazione sfrutta l'architettura già presente in `RoomAtmosphereCalculator` e `RoomSaturationManager`:

```
┌─────────────────────────────────────────────────────────────┐
│ BFS EXPLORER / ROOM ATMOSPHERE SCANNER                      │
│ • Durante l'esplorazione dell'aria della stanza:            │
│   - Rileva blocchi DehumidifierBlock adiacenti              │
│   - Se STATUS == RUNNING, conteggia il deumidificatore      │
│ • Calcola il target di equilibrio:                          │
│   Htarget = Base + Depth + Acqua - (N_deum * DryingPower)   │
│             - AerationDryingBonus                           │
└─────────────────────────────────────────────────────────────┘
                               ▲
                               │ Sottrae umidità dal bilancio
┌──────────────────────────────┴──────────────────────────────┐
│ DEHUMIDIFIER BLOCK ENTITY (Server Tick)                     │
│ • Legge H(t) della stanza dal RoomSaturationManager         │
│ • Se H(t) > 0 e c'è combustibile:                           │
│   - Brucia carburante e imposta STATUS = RUNNING            │
│   - Incrementa serbatoio interno in base a H(t)             │
│ • Se serbatoio >= 2000 mB:                                  │
│   - Imposta STATUS = FULL (Standby)                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Valori Configurabili (`ModConfig.java`)

Nuova categoria o sezione dedicata al macchinario:
```java
public static class DehumidifierConfig {
    public int dehumidifier_capacity_mb = 2000;
    public int dehumidifier_ticks_per_mb = 24;
    public float dehumidifier_fuel_multiplier = 4.0f;
    public double dehumidifier_drying_power = 1.0;
}
```

---

## 6. Integrazione Jade HUD

Creazione del provider dedicato `DehumidifierBlockProvider` (registrato sia in `IWailaCommonRegistration` che `IWailaClientRegistration`):
* Visualizza:
  - **Stato Operativo:** `Status: Running` (verde), `Status: Standby (Full)` (blu), o `Status: Off` (grigio).
  - **Serbatoio Condensa:** `Water: X / 2000 mB`.
  - **Combustibile:** Tempo di combustione residuo in secondi/minuti.

---

## 7. Fasi di Implementazione (Roadmap)

- [ ] **Fase 1: Configurazione & Proprietà di Stato**
  - Aggiungere `DehumidifierConfig` in `ModConfig.java` con default equilibrati.
  - Creare enum `DehumidifierStatus` (`OFF`, `RUNNING`, `FULL`).
  - Dichiarare proprietà `STATUS` e `WATER_LEVEL` (0..4).

- [ ] **Fase 2: Blocco & BlockEntity**
  - Implementare `DehumidifierBlock` (VoxelShape, rotazione direzionale, interazione click destro con secchio, logica comparatore redstone).
  - Implementare `DehumidifierBlockEntity` (gestione inventario combustibile, timer di bruciatura moltiplicato x4, accumulo condensa mB, auto-standby a 2000 mB).
  - Registrazione in `ModBlocks`, `ModItems`, `ModBlockEntities`.

- [ ] **Fase 3: GUI & ScreenHandler**
  - Creare `DehumidifierScreenHandler` e `DehumidifierScreen`.
  - Texture per GUI (`dehumidifier_gui.png`) con slot combustibile, fiamma animata e barra serbatoio graduata.
  - Registrazione dello ScreenHandler nel client.

- [ ] **Fase 4: Integrazione con l'Atmosfera della Stanza**
  - In `BFSExplorer` / `RoomAtmosphereCalculator`: identificare i deumidificatori `RUNNING` tra i blocchi perimetrali della stanza.
  - Sottrarre il contributo `dehumidifier_drying_power` in `Htarget`.
  - In `DehumidifierBlockEntity`: leggere `currentHumidity` da `RoomSaturationManager` per modulare la velocità di condensazione.

- [ ] **Fase 5: Integrazione Jade**
  - Creare `DehumidifierBlockProvider` che sincronizza tramite `IServerDataProvider` stato, mB e tempo combustibile sul tooltip Jade.

- [ ] **Fase 6: Ricette, Asset & Datagen**
  - Modello blocco e blockstate con texture animate o varianti LED frontali.
  - Ricetta di crafting bilanciata (ferro, rame, pietra liscia, fornace).
  - Traduzioni complete in 5 lingue (it_it, en_us, de_de, fr_fr, es_es).
  - Loot table per il drop del blocco con conservazione del contenuto se necessario.

- [ ] **Fase 7: GameTest Suite**
  - Test combustione e moltiplicatore efficienza carburante.
  - Test accumulo condensa e standby a 2000 mB.
  - Test svuotamento con secchio vuoto ➔ secchio d'acqua.
  - Test emissione segnale comparatore da 0 a 15.
  - Test abbattimento di $H_{\text{target}}$ e asciugatura $H(t)$ di una stanza chiusa.
