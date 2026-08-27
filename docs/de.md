# Spores & Shadows 

**Spores & Shadows** ist eine Mod für Minecraft (Fabric 1.21.1), die ein dynamisches, realistisches und unerbittliches Ökosystem für den Zerfall von Holz einführt. Keine Struktur ist vor der Zeit und den Elementen sicher!

---

## 🌳 Übersicht und Inhalte

Hast du jemals eine majestätische Holzhütte gebaut und gedacht, dass sie unversehrt bleibt, die Jahrhunderte überdauert und keinerlei Wartung bedarf? **Spores & Shadows** revolutioniert diese Gewissheit und verwandelt Holz von einem einfachen inerten Block in ein lebendiges, verletzliches und auf die Umgebung reagierendes Material.

Die Mod ersetzt nahtlos und unbemerkt jedes vom Spieler platzierte (oder natürlich in Strukturen wie Schiffswracks und Minen generierte) Holz durch eine "ruhende" Variante. Im Laufe der Zeit werden Witterungseinflüsse wie Regen, Feuchtigkeit, Dunkelheit und sogar das Biom, in dem du dich befindest, das Schicksal deiner Konstruktionen besiegeln. Du bist gezwungen, deine Gebäude zu schützen oder hilflos ihrem unaufhaltsamen Verfall zuzusehen.

### 🔢 Technische Details und neue Blöcke

Auf technischer Ebene fügt die Mod für jede einzelne Holzvariante ein komplettes Ökosystem ein.

* **🧱 13 Architektonische Formate**: *Stämme*, *Entrindete Stämme*, *Holz*, *Entrindetes Holz*, *Bretter*, *Treppen*, *Stufen*, *Zäune*, *Zauntore*, *Türen*, *Falltüren*, *Druckplatten*, *Knöpfe*.

Für jedes der 130 Basis-Holzformate fügt die Mod **3 schimmlige Varianten** hinzu (Befallen, Schimmelig, Verrottet). Zusätzlich wird für jeden dieser Blöcke — einschließlich des ursprünglichen Vanilla-Blocks — die entsprechende **gewachste Variante** erstellt.

Auf diese Weise bietet das Spiel unglaubliche **910 einzigartige Varianten, die im Survival-Modus erhältlich sind**:
1. Die **130 gewachsten Vanilla-Varianten**: Die geschützte und gewachste Kopie des Vanilla-Basisblocks.
2. Die **390 schimmligen Varianten**: Die drei natürlichen Zerfallsstadien.
3. Die **390 gewachsten schimmligen Varianten**: Die verfallenen, aber durch Wachs in der Zeit eingefrorenen Blöcke.

Dieses System ermöglicht es dir, teilweise verschimmelte Blöcke im Survival-Modus zu erhalten und sie dann mit einer Honigwabe zu "versiegeln". So können sie völlig sicher für dekorative Zwecke verwendet werden, ohne das Risiko, benachbarte Konstruktionen zu infizieren.

---

## 🦠 Der Schimmelkreislauf

Das Holz durchläuft 4 Zerfallsstadien: **Vanilla (0) ➔ Befallen (1) ➔ Schimmelig (2) ➔ Verrottet (3)**.

Der Fortschritt findet nur statt, wenn das "Infektionsrisiko" (`R`), das ständig neu berechnet wird, den festen Schwellenwert von **0.4** überschreitet. Die genaue Formel lautet:
`R = ((Feuchtigkeit * Licht * Anfälligkeit) + Ansteckung) * Temperatur`

### Genaue Faktoren und Variablen
* 💧 **Feuchtigkeit (Klima + Tiefe + Wasser)**: 
  - Der Basiswert hängt vom Niederschlag des Bioms ab (Regen/Schnee: `0.8`, Trocken: `0.3`). 
  - **Tiefen-Malus**: Wenn man unter den Meeresspiegel sinkt (`Y < 64`), steigt die Feuchtigkeit rapide um `+0.01` für jeden Block Tiefe, was Minen zu extrem feuchten Umgebungen macht.
  - **Lokaler Malus**: Die Nähe zu Wasserblöcken (`+0.15`) oder Kesseln (`+0.10`) fügt dem Block zusätzliche Feuchtigkeit hinzu.
