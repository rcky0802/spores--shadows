# 💻 Konfiguration und Technik

Ein Nachschlagewerk für Modpack-Entwickler, Server-Administratoren und fortgeschrittene Spieler, die das Verhalten der Mod im Detail verstehen oder anpassen möchten.

---

## 📖 JEI-Integration (Just Enough Items)

Die Mod integriert 7 native JEI-Kategorien, um alle Spielmechaniken direkt im Spiel ohne externe Wikis zu dokumentieren:

1. **Wachsen** — Alle Transformationen von Holzblöcken zu gewachsten Varianten mittels Honigwabe
2. **Abschaben** — Wachsentfernung und Schimmelheilung ($2 \rightarrow 1 \rightarrow 0$) mit der Axt
3. **Bretter-Rettung** — Handwerksgitter zur Aufreinigung und Kompression befallener Bretter
4. **Infektionszyklus** — Visualisierung der natürlichen Verfallsstufen ($0 \rightarrow 1 \rightarrow 2 \rightarrow 3$)
5. **Info-Karten** — Brüchigkeit, Drop-Verlust und Sonderregeln für Stufe 3
6. **Luftentfeuchter** — Energieverbrauch, Wirkungsbereich und Betriebsmodi
7. **Luftreiniger** — Filterrezepte und Reinigungsleistung

## 🔍 Jade- / WTHIT-Integration

Der kontextuelle Tooltip zeigt für jeden anvisierten Block:

- Schimmelstufe und Wachsstatus
- Lokales Infektionsrisiko ($R\%$) mit dynamischer Farbgebung (Grau = stabil, Rot = gefährdet)
- Füllstand von Kompostern
- Energiestatus und aktiven Betriebsmodus von Maschinen

## 🏆 Fortschritte (Advancements)

Die Mod enthält einen vollständigen Baum aus **11 Fortschritten**, aufgeteilt in Überleben, technisches Monitoring und anspruchsvolle Sanierungs-Meilensteine:

### 🌿 Überleben und Schimmelbehandlung
- **Spores & Shadows** *(Wurzel)* — Überlebe den Zerfall der Natur in deiner Welt.
- **Natürliche Prävention** (*Natural Prevention*) — Verwende eine Honigwabe, um einen Holzblock zu wachsen und den Schimmel aufzuhalten.
- **Muskelschmalz** (*Elbow Grease*) — Kratze den Schimmel mit einer Axt von einem Holzblock ab.
- **Kurzer Atem** (*Short Breath*) — Leide unter dem Gift des Miasmas, weil du zu viel Schimmel eingeatmet hast.
- **Staub zu Staub** (*Dust to Dust*) — Versuche, einen verfaulten Holzblock (Stufe 3) abzubauen und sieh zu, wie er zu nichts zerfällt.

### 🧭 Instrumente und Überwachung
- **Feuchtigkeitserkennung** (*Moisture Sensor*) — Stelle einen Feuchtigkeitsdetektor her, um die Raumfeuchte zu überwachen.
- **Luftwächter** (*Air Sentry*) — Stelle einen Sporendetektor her, um die Luftqualität und giftiges Miasma zu überwachen.

### ⚙️ Ingenieurskunst und Sanierungs-Herausforderungen
- **Klimaregulierung** (*Climate Control*) — Stelle einen Luftentfeuchter her, um geschlossene Räume zu trocknen und Kondenswasser zu sammeln.
- **Hermetischer Bunker** (*Hermetic Bunker*) — Stelle einen Luftreiniger her, um Miasma zu beseitigen und geschlossene Räume atembar zu machen.
- 🏆 **Unterirdische Oase** (*Dry Oasis*, Herausforderung) — Trockne eine unterirdische Kammer ($Y \le 40$) mit einem Entfeuchter auf unter 15% Luftfeuchtigkeit.
- 🏆 **Reine Luft in der Tiefe** (*Pure Air in the Depths*, Herausforderung) — Dekontaminiere einen schimmelbefallenen unterirdischen Raum ($Y \le 0$) vollständig und stelle saubere Luft (`CLEAN`) her.

---

## ⚙️ Konfiguration (ModMenu & Cloth Config)

Spores & Shadows bietet 19 Konfigurationskategorien, die direkt im Spiel über das ModMenu-Interface angepasst werden können (gespeichert in `config/spores_and_shadows.json`):

