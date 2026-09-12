from .dehumidifier_textures import generate_dehumidifier_textures
from .air_purifier_textures import generate_air_purifier_textures

def generate_all_textures(out_base=None):
    """Genera tutte le texture procedurali per tutti i macchinari."""
    print("--- [Textures] Generazione texture Deumidificatore... ---")
    generate_dehumidifier_textures(out_dir=out_base / "dehumidifier" if out_base else None)
    print("--- [Textures] Generazione texture Depuratore d'Aria... ---")
    generate_air_purifier_textures(out_dir=out_base / "air_purifier" if out_base else None)
    print("--- [Textures] Completata con successo! ---")