* ☀️ **Licht (UV)**: Skaliert linear von `0.0` (Lichtlevel 15, blockiert die Infektion vollständig) bis `1.0` (Totale Dunkelheit).
* 🪓 **Anfälligkeit des Materials**: Entrindetes Holz ist extrem verletzlich (`x1.4`), rohe Stämme sind Standard (`x1.0`), während zu Brettern verarbeitetes Holz etwas widerstandsfähiger ist (`x0.8`).
* 🌡️ **Temperatur (Biom + Höhe/Tiefe)**: 
  - Wirkt als Überlebensfilter. Schimmel wächst **nur**, wenn die lokale Temperatur zwischen `0.15` und `1.5` liegt.
  - **An der Oberfläche**: Abhängig vom Biom. Extreme Klimazonen wie Wüsten oder Gletscher stoppen die Infektion vollständig und senken den Faktor auf `0.0`.
  - **Nether & End**: Die extreme Hitze des Nethers und die eisige Leere des Ends sind für Schimmel absolut tödlich. Holz wird in diesen Dimensionen niemals verrotten.
  - **Unterirdisch (`Y < 64`)**: Unabhängig vom Oberflächenbiom normalisiert sich die Temperatur mit zunehmender Tiefe allmählich und stabilisiert sich unterhalb von `Y=48` bei perfekten `0.5` (mild). Selbst in einer Wüste oder einem eisigen Biom entwickelt sich in tiefen Höhlen Schimmel!
  - **Große Höhen (`Y > 128`)**: Mit zunehmender Höhe sinkt die Temperatur allmählich und friert auf Höhe `Y=256` bei `-0.5` ein. Der Bau von Hütten im Hochgebirge schützt das Holz fast überall.
* ☣️ **Ansteckung (Katalysatoren)**: Addiert einen direkten Malus, wenn das Holz in Kontakt mit infektiösen Erregern kommt:
  - Infiziertes Holz: Befallen (`+0.05`), Schimmelig (`+0.10`), Verrottet (`+0.20`).
  - Umgebung: Schlamm (`+0.05`), Podsol/Myzel (`+0.15`), Pilze (`+0.25`), Sporenblüte (`+0.80`).

## ☠️ Umweltgefahren (Miasma)

- ✨ **Sporenpartikel**: Blöcke im Stadium **Schimmelig** oder **Verrottet** geben Sporen an exponierten Flächen ab (unter Wasser deaktiviert).
- 🤢 **Toxisches Miasma**: Die Mod scannt einen Radius von 4 Blöcken um den Spieler. Jeder Block addiert seine Zerfallsstufe zum gesamten Toxizitätswert:
  - **Befallen**: +1
  - **Schimmelig**: +2
  - **Verrottet**: +3
  - *(Gewachste Blöcke sind sicher und tragen 0 bei).*
  
  **Effekte**:
  - Wert > **15**: **Übelkeit**
  - Wert > **35**: **Übelkeit + Vergiftung**

---

## 🛠️ Interaktionen und Vorbeugung

Der Spieler ist der Natur nicht schutzlos ausgeliefert. Durch Ausrüsten des richtigen Werkzeugs und Agieren im **Schleich-Modus (Sneaking / Shift)** kannst du direkt mit dem Lebenszustand des Holzes interagieren. 
*(Das Schleichen ist zwingend erforderlich, um ein versehentliches Wachsen oder Abkratzen von interaktiven Blöcken wie Türen, Falltüren oder Knöpfen zu vermeiden).*

