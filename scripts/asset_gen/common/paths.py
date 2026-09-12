from pathlib import Path

# Common directory anchors
COMMON_DIR = Path(__file__).resolve().parent
ASSET_GEN_DIR = COMMON_DIR.parent
SCRIPTS_DIR = ASSET_GEN_DIR.parent
PROJECT_ROOT = SCRIPTS_DIR.parent

# Asset folders
RESOURCES_DIR = PROJECT_ROOT / "src" / "main" / "resources"
ASSETS_DIR = RESOURCES_DIR / "assets" / "spores--shadows"

BLOCK_TEXTURES_DIR = ASSETS_DIR / "textures" / "block"
DEHUMIDIFIER_TEXTURES_DIR = BLOCK_TEXTURES_DIR / "dehumidifier"
AIR_PURIFIER_TEXTURES_DIR = BLOCK_TEXTURES_DIR / "air_purifier"
DETECTORS_TEXTURES_DIR = BLOCK_TEXTURES_DIR / "detectors"
MOLD_TEXTURES_DIR = BLOCK_TEXTURES_DIR / "mold"

BLOCK_MODELS_DIR = ASSETS_DIR / "models" / "block"
DEHUMIDIFIER_MODELS_DIR = BLOCK_MODELS_DIR / "dehumidifier"
BLOCKSTATES_DIR = ASSETS_DIR / "blockstates"
