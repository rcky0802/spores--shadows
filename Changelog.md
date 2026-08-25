# Changelog

All notable changes to **Spores & Shadows** will be documented in this file.

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
