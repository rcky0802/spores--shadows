# Piano di Implementazione: Regole di Fabbricazione e Recupero Assi (Planks Crafting & Recovery)

Il presente documento definisce la correzione del modello di fabbricazione e recupero delle assi nel mod **Spores & Shadows**, applicando rigorosamente il principio di gameplay:
> **Solo le assi sane (vanilla e waxed vanilla) possono essere impiegate per fabbricare manufatti complessi.**  
> Le assi e i tronchi infetti (*Tainted*, *Moldy*, *Rotten*) possono essere utilizzati unicamente per il **recupero di assi sane**, secondo la proporzione matematica di dimezzamento continuo per ogni stadio di infezione: **/ 2 arrotondato per difetto**.

---

## 1. Modello Matematico dei Rendimenti (/ 2 per difetto)

### 1.1 Resa Tronchi d'Albero & Steli del Nether (Base Vanilla: 4 Assi)
| Tipologia | Stadio | Resa Assi Sane | Note |
| :--- | :--- | :--- | :--- |
| **Vanilla Log / Stem** | Sano (0) | **4** | Ricetta vanilla standard |
| **Waxed Vanilla Log / Stem** | Waxed (0) | **4** | Ricetta mod: Tronco cerato sano -> 4 assi vanilla |
| **Tainted Log / Stem** | Tainted (1) | **2** | `4 / 2 = 2` assi sane |
| **Waxed Tainted Log / Stem**| Tainted (1) | **2** | Tronco cerato a stadio 1 |
| **Moldy Log / Stem** | Moldy (2) | **1** | `2 / 2 = 1` asse sana |
| **Waxed Moldy Log / Stem** | Moldy (2) | **1** | Tronco cerato a stadio 2 |
| **Rotten Log / Stem** | Rotten (3) | **0** | `1 / 2 = 0` (Nessuna resa di assi, materiale marcio) |

*Nota Fusti di Legno (Wood/Hyphae a 6 facce, crafting 2x2 = 4 tronchi):*
- Waxed Vanilla: 4 tronchi -> 3 wood
- Tainted: 4 tronchi -> `3 / 2 = 1` wood
- Moldy / Rotten: `0`

---

### 1.2 Resa Fusti di Bambù (*Bamboo Block*, Base Vanilla: 2 Assi)
| Tipologia | Stadio | Resa Assi Sane | Note |
| :--- | :--- | :--- | :--- |
| **Vanilla Bamboo Block** | Sano (0) | **2** | Ricetta vanilla standard |
| **Waxed Bamboo Block** | Waxed (0) | **2** | Blocco di bambù cerato sano -> 2 bamboo planks |
| **Tainted Bamboo Block** | Tainted (1) | **1** | `2 / 2 = 1` bamboo plank |
| **Waxed Tainted Bamboo Block** | Tainted (1) | **1** | Blocco cerato a stadio 1 |
| **Moldy Bamboo Block** | Moldy (2) | **0** | `1 / 2 = 0` (Nessuna resa di assi) |
| **Rotten Bamboo Block** | Rotten (3) | **0** | `0 / 2 = 0` (Nessuna resa di assi) |

---

### 1.3 Recupero di Assi Sane da Assi Infette (*Planks Recovery*)
I giocatori che abbattono strutture degradate o raccolgono assi infette possono "ripulire" e recuperare il legno ancora sano:
- **Tainted Planks (Stadio 1):** Essendo dimezzata la resa (`1/2`), servono 2 assi infette per ricavare 1 asse sana:
  - `2 Tainted Planks (shuffled/waxed)` $\rightarrow$ **1 Vanilla Plank sana**
- **Moldy Planks (Stadio 2):** Essendo la resa pari a `1/4`, servono 4 assi ammuffite:
  - `4 Moldy Planks (2x2 nel crafting grid)` $\rightarrow$ **1 Vanilla Plank sana**
- **Rotten Planks (Stadio 3):** Resa pari a `0`:
  - Le assi marce sono strutturalmente compromise e irrecuperabili. Possono essere usate unicamente nel composter o bruciate come combustibile d'emergenza.

---

