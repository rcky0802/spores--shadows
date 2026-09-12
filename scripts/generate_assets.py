#!/usr/bin/env python3
"""
CLI unificata per la generazione automatizzata di risorse (Texture pixel-art e Modelli JSON)
per il mod Minecraft 'Spores & Shadows'.

Utilizzo:
    python scripts/generate_assets.py --all
    python scripts/generate_assets.py --textures
    python scripts/generate_assets.py --models
    python scripts/generate_assets.py --block dehumidifier
    python scripts/generate_assets.py --block purifier
"""
import sys
import os
import argparse
from pathlib import Path

# Assicura che la directory degli script sia nel sys.path per importare asset_gen
SCRIPTS_DIR = Path(__file__).resolve().parent
if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

from asset_gen.textures import (
    generate_all_textures,
    generate_dehumidifier_textures,
    generate_air_purifier_textures
)
from asset_gen.models import (
    generate_all_models,
    generate_dehumidifier_models_and_blockstates
)

def main():
    parser = argparse.ArgumentParser(description="Generatore di Asset per Spores & Shadows")
    parser.add_argument("--all", action="store_true", help="Rigenera tutte le texture e tutti i modelli (predefinito se nessun'altra opzione)")
    parser.add_argument("--textures", action="store_true", help="Rigenera solo le texture procedurali PNG")
    parser.add_argument("--models", action="store_true", help="Rigenera solo i modelli e blockstates JSON")
    parser.add_argument("--block", choices=["all", "dehumidifier", "purifier"], default="all",
                        help="Filtra la generazione per uno specifico blocco (default: all)")

    args = parser.parse_args()

    # Se nessuna operazione specifica è richiesta, default a --all
    run_all = args.all or (not args.textures and not args.models)

    print("==================================================")
    print("      SPORES & SHADOWS - ASSET GENERATION         ")
    print("==================================================")

    # 1. Texture
    if run_all or args.textures:
        print("\n[Fase: Textures]")
        if args.block in ("all", "dehumidifier"):
            print(" -> Generazione texture Deumidificatore...")
            generate_dehumidifier_textures()
        if args.block in ("all", "purifier"):
            print(" -> Generazione texture Depuratore d'Aria...")
            generate_air_purifier_textures()

    # 2. Modelli
    if run_all or args.models:
        print("\n[Fase: Modelli e Blockstates]")
        if args.block in ("all", "dehumidifier"):
            print(" -> Generazione modelli Deumidificatore...")
            generate_dehumidifier_models_and_blockstates()

    print("\n==================================================")
    print("  Operazione completata con successo!              ")
    print("==================================================")

if __name__ == "__main__":
    main()
