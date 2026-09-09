# 📋 Spores & Shadows - Task & TODO List

Documento di tracciamento per le funzionalità pianificate, miglioramenti architetturali e future evoluzioni del gameplay di **Spores & Shadows**.

---

## 📌 Funzionalità Pianificate (In Coda)

### 1. 💨 Aerazione nella Formula di Crescita Muffa ($R$)
- [x] **Integrare l'aerazione/ventilazione nella formula di calcolo del rischio muffa ($R$)**
  * **Obiettivo**: Estendere il concetto di aerazione e ventilazione naturale (attualmente utilizzato nel sistema di diffusione del Miasma in [`ToxicAirEvent.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/event/ToxicAirEvent.java)) direttamente all'interno della formula di proliferazione e infezione del legno in [`MoldyBlockHelper.java`](file:///C:/Users/r.pirosu/Desktop/spores--shadows-template-1.21.1/src/main/java/moldmod/block/MoldyBlockHelper.java).
  * **Concetto di Gameplay**: Gli edifici e le stanze ben ventilate (provvisti di varchi d'aria, staccionate, sbarre di ferro, finestre aperte o vicinanza a correnti d'aria esterna) mitigano l'umidità effettiva (`Heff`) e applicano un fattore di dispersione delle spore, riducendo significativamente il rischio di infezione $R$ e preservando le strutture in legno.
  * **Stato**: ✅ *Completato*

---

### 2. 😷 Equipaggiamento Protettivo: Maschera Antigas / Respiratore
- [x] **Nuovo Elmo: Maschera Antispore / Respiratore (*Spore Mask / Respirator*)**
  * **Obiettivo**: Introdurre un copricapo speciale equipaggiabile nello slot elmo che protegge completamente chi lo indossa dagli effetti nocivi del Miasma (Fame, Nausea, Veleno).
  * **Incantabilità**: Compatibile con i principali incantesimi da elmo/armatura (*Unbreaking / Indistruttibilità*, *Mending / Ripristino*, *Protezione*, ecc.).
  * **Bilanciamento & Durabilità**: Consuma durabilità progressiva nel tempo mentre filtra attivamente il miasma presente nell'ambiente (165 usi base). Riparabile all'incudine sostituendo il filtro con **Lana (`#minecraft:wool`)**.
  * **Rendering 3D Realistico**: Modello volumetrico personalizzato con becco frontale sporgente di $4.5$ voxel (bocchettone filtro stile gas mask cinematografica), bombolette cilindriche laterali e visore sagomato con Fabric `ArmorRenderer` e compatibilità Polymer.
  * **Integrazioni**: Compatibile con JEI, Jade, Cloth Config, ModMenu e Polymer.
  * **Stato**: ✅ *Completato*

---

### 3. ✨ Incantesimo per Elmetti: Filtrazione Spore / Respirazione Tossica
- [x] **Nuovo Incantesimo per Elmi (*Spore Filtration / Toxic Respiration*)**
  * **Obiettivo**: Permettere a qualsiasi elmo convenzionale (cuoio, ferro, diamante, netherite, oro, maglia, tartaruga) di essere incantato con un'abilità di purificazione dell'aria.
  * **Compatibilità & Regole**: Non in conflitto con *Respiration / Respirazione* o altri incantesimi standard. La Maschera Antispore ha `enchantability = 0` ed è limitata all'incudine a *Unbreaking*, *Mending* e *Curse of Vanishing*.
  * **Meccanica**: Neutralizza al 100% gli effetti del miasma (Fame, Nausea, Veleno) con consumo continuo scalare di durabilità (Livello I: 2 pt, Livello II: 1 pt, Livello III: 50% risparmio).
  * **Stato**: ✅ *Completato*

---

### 4. 🧭 Strumentazione: Rilevatore di Miasma (*Miasma Detector / Spore Analyzer*)
- [x] **Item / Blocco: Rilevatore di Miasma & Termometro a Spore (*Spore Detector / Miasmometer*)**
  * **Obiettivo**: Uno strumento portatile e posizionabile in stile termometro/igrometro vintage in rame e vetro in grado di misurare la qualità dell'aria circostante, il volume della stanza, la densità di spore e il punteggio di ventilazione.
  * **Modalità d'Uso**:
    * **In Mano (Passivo)**: Texture dinamica con colonnina graduata che sale in base al miasma (Verde, Giallo, Arancione, Rosso) e ticchettio audio stile contatore Geiger.
    * **Shift + Tasto Destro**: Scansione diagnostica istantanea con report chiaro e dettagliato nell'Action Bar.
    * **Tasto Destro su Blocco**: Posizionabile a parete o a pavimento (stile termometro a muro / piedistallo).
    * **Emissione Redstone**: Emette un segnale analogico (0–15) proporzionale alla concentrazione di spore per pilotare circuiti e ventole di aspirazione.
  * **Integrazioni**: Compatibilità completa con JEI, Jade, Polymer (client vanilla), Datagen in 5 lingue e inserito nei tab creativi (Strumenti, Pietrarossa, Blocchi Funzionali).
  * **Stato**: ✅ *Completato*

---

### 5. 💧 Ristrutturazione Umidità Volumetrica Dinamica di Stanza (*Room Humidity Refactor*)
- [x] **Ristrutturazione Umidità di Stanza & Rischio Muffa ($R$)**
  * **Obiettivo**: Rimpiazzare il vecchio algoritmo cubico 3x3x3 di scansione acqua con un sistema volumetrico discreto identico al Miasma, guidato da espansione BFS nello spazio aereo continuo.
  * **Meccaniche**:
    * Calcolo sorgenti d'acqua affacciate sul volume d'aria della stanza (+0.15 cad., tetto max 0.60).
    * Inerzia temporale dinamica asintotica via `RoomSaturationManager` ($\alpha_{\text{sat}} = 0.05$, $\alpha_{\text{diss}} = 0.08$).
    * Condizioni al contorno realistiche: mediazione facce aeree per blocchi esposti a più stanze, fallback geologico per blocchi interrati, 100% umidità per blocchi waterlogged.
    * Comando `/moldrisk` allineato con diagnostica completa di stanza, trend di saturazione/svuotamento e $\alpha$ attiva.
    * 7 GameTest dedicati (`RoomHumidityGameTests`) e 100% test passanti.
  * **Stato**: ✅ *Completato*

---

### 6. 💨 Macchinari: Deumidificatore (*Dehumidifier*)
- [ ] **Blocco Interattivo: Deumidificatore (*Dehumidifier*)**
  * **Obiettivo**: Un macchinario posizionabile all'interno di ambienti chiusi o sotterranei per rimuovere l'umidità ambientale, bloccando attivamente la proliferazione della muffa.
  * **Integrazione Meccanica**:
    * Agisce direttamente sul calcolatore di rischio (`MoldRiskCalculator`), abbattendo brutalmente l'umidità locale (`localHumidityBonus` o `Hraw`) nel suo raggio d'azione.
    * Pensato appositamente per bunker, cantine o miniere dove non è possibile creare prese d'aria verso la superficie.
    * Attivabile tramite Pietrarossa. Potrebbe richiedere di essere svuotato periodicamente dall'acqua accumulata (es. secchi) o consumare carburante.
  * **Stato**: ⏳ *Pianificato*

### 7. 🌬️ Macchinari: Depuratore d'Aria / Filtro HEPA (*Air Purifier*)
- [ ] **Blocco Interattivo: Depuratore d'Aria a Spore (*Spore Purifier / HEPA Filter*)**
  * **Obiettivo**: Un sistema di filtrazione avanzato capace di distruggere attivamente il miasma accumulato in una stanza sigillata, depurando l'aria senza necessità di condotti verso l'esterno.
  * **Integrazione Meccanica**:
    * Intercetta il `MiasmaCalculator` riducendo matematicamente la tossicità totale della stanza e sopprimendo la pressione delle spore nell'aria.
    * Crea la perfetta "Safe Room" sotterranea in assenza di ventilazione naturale.
    * Consuma filtri sacrificabili (es. Lana, Carta o un nuovo item dedicato) o durabilità nel tempo, garantendo un loop di manutenzione bilanciato.
  * **Stato**: ⏳ *Pianificato*