### 1.4 Esclusione Assoluta delle Assi Infette dai Manufatti Complessi
Vengono rimosse tutte le ricette che consentono di craftare oggetti finiti a partire da assi infette:
- ❌ **Scale (*Stairs*):** Solo assi vanilla / waxed vanilla.
- ❌ **Lastre (*Slabs*):** Solo assi vanilla / waxed vanilla.
- ❌ **Porte (*Doors*):** Solo assi vanilla / waxed vanilla.
- ❌ **Botole (*Trapdoors*):** Solo assi vanilla / waxed vanilla.
- ❌ **Staccionate (*Fences*):** Solo assi vanilla / waxed vanilla.
- ❌ **Cancelli (*Fence Gates*):** Solo assi vanilla / waxed vanilla.
- ❌ **Bottoni (*Buttons*) & Pedane (*Pressure Plates*):** Solo assi vanilla / waxed vanilla.
- ❌ **Cartelli (*Signs*):** Solo assi vanilla / waxed vanilla.
- ❌ **Barche & Zattere (*Boats & Rafts*):** Solo assi vanilla / waxed vanilla.
- ❌ **Bastoncini (*Sticks*):** Solo assi vanilla / waxed vanilla.

---

### 1.5 Intercambiabilità nello Stesso Stadio & Output Sempre Non Cerato

1. **Intercambiabilità Cerato / Non Cerato dello Stesso Stadio:**
   - In tutti i crafting, le varianti cerate e non cerate dello **stesso identico stadio** sono interamente intercambiabili e possono essere combinate:
     - **Stadio 0 (Sano):** `Vanilla Planks` e `Waxed Vanilla Planks` sono equivalenti (`Ingredient.ofItems(vanillaPlanks, waxedPlanks)`).
     - **Stadio 1 (Tainted):** `Tainted Planks` e `Waxed Tainted Planks` sono equivalenti per il recupero (`Ingredient.ofItems(taintedPlanks, waxedTaintedPlanks)`).
     - **Stadio 2 (Moldy):** `Moldy Planks` e `Waxed Moldy Planks` sono equivalenti per il recupero (`Ingredient.ofItems(moldyPlanks, waxedMoldyPlanks)`).
     - **Tronchi / Fusti:** `Vanilla Log` e `Waxed Vanilla Log` producono entrambi 4 assi sane; `Tainted Log` e `Waxed Tainted Log` producono entrambi 2 assi sane; `Moldy Log` e `Waxed Moldy Log` producono entrambi 1 asse sana.

2. **Tutti i Crafting Producono la Versione NON CERATA:**
   - Qualsiasi ricetta di fabbricazione (tronco $\rightarrow$ assi, assi sane $\rightarrow$ manufatto complesso, assi infette $\rightarrow$ recupero assi sane) **produce sempre e soltanto l'oggetto standard NON CERATO**.
   - Per ottenere una variante cerata, il giocatore deve applicare manualmente la cera (*honeycomb*) nel mondo (tasto destro furtivo) o tramite apposita interazione. Nessun crafting restituisce manufatti pre-cerati.

---

### 1.6 Correzione e Allineamento dell'Integrazione JEI (*Just Enough Items*)

Per offrire un'esperienza utente chiara e priva di ambiguità all'interno dell'interfaccia JEI:
1. **Schede Informative (*Ingredient Info*) per Assi Infette:**
   - JEI possiede già una scheda informativa per il legno marcio (`jei.spores--shadows.info.rotten_wood`).
   - È necessario introdurre schede informative esplicative anche per:
     - **Tainted Planks & Waxed Tainted Planks (`jei.spores--shadows.info.tainted_planks`):** Spiega che non possono essere usate per fabbricare scale, lastre, porte o altri manufatti complessi, ma possono essere ripulite nel banco da lavoro (rapporto 2:1 per ottenere 1 asse sana) oppure piazzate e raschiate con ascia.
     - **Moldy Planks & Waxed Moldy Planks (`jei.spores--shadows.info.moldy_planks`):** Spiega il rapporto di recupero 4:1 per ottenere 1 asse sana e l'impossibilità di fabbricare manufatti complessi.
2. **Supporto Intercambiabilità Cerato/Non Cerato in JEI:**
   - Assicurarsi che le ricette di recupero registrate nel `RecipeManager` espongano chiaramente a JEI gli ingredienti con tag/alternative multiple (variante non cerata e variante cerata intercambiabili).
3. **Traduzioni Complete i18n per JEI:**
   - Aggiunta delle stringhe descrittive in tutte le 5 lingue supportate (`it_it`, `en_us`, `es_es`, `fr_fr`, `de_de`).
