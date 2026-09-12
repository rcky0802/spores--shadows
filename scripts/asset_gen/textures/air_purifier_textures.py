import os
import shutil
from PIL import Image

from ..common.paths import AIR_PURIFIER_TEXTURES_DIR
from ..common.machine_casing import (
    copper_light, copper_base, copper_dark, copper_highlight,
    slate_black, create_base_casing, draw_led, draw_gauge_frame,
    create_shared_bottom, create_shared_side
)

def generate_air_purifier_textures(out_dir=None):
    output_dir = out_dir if out_dir is not None else AIR_PURIFIER_TEXTURES_DIR
    os.makedirs(output_dir, exist_ok=True)

    # 1. Base / Sotto - Condivide la medesima base strutturale
    img_bottom = create_shared_bottom()
    img_bottom.save(os.path.join(output_dir, "air_purifier_bottom.png"))

    # 2. Lato - Condivide la medesima scocca e tubazione rame per perfetto allineamento modulare
    img_side = create_shared_side()
    img_side.save(os.path.join(output_dir, "air_purifier_side.png"))

    # 3. Sopra - Bocchettone di scarico aria decontaminata (turbina di espulsione con grata protettiva)
    img_top = create_base_casing()

    # Cornice ottagonale in rame con smussatura 3D
    top_rim_hi = [
        (5, 2), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
        (4, 3), (11, 3),
        (3, 4), (12, 4),
        (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
    ]
    top_rim_sh = [
        (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10),
        (3, 11), (12, 11),
        (4, 12), (11, 12),
        (5, 13), (6, 13), (7, 13), (8, 13), (9, 13), (10, 13)
    ]
    for pt in top_rim_hi:
        img_top.putpixel(pt, copper_highlight if pt[1] == 2 or pt[0] == 2 else copper_light)
    for pt in top_rim_sh:
        img_top.putpixel(pt, copper_dark if pt[1] == 13 or pt[0] == 13 else copper_base)

    # Rivetti angolari della flangia superiore
    img_top.putpixel((4, 3), (255, 195, 115, 255))
    img_top.putpixel((11, 3), copper_light)
    img_top.putpixel((3, 4), (255, 195, 115, 255))
    img_top.putpixel((12, 4), copper_light)
    img_top.putpixel((3, 11), copper_light)
    img_top.putpixel((12, 11), copper_dark)
    img_top.putpixel((4, 12), copper_light)
    img_top.putpixel((11, 12), copper_dark)

    # Cavità interna profonda dello scarico
    for y in range(3, 13):
        for x in range(3, 13):
            if (x, y) in top_rim_hi or (x, y) in top_rim_sh:
                continue
            if x in (3, 12) and y in (3, 12):
                continue
            img_top.putpixel((x, y), slate_black)

    # Ombra di profondità sotto il bordo superiore/sinistro
    for x in range(5, 11):
        img_top.putpixel((x, 3), (12, 14, 18, 255))
    for y in range(5, 11):
        img_top.putpixel((3, y), (12, 14, 18, 255))
    img_top.putpixel((4, 4), (12, 14, 18, 255))

    # Pale curve della girante di espulsione (visibili sotto la grata)
    blade_hi = (190, 120, 60, 255)
    blade_mid = (140, 75, 35, 255)
    blade_sh = (75, 38, 16, 255)

    # Pala Nord-Est
    for pt, c in [((8, 6), blade_hi), ((9, 6), blade_mid), ((9, 5), blade_hi), ((10, 5), blade_mid), ((10, 4), blade_hi), ((11, 4), blade_mid), ((10, 6), blade_sh), ((11, 5), blade_sh)]:
        img_top.putpixel(pt, c)
    # Pala Sud-Est
    for pt, c in [((9, 8), blade_hi), ((9, 9), blade_mid), ((10, 9), blade_hi), ((10, 10), blade_mid), ((11, 10), blade_mid), ((10, 11), blade_sh), ((9, 10), blade_sh)]:
        img_top.putpixel(pt, c)
    # Pala Sud-Ovest
    for pt, c in [((7, 9), blade_hi), ((6, 9), blade_mid), ((6, 10), blade_hi), ((5, 10), blade_mid), ((5, 11), blade_mid), ((4, 11), blade_sh), ((5, 9), blade_sh)]:
        img_top.putpixel(pt, c)
    # Pala Nord-Ovest
    for pt, c in [((6, 7), blade_hi), ((6, 6), blade_mid), ((5, 6), blade_hi), ((5, 5), blade_mid), ((4, 5), blade_hi), ((4, 4), blade_sh), ((6, 5), blade_sh)]:
        img_top.putpixel(pt, c)

    # Grata di sicurezza in fil di rame con anello concentrico
    wire_ring = [
        (6, 4), (7, 4), (8, 4), (9, 4),
        (5, 5), (10, 5),
        (4, 6), (11, 6),
        (4, 7), (11, 7),
        (4, 8), (11, 8),
        (4, 9), (11, 9),
        (5, 10), (10, 10),
        (6, 11), (7, 11), (8, 11), (9, 11)
    ]
    for pt in wire_ring:
        if pt[1] in (4, 5) or pt[0] in (4, 5):
            img_top.putpixel(pt, (160, 100, 50, 255))
        else:
            img_top.putpixel(pt, (100, 55, 25, 255))

    # Razze strutturali della grata (Verticali e Orizzontali)
    for y in [3, 4, 5, 6, 9, 10, 11, 12]:
        img_top.putpixel((7, y), (205, 135, 75, 255) if y < 7 else (175, 110, 55, 255))
        img_top.putpixel((8, y), (90, 45, 20, 255))
    for x in [3, 4, 5, 6, 9, 10, 11, 12]:
        img_top.putpixel((x, 7), (205, 135, 75, 255) if x < 7 else (175, 110, 55, 255))
        img_top.putpixel((x, 8), (90, 45, 20, 255))

    # Bullone e calotta centrale dell'albero motore
    img_top.putpixel((7, 7), (255, 220, 140, 255))
    img_top.putpixel((8, 7), (210, 140, 75, 255))
    img_top.putpixel((7, 8), (160, 95, 45, 255))
    img_top.putpixel((8, 8), (80, 40, 18, 255))

    img_top.save(os.path.join(output_dir, "air_purifier_top.png"))

    # 4. Front Face (15 varianti: 3 status x 5 filter levels)
    filter_palette = {
        4: {'core': (50, 210, 90, 255),  'top': (140, 255, 170, 255), 'shadow': (25, 120, 60, 255)}, # 100% (Verde vivo)
        3: {'core': (160, 220, 50, 255), 'top': (210, 255, 110, 255), 'shadow': (95, 140, 20, 255)}, # 75% (Verde-Giallo)
        2: {'core': (235, 160, 40, 255), 'top': (255, 210, 100, 255), 'shadow': (150, 95, 15, 255)}, # 50% (Ambra/Arancio)
        1: {'core': (220, 80, 30, 255),  'top': (255, 140, 80, 255),  'shadow': (140, 40, 15, 255)}, # 25% (Rosso-Arancio)
    }

    fill_top_y = {0: 13, 1: 10, 2: 8, 3: 5, 4: 3}

    # Bordo bocchettone frontale di aspirazione 8x8 (x=2..9, y=6..13)
    cowl_rim_hi = [
        (4, 6), (5, 6), (6, 6), (7, 6),
        (3, 7), (8, 7),
        (2, 8), (2, 9), (2, 10), (2, 11),
        (9, 8)
    ]
    cowl_rim_sh = [
        (9, 9), (9, 10), (9, 11),
        (3, 12), (8, 12),
        (4, 13), (5, 13), (6, 13), (7, 13)
    ]

    for status_name in ['running', 'off', 'filter_depleted']:
        for filter_lvl in range(5):
            img_front = create_base_casing()

            # A. Spia LED di stato comune (Alto a sinistra: x=3..5, y=3..5)
            draw_led(img_front, status_name)

            # B. Turbina Circolare di Aspirazione Spore (Flangia 8x8 con profondità)
            for pt in cowl_rim_hi:
                img_front.putpixel(pt, copper_highlight if pt[1] == 6 or pt[0] == 2 else copper_light)
            for pt in cowl_rim_sh:
                img_front.putpixel(pt, copper_dark if pt[1] == 13 or pt[0] == 9 else copper_base)

            # Borchie di fissaggio flangia
            img_front.putpixel((3, 7), (255, 190, 110, 255))
            img_front.putpixel((8, 7), copper_light)
            img_front.putpixel((3, 12), copper_light)
            img_front.putpixel((8, 12), copper_dark)

            # Cavità interna d'aspirazione profonda
            for y in range(7, 13):
                for x in range(3, 9):
                    if (x, y) not in cowl_rim_hi and (x, y) not in cowl_rim_sh:
                        img_front.putpixel((x, y), slate_black)

            # Cono d'ombra di profondità svasata
            img_front.putpixel((4, 7), (10, 12, 15, 255))
            img_front.putpixel((5, 7), (10, 12, 15, 255))
            img_front.putpixel((6, 7), (10, 12, 15, 255))
            img_front.putpixel((3, 8), (10, 12, 15, 255))
            img_front.putpixel((3, 9), (10, 12, 15, 255))

            # Mozzo rotore centrale (2x2)
            if status_name == 'running':
                img_front.putpixel((5, 9), (255, 235, 170, 255))
                img_front.putpixel((6, 9), (240, 160, 90, 255))
                img_front.putpixel((5, 10), (200, 120, 60, 255))
                img_front.putpixel((6, 10), (120, 60, 25, 255))

                # Spazi vuoti tra le 4 pale cardinali
                img_front.putpixel((5, 8), slate_black)
                img_front.putpixel((7, 9), slate_black)
                img_front.putpixel((6, 11), slate_black)
                img_front.putpixel((4, 10), slate_black)

                # Pale della ventola in rotazione: archi cinetici ad alta velocità
                arc_glow = (255, 220, 130, 255)
                arc_hi = (235, 160, 75, 255)
                arc_mid = (175, 100, 45, 255)
                arc_trail = (115, 55, 22, 255)

                # Pala 1 (arco verso Nord-Est)
                img_front.putpixel((6, 8), arc_glow)
                img_front.putpixel((7, 8), arc_hi)
                img_front.putpixel((8, 8), arc_mid)
                img_front.putpixel((7, 7), arc_mid)
                img_front.putpixel((8, 9), arc_trail)

                # Pala 2 (arco verso Sud-Est)
                img_front.putpixel((7, 10), arc_glow)
                img_front.putpixel((7, 11), arc_hi)
                img_front.putpixel((7, 12), arc_mid)
                img_front.putpixel((8, 11), arc_mid)
                img_front.putpixel((6, 12), arc_trail)

                # Pala 3 (arco verso Sud-Ovest)
                img_front.putpixel((5, 11), arc_glow)
                img_front.putpixel((4, 11), arc_hi)
                img_front.putpixel((3, 11), arc_mid)
                img_front.putpixel((4, 12), arc_mid)
                img_front.putpixel((3, 10), arc_trail)

                # Pala 4 (arco verso Nord-Ovest)
                img_front.putpixel((4, 9), arc_glow)
                img_front.putpixel((4, 8), arc_hi)
                img_front.putpixel((4, 7), arc_mid)
                img_front.putpixel((3, 8), arc_mid)
                img_front.putpixel((5, 7), arc_trail)
            else:
                # Ventola ferma / spenta: pale meccaniche curve statiche ben definite a 4 razze
                img_front.putpixel((5, 9), (230, 155, 85, 255))
                img_front.putpixel((6, 9), copper_light)
                img_front.putpixel((5, 10), copper_base)
                img_front.putpixel((6, 10), copper_dark)

                # Spazi vuoti tra le 4 pale cardinali
                img_front.putpixel((5, 8), slate_black)
                img_front.putpixel((7, 9), slate_black)
                img_front.putpixel((6, 11), slate_black)
                img_front.putpixel((4, 10), slate_black)

                b_hi = (220, 150, 80, 255)
                b_mid = (160, 95, 45, 255)
                b_sh = (85, 42, 18, 255)

                # Pala 1: Nord-Est
                img_front.putpixel((6, 8), b_hi)
                img_front.putpixel((7, 8), b_mid)
                img_front.putpixel((7, 7), b_mid)
                img_front.putpixel((8, 8), b_sh)

                # Pala 2: Sud-Est
                img_front.putpixel((7, 10), b_hi)
                img_front.putpixel((7, 11), b_mid)
                img_front.putpixel((8, 11), b_mid)
                img_front.putpixel((8, 10), b_sh)

                # Pala 3: Sud-Ovest
                img_front.putpixel((5, 11), b_hi)
                img_front.putpixel((4, 11), b_mid)
                img_front.putpixel((4, 12), b_mid)
                img_front.putpixel((3, 11), b_sh)

                # Pala 4: Nord-Ovest
                img_front.putpixel((4, 9), b_hi)
                img_front.putpixel((4, 8), b_mid)
                img_front.putpixel((3, 8), b_mid)
                img_front.putpixel((4, 7), b_sh)

            # C. Colonna Cartuccia Filtro Antispore (Cornice comune a destra x=11..14, y=2..13)
            draw_gauge_frame(img_front)

            top_y = fill_top_y[filter_lvl]
            for y in range(3, 13):
                if y < top_y or filter_lvl == 0:
                    # Canale vuoto / filtro esaurito: vetro trasparente e vano posteriore scuro
                    img_front.putpixel((12, y), (42, 54, 68, 255))
                    img_front.putpixel((13, y), (20, 28, 38, 255))
                elif y == top_y:
                    # Menisco superiore della cartuccia filtro
                    p = filter_palette[filter_lvl]
                    img_front.putpixel((12, y), p['top'])
                    img_front.putpixel((13, y), p['core'])
                else:
                    # Riempimento corpo cartuccia filtrante attiva
                    p = filter_palette[filter_lvl]
                    img_front.putpixel((12, y), p['core'])
                    img_front.putpixel((13, y), p['shadow'])

            filename = f"air_purifier_front_{status_name}_{filter_lvl}.png"
            img_front.save(os.path.join(output_dir, filename))

    # File di fallback per visualizzazione predefinita
    shutil.copy(os.path.join(output_dir, "air_purifier_front_off_0.png"), os.path.join(output_dir, "air_purifier_front.png"))

    print(f"[OK] Generated 18 air purifier textures in {output_dir}")

if __name__ == '__main__':
    generate_air_purifier_textures()
