# ☠️ Die tödliche Luft

Verrottendes Holz ist kein rein statisches Problem: Es stößt aktiv Sporen an die umgebende Raumluft aus. In geschlossenen, schlecht belüfteten Räumen sammelt sich diese Kontamination an, bis sie eine tödliche Konzentration erreicht.

---

## 🌫️ Das volumetrische Miasma

Das Spiel überwacht kontinuierlich die Luftqualität um den Kopf des Spielers mithilfe eines dreidimensionalen BFS-Algorithmus (Breadth-First Search).

- **Analysiertes Raumvolumen**: Bis zu **2048 m³** zusammenhängendes Luftvolumen.
- **Maximaler Radius**: **16 Blöcke** euklidische Distanz ab der Kopfposition des Spielers.
- **Weitläufige Höhlen**: Wenn das Volumen 2048 m³ übersteigt, ohne auf geschlossene Wände zu treffen, wird der Bereich als *offen* eingestuft und das Miasma verflüchtigt sich augenblicklich durch den natürlichen Luftzug.

### Was Miasma blockiert & Belüftungswerte (Basis 6)

| Element | Verhalten | Durchflusswert ($V$) |
| :--- | :--- | :---: |
| **Feste Blöcke, Glasblöcke** | Hermetische luftdichte Barriere | `0.0` |
| **Geschlossene Türen, Falltüren** | Hermetische Barriere | `0.0` |
| **Überflutete Blöcke (waterlogged)** | **Hydraulischer Siphon** — absolut luftdichte Barriere | `0.0` |
| **Glasscheiben ($\ge 2$ Seiten verbunden)** | Durchgehendes oder Eckfenster: hermetisch dicht | `0.0` |
| **Glasscheiben ($1$ Seite verbunden)** | Teilweise offenes Fenster: mittlere Belüftung | **`+12.0`** / Block |
| **Glasscheiben (0 Verbindungen)** | Isolierte Einzelscheibe: offener Punkt | **`+24.0`** / Block |
| **Glasscheiben (vertikal)** | UP / DOWN Achse: Luft strömt frei darüber/darunter | **`+24.0`** / Block |
| **Mauern ($\ge 2$ Seiten verbunden)** | Durchgehende oder Eckmauer: hermetische Barriere | `0.0` |
| **Mauern ($1$ Seite verbunden)** | Vorspringende Teilmauer: mittlere Belüftung | **`+12.0`** / Block |
| **Mauern (0 Verbindungen)** | Einzelner Mauerpfeiler: kleiner Spalt | **`+6.0`** / Block |
| **Mauern (vertikal)** | UP / DOWN Achse: Luft strömt frei darüber/darunter | **`+18.0`** / Block |
| **Zäune / Fences (vertikal)** | UP / DOWN Achse: Luft strömt frei darüber/darunter | **`+18.0`** / Block |
| **Zäune / Fences ($\ge 2$ Seiten verbunden)** | Spalten zwischen Querlatten (rechts & links) | **`+12.0`** / Block |
| **Zäune / Fences ($1$ Seite oder 0 verbunden)** | Offener Durchlass (nur eine Seite oder Einzelpfosten) | **`+18.0`** / Block |
| **Direkter offener Himmel** | Natürlicher atmosphärischer Kamin | **`+24.0`** / Block |
| **Offene Türen/Falltüren, Kupfergitter, Blätter** | Primäre Belüftungsöffnungen | **`+18.0`** / Block |
| **Stufen (Slabs)** | Halboffene Schlitze | **`+12.0`** / Block |
| **Treppen (Stairs)** | Kleinere Ritzen und Spalten | **`+6.0`** / Block |

Die Sättigung eines Raumes geschieht nicht schlagartig: Sie steigt mit zeitlicher Trägheit (`saturation_speed = 0.15`), sobald Durchgänge versiegelt werden, und baut sich deutlich schneller ab (`dissipation_speed = 0.35`), wenn auch nur ein einzelnes Fenster oder eine Luke geöffnet wird.

### 🧮 Berechnungsformel für Raum-Miasma

In jedem geschlossenen Raum berechnet sich das Miasma-Zielniveau ($M_{target}$) aus dem Gleichgewicht zwischen biologischer Sporenproduktion und Frischluftabfuhr:

$$M_{target} = \max\Big(0.0, \ \text{Score}_{\text{toxisch}} - \text{Score}_{\text{belüftung}} - \text{Leistung}_{\text{reiniger}}\Big)$$

- **$\text{Score}_{\text{toxisch}}$**: Jeder ungewachste befallene Holzblock, der mit der Raumluft in Kontakt steht, emittiert Sporen entsprechend seiner Zerfallsstufe:
  - 🟢 Stufe 1 (Befallen): **$+1.0$**
  - 🦠 Stufe 2 (Schimmelig): **$+2.0$**
  - ☠️ Stufe 3 (Verrottet): **$+4.0$**  
  *(Gewachste Blöcke geben keinerlei Sporen ab und haben den Wert null).*
- **$\text{Score}_{\text{belüftung}}$**: Kumulativer Luftdurchsatz, der durch Öffnungen nach außen gewährleistet wird (berechnet via *Max-Flow-Algorithmus*).
- **$\text{Leistung}_{\text{reiniger}}$**: Jeder aktive Luftreiniger im Raum neutralisiert **$-48.0$** toxische Punkte.

Das **Netto-Miasma ($M_{net}$)** gleicht sich in jedem Aktualisierungszyklus dynamisch an $M_{target}$ an. Die **Sporendichte** bestimmt die Nebeltrübung und die Lungenbelastung pro m³:

$$\text{Dichte} = \frac{M_{net}}{\text{Luftvolumen } (m^3)}$$

### Toxizitätseffekte auf den Spieler (Schwellenwerte Basis 6)

| Netto-Miasma | Sporendichte | Zustand und Symptome des Spielers |
| :---: | :---: | :--- |
| **$\ge 2.0$** | $\ge 0.0417$ ($1/24$) | **Warnung**: Sichtbare Myzelpartikel in der Luft, dumpfe organische Geräusche |
| **$\ge 6.0$** | $\ge 0.0833$ ($2/24$) | **Gefahr**: Statuseffekt **Hunger** (der Körper zehrt Energie auf) |
| **$\ge 18.0$** | $\ge 0.1667$ ($4/24$) | **Tödlich**: Einsetzen von **Übelkeit**, unmittelbar gefolgt von **tödlichem Gift** |

---

| | |
| :--- | ---: |
| [← Bauen mit verrottetem Holz](03_bauen_mit_verrottetem_holz.md) | [Sich verteidigen →](05_sich_verteidigen.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
