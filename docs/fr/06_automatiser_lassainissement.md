# ⚙️ Automatiser l'Assainissement

Les détecteurs portatifs et le cirage préventif répondent aux situations courantes. Pour les vastes structures, les réseaux souterrains ou les bases autonomes où le joueur ne peut être constamment présent, le mod met à disposition un système d'automatisation complet : capteurs permanents, machines industrielles et réseaux Redstone.

---

## 📡 Capteurs Redstone Permanents (Système Hybride)

Les deux détecteurs peuvent être fixés dans n'importe quelle orientation sur des blocs solides (**sol, mur ou plafond**). En plus de leur affichage visuel et diagnostique, ils intègrent un **système Redstone hybride** (Émission Directe + Sortie Comparateur) :

**💧 Détecteur d'Humidité (posé)** — Surveille l'humidité effective $H_{eff}$ et le microclimat local :
- **Cadran Visuel** : 4 paliers gradués (0 = Sec, 1 = Moite, 2 = Humide, 3 = Critique).
- **Signal Direct** : Émet une puissance Redstone proportionnelle au palier (**0, 5, 10, 15**), alimentant directement la poudre de redstone adjacente, les lampes de signalisation, les machines ou le bloc support mural (permettant de dissimuler les câblages derrière la cloison).
- **Prise en Charge Comparateur** : Tout comparateur adjacent lit nativement le même signal (0, 5, 10, 15) pour des logiques analogiques ou des seuils de précision.
- **Interrogation** : En effectuant un clic droit sur le bloc (à la main ou avec un outil), l'appareil émet un cliquetis mécanique et transmet dans le chat un rapport diagnostique complet.

**☢️ Détecteur de Spores (posé)** — Analyse périodiquement le volume BFS d'air de la pièce :
- **Cadran Visuel** : 4 paliers d'alerte toxique (0 = Sain, 1 = Attention, 2 = Faim, 3 = Poison Létal).
- **Signal Direct** : Émet une puissance Redstone échelonnée (**0, 5, 10, 15**), permettant de mettre en route automatiquement Déshumidificateurs et Purificateurs d'Air dès que le miasma s'accumule, sans nécessiter de répéteurs ou de circuits intermédiaires.
- **Prise en Charge Comparateur** : Parfaitement interfaçable avec des comparateurs pour concevoir des alarmes à seuil et des circuits d'urgence industriels.
- **Interrogation** : D'un clic droit, il s'enclenche avec un cliquetis métallique et transmet en chat privé la télémétrie volumétrique et la tendance du miasme (accumulation, stabilisation ou purification active).

## 💣 Mécanique Redstone : Coffres Piégés et Ratés

La moisissure altère également les mécanismes d'émission Redstone intégrés aux blocs de bois.

**Boutons en Bois** — Les filaments emprisonnent le ressort de l'axe, multipliant la durée de maintien de la pression :
- Stade 0 (Sain / Ciré) : **1,5 seconde** (30 ticks — standard Vanilla)
- Stade 1 (Altéré) : **3,0 secondes** (60 ticks)
- Stade 2 (Moisi) : **7,5 secondes** (150 ticks)
- Stade 3 (Pourri) : **22,5 secondes** (450 ticks) — désynchronise totalement n'importe quel timing de circuit

**Plaques de Pression en Bois** — La biomasse fongique ralentit la détente de la plaque après le passage d'une entité :
- Stade 0 (Sain / Ciré) : **1,0 seconde** (20 ticks — standard Vanilla)
- Stade 1 (Altéré) : **2,0 secondes** (40 ticks)
- Stade 2 (Moisi) : **5,0 secondes** (100 ticks)
- Stade 3 (Pourri) : **15,0 secondes** (300 ticks) — le signal persiste pendant 15 secondes après le passage

