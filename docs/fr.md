# Spores & Shadows 

**Spores & Shadows** est un mod pour Minecraft (Fabric 1.21.1) qui introduit un écosystème dynamique, réaliste et implacable de pourriture environnementale pour le bois. Aucune structure n'est à l'abri du temps et des éléments !

---

## 🌳 Aperçu et Contenu

Avez-vous déjà construit une majestueuse cabane en bois en pensant qu'elle resterait intacte, défiant les siècles, sans aucun besoin d'entretien ? **Spores & Shadows** révolutionne cette certitude, transformant le bois d'un simple bloc inerte en un matériau vivant, vulnérable et réactif à son environnement.

Le mod remplace de manière totalement transparente et silencieuse chaque morceau de bois placé par le joueur (ou généré naturellement dans les structures comme les épaves et les mines) par une variante "dormante". Avec le temps, les agents atmosphériques comme la pluie, l'humidité, l'obscurité et même le biome dans lequel vous vous trouvez scelleront le destin de vos constructions, vous forçant à protéger vos bâtiments ou à assister impuissant à leur déclin inexorable.

### 🔢 Détails Techniques et Blocs Ajoutés

Sur le plan technique, le mod injecte un écosystème complet pour chaque variante individuelle de bois.

* **🧱 13 Formats Architecturaux** : *Bûches*, *Bûches Écorcées*, *Bois*, *Bois Écorcé*, *Planches*, *Escaliers*, *Dalles*, *Barrières*, *Portillons*, *Portes*, *Trappes*, *Plaques de Pression*, *Boutons*.

Pour chacun des 104 formats de base en bois, le mod ajoute **3 variantes moisies** (Touché, Moisi, Pourri). De plus, pour chacun de ces blocs — y compris le bloc Vanilla original — la **variante cirée** respective est créée.

Ainsi, le jeu met à disposition pas moins de **728 variantes uniques et obtenables en Survie** :
1. Les **104 Variantes Vanilla Cirées** : La copie protégée et cirée du bloc Vanilla de base.
2. Les **312 Variantes Moisies** : Les trois stades naturels de pourriture.
3. Les **312 Variantes Moisies Cirées** : Les blocs dégradés mais figés dans le temps par la cire.

Ce système vous permet de récupérer des blocs partiellement moisis en survie pour ensuite les "sceller" avec des rayons de miel, pouvant ainsi les utiliser en toute sécurité à des fins décoratives sans risquer d'infecter les constructions voisines.

---

## 🦠 Le Cycle de la Moisissure

Le bois traverse 4 stades de pourriture : **Vanilla (0) ➔ Touché (1) ➔ Moisi (2) ➔ Pourri (3)**.

La progression ne se produit que si le "Risque d'Infection" (`R`), recalculé en permanence, dépasse le seuil fixe de **0.4**. La formule exacte est :
`R = ((Humidité * Lumière * Susceptibilité) + Contagion) * Température`

### Facteurs et Variables Exactes
* 💧 **Humidité (Climat + Profondeur + Eau)** : 
  - La valeur de base dépend des précipitations du biome (Pluie/Neige : `0.8`, Sec : `0.3`). 
  - **Malus de Profondeur** : En descendant sous le niveau de la mer (`Y < 64`), l'humidité augmente vertigineusement de `+0.01` pour chaque bloc de descente, rendant les mines des environnements extrêmement humides.
  - **Malus Local** : La proximité de blocs d'eau (`+0.15`) ou de chaudrons (`+0.10`) ajoute une humidité supplémentaire au bloc.
