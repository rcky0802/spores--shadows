# ⚙️ Automating Remediation

Handheld detectors and manual waxing cover everyday survival needs. For large settlements, deep underground infrastructure, or scenarios where the player cannot always be present, the mod provides a full-fledged automation suite: permanent sensors, industrial climate machinery, and Redstone circuitry.

---

## 📡 Permanent Redstone Sensors (Hybrid System)

Both detectors can be mounted in any orientation on solid surfaces (**floor, wall, or ceiling**). In addition to visual readouts, they incorporate a **hybrid Redstone system** (Direct Power Emission + Comparator Output):

**💧 Moisture Detector (Placed)** — Continuously monitors effective humidity $H_{eff}$ and the local microclimate:
- **Visual Dial**: 4 graduated stages (0 = Dry, 1 = Moderate, 2 = Humid, 3 = Critical).
- **Direct Signal**: Emits Redstone power proportional to the moisture stage (**0, 5, 10, 15**), directly energizing adjacent dust, indicator lamps, machinery, or the mounting wall block (allowing completely concealed wiring behind walls).
- **Comparator Support**: Any adjacent Comparator natively reads the identical signal level (0, 5, 10, 15) for analog threshold logic.
- **Inspection**: Right-clicking the block (with an empty hand or tool) clicks mechanically and prints the complete diagnostic report to private chat.

**☢️ Spore Detector (Placed)** — Periodically analyzes the BFS volumetric airspace of the room:
- **Visual Dial**: 4 toxic warning stages (0 = Clean, 1 = Warning, 2 = Hunger, 3 = Lethal Poison).
- **Direct Signal**: Emits scalar Redstone power (**0, 5, 10, 15**), directly starting Dehumidifiers and Air Purifiers as soon as miasma begins accumulating, with no repeaters or logic gates required.
- **Comparator Support**: Fully compatible with Comparators for automated threshold alarms and emergency ventilation circuits.
- **Inspection**: Right-clicking clicks with a metallic snap and transmits room volume telemetry and miasma trend (accumulating, stable, or purifying) to chat.

## 💣 Redstone Mechanics: Trapped Chests and Jamming

Encroaching mold also compromises the internal Redstone mechanisms built into wooden components.

**Wooden Buttons** — Fungal hyphae choke the return spring, greatly extending how long the button remains depressed:
- Stage 0 (Clean / Waxed Clean): **1.5 seconds** (30 ticks — Vanilla standard)
- Stage 1 (Tainted): **3.0 seconds** (60 ticks)
- Stage 2 (Moldy): **7.5 seconds** (150 ticks)
- Stage 3 (Rotten): **22.5 seconds** (450 ticks) — completely breaks precise redstone timing

**Wooden Pressure Plates** — Fungal biomass dampens plate recoil after an entity steps off:
- Stage 0 (Clean / Waxed Clean): **1.0 second** (20 ticks — Vanilla standard)
- Stage 1 (Tainted): **2.0 seconds** (40 ticks)
- Stage 2 (Moldy): **5.0 seconds** (100 ticks)
- Stage 3 (Rotten): **15.0 seconds** (300 ticks) — the output signal lingers for 15 full seconds

**Trapped Chests — Jamming** — Rust and fungal rot oxidize the internal hinge contacts. On each opening, there is an escalating chance of a *jamming misfire* (the chest clicks blankly without emitting any Redstone signal):
- Stage 1 (Tainted): **15%** jamming chance
- Stage 2 (Moldy): **50%** jamming chance
- Stage 3 (Rotten): **85%** jamming chance

The mechanical jamming state is intentionally concealed from external HUDs (Jade/WTHIT) to preserve gameplay surprise.

**Chiseled Bookshelves and Comparators** — Books stored inside remain completely unharmed across all stages of decay. The analog Redstone signal emitted by a rear Comparator (from 1 to 6 based on the last slot interacted with) remains 100% deterministic and true to Vanilla behavior, with zero signal loss.

---

## 🌀 Dehumidifier

The cornerstone machine for active atmospheric humidity control. While the Air Purifier works downstream by neutralizing existing miasma, the Dehumidifier acts upstream, preventing mold germination and growth by drying room air.

- **Drying Power (1.0)** — Each active Dehumidifier applies **1.0 point** of drying power to the room's hygrometric algorithm:
  $$H_{\text{target}} = \max(0.0, \, H_{\text{raw}} - 1.0 \times N_{\text{dehumidifiers}})$$
  Because natural raw humidity $H_{\text{raw}}$ ranges between $0.0$ and $1.0$, a single active dehumidifier is sufficient to drive effective humidity $H_{\text{eff}}$ down to **$0.0$ ($0\%$)** in any sealed room up to $2048\text{ m}^3$, transforming the space into a dry desert sanctuary immune to biological rot.
