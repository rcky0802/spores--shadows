# 🧱 Construire avec la Pourriture

Dès lors que le bois a commencé à pourrir, sa valeur en tant que matériau s'effondre rapidement. Le mod impose des répercussions concrètes sur l'artisanat, la résistance physique et l'utilisation comme combustible au four — le tout selon une logique interne rigoureuse : plus le bois est altéré, moins il est exploitable.

---

## 🧱 Dégradation Mécanique, Thermique et Friabilité

À mesure que les hyphes fongiques consomment la cellulose et la lignine, le bois perd sa cohésion structurelle et emprisonne des poussières sèches et friables. L'ensemble de ses propriétés physiques et thermiques se dégrade en parallèle :

| Propriété | 🌲 Stade 0 (Sain) | 🟢 Stade 1 (Altéré) | 🦠 Stade 2 (Moisi) | ☠️ Stade 3 (Pourri) |
| :--- | :---: | :---: | :---: | :---: |
| **Dureté du Bloc** | `2.0` (100%) | `1.6` (80%) | `1.0` (50%) | `0.4` (20%) |
| **Résistance aux Explosions (TNT)** | 100% | 80% | 50% | **10%** |
| **Efficacité des Outils** | Normale (Hache) | Normale (Hache) | Normale (Hache) | **Annulée (Poing = Hache)** |
| **Drop en Survie** | `100%` | `100%` | `50%` (Moitié perdue) | **`0%` (Effritement)** |
| **Drop avec Toucher de Soie / Cire** | `100%` | `100%` | `100%` | `100%` |
| **Bonus d'Allumage au Feu** | $+0$ (Vanilla) | $+5$ | $+10$ | $+20$ |
| **Bonus de Propagation des Flammes** | $+0$ (Vanilla) | $+10$ | $+25$ | $+60$ |
| **Pouvoir Combustible (Four)** | `100%` (1.0×) | `50%` (0.5×) | `25%` (0.25×) | `12.5%` (0.125×) |
| **Probabilité de Compostage** | — (Non compostable) | `50%` | `65%` | **`85%`** (Excellent fertilisant) |

> [!WARNING]
> **Stade 3 — Extrême Friabilité** : Briser un bloc pourri annule totalement l'avantage conféré par la hache : qu'il soit frappé avec une hache en Netherite ou à mains nues, l'opération prendra la même durée, et le bloc s'effritera sans laisser le moindre drop (à moins d'utiliser *Toucher de Soie* ou de l'avoir ciré au préalable).  
> **Cuisson du Charbon de Bois** : Les bûches infectées (Stades 1, 2, 3 — normales comme cirées) **ne peuvent pas être cuites pour produire du charbon de bois** : la matière fongique dégradée empêche la carbonisation. Seules les bûches saines de Stade 0 de l'Overworld (vanilla ou cirées saines) peuvent être carbonisées au four pour obtenir du charbon de bois. Les bûches infectées peuvent uniquement servir de combustible (avec une efficacité réduite).

---

## 📐 Règles d'Artisanat

**Seules les planches saines ou cirées peuvent servir à fabriquer des objets complexes** (portes, coffres, escaliers, établis, etc.). Les planches infectées sont refusées dans les recettes d'objets finis.

La conversion d'une bûche infectée en planches saines applique une **division par deux progressive** :

| Bûche | Planches Saines Obtenues |
| :--- | :---: |
| Saine / Cirée | 4 |
| Altérée | 2 |
| Moisie | 1 |
| Pourrie | 0 — irrécupérable |

**Recyclage des planches dans la grille** : si vous avez démonté une structure ancienne et récupéré des planches infectées, vous pouvez les assainir sur un établi :
- 2 Planches Altérées → 1 Planche Saine
- 4 Planches Moisies → 1 Planche Saine

*Les variantes normales et cirées d'un même stade peuvent être combinées librement dans la grille.*

---

| | |
| :--- | ---: |
| [← Pourquoi le Bois Pourrit](02_pourquoi_le_bois_pourrit.md) | [L'Air qui Tue →](04_lair_qui_tue.md) |
| [📑 Sommaire](README.md) | |
