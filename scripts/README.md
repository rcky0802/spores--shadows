# Spores & Shadows - Tooling & Python Scripts

Questa cartella raccoglie gli strumenti di supporto in Python per il modding, la generazione procedurale di asset e la simulazione degli algoritmi del mod.

---

## 📁 Struttura della Cartella

```text
scripts/
├── README.md                      # Questa guida
├── generate_assets.py             # CLI unificata per la generazione di Texture e Modelli
│
├── asset_gen/                     # Generazione procedurale di Texture e Modelli JSON
│   ├── common/                    # Risoluzione percorsi (paths.py) e scocca condivisa (machine_casing.py)
│   ├── textures/                  # Generatori PNG per Deumidificatore e Depuratore d'Aria
│   └── models/                    # Generatori JSON per modelli e blockstates (Deumidificatore)
│
└── ventilation_sim/               # Simulatore interattivo Voxel & Flusso Dinic 3D (ex Test)
    ├── README.md                  # Documentazione tecnica sull'algoritmo Max-Flow
    ├── main.py                    # Entry point applicazione grafica (Tkinter + Matplotlib)
    ├── run.bat                    # Launcher rapido per Windows
    ├── layouts/                   # Scenari e geometrie salvate (Casa.txt, Tunnel.txt, ecc.)
    └── *.py                       # Motore algoritmico (Dinic, BFS, World, GUI)
```

---

## 🎨 1. Generazione Asset (`generate_assets.py`)

Richiede Python 3.10+ e la libreria `Pillow`:
```bash
pip install pillow
```

### Comandi Disponibili:
```bash
# Rigenera tutto (Tutte le texture + Tutti i modelli JSON)
python scripts/generate_assets.py --all

# Rigenera solo le texture procedurali PNG
python scripts/generate_assets.py --textures

# Rigenera solo i modelli e blockstates JSON
python scripts/generate_assets.py --models

# Rigenera solo le risorse di uno specifico blocco
python scripts/generate_assets.py --block dehumidifier
python scripts/generate_assets.py --block purifier
```

Tutti i percorsi sono risolti dinamicamente a partire dalla root del repository: il comando può essere eseguito da qualsiasi directory di lavoro.

---

## 🌪️ 2. Simulatore di Ventilazione (`ventilation_sim`)

Simulatore visivo interattivo (2D/3D) per sperimentare la dinamica dei fluidi, la saturazione volumetrica, i portali e l'algoritmo di Flusso Massimo (Dinic 3D) per l'aerazione degli ambienti.

### Requisiti:
```bash
pip install numpy matplotlib
```

### Avvio:
- **Windows (Doppio Clic)**: eseguire `scripts/ventilation_sim/run.bat`
- **Terminale**:
  ```bash
  python scripts/ventilation_sim/main.py
  ```

Gli scenari di test possono essere importati/esportati direttamente dalla cartella `scripts/ventilation_sim/layouts/`.
