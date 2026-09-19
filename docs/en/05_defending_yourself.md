# 🤿 Defending Yourself

Knowing the risks is not enough: you need the right tools. The mod introduces a cohesive set of defense mechanics, from passive prevention to active protective equipment.

---

## 🐝 Preventive Waxing

Applying a **Honeycomb** to any wooden block seals it with a layer of beeswax, permanently freezing its current decay stage.

A waxed block:
- Does not decay any further, regardless of environmental conditions
- Does not release spores into the surrounding atmosphere
- Cannot spread contagion to adjacent blocks
- Always drops at **100%** when broken, even at Stage 3

Waxing does not heal the block — it preserves it at the stage it was sealed. A waxed Rotten block remains Rotten, but ceases to be an active threat.

## 🪓 Curing with an Axe

With **Sneak + Right-Click** while holding an axe, you can directly intervene on the block:

- **Dewaxing**: Strips off the protective wax coating (`ITEM_AXE_WAX_OFF`), re-enabling the biological decay cycle at the cost of 1 durability point.
- **Scraping Mold**: On unwaxed Stage 1 or Stage 2 wood, the axe scrapes away surface hyphae (`ITEM_AXE_SCRAPE`), regressing the infection by one stage ($2 \rightarrow 1 \rightarrow 0$) at the cost of 1 durability point.
- **Stage 3 (Rotten) — Completely Incurable**: The internal structural fibers are irreparably compromised. The axe produces no scraping effect on rotten wood. The only way to render it inert without breaking it is to seal it with a honeycomb (waxing).

---

## 😷 The Spore Mask

The `Spore Mask` is the primary equipment item providing passive survival in prolonged miasma. It is worn in the helmet slot and features a protruding 3D model with a visor, side respirators, and a filtration canister.

**Protection & Combat**: Completely negates the lethal effects of miasma (Hunger, Nausea, Poison). It also serves as light armor (providing **1 armor point**, equivalent to a leather cap, with **165 durability points**): it takes damage normally from hits sustained in **combat**, and consumes 1 durability point per check cycle while filtering toxic air in place of the player's lungs.

**Repair**: Repaired exclusively with **Spore Filters** on an anvil — crafted with wool, charcoal, and string. Each filter restores 100% of the mask's durability. Alternatively, two worn masks can be combined in a crafting grid for a quick field repair.

### 🔮 Spore Mask Enchantability
The Spore Mask has an **Enchantability of 0** (it cannot be enchanted at an Enchanting Table). It can receive enchantments **exclusively via enchanted books on an Anvil**, with strict compatibility restrictions:

| Enchantment | Mask Compatibility | Effect on Mask |
| :--- | :---: | :--- |
| **Unbreaking (I–III)** | ✅ **Allowed** | Reduces chance of durability loss from both combat hits and filtered air. |
| **Mending** | ✅ **Allowed** | Repairs mask durability by collecting experience orbs. |
| **Curse of Vanishing** | ✅ **Allowed** | Mask disappears upon player death instead of dropping on the ground. |
| **Spore Filtration** | ❌ **Incompatible** | **Not applicable**: Mask already natively filters miasma; enchantment is redundant. |
| **Protection / Respiration / Aqua Affinity / Thorns** | ❌ **Incompatible** | Rejected: The mask is a technical respirator, not an enchanted combat helmet. |

---

### ✨ Helmet Enchantment: Spore Filtration (`Spore Filtration`)

`Spore Filtration` is an enchantment engineered specifically for **any conventional helmet** (leather, iron, diamond, netherite, turtle shell). It allows players wearing standard armor to breathe safely inside miasma without donning a Spore Mask, offloading the toxic pulmonary burden directly onto the helmet's durability:

| Level | Durability Cost per Cycle | Durability Efficiency | Behavior |
| :---: | :---: | :---: | :--- |
| **I** | **2 points** / cycle | Standard | Crude filtration: Neutralizes miasma but wears down helmet quickly. |
| **II** | **1 point** / cycle | Optimized | Balanced filtration: Matches the efficiency of the base Spore Mask. |
| **III** | **0 or 1 point** (average 0.5) | **50% Preservation** | Advanced filtration: **50% chance to consume zero durability** per exposure cycle. |

> [!TIP]
> Applied to a high-tier helmet (such as a Netherite Helmet paired with *Unbreaking III* and *Mending*), `Spore Filtration III` allows players to explore and fight safely inside lethal miasma environments while retaining full heavy armor protection!

---

## 🧭 Handheld Detectors

To diagnose unknown environments or plan remediation:

**💧 Moisture Detector** — Held in hand and used in the air (right-click), clicks mechanically and prints a detailed analytical report of local Infection Risk ($H_{eff}$, light, temperature, adjacent catalysts) to private chat. Essential for understanding immediately why a room keeps rotting.

**☢️ Spore Detector** — Held in hand and used in the air (right-click), clicks mechanically and performs an instant scan of the air around the player's eyes (room volume, active ventilation, spore density, dynamic trend). It is completely silent while walking (no continuous passive clicking sound), ensuring maximum stealth while exploring.

*The stationary operation of these detectors — as hybrid wall/floor/ceiling Redstone sensors — is covered in [Chapter 6](06_automating_remediation.md).*

---

| | |
| :--- | ---: |
| [← The Air That Kills](04_the_air_that_kills.md) | [Automating Remediation →](06_automating_remediation.md) |
| [📑 Table of Contents](README.md) | |
