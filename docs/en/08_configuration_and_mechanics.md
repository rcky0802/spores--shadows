# 💻 Configuration and Mechanics

A technical reference for modpack creators, server operators, and advanced survivalists seeking to understand or fine-tune the inner workings of the mod.

---

## 📖 JEI (Just Enough Items) Integration

The mod natively integrates 7 dedicated JEI recipe and information categories to document mechanics directly in-game, without requiring external wikis:

1. **Waxing** — All block → waxed block transformations using Honeycomb
2. **Axe Scraping** — Wax stripping and mold curing ($2 \rightarrow 1 \rightarrow 0$) using an axe
3. **Plank Recovery** — Crafting grids for cleaning and reclaiming infected planks
4. **Mold Infection** — Visualized natural environmental progression ($0 \rightarrow 1 \rightarrow 2 \rightarrow 3$)
5. **Information Tabs** — Extreme friability, missing drops, and special rules for Stage 3
6. **Dehumidification & Water** — Operating power, moisture extraction rates, and fluid modes
7. **Purification & Filters** — Filter cartridge recipes and volumetric purification stats

## 🔍 Jade / WTHIT Integration

The contextual in-game tooltip displays real-time diagnostics for any targeted wooden block:

- Fungal decay stage and waxed status
- Local Environmental Infection Risk ($R\%$) with dynamic coloring (gray = stable, red = at risk)
- Composter fill level and odds
- Energy reserves, active operating mode, and fluid/filter status for machinery

## 🏆 Advancements

The mod features a comprehensive tree of **11 advancements** spanning survival basics, technological monitoring, and major climate engineering challenges:

### 🌿 Survival and Mold Remediation
- **Spores & Shadows** *(Root)* — Survive the decay of nature.
- **Natural Prevention** — Use a honeycomb to wax a wood block and stop the mold.
- **Elbow Grease** — Scrape the mold off a wood block using an axe.
- **Short Breath** — Suffer the poison of the miasma by breathing too much mold.
- **Dust to Dust** — Attempt to break an unwaxed rotten wood block (Stage 3) and watch it crumble into nothing.

### 🧭 Instrumentation and Monitoring
- **Moisture Sensing** — Craft a Moisture Detector to monitor room humidity.
- **Airborne Sentinel** — Craft a Spore Detector to monitor air quality and fungal miasma.

### ⚙️ Engineering and Remediation Challenges
- **Climate Control** — Craft a Dehumidifier to actively dry enclosed rooms and collect condensed water.
- **Hermetic Bunker** — Craft an Air Purifier to purge miasma and make enclosed rooms breathable.
- 🏆 **Subterranean Oasis** *(Challenge)* — Dry an underground chamber ($Y \le 40$) down to less than 15% humidity using a Dehumidifier.
- 🏆 **Pure Air in the Depths** *(Challenge)* — Completely decontaminate a mold-infested underground room in the depths of the world ($Y \le 0$), restoring air quality to `CLEAN`.

---

## ⚙️ Configuration (ModMenu & Cloth Config)

Spores & Shadows exposes 19 configuration categories that can be hot-reloaded in-game through the ModMenu GUI (saved in `config/spores_and_shadows.json`):

| Category | Description & Controlled Features |
| :--- | :--- |
| **General** | Global decay toggle, scan radius, infection threshold (default 0.50), axe scrape damage, functional block break chance on use (10%) |
| **Susceptibility** | Material vulnerability multipliers ($S_{mat}$): stripped (1.4×), planks/stairs/slabs/mosaics (0.8×), default (1.0×) |
| **Catalysts** | Catalyst risk weights ($C_{bonus}$): mud (+0.05), podzol/mycelium (+0.15), fungi (+0.25), Spore Blossom (+0.80), infected blocks (+0.03 / +0.06 / +0.12) |
| **Environment** | Base rain/dry humidity, depth humidity gradient, water contribution, ventilation drying, and humidity saturation/dissipation speeds |
| **Drops** | Non-Silk Touch drop chances: Stage 2 (50%) and Stage 3 (0%) |
| **Structures** | Pre-decay chances for world-generated structures and environmental bonuses (underwater, depth, ground contact) |
| **Furnace Fuel Efficiency** | Fuel burn time multipliers per decay stage (1.0×, 0.5×, 0.25×, 0.125×) |
| **Flammability** | Fire catch bonuses (+5, +10, +20) and flame spread bonuses (+10, +25, +60) for stages 1, 2, and 3 |
| **Blast Resistance** | Explosion resistance (TNT) multipliers per decay stage (80%, 50%, 10%) |
| **Hardness & Degradation** | Block hardness scaling per decay stage (80%, 50%, 20%) |
| **Redstone** | Extended pulse duration multipliers for buttons/plates and trapped chest jamming chances (15%, 50%, 85%) |
| **Composter** | Composting success chances per decay stage (50%, 65%, 85%) |
| **Particles** | Particle counts and types spawned upon breaking advanced infected wooden blocks |
| **Spore Detector & Mask** | Tick delays (initial/periodic), Redstone signal multiplier (default 5× per stage), item use cooldown, Spore Mask armor & durability |
| **Moisture Detector** | Tick delays (initial/periodic), Redstone signal multiplier (default 5× per stage), item use cooldown |
| **Dehumidifier** | Water tank capacity (2000 mB), ticks per mB (24), fuel multiplier (4.0×), drying power (1.0), FE capacity (32,000 FE) and consumption (10 FE/t) |
| **Air Purifier** | Toxic cleaning power (48.0), Spore Filter durability (2400 ticks / 2 min), fuel multiplier (4.0×), FE capacity (32,000 FE) and consumption (10 FE/t) |
| **Toxicity** | BFS volume (2048 m³), scan radius (16 blocks), ventilation flow values (Base 6/24), 3 toxicity thresholds (6, 12, 18), Spore Mask and Spore Filtration mechanics |
| **Client & Shaders** | Anti-Z-fighting Z-offset for block rendering (0.002) and GUI mold overlay opacity intensity (1.0) |

## 💻 Administrative Commands

Requires operator permission level 2:

- `/miasma` — Real-time BFS room scan: total room air volume, toxic score, active ventilation score, environment classification (Open Air / Enclosed).
- `/moldrisk` — Complete mathematical breakdown of Infection Risk $R$ for the targeted block: $H_{eff}$, $L_{uv}$, $S_{mat}$, detected catalysts, $M_{bonus}$, $T_{mult}$, and final $R$ value.

---

| | |
| :--- | ---: |
| [← Decay in the Last Detail](07_decay_in_the_last_detail.md) | [📑 Table of Contents](README.md) |
