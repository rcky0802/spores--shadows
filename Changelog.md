# Changelog

All notable changes to **Spores & Shadows** will be documented in this file.

## [1.2.0] - The Atmospheric Engineering & Workstations Update
**World & Wood Ecosystem (1,442 Survival Block Variants):**
- **Bamboo Woodset Integration**: Added full decay coverage for the Bamboo woodset across all 4 stages (Healthy, Tainted, Moldy, Rotten) and their Waxed counterparts: Bamboo Blocks, Stripped Bamboo Blocks, Bamboo Planks, Bamboo Mosaic, Bamboo Mosaic Stairs/Slabs, Doors, Trapdoors, Fences, Fence Gates, Buttons, and Pressure Plates.
- **Signs & Hanging Signs**: Added decaying and waxed variants for all 11 wood species with non-destructive mold texturing preserving text legibility.
- **Z-Fighting Elimination**: Re-anchored multi-layer chest rendering with dynamic scaling (`yScale = 0.9001f`) across container open/close animations, eliminating graphical flickering artifacts.
- **Sensory Immersion & Particles**: Custom break sound (`BLOCK_FUNGUS_BREAK`) and spore particle bursts upon breaking un-waxed decayed blocks without Silk Touch (42 particles for Stage 2, 80 particles for Stage 3).
- **Physical Scaling**: Degraded blocks suffer progressive penalties: block hardness scales from 100% down to 20%, blast resistance from 100% down to 10%, flammability increases up to +60% spread and +20% catch rate, and furnace fuel efficiency drops to 12.5%. Rotten blocks completely cancel tool efficiency (bare hand equals Netherite axe).
- Total survival block count expanded to **1,442 variants**.

**Volumetric 3D BFS Miasma Engine:**
- **3D Breadth-First Search (BFS)**: Replaced legacy cylindrical checks with a true volumetric flood-fill simulation (2,048 m³ volume limit, 16-block Euclidean radius). If a room volume exceeds 2,048 m³ without enclosure, the space is classified as open cavern and miasma disperses.
- **Base-6 Airflow Permeability Scoring**: Dynamic airflow resistance calculated across 6 cardinal directions: open air / direct sky (+24.0), open doors/trapdoors/copper grates/leaves (+18.0), slabs (+12.0), stairs/fences (+6.0), and solid walls/closed doors (0.0).
- **Hydraulic Siphon**: Waterlogged blocks and water sources act as airtight seals preventing toxic gas transfer.
- **Inertia & Dynamic Equilibrium**: Added realistic saturation (`saturation_speed = 0.15`) and rapid dissipation (`dissipation_speed = 0.35`).
- **Toxicity Thresholds (Base-6)**: Calibrated symptoms: Warning ($\ge 2.0$, spore particles and ambient sounds), Danger ($\ge 6.0$, hunger status effect), and Lethal ($\ge 18.0$, nausea followed by lethal poison).

**Planks Recovery & Crafting Overhaul:**
- **Progressive Halving**: Log-to-plank conversion follows an integer division formula ($\lfloor / 2 \rfloor$: Healthy yields 4, Tainted yields 2, Moldy yields 1, Rotten yields 0).
- **Planks Purification**: Purify contaminated wood on a crafting table (2 Tainted Planks $\rightarrow$ 1 Clean Plank, 4 Moldy Planks $\rightarrow$ 1 Clean Plank). Normal and waxed planks of the same stage can be freely mixed.
- **Strict Recipe Tags**: High-tier crafted vanilla recipes (doors, chests, stairs, crafting tables) strictly require clean planks.
- **Smelting Restriction**: Infected logs (stages 1, 2, 3) can no longer be smelted into charcoal in furnaces.

**Atmospheric Engineering & Steampunk Machinery:**
- **Dehumidifier**: Condenses atmospheric moisture with 1.0 drying power into an internal 2,000 mB water tank (1 mB / 24 ticks).
  - *Automated Standby*: Automatically suspends operation when the tank reaches capacity (2,000 mB / 2 buckets) to prevent energy and fuel waste.
  - *Humidify Mode*: Invert polarity to spray vapor into the air (+1.0 room moisture), with automatic safety shut-off when reaching 98% RH.
  - *Hybrid Power*: Consumes 10 FE/tick from an internal 32,000 FE buffer, or solid fuel in Slot 0 with 4.0× furnace burn efficiency (one piece of coal yields 64,000 FE).
  - *Fluid API & Comparators*: Supports manual bucket extraction (1,000 mB per click), automated pipe drainage via Fabric Transfer API, and emits 0..15 analog comparator power proportional to tank fullness.
- **Air Purifier**: Neutralizes volatile miasma spores with -48.0 scrubbing power (clearing the toxic output of over 21 infected logs simultaneously).
  - *Spore Filter Cartridges*: Consumes replaceable Spore Filters (Slot 1, 2,400 ticks / 2 minutes lifespan) with automatic reload from the internal stack.
  - *Accelerated Wear*: Filter wear doubles to 2 points/tick under lethal miasma concentrations.
  - *Depletion Warning*: Emits an audible dispenser click (`BLOCK_DISPENSER_FAIL`) when filters are exhausted.
  - *Sided Inventory*: Top face strictly feeds Spore Filters; lateral and bottom faces strictly feed solid fuel.
  - *Comparator Output*: Emits 0..15 analog power based on remaining filter items in stack.
