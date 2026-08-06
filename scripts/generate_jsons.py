import os
import json

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
assets_dir = os.path.join(project_root, 'src', 'main', 'resources', 'assets', 'spores--shadows')
blockstates_dir = os.path.join(assets_dir, 'blockstates')
models_dir = os.path.join(assets_dir, 'models', 'block')
item_models_dir = os.path.join(assets_dir, 'models', 'item')

WOODS = ["oak"]

def write_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)

def gen_item_model(wood, base_name, parent_model_name, stage, is_2d=False):
    stages_names = {1: 'tainted', 2: 'moldy', 3: 'rotten'}
    if stage in stages_names:
        item_name = f"{stages_names[stage]}_{base_name}"
        data = {"parent": f"spores--shadows:block/{parent_model_name}"}
        if is_2d:
            data = {
                "parent": "minecraft:item/generated",
                "textures": {
                    "layer0": f"spores--shadows:item/moldy_{base_name}_stage_{stage}"
                }
            }
        write_json(os.path.join(item_models_dir, f"{item_name}.json"), data)

def get_common_props():
    for waxed in ['false', 'true']:
        for structural in ['false', 'true']:
            yield f'structural={structural},waxed={waxed}'

def gen_slab(wood, prefix):
    block_id = f'moldy_{prefix}_slab'
    variants = {}
    for stage in [0, 1, 2, 3]:
        tex = f'minecraft:block/{prefix}_planks' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_planks_stage_{stage}'
        if stage > 0:
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}.json'), {'parent': 'minecraft:block/slab', 'textures': {'bottom': tex, 'top': tex, 'side': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}_top.json'), {'parent': 'minecraft:block/slab_top', 'textures': {'bottom': tex, 'top': tex, 'side': tex}})
            gen_item_model(wood, f"{prefix}_slab", f'{block_id}_stage_{stage}', stage)
        
        m_bottom = f'minecraft:block/{prefix}_slab' if stage == 0 else f'spores--shadows:block/{block_id}_stage_{stage}'
        m_top = f'minecraft:block/{prefix}_slab_top' if stage == 0 else f'spores--shadows:block/{block_id}_stage_{stage}_top'
        m_double = f'minecraft:block/{prefix}_planks' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_planks_stage_{stage}'
        
        for waterlogged in ['false', 'true']:
            for common in get_common_props():
                variants[f'stage={stage},{common},type=bottom,waterlogged={waterlogged}'] = {'model': m_bottom}
                variants[f'stage={stage},{common},type=top,waterlogged={waterlogged}'] = {'model': m_top}
                variants[f'stage={stage},{common},type=double,waterlogged={waterlogged}'] = {'model': m_double}
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'variants': variants})

def gen_stairs(wood, prefix):
    block_id = f'moldy_{prefix}_stairs'
    variants = {}
    facings = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    shapes = ['straight', 'inner_left', 'inner_right', 'outer_left', 'outer_right']
    halfs = ['bottom', 'top']
    for stage in [0, 1, 2, 3]:
        tex = f'minecraft:block/{prefix}_planks' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_planks_stage_{stage}'
        if stage > 0:
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}.json'), {'parent': 'minecraft:block/stairs', 'textures': {'bottom': tex, 'top': tex, 'side': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}_inner.json'), {'parent': 'minecraft:block/inner_stairs', 'textures': {'bottom': tex, 'top': tex, 'side': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}_outer.json'), {'parent': 'minecraft:block/outer_stairs', 'textures': {'bottom': tex, 'top': tex, 'side': tex}})
            gen_item_model(wood, f"{prefix}_stairs", f'{block_id}_stage_{stage}', stage)
        
        for facing, y in facings.items():
            for half in halfs:
                for shape in shapes:
                    x = 180 if half == 'top' else 0
                    y_rot = y
                    if shape == 'inner_left': y_rot = (y + 270) % 360
                    if shape == 'inner_right': y_rot = y
                    if shape == 'outer_left': y_rot = (y + 270) % 360
                    if shape == 'outer_right': y_rot = y
                    if half == 'top':
                        if shape == 'inner_left': y_rot = (y_rot + 90) % 360
                        if shape == 'inner_right': y_rot = (y_rot + 270) % 360
                        if shape == 'outer_left': y_rot = (y_rot + 90) % 360
                        if shape == 'outer_right': y_rot = (y_rot + 270) % 360

                    model_name = 'stairs' if shape == 'straight' else ('inner_stairs' if 'inner' in shape else 'outer_stairs')
                    
                    if stage == 0: m = f'minecraft:block/{prefix}_{model_name}'
                    else:
                        m = f'spores--shadows:block/{block_id}_stage_{stage}'
                        if 'inner' in shape: m += '_inner'
                        if 'outer' in shape: m += '_outer'
                    
                    for waterlogged in ['false', 'true']:
                        for common in get_common_props():
                            variants[f'facing={facing},half={half},shape={shape},stage={stage},{common},waterlogged={waterlogged}'] = {
                                'model': m, 'x': x, 'y': y_rot, 'uvlock': True
                            }
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'variants': variants})

