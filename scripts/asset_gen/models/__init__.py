from .dehumidifier_models import generate_dehumidifier_models_and_blockstates

def generate_all_models():
    """Genera tutti i file di modelli e blockstates JSON gestiti da script."""
    print("--- [Models] Generazione modelli e blockstates Deumidificatore... ---")
    generate_dehumidifier_models_and_blockstates()
    print("--- [Models] Completata con successo! ---")
