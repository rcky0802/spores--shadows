# 🔬 Pourquoi le Bois Pourrit

Chaque bloc de bois dans le monde est évalué de manière autonome à chaque tick. Le résultat de cette évaluation est un nombre — le **Risque d'Infection ($R$)** — qui détermine si la décomposition progresse ou s'arrête.

$$R = \Big( (H_{eff} \cdot L_{uv} \cdot S_{mat}) + C_{bonus} + M_{bonus} \Big) \cdot T_{mult}$$

Si $R > 0.50$, le stade progresse. Sinon, le bloc reste stable. Chaque facteur correspond à une condition environnementale réelle.

---

## 💧 Humidité Effective ($H_{eff}$)

C'est le moteur principal de la décomposition. Pour tout bloc totalement submergé (*waterlogged*), $H_{eff} = 1.0$ (maximum absolu). Dans les autres cas, elle se calcule en combinant l'humidité atmosphérique locale, les catalyseurs et l'effet asséchant du vent :

$$H_{eff} = \text{clamp}\Big( H_{current} + C_{humidity} - A_{drying}, \ 0.0, \ 1.0 \Big)$$

Où l'humidité atmosphérique ($H_{current}$) converge dynamiquement vers la cible environnementale de la pièce :

$$H_{target} = \text{clamp}\Big( H_{base} + D_{depth} + W_{water} + H_{humidifier} - D_{dehumidifier}, \ 0.0, \ 1.0 \Big)$$

- **Humidité de Base du Biome ($H_{base}$)** : les biomes pluvieux ou enneigés partent de `0.80` ; les climats arides ou désertiques de `0.30`.
- **Modificateur de Profondeur ($D_{depth}$)** : en descendant sous $Y = 64$, il augmente graduellement jusqu'à atteindre son plafond maximal de $+0.40$ à la cote $Y \le 48$, restant constant dans tout l'Abîme (*Deepslate*) jusqu'à $Y = -64$.
- **Sources d'Eau dans la Pièce ($W_{water}$)** : chaque bloc d'eau présent dans la pièce ajoute $+0.15$ (jusqu'à un maximum de $+0.60$).
- **Machines ($H_{humidifier} / D_{dehumidifier}$)** : Brumiseurs ($+1.0$ ch.) ou Déshumidificateurs ($-1.0$ ch.).
- **Catalyseurs Locaux ($C_{humidity}$)** : les blocs adjacents gorgés d'eau (ex. boue ou chaudrons d'eau) ajoutent $+0.10$.
- **Séchage par Aération ($A_{drying}$)** : la ventilation locale assèche la surface du bloc : $A_{drying} = \text{Aération} \cdot 0.50$.

## ☀️ Lumière UV ($L_{uv}$)

