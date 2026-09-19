# 🔬 Warum Holz verrottet

Jeder Holzblock in der Welt wird mit jedem Tick eigenständig bewertet. Das Ergebnis dieser Berechnung ist eine Zahl — das **Infektionsrisiko ($R$)** — die darüber entscheidet, ob der Zerfall fortschreitet oder stagniert.

$$R = \Big( (H_{eff} \cdot L_{uv} \cdot S_{mat}) + C_{bonus} + M_{bonus} \Big) \cdot T_{mult}$$

Wenn $R > 0.50$ ist, schreitet die Verfallsstufe voran. Andernfalls bleibt der Block stabil. Jeder Faktor repräsentiert eine reale Umweltbedingung.

---

## 💧 Effektive Feuchtigkeit ($H_{eff}$)

Sie ist der Hauptantrieb des Zerfalls. Für jeden vollständig unter Wasser stehenden Block (*waterlogged*) gilt $H_{eff} = 1.0$ (absolutes Maximum). In allen anderen Fällen berechnet sie sich aus der lokalen Luftfeuchtigkeit, den Katalysatoren und dem trocknenden Einfluss von Frischluft:

$$H_{eff} = \text{clamp}\Big( H_{current} + C_{humidity} - A_{drying}, \ 0.0, \ 1.0 \Big)$$

Wobei die aktuelle Raumfeuchtigkeit ($H_{current}$) dynamisch gegen das Zielklima des Raumes konvergiert:

$$H_{target} = \text{clamp}\Big( H_{base} + D_{depth} + W_{water} + H_{humidifier} - D_{dehumidifier}, \ 0.0, \ 1.0 \Big)$$

- **Biom-Basisfeuchtigkeit ($H_{base}$)**: Regen- oder Schneebiome starten bei `0.80`; trockene oder Wüstenbiome bei `0.30`.
- **Tiefenmodifikator ($D_{depth}$)**: Unterhalb von $Y = 64$ steigt er stetig an, bis er bei $Y \le 48$ das Maximum von $+0.40$ erreicht, welches im gesamten Tiefenschiefer bis $Y = -64$ konstant bleibt.
- **Wasserquellen im Raum ($W_{water}$)**: Jeder im Raum befindliche Wasserblock addiert $+0.15$ (bis zu einem Maximum von $+0.60$).
- **Maschinen ($H_{humidifier} / D_{dehumidifier}$)**: Befeuchter ($+1.0$ je Einheit) oder Luftentfeuchter ($-1.0$ je Einheit).
- **Lokale Katalysatoren ($C_{humidity}$)**: Angrenzende wasserreiche Blöcke (z. B. Schlamm oder gefüllte Wasserkessel) addieren $+0.10$.
- **Belüftungstrocknung ($A_{drying}$)**: Lokale Frischluftströmung trocknet die Holzoberfläche ab: $A_{drying} = \text{Belüftung} \cdot 0.50$.

## ☀️ UV-Licht ($L_{uv}$)

Licht wirkt sterilisierend. Das Helligkeitsniveau wird rund um den Block abgetastet:
- **6 Messpunkte** (die angrenzenden Blockseiten) für feste und undurchsichtige Blöcke (Stämme, Bretter).
- **7 Messpunkte** (die 6 Seiten + der Innenraum des Blocks) für nicht-volle oder transparente Konstruktionen (Treppen, Stufen, Zäune, Türen, Schilder).

Der gemessene Durchschnittswert wird zwischen `0.0` (maximale Helligkeit 15 — Sterilisation, Infektion gestoppt) und `1.0` (völlige Dunkelheit 0 — volles Risiko) skaliert. Ein gut beleuchteter Block in einem offenen Raum hat ein Risiko nahe null; derselbe Block in einer finsteren Höhle ist extrem gefährdet.

## 🪓 Anfälligkeit des Materials ($S_{mat}$)

Nicht alle Blöcke besitzen dieselbe biologische Verwundbarkeit:

| Kategorie | Multiplikator ($S_{mat}$) | Details & Blöcke |
| :--- | :---: | :--- |
| **Entrindetes Holz** | **1.4×** | Entrindete Stämme und Hölzer (`stripped_*`). Ohne schützende Rinde sind die freiliegenden Fasern am anfälligsten. |
| **Standard / Stämme & Bauteile** | **1.0×** | Stämme mit Rinde, Möbel, Türen, Falltüren, Zäune, Schilder, Truhen und Werkbänke. |
| **Verarbeitetes Bauholz** | **0.8×** | Bretter (`*_planks`), Treppen (`*_stairs`), Stufen (`*_slab`) und Bambusmosaik. Abgelagertes, zugeschnittenes Konstruktionsholz mit höherer struktureller Widerstandskraft. |

