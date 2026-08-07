import os
import json

out_dir = "src/main/resources/data/spores--shadows/loot_tables/blocks"
os.makedirs(out_dir, exist_ok=True)

WOODS = ["oak"]

def write_loot_table(block_name):
    path = os.path.join(out_dir, f"{block_name}.json")
    
    # We drop the block itself for stages 0 (if somehow silk touched), 1, 2, 3.
    # Actually, a block with STAGE property will just drop the item variant that corresponds to its stage!
    # Wait, in Minecraft 1.21, block states (like STAGE) can be preserved on the item using "minecraft:copy_state".
    # This means ONE loot table can handle ALL stages if the item drops itself and copies the STAGE property!
    
    base = block_name.replace("moldy_", "")
    
    loot_json = {
      "type": "minecraft:block",
      "pools": [
        {
          "bonus_rolls": 0.0,
          "entries": [
            {
              "type": "minecraft:item",
              "name": f"spores--shadows:rotten_{base}",
              "functions": [{"function": "minecraft:copy_state", "block": f"spores--shadows:{block_name}", "properties": ["waxed"]}],
              "conditions": [
                {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "3"}},
                {"condition": "minecraft:any_of", "terms": [
                  {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"waxed": "true"}},
                  {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
                ]}
              ]
            },
            {
              "type": "minecraft:item",
              "name": f"spores--shadows:moldy_{base}",
              "functions": [{"function": "minecraft:copy_state", "block": f"spores--shadows:{block_name}", "properties": ["waxed"]}],
              "conditions": [
                {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "2"}},
                {"condition": "minecraft:any_of", "terms": [
                  {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"waxed": "true"}},
                  {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
                ]}
              ]
            },
            {
              "type": "minecraft:item",
              "name": f"spores--shadows:moldy_{base}",
              "functions": [{"function": "minecraft:copy_state", "block": f"spores--shadows:{block_name}", "properties": ["waxed"]}],
              "conditions": [
                {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "2"}},
                {"condition": "minecraft:inverted", "term": {
                  "condition": "minecraft:any_of", "terms": [
                    {"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"waxed": "true"}},
                    {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
                  ]
                }},
                {"condition": "minecraft:random_chance", "chance": 0.5}
              ]
            },
            {
              "type": "minecraft:item",
              "name": f"spores--shadows:tainted_{base}",
              "functions": [{"function": "minecraft:copy_state", "block": f"spores--shadows:{block_name}", "properties": ["waxed"]}],
              "conditions": [{"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "1"}}]
            },
            {
              "type": "minecraft:item",
              "name": f"spores--shadows:waxed_{base}",
              "conditions": [{"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "0", "waxed": "true"}}]
            },
            {
              "type": "minecraft:item",
              "name": f"minecraft:{base}",
              "conditions": [{"condition": "minecraft:block_state_property", "block": f"spores--shadows:{block_name}", "properties": {"stage": "0", "waxed": "false"}}]
            }
          ],
          "rolls": 1.0,
          "conditions": [
            {
              "condition": "minecraft:survives_explosion"
            }
          ]
        }
      ]
    }

    # For doors, we must check if the half is lower, to avoid dropping two doors.
    if "door" in block_name and "trapdoor" not in block_name:
        loot_json["pools"][0]["conditions"].insert(0, {
            "condition": "minecraft:block_state_property",
            "block": f"spores--shadows:{block_name}",
            "properties": {
              "half": "lower"
            }
        })

    with open(path, 'w', encoding='utf-8') as f:
        json.dump(loot_json, f, indent=2)

def main():
    for w in WOODS:
        for prefix in [w]:
            blocks = [
                f"moldy_{prefix}_log", f"moldy_stripped_{prefix}_log",
                f"moldy_{prefix}_wood", f"moldy_stripped_{prefix}_wood",
                f"moldy_{prefix}_planks", f"moldy_{prefix}_stairs",
                f"moldy_{prefix}_slab", f"moldy_{prefix}_fence",
                f"moldy_{prefix}_fence_gate", f"moldy_{prefix}_door",
                f"moldy_{prefix}_trapdoor"
            ]
            
            for b in blocks:
                write_loot_table(b)

    print("Loot tables generated successfully!")

if __name__ == "__main__":
    main()
