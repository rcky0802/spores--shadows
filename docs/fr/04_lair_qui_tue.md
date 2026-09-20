# ☠️ L'Air qui Tue

Le bois qui pourrit n'est pas un problème inerte : il libère activement des spores dans l'air ambiant. Dans des espaces clos et mal ventilés, cette contamination s'accumule jusqu'à devenir mortelle.

---

## 🌫️ Le Miasme Volumétrique

Le jeu évalue en permanence l'air entourant la tête du joueur à l'aide d'un algorithme BFS (Breadth-First Search) tridimensionnel.

- **Volume analysé** : jusqu'à **2048 m³** d'air contigu.
- **Rayon maximal** : **16 blocs** de distance euclidienne par rapport à la tête du joueur.
- **Cavernes ouvertes** : si le volume dépasse 2048 m³ sans rencontrer de parois closes, la zone est classée comme *ouverte* et le Miasme se dissipe instantanément — comme balayé par un courant d'air naturel.

### Ce qui bloque le Miasme & Scores de Ventilation (Base 6)

| Élément | Comportement | Score de Débit ($V$) |
| :--- | :--- | :---: |
| **Blocs pleins, blocs de verre** | Barrière hermétique étanche | `0.0` |
| **Portes fermées, trappes fermées** | Barrière hermétique | `0.0` |
| **Blocs submergés (waterlogged)** | **Siphon hydraulique** — barrière étanche absolue | `0.0` |
| **Vitrages/Panneaux de verre ($\ge 2$ côtés reliés)** | Fenêtre continue ou en coin : étanchéité hermétique | `0.0` |
| **Vitrages/Panneaux de verre ($1$ côté relié)** | Fenêtre partielle/ouverte : ventilation intermédiaire | **`+12.0`** / bloc |
| **Vitrages/Panneaux de verre (0 connexion)** | Panneau isolé en poteau : point ouvert | **`+24.0`** / bloc |
| **Vitrages/Panneaux de verre (en vertical)** | Axe UP / DOWN : l'air circule librement au-dessus/en-dessous | **`+24.0`** / bloc |
| **Murets ($\ge 2$ côtés reliés)** | Muret continu ou en coin : barrière hermétique | `0.0` |
| **Murets ($1$ côté relié)** | Muret partiel/saillant : ventilation intermédiaire | **`+12.0`** / bloc |
| **Murets (0 connexion)** | Poteau de muret isolé : ouverture réduite | **`+6.0`** / bloc |
| **Murets (en vertical)** | Axe UP / DOWN : l'air franchit librement par le dessus/dessous | **`+18.0`** / bloc |
| **Barrières / Fences (en vertical)** | Axe UP / DOWN : l'air circule librement | **`+18.0`** / bloc |
| **Barrières / Fences ($\ge 2$ côtés reliés)** | Espaces entre traverses (droite et gauche) | **`+12.0`** / bloc |
| **Barrières / Fences ($1$ côté relié ou 0)** | Ouverture large (un seul côté ou poteau isolé) | **`+18.0`** / bloc |
| **Ciel ouvert direct** | Cheminée atmosphérique naturelle | **`+24.0`** / bloc |
| **Portes/Trappes ouvertes, Grilles de cuivre, Feuilles** | Conduits de ventilation principaux | **`+18.0`** / bloc |
| **Dalles (Slabs)** | Conduits partiels semi-pleins | **`+12.0`** / bloc |
| **Escaliers (Stairs)** | Fissures et ouvertures mineures | **`+6.0`** / bloc |

La saturation de la pièce n'est pas instantanée : elle progresse avec une certaine inertie temporelle (`saturation_speed = 0.15`) lorsque les ouvertures sont scellées, et se dissipe bien plus vite (`dissipation_speed = 0.35`) dès qu'une simple fenêtre ou issue est dégagée.

### 🧮 Formule de Génération du Miasme

Dans tout environnement confiné, le miasme cible de la pièce ($M_{target}$) est calculé comme l'équilibre entre la production biologique et le renouvellement de l'air :

$$M_{target} = \max\Big(0.0, \ \text{Score}_{\text{toxique}} - \text{Score}_{\text{ventilation}} - \text{Puissance}_{\text{purificateurs}}\Big)$$

- **$\text{Score}_{\text{toxique}}$** : chaque bloc de bois infecté non ciré en contact direct avec l'air de la pièce émet des spores selon son stade :
  - 🟢 Stade 1 (Altéré) : **$+1.0$**
  - 🦠 Stade 2 (Moisi) : **$+2.0$**
  - ☠️ Stade 3 (Pourri) : **$+4.0$**  
  *(Les blocs cirés n'émettent aucune spore et ont une contribution strictement nulle).*
- **$\text{Score}_{\text{ventilation}}$** : débit d'air cumulé assuré par les ouvertures donnant sur l'extérieur (résolu par algorithme *Max-Flow*).
- **$\text{Puissance}_{\text{purificateurs}}$** : chaque Purificateur d'Air actif dans la pièce neutralise **$-48.0$** points de toxicité.

Le **Miasme Net ($M_{net}$)** converge dynamiquement vers $M_{target}$ à chaque cycle de mise à jour. La **Densité de Spores** détermine l'opacité du brouillard et la charge pulmonaire par $m^3$ :

$$\text{Densité} = \frac{M_{net}}{\text{Volume d'Air } (m^3)}$$

### Effets de Toxicité sur le Joueur (Seuils Base 6)

| Miasme Net | Densité de Spores | État et Symptômes sur le Joueur |
| :---: | :---: | :--- |
| **$\ge 2.0$** | $\ge 0.0417$ ($1/24$) | **Avertissement** : particules de mycélium visibles dans l'air, grondements organiques sourds |
| **$\ge 6.0$** | $\ge 0.0833$ ($2/24$) | **Danger** : effet de statut **Faim** (l'organisme brûle ses réserves énergétiques) |
| **$\ge 18.0$** | $\ge 0.1667$ ($4/24$) | **Létal** : déclenchement de **Nausée** suivi de **Poison Létal** |

---

| | |
| :--- | ---: |
| [← Construire avec la Pourriture](03_construire_avec_la_pourriture.md) | [Se Défendre →](05_se_defendre.md) |
| [📑 Sommaire](README.md) | |
