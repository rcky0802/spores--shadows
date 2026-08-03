import os
import random
from PIL import Image

def generate_layer(stage, width, height, is_top):
    # Mold colors
    mold_colors = [(232, 232, 232, 255), (199, 199, 199, 255), (166, 166, 166, 255), (133, 133, 133, 255)]
    
    random.seed(42 + stage * 10 + (1 if is_top else 0))
    
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
    
    img = Image.new("RGBA", (width, height), (0,0,0,0))
    pixels = img.load()
    
    for y in range(height):
        for x in range(width):
            if random.random() < mold_chance:
                pixels[x, y] = random.choice(mold_colors)
            elif random.random() < rot_chance:
                pixels[x, y] = (0, 0, 0, 120) # semi-transparent black for rot
                
    return img

output_dir = r"src\main\resources\assets\spores--shadows\textures\block"
os.makedirs(output_dir, exist_ok=True)

for stage in [1, 2, 3]:
    for is_top in [False, True]:
        suffix = "_top" if is_top else ""
        filename = f"moldy_oak_log_stage_{stage}{suffix}.png"
        
        img = generate_layer(stage, 16, 16, is_top)
        img.save(os.path.join(output_dir, filename))

print("Transparent overlay textures generated!")