| Categorie | Was gesteuert wird |
| :--- | :--- |
| **Allgemein** (`General`) | Globaler Schimmelschalter, Scan-Radius, Infektionsschwelle (Standard 0.50), Axt-Schabeschaden, Einsturz bei Benutzung (10%) |
| **Anfälligkeit** (`Susceptibility`) | Multiplikatoren nach Format ($S_{mat}$): Entrindet (1.4×), Bretter/Treppen/Stufen/Mosaike (0.8×), Standard (1.0×) |
| **Katalysatoren** (`Catalysts`) | Katalysator-Gewichte ($C_{bonus}$): Schlamm (+0.05), Podsol/Myzel (+0.15), Pilze (+0.25), Sporenblüte (+0.80), befallene Blöcke (+0.03 / +0.06 / +0.12) |
| **Umwelt** (`Environment`) | Basisfeuchte Regen/Trocken, Tiefengradient, Wasserbeitrag, Belüftung sowie Sättigungs- und Verflüchtigungsrate |
| **Drops** (`Drops`) | Dropchancen ohne Behutsamkeit: Stufe 2 (50%) und Stufe 3 (0%) |
| **Strukturen** (`Structures`) | Voralterung natürlich generierter Vanilla-Strukturen und Umweltboni (unter Wasser, Tiefe, Bodenkontakt) |
| **Ofeneffizienz** (`Furnace Multipliers`) | Brenndauer-Multiplikatoren nach Stufe (1.0×, 0.5×, 0.25×, 0.125×) |
| **Entflammbarkeit** (`Flammability`) | Entzündungsbonus (+5, +10, +20) und Brandausbreitung (+10, +25, +60) für Stufe 1, 2 und 3 |
| **Explosionsresistenz** (`Blast Resistance`) | Multiplikatoren für TNT-Resistenz nach Stufe (80%, 50%, 10%) |
| **Härte & Zersetzung** (`Hardness`) | Skalierung der Blockhärte (80%, 50%, 20%) |
| **Redstone** (`Redstone`) | Verlängerte Impulsdauer bei Knöpfen/Druckplatten und Klemmen von Redstone-Truhen (15%, 50%, 85%) |
| **Komposter** (`Composter`) | Kompostierungschancen nach Stufe (50%, 65%, 85%) |
| **Partikel** (`Particles`) | Anzahl und Typen der Sporenpartikel beim Abbau befallener Blöcke |
| **Sporendetektor & Maske** (`Spore Detector`) | Scan-Verzögerung (initial/periodisch), Redstone-Multiplikator (Standard 5× je Stufe), Abklingzeit, Masken-Werte |
| **Feuchtigkeitsdetektor** (`Moisture Detector`) | Scan-Verzögerung (initial/periodisch), Redstone-Multiplikator (Standard 5× je Stufe), Abklingzeit des Handgeräts |
| **Luftentfeuchter** (`Dehumidifier`) | Tankkapazität (2000 mB), Ticks pro mB (24), Brennstoff-Effizienz (4.0×), Entfeuchtungsleistung (1.0), FE-Kapazität und -Verbrauch |
| **Luftreiniger** (`Air Purifier`) | Reinigungskraft (48.0), Filterhaltbarkeit (2400 Ticks / 2 Min), Brennstoff-Effizienz (4.0×), FE-Kapazität und -Verbrauch |
| **Toxizität** (`Toxicity`) | BFS-Volumen (2048 m³), Radius (16 Blöcke), Belüftungswerte (Basis 6/24), 3 Schwellenwerte (2, 6, 18), Maske und Sporenfiltration |
| **Client & Shader** (`Client`) | Z-Versatz gegen Z-Fighting beim Block-Rendering (0.002) und Intensität der Schimmelüberlagerung in GUIs (1.0) |

## 💻 Admin-Befehle

Erfordern Operator-Rechte (Level 2):

- `/miasma` — BFS-Echtzeitscan: Raumvolumen, toxischer Score, aktive Belüftung, Raumklassifizierung (Offen / Geschlossen).
- `/moldrisk` — Vollständige Aufschlüsselung des Risikos $R$ für den anvisierten Block: $H_{eff}$, $L_{uv}$, $S_{mat}$, erkannte Katalysatoren, $M_{bonus}$, $T_{mult}$, finaler $R$-Wert.

---

| | |
| :--- | ---: |
| [← Zerfall im letzten Detail](07_zerfall_im_letzten_detail.md) | [📑 Inhaltsverzeichnis](README.md) |
