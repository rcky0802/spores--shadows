# 🧭 Piano di Implementazione: Rilevatore di Miasma & Termometro a Spore (*Spore Detector / Miasmometer*)

Documento di pianificazione tecnica e artistica per l'implementazione del **Punto 4 della TODO.md** in **Spores & Shadows**.

---

## 📌 1. Visione del Dispositivo
Il **Rilevatore di Miasma / Termometro a Spore** è uno strumento di misurazione scientifico-biologico a tema *rame/vetro vintage* in grado di:
1. **Monitorare in tempo reale** l'aria circostante quando tenuto in mano (aggiornando la colonnina di liquido graduata e ticchettando stile contatore Geiger).
2. **Eseguire una diagnosi istantanea** con **`Shift + Tasto Destro`** mostrando un report chiaro e compatto nell'Action Bar.
3. **Essere posizionato con `Tasto Destro`** come blocco decorativo/funzionale a parete o a terra.
4. **Emettere segnale di Pietrarossa (Redstone)** proporzionale alla concentrazione di spore per automatizzare la futura *Ventola di Ventilazione (Punto 5)*.

---

## 🎨 2. Direzione Artistica & Creazione Asset

### A. Texture dell'Item (`spore_detector.png` / Sub-Textures)
* **Formato**: 16x16 pixel art.
* **Palette**: Rame ossidato/lucido, bordi in ottone, tubicino di vetro trasparente e liquido interno fluorescente/reattivo.
* **Stati della Scala Graduata (Item Model Properties)**:
  * `0.0 (Clean)`: Liquido verde/azzurro trasparente sul fondo del bulbo.
  * `1.0 (Warning)`: Colonnina sale al primo terzo con tonalità gialla.
  * `2.0 (Hunger)`: Colonnina sale a metà con colorazione arancione ambrata.
  * `3.0 (Poison)`: Colonnina colma fino in cima, rosso vivo pulsante.

### B. Modello 3D del Blocco (Parete & Pavimento)
* **Versione a Parete (*Wall Spore Detector*)**:
  * Placca posteriore in rame sagomata (spessore 2 voxel).
  * Staffe metalliche che reggono il tubetto di vetro graduato (alto 10 voxel, largo 2 voxel).
  * Bulbo sferico inferiore in rame/vetro.
* **Versione a Pavimento (*Floor Spore Detector*)**:
  * Piedistallo a treppiede in rame da appoggio.
  * Tubo verticale graduato ben visibile a 360°.

---

## ⚙️ 3. Architettura Tecnica & Classi

### A. Classi Java Principali
1. **`SporeDetectorBlock.java`** (estende `Block` o `HorizontalFacingBlock` con `Waterloggable`):
   * **Stati BlockState**:
     * `FACING` (Nord, Sud, Est, Ovest, Up/Floor).
     * `TOXICITY_LEVEL` (IntProperty 0..3: Clean, Warning, Moderate, Lethal).
     * `POWER` (IntProperty 0..15 per output Redstone fine).
   * **Tick & Aggiornamento**:
     * Esegue `calculateMiasma` a intervalli di tick (es. ogni 20-40 tick) per mantenere aggiornata la stanza senza impattare le prestazioni.
   * **Redstone**:
     * Override di `emitsRedstonePower()` e `getWeakPower()`.
   * **Interazione Player (`onUse`)**:
     * Con mano vuota o `Shift + Click`: stampa la diagnosi nell'Action Bar del giocatore.

2. **`SporeDetectorItem.java`** (estende `BlockItem` o `Item` custom compatibile con piazzamento):
   * Gestisce il posizionamento con click destro normale su blocchi.
   * Gestisce l'uso in aria con `Shift + Tasto Destro` per scansione attiva.
   * Integrazione client per suoni Geiger periodici quando presente nella mano primaria o secondaria.

3. **`ModItems.java` & `ModBlocks.java`**:
   * Registrazione di `SPORE_DETECTOR` (Item & Block) con identificatore `spores--shadows:spore_detector`.
   * Aggiunta al gruppo creativo `ItemGroups.TOOLS` e `ItemGroups.REDSTONE`.

---

## 🛠️ 4. Data Generation & Ricette

### A. Ricetta di Crafting (`ModRecipeProvider.java`)
* **Struttura (Banco da lavoro)**:
  * Alto: Lingotto o Pepita di Rame.
  * Centro: Bottiglia di Vetro / Pannello di Vetro + Polvere di Pietrarossa.
  * Basso: Lingotto di Rame / Lastra di Rame.
* **Sblocco Ricetta**: Ottenere Rame o Pietrarossa.

### B. Loot Tables & Tags (`ModLootTableProvider.java`, `ModBlockTagProvider.java`)
* Drop garantito dell'oggetto integro quando distrutto.
* Tag strumento per rottura rapida: `#minecraft:mineable/pickaxe` o `#minecraft:mineable/axe`.

### C. Localizzazione i18n (`Mod*LanguageProvider.java`)
Supporto multilingua completo in 5 lingue:
* `it_it`: "Rilevatore di Miasma", "Livello di Tossicità: %s", "Ventilazione Stanza: %s", ecc.
* `en_us`: "Spore Detector", "Toxicity Level: %s", "Room Ventilation: %s", ecc.
* `fr_fr`, `de_de`, `es_es`.

---

## 🔌 5. Integrazioni & Compatibilità

1. **Jade Plugin (`SporeDetectorProvider.java`)**:
   * Quando il giocatore punta il mirino sul termometro posizionato, il tooltip mostra:
     * Livello Qualità Aria (Icona + Colore).
     * Densità Spore & Volume Stanza.
     * Segnale Redstone emesso.
2. **Cloth Config / ModMenu**:
   * Opzione nel menu di configurazione per attivare/disattivare il ticchettio audio Geiger e regolarne il volume.

---

## 🚀 Fasi di Sviluppo (Roadmap)

- [x] **Fase 1: Creazione Texture & Modelli Grafici** (File `.png` e modelli `.json` per item e blocco).
- [x] **Fase 2: Implementazione Backend Java** (`SporeDetectorBlock`, `SporeDetectorItem`, registrazioni e logica Redstone).
- [x] **Fase 3: Data Generation** (Ricette, Loot Tables, Tags, Lingue).
- [x] **Fase 4: Integrazione Client & Audio** (Property override modello, suoni Geiger, feedback Action Bar).
- [x] **Fase 5: Integrazione Jade & Test di Gioco** (Verifica posizionamento a parete, emissione Redstone e lettura del miasma).
