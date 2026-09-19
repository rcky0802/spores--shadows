# 🔬 Why Wood Rots

Every wooden block in the world is evaluated autonomously on every tick. The outcome of that evaluation is a single number — the **Infection Risk ($R$)** — which determines whether decay advances or halts.

$$R = \Big( (H_{eff} \cdot L_{uv} \cdot S_{mat}) + C_{bonus} + M_{bonus} \Big) \cdot T_{mult}$$

If $R > 0.50$, the decay stage advances. Otherwise, the block remains stable. Each factor represents an authentic environmental condition.

---

## 💧 Effective Moisture ($H_{eff}$)

Moisture is the primary driver of decay. For any completely waterlogged block, $H_{eff} = 1.0$ (absolute maximum). In all other cases, it is calculated by combining local atmospheric humidity, catalysts, and the drying effect of airflow:

$$H_{eff} = \text{clamp}\Big( H_{current} + C_{humidity} - A_{drying}, \ 0.0, \ 1.0 \Big)$$

Where room atmospheric humidity ($H_{current}$) dynamically converges toward the room's environmental target:

$$H_{target} = \text{clamp}\Big( H_{base} + D_{depth} + W_{water} + H_{humidifier} - D_{dehumidifier}, \ 0.0, \ 1.0 \Big)$$

- **Biome Base Humidity ($H_{base}$)**: Rain or snow biomes start at `0.80`; arid or desert climates start at `0.30`.
- **Depth Modifier ($D_{depth}$)**: Descending below $Y = 64$, moisture increases gradually until reaching the maximum cap of $+0.40$ at $Y \le 48$, remaining constant throughout Deepslate down to $Y = -64$.
- **Water Sources in the Room ($W_{water}$)**: Each water block present facing the room adds $+0.15$ (up to a maximum cap of $+0.60$).
- **Machinery ($H_{humidifier} / D_{dehumidifier}$)**: Active Humidifiers ($+1.0$ each) or Dehumidifiers ($-1.0$ each).
- **Local Catalysts ($C_{humidity}$)**: Adjacent water-rich blocks (e.g. mud or water cauldrons) add $+0.10$.
- **Aeration Drying ($A_{drying}$)**: Local ventilation actively dries the block surface: $A_{drying} = \text{Aeration} \cdot 0.50$.

## ☀️ UV Light ($L_{uv}$)

Light acts as a natural sterilizer. The illumination level is sampled around the block:
- **6 sample points** (adjacent faces) for solid, opaque blocks (logs, planks).
- **7 sample points** (6 faces + interior block space) for non-solid or transparent blocks (stairs, slabs, fences, doors, signs).

The average detected light is scaled between `0.0` (maximum light level 15 — full sterilization, infection completely blocked) and `1.0` (pitch darkness 0 — full risk). A well-lit block in an open room has its risk virtually eliminated; the exact same block in a dark cavern is extremely vulnerable.

## 🪓 Material Susceptibility ($S_{mat}$)

Not all wooden components share the same biological vulnerability:

| Category | Multiplier ($S_{mat}$) | Block Details |
| :--- | :---: | :--- |
| **Stripped Wood** | **1.4×** | Stripped logs, stripped wood, stripped stems, and hyphae (`stripped_*`). Stripped of protective bark, living fibers are the most vulnerable of all. |
| **Default / Logs & Furnishings** | **1.0×** | Logs with protective bark, furnishings, doors, trapdoors, fences, signs, chests, and crafting tables. |
| **Processed Construction Timber** | **0.8×** | Planks (`*_planks`), Stairs (`*_stairs`), Slabs (`*_slab`), and Bamboo Mosaic. Seasoned, squared construction lumber with superior structural resistance. |

## 🌡️ Temperature & Biological Window ($T_{mult}$)

Fungal spores only thrive within the "Biological Window" ($0.15 \le \text{Temp} \le 1.50$). Outside this range, $T_{mult} = 0.0$ and fungal proliferation halts completely:

- **Mountains and High Altitude**:
  - Progressive cooling begins when rising above **$Y = 128$**.
  - As you climb toward **$Y = 256$**, temperature steadily drops until reaching $-0.50$ (a value that remains fixed up to the world ceiling at **$Y = 320$**).
  - Mold growth **halts completely as soon as temperature drops below $0.15$** (typically between $Y \approx 180$ and $Y \approx 220$ depending on biome), leaving mountain chalets naturally protected by sub-zero frost.
- **Underground and Caves**:
  - Descending below sea level (**$Y = 64$**), temperature normalizes toward the damp microclimate of caves.
  - Starting from **$Y \le 48$** and throughout Deepslate down to **$Y = -64$**, temperature constantly stabilizes at the ideal value of **`0.50`**, ensuring abandoned mines always rot regardless of surface weather (even beneath an arid desert or frozen tundra).

## ☣️ Physical Catalysts ($C_{bonus}$) & Miasma Pressure ($M_{bonus}$)

Biological blocks situated within the surrounding scan radius (a 3×3×3 cube centered on the block) accelerate infection by adding a direct bonus to Infection Risk $R$, or by increasing local moisture:

| Catalyst | Risk Bonus ($C_{bonus}$) | Local Moisture Bonus | Details & Behavior |
| :--- | :---: | :---: | :--- |
| **Spore Blossom** (`Spore Blossom`) | **+0.80** (+80%) | — | **Extremely lethal**: on its own exceeds the infection threshold (0.50). Highly discouraged as decoration near timber structures! |
| **Mushrooms** (red, brown, huge mushroom blocks) | **+0.25** (+25%) | — | Ground mushrooms and huge mushroom blocks continuously shed spores upon contact. |
| **Podzol & Mycelium** | **+0.15** (+15%) | — | Organic soils rich in subterranean fungal hyphae. |
| **Mud** (`Mud`) | **+0.05** (+5%) | **+0.10** | Retains heavy moisture and accelerates rot at the base of buildings. |
| **Water Cauldron** | — | **+0.10** | Adds stagnant local moisture within a 3-block radius. |
| **Unwaxed Tainted Block** (Stage 1) | **+0.03** each | — | Every nearby diseased block passively spreads contagion to adjacent clean timber. |
| **Unwaxed Moldy Block** (Stage 2) | **+0.06** each | — | Contagion pressure doubled compared to Stage 1. |
| **Unwaxed Rotten Block** (Stage 3) | **+0.12** each | — | Massive biological contagion load radiating to all neighboring blocks. |

> [!NOTE]
> **Waxed** wooden blocks (`waxed`) do **not** act as catalysts: the protective honeycomb layer completely seals away spores and nullifies their infectious contribution toward neighboring blocks.

### 🌫️ Airborne Miasma Pressure ($M_{bonus}$)
Beyond solid physical contact, timber exposed to stagnant air inside a miasma-saturated room suffers continuous airborne contamination:
$$M_{bonus} = \text{ExposureIndex} \cdot 0.50$$
An asphyxiating, toxic room (detailed in [Chapter 4](04_the_air_that_kills.md)) causes even dry timber to succumb rapidly to airborne infection.

---

| | |
| :--- | ---: |
| [← The Rotting World](01_the_rotting_world.md) | [Building with Rot →](03_building_with_rot.md) |
| [📑 Table of Contents](README.md) | |
