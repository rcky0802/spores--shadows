import os
import re

WOODS = ["spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"]

DIRS_TO_PROCESS = [
    "../src/main/resources/assets/spores--shadows/blockstates",
    "../src/main/resources/assets/spores--shadows/models/block",
    "../src/main/resources/assets/spores--shadows/models/item"
]

def duplicate_for_woods():
    for d in DIRS_TO_PROCESS:
        if not os.path.exists(d): continue
        
        # Collect all oak files
        oak_files = []
        for filename in os.listdir(d):
            if "oak" in filename and filename.endswith(".json"):
                # Make sure it's an oak file, not dark_oak (dark_oak starts with dark_oak)
                # But to be safe, we check if it has '_oak_' or ends with '_oak.json'
                if "_oak_" in filename or filename.endswith("_oak.json") or "moldy_oak" in filename or "rotten_oak" in filename or "tainted_oak" in filename:
                    if "dark_oak" not in filename:
                        oak_files.append(filename)
                        
        # For each oak file, duplicate it for all other woods
        for oak_filename in oak_files:
            oak_path = os.path.join(d, oak_filename)
            with open(oak_path, "r", encoding="utf-8") as f:
                oak_content = f.read()
                
            for wood in WOODS:
                # Replace 'oak' with the current wood name in the filename
                new_filename = oak_filename.replace("oak", wood)
                new_path = os.path.join(d, new_filename)
                
                # We skip if the file already exists? No, we can overwrite to be sure it's updated
                # In the content, we replace 'oak' with 'wood'
                # But we have to be careful: "oak_planks" -> "spruce_planks"
                # "moldy_oak_trapdoor" -> "moldy_spruce_trapdoor"
                new_content = oak_content.replace("oak", wood)
                
                with open(new_path, "w", encoding="utf-8") as f:
                    f.write(new_content)
                    
                print(f"Creato {new_filename} in {os.path.basename(d)}")

if __name__ == "__main__":
    duplicate_for_woods()
    print("Fatto! Tutti i JSON mancanti (spruce, birch, ecc) sono stati generati!")