def gen_door(wood, prefix):
    block_id = f'moldy_{prefix}_door'
    variants = {}
    facings = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    for stage in [0, 1, 2, 3]:
        tex_bot = f'minecraft:block/{prefix}_door_bottom' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_door_bottom_stage_{stage}'
        tex_top = f'minecraft:block/{prefix}_door_top' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_door_top_stage_{stage}'
        if stage > 0:
            for half in ['bottom', 'top']:
                for hinge in ['left', 'right']:
                    for open_s in ['', '_open']:
                        m_name = f'{block_id}_{half}_{hinge}{open_s}_stage_{stage}'
                        parent = f'minecraft:block/door_{half}_{hinge}{open_s}'
                        write_json(os.path.join(models_dir, f'{m_name}.json'), {
                            'parent': parent,
                            'textures': {'bottom': tex_bot, 'top': tex_top}
                        })
            gen_item_model(wood, f"{prefix}_door", f'{block_id}_bottom_left_stage_{stage}', stage, is_2d=True)
        
        for facing in ['north', 'east', 'south', 'west']:
            for half in ['lower', 'upper']:
                for hinge in ['left', 'right']:
                    for open_state in ['false', 'true']:
                        for powered in ['false', 'true']:
                            y_rot = 0
                            if facing == 'east':
                                if open_state == 'true': y_rot = 90 if hinge == 'left' else 270
                                else: y_rot = 0
                            elif facing == 'south':
                                if open_state == 'true': y_rot = 180 if hinge == 'left' else 0
                                else: y_rot = 90
                            elif facing == 'west':
                                if open_state == 'true': y_rot = 270 if hinge == 'left' else 90
                                else: y_rot = 180
                            elif facing == 'north':
                                if open_state == 'true': y_rot = 0 if hinge == 'left' else 180
                                else: y_rot = 270
                            
                            t_half = 'bottom' if half == 'lower' else 'top'
                            open_s = '_open' if open_state == 'true' else ''
                            
                            if stage == 0: m = f'minecraft:block/{prefix}_door_{t_half}_{hinge}{open_s}'
                            else: m = f'spores--shadows:block/{block_id}_{t_half}_{hinge}{open_s}_stage_{stage}'
                                
                            for common in get_common_props():
                                variant_data = {'model': m}
                                if y_rot != 0: variant_data['y'] = y_rot
                                variants[f'facing={facing},half={half},hinge={hinge},open={open_state},powered={powered},stage={stage},{common}'] = variant_data
                        
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'variants': variants})

def gen_trapdoor(wood, prefix):
    block_id = f'moldy_{prefix}_trapdoor'
    variants = {}
    facings = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    for stage in [0, 1, 2, 3]:
        tex = f'minecraft:block/{prefix}_trapdoor' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_trapdoor_stage_{stage}'
        if stage > 0:
            write_json(os.path.join(models_dir, f'{block_id}_bottom_stage_{stage}.json'), {'parent': 'minecraft:block/template_orientable_trapdoor_bottom', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_top_stage_{stage}.json'), {'parent': 'minecraft:block/template_orientable_trapdoor_top', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_open_stage_{stage}.json'), {'parent': 'minecraft:block/template_orientable_trapdoor_open', 'textures': {'texture': tex}})
            gen_item_model(wood, f"{prefix}_trapdoor", f'{block_id}_bottom_stage_{stage}', stage)
        for facing, y in facings.items():
            for half in ['bottom', 'top']:
                for open_state in ['false', 'true']:
                    for powered in ['false', 'true']:
                        for waterlogged in ['false', 'true']:
                            m = ''
                            y_rot = y
                            x_rot = 0
                            if open_state == 'true':
                                if stage == 0: m = f'minecraft:block/{prefix}_trapdoor_open'
                                else: m = f'spores--shadows:block/{block_id}_open_stage_{stage}'
                            elif half == 'top':
                                if stage == 0: m = f'minecraft:block/{prefix}_trapdoor_top'
                                else: m = f'spores--shadows:block/{block_id}_top_stage_{stage}'
                            else:
                                if stage == 0: m = f'minecraft:block/{prefix}_trapdoor_bottom'
                                else: m = f'spores--shadows:block/{block_id}_bottom_stage_{stage}'
                                
                            for common in get_common_props():
                                variants[f'facing={facing},half={half},open={open_state},powered={powered},stage={stage},{common},waterlogged={waterlogged}'] = {'model': m, 'x': x_rot, 'y': y_rot}
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'variants': variants})

