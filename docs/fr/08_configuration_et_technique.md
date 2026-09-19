# 💻 Configuration et Technique

Une référence destinée aux concepteurs de modpacks, administrateurs de serveurs et joueurs chevronnés désireux de comprendre ou d'ajuster le comportement du mod.

---

## 📖 Intégration JEI (Just Enough Items)

Le mod intègre 7 catégories JEI natives pour documenter ses mécaniques directement en jeu, sans recourir à un wiki externe :

1. **Cirage** — toutes les transformations bloc → bloc ciré à l'aide d'un rayon de miel
2. **Raclure** — retrait de la cire et résorption de la moisissure ($2 \rightarrow 1 \rightarrow 0$) à la hache
3. **Recyclage des Planches** — grilles de nettoyage et de refaçonnage des planches infectées
4. **Cycle d'Infection** — visualisation didactique de la progression ($0 \rightarrow 1 \rightarrow 2 \rightarrow 3$)
5. **Fiches Informatisées** — friabilité, anéantissement des drops, règles spéciales du stade 3
6. **Déshumidificateur** — consommations énergétiques, rayon d'action, modes opératoires
7. **Purificateur d'Air** — recettes des cartouches et rendements d'épuration

## 🔍 Intégration Jade / WTHIT

L'infobulle contextuelle affiche en temps réel pour chaque bloc ciblé :

- Stade d'infection fongique et présence de cire protectrice
- Risque d'Infection local ($R\%$) avec coloration dynamique (gris = stable, rouge = vulnérable)
- Niveau de remplissage des composteurs
- Statut énergétique et mode de fonctionnement actif des machines

## 🏆 Progrès (Advancements)

Le mod comprend un arbre complet de **11 progrès** répartis entre survie élémentaire, instrumentation de pointe et grands défis d'assainissement :

### 🌿 Survie et Traitement de la Moisissure
- **Spores & Shadows** *(Racine)* — Survivez à la décadence de la nature dans votre monde.
- **Prévention Naturelle** (*Natural Prevention*) — Utilisez un rayon de miel pour cirer un bloc de bois et arrêter la moisissure.
- **Huile de Coude** (*Elbow Grease*) — Grattez la moisissure d'un bloc de bois avec une hache.
- **Souffle Court** (*Short Breath*) — Subissez le poison du miasme en respirant trop de moisissure.
- **Poussière à Poussière** (*Dust to Dust*) — Tentez de briser un bloc de bois pourri (Stade 3) non ciré et regardez-le s'effriter dans le néant sans aucun drop.

### 🧭 Instrumentation et Surveillance
- **Détection d'Humidité** (*Moisture Detector*) — Fabriquez un Détecteur d'Humidité pour surveiller l'hygrométrie ambiante.
- **Sentinelle de l'Air** (*Air Sentry*) — Fabriquez un Détecteur de Spores pour surveiller la qualité de l'air et le miasme toxique.

### ⚙️ Ingénierie et Grands Défis d'Assainissement
- **Contrôle du Climat** (*Climate Control*) — Fabriquez un Déshumidificateur pour assécher des pièces et collecter de l'eau condensée.
- **Bunker Hermétique** (*Hermetic Bunker*) — Fabriquez un Purificateur d'Air pour assainir le miasme et rendre les pièces closes respirables.
- 🏆 **Oasis Souterraine** (*Dry Oasis*, Défi) — Asséchez une pièce souterraine ($Y \le 40$) sous 15 % d'humidité à l'aide d'un Déshumidificateur.
- 🏆 **Air Pur dans les Profondeurs** (*Pure Air in the Depths*, Défi) — Décontaminez entièrement une pièce souterraine infestée de moisissure ($Y \le 0$) en restaurant un air pur (`CLEAN`).

---

## ⚙️ Configuration (ModMenu & Cloth Config)

Spores & Shadows met à disposition 19 catégories de configuration ajustables à chaud depuis l'interface de ModMenu (enregistrées dans `config/spores_and_shadows.json`) :

| Catégorie | Ce qu'elle contrôle |
| :--- | :--- |
| **General** | Bascule du déclin global, rayon de balayage, seuil d'infection (défaut 0.50), usure des haches, rupture à l'usage des blocs (10%) |
| **Susceptibility** | Multiplicateurs de vulnérabilité par format ($S_{mat}$) : écorcé (1.4×), planches/escaliers/dalles/mosaïques (0.8×), défaut (1.0×) |
| **Catalysts** | Poids des catalyseurs ($C_{bonus}$) : boue (+0.05), podzol/mycélium (+0.15), champignons (+0.25), Spore Blossom (+0.80), blocs infectés (+0.03 / +0.06 / +0.12) |
| **Environment** | Humidité de base pluie/sec, gradient de profondeur, apport de l'eau, aération et vitesses de saturation/dissipation de l'humidité |
| **Drops** | Probabilité de drop sans Silk Touch : Stade 2 (50%) et Stade 3 (0%) |
| **Structures** | Pré-dégradation des structures vanilla générées dans le monde et bonus environnementaux (immersion, profondeur, contact au sol) |
| **Furnace Multipliers** | Pouvoir calorifique du combustible par stade (1.0×, 0.5×, 0.25×, 0.125×) |
| **Flammability** | Bonus d'allumage (+5, +10, +20) et de propagation des flammes (+10, +25, +60) pour les stades 1, 2 et 3 |
| **Blast Resistance** | Multiplicateurs de résistance aux explosions (TNT) par stade (80%, 50%, 10%) |
| **Hardness** | Échelonnage de la dureté des blocs (80%, 50%, 20%) |
| **Redstone** | Maintien prolongé des boutons/plaques et pourcentage de ratés des coffres piégés (15%, 50%, 85%) |
| **Composter** | Taux de réussite au composteur par stade (50%, 65%, 85%) |
| **Particles** | Dénombrement et types de particules émises lors de la destruction des blocs infectés |
| **Spore Detector** | Délais en ticks (initial/périodique), multiplicateur de signal Redstone (défaut 5× par stade), cooldown d'usage, propriétés du Masque |
| **Moisture Detector** | Délais en ticks (initial/périodique), multiplicateur de signal Redstone (défaut 5× par stade), cooldown d'usage de l'hygromètre portable |
| **Dehumidifier** | Capacité du réservoir (2000 mB), ticks par mB (24), efficacité combustible (4.0×), puissance déshumidifiante (1.0), capacité et conso FE |
| **Air Purifier** | Puissance d'épuration toxique (48.0), durée des cartouches Spore Filter (2400 ticks / 2 min), efficacité combustible (4.0×), capacité et conso FE |
| **Toxicity** | Volume BFS (2048 m³), rayon (16 blocs), ventilation des nœuds (Base 6/24), 3 seuils de toxicité (6, 12, 18), Masque et Spore Filtration |
| **Client** | Décalage Z anti-conflit de profondeur pour le rendu des blocs (0.002) et intensité des surcouches de moisissure dans les GUI (1.0) |

## 💻 Commandes Administratives

Nécessitent un niveau d'opérateur 2 :

- `/miasma` — Analyse BFS en temps réel : volume de la pièce, toxic score, ventilation active, classification du milieu (Ouvert / Confiné).
- `/moldrisk` — Décomposition exhaustive du Risque $R$ pour le bloc ciblé : $H_{eff}$, $L_{uv}$, $S_{mat}$, catalyseurs détectés, $M_{bonus}$, $T_{mult}$, valeur finale de $R$.

---

| | |
| :--- | ---: |
| [← Le Déclin dans les Moindres Détails](07_le_declin_dans_les_moindres_details.md) | [📑 Sommaire](README.md) |
