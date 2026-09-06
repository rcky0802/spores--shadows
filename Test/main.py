import sys
import signal
import tkinter as tk
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