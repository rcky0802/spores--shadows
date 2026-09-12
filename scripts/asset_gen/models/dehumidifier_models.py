import os
import json
from ..common.paths import DEHUMIDIFIER_MODELS_DIR, BLOCKSTATES_DIR

def generate_dehumidifier_models_and_blockstates(models_out=None, blockstates_out=None):
    models_dir = models_out if models_out is not None else DEHUMIDIFIER_MODELS_DIR
    blockstates_dir = blockstates_out if blockstates_out is not None else BLOCKSTATES_DIR
    os.makedirs(models_dir, exist_ok=True)
    os.makedirs(blockstates_dir, exist_ok=True)

    statuses = ['off', 'running', 'full']
    water_levels = [0, 1, 2, 3, 4]
    facings = {
        'north': 0,
        'east': 90,
        'south': 180,
        'west': 270
    }

    # 1. Genera i 15 file di modello per blocco
    for status in statuses:
        for lvl in water_levels:
            model_name = f"dehumidifier_{status}_{lvl}"
            model_data = {
                "parent": "minecraft:block/cube",
                "textures": {
                    "particle": f"spores--shadows:block/dehumidifier/dehumidifier_front_{status}_{lvl}",
                    "down": "spores--shadows:block/dehumidifier/dehumidifier_bottom",
                    "up": "spores--shadows:block/dehumidifier/dehumidifier_top",
                    "north": f"spores--shadows:block/dehumidifier/dehumidifier_front_{status}_{lvl}",
                    "south": "spores--shadows:block/dehumidifier/dehumidifier_side",
                    "west": "spores--shadows:block/dehumidifier/dehumidifier_side",
                    "east": "spores--shadows:block/dehumidifier/dehumidifier_side"
                }
            }
            with open(os.path.join(models_dir, f"{model_name}.json"), 'w', encoding='utf-8') as f:
                json.dump(model_data, f, indent=2)

    # Modelli di fallback per retrocompatibilità
    with open(os.path.join(models_dir, "dehumidifier.json"), 'w', encoding='utf-8') as f:
        json.dump({
            "parent": "minecraft:block/cube",
            "textures": {
                "particle": "spores--shadows:block/dehumidifier/dehumidifier_front_off_0",
                "down": "spores--shadows:block/dehumidifier/dehumidifier_bottom",
                "up": "spores--shadows:block/dehumidifier/dehumidifier_top",
                "north": "spores--shadows:block/dehumidifier/dehumidifier_front_off_0",
                "south": "spores--shadows:block/dehumidifier/dehumidifier_side",
                "west": "spores--shadows:block/dehumidifier/dehumidifier_side",
                "east": "spores--shadows:block/dehumidifier/dehumidifier_side"
            }
        }, f, indent=2)

    with open(os.path.join(models_dir, "dehumidifier_on.json"), 'w', encoding='utf-8') as f:
        json.dump({
            "parent": "minecraft:block/cube",
            "textures": {
                "particle": "spores--shadows:block/dehumidifier/dehumidifier_front_running_0",
                "down": "spores--shadows:block/dehumidifier/dehumidifier_bottom",
                "up": "spores--shadows:block/dehumidifier/dehumidifier_top",
                "north": "spores--shadows:block/dehumidifier/dehumidifier_front_running_0",
                "south": "spores--shadows:block/dehumidifier/dehumidifier_side",
                "west": "spores--shadows:block/dehumidifier/dehumidifier_side",
                "east": "spores--shadows:block/dehumidifier/dehumidifier_side"
            }
        }, f, indent=2)

    # 2. Genera blockstates/dehumidifier.json con tutte le 60 combinazioni (facing, status, water_level)
    variants = {}
    for facing, y_rot in facings.items():
        for status in statuses:
            for lvl in water_levels:
                key = f"facing={facing},status={status},water_level={lvl}"
                val = {
                    "model": f"spores--shadows:block/dehumidifier/dehumidifier_{status}_{lvl}"
                }
                if y_rot != 0:
                    val["y"] = y_rot
                variants[key] = val

    blockstate_data = {
        "variants": variants
    }
    with open(os.path.join(blockstates_dir, "dehumidifier.json"), 'w', encoding='utf-8') as f:
        json.dump(blockstate_data, f, indent=2)

    print(f"[OK] Successfully generated all dehumidifier block models and blockstates in {models_dir}!")

if __name__ == '__main__':
    generate_dehumidifier_models_and_blockstates()
