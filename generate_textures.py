import zipfile
import os
from PIL import Image, ImageEnhance

jar_path = r"C:\Users\r.pirosu\.gradle\caches\fabric-loom\1.21.1\minecraft-client.jar"
out_dir = r"C:\Users\r.pirosu\Downloads\spores--shadows-template-1.21.1\src\main\resources\assets\spores--shadows\textures\block"

textures_to_extract = {
    "assets/minecraft/textures/block/oak_planks.png": "oak_planks.png",
    "assets/minecraft/textures/block/stripped_oak_log.png": "stripped_oak_log.png",
    "assets/minecraft/textures/block/stripped_oak_log_top.png": "stripped_oak_log_top.png"
}

# Extract vanilla textures
with zipfile.ZipFile(jar_path, 'r') as jar:
    for zip_path, local_name in textures_to_extract.items():
        with jar.open(zip_path) as f:
            img = Image.open(f).convert("RGBA")
            img.save(local_name)

# Define stages
def apply_mold(img_path, output_name, stage):
    img = Image.open(img_path).convert("RGBA")
    
    # Create overlay color
    overlay = Image.new("RGBA", img.size, (255, 255, 255, 0))
    if stage == 1:
        color = (111, 139, 111, 255) # Light green
        alpha = 0.3
        sat = 0.9
        bright = 0.95
    elif stage == 2:
        color = (90, 115, 77, 255) # Med green
        alpha = 0.6
        sat = 0.7
        bright = 0.85
    elif stage == 3:
        color = (62, 77, 62, 255) # Dark rot
        alpha = 0.8
        sat = 0.5
        bright = 0.6
        
    solid = Image.new("RGBA", img.size, color)
    blended = Image.blend(img, solid, alpha)
    
    # Restore original alpha channel to avoid making transparent parts colored
    r, g, b, a = blended.split()
    orig_a = img.split()[3]
    blended = Image.merge("RGBA", (r, g, b, orig_a))
    
    # Adjust saturation
    enhancer = ImageEnhance.Color(blended)
    blended = enhancer.enhance(sat)
    
    # Adjust brightness
    enhancer_b = ImageEnhance.Brightness(blended)
    blended = enhancer_b.enhance(bright)
    
    blended.save(os.path.join(out_dir, output_name))

# Process
for base in ["oak_planks", "stripped_oak_log", "stripped_oak_log_top"]:
    for stage in [1, 2, 3]:
        out_name = f"moldy_{base}_stage_{stage}.png"
        if base == "stripped_oak_log_top":
            out_name = f"moldy_stripped_oak_log_stage_{stage}_top.png"
        apply_mold(f"{base}.png", out_name, stage)

print("Textures generated successfully!")
