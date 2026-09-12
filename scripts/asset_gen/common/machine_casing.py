from PIL import Image

# Palette ufficiale Detector (Spore & Moisture Detector, Dehumidifier, Air Purifier)
copper_highlight = (204, 120, 64, 255)
copper_light = (168, 80, 40, 255)
copper_base = (107, 48, 24, 255)
copper_dark = (46, 22, 10, 255)

# Ardesia / Metallo scuro industriale per scocca e pannelli
slate_highlight = (70, 75, 85, 255)
slate_mid = (42, 45, 52, 255)
slate_dark = (28, 30, 36, 255)
slate_black = (17, 19, 23, 255)

# Vetro e misuratori stile detector
glass_rim = (72, 136, 168, 255)
glass_shine = (144, 200, 220, 255)

# Colori LED di stato comuni
LED_COLORS = {
    'running': { # Verde (attivo)
        'glow': (96, 255, 170, 255),
        'core': (24, 196, 88, 255),
        'shadow': (12, 110, 45, 255)
    },
    'off': { # Rosso (spento)
        'glow': (255, 120, 120, 255),
        'core': (220, 35, 35, 255),
        'shadow': (125, 15, 15, 255)
    },
    'full': { # Blu (deumidificatore pieno)
        'glow': (144, 224, 255, 255),
        'core': (32, 144, 235, 255),
        'shadow': (16, 75, 160, 255)
    },
    'filter_depleted': { # Arancione (depuratore filtro esaurito)
        'glow': (255, 180, 80, 255),
        'core': (240, 130, 20, 255),
        'shadow': (140, 65, 10, 255)
    }
}

def create_base_casing():
    """Genera la scocca comune (16x16 px) in rame e ardesia scura condivisa da Deumidificatore e Depuratore."""
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

def draw_led(img, status_name):
    """Disegna l'indicatore LED comune a 3x3 pixel nella scocca in alto a sinistra (x=3..5, y=3..5)."""
    led = LED_COLORS[status_name]
    for bx in range(2, 6):
        for by in range(2, 6):
            img.putpixel((bx, by), copper_dark)

    for bx in range(3, 6):
        for by in range(3, 6):
            img.putpixel((bx, by), led['shadow'])

    img.putpixel((3, 3), led['glow'])
    img.putpixel((4, 3), led['glow'])
    img.putpixel((3, 4), led['core'])
    img.putpixel((4, 4), led['core'])
    img.putpixel((5, 3), led['core'])
    img.putpixel((5, 4), led['shadow'])
    img.putpixel((4, 5), led['shadow'])

def draw_gauge_frame(img):
    """Disegna la cornice comune per la colonna graduata a destra (x=11..14, y=2..13)."""
    for x in range(11, 15):
        img.putpixel((x, 2), copper_light if x in (12, 13) else copper_base)
        img.putpixel((x, 13), copper_base if x in (12, 13) else copper_dark)

    for y in range(3, 13):
        img.putpixel((11, y), (28, 24, 24, 255))
        img.putpixel((14, y), copper_dark)

def create_shared_bottom():
    """Base inferiore strutturale comune a entrambi i macchinari."""
    img = create_base_casing()
    for x in range(4, 12):
        for y in range(4, 12):
            img.putpixel((x, y), slate_dark)
    for x in range(4, 12):
        img.putpixel((x, 4), copper_base)
        img.putpixel((x, 11), copper_dark)
    for y in range(4, 12):
        img.putpixel((4, y), copper_base)
        img.putpixel((11, y), copper_dark)
    for c in [(3, 3), (12, 3), (3, 12), (12, 12)]:
        img.putpixel(c, copper_highlight)
    return img

def create_shared_side():
    """Scocca laterale comune: garantisce allineamento perfetto delle tubazioni in rame e borchie tra i macchinari affiancati."""
    img = create_base_casing()
    for y in range(2, 14):
        img.putpixel((7, y), copper_light)
        img.putpixel((8, y), copper_highlight)
        img.putpixel((9, y), copper_base)
    for cy in [4, 11]:
        for x in range(6, 11):
            img.putpixel((x, cy), copper_highlight if x == 7 or x == 8 else copper_dark)
        img.putpixel((6, cy), copper_highlight)
        img.putpixel((10, cy), copper_base)
    return img
