import os
import sys
import urllib.request
from PIL import Image

# Config
WOODS = ["spruce"] # Aggiungi qui gli altri legni in futuro!
STAGES = [1, 2, 3]

# Texture Vanilla da scaricare (Log lato, Log sopra, Planks)
VANILLA_TEXTURES = {
    "log": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_log.png",
    "log_top": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_log_top.png",
    "planks": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_planks.png",
    "stripped_log": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/stripped_{wood}_log.png",
    "stripped_log_top": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/stripped_{wood}_log_top.png",
    "door_bottom": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_door_bottom.png",
    "door_top": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_door_top.png",
    "trapdoor": "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/{wood}_trapdoor.png",
}

OUTPUT_DIR = "../src/main/resources/assets/spores--shadows/textures/block/"

def main():
    if not os.path.exists("overlays"):
        os.makedirs("overlays")
        print("Ho creato la cartella 'overlays'. Per favore inserisci qui i file 'mold_stage_1.png', 'mold_stage_2.png' e 'mold_stage_3.png' e riavvia lo script.")
        return

    overlays = {}
    for stage in STAGES:
        path = f"overlays/mold_stage_{stage}.png"
        if not os.path.exists(path):
            print(f"ERRORE: Manca il file {path}. Crealo e riprova!")
            return
        overlays[stage] = Image.open(path).convert("RGBA")

    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)

    for wood in WOODS:
        print(f"Generando texture per {wood}...")
        for tex_key, url_template in VANILLA_TEXTURES.items():
            url = url_template.format(wood=wood)
            temp_file = f"temp_{wood}_{tex_key}.png"
            
            # Scarica la texture vanilla
            try:
                urllib.request.urlretrieve(url, temp_file)
            except Exception as e:
                print(f"  [!] Impossibile scaricare {url}: {e}")
                continue

            base_img = Image.open(temp_file).convert("RGBA")

            for stage in STAGES:
                # Sovrappone la muffa
                combined = Image.alpha_composite(base_img, overlays[stage])
                
                # Salva il risultato
                if tex_key == "log" or tex_key == "log_top":
                    out_name = f"moldy_{wood}_{tex_key}_stage_{stage}.png"
                elif tex_key.startswith("stripped"):
                    out_name = f"moldy_{tex_key}_stage_{stage}.png"
                else:
                    out_name = f"moldy_{wood}_{tex_key}_stage_{stage}.png"
                    
                combined.save(os.path.join(OUTPUT_DIR, out_name))
                print(f"  -> Salvato {out_name}")

            os.remove(temp_file)

    print("Finito! Tutte le texture sono state generate.")

if __name__ == "__main__":
    main()
