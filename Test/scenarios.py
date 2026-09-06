"""
Modulo dedicato alla definizione, esportazione e importazione
robusta di scenari in formato TXT o CSV (supporto a tutti i tipi di blocchi e geometrie).
"""
import csv
import os
from constants import (
    SOLID, EMPTY, START,
    SLAB_BOTTOM, SLAB_TOP, STAIRS_NORTH, STAIRS_SOUTH, STAIRS_WEST, STAIRS_EAST,
    COPPER_GRATE, DOOR_OPEN, DOOR_CLOSED, TRAPDOOR_OPEN, TRAPDOOR_CLOSED, FENCE,
    FENCE_GATE_OPEN, FENCE_GATE_CLOSED, WALL_CONNECTED, LEAVES, IRON_BARS,
    PORTAL_12, PORTAL_6, PORTAL_3, PORTAL_AUTO_24,
    PORTAL_12_5, PORTAL_6_25, PORTAL_AUTO_25
)

TYPE_TO_TAG = {
    SOLID: "WALL",
    START: "START",
    SLAB_BOTTOM: "SLAB_BOTTOM",
    SLAB_TOP: "SLAB_TOP",
    STAIRS_NORTH: "STAIRS_NORTH",
    STAIRS_SOUTH: "STAIRS_SOUTH",
    STAIRS_WEST: "STAIRS_WEST",
    STAIRS_EAST: "STAIRS_EAST",
    COPPER_GRATE: "COPPER_GRATE",
    DOOR_OPEN: "DOOR_OPEN",
    DOOR_CLOSED: "DOOR_CLOSED",
    TRAPDOOR_OPEN: "TRAPDOOR_OPEN",
    TRAPDOOR_CLOSED: "TRAPDOOR_CLOSED",
    FENCE: "FENCE",
    FENCE_GATE_OPEN: "FENCE_GATE_OPEN",
    FENCE_GATE_CLOSED: "FENCE_GATE_CLOSED",
    WALL_CONNECTED: "WALL_CONNECTED",
    LEAVES: "LEAVES",
    IRON_BARS: "IRON_BARS",
    PORTAL_12: "PORTAL_12",
    PORTAL_6: "PORTAL_6",
    PORTAL_3: "PORTAL_3",
    PORTAL_AUTO_24: "PORTAL_AUTO_24",
}

TAG_TO_TYPE = {v: k for k, v in TYPE_TO_TAG.items()}
TAG_TO_TYPE["DOOR"] = DOOR_OPEN
TAG_TO_TYPE["TRAPDOOR"] = TRAPDOOR_OPEN
TAG_TO_TYPE["GRATE"] = COPPER_GRATE
TAG_TO_TYPE["STAIRS"] = STAIRS_NORTH
TAG_TO_TYPE["GATE"] = FENCE_GATE_OPEN
TAG_TO_TYPE["GATE_OPEN"] = FENCE_GATE_OPEN
TAG_TO_TYPE["GATE_CLOSED"] = FENCE_GATE_CLOSED
TAG_TO_TYPE["BARS"] = IRON_BARS
TAG_TO_TYPE["PORTAL_12_5"] = PORTAL_12
TAG_TO_TYPE["PORTAL_6_25"] = PORTAL_6
TAG_TO_TYPE["PORTAL_AUTO_25"] = PORTAL_AUTO_24


