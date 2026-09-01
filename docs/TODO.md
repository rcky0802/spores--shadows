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
  * **Integrazioni**: Compatibile con JEI, Jade, Cloth Config, ModMenu e Polymer.
  * **Stato**: ✅ *Completato*

---

### 3. ✨ Incantesimo per Elmetti: Filtrazione Spore / Respirazione Tossica
- [ ] **Nuovo Incantesimo per Elmi (*Spore Filtration / Toxic Respiration*)**
  * **Obiettivo**: Permettere a qualsiasi elmo convenzionale (cuoio, ferro, diamante, netherite) di essere incantato con un'abilità di purificazione dell'aria.
  * **Compatibilità**: Non in conflitto con *Respiration / Respirazione* o altri incantesimi standard.
  * **Meccanica**: Neutralizza gli effetti del miasma a costo di un consumo continuo di durabilità dell'elmo per ogni tick di esposizione tossica.
  * **Stato**: ⏳ *Pianificato*

---

### 4. 🧭 Strumentazione: Rilevatore di Miasma (*Miasma Detector / Spore Analyzer*)
- [ ] **Item / Gadget: Rilevatore di Miasma e Qualità dell'Aria**
  * **Obiettivo**: Uno strumento portatile (o posizionabile) in grado di misurare la qualità dell'aria circostante, il volume della stanza, la densità di spore e il punteggio di ventilazione.
  * **Feedback Giocatore**: Fornisce un display HUD o un segnale acustico/visivo (es. led a colori o lancetta) che avvisa quando ci si avvicina a soglie pericolose di saturazione del gas prima di subire danni.
  * **Stato**: ⏳ *Pianificato*

---

### 5. 🌀 Macchinari: Ventola di Aspirazione & Sfiatamento (*Ventilation Fan*)
- [ ] **Blocco Interattivo: Ventola di Ventilazione / Estrattore (*Ventilation Fan*)**
  * **Obiettivo**: Un blocco direzionale (orientabile con asse frontale/posteriore) in grado di aspirare attivamente l'aria miasmatica dalla stanza frontale ed espellerla/sfiatarla sul retro.
  * **Integrazione Meccanica**:
    * Riduce attivamente il volume tossico e incrementa il `ventilationScore` dello spazio chiuso.
    * Espelle particelle di fumo e spore sul retro del blocco (verso l'esterno dell'edificio o una canna fumaria).
    * Attivabile tramite segnale di Pietrarossa o funzionamento continuo.
  * **Stato**: ⏳ *Pianificato*