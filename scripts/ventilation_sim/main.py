import sys
import os
import signal
from pathlib import Path
import tkinter as tk

# Assicura che la directory ventilation_sim sia nel sys.path
SIM_DIR = Path(__file__).resolve().parent
if str(SIM_DIR) not in sys.path:
    sys.path.insert(0, str(SIM_DIR))

from gui import VoxelAdvancedApp

def main():
    root = tk.Tk()

    # Gestione chiusura pulita da terminale VS Code (Ctrl+C)
    def handle_sigint(sig, frame):
        print("\n[INFO] Ricevuto Ctrl+C. Chiusura pulita dell'applicazione...")
        try:
            root.destroy()
        except Exception:
            pass
        sys.exit(0)

    signal.signal(signal.SIGINT, handle_sigint)

    app = VoxelAdvancedApp(root)
    root.mainloop()

if __name__ == "__main__":
    main()