def export_scenario_to_file(world, filepath: str) -> None:
    """
    Esporta la configurazione dello scenario attuale in un file TXT o CSV.
    """
    with open(filepath, mode="w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["# VOXEL SCENARIO CONFIGURATION (1:1 with Java)"])
        writer.writerow(["TYPE", "X", "Y", "Z", "VALUE"])
        
        # Dimensioni
        writer.writerow(["DIMENSIONS", world.sx, world.sy, world.sz, ""])
        
        # Starts
        for sx, sy, sz in sorted(world.manual_starts):
            writer.writerow(["START", sx, sy, sz, ""])
        
        # Goal manuali
        for gx, gy, gz in sorted(world.manual_goals):
            writer.writerow(["GOAL", gx, gy, gz, ""])
            
        # Portali storici
        for (px, py, pz), p_type in sorted(world.portals.items()):
            writer.writerow(["PORTAL", px, py, pz, p_type])
            
        # Tutti i blocchi non vuoti
        for x in range(world.sx):
            for y in range(world.sy):
                for z in range(world.sz):
                    pos = (x, y, z)
                    if pos in world.manual_starts or pos in world.manual_goals or pos in world.portals:
                        continue
                    b_type = world.grid[pos]
                    if b_type != EMPTY:
                        tag = TYPE_TO_TAG.get(b_type, "WALL")
                        writer.writerow([tag, x, y, z, ""])


def import_scenario_from_file(world, filepath: str) -> None:
    """
    Importa e ricostruisce la configurazione dello scenario da un file TXT o CSV.
    """
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"Il file '{filepath}' non esiste.")

    content = ""
    for enc in ["utf-8-sig", "utf-8", "latin-1", "cp1252"]:
        try:
            with open(filepath, mode="r", encoding=enc) as f:
                content = f.read()
            break
        except Exception:
            continue

    if not content.strip():
        raise ValueError("Il file selezionato è vuoto.")

    lines = content.splitlines()
    parsed_rows = []
    
    for line in lines:
        line_str = line.strip()
        if not line_str or line_str.startswith("#"):
            continue
        
        if ";" in line_str:
            parts = [p.strip() for p in line_str.split(";")]
        elif "\t" in line_str:
            parts = [p.strip() for p in line_str.split("\t")]
        else:
            parts = [p.strip() for p in line_str.split(",")]
            
        if parts:
            parsed_rows.append(parts)

    if not parsed_rows:
        raise ValueError("Nessun dato valido trovato nel file.")

    # 1. Ricerca riga DIMENSIONS
    dim_row = next((r for r in parsed_rows if len(r) >= 4 and r[0].strip().upper() == "DIMENSIONS"), None)
    if dim_row:
        sx, sy, sz = int(dim_row[1]), int(dim_row[2]), int(dim_row[3])
    else:
        max_x = max_y = max_z = 0
        for r in parsed_rows:
            if len(r) >= 4 and r[0].strip().upper() in ("START", "GOAL", "PORTAL", "WALL") or r[0].strip().upper() in TAG_TO_TYPE:
                max_x = max(max_x, int(r[1]))
                max_y = max(max_y, int(r[2]))
                max_z = max(max_z, int(r[3]))
        sx, sy, sz = max(max_x + 1, world.sx), max(max_y + 1, world.sy), max(max_z + 1, world.sz)

    world.resize(sx, sy, sz)
    world.manual_starts.clear()
    world.manual_goals.clear()
    world.goals.clear()
    world.portals.clear()
    world.grid.fill(EMPTY)

    imported_starts = set()
    imported_goals = set()

    for row in parsed_rows:
        if len(row) < 4:
            continue
        tag = row[0].strip().upper()
        if tag in ("DIMENSIONS", "TYPE"):
            continue
        
        try:
            x, y, z = int(row[1]), int(row[2]), int(row[3])
        except ValueError:
            continue
            
        if 0 <= x < sx and 0 <= y < sy and 0 <= z < sz:
            if tag == "WALL":
                world.grid[x, y, z] = SOLID
            elif tag == "GOAL":
                imported_goals.add((x, y, z))
            elif tag == "PORTAL":
                p_type = int(row[4]) if len(row) > 4 and row[4].strip().isdigit() else PORTAL_12_5
                world.portals[(x, y, z)] = p_type
                world.grid[x, y, z] = p_type
            elif tag == "START":
                imported_starts.add((x, y, z))
            elif tag in TAG_TO_TYPE:
                world.grid[x, y, z] = TAG_TO_TYPE[tag]

    if len(imported_starts) > 20 and imported_goals:
        world.manual_starts = set(imported_goals)
    elif imported_starts:
        world.manual_starts = set(imported_starts)
        world.manual_goals = set(imported_goals)
    elif imported_goals:
        world.manual_starts = set(imported_goals)

    for s in world.manual_starts:
        world.grid[s] = START

    world.reset_flow_network()
