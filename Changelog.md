# Changelog

All notable changes to **Spores & Shadows** will be documented in this file.


## [1.2.0] - Toxic Miasma Overhaul, Config & Final Polish
**Added:**
- Overhauled the Toxic Miasma mechanic using a highly-optimized O(R³) Flood Fill algorithm. The toxic gas now realistically propagates through slightly open spaces (trapdoors, doors, stairs) and is blocked by solid walls.
- **Natural Ventilation**: Added the ability for players to create air vents using partial blocks (fences, walls, iron bars) to dissipate the miasma out of confined rooms, heavily reducing the toxicity score.
- The miasma now scales based on the volume of the room. Rooms larger than 180 blocks, or open-air areas, are completely immune to toxic gas buildup.
- Added a new tier of poisoning for slightly contaminated rooms: Hunger effect (Miasma > 8.0) and light mycelium particles.
- Added a new admin command `/miasma` to inspect the exact properties of the room the player is standing in (Volume, Toxic Score, Ventilation Score, Net Miasma, and Status).

**Changed / Refactored:**
- **Full Configurability Restored**: Restored and wired all `ModConfig` keys (Drops, Toxicity thresholds, Furnace Multipliers). Every single penalty (from furnace efficiency to toxic miasma thresholds) is now 100% customizable in-game via Cloth Config!
- **Massive Code Cleanup**: Conducted a deep codebase audit. Wiped out hundreds of lines of dead code, redundant mixins (`BlockPickMixin`), orphaned arrays (`SporesShadows.WOODS`), and unused compiler warnings.
- **DataGen Optimization**: Migrated all 5 Language Providers (`EN`, `IT`, `FR`, `ES`, `DE`) to use highly optimized static HashMaps (`Map.of()`) and removed all hardcoded MOD_ID strings.
- **Documentation Overhaul**: Completely updated the user manual (`docs/*.md`) in 5 languages to reflect the newest 1.2.0 features (Flood fill, hybrid crafting, 100% safe waxed drops, and total configurability).

---

## [1.1.1] - The Architecture & Quality of Life Update
**Major Backend Refactor:**
- **Split Registries**: We performed a massive backend overhaul, splitting "Waxed" and "Moldy" blocks into two completely distinct block IDs (e.g., `waxed_oak_log` vs `moldy_oak_log`) instead of relying on a blockstate property. This allowed us to bypass Vanilla limitations and implement a huge wave of requested features!
- **Single Source of Truth (SoT)**: Refactored the entire project to use a unified Data and Constant core for all generation elements. Zero hardcoded woods, stages, or IDs in loops!

**Added / Changed:**
- **Configurable Magic Numbers**: Added `axe_scrape_damage`, `nausea_amplifier`, and `poison_amplifier` to the configuration file (Mod Menu / Cloth Config) for full modpack customization!
- **Hybrid Crafting**: You can now freely mix normal infected wood and waxed infected wood (of the same decay stage) in the same crafting grid!
- **Furnace & Composter Parity**: Waxed blocks are no longer inert! They now burn in furnaces and can be composted with the exact same timings and probabilities as their unwaxed counterparts.
- **Safe Extraction (Drops)**: Waxing a "Rotten" (Stage 3) block now structurally reinforces it. Breaking a waxed rotten block guarantees a 100% drop rate, completely bypassing the crumbling mechanic without needing Silk Touch!
- **Inventory Parity**: Waxed blocks now display their lore/descriptions in the inventory, informing players that they can still be broken down into planks.
- **True Inventory Models**: Because Waxed blocks are now standalone items, they no longer look like standard moldy wood in your inventory. They have their own dedicated items!
- **Performance Boost**: Waxed blocks no longer receive Random Ticks from the server, entirely skipping the mold spread calculations and drastically saving CPU resources on large builds.

**Fixed:**
- **Jade HUD UI**: Jade now dynamically reads and displays the precise name and icon for every block state (e.g. "Waxed Tainted Oak Planks"). Redundant "Waxed: Yes/No" and "Stage" texts were removed. 
- **Jade HUD Logic**: The Infection Risk tooltip is now completely hidden on Waxed blocks (since their risk is zero) and on Rotten blocks (Stage 3).
- **Tooltips Fix**: Fixed a bug where waxed blocks were missing their gameplay lore (e.g. "Structurally weakened") in the inventory.

---

## [1.1.0] - Quality of Life, Jade Integration & Biome Overhaul
- Jade / WTHIT Integration: The HUD now shows the exact decay stage, infection risk (%), and waxed status of any looked-at wood block.
- Jade HUD percentage is now dynamically aligned with the `/moldrisk` command and features dynamic coloring (Grey for Safe, Red for At Risk).
- Added full decay support for Nether wood types (Crimson and Warped), including all their block variants (planks, stairs, doors, etc).
- 5 new Advancements to guide players through the mod's mechanics: *Spores & Shadows*, *Natural Prevention*, *Elbow Grease*, *Short Breath*, *Dust to Dust*.
- Advancements and Jade tooltips are fully localized in English, Italian, Spanish, German, and French via DataGen.
- Stage 3 (Rotten) blocks emit slime sounds when walked on or broken.
- **Shader Compatibility:** Added `Mold Z-Offset` parameter to the in-game Client Config menu to fix Z-fighting/flickering with modern shaders (Sodium/Iris).

**Changed/Fixed:**
- **Nether & End Immunity**: Mold decay is now strictly disabled in all Nether and End biomes (due to extreme temperatures).
- Fixed a client console spam issue caused by missing Item Models for Vanilla Waxed Items (`waxed_crimson_stem`, etc.) by properly registering them in DataGen.
- Fixed game-breaking tag load errors where stale block references were still lingering in Vanilla Block Tags.
- Fixed a bug where Stage 3 (Rotten) blocks still received a breaking speed bonus from axes. They now strictly ignore tool efficiency, taking the same time to break with an axe as with a bare hand.
- Restored UTF-8 encoding across all JavaDocs, documentation, and translation files.

---

## [1.0.1] - Hotfix
**Fixed:**
- Fixed a critical startup crash present in the initial release.
- Fully rotten interactive blocks (buttons, pressure plates, doors, trapdoors, fence gates) now break upon use and vanish without dropping items.
- Waxed interactive blocks are immune to random breaking.

---

## [1.0.0] - Initial Release
**Added:**
- Complete wood decay ecosystem in 4 stages: Healthy, Tainted, Moldy, Rotten.
- Decaying variants for 8 wood types (including stairs, slabs, doors, fences, etc).
- "Waxing" mechanic using honeycomb to freeze decay.
- "Toxic Miasma" mechanic: area around the player inflicts Nausea and Poison based on local mold density.
- Penalties to drops, crafting yields, and furnace fuel for degraded blocks.
- Integration with the Vanilla Composter.
- Natural spreading and climate/altitude logic for decay generation.
- In-game configuration interface via Cloth Config and ModMenu.
- Localization and documentation in 5 languages (EN, IT, ES, FR, DE) via DataGen.
- Official mod logo and VS Code build tasks.
