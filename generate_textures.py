import os
import random
import re
from PIL import Image, ImageDraw

def rgb_to_hex(r, g, b, a=255):
    return f"#{r:02x}{g:02x}{b:02x}"

def rot_color(r, g, b):
    # Crea un effetto "legno marcio" desaturando (verso il grigio) e scurendo il colore originale
    gray = int(0.3 * r + 0.59 * g + 0.11 * b)
    nr = int((r * 0.4 + gray * 0.6) * 0.65)
    ng = int((g * 0.4 + gray * 0.6) * 0.65)
    nb = int((b * 0.4 + gray * 0.6) * 0.65)
    return rgb_to_hex(nr, ng, nb)

def create_svg(stage, base_pixels, is_top):
    # Nuova palette: Muffa bianca/grigia come richiesto
    mold_colors = ["#E8E8E8", "#C7C7C7", "#A6A6A6", "#858585"]
    
    random.seed(42 + stage * 10 + (1 if is_top else 0))
    
    # Impostiamo le probabilità di muffa e marciume molto più basse
    if stage == 1: 
        mold_chance = 0.03
        rot_chance = 0.05
    elif stage == 2: 
        mold_chance = 0.12
        rot_chance = 0.25
    elif stage == 3: 
        mold_chance = 0.25
        rot_chance = 0.50
    else: 
        mold_chance = 0
        rot_chance = 0
    
    svg = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16">']
    
    for y in range(16):
        for x in range(16):
            br, bg, bb, ba = base_pixels[x, y]
            color = rgb_to_hex(br, bg, bb)
            
            if ba > 0:
                # Applica l'effetto marciume al legno
                if random.random() < rot_chance:
                    color = rot_color(br, bg, bb)
                    
                # Applica la muffa (che copre il legno, sano o marcio che sia)
                if random.random() < mold_chance:
                    color = random.choice(mold_colors)
                    
            svg.append(f'  <rect x="{x}" y="{y}" width="1" height="1" fill="{color}"/>')
            
    svg.append('</svg>')
    return "\n".join(svg)

def convert_svg_to_png(svg_path, png_path):
    with open(svg_path, 'r') as f:
        svg_data = f.read()
    img = Image.new("RGBA", (16, 16), (0,0,0,0))
    draw = ImageDraw.Draw(img)
    pattern = re.compile(r'<rect x="(\d+)" y="(\d+)".*?fill="([^"]+)".*?/>')
    for match in pattern.finditer(svg_data):
        x, y, color = match.groups()
        draw.point((int(x), int(y)), fill=color)
    img.save(png_path)

output_dir = r"C:\Users\r.pirosu\Downloads\spores--shadows-template-1.21.1\src\main\resources\assets\spores--shadows\textures\block"

base_img = Image.open(os.path.join(output_dir, "yeeeeeeees.png")).convert("RGBA")

# SCAMBIO CORRETTO: A quanto pare la prima metà (sinistra) era il Top, e la seconda (destra) era il Lato!
base_top = base_img.crop((0, 0, 16, 16)).load()
base_side = base_img.crop((16, 0, 32, 16)).load()

svg_files = []
for stage in [1, 2, 3]:
    for is_top in [False, True]:
        suffix = "_top" if is_top else ""
        filename = f"moldy_oak_log_stage_{stage}{suffix}"
        
        base_px = base_top if is_top else base_side
        
        svg_content = create_svg(stage, base_px, is_top)
        
        svg_path = os.path.join(output_dir, f"{filename}.svg")
        with open(svg_path, "w") as f:
            f.write(svg_content)
        svg_files.append(svg_path)
        
        png_path = os.path.join(output_dir, f"{filename}.png")
        convert_svg_to_png(svg_path, png_path)

for f in svg_files:
    os.remove(f)

print("Texture ricolorate (bianco/grigio e legno marcio) e invertite generate con successo!")
