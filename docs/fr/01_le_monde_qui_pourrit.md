# 🌳 Le Monde qui Pourrit

Quand vous installez Spores & Shadows, le monde de Minecraft ne change pas d'apparence du jour au lendemain. Il change au fil du temps — et souvent, on s'en aperçoit trop tard.

Le mod remplace de manière transparente chaque bloc de bois par une variante dynamique. L'effet est invisible au départ : le bloc conserve la même apparence, les mêmes propriétés. Mais les conditions environnementales agissent sur lui à chaque tick, et tôt ou tard, la moisissure l'emporte.

---

## 🪵 Ce qui se Dégrade

Tous les blocs appartenant à n'importe quelle essence de bois de Minecraft sont soumis au cycle : bûches, planches, escaliers, dalles, barrières, portillons, portes, trappes, plaques de pression, boutons, pancartes — dans l'ensemble des 11 essences de bois présentes dans le jeu. Aucun format n'est immunisé.

## 🦠 Les Quatre Stades

La décomposition est une séquence unidirectionnelle et inexorable si l'on n'intervient pas :

| Stade | Nom | Apparence | Risque d'Infection |
| :---: | :--- | :--- | :--- |
| **0** | Sain (Vanilla) | Apparence originelle du bloc | Aucun |
| **1** | Altéré | Légères taches de mycélium en surface | Modéré |
| **2** | Moisi | Hyphes denses, couleur terne, surface organique | Élevé |
| **3** | Pourri | Structure effondrée, poussière fongique, texture désagrégée | Irréversible |

La transition d'un stade au suivant s'opère lors des *random block ticks* du serveur chaque fois que le Risque d'Infection dépasse le seuil de **50 %** (voir [Chapitre 2](02_pourquoi_le_bois_pourrit.md)).

Chaque stade existe également en variante **cirée** : un bloc scellé avec de la cire d'abeille fige sa décomposition dans son stade actuel, mais ne revient pas en arrière.

### 🔊 Immersion Sensorielle : Sons et Particules Personnalisés
La décomposition n'est pas seulement une texture différente : chaque stade possède sa propre identité sensorielle :
- **Audio de Destruction** : briser des blocs dégradés (Stades 2 et 3) produit un son sourd et déchirant de rupture organique (`BLOCK_FUNGUS_BREAK`), remplaçant le claquement sec classique du bois sain.
- **Nuages de Spores** : détruire des blocs aux stades avancés sans *Toucher de Soie* (*Silk Touch*) libère une explosion visuelle de particules fongiques dans l'air (spores aériennes, spores tombantes et fragments de mycélium — 42 particules au Stade 2 et pas moins de 80 au Stade 3).

## 🗺️ Les Structures Générées dans le Monde

Les structures naturelles apparaissent déjà pré-vieillies, en fonction de leur histoire environnementale simulée :

- **Dégradation Critique** — Épaves, Huttes de Sorcière : présence massive de Stade 3.
- **Dégradation Élevée** — Mines abandonnées, Villages Zombies, Ruines : mélange de Stades 1 et 2.
- **Dégradation Modérée** — Avant-postes de Pillards, Portails en Ruine : principalement Stade 1.
- **Dégradation Minimale** — Villages, Manoirs des Bois : quasi intacts.

Les arbres vivants sont immunisés tant qu'ils ne sont pas abattus. Dès qu'un tronc tombe, la dégradation peut commencer.

---

| | |
| :--- | ---: |
| [📑 Sommaire](README.md) | [Pourquoi le Bois Pourrit →](02_pourquoi_le_bois_pourrit.md) |
