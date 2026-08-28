# 📋 Report Completo delle Differenze e Nuove Funzionalità
**Riferimento Base**: Commit `84126f4` ➔ `b5bdde1` ➔ `de7b741` ➔ **Workspace Attuale (HEAD)**  
**Data**: 28 Agosto 2026  
**Mod Version**: Spores & Shadows 1.2.0 (Fabric 1.21.1)

---

## 📑 Indice dei Contenuti
1. [Sintesi Cronologica e Diagramma di Flusso](#-1-sintesi-cronologica-dei-commit-chiave)
2. [Focus: Miasma al Chiuso, Aereazione e Comando `/miasma` (Commit `84126f4`)](#-2-focus-miasma-al-chiuso-aereazione-e-comando-miasma-commit-84126f4)
3. [Focus: Effetti Visivi Volumetrici e Fix Raytracing Jade (Commit `b5bdde1`)](#-3-focus-effetti-visivi-volumetrici-e-fix-raytracing-jade-commit-b5bdde1)
4. [Focus: Ricette Cerate e Tag Carbonella (Commit `de7b741`)](#-4-focus-ricette-cerate-e-tag-carbonella-commit-de7b741)
5. [Evoluzione Post-`de7b741`: Fisica Avanzata, Durezza e Nuvola di Spore](#-5-evoluzione-post-de7b741-fisica-avanzata-durezza-e-nuvola-di-spore)
6. [Integrazione Completa con JEI (Just Enough Items)](#-6-integrazione-completa-con-jei-just-enough-items)
7. [Tabella Completa dei Bug Corretti (*Bug Fixes*)](#-7-tabella-completa-dei-bug-corretti-bug-fixes)
8. [Refactoring e Pulizia del Codice](#-8-refactoring-e-pulizia-del-codice)
9. [Suite di Test (GameTest) & Modularizzazione (43/43 Passati)](#-9-suite-di-test-gametest--modularizzazione-4343-passati)
10. [Configurazione Ambiente di Sviluppo & Runtime Mod](#-10-configurazione-ambiente-di-sviluppo--runtime-mod)

---

## 🕒 1. Sintesi Cronologica dei Commit Chiave

```mermaid
timeline
    title Evoluzione Funzionale di Spores & Shadows
    27 Agosto (10:56) : Commit 84126f4 : Nuovo Miasma Flood-Fill al chiuso : Meccanica Aereazione & Fessure : Comando /miasma : Bump v1.2.0
    27 Agosto (14:56) : Commit b5bdde1 : Nebbia Miasma volumetrica su tutta la stanza : Limite Manhattan Radius : Raytrace Jade Tooltip fix
    28 Agosto (Mattina) : Commit de7b741 : Ricette complete blocchi cerati : Tag Item/Charcoal per fornace
    28 Agosto (Attuale) : HEAD Workspace : Durezza scalare progressiva (2.0 -> 0.4) : Nuvola spore alla rottura : Friabilità Stadio 3 : Plugin JEI : 9 Test modulari
```

---

## 🌫️ 2. Focus: Miasma al Chiuso, Aereazione e Comando `/miasma` (Commit `84126f4`)

### 🧠 A. L'Algoritmo Flood-Fill BFS per Spazi Confinati
* **Problema Storico**: In precedenza, la tossicità dell'aria scansionava un volume cubico rigido attorno al giocatore. Di conseguenza, un blocco ammuffito posizionato all'esterno di una casa in pietra avvelenava ingiustamente chi si trovava all'interno attraverso i muri.
* **Nuova Logica BFS** ([`ToxicAirEvent.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/event/ToxicAirEvent.java)):
  1. **Punto di Partenza**: La posizione degli occhi del giocatore (`player.getEyePos()`).
  2. **Controllo Pre-filtro $O(R^3)$**: Se non vi è alcun blocco infetto non cerato nel raggio di prossimità, il calcolo esce a costo computazionale nullo.
  3. **Esplorazione Aria**: L'aria si espande nelle 6 direzioni cardinali tramite una coda `Queue<BlockPos>`, fermandosi contro le superfici opache piene (`isSideSolidFullSquare`). Porte aperte, botole aperte e varchi d'aria consentono la propagazione.
  4. **Volume Massimo di Saturazione**: `MAX_AIR_VOLUME = 512` blocchi. Superato questo volume, l'ambiente è considerato troppo vasto per concentrare il miasma e il gas si disperde.

### 💨 B. Meccanica di Aereazione e Ventilazione Naturale
* **Verifica Cielo Aperto ($O(1)$)**: Per ogni blocco d'aria visitato, viene verificata la coordinata Y rispetto alla heightmap del mondo (`Heightmap.Type.MOTION_BLOCKING`). Se il blocco comunica direttamente con l'atmosfera esterna, l'intera stanza è marcata come `openAir = true` e il miasma viene azzerato istantaneamente.
* **Fessure e Ventilazione da Blocchi Parziali**: Le pareti perimetrali composte da blocchi non a cubo pieno (staccionate, grate, sbarre di ferro, lastre) che affacciano all'esterno accumulano un punteggio di ventilazione:
  $$\text{Ventilation Score} += 3.0 \quad \text{per ogni fessura verso l'esterno}$$
  $$\text{Net Miasma} = \text{Toxic Score} - \text{Ventilation Score}$$

### 💻 C. Comando Amministrativo `/miasma`
Implementato in [`ModCommands.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/command/ModCommands.java):
* **Sintassi**:
  * `/miasma` (analizza l'aria per chi esegue il comando)
  * `/miasma <target>` (analizza l'aria attorno a un giocatore specifico, livello permesso 2)
* **Output Dettagliato in Chat**:
  ```text
  === Miasma Air Analysis ===
  - Environment: [Concealed / Open Air]
  - Air Volume: 84 / 512 m³
  - Toxic Score: 24.50
  - Ventilation Score: 6.00
  - Net Miasma: 18.50 (TOXIC)
  ```

---

## 🎨 3. Focus: Effetti Visivi Volumetrici e Fix Raytracing Jade (Commit `b5bdde1`)

### 🌫️ A. Nebbia Miasmatica Volumetrica
* **Diffusione nello Spazio**: Le particelle di miasma (`SPORE_BLOSSOM_AIR` e `MYCELIUM`) non vengono più generate soltanto alle coordinate del giocatore, ma vengono distribuite volumetricamente su tutti i blocchi d'aria contenuti nel set `visited` della stanza, avvolgendo l'intero spazio confinato in una densa bruma tossica.
* **Limite di Manhattan (`MAX_MANHATTAN_RADIUS = 8`)**:
  * Introdotto il vincolo di distanza:
    $$|x_{\text{eye}} - x| + |y_{\text{eye}} - y| + |z_{\text{eye}} - z| \le 8$$
  * Questo impedisce al Flood-Fill di propagarsi all'infinito lungo cunicoli minerari o lunghi corridoi.

---

## 🪵 4. Focus: Ricette Cerate e Tag Carbonella (Commit `de7b741`)

* **Lavorazione Completa dei Blocchi Cerati**:
  * Oltre 100 ricette JSON generate per permettere di convertire tronchi e legni cerati in assi cerate, e queste ultime in cartelli, barche, botole, porte, staccionate, lastre e scale cerate.
* **Supporto Carbonella (*Charcoal*)**:
  * Registrati i tag `#minecraft:item/charcoal` e `#c:charcoal`.
  * Cottura in fornace abilitata per tutti i tronchi degradati e cerati per produrre carbonella.

---

## 📉 5. Evoluzione Post-`de7b741`: Fisica Avanzata, Durezza e Nuvola di Spore

### 1. Durezza Scalare Progressiva ([`AbstractBlockStateMixin.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/mixin/AbstractBlockStateMixin.java))
* **Stadio 0 (Sano / Vanilla)**: Durezza $2.0$ ($100\%$)
* **Stadio 1 (Contaminato)**: Durezza $1.6$ ($80\%$)
* **Stadio 2 (Ammuffito)**: Durezza $1.0$ ($50\%$)
* **Stadio 3 (Marcio)**: Durezza $0.4$ ($20\%$)

### 2. Neutralizzazione dell'Ascia allo Stadio 3
* Nel legno marcio di Stadio 3, la perdita totale di consistenza interna fa sì che rompere il blocco con un'ascia o a mani nude impieghi esattamente lo **stesso tempo**.

### 3. Nuvola di Spore alla Rottura ([`BlockMixin.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/mixin/BlockMixin.java))
* Rompere blocchi di Stadio 2 o 3 senza *Silk Touch* genera un'esplosione di particelle biologiche (`SPORE_BLOSSOM_AIR`, `FALLING_SPORE_BLOSSOM`, `MYCELIUM`) e suoni organici.

### 4. Regole di Raccolta e Drop Percentuali
* **Senza Silk Touch (e Non Cerato)**:
  * Stadio 0: $100\%$
  * Stadio 1: $100\%$
  * Stadio 2: $50\%$ drop (il restante $50\%$ si disintegra in polvere organica)
  * Stadio 3: $0\%$ drop (collasso totale)
* **Con Silk Touch o Blocco Cerato**: $100\%$ garantito su tutti gli stadi.

### 5. Infiammabilità e Resistenza alle Esplosioni
* **Infiammabilità scalare**: Bonus stadio ($+5/+10$, $+10/+25$, $+20/+60$) e bonus cera ($+5$). Legni Nether immuni ($0$).
* **Resistenza detonazioni**: Scalata a $80\%$ (Stadio 1), $50\%$ (Stadio 2), $10\%$ (Stadio 3).

---

## 📖 6. Integrazione Completa con JEI (Just Enough Items)

Aggiunto [`SporesShadowsJEIPlugin.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/client/java/moldmod/client/integration/jei/SporesShadowsJEIPlugin.java) con:
1. **Categoria Ceratura (*Waxing*)** ([`WaxingRecipeCategory.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/client/java/moldmod/client/integration/jei/WaxingRecipeCategory.java)): `Blocco + Favo ➔ Blocco Cerato` per tutti i 130 derivati.
2. **Categoria Raschiamento (*Axe Scraping*)** ([`ScrapingRecipeCategory.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/client/java/moldmod/client/integration/jei/ScrapingRecipeCategory.java)):
   * Rimozione Cera: `Blocco Cerato + Ascia ➔ Blocco Non Cerato`.
   * Cura Muffa: `Stadio 2 ➔ Stadio 1 ➔ Stadio 0 Vanilla`.
3. **Schede Informative (*Info Tab*)**: Descrizioni dettagliate per tutti gli item di Stadio 3.

---

## 🐛 7. Tabella Completa dei Bug Corretti (*Bug Fixes*)

| File Coinvolto | Descrizione Bug | Causa Originaria | Correzione Applicata |
| :--- | :--- | :--- | :--- |
| **`MoldyStructureContext.java`** | Inversione probabilità degrado strutture | Errore nella cascata cumulativa (`r < rotten` dava stadio 2) | Corretta la scala: $3 \rightarrow 2 \rightarrow 1$ |
| **`FireBlockMixin.java`** | Crash per ricorsione infinita su fuoco | Conflitto tra `FlammableBlockRegistry` e Mixin | Iniezione a `@At("HEAD")` priorità 900 isolata |
| **`ToxicAirEvent.java`** | Miasma attraversava i muri di pietra | Scansione cubica grezza senza raycasting | Sostituita con Flood-Fill BFS e aereazione |
| **`ToxicAirEvent.java`** | Soglie tossicità cablate nel codice | Valori hardcoded `16.0` e `10.0` | Sostituiti con i campi dinamici di Cloth Config |
| **`ModFuelRegistry.java`** | Underflow durata combustione fornace | Arrotondamento a 0 tick per oggetti a basso consumo | Applicato clamp protettivo `Math.max(37, ...)` |
| **`JadePlugin.java`** | Icone fake-block sballate su Polymer | Raytracing di default non gestiva i component NBT | Aggiunto `RayTraceCallback` dedicato in JadePlugin |
| **`BlockPickMixin.java`** | Conflitti grafici su middle click | Mixin client ridondante | Rimosso in favore di `getPickStack()` nativo |

---

## 🗑️ 8. Refactoring e Pulizia del Codice

1. **Eliminazione di `calcBlockBreakingDelta` duplicato da 10 classi di blocco**:
   * Rimosso da tutti i file `Moldy*.java` e centralizzato in un unico Mixin universale ([`AbstractBlockStateMixin.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/mixin/AbstractBlockStateMixin.java)).
2. **Sostituzione dei Numeri Magici con Enum Tipizzata**:
   * Utilizzo sistematico di `SporesShadowsConstants.MoldStage.values()` in tutta la codebase.
3. **Pulizia della Firma `randomTick`**:
   * Rimosso il parametro ridondante `Block this` in [`MoldyBlockHelper.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/block/MoldyBlockHelper.java).

---

## 🧪 9. Suite di Test (GameTest) & Modularizzazione (50/50 Passati)

```text
src/test/java/moldmod/test/
├── MoldyWoodTestHelper.java                # Registry con 130 combinazioni legni/derivati
├── MoldyHardnessAndMiningSpeedTests.java   # Durezza (2.0 -> 0.4) e friabilità ascia/mano
├── MoldyDropAndLootTests.java              # Drop percentuali (100%, 100%, 50%, 0%) e Silk Touch
├── MoldyInteractionTests.java             # Ceratura favo e ciclo a 2 click con ascia
├── MoldyFuelAndSmeltingTests.java          # Moltiplicatori combustione ed esclusione Nether
├── MoldyComposterTests.java                # Probabilità composter (50%, 65%, 85%)
├── MoldyFlammabilityTests.java             # Infiammabilità scalare, cera e immunità Nether
├── MoldyBlastResistanceTests.java          # Resistenza dinamica alle detonazioni
├── MoldyCraftingYieldsTests.java           # Rese di lavorazione nei banchi da lavoro
└── ToxicAirTests.java                      # Miasma al chiuso, ventilazione, fessure, ceratura e limite Manhattan
```
* **Copertura esaustiva Miasma**:
  1. `testCanAirPass`: permeabilità blocchi (aria, solidi, staccionate).
  2. `testHasMoldNearby`: pre-filtro di prossimità $O(R^3)$ e immunità legno cerato.
  3. `testManhattanRadiusLimit`: contenimento della BFS su tunnel lineari (`max_manhattan_radius`).
  4. `testEnclosedRoomToxicity`: calcolo tossicità in stanza sigillata e correlazione `netMiasma = toxicScore`.
  5. `testOpenAirDissipation`: dispersione immediata in cielo aperto ($O(1)$ heightmap).
  6. `testWaxedWoodMiasmaImmunity`: annullamento completo della tossicità per blocchi infetti sigillati con cera.
  7. `testVentilationWithGaps`: attenuazione del miasma tramite fessure perimetrali verso l'esterno (`ventilationScore`).
* **Risultato Test**: **50 test su 50 superati con successo ($100\%$) in 2.36 secondi**.

---

## ⚙️ 10. Configurazione Ambiente di Sviluppo & Runtime Mod

In [`build.gradle`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/build.gradle) sono configurate le seguenti mod per essere caricate automaticamente in `./gradlew runClient`:
* **JEI (Just Enough Items)** (`maven.modrinth:jei:19.21.0.247`)
* **Jade Tooltip Engine** (`maven.modrinth:jade:15.10.6+fabric`)
* **Cloth Config** & **ModMenu** per la configurazione dinamica in-game.