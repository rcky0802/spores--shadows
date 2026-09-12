import sys
import os
import numpy as np
import tkinter as tk
from tkinter import ttk, messagebox, filedialog
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure

from constants import (
    EMPTY,
    SOLID,
    START,
    SLAB_BOTTOM,
    SLAB_TOP,
    STAIRS_NORTH,
    STAIRS_SOUTH,
    STAIRS_WEST,
    STAIRS_EAST,
    COPPER_GRATE,
    DOOR_OPEN,
    DOOR_CLOSED,
    TRAPDOOR_OPEN,
    TRAPDOOR_CLOSED,
    FENCE,
    FENCE_GATE_OPEN,
    FENCE_GATE_CLOSED,
    WALL_CONNECTED,
    LEAVES,
    IRON_BARS,
    PORTAL_12,
    PORTAL_6,
    PORTAL_3,
    PORTAL_AUTO_24,
    PORTAL_12_5,
    PORTAL_6_25,
    PORTAL_AUTO_25
)
from voxel_world import VoxelWorld
from scenarios import (
    export_scenario_to_file,
    import_scenario_from_file
)


class VoxelAdvancedApp:
    """
    Interfaccia Grafica Tkinter + Visualizzazione 3D Max-Flow Network Studio (Dinic 3D).
    Replica fedele 1:1 dell'algoritmo di aerazione implementato in Java.
    """

    def __init__(self, window):
        self.root = window
        self.root.title("Voxel Max-Flow Network Studio (Dinic 3D Sky Absorption)")
        self.root.geometry("1480x900")
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

        self.world = VoxelWorld(10, 10, 5)
        self.animating = False
        self.active_layer_z = 0
        self.brush_mode = tk.StringVar(value="WALL")

        self.left_panel = ttk.Frame(self.root, padding=8, width=460)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)

        self.right_panel = ttk.Frame(self.root)
        self.right_panel.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        self._build_dimension_controls()
        self._build_editor_controls()
        self._build_flow_controls()
        self._build_3d_viewport()

        self.check_signals()
        self.redraw_all()

    def check_signals(self):
        self.root.after(200, self.check_signals)

    def on_close(self):
        self.animating = False
        self.root.destroy()
        sys.exit(0)

    # --- 1. CONFIGURAZIONE DIMENSIONI ---
    def _build_dimension_controls(self):
        group = ttk.LabelFrame(self.left_panel, text="1. Dimensioni Volume", padding=6)
        group.pack(fill=tk.X, pady=3)

        f = ttk.Frame(group)
        f.pack(fill=tk.X)
        ttk.Label(f, text="X:").grid(row=0, column=0, padx=2)
        self.spin_x = ttk.Spinbox(f, from_=3, to=18, width=3)
        self.spin_x.set(self.world.sx)
        self.spin_x.grid(row=0, column=1, padx=2)

        ttk.Label(f, text="Y:").grid(row=0, column=2, padx=2)
        self.spin_y = ttk.Spinbox(f, from_=3, to=15, width=3)
        self.spin_y.set(self.world.sy)
        self.spin_y.grid(row=0, column=3, padx=2)

        ttk.Label(f, text="Z:").grid(row=0, column=4, padx=2)
        self.spin_z = ttk.Spinbox(f, from_=2, to=8, width=3)
        self.spin_z.set(self.world.sz)
        self.spin_z.grid(row=0, column=5, padx=2)

        btn_grid = ttk.Frame(group)
        btn_grid.pack(fill=tk.X, pady=4)
        ttk.Button(btn_grid, text="Applica e Ricrea Griglia", command=self.apply_resize).pack(side=tk.LEFT, expand=True, fill=tk.X, padx=1)

        # Sezione Scenari & File
        file_frame = ttk.Frame(group)
        file_frame.pack(fill=tk.X, pady=2)
        ttk.Button(file_frame, text="📥 Importa (TXT/CSV)", command=self.import_scenario_action).pack(side=tk.LEFT, expand=True, fill=tk.X, padx=1)
        ttk.Button(file_frame, text="📤 Esporta (TXT/CSV)", command=self.export_scenario_action).pack(side=tk.LEFT, expand=True, fill=tk.X, padx=1)

    def apply_resize(self):
        try:
            x, y, z = int(self.spin_x.get()), int(self.spin_y.get()), int(self.spin_z.get())
            self.animating = False
            self.active_layer_z = 0
            self.world.resize(x, y, z)
            self._rebuild_grid_buttons()
            self.redraw_all()
        except ValueError:
            messagebox.showerror("Errore", "Dimensioni non valide.")

    def export_scenario_action(self):
        layouts_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "layouts")
        filepath = filedialog.asksaveasfilename(
            title="Esporta Scenario Voxel",
            initialdir=layouts_dir if os.path.exists(layouts_dir) else None,
            defaultextension=".txt",
            filetypes=[
                ("File di Testo (*.txt)", "*.txt"),
                ("File CSV (*.csv)", "*.csv"),
                ("Tutti i file (*.*)", "*.*")
            ]
        )
        if filepath:
            try:
                export_scenario_to_file(self.world, filepath)
                messagebox.showinfo("Esportazione Completata", f"Scenario salvato con successo in:\n{filepath}")
                self.lbl_status.config(text="Scenario esportato con successo.", foreground="#27ae60")
            except Exception as e:
                messagebox.showerror("Errore Esportazione", f"Impossibile salvare lo scenario:\n{e}")

    def import_scenario_action(self):
        layouts_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "layouts")
        filepath = filedialog.askopenfilename(
            title="Importa Scenario Voxel (TXT o CSV)",
            initialdir=layouts_dir if os.path.exists(layouts_dir) else None,
            filetypes=[
                ("File Scenari (*.txt, *.csv)", "*.txt *.csv"),
                ("File di Testo (*.txt)", "*.txt"),
                ("File CSV (*.csv)", "*.csv"),
                ("Tutti i file (*.*)", "*.*")
            ]
        )
        if filepath:
            try:
                import_scenario_from_file(self.world, filepath)
                self.spin_x.set(self.world.sx)
                self.spin_y.set(self.world.sy)
                self.spin_z.set(self.world.sz)
                self.active_layer_z = 0
                self.lbl_z.config(text=f"Piano Z Attivo: {self.active_layer_z}")
                self._rebuild_grid_buttons()
                self.update_score_labels()
                self.lbl_status.config(text=f"Scenario '{os.path.basename(filepath)}' importato!", foreground="#27ae60")
                self.redraw_all()
                messagebox.showinfo("Importazione Completata", f"Scenario importato con successo da:\n{filepath}")
            except Exception as e:
                messagebox.showerror("Errore Importazione", f"Impossibile importare lo scenario:\n{e}")

    # --- 2. EDITOR MATRICE & PENNELLI ---
    def _build_editor_controls(self):
        group = ttk.LabelFrame(self.left_panel, text="2. Strumenti e Blocchi Fisici", padding=6)
        group.pack(fill=tk.BOTH, expand=True, pady=3)

        # Riga 1: Muro, Start, Goal, Foglie
        r1 = ttk.Frame(group)
        r1.pack(fill=tk.X, pady=1)
        ttk.Radiobutton(r1, text="Muro (0)", value="WALL", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r1, text="Start", value="START", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r1, text="Goal", value="GOAL", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r1, text="Foglie (18)", value="LEAVES", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)

        # Riga 2: Mezzi Blocchi (Slab, Grate, Scale)
        r2 = ttk.Frame(group)
        r2.pack(fill=tk.X, pady=1)
        ttk.Radiobutton(r2, text="Slab B (12)", value="SLAB_B", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r2, text="Slab A (12)", value="SLAB_T", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r2, text="Scale (6)", value="STAIRS_N", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r2, text="Grata (18)", value="GRATE", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)

        # Riga 3: Porte e Botole
        r3 = ttk.Frame(group)
        r3.pack(fill=tk.X, pady=1)
        ttk.Radiobutton(r3, text="Porta Ap (18)", value="DOOR_O", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r3, text="Porta Ch (0)", value="DOOR_C", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r3, text="Botola Ap (18)", value="TRAP_O", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r3, text="Botola Ch (0)", value="TRAP_C", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)

        # Riga 4: Recinzioni, Sbarre & Muretti
        r4 = ttk.Frame(group)
        r4.pack(fill=tk.X, pady=1)
        ttk.Radiobutton(r4, text="Fence (8)", value="FENCE", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r4, text="Sbarre (8)", value="BARS", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r4, text="Canc Ap (18)", value="GATE_O", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r4, text="Canc Ch (8)", value="GATE_C", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)
        ttk.Radiobutton(r4, text="Mur Conn (0)", value="WALL_CONN", variable=self.brush_mode).pack(side=tk.LEFT, padx=2)

        z_nav = ttk.Frame(group)
        z_nav.pack(fill=tk.X, pady=3)
        ttk.Button(z_nav, text="◀ Z - 1 (Scendi)", command=lambda: self.change_z(-1)).pack(side=tk.LEFT)
        self.lbl_z = ttk.Label(z_nav, text=f"Piano Z Attivo: {self.active_layer_z}", font=("Segoe UI", 9, "bold"))
        self.lbl_z.pack(side=tk.LEFT, expand=True)
        ttk.Button(z_nav, text="Z + 1 ▶ (Sali)", command=lambda: self.change_z(1)).pack(side=tk.RIGHT)

        self.grid_frame = ttk.Frame(group)
        self.grid_frame.pack(pady=3)
        self.cell_buttons = {}
        self._rebuild_grid_buttons()

        ttk.Button(group, text="Pulisci Piano Attivo", command=self.clear_current_layer).pack(fill=tk.X, pady=1)
        ttk.Button(group, text="Pulisci Tutto", command=self.clear_all).pack(fill=tk.X, pady=1)

    def change_z(self, delta):
        new_z = self.active_layer_z + delta
        if 0 <= new_z < self.world.sz:
            self.active_layer_z = new_z
            self.lbl_z.config(text=f"Piano Z Attivo: {self.active_layer_z}")
            self.update_grid_button_colors()

    def _rebuild_grid_buttons(self):
        for w in self.grid_frame.winfo_children():
            w.destroy()
        self.cell_buttons.clear()

        for y in range(self.world.sy):
            for x in range(self.world.sx):
                btn = tk.Button(self.grid_frame, width=3, height=1, relief=tk.RAISED,
                                command=lambda cx=x, cy=y: self.cell_clicked(cx, cy))
                btn.grid(row=y, column=x, padx=1, pady=1)
                self.cell_buttons[(x, y)] = btn
        self.update_grid_button_colors()

    def cell_clicked(self, x, y):
        z = self.active_layer_z
        pos = (x, y, z)
        mode = self.brush_mode.get()

        if mode == "START":
            if pos in self.world.manual_starts:
                self.world.remove_start(x, y, z)
            else:
                self.world.add_start(x, y, z)
        elif mode == "GOAL":
            if pos in self.world.manual_goals:
                self.world.remove_goal(x, y, z)
            else:
                self.world.add_goal(x, y, z)
        elif mode == "WALL":
            if self.world.grid[pos] == SOLID:
                self.world.clear_cell(x, y, z)
            else:
                self.world.set_block(x, y, z, SOLID)
        elif mode == "LEAVES":
            self.world.set_block(x, y, z, LEAVES if self.world.grid[pos] != LEAVES else EMPTY)
        elif mode == "SLAB_B":
            self.world.set_block(x, y, z, SLAB_BOTTOM if self.world.grid[pos] != SLAB_BOTTOM else EMPTY)
        elif mode == "SLAB_T":
            self.world.set_block(x, y, z, SLAB_TOP if self.world.grid[pos] != SLAB_TOP else EMPTY)
        elif mode == "GRATE":
            self.world.set_block(x, y, z, COPPER_GRATE if self.world.grid[pos] != COPPER_GRATE else EMPTY)
        elif mode == "FENCE":
            self.world.set_block(x, y, z, FENCE if self.world.grid[pos] != FENCE else EMPTY)
        elif mode == "BARS":
            self.world.set_block(x, y, z, IRON_BARS if self.world.grid[pos] != IRON_BARS else EMPTY)
        elif mode == "GATE_O":
            self.world.set_block(x, y, z, FENCE_GATE_OPEN if self.world.grid[pos] != FENCE_GATE_OPEN else EMPTY)
        elif mode == "GATE_C":
            self.world.set_block(x, y, z, FENCE_GATE_CLOSED if self.world.grid[pos] != FENCE_GATE_CLOSED else EMPTY)
        elif mode == "WALL_CONN":
            self.world.set_block(x, y, z, WALL_CONNECTED if self.world.grid[pos] != WALL_CONNECTED else EMPTY)
        elif mode == "DOOR_O":
            self.world.set_block(x, y, z, DOOR_OPEN if self.world.grid[pos] != DOOR_OPEN else EMPTY)
        elif mode == "DOOR_C":
            self.world.set_block(x, y, z, DOOR_CLOSED if self.world.grid[pos] != DOOR_CLOSED else EMPTY)
        elif mode == "TRAP_O":
            self.world.set_block(x, y, z, TRAPDOOR_OPEN if self.world.grid[pos] != TRAPDOOR_OPEN else EMPTY)
        elif mode == "TRAP_C":
            self.world.set_block(x, y, z, TRAPDOOR_CLOSED if self.world.grid[pos] != TRAPDOOR_CLOSED else EMPTY)
        elif mode == "STAIRS_N":
            self.world.set_block(x, y, z, STAIRS_NORTH if self.world.grid[pos] != STAIRS_NORTH else EMPTY)

        self.update_grid_button_colors()
        self.update_score_labels()
        self.redraw_all()

    def update_grid_button_colors(self):
        z = self.active_layer_z
        for (x, y), btn in self.cell_buttons.items():
            pos = (x, y, z)
            val = self.world.grid[pos]

            if pos in self.world.manual_starts:
                b_flow = self.world.start_block_flows.get(pos, 0.0)
                if self.world.finished and b_flow > 0:
                    btn.config(bg="#2ecc71", fg="black", text=f"S:{b_flow:g}")
                else:
                    btn.config(bg="#2ecc71", fg="black", text="S")
            elif pos in self.world.manual_goals:
                btn.config(bg="#e74c3c", fg="white", text="G")
            elif pos in self.world.sky_goals:
                flow = self.world.node_flows.get(pos, 0.0)
                if flow > 0:
                    btn.config(bg="#00d2d3", fg="black", text=f"{flow:g}")
                else:
                    btn.config(bg="#c7ecee", fg="#0984e3", text="☀")
            elif val == SOLID:
                btn.config(bg="#2c3e50", fg="white", text="")
            elif val == WALL_CONNECTED:
                btn.config(bg="#34495e", fg="#e74c3c", text="MC")
            elif val == LEAVES:
                btn.config(bg="#27ae60", fg="white", text="LV")
            elif val == SLAB_BOTTOM:
                btn.config(bg="#f39c12", fg="white", text="SB")
            elif val == SLAB_TOP:
                btn.config(bg="#d35400", fg="white", text="ST")
            elif val == COPPER_GRATE:
                btn.config(bg="#e67e22", fg="white", text="GR")
            elif val == FENCE:
                btn.config(bg="#16a085", fg="white", text="FN")
            elif val == IRON_BARS:
                btn.config(bg="#7f8c8d", fg="white", text="IB")
            elif val == FENCE_GATE_OPEN:
                btn.config(bg="#2ecc71", fg="black", text="GO")
            elif val == FENCE_GATE_CLOSED:
                btn.config(bg="#e67e22", fg="black", text="GC")
            elif val == DOOR_OPEN:
                btn.config(bg="#27ae60", fg="white", text="DO")
            elif val == DOOR_CLOSED:
                btn.config(bg="#c0392b", fg="white", text="DC")
            elif val == TRAPDOOR_OPEN:
                btn.config(bg="#2ecc71", fg="black", text="TO")
            elif val == TRAPDOOR_CLOSED:
                btn.config(bg="#c0392b", fg="white", text="TC")
            elif val == STAIRS_NORTH:
                btn.config(bg="#8e44ad", fg="white", text="▲")
            elif val in (PORTAL_12, PORTAL_12_5):
                btn.config(bg="#e056fd", fg="white", text="12")
            elif val in (PORTAL_6, PORTAL_6_25):
                btn.config(bg="#22a6b3", fg="white", text="6")
            elif val == PORTAL_3:
                btn.config(bg="#7ed6df", fg="black", text="3")
            else:
                flow = self.world.node_flows.get(pos, 0.0)
                if flow > 0:
                    btn.config(bg="#fd79a8", fg="black", text=f"{flow:g}")
                else:
                    btn.config(bg="#ecf0f1", fg="black", text="")

    def clear_current_layer(self):
        z = self.active_layer_z
        for x in range(self.world.sx):
            for y in range(self.world.sy):
                pos = (x, y, z)
                if pos not in self.world.manual_starts:
                    self.world.clear_cell(x, y, z)
        self.world.reset_flow_network()
        self.update_grid_button_colors()
        self.redraw_all()

    def clear_all(self):
        for x in range(self.world.sx):
            for y in range(self.world.sy):
                for z in range(self.world.sz):
                    pos = (x, y, z)
                    if pos not in self.world.manual_starts:
                        self.world.clear_cell(x, y, z)
        self.world.reset_flow_network()
        self.update_grid_button_colors()
        self.redraw_all()

    # --- 3. CONTROLLI DINIC & FLUSSO ---
    def _build_flow_controls(self):
        group = ttk.LabelFrame(self.left_panel, text="3. Motore Dinic 3D (Sky Absorption)", padding=6)
        group.pack(fill=tk.X, pady=3)

        btn_row = ttk.Frame(group)
        btn_row.pack(fill=tk.X, pady=2)
        ttk.Button(btn_row, text="⚡ Calcola Max-Flow (Dinic)", command=self.solve_all).pack(side=tk.LEFT, expand=True, fill=tk.X, padx=1)
        ttk.Button(btn_row, text="Reset Rete", command=self.reset_flow_action).pack(side=tk.LEFT, expand=True, fill=tk.X, padx=1)

        score_box = ttk.Frame(group, relief=tk.GROOVE, padding=4)
        score_box.pack(fill=tk.X, pady=4)

        self.lbl_goals_summary = ttk.Label(score_box, text="Starts: 0 | Sky Goals: 0 | Voxel BFS: 0", font=("Segoe UI", 9, "bold"))
        self.lbl_goals_summary.pack(anchor="w")

        self.lbl_aeration_info = ttk.Label(score_box, text="Aerazione Globale: 0.0%", font=("Segoe UI", 10, "bold"), foreground="#2980b9")
        self.lbl_aeration_info.pack(anchor="w", pady=1)

        self.lbl_final_flow = ttk.Label(
            score_box,
            text="Flusso Totale: 0.0 / 0.0",
            font=("Segoe UI", 11, "bold"),
            foreground="#27ae60"
        )
        self.lbl_final_flow.pack(anchor="w", pady=2)

        self.lbl_starts_detail = ttk.Label(
            score_box,
            text="Dettaglio Sorgenti: -",
            font=("Segoe UI", 8),
            foreground="#34495e",
            justify=tk.LEFT
        )
        self.lbl_starts_detail.pack(anchor="w", pady=2)

        self.lbl_status = ttk.Label(group, text="Stato: Dinic 3D Pronto", foreground="#2980b9")
        self.lbl_status.pack(pady=2)

    def update_score_labels(self):
        total_req = getattr(self.world, 'total_requested', 0.0)
        if total_req <= 1e-4:
            total_req = self.world.calculate_total_requested() if hasattr(self.world, 'calculate_total_requested') else len(self.world.starts) * 25.0
        self.lbl_goals_summary.config(
            text=f"Starts: {len(self.world.starts)} | Sky Goals: {len(self.world.sky_goals)} | Voxel BFS: {len(getattr(self.world, 'indexed_positions', []))}"
        )
        self.lbl_aeration_info.config(
            text=f"Aerazione Globale: {self.world.normalized_aeration * 100.0:.1f}%"
        )
        self.lbl_final_flow.config(
            text=f"Flusso Totale: {self.world.total_max_flow:g} / {total_req:g}"
        )

        # Dettaglio per ciascuna sorgente START
        if hasattr(self.world, 'start_block_flows') and self.world.start_block_flows:
            lines = []
            for s in sorted(self.world.starts):
                fl = self.world.start_block_flows.get(s, 0.0)
                cp = self.world.start_block_caps.get(s, 0.0)
                sc = self.world.start_block_scores.get(s, 0.0)
                lines.append(f"• Start {s}: {fl:g}/{cp:g} ({sc:.1f}%)")
            self.lbl_starts_detail.config(text="\n".join(lines))
        else:
            self.lbl_starts_detail.config(text="Dettaglio Sorgenti: In attesa di calcolo")


    def solve_all(self):
        self.world.solve_all()
        self.update_score_labels()
        self.update_grid_button_colors()
        self.lbl_status.config(
            text=f"Dinic Risolto: {self.world.total_max_flow:g} ({self.world.normalized_aeration * 100.0:.1f}%)",
            foreground="#27ae60"
        )
        self.redraw_all()

    def reset_flow_action(self):
        self.world.reset_flow_network()
        self.update_score_labels()
        self.update_grid_button_colors()
        self.lbl_status.config(text="Stato: Rete reimpostata", foreground="#2980b9")
        self.redraw_all()

    # --- 4. VIEWPORT 3D (COLORI 1:1 CON JAVA) ---
    def _build_3d_viewport(self):
        self.fig = Figure(figsize=(7.5, 7.5), dpi=100)
        self.ax = self.fig.add_subplot(111, projection='3d')
        self.canvas = FigureCanvasTkAgg(self.fig, master=self.right_panel)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    def redraw_all(self):
        self.ax.clear()

        filled = np.zeros(self.world.grid.shape, dtype=bool)
        colors = np.zeros(self.world.grid.shape + (4,), dtype=float)

        for x in range(self.world.sx):
            for y in range(self.world.sy):
                for z in range(self.world.sz):
                    pos = (x, y, z)
                    val = self.world.grid[pos]

                    # 1. Starts / Emettitori (Verde Lime)
                    if pos in self.world.manual_starts:
                        filled[pos] = True
                        colors[pos] = [0.2, 1.0, 0.2, 0.95]
                    # 2. Sky Goals (Ciano)
                    elif pos in self.world.sky_goals:
                        flow = self.world.node_flows.get(pos, 0.0)
                        filled[pos] = True
                        if flow > 0:
                            colors[pos] = [0.0, 0.85, 1.0, 0.95]
                        else:
                            colors[pos] = [0.0, 0.85, 1.0, 0.35]
                    # 3. Solidi / Muri (Grigio Scuro)
                    elif val == SOLID:
                        filled[pos] = True
                        colors[pos] = [0.2, 0.2, 0.25, 0.80]
                    # 4. Portali / Mezzi Blocchi / Aperture (Oro / Giallo)
                    elif val in (SLAB_BOTTOM, SLAB_TOP, COPPER_GRATE, FENCE, DOOR_OPEN, TRAPDOOR_OPEN, PORTAL_12, PORTAL_6, PORTAL_3, PORTAL_AUTO_24, PORTAL_12_5, PORTAL_6_25, PORTAL_AUTO_25):
                        filled[pos] = True
                        colors[pos] = [1.0, 0.85, 0.0, 0.85]
                    # 5. Porte / Botole Chiuse (Rosso Scuro)
                    elif val in (DOOR_CLOSED, TRAPDOOR_CLOSED):
                        filled[pos] = True
                        colors[pos] = [0.7, 0.1, 0.1, 0.80]
                    # 6. Voxel con Flusso Attivo (Magenta)
                    elif pos in self.world.node_flows and self.world.node_flows[pos] > 1e-4:
                        filled[pos] = True
                        colors[pos] = [0.9, 0.2, 0.9, 0.75]
                    # 7. Voxel Esplorati BFS Passivi (Grigio Chiaro Trasparente)
                    elif pos in self.world.visited_voxels:
                        filled[pos] = True
                        colors[pos] = [0.75, 0.75, 0.85, 0.15]

        self.ax.voxels(filled, facecolors=colors, edgecolors='gray', linewidth=0.20)

        # Traccia le linee dei cammini aumentanti Dinic 3D
        for path, flow_val in self.world.active_paths:
            if len(path) >= 2:
                xs = [p[0] + 0.5 for p in path]
                ys = [p[1] + 0.5 for p in path]
                zs = [p[2] + 0.5 for p in path]
                self.ax.plot(xs, ys, zs, color='#e056fd', linewidth=2.5, marker='o', markersize=3)

        self.ax.set_xlim(0, self.world.sx)
        self.ax.set_ylim(0, self.world.sy)
        self.ax.set_zlim(0, self.world.sz)
        self.ax.set_xlabel('X')
        self.ax.set_ylabel('Y')
        self.ax.set_zlabel('Z')

        self.ax.set_title(f"Dinic 3D Sky Absorption | Max Flow: {self.world.total_max_flow:g} | Aeration: {self.world.normalized_aeration * 100.0:.1f}%")
        self.canvas.draw_idle()
