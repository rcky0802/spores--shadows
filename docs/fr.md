# Spores & Shadows 

**Spores & Shadows** est un mod pour Minecraft (Fabric 1.21.1) qui introduit un écosystème dynamique, réaliste et implacable de décomposition environnementale pour le bois. Aucune structure n'est à l'abri du temps et des éléments !

---

## 🌳 Aperçu et Contenu

Avez-vous déjà construit une majestueuse cabane en bois en pensant qu'elle resterait intacte, défiant les siècles, sans aucun besoin d'entretien ? **Spores & Shadows** révolutionne cette certitude, transformant le bois d'un simple bloc inerte en un matériau vivant, vulnérable et réactif à son environnement.

Le mod remplace de manière totalement transparente et silencieuse chaque morceau de bois placé par le joueur (ou généré naturellement dans les structures comme les épaves et les mines) par une variante dormante. Avec le temps, les agents atmosphériques comme la pluie, l'humidité, l'obscurité et même le biome dans lequel vous vous trouvez scelleront le destin de vos constructions, vous forçant à protéger vos bâtiments ou à assister impuissant à leur déclin inexorable.

### 🔢 Détails Techniques et Blocs Ajoutés

Sur le plan technique, le mod injecte un écosystème complet pour chaque variante individuelle de bois (y compris les bois du Nether : Carmin et Biscornu).

* **🧱 13 Formats Architecturaux** : *Bûches*, *Bûches Écorcées*, *Bois*, *Bois Écorcé*, *Planches*, *Escaliers*, *Dalles*, *Barrières*, *Portillons*, *Portes*, *Trappes*, *Plaques de Pression*, *Boutons*.

Pour chacun des 130 formats de base en bois, le mod ajoute **3 variantes moisies** (Touché, Moisi, Pourri). De plus, pour chacun de ces blocs — y compris le bloc Vanilla original — la **variante cirée** respective est créée.

Ainsi, le jeu met à disposition pas moins de **910 variantes uniques et obtenables en Survie** :
1. Les **130 Variantes Vanilla Cirées** : La copie protégée et cirée du bloc Vanilla de base.
2. Les **390 Variantes Moisies** : Les trois stades naturels de pourriture (*Touché*, *Moisi*, *Pourri*).
3. Les **390 Variantes Moisies Cirées** : Les blocs dégradés mais figés dans le temps par la cire.

Ce système vous permet de récupérer des blocs partiellement moisis en survie pour ensuite les sceller avec des rayons de miel, pouvant ainsi les utiliser en toute sécurité à des fins décoratives sans risquer d'infecter les constructions voisines.

---

## 🦠 Le Cycle de la Moisissure

Le bois traverse 4 stades de pourriture : **Sain / Vanilla (0) ➔ Touché (1) ➔ Moisi (2) ➔ Pourri (3)**.

La progression ne se produit que si le Risque d'Infection (`R`), recalculé en permanence, dépasse le seuil fixe de **0.4** (valeur par défaut, entièrement configurable). La formule mathématique exacte est :

$$\mathbf{R = \left(\left(H_{\text{eff}} \times L_{\text{uv}} \times S_{\text{mat}}\right) + \text{Contagion}\right) \times T_{\text{mult}}}$$

### Facteurs et Variables Exactes

* 💧 **Humidité Effective $H_{\text{eff}}$ (Climat + Profondeur + Eau)** : 
  - La valeur de base dépend des précipitations du biome (Pluie/Neige : `0.8`, Biomes secs : `0.3`). 
  - **Malus de Profondeur** : En descendant sous le niveau de la mer ($Y < 64$), l'humidité augmente progressivement de `+0.01` par bloc de descente (jusqu'à un bonus maximal de `+1.28`), rendant les mines et grottes souterraines des milieux particulièrement humides.
  - **Malus de Proximité** : La présence d'eau adjacente (`+0.15`) ou de chaudrons d'eau (`+0.10`) dans un rayon de 3 blocs ajoute une humidité locale supplémentaire (plafonné à `+0.60`).