## 🌡️ Temperatur und biologisches Fenster ($T_{mult}$)

Pilzsporen gedeihen nur innerhalb des „biologischen Fensters“ ($0.15 \le \text{Temp} \le 1.50$). Außerhalb dieses Bereichs beträgt $T_{mult} = 0.0$ und das Pilzwachstum stoppt vollständig:

- **Gebirge und Höhenlagen**:
  - Die stetige Abkühlung setzt oberhalb von **$Y = 128$** ein.
  - Im Aufstieg Richtung **$Y = 256$** sinkt die Temperatur allmählich auf $-0.50$ ab (dieser Wert bleibt bis zur Bauhöhenbegrenzung von **$Y = 320$** fixiert).
  - Das Schimmelwachstum **stoppt vollständig, sobald die Temperatur unter $0.15$ fällt** (je nach Biom typischerweise zwischen $Y \approx 180$ und $Y \approx 220$), wodurch Berghütten durch den Frost natürlichen Schutz genießen.
- **Untergrund und Höhlen**:
  - Unterhalb des Meeresspiegels (**$Y = 64$**) gleicht sich die Temperatur an das feuchte Mikroklima von Höhlen an.
  - Ab **$Y \le 48$** und im gesamten Tiefenschiefer bis **$Y = -64$** stabilisiert sich die Temperatur dauerhaft auf dem Idealwert von **`0.50`**. Dies garantiert, dass verlassene Minenschächte stets verrotten — völlig unabhängig vom Oberflächenbiom (selbst unter Wüsten oder Tundren).

## ☣️ Physische Katalysatoren ($C_{bonus}$) und Miasma-Sporendruck ($M_{bonus}$)

Biologische Blöcke im umliegenden Scan-Radius (3×3×3-Würfel um den Block) beschleunigen die Infektion durch einen direkten Bonus auf das Risiko $R$ oder durch Erhöhung der lokalen Feuchtigkeit:

| Katalysator | Risikobonus ($C_{bonus}$) | Lokaler Feuchtigkeitsbonus | Details & Verhalten |
| :--- | :---: | :---: | :--- |
| **Sporenblüte** (`Spore Blossom`) | **+0.80** (+80%) | — | **Extrem tödlich**: Überschreitet für sich allein bereits die Infektionsschwelle (0.50). Als Dekoration nahe Holzbalken absolut ungeeignet! |
| **Pilze** (rote, braune, Riesenpilzblöcke) | **+0.25** (+25%) | — | Pilze am Boden oder riesige Pilzblöcke sondern kontinuierlich Sporen bei Kontakt ab. |
| **Podsol & Myzel** | **+0.15** (+15%) | — | Organische Böden voller unterirdischer Pilzhyphen. |
| **Schlamm** (`Mud`) | **+0.05** (+5%) | **+0.10** | Hält Nässe extrem fest und beschleunigt Fäulnis am Gebäudefundament. |
| **Gefüllter Wasserkessel** | — | **+0.10** | Erzeugt stehende lokale Feuchte im Umkreis von 3 Blöcken. |
| **Ungewachster befallener Block** (Stufe 1) | **+0.03** je Block | — | Jeder erkrankte Nachbarblock überträgt passiv Sporen auf gesunde Blöcke. |
| **Ungewachster schimmeliger Block** (Stufe 2) | **+0.06** je Block | — | Verdoppelter Infektionsdruck gegenüber Stufe 1. |
| **Ungewachster verrotteter Block** (Stufe 3) | **+0.12** je Block | — | Enorme biologische Infektionslast für alle angrenzenden Blöcke. |

> [!NOTE]
> **Gewachste** Holzblöcke (`waxed`) wirken **nicht** als Katalysatoren: Die Bienenwachsschicht versiegelt alle Sporen hermetisch und neutralisiert jegliche Ansteckungsgefahr für Nachbarblöcke.

### 🌫️ Miasma-Sporendruck der Raumluft ($M_{bonus}$)
Neben festen Kontaktblöcken leidet Holz in der stehenden Luft eines miasmagesättigten Raumes unter kontinuierlicher Luftkontamination:
$$M_{bonus} = \text{ExposureIndex} \cdot 0.50$$
Eine giftige, schlecht belüftete Umgebung (detailliert beschrieben in [Kapitel 4](04_die_toedliche_luft.md)) führt dazu, dass selbst trockenes Bauholz rasch durch den Luftsporendruck befallen wird.

---

| | |
| :--- | ---: |
| [← Die verrottende Welt](01_die_verrottende_welt.md) | [Bauen mit verrottetem Holz →](03_bauen_mit_verrottetem_holz.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