La lumière agit comme un stérilisateur. Le niveau d'éclairement est échantillonné autour du bloc :
- **6 points** (les faces adjacentes) pour les blocs pleins et opaques (bûches, planches).
- **7 points** (les 6 faces + l'espace intérieur du bloc) pour les éléments non pleins ou ajourés (escaliers, dalles, barrières, portes, pancartes).

La moyenne de la lumière relevée est échelonnée entre `0.0` (lumière maximale 15 — stérilisation et infection bloquée) et `1.0` (obscurité totale 0 — plein risque). Un bloc bien éclairé dans une pièce ouverte présente un risque quasi nul ; ce même bloc dans une mine obscure est extrêmement vulnérable.

## 🪓 Susceptibilité du Matériau ($S_{mat}$)

Tous les blocs ne présentent pas la même vulnérabilité biologique :

| Catégorie | Multiplicateur ($S_{mat}$) | Détail des Blocs |
| :--- | :---: | :--- |
| **Bois Écorcé** | **1.4×** | Bûches, bois et tiges écorcés (`stripped_*`). Dépourvues de leur écorce protectrice, les fibres vives sont les plus vulnérables de toutes. |
| **Par Défaut / Bûches et Façonnés** | **1.0×** | Bûches avec écorce protectrice, mobilier, portes, trappes, barrières, pancartes, coffres et établis. |
| **Bois d'Œuvre de Construction** | **0.8×** | Planches (`*_planks`), Escaliers (`*_stairs`), Dalles (`*_slab`) et Mosaïque de bambou. Bois d'œuvre séché et équarri offrant une résistance structurelle supérieure. |

## 🌡️ Température et Fenêtre Biologique ($T_{mult}$)

Les spores ne prolifèrent que dans la « Fenêtre Biologique » ($0.15 \le \text{Temp} \le 1.50$). En dehors de cet intervalle, $T_{mult} = 0.0$ et la prolifération fongique s'arrête complètement :

- **Montagnes et Haute Altitude** :
  - Le refroidissement progressif commence en s'élevant au-dessus de **$Y = 128$**.
  - À mesure que l'on grimpe vers **$Y = 256$**, la température chute graduellement jusqu'à atteindre $-0.50$ (valeur qui demeure fixe jusqu'à la limite du monde à **$Y = 320$**).
  - La croissance des moisissures se **bloque complètement dès que la température descend sous $0.15$** (typiquement entre $Y \approx 180$ et $Y \approx 220$ selon le biome), préservant naturellement les chalets de montagne grâce au gel.
- **Souterrains et Cavernes** :
  - En descendant sous le niveau de la mer (**$Y = 64$**), la température se normalise vers le microclimat humide des grottes.
  - Dès **$Y \le 48$** et dans tout le Deepslate jusqu'à **$Y = -64$**, la température se stabilise invariablement à la valeur idéale de **`0.50`**, garantissant que les mines abandonnées pourrissent en permanence, indépendamment du climat de surface (même s'il s'agit d'un désert ou d'une toundra).

## ☣️ Catalyseurs Physiques ($C_{bonus}$) et Pression du Miasme ($M_{bonus}$)

Les blocs biologiques situés dans le rayon de balayage environnant (cube 3×3×3 autour du bloc) accélèrent l'infection en ajoutant un bonus direct au risque $R$, ou en augmentant l'humidité locale :

| Catalyseur | Bonus Risque ($C_{bonus}$) | Bonus Humidité Locale | Détails & Comportement |
| :--- | :---: | :---: | :--- |
| **Fleur de Spores** (`Spore Blossom`) | **+0.80** (+80%) | — | **Extrêmement létale** : franchit à elle seule le seuil d'infection (0.50). Fortement déconseillée en décoration près de poutres en bois ! |
| **Champignons** (rouges, bruns, blocs géants) | **+0.25** (+25%) | — | Les champignons au sol ou les blocs de champignon géant libèrent des spores continues par contact. |
| **Podzol & Mycélium** | **+0.15** (+15%) | — | Sols organiques gorgés d'hyphes fongiques souterraines. |
| **Boue** (`Mud`) | **+0.05** (+5%) | **+0.10** | Retient une forte humidité et accélère le pourrissement au pied des bâtiments. |
| **Chaudron d'Eau** | — | **+0.10** | Ajoute une humidité locale stagnante dans un rayon de 3 blocs. |
| **Bloc Altéré non ciré** (Stade 1) | **+0.03** ch. | — | Chaque bloc infecté voisin propage passivement la contamination aux blocs sains adjacents. |
| **Bloc Moisi non ciré** (Stade 2) | **+0.06** ch. | — | Pression de contagion doublée par rapport au stade 1. |
| **Bloc Pourri non ciré** (Stade 3) | **+0.12** ch. | — | Charge biologique infectieuse élevée pour tous les blocs mitoyens. |

> [!NOTE]
> Les blocs de bois **cirés** (`waxed`) ne font **pas** office de catalyseurs : le film de cire d'abeille scelle intégralement les spores et annule tout potentiel infectieux envers le voisinage.

### 🌫️ Pression du Miasme Aérien ($M_{bonus}$)
Au-delà du contact physique solide, le bois exposé à l'air stagnant d'une pièce saturée de miasme subit une contamination aéroportée constante :
$$M_{bonus} = \text{ExposureIndex} \cdot 0.50$$
Un environnement asphyxiant et toxique (détaillé au [Chapitre 4](04_lair_qui_tue.md)) pousse même le bois pourtant sec à succomber rapidement par infection aérienne.

---

| | |
| :--- | ---: |
| [← Le Monde qui Pourrit](01_le_monde_qui_pourrit.md) | [Construire avec la Pourriture →](03_construire_avec_la_pourriture.md) |
| [📑 Sommaire](README.md) | |
