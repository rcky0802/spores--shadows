# 🤿 Sich verteidigen

Die Gefahren zu kennen reicht nicht aus: Man benötigt das richtige Werkzeug. Die Mod führt ein durchdachtes System von Schutzmechaniken ein — von passiver Prävention bis hin zu aktiver Schutzausrüstung.

---

## 🐝 Vorbeugendes Wachsen

Die Anwendung einer **Honigwabe** auf einen beliebigen Holzblock versiegelt ihn mit einer schützenden Wachsschicht und friert seinen aktuellen Zustand dauerhaft ein.

Ein gewachster Block:
- Verfällt nicht weiter, völlig ungeachtet der Umgebungsbedingungen
- Emittiert keinerlei Sporen an die umgebende Raumluft
- Kann angrenzende Blöcke nicht anstecken
- Droppt beim Abbau stets zu **100%**, selbst auf Stufe 3

Wachs heilt das Holz nicht — es konserviert es exakt auf seiner aktuellen Stufe. Ein gewachster verrotteter Block bleibt verrottet, stellt aber keine Bedrohung mehr dar.

## 🪓 Behandlung mit der Axt

Durch **Schleichen (Sneak) + Rechtsklick** mit einer Axt in der Hand lässt sich direkt auf den Block einwirken:

- **Entwachsen**: Entfernt die Wachsschicht (`ITEM_AXE_WAX_OFF`) und reaktiviert den biologischen Zerfallszyklus zum Preis von 1 Haltbarkeitspunkt der Axt.
- **Schimmel abkratzen**: Auf ungewachstem Holz der Stufe 1 oder 2 schabt die Axt die oberflächlichen Hyphen ab (`ITEM_AXE_SCRAPE`) und wirft die Infektion um eine Stufe zurück ($2 \rightarrow 1 \rightarrow 0$), ebenfalls für 1 Haltbarkeitspunkt.
- **Stufe 3 (Verrottet) — Völlig unheilbar**: Die innere Struktur ist irreversibel zerstört. Die Axt hat auf morschem Holz keinerlei heilende Wirkung. Die einzige Möglichkeit, verrottetes Holz unschädlich zu machen, ohne es abzureißen, ist die Versiegelung mit einer Honigwabe (Wachsen).

---

## 😷 Die Sporenmaske

Die `Spore Mask` ist das einzige Ausrüstungsteil, das ein dauerhaftes passives Überleben im Miasma garantiert. Sie wird im Helm-Slot getragen und verfügt über ein hervorgehobenes 3D-Modell mit Visier, Atemreglern und Filterkartusche.

**Schutz & Kampf**: Sie neutralisiert die tödlichen Effekte des Miasmas vollständig (Hunger, Übelkeit, Gift). Gleichzeitig fungiert sie als leichte Rüstung (bietet **1 Rüstungspunkt**, vergleichbar mit einer Lederkappe, bei **165 Haltbarkeitspunkten**): Sie nimmt sowohl regulären Schaden durch Treffer im **Kampf** als auch 1 Punkt Haltbarkeitsabzug pro Zyklus, in dem sie giftige Raumluft anstelle der Lunge des Spielers filtert.

**Reparatur**: Ausschließlich mit **Sporenfiltern (Spore Filters)** auf dem Amboss — hergestellt aus Wolle, Holzkohle und Faden. Jeder Filter stellt 100% der Haltbarkeit wieder her. Alternativ können zwei abgenutzte Masken im Handwerksgitter für eine schnelle Feldreparatur kombiniert werden.

### 🔮 Verzauberbarkeit der Sporenmaske
Die Sporenmaske besitzt eine **Verzauberbarkeit von 0** (sie kann nicht am Zaubertisch verzaubert werden). Sie kann Verzauberungen **ausschließlich über verzauberte Bücher auf dem Amboss** empfangen, unterliegt jedoch strengen Kompatibilitätsregeln:

