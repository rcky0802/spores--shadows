# 🤿 Se Défendre

Connaître les risques ne suffit pas : il faut disposer des bons outils. Le mod introduit un ensemble cohérent de mécaniques de défense, de la prévention passive à l'équipement de protection actif.

---

## 🐝 Cirage Préventif

Appliquer un **Rayon de Miel** sur n'importe quel bloc de bois le scelle d'une pellicule de cire, figeant définitivement son stade actuel.

Un bloc ciré :
- Ne se dégrade plus, quelles que soient les conditions environnementales
- N'émet aucune spore dans l'air ambiant
- Ne peut pas contaminer les blocs adjacents
- Droppe toujours à **100 %** lorsqu'il est brisé, même au Stade 3

La cire ne guérit pas le bloc — elle le conserve dans son état actuel. Un bloc Pourri ciré reste Pourri, mais cesse d'être une source de prolifération.

## 🪓 Traitement à la Hache

En mode **Furtif (Sneak) + Clic Droit** en tenant une hache, il est possible d'intervenir directement sur le bloc :

- **Décirage** : retire la couche de cire (`ITEM_AXE_WAX_OFF`), réactivant le cycle biologique pour un coût de 1 point de durabilité.
- **Raclure de Moisissure** : sur du bois non ciré de Stade 1 ou 2, la hache retire les hyphes superficielles (`ITEM_AXE_SCRAPE`), faisant régresser l'infection d'un stade ($2 \rightarrow 1 \rightarrow 0$) pour un coût de 1 point de durabilité.
- **Stade 3 (Pourri) — Totalement Incurable** : la structure interne est irrémédiablement compromise. La hache n'a aucun effet sur le bois pourri. La seule manière de le rendre inerte sans le détruire est de le sceller avec un rayon de miel (cirage).

---

## 😷 Le Masque Anti-Spores

Le `Spore Mask` est l'unique équipement garantissant une survie passive dans un miasme prolongé. Il s'équipe à l'emplacement du casque et présente un modèle 3D en relief avec visière, respirateurs latéraux et cartouche filtrante.

**Protection & Combat** : neutralise intégralement les effets létaux du miasme (Faim, Nausée, Poison). Il fait également office d'armure légère (confère **1 point d'armure**, équivalent à un casque en cuir, pour **165 points de durabilité**) : il s'abîme normalement en recevant des coups en **combat**, tout en consommant 1 point de durabilité à chaque cycle où il filtre l'air toxique à la place des poumons du joueur.

**Réparation** : s'effectue exclusivement avec des **Filtres à Spores (Spore Filters)** sur une enclume — fabriqués avec de la laine, du charbon de bois et de la ficelle. Chaque filtre restaure 100 % de la durabilité. Alternativement, combiner deux masques usés dans la grille d'artisanat offre une réparation d'urgence sur le terrain.

### 🔮 Enchantabilité du Masque Anti-Spores
Le Masque Anti-Spores possède une **Enchantabilité = 0** (il ne peut pas être enchanté sur la Table d'Enchantement). Il reçoit des enchantements **exclusivement via des livres enchantés sur une Enclume**, sous réserve de strictes restrictions de compatibilité :

| Enchantement | Compatibilité Masque | Effet sur le Masque |
| :--- | :---: | :--- |
| **Solidité (Unbreaking I–III)** | ✅ **Autorisé** | Réduit la probabilité d'usure, aussi bien face aux attaques qu'en filtrant l'air. |
| **Raccommodage (Mending)** | ✅ **Autorisé** | Répare la durabilité du masque en ramassant des orbes d'expérience. |
| **Malédiction de Disparition (Vanishing)** | ✅ **Autorisé** | Le masque disparaît à la mort du joueur au lieu de tomber au sol. |
| **Filtration de Spores (Spore Filtration)** | ❌ **Incompatible** | **Non applicable** : le masque filtre déjà nativement le miasme ; cet enchantement est superflu. |
| **Protection / Apnée / Affinité aquatique / Épines** | ❌ **Incompatible** | Rejetés : le masque est un respirateur technique et non un heaume de guerre enchanté. |

---

### ✨ Enchantement pour Casques : Filtration de Spores (`Spore Filtration`)

`Spore Filtration` est un enchantement conçu sur mesure pour **tout casque conventionnel** (cuir, fer, diamant, Netherite, carapace de tortue). Il permet aux porteurs d'armures standard de respirer en toute sérénité dans le miasme sans avoir à revêtir le Masque Anti-Spores, en reportant la charge toxique sur la durabilité du casque :

| Niveau | Usure de Durabilité par Cycle | Rendement d'Économie | Comportement |
| :---: | :---: | :---: | :--- |
| **I** | **2 points** / cycle | Standard | Filtration brute : neutralise le miasme mais use rapidement le casque. |
| **II** | **1 point** / cycle | Optimisée | Filtration équilibrée : égale l'efficacité d'usure du Masque Anti-Spores de base. |
| **III** | **0 ou 1 point** (moyenne 0.5) | **50% de Sauvegarde** | Filtration avancée : **50% de chances d'annuler la consommation de durabilité** à chaque cycle d'exposition. |

> [!TIP]
> Appliqué sur un casque de haute résistance (tel qu'un heaume en Netherite doté de *Solidité III* et *Raccommodage*), `Spore Filtration III` permet d'explorer et de guerroyer dans les zones à miasme létal tout en profitant de l'armure maximale d'une panoplie lourde !

---

## 🧭 Détecteurs Portatifs

Pour inspecter des milieux inconnus ou préparer vos chantiers d'assainissement :

**💧 Détecteur d'Humidité** — Tenu en main principale et activé dans le vide (clic droit), il s'enclenche avec un claquement mécanique et transmet dans le chat privé un bilan analytique du Risque d'Infection local ($H_{eff}$, luminosité, température, catalyseurs adjacents). Idéal pour comprendre instantanément pourquoi une pièce ne cesse de pourrir.

**☢️ Détecteur de Spores** — Tenu en main et activé dans le vide (clic droit), il se déclenche avec un clic mécanique et opère un scan instantané de l'air au niveau des yeux du joueur (volume de la pièce, ventilation active, densité de spores, tendance dynamique). Il demeure totalement silencieux lors de vos déplacements (aucun cliquetis passif continu), assurant une discrétion absolue en exploration.

*L'utilisation de ces appareils en poste fixe — comme capteurs Redstone hybrides au mur, au sol ou au plafond — est développée au [Chapitre 6](06_automatiser_lassainissement.md).*

---

| | |
| :--- | ---: |
| [← L'Air qui Tue](04_lair_qui_tue.md) | [Automatiser l'Assainissement →](06_automatiser_lassainissement.md) |
| [📑 Sommaire](README.md) | |
