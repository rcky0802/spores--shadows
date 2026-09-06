# Logica di Implementazione dell'Algoritmo di Aerazione (3D Max-Flow Sky Absorption Model)

Questo documento descrive dettagliatamente l'architettura, i principi matematico-fisici e i passaggi implementativi del nuovo algoritmo di **Flusso Massimo 3D su Spazio Voxel (Sky Absorption Model)** e ventilazione convettiva utilizzato in *Spores & Shadows*.

I file sorgente di riferimento principali sono:
- [VoxelMaxFlowEngine.java](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/event/VoxelMaxFlowEngine.java)
- [ToxicAirEvent.java](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/event/ToxicAirEvent.java)
- [MoldyBlockHelper.java](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/block/MoldyBlockHelper.java)
- [ModConfig.java](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/config/ModConfig.java)

---

## 1. Obiettivo del Sistema e Modello Fisico

L'aerazione ($A \in [0.0, 1.0]$) quantifica il livello di ricircolo e portata d'aria convettiva ($F_{\text{max}}$) a cui è esposta una superficie solida o un blocco.

Nel nuovo modello **3D Max-Flow Sky Absorption**:
1. **Emettitori / Starts (Sorgente)**: Le facce d'aria a contatto con il blocco sotto esame richiedono e generano flusso d'aria (ciascuna connessa alla super-sorgente `SOURCE` con capacità $25.0$).
2. **Assorbitori del Cielo (Sky Goals / Sinks)**: I punti a cielo aperto (`!isCoveredByCeiling`) o i varchi verso l'esterno (`isVentilatedToOutside`) fungono da pozzi di assorbimento collegati al super-pozzo `SINK` con capacità $25.0$.
3. **Rete di Conduzione Idraulica/Convettiva (Node-Splitting)**: Ogni voxel d'aria e passaggio intermedio ha una banda passante finita e archi di adiacenza 3D pesati geometricamente sui 4 quadranti condivisi.
4. **Algoritmo di Dinic**: Risolve in sub-millisecondo il flusso massimo effettivo $F_{\text{max}}$ che riesce a transitare dal blocco fino al cielo aperto, rispettando tutti i colli di bottiglia (strozzature, finestre, porte, grate, buchi).

---

## 2. Architettura a Due Fasi

