import json
import os

vanilla_models = {
    "button": {
        "elements": [
            {   "from": [5, 0, 6],
                "to": [11, 2, 10],
                "faces": {
                    "down":  { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "up":    { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "north": { "uv": [ 5, 14, 11, 16 ], "texture": "#texture" },
                    "south": { "uv": [ 5, 14, 11, 16 ], "texture": "#texture" },
                    "west":  { "uv": [ 6, 14, 10, 16 ], "texture": "#texture" },
                    "east":  { "uv": [ 6, 14, 10, 16 ], "texture": "#texture" }
                }
            }
        ]
    },
    "button_pressed": {
        "elements": [
            {   "from": [5, 0, 6],
                "to": [11, 1, 10],
                "faces": {
                    "down":  { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "up":    { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "north": { "uv": [ 5, 15, 11, 16 ], "texture": "#texture" },
                    "south": { "uv": [ 5, 15, 11, 16 ], "texture": "#texture" },
                    "west":  { "uv": [ 6, 15, 10, 16 ], "texture": "#texture" },
                    "east":  { "uv": [ 6, 15, 10, 16 ], "texture": "#texture" }
                }
            }
        ]
    },
    "button_inventory": {
        "display": {
            "gui": {
                "rotation": [30, 225, 0],
                "translation": [0, 0, 0],
                "scale": [0.625, 0.625, 0.625]
            },
            "ground": {
                "rotation": [0, 0, 0],
                "translation": [0, 3, 0],
                "scale": [0.25, 0.25, 0.25]
            },
            "fixed": {
                "rotation": [0, 0, 0],
                "translation": [0, 0, 0],
                "scale": [0.5, 0.5, 0.5]
            },
            "thirdperson_righthand": {
                "rotation": [75, 45, 0],
                "translation": [0, 2.5, 0],
                "scale": [0.375, 0.375, 0.375]
            },
            "firstperson_righthand": {
                "rotation": [0, 45, 0],
                "translation": [0, 0, 0],
                "scale": [0.4, 0.4, 0.4]
            },
            "firstperson_lefthand": {
                "rotation": [0, 225, 0],
                "translation": [0, 0, 0],
                "scale": [0.4, 0.4, 0.4]
            }
        },
        "elements": [
            {   "from": [5, 6, 7],
                "to": [11, 10, 9],
                "faces": {
                    "down":  { "uv": [ 5, 7, 11, 9 ], "texture": "#texture" },
                    "up":    { "uv": [ 5, 7, 11, 9 ], "texture": "#texture" },
                    "north": { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "south": { "uv": [ 5, 6, 11, 10 ], "texture": "#texture" },
                    "west":  { "uv": [ 7, 6, 9, 10 ], "texture": "#texture" },
                    "east":  { "uv": [ 7, 6, 9, 10 ], "texture": "#texture" }
                }
            }
        ]
    },
    "pressure_plate_up": {
        "elements": [
            {   "from": [ 1, 0, 1 ],
                "to": [ 15, 1, 15 ],
                "faces": {
                    "down":  { "uv": [ 1, 1, 15, 15 ], "texture": "#texture" },
                    "up":    { "uv": [ 1, 1, 15, 15 ], "texture": "#texture" },
                    "north": { "uv": [ 1, 15, 15, 16 ], "texture": "#texture" },
                    "south": { "uv": [ 1, 15, 15, 16 ], "texture": "#texture" },
                    "west":  { "uv": [ 1, 15, 15, 16 ], "texture": "#texture" },
                    "east":  { "uv": [ 1, 15, 15, 16 ], "texture": "#texture" }
                }
            }
        ]
    },
    "pressure_plate_down": {
        "elements": [
            {   "from": [ 1, 0, 1 ],
                "to": [ 15, 0.5, 15 ],
                "faces": {
                    "down":  { "uv": [ 1, 1, 15, 15 ], "texture": "#texture" },
                    "up":    { "uv": [ 1, 1, 15, 15 ], "texture": "#texture" },
                    "north": { "uv": [ 1, 15.5, 15, 16 ], "texture": "#texture" },
                    "south": { "uv": [ 1, 15.5, 15, 16 ], "texture": "#texture" },
                    "west":  { "uv": [ 1, 15.5, 15, 16 ], "texture": "#texture" },
                    "east":  { "uv": [ 1, 15.5, 15, 16 ], "texture": "#texture" }
                }
            }
        ]
    }
}

out_dir = "src/main/resources/assets/spores--shadows/models/block"
os.makedirs(out_dir, exist_ok=True)

def inflate(element):
    res = {}
    res["from"] = [x - 0.005 for x in element["from"]]
    res["to"] = [x + 0.005 for x in element["to"]]
    faces = {}
    for face, val in element["faces"].items():
        faces[face] = {"uv": val["uv"], "texture": "#overlay"}
    res["faces"] = faces
    return res

for name, model in vanilla_models.items():
    new_elements = []
    for el in model["elements"]:
        new_elements.append(el)
        new_elements.append(inflate(el))
    
    out_model = {
        "textures": {
            "particle": "#texture"
        },
        "elements": new_elements
    }
    
    if "display" in model:
        out_model["display"] = model["display"]
        
    out_path = os.path.join(out_dir, f"moldy_{name}.json")
    with open(out_path, "w") as f:
        json.dump(out_model, f, indent=4)
