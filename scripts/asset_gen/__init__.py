from .textures import (
    generate_all_textures,
    generate_dehumidifier_textures,
    generate_air_purifier_textures
)
from .models import (
    generate_all_models,
    generate_dehumidifier_models_and_blockstates
)

def generate_all_assets():
    """Genera sia tutte le texture procedurali che tutti i modelli/blockstates."""
    print("==================================================")
    print("      SPORES & SHADOWS - ASSET GENERATION         ")
    print("==================================================")
    generate_all_textures()
    print()
    generate_all_models()
    print("==================================================")
    print("  TUTTI GLI ASSET SONO STATI GENERATI CON SUCCESSO ")
    print("==================================================")
