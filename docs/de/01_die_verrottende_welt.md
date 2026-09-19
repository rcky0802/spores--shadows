# 🌳 Die verrottende Welt

Wenn du Spores & Shadows installierst, verändert sich die Minecraft-Welt nicht von heute auf morgen. Sie verändert sich mit der Zeit — und oft bemerkst du es erst, wenn es bereits zu spät ist.

Die Mod ersetzt nahtlos jeden Holzblock durch eine dynamische Variante. Der Effekt ist anfangs unsichtbar: Der Block sieht vertraut aus und besitzt die gleichen Eigenschaften. Doch die Umweltbedingungen wirken mit jedem Tick auf ihn ein, und früher oder später gewinnt der Schimmel.

---

## 🪵 Was zerfällt

Alle Blöcke sämtlicher Holzarten von Minecraft unterliegen dem Zyklus: Stämme, Bretter, Treppen, Stufen, Zäune, Zauntore, Türen, Falltüren, Druckplatten, Knöpfe, Schilder — in allen 11 im Spiel vorhandenen Holzarten. Kein Format ist immun.

## 🦠 Die vier Stufen

Der Zerfall ist eine unumkehrbare und unaufhaltsame Sequenz, wenn man nicht eingreift:

| Stufe | Name | Erscheinungsbild | Infektionsrisiko |
| :---: | :--- | :--- | :--- |
| **0** | Sauber (Vanilla) | Ursprüngliches Aussehen des Blocks | Keines |
| **1** | Befallen | Leichte Myzelflecken auf der Oberfläche | Mäßig |
| **2** | Schimmelig | Dichte Hyphen, verblasste Farbe, organische Textur | Hoch |
| **3** | Verrottet | Eingestürzte Struktur, Pilzstaub, zersetzte Textur | Irreversibel |

Der Übergang von einer Stufe zur nächsten erfolgt über die *Random Block Ticks* des Servers, sobald das Infektionsrisiko den Schwellenwert von **50%** überschreitet (siehe [Kapitel 2](02_warum_holz_verrottet.md)).

Jede Stufe existiert auch in einer **gewachsten** Variante: Ein mit Bienenwachs (Honigwabe) versiegelter Block friert seinen Verfall auf der aktuellen Stufe dauerhaft ein, heilt jedoch nicht zurück.

### 🔊 Sensorische Immersion: Eigene Klänge und Partikel
Der Zerfall ist nicht bloß eine andere Textur: Jede Stufe besitzt ihre eigene sensorische Identität:
- **Abbaugeräusche**: Das Zerstören degradierter Blöcke (Stufe 2 und 3) erzeugt ein dumpfes, reißendes Geräusch organischen Bruchs (`BLOCK_FUNGUS_BREAK`), das das klassische trockene Knacken gesunden Holzes ersetzt.
- **Sporenwolken**: Das Zerstören fortgeschrittener Blöcke ohne *Behutsamkeit* (*Silk Touch*) löst eine visuelle Eruption von Pilzpartikeln in der Luft aus (Luftsporen, fallende Sporen und Myzelfragmente — 42 Partikel auf Stufe 2 und stolze 80 auf Stufe 3).

## 🗺️ Natürlich generierte Strukturen in der Welt

Natürliche Strukturen erscheinen bereits vorgealtert, abhängig von ihrer simulierten Umweltgeschichte:

- **Kritischer Zerfall** — Schiffswracks, Hexenhütten: Massives Vorkommen von Stufe 3.
- **Hoher Zerfall** — Verlassene Minenschächte, Zombiedörfer, Ruinen: Mischung aus Stufe 1 und 2.
- **Mäßiger Zerfall** — Vorposten, Ruinenportale: Vorwiegend Stufe 1.
- **Minimaler Zerfall** — Dörfer, Waldanwesen: Nahezu unversehrt.

Lebende Bäume sind immun, solange sie nicht gefällt werden. Sobald ein Stamm gefällt wird, kann der Zerfallsprozess einsetzen.

---

| | |
| :--- | ---: |
| [📑 Inhaltsverzeichnis](README.md) | [Warum Holz verrottet →](02_warum_holz_verrottet.md) |