```
                       [ SUPER-SOURCE ]
                              │ (Cap = ∞)
                              ▼
                [ Emettitore: Blocco Target ]
                              │ (Cap = 25.0 per faccia libera)
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ FASE 1: Censimento & Scoperta Ambientale (BFS Delimitata)   │
│    - Scoperta rapida dei voxel d'aria connessi fino a       │
│      max_air_volume (512) e raggio max_euclidean_radius     │
│    - Assegnazione ID interi compatti (0 .. N-1)             │
│    - Etichettatura Sky Goals (cielo aperto e varchi esterni)│
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│ FASE 2: Costruzione Grafo a Node-Splitting (u_IN -> u_OUT)  │
│    - Capacità interna nodo: 25.0 * frazione apertura blocco │
│      (Aria=25.0, Grata=15.0, Slab=12.5, Scale=6.25)         │
│    - Capacità archi adiacenti u_OUT -> v_IN:                │
│      25.0 * (sharedBits / 4.0) tramite bitmask a 4 quadranti│
│    - Collegamento Sky Goals -> SINK (capacità 25.0)         │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│ FASE 3: Risoluzione Dinic Max-Flow su Rete Residua G_f      │
│    - Costruzione Level Graph (BFS)                          │
│    - Ricerca Blocking Flow (DFS con puntatori ptr[])        │
│    - Calcolo Flusso Massimo Totale F_max                    │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│ FASE 4: Normalizzazione Aerazione e Regola di Sicurezza     │
│    - Aeration = min(1.0, F_max / TotalRequestedFlow)        │
│    - Applicazione all'equazione di asciugatura H_eff        │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Dettaglio Implementativo dei Moduli

### 3.1. Node-Splitting e Capacità Geometriche

Ogni voxel $u$ scoperto viene sdoppiato in due nodi per vincolare la capacità volumetrica:
$$u_{\text{IN}} = 2 \cdot i, \quad u_{\text{OUT}} = 2 \cdot i + 1$$

L'arco interno $(u_{\text{IN}}, u_{\text{OUT}})$ ha capacità pari alla permeabilità intrinseca del blocco:

| Tipo Voxel / Blocco | Capacità Nodo ($c(u_{\text{IN}}, u_{\text{OUT}})$) |
|---|---|
| **Aria Libera (`AIR`)** | $25.0$ (`open_sky_ventilation_per_block`) |
| **Grata di Rame (`GrateBlock`)** | $15.0$ (`copper_grate_ventilation_per_block`) |
| **Porta Aperta (`DoorBlock`)** | $15.0$ (`door_ventilation_value`) |
| **Botola Aperta (`TrapdoorBlock`)** | $15.0$ (`trapdoor_ventilation_value`) |
| **Staccionata / Sbarre / Cancelletto** | $3.0$ (`ventilation_gap_bonus`) |
| **Mezza Lastra (`SlabBlock`)** | $12.5$ ($25.0 \times 0.5$) |
| **Scala (`StairsBlock`)** | $6.25$ ($25.0 \times 0.25$) |

### 3.2. Maschere di Contatto a 4 Quadranti (`getFaceOpenMask`)
Per due voxel adiacenti $u$ e $v$ lungo la direzione $\text{dir}$, la capacità dell'arco $(u_{\text{OUT}}, v_{\text{IN}})$ è proporzionale all'area di sovrapposizione effettiva:

$$\text{sharedBits} = \text{bitCount}(\text{getFaceOpenMask}(u, \text{dir}) \ \& \ \text{getFaceOpenMask}(v, \text{dir}^{-1}))$$
$$c(u_{\text{OUT}}, v_{\text{IN}}) = 25.0 \times \frac{\text{sharedBits}}{4.0}$$

---

### 3.3. Risoluzione con Algoritmo di Dinic

Il risolutore implementato in [`VoxelMaxFlowEngine.java`](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/event/VoxelMaxFlowEngine.java) garantisce complessità temporale $O(V^2 E)$ e tempi di esecuzione dell'ordine dei microsecondi grazie all'impiego di array primitivi:

1. **`bfs(SOURCE, SINK)`**: Costruisce il grafo a livelli `level[]`, identificando le distanze minime dalla sorgente al pozzo.
2. **`dfs(u, SINK, pushed)`**: Effettua l'inoltro del flusso bloccante lungo i soli archi a livello incrementale (`level[v] == level[u] + 1`), aggiornando le capacità residue in tempo reale con puntatori di avanzamento `ptr[u]`.

---

## 4. Normalizzazione ed Equazioni di Crescita Muffa

Nel calcolo del rischio muffa ([`MoldyBlockHelper.java`](file:///C:/Users/rcky0/Desktop/spores--shadows/src/main/java/moldmod/block/MoldyBlockHelper.java#L267-L278)), l'aerazione normalizzata riduce l'umidità grezza ($H_{raw}$):

$$\text{Aeration} = \min\left(1.0, \frac{F_{\text{max}}}{\sum \text{RequestedFlow}_{\text{faces}}}\right)$$

$$\text{AerationDryingBonus} = \text{Aeration} \times \text{config.aeration\_drying\_bonus}$$

$$H_{eff} = \max(0.0, \min(1.0, H_{raw} - \text{AerationDryingBonus}))$$

---

## 5. Risultati della Suite di Test (GameTest 96/96)

Tutti i 96 test automatici di Minecraft passano con successo:
- ✅ **Open Air**: $F_{\text{max}} = 25.0$ per faccia libera $\to \text{Aeration} = 1.0$.
- ✅ **Cella Sigillata**: Nessun cammino verso lo Sky SINK $\to F_{\text{max}} = 0.0 \to \text{Aeration} = 0.0$.
- ✅ **Media Multi-Faccia**: 1 faccia aperta e 1 chiusa $\to (25.0 + 0.0) / 50.0 = 0.50$.
- ✅ **Collo di Bottiglia (Hole 1x1)**: Foro $1 \times 1$ limita rigorosamente il flusso a $25.0$.
- ✅ **Varchi e Fessure**: Staccionate, botole, porte e grate modulano accuratamente il flusso residuo.