* ☀️ **Lumière (UV)** : Évolue linéairement de `0.0` (Lumière 15, bloque totalement l'infection) à `1.0` (Obscurité totale).
* 🪓 **Susceptibilité du Matériau** : Le bois écorcé est extrêmement vulnérable (`x1.4`), les bûches brutes sont standard (`x1.0`), tandis que le bois transformé en planches résiste légèrement mieux (`x0.8`).
* 🌡️ **Température (Biome + Altitude/Profondeur)** : 
  - Agit comme un filtre de survie. La moisissure prolifère **uniquement** si la température locale est comprise entre `0.15` et `1.5`.
  - **En Surface** : Dépend du biome. Les climats extrêmes comme les déserts ou les glaciers bloquent totalement l'infection en fixant le facteur à `0.0`.
  - **Sous terre (`Y < 64`)** : Indépendamment du biome de surface, la température se normalise progressivement en descendant, se stabilisant à la valeur parfaite de `0.5` (doux) sous `Y=48`. Même dans un désert ou un biome glacé, les cavernes profondes développeront de la moisissure !
  - **Haute Altitude (`Y > 128`)** : En prenant de l'altitude, la température chute progressivement, gelant à `-0.5` au niveau `Y=256`. Construire des chalets en haute montagne préservera le bois presque partout.
* ☣️ **Contagion (Catalyseurs)** : Ajoute un malus direct si le bois est en contact avec des agents infectieux :
  - Bois infecté : Touché (`+0.05`), Moisi (`+0.10`), Pourri (`+0.20`).
  - Environnement : Boue (`+0.05`), Podzol/Mycélium (`+0.15`), Champignons (`+0.25`), Fleur Sporifère (`+0.80`).

---

## 🛠️ Interactions et Prévention

Le joueur n'est pas sans défense face à la nature. En vous équipant du bon outil et en agissant en **mode furtif (Sneaking / Maj)**, vous pouvez interagir directement avec l'état vital du bois. 
*(Le mode furtif est obligatoire pour éviter de cirer ou de gratter par erreur les blocs interactifs, comme les portes, les trappes ou les boutons).*

* 🪓 **Utilisation de la Hache (Grattage)** : En faisant *Maj + Clic Droit* avec une hache :
  - Si le bloc est **Ciré**, la hache retirera la couche de cire, restaurant le cycle vital normal.
  - Si le bloc est **Touché ou Moisi**, la hache grattera la couche superficielle de champignons, réduisant la dégradation d'un stade. Un bloc au Stade 1 redeviendra parfaitement propre (Stade 0 Vanilla).
  *(Chaque grattage consomme normalement de la durabilité).*
* 🐝 **Utilisation du Rayon de Miel (Cirage)** : En faisant *Maj + Clic Droit* avec un rayon de miel sur un bloc à *n'importe quel* stade, il deviendra **Ciré**. Le bois ciré est scellé : il devient immunisé contre les dommages environnementaux, fige sa dégradation à l'infini et perd sa capacité à infecter les blocs voisins. 

*Fonctionnalité Intelligente : Si vous effectuez ces actions sur un bloc multiple (comme la moitié supérieure ou inférieure d'une Porte), la mise à jour sera appliquée instantanément et en totale synchronisation à l'ensemble de la structure !*

---

## ☠️ Dangers Environnementaux (Miasme)
- ✨ **Particules de Spores** : Les blocs au stade **Moisi** ou **Pourri** émettent des spores depuis leurs faces exposées (désactivé sous l'eau).
- 🤢 **Miasme Toxique** : Le mod scanne un rayon de 4 blocs autour du joueur. Chaque bloc ajoute son stade de décomposition au score total de toxicité :
  - **Touché** : +1
  - **Moisi** : +2
  - **Pourri** : +3
  - *(Les blocs cirés sont inoffensifs et comptent pour 0).*
  
  **Effets** :
  - Score > **15** : **Nausée**
  - Score > **35** : **Nausée + Poison**

---

## ⚖️ Pénalités et Artisanat

Utiliser du bois pourri pour l'artisanat n'est pas sage. La structure interne du matériau est irrémédiablement compromise, introduisant de sévères malus qui punissent la paresse :

* 💥 **Intégrité Structurelle (Loot) et Minage** :
  L'outil de prédilection pour miner ces blocs reste la **Hache** (exactement comme dans Vanilla), à la seule exception des blocs au Stade 3, si fragiles qu'ils n'ont aucun outil associé (ils s'effritent en un instant même à mains nues).
  - Les blocs Vanilla et **Touchés** restent solides (ils tombent toujours à **100%**).
  - Les blocs **Moisis** sont fragiles : ils n'ont que **50%** de chances de se lâcher eux-mêmes, sinon ils se briseront en morceaux et disparaîtront.
  - Les blocs **Pourris** s'effritent instantanément au toucher (probabilité de loot de **0%**).
  
  *(💡 **Le secret de la Cire** : Cirer un bloc consolide sa structure. N'importe quel bloc du mod, même Pourri, s'il est **Ciré** aura toujours **100% de chances de loot**, même sans utiliser l'enchantement Délicatesse !)*
* 🛠️ **Rendement de l'Artisanat (Récupération)** :
  Vous pouvez toujours utiliser le bois infecté dans l'établi pour fabriquer des objets de base (comme des Planches, Dalles, Escaliers ou Bâtons). L'objet final sera toujours parfaitement propre (**Vanilla**), mais comme vous êtes forcé de jeter les parties pourries du bois d'origine, la quantité d'objets obtenus chutera drastiquement :

  | Qualité du Matériau | 🌳 Ex : Bûche ➔ Planches | 🦯 Ex : Planches ➔ Bâtons |
  | :--- | :---: | :---: |
  | 🌲 **Sain (Vanilla)** | 1 Bûche ➔ **4** Planches | 2 Planches ➔ **4** Bâtons |
  | 🟢 **Touché** | 1 Bûche ➔ **2** Planches | 2 Planches ➔ **2** Bâtons |
  | 🦠 **Moisi** | 1 Bûche ➔ **1** Planche | 2 Planches ➔ **1** Bâton |
  | ☠️ **Pourri** | *Recette Invalide* ❌ | *Recette Invalide* ❌ |

* 🔥 **Pouvoir Combustible** : 
  - Le bois Touché brûle avec une efficacité réduite de moitié (**50%**).
  - Le Moisi tombe à un quart de l'efficacité (**25%**).
  - Le Pourri brûle en quelques instants (**12.5%**), le rendant inutile comme combustible.
* ♻️ **Composteur (Le bon côté de la Pourriture)** :
  Si un bloc est trop pourri pour construire avec, recyclez-le ! Tout le bois du mod a été intégré avec le Composteur Vanilla pour générer de la Poudre d'Os. Plus le bois est dégradé (et riche en spores), plus la probabilité de succès sera grande :
  - Bois Touché : **50%**
  - Bois Moisi : **65%**
  - Bois Pourri : **85%** (Excellent fertilisant !)

*(💡 **Note sur les Blocs Cirés** : La cire est un scellant environnemental, mais ne bloque pas l'utilisation de l'objet ! Vous pouvez utiliser les blocs cirés dans l'établi, les brûler dans le four ou les jeter dans le composteur : ils se comporteront exactement comme leur homologue non ciré, conservant les mêmes malus ou bonus liés uniquement à leur niveau interne de pourriture).*

---

## 🗺️ Génération des Structures

La moisissure ne se limite pas aux blocs placés par le joueur. Le mod intercepte le moteur de génération de Minecraft pour appliquer l'usure du temps à toutes les structures en bois que vous découvrirez dans le monde. 

Les structures sont divisées en 4 niveaux de dégradation de base :
1. 🏴‍☠️ **Dégradation Critique** (Pourcentage élevé de bois Pourri) : Épaves Submergées (`shipwreck`), Cabanes de Marais (`swamp_hut`).
2. 🧟 **Dégradation Élevée** (Mélange de Touché et Moisi) : Mines Abandonnées (`mineshaft`), Villages Zombies (`zombie_village`), Ruines de Piste (`trail_ruins`).
3. 🏹 **Dégradation Modérée** (Principalement Touché) : Avant-postes de Pilleurs (`pillager_outpost`), Portails en Ruine (`ruined_portal`).
4. 🏡 **Dégradation Minimale** (Presque totalement sain) : Villages normaux (`village`), Manoirs de la Forêt (`mansion`).

*(💡 **Facteurs Dynamiques** : Pendant la génération, le code analyse l'environnement bloc par bloc ! Si un mur de l'épave est exposé à l'air et au soleil, il sera plus intact, tandis que les planches enfoncées dans les fonds marins ou sous terre seront drastiquement plus pourries).*

**🛡️ L'Immunité du Bois Naturel et des Structures** :
Pour ne pas ruiner l'expérience de jeu (en évitant que les joueurs trouvent le monde entier déjà effondré avant de pouvoir l'explorer), il y a deux exceptions à la pourriture automatique :
* **Arbres Natifs** : Les arbres générés naturellement (ou cultivés à partir de pousses) ne génèrent pas de moisissure car le bois est encore "vivant". Seul le bois abattu et travaillé par le joueur commence à pourrir.
* **Structures Suspendues** : Les structures se génèrent avec le pourcentage de moisissure indiqué ci-dessus, mais ensuite "se figent". Les blocs des structures sont nativement immunisés contre l'avancée de la pourriture, à moins que le joueur n'interagisse avec eux (ex. en les cassant, en les grattant ou en les modifiant). Cette protection sauve les villages de la destruction spontanée. Si vous souhaitez une expérience super-hardcore, vous pouvez désactiver l'immunité des structures depuis le menu de configuration !

---

## ⚙️ Configuration du Mod
Le mod inclut un menu de configuration accessible directement depuis le jeu (nécessite **Cloth Config** et **ModMenu**) qui vous garantit un contrôle absolu sur chaque mécanique individuelle. 
Les options sont divisées en 8 catégories principales :

* 🛠️ **Général** : Désactivez la croissance de la moisissure globalement, changez le seuil d'infection, élargissez le rayon de balayage ou **désactivez l'immunité des structures** pour faire pourrir spontanément les villages !
* 🌡️ **Environnement (Environment)** : Modifiez les valeurs de base pour la pluie/sécheresse, les bonus pour l'eau, ou personnalisez à quelles altitudes et températures la moisissure doit geler ou proliférer.
* 🪓 **Susceptibilité (Susceptibility)** : Ajustez la vitesse à laquelle les blocs travaillés (planches) pourrissent par rapport aux blocs bruts ou écorcés.
* ☣️ **Catalyseurs (Catalysts)** : Équilibrez l'agressivité des champignons, de la boue, des *fleurs sporifères* et des blocs de bois infectés eux-mêmes.
* 🗺️ **Structures (Structures)** : Personnalisez en détail (pourcentage par pourcentage) la façon dont les épaves, les villages et les mines sont générés.
* 🔥 **Four (Furnace Multipliers)** : Modifiez l'efficacité de cuisson du bois pour les différents stades de pourriture.
* 💥 **Loot** : Augmentez ou diminuez le taux de butin du bois fragile, si vous trouvez le mod trop punitif.
* ☠️ **Toxicité (Toxicity)** : Modifiez les seuils, la durée et le rayon du nuage toxique, ainsi que le rayon de détection de l'eau.
