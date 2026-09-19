# 🧱 Building with Rot

Once wood begins to rot, its value as a building material plummets quickly. The mod imposes tangible consequences on crafting, physical durability, and furnace smelting — all tied together by coherent internal logic: the more compromised the timber, the less you can extract from it.

---

## 🧱 Mechanical, Thermal Degradation and Brittleness

As fungal hyphae consume cellulose and lignin, wood loses its structural cohesion and retains dry, fine dust. All of its physical and thermal properties degrade in parallel:

| Property | 🌲 Stage 0 (Clean) | 🟢 Stage 1 (Tainted) | 🦠 Stage 2 (Moldy) | ☠️ Stage 3 (Rotten) |
| :--- | :---: | :---: | :---: | :---: |
| **Block Hardness** | `2.0` (100%) | `1.6` (80%) | `1.0` (50%) | `0.4` (20%) |
| **Blast Resistance (TNT)** | 100% | 80% | 50% | **10%** |
| **Tool Effectiveness** | Normal (Axe) | Normal (Axe) | Normal (Axe) | **Nullified (Fist = Axe)** |
| **Survival Drops** | `100%` | `100%` | `50%` (Half lost) | **`0%` (Crumbles to dust)** |
| **Drops with Silk Touch / Wax** | `100%` | `100%` | `100%` | `100%` |
| **Fire Catch Bonus** | $+0$ (Vanilla) | $+5$ | $+10$ | $+20$ |
| **Fire Spread Bonus** | $+0$ (Vanilla) | $+10$ | $+25$ | $+60$ |
| **Furnace Fuel Power** | `100%` (1.0×) | `50%` (0.5×) | `25%` (0.25×) | `12.5%` (0.125×) |
| **Composting Chance** | — (Not compostable) | `50%` | `65%` | **`85%`** (Excellent fertilizer) |

> [!WARNING]
> **Stage 3 — Extreme Friability**: Breaking a rotten block completely negates the axe advantage: whether using a Netherite axe or bare fists, it takes the same amount of time, and the block crumbles to dust without dropping anything (unless harvested with *Silk Touch* or previously waxed).  
> **Smelting Charcoal**: Infected logs (Stages 1, 2, 3 — both regular and waxed) **cannot be smelted into charcoal**: compromised fungal matter prevents carbonization. Only clean Stage 0 Overworld logs (vanilla or waxed clean) can be smelted in a furnace to obtain charcoal. Infected logs can only be used as fuel (with reduced burn duration).

---

## 📐 Crafting Rules

**Only clean or waxed clean planks can be used to craft complex wooden items** (doors, chests, stairs, crafting tables, etc.). Infected planks are rejected by recipes for finished wooden goods.

The conversion of infected logs into clean planks follows **progressive halving**:

| Log | Clean Planks Yield |
| :--- | :---: |
| Clean / Waxed Clean | 4 |
| Tainted | 2 |
| Moldy | 1 |
| Rotten | 0 — unrecoverable |

**Purifying planks on the crafting grid**: if you have dismantled an old structure and recovered infected planks, you can reclaim clean planks at a crafting table:
- 2 Tainted Planks → 1 Clean Plank
- 4 Moldy Planks → 1 Clean Plank

*Regular and waxed variants of the same decay stage can be freely mixed in the crafting grid.*

---

| | |
| :--- | ---: |
| [← Why Wood Rots](02_why_wood_rots.md) | [The Air That Kills →](04_the_air_that_kills.md) |
| [📑 Table of Contents](README.md) | |
