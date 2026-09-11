import os
from PIL import Image, ImageDraw

def generate_textures():
    out_dir = r"src\main\resources\assets\spores--shadows\textures\block"
    os.makedirs(out_dir, exist_ok=True)

    # Palette Ferro
    iron_darkest = (65, 65, 65, 255)
    iron_dark = (90, 90, 90, 255)
    iron_mid = (125, 125, 125, 255)
    iron_light = (165, 165, 165, 255)
    iron_highlight = (195, 195, 195, 255)

    # Palette Rame
    copper_dark = (135, 65, 45, 255)
    copper_mid = (185, 95, 65, 255)
    copper_light = (215, 125, 90, 255)
    copper_highlight = (235, 150, 115, 255)

    def base_panel():
        img = Image.new('RGBA', (16, 16), iron_mid)
        for x in range(16):
            img.putpixel((x, 0), iron_highlight)
            img.putpixel((x, 15), iron_darkest)
        for y in range(16):
            img.putpixel((0, y), iron_highlight)
            img.putpixel((15, y), iron_darkest)
        import random
        random.seed(42)
        for y in range(1, 15):
            for x in range(1, 15):
                n = random.randint(-10, 10)
                r = min(255, max(0, iron_mid[0] + n))
                img.putpixel((x, y), (r, r, r, 255))
        return img

    # 1. Base / Sotto (dehumidifier_bottom.png)
    img_bottom = base_panel()
    for corner in [(2, 2), (13, 2), (2, 13), (13, 13)]:
        img_bottom.putpixel(corner, iron_darkest)
        img_bottom.putpixel((corner[0]+1, corner[1]), iron_light)
    img_bottom.save(os.path.join(out_dir, "dehumidifier_bottom.png"))

    # 2. Sopra (dehumidifier_top.png) - Grata di sfogo aria secca con dettagli rame
    img_top = base_panel()
    draw_top = ImageDraw.Draw(img_top)
    draw_top.rectangle([4, 4, 11, 11], outline=copper_dark, fill=iron_darkest)
    for x in range(5, 11, 2):
        for y in range(5, 11):
            img_top.putpixel((x, y), copper_mid)
    for c in [(1, 1), (14, 1), (1, 14), (14, 14)]:
        img_top.putpixel(c, copper_light)
    img_top.save(os.path.join(out_dir, "dehumidifier_top.png"))

    # 3. Lati (dehumidifier_side.png) - Struttura industriale, tubo condensa e borchie
    img_side = base_panel()
    for y in range(1, 15):
        img_side.putpixel((7, y), copper_light)
        img_side.putpixel((8, y), copper_dark)
    for fy in [4, 11]:
        for x in range(6, 10):
            img_side.putpixel((x, fy), iron_highlight)
    img_side.save(os.path.join(out_dir, "dehumidifier_side.png"))

    # 4. Fronte Spento (dehumidifier_front.png)
    img_front = base_panel()
    for y in range(8, 14, 2):
        for x in range(4, 12):
            img_front.putpixel((x, y), copper_dark)
            img_front.putpixel((x, y+1), iron_darkest)
    for y in range(3, 14):
        img_front.putpixel((12, y), iron_darkest)
        img_front.putpixel((13, y), (40, 80, 140, 255))
    for dx in range(2):
        for dy in range(2):
            img_front.putpixel((3 + dx, 3 + dy), (160, 30, 30, 255))
    img_front.save(os.path.join(out_dir, "dehumidifier_front.png"))

    # 5. Fronte Acceso (dehumidifier_front_on.png)
    img_front_on = base_panel()
    for y in range(8, 14, 2):
        for x in range(4, 12):
            img_front_on.putpixel((x, y), copper_highlight)
            img_front_on.putpixel((x, y+1), (180, 80, 20, 255))
    for y in range(3, 14):
        img_front_on.putpixel((12, y), iron_darkest)
        img_front_on.putpixel((13, y), (60, 140, 220, 255))
    for dx in range(2):
        for dy in range(2):
            img_front_on.putpixel((3 + dx, 3 + dy), (40, 230, 60, 255))
    img_front_on.save(os.path.join(out_dir, "dehumidifier_front_on.png"))

    print(f"Generated 5 dehumidifier block textures in {out_dir}")

if __name__ == '__main__':
    generate_textures()