**Coffres Piégés — Blocage Mécanique (*Jamming*)** — La moisissure oxyde les lamelles de la charnière interne. À chaque ouverture, il existe un risque croissant de *raté* (absence d'émission de signal avec un déclic à vide) :
- Stade 1 (Altéré) : **15%** de probabilité de raté
- Stade 2 (Moisi) : **50%** de probabilité de raté
- Stade 3 (Pourri) : **85%** de probabilité de raté

L'état du mécanisme est intentionnellement dissimulé aux HUDs externes (Jade/WTHIT) pour préserver l'effet de surprise.

**Bibliothèques Sculptées et Comparateurs** — Les livres conservés à l'intérieur traversent la décomposition indemnes à tous les stades. Le signal analogique émis par le comparateur à l'arrière (de 1 à 6 selon le dernier emplacement manipulé) demeure strictement déterministe et conforme au comportement Vanilla, sans la moindre perte de signal.

---

## 🌀 Déshumidificateur

La machine essentielle pour l'assainissement hygrométrique actif. Tandis que le Purificateur intervient en aval en neutralisant le Miasme déjà formé, le Déshumidificateur agit en amont en prévenant l'apparition et la prolifération de toute moisissure par assèchement de l'air ambiant.

- **Puissance d'Assèchement (1.0)** — Chaque Déshumidificateur actif applique une puissance d'assèchement de **1.0 point** à l'algorithme hygrométrique de la pièce :
  $$H_{\text{target}} = \max(0.0, \, H_{\text{raw}} - 1.0 \times N_{\text{déshumidificateurs}})$$
  Puisque l'humidité brute naturelle $H_{\text{raw}}$ est comprise entre $0.0$ et $1.0$, un seul déshumidificateur en fonction suffit à abaisser l'humidité effective $H_{\text{eff}}$ à **$0.0$ ($0\%$)** dans n'importe quelle pièce scellée jusqu'à $2048\text{ m}^3$, métamorphosant les lieux en une oasis désertique totalement immunisée contre la pourriture.
- **Alimentation Hybride (32 000 FE / Combustible 4.0×)** — Consomme **10 FE/tick** en fonctionnement actif ($0\text{ FE/tick}$ en veille/éteint). Accepte la recharge par câbles électriques (jusqu'à 500 FE/t sur n'importe quelle face) ou le combustible solide dans l'Emplacement 0 avec un rendement quadruplé ($4.0\times$, un morceau de charbon génère $64\ 000\text{ FE}$, saturant l'intégralité du réservoir énergétique).
- **Réservoir de Condensation & Modes Opératoires (2 000 mB)** — Doté d'un réservoir de fluides interne :
  - **Mode Déshumidification** : absorbe activement l'humidité et emmagasine de l'eau condensée au rythme de **1 mB tous les 24 ticks** de base (la condensation s'accélère dans les pièces très humides).
  - **Veille Réservoir Plein (FULL)** : une fois la capacité maximale de 2 000 mB atteinte (2 seaux d'eau), la machine se met en veille pour ne gaspiller ni énergie ni combustible.
  - **Mode Brumisation (Humidify)** : polarité inversée ; vaporise l'eau du réservoir (1 mB tous les 24 ticks) pour injecter de l'humidité dans l'air ($+1.0$). Elle s'arrête automatiquement si l'humidité de la pièce atteint 98% ($0.98$). Très utile pour exploiter des champignonnières contrôlées.
- **Automatisation par Entonnoirs et Tuyaux (SidedInventory & Fluid Transfer)** :
  - **Combustible Solide** : un entonnoir peut insérer du carburant depuis **n'importe quelle face** du bloc dans l'Emplacement 0.
  - **Extraction/Remplissage d'Eau** : prend en charge le soutirage/vidange manuel au seau (1 000 mB par clic droit) et la connexion aux conduits de fluides automatisés via la *Fabric Transfer API*.
- **Interface, Redstone et Comparateur** :
  - **Contrôle Redstone** : bascule à trois états (*Toujours Actif* `IGNORED`, *Actif avec Redstone* `HIGH`, *Désactivé avec Redstone* `LOW`). Relié à un Détecteur d'Humidité mural, il s'enclenche uniquement dès que l'air franchit le seuil critique.
  - **Sortie Comparateur** : émet un signal analogique de **$0$ à $15$** proportionnel au niveau de remplissage du réservoir d'eau interne (0 si vide, 15 si plein à 2 000 mB).

---

## 🌬️ Purificateur d'Air

La machine incontournable pour l'éradication active de la toxicité atmosphérique. Alors que le Déshumidificateur prévient l'infection en asséchant l'air, le Purificateur intervient pour neutraliser directement les spores volatiles du Miasme dans les pièces fermées déjà contaminées.

- **Puissance de Purification (48.0)** — Chaque Purificateur actif déduit **48.0 points** de la charge toxique volumétrique de la pièce :
  $$\text{targetMiasma} = \max(0.0, \, \text{toxicScore} - \text{ventilationScore} - 48.0 \times N_{\text{purificateurs}})$$
  Puisqu'une bûche moisie génère environ $2.25$ points de toxicité, un unique purificateur compense les émanations de **plus de 21 blocs infectés simultanément**, anéantissant le Miasme dans des pièces closes atteignant jusqu'à $2048\text{ m}^3$.
- **Alimentation Hybride (32 000 FE / Combustible 4.0×)** — Consomme **10 FE/tick** pendant la filtration active ($0\text{ FE/tick}$ en veille ou filtre épuisé). Accepte l'alimentation électrique par câbles (jusqu'à 500 FE/t sur toute face) ou le combustible solide dans l'Emplacement 0 avec un rendement quadruplé ($4.0\times$, un morceau de charbon fournit $64\ 000\text{ FE}$, remplissant le buffer de 32 000 FE).
- **Cartouches Filtre à Spores & Usure Dynamique (Spore Filters)** — Placées dans l'Emplacement 1 (empilables jusqu'à 64 unités) :
  - **Durabilité de Base** : **2 400 ticks (2 minutes en continu)** par filtre à raison de 1 point/tick.
  - **Rechargement Automatique** : dès qu'une cartouche s'épuise, la machine charge instantanément le filtre suivant de la pile de réserve.
  - **Usure Accélérée sous Miasme Létal** : si la concentration de la pièce atteint le stade critique *LETHAL_POISON*, l'usure double à **2 points/tick** (durée ramenée à 60 secondes) face à l'afflux massif de microspores.
  - **Alerte d'Épuisement (FILTER_DEPLETED)** : à court de filtres, la machine coupe la ventilation et émet un clic métallique à vide (`BLOCK_DISPENSER_FAIL`).