| Verzauberung | Masken-Kompatibilität | Effekt auf die Maske |
| :--- | :---: | :--- |
| **Haltbarkeit (Unbreaking I–III)** | ✅ **Erlaubt** | Verringert die Abnutzungschance sowohl im Kampf als auch beim Luftfiltern. |
| **Reparatur (Mending)** | ✅ **Erlaubt** | Repariert die Maske beim Aufsammeln von Erfahrungskugeln. |
| **Fluch des Verschwindens (Vanishing)** | ✅ **Erlaubt** | Die Maske verschwindet beim Tod des Spielers, statt fallengelassen zu werden. |
| **Sporenfiltration (Spore Filtration)** | ❌ **Inkompatibel** | **Nicht anwendbar**: Die Maske filtert Miasma bereits nativ; der Zauber wäre redundant. |
| **Schutz / Atmung / Wasseraffinität / Dornen** | ❌ **Inkompatibel** | Abgewiesen: Die Maske ist ein technisches Atemschutzgerät, kein verzauberter Kampfhelm. |

---

### ✨ Helm-Verzauberung: Sporenfiltration (`Spore Filtration`)

`Spore Filtration` ist eine Spezialverzauberung für **jeden herkömmlichen Helm** (Leder, Eisen, Diamant, Netherit, Schildkrötenpanzer). Sie gestattet es Trägern schwerer Rüstungen, ohne Sporenmaske sicher im Miasma zu atmen, indem die Schadstoffbelastung auf die Haltbarkeit des Helms umgelegt wird:

| Stufe | Haltbarkeitsverbrauch / Zyklus | Schutzeffizienz | Verhalten |
| :---: | :---: | :---: | :--- |
| **I** | **2 Punkte** / Zyklus | Standard | Grobe Filtration: Neutralisiert Miasma, nutzt den Helm jedoch rasch ab. |
| **II** | **1 Punkt** / Zyklus | Optimiert | Ausgewogene Filtration: Gleicht die Haltbarkeitseffizienz der Basis-Sporenmaske an. |
| **III** | **0 oder 1 Punkt** (Schnitt 0.5) | **50% Ersparnis** | Hochentwickelte Filtration: **50% Chance, bei einer Miasmaprüfung gar keine Haltbarkeit zu verbrauchen**. |

> [!TIP]
> Auf einem widerstandsfähigen Helm angewendet (z. B. einem Netherithelm kombiniert mit *Haltbarkeit III* und *Reparatur*), ermöglicht `Spore Filtration III` gefahrloses Erkunden und Kämpfen in tödlichem Miasma bei voller Schutzwirkung schwerer Rüstung!

---

## 🧭 Tragbare Detektoren

Zur Erkundung unbekannter Räume und zur Diagnose von Gebäudeschäden:

**💧 Feuchtigkeitsdetektor** — In der Hand gehalten und per Rechtsklick in die Luft ausgelöst, rastet das Gerät mit einem mechanischen Klicken ein und sendet einen detaillierten Diagnosebericht des lokalen Infektionsrisikos ($H_{eff}$, Licht, Temperatur, Nachbarkatalysatoren) in den Privatchat. Ideal, um sofort zu verstehen, warum ein Bauwerk unaufhörlich schimmelt.

**☢️ Sporendetektor** — In der Hand gehalten und per Rechtsklick bedient, analysiert das Gerät mit einem Klick sofort die Raumluft um die Augen des Spielers (Raumvolumen, aktive Belüftung, Sporendichte, dynamischer Trend). Er verhält sich beim Bewegen vollkommen lautlos (kein dauerhaftes passives Ticken) und gewährleistet höchste Diskretion bei der Höhlenerkundung.

*Der stationäre Einsatz dieser Detektoren — als hybride Redstone-Wand-, Boden- und Deckensensoren — wird in [Kapitel 6](06_sanierung_automatisieren.md) erläutert.*

---

| | |
| :--- | ---: |
| [← Die tödliche Luft](04_die_toedliche_luft.md) | [Sanierung automatisieren →](06_sanierung_automatisieren.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