* ☀️ **Lumière / Obscurité $L_{\text{uv}}$** : Évolue linéairement de `0.0` (Lumière de bloc/ciel à 15, bloque totalement l'infection) à `1.0` (Obscurité totale à 0).
* 🪓 **Susceptibilité du Matériau $S_{\text{mat}}$** : Le bois écorcé est extrêmement vulnérable (`x1.4`), les bûches brutes et blocs de bois sont standards (`x1.0`), tandis que le bois manufacturé en planches et dérivés résiste légèrement mieux (`x0.8`).
* 🌡️ **Multiplicateur Thermique $T_{\text{mult}}$ (Biome + Altitude/Profondeur)** : 
  - Agit comme un filtre vital de survie. La moisissure prolifère **uniquement** si la température effective locale est comprise entre `0.15` et `1.5`.
  - **En surface** : Dépend directement du biome. Les chaleurs arides des déserts ou les froids polaires des glaciers ramènent le facteur à `0.0`, stoppant toute décomposition.
  - **Nether & End** : La chaleur suffocante du Nether et le vide astral de l'End sont totalement hostiles au développement fongique. Le bois n'y pourrira jamais.
  - **Sous terre ($Y < 64$)** : Indépendamment du biome en surface, au fur et à mesure de la descente, la température converge vers un climat tempéré doux stabilisé à `0.5` en dessous de $Y=48$. Les cavernes profondes développent ainsi de la moisissure même sous un désert ou un glacier !
  - **Haute Altitude ($Y > 128$)** : L'air se refroidit avec l'altitude, atteignant une température glaciale de `-0.5` à $Y=256$. Les constructions en haute montagne sont ainsi naturellement protégées.
* ☣️ **Contagion & Catalyseurs Biologiques** : Ajoute un bonus d'infection direct si le bloc est en contact avec des foyers biologiques :
  - **Blocs de bois infectés voisins** : Touché (`+0.05`), Moisi (`+0.10`), Pourri (`+0.20`).
  - **Matières organiques et fongiques** : Boue (`+0.05`), Podzol / Mycélium (`+0.15`), Champignons (`+0.25`), Fleur Sporifère (*Spore Blossom*) (`+0.80`).

---

## ☠️ Dangers Environnementaux (Miasme Volumétrique & Spores)

Le pourrissement du bois ne détériore pas seulement les bâtiments : il altère l'air environnant et constitue une menace biologique pour les explorateurs imprudents.

### ✨ 1. Particules d'Ambiance et Nuage de Spores
- **Émission Passive** : Les blocs au stade **Moisi** ou **Pourri** émettent continuellement des particules de spores depuis leurs faces exposées à l'air libre (désactivé si le bloc est immergé dans l'eau).
- **💥 Explosion de Nuage de Spores à la Destruction** : Casser un bloc de Stade 2 (**Moisi**) ou Stade 3 (**Pourri**) sans l'enchantement **Toucher de Soie (*Silk Touch / Délicatesse*)** provoque la rupture brutale des parois fongiques internes :
  - Une violente déflagration de particules biologiques (`SPORE_BLOSSOM_AIR`, `FALLING_SPORE_BLOSSOM`, `MYCELIUM`) est libérée dans un nuage dense.
  - Un son spongieux et organique d'éclatement retentit lors de la destruction.
  - *(Note : L'extraction avec Toucher de Soie ou sur un bloc **Ciré** empêche cette explosion de particules).*

### 🤢 2. Miasme Toxique Volumétrique (Algorithme BFS en Milieu Clos)
Grâce à un algorithme de propagation par remplissage volumétrique (*Breadth-First Search / Flood-Fill* à 6 directions), le mod évalue précisément la concentration de spores dans les pièces fermées :
- **Rayon de Manhattan Limité ($R \le 8$)** : Empêche la propagation infinie dans les galeries minières sans fin.
- **Dispersion à l'Air Libre** : Si l'algorithme détecte un accès direct au ciel libre ou un volume d'air supérieur à **180 blocs**, le miasme est considéré comme ventilé et se dissipe sans danger.
- **Concentration en Espace Clos ($< 180\text{ m}^3$)** : Dans les caves, réduits et pièces fermées, chaque bloc contaminé non ciré augmente le score de toxicité :
  $$\text{Densité de Spores} = \frac{\text{Miasme Net}}{\text{Volume de la Pièce}}$$
- **Ventilation Naturelle par Fissures** : Les blocs non pleins exposés sur l'extérieur (barrières, portillons, barreaux de fer, dalles, trappes, escaliers) apportent des points de ventilation ($+3.0$ par ouverture), réduisant drastiquement le miasme net ($\text{Miasme Net} = \text{Score Toxique} - \text{Score de Ventilation}$).

**Effets de Statut selon la Concentration (Seuils Configurables)** :
- 🍞 **Faim** : $\text{Miasme Net} \ge 8.0$ (ou $\text{Densité} \ge 0.09$ et $\text{Miasme Net} \ge 5.0$).
- 🤢 **Nausée + Poison** : $\text{Miasme Net} \ge 35.0$ (seuil configurable) ou ($\text{Densité} \ge 0.18$ et $\text{Miasme Net} \ge 15.0$).

---

## 🛠️ Interactions et Prévention

Le joueur dispose d'outils simples et efficaces pour entretenir ou assainir ses constructions. Pour éviter toute interaction involontaire avec les blocs fonctionnels (portes, boutons, trappes), les actions s'effectuent en **mode accroupi (Sneaking / Maj)**.

* 🪓 **Grattage à la Hache (*Axe Scraping*)** (*Maj + Clic Droit avec une hache*) :
  - **Sur un bloc Ciré** : Retire la couche protectrice de cire et réintègre le bloc dans le cycle vivant normal.
  - **Sur un bloc Touché (Stade 1) ou Moisi (Stade 2)** : Décape la couche fongique externe et fait régresser la moisissure d'un stade (Stade 2 ➔ Stade 1 ➔ Stade 0 Vanilla propre).
  - *Consomme normalement 1 point de durabilité d'outil par opération (configurable).*
* 🐝 **Cirage au Rayon de Miel (*Waxing*)** (*Maj + Clic Droit avec un rayon de miel*) :
  - Applicable sur n'importe quel bloc à n'importe quel stade (0, 1, 2 ou 3).
  - Scelle hermétiquement le bloc : stoppe définitivement la progression de la pourriture, immunise contre les facteurs climatiques, supprime la contagion vers les blocs voisins et neutralise l'émission de miasme.

*🔄 **Synchronisation Multi-Blocs** : Pour les blocs composites (comme les portes hautes de 2 blocs), gratter ou cirer une moitié met à jour instantanément les deux moitiés en parfaite synchronisation.*

---

## ⚖️ Pénalités, Physique et Artisanat

La détérioration du bois fragilise sa structure interne, modifiant ses propriétés physiques, son comportement face au feu, aux explosions et aux outils :

### 🔨 1. Dureté Progressive & Friabilité Extrême au Stade 3
La résistance mécanique du matériau s'effondre avec la prolifération fongique :
* **Stade 0 (Sain / Vanilla)** : Dureté standard de $2.0$ ($100\%$).
* **Stade 1 (Touché)** : Dureté de $1.6$ ($80\%$).
* **Stade 2 (Moisi)** : Dureté de $1.0$ ($50\%$).
* **Stade 3 (Pourri)** : Dureté critique de $0.4$ ($20\%$).
* **Neutralisation de la Hache au Stade 3** : Le bois pourri a perdu toute cohérence structurelle. Le miner avec une hache en netherite ou à mains nues prend rigoureusement le **même temps instantané** (absence d'outil privilégié).

### 💥 2. Intégrité Structurelle, Drops et Règle du Toucher de Soie
* **Sans Toucher de Soie (et Non Ciré)** :
  - **Stade 0 & Stade 1** : Récolte garantie à **$100\%$**.
  - **Stade 2 (Moisi)** : Fragile — **$50\%$** de chances d'obtenir l'objet, sinon il s'effondre en poussière.
  - **Stade 3 (Pourri)** : S'effrite immédiatement au contact — **$0\%$** de drop (disparition totale).
* **Avec Toucher de Soie (*Silk Touch*) OU Bloc Ciré** : **$100\%$ de drop garanti** sur tous les stades, y compris les blocs pourris de Stade 3.

### 💣 3. Résistance aux Explosions Dynamique
Le bois dégradé absorbe beaucoup moins bien les ondes de choc (TNT, Creepers, etc.) :
* **Stade 0 (Vanilla)** : Résistance normale ($100\%$).
* **Stade 1 (Touché)** : Résistance réduite à **$80\%$** ($0.8\times$).
* **Stade 2 (Moisi)** : Résistance réduite à **$50\%$** ($0.5\times$).
* **Stade 3 (Pourri)** : Résistance quasi-nulle de **$10\%$** ($0.1\times$, vole en éclats à la moindre déflagration).

### 🔥 4. Inflammabilité Scalaire et Immunité du Nether
La présence de moisissure sèche et poreuse accélère drastiquement la prise de feu :
* **Stade 1 (Touché)** : $+5$ combustion / $+10$ propagation.
* **Stade 2 (Moisi)** : $+10$ combustion / $+25$ propagation.
* **Stade 3 (Pourri)** : $+20$ combustion / $+60$ propagation (hautement inflammable !).
* **Bonus de Cire** : $+5$ au taux de combustion (la cire d'abeille s'embrase rapidement).
* **🛡️ Immunité Stricte des Bois du Nether** : Les tiges de champignon Carmin et Biscornu (*Crimson & Warped*) conservent leur incombustibilité naturelle absolue ($0$), même dégradées ou cirées.

### 🛠️ 5. Rendement d'Artisanat & Artisanat Hybride
Le bois dégradé peut toujours être transformé dans l'établi en objets sains (**Vanilla**), mais les parties abîmées sont éliminées, diminuant le rendement :

| Qualité du Matériau | 🌳 Exemple : Bûche ➔ Planches | 🦯 Exemple : Planches ➔ Bâtons |
| :--- | :---: | :---: |
| 🌲 **Sain (Vanilla / Ciré)** | 1 Bûche ➔ **4** Planches | 2 Planches ➔ **4** Bâtons |
| 🟢 **Touché (Stade 1)** | 1 Bûche ➔ **2** Planches | 2 Planches ➔ **2** Bâtons |
| 🦠 **Moisi (Stade 2)** | 1 Bûche ➔ **1** Planche | 2 Planches ➔ **1** Bâton |
| ☠️ **Pourri (Stade 3)** | *Recette Invalide* ❌ | *Recette Invalide* ❌ |

*(💡 **Artisanat Hybride** : Vous pouvez mélanger librement des blocs normaux et leurs variantes cirées du même stade dans la grille de fabrication).*

### 🔥 6. Efficacité Combustible au Four & Charbon de Bois
* **Rendement de Combustion** :
  - **Sain (Stade 0)** : $100\%$ ($1.0\times$).
  - **Touché (Stade 1)** : $50\%$ ($0.5\times$).
  - **Moisi (Stade 2)** : $25\%$ ($0.25\times$).
  - **Pourri (Stade 3)** : $12.5\%$ ($0.125\times$, combustion quasi instantanée).
* **Fabrication de Charbon de Bois (*Charcoal*)** : Tous les troncs et bois dégradés (et leurs variantes cirées) sont enregistrés dans les tags `#minecraft:item/charcoal` et `#c:charcoal`, permettant leur cuisson en four pour produire du charbon de bois.

### ♻️ 7. Composteur
Le bois moisi fait un excellent fertilisant dans le composteur vanilla :
* Bois Touché : **$50\%$** de chances de faire monter le niveau de compost.
* Bois Moisi : **$65\%$**.
* Bois Pourri : **$85\%$** (engrais organique de premier choix).

### 🔴 8. Composants Redstone (Boutons et Plaques de Pression)
Les dépôts fongiques enrayent les mécanismes en bois, prolongeant la durée d'impulsion du signal :
* **Sain (Vanilla)** : $1.5$ seconde (30 ticks).
* **Touché** : $3.0$ secondes (60 ticks).
* **Moisi** : $7.5$ secondes (150 ticks).
* **Pourri** : $22.5$ secondes (450 ticks).

---

## 🗺️ Génération des Structures & Immunités

Lors de la création du monde, le mod injecte de la moisissure dans les structures générées naturellement selon 4 profils d'usure :

1. 🏴‍☠️ **Dégradation Critique** (forte présence de bois Pourri) : Épaves sous-marines (`shipwreck`), Cabanes de marais (`swamp_hut`).
2. 🧟 **Dégradation Élevée** (mélange de Touché et Moisi) : Mines abandonnées (`mineshaft`), Villages zombies (`zombie_village`), Ruines de piste (`trail_ruins`).
3. 🏹 **Dégradation Modérée** (principalement Touché) : Avant-postes de pillards (`pillager_outpost`), Portails en ruine (`ruined_portal`).
4. 🏡 **Dégradation Faible** (presque intact) : Villages ordinaires (`village`), Manoirs de la forêt (`mansion`).

### 🛡️ Règles de Préservation
* **Arbres Vivants** : Les arbres naturels ou cultivés par des pousses ne pourrissent jamais tant qu'ils sont vivants.
* **Immunité des Structures Générées** : Les structures apparaissent partiellement dégradées à la génération, puis se figent. La moisissure ne progresse pas spontanément dans les villages tant qu'un joueur n'interagit pas avec les blocs (option désactivable dans la configuration pour les amateurs de mode *hardcore*).

---

## 📖 Intégrations Mods & Progrès

### 🔍 1. Jade / WTHIT
En regardant n'importe quel bloc de bois en jeu, le HUD Jade affiche dynamiquement :
- La variante exacte et son icône fidèle (y compris avec le système de blocs Polymer).
- L'état de cirage (*Ciré / Waxed*).
- Le risque d'infection instantané en pourcentage (avec code couleur : **Gris = Sûr**, **Rouge = En Danger**).

### 📚 2. JEI (Just Enough Items)
Un plugin complet est intégré pour documenter les recettes et comportements en jeu :
* **Catégorie Cirage (*Waxing*)** : Affiche la recette de scellage (`Bloc + Rayon de Miel ➔ Bloc Ciré`) pour l'ensemble des 130 blocs de bois et leurs dérivés.
* **Catégorie Décapage à la Hache (*Axe Scraping*)** :
  - Retrait de la cire : `Bloc Ciré + Hache ➔ Bloc Non-Ciré`.
  - Guérison de la pourriture : `Stade 2 (Moisi) ➔ Stade 1 (Touché) ➔ Stade 0 (Vanilla)`.
* **Onglets d'Information (*Info Tab*)** : Descriptions détaillées sur tous les blocs de **Stade 3 (Pourri)** expliquant l'effritement à mains nues, l'absence de recette d'établi directe et le drop garanti par la cire ou Toucher de Soie.

### 🏆 3. Progrès Personnalisés (Advancements)
- **Spores & Shadows** : Survivez au déclin inexorable de la nature.
- **Prévention Naturelle** : Utilisez un rayon de miel pour cirer un bloc de bois et figer la moisissure.
- **Huile de Coude** : Grattez la pourriture d'un bloc de bois à l'aide d'une hache.
- **Souffle Court** : Subissez le poison du miasme en respirant trop de spores dans un espace clos.
- **Poussière à la Poussière** : Tentez de briser un bloc de bois pourri et observez-le s'effondrer en morceaux.

---

## 💻 Commandes Administrateur

Les opérateurs et administrateurs (niveau de permission 2) disposent de plusieurs commandes dédiées pour surveiller et calibrer les systèmes :

* 🔬 `/moldrisk` ou `/moldyrisk` :
  - Analyse en temps réel le bloc de bois ciblé par le regard.
  - Affiche les composantes de la formule ($H_{\text{eff}}$, $L_{\text{uv}}$, $S_{\text{mat}}$, catalyseurs, température $T_{\text{mult}}$, seuil et valeur finale $R$) avec mention `SAFE` ou `WILL GROW`.
  - `/moldrisk verbose` (ou `/moldyrisk verbose`) détaille l'ensemble des sous-calculs (bonus d'altitude, de profondeur, d'eau locale, lumière moyenne).
* 🌫️ `/miasma` :
  - Scanne la pièce occupée par le joueur via l'algorithme volumétrique.
  - Affiche l'environnement (*Air Libre / Espace Confiné*), le volume d'air exploré, le score de toxicité fongique, le score de ventilation passive des ouvertures, le miasme net et la densité de spores par $\text{m}^3$.
* 🔄 `/spores reload` :
  - Recharge instantanément le fichier de configuration du mod (Cloth Config) sans nécessiter de redémarrage du serveur ou du client.

---

## ⚙️ Configuration du Mod (Cloth Config / ModMenu)

Le menu de configuration graphique en jeu (nécessitant **Cloth Config** et **ModMenu**) permet d'ajuster chaque paramètre au millimètre à travers 12 catégories distinctes :

1. 🛠️ **Général (General)** : Activation globale de la moisissure, seuil d'infection (`infection_threshold` = `0.40`), rayon de balayage, immunité passive des structures et usure de la hache au grattage.
2. 🌡️ **Environnement (Environment)** : Humidité de base pluie/sécheresse, malus de profondeur sous $Y=64$, bonus de proximité d'eau/chaudrons, plages de survie en température, température stable des cavernes ($0.5$ à $Y \le 48$) et température de gel en altitude ($-0.5$ à $Y \ge 256$).
3. 🪓 **Susceptibilité (Susceptibility)** : Coefficients de vulnérabilité pour bois écorcé (`1.4`), planches (`0.8`) et bûches brutes (`1.0`).
4. ☣️ **Catalyseurs (Catalysts)** : Pondération de contagion pour la boue, le podzol/mycélium, les champignons, les fleurs sporifères et les blocs contaminés voisins (Stades 1, 2 et 3).
5. ☠️ **Toxicité (Toxicity)** : Fréquence des vérifications de miasme (ticks), rayon de scan, seuils d'activation de la Nausée (`15`) et du Poison (`35`), durée des effets et amplificateurs de potion.
6. 🗺️ **Structures (Structures)** : Pourcentages de dégradation personnalisables par niveau de structure (Critique, Élevée, Modérée, Faible).
7. 🔥 **Efficacité Four (Furnace Multipliers)** : Multiplicateurs de durée de cuisson et combustion pour les 4 stades de dégradation ($1.0$, $0.5$, $0.25$, $0.125$).
8. 💥 **Loot & Récolte (Drops)** : Probabilités de drop pour les blocs non cirés au Stade 2 ($50\%$) et Stade 3 ($0\%$).
9. 🔨 **Dureté (Hardness)** : Multiplicateurs de dureté mécanique par stade ($0.80$, $0.50$, $0.20$) et activation de l'explosion de nuage de spores à la casse sans Délicatesse.
10. 🔥 **Inflammabilité (Flammability)** : Bonus de combustion et de propagation du feu par stade, bonus de cire et respect de l'immunité Nether.
11. 💣 **Résistance aux Déflagrations (Blast Resistance)** : Multiplicateurs de résistance aux explosions ($0.80$, $0.50$, $0.10$).
12. 🖥️ **Client (Client)** : Paramètres de rendu graphique (décalage de texture `mold_z_offset`) pour une compatibilité sans faille avec Sodium et Iris Shaders.

