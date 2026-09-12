import os
import shutil
from PIL import Image

from ..common.paths import DEHUMIDIFIER_TEXTURES_DIR
from ..common.machine_casing import (
    copper_light, copper_base, copper_dark, copper_highlight,
    slate_black, create_base_casing, draw_led, draw_gauge_frame,
    create_shared_bottom, create_shared_side
)

def generate_dehumidifier_textures(out_dir=None):
    output_dir = out_dir if out_dir is not None else DEHUMIDIFIER_TEXTURES_DIR
    os.makedirs(output_dir, exist_ok=True)

    # 1. Base / Sotto
    img_bottom = create_shared_bottom()
    img_bottom.save(os.path.join(output_dir, "dehumidifier_bottom.png"))

    # 2. Sopra - Grata di ventilazione detector style
    img_top = create_base_casing()
    grille_frame = [(5, 3), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3),
                    (4, 4), (11, 4), (3, 5), (12, 5), (3, 6), (12, 6),
                    (3, 7), (12, 7), (3, 8), (12, 8), (3, 9), (12, 9),
                    (3, 10), (12, 10), (4, 11), (11, 11),
                    (5, 12), (6, 12), (7, 12), (8, 12), (9, 12), (10, 12)]
    for pt in grille_frame:
        img_top.putpixel(pt, copper_light)

    for y in range(4, 12):
        for x in range(4, 12):
            if (x, y) not in grille_frame:
                img_top.putpixel((x, y), slate_black)
    for y in [5, 7, 9]:
        for x in range(4, 12):
            if 3 < x < 12:
                img_top.putpixel((x, y), copper_base)
                img_top.putpixel((x, y+1), copper_dark)
    img_top.save(os.path.join(output_dir, "dehumidifier_top.png"))

    # 3. Lato - Condivide la stessa scocca e tubazione rame
    img_side = create_shared_side()
    img_side.save(os.path.join(output_dir, "dehumidifier_side.png"))

    # 4. Front Face (15 varianti: 3 status x 5 water levels)
    fill_top_y = {0: 13, 1: 10, 2: 8, 3: 5, 4: 3}

    for status_name in ['running', 'off', 'full']:
        for water_lvl in range(5):
            img_front = create_base_casing()

            # A. Spia LED di stato comune (Alto a sinistra)
            draw_led(img_front, status_name)

            # B. Serpentina di Deumidificazione / Griglia (Centro-basso)
            for gx in range(3, 10):
                img_front.putpixel((gx, 7), copper_base)
                img_front.putpixel((gx, 13), copper_dark)
            for gy in range(7, 14):
                img_front.putpixel((3, gy), copper_base)
                img_front.putpixel((9, gy), copper_dark)

            for gy in [8, 10, 12]:
                for gx in range(4, 9):
                    img_front.putpixel((gx, gy), copper_light if status_name == 'running' else copper_base)
            for gy in [9, 11]:
                for gx in range(4, 9):
                    img_front.putpixel((gx, gy), slate_black)

            # C. Colonna d'acqua a destra (cornice comune)
            draw_gauge_frame(img_front)

            top_y = fill_top_y[water_lvl]
            for y in range(3, 13):
                if y < top_y:
                    img_front.putpixel((12, y), (42, 54, 68, 255))
                    img_front.putpixel((13, y), (20, 28, 38, 255))
                elif y == top_y:
                    img_front.putpixel((12, y), (130, 220, 255, 255))
                    img_front.putpixel((13, y), (90, 185, 245, 255))
                else:
                    img_front.putpixel((12, y), (50, 150, 235, 255))
                    img_front.putpixel((13, y), (25, 100, 185, 255))

            filename = f"dehumidifier_front_{status_name}_{water_lvl}.png"
            img_front.save(os.path.join(output_dir, filename))

    # Fallback copies
    shutil.copy(os.path.join(output_dir, "dehumidifier_front_off_0.png"), os.path.join(output_dir, "dehumidifier_front.png"))
    shutil.copy(os.path.join(output_dir, "dehumidifier_front_running_0.png"), os.path.join(output_dir, "dehumidifier_front_on.png"))

    print(f"[OK] Generated 18 dehumidifier textures in {output_dir}")

if __name__ == '__main__':
    generate_dehumidifier_textures()