def gen_fence(wood, prefix):
    block_id = f'moldy_{prefix}_fence'
    multipart = []
    for stage in [0, 1, 2, 3]:
        tex = f'minecraft:block/{prefix}_planks' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_planks_stage_{stage}'
        if stage > 0:
            write_json(os.path.join(models_dir, f'{block_id}_post_stage_{stage}.json'), {'parent': 'minecraft:block/fence_post', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_side_stage_{stage}.json'), {'parent': 'minecraft:block/fence_side', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_inventory_stage_{stage}.json'), {'parent': 'minecraft:block/fence_inventory', 'textures': {'texture': tex}})
            gen_item_model(wood, f"{prefix}_fence", f'{block_id}_inventory_stage_{stage}', stage)
            
        m_post = f'minecraft:block/{prefix}_fence_post' if stage == 0 else f'spores--shadows:block/{block_id}_post_stage_{stage}'
        m_side = f'minecraft:block/{prefix}_fence_side' if stage == 0 else f'spores--shadows:block/{block_id}_side_stage_{stage}'
        
        for waterlogged in ['false', 'true']:
            for common in get_common_props():
                props = f"stage={stage},{common},waterlogged={waterlogged}"
                multipart.append({'when': {'stage': str(stage), 'structural': common.split(',')[0].split('=')[1], 'waxed': common.split(',')[1].split('=')[1], 'waterlogged': waterlogged}, 'apply': {'model': m_post}})
                multipart.append({'when': {'stage': str(stage), 'structural': common.split(',')[0].split('=')[1], 'waxed': common.split(',')[1].split('=')[1], 'waterlogged': waterlogged, 'north': 'true'}, 'apply': {'model': m_side, 'uvlock': True}})
                multipart.append({'when': {'stage': str(stage), 'structural': common.split(',')[0].split('=')[1], 'waxed': common.split(',')[1].split('=')[1], 'waterlogged': waterlogged, 'east': 'true'}, 'apply': {'model': m_side, 'y': 90, 'uvlock': True}})
                multipart.append({'when': {'stage': str(stage), 'structural': common.split(',')[0].split('=')[1], 'waxed': common.split(',')[1].split('=')[1], 'waterlogged': waterlogged, 'south': 'true'}, 'apply': {'model': m_side, 'y': 180, 'uvlock': True}})
                multipart.append({'when': {'stage': str(stage), 'structural': common.split(',')[0].split('=')[1], 'waxed': common.split(',')[1].split('=')[1], 'waterlogged': waterlogged, 'west': 'true'}, 'apply': {'model': m_side, 'y': 270, 'uvlock': True}})
        
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'multipart': multipart})

def gen_gate(wood, prefix):
    block_id = f'moldy_{prefix}_fence_gate'
    variants = {}
    facings = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    for stage in [0, 1, 2, 3]:
        tex = f'minecraft:block/{prefix}_planks' if stage == 0 else f'spores--shadows:block/moldy_{prefix}_planks_stage_{stage}'
        if stage > 0:
            write_json(os.path.join(models_dir, f'{block_id}_stage_{stage}.json'), {'parent': 'minecraft:block/template_fence_gate', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_open_stage_{stage}.json'), {'parent': 'minecraft:block/template_fence_gate_open', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_wall_stage_{stage}.json'), {'parent': 'minecraft:block/template_fence_gate_wall', 'textures': {'texture': tex}})
            write_json(os.path.join(models_dir, f'{block_id}_wall_open_stage_{stage}.json'), {'parent': 'minecraft:block/template_fence_gate_wall_open', 'textures': {'texture': tex}})
            gen_item_model(wood, f"{prefix}_fence_gate", f'{block_id}_stage_{stage}', stage)
            
        for facing, y in facings.items():
            for in_wall in ['false', 'true']:
                for open_state in ['false', 'true']:
                    for powered in ['false', 'true']:
                        m = ''
                        if in_wall == 'true':
                            if open_state == 'true': m = f'minecraft:block/{prefix}_fence_gate_wall_open' if stage == 0 else f'spores--shadows:block/{block_id}_wall_open_stage_{stage}'
                            else: m = f'minecraft:block/{prefix}_fence_gate_wall' if stage == 0 else f'spores--shadows:block/{block_id}_wall_stage_{stage}'
                        else:
                            if open_state == 'true': m = f'minecraft:block/{prefix}_fence_gate_open' if stage == 0 else f'spores--shadows:block/{block_id}_open_stage_{stage}'
                            else: m = f'minecraft:block/{prefix}_fence_gate' if stage == 0 else f'spores--shadows:block/{block_id}_stage_{stage}'
                            
                        for common in get_common_props():
                            variants[f'facing={facing},in_wall={in_wall},open={open_state},powered={powered},stage={stage},{common}'] = {'model': m, 'y': y, 'uvlock': True}
    write_json(os.path.join(blockstates_dir, f'{block_id}.json'), {'variants': variants})

for w in WOODS:
    p = 'bamboo' if w == 'bamboo' else w
    gen_slab(w, p)
    gen_stairs(w, p)
    gen_door(w, p)
    gen_trapdoor(w, p)
    gen_fence(w, p)
    gen_gate(w, p)

print('Done generating advanced JSONs')