* 🪓 **Verwendung der Axt (Abkratzen)**: Durch *Shift + Rechtsklick* mit einer Axt:
  - Wenn der Block **Gewachst** ist, entfernt die Axt die Wachsschicht und stellt den normalen Lebenszyklus wieder her.
  - Wenn der Block **Befallen oder Schimmelig** ist, kratzt die Axt die oberflächliche Pilzschicht ab und reduziert den Zerfall um 1 Stufe. Ein Block auf Stufe 1 wird wieder perfekt sauber (Stufe 0 Vanilla).
  *(Jedes Abkratzen verbraucht wie gewohnt Haltbarkeit).*
* 🐝 **Verwendung der Honigwabe (Wachsen)**: Durch *Shift + Rechtsklick* mit einer Honigwabe auf einen Block in *jedem* Stadium wird dieser **Gewachst**. Gewachstes Holz ist versiegelt: Es wird immun gegen Umwelteinflüsse, friert seinen Zerfall auf unbestimmte Zeit ein und verliert die Fähigkeit, benachbarte Blöcke zu infizieren. 

*Smarte Funktion: Wenn du diese Aktionen an einem mehrteiligen Block (wie der oberen oder unteren Hälfte einer Tür) durchführst, wird die Aktualisierung sofort und völlig synchron auf die gesamte Struktur angewendet!*

---

## ⚖️ Strafen und Crafting

Die Verwendung von verrottetem Holz zum Crafting ist unklug. Die innere Struktur des Materials ist irreparabel beschädigt, was zu strengen Strafen führt, die Faulheit bestrafen:

* 💥 **Strukturelle Integrität (Drop) und Abbau**:
  Das bevorzugte Werkzeug zum Abbauen dieser Blöcke bleibt die **Axt** (genau wie in Vanilla), mit der einzigen Ausnahme der Blöcke auf Stufe 3, die so schwach sind, dass ihnen kein Werkzeug zugeordnet ist (sie zerbröckeln selbst mit bloßen Händen in einem Augenblick).
  - Vanilla-Blöcke und **befallene** Blöcke bleiben solide (droppen immer zu **100%**).
  - **Schimmelige** Blöcke sind zerbrechlich: Sie haben nur eine **50%ige** Chance, sich selbst zu droppen, andernfalls zerfallen sie zu Nichts.
  - **Verrottete** Blöcke zerbröckeln bei Berührung sofort (**0%** Drop-Chance).
  
  *(💡 **Das Geheimnis des Wachses**: Das Wachsen eines Blocks festigt seine Struktur. Jeder Block der Mod, selbst der verrottete, wird, wenn er **gewachst** ist, immer eine **100%ige Drop-Chance** haben, selbst ohne Verwendung von Behutsamkeit!)*
* 🛠️ **Crafting-Ertrag (Rückgewinnung)**:
  Du kannst infiziertes Holz weiterhin auf der Werkbank verwenden, um grundlegende Gegenstände (wie Bretter, Stufen, Treppen oder Stöcke) herzustellen. Der Endgegenstand wird immer perfekt sauber sein (**Vanilla**), aber da du gezwungen bist, die verrotteten Teile des ursprünglichen Holzes zu verwerfen, wird die Menge der erhaltenen Gegenstände drastisch sinken:
  
  *(💡 **Hybrides Crafting**: Infizierte und gewachste Holzblöcke desselben Schimmelstadiums können nun beliebig in derselben Crafting-Matrix gemischt werden, um saubere Vanilla-Gegenstände herzustellen!)*

  | Materialqualität | 🌳 Bsp: Stamm ➔ Bretter | 🦯 Bsp: Bretter ➔ Stöcke |
  | :--- | :---: | :---: |
  | 🌲 **Gesund (Vanilla)** | 1 Stamm ➔ **4** Bretter | 2 Bretter ➔ **4** Stöcke |
  | 🟢 **Befallen** | 1 Stamm ➔ **2** Bretter | 2 Bretter ➔ **2** Stöcke |
  | 🦠 **Schimmelig** | 1 Stamm ➔ **1** Brett | 2 Bretter ➔ **1** Stock |
  | ☠️ **Verrottet** | *Ungültiges Rezept* ❌ | *Ungültiges Rezept* ❌ |

