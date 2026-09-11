import os
from PIL import Image

def create_textures():
    out_dir = r"src/main/resources/assets/spores--shadows/textures/block"
    os.makedirs(out_dir, exist_ok=True)

    # Palette ufficiale Detector (Spore & Moisture Detector)
    copper_highlight = (204, 120, 64, 255)
    copper_light = (168, 80, 40, 255)
    copper_base = (107, 48, 24, 255)
    copper_dark = (46, 22, 10, 255)

    # Ardesia / Metallo scuro industriale per piastre interne e griglie
    slate_highlight = (70, 75, 85, 255)
    slate_mid = (42, 45, 52, 255)
    slate_dark = (28, 30, 36, 255)
    slate_black = (17, 19, 23, 255)

    # Vetro e misuratori stile detector
    glass_rim = (72, 136, 168, 255)
    glass_shine = (144, 200, 220, 255)

    # Colori Acqua
    water_deep = (20, 70, 140, 255)
    water_mid = (36, 115, 195, 255)
    water_light = (60, 165, 235, 255)
    water_highlight = (144, 220, 255, 255)

    # Stati LED Bottone
    led_colors = {
        'running': { # Verde (attivo)
            'glow': (96, 255, 170, 255),
            'core': (24, 196, 88, 255),
            'shadow': (12, 110, 45, 255)
        },
        'off': { # Rosso (spenta)
            'glow': (255, 120, 120, 255),
            'core': (220, 35, 35, 255),
            'shadow': (125, 15, 15, 255)
        },
        'full': { # Blu (fermo perché deposito pieno)
            'glow': (144, 224, 255, 255),
            'core': (32, 144, 235, 255),
            'shadow': (16, 75, 160, 255)
        }
    }

    def create_base_casing():
        img = Image.new('RGBA', (16, 16), slate_mid)
        # Bordo esterno in rame detector
        for x in range(16):
            img.putpixel((x, 0), copper_highlight)
            img.putpixel((x, 15), copper_dark)
        for y in range(16):
            img.putpixel((0, y), copper_light)
            img.putpixel((15, y), copper_dark)

        # Secondo anello interno cornice rame
        for x in range(1, 15):
            img.putpixel((x, 1), copper_light)
            img.putpixel((x, 14), copper_base)
        for y in range(1, 15):
            img.putpixel((1, y), copper_base)
            img.putpixel((14, y), copper_base)

        # Angoli rinforzati con rivetti
        for c in [(1, 1), (14, 1), (1, 14), (14, 14)]:
            img.putpixel(c, copper_highlight)
        for c in [(2, 2), (13, 2), (2, 13), (13, 13)]:
            img.putpixel(c, copper_dark)

        # Pannello centrale in ardesia scura
        for y in range(2, 14):
            for x in range(2, 14):
                shade = slate_mid
                if (x + y) % 3 == 0:
                    shade = slate_dark
                elif (x * y) % 5 == 0:
                    shade = slate_highlight
                img.putpixel((x, y), shade)

        return img

    # 1. Base / Sotto (dehumidifier_bottom.png)
    img_bottom = create_base_casing()
    for x in range(4, 12):
        for y in range(4, 12):
            img_bottom.putpixel((x, y), slate_dark)
    for x in range(4, 12):
        img_bottom.putpixel((x, 4), copper_base)
        img_bottom.putpixel((x, 11), copper_dark)
    for y in range(4, 12):
        img_bottom.putpixel((4, y), copper_base)
        img_bottom.putpixel((11, y), copper_dark)
    for c in [(3, 3), (12, 3), (3, 12), (12, 12)]:
        img_bottom.putpixel(c, copper_highlight)
    img_bottom.save(os.path.join(out_dir, "dehumidifier_bottom.png"))

    # 2. Sopra (dehumidifier_top.png) - Grata di ventilazione detector style
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
    img_top.save(os.path.join(out_dir, "dehumidifier_top.png"))

    # 3. Lato (dehumidifier_side.png) - Cassa rame, ardesia e tubazioni rame
    img_side = create_base_casing()
    for y in range(2, 14):
        img_side.putpixel((7, y), copper_light)
        img_side.putpixel((8, y), copper_highlight)
        img_side.putpixel((9, y), copper_base)
    for cy in [4, 11]:
        for x in range(6, 11):
            img_side.putpixel((x, cy), copper_highlight if x == 7 or x == 8 else copper_dark)
        img_side.putpixel((6, cy), copper_highlight)
        img_side.putpixel((10, cy), copper_base)
    img_side.save(os.path.join(out_dir, "dehumidifier_side.png"))

    # 4. Front Face Generatore (per i 3 stati e i 5 livelli acqua = 15 varianti)
    for status_name, led in led_colors.items():
        for water_lvl in range(5):
            img_front = create_base_casing()

            # --- A. Sezione Bottone / Spia di Stato (Alto a sinistra: x=3..5, y=3..5) ---
            for bx in range(2, 6):
                for by in range(2, 6):
                    img_front.putpixel((bx, by), copper_dark)

            for bx in range(3, 6):
                for by in range(3, 6):
                    img_front.putpixel((bx, by), led['shadow'])

            img_front.putpixel((3, 3), led['glow'])
            img_front.putpixel((4, 3), led['glow'])
            img_front.putpixel((3, 4), led['core'])
            img_front.putpixel((4, 4), led['core'])
            img_front.putpixel((5, 3), led['core'])
            img_front.putpixel((5, 4), led['shadow'])
            img_front.putpixel((4, 5), led['shadow'])

            # --- B. Sezione Griglia di Deumidificazione / Serpentina (Centro-basso: x=3..9, y=7..13) ---
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

            # --- C. Barra dell'Acqua Dinamica (Destra: x=11..14, y=2..13) - Liscia e Lineare ---
            # Supporti/raccordi in rame alle estremità (top y=2, bottom y=13)
            for x in range(11, 15):
                img_front.putpixel((x, 2), copper_light if x in (12, 13) else copper_base)
                img_front.putpixel((x, 13), copper_base if x in (12, 13) else copper_dark)

            # Guide laterali verticali lisce e continue (binari canalina)
            for y in range(3, 13):
                img_front.putpixel((11, y), (28, 24, 24, 255))
                img_front.putpixel((14, y), copper_dark)

            # Riempimento dinamico liscio e lineare della colonna in vetro (y da 3 a 12 = 10 px)
            fill_top_y = {
                0: 13, # Nessuna acqua (0 px)
                1: 10, # 25% (3 px di acqua: y=10..12)
                2: 8,  # 50% (5 px di acqua: y=8..12)
                3: 5,  # 75% (8 px di acqua: y=5..12)
                4: 3   # 100% pieno (10 px di acqua: y=3..12)
            }[water_lvl]

            for y in range(3, 13):
                if y < fill_top_y:
                    # Porzione vuota: tubo in vetro liscio e pulito
                    img_front.putpixel((12, y), (42, 54, 68, 255))  # riflesso lineare vetro
                    img_front.putpixel((13, y), (20, 28, 38, 255))  # sfondo scuro interno
                elif y == fill_top_y:
                    # Superficie dell'acqua: menisco orizzontale netto e brillante
                    img_front.putpixel((12, y), (130, 220, 255, 255))
                    img_front.putpixel((13, y), (90, 185, 245, 255))
                else:
                    # Colonna d'acqua: liquido azzurro continuo e compatto
                    img_front.putpixel((12, y), (50, 150, 235, 255)) # colonna viva
                    img_front.putpixel((13, y), (25, 100, 185, 255)) # profondità acqua

            filename = f"dehumidifier_front_{status_name}_{water_lvl}.png"
            img_front.save(os.path.join(out_dir, filename))

    # Salva anche i file di fallback
    import shutil
    shutil.copy(os.path.join(out_dir, "dehumidifier_front_off_0.png"), os.path.join(out_dir, "dehumidifier_front.png"))
    shutil.copy(os.path.join(out_dir, "dehumidifier_front_running_0.png"), os.path.join(out_dir, "dehumidifier_front_on.png"))

    print(f"Generated all dehumidifier detector-style textures in {out_dir}")

if __name__ == '__main__':
    create_textures()
