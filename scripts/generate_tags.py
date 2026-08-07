import os
import json

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
tags_dir = os.path.join(project_root, 'src', 'main', 'resources', 'data', 'spores--shadows', 'tags', 'blocks')
os.makedirs(tags_dir, exist_ok=True)

mc_tags_dir = os.path.join(project_root, 'src', 'main', 'resources', 'data', 'minecraft', 'tags', 'blocks')
os.makedirs(mc_tags_dir, exist_ok=True)

WOODS = ["oak"]

def write_tag(path, values):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump({
            "replace": False,
            "values": values
        }, f, indent=2)

def main():
    axe_blocks = []
    logs = []
    planks = []
    stairs = []
    slabs = []
    fences = []
    fence_gates = []
    doors = []
    trapdoors = []

    for w in WOODS:
        for prefix in [w]:
            blocks = [
                f"moldy_{prefix}_log", f"moldy_stripped_{prefix}_log",
                f"moldy_{prefix}_wood", f"moldy_stripped_{prefix}_wood",
                f"moldy_{prefix}_planks", f"moldy_{prefix}_stairs",
                f"moldy_{prefix}_slab", f"moldy_{prefix}_fence",
                f"moldy_{prefix}_fence_gate", f"moldy_{prefix}_door",
                f"moldy_{prefix}_trapdoor"
            ]
            
            for b in blocks:
                id = f"spores--shadows:{b}"
                if "rotten" not in b:
                    axe_blocks.append(id)
                if "log" in b or "wood" in b: logs.append(id)
                if "planks" in b: planks.append(id)
                if "stairs" in b: stairs.append(id)
                if "slab" in b: slabs.append(id)
                if "fence" in b and "gate" not in b: fences.append(id)
                if "gate" in b: fence_gates.append(id)
                if "door" in b and "trap" not in b: doors.append(id)
                if "trapdoor" in b: trapdoors.append(id)

    # Minecraft Block Tags
    write_tag(os.path.join(mc_tags_dir, 'mineable', 'axe.json'), axe_blocks)
    write_tag(os.path.join(mc_tags_dir, 'logs.json'), logs)
    write_tag(os.path.join(mc_tags_dir, 'planks.json'), planks)
    write_tag(os.path.join(mc_tags_dir, 'wooden_stairs.json'), stairs)
    write_tag(os.path.join(mc_tags_dir, 'wooden_slabs.json'), slabs)
    write_tag(os.path.join(mc_tags_dir, 'wooden_fences.json'), fences)
    write_tag(os.path.join(mc_tags_dir, 'fence_gates.json'), fence_gates)
    write_tag(os.path.join(mc_tags_dir, 'wooden_doors.json'), doors)
    write_tag(os.path.join(mc_tags_dir, 'wooden_trapdoors.json'), trapdoors)

    # Item Tags (ONLY WAXED)
    item_tags_dir = os.path.join(project_root, 'src', 'main', 'resources', 'data', 'minecraft', 'tags', 'items')
    waxed_logs = [f"spores--shadows:waxed_{w}_log" for w in WOODS] + [f"spores--shadows:waxed_stripped_{w}_log" for w in WOODS] + [f"spores--shadows:waxed_{w}_wood" for w in WOODS] + [f"spores--shadows:waxed_stripped_{w}_wood" for w in WOODS]
    waxed_planks = [f"spores--shadows:waxed_{w}_planks" for w in WOODS]
    waxed_stairs = [f"spores--shadows:waxed_{w}_stairs" for w in WOODS]
    waxed_slabs = [f"spores--shadows:waxed_{w}_slab" for w in WOODS]
    waxed_fences = [f"spores--shadows:waxed_{w}_fence" for w in WOODS]
    waxed_fence_gates = [f"spores--shadows:waxed_{w}_fence_gate" for w in WOODS]
    waxed_doors = [f"spores--shadows:waxed_{w}_door" for w in WOODS]
    waxed_trapdoors = [f"spores--shadows:waxed_{w}_trapdoor" for w in WOODS]

    write_tag(os.path.join(item_tags_dir, 'logs.json'), waxed_logs)
    write_tag(os.path.join(item_tags_dir, 'planks.json'), waxed_planks)
    write_tag(os.path.join(item_tags_dir, 'wooden_stairs.json'), waxed_stairs)
    write_tag(os.path.join(item_tags_dir, 'wooden_slabs.json'), waxed_slabs)
    write_tag(os.path.join(item_tags_dir, 'wooden_fences.json'), waxed_fences)
    write_tag(os.path.join(item_tags_dir, 'fence_gates.json'), waxed_fence_gates)
    write_tag(os.path.join(item_tags_dir, 'wooden_doors.json'), waxed_doors)
    write_tag(os.path.join(item_tags_dir, 'wooden_trapdoors.json'), waxed_trapdoors)

    # Custom Mod Tags
    moldy_blocks = []
    moldy_items = []
    
    for w in WOODS:
        bases = [
            f"{w}_log", f"stripped_{w}_log",
            f"{w}_wood", f"stripped_{w}_wood",
            f"{w}_planks", f"{w}_stairs",
            f"{w}_slab", f"{w}_fence",
            f"{w}_fence_gate", f"{w}_door",
            f"{w}_trapdoor"
        ]
        
        for base in bases:
            # Blocks are just moldy_*, items are tainted_*, moldy_*, rotten_*
            moldy_blocks.append(f"spores--shadows:moldy_{base}")
            
            moldy_items.append(f"spores--shadows:tainted_{base}")
            moldy_items.append(f"spores--shadows:moldy_{base}")
            moldy_items.append(f"spores--shadows:rotten_{base}")

    write_tag(os.path.join(tags_dir, 'moldy_blocks.json'), moldy_blocks)
    
    mod_item_tags_dir = os.path.join(project_root, 'src', 'main', 'resources', 'data', 'spores--shadows', 'tags', 'items')
    write_tag(os.path.join(mod_item_tags_dir, 'moldy_items.json'), moldy_items)


    print("Tags generated successfully!")

if __name__ == "__main__":
    main()
