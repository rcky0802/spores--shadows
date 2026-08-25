# Spores & Shadows 

**Spores & Shadows** is a Minecraft mod (Fabric 1.21.1) that introduces a dynamic, realistic, and unforgiving environmental decay ecosystem for wood. No structure is safe from time and the elements!

---

## 🌳 Overview and Content

Have you ever built a majestic wooden cabin thinking it would stand there untouched, defying the centuries without any need for maintenance? **Spores & Shadows** revolutionizes this certainty, transforming wood from a simple inert block into a living, vulnerable material that reacts to the surrounding environment.

The mod seamlessly and silently replaces every piece of wood placed by the player (or naturally generated in structures like shipwrecks and mineshafts) with a "dormant" variant. Over time, environmental factors such as rain, moisture, darkness, and even the biome you are in will determine the fate of your builds, forcing you to protect your structures or watch helplessly as they inevitably decay.

### 🔢 Technical Details and Added Blocks

On a technical level, the mod injects a complete ecosystem for every single wood variant.

* **🧱 13 Architectural Formats**: *Logs*, *Stripped Logs*, *Wood*, *Stripped Wood*, *Planks*, *Stairs*, *Slabs*, *Fences*, *Fence Gates*, *Doors*, *Trapdoors*, *Pressure Plates*, *Buttons*.

For each of the 104 base wood formats, the mod adds **3 moldy variants** (Tainted, Moldy, Rotten). Furthermore, for each of these blocks — including the original Vanilla block — their respective **waxed variant** is created.

This way, the game provides a whopping **728 unique variants obtainable in Survival**:
1. The **104 Waxed Vanilla Variants**: The protected and waxed copy of the base Vanilla block.
2. The **312 Moldy Variants**: The three natural stages of decay.
3. The **312 Waxed Moldy Variants**: The decayed blocks frozen in time by wax.

This system allows you to obtain partially moldy blocks in survival and then "seal" them with honeycomb, letting you safely use them for decorative purposes without the risk of infecting nearby structures.

---

## 🦠 The Mold Cycle

Wood goes through 4 stages of decay: **Vanilla (0) ➔ Tainted (1) ➔ Moldy (2) ➔ Rotten (3)**.

Progression only occurs if the "Infection Risk" (`R`), which is constantly recalculated, exceeds the fixed threshold of **0.4**. The exact formula is:
`R = ((Moisture * Light * Susceptibility) + Contagion) * Temperature`

### Exact Factors and Variables
* 💧 **Moisture (Climate + Depth + Water)**: 
  - The base value depends on the biome's precipitation (Rain/Snow: `0.8`, Dry: `0.3`). 
  - **Depth Malus**: Going below sea level (`Y < 64`), moisture drastically increases by `+0.01` for each block descended, making caves highly humid environments.
  - **Local Malus**: Adjacency to water blocks (`+0.15`) or cauldrons (`+0.10`) adds further moisture to the block.
* ☀️ **Light (UV)**: Scales linearly from `0.0` (Light level 15, completely halts infection) to `1.0` (Total darkness).
* 🪓 **Material Susceptibility**: Stripped wood is extremely vulnerable (`x1.4`), raw logs are standard (`x1.0`), while wood crafted into planks resists slightly more (`x0.8`).
* 🌡️ **Temperature (Biome + Altitude/Depth)**: 
  - Acts as a survival filter. Mold **only** thrives if the local temperature is between `0.15` and `1.5`.
  - **Surface Level**: Depends on the biome. Extreme climates like deserts or glaciers totally halt infection by locking the factor at `0.0`.
  - **Underground (`Y < 64`)**: Regardless of the surface biome, as you descend, the temperature gradually normalizes, stabilizing at the perfect value of `0.5` (mild) below `Y=48`. Even in a desert or ice biome, deep caves will grow mold!
  - **High Altitude (`Y > 128`)**: As you climb higher, the temperature gradually drops, freezing at `-0.5` at level `Y=256`. Building cabins high in the mountains will preserve wood almost anywhere.
* ☣️ **Contagion (Catalysts)**: Adds a direct malus if the wood is in contact with infectious agents:
  - Infected wood: Tainted (`+0.05`), Moldy (`+0.10`), Rotten (`+0.20`).
  - Environment: Mud (`+0.05`), Podzol/Mycelium (`+0.15`), Mushrooms (`+0.25`), Spore Blossom (`+0.80`).

