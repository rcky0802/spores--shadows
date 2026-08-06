import os
import json
import re

BLOCK_MODELS_DIR = "../src/main/resources/assets/spores--shadows/models/block"
ITEM_MODELS_DIR = "../src/main/resources/assets/spores--shadows/models/item"

def refactor_block_models():
    if not os.path.exists(BLOCK_MODELS_DIR): return
    for filename in os.listdir(BLOCK_MODELS_DIR):
        if not filename.endswith(".json"): continue
        path = os.path.join(BLOCK_MODELS_DIR, filename)
        
        with open(path, "r") as f:
            data = json.load(f)
            
        changed = False
        if "textures" in data:
            for k, v in data["textures"].items():
                # Replace "spores--shadows:block/moldy_oak_planks_stage_2" with "minecraft:block/oak_planks"
                if isinstance(v, str) and v.startswith("spores--shadows:block/moldy_"):
                    # Extract the base texture name by removing 'moldy_' and '_stage_X'
                    base_tex = v.replace("spores--shadows:block/moldy_", "")
                    base_tex = re.sub(r'_stage_\d', '', base_tex)
                    # Replace stripped_ prefix if needed? Vanilla is stripped_oak_log
                    data["textures"][k] = f"minecraft:block/{base_tex}"
                    changed = True
                    
                elif isinstance(v, str) and v.startswith("spores--shadows:block/rotten_"):
                    base_tex = v.replace("spores--shadows:block/rotten_", "")
                    base_tex = re.sub(r'_stage_\d', '', base_tex)
                    data["textures"][k] = f"minecraft:block/{base_tex}"
                    changed = True
                    
                elif isinstance(v, str) and v.startswith("spores--shadows:block/tainted_"):
                    base_tex = v.replace("spores--shadows:block/tainted_", "")
                    base_tex = re.sub(r'_stage_\d', '', base_tex)
                    data["textures"][k] = f"minecraft:block/{base_tex}"
                    changed = True

        if changed:
            with open(path, "w") as f:
                json.dump(data, f, indent=2)
            print(f"Aggiornato block model: {filename}")

def refactor_item_models():
    if not os.path.exists(ITEM_MODELS_DIR): return
    for filename in os.listdir(ITEM_MODELS_DIR):
        if not filename.endswith(".json"): continue
        path = os.path.join(ITEM_MODELS_DIR, filename)
        
        with open(path, "r") as f:
            data = json.load(f)
            
        changed = False
        if data.get("parent") == "minecraft:item/generated" and "textures" in data:
            layer0 = data["textures"].get("layer0", "")
            if layer0.startswith("spores--shadows:item/moldy_"):
                # E.g. moldy_oak_door_stage_2
                match = re.search(r'moldy_(.*)_door_stage_(\d)', layer0)
                if match:
                    wood = match.group(1)
                    stage = match.group(2)
                    data["textures"]["layer0"] = f"minecraft:item/{wood}_door"
                    data["textures"]["layer1"] = f"spores--shadows:block/mold_stage_{stage}"
                    changed = True
            elif layer0.startswith("spores--shadows:item/rotten_"):
                match = re.search(r'rotten_(.*)_door', layer0)
                if match:
                    wood = match.group(1)
                    data["textures"]["layer0"] = f"minecraft:item/{wood}_door"
                    data["textures"]["layer1"] = f"spores--shadows:block/mold_stage_3"
                    changed = True
            elif layer0.startswith("spores--shadows:item/tainted_"):
                match = re.search(r'tainted_(.*)_door', layer0)
                if match:
                    wood = match.group(1)
                    data["textures"]["layer0"] = f"minecraft:item/{wood}_door"
                    data["textures"]["layer1"] = f"spores--shadows:block/mold_stage_1"
                    changed = True
                    
        if changed:
            with open(path, "w") as f:
                json.dump(data, f, indent=2)
            print(f"Aggiornato item model: {filename}")

if __name__ == "__main__":
    refactor_block_models()
    refactor_item_models()