- **Hybrid Dual-Power (32,000 FE Buffer / 4.0× Fuel Multiplier)** — Consumes **10 FE/tick** while actively running ($0\text{ FE/tick}$ while in standby/off). Supports power cables (accepting up to 500 FE/t on any face) or solid fuel placed in Slot 0 with quadrupled burn efficiency ($4.0\times$, a single piece of coal produces $64,000\text{ FE}$, saturating the entire internal buffer).
- **Condensate Tank & Operating Modes (2000 mB)** — Equipped with an internal fluid tank:
  - **Dehumidify Mode**: Actively extracts moisture from ambient air and collects condensate at **1 mB per 24 ticks** base (condensing faster in damp rooms).
  - **Full Tank Standby (FULL)**: Upon reaching the 2000 mB limit (2 water buckets), the unit pauses into standby, preventing any waste of power or fuel.
  - **Humidify Mode (Vaporizing/Nebulization)**: Reverses polarity; consumes water from the tank (1 mB per 24 ticks) to disperse vapor into dry air ($+1.0$ humidity). Automatically shuts off if room humidity reaches 98% ($0.98$). Ideal for automated underground mushroom farms.
- **Hopper & Fluid Automation (SidedInventory & Fluid Transfer)**:
  - **Solid Fuel**: Hoppers can insert fuel from **any face** into Slot 0.
  - **Water Extraction/Input**: Supports manual bucket interaction (1000 mB per right-click) and automated fluid pipe connections via the *Fabric Transfer API*. Pipes dynamically adapt to active mode (extracting in dehumidify, inserting in humidify).
- **Interface, Redstone and Comparator**:
  - **Redstone Control**: Three-state toggle (*Always Active* `IGNORED`, *Active with Signal* `HIGH`, *Active without Signal* `LOW`). Paired with a wall-mounted Moisture Detector, it turns on only when humidity exceeds a safe threshold.
  - **Comparator Output**: Emits an analog signal from **$0$ to $15$** proportional to internal water tank volume (0 when empty, 15 when full at 2000 mB).

---

## 🌬️ Air Purifier

The cornerstone machine for active atmospheric toxicity decontamination. While the Dehumidifier prevents infection by drying air, the Purifier neutralizes airborne miasma spores in sealed rooms that are already contaminated.

- **Purifying Power (48.0)** — Each active Air Purifier subtracts **48.0 points** from the room's volumetric toxic load:
  $$\text{targetMiasma} = \max(0.0, \, \text{toxicScore} - \text{ventilationScore} - 48.0 \times N_{\text{purifiers}})$$
  Since a single moldy log generates approximately $2.25$ toxicity points, one running purifier neutralizes the exhalations of **over 21 infected blocks simultaneously**, clearing miasma in sealed rooms up to $2048\text{ m}^3$.
- **Hybrid Dual-Power (32,000 FE Buffer / 4.0× Fuel Multiplier)** — Consumes **10 FE/tick** during active filtration ($0\text{ FE/tick}$ while in standby/filter depleted). Supports electric cables (up to 500 FE/t on any face) or solid fuel in Slot 0 with quadrupled burn yield ($4.0\times$, 1 coal delivers $64,000\text{ FE}$, filling the entire buffer).
- **Spore Filter Cartridges & Dynamic Wear (Spore Filters)** — Loaded into Slot 1 (stackable up to 64 units):
  - **Base Durability**: **2400 ticks (2 continuous minutes)** per filter at 1 point/tick.
  - **Auto-Reload**: When the active filter wears out, the unit automatically draws the next cartridge from the reserve stack.
  - **Accelerated Wear under Lethal Miasma**: In rooms with critical *LETHAL_POISON* miasma, wear doubles to **2 points/tick** (60 seconds per filter) due to microspore overload.
  - **Depletion Alarm (FILTER_DEPLETED)**: When filters run out, the machine shuts off its filtration grid and emits a mechanical click (`BLOCK_DISPENSER_FAIL`).
- **Hopper Automation (SidedInventory)**:
  - **Top Face (UP)**: Hoppers insert *exclusively* **Spore Filters** (Slot 1).
  - **Side and Bottom Faces (NORTH, SOUTH, EAST, WEST, DOWN)**: Accept *exclusively* **Solid Fuel** (Slot 0).
- **Interface, Redstone and Comparator**:
  - **Redstone Control**: Three-state toggle (*Always Active* `IGNORED`, *Active with Signal* `HIGH`, *Active without Signal* `LOW`). Connected to a wall Spore Detector, it turns on automatically only when miasma builds up, eliminating filter waste once the room is cleansed.
  - **Comparator Output**: Emits an analog signal from **$0$ to $15$** proportional to remaining filter stock in the slot (0 if empty, 15 for a full stack of 64), ideal for triggering low-filter warning lamps.

---

| | |
| :--- | ---: |
| [← Defending Yourself](05_defending_yourself.md) | [Decay in the Last Detail →](07_decay_in_the_last_detail.md) |
| [📑 Table of Contents](README.md) | |