## ☠️ Environmental Hazards (Miasma)

- ✨ **Spore Particles**: Blocks in the **Moldy** or **Rotten** stage emit spores from exposed faces (disabled underwater).
- 🤢 **Toxic Miasma**: The mod scans a 4-block radius around the player. Each block adds its decay stage to the total toxicity score:
  - **Tainted**: +1
  - **Moldy**: +2
  - **Rotten**: +3
  - *(Waxed blocks are safe and contribute 0).*
  
  **Effects**:
  - Score > **15**: **Nausea**
  - Score > **35**: **Nausea + Poison**

---

## 🛠️ Interactions and Prevention

The player is not defenseless against nature. By equipping the right tool and acting in **stealth mode (Sneaking / Shift)**, you can directly interact with the life state of the wood. 
*(Sneaking is mandatory to prevent accidentally waxing or scraping interactive blocks, like doors, trapdoors, or buttons).*

* 🪓 **Using an Axe (Scrape)**: By doing *Shift + Right Click* with an axe:
  - If the block is **Waxed**, the axe will remove the wax layer, restoring the normal life cycle.
  - If the block is **Tainted or Moldy**, the axe will scrape off the superficial layer of fungus, reducing the decay by 1 stage. A Stage 1 block will revert to being perfectly clean (Stage 0 Vanilla).
  *(Every scrape normally consumes durability).*
* 🐝 **Using Honeycomb (Waxing)**: By doing *Shift + Right Click* with a honeycomb on a block at *any* stage, it will become **Waxed**. Waxed wood is sealed: it becomes immune to environmental damage, freezes its decay forever, and loses the ability to infect nearby blocks or contribute to the Toxic Miasma. 

*Smart Feature: If you perform these actions on a multi-part block (like the upper or lower half of a Door), the update will be applied instantly and in total sync to the entire structure!*

---

## ⚖️ Penalties and Crafting

Using rotten wood for crafting is unwise. The internal structure of the material is irreversibly compromised, introducing severe penalties that punish laziness:

* 💥 **Structural Integrity (Drops) and Mining**:
  The preferred tool for mining these blocks remains the **Axe** (exactly as in Vanilla), with the only exception being Stage 3 blocks, which are so weak they have no associated tool (they crumble instantly even with bare hands).
  - Vanilla and **Tainted** blocks remain solid (they always drop at **100%**).
  - **Moldy** blocks are fragile: they only have a **50%** chance to drop themselves, otherwise they will shatter into nothingness.
  - **Rotten** blocks crumble instantly upon touch (**0%** drop chance).
  
  *(💡 **The Secret of Wax**: Waxing a block consolidates its structure. Any block from the mod, even Rotten ones, if **Waxed** will always have a **100% drop chance**, even without using Silk Touch!)*
* 🛠️ **Crafting Yield (Recovery)**:
  You can still use infected wood in a crafting table to craft basic items (like Planks, Slabs, Stairs, or Sticks). The final item will always be perfectly clean (**Vanilla**), but since you are forced to discard the rotten parts of the original wood, the quantity of items obtained will drop drastically:

  | Material Quality | 🌳 Ex: Log ➔ Planks | 🦯 Ex: Planks ➔ Sticks |
  | :--- | :---: | :---: |
  | 🌲 **Healthy (Vanilla)** | 1 Log ➔ **4** Planks | 2 Planks ➔ **4** Sticks |
  | 🟢 **Tainted** | 1 Log ➔ **2** Planks | 2 Planks ➔ **2** Sticks |
  | 🦠 **Moldy** | 1 Log ➔ **1** Plank | 2 Planks ➔ **1** Stick |
  | ☠️ **Rotten** | *Invalid Recipe* ❌ | *Invalid Recipe* ❌ |

* 🔥 **Fuel Power**: 
  - Tainted wood burns with halved efficiency (**50%**).
  - Moldy wood drops to a quarter of the efficiency (**25%**).
  - Rotten wood burns up in mere moments (**12.5%**), making it useless as fuel.
