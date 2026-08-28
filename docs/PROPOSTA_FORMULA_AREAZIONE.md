# 🍄 Specifiche Tecniche & Architettura Ufficiale: Formula del Rischio Muffa, Areazione e Miasma Unificato

Questo documento descrive formalmente l'architettura implementata in **Spores & Shadows** per il calcolo del **Rischio di Infezione della Muffa ($R$)**, l'**Asciugatura Dinamica da Areazione (Variante C)** e il **Modello Fisico Unificato del Miasma Aereo** condiviso tra giocatore e ambiente.

---

## 🔬 1. Fondamenti Scientifici & Dinamica Biologica

Il sistema si fonda su tre principi fisici e micologici realistici:

1. **Asciugatura Dinamica da Flusso d'Aria (Areazione $\text{Aer}$)**:
   * La ventilazione (esposizione al cielo o prese d'aria/grate perimetrali) asporta per convezione lo strato limite di vapore acqueo superficiale, favorendo l'evaporazione rapida.
   * L'areazione sottrae direttamente umidità dal legno ($H_{\text{eff}}$), abbassando l'attività dell'acqua ($a_w$) e impedendo alle spore di germinare in ambienti ben ventilati.
2. **Contagio Diretto da Ife del Micelio (Contatto Solido $3 \times 3 \times 3$)**:
   * Due travi di legno a contatto diretto o il contatto con terreni biologici (fango, podzol, funghi, spore blossom) trasmettono l'infezione per penetrazione fisica delle fibre lignee (crescita vegetativa).
3. **Contagio Aereo & Tossicità Volumetrica (Aerosol di Spore / Miasma)**:
   * In ambienti confinati e non ventilati, i legni marcescenti saturano l'aria di spore microscopiche ($Miasma$).
   * L'esposizione biologica ($E_{\text{miasma}}$) dipende in modo continuo sia dalla **concentrazione volumetrica locale ($\text{Densità}$)** sia dal **carico totale dei focolai attivi ($\text{NetMiasma}$)**.
   * Questa qualità dell'aria viene calcolata una sola volta e governa sia gli effetti sul **giocatore** (fame, nausea, veleno) sia la pressione infettiva aerea sul **legno** ($M_{\text{aria}}$).

---

## 📐 2. La Formula Matematica Finale del Rischio ($R$)

$$R = \Big( \big(H_{\text{eff}} \times L_{\text{uv}} \times S_{\text{mat}}\big) + \text{CatalizzatoriContatto} + M_{\text{aria}} \Big) \times T_{\text{mult}}$$

Se $R > \text{soglia\_infezione}$ (default: **$0.40$**), il blocco avanza allo stadio successivo durante il `randomTick`.

---

### 💧 A. Calcolo dell'Umidità Effettiva ($H_{\text{eff}}$)

$$H_{\text{raw}} = H_{\text{base}} + H_{\text{depth}} + H_{\text{water}}$$
$$H_{\text{eff}} = \max\Big(0.0, \; \min\big(1.0, \; H_{\text{raw}} - (\text{Aer} \times \text{BonusAsciugatura})\big)\Big)$$

* $H_{\text{base}}$: Umidità climatica di base (`0.80` pioggia/neve, `0.30` secco/arido).
* $H_{\text{depth}}$: Malus profondità sotto il livello del mare ($Y < 64$, $+0.01$/blocco fino a $+1.28$).
* $H_{\text{water}}$: Prossimità a blocchi d'acqua (`+0.15`) o calderoni (`+0.10`), raggio 3 blocchi (fino a $+0.60$).
* $\text{Aer} \in [0.0, 1.0]$: Coefficiente di ventilazione della stanza determinato via BFS.
* $\text{BonusAsciugatura}$ (default: **`0.50`**): Potere asciugante massimo del flusso d'aria.

---

### ☀️ B. Fattori Ambientali Standard
* $L_{\text{uv}}$: Grado di oscurità / assenza di luce UV ($\frac{15 - \text{Luce}}{15}$). Scala da `0.0` (Luce 15: sterilizzazione) a `1.0` (Buio totale: Luce 0).
* $S_{\text{mat}}$: Suscettibilità del materiale (tronco grezzo `1.0`, assi lavorate `0.80`, legno scortecciato `1.40`).
* $T_{\text{mult}}$: Moltiplicatore temperatura ($1.0$ nel range vitale $[0.15, 1.50]$, altrimenti $0.0$ per sterilizzazione termica).

---

### 🪵 C. Catalizzatori di Contatto Diretto ($\text{CatalizzatoriContatto}$)
Rilevati tramite scansione locale a corto raggio ($3 \times 3 \times 3$):
* **Suoli Biologici & Funghi**:
  * Fango / Mud: `+0.05`
  * Podzol / Micelio: `+0.15`
  * Funghi (Rossi / Marroni / Fusti): `+0.25`
  * Fiore di Spore / *Spore Blossom*: `+0.80`
* **Contatto Legno-Legno (Penetrazione Ife)**:
  * Legno Intaccato adiacente (Stadio 1): `+0.03`
  * Legno Ammuffito adiacente (Stadio 2): `+0.06`
  * Legno Marcio adiacente (Stadio 3): `+0.12`

---

### 💨 D. Pressione Aerea del Miasma ($M_{\text{aria}}$)

$$M_{\text{aria}} = E_{\text{miasma}} \times k_{\text{miasma\_spore\_multiplier}}$$

dove l'**Indice di Esposizione Unificato ($E_{\text{miasma}}$)** è calcolato centralmente in `MiasmaResult`:

$$E_{\text{miasma}} = \text{Densità} \times \left(0.5 + 0.5 \times \min\left(2.0, \sqrt{\frac{\text{NetMiasma}}{8.0}}\right)\right)$$

* $\text{Densità} = \frac{\text{NetMiasma}}{\text{VolumeStanza}}$.
* $\text{NetMiasma} = \max(0.0, \text{ToxicScore} - \text{VentilationScore})$.
* $k_{\text{miasma\_spore\_multiplier}}$ (default: **`0.50`**).
* Se la stanza è all'aperto o ventilata con grate/finestre $\implies \text{NetMiasma} = 0.0 \implies E_{\text{miasma}} = 0.0 \implies \mathbf{M_{\text{aria}} = 0.0}$.

---

## 🧠 3. Architettura del Codice & Zero Duplicazione

La classe centralizzata `ToxicAirEvent.MiasmaResult` gestisce interamente l'analisi dell'aria:

```mermaid
graph TD
    BFS[Scansione BFS Unica dell'Aria] --> MR[ToxicAirEvent.MiasmaResult]
    
    MR -->|1. Valutazione Qualità Aria| Status[calcola: netMiasma, density, exposureIndex, AirToxicityLevel]
    
    Status -->|2. Applicazione al Giocatore| Player[checkRoomMiasma: switch su level -> VELENO / FAME / PARTICELLE]
    Status -->|3. Contagio Aereo del Legno| Mold[MoldyBlockHelper: Maria = exposureIndex * k_spore]
    Status -->|4. Diagnostica Unificata| Commands[/miasma & /moldrisk: medesime metriche e terminologia]
```

### Livelli di Severità Unificati (`AirToxicityLevel`):
1. **`CLEAN`**: Ambiente all'aperto, ventilato o senza focolai di muffa $\rightarrow$ Zero miasma, $M_{\text{aria}} = 0.0$.
2. **`WARNING`**: Bassa concentrazione di spore ($\text{Net} \ge 2.6$ o $\text{Densità} \ge 0.04$) $\rightarrow$ Particelle fungine nell'aria, nessun effetto negativo immediato.
3. **`MODERATE_HUNGER`**: Concentrazione moderata ($\text{Net} \ge 8.0$ o $\text{Densità} \ge 0.09$) $\rightarrow$ Effetto Fame sul giocatore, $M_{\text{aria}} > 0$ modesto sul legno.
4. **`LETHAL_POISON`**: Saturazione letale ($\text{Net} \ge 16.0$ o $\text{Densità} \ge 0.18$ con $\text{Net} \ge 10.0$) $\rightarrow$ Nausea + Veleno sul giocatore, $M_{\text{aria}} > 0$ elevato con rapido contagio aereo su tutte le travi della stanza.

---

## 📊 4. Tabella degli Scenari di Gioco (Soglia $R > 0.40$)

| Scenario | $H_{\text{raw}}$ | $\text{Aer}$ | $H_{\text{eff}}$ | Buio ($L_{\text{uv}}$) | Contatto Diretto | Miasma ($M_{\text{aria}}$) | Rischio $R$ | Esito | Dinamica |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| 🌲 **All'aperto di notte** | 0.30 | **1.00** | **0.00** | 1.00 | 0.00 | **0.00** | **0.00** | ✅ **SICURO** | L'aria asciuga il legno; la notte è innocua. |
| 🕳️ **Cantina buia sigillata pulita** | 0.60 | **0.00** | **0.60** | 1.00 | 0.00 | **0.00** | **0.60** | ❌ **INFETTA** | Umidità sotterranea e aria stagnante generano la prima infezione spontanea. |
| 🪟 **Cantina con 2 grate esterne** | 0.60 | **1.00** | **0.10** | 1.00 | 0.00 | **0.00** | **0.10** | ✅ **SICURO** | Le finestrelle asciugano l'umidità; legno salvo. |
| 🪵 **Trave a contatto con legno marcio all'aperto** | 0.30 | **1.00** | **0.00** | 0.00 | **+0.12** | **0.00** | **0.12** | ✅ **RESISTE** | Il contatto solido trasmette ife, ma la luce solare sterilizza e impedisce il degrado. |
| 🪵 **Trave a contatto con legno marcio in cantina areata** | 0.60 | **1.00** | **0.10** | 1.00 | **+0.12** | **0.00** | **0.22** | ✅ **RESISTE** | L'areazione mantiene $R < 0.40$. |
| ☣️ **Cantina chiusa mefitica (Trave sul soffitto distante)** | 0.60 | **0.00** | **0.60** | 1.00 | 0.00 *(No contatto)* | **+0.28** *(Miasma aereo)* | **0.88** | ❌ **INFETTA** | **Contagio aereo da miasma**: le spore sospese nell'aria infettano a distanza la trave sana! |

---

## ⚙️ 5. Parametri di Configurazione (`ModConfig.java`)

```java
public static class Environment {
    // Areazione & Asciugatura
    public boolean enable_ventilation_drying = true;
    public double aeration_drying_bonus = 0.50; // Quanta umidità rimuove la ventilazione (Aer = 1.0)
    public double ventilation_threshold_full_aeration = 6.0; // Punteggio fessure/aperture per Aer = 1.0
    
    // Miasma Aereo
    public boolean enable_miasma_spore_pressure = true;
    public double miasma_spore_multiplier = 0.50; // Moltiplicatore di pressione infettiva aerea
}

public static class Catalysts {
    public float mud_bonus = 0.05f;
    public float podzol_mycelium_bonus = 0.15f;
    public float fungi_bonus = 0.25f;
    public float spore_blossom_bonus = 0.80f;
    
    // Contatto diretto travi di legno (Ife)
    public float tainted_block_bonus = 0.03f;
    public float moldy_block_bonus = 0.06f;
    public float rotten_block_bonus = 0.12f;
}
```

---

## 💻 6. Comandi Diagnostici di Gioco

### `/moldrisk verbose`
```text
[Mold Risk Verbose] Block at 14, 42, -88
- Block: minecraft:oak_planks (Stage: Normal, Waxed: No)
- Formula: ((Heff * Luv * Smat) + DirectCatalysts + MiasmaAir) * Tmult
- Raw Humidity: 0.62 [Base: 0.30 | Depth: +0.32 | Water: +0.00]
- Aeration (Airflow): 0.00 [Drying Bonus: -0.00 | Air Vol: 45]
- Effective Humidity (Heff): 0.62
- Luv (Darkness): 1.00 [Avg Light: 0.0 / 15.0]
- Smat (Material): 0.80 [Planks]
- Direct Catalysts: 0.00 [Adjacent blocks]
- Miasma Air Pressure: +0.22 [Net Miasma: 20.00]
- Effective Temp: 0.50 => Tmult: 1.00
- Infection Threshold: 0.40
=> R = 0.7160 (WILL GROW)
```

### `/miasma`
```text
[Miasma Scanner] Scanning environment...
- Environment: Confined Space (Volume: 45 blocks analyzed)
- Toxicity Score (from mold): +20.00
- Ventilation Score (from openings/gaps): +0.00
- Net Miasma: 20.00
- Spore Density: 0.444 | Exposure Index: 0.697
[WARNING] Lethal level! Nausea and Poison imminent!
```
