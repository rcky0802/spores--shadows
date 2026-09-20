# ☠️ The Air That Kills

Rotting wood is not a static hazard: it actively sheds fungal spores into the surrounding atmosphere. Inside enclosed and poorly ventilated spaces, this airborne contamination accumulates until it becomes lethal.

---

## 🌫️ Volumetric Miasma

The game continuously evaluates the air around the player's head using a three-dimensional Breadth-First Search (BFS) algorithm.

- **Analyzed volume**: Up to **2048 m³** of contiguous air.
- **Maximum radius**: **16 blocks** of Euclidean distance from the player's head.
- **Open caverns**: If the scanned volume exceeds 2048 m³ without encountering enclosing walls, the environment is classified as *open air* and miasma disperses instantly — as if swept away by natural drafts.

### What Blocks Miasma & Ventilation Flow Scores (Base 6)

| Element | Behavior | Flow Score ($V$) |
| :--- | :--- | :---: |
| **Solid blocks, glass blocks** | Hermetic airtight seal | `0.0` |
| **Closed doors, closed trapdoors** | Hermetic airtight seal | `0.0` |
| **Waterlogged blocks** | **Hydraulic siphon** — perfect watertight barrier | `0.0` |
| **Glass panes (connected $\ge 2$ sides)** | Continuous or corner window: airtight hermetic seal | `0.0` |
| **Glass panes (connected $1$ side)** | Partial/open window: intermediate ventilation | **`+12.0`** / block |
| **Glass panes (0 connections)** | Isolated single pane post: fully open point | **`+24.0`** / block |
| **Glass panes (vertical)** | UP / DOWN axis: air flows freely over/under | **`+24.0`** / block |
| **Walls (connected $\ge 2$ sides)** | Continuous or corner wall: airtight seal | `0.0` |
| **Walls (connected $1$ side)** | Partial/jutting wall: intermediate ventilation | **`+12.0`** / block |
| **Walls (0 connections)** | Standalone wall post: minor gap | **`+6.0`** / block |
| **Walls (vertical)** | UP / DOWN axis: air passes freely over/under | **`+18.0`** / block |
| **Fences (vertical)** | UP / DOWN axis: air flows freely | **`+18.0`** / block |
| **Fences (connected $\ge 2$ sides)** | Rail gaps (right and left): partial passage | **`+12.0`** / block |
| **Fences (connected $1$ side or 0)** | Open gap (single side or isolated post) | **`+18.0`** / block |
| **Direct open sky** | Natural atmospheric chimney | **`+24.0`** / block |
| **Open doors/trapdoors, copper grates, leaves** | Primary ventilation gaps | **`+18.0`** / block |
| **Slabs** | Semi-open partial gaps | **`+12.0`** / block |
| **Stairs** | Minor cracks and slits | **`+6.0`** / block |

Room saturation is not instantaneous: it builds up with temporal inertia (`saturation_speed = 0.15`) when ventilation openings are sealed, and dissipates significantly faster (`dissipation_speed = 0.35`) upon opening even a single window or aperture.

### 🧮 Miasma Generation Formula

In any enclosed environment, the room's target miasma score ($M_{target}$) is calculated as an equilibrium between biological spore emission and atmospheric evacuation:

$$M_{target} = \max\Big(0.0, \ \text{Score}_{\text{toxic}} - \text{Score}_{\text{ventilation}} - \text{Power}_{\text{purifiers}}\Big)$$

- **$\text{Score}_{\text{toxic}}$**: Every unwaxed infected wooden block facing the room's airspace emits spores according to its stage:
  - 🟢 Stage 1 (Tainted): **$+1.0$**
  - 🦠 Stage 2 (Moldy): **$+2.0$**
  - ☠️ Stage 3 (Rotten): **$+4.0$**  
  *(Waxed blocks release no spores and contribute zero).*
- **$\text{Score}_{\text{ventilation}}$**: Cumulative airflow provided by gaps leading to the exterior (resolved via a *Max-Flow* algorithm).
- **$\text{Power}_{\text{purifiers}}$**: Each active Air Purifier running in the room eliminates **$-48.0$** toxic points.

The **Net Miasma ($M_{net}$)** dynamically converges toward $M_{target}$ on each update cycle. The **Spore Density** determines visual fog opacity and respiratory pulmonary load per $m^3$:

$$\text{Density} = \frac{M_{net}}{\text{Air Volume } (m^3)}$$

### Toxicity Effects on the Player (Base 6 Thresholds)

| Net Miasma | Spore Density | Status & Symptoms Inflicted on Player |
| :---: | :---: | :--- |
| **$\ge 2.0$** | $\ge 0.0417$ ($1/24$) | **Warning**: Visible mycelium particles floating in air, deep organic ambient sounds |
| **$\ge 6.0$** | $\ge 0.0833$ ($2/24$) | **Hazard**: **Hunger** status effect (body burns energy rapidly) |
| **$\ge 18.0$** | $\ge 0.1667$ ($4/24$) | **Lethal**: Contraction of **Nausea** followed by **Lethal Poison** |

---

| | |
| :--- | ---: |
| [← Building with Rot](03_building_with_rot.md) | [Defending Yourself →](05_defending_yourself.md) |
| [📑 Table of Contents](README.md) | |
