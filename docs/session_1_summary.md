# Spores & Shadows - Dev Log: Session 1

## Obiettivi Raggiunti
In questa sessione iniziale di pair programming abbiamo gettato le fondamenta della mod *Spores & Shadows* implementando la prima meccanica core: **L'ammuffimento dinamico dei tronchi**.

### 1. Sistema di Texturing Procedurale (Python)
Invece di disegnare a mano pixel per pixel i diversi stadi della muffa, abbiamo creato uno script Python (`generate_textures.py`) che genera proceduralmente i file in formato SVG e li converte automaticamente in PNG a 16x16.
- **Base Fedele**: Prende in input la texture del tronco Vanilla originale.
- **Layer Muffa & Marciume**: Applica algoritmi di decolorazione del legno e sparge macchie di muffa bianca/grigia in 3 densità diverse (Stage 1, 2, e 3).
- **Automazione**: Produce sia il lato (`placed_oak_log`) che la cima (`placed_oak_log_top`) in pochi secondi.

### 2. Architettura DataGen (JSON Auto-Generati)
Abbiamo seguito alla lettera la Skill Architetturale impostando Fabric DataGen per non scrivere mai file JSON a mano.
- Creati `ModModelProvider`, `ModLootTableProvider`, `ModBlockTagProvider` e `ModEnglishLanguageProvider`.
- Tutti i modelli 3D (Blockstates), Loot Tables (Drops) e Tag (`mineable/axe`, `logs_that_burn`) sono auto-generati dal comando `./gradlew runDatagen`.

### 3. Logica del Blocco Dinamico (Java & Mixin)
Per far sì che l'ammuffimento si applichi **solo ai tronchi piazzati dai giocatori** (e non a quelli generati naturalmente nelle foreste) abbiamo adottato un design pattern invisibile e pulito:
- **BlockItemMixin**: Intercetta il click destro del giocatore. Se il giocatore piazza un `oak_log` vanilla, la mod lo sostituisce istantaneamente col nostro blocco custom `placed_oak_log`.
- **PlacedOakLogBlock**: Un blocco che appare e suona identico a un tronco normale, ma utilizza il `randomTick` per valutare l'ambiente circostante. In base all'umidità (acqua vicina), al buio (sottoterra) e alla temperatura del bioma, ha una chance dinamica di iniziare ad ammuffire (passando allo Stage 1).
- **MoldyOakLogBlock**: Gestisce il decadimento progressivo (da Stage 1 fino allo Stage 3).

## Prossimi Passi
- Espandere la logica di ammuffimento ad altri tipi di legno.
- Introdurre effetti particellari o meccaniche di "infezione" a catena sui blocchi adiacenti.
