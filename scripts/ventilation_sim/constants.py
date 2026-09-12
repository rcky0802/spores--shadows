# =============================================================================
# CONSTANTS & CONFIGURATION (1:1 with Java Minecraft Engine)
# =============================================================================
#
# [JAVA MINECRAFT MAPPING]:
# - EMPTY          <-> Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR
# - SOLID          <-> Blocks.STONE, Blocks.DIRT, Blocks.OAK_PLANKS, etc. (state.isSolidRender())
# - SLAB_BOTTOM    <-> SlabBlock con (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM)
# - SLAB_TOP       <-> SlabBlock con (state.getValue(SlabBlock.TYPE) == SlabType.TOP)
# - STAIRS_*       <-> StairBlock con state.getValue(StairBlock.FACING)
# - COPPER_GRATE   <-> Blocks.COPPER_GRATE (1.21+) o Iron Bars
# - DOOR_OPEN      <-> DoorBlock con (state.getValue(DoorBlock.OPEN) == true)
# - DOOR_CLOSED    <-> DoorBlock con (state.getValue(DoorBlock.OPEN) == false)
# - TRAPDOOR_OPEN  <-> TrapDoorBlock con (state.getValue(TrapDoorBlock.OPEN) == true)
# - TRAPDOOR_CLOSED<-> TrapDoorBlock con (state.getValue(TrapDoorBlock.OPEN) == false)
# - FENCE          <-> FenceBlock, WallBlock
# =============================================================================

# Tipi Voxel Fisici
EMPTY = 0
SOLID = 1
START = 2

# Mezzi Blocchi e Micro-Geometrie
SLAB_BOTTOM = 3        # 2/4 di blocco (12.0)
SLAB_TOP = 4           # 2/4 di blocco (12.0)
STAIRS_NORTH = 5       # 1/4 di blocco (6.0)
STAIRS_SOUTH = 6       # 1/4 di blocco (6.0)
STAIRS_WEST = 7        # 1/4 di blocco (6.0)
STAIRS_EAST = 8        # 1/4 di blocco (6.0)
COPPER_GRATE = 9       # 3/4 di blocco (18.0)
DOOR_OPEN = 10         # 3/4 di blocco (18.0)
DOOR_CLOSED = 11       # Ermetico (0.0)
TRAPDOOR_OPEN = 12     # 3/4 di blocco (18.0)
TRAPDOOR_CLOSED = 13   # Ermetico (0.0)
FENCE = 14             # 1/3 di blocco (8.0 - include Fence, Muretto singolo)
FENCE_GATE_OPEN = 15   # 3/4 di blocco (18.0 - Cancelletto aperto)
FENCE_GATE_CLOSED = 16 # 1/3 di blocco (8.0 - Cancelletto chiuso, come fence)
WALL_CONNECTED = 17    # Ermetico (0.0 - Muretto connesso a destra e sinistra)
LEAVES = 18            # 3/4 di blocco (18.0 - Foglie)
IRON_BARS = 19         # 1/3 di blocco (8.0 - Sbarre di ferro)

# Portali di compatibilità (Divisori di 24: 12.0, 6.0, 3.0, 24.0)
PORTAL_12 = 21
PORTAL_6 = 22
PORTAL_3 = 23
PORTAL_AUTO_24 = 24

# Alias storici
PORTAL_12_5 = PORTAL_12
PORTAL_6_25 = PORTAL_6
PORTAL_AUTO_25 = PORTAL_AUTO_24


# Valori di Banda Passante Interna del Voxel (Node Capacity) - Base 24.0
BASE_UNIT_CAPACITY = 24.0

INTERNAL_CAPACITY_MAP = {
    EMPTY: 24.0,              # 1/1 = 24.0 (Aria libera)
    SOLID: 0.0,               # Solido impermeabile
    START: float('inf'),      # Emettitore puro
    
    # 2/4 di blocco (12.0)
    SLAB_BOTTOM: 12.0,
    SLAB_TOP: 12.0,
    PORTAL_12: 12.0,
    PORTAL_12_5: 12.0,

    # 1/4 di blocco (6.0)
    STAIRS_NORTH: 6.0,
    STAIRS_SOUTH: 6.0,
    STAIRS_WEST: 6.0,
    STAIRS_EAST: 6.0,
    PORTAL_6: 6.0,
    PORTAL_6_25: 6.0,

    # 1/3 di blocco (8.0)
    FENCE: 8.0,
    IRON_BARS: 8.0,
    FENCE_GATE_CLOSED: 8.0,

    # 3/4 di blocco (18.0)
    COPPER_GRATE: 18.0,
    DOOR_OPEN: 18.0,
    TRAPDOOR_OPEN: 18.0,
    FENCE_GATE_OPEN: 18.0,
    LEAVES: 18.0,

    # Ermetici (0.0)
    DOOR_CLOSED: 0.0,
    TRAPDOOR_CLOSED: 0.0,
    WALL_CONNECTED: 0.0,

    # Portali legacy
    PORTAL_3: 3.0,
    PORTAL_AUTO_24: 24.0,
    PORTAL_AUTO_25: 24.0,
}

GOAL_BASE_VALUE = 24.0
VENTILATION_DISTANCE_ALPHA = 0.25

# 6 Direzioni 3D: (dx, dy, dz) dove z è l'asse verticale (UP / DOWN)
# Notazione: NORTH (0, -1, 0), SOUTH (0, 1, 0), WEST (-1, 0, 0), EAST (1, 0, 0), UP (0, 0, 1), DOWN (0, 0, -1)
DIR_NORTH = (0, -1, 0)
DIR_SOUTH = (0, 1, 0)
DIR_WEST = (-1, 0, 0)
DIR_EAST = (1, 0, 0)
DIR_UP = (0, 0, 1)
DIR_DOWN = (0, 0, -1)

DIRECTIONS_6 = [
    DIR_NORTH,
    DIR_SOUTH,
    DIR_WEST,
    DIR_EAST,
    DIR_UP,
    DIR_DOWN
]

OPPOSITE_DIR = {
    DIR_NORTH: DIR_SOUTH,
    DIR_SOUTH: DIR_NORTH,
    DIR_WEST: DIR_EAST,
    DIR_EAST: DIR_WEST,
    DIR_UP: DIR_DOWN,
    DIR_DOWN: DIR_UP
}