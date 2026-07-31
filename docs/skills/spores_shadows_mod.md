---
name: spores-and-shadows-fabric-mod
description: Istruzioni architetturali e linee guida per lo sviluppo della mod Minecraft "Spores & Shadows" per Fabric 1.21.1.
---
#  Spores & Shadows  Fabric Mod Development Skill
Questa skill definisce le linee guida architetturali, la struttura del codice e le regole di sviluppo per la mod Fabric di Minecraft **"Spores & Shadows"** (ID: moldmod).
---
##  Tech Stack & Requisiti
* **Target:** Minecraft 1.21.1 (Java Edition)
* **Mod Loader:** Fabric Loader
* **Java Version:** Java 21 (JDK 21)
* **Mappings:** Fabric Yarn Mappings
* **Progetto:** Split Source Sets (main / client)
* **Data Generation:** Abilitato (Fabric DataGen API)
---
##  Regole Architetturali Tassative
1. **Separazione Strict Client / Server:**
* La logica di gioco, la registrazione di blocchi/item, la gestione del mondo e dei tick **MUST** risiedere in main (net.dev.moldmod).
* Grafica, modelli visuali, particelle, schermate (GUI) e rendering delle fasi **MUST** risiedere in client (net.dev.moldmod.client).
* **MAI** importare classi del client (net.minecraft.client.*) nelle classi comuni/server per evitare crash su server dedicati.
2. **Registry Pattern:**
* Ogni nuovo blocco, oggetto o evento deve essere registrato tramite i Registri di Fabric nell'inizializzatore MoldMod.onInitialize().
3. **Data Generation:**
* Utilizzare la Fabric DataGen API per generare automaticamente i file JSON di models, blockstates, loot_tables e traduzioni lang.