- **Redstone Automation**: Both machines feature a 3-way toggle switch (*Always Active* `IGNORED`, *Active with Redstone* `HIGH`, *Deactivate with Redstone* `LOW`).

**Environmental Detectors & Hybrid Redstone Sensors:**
- **Handheld Inspection**: Moisture Detector and Spore Detector provide discrete mechanical audio clicks and send silent, private in-chat telemetry reports (ambient humidity, light, temperature, catalysts, BFS room volume, and spore density). Totally silent while moving.
- **Universal Orientation**: Both detector blocks can be mounted in any orientation (floors, walls, and ceilings).
- **Hybrid Redstone Output**: Emits direct redstone signal levels (**0, 5, 10, 15**) and native vanilla comparator signals, propagating neighbor updates through solid support blocks.

**Degradable Workstations & Native GUI Overlays:**
- **9 Workstation Blocks**: Crafting Table, Chest (Single & Double), Trapped Chest, Barrel, Composter, Loom, Cartography Table, and Lectern now suffer fungal decay.
- **Native GUI Overlays**: Fungal stains, slime, and rot creep dynamically into container interfaces matching the block's current decay stage.
- **Composter Progression**: Composting probability scales with decay stage (Healthy: inert, Tainted: 50%, Moldy: 65%, Rotten: 85%).
- **Villager POI Parity**: Rotting workstations remain 100% valid Points of Interest for Villager professions.

**Mechanical Decay, Audio & Magic:**
- **Mechanical Jamming**: Rotten wooden buttons remain pressed for up to 22.5 seconds (450 ticks); rotten pressure plates stay active for up to 15 seconds (300 ticks). Trapped chests suffer a 15%, 50%, or 85% signal failure rate with an audible click misfire.
- **Structural Collapse on Use**: 10% chance for un-waxed Rotten interactive blocks (doors, trapdoors, fence gates, buttons) to break and vanish without dropping items upon interaction.
- **Bookshelf Decay**: Enchanting power scales down ($1.0 \rightarrow 0.66 \rightarrow 0.33 \rightarrow 0.0$), and book drops scale ($3 \rightarrow 2 \rightarrow 1 \rightarrow 0$) when broken without Silk Touch.
- **Chiseled Bookshelf Preservation**: Books inside Chiseled Bookshelves are 100% preserved through all decay stages and scatter safely upon breaking; comparator output (1..6) remains fully deterministic.
- **Acoustic Alterations**: Note blocks exhibit pitch distortion and emit spore particles; Jukeboxes play warped, sluggish vinyl music.

**Gear, Enchantments & Advancements:**
- **3D Spore Mask**: Custom 3D headpiece with respirator filters and visor (165 durability, 1 armor point), protecting against all miasma status effects. Repairable on anvils using Spore Filters.
  - *Enchanting Rules*: Compatible with Unbreaking I–III, Mending, and Curse of Vanishing via enchanted books on anvils. Rejects combat enchants.
- **Spore Filtration Enchantment**: Custom helmet enchantment (Tiers I–III) allowing standard vanilla helmets to filter miasma at the cost of helmet durability; Tier III grants a 50% chance to consume zero durability per cycle.
- **6 New Advancements (11 Total)**:
  - *Moisture Sensor* (Craft a Moisture Detector)
  - *Air Sentry* (Craft a Spore Detector)
  - *Climate Control* (Construct a Dehumidifier)
  - *Hermetic Bunker* (Construct an Air Purifier)
  - 🏆 *Dry Oasis* (Challenge: Dehumidify a deep underground room at $Y \le 40$ below 15% RH)
  - 🏆 *Pure Air in the Depths* (Challenge: Fully purify an underground room at $Y \le 0$ to `CLEAN`)

**Integrations, Configuration & Quality:**
- **7 Native JEI Categories**: Waxing, Scraping, Planks Recovery, Infection Cycle, Decay Mechanics & Crumbles, Dehumidifier, Air Purifier.
- **Jade / WTHIT Integration**: Contextual HUD displays decay stage, waxing status, real-time risk percentage ($R\%$), composter fill level, and machine energy/filter states.
- **19 Cloth Config Categories**: Complete in-game configuration interface via ModMenu (`config/spores_and_shadows.json`).
- **Diagnostic Commands**: Operator level 2 `/miasma` (telemetry, volume, permeability) and `/moldrisk` (mathematical risk decomposition) commands.
- **Comprehensive Testing Suite**: 287 automated tests (263 Fabric GameTests + 24 JUnit Unit Tests) passing at 100%.
- **Full 5-Language Localization**: English, Italian, German, Spanish, and French for all items, blocks, GUI overlays, tooltips, advancements, and JEI tabs.

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