* 🔥 **Brennwert**: 
  - Befallenes Holz brennt mit halber Effizienz (**50%**).
  - Schimmeliges Holz sinkt auf ein Viertel der Effizienz (**25%**).
  - Verrottetes Holz verbrennt in wenigen Augenblicken (**12.5%**), was es als Brennstoff nutzlos macht.
* ♻️ **Komposter (Die positive Seite der Fäulnis)**:
  Wenn ein Block zu verrottet ist, um damit zu bauen, recycele ihn! Das gesamte Holz der Mod wurde in den Vanilla-Komposter integriert, um Knochenmehl zu erzeugen. Je stärker das Holz zersetzt (und reich an Sporen) ist, desto höher ist die Erfolgschance:
  - Befallenes Holz: **50%**
  - Schimmeliges Holz: **65%**
  - Verrottetes Holz: **85%** (Hervorragender Dünger!)
* 🔴 **Redstone-Komponenten (Knöpfe und Druckplatten)**:
  Schimmel beeinträchtigt die inneren Mechanismen der Redstone-Komponenten, sodass diese klemmen und viel länger aktiv bleiben. Ein normaler, gesunder Holzknopf bleibt beispielsweise 1,5 Sekunden (30 Ticks) aktiv, aber je mehr er verrottet:
  - Befallen: **3 Sekunden** (60 Ticks).
  - Schimmelig: **7,5 Sekunden** (150 Ticks).
  - Verrottet: **22,5 Sekunden** (450 Ticks).

*(💡 **Hinweis zu gewachsten Blöcken**: Wachs ist ein umweltbedingtes Dichtmittel, verhindert aber nicht die Nutzung des Gegenstands! Du kannst gewachste Blöcke in der Werkbank verwenden, im Ofen verbrennen oder in den Komposter werfen: Sie verhalten sich genau wie ihr unbewachstes Gegenstück und behalten die exakt gleichen Mali oder Boni bei, die ausschließlich an ihren inneren Fäulnisgrad gebunden sind).*

---

## 🗺️ Generierung von Strukturen

Der Schimmel beschränkt sich nicht auf vom Spieler platzierte Blöcke. Die Mod greift in die Generierungs-Engine von Minecraft ein, um den Zahn der Zeit auf alle Holzstrukturen anzuwenden, die du in der Welt entdeckst. 

Die Strukturen sind in 4 grundlegende Zerfallsstufen unterteilt:
1. 🏴‍☠️ **Kritischer Zerfall** (Hoher Anteil an verrottetem Holz): Schiffswracks (`shipwreck`), Sumpfhütten (`swamp_hut`).
2. 🧟 **Hoher Zerfall** (Mischung aus befallenem und schimmeligem Holz): Verlassene Minenschächte (`mineshaft`), Zombiedörfer (`zombie_village`), Pfadruinen (`trail_ruins`).
3. 🏹 **Mäßiger Zerfall** (Hauptsächlich befallen): Plünderer-Außenposten (`pillager_outpost`), Zerstörte Portale (`ruined_portal`).
4. 🏡 **Minimaler Zerfall** (Fast vollständig gesund): Normale Dörfer (`village`), Waldanwesen (`mansion`).

*(💡 **Dynamische Faktoren**: Während der Generierung analysiert der Code die Umgebung Block für Block! Wenn eine Wand des Wracks Luft und Sonne ausgesetzt ist, bleibt sie intakter, während Bretter, die auf dem Meeresgrund oder unter der Erde versunken sind, drastisch stärker verrottet sein werden).*

