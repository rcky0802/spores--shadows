import json
import os

en_us_path = "src/main/resources/assets/spores--shadows/lang/en_us.json"
it_it_path = "src/main/resources/assets/spores--shadows/lang/it_it.json"

WOODS = ["oak"]
blocks_en = [
    ("log", "Log"),
    ("stripped_log", "Stripped Log"),
    ("wood", "Wood"),
    ("stripped_wood", "Stripped Wood"),
    ("planks", "Planks"),
    ("stairs", "Stairs"),
    ("slab", "Slab"),
    ("fence", "Fence"),
    ("fence_gate", "Fence Gate"),
    ("door", "Door"),
    ("trapdoor", "Trapdoor"),
    ("button", "Button"),
    ("pressure_plate", "Pressure Plate")
]

blocks_it = [
    ("log", "Tronco di"),
    ("stripped_log", "Tronco scortecciato di"),
    ("wood", "Legno di"),
    ("stripped_wood", "Legno scortecciato di"),
    ("planks", "Assi di"),
    ("stairs", "Scale di"),
    ("slab", "Lastra di"),
    ("fence", "Staccionata di"),
    ("fence_gate", "Cancelletto di"),
    ("door", "Porta di"),
    ("trapdoor", "Botola di"),
    ("button", "Pulsante di"),
    ("pressure_plate", "Pedana a Pressione di")
]

def update_lang_file(path, is_it):
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8') as f:
            lang = json.load(f)
    else:
        lang = {}

    blocks = blocks_it if is_it else blocks_en

    for w in WOODS:
        w_cap = "Quercia" if is_it else w.capitalize()
        for b_id, b_name in blocks:
            # Format the identifier to match ModBlocks.java ("stripped_oak_log" instead of "oak_stripped_log")
            if b_id.startswith("stripped_"):
                suffix = b_id.replace("stripped_", "")
                b_key = f"stripped_{w}_{suffix}"
            else:
                b_key = f"{w}_{b_id}"
                
            if is_it:
                lang[f"item.spores--shadows.waxed_{b_key}"] = f"{b_name} {w_cap} (Cerato)"
                lang[f"item.spores--shadows.tainted_{b_key}"] = f"{b_name} {w_cap} (Intaccato)"
                lang[f"item.spores--shadows.moldy_{b_key}"] = f"{b_name} {w_cap} (Ammuffito)"
                lang[f"item.spores--shadows.rotten_{b_key}"] = f"{b_name} {w_cap} (Marcio)"
                lang[f"block.spores--shadows.waxed_{b_key}"] = f"{b_name} {w_cap} (Cerato)"
                lang[f"block.spores--shadows.tainted_{b_key}"] = f"{b_name} {w_cap} (Intaccato)"
                lang[f"block.spores--shadows.moldy_{b_key}"] = f"{b_name} {w_cap} (Ammuffito)"
                lang[f"block.spores--shadows.rotten_{b_key}"] = f"{b_name} {w_cap} (Marcio)"
            else:
                lang[f"item.spores--shadows.waxed_{b_key}"] = f"Waxed {w_cap} {b_name}"
                lang[f"item.spores--shadows.tainted_{b_key}"] = f"Tainted {w_cap} {b_name}"
                lang[f"item.spores--shadows.moldy_{b_key}"] = f"Moldy {w_cap} {b_name}"
                lang[f"item.spores--shadows.rotten_{b_key}"] = f"Rotten {w_cap} {b_name}"
                lang[f"block.spores--shadows.waxed_{b_key}"] = f"Waxed {w_cap} {b_name}"
                lang[f"block.spores--shadows.tainted_{b_key}"] = f"Tainted {w_cap} {b_name}"
                lang[f"block.spores--shadows.moldy_{b_key}"] = f"Moldy {w_cap} {b_name}"
                lang[f"block.spores--shadows.rotten_{b_key}"] = f"Rotten {w_cap} {b_name}"

    if is_it:
        lang["tooltip.spores--shadows.moldy_desc"] = "Intaccato dalla muffa e marcescente."
        lang["tooltip.spores--shadows.waxed"] = "Sigillato con cera: Immune alla muffa"
    else:
        lang["tooltip.spores--shadows.moldy_desc"] = "Infected by mold and rotting away."
        lang["tooltip.spores--shadows.waxed"] = "Sealed with wax: Immune to mold"

    with open(path, 'w', encoding='utf-8') as f:
        json.dump(lang, f, indent=2, ensure_ascii=False)

update_lang_file(en_path, False)
update_lang_file(it_path, True)

print("Updated en_us.json and it_it.json successfully!")