- **Automatisation par Entonnoirs (SidedInventory)** :
  - **Face Supérieure (UP)** : l'entonnoir insère *exclusivement* les **Filtres à Spores** (Emplacement 1).
  - **Faces Latérales et Inférieure (NORTH, SOUTH, EAST, WEST, DOWN)** : acceptent *exclusivement* le **Combustible solide** (Emplacement 0).
- **Interface, Redstone et Comparateur** :
  - **Contrôle Redstone** : bascule à trois états (*Toujours Actif* `IGNORED`, *Actif avec Redstone* `HIGH`, *Désactivé avec Redstone* `LOW`). Relié à un Détecteur de Spores mural, il s'allume automatiquement en présence de miasme et s'arrête dès que la zone est assainie, évitant l'usure inutile des filtres.
  - **Sortie Comparateur** : émet un signal analogique de **$0$ à $15$** proportionnel au nombre de filtres restants dans l'emplacement (0 si vide, 15 pour une pile pleine de 64), idéal pour alimenter des voyants d'avertissement de réapprovisionnement.

---

| | |
| :--- | ---: |
| [← Se Défendre](05_se_defendre.md) | [Le Déclin dans les Moindres Détails →](07_le_declin_dans_les_moindres_details.md) |
| [📑 Sommaire](README.md) | |