**🛡️ Die Immunität von Naturholz und Strukturen**:
Um das Spielerlebnis nicht zu ruinieren (und zu vermeiden, dass Spieler die ganze Welt bereits kollabiert vorfinden, bevor sie sie erkunden können), gibt es zwei Ausnahmen vom automatischen Zerfall:
* **Heimische Bäume**: Natürlich generierte (oder aus Setzlingen gewachsene) Bäume bilden keinen Schimmel, da das Holz noch "lebendig" ist. Nur vom Spieler gefälltes und bearbeitetes Holz beginnt zu verrotten.
* **Schwebende Strukturen**: Strukturen generieren sich mit dem oben angegebenen Schimmelprozentsatz, aber dann "frieren" sie ein. Die Blöcke der Strukturen sind von Natur aus immun gegen das Fortschreiten der Fäulnis, es sei denn, der Spieler interagiert mit ihnen (z. B. durch Zerstören, Abkratzen oder Modifizieren). Dieser Schutz bewahrt die Dörfer vor spontaner Zerstörung. Wenn du ein super-hardcore Erlebnis möchtest, kannst du die Immunität der Strukturen im Konfigurationsmenü deaktivieren!

---

## 📊 HUD-Integration & Fortschritte

* **🔍 Jade / WTHIT-Integration**: Die Mod ist vollständig in **Jade** integriert. Wenn Sie einen beliebigen Holzblock ansehen, zeigt das HUD nativ seine genaue Variante (z. B. "Waxed Tainted Oak Planks") und sein Symbol an, zusammen mit dem aktuellen Infektionsrisiko (%). Das Risiko wird für gewachste Blöcke exakt mit 0% bewertet und bei vollständig verrotteten Blöcken (Rotten) ausgeblendet. Der Risikoprozentsatz ändert seine Farbe dynamisch (**Grau = Sicher**, **Rot = Gefährdet**). Administratoren können auch den Befehl `/moldrisk [verbose]` verwenden, um die genaue mathematische Formel des Blocks zu berechnen, den sie ansehen!
* **🏆 Fortschritte (Advancements)**: 5 benutzerdefinierte Fortschritte leiten Spieler durch die verschiedenen Mechaniken der Mod.

---

## ⚙️ Mod-Konfiguration
Die Mod enthält ein direkt aus dem Spiel zugängliches Konfigurationsmenü (erfordert **Cloth Config** und **ModMenu**), das dir die absolute Kontrolle über jede einzelne Mechanik garantiert. 
Die Optionen sind in 8 Hauptkategorien unterteilt:

* 🛠️ **Allgemein (General)**: Deaktiviere das Schimmelwachstum global, ändere den Infektionsschwellenwert, erweitere den Scanradius oder **deaktiviere die Immunität von Strukturen**, um Dörfer spontan verrotten zu lassen!
* 🌡️ **Umgebung (Environment)**: Ändere die Basiswerte für Regen/Trockenheit, die Boni für Wasser oder passe an, in welchen Höhen und bei welchen Temperaturen der Schimmel einfrieren oder sich vermehren soll.
* 🪓 **Anfälligkeit (Susceptibility)**: Stelle ein, wie schnell bearbeitete Blöcke (Bretter) im Vergleich zu rohen oder entrindeten Blöcken verrotten.
* ☣️ **Katalysatoren (Catalysts)**: Balanciere die Aggressivität von Pilzen, Schlamm, *Sporenblüten* und den infizierten Holzblöcken selbst aus.
* 🗺️ **Strukturen (Structures)**: Passe detailliert (Prozentsatz für Prozentsatz) an, wie Schiffswracks, Dörfer und Minen generiert werden.
* 🔥 **Ofen (Furnace Multipliers)**: Ändere die Brenneffizienz von Holz für die verschiedenen Zerfallsstadien.
* 💥 **Drops**: Erhöhe oder senke die Drop-Rate von zerbrechlichem Holz, wenn du die Mod als zu bestrafend empfindest.
* 🤢 **Toxizität (Toxicity)**: Ermöglicht die Änderung von Schwellenwerten, Dauer und Radius der Giftwolke sowie des Wasser-Scan-Radius.
