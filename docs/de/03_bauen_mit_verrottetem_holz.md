# 🧱 Bauen mit verrottetem Holz

Sobald Holz zu verfallen beginnt, bricht sein Wert als Baumaterial rapide ein. Die Mod bringt spürbare Konsequenzen für das Handwerk, die physische Belastbarkeit und die Ofennutzung mit sich — alles nach einer schlüssigen Logik: Je stärker das Holz zersetzt ist, desto weniger lässt sich daraus gewinnen.

---

## 🧱 Mechanischer und thermischer Zerfall & Brüchigkeit

Während Pilzhyphen Zellulose und Lignin zersetzen, verliert das Holz seinen inneren Zusammenhalt und bindet feinen, trockenen Staub. Sämtliche physikalischen und thermischen Eigenschaften verfallen parallel:

| Eigenschaft | 🌲 Stufe 0 (Sauber) | 🟢 Stufe 1 (Befallen) | 🦠 Stufe 2 (Schimmelig) | ☠️ Stufe 3 (Verrottet) |
| :--- | :---: | :---: | :---: | :---: |
| **Blockhärte** | `2.0` (100%) | `1.6` (80%) | `1.0` (50%) | `0.4` (20%) |
| **Explosionsresistenz (TNT)** | 100% | 80% | 50% | **10%** |
| **Werkzeugeffektivität** | Normal (Axt) | Normal (Axt) | Normal (Axt) | **Aufgehoben (Faust = Axt)** |
| **Überlebens-Drop** | `100%` | `100%` | `50%` (Hälfte verloren) | **`0%` (Zerfällt zu Staub)** |
| **Drop mit Behutsamkeit / Wachs** | `100%` | `100%` | `100%` | `100%` |
| **Feuer-Entzündungsbonus** | $+0$ (Vanilla) | $+5$ | $+10$ | $+20$ |
| **Feuer-Ausbreitungsbonus** | $+0$ (Vanilla) | $+10$ | $+25$ | $+60$ |
| **Brennwert (Ofen)** | `100%` (1.0×) | `50%` (0.5×) | `25%` (0.25×) | `12.5%` (0.125×) |
| **Kompostierungschance** | — (Nicht kompostierbar) | `50%` | `65%` | **`85%`** (Hervorragender Dünger) |

> [!WARNING]
> **Stufe 3 — Extreme Brüchigkeit**: Das Zerstören eines verrotteten Blocks hebt den Werkzeugvorteil der Axt komplett auf: Ob mit einer Netheritaxt oder mit bloßen Fäusten — der Abbau dauert exakt gleich lang, und der Block zerfällt zu Staub ohne jeglichen Drop (sofern er nicht mit *Behutsamkeit* abgebaut oder zuvor gewachst wurde).  
> **Holzkohlegewinnung**: Befallene Stämme (Stufen 1, 2 und 3 — sowohl ungewachst als auch gewachst) **können im Ofen nicht zu Holzkohle gebrannt werden**: Das zersetzte Pilzgewebe verhindert eine saubere Verkohlung. Nur gesunde Stämme der Stufe 0 aus der Oberwelt (Vanilla oder gewachst sauber) liefern Holzkohle. Befallene Stämme dienen ausschließlich als Brennstoff mit reduzierter Brenndauer.

---

## 📐 Handwerksregeln (Crafting)

**Nur saubere oder gewachst saubere Bretter können für komplexe Gegenstände verwendet werden** (Türen, Truhen, Treppen, Werkbänke usw.). Befallene Bretter werden in Rezepten fertiger Gegenstände nicht akzeptiert.

Die Umwandlung von befallenen Stämmen in saubere Bretter folgt dem Prinzip der **progressiven Halbierung**:

| Stamm | Erhaltene saubere Bretter |
| :--- | :---: |
| Sauber / Gewachst Sauber | 4 |
| Befallen | 2 |
| Schimmelig | 1 |
| Verrottet | 0 — unbrauchbar |

**Aufreinigung von Brettern im Handwerksgitter**: Wenn du eine alte Struktur abgerissen und befallene Bretter geborgen hast, kannst du diese an der Werkbank aufbereiten:
- 2 Befallene Bretter → 1 Sauberes Brett
- 4 Schimmelige Bretter → 1 Sauberes Brett

*Ungewachste und gewachste Varianten derselben Zerfallsstufe lassen sich im Handwerksgitter beliebig kombinieren.*

---

| | |
| :--- | ---: |
| [← Warum Holz verrottet](02_warum_holz_verrottet.md) | [Die tödliche Luft →](04_die_toedliche_luft.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
