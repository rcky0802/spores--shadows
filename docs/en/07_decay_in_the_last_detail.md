# 🎭 Decay in the Last Detail

Mold does not stop at the exterior surface of blocks. It penetrates user interfaces, alters ambient acoustics, compromises the arcane power of bookshelves, and — surprisingly — never disrupts villagers in their daily trades.

---

## 🖥️ Visual Decay of Interfaces (GUI Overlays)

Opening an infected workstation reflects the block's physical deterioration directly onto your screen, utilizing native-resolution graphic overlays that seamlessly superimpose over the vanilla GUI:

- **Crafting Tables**: The 3×3 crafting grid is overrun with fungal spots and degraded joints.
- **Chests and Barrels**: The 9×3 and 9×6 storage slots display mossy, ragged borders.
- **Looms and Cartography Tables**: Stretched parchment and canvas absorb dampness along interface borders.
- **Lecterns**: Reading a book on an infected lectern casts an organic fungal overlay onto the margins of the pages themselves.

Chest rendering has been further refined: classic *Z-fighting* (texture flickering along the base-and-lid seam) is resolved via a millimeter scale adjustment that transitions smoothly during chest opening and closing animations.

## 🎶 Acoustic Alteration

Mold creeps into acoustic resonance chambers, deeply distorting the sound profile of musical blocks:

- **Note Blocks**: Striking a block produces dissonant, pitch-shifted dull thuds, emitting fungal particles instead of musical notes.
- **Jukeboxes**: Vanilla music discs play back at slowed speeds with warped, degraded pitch proportional to the decay stage. The unsettling effect is intentionally eerie — ideal for horror maps, ruins, and dungeon crawls.

## 📚 Bookshelf Decay: Magic and Drops

**Standard Bookshelves** undergo dual degradation as fungal infestation advances: they lose arcane power contribution to the Enchanting Table, and when broken without *Silk Touch*, they drop a decreasing number of books (as mold rots away parchment and binding).

| Stage | Enchanting Power per block | Books Dropped on Break *(Without Silk Touch)* | With Silk Touch |
| :---: | :---: | :---: | :---: |
| **0 — Clean** *(or Waxed Clean)* | **1.0** (full) | **3 books** *(Vanilla)* | Drops clean bookshelf |
| **1 — Tainted** | **0.66** | **2 books** | Drops tainted bookshelf |
| **2 — Moldy** | **0.33** | **1 book** | Drops moldy bookshelf |
| **3 — Rotten** | **0.0** *(no contribution)* | **0 books** *(rotted paper)* | Drops rotten bookshelf |

> [!NOTE]
> If waxed (*Waxed*), bookshelves freeze their current stage: they retain the book drops of that stage and provide full enchanting power (1.0) if preserved when clean. With *Silk Touch*, you always retrieve the respective bookshelf block (waxed or unwaxed) of that specific decay stage.

### 📖 Chiseled Bookshelves
The behavior of **Chiseled Bookshelves** is fundamentally different, designed to protect player investments:
- **Book Preservation**: Volumes stored in slots (regular books, written books, or enchanted tomes) remain 100% protected and unharmed across all stages of decay, including through the transition to Rotten or during waxing and dewaxing with an axe.
- **Breakage**: When the block is destroyed, all stored books spill safely onto the ground (`ItemScatterer`), alongside the chiseled bookshelf block itself.
- **Comparator**: The rear analog Redstone output (1..6 based on the last slot interacted with) remains completely faithful to Vanilla standards without jamming or distortion (see also [Chapter 6](06_automating_remediation.md)).

## 👨‍🌾 Villagers and Points of Interest (POIs)

Despite heavy visual decay, villagers never abandon their workstations. Fishermen, Farmers, Shepherds, Cartographers, Fletchers, and Librarians natively recognize Stage 3 blocks as valid profession workstations, claiming them, restocking trades, and interacting without any compatibility issues.

## 🚪 Use-Fragility of Functional Blocks

Rotten wood possesses zero structural integrity or hinge strength:
- **Break Chance on Use**: Whenever a player interacts with an unwaxed **Stage 3 (Rotten)** functional block — such as opening a **door**, flipping a **trapdoor**, opening a **fence gate**, or pressing a wooden **button** — there is a **10%** chance (`rotten_break_chance_on_use`) that the mechanism instantly buckles.
- In the event of failure, the block snaps with a sharp splintering sound (`BLOCK_WOOD_BREAK`) and is **destroyed without dropping anything**.
- **Solution**: Preemptively applying a honeycomb (**waxing**) locks the structure and prevents accidental collapse during operation.

---

| | |
| :--- | ---: |
| [← Automating Remediation](06_automating_remediation.md) | [Configuration and Mechanics →](08_configuration_and_mechanics.md) |
| [📑 Table of Contents](README.md) | |