4. **GameTests JEI Dedicati:**
   - Estendere la suite GameTest (`JEIIngredientInfoGameTests.java`) per verificare la copertura e la presenza delle descrizioni informative per tutte le assi infette.

---

## 2. Dettaglio Fasi Operative con Checkbox

- [x] **Fase 1: Aggiornamento Datagen Ricette (`ModRecipeProvider.java`):**
  - [x] **1.1 Regola Resa Tronchi e Fusti (`generatePlanksRecipe`):**
    - Alberi e Nether: Waxed Vanilla = 4, Tainted = 2, Moldy = 1, Rotten = 0 (nessuna ricetta per Rotten).
    - Bambù: Waxed Vanilla = 2, Tainted = 1, Moldy = 0 (nessuna ricetta per Moldy), Rotten = 0.
  - [x] **1.2 Ricette di Recupero da Assi Infette (`generatePlanksRecoveryRecipes`):**
    - Implementare shapeless `2 Tainted Planks` $\rightarrow$ `1 Vanilla Plank` per tutti gli 11 legni.
    - Implementare shaped/shapeless `4 Moldy Planks` $\rightarrow$ `1 Vanilla Plank` per tutti gli 11 legni.
  - [x] **1.3 Pulizia Manufatti Complessi (`generateProcessedRecipes`):**
    - Rimuovere tutti i blocchi di ricette `*_from_tainted` e `*_from_moldy` per scale, lastre, porte, botole, staccionate, bastoncini.
    - Mantenere la fabbricazione solo tramite `mixedPlanks` (Vanilla Planks + Waxed Vanilla Planks).

- [x] **Fase 2: Rigenerazione Datagen & Allineamento File:**
  - [x] **2.1 Esecuzione `runDatagen`:** Rigenerazione automatica di tutti i file JSON in `src/main/generated/data/spores--shadows/recipe/`.
  - [x] **2.2 Pulizia File Obsoleti:** Eliminazione automatica e manuale dei 202 file JSON generati in precedenza per le ricette rimosse.

- [x] **Fase 3: Correzione & Potenziamento Implementazione JEI (`SporesShadowsJEIPlugin.java`):**
  - [x] **3.1 Registrazione Info Assi Infette in JEI:**
    - Raccogliere tutte le varianti di assi `tainted` e `waxed_tainted` di tutti gli 11 legni e registrare `addIngredientInfo` con chiave `jei.spores--shadows.info.tainted_planks`.
    - Raccogliere tutte le varianti di assi `moldy` e `waxed_moldy` di tutti gli 11 legni e registrare `addIngredientInfo` con chiave `jei.spores--shadows.info.moldy_planks`.
    - Verificare che le assi `rotten` e `waxed_rotten` siano già incluse nella lista `rottenStacks`.
  - [x] **3.2 Localizzazioni Linguistiche JEI (5 Lingue):**
    - Inserire le traduzioni per `info.tainted_planks` e `info.moldy_planks` nei provider datagen (`ModEnglishLanguageProvider`, `ModItalianLanguageProvider`, `ModSpanishLanguageProvider`, `ModFrenchLanguageProvider`, `ModGermanLanguageProvider`).
    - Rigenerare i file di lingua con `runDatagen`.
  - [x] **3.3 Estensione GameTest JEI (`JEIIngredientInfoGameTests.java`):**
    - Aggiungere il test `testInfectedPlanksInfoCoverage` per validare che tutte le assi infette abbiano la scheda informativa attiva e non vuota in JEI.

- [x] **Fase 4: Implementazione Test & Collaudo Globale:**
  - [x] **4.1 Gametest Rese e Recupero (`MoldyCraftingYieldsTests.java`):**
    - Verificare che i tronchi producano esattamente 4/2/1/0 assi sane.
    - Verificare che il bambù produca esattamente 2/1/0/0 assi sane.
    - Verificare che 2 tainted planks diano 1 asse sana.
    - Verificare che 4 moldy planks diano 1 asse sana.
    - Verificare che le planks rotten non abbiano ricette di assi sane.
  - [x] **4.2 Asserzioni Negative Manufatti Complessi:**
    - Verificare che nessuna combinazione di tainted, moldy o rotten planks possa craftare scale, lastre, porte, botole, staccionate, cancelli, bottoni, pedane o bastoncini.
  - [x] **4.3 Superamento Suite Globale:** Esecuzione di `./gradlew test` e `./gradlew runGametest` con esito 100% positivo dopo l'integrazione JEI.
