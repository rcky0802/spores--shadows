# 🎭 Zerfall im letzten Detail

Schimmel macht nicht an der Blockoberfläche halt. Er frisst sich in Benutzeroberflächen, verändert Umgebungsklänge, schwächt die Magie von Bücherregalen und lässt — erstaunlicherweise — die Dorfbewohner bei ihrer Arbeit völlig unberührt.

---

## 🖥️ Visueller Zerfall der Benutzeroberflächen (GUI-Overlays)

Das Öffnen einer befallenen Arbeitsstation spiegelt den Zustand des Blocks unmittelbar im Vollbild wider — über hochauflösende Texturoverlays, die sich nahtlos über die Vanilla-GUI legen:

- **Werkbänke**: Das 3×3-Gitter überzieht sich mit organischen Flecken und zersetzten Kanten.
- **Truhen und Fässer**: Die 9×3- und 9×6-Inventarslots weisen von Moos und Pilzen angefressene Ränder auf.
- **Webstühle und Kartentische**: Feuchtigkeit und Schimmelspuren ziehen sich entlang des gesamten Interface-Rahmens.
- **Lesepulte**: Beim Lesen eines Buches auf einem befallenen Lesepult greift das Schimmel-Overlay sogar auf die Seitenränder des Buches über.

Auch das Rendering von Truhen wurde verfeinert: Das typische *Z-Fighting* (Flackern an der Nahtstelle zwischen Korpus und Deckel) wurde durch eine millimetergenaue Anpassung des Truhendeckel-Modells gelöst, die während der Öffnungsanimation flüssig skaliert.

## 🎶 Akustische Verzerrung

Pilzsporen dringen in Resonanzkörper ein und verändern das akustische Verhalten von Musikinstrumenten grundlegend:

- **Notenblöcke**: Jeder Anschlag erzeugt verstimmte Töne mit finsterem, dumpfem Pitch-Shift, begleitet von Pilzsporen anstelle klassischer Notenpartikel.
- **Plattenspieler (Jukebox)**: Vanilla-Musikdiscs werden verlangsamt, leiernd und mit zunehmendem Störrauschen abgespielt — streng proportional zur Zerfallsstufe. Der Effekt wirkt beklemmend und eignet sich perfekt für Horror-Szenarien oder verlassene Verliese.

## 📚 Zerfall von Bücherregalen: Magie und Drops

Klassische **Bücherregale** erleiden mit fortschreitendem Zerfall einen doppelten Verlust: Sie büßen Zauberkraft am Zaubertisch ein und droppen beim Abbau ohne *Behutsamkeit* immer weniger Bücher (da Papier und Einbände verrotten):

| Stufe | Verzauberungskraft pro Block | Bücher-Drop beim Abbau *(ohne Behutsamkeit)* | Mit Behutsamkeit |
| :---: | :---: | :---: | :---: |
| **0 — Sauber** *(oder Gewachst)* | **1.0** (voll) | **3 Bücher** *(Vanilla)* | Droppt sauberes Regal |
| **1 — Befallen** | **0.66** | **2 Bücher** | Droppt befallenes Regal |
| **2 — Schimmelig** | **0.33** | **1 Buch** | Droppt schimmeliges Regal |
| **3 — Verrottet** | **0.0** *(kein Beitrag)* | **0 Bücher** *(Papier verfault)* | Droppt verrottetes Regal |

> [!NOTE]
> Werden Bücherregale **gewachst** (*Waxed*), frieren sie ihren aktuellen Zustand ein: Sie behalten ihren Bücher-Drop dauerhaft bei und bieten volle Zauberkraft (1.0), sofern sie unbeschädigt gewachst wurden. Mit *Behutsamkeit* (*Silk Touch*) wird stets der Block der jeweiligen Zerfallsstufe geborgen.

### 📖 Gemeißelte Bücherregale (Chiseled Bookshelves)
Das Verhalten **Gemeißelter Bücherregale** unterscheidet sich grundlegend, um die Sammlungen des Spielers zu schützen:
- **Erhalt der Bücher**: Alle eingelegten Folianten (normale, beschriebene oder verzauberte Bücher) bleiben zu 100% geschützt und unbeschädigt — selbst beim Übergang zu Stufe 3 oder beim Wachsen und Entwachsen mit der Axt.
- **Abbau**: Wird das Regal zerstört, werden alle enthaltenen Bücher unversehrt auf den Boden ausgeworfen (`ItemScatterer`), zusammen mit dem gemeißelten Regalblock selbst.
- **Komparator**: Das rückwärtige analoge Redstone-Signal (1 bis 6, basierend auf dem zuletzt genutzten Buchslot) bleibt exakt nach Vanilla-Standard ohne Signalverlust oder Klemmen erhalten (siehe auch [Kapitel 6](06_sanierung_automatisieren.md)).

## 👨‍🌾 Dorfbewohner und Arbeitsstätten

Trotz des sichtbaren Verfalls verlassen Dorfbewohner niemals ihre Arbeitsplätze. Fischer, Bauern, Schäfer, Kartografen, Pfeilmacher und Bibliothekare erkennen auch Blöcke der Stufe 3 vollumfänglich als legitime Arbeitsstationen an, belegen sie, gehen ihrem Tagewerk nach und handeln ohne jegliche Einschränkungen.

## 🚪 Bruchwahrscheinlichkeit bei Benutzung

Verrottetem Holz fehlt jegliche innere Spannkraft und Stabilität in den Scharnieren:
- **Bruchgefahr bei Benutzung**: Jedes Mal, wenn ein Spieler mit einem ungewachsten Funktionsblock der **Stufe 3 (Verrottet)** interagiert — wie dem Öffnen einer **Tür**, dem Umlegen einer **Falltür**, dem Bewegen eines **Zauntors** oder dem Drücken eines Holz-**Knopfs** — besteht eine Wahrscheinlichkeit von **10%** (`rotten_break_chance_on_use`), dass der Mechanismus sofort nachgibt.
- Bei einem Bruch zersplittert der Block mit einem Holzberst-Geräusch (`BLOCK_WOOD_BREAK`) und wird **vollständig zerstört, ohne einen Gegenstand zu hinterlassen**.
- **Lösung**: Das vorbeugende Auftragen einer Honigwabe (**Wachsen**) stabilisiert die Beschläge und verhindert das Zerbrechen bei der Benutzung.

---

| | |
| :--- | ---: |
| [← Sanierung automatisieren](06_sanierung_automatisieren.md) | [Konfiguration und Technik →](08_konfiguration_und_technik.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
