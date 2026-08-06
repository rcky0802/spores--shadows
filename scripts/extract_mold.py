import os
import urllib.request
from PIL import Image

TEXTURE_DIR = "../src/main/resources/assets/spores--shadows/textures/block/"
STAGES = [1, 2, 3]

def get_vanilla_oak():
    url = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures/block/oak_log.png"
    temp_file = "temp_oak_log.png"
    urllib.request.urlretrieve(url, temp_file)
    img = Image.open(temp_file).convert("RGBA")
    os.remove(temp_file)
    return img

def main():
    if not os.path.exists(TEXTURE_DIR):
        print("Cartella texture non trovata!")
        return

    print("Scaricando la texture Vanilla del tronco di quercia per fare il confronto...")
    vanilla_img = get_vanilla_oak()
    v_pixels = vanilla_img.load()

    for stage in STAGES:
        moldy_path = os.path.join(TEXTURE_DIR, f"moldy_oak_log_stage_{stage}.png")
        if not os.path.exists(moldy_path):
            print(f"ERRORE: Non trovo {moldy_path}")
            continue

        print(f"Estraendo la muffa da {moldy_path}...")
        moldy_img = Image.open(moldy_path).convert("RGBA")
        m_pixels = moldy_img.load()

        # Crea un'immagine vuota trasparente
        out_img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        out_pixels = out_img.load()

        for y in range(16):
            for x in range(16):
                # Se il pixel della tua texture differisce da quello vanilla, è muffa!
                # Usiamo una soglia di tolleranza piccola per evitare artefatti di compressione
                if v_pixels[x, y] != m_pixels[x, y]:
                    # Estraiamo il pixel esatto della tua muffa e lo rendiamo opaco al 100%
                    r, g, b, a = m_pixels[x, y]
                    # Se il tuo programma di grafica ha fuso i pixel (blending), 
                    # preleviamo il colore risultante per la maschera
                    out_pixels[x, y] = (r, g, b, 255)

        out_path = os.path.join(TEXTURE_DIR, f"mold_stage_{stage}.png")
        out_img.save(out_path)
        print(f"-> Creato overlay generico: {out_path}")

    # Ora puliamo le vecchie texture
    print("\nElimino i vecchi file PNG cotti inutili...")
    deleted_count = 0
    for filename in os.listdir(TEXTURE_DIR):
        if filename.startswith("moldy_") and filename.endswith(".png"):
            filepath = os.path.join(TEXTURE_DIR, filename)
            os.remove(filepath)
            deleted_count += 1
    
    print(f"Fatto! Eliminati {deleted_count} vecchi file. Ora hai solo le 3 texture di base pulite!")

if __name__ == "__main__":
    main()
