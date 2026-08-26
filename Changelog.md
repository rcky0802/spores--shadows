# Changelog

All notable changes to **Spores & Shadows** will be documented in this file.

## [1.1.0] - Quality of Life, Jade Integration & Biome Overhaul
**Added:**
- Jade / WTHIT Integration: The HUD now shows the exact decay stage, infection risk (%), and waxed status of any looked-at wood block.
- Jade HUD percentage is now dynamically aligned with the `/moldrisk` command and features dynamic coloring (Grey for Safe, Red for At Risk).
- Added full decay support for Nether wood types (Crimson and Warped), including all their block variants (planks, stairs, doors, etc).
- 5 new Advancements to guide players through the mod's mechanics: *Spores & Shadows*, *Natural Prevention*, *Elbow Grease*, *Short Breath*, *Dust to Dust*.
- Advancements and Jade tooltips are fully localized in English, Italian, Spanish, German, and French via DataGen.
- Stage 3 (Rotten) blocks emit slime sounds when walked on or broken.

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
