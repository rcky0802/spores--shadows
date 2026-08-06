import os
from PIL import Image, ImageChops

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
textures_dir = os.path.join(project_root, "src", "main", "resources", "assets", "spores--shadows", "textures", "block")
vanilla_dir = r"C:\Users\r.pirosu\Desktop\Texture\1.21.1_extracted\assets\minecraft\textures\block"

WOODS = ["oak"]

def bake_texture(vanilla_name, mold_img, stage, has_transparency=False, rotate_mold=False):
    vanilla_path = os.path.join(vanilla_dir, f"{vanilla_name}.png")
    if not os.path.exists(vanilla_path):
        print(f"Vanilla texture missing: {vanilla_name}")
        return

    vanilla_img = Image.open(vanilla_path).convert("RGBA")
    mold_to_apply = mold_img.copy()

    if rotate_mold:
        mold_to_apply = mold_to_apply.rotate(90)

    mold_resized = mold_to_apply.resize(vanilla_img.size, Image.NEAREST)

    if has_transparency:
        _, _, _, mold_a = mold_resized.split()
        _, _, _, van_a = vanilla_img.split()
        new_alpha = ImageChops.multiply(mold_a, van_a)
        r, g, b, _ = mold_resized.split()
        masked_mold = Image.merge("RGBA", (r, g, b, new_alpha))
        baked = Image.alpha_composite(vanilla_img, masked_mold)
    else:
        baked = Image.alpha_composite(vanilla_img, mold_resized)

    out_name = f"moldy_{vanilla_name}_stage_{stage}.png"
    out_path = os.path.join(textures_dir, out_name)
    baked.save(out_path)

def generate_baked():
    for stage in [1, 2, 3]:
        source_path = rf"C:\Users\r.pirosu\Desktop\Texture\muffa_legno_stage{stage}.png"
        if not os.path.exists(source_path):
            print(f"Warning: mold source not found at {source_path}")
            continue
                
        mold_img = Image.open(source_path).convert("RGBA")

        item_vanilla_dir = r"C:\Users\r.pirosu\Desktop\Texture\1.21.1_extracted\assets\minecraft\textures\item"
        item_out_dir = os.path.join(project_root, "src", "main", "resources", "assets", "spores--shadows", "textures", "item")
        os.makedirs(item_out_dir, exist_ok=True)
        
        def bake_item(vanilla_name, mold_img_item, stage_item):
            vanilla_path = os.path.join(item_vanilla_dir, f"{vanilla_name}.png")
            if not os.path.exists(vanilla_path): return
            vanilla_img = Image.open(vanilla_path).convert("RGBA")
            mold_resized = mold_img_item.copy().resize(vanilla_img.size, Image.NEAREST)
            _, _, _, mold_a = mold_resized.split()
            _, _, _, van_a = vanilla_img.split()
            new_alpha = ImageChops.multiply(mold_a, van_a)
            r, g, b, _ = mold_resized.split()
            masked_mold = Image.merge("RGBA", (r, g, b, new_alpha))
            baked = Image.alpha_composite(vanilla_img, masked_mold)
            baked.save(os.path.join(item_out_dir, f"moldy_{vanilla_name}_stage_{stage_item}.png"))

        for wood in WOODS:
            is_bamboo = wood == "bamboo"
            log = "bamboo_block" if is_bamboo else f"{wood}_log"
            wood_block = None if is_bamboo else f"{wood}_wood"
            prefix = "bamboo" if is_bamboo else wood

            # Logs
            bake_texture(log, mold_img, stage, False, False)
            bake_texture(f"{log}_top", mold_img, stage, False, True)
            
            if not is_bamboo:
                bake_texture(f"stripped_{log}", mold_img, stage, False, False)
                bake_texture(f"stripped_{log}_top", mold_img, stage, False, True)

            # Planks (bamboo planks exist in 1.21)
            bake_texture(f"{prefix}_planks", mold_img, stage, False, False)

            # Doors and trapdoors (Bamboo doors are bamboo_door)
            door = f"{prefix}_door"
            trapdoor = f"{prefix}_trapdoor"
            bake_texture(f"{door}_bottom", mold_img, stage, True, False)
            bake_texture(f"{door}_top", mold_img, stage, True, False)
            bake_texture(trapdoor, mold_img, stage, True, False)
            
            # Door Item
            bake_item(f"{prefix}_door", mold_img, stage)

if __name__ == "__main__":
    print("Starting generation of baked textures for all woods...")
    generate_baked()
    print("Done!")
