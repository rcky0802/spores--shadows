# 🌳 The Rotting World

When you install Spores & Shadows, the world of Minecraft does not change overnight. It changes over time — and often you notice it too late.

The mod seamlessly replaces every wooden block with a dynamic variant. The effect is invisible at first: the block looks the same, functions the same. But environmental conditions act upon it every tick, and sooner or later the mold wins.

---

## 🪵 What Decays

All blocks belonging to any wood species in Minecraft are subject to the decay cycle: logs, planks, stairs, slabs, fences, fence gates, doors, trapdoors, pressure plates, buttons, signs — across all 11 wood species present in the game. No architectural format is immune.

## 🦠 The Four Stages

Decay is a unidirectional and relentless progression if left unaddressed:

| Stage | Name | Appearance | Infection Risk |
| :---: | :--- | :--- | :--- |
| **0** | Clean (Vanilla) | Original block appearance | None |
| **1** | Tainted | Light mycelium patches on the surface | Moderate |
| **2** | Moldy | Dense hyphae, dull color, organic surface | High |
| **3** | Rotten | Collapsed structure, fungal dust, disintegrated texture | Irreversible |

Transition from one stage to the next occurs on server *random block ticks* whenever the Infection Risk exceeds the **50%** threshold (see [Chapter 2](02_why_wood_rots.md)).

Every stage also exists in a **waxed** variant: a block sealed with honeycomb freezes its decay at its current stage, but does not revert backwards.

### 🔊 Sensory Immersion: Custom Sounds and Particles
Decay is not just a texture change: each stage possesses its own sensory identity:
- **Breaking Audio**: breaking degraded blocks (Stages 2 and 3) plays a dull, tearing sound of organic fracture (`BLOCK_FUNGUS_BREAK`), replacing the classic crisp snap of clean timber.
- **Spore Clouds**: destroying advanced blocks without *Silk Touch* triggers a visual burst of fungal particles into the air (airborne spores, falling spores, and mycelium fragments — 42 particles at Stage 2 and a massive 80 particles at Stage 3).

## 🗺️ Naturally Generated World Structures

Naturally generated structures spawn pre-aged based on their simulated environmental history:

- **Critical Decay** — Shipwrecks, Witch Huts: massive presence of Stage 3.
- **High Decay** — Abandoned Mineshafts, Zombie Villages, Ruins: mix of Stages 1 and 2.
- **Moderate Decay** — Pillager Outposts, Ruined Portals: predominantly Stage 1.
- **Minimal Decay** — Villages, Woodland Mansions: almost intact.

Living trees are immune until chopped down. Once a log is felled, degradation can begin.

---

| | |
| :--- | ---: |
| [📑 Table of Contents](README.md) | [Why Wood Rots →](02_why_wood_rots.md) |