* ♻️ **Composter (The Bright Side of Rot)**:
  If a block is too rotten to build with, recycle it! All wood from the mod has been integrated with the Vanilla Composter to generate Bone Meal. The more degraded (and spore-rich) the wood is, the higher the chance of success:
  - Tainted Wood: **50%**
  - Moldy Wood: **65%**
  - Rotten Wood: **85%** (Excellent fertilizer!)
* 🔴 **Redstone Components (Buttons & Pressure Plates)**:
  Mold compromises the internal mechanisms of redstone components, causing them to get stuck and stay active for much longer. For example, a healthy wooden button stays active for 1.5 seconds (30 ticks), but as rot progresses:
  - Tainted: **3 seconds** (60 ticks).
  - Moldy: **7.5 seconds** (150 ticks).
  - Rotten: **22.5 seconds** (450 ticks).

*(💡 **Note on Waxed Blocks**: Wax is an environmental sealant, but it does not block the use of the item! You can use waxed blocks in the crafting table, burn them in the furnace, or toss them in the composter: they will behave exactly like their unwaxed counterpart, maintaining the exact same penalties or bonuses tied solely to their internal rot level).*

---

## 🗺️ Structure Generation

Mold is not limited to blocks placed by the player. The mod intercepts Minecraft's generation engine to apply the wear and tear of time to all wooden structures you will discover in the world. 

Structures are divided into 4 base levels of decay:
1. 🏴‍☠️ **Critical Decay** (High percentage of Rotten wood): Shipwrecks (`shipwreck`), Swamp Huts (`swamp_hut`).
2. 🧟 **High Decay** (Mix of Tainted and Moldy): Mineshafts (`mineshaft`), Zombie Villages (`zombie_village`), Trail Ruins (`trail_ruins`).
3. 🏹 **Moderate Decay** (Mostly Tainted): Pillager Outposts (`pillager_outpost`), Ruined Portals (`ruined_portal`).
4. 🏡 **Minimal Decay** (Almost completely healthy): Normal Villages (`village`), Woodland Mansions (`mansion`).

*(💡 **Dynamic Factors**: During generation, the code analyzes the environment block by block! If a wall of a shipwreck is exposed to air and sun, it will be more intact, while planks sunk into the seafloor or underground will be drastically more rotten).*

**🛡️ The Immunity of Natural Wood and Structures**:
To avoid ruining the gameplay experience (preventing players from finding the entire world already collapsed before they can explore it), there are two exceptions to automatic decay:
* **Native Trees**: Naturally generated trees (or those grown from saplings) do not generate mold because the wood is still "alive". Only wood chopped down and processed by the player begins to rot.
* **Suspended Structures**: Structures generate with the mold percentage indicated above, but then they "freeze". Structure blocks are natively immune to the progression of rot, unless the player interacts with them (e.g., breaking, scraping, or modifying them). This protection saves villages from spontaneous destruction. If you want a super-hardcore experience, you can disable structure immunity from the configuration menu!

---

## ⚙️ Mod Configuration
The mod includes a configuration menu accessible directly in-game (requires **Cloth Config** and **ModMenu**) that grants you absolute control over every single mechanic. 
Options are divided into 8 main categories:

* 🛠️ **General**: Disable mold growth globally, change the infection threshold, expand the environment scanning radius, or **disable structure immunity** to let villages spontaneously rot!
* 🌡️ **Environment**: Modify the base values for rain/dryness, bonuses for water adjacency, or customize at what altitudes and temperatures mold should freeze or thrive.
* 🪓 **Susceptibility**: Adjust how fast processed blocks (planks) rot compared to raw or stripped ones.
* ☣️ **Catalysts**: Balance the aggressiveness of mushrooms, mud, *spore blossoms*, and infected wood blocks themselves.
* ☠️ **Toxicity**: Customize the Toxic Miasma thresholds for Nausea and Poison, alter the scan radius, and change how long the status effects last.
* 🗺️ **Structures**: Customize in detail (percentage by percentage) how shipwrecks, villages, and mineshafts generate.
* 🔥 **Furnace Multipliers**: Modify the smelting efficiency of wood for the various stages of decay.
* 💥 **Drops**: Raise or lower the drop rate of fragile wood, if you find the mod too punishing.
