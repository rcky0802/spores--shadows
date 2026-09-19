# 🎭 Le Déclin dans les Moindres Détails

La moisissure ne s'arrête pas à la surface des blocs. Elle s'immisce dans les interfaces utilisateur, déforme les sonorités ambiantes, compromet les vertus magiques des bibliothèques et — curieusement — n'entrave en rien le labeur des villageois.

---

## 🖥️ Dégradation Visuelle des Interfaces (Surcouches GUI)

Ouvrir un poste de travail infecté reflète fidèlement la décrépitude du bloc dans le monde, grâce à des surcouches graphiques en résolution native superposées à l'interface vanilla :

- **Établis** : la grille 3×3 se couvre d'auréoles organiques et de jointures gâtées.
- **Coffres et Tonneaux** : les rangées de cases (9×3 et 9×6) exhibent des bordures effilochées par les mousses.
- **Métiers à Tisser et Tables de Cartographie** : le canevas s'imbibe d'humidité sur le pourtour de l'interface.
- **Pupitres** : consulter un livre sur un pupitre infecté projette des moisissures organiques jusque dans les marges du texte.

Le rendu des coffres a également été soigné : l'habituel phénomène de *Z-fighting* (scintillement à la jonction du bâti et du couvercle) est éliminé au moyen d'un ajustement millimétrique de l'échelle du modèle 3D qui s'anime sans heurts à l'ouverture.

## 🎶 Altération Acoustique

Le champignon colonise les caisses de résonance, perturbant en profondeur le comportement acoustique des instruments :

- **Blocs Musicaux (Note Blocks)** : la percussion engendre des notes fausses, au ton assombri et rauque, avec des nuages de particules de spores remplaçant les notes colorées traditionnelles.
- **Jukebox** : les disques de musique vanilla sont joués avec un tempo ralenti et une hauteur de note dégradée, proportionnellement au degré d'infection. L'effet distordu est particulièrement oppressant — parfait pour les donjons ou les ambiances d'horreur.

## 📚 Dégradation des Bibliothèques : Magie et Butin

Les **Bibliothèques classiques** subissent une double déchéance sous l'assaut du mycélium : elles perdent leur puissance magique vis-à-vis de la Table d'Enchantement et, si elles sont brisées sans *Toucher de Soie*, laissent échapper un nombre décroissant de livres (la moisissure désagrégeant le papier et les reliures).

| Stade | Puissance d'Enchantement par bloc | Livres Lâchés à la Destruction *(Sans Toucher de Soie)* | Avec Toucher de Soie |
| :---: | :---: | :---: | :---: |
| **0 — Sain** *(ou Ciré)* | **1.0** (totale) | **3 livres** *(Vanilla)* | Confère la bibliothèque saine |
| **1 — Altéré** | **0.66** | **2 livres** | Confère la bibliothèque altérée |
| **2 — Moisi** | **0.33** | **1 livre** | Confère la bibliothèque moisie |
| **3 — Pourri** | **0.0** *(aucun apport)* | **0 livre** *(papier désagrégé)* | Confère la bibliothèque pourrie |

> [!NOTE]
> Si elles sont cirées (*Waxed*), les bibliothèques figent leur stade actuel : elles conservent le nombre de livres lâchés de leur palier et fournissent leur pleine contribution magique d'origine (1.0) si elles ont été cirées au Stade 0. Avec l'enchantement *Toucher de Soie* (*Silk Touch*), on récupère toujours le bloc de bibliothèque lui-même (ciré ou non) correspondant à son stade d'usure.

### 📖 Bibliothèques Sculptées (Chiseled Bookshelves)
Le comportement des **Bibliothèques Sculptées** est totalement distinct et préserve précieusement les biens du joueur :
- **Protection des Livres** : les volumes placés dans les rayonnages (livres classiques, écrits ou grimoires enchantés) restent protégés et intacts à 100 % quel que soit le niveau de décomposition, y compris lors du passage au stade Pourri ou lors des opérations de cirage et de raclage à la hache.
- **Destruction** : en cas de bris du meuble, tous les livres contenus sont éjectés intacts sur le sol (`ItemScatterer`), accompagnés du bloc de bibliothèque sculptée.
- **Comparateur** : le signal analogique Redstone émis à l'arrière (de 1 à 6 selon le dernier emplacement sollicité) respecte scrupuleusement la norme Vanilla, sans défaillance ni altération (voir également le [Chapitre 6](06_automatiser_lassainissement.md)).

## 👨‍🌾 Villageois et Postes de Travail

Malgré l'avancée de la moisissure, les villageois ne délaissent jamais leur poste de travail. Pêcheurs, Fermiers, Bergers, Cartographes, Fléchiers et Bibliothécaires reconnaissent nativement les meubles même au Stade 3 comme des postes valides ; ils s'y installent, y travaillent et négocient leurs échanges commerciaux sans la moindre incompatibilité.

## 🚪 Friabilité à l'Utilisation des Blocs Fonctionnels

Le bois pourri perd l'essentiel de sa cohésion mécanique et du maintien de ses gonds :
- **Risque de Rupture à l'Utilisation** : chaque fois qu'un joueur actionne un bloc fonctionnel non ciré de **Stade 3 (Pourri)** — qu'il s'agisse d'ouvrir une **porte**, de rabattre une **trappe**, d'ouvrir un **portillon** ou d'enfoncer un **bouton** en bois — il existe un risque de **10%** (`rotten_break_chance_on_use`) que le mécanisme se rompe sur-le-champ.
- En cas de bris, le bloc se fracasse dans un craquement sec de bois brisé (`BLOCK_WOOD_BREAK`) et se trouve **anéanti sans laisser aucun drop**.
- **Remède** : appliquer préventivement un rayon de miel (**cirage**) consolide l'assemblage et évite tout effondrement accidentel à l'usage.

---

| | |
| :--- | ---: |
| [← Automatiser l'Assainissement](06_automatiser_lassainissement.md) | [Configuration et Technique →](08_configuration_et_technique.md) |
| [📑 Sommaire](README.md) | |
