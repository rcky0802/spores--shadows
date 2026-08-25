# Changelog

All notable changes to **Spores & Shadows** will be documented in this file.

## [1.1.0] - (Unreleased)
**Added:**
- Visual spore particles emitted from exposed faces of Moldy and Rotten blocks.
- "Toxic Miasma" mechanic: area around the player inflicts Nausea and Poison based on local mold density.
- New "Toxicity" category in the configuration menu.
- Dedicated configuration option for the water scan radius.

**Changed/Optimized:**
- Massive refactoring of the linguistic DataGen system (via `AbstractModLanguageProvider`).
- Extreme optimizations on environmental scans (Water and Miasma) using `BlockPos.Mutable`, early-exits, and tick staggering.

---

## [1.0.0] - Initial Release
**Added:**
- Complete wood decay ecosystem in 4 stages: Healthy, Tainted, Moldy, Rotten.
- Decaying variants for 8 wood types (including stairs, slabs, doors, fences, etc).
- "Waxing" mechanic using honeycomb to freeze decay.
- Penalties to drops, crafting yields, and furnace fuel for degraded blocks.
- Integration with the Vanilla Composter.
- Natural spreading and climate/altitude logic for decay generation.
- In-game configuration interface via Cloth Config and ModMenu.
- Localization and documentation in 5 languages (EN, IT, ES, FR, DE) via DataGen.
- Official mod logo and VS Code build tasks.
