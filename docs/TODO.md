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

### 5. 🌀 Macchinari: Ventola di Aspirazione & Sfiatamento (*Ventilation Fan*)
- [ ] **Blocco Interattivo: Ventola di Ventilazione / Estrattore (*Ventilation Fan*)**
  * **Obiettivo**: Un blocco direzionale (orientabile con asse frontale/posteriore) in grado di aspirare attivamente l'aria miasmatica dalla stanza frontale ed espellerla/sfiatarla sul retro.
  * **Integrazione Meccanica**:
    * Riduce attivamente il volume tossico e incrementa il `ventilationScore` dello spazio chiuso.
    * Espelle particelle di fumo e spore sul retro del blocco (verso l'esterno dell'edificio o una canna fumaria).
    * Attivabile tramite segnale di Pietrarossa o funzionamento continuo.
  * **Stato**: ⏳ *Pianificato*